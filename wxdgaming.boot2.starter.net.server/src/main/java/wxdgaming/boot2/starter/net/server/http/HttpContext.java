package wxdgaming.boot2.starter.net.server.http;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.AsciiString;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.core.io.FileReadUtil;
import wxdgaming.boot2.core.util.BytesUnit;
import wxdgaming.boot2.core.zip.GzipUtil;
import wxdgaming.boot2.starter.net.server.NioFactory;
import wxdgaming.boot2.starter.net.server.ssl.WxdOptionalSslHandler;

import java.io.File;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * http 请求当前上下文
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-14 20:07
 **/
@Slf4j
@Getter
public class HttpContext implements AutoCloseable {

    /** 大于 5mb 的请求会使用硬盘帮忙存储，如果小5mb会使用内存 */
    public static HttpDataFactory factory = new DefaultHttpDataFactory(BytesUnit.Mb.toBytes(10), StandardCharsets.UTF_8);

    private final ChannelHandlerContext ctx;
    private final Request request;
    private final Response response;

    private String ip = null;
    private String localAddress = null;
    private String remoteAddress = null;
    private final AtomicBoolean disconnected = new AtomicBoolean();
    private StringBuilder showLogStringBuilder = null;

    public HttpContext(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {
        this.ctx = ctx;
        this.request = new Request(fullHttpRequest);
        this.response = new Response();
    }

    public StringBuilder showLog() {
        if (showLogStringBuilder == null) {
            showLogStringBuilder = new StringBuilder();
            if (request != null) {
                showLogStringBuilder
                        .append("\n")
                        .append("\n=============================================请求================================================")
                        .append("\n").append(request.httpMethod()).append(" ").append(request.getCompleteUri())
                        .append("\nSession：").append(NioFactory.getCtxName(this.ctx)).append(" ").append("; Remote-Host：").append(this.getRemoteAddress()).append("; Local-Host：").append(this.getLocalAddress())
                        .append(";\nContent-type：").append(request.getReqContentType())
                        .append(";\nkeepAlive：").append(HttpContext.this.getRequest().keepAlive())
                        .append(";\n").append(HttpHeaderNames.COOKIE).append("：").append(request.header(HttpHeaderNames.COOKIE))
                        .append(";\n参数：").append(request.getReqContent()).append("\n");
            }
        }
        return showLogStringBuilder;
    }

    public boolean ssl() {
        return Boolean.TRUE.equals(NioFactory.attr(this.getCtx(), WxdOptionalSslHandler.SSL_KEY));
    }

    public String getIp() {
        if (StringUtils.isBlank(this.ip))
            this.ip = NioFactory.getIP(this.ctx);
        return ip;
    }

    public String getLocalAddress() {
        if (StringUtils.isBlank(this.localAddress))
            this.localAddress = NioFactory.getLocalAddress(ctx);
        return localAddress;
    }

    public String getRemoteAddress() {
        if (StringUtils.isBlank(this.remoteAddress))
            this.remoteAddress = NioFactory.getRemoteAddress(ctx);
        return remoteAddress;
    }

    @Getter
    public class Request implements AutoCloseable {
        private final FullHttpRequest fullHttpRequest;

        private String reqContentType;
        private boolean content_gzip = false;
        /** 完整content参数 */
        private String reqContent = "";
        private final CookiePack reqCookies = new CookiePack();
        private HttpPostMultipartRequestDecoder httpDecoder;
        /** post或者get完整参数 */
        private JSONObject reqParams;
        /*上传的文件集合*/
        private Map<String, FileUpload> uploadFileMap;
        /** 域名 */
        private String domainName;
        /** 绑定 */
        private URI uri;
        private String uriPath;
        /** 完整的url */
        private String completeUri;

        public Request(FullHttpRequest fullHttpRequest) throws Exception {
            this.fullHttpRequest = fullHttpRequest;
            this.init();
        }

        @Override public void close() {
            try {
                if (httpDecoder != null) {
                    httpDecoder.cleanFiles();
                    httpDecoder.destroy();
                }
            } catch (Exception ignored) {}
            try {
                fullHttpRequest.content().release();
            } catch (Exception e) {
                log.debug("release() {}", this, e);
            }
        }

        protected void init() throws Exception {
            reqContentType = header(HttpHeaderNames.CONTENT_TYPE);
            if (reqContentType == null) {
                reqContentType = HttpHeadValueType.Application.getValue();
            }
            reqContentType = reqContentType.toLowerCase();

            String content_Encoding = this.headerOptional(HttpHeaderNames.CONTENT_ENCODING)
                    .map(String::toLowerCase)
                    .orElse(null);

            if (content_Encoding != null && content_Encoding.contains("gzip")) {
                this.content_gzip = true;
            }

            this.reqCookies.decodeServerCookie(this.header(HttpHeaderNames.COOKIE));

            String host = this.header(HttpHeaderNames.HOST);
            String uriString = this.getFullHttpRequest().uri();
            URI uriPath = new URI(uriString);
            String uriPathString = uriPath.getPath();
            uriPathString = HttpDataAction.rawUrlDecode(uriPathString);
            if (uriPathString.length() > 1) {
                if (uriPathString.endsWith("/")) {
                    uriPathString = uriPathString.substring(0, uriPathString.length() - 1);
                }
            }

            if (StringUtils.isBlank(uriPathString) || "/".equalsIgnoreCase(uriPathString)) {
                uriPathString = "/index.html";
            }

            this.uri = uriPath;
            String http = ssl() ? "https" : "http";
            this.uriPath = uriPathString;
            this.domainName = http + "://" + host;
            this.completeUri = this.domainName + uriPathString;
            actionGetData();
            actionPostData();
        }

        protected void actionGetData() throws Exception {
            if (this.getUri() != null) {
                String queryString = this.getUri().getQuery();
                if (StringUtils.isNotBlank(queryString)) {
                    if (!this.reqContent.isEmpty()) {
                        this.reqContent += "&";
                    }
                    this.reqContent += queryString;
                    HttpDataAction.httpDataDecoder(getReqParams(), this.reqContent);
                }
            }
        }

        /**
         * @return
         * @throws Exception
         */
        protected void actionPostData() throws Exception {
            if (isMultipart()) {
                httpDecoder = new HttpPostMultipartRequestDecoder(factory, fullHttpRequest, StandardCharsets.UTF_8);
                httpDecoder.setDiscardThreshold(0);
                httpDecoder.offer(fullHttpRequest);
                try {
                    while (httpDecoder.hasNext()) {
                        InterfaceHttpData data = httpDecoder.next();
                        if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                            Attribute attribute = (Attribute) data;
                            String get = this.getReqParams().getString(data.getName());
                            if (StringUtils.isNotBlank(get)) {
                                get = get + "," + attribute.getValue();
                            } else {
                                get = attribute.getValue();
                            }
                            if (isMultipart()) {
                                /*多段式提交的话，会多包装了一层*/
                                get = URLDecoder.decode(get, StandardCharsets.UTF_8);
                            }
                            this.getReqParams().put(data.getName(), get);
                        } else if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                            FileUpload fileUpload = (FileUpload) data;
                            if (fileUpload.isCompleted()) {
                                String fileName = URLDecoder.decode(fileUpload.getFilename(), StandardCharsets.UTF_8);
                                FileUpload retainedDuplicate = fileUpload.retainedDuplicate();
                                this.getUploadFileMap().put(fileName, retainedDuplicate);
                            }
                        }
                    }
                } catch (HttpPostRequestDecoder.EndOfDataDecoderException e) {
                    /*这里无需打印*/
                }
                this.reqContent = HttpDataAction.httpData(this.getReqParams());
            } else {
                byte[] bytes = ByteBufUtil.getBytes(fullHttpRequest.content());
                this.reqContent = new String(bytes, StandardCharsets.UTF_8);
                this.reqContent = URLDecoder.decode(this.reqContent, StandardCharsets.UTF_8);
                if (this.reqContentType.contains("json")) {
                    if (StringUtils.isNotBlank(this.reqContent)) {
                        final JSONObject jsonObject = FastJsonUtil.parse(this.reqContent);
                        if (jsonObject != null && !jsonObject.isEmpty()) {
                            this.getReqParams().putAll(jsonObject);
                        }
                    }
                } else if (this.reqContentType.contains("xml") || this.reqContentType.contains("pure-text")) {

                } else {
                    HttpDataAction.httpDataDecoder(getReqParams(), this.reqContent);
                }
            }
        }

        public String header(AsciiString name) {
            return fullHttpRequest.headers().get(name);
        }

        public Optional<String> headerOptional(AsciiString name) {
            return Optional.ofNullable(fullHttpRequest.headers().get(name));
        }

        public String header(String name) {
            return fullHttpRequest.headers().get(name);
        }

        public Optional<String> headerOptional(String name) {
            return Optional.ofNullable(fullHttpRequest.headers().get(name));
        }

        public HttpMethod httpMethod() {
            return fullHttpRequest.method();
        }

        public boolean isMultipart() {

            return fullHttpRequest.method().equals(HttpMethod.POST)
                   && reqContentType != null
                   && reqContentType.toLowerCase().contains("multipart");
        }

        public boolean keepAlive() {
            return HttpUtil.isKeepAlive(fullHttpRequest);
        }

        public JSONObject getReqParams() {
            if (reqParams == null) {
                reqParams = MapOf.newJSONObject();
            }
            return reqParams;
        }

        public Map<String, FileUpload> getUploadFileMap() {
            if (uploadFileMap == null) {
                uploadFileMap = new LinkedHashMap<>();
            }
            return uploadFileMap;
        }
    }

    @Getter
    @Setter
    public class Response implements AutoCloseable {

        private final CookiePack responseCookie = new CookiePack();
        private final Map<String, String> headers = new LinkedHashMap<>();
        private HttpResponseStatus status = HttpResponseStatus.OK;
        private HttpHeadValueType responseContentType;


        public Response() {
        }

        public void header(String name, String value) {
            this.headers.put(name, value);
        }

        public void responseHtml(Object string) {
            responseContentType = HttpHeadValueType.Html;
            response(string);
        }

        public void responseJson(Object string) {
            responseContentType = HttpHeadValueType.Json;
            response(string);
        }

        public void responseText(Object string) {
            responseContentType = HttpHeadValueType.Text;
            response(string);
        }

        public void response(Object data) {

            if (disconnected.get()) return;

            HttpContext.this.disconnected.set(true);

            byte[] dataBytes;
            if (data instanceof byte[] bytes) {
                dataBytes = bytes;
                if (responseContentType == null)
                    responseContentType = HttpHeadValueType.OctetStream;
            } else if (data instanceof String str) {
                dataBytes = str.getBytes(StandardCharsets.UTF_8);
            } else if (data instanceof File file) {
                dataBytes = FileReadUtil.readBytes(file.toPath());
                header(HttpHeaderNames.CONTENT_DISPOSITION.toString(), "attachment;filename=" + HttpDataAction.urlEncoder(file));
                header(HttpHeaderNames.EXPIRES.toString(), "0");
                if (responseContentType == null)
                    responseContentType = HttpHeadValueType.findContentType(file);
            } else {
                dataBytes = FastJsonUtil.toBytes(data);
                if (responseContentType == null)
                    responseContentType = HttpHeadValueType.Json;
            }


            if (responseContentType == null)
                responseContentType = HttpHeadValueType.Text;

            boolean accept_gzip = false;

            if (dataBytes.length > 512) {
                String accept_Encoding = HttpContext.this.getRequest().headerOptional(HttpHeaderNames.ACCEPT_ENCODING)
                        .map(String::toLowerCase)
                        .orElse(null);

                if (accept_Encoding != null && accept_Encoding.contains("gzip")) {
                    accept_gzip = true;
                }
            }

            ByteBuf byteBuf;
            if (accept_gzip) {
                byteBuf = Unpooled.wrappedBuffer(GzipUtil.gzip(dataBytes));
            } else {
                byteBuf = Unpooled.wrappedBuffer(dataBytes);
            }

            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpContext.this.getRequest().fullHttpRequest.protocolVersion(), status, byteBuf);

            response.headers().set(HttpHeaderNames.CONTENT_TYPE, responseContentType);

            responseCookie.serverCookie(response.headers());

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                response.headers().set(entry.getKey(), entry.getValue());
            }
            if (accept_gzip) {
                response.headers().set(HttpHeaderNames.CONTENT_ENCODING, HttpHeaderValues.GZIP);
            }

            int readableBytes = byteBuf.readableBytes();
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, readableBytes);

            boolean keepAlive = HttpContext.this.getRequest().keepAlive();
            if (keepAlive) {
                /* TODO 复用连接池 */
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            } else {
                /* TODO 非复用的连接池 */
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            }

            HttpContext.this.ctx
                    .writeAndFlush(response)
                    .addListener((ChannelFutureListener) future1 -> {
                        if (!keepAlive) {
                            /* TODO 非复用的连接池 */
                            HttpContext.this.ctx.disconnect();
                        }
                    });

            if (log.isDebugEnabled()) {
                StringBuilder showLog = showLog();
                if (!showLog.isEmpty()) {
                    showLog.append("=============================================输出================================================")
                            .append("\n")
                            .append(HttpHeaderNames.CONTENT_TYPE).append("=").append(responseContentType)
                            .append("\n")
                            .append(HttpHeaderNames.CONTENT_LENGTH).append("=").append(readableBytes)
                            .append("\nbody: ")
                            .append(data)
                            .append("\n=============================================结束================================================")
                            .append("\n");
                    log.debug("{}", showLog);
                }
            }

        }

        @Override public void close() {

        }

    }

    @Override public void close() {
        this.request.close();
        this.response.close();
    }

}

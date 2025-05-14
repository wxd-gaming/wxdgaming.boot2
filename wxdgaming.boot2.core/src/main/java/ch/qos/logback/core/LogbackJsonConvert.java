package ch.qos.logback.core;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.contrib.json.classic.JsonLayout;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 为了同步修改日志记录的时间
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2022-08-31 21:11
 **/
public class LogbackJsonConvert extends ClassicConverter {

    @Override public String convert(ILoggingEvent event) {
        JSONObject jsonObject = new JSONObject(true);
        jsonObject.put("time", event.getTimeStamp());
        jsonObject.put("level", event.getLevel().levelStr);
        jsonObject.put("thread", event.getThreadName());
        jsonObject.put("logger", event.getLoggerName());
        jsonObject.put("message", event.getFormattedMessage());
        jsonObject.put("stackTrace", formatStackTrace(event.getThrowableProxy()));
        return JSON.toJSONString(jsonObject, SerializerFeature.PrettyFormat);
    }

    private String formatStackTrace(IThrowableProxy throwableProxy) {
        if (throwableProxy == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (StackTraceElementProxy step : throwableProxy.getStackTraceElementProxyArray()) {
            sb.append("  ")  .append(step).append("\n");
        }
        return sb.toString();
    }
}

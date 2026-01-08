package code;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WGet {

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = new ThreadPoolExecutor(6, 6,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(100),
                new RejectedExecutionHandler() {
                    @Override public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        try {
                            executor.getQueue().put(r);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        );
        HttpClient httpClient = HttpClient.newBuilder().executor(Executors.newFixedThreadPool(6)).connectTimeout(Duration.ofSeconds(5)).build();
        Path wget = Paths.get("/wget");
        Files.createDirectories(wget);
        for (int i = 0; i < 1000; i++) {
            Files.createDirectories(wget.resolve("error").resolve(String.valueOf(i)));
        }
        final int fmax = 10000000;
        AtomicInteger count = new AtomicInteger(fmax);
        for (int i = 0; i < fmax; i++) {
            String random = generateRandomString(4);
            random += "." + generateRandomString(4);
            String url = "http://%s.com".formatted(random);
            Path path = wget.resolve(random + ".url");
            if (Files.exists(path)) {
                count.decrementAndGet();
                continue;
            }
            final int index = Math.abs(random.hashCode() % 1000);
            Path errorPath = wget.resolve("error").resolve(String.valueOf(index)).resolve(random + ".url");
            if (Files.exists(errorPath)) {
                count.decrementAndGet();
                continue;
            }
            executorService.execute(() -> {
                try {
                    HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
                    HttpResponse<String> httpResponse = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).join();
                    if (httpResponse.statusCode() != 200) {
                        System.out.println("无效网站1：" + url);
                        writeFile(errorPath, url);
                    } else {
                        String body = httpResponse.body();
                        if (body.contains("无码") && body.contains("伦理") && body.contains("国产")) {
                            //                            System.err.println("有效网站：" + url + " - " + body.replace("\n", ""));
                            writeFile(path, url);
                        } else {
                            System.out.println("无效网站3：" + url);
                            writeFile(errorPath, url);
                        }
                    }
                } catch (Exception ignored) {
                    System.out.println("无效网站2：" + url);
                    writeFile(errorPath, url);
                } finally {
                    count.decrementAndGet();
                }
            });
        }
        while (count.get() > 0) {}
    }

    public static void writeFile(Path path, String url) {
        String content = """
                [InternetShortcut]
                URL=%s
                """.formatted(url);
        try {
            Files.writeString(
                    path,
                    content,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            System.out.println("写文件异常 " + path + ", " + e.getMessage());
        }
    }

    /**
     * 生成指定长度的随机小写字母和数字组合的字符串
     *
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String generateRandomString(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

}

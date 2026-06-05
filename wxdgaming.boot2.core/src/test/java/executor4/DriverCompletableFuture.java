package executor4;

import java.util.concurrent.CompletableFuture;

/**
 * 驱动器异步
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-06-04 21:05
 **/
public class DriverCompletableFuture {

    final CompletableFuture<?> future;

    public DriverCompletableFuture() {
        future = new CompletableFuture<>();
    }

}

package wxdgaming.kotlin.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Clock

fun b34(){

}

class b {
    fun b() {
        // 创建一个 CoroutineScope
        val scope = CoroutineScope(Dispatchers.Default)
        for (i in 1..1000) {
            scope.launch {
                var currentTimeMillis = System.currentTimeMillis()
                delay(1000)
                // 延迟1秒
                var main = Main()
                main.close(currentTimeMillis);
            }
        }
        // 保持主线程不退出，以便协程可以运行
        Thread.sleep(2000)  // 等待2秒以确保协程完成
    }
}
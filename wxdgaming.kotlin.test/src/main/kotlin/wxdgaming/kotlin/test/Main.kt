package wxdgaming.kotlin.test

fun main(args: Array<String>) {
    println("Hello World!")
    var b = b()
    b.b();
}

class Main {
    fun close(currentTimeMillis: Long) {
        println(currentTimeMillis.toString() + " " + System.currentTimeMillis() + " close " + Thread.currentThread())
    }
}
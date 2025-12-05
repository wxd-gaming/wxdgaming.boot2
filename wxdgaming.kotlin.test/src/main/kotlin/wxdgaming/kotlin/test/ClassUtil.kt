package wxdgaming.kotlin.test

class ClassUtil {

    fun getClassName(): String {
        return this.javaClass.name
    }

    companion object {

        fun findClass(): List<Class<*>> {
            return emptyList();
        }

    }

}
import groovy.transform.CompileStatic
import wxdgaming.boot2.starter.groovy.GroovyHandler

// 使用 @CompileStatic 提高性能
@CompileStatic
public class b implements GroovyHandler {

    b() {

    }

    @Override
    void doAction(Object[] args) {
        println(Arrays.toString(args))
    }
}

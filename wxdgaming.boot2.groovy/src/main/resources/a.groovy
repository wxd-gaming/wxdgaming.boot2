import wxdgaming.boot2.core.SpringUtil
import wxdgaming.boot2.groovy.GroovyService

def provider = SpringUtil.mainApplicationContextProvider
def groovyService = provider.getBean(GroovyService.class)
groovyService.version = 2;
return groovyService.version;
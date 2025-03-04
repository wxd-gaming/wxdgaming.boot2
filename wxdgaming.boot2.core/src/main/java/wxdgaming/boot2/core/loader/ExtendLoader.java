package wxdgaming.boot2.core.loader;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * 扩展加载器,
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-23 16:05
 **/
@Getter
@Setter
@Accessors(chain = true)
public class ExtendLoader extends URLClassLoader {

    protected ClassLoader mainClassLoader;
    protected List<String> extendPackages = new ArrayList<>();

    public ExtendLoader(ClassLoader mainClassLoader) {
        super(URLUtil.javaClassPathArray(), null);
        this.mainClassLoader = mainClassLoader;
    }

    public ExtendLoader(ClassLoader mainClassLoader, URL[] urls) {
        super(urls, null);
        this.mainClassLoader = mainClassLoader;
    }

    public void addExtendPackages(String... packages) {
        for (String aPackage : packages) {
            if (!extendPackages.contains(aPackage))
                extendPackages.add(aPackage);
        }
    }

    public void addURLs(String... paths) {
        URL[] urls = URLUtil.stringsToURLArray(paths);
        for (int i = 0; i < urls.length; i++) {
            URL url = urls[i];
            addURL(url);
        }
    }

    public void addURL(URL... urls) {
        for (int i = 0; i < urls.length; i++) {
            URL url = urls[i];
            addURL(url);
        }
    }

    @Override public void addURL(URL url) {
        super.addURL(url);
    }

    public boolean isExtendPackage(String className) {
        if (extendPackages == null || extendPackages.isEmpty()) {
            throw new RuntimeException("异常 扩展包 null");
        }
        className = className.replace('/', '.');
        for (int i = 0; i < extendPackages.size(); i++) {
            String extendPackage = extendPackages.get(i);
            if (className.startsWith(extendPackage)) {
                return true;
            }
        }
        return false;
    }

    @Override public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (isExtendPackage(name)) {
            return super.loadClass(name);
        } else {
            return mainClassLoader.loadClass(name);
        }
    }

    @Override protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }

    @Override protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (URLUtil.printLogger) {
            System.out.println("ExtendLoader findClass: " + name);
        }
        return super.findClass(name);
    }

    @Override public InputStream getResourceAsStream(String name) {
        return super.getResourceAsStream(name);
    }

    @Override public URL getResource(String name) {
        return super.getResource(name);
    }

    @Override public URL findResource(String name) {
        if (URLUtil.printLogger) {
            System.out.println("ExtendLoader findResource: " + name);
        }
        return super.findResource(name);
    }
}

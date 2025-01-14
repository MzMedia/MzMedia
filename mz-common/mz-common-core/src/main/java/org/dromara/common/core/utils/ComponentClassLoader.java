package org.dromara.common.core.utils;

import org.dromara.common.core.enums.ErrCode;
import org.dromara.common.core.exception.JarException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ComponentClassLoader {
    private static final Map<String, URLClassLoader> classLoaders = new HashMap<>();

    protected static <T> Class<T> findClass(String name, String clsName) throws ClassNotFoundException {
        ClassLoader classLoader = classLoaders.get(name);
        return (Class<T>) classLoader.loadClass(clsName);
    }

    private static String addUrl(String name, File jarPath) throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, IOException {
        URLClassLoader classLoader = classLoaders.get(name);
        if (classLoader != null) {
            classLoader.close();
        }

        classLoader = URLClassLoader.newInstance(new URL[]{jarPath.toURI().toURL()},
                Thread.currentThread().getContextClassLoader());
        classLoaders.put(name, classLoader);

        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        if (!method.canAccess(classLoader)) {
            method.setAccessible(true);
        }

        URL url = jarPath.toURI().toURL();
        method.invoke(classLoader, url);
        InputStream is = classLoader.getResourceAsStream("protocol.spi");
        if (is == null) {
            return null;
        }

        //多行只取第1行，并处理空格
        String[] lines = IOUtils.toString(is, StandardCharsets.UTF_8).split("\\s");
        if (lines.length == 0) {
            return null;
        }
        return lines[0].trim();
    }

    public static <T> T getComponent(String name, File jarFile) throws Exception {
        String className = addUrl(name, jarFile);
        if (StringUtils.isBlank(className)) {
            throw new JarException(ErrCode.GET_SPI_COMPONENT_ERROR);
        }
        Class<T> componentClass = findClass(name, className);
        return componentClass.getDeclaredConstructor().newInstance();
    }

    public static <T> T getCodec(String name) throws Exception {
        URLClassLoader classLoader = classLoaders.get(name);
        InputStream is = classLoader.getResourceAsStream("codec.spi");
        if (is == null) {
            return null;
        }

        //多行只取第1行，并处理空格
        String[] lines = IOUtils.toString(is, StandardCharsets.UTF_8).split("\\s");
        if (lines.length == 0) {
            throw new JarException(ErrCode.GET_SPI_CONVERT_ERROR);
        }
        String className = lines[0].trim();
        Class<T> converterClass = findClass(name, className);
        return converterClass.getDeclaredConstructor().newInstance();
    }

    public static void closeClassLoader(String name)  {
        try {
            URLClassLoader classLoader = classLoaders.get(name);
            if (classLoader != null){
                classLoader.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

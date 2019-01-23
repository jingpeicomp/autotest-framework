package com.qianmi.autotest.base.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.WebElement;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 通用工具类
 * Created by liuzhaoming on 16/9/27.
 */
@SuppressWarnings({"WeakerAccess", "unused", "unchecked"})
public final class AutotestUtils {
    private AutotestUtils() {
    }

    /**
     * 获取价格
     *
     * @param priceStr 价格字符串,格式为'￥30'
     * @return 价格 数值类型
     */
    public static BigDecimal getPrice(String priceStr) {
        if (StringUtils.isBlank(priceStr)) {
            return BigDecimal.ZERO;
        }

        String formatStr = StringUtils.strip(priceStr, "￥ ");
        return BigDecimal.valueOf(NumberUtils.toDouble(formatStr, 0d));
    }

    /**
     * 获取价格
     *
     * @param priceStr 价格字符串,格式为'￥30'
     * @return 价格 数值类型字符串
     */
    public static String getPriceStr(String priceStr) {
        if (StringUtils.isBlank(priceStr)) {
            return "";
        }

        return StringUtils.strip(priceStr, "￥ ");
    }

    /**
     * 比较两个商品价格字符串
     *
     * @param priceStr1 价格字符串,格式为'￥30'
     * @param priceStr2 价格字符串,格式为'￥30'
     * @return boolean
     */
    public static boolean comparePrice(String priceStr1, String priceStr2) {
        BigDecimal price1 = getPrice(priceStr1);
        BigDecimal price2 = getPrice(priceStr2);

        return price1.equals(price2);
    }

    /**
     * 获取 WebElement的描述信息
     *
     * @param webElement webElement
     * @return 字符串描述信息
     */
    public static String getWebElementDesc(WebElement webElement) {
        StringBuilder sb = new StringBuilder("WebElement[");
        try {
            sb.append("class=");
            sb.append(webElement.getClass().toString());
            sb.append(" ,tag=");
            sb.append(webElement.getTagName());
            sb.append(" ,text=");
            sb.append(webElement.getText());
            sb.append(" ,location=");
            sb.append(webElement.getLocation());
        } catch (Exception ignored) {
        }

        sb.append("]");

        return sb.toString();
    }

    /**
     * 线程sleep
     *
     * @param timeInMills 毫秒值
     */
    public static void sleep(long timeInMills) {
        try {
            Thread.sleep(timeInMills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取测试方法的测试场景
     *
     * @param method 测试方法
     * @return 测试场景
     */
    public static Scene getScene(Method method) {
        Scene scene = method.getAnnotation(Scene.class);
        if (null != scene) {
            return scene;
        }

        Class methodClass = method.getDeclaringClass();
        return (Scene) methodClass.getAnnotation(Scene.class);
    }

    /**
     * 获取方法测试场景名称
     *
     * @param method 测试方法
     * @return 测试场景
     */
    public static String getSceneName(Method method) {
        Scene scene = getScene(method);
        if (null != scene) {
            return scene.value();
        }

        return "";
    }

    /**
     * 获取测试方法的测试模块
     *
     * @param method 测试方法
     * @return 测试模块
     */
    public static Module getModule(Method method) {
        Module module = method.getAnnotation(Module.class);
        if (null != module) {
            return module;
        }

        Class methodClass = method.getDeclaringClass();
        return (Module) methodClass.getAnnotation(Module.class);
    }

    /**
     * 获取方法测试模块名称
     *
     * @param method 测试方法
     * @return 测试模块
     */
    public static String getModuleName(Method method) {
        Module module = getModule(method);
        if (null != module) {
            return module.value();
        }

        return "";
    }

    /**
     * 获取框架中所有要执行的Test类
     *
     * @return Test类名
     */
    public static String[] getAllTestClasses() {
        Reflections reflections = new Reflections("com.qianmi.autotest");
        Set<Class<? extends BasePageTest>> subTestClassSet = reflections.getSubTypesOf
                (BasePageTest.class);
        return subTestClassSet.stream()
                .filter(classT -> !Modifier.isAbstract(classT.getModifiers()))
                .map(Class::getName)
                .toArray(String[]::new);
    }

    /**
     * 获取方法名
     *
     * @return 所有的方法名
     */
    public static List<String> getTestMethods() {
        Reflections reflections = new Reflections("com.qianmi.autotest", new MethodAnnotationsScanner());
        Set<Method> methods = reflections.getMethodsAnnotatedWith(Test.class);
        Stream<Method> methodStream = methods.stream();
        methodStream = filterScene(methodStream);
        methodStream = filterModule(methodStream);
        Stream<String> methodNames = methodStream.map(AutotestUtils::getMethodName);

        return filterMethod(methodNames).collect(Collectors.toList());
    }

    /**
     * 过滤启动参数中的场景配置项
     *
     * @param inputStream 输入流
     * @return 输出流
     */
    private static Stream<Method> filterScene(Stream<Method> inputStream) {
        String sceneStr = System.getProperty("runScenes");
        if (StringUtils.isBlank(sceneStr)) {
            return inputStream;
        }

        String[] scenes = sceneStr.split(",");
        return inputStream.filter(method -> {
            String sceneName = getSceneName(method);
            int index = Arrays.binarySearch(scenes, sceneName);
            return index > -1;
        });
    }

    /**
     * 过滤启动参数中的模块配置项
     *
     * @param inputStream 输入流
     * @return 输出流
     */
    private static Stream<Method> filterModule(Stream<Method> inputStream) {
        String moduleStr = System.getProperty("runModules");
        if (StringUtils.isBlank(moduleStr)) {
            return inputStream;
        }

        String[] modules = moduleStr.split(",");
        return inputStream.filter(method -> {
            String moduleName = getModuleName(method);
            int index = Arrays.binarySearch(modules, moduleName);
            return index > -1;
        });
    }

    /**
     * 过滤启动参数中的方法配置项
     *
     * @param inputStream 输入流
     * @return 输出流
     */
    private static Stream<String> filterMethod(Stream<String> inputStream) {
        String methodStr = System.getProperty("runMethods");
        if (StringUtils.isBlank(methodStr)) {
            return inputStream;
        }

        String[] methods = methodStr.split(",");
        return inputStream.filter(methodName -> Arrays.binarySearch(methods, methodName) > -1);
    }

    /**
     * 获取方法全名
     *
     * @param method 方法
     * @return 方法名
     */
    private static String getMethodName(Method method) {
        return method.getDeclaringClass().getName() + "." + method.getName();
    }

    /**
     * 获取启动参数中的Devices配置参数
     */
    public static void initSystemProperties(String[] args) {
        Arrays.asList(args).forEach(arg -> {
            String[] templates = arg.split("=");
            if (templates.length > 1) {
                System.setProperty(templates[0], templates[1]);
            } else {
                System.setProperty(templates[0], "");
            }
        });
    }
}

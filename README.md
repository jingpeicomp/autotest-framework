# 前端自动化测试框架(UI Autotest Framework)

 框架提供统一的接口、设计原语和开发模式，支持 APP、微信、HTML5、Web 网页。自动化测试用例开发人员只需要学习一次，就可以编写前端自动化测试用例，对前端产品进行自动化测试。

## 前端自动化测试框架包含如下模块: APP 自动化测试框架、HTML5 网页自动化测试框架、Web网页自动化测试框架。  

* APP 自动化测试框架主要用于移动端APP自动化测试项目，目标程序运行在移动设备上。

* HTML5 网页自动化测试框架主要用于移动端H5网页（比如微信程序），目标网页通过移动设备上的 Chrome 或者 Safari 浏览器运行。

* Web 网页自动化测试主要用于 PC 端网页，目前支持 Chrome 、Safari 、Firefox 、IE 、Edge 浏览器。

## 特性

Autotest Framework 有如下特性：

* 采用Java语言，基于 SpringBoot 框架。

* 基于 Page Object 设计模式，将 UI 界面抽象为 Page Object，可以减少重复代码和降低维护成本。

* 基于 TesgNG 测试框架构建测试用例，支持钉钉消息通知、失败截屏、HTTP 报告、并发执行等特性。

* 统一管理和维护 Adb 连接、Appium server，对上层测试程序屏蔽实现细节，降低测试人员编写用例难度。

* 封装和抽象配置和数据仓库，直接注入到测试用例中，无需额外获取。

## 架构

### APP 测试框架的逻辑视图

![APP 测试框架逻辑视图](http://ww1.sinaimg.cn/large/44608603gy1fzhgmxlgl6j20qw0g5aam.jpg)

测试程序主要分为三层：

* APP 自动化测试程序层，包含 Page Object 对象和测试用例

* APP Framework 层，主要提供统一的系统封装

* Appium Server Manger 层，提供 Adb 连接、Appium Server、Apk 的管理和维护

### APP 测试框架模块视图

![APP 测试框架模块视图](http://ww1.sinaimg.cn/large/44608603gy1fzgrd7kzh6j20pp0gg0ti.jpg)

## 开发指南

### 1. 创建测试项目

以 APP 自动化测试为例：只需要创建一个自动化测试项目，并且依赖 APP 自动化测试框架 autotest-app 即可。

```xml
<dependencies>
    <dependency>
        <groupId>com.qianmi</groupId>
        <artifactId>autotest-app</artifactId>
        <version>2.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

再配置一个 SpringBoot 的 Maven 打包插件，mainClass 属性配置为对应框架的启动类。

* APP 的启动类为：**`com.qianmi.autotest.app.AppTestApplication`**
* HTML5 的启动类为：**`com.qianmi.autotest.html5.Html5TestApplication`**
* Web 的启动类为：**`com.qianmi.autotest.web.WebTestApplication`**

```xml
<build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <mainClass>com.qianmi.autotest.app.AppTestApplication</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
```

### 2.配置文件

#### 项目配置文件

项目配置文件位于：`src/main/resources/application.properties`

#### 测试数据

测试数据文件位于：`src/main/resources/data.properties`。输入数据以 input 开头，输出数据以 output 开头，支持根据手机指定特定的输入输出数据。

```properties
input.userName=p031801
input.password=000000
input.productName1=TestingGoods
input.productPrice1=1
input.productName2=可亲 香浓燕麦片 袋装 400g
input.productPrice2=11
input.productName3=漫步者(EDIFIER) H185 手机耳机input.productPrice3=5
input.productName4=卡乐比 水果果仁谷物麦片 袋装 800g
input.productPrice4=2
input.cateName = 食品、饮料

device.htcM8.input.userName=p031801
device.htcM8.input.password=000000
device.huawei8.input.userName=p031802
device.huawei8.input.password=000000
device.meizu.input.userName=p031803
device.meizu.input.password=000000
device.mi4.input.userName=p031804
device.mi4.input.password=000000
device.samsung.input.userName=p031805
device.samsung.input.password=000000
device.hwp7.input.userName=p031806
device.hwp7.input.password=000000
```

特定机型的参数优先级高于全局配置。比如有两个配置项：

```properties
input.userName=p031801  
device.huawei8.input.userName=p031802
```

针对 huawei8 手机，其 userName 配置为 p031802 ，如果不指定第二个配置项，那么其 userName 配置为 p031801 。

### 3. 代码开发

项目代码包含两个包：page 和 test 。

#### 3.1  Page Object 编写

Page Object 对象是指 UI 界面上用于与用户进行交互的对象，一般指某个页面。对于一个 Page Object 对象，它有两方面的特征：

* 元素 ( WebElement )

* 功能 ( Service )

元素就是界面上的标签、输入框、按钮等控件。而 Page Object 通常也都是实现一定的功能的，这些功能就是 Service 。比如登录页面有用户名输入框、密码输入框、登录按钮，点击登录按钮要么登录成功跳转到首页，要么提示登录失败。用户名输入框、密码输入框、登录按钮就是登录页面元素，登录操作就是登录页面对外提供的 Service 。

我们对 Page Object 统一定义如下：

1. Page Object 类的命名格式为：XxxPage，类上请加上 `@Component`，标明这个类由 Spring 容器托管。类注释请标明该类是什么页面，主要功能描述，方便后续人员维护。
2. Page 对象类必须继承对应框架中的 BasePage 类，元素使用注解进行定义。
3. public 方法对应页面提供的功能，方法必须返回 Page 对象，要么是自身，要么是另一个 Page 对象。如果操作失败，请抛出 AutoTestException 异常。
4. 页面跳转请调用父类的 gotoPage 方法。

#### 3.2 测试用例编写

测试用例类就是调用 Page Object 类完成某个待测功能。

1. 测试用例类的命名为 XxxTest
2. 测试用例类必须继承对应框架的 PageTest 类
3. 测试用户中的每个方法对应一个一个测试功能，方法上面加上 `@Test` 注解，注解的 priority 属性对于测试功能的执行优先级，priority 越小越先执行，priority 相同按照定义的先后执行

## 执行

编码完成后，使用 Maven 打包，执行 `java -jar` 命令执行即可，文件会输出测试报告，失败的话会截屏，并且支持消息通知、重试等功能。

如果不使用 Appium Server Mng  而在本地执行的话，需要安装 Appium ， 安装教程见：[http://appium.io/docs/en/about-appium/getting-started](http://appium.io/docs/en/about-appium/getting-started)。

### 执行 Demo

[autotest-demo](/autotest-demo) 目录下是三个模块 ，分别是 APP 、 HTML5 、 Web 自动化测试示例。

以运行autotest-demo-web为例：

```shell
  $ cd  {project_home}
  $ git clone git@github.com:jingpeicomp/autotest-framework.git
  $ cd autotest-framework
  $ mvn clean package -Dmaven.test.skip=true
  $ cd autotest-demo/autotest-demo-web/target
  $ chmod a+x autotest-demo-web-2.0.0-SNAPSHOT.jar
  $ java -jar autotest-demo-web-2.0.0-SNAPSHOT.jar
```

执行完成后，报告位于 `test-output/custom-test-report.html` （可以自定义路径），报告的格式如下：

![Web 执行报告](http://ww1.sinaimg.cn/mw690/44608603gy1fzhn5o5gd4j21se14eajp.jpg)

---

* [APP demo](/autotest-demo/autotest-demo-app) 执行过程录屏如下：

![APP demo 执行过程](http://ww1.sinaimg.cn/large/44608603gy1fzhmojmyk9g20680dcx73.gif)

---

* [HTML5 demo](/autotest-demo/autotest-demo-html5) 执行过程录屏如下：

![HTML5 demo 执行过程](http://ww1.sinaimg.cn/large/44608603gy1fzhmsvnbe7g20680dc4qu.gif)

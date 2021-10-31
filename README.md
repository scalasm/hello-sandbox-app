# Spring Boot sandbox with GraalVM

This is the demo project using with [Spring Boot](https://spring.io/projects/spring-boot) and [GraalVM](https://www.graalvm.org/) in order to improve overall performances of Spring Boot applications using native executable.

# TL;DR

Native compilation make Spring Boot applications blazing fast at startup, making them suitable for FAAS use cases (e.g., AWS Lambda or Azure Functions).

Compilation step is a long operation and it is not for development workflow: developers should continue running and debugging their applications as standard Java apps in order to be productive. 

Native compilation is something that should be part of a CI/CD pipeline in addition to a good automated test suite.

# Pre-requisites

This sandbox was tested with:
* Apache Maven 3.8.3 (it should work for other versions too)
* Spring Boot 2.5.6
* GraalVM Community Edition 21.3.0
* WSL2 on Windows 10 21H2 (everything should be applicable to other platforms)
* Docker for Desktop 4.1.1 (every docker daemon should be fine)

If there are new versions available, feel free to upgrade and test by yourself: then submit a pull request to this repository :)

You need the GraalVM installed AND `native-image` (they are shipped separately at this moment):
 * [Install GraalVM on you Linux Box](https://www.graalvm.org/docs/getting-started/linux/)
   * [Direct download link for Linux and Java 11](https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-21.3.0/graalvm-ce-java11-linux-amd64-21.3.0.tar.gz)
 * [Install native-image](https://www.graalvm.org/reference-manual/native-image/#install-native-image)
 ```
 gu install native-image
 ```

# Maven profiles

I have defined two maven profiles:
* `personal` (default) - run as standard Spring Boot app running on the JVM
* `native` - triggers the build of a Docker container image (running the built executable)
  * Note that the build process may require several minutes, depending on you machine and Internet connection (about)  

# Build and run

You can run the application as standard Java application (standard JAR and running on a JVM) or as native executable.

## Run as standard Java app

```
mvn clean spring-boot:run
```

```

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.5.6)

2021-10-31 12:42:08.980  INFO 4108 --- [  restartedMain] m.m.hsa.HelloSandboxAppApplication       : Starting HelloSandboxAppApplication using Java 11.0.13 on scalasm-xps with PID 4108 (/home/mario/src/hello-sandbox-app/target/classes started by mario in /home/mario/src/hello-sandbox-app)
2021-10-31 12:42:08.981  INFO 4108 --- [  restartedMain] m.m.hsa.HelloSandboxAppApplication       : No active profile set, falling back to default profiles: default
2021-10-31 12:42:09.050  INFO 4108 --- [  restartedMain] .e.DevToolsPropertyDefaultsPostProcessor : Devtools property defaults active! Set 'spring.devtools.add-properties' to 'false' to disable
2021-10-31 12:42:09.051  INFO 4108 --- [  restartedMain] .e.DevToolsPropertyDefaultsPostProcessor : For additional web related logging consider setting the 'logging.level.web' property to 'DEBUG'
2021-10-31 12:42:10.370  INFO 4108 --- [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
2021-10-31 12:42:10.392  INFO 4108 --- [  restartedMain] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2021-10-31 12:42:10.392  INFO 4108 --- [  restartedMain] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.54]
2021-10-31 12:42:10.624  INFO 4108 --- [  restartedMain] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2021-10-31 12:42:10.624  INFO 4108 --- [  restartedMain] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1572 ms
2021-10-31 12:42:11.027  INFO 4108 --- [  restartedMain] o.s.b.d.a.OptionalLiveReloadServer       : LiveReload server is running on port 35729
2021-10-31 12:42:11.055  INFO 4108 --- [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2021-10-31 12:42:11.068  INFO 4108 --- [  restartedMain] m.m.hsa.HelloSandboxAppApplication       : Started HelloSandboxAppApplication in 2.625 seconds (JVM running for 3.31)
```

## Run as a native image

```
mvn clean spring-boot:build-image -Pnative,\!personal
```

```
...
[INFO] Successfully built image 'docker.io/library/hello-sandbox-app:0.0.1-SNAPSHOT'
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  29:18 min
[INFO] Finished at: 2021-10-31T12:29:09Z
[INFO] ------------------------------------------------------------------------
[WARNING] The requested profile "personal" could not be activated because it does not exist.
mario@scalasm-xps:~/src/hello-sandbox-app$ docker image ls
REPOSITORY                 TAG              IMAGE ID       CREATED        SIZE
paketobuildpacks/run       tiny-cnb         59e86ed6bb4f   2 days ago     17.4MB
hello-sandbox-app          0.0.1-SNAPSHOT   5d2ff9785274   41 years ago   114MB
paketobuildpacks/builder   tiny             327b16eb4111   41 years ago   455MB
mario@scalasm-xps:~/src/hello-sandbox-app$ 
```


```
mario@scalasm-xps:~/src/hello-sandbox-app$ docker run --rm -p 8080:8080 hello-sandbox-app:0.0.1-SNAPSHOT
2021-10-31 12:33:38.965  INFO 1 --- [           main] o.s.nativex.NativeListener               : This application is bootstrapped with code generated with Spring AOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.5.6)

2021-10-31 12:33:38.966  INFO 1 --- [           main] m.m.hsa.HelloSandboxAppApplication       : Starting HelloSandboxAppApplication using Java 11.0.13 on 0b0cea416107 with PID 1 (/workspace/me.marioscalasm.hsa.HelloSandboxAppApplication started by cnb in /workspace)
2021-10-31 12:33:38.966  INFO 1 --- [           main] m.m.hsa.HelloSandboxAppApplication       : No active profile set, falling back to default profiles: default
2021-10-31 12:33:38.999  INFO 1 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
2021-10-31 12:33:38.999  INFO 1 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2021-10-31 12:33:38.999  INFO 1 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.54]
2021-10-31 12:33:39.003  INFO 1 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2021-10-31 12:33:39.003  INFO 1 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 36 ms
2021-10-31 12:33:39.030  INFO 1 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2021-10-31 12:33:39.031  INFO 1 --- [           main] m.m.hsa.HelloSandboxAppApplication       : Started HelloSandboxAppApplication in 0.076 seconds (JVM running for 0.078)
```

# Performance data

*Note that these timings are indicative and not really of statistical value: a better performance suite will be needed!*

Additionally the use case is quite simple, so more complex real-world scenarios are needed.

## Startup time
 * JAR   : JVM running for 3.31
 * Native: JVM running for 0.078

## Request latency

Running the command:
```
curl -w "@curl-format.txt" http://localhost:8080/hello?name=Mario
```

for 5 times and making an average.

## time_total

| Request | JAR | Native |
| --------|-----|--------|
|  1 | 0.262696 | 0.007720|
|  2 | 0.004951 | 0.002497|
|  3 | 0.005107 | 0.002564|
|  4 | 0.003049 | 0.002548|
|  5 | 0.003049 | 0.002424|
|  6 | 0.002694 | 0.002116|

First request is slower for both JAR and native applications (Spring servlet dispatcher initializing lazily). After that the request time is similar even though the the native application reaches a near-final value faster than the JAR application. Native looks marginally faster on the long run.
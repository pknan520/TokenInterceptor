# TokenInterceptor

内容
--------
- nodejs实现的本地服务端
- 基于OkHttp的Interceptor实现的token刷新的拦截器
- 实现DEMO
  
服务端介绍
--------
在项目根目录下的``server/refresh_token.js``
安装NodeJs环境，cd到该目录下，执行``node refresh_token.js``命令即可启动服务

**功能介绍**  
主要功能获取token，校验token。token需要放在``header``里，字段为``accessToken``。  
为方便调试，token生成规则是当前时间戳，token失效时间为**30s**

**访问地址（baseUrl）：**  
  真机访问（同一局域网下）:http://电脑局域网ip:8888/  
  模拟器访问：http://10.0.2.2:8888/  

**接口(都是GET方法)**  
  ``/get_token``  
  ``refresh_token``  
  返回：  
  ``{"success" : true,"data" : {"token" : new Date().getTime().toString()}}``

  ``/request``  
  返回：  
  ``{"success" : true,"data" : {"result" : true}}``accessToken有效  
  ``{"success": false, "error_code" : 1001}``accessToken过期  
  ``{"success": false, "error_code" : 1000}``accessToken为空

Token刷新拦截器实现
--------
TokenInterceptor在项目的``app/src/main/java.com.midea/httpDemo/http``下

关键在于``ConditionVariable``与``AtomicBoolean``的使用，具体逻辑请看代码  

要注意``chain.proceed(originalRequest)``的执行，就意味请求发出去了，所以在请求token的时候要阻塞其余请求的``chain.proceed()``
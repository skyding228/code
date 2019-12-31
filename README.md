# common code 
Here are many really common codes which are used while coding javaweb project especially developed with spring framework.

# projects
- code 
The parent project.

- utils
many static util methods.

# swagger 使用nginx配置之后host不正确
- 方法1：修改nginx配置
https://blog.csdn.net/Javamine/article/details/87433289
```text
proxy_set_header X-Forwarded-Host $host;
proxy_set_header X-Forwarded-Port $server_port;
```

- 方法2: 配置固定host
在application.properties 配置swagger.host

# guava cache
- 异步刷新
异步刷新的第一次会同步加载

- getIfPresent 与 get 区别

get会触发加载，无论同步异步，如果不存在时就会进行加载。
getIfPresent 不存在时直接返回null，存在时会如果需要，会触发加载(过期时不会触发加载)

- 多线程同时请求同一个不存在的key时，会进行几次加载？

同步/异步刷新均请求一次。

- 到了过期时间之后请求，返回什么？
同步/异步都是直接调用加载方法重新加载
加载失败：直接抛出异常

- 到了刷新时间之后请求，返回什么？
同步直接重新加载，异步触发加载但是返回之前数据；
加载失败：
同步直接抛出异常，异步如果加载失败，返回前值，并且之后每次请求都会进行刷新
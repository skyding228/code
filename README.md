# common code 
Here are many really common codes which are used while coding javaweb project especially developed with spring framework.

# projects
- code 
The parent project.

- utils
many static util methods.

# guava cache
- getIfPresent 与 get 区别

get会触发加载，无论同步异步，如果不存在时就会进行加载。
getIfPresent 不存在时直接返回null，存在时会如果需要，会触发加载

- 多线程同时请求同一个不存在的key时，会进行几次加载？

同步/异步刷新均请求一次。

- 到了过期时间之后请求，返回什么？
同步/异步都是直接调用加载方法重新加载

- 到了刷新时间之后请求，返回什么？
同步直接重新加载，异步触发加载但是返回之前数据
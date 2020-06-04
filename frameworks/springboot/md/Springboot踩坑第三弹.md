[toc]

# 一、Failed to bind properties under 'logging.level' to java.util.Map<String, LogLevel>

原因：在配置yml的时候格式出现了错误，报错信息一目了然，logging.level属性的值需要一个<String，LogLevel>形式的Map。

正确写法如下：

```yml
#设置mapper包下的日志级别为debug
logging:
  level:
    com.smday.cache.mapper: debug
```


# String类型操作

```java
    @Test
    public void testString(){
        //获取连接
        Jedis jedis = new Jedis("localhost",6379);
        //存储字符串
        jedis.set("username","summerday");
        //获取
        String username = jedis.get("username");
        System.out.println(username);
        //指定过期时间10s
        jedis.setex("activeCode",10,"Y");
        //关闭连接
        jedis.close();
    }
```

# Hash类型操作

```java
    @Test
    public void testHash(){
        //获取连接
        Jedis jedis = new Jedis();
        //存储hash
        jedis.hset("user","name", "summer");
        jedis.hset("user","age", "18");
        jedis.hset("user","gender", "male");
        //获取
        String name = jedis.hget("user", "name");
        System.out.println(name);
        //获取hash的所有map中的数据
        Map<String, String> user = jedis.hgetAll("user");
        for (String key : user.keySet()) {
            String value = user.get(key);
            System.out.println(key+">>>"+value);
        }
        //关闭连接
        jedis.close();
    }
```
# List类型操作

```java
    @Test
    public void testList(){
        //获取连接
        Jedis jedis = new Jedis();
        // 左插
        jedis.lpush("list","a","b","c");
        //右插
        jedis.rpush("list","a","b","c");
        //list范围获取
        List<String> list1 = jedis.lrange("list", 0, -1);
        //[c, b, a, a, b, c]
        System.out.println(list1);
        //list左弹出left>>>c
        String left = jedis.lpop("list");
        System.out.println("left>>>"+left);
        //list右弹出right>>>b
        String right = jedis.lpop("list");
        System.out.println("right>>>"+right);
        //[a, a, b, c]
        List<String>  list2 = jedis.lrange("list", 0, -1);
        System.out.println(list2);
        //关闭连接
        jedis.close();
    }
```

# Set类型操作

```java
    @Test
    public void testSet(){
        //获取连接
        Jedis jedis = new Jedis();
        jedis.del("set");
        System.out.println("set已删除");
        //存储set
        jedis.sadd("set","11","111","21","31","11");
        Set<String> set = jedis.smembers("set");
        //[11, 111, 31, 21]
        System.out.println(set);
        //关闭连接
        jedis.close();
    }
```

# SortedSet操作

```java
    @Test
    public void testSortedSet(){
        //获取连接
        Jedis jedis = new Jedis();
        jedis.del("sortedSet");
        jedis.zadd("sortedSet",111,"mem111");
        jedis.zadd("sortedSet",12,"mem12");
        jedis.zadd("sortedSet",34,"mem34");
        jedis.zadd("sortedSet",12,"mem12");
        jedis.zadd("sortedSet",6,"mem6");
        Set<String> sortedSet = jedis.zrange("sortedSet", 0, -1);
        //[mem6, mem12, mem34, mem111]
        System.out.println(sortedSet);
        //关闭连接
        jedis.close();
    }
```

# Jedis连接池工具类

```java
package com.travel.utils;

/**
 * @author Summerday
 */

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Jedis工具类
 */
public final class JedisUtils {
    private static JedisPool jedisPool;
    static {
        //读取配置文件
        InputStream is = JedisPool.class.getClassLoader().getResourceAsStream("jedis.properties");
        //创建Properties对象
        Properties pro = new Properties();
        //关联文件
        try {
            pro.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取数据，设置到JedisPoolConfig中
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(Integer.parseInt(pro.getProperty("maxTotal")));
        config.setMaxIdle(Integer.parseInt(pro.getProperty("maxIdle")));
        //初始化JedisPool
        jedisPool = new JedisPool(config, pro.getProperty("host"), Integer.parseInt(pro.getProperty("port")));

    }
    /**
     * 获取连接方法
     */
    public static Jedis getJedis() {
        return jedisPool.getResource();
    }
    /**
     * 关闭Jedis
     */
    public static void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
```

# 配置文件jedis.properties

```properties
#最大活动对象数     
redis.pool.maxTotal=1000    
#最大能够保持idel状态的对象数      
redis.pool.maxIdle=100  
#最小能够保持idel状态的对象数   
redis.pool.minIdle=50    
#当池内没有返回对象时，最大等待时间    
redis.pool.maxWaitMillis=10000    
#当调用borrow Object方法时，是否进行有效性检查    
redis.pool.testOnBorrow=true    
#当调用return Object方法时，是否进行有效性检查    
redis.pool.testOnReturn=true  
#“空闲链接”检测线程，检测的周期，毫秒数。如果为负值，表示不运行“检测线程”。默认为-1.  
redis.pool.timeBetweenEvictionRunsMillis=30000  
#向调用者输出“链接”对象时，是否检测它的空闲超时；  
redis.pool.testWhileIdle=true  
# 对于“空闲链接”检测线程而言，每次检测的链接资源的个数。默认为3.  
redis.pool.numTestsPerEvictionRun=50  
#redis服务器的IP    
redis.ip=xxxxxx  
#redis服务器的Port    
redis1.port=6379  
```

# 总结案例

最近其实在跟黑马旅游网的项目，总结了很多很多细碎的知识点，关于redis和jedis细节的部分以后再做总结，暂时将这些redis操作先记录着。

由于零碎知识实在太多，还是暂时先记录一部分统一的：

【缓存优化】

有些资源每次加载页面都会重新请求数据库数据来加载，对数据库的压力比较大，且这些数据不会经常发生变化，可以进行缓存优化。

1. 在service层中，首先判断数据是否存在于redis缓存中，如果有的话直接从缓存中获取。
2. 如果缓存中没有，例如第一次请求时缓存中还不存在，这就需要去数据库中查询，并将查询得到的数据添加进缓存。

```java
/**
 * @author Summerday
 * dao层代码实现
 */
public class CategoryDaoImpl implements CategoryDao {

    private JdbcTemplate template = new JdbcTemplate(JDBCUtils.getDataSource());

    @Override
    public List<Category> findAll() {
        List<Category> list = Collections.emptyList();
        //List<Category> list = new ArrayList<>();
        try{
            String sql = "select * from tab_category";
            list = template.query(sql,new BeanPropertyRowMapper<>(Category.class));
        }catch (Exception e){
        }
        return list;
    }
}
```

```java
/**
 * @author Summerday
 * service层代码实现
 */
public class CategoryServiceImpl implements CategoryService {

    CategoryDao categoryDao = new CategoryDaoImpl();

    @Override
    public List<Category> findAll() {
        //从redis中查询
        //获取jedis客户端
        Jedis jedis = JedisUtils.getJedis();
        //查询sortedSet的score(cid)和值(cname)
        Set<Tuple> tupleSetInRedis = jedis.zrangeWithScores("category", 0, -1);

        //用于存储mysql数据库中查询的list
        List<Category> categoryInDb = null;

        //判断查询的集合是否为空
        if(tupleSetInRedis == null||tupleSetInRedis.size() == 0){
            //如果为空,则从数据库查询,并存入数据进redis
            System.out.println("从数据库中查询数据");
            categoryInDb = categoryDao.findAll();
            //将all存储到redis的category中
            for (int i = 0; i < categoryInDb.size(); i++) {
                jedis.zadd("category",categoryInDb.get(i).getCid(),categoryInDb.get(i).getCname());
            }
        }else {
            System.out.println("从缓存中读取数据");
            Category category;
            //如果不为空,将set中的数据存入list
            categoryInDb = new ArrayList<>();
            for (Tuple tuple : tupleSetInRedis) {
                category = new Category();
                //转移的过程
                category.setCname(tuple.getElement());
                category.setCid((int)tuple.getScore());
                categoryInDb.add(category);
            }
        }
        return categoryInDb;
    }
}
```


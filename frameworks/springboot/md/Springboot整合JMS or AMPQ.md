JMS：Java Message Service

AMPQ：Advanced Message Queue Protocol

# RabbitMQ

erlang开发的AMPQ的实现。

**Message**：消息，不具名，由消息头（由路由键，优先权，持久性存储等可选属性构成）和消息体（不透明）组成。

**Publisher**：消息的生产者，一个向交换器发布消息的客户端应用程序。

**Exchange**：交换器，接收生产者发送的消息并将这些消息路由给服务器中的队列。有direct，fanout，topic和headers四种类型。

**Queue**：消息队列，用以保存信息直到发送给消费者。它是消息的容器，也是消息的重点。一个消息可投入一个或多个队列。

**Binding**：绑定，用于消息队列和交换器之间的关联。一个绑定就是基于路由键将交换器和消息队列连接起来的路由规则，交换器可以看成是一个由绑定构成的路由表。

**Connection**：网络连接，如一个TCP连接。

**Channel**：信道，多路复用连接中的一条独立的双向数据流通道，引入信道概念，以复用一条TCP连接。

**Consumer**：消息的消费者，表示一个从消息队列中取得消息的客户端应用程序。

**Virtual Host**：虚拟主机，表示一批交换器、消息队列和相关对象。虚拟主机是共享相同的身份认证加密环境的独立服务器域，每个vhost本质上就是一个mini版的RabbitMQ服务器，拥有自己的队列、交换器、绑定和权限机制。vhost必须在连接时指定，RabbitMQ默认的vhost是`/`。

**Broker**：表示消息队列服务器实体。

# Linux系统上Rabbitmq的安装

```bash
docker pull rabbitmq:3-management # management带web界面管理
docker run -d --name myrabbit -p 5672:5672 -p 15672:15672 cc86ffa2f398 #启动

systemctl status firewalld #查看防火墙的状态【(running)意思是打开，我们需要设置开放的端口】
firewall-cmd --list-ports #查看防火墙开放的端口
firewall-cmd --zone=public --add-port=15672/tcp --permanent # 开放15672
firewall-cmd --zone=public --add-port=5672/tcp --permanent # 开放5672
firewall-cmd --reload # 使修改生效

```

 此时就可以通过`http://192.168.213.129:15672/`访问rabbitmq管理界面。

如果是在云服务器上部署，需要设置15672和5672安全组。

# 快速体验

配置yml

```yml
spring:
  rabbitmq:
    host: 121.199.16.31
    username: guest
    password: guest
```

```java
@SpringBootTest
class SpringbootAmqpApplicationTests {

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Test
    void contextLoads() {

        Map<String,Object> map = new HashMap<>();
        map.put("msg","hello");
        map.put("data", Arrays.asList("1","2"));
        //对象被默认序列化后发送出去
        rabbitTemplate.convertAndSend("exchange.direct","summerday.news",map);
    }

    @Test
    public void receive(){
        //接收消息之后,队列中消失
        Object o = rabbitTemplate.receiveAndConvert("summerday.news");
        System.out.println(o.getClass());
        System.out.println(o);
    }
    @Test
    public void fanout(){
      	rabbitTemplate.convertAndSend("exchange.fanout","","xxx");
    }

}
```

自定义messageconverter

```java
@Configuration
public class MyAMPQConfig {

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
```

监听事件

```java
@Service
public class BookService {


    @RabbitListener(queues = "atguigu.news")
    public void receive(Book book){
        System.out.println("收到消息: "+book);
    }
    @RabbitListener(queues = "summerday.news")
    public void receive01(Message message){
        System.out.println(message.getBody());
        System.out.println(message.getMessageProperties());
    }
}
```

使用amqpadmin创建

```java
@Autowired
AmqpAdmin amqpAdmin;

@Test
public void createExchange(){
    amqpAdmin.declareExchange(new DirectExchange("amqpadmin.exchange"));
    System.out.println("创建完成!");
}
@Test
public void createQueue(){
    amqpAdmin.declareQueue(new Queue("amqpadmin.queue",true));
    System.out.println("创建完成!");
}
@Test
public void bind(){
    amqpAdmin.declareBinding(new Binding("amqpadmin.queue", Binding.DestinationType.QUEUE,"amqpadmin.exchange","amqp.haha", null));
}
```

![](img/amqp.png)



# rabbitmq-plugins: command not found

遇到这种情况，可以通过进入命令行，再执行相应的指令：

```bash
docker exec -it 1aac0eefb47f /bin/bash
```


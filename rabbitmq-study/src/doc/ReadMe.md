# 1.项目立意
1.  rabbitMQ是经常用的；而且是一个client的网络通信，所以想好好看下

# 2.各个特性总结

## 2.1 基本的发送数据
1.  默认端口是5672，而不是15672，后者是管理后台的端口
2.  凭着感觉，编写了Sender和Receiver的代码，还是挺简单的，为什么之前觉得很难呢？可能是因为工作太忙，没有时间取看它，而它其实没有想象中的难；
    以前的想象不是难，而只是距离而已。

3.  大致总结下：connection对应的是tcp链接，channel对应的是session，
    发送消息最终就是调用OutputStream的write方法。
    接收消息呢？还不知道是怎么被回调的，看doc是在一个dispatch thread中。

4.  展望：
    1.  看一下那些consumerTag、messageTag、envelope、Property都是什么，这一点可以从协议看起。
    2.  rabbitmq的一些功能特点，要尝试一下，并看一下他们的实现——难道要看elang语言么
        ```markdown
        1.  aqs
        2.  链接池-胡写的

        ```
        

# 3.专题
1.  如何
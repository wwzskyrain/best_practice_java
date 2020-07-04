# 1.编程API

## 1.1 RingBuffer的基本用法

1. ​	RingBuffer已经被Disruptor包装了，所以，从使用的角度来看，可以不用学习了。

## 1.2 Disruptor的基本用法

​	

```java
//      1.  构造disruptor：提供事件Factory，buffer_size，ThreadFactory，ProducerType，WaitStrategy
        Disruptor<LongEvent> disruptor =
                new Disruptor<LongEvent>(
                        LongEvent::new,
                        BUFFER_SIZE,
                        new DisruptorUsageThreadFactory(),
                        ProducerType.SINGLE,
                        new YieldingWaitStrategy());

//      2. 链接事件处理
        LongEventHandler1 handler1 = new LongEventHandler1();
        disruptor.handleEventsWith(handler1);
//      3. 启动disruptor
        disruptor.start();
//      4. 发布事件
        LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(disruptor.getRingBuffer());
        try {
            producer.productEvent(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//      5. 关闭
        disruptor.shutdown();
```



## 1.3 拓扑用法

```java
appendEventDisruptor
  .handleEventsWith(new AppendEventHandler1(), new AppendEventHandler2())
  .handleEventsWith(new AppendEventHandler3())
  .handleEventsWith(new AppendEventHandler4(), new AppendEventHandler5())
  .handleEventsWith(new AppendEventHandler6());
```



# 2.原理分析和代码解读

## 2.1 工作方式分析

### 1. EventProcessor：事件处理器

1. 每一个Handler都会被包装成一个EventProcessor。

2. EventProcessor的工作方式：

   1. 首先要了解到，它实现了Runnable接口，它运行在独立的线程中的。

   2. 它的最直接的使命就是消费下一个Sequence位置的事件，消费逻辑变是Handler。Sequence是一个连续自然数

   3. 但是它一般会等待在strategy.waitFor(sequence)上，等待可用的事件。

   4. 代码如下：

      ```java
      while (true)
              {
                  try
                  {
                      final long availableSequence = sequenceBarrier.waitFor(nextSequence); 	//等待-依赖
                     	...
                      while (nextSequence <= availableSequence)
                      {
                          event = dataProvider.get(nextSequence);
                          eventHandler.onEvent(event, nextSequence, nextSequence == availableSequence);	 //回调handler的方法
                          nextSequence++;
                      }
                      sequence.set(availableSequence);//更新自己的Sequence，释放(虚拟)后继结点的waitFor(nextSequence)
                  }
                  catch ...
              }
      ```

      

### 2. Sequence：传递数据vs传递数据位置

1. 与BlockQueue的传递数据不同，Disruptor不传递数据，传递数据的位置（sequence就是数据位置），接收者去指定的sequence处取出数据并消费。

   	2.	sequence必须是并发安全的。

### 3. SequenceBarrier：构建偏序关系

 1. 以ProcessingSequenceBarrier为例：

    ```java
        private   WaitStrategy waitStrategy;		//等待策略：根据依赖Sequence和cursorSequence计算当期可消费的availableSequence
        private   Sequence dependentSequence; 	//依赖的Sequence，可以是多个Sequence[]
        private   Sequence cursorSequence;		//当前的Sequence，也叫游标
        private   Sequencer sequencer;	//用于加速消费
    ```

    

 2. 作用：维护’依赖Sequence‘和当下’Sequence‘的偏序关系。

 3. 关系类型：多对一，即多个Sequence对应一个Sequence。想象一个只有一个start的有序无环图

 4. SequenceBarrier的waitFor函数：

    ```java
    long availableSequence = waitStrategy.waitFor(sequence, cursorSequence, dependentSequence, this);
    if (availableSequence < sequence){
      //可用序列小于请求队列时，直接返回可用序列
      return availableSequence;
    }
    return sequencer.getHighestPublishedSequence(sequence, availableSequence);//返回availableSequence.
    ```

    

### 4.WaitStrategy：等待策略

 1. 被SequenceBarrier使用，定义(狭义感性认识)当无事件消费时EventProcessor应该如何行为。

 2. 已知的几个等待策略如下：

    1. BlockingWaitStrategy：效率低下，不适合用于高吞吐量；但是在低吞吐量时，对cpu友好

       ```java
       //加锁
       while (cursorSequence.get() < sequence){
         barrier.checkAlert();
         processorNotifyCondition.await();
       }
       //解锁
       ```

    2. BusySpinWaitStrategy ：效率高，适合高吞吐量；地吞吐量时，造成cpu资源浪费

       ```java
       while ((availableSequence = dependentSequence.get()) < sequence){ //当可用的sequence小于请求的sequence时
         ThreadHints.onSpinWait(); //自旋-jdk中实现的一个方法
       }
       ```

    3. YieldingWaitStrategy：比BusySpinWaitStrategy更节约cpu

       ```java
       while ((availableSequence = dependentSequence.get()) < sequence){ //当可用的sequence小于请求的sequence时
       	counter = applyWaitMethod(barrier, counter); //累积到一定的count后Thread.yield().
       }
       ```

​    

## 2.2 高性能分析

### 1.伪共享和缓存行填充

 1. [剖析Disruptor:为什么会这么快？（二）神奇的缓存行填充]: http://ifeve.com/disruptor-cacheline-padding

    2. [剖析Disruptor:为什么会这么快？(三)揭秘内存屏障]: http://ifeve.com/disruptor-memory-barrier/

    

### 2.无锁设计

1. [The Disruptor – Lock-free publishing]: http://blog.codeaholics.org/2011/the-disruptor-lock-free-publishing/

   

### 3.jdk的队列不够用吗？



## 2.3 具体问题分析

1. 如何保证生产者不覆盖尚未消费的事件？
   1. 在获取下一个序列时，next就会计算所有的sequence的最小值是否小于当前cursor-buffer_size。如果小的话，就阻塞了。
2. 如何触发后继结点呢？
   1. 当前handler处理完event时，只需要增加当前结点的sequence就可以了。
   2. 后继结点是等待在sequence的。
3. 如何实现WorkPool
   1. 多个handler共同cas方式争夺同一个sequence
4. BatchEventProcessor如何实现批量操作
   1. 所谓批量操作，就是发现当前可处理的curSequence-requestSequenced = batchSize > 1时，for循环调用handler.onEvent()而已。
   2. 由于批量操作，导致我们最初观察到一个’阻塞‘现象，为了解开这个谜团，我们才入坑Disruptor源码的。
   3. ’阻塞‘假象：在线性拓扑中，比如h1->h2->h3，对于事件流e1、e2、e3，，，dn，h1每处理完一个时间e1，h2就可以紧接着处理e1，然后是h3；而不用等到h1处理完dn之后，h2才开始处理e1、e2、e3，，，dn——这是完全是阻塞的表现
   4. 解密阻塞假象：因为生产事件是非常快的，所以h1一下子进行了批处理，导致了（e1、e2、e3，，，dn）同步在一个批处理中完成，而后才设置Sequence，所以h2是一下子看到了这n个事件，然后继续批处理，然后把这一批在一块给h3.
   5. 验证：
      1. 让生产者生产大量的事件，比如10w，然后对比第一个事件的处理流和第10w个事件的处理流
      2. 让生产者循环生产事件的时候，进行睡眠一会会，以降低批处理的batchSize，从而就不会整体’阻塞‘了。
      3. 让事件消费这实现BatchStartAware接口，从而感知到批处理的存在。

# 3.扩展和讨论

## 1.面向执行流编程 Vs 面向事件流的编程

1. 为什么要用内存队列

2. Disruptor与BlockQueue有什么功能优势

   1. 优点：不用传递数据
   2. 优点：支持广播
   3. 缺点：？

3. 面向事件编程的demo

   1. 订单确认事件：
      1. 内部系统：履约rpc、库存下单
      2. 外部下游系统：发送RabbitMQ等
   2. UserBuyRecordMsg:
      1. save/update
      2. 加班
      3. 发送短信消息
      4. 积分
      5. 月课送体验营
      6. 孪生专辑
      7. 履约消息

   

## 2.有界队列抑制生产效率时

​	1.	这说明系统的处理能力不行，需要拆分业务出去，或者使用更强大的机器

## 3.如何在SpringBoot中使用Disruptor呢

​	1.	

4. 扩展阅读：

   1. [[老李读disruptor源码](https://www.jianshu.com/nb/16218289)]

   2. [[Mechanical Sympathy](https://mechanical-sympathy.blogspot.com/])]
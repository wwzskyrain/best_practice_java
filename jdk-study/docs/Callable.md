# 名词解释
## Callable 接口
1. <font color=red>Callable(java.util.concurrent.Callable) </font> 是任务接口，多用于被其他线程执行。
2. 任务(task)——有返回值，并且可以抛出异常。这两点也就是，也正是其余Runnable的区别。
3. Callable接口和Runnable接口的比较
   1. 相同点：
      1. 都是用来被其他线程执行的。
   2. 不同点：
      1. Callable有返回值。
      2. **Callable可以抛出异常**

## Future接口.
1. <font color=green>Future(java.util.concurrent.Future)</font>接口,

# Callable的入门使用demo.
&emsp;&emsp;参见`study.yueyi.edu.MyCallable`。其使用过程分三步：定义任务、提交任务、获取结果，其中获取结果是以任务执行完成为前提的.
package study.yueyi.asyn.threadpool;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoinDoubleAction {

    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        int[] array = {1, 5, 10, 15, 20, 25, 50};
        DoubleNumber doubleNumberTask = new DoubleNumber(array, 0, array.length);
        forkJoinPool.invoke(doubleNumberTask);
        doubleNumberTask.join();
        System.out.println(DoubleNumber.result);
    }

}

class DoubleNumber extends RecursiveAction {

    final int PROCESS_THRESHOLD = 2;
    int[] array;
    int startIndex, endIndex;
    static int result; // 这个是共享数据

    DoubleNumber(int[] array, int startIndex, int endIndex) {
        this.array = array;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    /**
     * 1.虽然没有返回数据，但是他们各个子任务把自己的结果累积到了result上。
     * 2.注意fork和join的使用。
     */
    @Override
    protected void compute() {
        if (endIndex - startIndex <= PROCESS_THRESHOLD) {
            for (int i = startIndex; i < endIndex; i++) {
                result += array[i] * 2;
            }
        } else {
            int mid = (startIndex + endIndex) / 2;
            DoubleNumber leftArray = new DoubleNumber(array, startIndex, mid);
            DoubleNumber rightArray = new DoubleNumber(array, mid, endIndex);

            // Invokes the compute method recursively
            leftArray.fork();  //fork() 相当于开启线程， 然后leftArray相当于线程句柄了。
            rightArray.fork();

//            // Joins results from recursive invocations
            leftArray.join();   //当前线程会阻塞在这里，等待leftArray线程的返回。
            rightArray.join();  //都没有后续操作了，为什么这个地方要join()等待一下呢？
            //哈哈，因为，这里虽然没有后续操作，但是外面总是有的啊，至少要取出计算结果吧，再不济，也需要知道什么时候计算完成了吧。
        }
    }
}

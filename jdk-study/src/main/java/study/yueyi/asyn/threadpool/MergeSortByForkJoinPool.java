package study.yueyi.asyn.threadpool;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MergeSortByForkJoinPool {

    public static void main(String[] args) {
        int[] nums = new int[]{5, 2, 6, 3, 7, 1};
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        System.out.println("Before Sort:" + Arrays.toString(nums));
        forkJoinPool.invoke(new MergeSortRecursiveTask(0, nums.length - 1, nums));
        System.out.println("After Sort" + Arrays.toString(nums));
    }


    public static class MergeSortRecursiveTask extends RecursiveTask<Integer> {
        int left;
        int right;
        int[] nums;


        public MergeSortRecursiveTask(int left, int right, int[] nums) {
            this.left = left;
            this.right = right;
            this.nums = nums;
        }

        /**
         * 其实可以计算逆序的，逆序作为返回值返回即可。
         *
         * @return
         */
        @Override
        protected Integer compute() {
            if (left == right) {
                return 0;
            } else {
                int mid = (left + right) / 2;
                MergeSortRecursiveTask leftTask = new MergeSortRecursiveTask(left, mid, nums);
                MergeSortRecursiveTask rightTask = new MergeSortRecursiveTask(mid + 1, right, nums);
                leftTask.fork();
                rightTask.fork();

                //刚才把这两个join()忘了，结果就随机了，哈哈哈。
                leftTask.join();
                rightTask.join();

                int i = left;
                int j = mid + 1;
                int index = 0;
                int[] temp = new int[right - left + 1];
                while (i <= mid && j <= right) {
                    if (nums[i] < nums[j]) {
                        temp[index++] = nums[i];
                        i++;
                    } else {
                        temp[index++] = nums[j];
                        j++;
                    }
                }
                while (i <= mid) {
                    temp[index++] = nums[i++];
                }
                while (j <= right) {
                    temp[index++] = nums[j++];
                }
                while (index > 0) {
                    nums[--j] = temp[--index];
                }
                return 0;
            }
        }
    }

}

/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package study.yueyi.asyn;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author yueyi
 * @version : CompletionServiceDemo.java, v 0.1 2023年01月29日 16:38 yueyi Exp $
 */
public class CompletionServiceDemo {

    private static ExecutorService executor = Executors.newFixedThreadPool(100);

    public static void main(String[] args) {
        CompletionService<String> completionService = new ExecutorCompletionService<>(executor);
        final int groupNum = 5;
        Random random = new Random(47);
        for (int i = 0; i < groupNum; i++) {
            final String name = "name" + i;
            completionService.submit(() -> {
                TimeUnit.MILLISECONDS.sleep(random.nextInt(10000));
                System.out.println(name + ", Done");
                return name;
            });
        }

        ArrayBlockingQueue<String> list = new ArrayBlockingQueue<>(100);

        for (int i = 0; i < groupNum; i++) {
            try {
                // 一有线程结束，这里就会返回，而且这里是单线程的哟。
                String ret = completionService.take().get();
                System.out.println(ret + "收到");
                list.add(ret);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println(list.stream().collect(Collectors.joining("->")));
        System.exit(0);
    }

}
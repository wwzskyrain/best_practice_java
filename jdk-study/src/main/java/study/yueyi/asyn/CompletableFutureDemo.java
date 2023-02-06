/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package study.yueyi.asyn;

import study.yueyi.tool.Log;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author yueyi
 * @version : CompletableFutureDemo.java, v 0.1 2023年01月29日 16:33 yueyi Exp $
 * 全部代码都来自： https://www.callicoder.com/java-8-completablefuture-tutorial/
 */
public class CompletableFutureDemo {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        //test1GetAndWaitUntilCompleted();
        //test2SupplyAsync();
        //test3SupplyAsync();
        //test4ThenApply();
        //test5SeriesThenApply();
        //test6ThenApplyAndReturnNestedCompletableFuture();
        //test7ThenCompose();
        //test8ThenCombine();
        //test9AllOf();
        test10AnyOf();
    }

    public static void test10AnyOf() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Result of Future 1";
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Result of Future 2";
        });

        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Result of Future 3";
        });

        CompletableFuture<Double> future4 = CompletableFuture.supplyAsync(() -> {
            sleepSpecificMilliseconds(1000);
            return 122.3;
        });

        //anyOf的返回值是Object，所以推测其入参可以是不同的future.
        CompletableFuture<Object> anyOfFuture = CompletableFuture.anyOf(future1, future2, future3, future4);

        System.out.println(anyOfFuture.get()); // Result of Future 2
    }

    public static void test9AllOf() throws ExecutionException, InterruptedException {
        List<String> webPageLinks = IntStream.range(0, 100).boxed().map(Object::toString).collect(Collectors.toList());
        // Download contents of all the web pages asynchronously
        List<CompletableFuture<String>> pageContentFutures = webPageLinks.stream()
                .map(webPageLink -> downloadWebPage(webPageLink))
                .collect(Collectors.toList());

        // 从时间可以看出来，100个异步处理并不是完全并发。因为整个执行花了17s多的时间。
        // 可能是线程池大小的原因。
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(pageContentFutures.toArray(new CompletableFuture[0]));
        // When all the Futures are completed, call `future.join()` to get their results and collect the results in a list -
        // 收集100个网页的内容，分别在List<String>中.
        CompletableFuture<List<String>> allPageContentsFuture = allFutures.thenApply(
                v -> pageContentFutures.stream()
                        .map(pageContentFuture -> pageContentFuture.join())
                        .collect(Collectors.toList()));

        CompletableFuture<Long> countFuture = allPageContentsFuture.thenApply(
                pageContents -> pageContents.stream()
                        .filter(pageContent -> pageContent.contains("webContent"))
                        .count());

        System.out.println("Number of Web Pages having CompletableFuture keyword - " +
                countFuture.get());
    }

    public static CompletableFuture<String> downloadWebPage(String pageLink) {
        return CompletableFuture.supplyAsync(() -> {
            // Code to download and return the web page's content
            long currentTimeMillis = System.currentTimeMillis();
            sleepSpecificMilliseconds(1000);
            Log.log(String.format("downloadWebPage(%s), taking %d milliseconds", pageLink, System.currentTimeMillis() - currentTimeMillis));
            return "webContent";
        });
    }

    /**
     * combine 组合
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void test8ThenCombine() throws ExecutionException, InterruptedException {
        System.out.println("Retrieving weight.");
        CompletableFuture<Double> weightInKgFuture = CompletableFuture.supplyAsync(() -> {
            sleepSpecificMilliseconds(2000);
            return 75.3;
        });

        System.out.println("Retrieving height.");
        CompletableFuture<Double> heightInCmFuture = CompletableFuture.supplyAsync(() -> {
            sleepSpecificMilliseconds(2000);
            return 172.00;
        });

        System.out.println("Calculating BMI.");
        CompletableFuture<Double> combinedFuture = weightInKgFuture
                .thenCombine(heightInCmFuture, (weightInKg, heightInCm) -> {
                    Double heightInMeter = heightInCm / 100;
                    return weightInKg / (heightInMeter * heightInMeter);
                });

        System.out.println("Your BMI is - " + combinedFuture.get());
    }

    /**
     * 组成
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void test7ThenCompose() throws ExecutionException, InterruptedException {
        CompletableFuture<Double> doubleCompletableFuture = getUsersDetail().thenCompose(
                CompletableFutureDemo::getCreditRating);
        Log.log(doubleCompletableFuture.get().toString());
    }

    public static void test6ThenApplyAndReturnNestedCompletableFuture() throws ExecutionException, InterruptedException {
        CompletableFuture<CompletableFuture<Double>> completableFutureCompletableFuture = getUsersDetail().thenApply(
                CompletableFutureDemo::getCreditRating);
        Log.log(completableFutureCompletableFuture.get().get().toString());
    }

    public static CompletableFuture<String> getUsersDetail() {

        return CompletableFuture.supplyAsync(() -> {
            sleepSpecificMilliseconds(2000);
            return "user_detail";
        });

    }

    public static CompletableFuture<Double> getCreditRating(String userDetail) {

        return CompletableFuture.supplyAsync(() -> {
            sleepSpecificMilliseconds(2000);
            return 2.333;
        });

    }

    public static void test5SeriesThenApply() throws ExecutionException, InterruptedException {
        CompletableFuture.supplyAsync(() -> {
            String one = "一";
            return one;
        }).thenApply(s -> {
            String two = "二";
            Log.log(s + "生" + two);
            return two;
        }).thenApply(s -> {
            String three = "三";
            Log.log(s + "生" + three);
            return three;
        }).thenApply(s -> {
            String everyThing = "万物";
            Log.log(s + "生" + everyThing);
            return everyThing;
        });
        sleepSpecificMilliseconds(3000);
        Log.log("main thread.");
    }

    /**
     * 测试'thenApply()'方法。
     * 后驱编排
     */
    public static void test4ThenApply() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future
                = CompletableFuture.supplyAsync(() ->
        {
            sleepSpecificMilliseconds(2000);
            Log.log("supply 'Hello'");
            return "Hello";
        });
        CompletableFuture<String> thenApplyFuture = future.thenApply(s -> {
            String res = s + " world!";
            Log.log("appen 'world!'");
            return res;
        });
        Log.log("执行future.get()");
        // 可以早于thenApply，也可能晚。
        Log.log("future.get()=" + future.get());
        Log.log("thenApplyFuture.get()=" + thenApplyFuture.get());

    }

    /**
     * 测试'thenApply()'方法。
     * 后驱编排
     */
    public static void test3SupplyAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future
                = CompletableFuture.supplyAsync(() ->
        {
            sleepSpecificMilliseconds(2000);
            Log.log("supplyAsync() 供应返回.");
            return "Hello";
        });

        Log.log("main thread ...");
        Log.log(String.format("%s ,world! I am supplyAsync()", future.get()));
        Log.log("main thread end !");
    }

    public static void test2SupplyAsync() throws ExecutionException, InterruptedException {

        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
            Log.log("runAsync() start...");
            sleepSpecificMilliseconds(2000);
            Log.log("runAsync() end...");
        });

        // runnable是不打算回收其值的，所以这里的get只是示意而已。
        System.out.println(completableFuture.get());
        Log.log("main thread end.");
    }

    public static void test1GetAndWaitUntilCompleted() throws InterruptedException, ExecutionException {

        Future<String> stringFuture = calculateAsync();
        System.out.println("start--");
        // 这里在get()处，阻塞。
        System.out.println(stringFuture.get());
        System.out.println("end--");

    }

    public static Future<String> calculateAsync() throws InterruptedException {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            Thread.sleep(500);
            // 这里直接给结果
            completableFuture.complete("Hello");
            return null;
        });

        return completableFuture;
    }

    public static Future<String> supplyAsync() {
        return CompletableFuture.supplyAsync(() -> "Hello");
    }

    public static void sleepSpecificMilliseconds(long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleepRandom(long milliseconds) {
        Random random = new Random();
        try {
            TimeUnit.MILLISECONDS.sleep(random.nextInt(((int) milliseconds)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
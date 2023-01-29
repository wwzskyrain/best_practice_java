/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package study.yueyi.asyn;

import java.util.concurrent.CompletableFuture;

/**
 * @author yueyi
 * @version : CompletableFutureDemo.java, v 0.1 2023年01月29日 16:33 yueyi Exp $
 */
public class CompletableFutureDemo {

    public static void main(String[] args) {

        CompletableFuture.supplyAsync(()->"hello");

    }

}
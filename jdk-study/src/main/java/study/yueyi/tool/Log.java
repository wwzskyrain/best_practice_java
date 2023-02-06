/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package study.yueyi.tool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yueyi
 * @version : Log.java, v 0.1 2023年01月30日 08:27 yueyi Exp $
 */
public class Log {

    public static final AtomicInteger count = new AtomicInteger();

    public static void log(String msg) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String no = String.format("No.%-5d", count.getAndIncrement());
        sb.append(no)
                .append(format.format(new Date()))
                .append("   ")
                .append(msg);
        System.out.println(sb);
    }

    public static final ExecutorService executor = Executors.newFixedThreadPool(20);

    public static void main(String[] args) {

        for (int i = 0; i < 100; i++) {
            final String msg = "msg" + i;
            executor.submit(() -> Log.log(msg));
        }

    }

}
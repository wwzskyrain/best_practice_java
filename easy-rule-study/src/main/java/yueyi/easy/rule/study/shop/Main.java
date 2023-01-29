/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package yueyi.easy.rule.study.shop;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author yueyi
 * @version : Main.java, v 0.1 2022年03月26日 9:31 下午 yueyi Exp $
 */
public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException {

        FileReader fileReader = new FileReader(new File(new Main().getClass().getClassLoader().getResource("alcohol-rule.yml").toURI()));
        int c;
        while ((c = fileReader.read()) >= 0) {
            System.out.print(((char) c));
        }
    }
}
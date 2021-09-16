/** * Alipay.com Inc. * Copyright (c) 2004-2021 All Rights Reserved. */package best.pratice.jdk15.helpful.npe;/** * @author yueyi * @version : ExampleHeapfulNpe.java, v 0.1 2021年09月16日 10:00 上午 yueyi Exp $ */public class ExampleHelpfulNpe {    public static record Customer(String name, Address address) {}    public static record Address(String provinceCode, String cityCode, String districtName) {}    /**     * 测试点：运行后，看一看友好的npe提示     *     * @param args     */    public static void main(String[] args) {        try {            Customer customerWithNullAddress = new Customer("小二郎货卖公司", null);            System.out.println(customerWithNullAddress.address().provinceCode().toUpperCase());        } catch (Exception e) {            System.out.println(e);        }        try {            Address addressWithNullProvinceCode = new Address(null, "cityCode", "districtCode");            Customer customerWithAddressWithNullProvinceCode = new Customer("小二郎货卖公司", addressWithNullProvinceCode);            System.out.println(customerWithAddressWithNullProvinceCode.address().provinceCode().toUpperCase());        } catch (Exception e) {            System.out.println(e);        }    }}
package study.yueyi.deep.learn.jvm;

/**
 * 运行参数 -Xmx10m -XX:UseSerialGC -XX:-UseCompressedOops
 */
public class JHSDB_TestCase {

    static class Test {
        static ObjectHolder staticObj = new ObjectHolder();

        ObjectHolder instanceObj = new ObjectHolder();

        void foo() {
            ObjectHolder localObj = new ObjectHolder();
            System.out.println("done");
        }
    }

    public static class ObjectHolder {
    }

    public static void main(String[] args) {
        Test test = new Test();
        test.foo();
    }

}

package erik.best.practice.classload;

public class TestDefinedClassLoader {
    public static void main(String[] args) throws Exception {
        DefindedClassLoader myClassLoader = new DefindedClassLoader();
        //默认父类加载器是ApplicationClassLoader
        ClassLoader parent = myClassLoader.getParent();
        System.out.println(parent);
        //开始类加载，会不会调用到我们自定的类加载器呢？
        Class<?> c1 = Class.forName("erik.best.practice.classload.Person", true, myClassLoader);
        Object obj = c1.newInstance();
        System.out.println(obj);
        System.out.println(obj.getClass().getClassLoader());
    }
}
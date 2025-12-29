package erik.best.practice.classload;


import java.io.IOException;
import java.io.InputStream;


public class ClassLoaderTest {

    public static void main(String[] args) throws Exception {

        ClassLoader myClassLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                try {
                    String fileName = name.substring(name.lastIndexOf(".") + 1) + ".class";
                    InputStream is = getClass().getResourceAsStream(fileName);
                    if (is == null) {
                        return super.loadClass(name);
                    }
                    byte[] b = new byte[is.available()];
                    is.read(b);
                    return defineClass(name, b, 0, b.length);
                } catch (IOException e) {
                    throw new ClassNotFoundException(name);
                }

            }
        };

        /*
        * 1.不要直接用loadClass方法，要用Class.forName("类全限定名", true, myClassLoader);
        * 2.如果用Class.forName("全限定名") 不带 自定义类加载器入参，则和直接使用 ClassLoaderTest一样，都是系统加载。
        * 3.可以分别注释掉一下三句，来看看效果。
        * */
//        Class<?> myClass = myClassLoader.loadClass("erik.best.practice.classload.ClassLoaderTest");
//        Class<?> myClass = Class.forName("erik.best.practice.classload.ClassLoaderTest", true, myClassLoader);
        Class<?> myClass = Class.forName("erik.best.practice.classload.ClassLoaderTest");
        Object obj = myClass.newInstance();
        System.out.println(obj.getClass());
        System.out.println(obj instanceof ClassLoaderTest);

        ClassLoaderTest classLoaderTest = new ClassLoaderTest();
        System.out.println(classLoaderTest.getClass());
        System.out.println(classLoaderTest instanceof ClassLoaderTest);

    }

}

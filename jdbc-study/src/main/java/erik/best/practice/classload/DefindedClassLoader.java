package erik.best.practice.classload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * 自定义类加载器
 */
public class DefindedClassLoader extends ClassLoader {
    public DefindedClassLoader() {
    }

    public DefindedClassLoader(ClassLoader parent) {
        super(parent);
    }

    /**
     * 接受 Class.forName("erik.best.practice.classload.Person", true, myClassLoader)
     *
     * @param name
     * @return
     * @throws ClassNotFoundException
     */
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        //这里可以维护一个类变量，Map<String, Location> className2LocationForClassFile.
        File file = getClassFile(name);
        try {
            byte[] bytes = getClassBytes(file);
            Class<?> c = this.defineClass(name, bytes, 0, bytes.length);
            return c;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.findClass(name);
    }

    private File getClassFile(String name) {
        File file = new File("/Users/yueyi/git_project/best_practice_java/jdbc-study/src/class_file/Person.class");
        return file;
    }

    private byte[] getClassBytes(File file) throws Exception {
        // 这里要读入.class的字节，因此要使用字节流
        FileInputStream fis = new FileInputStream(file);
        FileChannel fc = fis.getChannel();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        WritableByteChannel wbc = Channels.newChannel(baos);
        ByteBuffer by = ByteBuffer.allocate(1024);

        while (true) {
            int i = fc.read(by);
            if (i == 0 || i == -1)
                break;
            by.flip();
            wbc.write(by);
            by.clear();
        }

        fis.close();

        return baos.toByteArray();
    }
}
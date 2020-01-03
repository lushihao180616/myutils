package com.lushihao.myutils.file;

import com.lushihao.myutils.check.LSHCheckUtils;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class LSHIOUtils {

    /**
     * 编码格式
     */
    public static final String UTF8 = "UTF-8";
    /**
     * 编码格式
     */
    public static final String GBK = "GBK";

    /**
     * 磁盘使用率
     *
     * @param path
     * @return
     */
    public static BigDecimal diskUseRate(String path) {
        File file = new File("..");
        // 当前磁盘总空间
        long totalSpace = file.getTotalSpace();
        // 空闲空间
        long freeSpace = file.getFreeSpace();
        // 已使用的空间
        double usageRate = 1.0 * 100 * (totalSpace - freeSpace) / totalSpace;
        // BigDecimal：有效位的数进行精确的运算
        // MathContext(指定的精度, 舍入模式)：根据上下文设置进行舍入
        // RoundingMode：指定丢弃精度的数值运算的舍入行为
        BigDecimal bDecimal = new BigDecimal(usageRate, new MathContext(3, RoundingMode.HALF_UP));
        System.out.println("当前磁盘使用率：" + bDecimal + "%");
        return bDecimal;
    }

    /**
     * 文档压缩
     *
     * @param file 需要压缩的文件或目录
     * @param dest 压缩后的文件名称
     * @throws IOException
     */
    public static void file2Zip(File file, String dest) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(dest))) {
            String dir = "";
            if (file.isDirectory()) {
                dir = file.getName();
            }
            zipFile(file, zos, dir);
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * 文档解压
     *
     * @param source 需要解压缩的文档名称
     * @param path   需要解压缩的路径
     */
    public static void zip2File(File source, String path) throws IOException {
        ZipEntry zipEntry = null;
        createPaths(path);
        //实例化ZipFile，每一个zip压缩文件都可以表示为一个ZipFile
        //实例化一个Zip压缩文件的ZipInputStream对象，可以利用该类的getNextEntry()方法依次拿到每一个ZipEntry对象
        try (
                ZipFile zipFile = new ZipFile(source);
                ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(source))
        ) {
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String fileName = zipEntry.getName();
                String filePath = path + "/" + fileName;
                if (zipEntry.isDirectory()) {
                    File temp = new File(filePath);
                    if (!temp.exists()) {
                        temp.mkdirs();
                    }
                } else {
                    File temp = new File(filePath);
                    if (!temp.getParentFile().exists()) {
                        temp.getParentFile().mkdirs();
                    }
                    try (OutputStream os = new FileOutputStream(temp);
                         //通过ZipFile的getInputStream方法拿到具体的ZipEntry的输入流
                         InputStream is = zipFile.getInputStream(zipEntry)) {
                        int len = 0;
                        while ((len = is.read()) != -1) {
                            os.write(len);
                        }
                    } catch (IOException e) {
                        throw e;
                    }
                }
            }
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * 读取指定路径文本文件
     */
    public static String readText(String filePath) {
        StringBuilder str = new StringBuilder();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(filePath));
            String s;
            try {
                while ((s = in.readLine()) != null)
                    str.append(s + '\n');
            } finally {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    /**
     * 写入指定的文本文件，append为true表示追加，false表示重头开始写
     */
    public static void writeText(String filePath, boolean append, String text) {
        if (text == null) return;
        createPaths(filePath);
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filePath,
                    append));
            try {
                out.write(text);
            } finally {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 把二进制文件读入字节数组，如果没有内容，字节数组为null
     */
    public static byte[] readBinary(String filePath) {
        byte[] data = null;
        try {
            BufferedInputStream in = new BufferedInputStream(
                    new FileInputStream(filePath));
            try {
                data = new byte[in.available()];
                in.read(data);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 把字节数组为写入二进制文件，数组为null时直接返回
     */
    public static void writeBinary(String filePath, byte[] data) {
        if (data == null) return;
        createPaths(filePath);
        try {
            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(filePath));
            try {
                out.write(data);
            } finally {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取对象，返回一个对象数组，count表示要读的对象的个数
     */
    public static Object[] readObject(String filePath, int count) {
        Object[] objects = new Object[count];
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(
                    filePath));
            try {
                for (int i = 0; i < count; i++) {
                    //第二次调用in.readObject()就抛出异常，这是为什么？
                    objects[i] = in.readObject();
                }
            } finally {
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objects;
    }

    /**
     * 把一个对象数组写入文件，isAppend为true表示追加方式写，false表示重新写
     */
    public static void writeObject(String filePath, Object[] objects, boolean isAppend) {
        if (objects == null) return;
        createPaths(filePath);
        try {
            ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream(filePath, isAppend));
            try {
                for (Object o : objects)
                    out.writeObject(o);
            } finally {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建文件支持多级目录
     *
     * @param filePath 需要创建的文件
     * @return 是否成功
     */
    public static boolean createFiles(String filePath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            if (!file.exists()) {
                return file.mkdirs();
            }
        } else {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                if (dir.mkdirs()) {
                    try {
                        return file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    /**
     * 罗列指定路径下的全部文件
     *
     * @param path 需要处理的文件
     * @return 包含所有文件的的list
     */
    public final static List<File> listFile(String path) {
        File file = new File(path);
        return listFile(file);
    }

    /**
     * 罗列指定路径下的全部文件
     *
     * @param path  需要处理的文件
     * @param child 是否罗列子文件
     * @return 包含所有文件的的list
     */
    public final static List<File> listFile(String path, boolean child) {
        return listFile(new File(path), child);
    }


    /**
     * 罗列指定路径下的全部文件
     *
     * @param path 需要处理的文件
     * @return 返回文件列表
     */
    public final static List<File> listFile(File path) {
        List<File> list = new ArrayList<>();
        File[] files = path.listFiles();
        if (LSHCheckUtils.valid(files)) {
            for (File file : files) {
                if (file.isDirectory()) {
                    list.addAll(listFile(file));
                } else {
                    list.add(file);
                }
            }
        }
        return list;
    }

    /**
     * 罗列指定路径下的全部文件
     *
     * @param path  指定的路径
     * @param child 是否罗列子目录
     * @return
     */
    public final static List<File> listFile(File path, boolean child) {
        List<File> list = new ArrayList<>();
        File[] files = path.listFiles();
        if (LSHCheckUtils.valid(files)) {
            for (File file : files) {
                if (child && file.isDirectory()) {
                    list.addAll(listFile(file));
                } else {
                    list.add(file);
                }
            }
        }
        return list;
    }

    /**
     * 创建多级目录
     *
     * @param inFile 文件或文件夹
     * @param zos    压缩流
     * @param dir    路径
     */
    private static void zipFile(File inFile, ZipOutputStream zos, String dir) throws IOException {
        if (inFile.isDirectory()) {
            File[] files = inFile.listFiles();
            if (files == null || files.length == 0) {
                String entryName = dir + "/";
                zos.putNextEntry(new ZipEntry(entryName));
                return;
            }
            for (File file : files) {
                String entryName = dir + "/" + file.getName();
                if (file.isDirectory()) {
                    zipFile(file, zos, entryName);
                } else {
                    ZipEntry entry = new ZipEntry(entryName);
                    zos.putNextEntry(entry);
                    try (InputStream is = new FileInputStream(file)) {
                        int len = 0;
                        while ((len = is.read()) != -1) {
                            zos.write(len);
                        }
                    } catch (IOException e) {
                        throw e;
                    }
                }
            }
        } else {
            String entryName = dir + "/" + inFile.getName();
            ZipEntry entry = new ZipEntry(entryName);
            zos.putNextEntry(entry);
            try (InputStream is = new FileInputStream(inFile)) {
                int len = 0;
                while ((len = is.read()) != -1) {
                    zos.write(len);
                }
            } catch (IOException e) {
                throw e;
            }
        }
    }

    /**
     * 创建多级目录
     *
     * @param paths 需要创建的目录
     * @return 是否成功
     */
    private static boolean createPaths(String paths) {
        File dir = new File(paths);
        return !dir.exists() && dir.mkdir();
    }
}
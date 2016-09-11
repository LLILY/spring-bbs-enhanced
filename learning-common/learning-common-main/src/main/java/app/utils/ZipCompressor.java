package app.utils;

import java.io.*;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

/**
 * Created by lili19289 on 2016/8/31.
 */
public class ZipCompressor {

    static final int BUFFER = 8192;

    private File zipFile;

    public ZipCompressor(String pathName) {
        zipFile = new File(pathName);
    }

    public void compress(String srcPathName) {
        File file = new File(srcPathName);
        if (!file.exists())
            throw new RuntimeException(srcPathName + "不存在！");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
            CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream,
                    new CRC32());
            ZipOutputStream out = new ZipOutputStream(cos);
            String basedir = "";
            compress(file, out, basedir);
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void compress(File file, ZipOutputStream out, String basedir) {
        if (!file.exists()) {
            return;
        }
		/* 判断是目录还是文件 */
        if (file.isDirectory()) {
            System.out.println("压缩：" + basedir + file.getName());
            this.compressDirectory(file, out, basedir);
        } else {
            System.out.println("压缩：" + basedir + file.getName());
            this.compressFile(file, out, basedir);
        }
    }

    /** 压缩一个目录 */
    private void compressDirectory(File dir, ZipOutputStream out, String basedir) {
        if (!dir.exists())
            return;

        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
			/* 递归 */
            File file = files[i];
            if (file.exists()) {
                compress(files[i], out, basedir + dir.getName() + "/");
            }
        }
    }

    /** 压缩一个文件 */
    private void compressFile(File file, ZipOutputStream out, String basedir) {
        if (!file.exists()) {
            return;
        }
        try {
            FileInputStream fs = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fs
            );
            ZipEntry entry = new ZipEntry(basedir + file.getName());
            out.putNextEntry(entry);
            int count;
            byte data[] = new byte[BUFFER];
            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            bis.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param inputFileName
     *            输入一个文件夹
     * @param zipFileName
     *            输出一个压缩文件夹，打包后文件名字
     * @throws Exception
     */
    public static OutputStream zip(String inputFileName, String zipFileName)
            throws Exception {
        return zip(zipFileName, new File(inputFileName));
    }

    private static OutputStream zip(String zipFileName, File inputFile)
            throws Exception {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
                zipFileName));
        zip(out, inputFile, "");
        out.close();
        return out;
    }

    private static void zip(ZipOutputStream out, File f, String base)
            throws Exception {
        if (f.isDirectory()) { // 判断是否为目录
            File[] fl = f.listFiles();
            // out.putNextEntry(new org.apache.tools.zip.ZipEntry(base + "/"));
            // out.putNextEntry(new ZipEntry(base + "/"));
            ZipEntry zipEntry = new ZipEntry(base
                    + System.getProperties().getProperty("file.separator"));
            out.putNextEntry(zipEntry);
            // base = base.length() == 0 ? "" : base + "/";
            base = base.length() == 0 ? "" : base
                    + System.getProperties().getProperty("file.separator");
            for (int i = 0; i < fl.length; i++) {
                zip(out, fl[i], base + fl[i].getName());
            }
        } else { // 压缩目录中的所有文件
            // out.putNextEntry(new
            // org.apache.tools.zip.ZipEntry(base));
            ZipEntry zipEntry = new ZipEntry(base);
            out.putNextEntry(zipEntry);
            FileInputStream in = new FileInputStream(f);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            in.close();
        }
    }

}

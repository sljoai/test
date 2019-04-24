package com.song.cn.basic.files;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

/**
 * 获取文件的行数
 */
public class FileLineTest {

    public static void main(String[] args) throws IOException {
        int line = new FileLineTest().getTotalLines(new File("D:\\宋来将\\123\\error.log"));
        System.out.println(line);
    }

    public int getTotalLines(File file) throws IOException {
        long startTime = System.currentTimeMillis();
        FileReader in = new FileReader(file);
        LineNumberReader reader = new LineNumberReader(in);
        reader.skip(Long.MAX_VALUE);
        int lines = reader.getLineNumber();
        reader.close();
        long endTime = System.currentTimeMillis();

        System.out.println("统计文件行数运行时间： " + (endTime - startTime) + "ms");
        return lines;
    }
}

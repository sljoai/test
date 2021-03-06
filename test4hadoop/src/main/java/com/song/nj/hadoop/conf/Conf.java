package com.song.nj.hadoop.conf;

import org.apache.hadoop.conf.Configuration;

public class Conf {
    public static Configuration get(){
        String hdfsUrl = "hdfs://sparkproject1:9000";
        String hdfsName = "fs.defaultFS";
        String jarPath = "D:\\Java\\SRC\\test\\out\\artifacts\\test4hadoop_jar\\test4hadoop.jar";

        Configuration conf = new Configuration();

        conf.set(hdfsName,hdfsUrl);
        conf.set("mapreduce.app-submission.cross-platform","true");
        conf.set("mapreduce.job.jar",jarPath);

        return conf;
    }
}

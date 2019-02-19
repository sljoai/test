package com.song.nj.hadoop.mr.pagerank;

import java.io.IOException;

import com.song.nj.hadoop.conf.Conf;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class PageRankViewer {
    public static class PageRankViewerMapper extends
            Mapper<LongWritable, Text, FloatWritable, Text> {
        private Text outPage = new Text();
        private FloatWritable outPr = new FloatWritable();

        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String[] line = value.toString().split("\t");
            String page = line[0];
            float pr = Float.parseFloat(line[1]);
            outPage.set(page);
            outPr.set(pr);
            context.write(outPr, outPage);
        }
    }

    /**重载key的比较函数，使其经过shuffle和sort后反序（从大到小）输出**/
    public static class DescFloatComparator extends FloatWritable.Comparator {
        // @Override
        public float compare(WritableComparator a,
                             WritableComparable<FloatWritable> b) {
            return -super.compare(a, b);
        }

        public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
            return -super.compare(b1, s1, l1, b2, s2, l2);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = Conf.get();
        Job job3 = Job.getInstance(conf, "PageRankViewer");
        job3.setJarByClass(PageRankViewer.class);
        job3.setOutputKeyClass(FloatWritable.class);
        job3.setSortComparatorClass(DescFloatComparator.class);
        job3.setOutputValueClass(Text.class);
        job3.setMapperClass(PageRankViewerMapper.class);
        FileInputFormat.addInputPath(job3, new Path(args[0]));
        FileOutputFormat.setOutputPath(job3, new Path(args[1]));
        job3.waitForCompletion(true);
    }
}


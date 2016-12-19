package com.Java_Spark.www;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.net.URI;

/**
 * Created by Administrator on 2016/3/4.
 */
public class Hadoop_hdfs {

    static final String PATH = "hdfs://master:9000/";
    static final String DIR = "/dir";
    static final String FILE = "/dir";
    static final String User = "root";
    public static void main(String[] args) {

        Frame f = new Frame("broadcastAndaccumulator");
        Button b = new Button("上传");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    put_hdfs();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        f.add(b);
        f.pack();
        f.setVisible(true);
    }
    static void put_hdfs () throws Exception{
        FileSystem fileSystem = FileSystem.get(new URI(PATH),new Configuration(),User);
        //上传文件
        FSDataOutputStream out = fileSystem.create(new Path(FILE));
        FileInputStream in = new FileInputStream("c://test.java");
        IOUtils.copyBytes(in, out, 1024, true);
    }
    static  void download_hdfs() throws Exception {
        FileSystem fileSystem = FileSystem.get(new URI(PATH),new Configuration(),User);
        FSDataInputStream getIn = fileSystem.open(new Path(FILE));
        IOUtils.copyBytes(getIn,System.out,1024,true);
    }
}

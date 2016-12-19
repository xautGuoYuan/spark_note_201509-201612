package com.java.SparkSQL;

import org.apache.calcite.avatica.ColumnMetaData;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/17.
 */
public class RDD2DataFrameByProgrammatically {

    public static void main(String [] args) {

        SparkConf conf = new SparkConf().setMaster("local").setAppName("RDD2DataFrameByProgrammatically");
        JavaSparkContext sc = new JavaSparkContext(conf);
        SQLContext sqlContext = new SQLContext(sc);

        JavaRDD<String> lines = sc.textFile("E:/softwares/spark-1.6.0/examples/src/main/resources/person.txt");
        /**
         * 在RDD的基础上创建类型为Row的RDD
         */
        JavaRDD perpsonsRDD = lines.map(new Function<String, Row>() {
            @Override
            public Row call(String lines) throws Exception {
                String[] splited = lines.split("\t");
                return RowFactory.create(Integer.valueOf(splited[0]), splited[1], Integer.valueOf(splited[2]));
            }
        });

        /**
         * 动态构造DateFrame的元数据
         */
        List<StructField>  structField = new ArrayList<StructField>();
        structField.add(DataTypes.createStructField("id", DataTypes.IntegerType, true));
        structField.add(DataTypes.createStructField("name", DataTypes.StringType, true));
        structField.add(DataTypes.createStructField("age", DataTypes.IntegerType, true));
        //构建StructType，用于最后DataFrame元数据的描述
        StructType structType = DataTypes.createStructType(structField);
        /**
         * 基于已有的MetaData以及RDD<ROW>来构造DataFrame
         */
        DataFrame personsDF = sqlContext.createDataFrame(perpsonsRDD,structType);

        /**
         * 注册成为临时表
         */
        personsDF.registerTempTable("persons");
        /**
         * 进行数据的多维度分析
         */
        DataFrame result = sqlContext.sql("select * from persons");

        result.show();

        /**
         * DataFrame转换成为RDD
         */
        List<Row>  listRow = result.javaRDD().collect();

        for(Row row : listRow) {
            System.out.println(row.getAs("id").toString());
            System.out.println(row);
        }

    }
}

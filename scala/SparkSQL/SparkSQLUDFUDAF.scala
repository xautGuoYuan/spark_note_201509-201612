package scala.SparkSQL

import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/4/5.
 */

/**
 * UDF:User Defined Function，用户自定义的函数，函数的输入时一条具体的数据记录，实现上讲就是普通的scala函数
 * UDAF：User Defined Aggregation Function，用户自定义的聚合函数，函数本身作用于数据集合，能够在聚合操作的基础上进行
 * 自定义操作。
 *
 * 实质上讲，例如说UDF会被Spark SQL中的Catalyst封装成为Expression，最终会通过eval方法来计算输入的数据Row（此处的Row和
 * DataFrame中的Row没有任何关系）
 */
object SparkSQLUDFUDAF {

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("SparkSQLUDFUDAF").setMaster("local[4]")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val bigData = Array("Spark","Spark","hadoop","spark","hadoop","spark","spark","hadoop","spark","hadoop")
    val bigDataRDD = sc.parallelize(bigData)
    val bigDataRDDRow = bigDataRDD.map(item => Row(item))
    val structType = StructType(Array(StructField("word",DataTypes.StringType,true)))
    val bigDataDF = sqlContext.createDataFrame(bigDataRDDRow,structType)
    bigDataDF.registerTempTable("bigDataTable")

    /**
     * 通过SQLContext注册UDF，在Scala 2.10.x版本UDF函数最多可以接受22个输入参数；
     */
    sqlContext.udf.register("computeLength",(input:String) => input.length)

    //直接在SQL语句中使用UDF，就像使用SQL自动的内部函数一样
    sqlContext.sql("select word,computeLength(word) length  from bigDataTable").show()

    sqlContext.udf.register("wordCount",new MyUDAF)
    sqlContext.sql("select word,wordCount(word) as count,computeLength(word) as length from bigDataTable " +
      "group by word").show()

    while(true) {

    }
  }
}
    /**
     * 按照模板实现UDAF
     */
class MyUDAF extends UserDefinedAggregateFunction {
  /**
   * 该方法指定具体输入数据的类型
   * @return
   */
  override def inputSchema: StructType = StructType(Array(StructField("input",DataTypes.StringType,true)))

  /**
   * 在进行聚合操作的时候所要处理的数据的结果的类型
   * @return
   */
  override def bufferSchema: StructType = StructType(Array(StructField("count",DataTypes.IntegerType,true)))

  /**
   * 指定UDAF函数计算后返回的结果类型
   * @return
   */
  override def dataType: DataType = IntegerType

  override def deterministic: Boolean = true

  /**
   * 在aggregate之前每组数据的初始化结果
   * @param buffer
   */
  override def initialize(buffer: MutableAggregationBuffer): Unit = {buffer(0) = 0}

  /**
   * 在进行聚合的时候每当有新的值进来，对分组后的聚合如何进行计算
   * 本地的聚合操作，相当于hadoop MapReduce模型中的combiner
   * @param buffer
   * @param input
   */
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    buffer(0) = buffer.getAs[Int](0) + 1
  }

  /**
   * 最后在分布式节点进行local Reduce完成后需要进行全局级别的Merge操作
   * @param buffer1
   * @param buffer2
   */
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) =  buffer1.getAs[Int](0) +buffer2.getAs[Int](0)
  }

  /**
   * 返回UDAF最后的计算结果
   * @param buffer
   * @return
   */
  override def evaluate(buffer: Row): Any = buffer.getAs[Int](0)
}

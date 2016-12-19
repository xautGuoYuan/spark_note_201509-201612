# spark_note_201509-201612

姓名：郭元
QQ：616526018


这一段时间主要学习了spark1.6及之前的版本
文件夹说明如下：
	com-----spark学习笔记java版本	
		common---主要涉及到模拟数据的生成
		java
			Spark_SQL---主要涉及SparkSQL方面的学习
				：DataFrameOps---DataFrame常用操作
				：RDD2DataFrameByProgrammatically --DF创建方法一
				：RDD2DataFrameByReflection--DF创建方法二
				：SparkSQL链接MySQL数据库
				：SparkSQLJDBC2ThriftServer---Spark SQL Thrift Server
				；SparkSQLWithJoin---sparkSQL join
		java_SparkStreaming---SparkStreaming
			：AdClickedStreamingStats---广告点击（未经测试）
			：broadcastAndaccumulator---广播变量和累加器在SparkStreming中的使用
			：ConnectionPool---简单的数据库连接池
			：FlumePushData2SparkStreaming---Flume推送数据到SparkStreaming
			：SparkStreamingPullDataFromFlume--SparkStreaming拉取Flume中的数据
			：wordCountOnHDFS---SparkStreaming操作HDFS上的数据
			：wordCountOnKafkaDerected---SparkStreaming操作Kafka的数据方式一
			：wordCountOnKafkaReceiver---SparkStreaming操作Kafka的数据方式二
			：wordCountOnLine---SparkStreaming与socket
		Java_Spark ---SparkCore
			www
				：SecondarySortApp  SecondarySortKey----二次排序
				：TopNGroup----分组去TopN
	broadcastAndaccumulator---SparkCore 广播变量与累加器
	
				
	scala---spark学习笔记scala版本
		ALS
			MovieCF   SparkMllibALS ---主要涉及SparkMllib自带的ALS推荐算法
			ItemBased UserBased ---基于物品和基于用户的推荐
		beifengwang
			---scala的学习笔记（Spark从入门到精通scala部分）
		ItemCF
			--基于物品的协同过滤算法--Spark实现
		lianshuchengjin
			--SparkMllib常用算法学习笔记，主要涉及到学习过程中的作业
		ML
			--测试一些SparkMLlib自带的算法
			--朴素贝叶斯，支持向量机，TF-IDF，word2Vector
		MLlib_Book
			--学习《Spark Mllib机器学习实战》主要涉及了一些基础的MLlib
			--包括矩阵，常用算子
		spark_source_test
		SparkSQL
			：DataFrameOps---DataFrame常用操作
			：RDD2DataFrameByProgrammatically --DF创建方法一
			：RDD2DataFrameByReflection--DF创建方法二
			：SparkSQL
			：SparkSQLJDBC2ThriftServer---Spark SQL Thrift Server
			：SparkSQLAgg--agg算子的学习
			：SparkSQLJDBC2MySQL--SparkSQL操作数据库
			：SparkSQLUDFUDAF---udf与udaf学习
			：SparkSQLWindowFunctionOps--窗口函数
			；SparkSQLWithJoin---sparkSQL join
		SparkStreaming
			：和Java版本差不多，所以没有过多编写
		test
		Broadcast---广播变量
		SecondarySortApp  SecondarySortKey----二次排序
		Test_Accumulator --累加器
		test_break--scala中break的使用
		Test_cogroup--cogroup算子的使用
		TopNBasic--topN
		TopNGroup--分组取TopN
		
		

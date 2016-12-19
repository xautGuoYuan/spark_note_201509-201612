package scala.ML

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2016/7/2.
 */
object TF_IDF {

  def main(args: Array[String]) {

    val conf = new SparkConf().setMaster("local[16]").setAppName("TF_IDF")
    val sc = new SparkContext(conf)
    sc.setLogLevel("OFF")

    //数据下载：http://qwone.com/~jason/20Newsgroups/
    val path = "E:\\数据集\\新闻组数据集\\20news-bydate\\20news-bydate-train/*"
    val rdd = sc.wholeTextFiles(path)
    val text = rdd.map{ case (file,text) => text }
    println(text.count())

    //新闻组主题 根据输出信息我们可以得到各个主题中的消息数量基本相等；
    val newsgroups = rdd.map { case (file, text) =>
      file.split("/").takeRight(2).head
    }
    val countByGroup = newsgroups.map(n => (n,1)).reduceByKey(_+_).collect.sortBy(-_._2).mkString("\n")
    println("新闻组主题： "+countByGroup)

    /**
     * 我们文本处理流程的第一步就是切分每一个文档的原始内容为多个单词（也叫作词项），组成集合。这个过程叫做分词。
     * 我们实现最简单的空格分词，并把每个文档的所有单词变为小写；
     * 运行结果 402978
     * 从结果中可以的出来几时相对较小的文本集，不同单词的个数（也就是我们特征向量的维数）也可能会非常高；
     */
    val whiteSpaceSplit = text.flatMap(t => t.split(" ").map(_.toLowerCase()))
    println("单词统计并且转换为小写： "+whiteSpaceSplit.distinct().count())

    /**
     * 之前简单的分词方法会产生很多单词，而且许多不是单词的字符（不如标点符号）没有过滤掉。大部分分词方案都会
     * 把这些字符移除。我们可以使用正则表达式切分原始文档来移除这些非单词字符；
     * 这将极大减少不同单词的数量
     */
    val nonWordSplit = text.flatMap(t => t.split("""\W+""")).map(_.toLowerCase())
    println("移除非法单词： "+nonWordSplit.distinct().count())

    /**
     * 尽管我们使用非单词正则模式来切分文本的效果不错，但仍然有很多包含数字的单词剩下。在有些情况下，数字会成为文档中的
     * 重要内容。但对于我们来说，下一步就是要过滤掉数字和包含数字的单词；
     * 使用正则模式可以过滤掉和这个模式不匹配的单词；
     */
    val regex = """[^0-9]*""".r
    val filterNumbers = nonWordSplit.filter(token =>
      regex.pattern.matcher(token).matches()
    )
    println("移除数字： "+filterNumbers.distinct().count())

    /**
     * 移除停用词
     * 停用词是指出现在一个文本集（和大多数文本集）所有文档中很多次的常用词。标准的英语停用词包括 and、but、the、of等。
     * 提取文本特征的标准做法是从抽取的词中排出停用词。
     * 当使用TF_IDF加权时，加权模式已经做了这点。一个停用词总是很低的IDF分数，会有一个很低的TF-IDF权值，因此成为一个
     * 不重要的词。有些时候，对于信息检索和搜索任务，停用词又需要被包含。但是，最好还是在提取特征时移除停用词，因为这
     * 可以降低特征向量的维度和训练数据的大小。
     *
     * 查看高频词 得到名列前茅的单词
     */
    val tokenCounts = filterNumbers.map(t => (t,1)).reduceByKey(_+_)
    val oreringDesc = Ordering.by[(String,Int),Int](_._2)
    println("移除停用词： "+tokenCounts.top(20)(oreringDesc).mkString("\n"))

    /**
     * （the,146532）
     * (to,75064)
     * (of,...)
     * (a,...)
     * .......
     * 如我们所料，很多常用词可以被标注为停用词。把这些词中的某些词和其他常用词集合成一个停用词集，过滤掉这些词之后
     * 就可以看到剩下的单词
     * 停用词集一般很大，这里展示一小部分（部分原因是为了之后展开TF-IDF对于常用词的影响）
     */
    val stopwords = Set(
      "the","a","an","of","or","in","for","by","on","but","is","not",
    "with","as","was","if",
      "they","are","this","and","it","have","from","at","my",
    "be","that","to"
    )
    val tokenCountsFilteredStopwords = tokenCounts.filter{ case(k,v) =>
      !stopwords.contains(k)
    }
    println("移除停用词： "+tokenCountsFilteredStopwords.top(20)(oreringDesc).mkString("\n"))

    /**
     * 删除那些仅仅含有一个字符的单词，与移除停用词的原因类似
     */
    val tokenCountsFilteredSize = tokenCountsFilteredStopwords.filter{ case (k,v) => k.size >= 2}
    println("移除单个字符： "+tokenCountsFilteredSize.top(20)(oreringDesc).mkString("\n"))

    /**
     * 基于频率去除单词
     *
     * 在分词的时候，还有一种比较常用的去除单词的方法是去掉整个文本库中出现频率很低的单词。例如，检查文本库中
     * 出现频率最低的单词（注意我们致力使用不同的排序方式，返回上升排序的结果）
     */
    val oreringAsc = Ordering.by[(String,Int),Int](-_._2)
    println("查看词频底的词： "+tokenCountsFilteredSize.top(20)(oreringAsc).mkString("\n"))

    /**
     * (lnegth,1)
     * (bluffing,1)
     * (preload,1)
     * ........
     * 只出现一次的单词是没有价值的，因为这些单词我们没有足够的训练数据。
     * 应用另一个过滤函数来排出这些很少出现的单词
     */
    val rareTokens = tokenCounts.filter{ case (k,v) => v < 2}.map{ case (k,v) => k}.collect().toSet
    val tokenCounttsFilteredAll = tokenCountsFilteredSize.filter{ case ( k,v) => !rareTokens.contains(k)}
    println("移除低词频的单词之后： "+tokenCounttsFilteredAll.top(20)(oreringAsc).mkString("\n"))
    println("移除低词频单词： "+tokenCounttsFilteredAll.count) //51801
    /**
     * 通过在分词流程中应用所欲这些过滤步骤，把特征的维度从402978降低到51801
     * 现在把过滤逻辑组合到一个函数中，并应用到RDD中的每个文档；
     */
    def tokenize(line:String):Seq[String] = {
      line.split("""\W+""")
      .map(_.toLowerCase())
      .filter(token => regex.pattern.matcher(token).matches)
      .filterNot(token => stopwords.contains(token))
      .filterNot(token => rareTokens.contains(token))
      .filter(token => token.size >= 2)
      .toSeq
    }
    //通过下面的代码可以检查上面函数是否给出相同的输出
    println("检查： "+text.flatMap(doc => tokenize(doc)).distinct().count())

    /**
     * 我们可以把RDD中每个文档按照下面的方式分词
     */
    val tokens = text.map(doc => tokenize(doc))
    println("每个文档测试结果： "+tokens.first.take(20))

    /**
     * 关于提取词干
     * 提取词干子文本处理和分词中比较常用。这是一个把整个单词转换为一个基的形式（叫做词根）的方法。例如，复数形式可以
     * 转换为单数（dogs变成dog），像walking和walker这样的可以转换为walk。提取词干很复杂，一般通过标准的NLP方法或者
     * 搜索引擎软件实现（例如NLTK、OpenNLP和Lucene）。在这里不做考虑
     */

    /**
     * 训练TF-IDF模型
     * 现在我们使用MLLib把每篇处理成词项形式的文档以向量形式表达。第一步是使用HashingTF实现，它使用特征哈希把每个输入
     * 文本的词项映射为一个词频向量的下标。之后，使用一个全局的IDF向量把词频向量转换为TF-IDF向量。
     *
     * 每个词项的下标是这个词的哈希值（依次映射到特征向量的某个维度）。词项的值是本身的TF-IDF权重
     *
     * 首先，引入我们需要的类，差UN个见一个HasingTF实例，传入的维度参数dim。默认特征维度是pow(20,20)（或者接近100万），
     * 因此我们选择pow(2,18)（或者26000），因为使用50000个单词应该不会产生很多的哈希冲突，而较小的维度赵勇内存更少并且
     * 展示起来更方便。
     */
    import org.apache.spark.mllib.linalg.{SparseVector => SV}
    import org.apache.spark.mllib.feature.HashingTF
    import org.apache.spark.mllib.feature.IDF
    val dim = math.pow(2,18).toInt
    val hashingTF = new HashingTF(dim)
    val tf = hashingTF.transform(tokens)
    tf.cache()

    /**
     * HashingTF的Transform函数把每个输入文档（即词项的序列）映射到一个MLlib的Vector对象。
     * 观察一下转化后数据的第一个元素的信息
     */
    val v = tf.first().asInstanceOf[SV]
    println("v.size: "+v.size)
    println("v.values.size: " + v.values.size)
    println("v.values.take(10).toSeq: "+ v.values.take(10).toSeq)
    println("v.indices.take(10).toSeq : "+ v.indices.take(10).toSeq)

    /**
     * 现在通过创建新的IDF实例并调用RDD的fit方法，利用词频向量作为输入来对文库中的每个单词计算逆向文本频率。之后
     * 使用IDF的transform方法转换词频向量为TF-IDF向量
     */
    val idf = new IDF().fit(tf)
    val tfidf = idf.transform(tf)
    val v2 = tfidf.first.asInstanceOf[SV]
    println("v2.values.size: " + v2.values.size)
    println("v2.values.take(10)： "+ v2.values.take(10).toSeq)
    println("v2.indices.take(10).toSeq: "+ v2.indices.take(10).toSeq)

    /**
     * 分析 TF-IDF权重
     */
    //首先计算整个文档的TF-IDF最小和最大权值
    val minMaxVals = tfidf.map{ v =>
      val sv  = v.asInstanceOf[SV]
      (sv.values.min,sv.values.max)
    }
    val globalMinMax = minMaxVals.reduce{ case ((min1,max1),(min2,max2)) =>
      (math.min(min1,min2),math.max(max1,max2))
    }
    println("globalMinMax: "+ globalMinMax)
    //观察一下不同单词的TF-IDF
    //没有过滤的停用词
    val common = sc.parallelize(Seq(Seq("you","do","we")))
    val tfCommon = hashingTF.transform(common)
    val tfidfCommon = idf.transform(tfCommon)
    val  commonVector = tfidfCommon.first().asInstanceOf[SV]
    println("commonVector.values.toSeq: "+ commonVector.values.toSeq)

    //对不常出现的单词应用相同的转换
    val uncommon = sc.parallelize(Seq(Seq("telescope","legislation","investment")))
    val tfUncommon = hashingTF.transform(uncommon)
    val tfidfUncommon = idf.transform(tfUncommon)
    val  uncommonVector = tfidfUncommon.first().asInstanceOf[SV]
    println("uncommonVector.values.toSeq: "+ uncommonVector.values.toSeq)

    //应用一：使用TF-IDF向量来计算文本相似度
    /**
     * 余弦相似度
     * 我们已经通过TF-IDF把文本转换成向量表示了。
     *
     * 可以认为两个文档共有的单词越多相似度越高，反之相似度越低。因为我们可以通过计算两个向量的点积来计算余弦相似度，而
     * 每一个向量都有文档中的单词构成，所以共有的单词更多的文档余弦相似度也会更高。
     * 我们有理由期待即使非常不同的文档也可能包含很多相同的常用词（例如停用词）。然而，因为较低的TF-IDF权值，这些单词
     * 不会对点积的结果产生较大影响，因此不会对相似度的计算产生太大影响。
     *
     * 例如我们预估两个从曲棒球新闻组随机选择的新闻比较相似。然后看一下是不是这个样子：
     */
    val hockeyText = rdd.filter{ case (file,text) =>
        file.contains("hockey")
    }
    val hockeyTF = hockeyText.mapValues(doc =>
      hashingTF.transform(tokenize(doc))
    )
    val hockeyTfIdf = idf.transform(hockeyTF.map(_._2))

    /**
     * 有了曲棍球文档向量后，就可以随机选择其中两个向量，并计算他们的余弦相似度（使用Breeze的线性代数函数 ，首先把MLlib
     * 向量转换成Breeze稀疏向量）
     */
    import breeze.linalg._
    val hockey1 = hockeyTfIdf.sample(true,0.1,42).first.asInstanceOf[SV]
    val breeze1 = new SparseVector(hockey1.indices,hockey1.values,hockey1.size)
    val hockey2 = hockeyTfIdf.sample(true,0.1,43).first.asInstanceOf[SV]
    val breeze2 = new SparseVector(hockey2.indices,hockey2.values,hockey2.size)
    val cosineSim = breeze1.dot(breeze2)/(norm(breeze1))*(norm(breeze2))
    println(cosineSim)  // 0.06
    /**
     * 这个值看起来很低了，但是文本数据中大量唯一的单词总会使特征点娥有效维度很高。因此，我们可以认为即使两个谈论相同话题
     * 的文档也可能有着较小的相同单词，因而会有较低的相似度分数。
     *
     * 作为对照，我们可以和另一个计算结果做比较，其中一个文档来自曲棒球文档，而另一个文档随机选择自comp.graphics新闻组，
     * 使用完全相同的方法：
     */
    val graphicsText = rdd.filter{ case (file,text) =>
      file.contains("com.graphics")
    }
    val graphicsTF = graphicsText.mapValues { doc =>
      hashingTF.transform(tokenize(doc))
    }
    val graphicsTfIdf = idf.transform(graphicsTF.map(_._2))
    val graphics = graphicsTfIdf.sample(true,0.1,42).first.asInstanceOf[SV]
    val breezeGraphics = new SparseVector(graphics.indices,graphics.values,graphics.size)
    val cosineSim2 = breeze1.dot(breezeGraphics)/(norm(breeze1)) * (norm(breezeGraphics))
    println(cosineSim2)  // 0.0047
    /**
     * 最后，一篇运动相关话题组的文档很可能回合曲棒球文档有较高的相似度。但我们希望谈论的文档不应该和谈论曲棒球的文档
     * 那么相似。下面通过计算来说明是否如此：
     */
    val baseballText = rdd.filter{ case (file,text) =>
      file.contains("com.baseball")
    }
    val baseballTF = baseballText.mapValues { doc =>
      hashingTF.transform(tokenize(doc))
    }
    val baseballTfIdf = idf.transform(baseballTF.map(_._2))
    val baseball = baseballTfIdf.sample(true,0.1,42).first.asInstanceOf[SV]
    val breezeBaseball = new SparseVector(baseball.indices,baseball.values,baseball.size)
    val cosineSim3 = breeze1.dot(breezeBaseball)/(norm(breeze1)) * (norm(breezeBaseball))
    println(cosineSim3)  // 0.05
    //我们找到的棒球和曲棒球文档的余弦相似度是0.05，与comop.graphics文档相比已经很高，但是和另一篇曲棒球文档相比则较低
  }

}

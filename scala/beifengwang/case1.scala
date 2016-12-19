package scala.beifengwang

/**
 * Created by Administrator on 2016/7/31.
 * 移除第一个负数后面所有的负数
 */
object case1 {

  def main(args: Array[String]) {

    import scala.collection.mutable.ArrayBuffer
    val a = ArrayBuffer[Int]()
    a += (1,2,3,4,5,-1,-3,-5,-9)

    /**效率低下，原因是remove的时候都要移动一次数组元素的位置*/
    var foundFirstNegative = false
    var arrayLength = a.length
    var index = 0
    while(index < arrayLength) {
      if(a(index) >= 0) {
        index += 1
      } else {
        if(!foundFirstNegative) {foundFirstNegative = true;index += 1}
        else{a.remove(index);arrayLength -= 1}
      }
    }
    println(a.mkString(","))

    val b = ArrayBuffer[Int]()
    b += (1,2,3,4,5,-1,-3,-5,-9)
    //没记录所有不需要移除的元素的索引，稍后一次性移除需要移除的元素
    //性能较高，数组内的元素迁移只要执行一次即可
    var  foundFirstNegative1 = false
    val keepIndex = for(i <- 0 until b.length if !foundFirstNegative1 || b(i) >0) yield{
      if(b(i) < 0 ) foundFirstNegative1 = true
      i
    }
    for(i <- 0 until keepIndex.length){b(i) = b(keepIndex(i))}
    b.trimEnd(b.length - keepIndex.length)
    println(b.mkString(","))
  }

}

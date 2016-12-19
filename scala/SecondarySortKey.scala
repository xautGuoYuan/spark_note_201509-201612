package scala

/**
 * Created by Administrator on 2016/1/25.
 */
class SecondarySortKey(val first:Int, val second:Int) extends Ordered[SecondarySortKey] with Serializable{
  override def compare(other: SecondarySortKey): Int = {
    if(this.first - other.first != 0) {
      return this.first - other.first;
    } else {
      return this.second - other.second;
    }
  }
}
object  SecondarySortKey {
  def apply(first:Int,second:Int): Unit = {
    new SecondarySortKey(first,second);
  }
}

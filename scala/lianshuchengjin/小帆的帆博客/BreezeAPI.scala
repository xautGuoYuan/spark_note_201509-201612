package scala.lianshuchengjin.小帆的帆博客

import breeze.linalg._

/**
 * Created by Administrator on 2016/6/25.
 */
object BreezeAPI {

  def main(args: Array[String]) {

    /**
     * 运算
     * 加减乘除
     * 向量与向量
     * 加：+
     * 减：-
     * 乘： ：*
     * 除： ：/
     * 规则1：乘法前面，加冒号；单独的乘号和除号分别表示点积和线性求解
     * 规则2：累加效果，加等号
     */
    val v1 = DenseVector(1.0,2.0,3.0,4.0)
    val v2 = DenseVector(0.5,0.5,0.5,0.5)
    println("\nv1+v2 : ")
    println(v1 + v2) //DenseVector(1.5, 2.5, 3.5, 4.5)

    println("\nv1-v2 : ")
    println(v1 - v2) //DenseVector(0.5, 1.5, 2.5, 3.5)

    println("\nv1 :* v2 : ")
    //规则1：乘号前面加了冒号
    println(v1 :* v2) //DenseVector(0.5, 1.0, 1.5, 2.0)

    println("\nv1 :/ v2 :")
    //规则1：除号前面多了冒号
    println(v1 :/ v2) //DenseVector(2.0, 4.0, 6.0, 8.0)

    //规则2
    //如果想把最后的结果保存到v1上，需要加等号
    println("\nv1 += v2 : ")
    println(v1 += v2) //DenseVector(1.5, 2.5, 3.5, 4.5)
    println(v1) //DenseVector(1.5, 2.5, 3.5, 4.5)

    println("\nv1 - v2 : ")
    println(v1 -= v2 ) //DenseVector(1.0, 2.0, 3.0, 4.0)
    println(v1) //DenseVector(1.0, 2.0, 3.0, 4.0)

    println("\nv1 :*= v2 : ")
    println(v1 :*= v2) //DenseVector(0.5, 1.0, 1.5, 2.0)
    println(v1) //DenseVector(0.5, 1.0, 1.5, 2.0)

    println("\nv1 :/= v2 : ")
    println(v1 :/= v2) //DenseVector(1.0, 2.0, 3.0, 4.0)
    println(v1) //DenseVector(1.0, 2.0, 3.0, 4.0)


    /**
     * 矩阵与矩阵
     * 跟向量与向量完全一样
     * 加 ： +
     * 减 ： -
     * 乘 ： :*
     * 除 ： :/
     * 规则1：乘除前面，加冒号；单独的乘号和除号分别表示点积和线性求解
     * 规则：累加效果，加等号
     */
    val m1 = DenseMatrix((1.0,2.0),(3.0,4.0))
    val m2 = DenseMatrix((0.5,0.5),(0.5,0.5))

    /**
     * 1.5  2.5
     * 3.5  4.5
     */
    println("\nm1 + m2 : ")
    println(m1 + m2)

    /**
     *0.5 1.5
     * 2.5  3.5
     */
    println("\nm1 - m2 : ")
    println(m1 - m2)

    /**
     * 0.5  1.0
     * 1.5  2.0
     */
    println("\nm1 :* m2 :")
    //注意：乘号前面多了冒号
    println(m1 :* m2)

    /**
     * 2.0  4.0
     * 6.0  8.0
     */
    println("\nm1 :/ m2 :")
    //注意：除号前面多了冒号
    println(m1 :/ m2)

    //若果想把最后的结果保存到m1上，需要加等号
    /**
     * 1.5  2.5
     * 3.5  4.5
     */
    println("\nm1 += m2 :")
    println(m1 += m2)
    println(m1)

    /**
     * 减法乘除都累死
     */


    /**
     * 矩阵或向量与数值
     * 加 ： +
     * 减 ： -
     * 乘 ： *
     * 除 ： /
     * 规则1：累加效果，加等号
     * 注意：乘除号前不需要冒号，因为没有矩阵与数值的点积等计算
     */

    val vv1 = DenseVector(1.0,2.0,3.0,4.0)

    println("\nvv1 + 0.5 : ")
    println(vv1 + 0.5) //DenseVector(1.5, 2.5, 3.5, 4.5)

    println("\nvv1 - 0.5 : ")
    println(vv1 - 0.5) //DenseVector(0.5, 1.5, 2.5, 3.5)

    println("\nvv1 * 0.5 : ")
    println(vv1 * 0.5) //DenseVector(0.5, 1.0, 1.5, 2.0)

    println("\nvv1 / 0.5 : ")
    println(vv1/0.5) //DenseVector(2.0, 4.0, 6.0, 8.0)

    /**
     *  +=  -=  *=  /= 同上一样
     */

    /**
     * 矩阵与向量
     * 加 ： +
     * 减 ： -
     * 乘 ： :*
     * 除 :  :/
     * 规则1：乘除前面，加冒号；单独的乘号和除号分别表示点积和线性求解
     * 规则2：累加效果，加等号
     * 规则3：必须有星号
     * 规则4：星号在左，逐行；星号在右，逐列；与向量是列向量还是行向量无关
     * 规则5：向量必须是列向量
     */

    val mmm1 = DenseMatrix(
      (1.0,2.0),
      (3.0,4.0)
    )
    val vvv1 = DenseVector(1.0,2.0)
    // val vvv1 = DenseVector(1.0,2.0).t //y运行时异常，规则5，向量必须是列向量
    // val vvv1 = DenseVector(1.0,2.0).t.t //正确，如果是一个列向量，需要转换成行向量

    //规则4：星号在左，逐行；星号在右，逐列
    println("--------------星号在左边，就逐行操作---------------")

    /**
     * 2.0  4.0
     * 4.0  6.0
     */
    println("\nmmm1(*,::) + vvv1 : ")
    println(mmm1(*,::) + vvv1)

    /**
     * 0.0  0.0
     * 2.0  2.0
     */
    println("\nmmm1(*,::) - vvv1 : ")
    println(mmm1(*,::) - vvv1)

    //规则1乘除前面，加冒号

    /** *
      * 1.0 4.0
      * 3.0 8.0
      */
    println("\nmmm1(*,::) :* vvv1 : ")
    println(mmm1(*,::) :* vvv1)

    /**
     * 1.0  1.0
     * 3.0  2.0
     */
    println("\nmmm1(*,::) :/ vvv1 : ")
    println(mmm1(*,::) :/ vvv1)

    println("-------------星号在右边，就逐行操作-------")

    /** *
      * 2.0 3.0
      * 5.0 6.0
      */
    println("\nmmm1(::,*) + vvv1 : ")
    println(mmm1(::,*) + vvv1)

    /**
     * 减法，乘法除法同上
     * 累加同上   :*=   :/=   +=  -=
     */

    /**
     * 函数
     * 统计
     * 求和
     */
    val mmmm1 = DenseMatrix(
      (1.0,2.0),
      (3.0,4.0)
    )

    println("------------矩阵统计求和------------")
    //Axis._0 纵向
    // 4.0  6.0
    println(sum(mmmm1,Axis._0))

    //Axis._1 横向
    //3.0 7.0
    println(sum(mmmm1,Axis._1))

    //均值
    println("-------------均值--------------")
    import breeze.stats.mean
    //Axis._0 纵向
    //2.0 3.0
    println(mean(mmmm1,Axis._0))

    //Axis._1 横向
    //1.5 3.5
    println(mean(mmmm1,Axis._1))

    //方差和标准差
    println("--------------方差和标准差------------")
    import breeze.stats.{stddev,variance}

    //Axis._0 纵向
    //2.0 2.0
    println(variance(mmmm1,Axis._0))

    //Axis._1 横向
    //DenseVector(0.5, 0.5)
    println(variance(mmmm1,Axis._1))

    //Axis._0 纵向
    //1.4142135623730951  1.4142135623730951
    println(stddev(mmmm1,Axis._0))

    //Axis._1 横向
    //DenseVector(0.7071067811865476, 0.7071067811865476)
    println(stddev(mmmm1,Axis._1))

    //N次方和开方
    println("-----------N次方和开方-----------")
    import breeze.numerics.{pow,sqrt}

    /**
     * 1.0  4.0
     * 9.0  16.0
     */
    println(pow(mmmm1,2))

    /**
     * 1.0  8.0
     * 27.0 64.0
     */
    println(pow(mmmm1,3))

    /**
     *1.0                 1.4142135623730951
     * 1.7320508075688772  2.0
     */
    println(sqrt(mmmm1))

    //E和log
    println("----------------E和log------------")
    import breeze.numerics.{exp,log,log10,log1p}

    /**
     * 2.718281828459045   7.38905609893065
     * 20.085536923187668  54.598150033144236
     */
    //e = 2.718281828459045
    println(exp(mmmm1))

    /**
     * 0.0                 0.6931471805599453
     * 1.0986122886681098  1.3862943611198906
     */
    //以e为底
    println(log(mmmm1))

    /**
     * 0.0                  0.3010299956639812
     * 0.47712125471966244  0.6020599913279624
     */
    //以10为底
    println(log10(mmmm1))

    /**
     * 0.6931471805599453  1.0986122886681096
     * 1.3862943611198906  1.6094379124341003
     */
    // 以e为底
    // log1p() 以返回 log(1 + x)，甚至当 x 的值接近零也能计算出准确结果。
    println(log1p(m1))

    /**
     * 三角
     * sin, sinh, asin, asinh
     * cos, cosh, acos, acosh
     * tan, tanh, atan, atanh
     * atan2
     * sinc(x) == sin(x)/x
     * sincpi(x) == sinc(x * Pi)
     *
     */

    //取整
    println("------------取整--------------")
    import breeze.numerics._
    val a = DenseVector(1.4,0.5,-2.3)

    //四舍五入
    println(round(a))

    //向上取整
    println(ceil(a))

    //向下取整
    println(floor(a))

    //大于0，为1；小于0，为-1
    println(signum(a))

    //绝对值
    println(abs(a))

    /**
     * 示例
     * 模拟逻辑回归
     * 利用Breze进行，归一化，添加结局想，预测
     */
    println("------------模拟逻辑回归--------------")

    //随机产生数据
    //val featuresMatrix = DenseMatrix.rand[Double](3,3)
    //val labelMatrix = DenseMatrix.rand[Double](3,1)

    //测试数据
    val featuresMatrix = DenseMatrix(
      (1.0,2.0,3.0),
      (4.0,5.0,6.0),
      (7.0,8.0,9.0)
    )
    val LabelMatrix = DenseMatrix(
      1.0,
      1.0,
      0.0
    )
    //均值
    //DenseVector(4.0,5.0,6.0)
    val featuresMean = mean(featuresMatrix(::, *)).toDenseVector
    println("均值：")
    println(featuresMean)

    //标准差
    //DenseVector(3.0  3.0  3.0  )
    val featuresStddev = stddev(featuresMatrix(::,*)).toDenseVector
    println("\n标准差: ")
    println(featuresStddev)

    //减去均值
    /**
     * -3.0  -2.0  -1.0
     * -1.0  0.0   1.0
     * 1.0   2.0   3.0
     */
    featuresMatrix(*,::) -= featuresMean
    println("\n减去均值：")
    println(featuresMatrix)

    //除以标准差
    /**
     * -1.0                 -0.6666666666666666  -0.3333333333333333
     * -0.3333333333333333  0.0                  0.3333333333333333
     * 0.3333333333333333   0.6666666666666666   1.0
     */
    featuresMatrix(*,::) /= featuresStddev
    println("\n除以标准差： ")
    println(featuresMatrix)

    //生成截距
    /**
     * 1.0
     * 1.0
     * 1.0
     */
    val intercept = DenseMatrix.ones[Double](featuresMatrix.rows,1)
    println("\n截距： ")
    println(intercept)

    //拼接成为最终的训练集
    /**
     *1.0  -1.0                 -0.6666666666666666  -0.3333333333333333
     *1.0  -0.3333333333333333  0.0                  0.3333333333333333
     *1.0  0.3333333333333333   0.6666666666666666   1.0
     */
    val train = DenseMatrix.horzcat(intercept,featuresMatrix)
    println("\n训练集： ")
    println(train)

    //参数
    //为方便检查结果，这里全部设置为1
    /**
     * 1.0
     * 1.0
     * 1.0
     * 1.0
     */
    val w = new DenseMatrix(4,1,Array(1.0,1.0,1.0,1.0))
    //val w = DensMatrix.rand[Double](4,1) //随机生成，一定要指定类型
    println("\n参数 ： ")
    println(w)

    /**
     * -1.0
     * 1.0
     * 3.0
     */
    //随机生成w时，如果没有指定类型，A的计算结果虽然不会报错，但是后面将无法计算，除非使用asInstanceOf
    //如果w指定了类型，那么在idea中，转换语句会是灰色的，意思是这句话没有作用，可以不写
    val A = (train * w).asInstanceOf[DenseMatrix[Double]]
    println("\nA: ")
    println(A)

    /**
     * 0.2689414213699951
     * 0.7310585786300049
     * 0.9525741268224334
     */
    //Signoid函数
    val probability = 1.0/(exp(A * -1.0) + 1.0)
    println("\nprobability ")
    println((probability))

    /**
     * MSE:0.5047245335361858
     */
    val MSE = mean(pow(probability - LabelMatrix,2))
    println("\nMSE: ")
    println(MSE)

    /**
     * RMSE:0.710439676211982
     */
    val RMSE = sqrt(MSE)
    println("\nRMSE: ")
    println(RMSE)

  }

}

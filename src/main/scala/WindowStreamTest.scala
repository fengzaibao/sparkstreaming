import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 *
 */
object WindowStreamTest {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR) //配置日志
    //创建一个本地模式的streamingContext，设定master节点工作线程数为2，以5秒作为批处理时间间隔
    val conf = new SparkConf().setMaster("local[*]").setAppName("saveResultToText")
    val ssc = new StreamingContext(conf, Seconds(2))

    ssc.checkpoint("./chpoint") //window操作与有状态操作  就需要checkpoint
    val lines=ssc.socketTextStream("localhost",9999,StorageLevel.MEMORY_ONLY_SER);
    val words=lines.flatMap(x=>{
      x.split(" ")
    })
    //TODO:采用reducebykeyAndWindow操作进行叠加处理，窗口时间间隔与滑动时间间隔
    //每10秒统计前30秒各单词累计出现的次数
    val wordCounts=words.map(word=>{
      (word,1)
    }).reduceByKeyAndWindow((a:Int,b:Int)=>a+b,Seconds(30),Seconds(10))

    //wordCounts.print(20)

    printValues(wordCounts)
    ssc.start()
    ssc.awaitTermination()
  }
  //定义一个打印函数，打印RDD中所有的元素
  def printValues(stream: DStream[(String, Int)]) { //DStream -> n个RDD组成 ->一个RDD由n条记录组成 -> 一条记录由(String,Int)组成
    stream.foreachRDD(foreachFunc) //不要用foreach() ->foreachRDD
    def foreachFunc=(rdd:RDD[(String,Int)])=>{
      val array=rdd.collect() //采集worker端结果传到driver端
      println("----begin to show results---")
      for(res <- array){
        println(res)
      }
      println("-----ending show results-----")
    }
    println("-----结束搜集-----")
  }
}

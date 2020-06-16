import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object Test5_saveAsText {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR) //配置日志
    //  为什么这个线程不能少于2????
    val conf = new SparkConf().setMaster("local[*]").setAppName("saveResultToText")
    val ssc = new StreamingContext(conf, Seconds(2))

    val input="data/input/"
    val output="data/output/"

    val textStream=ssc.textFileStream(input)
    val wcStream=textStream.flatMap(line=>{
      line.split(" ")
    }).map(word=>{
      (word,1)
    }).reduceByKey(_+_)

    wcStream.print()
    wcStream.saveAsTextFiles(output+"savedTextFile")
    wcStream.saveAsObjectFiles(output+"saveObjectFile")

    ssc.start()
    ssc.awaitTermination()

  }
}

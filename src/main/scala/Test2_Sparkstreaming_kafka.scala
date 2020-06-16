import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010._
object Test2_Sparkstreaming_kafka {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)//配置日志
    val conf = new SparkConf().setMaster("local[*]").setAppName("NetworkWordCount")
    val ssc = new StreamingContext(conf, Seconds(2))

    //当前是consumer端，要反序列化消息
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "node1:9092,node2:9093,node3:9094",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "streaming74",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (true: java.lang.Boolean)//消息位移提交方式
    )

    //要订阅的主题
    val topics=Array("topic74streaming")
    //创建DStream
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)//SubscribePattern:主题名由正则表示    Subscribe：主题名固定  Assign:固定分区
    )
    //ConsumerRecord
    val lines=stream.map(record=>(record.value()))
    val words=lines.flatMap(_.split(" "))
    val wordAndOne=words.map((_,1))
    val reduced=wordAndOne.reduceByKey(_+_)
    reduced.print()
    ssc.start()
    ssc.awaitTermination()
  }
}

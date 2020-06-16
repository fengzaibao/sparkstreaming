import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

/**
 * 从kafka的yc74nameaddrphone主题读取两种消息分类。合并成一个完整的消息输出
 */
object Test4_streamGetDataFromKafka {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR) //配置日志
    val conf = new SparkConf().setMaster("local[*]").setAppName("kafkadatajoin")
      .set("spark.serializer","org.apache.spark.serializer.KryoSerializer")   // 解决:  ERROR KafkaRDD: Kafka ConsumerRecord is not serializable
    val ssc = new StreamingContext(conf, Seconds(2))
    //当前是consumer端，要反序列化消息
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "node1:9092,node2:9093,node3:9094",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "streaming74",    //消费者组编号
      "auto.offset.reset" -> "latest",       //消息从哪里开始读取  latest 从头
      "enable.auto.commit" -> (true: java.lang.Boolean)   //消息的位移提交方式
    )
    //要订阅的主题
    val topics = Array("yc74nameaddrphone")
    //创建DStream
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)    //     SubscribePattern:主题名由正则表示    , Subscribe:主题名固定   Assign:固定分区
    )
    val rdds = stream.cache()  //如不加这一句，则:   KafkaConsumer is not safe for multi-threaded access
    //type为0的数据
    val nameAddrDStream=rdds.map( _.value ).filter( record=>{
      val items=record.split("\t")
      items(2).toInt==0
    }).map(record=>{
      val items=record.split("\t")
      (items(0),items(1))
    })
    //type为1的数据
    val namePhoneDStream=rdds.map( _.value ).filter( record=>{
      val items=record.split("\t")
      items(2).toInt==1
    }).map(record=>{
      val items=record.split("\t")
      (items(0),items(1))
    })
    //join将两个DStream合并
    val r1=nameAddrDStream.join( namePhoneDStream )       //  (name,   ( v1,v2) )
    val r2=r1.map(   record=>{
      s"姓名:${record._1} ,地址:${record._2._1},电话:${record._2._2} "
    })

    r2.print()

    ssc.start()
    ssc.awaitTermination()

  }
}

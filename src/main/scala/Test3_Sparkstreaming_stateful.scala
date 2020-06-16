import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{HashPartitioner, SparkConf}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010._
object Test3_Sparkstreaming_stateful {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR) //配置日志
    val conf = new SparkConf().setMaster("local[*]").setAppName("NetworkWordCount")
    val ssc = new StreamingContext(conf, Seconds(2))

    ssc.checkpoint("./chpoint")   //以后是一个hdfs 对于窗口和有状态的操作必须checkpoint，通过StreamingContext的checkpoint来指定目录，

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
    val topics = Array("topic74streaming")
    //创建DStream
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)    //     SubscribePattern:主题名由正则表示    , Subscribe:主题名固定   Assign:固定分区
    )
    //    ConsumerRecord
    val lines:DStream[String]=stream.map(record => (   record.value)  )
    val words:DStream[String]=lines.flatMap(   _.split(" "))
    val wordAndOne:DStream[(String,Int)]=words.map(   (_,1) )

    //val reduced:DStream[  (String,Int) ]=wordAndOne.reduceByKey( _+_)
    val reduced=wordAndOne.updateStateByKey(   updateFunc, new HashPartitioner(  ssc.sparkContext.defaultMinPartitions  ),  true)

    reduced.print()
    ssc.start()
    ssc.awaitTermination()
  }
  /**
   * iter:   当前操作的RDD
   * String: 聚合的key
   * Seq[Int]: 在这个批次中此key在这个分区出现的次数集合  [1,1,1,1,1].sum()
   * Option[Int]:初始值或累加值   Some   None-> 模式匹配
   */
  val updateFunc= (   iter:Iterator[ (String,Seq[Int] ,  Option[Int] ) ] ) =>{
    //方案一:当成一个三元组运算
    // iter.map(    t=> (  t._1,   t._2.sum+t._3.getOrElse(0)  )    )   //  ->  { word：总次数}
    //方案二: 模式匹配来实现
    iter.map{  case(x,y,z)=>(  x,  y.sum+z.getOrElse(0)   ) }
  }
}


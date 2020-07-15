import com.mongodb.spark.MongoSpark
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}

object SparkMongoYc74ibikeLogAnalysis {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    val conf=new SparkConf()
      .setAppName("MongoSparkRDD")
      .setMaster("local[*]")
      .set("spark.mongodb.input.uri","mongodb://a:a@192.168.19.200:27017/yc74ibike.logs")
      .set("spark.mongodb.output.uri","mongodb://a:a@192.168.19.200:27017/yc74ibike.result")
    val sc=new SparkContext(conf)
    val docsRDD=MongoSpark.load(sc)
    //先缓存数据
    docsRDD.cache()

    println("原始数据")
    val r=docsRDD.collect()
    println(r.toBuffer)
    //计算pv
    val pv=docsRDD.count()//动作操作

    val uv=docsRDD.map(doc=>{
      doc.getString("openid")
    }).distinct().count()
    println("pv:"+pv+"uv:"+uv)
  }
}

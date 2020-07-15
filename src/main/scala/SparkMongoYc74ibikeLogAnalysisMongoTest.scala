import com.mongodb.spark.MongoSpark
import com.mongodb.spark.rdd.MongoRDD
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

import scala.text.Document

object SparkMongoYc74ibikeLogAnalysisMongoTest {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    val session=SparkSession.builder()
      .master("local[*]")
      .appName("MongoSpark")
      .config("spark.mongodb.input.uri","mongodb://192.168.19.200:23000,192.168.19.201:23000,192.168.19.202:23000/mybike.bikes?readPreference=secondaryPreferred")
      .config("spark.mongodb.output.uri","mongodb://192.168.19.200:23000,192.168.19.201:23000,192.168.19.202:23000/mybike.result") //result是用于存分析结果的集合
      .getOrCreate()
    val sc=session.sparkContext
    val docsRDD=MongoSpark.load(sc)
    val result=docsRDD.collect()
    result.foreach(println)

    val df=MongoSpark.load(session)
    df.createTempView("v_bikes")
    val bikes=session.sql("select * from v_bikes")
    MongoSpark.save(bikes) //保存到mongo的result中
    sc.stop()
  }
}

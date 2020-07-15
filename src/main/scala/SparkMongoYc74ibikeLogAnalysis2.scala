import com.mongodb.spark.MongoSpark
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

object SparkMongoYc74ibikeLogAnalysis2 {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    val session=SparkSession.builder()
      .master("local")
      .appName("MongoSparkConnectorIntro")
      .config("spark.mongodb.input.uri","mongodb://a:a@192.168.19.200:27017/yc74ibike.logs")
      .config("spark.mongodb.output.uri","mongodb://a:a@192.168.19.200:27017/yc74ibike.result")
      .getOrCreate()
    val df=MongoSpark.load(session)
    //创建视图
    df.createTempView("v_logs")
    val pv=session.sql("select count(_id) from v_logs")
    println("page view")
    pv.show()

    println("将pv和uv一次性计算出来")
    val uv=session.sql("select count(_id)pv,count(distinct openid)uv from v_logs")
    uv.show()
    MongoSpark.save(uv) //保存到mongo的result中
    session.stop()
  }
}

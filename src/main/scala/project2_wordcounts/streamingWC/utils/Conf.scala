package project2_wordcounts.streamingWC.utils

/*
系统的配置
 */
object Conf {

  val nGram = 3    //分词器单位长度的参数
  val updateFreq = 300000 //5min

  // 分词服务 api
  val segmentorHost = "http://localhost:8282"

  // spark 参数
  val master = "local[*]"
  val localDir = "./tmp"
  val perMaxRate = "5"    // 设定对目标topic每个partition每秒钟拉取的数据条数
  val interval = 3 // seconds
  val executorMem = "1G"    //   内存数/executor
  val coresMax = "3"     //总共最多几个核

  // kafka configuration
  val brokers = "node1:9092,node2:9093,node3:9094"
  val zk = "localhost:2181"
  val group = "wordFreqGroup"
  val topics = "comment"

  // mysql configuration
  val mysqlConfig = Map("url" -> "jdbc:mysql://localhost:3306/word?characterEncoding=UTF-8&serverTimezone=UTC", "username" -> "root", "password" -> "a")
  val maxPoolSize = 5
  val minPoolSize = 2
}
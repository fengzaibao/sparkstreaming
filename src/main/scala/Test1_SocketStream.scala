
  import org.apache.log4j.{Level, Logger}
  import org.apache.spark.SparkConf
  import org.apache.spark.streaming.dstream.ReceiverInputDStream
  import org.apache.spark.streaming.{Seconds, StreamingContext}

  /*
     利用SocketTextStream接口监听本地9999端口传来的数据形成输入DStream,之后利用flatMap对每一条数据按照空格切分映射为新的Dstream,然后再依次操作,
     与spark不同，启动应用后会一直等待计算机发出终止指令后程序才会停止.
     这里我的系统为 macos, 可以用 NetCat运行一个终端，用于对指定端口传输数据，   另外再启一个终端，负责运行spark streaming接收数据，并进行词频统计输出.
     NetCat的mac安装:    brew install netcat
     启动一个服务端并监听9999端口
           nc -l -p 9999
   */
  object Test1_SocketStream {
    def main(args: Array[String]): Unit = {

      Logger.getLogger("org").setLevel(Level.ERROR) //配置日志
      //  为什么这个线程不能少于2????
      val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
      val ssc = new StreamingContext(conf, Seconds(2))

      // 实时流监听的是  localhost,  9999端口
      val lines: ReceiverInputDStream[String] = ssc.socketTextStream("localhost", 9999)

      //进行单词计数
      val words=lines.flatMap(    _.split(" "))
      val pairs=words.map(  word=>(word,1))
      val wordsCounts=pairs.reduceByKey(   _+_ )

      wordsCounts.print()
      ssc.start()   //启动sparkstreaming 程序

      ssc.awaitTermination()//等待系统发出 退出信号
    }
  }


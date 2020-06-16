package project2_wordcounts.wordSetGener

import java.util.{Properties, Random}

import a.b.c.ReadWord
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.io.{BufferedSource, Source}

/**
 *
 * 创建主题: bin/kafka-topics.sh --create --zookeeper localhost:2181  --replication-factor 3  --partitions 3 --topic  comment
 * 主题列表:  bin/kafka-topics.sh --list --zookeeper localhost:2181
 * 查看主题中消息详情: bin/kafka-topics.sh --describe --zookeeper localhost:2181    --topic yc74Wordproducer
 * 发送消息: bin/kafka-console-producer.sh --broker-list node1:9092,node2:9093,node3:9094 --topic yc74Wordproducer
 * 消费消息:
 * bin/kafka-console-consumer.sh --bootstrap-server node1:9092,node2:9093,node3:9094  --topic   yc74Wordproducer  --from-beginning
 *
 * 生产消息到Kafka
 * 两种类型:    姓名+地址  nameAddr     姓名+电话 namePhones
 */
object Product {
  def main(args: Array[String]): Unit = {
    val event = 10
    val topic = "comment"
    val props = new Properties()
    props.put("bootstrap.servers", "node1:9092,node2:9093,node3:9094")
    props.put("acks", "all") // 确认的级别  ISR
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer") //生产端用序列化
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    val line: BufferedSource = Source.fromFile("data2/hanzi.txt")
    val rd = new Random();
    val li = line.mkString
    //随机发送10条消息
    for (i <- Range(0, event)) {
      val sb = new StringBuilder()
      //随机生成20个数据
      for (ind <- Range(0, rd.nextInt(200))) {
        sb += li.charAt(rd.nextInt(li.length))
      }
      val userkey = "User" + rd.nextInt(100)
      //发送消息
      val producer = new KafkaProducer[String, String](props);
      val pr = new ProducerRecord[String, String](topic, userkey, sb.toString()) // 0表示消息的类型  name 地址 类型
      producer.send(pr)
    }
    println("生产消息完成....")
  }
}

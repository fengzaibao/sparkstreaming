import java.util.{Properties, Random}

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

/**
 *
 * 创建主题: bin/kafka-topics.sh --create --zookeeper localhost:2181  --replication-factor 3  --partitions 3 --topic  yc74nameaddrphone
 * 主题列表:  bin/kafka-topics.sh --list --zookeeper localhost:2181
 * 查看主题中消息详情: bin/kafka-topics.sh --describe --zookeeper localhost:2181    --topic yc74nameaddrphone
 * 发送消息: bin/kafka-console-producer.sh --broker-list localhost:9092,localhost:9093,localhost:9094 --topic yc74nameaddrphone
 * 消费消息:
 * bin/kafka-console-consumer.sh --bootstrap-server localhost:9092,localhost:9093,localhost:9094  --topic   yc74nameaddrphone  --from-beginning
 *
 * 生产消息到Kafka
 * 两种类型:    姓名+地址  nameAddr     姓名+电话 namePhones
 */
object Test4_kafkaProducer {
  def main(args: Array[String]): Unit = {
    val topic="yc74nameaddrphone"
    val props=new Properties()
    props.put("bootstrap.servers","node1:9092,node2:9093,,node3:9094")
    props.put("acks","all") //确认级别
    props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer")//生产端用序列化
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer= new KafkaProducer[String,String](props);
    val nameAddrs=Map("smith"->"海南","tom"->"湖南","john"->"北京")
    val namePhones=Map("smith"->"11111111","tom"->"22222222","john"->"33333333")

    val rnd=new Random()

    for(nameAddr <- nameAddrs){
      val pr=new ProducerRecord[String,String](topic,nameAddr._1,s"${nameAddr._1}\t${nameAddr._2}\t0")// 0表示消息的类型  name 地址 类型
      producer.send(pr)

      Thread.sleep(rnd.nextInt(10))
    }

    for(namePhone <- namePhones){
      val pr=new ProducerRecord[String,String](topic,namePhone._1,s"${namePhone._1}\t${namePhone._2}\t1")// 1表示消息的类型为  name phone 类型
      producer.send(pr)
      Thread.sleep(rnd.nextInt(10))
    }
    producer.close()
  }
}

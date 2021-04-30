// http://www.stackframelayout.com/programowanie/kafka-simple-producer-consumer-example/
package _005_mykafka


import java.util.concurrent.Future
import java.util.{Collections, Properties}

import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}

import scala.collection.JavaConversions._

object HelloKafka {

  def main(args: Array[String]): Unit = {

    val topic = util.Try(args(0)).getOrElse("mytopic")
    println(s"Connecting to $topic")

    // PRODUCER
    val producerProperties = new Properties()
    producerProperties.put("bootstrap.servers", "127.0.0.1:9092")
    producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[String, String](producerProperties)

    // CONSUMER
    val consumerProperties = new Properties()
    consumerProperties.put("bootstrap.servers", "127.0.0.1:9092")
    consumerProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    consumerProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    consumerProperties.put("group.id", "dummy-group")
    val consumer = new KafkaConsumer[String, String](consumerProperties)
    consumer.subscribe(Collections.singletonList(topic))



    var count = 0

    while(true) { //for(i<- 1 to 50)
      count+=1

      val myDateTimeFormat = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy H:mm:ss")
      val myNow = java.time.LocalDateTime.now().format(myDateTimeFormat)

      // WRITING
      val record = new ProducerRecord[String, String](topic, "key"+count, "value"+count+ s" $myNow")

      // producer.send(record)
      // val myValuable = producer.send(record)

      // send returns future
      val metadataFuture: Future[RecordMetadata] = producer.send(record)
      val metadata = metadataFuture.get() // blocking!
      /* val msgLog =
      s"""
      |offset = ${metadata.offset()}
      |partition = ${metadata.partition()}
      |topic = ${metadata.topic()}
      """.stripMargin
      println(msgLog)
      */
      println
      println("producer callback:")
      println("checksum " + metadata.checksum())
      println("offset " + metadata.offset())
      println("partition " + metadata.partition())
      println("serialized key size " + metadata.serializedKeySize())
      println("serialized value size " + metadata.serializedValueSize())
      println("timestamp " + metadata.timestamp())
      println("topic " + metadata.topic())


      Thread.sleep(7000)

      // READING
      val records = consumer.poll(2000)
      for(record:ConsumerRecord[String, String] <- records) {
        println
        println("consumer reading")
        println("checksum " + record.checksum)
        println("key " + record.key)
        println("offset " + record.offset)
        println("partition " + record.partition )
        println("serializedKeySize " + record.serializedKeySize )
        println("serializedValueSize " + record.serializedValueSize )
        println("timestamp " + record.timestamp )
        println("timestampType " + record.timestampType )
        println("topic " + record.topic )
        println("value " + record.value )
      }
    }

    producer.close()
    consumer.close()

  }
}
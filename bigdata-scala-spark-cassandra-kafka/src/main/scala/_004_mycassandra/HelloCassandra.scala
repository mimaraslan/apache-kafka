package _004_mycassandra
import com.datastax.spark.connector._
import org.apache.spark.{SparkConf, SparkContext}
//import spark.implicits._

object HelloCassandra {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
      .setAppName("HelloCassandra")
      .setMaster("local[2]")
    //.set("spark.cassandra.connection.host", "192.168.1.4") // IP NO ILE
    //.set("spark.cassandra.auth.username", "cassandra")
    //.set("spark.cassandra.auth.password", "########")

    val sc = new SparkContext(conf)

    val test_spark_rdd = sc.cassandraTable("lab", "movies")
    test_spark_rdd.take(10).foreach(println)
    //println(test_spark_rdd.first)
    //test_spark_rdd.collect().foreach(println)
    //val counts = test_spark_rdd.count
    //println(counts)
    //
    //test_spark_rdd.toDF.write.csv("/home/my/Desktop/lolo")
  }
}
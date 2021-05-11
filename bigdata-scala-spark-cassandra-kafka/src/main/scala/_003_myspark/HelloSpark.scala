package _003_myspark

import org.apache.spark.{SparkConf, SparkContext}

object HelloSpark {

  def main(args: Array[String]) {

    //Create a SparkContext to initialize Spark
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("Word Count")
    val sc = new SparkContext(conf)

    // Load the text into a Spark RDD, which is a distributed representation of each line of text
    val textFile = sc.textFile("src/main/resources/shakespeare.txt")

    //word count
    val counts = textFile.flatMap(line => line.split(" "))
      .map(word => (word, 1))
      .reduceByKey(_+_)

    counts.foreach(println)
    System.out.println("Total words: " + counts.count());
    counts.saveAsTextFile("src/main/resources/shakespeareWordCount")
  }

}







/*
import org.apache.spark.{SparkConf, SparkContext}

object HelloSpark {

def main(args: Array[String]) {

//Create a SparkContext to initialize Spark
val conf = new SparkConf()
// conf.setMaster("local")
conf.setMaster(args(0))
conf.setAppName("Word Count")
val sc = new SparkContext(conf)

// Load the text into a Spark RDD, which is a distributed representation of each line of text
// val textFile = sc.textFile("src/main/resources/shakespeare.txt")
val textFile = sc.textFile(args(1))

//word count
val counts = textFile.flatMap(line => line.split(" "))
.map(word => (word, 1))
.reduceByKey( + )

counts.foreach(println)
System.out.println("Total words: " + counts.count());
//counts.saveAsTextFile("/tmp/shakespeareWordCount")
counts.saveAsTextFile(args(2))
}

}
*/
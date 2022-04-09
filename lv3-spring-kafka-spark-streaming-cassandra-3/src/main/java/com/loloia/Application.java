package com.loloia;

import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.CassandraStreamingJavaUtil;
import com.datastax.spark.connector.japi.rdd.CassandraTableScanJavaRDD;
import com.loloia.cassandra.CassandraMain;
import com.loloia.helpers.CassandraProperties;
import com.loloia.helpers.KafkaProperties;
import com.loloia.helpers.NewsClientParams;
import com.loloia.kafka.Kafka;
import com.loloia.model.Article;
import com.loloia.model.Words;
import com.loloia.rest.Client;
import com.loloia.spark.KafkaSparkConnector;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Mapper;


import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapRowTo;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapToRow;





public class Application {

	private static String topicName = KafkaProperties.TOPIC_NAME;
	private static Collection<String> topics = Arrays.asList(topicName);
	
	private static String keySpaceName = CassandraProperties.keySpaceName;
	private static String tableName = CassandraProperties.tableName; 
	
	
	/*
	private void persistWindowWords(JavaRDD<Words> rddWord) {
	    // Map Cassandra table column
	    Map<String, String> columnNameMappings = new HashMap<>();
	    columnNameMappings.put("routeId", "routeid");
	    columnNameMappings.put("vehicleType", "vehicletype");

	
	    // call CassandraStreamingJavaUtil function to save in DB
	    CassandraJavaUtil.javaFunctions(rddWord).writerBuilder(
	            "keySpaceName",
	            "tableName",
	            CassandraJavaUtil.mapToRow(Words.class, columnNameMappings)
	    ).saveToCassandra();
	}
	 */
	
	
	public static void main(String[]args) {
				
		// Cassandra
		CassandraMain cass = new CassandraMain(CassandraProperties.hostName, CassandraProperties.clusterName, 0);
		cass.createKeySpace(keySpaceName);
		cass.session.execute("CREATE TABLE IF NOT EXISTS news_words (word text, occurences int, PRIMARY KEY(word));");		
		
		//Mapper doesn't work with a Spark context. Thus using a Spark-Cassandra connector
		//MappingManager mp = cass.createMappingManager();		
		//Mapper<Words> mapper = mp.mapper(Words.class);
		
//		NewsClientParams newsParams = new NewsClientParams();
//		Client newsClients = new Client(newsParams.baseUri, newsParams.newsParams, newsParams.topHeadlines);
//		ArrayList<String> listName = newsClients.response.getBody().jsonPath().getJsonObject("'articles'.'source'.'name'");
//		ArrayList<String> listAuthor = newsClients.response.getBody().jsonPath().getJsonObject("'articles'.'author'");
//		ArrayList<String> listTitle = newsClients.response.getBody().jsonPath().getJsonObject("'articles'.'title'");
	
		// Kafka for TEST DATA
		 ArrayList<String> listName=new ArrayList<String>();  		 
		 ArrayList<String> listAuthor=new ArrayList<String>();  		 
		 ArrayList<String> listTitle=new ArrayList<String>();  		 
		 
		 for (int i = 0; i < 5; i++) {
			 listName.add("Name"+i);  
			 listAuthor.add("Author"+i);
			 listTitle.add("Title"+i);
			}
		 
		
		KafkaProperties kafkaProperties = new KafkaProperties();		
		Kafka kafka = new Kafka(topicName);
		KafkaProducer<String, String> kafkaProducer = kafka.createProducer(kafkaProperties.KafkaProducerProperties());

		
		// from Kafka to Spark
		KafkaSparkConnector kafkaSparkConnector = new KafkaSparkConnector(); 
		
		JavaPairDStream<String, String> kafkaStream = kafkaSparkConnector.createKafkaDirectStream(topics);				
		
		
		
		kafkaSparkConnector.processKafkaMessage(kafkaStream)
			.foreachRDD(rdd->{
							//	System.out.println("--- RDD size: " + rdd.partitions().size()
							//			+ " partitions: " + rdd.partitions() 
							//			+ " name: " + rdd.name());
								
								Map<String, Integer> wordCountMap = rdd.collectAsMap();
								
								 JavaRDD<Words> rddWord = null;
								for(String key : wordCountMap.keySet()) {
									List<Words> wordList = Arrays.asList(new Words(key.replaceAll("[^a-zA-Z]+",""), wordCountMap.get(key)));
								
									rddWord = kafkaSparkConnector.sparkContext.streamingContext.sparkContext().parallelize(wordList);
								//	rddWord.collect().forEach(System.out::println);
							    }

								rdd.foreach(record ->System.out.println(record));
							//	javaFunctions(rddWord).writerBuilder(keySpaceName,
							//	        		tableName, CassandraJavaUtil.mapToRow(Words.class))
							//	       .saveToCassandra();
								
								
							//	javaFunctions(rddWord).writerBuilder(keySpaceName, tableName, mapToRow(Words.class)).saveToCassandra();

								
							
								
								//wordCountMap.forEach((k, v) -> System.out.println(k + " " + v));
							});
		
	//	JavaRDD<Words> rddWord2 = null;
	//	javaFunctions(rddWord2).writerBuilder(keySpaceName, tableName, 
	//		CassandraJavaUtil.mapToRow(Words.class))
	//			.saveToCassandra();							        
		
		kafkaSparkConnector.sparkContext.streamingContext.start();
		
	
		for(int i = 0; i < listName.size(); i++) {
			Article article = new Article(listName.get(i)   != null ? listName.get(i)   : "noCompany",
										  listAuthor.get(i) != null ? listAuthor.get(i) : "noAuthor", 
										  listTitle.get(i) != null ?  listTitle.get(i) : "noTitle" );
			try {
				kafka.runKafkaProducer(article, kafkaProducer);
				Thread.sleep(1);
			//	System.out.println("runKafkaProducer:"+article.getArticleTitle());
			} catch (NotSerializableException e) {
				System.out.println("NotSerializableException:");
				e.printStackTrace();
			} catch (InterruptedException e) {
				System.out.println("InterruptedException:");
				e.printStackTrace();
			}
		}
		
		
	
		
		try {
			kafkaSparkConnector.sparkContext.streamingContext.awaitTermination();
		} catch (InterruptedException e) {
			System.out.println("InterruptedException");
			e.printStackTrace();
		}
		
		kafkaSparkConnector.sparkContext.streamingContext.stop();
		kafkaProducer.close();
	//	String selectQuery = "SELECT * FROM " + CassandraProperties.keySpaceName + "." + "news_Words"; 
	//	System.out.println("news_words CONTAINS:\n" + cass.session.execute(selectQuery));
		cass.closeConnection();
	}
}
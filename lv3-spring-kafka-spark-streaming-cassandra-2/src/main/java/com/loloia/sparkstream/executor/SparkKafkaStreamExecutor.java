package com.loloia.sparkstream.executor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kafka.serializer.StringDecoder;

@Component
public class SparkKafkaStreamExecutor implements Serializable,Runnable{

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(SparkKafkaStreamExecutor.class);
	
	@Value("${spark.stream.kafka.durations}")
	private String streamDurationTime;
	
	@Value("${kafka.broker.list}")
	private String metadatabrokerlist;
	
	@Value("${spark.kafka.topics}")
	private String topicsAll;
	
	@Autowired
	private transient Gson gson;

	private transient JavaStreamingContext jsc;
	@Autowired 
	private transient JavaSparkContext javaSparkContext;
	
	@Override
	public void run() {
		startStreamTask();
	}
	
	public void startStreamTask() {
		// System.setProperty("hadoop.home.dir", "D:\\hadoop-2.7.5");
		Set<String> topics = new HashSet<String>(Arrays.asList(topicsAll.split(",")));

		Map<String, String> kafkaParams = new HashMap<>();
		kafkaParams.put("metadata.broker.list", metadatabrokerlist);
		
		jsc = new JavaStreamingContext(javaSparkContext,
				Durations.seconds(Integer.valueOf(streamDurationTime)));
		jsc.checkpoint("checkpoint"); 

		final JavaPairInputDStream<String, String> stream = KafkaUtils.createDirectStream(jsc, String.class,
				String.class, StringDecoder.class, StringDecoder.class, kafkaParams, topics);
		System.out.println("stream started!");
		stream.print();
		stream.foreachRDD(v -> {

			List<String> topicDatas = v.values().collect();
			for (String topicData : topicDatas) {
//				List<Map<String, String>> list = gson.fromJson(topicData, new TypeToken<List<Map<String, String>>>() {}.getType());				
//				list.parallelStream().forEach(m->{
//					System.out.println(m);
//				});
				
				/*
				{"employees":[{"name":"Lolo1", "technology":"Java1"}]}
				{"employees":[{"name":"Lolo2", "technology":"Java2"}]}
				{"employees":[{"name":"Lolo3", "technology":"Java3"}]}
				*/
			    String jsonString = topicData; // "{\"employees\":[{\"name\":\"Lolo1\", \"technology\":\"Java1\"}]}";
				Software software = gson.fromJson(jsonString, Software.class);
			      System.out.println(software);
			}
			log.info("一topicDatas {}",topicDatas);
		});

//		stream.foreachRDD(t->{
//			t.foreachPartition(f->{
//				while(f.hasNext()) {
//					Map<String, Object> symbolLDAHandlered =LDAModelPpl
//							.LDAHandlerOneArticle(sparkSession, SymbolAndNews.symbolHandlerOneArticle(sparkSession, f.next()._2));
//				}
//			});
//		});
		jsc.start();
	}

	public void destoryStreamTask() {
		if(jsc!=null) {
			jsc.stop();
		}		
	}

	class Software {
		List<Employee> employees;

		@Override
		public String toString() {
			return "Software [employees=" + employees + "]";
		}
	}

	class Employee {
		String name;
		String technology;

		@Override
		public String toString() {
			return "Employee [name=" + name + ", technology=" + technology + "]";
		}
	}
}

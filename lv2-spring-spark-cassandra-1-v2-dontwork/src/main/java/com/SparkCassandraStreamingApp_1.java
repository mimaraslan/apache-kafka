package com;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapRowTo;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapToRow;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.Optional;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;

import com.datastax.driver.core.Session;
import com.datastax.spark.connector.cql.CassandraConnector;
import com.lolo.utils.PropertyFileReader;

import scala.Tuple2;

public class SparkCassandraStreamingApp_1 {

	private static final Logger logger = Logger.getLogger(SparkCassandraStreamingApp_1.class);

	public static void main(String[] args) {

		Properties prop = new Properties();
		try {
			prop = PropertyFileReader.readPropertyFile();
		} catch (Exception e1) {
			logger.error(e1.getMessage());
		}

		SparkConf conf = new SparkConf();
		conf.setAppName(prop.getProperty("app.name"));
		conf.setMaster("local[*]");
		conf.set("spark.cassandra.connection.host", prop.getProperty("spring.data.cassandra.host"));
		conf.set("spark.cassandra.connection.port", prop.getProperty("spring.data.cassandra.port"));

		JavaSparkContext sc = new JavaSparkContext(conf);

		generateData(sc);
		compute(sc);
		showResults(sc);
		sc.stop();
	}

	private static void showResults(JavaSparkContext sc) {

		JavaPairRDD<Integer, Summary> summariesRdd = javaFunctions(sc)
				.cassandraTable("java_api", "summaries", mapRowTo(Summary.class))
				.keyBy(new Function<Summary, Integer>() {

					private static final long serialVersionUID = 8411725791557810903L;

					@Override
					public Integer call(Summary summary) throws Exception {
						return summary.getProduct();
					}
				});

		JavaPairRDD<Integer, Product> productsRdd = javaFunctions(sc)
				.cassandraTable("java_api", "products", mapRowTo(Product.class))
				.keyBy(new Function<Product, Integer>() {

					private static final long serialVersionUID = 2377857907907177129L;

					@Override
					public Integer call(Product product) throws Exception {
						return product.getId();
					}
				});

		List<Tuple2<Product, Optional<Summary>>> results = productsRdd.leftOuterJoin(summariesRdd).values().collect();

		for (Tuple2<Product, Optional<Summary>> result : results) {
			System.out.println(result);
		}
	}

	private static void compute(JavaSparkContext sc) {

		JavaPairRDD<Integer, Product> productsRDD = javaFunctions(sc)
				.cassandraTable("java_api", "products", mapRowTo(Product.class))
				.keyBy(new Function<Product, Integer>() {

					private static final long serialVersionUID = 6528637475612197902L;

					@Override
					public Integer call(Product product) throws Exception {
						return product.getId();
					}
				});

		JavaPairRDD<Integer, Sale> salesRDD = javaFunctions(sc)
				.cassandraTable("java_api", "sales", mapRowTo(Sale.class)).keyBy(new Function<Sale, Integer>() {

					private static final long serialVersionUID = -7154131145886085732L;

					@Override
					public Integer call(Sale sale) throws Exception {
						return sale.getProduct();
					}
				});

		JavaPairRDD<Integer, Tuple2<Sale, Product>> joinedRDD = salesRDD.join(productsRDD);

		JavaPairRDD<Integer, BigDecimal> allSalesRDD = joinedRDD
				.flatMapToPair(new PairFlatMapFunction<Tuple2<Integer, Tuple2<Sale, Product>>, Integer, BigDecimal>() {

					private static final long serialVersionUID = -1708704007911053388L;

					@Override
					public Iterator<Tuple2<Integer, BigDecimal>> call(Tuple2<Integer, Tuple2<Sale, Product>> input)
							throws Exception {
						Tuple2<Sale, Product> saleWithProduct = input._2();
						List<Tuple2<Integer, BigDecimal>> allSales = new ArrayList<>(
								saleWithProduct._2().getParents().size() + 1);
						allSales.add(new Tuple2<>(saleWithProduct._1().getProduct(), saleWithProduct._1().getPrice()));
						for (Integer parentProduct : saleWithProduct._2().getParents()) {
							allSales.add(new Tuple2<>(parentProduct, saleWithProduct._1().getPrice()));
						}
						return allSales.iterator();
					}
				});

		JavaRDD<Summary> summariesRDD = allSalesRDD.reduceByKey(new Function2<BigDecimal, BigDecimal, BigDecimal>() {

			private static final long serialVersionUID = 7948252344390236052L;

			@Override
			public BigDecimal call(BigDecimal v1, BigDecimal v2) throws Exception {
				return v1.add(v2);
			}
		}).map(new Function<Tuple2<Integer, BigDecimal>, Summary>() {

			private static final long serialVersionUID = -1130784219003261735L;

			@Override
			public Summary call(Tuple2<Integer, BigDecimal> input) throws Exception {
				return new Summary(input._1(), input._2());
			}
		});

		javaFunctions(summariesRDD).writerBuilder("java_api", "summaries", mapToRow(Summary.class)).saveToCassandra();
	}

	private static void generateData(JavaSparkContext sc) {

		CassandraConnector connector = CassandraConnector.apply(sc.getConf());

		logger.info("GenerateData Started...");

		// Prepare the schema
		try (Session session = connector.openSession()) {
			session.execute("DROP KEYSPACE IF EXISTS java_api");
			session.execute(
					"CREATE KEYSPACE java_api WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");
			session.execute("CREATE TABLE java_api.products (id INT PRIMARY KEY, name TEXT, parents LIST<INT>)");
			session.execute("CREATE TABLE java_api.sales (id UUID PRIMARY KEY, product INT, price DECIMAL)");
			session.execute("CREATE TABLE java_api.summaries (product INT PRIMARY KEY, summary DECIMAL)");
		}

		logger.info("Products & Sales Summeries tables are created...");

		// Prepare the products hierarchy
		List<Product> products = Arrays.asList(new Product(0, "All products", Collections.<Integer>emptyList()),
				new Product(1, "Product A", Arrays.asList(0)), new Product(4, "Product A1", Arrays.asList(0, 1)),
				new Product(5, "Product A2", Arrays.asList(0, 1)),

				new Product(2, "Product B", Arrays.asList(0)), new Product(6, "Product B1", Arrays.asList(0, 2)),
				new Product(7, "Product B2", Arrays.asList(0, 2)),

				new Product(3, "Product C", Arrays.asList(0)), new Product(8, "Product C1", Arrays.asList(0, 3)),
				new Product(9, "Product C2", Arrays.asList(0, 3)));

		JavaRDD<Product> productsRDD = sc.parallelize(products);

		javaFunctions(productsRDD).writerBuilder("java_api", "products", mapToRow(Product.class)).saveToCassandra();

		logger.info("Products hierarchy prepared...");

		JavaRDD<Sale> salesRDD = productsRDD.filter(new Function<Product, Boolean>() {

			private static final long serialVersionUID = 6660479314919774583L;

			@Override
			public Boolean call(Product product) throws Exception {
				return product.getParents().size() == 2;
			}
		}).flatMap(new FlatMapFunction<Product, Sale>() {

			private static final long serialVersionUID = 6168643700702528212L;

			@Override
			public Iterator<Sale> call(Product product) throws Exception {

				Random random = new Random();
				List<Sale> sales = new ArrayList<Sale>(10);
				for (int i = 0; i < 10; i++) {
					sales.add(new Sale(UUID.randomUUID(), product.getId(), BigDecimal.valueOf(random.nextDouble())));
				}
				return sales.iterator();
			}

		});

		javaFunctions(salesRDD).writerBuilder("java_api", "sales", mapToRow(Sale.class)).saveToCassandra();

		logger.info("Sales Table Populated with Products...");
	}

	public static class Product implements Serializable {

		private static final long serialVersionUID = -3893364875076683712L;

		private Integer id;
		private String name;
		private List<Integer> parents;

		public Product() {
		}

		public Product(Integer id, String name, List<Integer> parents) {
			this.id = id;
			this.name = name;
			this.parents = parents;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<Integer> getParents() {
			return parents;
		}

		public void setParents(List<Integer> parents) {
			this.parents = parents;
		}

		@Override
		public String toString() {
			return MessageFormat.format("Product'{'id={0}, name=''{1}'', parents={2}'}'", id, name, parents);
		}
	}

	public static class Sale implements Serializable {

		private static final long serialVersionUID = -5862308582205961429L;

		private UUID id;
		private Integer product;
		private BigDecimal price;

		public Sale() {
		}

		public Sale(UUID id, Integer product, BigDecimal price) {
			this.id = id;
			this.product = product;
			this.price = price;
		}

		public UUID getId() {
			return id;
		}

		public void setId(UUID id) {
			this.id = id;
		}

		public Integer getProduct() {
			return product;
		}

		public void setProduct(Integer product) {
			this.product = product;
		}

		public BigDecimal getPrice() {
			return price;
		}

		public void setPrice(BigDecimal price) {
			this.price = price;
		}

		@Override
		public String toString() {
			return MessageFormat.format("Sale'{'id={0}, product={1}, price={2}'}'", id, product, price);
		}
	}

	public static class Summary implements Serializable {

		private static final long serialVersionUID = -837379470217195195L;

		private Integer product;
		private BigDecimal summary;

		public Summary() {
		}

		public Summary(Integer product, BigDecimal summary) {
			this.product = product;
			this.summary = summary;
		}

		public Integer getProduct() {
			return product;
		}

		public void setProduct(Integer product) {
			this.product = product;
		}

		public BigDecimal getSummary() {
			return summary;
		}

		public void setSummary(BigDecimal summary) {
			this.summary = summary;
		}

		@Override
		public String toString() {
			return MessageFormat.format("Summary'{'product={0}, summary={1}'}'", product, summary);
		}
	}
}

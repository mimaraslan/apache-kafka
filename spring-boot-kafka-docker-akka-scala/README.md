# spring-boot-kafka-docker-akka-scala

### Set up

    mvn clean install

    docker-compose up --build -d

### Create Customer

    -XPOST http://localhost:8080/customers
    {
	     "id" : 1,
	     "firstName": "Katerina",
	     "lastName": "Lena",
	     "metaInformation" : "test1 test2"
    }
    
### Search elasticsearch 

    http://localhost:9200/systemindex/_search    

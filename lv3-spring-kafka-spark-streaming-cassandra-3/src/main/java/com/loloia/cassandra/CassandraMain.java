package com.loloia.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.mapping.MappingManager;

public class CassandraMain {

	public Session session;
	public Cluster cluster;
	
	public CassandraMain(String hostAddress, String clusterName, int port) {
		Builder clusterBuilder = Cluster
						.builder()
						.addContactPoint(hostAddress);
		if(port > 0)
			clusterBuilder.withPort(port);
		this.cluster = clusterBuilder
						.withClusterName(clusterName)
						.withProtocolVersion(ProtocolVersion.NEWEST_SUPPORTED)
						.build();
	}
	
	public void closeConnection() {
		
		if(this.session != null)
			this.session.close();
		
		if(this.cluster != null)
			this.cluster.close();
	}
	
	/***
	 * 
	 * @param keySpaceName to connect
	 * @return true if connected and false if a KeySpace doesn't exist
	 */
	public Boolean connectToKeySpace(String keySpaceName) {
		Boolean result = null;
		try {
			this.session = this.cluster.connect(keySpaceName);
			StringBuilder sb = new StringBuilder();
			sb.append("USE ")
				.append(keySpaceName)
				.append(";");
			this.session.execute(sb.toString());
			System.out.println("Connected to KeySpace " + session.getLoggedKeyspace());
			result = true;
		}
		catch(NoHostAvailableException e) {
			return false;
		}
		return result;		
	}
	
	/***
	 * 
	 * @param nameSpaceName to create
	 * @return true if a KeySpace is created and false if a KeySpace existed before
	 */
	public void createKeySpace(String keySpaceName) {
		this.session = this.cluster.connect();
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE KEYSPACE IF NOT EXISTS ")
			.append(keySpaceName)
			.append("\nWITH replication = {\n")
			.append("'class' : 'SimpleStrategy', 'replication_factor' : 1};\n");
		this.session.execute(sb.toString());
		this.session.execute("USE " + keySpaceName + ";");
		System.out.println("KeySpace " + session.getLoggedKeyspace() + " was created");
	}
	
	public MappingManager createMappingManager() {
		MappingManager mapMan = null;
		try {
			mapMan = new MappingManager(this.session);
		}
		catch(IllegalArgumentException e) {
			System.out.println("Couldn't create a Mapping Manager");
			e.printStackTrace();
		}
		return mapMan;
	}
}
package com.loloia.model;

import java.io.Serializable;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Table;
import com.loloia.helpers.CassandraProperties;

@Table(keyspace = CassandraProperties.keySpaceName, name = "news_words")
public class Words implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name = "word")
	private String word;
	
    @Column(name = "occurences")
    private int occurences;
	
	public Words(String word, int occurences) {
		this.word = word;
		this.occurences = occurences;
	}
	
	public String getWord() {
		return word;
	}
	
	public void setWord(String word) {
		this.word = word;
	}
	
	public int getOccurences() {
		return occurences;
	}
	
	public void setOccurences(int occurences) {
		this.occurences = occurences;
	}
}

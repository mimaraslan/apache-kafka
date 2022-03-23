package com.loloia.model;

public class Article {

	private String articleAuthor;
	private String articleCompany;
	private String articleTitle;
	
	public Article(String articleCompany, String articleAuthor, String articleTitle) {
		this.articleAuthor = articleAuthor;
		this.articleCompany = articleCompany;
		this.articleTitle = articleTitle;
	}
	
	public String getArticleAuthor() {
		return articleAuthor;
	}
	
	public String getArticleCompany() {
		return articleCompany;
	}
	
	public String getArticleTitle() {
		return articleTitle;
	}
	
	public String getArticleKey() {
		return articleCompany + articleAuthor;
	}
}

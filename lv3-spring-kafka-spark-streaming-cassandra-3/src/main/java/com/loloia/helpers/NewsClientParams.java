package com.loloia.helpers;

import java.util.HashMap;
import java.util.Map;

public class NewsClientParams {
// http://newsapi.org/v2/everything?q=bitcoin&from=2020-02-01&sortBy=publishedAt&apiKey=8c2eaada1e3142c3a79666bb5bd09ccb	
	public final String baseUri = "https://newsapi.org/v2/";
	public final String pathEverything = "everything";
	public final String topHeadlines = "top-headlines";
	public Map<String, String> newsParams;
	
	public NewsClientParams() {
		this.newsParams = new HashMap<String,String>();
		//newsParams.put("q", "bitcoin");
		newsParams.put("from", "2020-02-01");
		newsParams.put("sortBy", "publishedAt");
		newsParams.put("apiKey", "8c2eaada1e3142c3a79666bb5bd09ccb");
	}
}
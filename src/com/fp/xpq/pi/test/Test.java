package com.fp.xpq.pi.test;

import java.io.IOException;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class Test {
	
	public void test() {
			String content = null;
			//	地区代码
			String regionCode = "EN-US";
			//	游戏代码
			String gameCode = "9p2275wk9kqw";
			//	登录页
			String httpUrl = "https://www.microsoft.com/"+regionCode+"/store/p/fp/"+gameCode;
			
			HttpClient httpClient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(httpUrl);
			try {
				HttpResponse response = httpClient.execute(httpGet);
				content = EntityUtils.toString(response.getEntity(), "UTF-8");
			} catch (ClientProtocolException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			
			//	解析页面,查询名为ProductInfo的JSON字符串
			if(content != null){
				int beginIndex = content.indexOf("ProductInfo:") + "ProductInfo:".length();
				int endIndex = content.indexOf("}", beginIndex) + 1;
				content = content.substring(beginIndex, endIndex);
				System.out.println(content);
				JSONObject obj = JSONObject.fromString(content);
				String id = obj.getString("id");
				String name = obj.getString("title");
				String type = obj.getString("type");
				String lstPrice = obj.getString("lstPrice");
				String rtPrice = obj.getString("rtPrice");
				String currency = obj.getString("cur");
			}
	}
}

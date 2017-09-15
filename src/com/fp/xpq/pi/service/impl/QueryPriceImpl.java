package com.fp.xpq.pi.service.impl;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fp.xpq.pi.service.QueryPrice;
import com.fp.xpq.utils.Global;

public class QueryPriceImpl implements QueryPrice{
	
	@Override
	public String getLowestPrice(String gameCode) {
		List<Map<String, String>> regionList = Global.regionList;
		List<Map<String, String>> rateList = Global.rateList;
		
		DecimalFormat df = new DecimalFormat("#.00");
		
		// 存放结果进行排序
		List<Double> list = new ArrayList<>();
		
		for (int i = 0; i < regionList.size(); i++) {
			String regionCode = regionList.get(i).get("code").toString();
			String country = regionList.get(i).get("name").toString();
			System.out.println("开始查询："+country);
			
			String content = null;
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
				
//				对已下架地区做过滤
				if(content.indexOf("lstPrice") == -1){
					System.out.println(country+"的价格暂时无法获取，已过滤!");
					continue;
				}
				
				JSONObject obj = JSONObject.fromString(content);
//				String id = obj.getString("id");
//				String name = obj.getString("title");
//				String type = obj.getString("type");
				String lstPrice = obj.getString("lstPrice");
//				String rtPrice = obj.getString("rtPrice");
//				String currency = obj.getString("cur");
				
				//	匹配当地货币汇率
				for (int j = 0; j < rateList.size(); j++) {
					if(country.equals(rateList.get(j).get("name").toString())){
						//	过滤没有汇率的地区
						if("0".equals(rateList.get(j).get("rate").toString()))	continue;
						
						Double rate = Double.parseDouble(rateList.get(j).get("rate").toString());
						Double price = Double.parseDouble(lstPrice);
						list.add(price/rate);
						System.out.println(country+"价格:"+df.format(price/rate));
					}
				}
			}
		}
		
		//	进行排序
		Collections.sort(list);
        String lowestPrice = df.format(list.get(0));
		
		return lowestPrice;
	}

}

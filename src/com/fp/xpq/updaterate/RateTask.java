package com.fp.xpq.updaterate;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fp.xpq.pi.service.QueryPrice;
import com.fp.xpq.pi.service.impl.QueryPriceImpl;
import com.fp.xpq.utils.Global;

/**
 * 
* @ClassName: RateTask
* @Description:	定时更新汇率
* @author: 冯盼
* @date: 2017年9月14日
* @修改备注:
 */
public class RateTask implements ServletContextListener{
	
    //时间间隔(一小时)  
	private static final long PERIOD_HOUR = 60 * 60 * 1000;
	
	/**
	* @Title: update 
	* @Description: 更新汇率
	* @return void
	* @throws 
	* @author : 冯盼
	* @date: 2017年9月14日
	 */
	public void update() {
		HttpClient httpClient = HttpClients.createDefault();

		List<Map<String, String>> regionList = Global.regionList;
		List<Map<String, String>> rateList = Global.rateList;
		
		for (int i = 0; i < regionList.size(); i++) {
			String httpUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22CNY"+regionList.get(i).get("currency").toString()+"%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
			HttpGet httpGet = new HttpGet(httpUrl);
			String content = null;
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
			
			//	更新汇率
			if(content != null){
				JSONObject obj = JSONObject.fromString(content);
				for (int j = 0; j < rateList.size(); j++) {
					if(rateList.get(j).get("name").toString().equals(regionList.get(i).get("name").toString())){
						String rate = obj.getJSONObject("query").getJSONObject("results").getJSONObject("rate").getString("Rate");
						System.out.println(rate);
						rateList.get(j).put("rate", rate);
					}
				}
			}
		}
		System.out.println("---------------汇率更新完成--------------");
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, 10);
		
		Timer timer = new Timer();  
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				update();
				QueryPrice qp = new QueryPriceImpl();
				System.out.println(qp.getLowestPrice("C4C2Q9BC2W28"));
			}
        },calendar.getTime(),PERIOD_HOUR);
	}
}

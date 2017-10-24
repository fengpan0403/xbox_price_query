package com.fp.xpq.updaterate;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.mysql.jdbc.PreparedStatement;

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
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//	更新汇率，检索不到“query”关键字则代表查询失败
			if(content != null && content.indexOf("query") != -1){
				JSONObject obj = JSONObject.fromString(content);
				String rate = obj.getJSONObject("query").getJSONObject("results").getJSONObject("rate").getString("Rate");
				System.out.println(rate);				
				//	将汇率更新至数据库
				PreparedStatement pstmt;
			    try {
		        	pstmt = (PreparedStatement) Global.getConn().prepareStatement("update sys_rate set rate=? where code=?");
			        pstmt.setString(1, regionList.get(i).get("code"));
			        pstmt.setString(2, rate);
			        pstmt.execute();
			        pstmt.close();
			    } catch (SQLException e) {
			        e.printStackTrace();
			    }
			}
		}
		
		PreparedStatement pstmt;
	    try {
	        pstmt = (PreparedStatement)Global.getConn().prepareStatement("select region,code,cur,rate from sys_rate");
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	        	String code = rs.getString(2);
	        	String rate = rs.getString(4);
	        	for (int i = 0; i < rateList.size(); i++) {
					if(code.equals(rateList.get(i).get("code"))){
						rateList.get(i).put("rate", rate);
						break;
					}
				}
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }		    
		
		try {
			Global.getConn().close();
		} catch (SQLException e) {
			e.printStackTrace();
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
				/*QueryPrice qp = new QueryPriceImpl();
				System.out.println(qp.getLowestPrice("9nwtg8qtf8xz"));*/
			}
        },calendar.getTime(),PERIOD_HOUR);
	}
}

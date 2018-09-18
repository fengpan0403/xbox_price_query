package com.fp.xpq.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mysql.jdbc.Connection;

/**
 * 
* @ClassName: Global
* @Description:	全局参数，存放汇率和地区代码
* @author: 冯盼
* @date: 2017年9月14日
* @修改备注:
 */
public class Global {
	public static Connection conn = null;
	
	//	区域
	public static List<Map<String, String>> regionList = new ArrayList<>();
	
	//	汇率
	public static List<Map<String, String>> rateList = new ArrayList<>();
	
	
	//	加载区域信息
	static{
		InputStream in = Global.class.getResourceAsStream("/regions.xml");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document doc = null;
		try {
			doc = builder.parse(in);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element rootElement = doc.getDocumentElement();
		NodeList nodes = rootElement.getElementsByTagName("country");
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			Element element = (Element)node;
			Map<String, String> map = new HashMap<>();
			map.put("name", element.getFirstChild().getNodeValue());
			map.put("code", element.getAttribute("code"));
			map.put("currency", element.getAttribute("cur"));
			regionList.add(map);
			
			map = new HashMap<>();
			map.put("name", element.getFirstChild().getNodeValue());
			map.put("code", element.getAttribute("code"));
			map.put("rate", "0");
			rateList.add(map);
		}
	}

	//	数据库好像丢了。。。。。。。
	public static Connection getConn() {
		if(conn == null){
		    String driver = "com.mysql.jdbc.Driver";
		    String url = "jdbc:mysql://localhost:3306/ali";
		    String username = "root";
		    String password = "admin";
		    try {
		        Class.forName(driver);
		        conn = (Connection) DriverManager.getConnection(url, username, password);
		    } catch (ClassNotFoundException e) {
		        e.printStackTrace();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		}
		return conn;
	}
}

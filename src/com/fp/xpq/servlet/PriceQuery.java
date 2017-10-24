package com.fp.xpq.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fp.xpq.pi.service.QueryPrice;
import com.fp.xpq.pi.service.impl.QueryPriceImpl;

@WebServlet("/query")
public class PriceQuery extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	QueryPrice qp = new QueryPriceImpl();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PriceQuery() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		
		if(id == null || id.length() == 0) {
			response.getWriter().print("id错误!");
		}else{
			String rs = this.qp.getLowestPrice(id);
			response.getWriter().print(rs);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}

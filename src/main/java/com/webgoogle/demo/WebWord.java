package com.webgoogle.demo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class WebWord
 */
public class WebWord extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WebWord() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sword = request.getParameter("inputword").toString();
		String topIndex = request.getParameter("topnum").toString();
		String googlefile = "E:/mywebsit/myWebAppMongoDB1/vectors.bin";
		DeepLearn DL = new DeepLearn();
		String res = DL.doDeepLearn(googlefile, sword, topIndex);
		request.setAttribute("resultString", res);
		
		getServletContext().getRequestDispatcher("/output.jsp").forward(request, response);
		
	}

}

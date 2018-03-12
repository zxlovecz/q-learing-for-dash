package com.wspn.jetty;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HelloServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// ������Ӧ��������
		response.setContentType("text/html;charset=UTF-8");

		PrintWriter out = response.getWriter();
		String title = "ʹ�� GET ������ȡ������";
		// ��������
		String name = new String(request.getParameter("name"));
		String docType = "<!DOCTYPE html> \n";
		out.println(
				docType + "<html>\n" + "<head><title>" + title + "</title></head>\n" + "<body bgcolor=\"#f0f0f0\">\n"
						+ "<h1 align=\"center\">" + title + "</h1>\n" + "<ul>\n" + "  <li><b>վ����</b>��" + name + "\n"
						+ "  <li><b>��ַ</b>��" + request.getParameter("url") + "\n" + "</ul>\n" + "</body></html>");
	}

	// ���� POST ��������ķ���
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	/*	Map<String, String[]> map = new HashMap<>();
		Rnis rnis = new Rnis();
		rnis.setRsrp(Integer.valueOf(request.getParameter("rsrp")));
		rnis.setRssnr(Integer.valueOf(request.getParameter("rssnr")));
		rnis.setImsi(request.getRemoteAddr());
		map = request.getParameterMap();
		boolean res = RnisOperation.getInstance().saverRnis(rnis);
		if (res == true) {
			System.out.println("��rnis���в������ݳɹ�");
		} else {
			System.out.println("��rnis���в�������ʧ��");
		}*/
		
		if(MyProxyServlet2.hashMapPcap.containsKey(request.getRemoteAddr())) {
			MyProxyServlet2.hashMapPcap.get(request.getRemoteAddr()).getUser().setRsrp(Integer.valueOf(request.getParameter("rsrp")));
		}
		
		// for(Entry<String, String[]> entry:map.entrySet()) {
		// for(String string:entry.getValue()) {
		// System.out.println(entry.getKey()+" "+string);
		// }
		// }
	}

}

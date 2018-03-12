package com.wspn.jetty;
import org.eclipse.jetty.proxy.AsyncProxyServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Request;  
import org.eclipse.jetty.server.Server;  
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.pcap4j.core.PcapNativeException;

import com.wspn.pcap4j.InitPcap;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;  
   
public class JettyServer {   
    
    public static void main(String[] args) throws Exception {
       /* // ����Server
        Server server = new Server(8888);
        // ���Handler
        ContextHandlerCollection context = new ContextHandlerCollection();
        ContextHandler contextHandler = context.addContext("/", "/");//the ContextHandler just added
        contextHandler.setHandler(new DispatchHandler());//Set the Handler which should be wrapped.

        //Default Handler--�þ���������������δ��������� ����favicon.ico�������ṩJettyͼ�ꡣ
        //����'/'�������ṩ������֪�������б��404�� ����������������������404���ṩ��
        Handler defaults = new DefaultHandler();
        
        //HandlerCollection--A collection of handlers. 
        //Ĭ��ʵ�����б�˳��������д�����򣬶�������Ӧ״̬���쳣��Ρ�
        HandlerCollection collection = new HandlerCollection();
        collection.setHandlers(new Handler[] { context, defaults });
 
        server.setHandler(collection);
 
        // ��������
        server.start();
        while (server.isStarted()) {
            System.out.println("server starting...");
            break;
        }
        System.out.println("server stared...");
        System.out.println("ContextHandlerCollectio.getServer() = "
                + context.getServer().hashCode());
        System.out.println("Server:" + server.hashCode());
        server.join();*/
    	Server server = new Server(8888);
    	
    	  ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
          context.setContextPath("/");
          context.setResourceBase(System.getProperty("java.io.tmpdir"));
          server.setHandler(context);
    	
    	
		//ServletHandler handler = new ServletHandler();
		//server.setHandler(handler);
		
		AsyncProxyServlet servlet = new MyProxyServlet2();
		String downloadPath = "D:\\Program Files\\apache-tomcat-8.5.5\\webapps\\cache2";
		String logPath = "C:\\Users\\Administrator\\workspace\\zx\\logs";
		MyProxyServlet2.fileOperation(downloadPath);
		MyProxyServlet2.countPopularity(logPath);
		InitPcap.init();
		servlet.setTimeout(30000);
		ServletHolder proxyServletHolder = new ServletHolder(servlet);
		proxyServletHolder.setAsyncSupported(true);
	
		proxyServletHolder.setInitParameter("maxThreads", "10000");
		//handler.addServletWithMapping(proxyServletHolder, "/*");
		context.addServlet(proxyServletHolder, "/");
		context.addServlet(HelloServlet.class, "/hello");
		
		NCSARequestLog requestLog = new NCSARequestLog("./logs/jetty-yyyy_mm_dd.request.log");
		requestLog.setAppend(true);
		requestLog.setExtended(false);
		requestLog.setLogTimeZone("GMT");
		requestLog.setLogDateFormat("d/MMM/yyyy:HH:mm:ss:SSS Z");
		requestLog.setLogLatency(true);
		server.setRequestLog(requestLog);
		
		//RL.init();
		
		server.start();
		server.join();

    	
    }
    public static void start() throws Exception {
        /* // ����Server
         Server server = new Server(8888);
         // ���Handler
         ContextHandlerCollection context = new ContextHandlerCollection();
         ContextHandler contextHandler = context.addContext("/", "/");//the ContextHandler just added
         contextHandler.setHandler(new DispatchHandler());//Set the Handler which should be wrapped.

         //Default Handler--�þ���������������δ��������� ����favicon.ico�������ṩJettyͼ�ꡣ
         //����'/'�������ṩ������֪�������б��404�� ����������������������404���ṩ��
         Handler defaults = new DefaultHandler();
         
         //HandlerCollection--A collection of handlers. 
         //Ĭ��ʵ�����б�˳��������д�����򣬶�������Ӧ״̬���쳣��Ρ�
         HandlerCollection collection = new HandlerCollection();
         collection.setHandlers(new Handler[] { context, defaults });
  
         server.setHandler(collection);
  
         // ��������
         server.start();
         while (server.isStarted()) {
             System.out.println("server starting...");
             break;
         }
         System.out.println("server stared...");
         System.out.println("ContextHandlerCollectio.getServer() = "
                 + context.getServer().hashCode());
         System.out.println("Server:" + server.hashCode());
         server.join();*/
     	Server server = new Server(8888);
     	
     	  ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
           context.setContextPath("/");
           context.setResourceBase(System.getProperty("java.io.tmpdir"));
           server.setHandler(context);
     	
     	
 		//ServletHandler handler = new ServletHandler();
 		//server.setHandler(handler);
 		
 		AsyncProxyServlet servlet = new MyProxyServlet2();
 		String downloadPath = "D:\\Program Files\\apache-tomcat-8.5.5\\webapps\\eee";
 		String logPath = "C:\\Users\\Administrator\\workspace\\zx\\logs";
 		MyProxyServlet2.fileOperation(downloadPath);
 		//MyProxyServlet2.countPopularity(logPath);
 		InitPcap.init();
 		servlet.setTimeout(30000);
 		ServletHolder proxyServletHolder = new ServletHolder(servlet);
 		proxyServletHolder.setAsyncSupported(true);
 	
 		proxyServletHolder.setInitParameter("maxThreads", "10000");
 		//handler.addServletWithMapping(proxyServletHolder, "/*");
 		context.addServlet(proxyServletHolder, "/");
 		context.addServlet(HelloServlet.class, "/hello");
 		
 		NCSARequestLog requestLog = new NCSARequestLog("./logs/jetty-yyyy_mm_dd.request.log");
 		requestLog.setAppend(true);
 		requestLog.setExtended(false);
 		requestLog.setLogTimeZone("GMT");
 		requestLog.setLogDateFormat("d/MMM/yyyy:HH:mm:ss:SSS Z");
 		requestLog.setLogLatency(true);
 		server.setRequestLog(requestLog);
 		
 		RL.init();
 		
 		server.start();
 		server.join();

     	
     }
}  
 
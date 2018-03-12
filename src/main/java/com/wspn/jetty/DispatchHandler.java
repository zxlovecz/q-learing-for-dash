package com.wspn.jetty;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class DispatchHandler extends AbstractHandler {
	
	
	public void handle(String target, Request baseRequest,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
 
        /**
         * ���ｫ�Ǹ��ݲ�ͬ���������ַ�����ͬ��Handler������
         */
		//target--�����Ŀ�� : һ��URI��һ�����ơ�
		//baseRequest--The original unwrapped request object;ԭʼ�Ľ���������
		//request--The request either as the Request object or a wrapper 
		//of that request.
		//response--The response as the Response object or a wrapper of that request. 
		//The HttpConnection.getCurrentConnection().getHttpChannel().getResponse() method 
		//can be used access the Response object if required.
		System.out.println("target"+target.toString());
		System.out.println("request"+request.toString());
		System.out.println("baseRequest"+baseRequest.toString());
		System.out.println("response"+response.toString());
        if (target.equals("/index")) {
            new IndexHandler().handle(target, baseRequest, request, response);
        } 
    }
}

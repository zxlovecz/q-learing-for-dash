package com.wspn.jetty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

	// JDBC �����������ݿ� URL
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/mec?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT&useSSL=false";

	// ���ݿ���û��������룬��Ҫ�����Լ�������
	static final String USER = "root";
	static final String PASS = "zx93123";

	private static Connection conn = null;

	public static Connection getCon() {

		try {
			// ע�� JDBC ����
			Class.forName("com.mysql.cj.jdbc.Driver");
			// ������
			System.out.println("�������ݿ�...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("�������ݿ�ʧ��");
			e.printStackTrace();
		}
		return conn;
	}
}

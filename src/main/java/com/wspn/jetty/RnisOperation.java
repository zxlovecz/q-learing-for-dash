package com.wspn.jetty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

//rnisloyeeOperation�����ڲ������ݿ⣬���µı�д������JAVA�����̵�һ�����ģʽ����Ϊ���������� ����ģʽ����������������
//���������жϸ���Ķ����Ƿ�Ϊ�գ�ֻ��Ϊ�ղŴ���(new) ,��˱�֤�ö����ڳ�������Զ��Ψһ�ģ����Ա����ظ�����������ɵ�ϵͳ�ڴ汻����ռ��
public class RnisOperation {
	private static RnisOperation instance = null;

	public static RnisOperation getInstance() { // ����rnisloyeeOperation��ʵ���ľ�̬����,����ģʽ��������
		if (instance == null) {
			instance = new RnisOperation();
		}
		
		
		return instance;
	}

	public boolean saverRnis(Rnis rnis) { // �����ݿ��м�������
		boolean result = false;
		Connection conn = null;
		try {

			conn = DatabaseConnection.getCon(); // �������ݿ�����
			String sqlInset = "insert into rnis(rsrp, rssnr,imsi) values(?, ?, ?)";
			String sqlUpdate = "update rnis set rsrp=? ,rssnr=? where imsi=?";
			
			PreparedStatement stmt = conn.prepareStatement(sqlUpdate); // ���׳��쳣

			stmt.setInt(1, rnis.getRsrp()); // ����SQL����һ����������ֵ
			stmt.setInt(2, rnis.getRssnr()); // ����SQL���ڶ�����������ֵ
			stmt.setString(3, rnis.getImsi()); // ����SQL����������������ֵ
			int i = stmt.executeUpdate(); // ִ�в������ݲ���������Ӱ�������
			if (i == 1) {
				result = true;
			}
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { // finally���ô��ǲ��ܳ����Ƿ�����쳣����Ҫִ��finally��䣬�����ڴ˴��ر�����
			try {

				if (conn != null)
					conn.close(); // ��һ��Connection���Ӻ����һ��Ҫ��������close���������ر����ӣ����ͷ�ϵͳ��Դ�����ݿ���Դ
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;

	}

	public List<Rnis> selectrnisloyee() { // �����ݿ��в�ѯ��������
		List<Rnis> rnisList = new ArrayList<Rnis>();
		Connection conn = null;
		try {
			conn = DatabaseConnection.getCon();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from rnis");// ִ��SQL�����ؽ����
			while (rs.next()) {
				Rnis rnis = new Rnis();
				rnis.setRsrp(rs.getInt("rsrp")); // �ӽ����rs�л�ȡ����ʱ����Ϊ�ַ������͵ģ���rs.getString("string")����
				rnis.setRssnr(rs.getInt("rssnr")); // ����strΪ��Ҫ�� ���ݿ�� �� �л�ȡ����Ϣ
				rnis.setImsi(rs.getString("imsi")); // ��Ϊint���ͣ���rs.getInt(number);
				rnisList.add(rnis);
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();// �ر�����
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return rnisList; // ���ؽ��
	}

	public boolean deleternisloyeeById(Rnis rnis) {
		boolean result = false;
		Connection conn = null;
		try {
			conn = DatabaseConnection.getCon();
			String sql = "delete from rnis where imsi = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, rnis.getImsi());
			int i = stmt.executeUpdate();
			if (i == 1) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

}
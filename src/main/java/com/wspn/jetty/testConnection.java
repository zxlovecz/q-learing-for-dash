package com.wspn.jetty;

import java.util.List;

public class testConnection {
	public static void main(String[] args) {
		
		Rnis rnis = new Rnis();
		rnis.setRsrp(1);
		rnis.setRssnr(1);
		rnis.setImsi("1");
		boolean res = RnisOperation.getInstance().saverRnis(rnis);
		if (res == true) {
			System.out.println("��rnis���в������ݳɹ�");
		} else {
			System.out.println("��rnis���в�������ʧ��");
		}
	}
}

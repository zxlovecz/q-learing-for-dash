package com.wspn.pcap4j;

import java.util.List;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;

public class InitPcap {

	// ��ȡ���������豸
	static List<PcapNetworkInterface> alldev = null;
	static PcapNetworkInterface nif = null;
	static PcapHandle.Builder phb=null;

	static public void init() {
		try {
			alldev = Pcaps.findAllDevs();
		} catch (PcapNativeException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}

		for (PcapNetworkInterface pcapNetworkInterface : alldev) {
			System.out.println(pcapNetworkInterface.toString());
		}
		// �����豸���Ƴ�ʼ��ץ���ӿ�
		try {
			nif = Pcaps.getDevByName(alldev.get(0).getName());
		} catch (PcapNativeException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		// ץȡ������
		int snaplen = 1024 * 1024 * 100;
		// ��ʱ50ms
		int timeout = 1000;
		// ��ʼ��ץ����
		phb = new PcapHandle.Builder(nif.getName()).snaplen(snaplen)
				.promiscuousMode(PromiscuousMode.PROMISCUOUS).timeoutMillis(timeout).bufferSize(100 * 1024 * 1024);
	}

}

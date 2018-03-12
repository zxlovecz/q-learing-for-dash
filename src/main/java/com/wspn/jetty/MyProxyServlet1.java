package com.wspn.jetty;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.proxy.AsyncProxyServlet;
import org.eclipse.jetty.server.handler.ContextHandler.StaticContext;

public class MyProxyServlet1 extends AsyncProxyServlet {

	static HashMap<String, File> hashMapFile = new HashMap<>();
	static HashMap<String, Integer> hashMapFilePopularity = new HashMap<>();
	static HashMap<String, Long> hashMapFileLatestTime = new HashMap<>();
	String urlForDL = "http://localhost:8080/dash2/";
	String urlForCache = "http://localhost:8080/cache/";
	String downloadPath = "D:\\Program Files\\apache-tomcat-8.5.5\\webapps\\cache";
	String logPath = "C:\\Users\\Administrator\\workspace\\jetty_mec\\logs";
	static long totalCacheBytes = 0;
	boolean fileFlag = true;

	
	
	@Override
	protected StreamWriter newWriteListener(HttpServletRequest request, Response proxyResponse) {
		// TODO Auto-generated method stub
		return new MyStreamWriter(request, proxyResponse);
	}

	@Override
	protected String rewriteTarget(HttpServletRequest request) {
		final String getRequestURL = request.getRequestURI().replace("/", "");
		/*
		 * for (String name:hashMapFilePopularity.keySet()) {
		 * System.out.println(name+"  "+hashMapFilePopularity.get(name)); }
		 */
		if (!hashMapFilePopularity.containsKey(getRequestURL)) {
			hashMapFilePopularity.put(getRequestURL, 1);
		} else {
			hashMapFilePopularity.put(getRequestURL, hashMapFilePopularity.get(getRequestURL) + 1);
		}
		Date date = new Date();
		hashMapFileLatestTime.put(getRequestURL, date.getTime() - 28800000);

		System.out.println("�ͻ�������Ŀ��: " + getRequestURL + "���ȶ�Ϊ" + hashMapFilePopularity.get(getRequestURL));
		System.out.println("�����ļ�" + totalCacheBytes);
//		for (Entry<String, File> entry : hashMapFile.entrySet()) {
//			System.out.println(entry.getKey());
//		}
		if (hashMapFile.containsKey(getRequestURL)) {
			System.out.println("����Ŀ��: " + getRequestURL + "�Ѵ���");
			return urlForCache.concat(getRequestURL);
		} else {
			final String return_URL = urlForDL.concat(getRequestURL);
			// System.out.println(Thread.currentThread().getId());
			if (fileFlag) {
				new Thread(new Runnable() {
					public void run() {
						String fileForPrefetch=getRequestURL;
						if (getRequestURL.split("\\.")[getRequestURL.split("\\.").length - 1].indexOf("4") != -1) {
							getInternetRes("D:\\Program Files\\apache-tomcat-8.5.5\\webapps\\cache", return_URL,
								getRequestURL);
							
						}
					}
					}).start();
			} else {
				System.err.println("�����ж��Ƿ������ļ�");
			}
			return return_URL;
		}
	}

	protected class MyStreamWriter extends StreamWriter {

		protected MyStreamWriter(HttpServletRequest request, Response proxyResponse) {
			super(request, proxyResponse);
			System.out.println("��ӦĿ��: " + proxyResponse.getRequest() + proxyResponse.toString());
			// System.out.println(Thread.currentThread().getId());
		}
	}

	/**
	 * ���ϻ�ȡ�ļ�
	 * 
	 * @param savepath
	 *            ����·��
	 * @param resurl
	 *            ��Դ·��
	 * @param fileName
	 *            �Զ�����Դ��
	 */
	public void getInternetRes(String savepath, String resurl, String fileName) {
		URL url = null;
		HttpURLConnection con = null;
		InputStream in = null;
		FileOutputStream out = null;
		Long bytesForCached;
		try {
			url = new URL(resurl);
			// ����http���ӣ��õ����Ӷ���
			con = (HttpURLConnection) url.openConnection();
			// con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;
			// Windows NT; DigExt)");
			bytesForCached = con.getContentLengthLong();// ��ȡ�������ص��ļ���С
			if (con.getResponseCode()!=200) {
				return;
			}
			System.out.println("��Ҫ���ص��ļ�Ϊ" + fileName);
			if (bytesForCached + totalCacheBytes < 1024 * 1024 * 999) {
				in = con.getInputStream();
				byte[] data = getByteData(in);// ת��Ϊbyte����

				File file = new File(savepath);
				if (!file.exists()) {
					file.mkdirs();
				}
				File res = new File(file + File.separator + fileName);
				out = new FileOutputStream(res);
				out.write(data);
				System.out.println("����Ŀ��" + fileName + "���سɹ�!" + file + File.separator + fileName);
				hashMapFile.put(fileName, res);
				totalCacheBytes += res.length();
			} else {
				cachePolicy(bytesForCached);
				// LRU(bytesForCached);
				in = con.getInputStream();
				byte[] data = getByteData(in);// ת��Ϊbyte����

				File file = new File(savepath);
				if (!file.exists()) {
					file.mkdirs();
				}
				File res = new File(file + File.separator + fileName);
				out = new FileOutputStream(res);
				out.write(data);
				System.out.println("����Ŀ��" + fileName + "���سɹ�!" + file + File.separator + fileName);
				hashMapFile.put(fileName, res);
				totalCacheBytes += res.length();

			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != out)
					out.close();
				if (null != in)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * ���ϻ�ȡ�ļ�
	 * 
	 * @param savepath
	 *            ����·��
	 * @param resurl
	 *            ��Դ·��
	 * @param fileName
	 *            �Զ�����Դ��
	 */
	public void prefetchRes(final String savepath, final String resurl, String fileName) {
		URL url = null;
		HttpURLConnection con = null;
		InputStream in = null;
		FileOutputStream out = null;
		final List<String> fileNameList = new ArrayList<>();
		if (fileName.split("\\.")[1].equals("mp4")) {
			fileNameList.add(fileName);
			for (int i = 1; i <= 3; i++) {
				fileNameList.add(fileName.split("\\.")[0].split("_")[0] + "_" + fileName.split("\\.")[0].split("_")[1]
						+ "_" + String.valueOf(i) + ".m4s");
			}
		} else {
			for (int i = 0; i <=3; i++) {
				fileNameList.add(fileName.split("\\.")[0].split("_")[0] + "_" + fileName.split("\\.")[0].split("_")[1]
						+ "_" + String.valueOf(Integer.valueOf(fileName.split("\\.")[0].split("_")[2]) + i) + ".m4s");
			}
		}
		
		new Thread(new Runnable() {
			public void run() {
				if(!hashMapFile.containsKey(fileNameList.get(0))) {
					download(savepath, resurl, fileNameList.get(0));
				}
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				if(!hashMapFile.containsKey(fileNameList.get(1))) {
					download(savepath, resurl, fileNameList.get(1));
				}
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				if(!hashMapFile.containsKey(fileNameList.get(2))) {
					download(savepath, resurl, fileNameList.get(2));
				}
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				if(!hashMapFile.containsKey(fileNameList.get(3))) {
					download(savepath, resurl, fileNameList.get(3));
				}
			}
		}).start();
	}
	public void download(String savepath, String resurl, String fileName) {
		URL url = null;
		HttpURLConnection con = null;
		InputStream in = null;
		FileOutputStream out = null;
		try {
			url = new URL(resurl);
			// ����http���ӣ��õ����Ӷ���
			con = (HttpURLConnection) url.openConnection();
			if(con.getResponseCode()==404) {
				return;
			}
			// con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;
			// Windows NT; DigExt)");
			in = con.getInputStream();
			byte[] data = getByteData(in);// ת��Ϊbyte����

			File file = new File(savepath);
			if (!file.exists()) {
				file.mkdirs();
			}
			File res = new File(file + File.separator + fileName);
			out = new FileOutputStream(res);
			out.write(data);
			System.out.println("����Ŀ��" + fileName + "���سɹ�!" + file + File.separator + fileName);
			hashMapFile.put(fileName, res);
			totalCacheBytes += res.length();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != out)
					out.close();
				if (null != in)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * ���������л�ȡ�ֽ�����
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private byte[] getByteData(InputStream in) throws IOException {
		byte[] b = new byte[1024];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int len = 0;
		while ((len = in.read(b)) != -1) {
			bos.write(b, 0, len);
		}
		if (null != bos) {
			bos.close();
		}
		return bos.toByteArray();
	}

	/**
	 * �ļ�����
	 * 
	 * @param downloadPath
	 * @return @throws
	 */
	public static void fileOperation(String downloadPath) {
		System.out.println("���������ļ�");
		File fileCacheFolder = new File(downloadPath);
		int filesNum = 0;
		if (fileCacheFolder.exists()) {
			File[] filesCache = fileCacheFolder.listFiles();
			for (File fileCache : filesCache) {
				if (fileCache.isFile()) {
					hashMapFile.put(fileCache.getName(), fileCache);
					totalCacheBytes += fileCache.length();
				}
			}
		}
		// System.out.println("һ��������"+totalCacheBytes+"bytes");
		/*
		 * for (Entry<String, File> fileEntry : hashMapFile.entrySet()) {
		 * System.out.println("�ļ�" + fileEntry.getKey() + "�Ĵ�СΪ:" +
		 * fileEntry.getValue().length() / 1024); filesNum++; }
		 * System.out.println(filesNum);
		 */
	}

	/**
	 * ��ȡPopularity
	 * 
	 * @param logPath
	 * @return @throws
	 */
	public static void countPopularity(String logPath) {
		System.out.println("�����ļ��ȶȺ����·���ʱ��");
		File fileCacheFolder = new File(logPath);
		int filesNum = 0;

		if (fileCacheFolder.exists()) {
			File[] filesCache = fileCacheFolder.listFiles();
			for (File fileCache : filesCache) {
				if (fileCache.isFile()) {
					int line = 0;
					filesNum++;
					// File file = new File("D:\\aa.txt");
					BufferedReader reader = null;
					String temp = null;
					try {
						reader = new BufferedReader(new FileReader(fileCache));
						while ((temp = reader.readLine()) != null) {
							// System.out.println("line" + line + ":" + temp);
							// line++;
							String[] arr = temp.split("\\s+");
							String nameKey = arr[6].replace("/", "");
							String time = arr[3].replace("[", "");
							SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss");
							Date date = format.parse(time);
							Long lastestTime = date.getTime();
							if (nameKey.equals("")) {
								nameKey = "index.html";
							}
							if (!hashMapFilePopularity.containsKey(nameKey)) {
								hashMapFilePopularity.put(nameKey, 1);
							} else {
								hashMapFilePopularity.put(nameKey, hashMapFilePopularity.get(nameKey) + 1);
							}
							hashMapFileLatestTime.put(nameKey, lastestTime);
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (reader != null) {
							try {
								reader.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		/*
		 * for (Entry<String, Integer> entry : hashMapFilePopularity.entrySet()) {
		 * System.out.println("�ļ�" + entry.getKey() + "���ȶ�Ϊ" + entry.getValue()); }
		 */

	}

	/**
	 * ɾ�������ļ�
	 *
	 * @param fileName
	 *            Ҫɾ�����ļ����ļ���
	 * @return �����ļ�ɾ���ɹ�����true�����򷵻�false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		// ����ļ�·������Ӧ���ļ����ڣ�������һ���ļ�����ֱ��ɾ��
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				System.out.println("ɾ�������ļ�" + fileName + "�ɹ���");
				return true;
			} else {
				System.out.println("ɾ�������ļ�" + fileName + "ʧ�ܣ�");
				return false;
			}
		} else {
			System.out.println("ɾ�������ļ�ʧ�ܣ�" + fileName + "�����ڣ�");
			return false;
		}
	}

	public void cachePolicy(long bytesForCached) {
		fileFlag = false;
		long bytesForDelete = 0;
		HashMap<String, Integer> hashMapContentPopularity = new HashMap<>();
		HashMap<String, Integer> hashMapaSegmentsPopularity = new HashMap<>();
		HashMap<String, Integer> hashMapaRepPopularity = new HashMap<>();
		HashMap<String, Integer> hashMapSegmentsRep = new HashMap<>();
		System.out.println("�����ļ�" + totalCacheBytes);
		for (Entry<String, File> entry : hashMapFile.entrySet()) {
			System.out.println(entry.getKey());
		}
		for (Entry<String, File> fileEntry : hashMapFile.entrySet()) {
			if (fileEntry.getKey().split("\\.")[fileEntry.getKey().split("\\.").length - 1].indexOf("4") != -1) {
				String contentName = fileEntry.getKey().split("_")[0];
				if (!hashMapContentPopularity.containsKey(contentName)) {
					hashMapContentPopularity.put(contentName, hashMapFilePopularity.get(fileEntry.getKey()));
				} else {
					hashMapContentPopularity.put(contentName,
							hashMapContentPopularity.get(contentName) + hashMapFilePopularity.get(fileEntry.getKey()));
				}
				String segmentsName = contentName + "_" + fileEntry.getKey().split("\\.")[0].split("_")[2];
				if (!hashMapaSegmentsPopularity.containsKey(segmentsName)) {
					hashMapaSegmentsPopularity.put(segmentsName, hashMapFilePopularity.get(fileEntry.getKey()));
					hashMapSegmentsRep.put(segmentsName, 1);
				} else {
					hashMapaSegmentsPopularity.put(segmentsName, hashMapaSegmentsPopularity.get(segmentsName)
							+ hashMapFilePopularity.get(fileEntry.getKey()));
					hashMapSegmentsRep.put(segmentsName, hashMapSegmentsRep.get(segmentsName) + 1);
				}
				if (!hashMapaRepPopularity.containsKey(fileEntry.getKey())) {
					hashMapaRepPopularity.put(fileEntry.getKey(), hashMapFilePopularity.get(fileEntry.getKey()));
				} else {
					hashMapaRepPopularity.put(fileEntry.getKey(), hashMapaRepPopularity.get(fileEntry.getKey())
							+ hashMapFilePopularity.get(fileEntry.getKey()));
				}
			}
		}

		List<Map.Entry<String, Integer>> listContent = new ArrayList<Map.Entry<String, Integer>>(
				hashMapContentPopularity.entrySet());
		List<Map.Entry<String, Integer>> listSegment = new ArrayList<Map.Entry<String, Integer>>(
				hashMapaSegmentsPopularity.entrySet());
		List<Map.Entry<String, Integer>> listRep = new ArrayList<Map.Entry<String, Integer>>(
				hashMapaRepPopularity.entrySet());
		Collections.sort(listContent, new Comparator<Map.Entry<String, Integer>>() {
			// ��������
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		Collections.sort(listSegment, new Comparator<Map.Entry<String, Integer>>() {
			// ��������
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		Collections.sort(listRep, new Comparator<Map.Entry<String, Integer>>() {
			// ��������
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		for (Map.Entry<String, Integer> mapping : listContent) {

			System.out.println("Content:  " + mapping.getKey() + ":" + mapping.getValue());
		}

		for (Map.Entry<String, Integer> mapping : listSegment) {

			System.out.println("Segment:  " + mapping.getKey() + ":" + mapping.getValue());
		}
		for (Map.Entry<String, Integer> mapping : listRep) {

			System.out.println("Rep:  " + mapping.getKey() + ":" + mapping.getValue());
		}
		for (Entry<String, Integer> entry : hashMapSegmentsRep.entrySet()) {
			System.out.println("hashMapSegmentsRep:  " + entry.getKey() + ":" + entry.getValue());
		}

		loop: for (Map.Entry<String, Integer> mappingContent : listContent) {
			System.out.println("�ж�Content " + mappingContent.getKey());
			for (Map.Entry<String, Integer> mappingSegment : listSegment) {
				System.out.println("�ж�Segment " + mappingSegment.getKey());
				if (mappingSegment.getKey().split("_")[0].equals(mappingContent.getKey())) {
					if (hashMapSegmentsRep.get(mappingSegment.getKey()) == 1) {
						continue;
					} else {
						for (Map.Entry<String, Integer> mappingRep : listRep) {
							if ((mappingRep.getKey().split("_")[0] + "_"
									+ mappingRep.getKey().split("\\.")[0].split("_")[2])
											.equals(mappingSegment.getKey())) {
								System.out.println("�ж�Rep1 " + mappingRep.getKey());
								long fileLength = hashMapFile.get(mappingRep.getKey()).length();
								bytesForDelete += fileLength;
								totalCacheBytes -= fileLength;
								System.out.println("��Ҫɾ�����ֽ�����һ��ɾ�����ֽ���1 " + bytesForCached + "  " + bytesForDelete);
								final String filePath = hashMapFile.get(mappingRep.getKey()).getAbsolutePath();
								hashMapFile.remove(mappingRep.getKey());
								new Thread(new Runnable() {
									public void run() {
										deleteFile(filePath);
									}
								}).start();
								if (bytesForDelete >= bytesForCached) {
									break loop;
								}

								hashMapSegmentsRep.put(mappingSegment.getKey(),
										hashMapSegmentsRep.get(mappingSegment.getKey()) - 1);
								if (hashMapSegmentsRep.get(mappingSegment.getKey()) == 1) {
									break;
								}

							}
						}
					}
				}
			}
			for (Map.Entry<String, Integer> mappingSegment : listSegment) {
				if (mappingSegment.getKey().split("_")[0].equals(mappingContent.getKey())) {
					for (Entry<String, File> entry : hashMapFile.entrySet()) {
						if ((entry.getKey().split("_")[0] + "_" + entry.getKey().split("\\.")[0].split("_")[2])
								.equals(mappingSegment.getKey())) {
							System.out.println("�ж�Rep1 " + entry.getKey());
							long fileLength = hashMapFile.get(entry.getKey()).length();
							bytesForDelete += fileLength;
							totalCacheBytes -= fileLength;
							System.out.println("��Ҫɾ�����ֽ�����һ��ɾ�����ֽ���2 " + bytesForCached + "  " + bytesForDelete);
							final String filePath = hashMapFile.get(entry.getKey()).getAbsolutePath();
							hashMapFile.remove(entry.getKey());
							new Thread(new Runnable() {
								public void run() {
									deleteFile(filePath);
								}
							}).start();
							if (bytesForDelete >= bytesForCached) {
								break loop;
							}
						}
					}
				}
			}
		}
		fileFlag = true;
	}

	/**
	 * LFU -- �������ʹ��
	 * 
	 * @param bytesForCached
	 * @author zhouxiang
	 * 
	 */
	public void LFU(long bytesForCached) {
		fileFlag = false;
		long bytesForDelete = 0;
		HashMap<String, Integer> hashMapaRepPopularity = new HashMap<>();
		for (Entry<String, File> fileEntry : hashMapFile.entrySet()) {
			if (fileEntry.getKey().split("\\.")[fileEntry.getKey().split("\\.").length - 1].indexOf("4") != -1) {
				if (!hashMapaRepPopularity.containsKey(fileEntry.getKey())) {
					hashMapaRepPopularity.put(fileEntry.getKey(), hashMapFilePopularity.get(fileEntry.getKey()));
				} else {
					hashMapaRepPopularity.put(fileEntry.getKey(), hashMapaRepPopularity.get(fileEntry.getKey())
							+ hashMapFilePopularity.get(fileEntry.getKey()));
				}
			}
		}
		List<Map.Entry<String, Integer>> listRep = new ArrayList<Map.Entry<String, Integer>>(
				hashMapaRepPopularity.entrySet());
		Collections.sort(listRep, new Comparator<Map.Entry<String, Integer>>() {
			// ��������
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		System.out.println("�����ļ����ȶ�����:");
		for (Entry<String, Integer> entry : listRep) {
			System.out.println(entry.getKey() + "���ȶ�Ϊ:" + entry.getValue());
		}
		for (Map.Entry<String, Integer> mappingRep : listRep) {
			System.out.println("LFU�ж�Rep " + mappingRep.getKey());
			long fileLength = hashMapFile.get(mappingRep.getKey()).length();
			final String filePath = hashMapFile.get(mappingRep.getKey()).getAbsolutePath();
			hashMapFile.remove(mappingRep.getKey());
			bytesForDelete += fileLength;
			totalCacheBytes -= fileLength;
			System.out.println("��Ҫɾ�����ֽ�����һ��ɾ�����ֽ��� " + bytesForCached + "  " + bytesForDelete);
			new Thread(new Runnable() {
				public void run() {
					deleteFile(filePath);
				}
			}).start();
			if (bytesForDelete >= bytesForCached) {
				break;
			}
		}
		fileFlag = true;
	}

	/**
	 * LRU -- ������δʹ��
	 * 
	 * @param bytesForCached
	 * @author zhouxiang
	 * 
	 */
	public void LRU(long bytesForCached) {
		fileFlag = false;
		long bytesForDelete = 0;
		HashMap<String, Long> hashMapaRepLastest = new HashMap<>();
		for (Entry<String, File> fileEntry : hashMapFile.entrySet()) {
			if (fileEntry.getKey().split("\\.")[fileEntry.getKey().split("\\.").length - 1].indexOf("4") != -1) {
				if (!hashMapaRepLastest.containsKey(fileEntry.getKey())) {
					hashMapaRepLastest.put(fileEntry.getKey(), hashMapFileLatestTime.get(fileEntry.getKey()));
				} else {
					hashMapaRepLastest.put(fileEntry.getKey(),
							hashMapaRepLastest.get(fileEntry.getKey()) + hashMapFileLatestTime.get(fileEntry.getKey()));
				}
			}
		}
		List<Map.Entry<String, Long>> listRep = new ArrayList<Map.Entry<String, Long>>(hashMapaRepLastest.entrySet());
		Collections.sort(listRep, new Comparator<Map.Entry<String, Long>>() {
			// ��������
			@Override
			public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		System.out.println("�����ļ�������ʱ������:");
		for (Entry<String, Long> entry : listRep) {
			System.out.println(entry.getKey() + "������ʱ��Ϊ:" + entry.getValue());
		}
		for (Map.Entry<String, Long> mappingRep : listRep) {
			System.out.println("LRU�ж�Rep " + mappingRep.getKey());
			long fileLength = hashMapFile.get(mappingRep.getKey()).length();
			final String filePath = hashMapFile.get(mappingRep.getKey()).getAbsolutePath();
			hashMapFile.remove(mappingRep.getKey());
			bytesForDelete += fileLength;
			totalCacheBytes -= fileLength;
			System.out.println("��Ҫɾ�����ֽ�����һ��ɾ�����ֽ��� " + bytesForCached + "  " + bytesForDelete);
			new Thread(new Runnable() {
				public void run() {
					deleteFile(filePath);
				}
			}).start();
			if (bytesForDelete >= bytesForCached) {
				break;
			}
		}
		fileFlag = true;
	}

}

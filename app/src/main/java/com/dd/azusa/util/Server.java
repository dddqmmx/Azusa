package com.dd.azusa.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class Server{

	public static void download(String url, String saveDir, String fileName) {

		BufferedOutputStream bos = null;
		InputStream is = null;
		try {
			byte[] buff = new byte[8192];
			is = new URL(url).openStream();
			File file = new File(saveDir, fileName);
			file.getParentFile().mkdirs();
			bos = new BufferedOutputStream(new FileOutputStream(file));
			int count = 0;
			while ( (count = is.read(buff)) != -1) {
				bos.write(buff, 0, count);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}

	public static String Loadweb(String path) throws IOException{
		String h;
		StringBuilder str= new StringBuilder();
		InputStreamReader Reader=new InputStreamReader(new URL(path).openConnection().getInputStream());
		BufferedReader liu=new BufferedReader(Reader);
		str.append(liu.readLine());
		while ((h=liu.readLine())!=null){
			str.append("\n").append(h);
		}
		liu.close();
		str.substring(0,str.length()-1);
		return str.toString();
	}

	public static final int TIME_OUT = 10 * 10000000; // 超时时间
	public static final String CHARSET = "utf-8"; // 设置编码
	public static boolean Upload(File file,String path){
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型
		System.out.println(path);
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
					+ BOUNDARY);
			if (file != null) {
				OutputStream outputSteam = conn.getOutputStream();

				DataOutputStream dos = new DataOutputStream(outputSteam);
				String sb = PREFIX +
						BOUNDARY +
						LINE_END +
						"Content-Disposition: form-data; name=\"file\"; filename=\""
						+ file.getName() + "\"" + LINE_END +
						"Content-Type: application/octet-stream; charset="
						+ CHARSET + LINE_END +
						LINE_END;
				dos.write(sb.getBytes());
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
						.getBytes();
				dos.write(end_data);
				dos.flush();
				int res = conn.getResponseCode();
				System.out.println(res);
				if (res == 200) {
					return true;
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}

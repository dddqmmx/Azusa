package com.dd.nanami.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class Files {
	public boolean write(String Path,String Content){
		File file=new File(Path);
	    try{
			if(!file.exists()){
				new File(getfolder(Path)).mkdirs();
			}
			FileWriter write=new FileWriter(file);
			write.write(Content);
			write.close();
			return true;
		}catch(IOException e){
			return false;
		}
	}
	public String get(String Path){
		String str="";
		try{
			if(exists(Path)){
			    BufferedReader rod=new BufferedReader(new FileReader(Path));
			    String h=rod.readLine();
			    while(h!=null){
				    str=h+"\n";
				    h=rod.readLine();
			    }
				return str;
			}else{
				return "0";
			}
		}catch(Exception e){
			return "0";
		}
	}
	public static void Rwrite(String Path,String Row,String Content){
		File f=new File(Path);
		try{
		    if(!f.exists()){
			    Objects.requireNonNull(f.getParentFile()).mkdirs();
			    f.createNewFile();
		    }
		    Properties p=new Properties();
		    p.load(new FileInputStream(Path));
		    p.setProperty(Row,Content);
		    p.store(new FileOutputStream(Path), null);
		}catch(IOException ignored){
		}
	}
	public static String Rget(String Path,String Row){
		File f=new File(Path);
		Properties p=new Properties();
		try {
			p.load(new FileInputStream(f));
		} catch (IOException ignored) {
		}
		return p.getProperty(Row,"0");
	}
	public void mkdirs(String Path){
		new File(Path).mkdirs();
	}
	public boolean exists(String Path){
		return new File(Path).exists();
	}
	public String getfilename(String Path){
		String[] split=(Path).split("/");
		return split[split.length-1];
	}
	public String getfolder(String Path){
		String[] split=(Path).split("/");
		StringBuilder result= new StringBuilder();
		for(int i=0;i<=split.length-2;i++){
			result.append(split[i]).append("/");
		}
		return result.toString();
	}
	public String getupfolder(String Path){
		String[] split=(Path).split("/");
		StringBuilder result= new StringBuilder();
		int number;
		if(split[split.length - 1] == null){
			number=split.length-3;
		}else{
			number=split.length-2;
		}
		for(int i=0;i<=number;i++){
			result.append(split[i]).append("/");
		}
		return result.toString();
	}
	public static boolean deleteFile(String fileName) {
        File file=new File(fileName);
        if(file.exists()&&file.isFile()){
			return file.delete();
        }else{
            return false;
        }
    }
	
	public boolean delete(String Path){
		File dirFile=new File(Path);
		if((!dirFile.exists())||(!dirFile.isDirectory())){
			return false;
		}
		boolean flag=true;
		File[] files=dirFile.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				flag = deleteFile(file.getAbsolutePath());
				if (!flag)
					break;
			} else if (file.isDirectory()) {
				flag = delete(file.getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if(!flag){
			return false;
		}
		return dirFile.delete();
	}

	public String[] list(String Path){
		return new File(Path).list();
	}
	public File[] orderByName(String Path) {
		File[] file=new File(Path).listFiles();
		List<File> files = Arrays.asList(file);
		Collections.sort(files, (o1, o2) -> {
			if (o1.isDirectory() && o2.isFile())
				return -1;
			if (o1.isFile() && o2.isDirectory())
				return 1;
			return o1.getName().compareTo(o2.getName());
		});
		return file;
	}
	public void DownloadImage(String ur,String path){
		try {
			if(!new File(getfolder(path)).exists()){
				System.out.println(getfolder(path));
				System.out.println(new File(getfolder(path)).mkdirs());
			}
			URL url = new URL(ur);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5*1000);
            InputStream inStream = conn.getInputStream();
            byte[] data = readInputStream(inStream);
            File imageFile=new File(path);
            FileOutputStream outStream=new FileOutputStream(imageFile);
            outStream.write(data);
            outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static byte[] readInputStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len;
        while((len=inStream.read(buffer)) != -1 ){  
            outStream.write(buffer, 0, len);  
        }  
        inStream.close();  
        return outStream.toByteArray();  
    }
}

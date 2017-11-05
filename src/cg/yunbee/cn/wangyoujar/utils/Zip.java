package cg.yunbee.cn.wangyoujar.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipInputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import cg.yunbee.cn.wangyoujar.pojo.TAGS;

public class Zip {
	// 压缩
	public static byte[] compress(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(str.getBytes("UTF-8"));
		gzip.close();
		return out.toByteArray();
	}

	// 解压缩
	public static String uncompress(byte[] str) throws IOException {
		if (str == null) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(str);
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		// toString()使用平台默认编码，也可以显式的指定如toString(&quot;GBK&quot;)
		return out.toString("UTF-8");
	}
	
	public static void unZip(String zipPath, String zipOutDir) {
		TAGS.log("----------------unZip----------------------");
		File file = new File(zipPath);
		if(!file.exists()) return;
		File outFile = new File(zipOutDir);
		if(!outFile.exists()) {
			outFile.mkdir();
		}
		
		TAGS.log("unZip->zipPath: " + zipPath);
		TAGS.log("unZip->zipOutDir: " + zipOutDir);
		
		ZipInputStream zipInput = null;
		ZipEntry entry = null;
		ZipFile zipFile = null;
		InputStream input = null;
		OutputStream output = null;
		try {
			zipFile = new ZipFile(file);
			Enumeration<?> enumeration = zipFile.getEntries();
			while(enumeration.hasMoreElements()) {
				entry = (ZipEntry) enumeration.nextElement();
				File outPathFile = new File(outFile, entry.getName());
				
				boolean isFile = entry.getName().lastIndexOf(".") > 0;
				
				if(!isFile) {
					if(!outPathFile.exists())
						outPathFile.mkdirs();
					continue;
				} else {
					if(!outPathFile.getParentFile().exists())
						outPathFile.getParentFile().mkdirs();
				}
				
				System.out.println(outPathFile.getAbsolutePath());
				if(!outPathFile.exists())
					outPathFile.createNewFile();
				
				input = zipFile.getInputStream(entry);
				output = new FileOutputStream(outPathFile);
				byte[] buffer = new byte[1024];
				int temp = 0;
				while((temp = input.read(buffer)) != -1) {
					output.write(buffer, 0, temp);
					output.flush();
				}
			}
			
			TAGS.log("unZip: success!");
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(output != null)
					output.close();
				if(input != null)
					input.close();
				if(zipInput != null)
					zipInput.close();
				if(zipFile != null)
					zipFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}

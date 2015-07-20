package net.fmchan.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtil {
	public static String getFile(File file) {
		if (!file.exists())
			return null;
		String content = "";
		System.out.println("filename:n " + file);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF8"));
			String line = null;
			while (true) {
				line = reader.readLine();
				if (line == null)
					break;
				content += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return content;
	}

	public static boolean createDir(File theDir) {
		boolean result = false;
		if (!theDir.exists()) {
			System.out.println("creating directory: " + theDir);
			try {
				theDir.mkdir();
				result = true;
			} catch (SecurityException se) {
			}
			if (result) {
				System.out.println("DIR created");
			}
		}
		return result;
	}

	public static void delete(File dir) {
		if (!dir.exists())
			return;
		if (dir.isFile())
			dir.delete();
		else if (dir.isDirectory())
			for (File file : dir.listFiles())
				file.delete();
	}
}

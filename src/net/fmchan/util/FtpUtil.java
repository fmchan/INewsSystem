package net.fmchan.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.Map.Entry;

import net.fmchan.model.Queue;
import net.fmchan.model.Story;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

public class FtpUtil {
	final static Logger logger = Logger.getLogger(FtpUtil.class);
	static FTPClient ftp;

	public static void startConnection() {
		ftp = new FTPClient();
		try {
			ftp.connect(ConfigUtil.get().getString("ftp.host"));
			ftp.login(ConfigUtil.get().getString("ftp.user"), ConfigUtil.get()
					.getString("ftp.pass"));
			ftp.setControlEncoding("UTF-8");
		} catch (SocketException e) {
			logger.error("Socket exception: ", e);
		} catch (IOException e) {
			logger.error("IO exception: ", e);
		}
	}
	public static void closeConnection() {
		try {
			ftp.logout();
		} catch (IOException e) {
			logger.error("IO exception: ", e);
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException e) {
					logger.error("ftp exception: ", e);
				}
			}
		}
	}
	public static Queue getFiles(Queue backup, String dirPrefix) {
		//System.out.println("getFiles");
		String backupName = backup.getName();
		Queue update = new Queue(backupName);
		String filePrefix = dirPrefix + backupName;

		try {
			logger.debug("backupName:" + backupName);
			ftp.changeWorkingDirectory(backupName);
			FTPFile[] files = ftp.listFiles();
			System.out.println("ftp file size:" + files.length);
			FileUtil.createDir(new File(filePrefix));
			int i = 0;
			for (FTPFile file : files) {
				System.out.println("ftp filename: " + file.getName());
				if (file.isFile()) {
					String filename = file.getName().substring(0, 8);
					String address = file.getName().substring(9, 27);
					Story iNews = new Story(filename, address, i);

					if (backup.getStories() == null
							|| backup.getStories() != null
							&& !backup.getStories().containsKey(filename))
						iNews.setStatus('C');
					else if (backup.getStories().containsKey(filename)) {
						if (backup.getStories().get(filename).getAddress()
								.equals(address))
							iNews.setStatus('O');
						else
							iNews.setStatus('U');
					}

					if (iNews.getStatus() == 'C' || iNews.getStatus() == 'U') {
						FileOutputStream fops = new FileOutputStream(filePrefix
								+ "/" + filename);
						// System.out.println("Downloading file...");
						ftp.retrieveFile(file.getName(), fops);
						fops.flush();
						fops.close();
					}
					update.getStories().put(filename, iNews);
				}
				i++;
			}

			if (backup.getStories() != null)
				for (Entry<String, Story> entry : backup.getStories()
						.entrySet()) {
					if (!update.getStories().containsKey(entry.getKey())) {
						Story iNews = entry.getValue();
						if (iNews.getStatus() != 'D') {
							iNews.setStatus('D');
							iNews.setOrder(-1);
							update.getStories().put(entry.getKey(), iNews);
						}
					}
				}
		} catch (IOException e) {
			logger.error("IO exception: ", e);
		}
		return update;
	}
}

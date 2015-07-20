package net.fmchan.job;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map.Entry;

import net.fmchan.model.Queue;
import net.fmchan.model.Story;
import net.fmchan.output.CGTransacton;
import net.fmchan.parser.CGParser;
import net.fmchan.parser.PrompterParser;
import net.fmchan.util.ConfigUtil;
import net.fmchan.util.FileUtil;
import net.fmchan.util.FtpUtil;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CheckUpdateJob implements Job {
	public static String name = "";
	private static int type = ConfigUtil.get().getInt("output.type");
	private static String dirPrefix = ConfigUtil.get().getString(
			"directory.temp");
	private static String shareFolder = ConfigUtil.get().getString(
			"directory.output");
	private static String fullName;
	public static Queue backup;

	final static Logger logger = Logger.getLogger(CheckUpdateJob.class);

	public static void setBackupQueue(String n) {
		try {
			name = URLEncoder.encode(n, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fullName = name
				+ ConfigUtil.get().getString(
						type == 1 ? "name.rundown" : "name.cg");
		backup = new Queue(fullName);
		if (name != "" && type == 2) {
			CGTransacton.messages = new String[] { "m\\1\\D/"
					+ ConfigUtil.get().getString("output.drive.name")
					+ "\\\\\n" };
			CGTransacton.transaction();
		}
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		if (!ConfigUtil.lockFtp && name != null && name != "") {
			ConfigUtil.lockFtp = true;
			System.out.println("selected queue:" + name);
			System.out.println("backup story size:"
					+ backup.getStories().size());
			try {
				Queue update = FtpUtil.getFiles(backup, dirPrefix);
				for (Entry<String, Story> entry : update.getStories()
						.entrySet())
					System.out.println(entry.getKey() + " > "
							+ entry.getValue().getFilename() + " - "
							+ entry.getValue().getStatus());
				if (type == 1)
					PrompterParser.parseByFiles(shareFolder, dirPrefix, backup,
							update);
				else if (type == 2)
					CGParser.parseByFiles(dirPrefix, backup, update);
				FileUtil.delete(new File(dirPrefix, fullName));
				backup = update;
			} catch (Exception e) {
				logger.error("JobExecutionException: ", e);
			}
		}
	}
}

package net.fmchan.main;

import org.apache.log4j.xml.DOMConfigurator;

import net.fmchan.gui.Selector;
import net.fmchan.output.CGTransacton;
import net.fmchan.util.ConfigUtil;
import net.fmchan.util.FtpUtil;
import net.fmchan.util.JobUtil;

public class Main {
	public static CGTransacton cg;
	public static void main(String[] args) throws Exception {
		System.setProperty("file.encoding", "UTF-8");
		DOMConfigurator.configure("log4j.xml");
		ConfigUtil.setConfig("setting.properties");
		FtpUtil.startConnection();

		if (ConfigUtil.get().getInt("output.type") == 2) {
			cg = new CGTransacton();
			cg.open();
			cg.input();
		}

		Selector.start();
		JobUtil.job();
	}
}

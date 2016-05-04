package net.fmchan.util;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class ConfigUtil {
	private static String config;

	public static boolean lockFtp = false;

	public static Configuration get() {
		try {
			return new PropertiesConfiguration(config);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static String getConfig() {
		return config;
	}

	public static void setConfig(String config) {
		ConfigUtil.config = config;
	}
}

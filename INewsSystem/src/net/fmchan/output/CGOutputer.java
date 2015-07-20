package net.fmchan.output;

import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import gnu.io.CommPortIdentifier;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

import net.fmchan.util.ConfigUtil;

public class CGOutputer {
	@SuppressWarnings("rawtypes")
	static Enumeration portList;
	static CommPortIdentifier portId;
	static SerialPort serialPort;
	static OutputStream outputStream;

	final static Logger logger = Logger.getLogger(CGOutputer.class);

	public static void execute(String message) {
		portList = CommPortIdentifier.getPortIdentifiers();

		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();
			System.out.println(portId.getName() + "\t" + portId.getPortType());
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL
					&& portId.getName().equals(
							ConfigUtil.get().getString("output.comm.name"))) {
				try {
					serialPort = (SerialPort) portId.open("SimpleWriteApp",
							ConfigUtil.get().getInt("output.comm.port"));
				} catch (PortInUseException e) {
					//logger.error("PortInUseException: ", e);
				}
				try {
					outputStream = serialPort.getOutputStream();
				} catch (IOException e) {
					logger.error("IOException: ", e);
				}
				try {
					serialPort.setSerialPortParams(
							ConfigUtil.get().getInt("output.comm.bps"),
							SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
							SerialPort.PARITY_NONE);
				} catch (UnsupportedCommOperationException e) {
					logger.error("UnsupportedCommOperationException: ", e);
				}
				try {
					System.out.println(message);
					logger.info("message: " + message);
					outputStream.write(message.getBytes("Big5"));
					// outputStream.write(new String(message.getBytes(),
					// "Big5").getBytes());
				} catch (IOException e) {
					logger.error("IOException: ", e);
				}
			}
		}
	}
}

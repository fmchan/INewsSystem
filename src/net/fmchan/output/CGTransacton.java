package net.fmchan.output;

import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import gnu.io.CommPortIdentifier;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

import net.fmchan.util.ConfigUtil;

public class CGTransacton implements Runnable, SerialPortEventListener {
	@SuppressWarnings("rawtypes")
	static Enumeration portList;
	static CommPortIdentifier portId;
	static SerialPort serialPort;
	static OutputStream outputStream;
	static InputStream inputStream;
	Thread readThread;
	static public String[] messages;
	int messageIndex = 0;

	final static Logger logger = Logger.getLogger(CGOutputer.class);

	public static void transaction() {
		output(messages[0]);
	}
	public void open() {
		portList = CommPortIdentifier.getPortIdentifiers();

		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();
			//System.out.println(portId.getName() + "\t" + portId.getPortType());
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL
					&& portId.getName().equals(
							ConfigUtil.get().getString("output.comm.name"))) {
				try {
					serialPort = (SerialPort) portId.open("SimpleWriteApp",
							ConfigUtil.get().getInt("output.comm.port"));
				} catch (PortInUseException e) {
					// logger.error("PortInUseException: ", e);
				}
				setSerialPort();
			}
		}
	}
	public void setSerialPort() {
		try {
			serialPort.setSerialPortParams(
					ConfigUtil.get().getInt("output.comm.bps"),
					SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
		} catch (UnsupportedCommOperationException e) {
			logger.error("UnsupportedCommOperationException: ", e);
		}
	}
	public static void output(String message) {
		try {
			outputStream = serialPort.getOutputStream();
		} catch (IOException e) {
			logger.error("IOException: ", e);
		}
		try {
			System.out.println("message: " + message);
			logger.info("message: " + message);
			outputStream.write(message.getBytes("BIG5_HKSCS"));
			// outputStream.write(new String(message.getBytes(),
			// "Big5").getBytes());
		} catch (IOException e) {
			logger.error("IOException: ", e);
		}
	}
	public void input() {
		try {
			inputStream = serialPort.getInputStream();
		} catch (IOException e) {
			logger.error("IOException: ", e);
		}
		try {
			serialPort.addEventListener(this);
		} catch (TooManyListenersException e) {
			//logger.error("TooManyListenersException: ", e);
		}
		serialPort.notifyOnDataAvailable(true);
		readThread = new Thread(this);
		readThread.start();
	}

	@Override
	public void run() {
		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			logger.error("InterruptedException: ", e);
		}
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		case SerialPortEvent.DATA_AVAILABLE:
			byte[] readBuffer = new byte[20];

			try {
				while (inputStream.available() > 0) {
					inputStream.read(readBuffer);
				}
				if (new String(readBuffer, "BIG5_HKSCS").contains("*")) {
					messageIndex++;
					System.out.println("true");
					if (messageIndex < messages.length)
						output(messages[messageIndex]);
					else {
						messageIndex = 0;
						ConfigUtil.lockFtp = false;
					}
				}
			} catch (IOException e) {
				logger.error("IOException: ", e);
			}
			break;
		}
	}
}

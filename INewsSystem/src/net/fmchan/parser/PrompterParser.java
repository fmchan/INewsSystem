package net.fmchan.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;

import net.fmchan.model.Prompter;
import net.fmchan.model.Queue;
import net.fmchan.model.Story;
import net.fmchan.util.ConfigUtil;
import net.fmchan.util.FileUtil;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PrompterParser extends XmlParser {
	final static Logger logger = Logger.getLogger(PrompterParser.class);
	private static String labelPi = ConfigUtil.get().getString("label.pi");

	public static void parseByFiles(String shareFolder, String dirPrefix,
			Queue backup, Queue update) {
		if (backup != null && backup.getUuid() != null)
			update.setUuid(backup.getUuid());
		else
			update.initPrompter();

		boolean writeIndexFile = false;
		for (Entry<String, Story> entry : update.getStories().entrySet()) {
			Story ni = entry.getValue();
			if (ni.getStatus() == 'C' || ni.getStatus() == 'U') {
				// parse data
				try {
					Prompter np = parser(dirPrefix + update.getName(),
							ni.getFilename());
					if (ni.getStatus() == 'U' && backup.getStories() != null
							&& backup.getStories().containsKey(entry.getKey())) {
						Prompter op = backup.getStories().get(entry.getKey())
								.getPrompter();
						np.setUuid(op.getUuid());
						if (op != null && np.getSlug().equals(op.getSlug())
								&& np.getBody().equals(op.getBody())
								&& (!op.isBreak() || np.isBreak())) {
							ni.setStatus('O');
							if (ni.getOrder() != backup.getStories()
									.get(entry.getKey()).getOrder()
									|| op.getVersion() != np.getVersion())
								writeIndexFile = true;
						}
					}
					ni.setPrompter(np);
				} catch (Exception e) {
					logger.error("Cannot parse Prompter xml: " + e);
				}
			} else if (backup.getStories() != null
					&& backup.getStories().containsKey(entry.getKey())) {
				// no parse data
				Prompter op = backup.getStories().get(entry.getKey())
						.getPrompter();
				if (op != null) {
					if (ni.getStatus() == 'D') {
						FileUtil.delete(new File(
								shareFolder + backup.getUuid(), op.getUuid()));
						writeIndexFile = true;
					} else if (ni.getStatus() == 'O')
						ni.setPrompter(op);
				}
			}
		}
		/* output body file */
		for (Entry<String, Story> entry : update.getStories().entrySet()) {
			Story ni = entry.getValue();
			if (ni.getStatus() == 'C' || ni.getStatus() == 'U') {
				writeIndexFile = true;
				if (!ni.getPrompter().isBreak())
					createBodyFile(ni.getPrompter(),
							shareFolder + update.getUuid());
			}
		}
		/* output index file */
		if (writeIndexFile)
			try {
				PrintWriter writer = new PrintWriter(shareFolder
						+ update.getUuid() + ".DAT", "UTF-16LE");
				writer.write("\uFEFF");
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				writer.println(URLDecoder.decode(update.getName(), "UTF-8")
						+ "\t" + df.format(new Date()) + "\t"
						+ df.format(new Date()));
				for (Entry<String, Story> entry : update.getStories()
						.entrySet()) {
					Prompter np = entry.getValue().getPrompter();
					if (!np.isBreak() && entry.getValue().getStatus() != 'D') {
						System.out.println(np.getOutput());
						writer.println(np.getOutput());
					}
				}
				writer.close();
			} catch (Exception e) {
				logger.error("Cannot output Prompter to file: " + e);
			}
		ConfigUtil.lockFtp = false;
	}

	private static boolean createBodyFile(Prompter prompter, String filePath) {
		if (prompter.getFilename() == null || !new File(filePath).exists()
				&& !FileUtil.createDir(new File(filePath)))
			return false;
		PrintWriter writer;
		try {
			writer = new PrintWriter(filePath + "\\" + prompter.getUuid(),
					"UTF-16LE");
			writer.write("\uFEFF");
			logger.info("create body: " + prompter.getFilename() + ", "
					+ prompter.getSlug());
			String[] body = prompter.getBody().split("\n");
			for (String p : body)
				writer.println(p);
			writer.close();
			return true;
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException: " + e);
		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException: " + e);
		}
		return false;
	}

	private static Prompter parser(String filePrefix, String file) {
		String slug = null;
		String format = null;
		int version = 0;
		String body = "";
		boolean isBreak = false;

		String content = FileUtil.getFile(new File(filePrefix, file));
		if (content == null)
			return new Prompter();
		try {
			Document doc = loadXMLFromString(content);
			doc.getDocumentElement().normalize();
			NodeList listOfMeta = doc.getElementsByTagName("meta");
			if (listOfMeta != null && listOfMeta.getLength() > 0) {
				Node nodeOfMeta = listOfMeta.item(0);
				if ((nodeOfMeta.getNodeType() == Node.ELEMENT_NODE)) {
					Element elOfMeta = (Element) nodeOfMeta;
					if (elOfMeta.getAttribute("version") != null
							&& elOfMeta.getAttribute("version") != "")
						version = Integer.valueOf(elOfMeta
								.getAttribute("version"));
					/*
					 * if (elOfMeta.getAttribute("break") != null &&
					 * elOfMeta.getAttribute("break").equals("1")) isBreak =
					 * true;
					 */
					// System.out.println("version: " + version);
				}
			}
			NodeList listOfString = doc.getElementsByTagName("string");
			for (int i = 0; i < listOfString.getLength(); i++) {
				Node nodeOfString = listOfString.item(i);
				if ((nodeOfString.getNodeType() == Node.ELEMENT_NODE)) {
					Element elOfString = (Element) nodeOfString;
					if (elOfString.getAttribute("id").equals(
							ConfigUtil.get().getString("attribute.title"))) {
						slug = elOfString.getTextContent();
						// System.out.println("slug: " + slug);
					} else if (elOfString.getAttribute("id").equals(
							ConfigUtil.get().getString("attribute.suffix"))) {
						format = elOfString.getTextContent();
						// System.out.println("format: " + format);
					}
				}
			}
			NodeList listOfCc = doc.getElementsByTagName("cc");
			if (listOfCc != null && listOfCc.getLength() > 0) {
				for (int i = 0; i < listOfCc.getLength(); i++) {
					Node nodeOfCc = listOfCc.item(i);
					if ((nodeOfCc.getNodeType() == Node.ELEMENT_NODE))
						nodeOfCc.setTextContent("");
				}
			}
			if (labelPi != null && labelPi != "") {
				NodeList listOfPi = doc.getElementsByTagName("pi");
				if (listOfPi != null && listOfPi.getLength() > 0) {
					for (int i = 0; i < listOfPi.getLength(); i++) {
						Node nodeOfPi = listOfPi.item(i);
						if ((nodeOfPi.getNodeType() == Node.ELEMENT_NODE)) {
							Element elOfPi = (Element) nodeOfPi;
							nodeOfPi.setTextContent(labelPi.charAt(0)
									+ elOfPi.getTextContent()
									+ labelPi.charAt(1));
						}
					}
				}
			}
			NodeList listOfP = doc.getElementsByTagName("p");
			if (listOfP != null && listOfP.getLength() > 0) {
				for (int i = 0; i < listOfP.getLength(); i++) {
					Node nodeOfP = listOfP.item(i);
					if ((nodeOfP.getNodeType() == Node.ELEMENT_NODE)) {
						Element elOfP = (Element) nodeOfP;
						body += elOfP.getTextContent() + "\n";
					}
				}
			}
		} catch (Exception e) {
			logger.error("Cannot load Prompter Xml to string: ", e);
		}

		// System.out.println(body);
		Prompter prompter = new Prompter(file, slug, format, version, body,
				isBreak);
		System.out.println("parsed:" + prompter.getUuid() + "\t"
				+ prompter.getSlug());
		return prompter;
	}
}
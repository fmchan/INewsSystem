package net.fmchan.parser;

import java.io.File;
import java.util.LinkedList;
import java.util.Map.Entry;

import net.fmchan.model.CG;
import net.fmchan.model.Queue;
import net.fmchan.model.Story;
import net.fmchan.output.CGTransacton;
import net.fmchan.util.ConfigUtil;
import net.fmchan.util.FileUtil;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CGParser extends XmlParser {
	final static Logger logger = Logger.getLogger(CGParser.class);

	public static void parseByFiles(String dirPrefix, Queue backup, Queue update) {
		int indexOffset = 0;
		for (Entry<String, Story> entry : update.getStories().entrySet()) {
			Story ni = entry.getValue();
			//System.out.println("state:" + ni.getStatus());
			if (ni.getStatus() == 'C' || ni.getStatus() == 'U') {
				// parse data
				try {
					CG np = parser(dirPrefix + update.getName(),
							ni.getFilename());
					if (np != null) {
						if (indexOffset > 0)
							ni.setOrder(ni.getOrder() - indexOffset);
						np.setIndex(Queue.getINDEX() + ni.getOrder());
						if (ni.getStatus() == 'U'
								&& backup.getStories() != null
								&& backup.getStories().containsKey(
										entry.getKey())) {
							CG op = backup.getStories().get(entry.getKey())
									.getCg();
							if (op != null
									&& np.getOutput().equals(op.getOutput()))
								ni.setStatus('O');
						}
						ni.setCg(np);
					} else
						indexOffset++;
				} catch (Exception e) {
					logger.error("Cannot parse CG xml: " + e);
				}
			} else if (backup.getStories() != null
					&& backup.getStories().containsKey(entry.getKey())) {
				// no parse data
				Story oi = backup.getStories().get(entry.getKey());

				if (ni.getStatus() == 'O') {
					if (oi.getCg() == null)
						indexOffset++;
					else
						ni.setCg(oi.getCg());
					if (indexOffset > 0)
						ni.setOrder(ni.getOrder() - indexOffset);
					if (ni.getCg() != null && ni.getOrder() != oi.getOrder()) {
						ni.getCg().setIndex(Queue.getINDEX() + ni.getOrder());
						ni.setStatus('U');
					}
				}
			}
		}
		/* output body file */
		LinkedList<String> messages = new LinkedList<String>();
		for (Entry<String, Story> entry : update.getStories().entrySet()) {
			Story ni = entry.getValue();
			if ((ni.getStatus() == 'C' || ni.getStatus() == 'U')
					&& ni.getCg() != null)
				// CGOutputer.execute(ni.getCg().getOutput());
				messages.add(ni.getCg().getOutput());
		}
		System.out.println("messages.size:" + messages.size());
		CGTransacton.messages = messages.toArray(new String[messages.size()]);
		if (messages.size() > 0)
			CGTransacton.transaction();
		else
			ConfigUtil.lockFtp = false;
	}

	private static CG parser(String filePrefix, String file) {
		String template = "";
		String body = "";
		String content = FileUtil.getFile(new File(filePrefix, file));
		boolean getBody = false;
		if (content == null)
			return null;
		try {
			Document doc = loadXMLFromString(content.replace("&#0;", ""));
			doc.getDocumentElement().normalize();
			/*NodeList listOfMeta = doc.getElementsByTagName("meta");
			if (listOfMeta != null && listOfMeta.getLength() > 0) {
				Node nodeOfMeta = listOfMeta.item(0);
				if ((nodeOfMeta.getNodeType() == Node.ELEMENT_NODE)) {
					Element elOfMeta = (Element) nodeOfMeta;
					if (elOfMeta.getAttribute("break") != null
							&& elOfMeta.getAttribute("break").equals("1"))
						return null;
				}
			}*/
			NodeList listOfString = doc.getElementsByTagName("string");
			for (int i = 0; i < listOfString.getLength(); i++) {
				Node nodeOfString = listOfString.item(i);
				if ((nodeOfString.getNodeType() == Node.ELEMENT_NODE)) {
					Element elOfString = (Element) nodeOfString;
					if (elOfString.getAttribute("id").equals("cg-template")
							|| elOfString.getAttribute("id").equals(
									"CG-TEMPLATE"))
						template = elOfString.getTextContent();
					else if (elOfString.getAttribute("id").equals("cg-text")
							|| elOfString.getAttribute("id").equals(
									"CG-TEXT"))
						//body = elOfString.getTextContent();
						getBody = true;
				}
			}
			if (getBody) {
				NodeList listOfP = doc.getElementsByTagName("p");
				if (listOfP != null && listOfP.getLength() > 0) {
					for (int i = 0; i < listOfP.getLength(); i++) {
						Node nodeOfP = listOfP.item(i);
						if ((nodeOfP.getNodeType() == Node.ELEMENT_NODE)) {
							Element elOfP = (Element) nodeOfP;
							body += elOfP.getTextContent() + "\\";
						}
					}
				}
			}

		} catch (Exception e) {
			logger.error("Cannot load CG Xml to string: ", e);
		}
		if (template == "") return null;
		return new CG(template, body);
	}
}

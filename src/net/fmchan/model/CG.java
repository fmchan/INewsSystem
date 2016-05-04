package net.fmchan.model;

public class CG {
	private int index;
	private String template;
	private String body;
	private static String PREFIX = "w\\1";
	
	public CG(String template, String body) {
		super();
		this.template = template;
		this.body = body;
	}
	public CG() {};
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public static String getPREFIX() {
		return PREFIX;
	}
	public static void setPREFIX(String pREFIX) {
		PREFIX = pREFIX;
	}
	public String getOutput() {
		return getPREFIX() + "\\" + getIndex() + "\\"
				+ getTemplate() + "\\" + getBody() + "\\\n";
	}
	@Override
	public String toString() {
		return "CG [index=" + index + ", template=" + template + ", body="
				+ body + "]";
	}
}

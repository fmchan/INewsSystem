package net.fmchan.model;

public class Story {
	private String filename;
	private String address;
	private char status; // C, U, D, O
	private int order;
	private Prompter prompter;
	private CG cg;

	public Story(String filename, String address, int order) {
		super();
		this.filename = filename;
		this.address = address;
		this.order = order;
	}
	public Story() {}

	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	public Prompter getPrompter() {
		return prompter;
	}
	public void setPrompter(Prompter prompter) {
		this.prompter = prompter;
	}
	public CG getCg() {
		return cg;
	}
	public void setCg(CG cg) {
		this.cg = cg;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
}

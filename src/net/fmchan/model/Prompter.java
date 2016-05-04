package net.fmchan.model;
import java.util.UUID;

public class Prompter {
	private String uuid;
	private String filename;
	private String slug;
	private String format;
	private int version;
	private String body;
	private boolean isBreak;

	public Prompter(String filename, String slug, String format, int version,
			String body, boolean isBreak) {
		super();
		this.uuid = UUID.randomUUID().toString().toUpperCase();
		this.filename = filename;
		this.slug = slug;
		this.format = format;
		this.version = version;
		this.body = body;
		this.isBreak = isBreak;
	}

	public Prompter() {};

	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getSlug() {
		return slug;
	}
	public void setSlug(String slug) {
		this.slug = slug;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	@Override
	public String toString() {
		return "Prompter [filename=" + filename + ", slug=" + slug
				+ ", format=" + format + ", version=" + version + ", body="
				+ body + ", uuid=" + uuid + "]";
	}
	public String getOutput() {
		String format = getFormat() != null && getFormat() != "" ? "-" + getFormat() : "";
		return getUuid() + "\t\t" + getVersion() + "\t" + getSlug() + format;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public boolean isBreak() {
		return isBreak;
	}

	public void setBreak(boolean isBreak) {
		this.isBreak = isBreak;
	}
}

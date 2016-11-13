package corrector;

public class PrefixWordSuffix {
	private String prefix;
	private Attribute attribute;
	private String suffix;

	public PrefixWordSuffix(String prefix, Attribute attribute, String suffix) {
		this.prefix = prefix;
		this.attribute = attribute;
		this.suffix = suffix;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	
}

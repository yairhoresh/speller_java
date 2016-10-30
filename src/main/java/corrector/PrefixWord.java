package corrector;

public class PrefixWord {
	private String prefix;
	private Attribute attribute;

	public PrefixWord(String prefix, Attribute attribute) {
		this.prefix = prefix;
		this.attribute = attribute;
	}

	public String getPrefix() {
		return prefix;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	
}

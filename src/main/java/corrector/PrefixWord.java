package corrector;

public class PrefixWord {
	private String prefix;
	private Attribute wordAttribute;

	public PrefixWord(String prefix, Attribute wordAttribute) {
		this.prefix = prefix;
		this.wordAttribute = wordAttribute;
	}

	public String getPrefix() {
		return prefix;
	}

	public Attribute getWordAttribute() {
		return wordAttribute;
	}

	
}

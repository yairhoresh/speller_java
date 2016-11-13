package corrector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WordPotentialMeanings {

	private String originalWord;
	final private Set<String> prefixes;
	final private Map<String, List<Attribute>> wordToAttributes;

	private List<PrefixWordSuffix> listOfPotentialInstances = new ArrayList<>();
	// Map<WordAttribute, String> attributeToWord = new HashMap<>();
	
	
	public WordPotentialMeanings(Set<String> prefixes, Map<String, List<Attribute>> wordToAttributes, String originalWord) {
		this.prefixes = prefixes;
		this.wordToAttributes = wordToAttributes;
		addWord(originalWord);
	}
	
	
	private void addWord(String originalWord) {
		
		this.originalWord = originalWord;
		addPotentials(originalWord, null);
		
		// break it 
		for (String prefix : prefixes) {
			if (originalWord.startsWith(prefix)) {
				String wordWithoutThePrefix = originalWord.substring(prefix.length());
				addPotentials(prefix, wordWithoutThePrefix);
			}
		}
	}

	
	private void addPotentials(String prefix, String word) {

		// add direct instances
		if (word == null) {
			PrefixWordSuffix prefixWord = new PrefixWordSuffix(prefix, new Attribute(-1, null, null));
			listOfPotentialInstances.add(prefixWord);
			return;
		}

		// add instances of word attribute
		List<Attribute> listOfAttributes = wordToAttributes.get(word);
		if (listOfAttributes == null)
			return;
		for (Attribute wordAttribute : listOfAttributes) {
			PrefixWordSuffix prefixWord = new PrefixWordSuffix(prefix, wordAttribute);
			listOfPotentialInstances.add(prefixWord);
		}
	}

	
	public String getOriginalWord() {
		return originalWord;
	}

	
	public List<PrefixWordSuffix> getListOfPotentialInstances() {
		return listOfPotentialInstances;
	}
	
	

	
}

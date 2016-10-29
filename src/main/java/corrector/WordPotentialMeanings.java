package corrector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WordPotentialMeanings {
	
	private String originalWord;
	private Set<String> prefixes;
	private Map<String, List<Attribute>> wordToAttributes;
	private List<PrefixWord> listOfPotentialInstances = new ArrayList<>();
	// Map<WordAttribute, String> attributeToWord = new HashMap<>();
	
	
	public WordPotentialMeanings(Set<String> prefixes, Map<String, List<Attribute>> wordToAttributes, String originalWord) {
		this.prefixes = prefixes;
		this.wordToAttributes = wordToAttributes;
		addWord(originalWord);
	}
	
	
	private void addWord(String originalWord) {
		
		this.originalWord = originalWord;
		addPotentials("", originalWord);
		
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
		PrefixWord prefixWord = new PrefixWord(prefix, new Attribute(-1, word));
		listOfPotentialInstances.add(prefixWord);

		// add instances of word attribute
		List<Attribute> listOfAttributes = wordToAttributes.get(word);
		if (listOfAttributes == null)
			return;
		for (Attribute wordAttribute : listOfAttributes) {
			
			prefixWord = new PrefixWord(prefix, wordAttribute);
			listOfPotentialInstances.add(prefixWord);
		}
	}

	
	public String getOriginalWord() {
		return originalWord;
	}

	
	public List<PrefixWord> getListOfPotentialInstances() {
		return listOfPotentialInstances;
	}
	
	

	
}
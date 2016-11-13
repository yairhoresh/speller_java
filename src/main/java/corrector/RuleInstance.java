package corrector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.Range;
import common.Utilities;

public class RuleInstance {
		
	private String wrongTextString;
	private String correctTextString;
	private List<PrefixWordSuffix> prefixWordListWrongRule;

	private Map<Range, Range> correctionTable = new HashMap<>();
	private List<Integer> wrongFormMatchingIndices = new ArrayList<>();
	private List<Integer> correctFormMatchingIndices = new ArrayList<>();
			
	public RuleInstance(final Map<Attribute,String> attributeToWord, final String wrongTextString, final String correctTextString) {
		
		this.wrongTextString = wrongTextString;
		this.correctTextString = correctTextString;
		
		compile(attributeToWord);
	
	}

	
	private void compile(Map<Attribute,String> attributeToWord) {
		
		prefixWordListWrongRule = fromStringToPrefixWordList(attributeToWord, wrongTextString);
		List<PrefixWordSuffix> prefixWordListCorrectRule = fromStringToPrefixWordList(attributeToWord, correctTextString);
		
		correctionTable.put(new Range(0, prefixWordListWrongRule.size()), new Range(0, prefixWordListCorrectRule.size()));
		// TODO: build a fine matcher
		/*
		lcs(prefixWordListWrongRule, correctRuleTokens);
		
		// build correction table
		// go over the matching indices, whatever is not matched enters the correction table
		int prevMatchWrongForm = -1;
		int prevMatchCorrectForm = -1;
		for (int i = 0; i < wrongFormMatchingIndices.size(); i++) {	// they must be of the same size
		
			int currentWrongIndex = wrongFormMatchingIndices.get(i); 
			int currentCorrectIndex = correctFormMatchingIndices.get(i);
			
			correctionTable.put(new Range(prevMatchWrongForm + 1, currentWrongIndex), 
					new Range(prevMatchCorrectForm + 1, currentCorrectIndex));
			
			prevMatchWrongForm = currentWrongIndex;
			prevMatchCorrectForm = currentCorrectIndex;
		}*/
	}

	
	public List<Range> getMatches(List<WordPotentialMeanings> inputTextInFullWordForm) {
		
		List<Range> matches = new ArrayList<>();
				
		// go over the text
		// TODO: inexact pattern matching
		for (int i = 0; i <= inputTextInFullWordForm.size() - prefixWordListWrongRule.size(); i++) {
			
			int endMatchIndex = exactMatchIndex(inputTextInFullWordForm, i);
			if (endMatchIndex != -1)
				matches.add(new Range(i, endMatchIndex));
		}
				
		return matches;
	}
	
	
	private int exactMatchIndex(List<WordPotentialMeanings> inputTextInFullWordForm, int startingIndex) {
		
		int textIndex = startingIndex;

		for (PrefixWordSuffix ruleToken : prefixWordListWrongRule) {

			WordPotentialMeanings prefixWordFromText = inputTextInFullWordForm.get(textIndex);
						
			String matchResult = matchType(prefixWordFromText, ruleToken, state, prefixOffset);
			
			if (matchResult == null) 
				return -1; 

			if (matchResult.equals("full word match")) {
				textIndex++;
				continue;
			}
			
			
			if (matchResult.startsWith("prefix match")) {
				continue; 
			}
			
		
			/*if (matchResult.endsWith("suffix match")) {
				textIndex++;
				continue;
			}*/

		}
		return textIndex;
	}



	private String matchType(WordPotentialMeanings oneWordFromText, PrefixWordSuffix ruleToken, String state, int prefixOffset) {

		String ruleTokenType = "XXXX";
		String ruleTokenValue = "XXXX";

		// simple exact match with the original word
		//if (ruleTokenType.equals("body") && ruleTokenValue.equals(oneWordFromText.getOriginalWord())) 
		//	return "full word match";

		// go over potential instances
		for (PrefixWordSuffix oneWordFromTextInstance : oneWordFromText.getListOfPotentialInstances()) {
			
			if (ruleTokenType.equals("prefix") && state.equals("body")) 
				return null;
			
			// prefix, body, suffix, attribute
			Attribute oneWordFromTextwordAttribute = oneWordFromTextInstance.getWordAttribute();
			String oneWordFromTextInstanceBody = oneWordFromTextwordAttribute.getName();
			
			// omit prefix offset
			oneWordFromTextInstanceBody = oneWordFromTextInstanceBody.substring(prefixOffset);
			
			boolean isAttribute = oneWordFromTextwordAttribute.getRecordId() == -1;

			if (state.equals("all") && ruleTokenType.equals("body") && ruleTokenValue.equals(oneWordFromTextInstanceBody)) 
				return "full word match";
			
			//boolean isAttribute = oneWordFromTextwordAttribute.getId() != -1; 
						
			// prefix match
			if (ruleTokenType.equals("prefix")) {
				String oneWordFromTextInstancePrefix = oneWordFromTextInstance.getPrefix();
				int startWithLastIndex = Utilities.startWithGetIndex(oneWordFromTextInstancePrefix, ruleTokenValue);
			
				if (startWithLastIndex != -1) 
					return "prefix match " + startWithLastIndex;
			}
			
			// suffix match
			/*
			if (ruleTokenType.equals("suffix")) {
				String oneWordFromTextInstanceSuffix = oneWordFromTextInstance.getSuffix();
				int endWithLastIndex = Utilities.endWithGetIndex(oneWordFromTextInstanceSuffix, ruleTokenValue);
				
				if (endWithLastIndex != -1) 
					return "suffix match " + endWithLastIndex;
			}*/
										
		}
			
		return null;
	}

	
	private List<PrefixWordSuffix> fromStringToPrefixWordList(final Map<Attribute, String> attributeToWord, String ruleString) {

		List<PrefixWordSuffix> prefixWordList = new ArrayList<>();
		
		//ruleString = ruleString.replaceAll("\\+", " \\+");
		//ruleString = ruleString.replaceAll("_", "_ ");
		String[] words = ruleString.split(" ");
		
		for (String word : words) {

			String prefix = "";
			String body = "";

			int endOfPrefix = word.indexOf('_');
			if (endOfPrefix != -1)
				prefix = word.substring(0, endOfPrefix);
			

			body = word.substring(prefix.length(), word.length());
			
			if (body.startsWith("[")) {
				String bodyNoBrackets  = word.substring(1, word.length() - 1);
				if (attributeToWord.get(bodyNoBrackets) == null) {
					System.out.println("ERROR, attribute not found:" + body);
					//System.exit(0);
//TODO: why do we use WordAttribute?
				}
			}
			
			prefixWordList.add(new PrefixWordSuffix(prefix, new Attribute(-1 ,"XXXXXX")));
		}
		
		return prefixWordList;
	}
}



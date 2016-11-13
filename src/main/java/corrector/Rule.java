package corrector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import common.Range;



public class Rule {
	
	private String referenceString;
	private String wrongTextString;
	private String correctTextString;
	private String severityString;
	private String inhibitorString;
	private String expansionString;
	
	private List<RuleInstance> ruleInstances = new ArrayList<>();
	
	Rule(Map<Attribute,String> attributeToWord, String wrongTextString, String correctTextString, String inhibitorString, String expansionString,
			String severityString, String referenceString) {
		
		this.wrongTextString = wrongTextString;
		this.correctTextString = correctTextString;
		this.inhibitorString = inhibitorString;
		this.expansionString = expansionString;
		this.severityString = severityString;
		this.referenceString = referenceString;
		
		compile(attributeToWord);
	
	}


	public void compile(Map<Attribute,String> attributeToWord) {


		// sanity check
		if (wrongTextString.contains("|") && correctTextString.contains("|")) {
			System.out.println("ERROR: both sides contain | sign");
			System.exit(0);
		}

		// stage 1: ignore expansion, build instances

		ruleInstances.add(new RuleInstance(attributeToWord, wrongTextString, correctTextString));
		
		// stage 2: get expansions
		
		List<String[]> headAndReplacersList = breakExpansionToHeadAndReplacers();
		
		// check expansions
		if (headAndReplacersList == null)
			return;

		for (String[] headAndReplacers : headAndReplacersList) {

			String head = headAndReplacers[0];
			
			for (int i = 1; i < headAndReplacers.length; i++) {
				
				String replacement = headAndReplacers[i];
				String modifiedWrongTextString = wrongTextString.replaceAll(head, replacement);
				String modifiedCorrectTextString = correctTextString.replaceAll(head, replacement);
				RuleInstance ruleInstance = new RuleInstance(attributeToWord, modifiedWrongTextString, modifiedCorrectTextString);
				ruleInstances.add(ruleInstance);
			}
			
		}
		
	
	}

	
	public List<Range> getMatches(List<WordPotentialMeanings> inputTextInFullWordForm) {
		
		List<Range> matches = new ArrayList<>();
		
		// go over the instances
		for (RuleInstance ruleInstance : ruleInstances) {
			matches.addAll(ruleInstance.getMatches(inputTextInFullWordForm));
		}
		
		
		return matches;
		
	}
	


	private List<String[]> breakExpansionToHeadAndReplacers() {

		if (expansionString == null || expansionString.isEmpty())
			return null;

		List<String[]> headAndReplacers = new ArrayList<>();

		// a comma separates expansion rules
		String[] expansionRules = expansionString.split(",");

		for (String expansionRule : expansionRules) {

			expansionRule = expansionRule.trim();
			int miunsSign = expansionRule.indexOf('-');
			if (miunsSign == -1)
			{
				System.out.println("ERROR: minus sign was not found");
				System.exit(0);
			}

			String[] replacers = expansionRule.substring(miunsSign + 1).split("[|]");

			String[] headAndReplacer = new String[replacers.length + 1];

			headAndReplacer[0] = expansionRule.substring(0, miunsSign).trim();

			for (int j = 1; j <= replacers.length; j++)
				headAndReplacer[j] = replacers[j - 1].trim();

			headAndReplacers.add(headAndReplacer);
		}
		return headAndReplacers;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((correctTextString == null) ? 0 : correctTextString
						.hashCode());
		result = prime * result
				+ ((expansionString == null) ? 0 : expansionString.hashCode());
		result = prime * result
				+ ((inhibitorString == null) ? 0 : inhibitorString.hashCode());
		result = prime * result
				+ ((referenceString == null) ? 0 : referenceString.hashCode());
		result = prime * result
				+ ((ruleInstances == null) ? 0 : ruleInstances.hashCode());
		result = prime * result
				+ ((severityString == null) ? 0 : severityString.hashCode());
		result = prime * result
				+ ((wrongTextString == null) ? 0 : wrongTextString.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rule other = (Rule) obj;
		if (correctTextString == null) {
			if (other.correctTextString != null)
				return false;
		} else if (!correctTextString.equals(other.correctTextString))
			return false;
		if (expansionString == null) {
			if (other.expansionString != null)
				return false;
		} else if (!expansionString.equals(other.expansionString))
			return false;
		if (inhibitorString == null) {
			if (other.inhibitorString != null)
				return false;
		} else if (!inhibitorString.equals(other.inhibitorString))
			return false;
		if (referenceString == null) {
			if (other.referenceString != null)
				return false;
		} else if (!referenceString.equals(other.referenceString))
			return false;
		if (ruleInstances == null) {
			if (other.ruleInstances != null)
				return false;
		} else if (!ruleInstances.equals(other.ruleInstances))
			return false;
		if (severityString == null) {
			if (other.severityString != null)
				return false;
		} else if (!severityString.equals(other.severityString))
			return false;
		if (wrongTextString == null) {
			if (other.wrongTextString != null)
				return false;
		} else if (!wrongTextString.equals(other.wrongTextString))
			return false;
		return true;
	}


}


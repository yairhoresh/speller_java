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
	protected String inhibitorString;
	private String expensionString;
	
	private List<RuleInstance> ruleIntances = new ArrayList<RuleInstance>();
	
	Rule(Map<Attribute,String> attributeToWord, String wrongTextString, String correctTextString, String inhibitorString, String expensionString, 
			String severityString, String referenceString) {
		
		this.wrongTextString = wrongTextString;
		this.correctTextString = correctTextString;
		this.inhibitorString = inhibitorString;
		this.expensionString = expensionString;
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

		ruleIntances.add(new RuleInstance(attributeToWord, wrongTextString, correctTextString));
		
		// stage 2: get expansions
		
		List<String[]> headAndReplacersList = breakExpansionToHeadAndREplacers();
		
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
				ruleIntances.add(ruleInstance);
			}
			
		}
		
	
	}

	
	public List<Range> getMatches(List<WordPotentialMeanings> inputTextInFullWordForm) {
		
		List<Range> matches = new ArrayList<>();
		
		// go over the instances
		for (RuleInstance ruleInstance : ruleIntances) {
			matches.addAll(ruleInstance.getMatches(inputTextInFullWordForm));
		}
		
		
		return matches;
		
	}
	


	private List<String[]> breakExpansionToHeadAndREplacers() {

		if (expensionString == null || expensionString.isEmpty())
			return null;

		List<String[]> headAndReplacers = new ArrayList<String[]>();

		// a comma separates expansion rules
		String[] expantionRules = expensionString.split(",");

		for (String expantionRule : expantionRules) {

			expantionRule = expantionRule.trim();
			int miunsSign = expantionRule.indexOf('-');
			if (miunsSign == -1)
			{
				System.out.println("ERROR: minus sign was not found");
				System.exit(0);
			}

			String[] replacers = expantionRule.substring(miunsSign + 1).split("[|]");

			String[] headAndReplacer = new String[replacers.length + 1];

			headAndReplacer[0] = expantionRule.substring(0, miunsSign).trim();

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
				+ ((expensionString == null) ? 0 : expensionString.hashCode());
		result = prime * result
				+ ((inhibitorString == null) ? 0 : inhibitorString.hashCode());
		result = prime * result
				+ ((referenceString == null) ? 0 : referenceString.hashCode());
		result = prime * result
				+ ((ruleIntances == null) ? 0 : ruleIntances.hashCode());
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
		if (expensionString == null) {
			if (other.expensionString != null)
				return false;
		} else if (!expensionString.equals(other.expensionString))
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
		if (ruleIntances == null) {
			if (other.ruleIntances != null)
				return false;
		} else if (!ruleIntances.equals(other.ruleIntances))
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


package common;


/**
 * Created by Horesh on 2/8/15.
 */
public class Utilities {
	
	public static boolean isLegalBrackets(String text) {

		int bracketsGap = 0;
		for (char ch : text.toCharArray()) {
			if (ch == '[')
				bracketsGap++;
			else if (ch == ']')
				bracketsGap--;

			if (bracketsGap < 0 || bracketsGap > 1)
				return false;
		}

		return true;
	}
	
	
	public static int startWithGetIndex(String lengthyString, String shorterString) {
		
		int i = 0;
		while (i < lengthyString.length() && i < shorterString.length() && lengthyString.charAt(i) == shorterString.charAt(i))
			i++;
		
		return i - 1;
		
	}

	
	public static int endWithGetIndex(String lengthyString, String shorterString) {
		
		int i = 0;
		while (i < lengthyString.length() && i < shorterString.length() && lengthyString.charAt(lengthyString.length() - 1 - i) == shorterString.charAt(shorterString.length() - 1 - i))
			i++;
		
		return i;
		
	}

	
	
}
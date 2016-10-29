package similarWords;


import com.google.gson.Gson;
import common.GoogleResults;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//

/**
 * @author YHoresh
 *
 */
public class DistinguishWords {

	static final int MAX_NUMBER_OF_RESULTS = 64;
	static final int MINIMAL_COUNT = 2;
	static final int LONG_TIME_TO_SLEEP = 65000;
	static final int SHORT_TIME_TO_SLEEP = 3000;
	static  final int RANDOM_RANGE = 15000;

	/**
	 * @param address - web address
	 * @param word - the word we are searching for
	 * @throws IOException - IO Exception
	 * @return - BeforeAndAfter object
	 */
	public BeforeAndAfter visitSuggestedSite(String address, String charset, String word) throws IOException {

		BeforeAndAfter beforeAndAfter = new BeforeAndAfter();
		BufferedReader input;
		// URL url = new URL(URLEncoder.encode(address, charset));

		try {

			URL url = new URL(address);
			input = new BufferedReader(new InputStreamReader(url.openStream(), charset));

		} catch(Exception e) {
			e.getMessage();
			e.printStackTrace();
			System.out.println("continuing...");
			return beforeAndAfter;
		}

		String line;

		while ((line = input.readLine()) != null) {

			//System.out.println(line);        	

			if (line.indexOf(word) == -1) {
				continue;
			}

			String[] words = line.split("[<>,.() ]");

			for (int i = 0; i < words.length; i++) {

				// skip if not contained
				if (!words[i].endsWith(word)) {
					continue;
				}

				if (i > 0 && words[i - 1].length() > 1 && containsOnlyLetters(words[i - 1], common.Constants.HEBREW_LETTERS)) {
					beforeAndAfter.addBefores(words[i - 1]);
				}
				if (i < words.length - 1 && words[i + 1].length() > 1 && containsOnlyLetters(words[i + 1], common.Constants.HEBREW_LETTERS)) {
					beforeAndAfter.addAfters(words[i + 1]);
				}
			}
		}

		input.close();
		return beforeAndAfter;

	}


	private static boolean containsOnlyLetters(String  word, String letters) {
		for (char chr : word.toCharArray()) {

			if (letters.indexOf(chr) == -1) {
				return false;
			}
		}

		return true;
	}


	public Set<String> askGoogleAPI(String siteRestriction, String word) {

		try {
			Set<String> setOfUrls = new HashSet<String>();
			GoogleResults results;

			for (int i = 0; i < MAX_NUMBER_OF_RESULTS; i = i + 4) {
				String address = "http://ajax.googleapis.com/ajax/services/search/web?v=3.0&start=" + i + "&q=";
				String query = siteRestriction + " " + word;
				System.out.println("address: " + address + query);
				//String charset = siteRestrictionAndCharset[1];

				try {
					URL url = new URL(address + URLEncoder.encode(query, "UTF-8"));
					Reader reader = new InputStreamReader(url.openStream(), "UTF-8");
					results = new Gson().fromJson(reader, GoogleResults.class);
				}catch(Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					System.out.println("Sleeping and Continuing");
					Thread.sleep(SHORT_TIME_TO_SLEEP);
					continue;
				}
				// Show title and URL of each results
				
				
				int numberOfResults;
				if (results == null || results.getResponseData() == null || results.getResponseData().getResults() == null) {
					numberOfResults = 0;
				} else {
					numberOfResults = results.getResponseData().getResults().size();
				}

				int numberOfTimesURLAlreadyExisted = 0;
				int numberOfTimesBadSuffix = 0;
				for (int j = 0; j < numberOfResults; j++) {

					String webAddress = results.getResponseData().getResults().get(j).getUrl();
					webAddress = webAddress.replace("%25", "%");
					webAddress = webAddress.replace("%3F", "?");
					webAddress = webAddress.replace("%3D", "=");

					if (setOfUrls.contains(webAddress)) {
						// google api is starting to repeat itself.
						numberOfTimesURLAlreadyExisted++;
						continue;
					}

					if (webAddress.endsWith(".pdf") || webAddress.endsWith(".doc") || webAddress.endsWith(".rtf") || webAddress.endsWith(".docx") || webAddress.endsWith(".xls") || webAddress.endsWith(".xlsx")) {
						numberOfTimesBadSuffix++;
						continue;
					}
					if (webAddress.endsWith(".PDF") || webAddress.endsWith(".DOC") || webAddress.endsWith(".RTF") || webAddress.endsWith(".DOCX") || webAddress.endsWith(".XLS") || webAddress.endsWith(".XLSX")) {
						numberOfTimesBadSuffix++;
						continue;
					}


					String title = results.getResponseData().getResults().get(j).getTitle();
					System.out.println("Title: " + title);

					System.out.println("URL: " + webAddress + "\n");
					setOfUrls.add(webAddress);
				}

				Thread.sleep(LONG_TIME_TO_SLEEP + Math.round(Math.random() * RANDOM_RANGE));
				
				if (numberOfResults == numberOfTimesURLAlreadyExisted + numberOfTimesBadSuffix) {
					break;
				}

				if (numberOfResults < 4) {
					break;
				}
			

			}

			return setOfUrls;

		}catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(0);
			return null; // dummy
		}


	}


	public BeforeAndAfter exploreSingleWord(List<String[]> siteRestrictionsAndCharsets, String word) {
		try {
			BeforeAndAfter beforeAndAfter = new BeforeAndAfter();
			for (String[] siteRestrictionAndCharset : siteRestrictionsAndCharsets) {

				System.out.println("checking site: " + siteRestrictionAndCharset[0] + ' ' + siteRestrictionAndCharset[1]);
				Set<String> results = askGoogleAPI(siteRestrictionAndCharset[0], word);

				for (String result : results) {

					BeforeAndAfter newBeforeAndAfter = visitSuggestedSite(result, siteRestrictionAndCharset[1], word);
					beforeAndAfter.concatenateWithAnother(newBeforeAndAfter);

					Thread.sleep(SHORT_TIME_TO_SLEEP + Math.round(Math.random() * RANDOM_RANGE));
				}
			}
			return beforeAndAfter;

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}

		return null;    // dummy
	}


	public List<BeforeAndAfter> distinguishTwoWords(List<String[]> siteRestrictionsAndCharset, String firstWord, String secondWord) {

		BeforeAndAfter firstSet = exploreSingleWord(siteRestrictionsAndCharset, firstWord);

		BeforeAndAfter secondSet = exploreSingleWord(siteRestrictionsAndCharset, secondWord);

		List<BeforeAndAfter> ret = new ArrayList<BeforeAndAfter>();
		ret.add(firstSet);
		ret.add(secondSet);

		return ret;
	}


	public void runner(List<String[]> siteRestrictionsAndCharset, int firstRowToStartWith, int lastRowToEndWith) {

		try {
			
			
			final String sqlQuery = "select firstWord, secondWord from similar_words";
			Class.forName(common.Constants.myDriver);   
			Connection conn = DriverManager.getConnection(common.Constants.connectionString, common.Constants.user, common.Constants.password);
	        Statement stmt = conn.createStatement();
	        stmt.setFetchSize(1000);
	        ResultSet rs = stmt.executeQuery(sqlQuery);
        
	        while(rs.next()) {
	        	 String firstWord = rs.getString("firstWord");
	             String secondWord = rs.getString("secondWord");
	        			
				System.out.println("### " + firstWord + ' ' + secondWord);
				List<BeforeAndAfter> beforeAndAfter = distinguishTwoWords(siteRestrictionsAndCharset, firstWord, secondWord);
				BeforeAndAfter firstSet = beforeAndAfter.get(0);
				BeforeAndAfter secondSet = beforeAndAfter.get(1);
				write(firstWord, secondWord, firstSet, secondSet);

				// sleep
                Thread.sleep(LONG_TIME_TO_SLEEP + Math.round(Math.random() * RANDOM_RANGE));
			}

		}catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}

	}


	private static void write(String firstWord, String secondWord, BeforeAndAfter firstSet, BeforeAndAfter secondSet) {

		BeforeAndAfter firstSetClone = firstSet.getClone();
		firstSet.RemoveNotUniques(secondSet);
		secondSet.RemoveNotUniques(firstSetClone);

		String words;

		// TODO: need to 1. write to table, 2. preserve prev. words found
		/*
		
		words = firstSet.getBeforesAndAfters(MINIMAL_COUNT, 'b');
		System.out.println(words);
        HSSFCell cell = row.getCell(5);   // "×§×•×“×ž×™×�×¨×§×œ×¨×�×©×•×Ÿ"
        cell.setCellValue(words);

		words = secondSet.getBeforesAndAfters(MINIMAL_COUNT, 'b');
		System.out.println(words);
        cell = row.getCell(7);     // "×§×•×“×ž×™×�×¨×§×œ×©× ×™"
        cell.setCellValue(words);

		words = firstSet.getBeforesAndAfters(MINIMAL_COUNT, 'a');
		System.out.println(words);
        cell = row.getCell(6);      // "×¢×•×§×‘×™×�×¨×§×œ×¨×�×©×•×Ÿ"
        cell.setCellValue(words);

		words = secondSet.getBeforesAndAfters(MINIMAL_COUNT, 'a');
		System.out.println(words);
        cell = row.getCell(8);    // "×¢×•×§×‘×™×�×¨×§×œ×©× ×™"
        cell.setCellValue(words);
*/
	}

	/**
	 * @param args - args
	 * @throws Exception - exception
	 */
	public static void main(String[] args) throws Exception  {

		List<String[]> siteRestrictionsAndCharset = new ArrayList<String[]>();
		String[] siteRestrictionAndCharset;

		siteRestrictionAndCharset = new String[2];
		siteRestrictionAndCharset[0] = "site:court.gov.il";
		siteRestrictionAndCharset[1] = "UTF-16";
		siteRestrictionsAndCharset.add(siteRestrictionAndCharset);

		siteRestrictionAndCharset = new String[2];
		siteRestrictionAndCharset[0] = "site:text.org.il";
		siteRestrictionAndCharset[1] = "Windows-1255";
		siteRestrictionsAndCharset.add(siteRestrictionAndCharset);

		siteRestrictionAndCharset = new String[2];
		siteRestrictionAndCharset[0] = "site:he.wikipedia.org";
		siteRestrictionAndCharset[1] = "UTF-8";
		siteRestrictionsAndCharset.add(siteRestrictionAndCharset);


		DistinguishWords distinguishWords = new DistinguishWords();

		//distinguishWords.visitSuggestedSite("http://www.text.org.il/index.php?book=1301121", "Windows-1255", "Ã—Â Ã—â€�");
		//System.exit(0);

		distinguishWords.runner(siteRestrictionsAndCharset, 998, 9999);

	}

}

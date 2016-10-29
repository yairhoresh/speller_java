package corrector;

/*
 * 		
1. fix getCorrectForm()
2. make append query in hebrew work
3. make the alignment between two forms.
 */


import common.Range;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Logger;

public class Corrector {

	private List<Rule> rules = new ArrayList<>();
	private Set<String> prefixes = new HashSet<>();
	private Map<String, List<Attribute>> wordToAttributes = new HashMap<>();
	private Map<Attribute, String> attributeToWord = new HashMap<>();
	//private Set<Character> legalHebrewLettersSet;
	private final static Logger logger = Logger.getLogger(Corrector.class.getName());

	
	public String getCorrectedForm(String inputText) throws Exception {
		
			// into FullWord format
			List<WordPotentialMeanings> inputTextInFullWordForm = new ArrayList<>();
			
			// TODO: relate to delimiters such as period, comma...
			String[] inputTextArray = inputText.split(" ");
			
			for(String word : inputTextArray) {
				inputTextInFullWordForm.add(new WordPotentialMeanings(prefixes, wordToAttributes, word));
				
			}
			
			Map<Rule, List<Range>> ruleToMatches = new HashMap<>();
			// match with rules
			for (Rule rule : rules) {
				List<Range> ranges = rule.getMatches(inputTextInFullWordForm);
				
				if (!ranges.isEmpty())
					ruleToMatches.put(rule, ranges);
			}
			
			String correctText = "Number of corrections:" + ruleToMatches.size();
			// TODO: calc corrected sentence
	
		// append query - reply to file
		/*Class.forName(common.Constants.myDriver);
		URI dbUri = new URI(System.getenv("JAWSDB_DATABASE_URL"));
	    String username = dbUri.getUserInfo().split(":")[0];
	    String password = dbUri.getUserInfo().split(":")[1];
	    String dbUrl = "jdbc:mysql://" + dbUri.getHost() + dbUri.getPath();
	    Connection conn = DriverManager.getConnection(dbUrl, username, password);
	    Statement stmt = conn.createStatement();
	    
		DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
		Date date = new Date();
		
		//stmt.execute("SET NAMES utf8");
	
		String sqlQuery = "insert into log values ('" + dateFormat.format(date)
				+ "', '" + query + "', '" + correctedText + "')";
		
		//sqlQuery = new String (sqlQuery.getBytes ("iso-8859-1"), "UTF-8");
	
		stmt.execute(sqlQuery);
		 */
		return correctText;
    
	        
	
	
	}

	
	public Corrector() throws Exception {
		loadTables();
		
		//legalHebrewLettersSet = new HashSet<Character>();
		//for (Character ch : common.Constants.HEBREW_LETTERS.toCharArray())
		//	legalHebrewLettersSet.add(ch);
	}


	private void loadTables() throws Exception {

	
			// init 
			Class.forName(common.Constants.myDriver);
			URI dbUri = new URI(System.getenv("JAWSDB_DATABASE_URL"));
		    String username = dbUri.getUserInfo().split(":")[0];
		    String password = dbUri.getUserInfo().split(":")[1];
		    String dbUrl = "jdbc:mysql://" + dbUri.getHost() + dbUri.getPath();
		    Connection conn = DriverManager.getConnection(dbUrl, username, password);
	        Statement stmt = conn.createStatement();
	        stmt.setFetchSize(1000);
	        ResultSet rs;
	        // prefix
			String sqlQuery = "select title, listOfWords from misc_word_groups -- where title = 'תחיליות";
			
	        rs = stmt.executeQuery(sqlQuery);
	        while(rs.next()) {        	       
	        	String title = rs.getString("title");
	        	if (title.equals("תחיליות") == false)
	        		continue;
	        	String prefix = rs.getString("listOfWords");
	        	//System.out.println(title);
	        	logger.info("### " + prefix);
			
	        	String[] prefixArray = prefix.split(",");
	        	for (String onePrefix : prefixArray)
	        		prefixes.add(onePrefix.trim());
	        }
	        			
	        rs.close();
	        	        
	        // verbs
			sqlQuery = "select * from pos_verbs";
	        rs = stmt.executeQuery(sqlQuery);
	        int runningNumber = 0;
	        while(rs.next()) {

	        	String shemHaPoal = rs.getString("shemHaPoal");
	        	updateWordsAndAttributes(shemHaPoal, "פעלים", "שם הפועל", runningNumber);
	        	
	        	String hoveRishonSheniShlishiZaharYahid = rs.getString("hoveRishonSheniShlishiZaharYahid");
	        	updateWordsAndAttributes(hoveRishonSheniShlishiZaharYahid, "פעלים", "הווה ראשון שני שלישי זכר יחיד", runningNumber);
	        	
	        	String hoveRishonSheniShlishiZaharRabim = rs.getString("hoveRishonSheniShlishiZaharRabim");
	        	updateWordsAndAttributes(hoveRishonSheniShlishiZaharRabim, "פעלים", "הווה ראשון שני שלישי זכר רבים", runningNumber);
	        	
	        	String hoveRishonSheniShlishiNekevaYahid = rs.getString("hoveRishonSheniShlishiNekevaYahid");
	        	updateWordsAndAttributes(hoveRishonSheniShlishiNekevaYahid, "פעלים", "ראשון שני שלישי נקבה יחיד", runningNumber);
	        	
	        	String hoveRishonSheniShlishiNekevaRabim = rs.getString("hoveRishonSheniShlishiNekevaRabim");
	        	updateWordsAndAttributes(hoveRishonSheniShlishiNekevaRabim, "פעלים", "הווה ראשון שני שלישי נקבה רבים", runningNumber);
	        	
	        	String avarRishonYahid = rs.getString("avarRishonYahid");
	        	updateWordsAndAttributes(avarRishonYahid, "פעלים", "עבר ראשון יחיד", runningNumber);
	        	
	        	String avarRishonRabim = rs.getString("avarRishonRabim");
	        	updateWordsAndAttributes(avarRishonRabim, "פעלים", "עבר ראשון רבים", runningNumber);
	        	
	        	String avarSheniZaharNekevaYahid = rs.getString("avarSheniZaharNekevaYahid");
	        	updateWordsAndAttributes(avarSheniZaharNekevaYahid, "פעלים", "עבר שני זכר נקבה יחיד", runningNumber);
	        	
	        	String avarSheniZaharRabim = rs.getString("avarSheniZaharRabim");
	        	updateWordsAndAttributes(avarSheniZaharRabim, "פעלים", "עבר שני זכר רבים", runningNumber);
	        	
	        	String avarSheniNekevaRabim = rs.getString("avarSheniNekevaRabim");
	        	updateWordsAndAttributes(avarSheniNekevaRabim, "פעלים", "עבר שני נקבה רבים", runningNumber);
	        	
	        	String avarShlishiZaharYahid = rs.getString("avarShlishiZaharYahid");
	        	updateWordsAndAttributes(avarShlishiZaharYahid, "פעלים", "עבר שלישי זכר יחיד", runningNumber);
	        	
	        	String avarShlishiNekevaYahid = rs.getString("avarShlishiNekevaYahid");
	        	updateWordsAndAttributes(avarShlishiNekevaYahid, "פעלים", "עבר שלישי נקבה יחיד", runningNumber);
	        	
	        	String avarShlishiZaharNekevaRabim = rs.getString("avarShlishiZaharNekevaRabim");
	        	updateWordsAndAttributes(avarShlishiZaharNekevaRabim, "פעלים", "עבר שלישי זכר נקבה רבים", runningNumber);
	        	
	        	String atidRishonYahid = rs.getString("atidRishonYahid");
	        	updateWordsAndAttributes(atidRishonYahid, "פעלים", "עתיד ראשון יחיד", runningNumber);
	        	
	        	String atidRishonRabim = rs.getString("atidRishonRabim");
	        	updateWordsAndAttributes(atidRishonRabim, "פעלים", "עתיד ראשון רבים", runningNumber);
	        	
	        	String atidSheniZaharYahidAtidShlishiNekevaYahid = rs.getString("atidSheniZaharYahidAtidShlishiNekevaYahid");
	        	updateWordsAndAttributes(atidSheniZaharYahidAtidShlishiNekevaYahid, "פעלים", "עתיד שני זכר יחיד עתיד שלישי נקבה יחיד", runningNumber);
	        	
	        	String atidSheniNekevaYahid = rs.getString("atidSheniNekevaYahid");
	        	updateWordsAndAttributes(atidSheniNekevaYahid, "פעלים", "עתיד שני נקבה יחיד", runningNumber);
	        	
	        	String atidSheniZaharNekevaRabim = rs.getString("atidSheniZaharNekevaRabim");
	        	updateWordsAndAttributes(atidSheniZaharNekevaRabim, "פעלים", "עתיד שני זכר נקבה רבים", runningNumber);
	        	
	        	String atidShlishiZaharYahid = rs.getString("atidShlishiZaharYahid");
	        	updateWordsAndAttributes(atidShlishiZaharYahid, "פעלים", "עתיד שלישי זכר יחיד", runningNumber);
	        	
	        	String atidShlishiZaharNekevaRabim = rs.getString("atidShlishiZaharNekevaRabim");
	        	updateWordsAndAttributes(atidShlishiZaharNekevaRabim, "פעלים", "עתיד שלישי זכר נקבה רבים", runningNumber);
			
				runningNumber++;
				

	        }
			
	        rs.close();
	        
	        
	        // יחס סדר
	        sqlQuery = "select zahar, nekeva from pos_ordinal";
	        rs = stmt.executeQuery(sqlQuery);
	        runningNumber = 0;
	        while(rs.next()) {

	        	String zahar = rs.getString("zahar");
	        	updateWordsAndAttributes(zahar, "יחס סדר", "זכר", runningNumber);
	        	String nekeva = rs.getString("nekeva");
	        	updateWordsAndAttributes(nekeva, "יחס סדר", "נקבה", runningNumber);

	        	runningNumber++;
	        }
	        			        
	        rs.close();

	        
	        
	        // מספרים
	        sqlQuery = "select zahar, nekeva from pos_numbers";
	        rs = stmt.executeQuery(sqlQuery);
	        runningNumber = 0;
	        while(rs.next()) {

	        	String zahar = rs.getString("zahar");
	        	updateWordsAndAttributes(zahar, "מספרים", "זכר", runningNumber);
	        	String nekeva = rs.getString("nekeva");
	        	updateWordsAndAttributes(nekeva, "מספרים", "נקבה", runningNumber);

	        	runningNumber++;
	        }
	        			        
	        rs.close();
	        
	        
	        
	        // שמות עצם
	        sqlQuery = "select * from pos_nouns";
	        rs = stmt.executeQuery(sqlQuery);
	        runningNumber = 0;
	        while(rs.next()) {
      	
	        	String pashotZaharYahid = rs.getString("pashotZaharYahid");
	        	updateWordsAndAttributes(pashotZaharYahid, "שמות עצם", "פשוט זכר יחיד", runningNumber);
	        	
	        	String pashotZaharRabim = rs.getString("pashotZaharRabim");
	        	updateWordsAndAttributes(pashotZaharRabim, "שמות עצם", "פשוט זכר רבים", runningNumber);
	        	
	        	String pashotNekevaYahid = rs.getString("pashotNekevaYahid");
	        	updateWordsAndAttributes(pashotNekevaYahid, "שמות עצם", "פשוט נקבה יחיד", runningNumber);
	        	
	        	String pashotNekevaRabim = rs.getString("pashotNekevaRabim");
	        	updateWordsAndAttributes(pashotNekevaRabim, "שמות עצם", "פשוט נקבה רבים", runningNumber);
	        	
	        	String shayahotRishonZaharYahidToYahid = rs.getString("shayahotRishonZaharYahidToYahid");
	        	updateWordsAndAttributes(shayahotRishonZaharYahidToYahid, "שמות עצם", "שיכות ראשון זכר יחיד ליחיד", runningNumber);
	        	
	        	String shayahotRishonZaharRabimToYahid = rs.getString("shayahotRishonZaharRabimToYahid");
	        	updateWordsAndAttributes(shayahotRishonZaharRabimToYahid, "שמות עצם", "שיכות ראשון זכר רבים ליחיד", runningNumber);
	        	
	        	String shayahotRishonNekevaYahidToYahid = rs.getString("shayahotRishonNekevaYahidToYahid");
	        	updateWordsAndAttributes(shayahotRishonNekevaYahidToYahid, "שמות עצם", "שיכות ראשון נקבה יחיד ליחיד", runningNumber);
	        	
	        	String shayahotRishonNekevaRabimToYahid = rs.getString("shayahotRishonNekevaRabimToYahid");
	        	updateWordsAndAttributes(shayahotRishonNekevaRabimToYahid, "שמות עצם", "שיכות ראשון נקבה רבים ליחיד", runningNumber);
	        	
	        	String shayahotRishonZaharYahidToRabim = rs.getString("shayahotRishonZaharYahidToRabim");
	        	updateWordsAndAttributes(shayahotRishonZaharYahidToRabim, "שמות עצם", "שיכות ראשון זכר יחיד לרבים", runningNumber);
	        	
	        	String shayahotRishonZaharRabimToRabim = rs.getString("shayahotRishonZaharRabimToRabim");
	        	updateWordsAndAttributes(shayahotRishonZaharRabimToRabim, "שמות עצם", "שיכות ראשון זכר רבים לרבים", runningNumber);
	        	
	        	String shayahotRishonNekevaYahidToRabim = rs.getString("shayahotRishonNekevaYahidToRabim");
	        	updateWordsAndAttributes(shayahotRishonNekevaYahidToRabim, "שמות עצם", "שיכות ראשון נקבה יחיד לרבים", runningNumber);
	        	
	        	String shayahotRishonNekevaRabimToRabim = rs.getString("shayahotRishonNekevaRabimToRabim");
	        	updateWordsAndAttributes(shayahotRishonNekevaRabimToRabim, "שמות עצם", "שיכות ראשון נקבה רבים לרבים", runningNumber);
	        	
	        	String shayahotSheniZaharYahidToZaharYahid = rs.getString("shayahotSheniZaharYahidToZaharYahid");
	        	updateWordsAndAttributes(shayahotSheniZaharYahidToZaharYahid, "שמות עצם", "שיכות שני זכר יחיד לזכר יחיד", runningNumber);
	        	
	        	String shayahotSheniZaharRabimToZaharYahid = rs.getString("shayahotSheniZaharRabimToZaharYahid");
	        	updateWordsAndAttributes(shayahotSheniZaharRabimToZaharYahid, "שמות עצם", "שיכות שני זכר רבים לזכר יחיד", runningNumber);
	        	
	        	String shayahotSheniNekevaYahidToZaharYahid = rs.getString("shayahotSheniNekevaYahidToZaharYahid");
	        	updateWordsAndAttributes(shayahotSheniNekevaYahidToZaharYahid, "שמות עצם", "שיכות שני נקבה יחיד לזכר יחיד", runningNumber);
	        	
	        	String shayahotSheniNekevaRabimToZaharYahid = rs.getString("shayahotSheniNekevaRabimToZaharYahid");
	        	updateWordsAndAttributes(shayahotSheniNekevaRabimToZaharYahid, "שמות עצם", "שיכות שני נקבה רבים לזכר יחיד", runningNumber);
	        	
	        	String shayahotSheniZaharYahidToZaharRabim = rs.getString("shayahotSheniZaharYahidToZaharRabim");
	        	updateWordsAndAttributes(shayahotSheniZaharYahidToZaharRabim, "שמות עצם", "שיכות שני זכר יחיד לזכר רבים", runningNumber);
	        	
	        	String shayahotSheniZaharRabimToZaharRabim = rs.getString("shayahotSheniZaharRabimToZaharRabim");
	        	updateWordsAndAttributes(shayahotSheniZaharRabimToZaharRabim, "שמות עצם", "שייכות שני זכר רבים לזכר רבים", runningNumber);
	        	
	        	String shayahotSheniNekevaYahidToZaharRabim = rs.getString("shayahotSheniNekevaYahidToZaharRabim");
	        	updateWordsAndAttributes(shayahotSheniNekevaYahidToZaharRabim, "שמות עצם", "שיכות שני נקבה יחיד לזכר רבים", runningNumber);
	        	
	        	String shayahotSheniNekevaRabimToZaharRabim = rs.getString("shayahotSheniNekevaRabimToZaharRabim");
	        	updateWordsAndAttributes(shayahotSheniNekevaRabimToZaharRabim, "שמות עצם", "שיכות שני נקבה רבים לזכר רבים", runningNumber);
	        	
	        	String shayahotSheniZaharYahidToNekavaYahid = rs.getString("shayahotSheniZaharYahidToNekavaYahid");
	        	updateWordsAndAttributes(shayahotSheniZaharYahidToNekavaYahid, "שמות עצם", "שיכות שני זכר יחיד לנקבה רבים", runningNumber);
	        	
	        	String shayahotSheniZaharRabimToNekavaYahid = rs.getString("shayahotSheniZaharRabimToNekavaYahid");
	        	updateWordsAndAttributes(shayahotSheniZaharRabimToNekavaYahid, "שמות עצם", "שיכות שני זכר רבים לנקבה יחיד", runningNumber);
	        	
	        	String shayahotSheniNekevaYahidToNekavaYahid = rs.getString("shayahotSheniNekevaYahidToNekavaYahid");
	        	updateWordsAndAttributes(shayahotSheniNekevaYahidToNekavaYahid, "שמות עצם", "שיכות שני נקבה יחיד לנקבה יחיד", runningNumber);
	        	
	        	String shayahotSheniNekevaRabimToNekavaYahid = rs.getString("shayahotSheniNekevaRabimToNekavaYahid");
	        	updateWordsAndAttributes(shayahotSheniNekevaRabimToNekavaYahid, "שמות עצם", "שיכות שני נקבה רבים לנקבה יחיד", runningNumber);
	        	
	        	String shayahotSheniZaharYahidToNekavaRabim = rs.getString("shayahotSheniZaharYahidToNekavaRabim");
	        	updateWordsAndAttributes(shayahotSheniZaharYahidToNekavaRabim, "שמות עצם", "שיכות שני זכר יחיד לנקבה רבים", runningNumber);
	        	
	        	String shayahotSheniZaharRabimToNekavaRabim = rs.getString("shayahotSheniZaharRabimToNekavaRabim");
	        	updateWordsAndAttributes(shayahotSheniZaharRabimToNekavaRabim, "שמות עצם", "שיכות שני זכר רבים לנקבה רבים", runningNumber);
	        	
	        	String shayahotSheniNekevaYahidToNekavaRabim = rs.getString("shayahotSheniNekevaYahidToNekavaRabim");
	        	updateWordsAndAttributes(shayahotSheniNekevaYahidToNekavaRabim, "שמות עצם", "שיכות שני נקבה יחיד לנקבה רבים", runningNumber);
	        	
	        	String shayahotSheniNekevaRabimToNekavaRabim = rs.getString("shayahotSheniNekevaRabimToNekavaRabim");
	        	updateWordsAndAttributes(shayahotSheniNekevaRabimToNekavaRabim, "שמות עצם", "שיכות שני נקבה רבים לנקבה רבים", runningNumber);
	        	
	        	String shayahotShlishiZaharYahidToZaharYahid = rs.getString("shayahotShlishiZaharYahidToZaharYahid");
	        	updateWordsAndAttributes(shayahotShlishiZaharYahidToZaharYahid, "שמות עצם", "שלישי זכר יחיד לזכר יחיד", runningNumber);
	        	
	        	String shayahotShlishiZaharRabimToZaharYahid = rs.getString("shayahotShlishiZaharRabimToZaharYahid");
	        	updateWordsAndAttributes(shayahotShlishiZaharRabimToZaharYahid, "שמות עצם", "שיכות שלישי זכר רבים לזכר יחיד", runningNumber);
	        	
	        	String shayahotShlishiNekevaYahidToZaharYahid = rs.getString("shayahotShlishiNekevaYahidToZaharYahid");
	        	updateWordsAndAttributes(shayahotShlishiNekevaYahidToZaharYahid, "שמות עצם", "שיכות שלישי נקבה יחיד לזכר יחיד", runningNumber);
	        	
	        	String shayahotShlishiNekevaRabimToZaharYahid = rs.getString("shayahotShlishiNekevaRabimToZaharYahid");
	        	updateWordsAndAttributes(shayahotShlishiNekevaRabimToZaharYahid, "שמות עצם", "שיכות שלישי נקבה רבים לזכר יחיד", runningNumber);
	        	
	        	String shayahotShlishiZaharYahidToZaharRabim = rs.getString("shayahotShlishiZaharYahidToZaharRabim");
	        	updateWordsAndAttributes(shayahotShlishiZaharYahidToZaharRabim, "שמות עצם", "שיכות שלישי זכר יחיד לזכר רבים", runningNumber);
	        	
	        	String shayahotShlishiZaharRabimToZaharRabim = rs.getString("shayahotShlishiZaharRabimToZaharRabim");
	        	updateWordsAndAttributes(shayahotShlishiZaharRabimToZaharRabim, "שמות עצם", "שיכות שלישי זכר רבים לזכר רבים", runningNumber);
	        	
		        String shayahotShlishiNekevaYahidToZaharRabim = rs.getString("shayahotShlishiZaharRabimToZaharRabim");
		        updateWordsAndAttributes(shayahotShlishiNekevaYahidToZaharRabim, "שמות עצם", "שיכות שלישי זכר רבים לזכר רבים", runningNumber);
		        
		        String shayahotShlishiNekevaRabimToZaharRabim = rs.getString("shayahotShlishiNekevaRabimToZaharRabim");
		        updateWordsAndAttributes(shayahotShlishiNekevaRabimToZaharRabim, "שמות עצם", "שיכות שלישי נקבה רבים לזכר רבים", runningNumber);
		        
		        String shayahotShlishiZaharYahidToNekavaYahid = rs.getString("shayahotShlishiZaharYahidToNekavaYahid");
		        updateWordsAndAttributes(shayahotShlishiZaharYahidToNekavaYahid, "שמות עצם", "שיכות שלישי זכר יחיד לנקבה יחיד", runningNumber);
		        
		        String shayahotShlishiZaharRabimToNekavaYahid = rs.getString("shayahotShlishiZaharRabimToNekavaYahid");
		        updateWordsAndAttributes(shayahotShlishiZaharRabimToNekavaYahid, "שמות עצם", "שיכות שלישי זכר רבים לנקבה יחיד", runningNumber);
		        
		        String shayahotShlishiNekevaYahidToNekavaYahid = rs.getString("shayahotShlishiNekevaYahidToNekavaYahid");
		        updateWordsAndAttributes(shayahotShlishiNekevaYahidToNekavaYahid, "שמות עצם", "שיכות שלישי נקבה יחיד לנקבה יחיד", runningNumber);
		        
		        String shayahotShlishiNekevaRabimToNekavaYahid = rs.getString("shayahotShlishiNekevaRabimToNekavaYahid");
		        updateWordsAndAttributes(shayahotShlishiNekevaRabimToNekavaYahid, "שמות עצם", "שיכות שלישי נקבה רבים לנקבה יחיד", runningNumber);
		        
		        String shayahotShlishiZaharYahidToNekavaRabim = rs.getString("shayahotShlishiZaharYahidToNekavaRabim");
		        updateWordsAndAttributes(shayahotShlishiZaharYahidToNekavaRabim, "שמות עצם", "שיכות שלישי זכר יחיד לנקבה רבים", runningNumber);
		        
		        String shayahotShlishiZaharRabimToNekavaRabim = rs.getString("shayahotShlishiZaharRabimToNekavaRabim");
		        updateWordsAndAttributes(shayahotShlishiZaharRabimToNekavaRabim, "שמות עצם", "שיכות שלישי זכר רבים לנקבה רבים", runningNumber);
		        
		        String shayahotShlishiNekevaYahidToNekavaRabim = rs.getString("shayahotShlishiNekevaYahidToNekavaRabim");
		        updateWordsAndAttributes(shayahotShlishiNekevaYahidToNekavaRabim, "שמות עצם", "שיכות שלישי נקבה יחיד לנקבה רבים", runningNumber);
		        
		        String shayahotShlishiNekevaRabimToNekavaRabim = rs.getString("shayahotShlishiNekevaRabimToNekavaRabim");
		        updateWordsAndAttributes(shayahotShlishiNekevaRabimToNekavaRabim, "שמות עצם", "שיכות שלישי נקבה רבים לנקבה רבים", runningNumber);


	        	runningNumber++;
	        }
	        			        
	        rs.close();
	        
		        
	        
	        // קבוצות מילים
	        sqlQuery = "select title, listOfWords from misc_word_groups";
	        rs = stmt.executeQuery(sqlQuery);

	        while(rs.next()) {

	        	String title = rs.getString("title");
	        	String listOfWords = rs.getString("listOfWords");
	        	
	        	String[] listOfWordsArray = listOfWords.split(",");
	        	
	        	for (String singleWord : listOfWordsArray) {
	        		
	        		updateWordsAndAttributes(singleWord.trim(), "קבוצת מילים", title, -1);
	        		
	        	}

	        }
	        			        
	        rs.close();


	        
	        // שמות נפוצים
	        sqlQuery = "select name from misc_common_names";
	        rs = stmt.executeQuery(sqlQuery);
	        while(rs.next()) {

	        	String name = rs.getString("name");
	        	updateWordsAndAttributes(name, "קבוצת מילים", "שמות נפוצים", -1);

	        }
	        			        
	        rs.close();
	        
	        
	        
	        
	        // זוגות מילים
	        sqlQuery = "select pair, thePair from misc_two_word_nouns";
	        rs = stmt.executeQuery(sqlQuery);
	        runningNumber = 0;
	        while(rs.next()) {

	        	String pair = rs.getString("pair");
	        	updateWordsAndAttributes(pair, "זוג מילים", "ללא ה הידיעה", runningNumber);
	        	
	        	String thePair = rs.getString("thePair");
	        	updateWordsAndAttributes(thePair, "זוג מילים", "עם ה הידיעה", runningNumber);
	        	
	        	
	        	runningNumber++;

	        }
	        			        
	        rs.close();
	        
	        // שם תואר
	        sqlQuery = "select zaharYahid, zaharRabim, nekevaYahid, nekevaRabim from pos_adjectives";
	        rs = stmt.executeQuery(sqlQuery);
	        runningNumber = 0;
	        while(rs.next()) {

	        	String zaharYahid = rs.getString("zaharYahid");
	        	updateWordsAndAttributes(zaharYahid, "שם תואר", "זכר יחיד", runningNumber);
	        	String zaharRabim = rs.getString("zaharRabim");
	        	updateWordsAndAttributes(zaharRabim, "שם תואר", "זכר רבים", runningNumber);
	        	String nekavaYahid = rs.getString("nekevaYahid");
	        	updateWordsAndAttributes(nekavaYahid, "שם תואר", "נקבה יחיד", runningNumber);
	        	String nekevaRabim = rs.getString("nekevaRabim");
	        	updateWordsAndAttributes(nekevaRabim, "שם תואר", "נקבה רבים", runningNumber);
	        	
	        	runningNumber++;

	        }
	        			        
	        rs.close();
	        
	        
	        // rules word pairs 
			sqlQuery = "select firstWord, secondWord, beforeOnlyFirst, afterOnlyFirst, beforeOnlySecond, afterOnlySecond from rules_word_pairs";
	        rs = stmt.executeQuery(sqlQuery);
	        
	        while(rs.next()) {

                String firstWord = rs.getString("firstWord");
				String secondWord = rs.getString("secondWord");
				String uniqueBeforeFirst = rs.getString("beforeOnlyFirst");
				String uniqueAfterFirst = rs.getString("afterOnlyFirst");
				String uniqueBeforeSecond = rs.getString("beforeOnlySecond"); 
				String uniqueAfterSecond = rs.getString("afterOnlySecond"); 
				
				
				if (firstWord.isEmpty() || secondWord.isEmpty())
					continue;

				logger.info("### " + firstWord + ' ' + secondWord);
				
				// go over each unique adjacent word and set a rule
				
				breakAdjacentWordsListAndMakeRules(uniqueBeforeFirst, true, true, firstWord, secondWord);
				breakAdjacentWordsListAndMakeRules(uniqueAfterFirst, false, true, firstWord, secondWord);
				breakAdjacentWordsListAndMakeRules(uniqueBeforeSecond, true, false, firstWord, secondWord);
				breakAdjacentWordsListAndMakeRules(uniqueAfterSecond, false, false, firstWord, secondWord);
            }
	        
	        rs.close();
	        
	        
	        // simple rules and complex rules 
			sqlQuery = "select * from rules_specific union SELECT * FROM rules_general";
			
			
			rs = stmt.executeQuery(sqlQuery);
			
	        while(rs.next()) {
				
	        	String wrongForm = rs.getString("wrong");
				String correctForm = rs.getString("correct");
				String inhibitor = rs.getString("inhibitor");
				String expension = rs.getString("expand");
				String severity = rs.getString("sevirity");
							
				if (wrongForm.isEmpty() || correctForm.isEmpty())
					continue;

				logger.info("### " + wrongForm + ' ' + correctForm);
				
				rules.add(new Rule(attributeToWord, wrongForm, correctForm, inhibitor, expension, severity, null));
				
			}

	        rs.close();
        
		
	}
	
	
	private void breakAdjacentWordsListAndMakeRules(String uniqueWordsList, boolean isBefore, boolean isFirst, String firstWord, String secondWord) {
		
		if (uniqueWordsList == null || uniqueWordsList.isEmpty())
			return;
		
		String[] uniqueWordsArray = uniqueWordsList.split(" ");
		String wrongRuleString = null;
		String correctRuleString = null;
		
		for(String adjacentWord : uniqueWordsArray) {
			
						
			if (isBefore && isFirst) {
				
				correctRuleString = adjacentWord + " " + firstWord;
				wrongRuleString = adjacentWord + " " + secondWord;
			}
			else if (!isBefore && isFirst) {
				
				correctRuleString = firstWord + " " + adjacentWord;
				wrongRuleString = secondWord + " " + adjacentWord;
			} 
			else if (isBefore && !isFirst) {
				
				correctRuleString = adjacentWord + " " + secondWord;
				wrongRuleString = adjacentWord + " " + firstWord;
				
			}
			else if (!isBefore && !isFirst) {
				
				correctRuleString = secondWord + " " + adjacentWord;
				wrongRuleString = firstWord + " " + adjacentWord;
			}
							
			rules.add(new Rule(attributeToWord, wrongRuleString, correctRuleString, null, null, null, null));
		}
				
	}

	
	private void updateWordsAndAttributes(String word, String tableName, String columnName, int id) {
		
		if (word == null) {
			System.out.println("Error: updateWordsAndAttributes, nulls... " + tableName + ", " + columnName);
			//System.exit(0);
			return;
		}
		
		// TODO: need to insert also before the comma (higher level)
		List<Attribute> attributes = wordToAttributes.get(word);
		if (attributes == null)
			attributes = new ArrayList<>();
		Attribute wordAttribute = new Attribute(id, tableName.trim(), columnName.trim());   
		attributes.add(wordAttribute);
		wordToAttributes.put(word, attributes);
		attributeToWord.put(wordAttribute, word);
	}
	

	

	public static void main(String[] args) throws Exception {

		
		/*String x = "aaa_ffff+fff fff_";
		x=x.replaceAll("\\+", " \\+");
		x=x.replaceAll("_", "_ ");
		String[] tokens = x.split(" ");
		for (String t : tokens)
			System.out.println(t);
		
		System.exit(0);
		*/
		
		Corrector rulesEvaluator = new Corrector();
		// rulesEvaluator.init();
		// rulesEvaluator.test();

		String reply = rulesEvaluator.getCorrectedForm("ארבע באוגוסט");

		System.out.println(reply);
	}
}

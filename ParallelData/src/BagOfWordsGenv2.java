/**
 * 
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

/**
 * @author Rakesh_BCKUP
 * 
 */
public class BagOfWordsGenv2 {

	private static BufferedReader idReader;
	private static BufferedReader contextReader;
	private static PrintWriter wordsWriter;
	Set<String> bagOfWords = new HashSet<String>();
	Set<Integer> citedPaperIds = new HashSet<Integer>();
	private PrintWriter titleWriter;
	private BufferedReader titleReader;
	private HashMap<String, Integer> titlesMap;
	private int ignored = 0;
	static final int MAX_FERTILITY = 100;
	static final double LOWERBOUND = 1/MAX_FERTILITY; 

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {

		BagOfWordsGenv2 parallel = new BagOfWordsGenv2();
		parallel.generate();

	}

	public void generate() throws IOException, ClassNotFoundException {
		// Buffered Readers

		
		readTitleMap();

		int numCitation = 0;
		String curPaperId, context, title;
		String previousId = "";
		
		curPaperId = idReader.readLine(); // read the column name
		context = contextReader.readLine(); // read the column name
		title = titleReader.readLine(); // read the column name
		System.out.println(curPaperId);
		long startTime = 0, endTime = 0;
		startTime = System.currentTimeMillis();
		int percentage=0;
		while ((curPaperId = idReader.readLine()) != null) {
			numCitation++;
			
			if (0 == numCitation%12000) {
				endTime = System.currentTimeMillis();
				System.out.println("Time taken for "+(++percentage)+"% = "
						+ ((endTime - startTime) / 1000));
				//break;
			}
			if (curPaperId.equals(previousId)) {

				// Read one citation context and get bag of words
				context = contextReader.readLine();
				removeStopWords(context);
				
				// Read corresponding paper title, fetch corresponding ID
				title = titleReader.readLine();
				int citedPaperId = titlesMap.get(title);
				citedPaperIds.add(citedPaperId);
				
			} else {
				if (bagOfWords.size() != 0 && citedPaperIds.size() !=0) {
					// write bagsOfWords to a file
					// write citedPaperIds to a file 
					writeToFile();
				}
				// Initialize Sets to read the new Paper citations
				bagOfWords.clear();
				citedPaperIds.clear();
				
				// Read one citation context and get bag of words
				context = contextReader.readLine();
				removeStopWords(context);
				
				// Read corresponding paper title, fetch correspoding ID
				title = titleReader.readLine();
				int citedPaperId = titlesMap.get(title);
				citedPaperIds.add(citedPaperId);
			}

			previousId = curPaperId;
		}
		
		// Write the last set of values
		if (bagOfWords != null && citedPaperIds !=null) {
			// write bagsOfWords to a file
			// write citedPaperIds to a file 
			writeToFile();
		}
		
		wordsWriter.close();
		titleWriter.close();
		System.out.println("Ignored entries = "+ignored);
	}
	
	

	public BagOfWordsGenv2() throws IOException {

		idReader = new BufferedReader(new FileReader("ID.txt"));
		titleReader = new BufferedReader(new FileReader("Title.txt"));
		contextReader = new BufferedReader(new FileReader("Context.txt"));
		
		// Buffered Writers
		wordsWriter = new PrintWriter(new FileWriter("BagOfWordsv3.txt"));
		titleWriter = new PrintWriter(new FileWriter("CitedPapersv2.txt"));
	}

	private void writeToFile() {
		double fertility = bagOfWords.size()/citedPaperIds.size();
		
		if (!((fertility>MAX_FERTILITY)||(fertility<LOWERBOUND))) {
			Iterator<String> it = bagOfWords.iterator();
			String write = "";
			while (it.hasNext()) {
				write = write + " " + it.next();
			}
			wordsWriter.println(write);
			Iterator<Integer> it2 = citedPaperIds.iterator();
			write = "";
			while (it2.hasNext()) {
				write = write + " " + it2.next();
			}
			titleWriter.println(write);
		}
		else{
			ignored++;
		}
	}

	public void removeStopWords(String input) throws IOException {
		// input string
		Version matchVersion = Version.LUCENE_35; // Substitute desired Lucene
													// version for XY
		Analyzer analyzer = new StandardAnalyzer(matchVersion); // or any other
																// analyzer
		TokenStream tokenStream = analyzer.tokenStream("test",
				new StringReader(input));
		// remove stop words
		tokenStream = new StopFilter(Version.LUCENE_35, tokenStream,
				EnglishAnalyzer.getDefaultStopSet());

		// retrieve the remaining tokens
		CharTermAttribute token = tokenStream
				.getAttribute(CharTermAttribute.class);
		if(token==null){
			@SuppressWarnings("unused")
			int i=0;
		}
		while (tokenStream.incrementToken()) {
			bagOfWords.add(token.toString());
		}
		
		tokenStream.end();
		tokenStream.close();
		analyzer.close();
	}
	
	private void readTitleMap() throws ClassNotFoundException, IOException {

		FileInputStream fin = new FileInputStream("Map.ser");
		ObjectInputStream ois = new ObjectInputStream(fin);
		titlesMap = (HashMap<String, Integer>) ois.readObject();
	}
}

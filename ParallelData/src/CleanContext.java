import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;


public class CleanContext {
	
	HashSet<String> bagOfWords=null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SnowballAnalyzer analyzer = new SnowballAnalyzer(Version.LUCENE_36, "SnowballFilter");
		TokenStream tokenStream = analyzer.tokenStream("test",
				new StringReader("I am an Idiot"));
		System.out.println(tokenStream.toString());
	}
	
	/*public void removeStopWords(String input) throws IOException {
		// input string
		Version matchVersion = Version.LUCENE_35; // Substitute desired Lucene
													// version for XY
		Analyzer analyzer = new StandardAnalyzer(matchVersion); // or any other
																// analyzer
		TokenStream tokenStream = analyzer.tokenStream("test",
				new StringReader(input));

		// tokenStream = new EnglishMinimalStemFilter(tokenStream);
		// remove stop words
		
		 * tokenStream = new StopFilter(Version.LUCENE_35, tokenStream,
		 * EnglishAnalyzer.getDefaultStopSet());
		 

		// retrieve the remaining tokens
		CharTermAttribute token = tokenStream
				.getAttribute(CharTermAttribute.class);

		while (tokenStream.incrementToken()) {
			bagOfWords.add(token.toString());
		}
		
		tokenStream.end();
		tokenStream.close();
		analyzer.close();

	}
	
	void remStopfrmFile(String fileName) throws IOException{
		BufferedReader r = new BufferedReader(new FileReader(fileName));
		PrintWriter w = new PrintWriter(new FileWriter(fileName+"Stop"));
		String s;		
		while((s=r.readLine())!=null){
			StringBuilder builder = new StringBuilder(100);
			removeStopWords(s);
			for(String str: bagOfWords){
				builder.append(str);
				builder.append(" ");
			}			
			w.println(builder.toString());
		}
		
	}*/

}

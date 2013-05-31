import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.search.QueryTermVector;
import org.apache.lucene.util.Version;

/**
 * 
 */

/**
 * @author rakesh
 *
 */

public class RefRec {
	
	static final int numContexts = 1200000;
	ContextFreq[] vcb;
	TTable table;
	double[] prob = new double[50000];
	List<Integer> queryTokens = new ArrayList<>();
	HashMap<String,Integer> wordsToID = new HashMap<>();	
	
	public RefRec(){
		try {
			table = readTTable();
			vcb = readICF();
			wordsToID = readWordsToID();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {
		RefRec ref = new RefRec();
		ref.rankPapers();
	}

	private void rankPapers() throws ClassNotFoundException, IOException {
		//storeDataStruct("", "Context.vcb");	
		String query = "myopia affects approximately 25% of adult Americans[2]. Ethnic diversity appears to distinguish different groups with regard to prevalence. Caucasians have a higher prevalence than African Americans=-=[3]-=-. Asian populations have the highest prevalence rates with reports ranging from 50-90%[1, 4-5]. Jewish Caucasians, one of the target populations of the present study, have consistently demonstrated a ";
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		TokenStream tokenStream = analyzer.tokenStream("query", new StringReader(query));
		CharTermAttribute token = tokenStream
				.getAttribute(CharTermAttribute.class);
		while(tokenStream.incrementToken()){
			queryTokens.add(wordsToID.get(token.toString()));
		}
		for(int paper : table.titles){
			for(Integer wordID : queryTokens){	
				if(wordID != null){
					prob[paper] += table.getTValue(wordID, paper)*vcb[wordID].icf;
				}
			}
		}
		
		System.out.println("done");
	}
	
	void storeDataStruct(String path, String fileName) throws IOException{
		vcb = new ContextFreq[TTable.findNumWords(fileName)];
		String[]arr;
		String temp;
		int index, freq; 
		int count = 0, percent =0;
		double icf;
		BufferedReader r = new BufferedReader(new FileReader(fileName));
		while ((temp = r.readLine())!=null) {
			arr = temp.split(" ");
			index = Integer.parseInt(arr[0]);
			String word = arr[1];
			freq =  Integer.parseInt(arr[2]);
			if(wordsToID.get(word)==null){
				wordsToID.put(word, index);
			}
			icf = Math.log(((double)numContexts)/freq);
			vcb[index] = new ContextFreq(word, freq, icf);
			if ((count%6000) == 0) {
				percent=count/6000;
				System.out.println(percent+"%");
			}
			if(percent > 96)
				System.out.println(count);
			count++;

		}
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("ICFArray.ser"));
		out.writeObject(vcb);
		out.close();
		out = new ObjectOutputStream(new FileOutputStream("WordsToID.ser"));
		out.writeObject(wordsToID);
		out.close();
		r.close();
	}
	
	ContextFreq[] readICF() throws ClassNotFoundException, IOException{
		ObjectInputStream in = new ObjectInputStream(new FileInputStream("ICFArray.ser"));
		return (ContextFreq[])in.readObject();
	}
	
	HashMap<String, Integer> readWordsToID() throws ClassNotFoundException, IOException{
		ObjectInputStream in = new ObjectInputStream(new FileInputStream("WordsToID.ser"));
		return (HashMap<String, Integer>)in.readObject();
	}
	
	TTable readTTable() throws ClassNotFoundException, IOException{
		ObjectInputStream in = new ObjectInputStream(new FileInputStream("TTable.ser"));
		return (TTable)in.readObject();
	}
}

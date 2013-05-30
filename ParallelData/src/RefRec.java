import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Ref;
import java.util.ArrayList;

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
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {
		
		RefRec ref = new RefRec();
		ref.vcb = ref.readICF();
		//.storeDataStruct("", "train.BagOfWords.vcb");
	}
	
	void storeDataStruct(String path, String fileName) throws IOException{
		vcb = new ContextFreq[600000];
		String[]arr;
		String temp;
		int index, freq; 
		int count = 0, percent =0;
		double icf;
		BufferedReader r = new BufferedReader(new FileReader(fileName));
		while ((temp = r.readLine())!=null) {
			arr = temp.split(" ");
			index = Integer.parseInt(arr[0]);
			freq =  Integer.parseInt(arr[2]);
			icf = Math.log(((double)numContexts)/freq);
			vcb[index] = new ContextFreq(arr[1], freq, icf);
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
		r.close();
	}
	
	ContextFreq[] readICF() throws ClassNotFoundException, IOException{
		ObjectInputStream in = new ObjectInputStream(new FileInputStream("ICFArray.ser"));
		return (ContextFreq[])in.readObject();
	}
}

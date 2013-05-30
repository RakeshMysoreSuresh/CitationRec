import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;


public class TTable {

	ArrayList<HashMap<Integer, Double>> table;
	
	
	public TTable readTransTable(String fileName) throws IOException, ClassNotFoundException {
		BufferedReader r = new BufferedReader(new FileReader(fileName));
		ObjectInputStream in = new ObjectInputStream(new FileInputStream("TTable.ser"));
		return (TTable)in.readObject();
		
	}
	
	private static int findNumWords(String fileName) throws IOException{
		BufferedReader r = new BufferedReader(new FileReader(fileName));
		for(int i=0;i<100000;i++){
			r.readLine();
		}
		String temp = null, curr;
		while((curr=r.readLine())!=null){
			temp = curr;
		}
		int num = Integer.parseInt(temp.split(" ")[0]);
		System.out.println(num);
		return num;
		
		
	}
	
	public void storeTT(String fileName, int numWords) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(fileName));
		table = new ArrayList<HashMap<Integer, Double>>(46983);
		String s;
		String[] spl;
		HashMap<Integer, Double> currWordMap = null;
		while((s = r.readLine())!=null){
			spl = s.split(" ");
			int wordId = Integer.parseInt(spl[0]);
			Integer titleId = Integer.parseInt(spl[1]);
			Double tValue = Double.parseDouble(spl[3]);
			
			if(table.get(wordId))
			
		}
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("TTable.ser"));
		out.writeObject(this);
		r.close();
		
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println(findNumWords("Test4(1-10-0-1-1-0).final"));
	}
}

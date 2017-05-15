package benchmarking;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

public class BMOWriter {
	
	
	public static void writeSML(ArrayList<ArrayList<Object>>S_ml, String className, int n, int k, String type, String option) throws IOException {
		
		String filename = "data/SML" + "-" + className + "-n" + n + "-k" + k + "-" + option + "-" + type + ".txt"; 
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8"));
		for (int i = 0; i < S_ml.size(); i++) {
			writer.write(S_ml.get(i).size() + "\n");
		}
		writer.close();
	}
	
	public static void writeResult(ArrayList<Object> result, String className, int n, int k, String type, String option) throws IOException {
		
		String filename = "data/Result" + "-" + className + "-n" + n + "-k" + k + "-" + option + "-" + type + ".txt"; 
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8"));
		for (int i = 0; i < result.size(); i++) {
			writer.write(result.get(i) + "\n");
		}
		writer.close();
	}
	

}

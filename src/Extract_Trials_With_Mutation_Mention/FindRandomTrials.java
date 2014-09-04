package Extract_Trials_With_Mutation_Mention;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

//read the file with trials output and select 50 random trials
public class FindRandomTrials {
	
	static int startLine= 1;
	static int endLine= 397;

	public static void main(String[] args) {
		
		
		
		ArrayList<Integer> lineNumber= new ArrayList<Integer>();
		StringBuilder sb = new StringBuilder(); 
		Random rand = new Random();
		
		//select 40 random line numbers between 1 and 397. 
		while(lineNumber.size()!=40){
			int randomNumber = rand.nextInt(endLine)+startLine;		
			
			if(!lineNumber.contains((int)randomNumber)){
				
				lineNumber.add(randomNumber);
			}
		}
		
		//sort the arraylist 
		Collections.sort(lineNumber);
		
//		for(int ln:lineNumber){
//			System.out.println(ln+"\t");
//			
//		}
		
		
		 try {
		
			// LineNumberReader extends BufferedReader
			LineNumberReader rdr = new LineNumberReader(new FileReader("C:\\myprojects\\Mayo_Internship_2014\\Research\\LinkedCTWithMutationMentions\\logMutationWordsSpan_withWS.txt")); 
					
			String line;
			int count=0;
			
			while((line = rdr.readLine())!=null){
				
				if(lineNumber.contains(rdr.getLineNumber())){
					count++;
					//line=line.replaceAll("##","\n").replaceAll(":\n",":\n\t");
					
					//sb.append("[Trail_"+count+"]\t");
					sb.append(line.substring(0,line.indexOf("\t")));
					sb.append("\n");
				}
				
			
				
			}
		  
			rdr.close();
			
			BufferedWriter bufferedWriter = new BufferedWriter(	new FileWriter("C:\\Users\\m128320\\Documents\\Research\\LinkedCTWithMutationMentions\\random_trials.txt"));
			bufferedWriter.write(sb.toString());
			
			bufferedWriter.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		
		  
		  
		  
		  
		  

	}

}

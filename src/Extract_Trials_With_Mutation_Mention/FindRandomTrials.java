package Extract_Trials_With_Mutation_Mention;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
		
		//include list of old random trial IDs I ground truthed before
				ArrayList<String> oldRandomTrials = new ArrayList<String>();
				try {
					BufferedReader bfrd = new BufferedReader(new FileReader("C:\\myprojects\\Mayo_Internship_2014\\Research\\Research\\LinkedCTWithMutationMentions\\random_trials.txt"));
					String content;
					while((content=bfrd.readLine())!=null){
						oldRandomTrials.add(content);
					}
					
					bfrd.close();
					
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		
		ArrayList<Integer> lineNumber= new ArrayList<Integer>();
		ArrayList<String> randomTrialIDs = new ArrayList<String>();
		Random rand = new Random();
		
		//select 40 random line numbers between 1 and 397. 
		while(lineNumber.size()!=80){
			int randomNumber = rand.nextInt(endLine)+startLine;		
			
			//make sure the random number is unique 
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
		
		
		LineNumberReader readr;
		try {
			readr = new LineNumberReader(new FileReader("C:\\myprojects\\Mayo_Internship_2014\\Research\\Research\\LinkedCTWithMutationMentions\\logMutationWordsSpan_withWS.txt"));
		
		String lineRead;
		while((lineRead = readr.readLine())!=null){
		if(lineNumber.contains(readr.getLineNumber())){
		
			String trialID = lineRead.substring(0,lineRead.indexOf("\t"));
			if(!oldRandomTrials.contains(trialID)){
			randomTrialIDs.add(trialID);
			}
			
		}
		}
		readr.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		System.out.println("# of Unique and new trials: "+randomTrialIDs.size());
		
		//now pull these random trials with their eligibility criteria from the raw file.
		 try {
		
			// LineNumberReader extends BufferedReader
			LineNumberReader rdr = new LineNumberReader(new FileReader("C:\\myprojects\\Mayo_Internship_2014\\Research\\Research\\LinkedCTWithMutationMentions\\Leukemia_mutation_filtered_trials_withWhiteSpace.txt")); 
			
			BufferedWriter bufferedWriter = new BufferedWriter(	new FileWriter("C:\\myprojects\\Mayo_Internship_2014\\Research\\Research\\LinkedCTWithMutationMentions\\random_trials__with_criteria_Leukemia_2.txt"));
			String line;
			
			while((line = rdr.readLine())!=null){
				
				if(line.startsWith("nct0")){
					String trialId = line.replaceAll("\\s",""); //remove white space
					if(randomTrialIDs.contains(trialId)){
					bufferedWriter.write(trialId); //include trialID in the trial file
					bufferedWriter.newLine();
					line= rdr.readLine();
					while(!line.startsWith("nct0")){
						bufferedWriter.write(line);
						bufferedWriter.newLine();
						line = rdr.readLine();
						if(line == null)
							break;
					}
					bufferedWriter.flush();
				}
				}
			
			}
		  
			rdr.close();
			
			
			
			
			//bufferedWriter.write(sb.toString());
			
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

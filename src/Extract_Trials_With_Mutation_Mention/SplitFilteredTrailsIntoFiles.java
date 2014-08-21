package Extract_Trials_With_Mutation_Mention;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class SplitFilteredTrailsIntoFiles {
	
	static final String DIR="C:\\Users\\m128320\\Documents\\Research\\LinkedCTWithMutationMentions\\";
	
	public static void main(String[] args){
		
		SplitFilteredTrailsIntoFiles obj = new SplitFilteredTrailsIntoFiles();
		obj.createFilePerTrial("C:\\Users\\m128320\\Documents\\Research\\LinkedCTWithMutationMentions\\Leukemia_mutation_filtered_trials_withWhiteSpace.txt","LeukemiaTrials");
		
	}
	
	void createFilePerTrial(String filteredTrialsFile, String subDirName){
		File subDirectory = new File(DIR+"\\"+subDirName);
		
		subDirectory.mkdir(); //create new directory everytime
		
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(filteredTrialsFile));
			
			BufferedWriter writer = null;
			
			//whenever we find a trialID (beginning with "nct0"), create a new file which contains all the sentences following this trialID. 
			//Stop including sentences at the beginning of next trialID
			String line = bReader.readLine();
			int fileCounter = 0;
			while(line!=null){
				if(line.startsWith("nct0"))
				{
					String trialId = line.replaceAll("\\s",""); //renmove white space
					writer = new BufferedWriter(new FileWriter(DIR+"\\"+subDirName+"\\"+"Trial_"+/*trialId+"_"+*/fileCounter+++".txt"));
					writer.write(trialId); //include trialID in the trial file
					writer.newLine();
					line= bReader.readLine();
					while(!line.startsWith("nct0")){
						writer.write(line);
						writer.newLine();
						line = bReader.readLine();
						if(line == null)
							break;
					}
					writer.flush();
				}
				
				writer.close();
				
			}
			
			bReader.close();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

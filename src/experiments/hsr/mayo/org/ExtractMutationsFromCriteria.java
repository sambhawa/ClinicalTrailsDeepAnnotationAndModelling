package experiments.hsr.mayo.org;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractMutationsFromCriteria {

	static LineNumberReader rdr;
	static BufferedWriter bufferedWriter;
	static int countMatches = 0 ;
	
	public static void main(String[] args) {


		StringBuffer incl_criteria = new StringBuffer();
		StringBuffer excl_criteria = new StringBuffer();
		boolean flag_incl = false;
		boolean flag_excl = false;
					
		
		
		try {
			rdr = new LineNumberReader(new FileReader("C:\\myprojects\\Mayo_Internship_2014\\Research\\Research\\LinkedCTWithMutationMentions\\random_trials_with_criteria_Leukemia_full.txt"));
			bufferedWriter = new BufferedWriter(new FileWriter("C:\\myprojects\\Mayo_Internship_2014\\Research\\Research\\LinkedCTWithMutationMentions\\mutations_in_random_trials_with_criteria_Leukemia_others.txt"));
			
			
			String line = rdr.readLine();
			while(line !=null){ //checks end of trials file.
				System.out.println("debug: "+line);
				if(line.startsWith("#")){
					line = rdr.readLine();
					continue; //skip preceeding comments in a file.
					
				}
					
			
				if(line.startsWith("nct0")){//checks beginning of a new trial
					//reset buffers and flags
					if(incl_criteria!=null)incl_criteria.delete(0, incl_criteria.length());
					if(excl_criteria!=null)excl_criteria.delete(0, excl_criteria.length());
					flag_incl = false;
					flag_excl = false;
					
					String trialId = line;	
					//System.out.println(trialId);
					//bufferedWriter.write(trialId); 				
					line= rdr.readLine();
					
					
					while(!line.startsWith("nct0")){//checks end of the current trial
						
						//split the criteria into incl./excl. - line beginning with "inclusion" or "disease characteristic" marks the beginning of incl. 
						//									  - line beginning with "exclusion" marks the end of incl. and start of excl. 
						//get two strings representing incl. and excl. and determine the mutation mentions in them. 
					
						
						
						String lineLowerCase = line.toLowerCase();
						
						if(lineLowerCase.contains("inclusion")||lineLowerCase.contains("disease characteristic") ){
							
							flag_incl = true;						
						}
						 if(lineLowerCase.contains("exclusion")){
							if(flag_incl)
								flag_incl = false;
							flag_excl = true;
						}
						
						if(flag_incl){
							//System.out.println(trialId+":"+line);
							incl_criteria.append(line);
						}
						
						if(flag_excl){
							excl_criteria.append(line);
						}
						
						
						//read further lines from this criteria
						line = rdr.readLine();
						//System.out.println(trialId+":"+line);
						if(line == null)
							break;
						
						
					}
					System.out.println("exiting: "+line);
				
					//bufferedWriter.flush();
					
					//now find the mutation mentions in the incl and excl criteria, per bullet point
					
					if(incl_criteria!=null){
						String incl_crit_alt = incl_criteria.toString().replaceAll("\\s-","~").replaceAll("\\n", "").replaceAll("( )+", " ").replaceAll("(\\.)+",".");
						
						//split by '~', and record each sentence which has mutation (and associated context, if any -- for future)
						String[] crit_lines = incl_crit_alt.split("~");
						
						for(String crit_line:crit_lines){
						//findStrAbn(trialId,"i",crit_line);
							findOtherMutMentions(trialId,"i",crit_line);
						}
					}
					if(excl_criteria!=null){
						String excl_crit_alt = excl_criteria.toString().replaceAll("\\s-","~").replaceAll("\\n", "").replaceAll("( )+", " ").replaceAll("(\\.)+",".");
						
						//split by '~', and record each sentence which has mutation (and associated context, if any -- for future)
						String[] crit_lines = excl_crit_alt.split("~");
						
						for(String crit_line:crit_lines){
						//findStrAbn(trialId,"e",crit_line);
							findOtherMutMentions(trialId,"i",crit_line);
						}	
					}
					
				}
				else{
					line = rdr.readLine();	
				}
				
				
				
			
			}
		  
			System.out.println("total matches: "+countMatches);
			rdr.close();			
			bufferedWriter.close();
		
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
					
		
		
			
		} 
	
	// check for different mutation patterns for a given line of incl or excl criteria and o/p: trialID,i/e,mut_label [,context]
	public static void findStrAbn(String trialId, String type, String crit_line) {
		boolean matchFound = false;

		
		try {

			ArrayList<Pattern> patterns = new ArrayList<Pattern>();
			
			//encode regex for capturing different types of mutation mentions
			
			/*//patterns - v.0.0.0
			patterns.add(Pattern.compile("t[\\s]*\\(([1-9]|1[0-9]|2[0-2])(;)?(:)?([1-9]|1[0-9]|2[0-2])\\)[\\s]*(\\([qp][0-9][0-9](.[1-9])?(;)?[qp][0-9][0-9]\\))?",Pattern.CASE_INSENSITIVE));// for translocation
			patterns.add(Pattern.compile("del[\\s]*\\([0-9][0-9]?[pq]\\)",Pattern.CASE_INSENSITIVE)); // for deletions
			patterns.add(Pattern.compile("inv[\\s]*(\\()?[0-9][0-9](\\))?(\\([pq][0-9][0-9](.[1-9])?(;)?[qp][0-9][0-9]\\))?",Pattern.CASE_INSENSITIVE)); // for inversions
			//patterns - v.0.0.0
*/			
			//patterns - v.0.0.1
			patterns.add(Pattern.compile("t[\\s]*(\\()?(\\[)?([1-9]|1[0-9]|2[0-2])[\\s]*(;)?(:)?[\\s]*([1-9]|1[0-9]|2[0-2])\\)[\\s]*(\\([qp][0-9][0-9](.[1-9])?[\\s]*(;)?[\\s]*[qp][0-9][0-9]\\))?"));// for translocation
			patterns.add(Pattern.compile("del[\\s]*\\([0-9][0-9]?[pq]\\)",Pattern.CASE_INSENSITIVE)); // for deletions
			patterns.add(Pattern.compile("(inv|inversion)[\\s]*[\\.]*(\\()?(1[0-9])(\\))?(\\([pq][0-9][0-9](.[1-9])?(;)?[qp][0-9][0-9]\\))?",Pattern.CASE_INSENSITIVE)); // for inversions
			patterns.add(Pattern.compile("11q23",Pattern.CASE_INSENSITIVE));
			patterns.add(Pattern.compile("[-\\+]([1-9]|1[0-9])/([1-9]|1[0-9])[qp]-",Pattern.CASE_INSENSITIVE));
			patterns.add(Pattern.compile("([1-9]|1[0-9])[qp]-",Pattern.CASE_INSENSITIVE));
			patterns.add(Pattern.compile("t([1-9][0-9]?[^;[1-9][0-9]?])"));
			//patterns - v0.0.1
			
			
			Matcher matcher;
			for (Pattern p : patterns) {
				matcher = p.matcher(crit_line);
				while (matcher.find()) {
					if(matchFound==false) {
						matchFound = true; //set the flag if any pattern match is found
						//System.out.print(trialId+","+type+",");
						
						bufferedWriter.write(trialId+","+type+",");
					}
			
					//System.out.print(matcher.group()+ ",");
					bufferedWriter.write(matcher.group()+ ","); //for the current trial, log the matching mutation mention and its span as character offset
				}
				
			}
			
			
			if(matchFound){
				//System.out.println();
				bufferedWriter.newLine(); // all mutation mentions for the current line are found, hence add new line now
			}
				
			
			bufferedWriter.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	
	public static void findOtherMutMentions(String trialId, String type, String crit_line) {
		boolean matchFound = false;

		
		try {

			ArrayList<Pattern> patterns = new ArrayList<Pattern>();
			
			
			//other-mutation mentions (phrases, protein mutations)
			//1. PML(-)?(/)?RAR(A)?(\s)?(\?)?
			//2. CBF(\?)?(B)?(/|-)?(\s)*(MYH11|SMMHC)
			//3. BCR(/|-)ABL
			//4. FLT3(-|/)ITD
			patterns.add(Pattern.compile("PML(-)?(/)?RAR(A)?(\\s)?(\\?)?",Pattern.CASE_INSENSITIVE));
			patterns.add(Pattern.compile("CBF(\\?)?(B)?(/|-)?(\\s)*(MYH11|SMMHC)",Pattern.CASE_INSENSITIVE));
			patterns.add(Pattern.compile("BCR(/|-)ABL",Pattern.CASE_INSENSITIVE));
			patterns.add(Pattern.compile("FLT3(-|/)ITD",Pattern.CASE_INSENSITIVE));
					
			//------------------Phrases-----------------
			//5. FLT3(-)?(\s)?(mutation(s)?|abnormalities)
			//6. chromosome(\s)*[1-9](,(\s)*([1-9][0-9]?))*(\s)*abnormalit(y|ies)
			//7. abnormalities of [1-9](,(\s)*([1-9][0-9]?))*
			//8. abnormal(\s)*((,)?(\s)*([1-9]|1[0-9]|2[0-2])[pq]?)+
			//9. abnormal(\s)*((,)?(\s)*([1-9]|1[0-9]|2[0-2])[pq]?)*
			//10. complex(\s)*(karyotype(s)?|cytogenetics)
			//11. tri(somy)?(\s)*([1-9]|1[0-9]|2[0-3])
			//12. hypodiploid(y)?
			//14. monosomy(\s)*([1-9]|1[0-9])?
			//15. MLL(\s)*(gene)?(\s)*(rearrangement(s)?|translocation)
			//16. philadelphia(\s)*(-)?(chromosome|positive|negative)+
			//17. AML(1)?(-|/)ETO
			
			patterns.add(Pattern.compile("FLT3(-)?(\\s)?(mutation(s)?|abnormalities)",Pattern.CASE_INSENSITIVE));
			patterns.add(Pattern.compile("chromosome(\\s)*[1-9](,(\\s)*([1-9][0-9]?))*(\\s)*abnormalit(y|ies)",Pattern.CASE_INSENSITIVE));
			patterns.add(Pattern.compile("abnormalities of [1-9](,(\\s)*([1-9][0-9]?))*",Pattern.CASE_INSENSITIVE));
			patterns.add(Pattern.compile("abnormal(\\s)*((,)?(\\s)*([1-9]|1[0-9]|2[0-2])[pq]?)+",Pattern.CASE_INSENSITIVE));
			patterns.add(Pattern.compile("complex(\\s)*(karyotype(s)?|cytogenetics)",Pattern.CASE_INSENSITIVE));
			patterns.add(Pattern.compile("tri(somy)?(\\s)*([1-9]|1[0-9]|2[0-3])",Pattern.CASE_INSENSITIVE));
			patterns.add(Pattern.compile("hypodiploid(y)?",Pattern.CASE_INSENSITIVE));
			patterns.add(Pattern.compile("monosomy(\\s)*([1-9]|1[0-9])?",Pattern.CASE_INSENSITIVE));
			patterns.add(Pattern.compile("MLL(\\s)*(gene)?(\\s)*(rearrangement(s)?|translocation)",Pattern.CASE_INSENSITIVE));
			patterns.add(Pattern.compile("philadelphia(\\s)*(-)?(chromosome|positive|negative)+",Pattern.CASE_INSENSITIVE));	
			patterns.add(Pattern.compile("AML(1)?(-|/)ETO",Pattern.CASE_INSENSITIVE));
			//mutation phrases
			
			
			Matcher matcher;
			
			for (Pattern p : patterns) {
				matcher = p.matcher(crit_line);
				while (matcher.find()) {
					if(matchFound==false) {
						matchFound = true; //set the flag if any pattern match is found
						//System.out.print(trialId+","+type+",");
						
						bufferedWriter.write(trialId+","+type+",");
					}
			
					countMatches++;
					//System.out.print(matcher.group()+ ",");
					bufferedWriter.write(matcher.group()+ ","); //for the current trial, log the matching mutation mention and its span as character offset
				}
				
			}
			
			
			if(matchFound){
				//System.out.println();
				bufferedWriter.newLine(); // all mutation mentions for the current line are found, hence add new line now
			}
				
			
			bufferedWriter.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	}



package Extract_Trials_With_Mutation_Mention;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class EligCritHandlerToFilterTrials extends DefaultHandler {

	boolean trials = false;
	boolean crit = false;
	
	
	
	StringBuffer crit_full = new StringBuffer();
	StringBuffer trialID = new StringBuffer();

	FileWriter fileWriter = null;
	BufferedWriter bufferedWriter = null;
	BufferedWriter logWriter;
	private int countCT=0;
	static int countMatches = 0;

	public void startDocument() throws SAXException {

		// Open output files
		try {

			bufferedWriter = new BufferedWriter(
					new FileWriter(
							"C:\\myprojects\\Mayo_Internship_2014\\Research\\Research\\LinkedCTWithMutationMentions\\Updated_Leukemia_mutation_filtered_trials_withWhiteSpace.txt"));//for filtered trials

			logWriter = new BufferedWriter(
					new FileWriter(
							"C:\\myprojects\\Mayo_Internship_2014\\Research\\Research\\LinkedCTWithMutationMentions\\Updated_logMutationWordsSpan_withWS.txt")); //for logs

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		// extract trial url and criteria

		if (qName.equalsIgnoreCase("result")) {

			if (crit_full.length() != 0) {
				// System.out.println("crit: "+crit_full);
				writeCriteriaToFile(crit_full);
				crit = false;
				crit_full.delete(0, crit_full.length());
			}

		}

		if (attributes.getValue(0) != null)
			if (attributes.getValue(0).equalsIgnoreCase("trials1")) {
				trials = true;
				if(trialID.length()!=0)
				{
					trialID.delete(0, trialID.length());
				}
			}

		if (attributes.getValue(0) != null)
			if (attributes.getValue(0).equalsIgnoreCase("crit")) {
				crit = true;

			}

	}

	private void writeTrailIDToFile(StringBuffer trialID2) {
		if (trialID2.length() != 0)
			try {
				String trialID2_str = trialID2.toString();
				trialID2_str = trialID2_str.substring(trialID2_str.indexOf("nct"));
				// System.out.println("trial: "+trialID);

				bufferedWriter.write(trialID2_str);
				logWriter.write(trialID2_str);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	// To perform filtering by regex to extract only those trials which have
	// mutation mentions in eligibility criteria string
	private void writeCriteriaToFile(StringBuffer crit_full2) {
		boolean foundMatch = false;
		if (crit_full2.length() != 0) {
			 
			// first put the criteria in one line, period separated, remove
			// newlines to get correct char offset of matches.
			//String noSpecChar = crit_full2.toString().replaceAll("\\n","#").replaceAll("( )+", " ")/*.replaceAll("\\n-","#")*/;

			String noSpecChar=crit_full2.toString().replaceAll("\\s-","~").replaceAll("\\n", "").replaceAll("( )+", " ").replaceAll("(\\.)+",".");
			// check if any mutation pattern is found
			foundMatch = (findStrAbn(noSpecChar)||findOtherMutMentions(noSpecChar)); //if anytype of mutation detected by our rule-based system. 
			try {	
			if (foundMatch) {
				
					countCT++;
					logWriter.newLine();
					
					bufferedWriter.write("\t" + crit_full2); //output the criteria
					bufferedWriter.newLine();
			}
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private boolean findMutationMentions(String noSpecChar) {
		boolean matchFound = false;

		// check for different mutation patterns

		try {

			ArrayList<Pattern> patterns = new ArrayList<Pattern>();
			
			//encode regex for capturing different types of mutation mentions
			
			//patterns - v.0.0.0
			patterns.add(Pattern.compile("t[\\s]*\\(([1-9]|1[0-9]|2[0-2])(;)?(:)?([1-9]|1[0-9]|2[0-2])\\)[\\s]*(\\([qp][0-9][0-9](.[1-9])?(;)?[qp][0-9][0-9]\\))?",Pattern.CASE_INSENSITIVE));// for translocation
			patterns.add(Pattern.compile("del[\\s]*\\([0-9][0-9]?[pq]\\)",Pattern.CASE_INSENSITIVE)); // for deletions
			patterns.add(Pattern.compile("inv[\\s]*(\\()?[0-9][0-9](\\))?(\\([pq][0-9][0-9](.[1-9])?(;)?[qp][0-9][0-9]\\))?",Pattern.CASE_INSENSITIVE)); // for inversions
			//patterns - v.0.0.0
			
			Matcher matcher;
			for (Pattern p : patterns) {
				matcher = p.matcher(noSpecChar);
				while (matcher.find()) {
					if(matchFound==false) matchFound = true; //set the flag if any pattern match is found
					
					//output trialID only if match is found
					writeTrailIDToFile(trialID); //writes to both logfile and output file
					trialID.delete(0, trialID.length());
					logWriter.write("\t"+matcher.group() + " [" + matcher.start()+ ":" + matcher.end() + "]\t"); //for the current trial, log the matching mutation mention and its span as character offset
				}
				
			}
			
			if(matchFound)
				logWriter.newLine(); // all mutation mentions for the current trial's elig criteria are found, hence new line
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return matchFound;
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		if (qName.equalsIgnoreCase("uri")) {
			if (trialID.length() != 0) {
				
				trials = false;
				
			}
		}
	}
	
	

	public void characters(char ch[], int start, int length)
			throws SAXException {

		if (trials) {
			trialID = trialID.append(ch, start, length);
		}

		if (crit) {
			crit_full = crit_full.append(ch, start, length);
		}

	}

	public void endDocument() throws SAXException {
		// Always close files.
		try {
			logWriter.write("Total number of trials with mutation mentions: "+countCT);
			logWriter.write("Total number of mutations: "+countMatches);
			bufferedWriter.close();
			logWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	// check for different mutation patterns for a given line of incl or excl criteria and o/p: trialID,i/e,mut_label [,context]
		public boolean findStrAbn(String crit_line) {
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
						if(matchFound==false) matchFound = true; //set the flag if any pattern match is found
						
						//output trialID only if match is found
						countMatches++;
						writeTrailIDToFile(trialID); //writes to both logfile and output file
						trialID.delete(0, trialID.length());
						logWriter.write("\t"+matcher.group()); //for the current trial, log the matching mutation mention and its span as character offset
					}
					
				}
				
				//if(matchFound)
					//logWriter.newLine(); // all mutation mentions for the current trial's elig criteria are found, hence new line
				
				logWriter.flush();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return matchFound;
			
		}
		
		
		public boolean findOtherMutMentions(String crit_line) {
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
				//7. abnormalities of [1-9](,(x\s)*([1-9][0-9]?))*
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
						if(matchFound==false) matchFound = true; //set the flag if any pattern match is found
						
						//output trialID only if match is found
						//writeTrailIDToFile(trialID); //writes to both logfile and output file
						//trialID.delete(0, trialID.length());
						countMatches++;
						logWriter.write("\t"+matcher.group()); //for the current trial, log the matching mutation mention and its span as character offset
					}
					
				}
				
				//if(matchFound)
					//logWriter.newLine(); // all mutation mentions for the current trial's elig criteria are found, hence new line
				
					
				
				logWriter.flush();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return matchFound;
		}

}

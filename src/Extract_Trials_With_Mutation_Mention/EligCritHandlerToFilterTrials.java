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

	public void startDocument() throws SAXException {

		// Open output files
		try {

			bufferedWriter = new BufferedWriter(
					new FileWriter(
							"C:\\Users\\m128320\\Documents\\Research\\LinkedCTWithMutationMentions\\Leukemia_mutation_filtered_trials_withWhiteSpace.txt"));//for filtered trials

			logWriter = new BufferedWriter(
					new FileWriter(
							"C:\\Users\\m128320\\Documents\\Research\\LinkedCTWithMutationMentions\\logMutationWordsSpan_withWS.txt")); //for logs

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
			String noSpecChar = crit_full2.toString().replaceAll("\\n","#").replaceAll("( )+", " ")/*.replaceAll("\\n-","#")*/;

			// check if any mutation pattern is found
			foundMatch = findMutationMentions(noSpecChar);
			try {	
			if (foundMatch) {
				
					countCT++;
					
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
			bufferedWriter.close();
			logWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

package Convert_MutFinder_Format;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class HandlerToConvertEligCritToMFFormat extends DefaultHandler {
	
	
	
	boolean trials = false;
	boolean crit = false;
	StringBuffer crit_full = new StringBuffer();
	StringBuffer trialID = new StringBuffer();
	
	 FileWriter fileWriter=null;
	 BufferedWriter bufferedWriter=null;
 
	 public void startDocument() throws SAXException {
		 
		 // Open output file
         try {
			fileWriter = new FileWriter("C:\\Users\\m128320\\Documents\\Research\\MutationFinderExpt\\lung_cancer_output.txt");
			 // Always wrap FileWriter in BufferedWriter.
	         bufferedWriter = new BufferedWriter(fileWriter);
	         
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        

	 }
	
	public void startElement(String uri, String localName,String qName, 
                Attributes attributes) throws SAXException {
 
		
		
	
		//extract trial url and criteria
 
		if(qName.equalsIgnoreCase("result"))
		{
			
			
			if(crit_full.length()!=0){
			//System.out.println("crit: "+crit_full);
			writeCriteriaToFile(crit_full);
			crit=false;			
			crit_full.delete(0, crit_full.length());}
			
		}
		
		if(attributes.getValue(0)!=null)
		if (attributes.getValue(0).equalsIgnoreCase("trials1")) {
			 trials = true;
		}
		
		if(attributes.getValue(0)!=null)
			if (attributes.getValue(0).equalsIgnoreCase("crit")) {
				crit = true;
				
				
			}
 
		
		
 
	}
 
	private void writeTrailIDToFile(StringBuffer trialID2) {
		if(trialID2.length()!=0)
		try {
			String trialID2_str = trialID2.toString();			
			trialID2_str=trialID2_str.substring(trialID2_str.indexOf("nct"));
			//System.out.println("trial: "+trialID);
			
			bufferedWriter.write(trialID2_str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}

	private void writeCriteriaToFile(StringBuffer crit_full2) {
		if(crit_full2.length()!=0){
		String noSpecChar = crit_full2.toString().replaceAll("\\n", "").replaceAll("( )+", " ").replaceAll("\\s-","");
		 try {
			bufferedWriter.write("\t"+noSpecChar+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}

	public void endElement(String uri, String localName,
		String qName) throws SAXException {
 
		 if(qName.equalsIgnoreCase("uri"))
		 {
			 if(trialID.length()!=0){
					writeTrailIDToFile(trialID);
					trials= false;
					trialID.delete(0, trialID.length());	
					}
		 }
	}
 
	public void characters(char ch[], int start, int length) throws SAXException {
 
		if (trials) {
			trialID=trialID.append(ch,start,length);
		}
 
		if (crit) {
			crit_full=crit_full.append(ch,start,length);
		}
		
		
 
	}
	
	 public void endDocument() throws SAXException {
		 // Always close files.
         try {
			bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
 

}

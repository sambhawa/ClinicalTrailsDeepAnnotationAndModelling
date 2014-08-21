package Convert_MutFinder_Format;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
 
public class ParseXMLToText{
 
   public static void main(String argv[]) {
 
    try {
 
	SAXParserFactory factory = SAXParserFactory.newInstance();
	SAXParser saxParser = factory.newSAXParser();
 
	/*DefaultHandler handler = new DefaultHandler() {
 
	boolean trials = false;
	boolean crit = false;
	StringBuffer crit_full = new StringBuffer();
 
	public void startElement(String uri, String localName,String qName, 
                Attributes attributes) throws SAXException {
 
		
		//System.out.println("qname :" + qName);
		
		//extract trial url and criteria
 
		if(qName.equalsIgnoreCase("result"))
		{
			if(crit_full.length()!=0)
			System.out.println("crit: "+crit_full);
			crit=false;			
			crit_full.delete(0, crit_full.length());
			
		}
		
		if(attributes.getValue(0)!=null)
		if (attributes.getValue(0).equalsIgnoreCase("trials")) {
			 trials = true;
		}
		
		if(attributes.getValue(0)!=null)
			if (attributes.getValue(0).equalsIgnoreCase("crit")) {
				crit = true;
			}
 
		
		
 
	}
 
	public void endElement(String uri, String localName,
		String qName) throws SAXException {
 
		
		
 
	}
 
	public void characters(char ch[], int start, int length) throws SAXException {
 
		if (trials) {
			System.out.println("trials : " + new String(ch, start, length));
			trials = false;
		}
 
		if (crit) {
			crit_full=crit_full.append(ch,start,length);
		}
		
		
 
	}
 
     };*/
 
//     File file = new File("C:\\Users\\m128320\\Documents\\Research\\MutationFinderExpt\\Elig_sample.xml");
//     InputStream inputStream= new FileInputStream(file);
//     Reader reader = new InputStreamReader(inputStream,"UTF-8");
//
//     InputSource is = new InputSource(reader);
//     is.setEncoding("UTF-8");
//
//     saxParser.parse(is, handler);

       //saxParser.parse("C:\\Users\\m128320\\Documents\\Research\\MutationFinderExpt\\Elig_sample.xml", new Elig_Crit_Handler());
 
	   //saxParser.parse("C:\\Users\\m128320\\Documents\\Research\\sparql_results_linkedct\\Leukemia_1.xml", new Elig_Crit_Handler()); // for Leukemia trials
		//saxParser.parse("C:\\Users\\m128320\\Documents\\Research\\sparql_results_linkedct\\prostate_trials.xml", new Elig_Crit_Handler());//for prostate trials
	//	saxParser.parse("C:\\Users\\m128320\\Documents\\Research\\sparql_results_linkedct\\colorectal_cancer.xml", new Elig_Crit_Handler()); //for colorectal cancers
	//saxParser.parse("C:\\Users\\m128320\\Documents\\Research\\sparql_results_linkedct\\tumor_trials.xml", new Elig_Crit_Handler()); //for tumors
	saxParser.parse("C:\\Users\\m128320\\Documents\\Research\\sparql_results_linkedct\\lung_cancer_trials.xml", new HandlerToConvertEligCritToMFFormat());
		
     }  catch(ParserConfigurationException e) {
    	 e.printStackTrace();
     }
     catch(SAXException e) {
    	 e.printStackTrace();
     }
     catch(IOException e) {
    	 e.printStackTrace();
     }
 
   }
 
}
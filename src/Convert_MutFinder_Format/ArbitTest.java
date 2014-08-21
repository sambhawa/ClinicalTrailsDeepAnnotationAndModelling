package Convert_MutFinder_Format;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class ArbitTest {
	
	public static void main(String[] args) throws IOException {
		
		try {
            // Open output file
            FileWriter fileWriter = new FileWriter("C:\\Users\\m128320\\Documents\\Research\\Test_Set_CT\\elig_5_nospace.txt");

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

          
           
		//parse xml to extract the trial_id and eligibility criteria. O/P format: trial_id<tab>crit
          
            	 
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
		    saxParser.parse("C:\\Users\\m128320\\Documents\\Research\\MutationFinderExpt\\Elig_sample.xml", new HandlerToConvertEligCritToMFFormat());    
            
		
		StringBuilder sb = new StringBuilder();
	     BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\m128320\\Documents\\Research\\Test_Set_CT\\elig_5.txt"));
	     
	     
	     
	     String line;
	     while ( (line=br.readLine()) != null) {
	       sb.append(line);
	       // or
	       //  sb.append(line).append(System.getProperty("line.separator"));
	     }
	     String nohtml = sb.toString().replaceAll("\\<.*?>","");
	     String noSpecChar = nohtml.replaceAll("\\n", "").replaceAll("( )+", " ").replaceAll("^-","");
	     
	     
	     bufferedWriter.write(noSpecChar);

	            // Always close files.
	            bufferedWriter.close();
	        }
	        catch(IOException ex) {
	            System.out.println(
	                "Error writing to file");
	            // Or we could just do this:
	            // ex.printStackTrace();
	        }
	     //System.out.println(noSpecChar);
 catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

}

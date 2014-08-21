package annotation_pipeline.hsr.mayo.org;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import elig_crit_model.hsr.mayo.org.JenaModelForOntology;

public class PopulateOntologyWithAnnotations {
	
			
	
			// create the base model
			static String tbox = "Mutations_In_LinkedCT_tbox.owl"; //the tbox
			static String SOURCE = "http://www.semanticweb.org/sambhawa/ontologies/2014/6/Mutations_In_LinkedCT.owl";
			final static String NS = SOURCE + "#";
	
			static OntModel ontModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM ); 
	
	public static void main(String[] args){		
		
		//read the tbox
		ontModel.read( tbox);
		
		//read a directory of filtered CT files (those that contain mutation mentions). Annotate each file one at a time.
		if(args.length==0){
			
				System.out.println("Please specify path of directory containing the trial files in the argument");
				System.exit(-1); //exit the program
			
		}		
		else {//args[0] = path of directory containing the trial files to be annotated. 
			
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter("Mutations_In_LinkedCT_abox.owl"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} // output file
			
			
			int countTrials = 0;
			File trialDir = new File(args[0]);
			
			for ( File trialFile : trialDir.listFiles()){	
		
			countTrials++;	
		
			try {
				
			
			
			
			//TODO - deal with Disease/Patient characteristics (CTs that don't have incl/excl criteria)
			
			//input a trial with inclusion and/or exclusion criteria
			//BufferedReader bReader = new BufferedReader(new FileReader("C:\\Users\\m128320\\Documents\\Research\\LinkedCTWithMutationMentions\\example_trial_for_testing.txt")); 
			System.out.println(trialFile.getPath());
			BufferedReader bReader = new BufferedReader(new FileReader(trialFile)); 
			
			//read trialID and create docID based on that (docID = CT_<trialID>)
			String line_0= bReader.readLine();
			String trialID =null;
			if(line_0.startsWith("nct"))
			{
				trialID = line_0;
			}
			
			System.out.println(trialID);
			
			//separate inclusion and exclusion criteria, handle disease/patient characteristics separately
			String line;
			boolean flag_incl= false;
			boolean flag_excl = false;
			StringBuilder incl_crit = new StringBuilder();
			StringBuilder excl_crit = new StringBuilder();
			
			//capture inclusion and/or exclusion criteria (TODO: capture disease/patient characteristics)
			while((line = bReader.readLine())!=null){
				
				if(line.contains("Inclusion Criteria"))
				{
				 flag_incl = true;	
				}
				
				if(line.contains("Exclusion Criteria")){
					
					
					if(flag_incl)
					flag_incl = false;
					flag_excl = true;
				}
				
				if(line.contains("##")) //marks end of the trial file
				{
					
					flag_incl=false;
					flag_excl = false;
					
				}

				
				if(flag_incl){
					
					incl_crit.append(line);
					
				}
				
			  if(flag_excl){
				  
				  excl_crit.append(line);
			  }
				
			}
			
		//now preprocess the incl and excl criteria before mutation detection to remove extra spaces, bullet points, etc.
		StringBuilder incl_crit_processed = prepare(incl_crit);
		StringBuilder excl_crit_processed = prepare(excl_crit);
		
		//create Document assertions
		Individual docInstance = ontModel.createIndividual(NS+"Doc_"+trialID,ontModel.getOntClass(NS+"Document") );
		docInstance.addProperty(ontModel.getOntProperty(NS+"has_Path"),trialFile.getPath() );//put the path of current criteria file
		//can add more properties to the document here...
		
		//create trial assertions
		createTrialAssertions(ontModel, trialID);
		
		//create assertions at criteria level - criteria_id, criteria_text, mutation_phrase
		createCriteriaAssertions(ontModel,trialID,incl_crit_processed,1);
		createCriteriaAssertions(ontModel,trialID,excl_crit_processed,2);
		
		
		//create mutation assertions - type, notation, annotation-info (sentence, char-offset, docID)		
		createMutationAssertions(ontModel,trialID, incl_crit_processed,1); //1 denotes incl criteria
		createMutationAssertions(ontModel, trialID,excl_crit_processed, 2);//2 denotes excl criteria
		
		
		
		//TODO - create mutation dependency assertions - negative dependency in inclusion criteria
		
		
		
		
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
			//debug feature
			if(countTrials==10)
				break;
			
			}//close of for-each loop
			
			//output the model in rdf/xml format
			
			
			ontModel.write(writer,"RDF/XML-ABBREV");
		
	}//close of else
		
		
		
	}

	private static void createTrialAssertions(OntModel ontModel, String trialID) {
		
		if(trialID!=null){
			
			OntClass Trail = ontModel.getOntClass(NS+"Trial");
			Individual trialInstance= ontModel.createIndividual(NS+trialID, Trail); 
			
			//link with corresponding linkedCT trial 
			trialInstance.addProperty(ontModel.getOntProperty(NS+"hasLinkedCT_URI"), "http://data.linkedct.org/data/trial/"+trialID);
		}
			
		
	}

	//calls methods of class CreateCriteriaAssertions - criteria_id, criteria_text, mutation_phrase
	private static void createCriteriaAssertions(OntModel ontModel, String trialID, StringBuilder crit, int i) {
		
		String critID= null;
		Individual critInstance = null; 
	
	 if(i==1){//inclusion criteria
		 
		 //create criteria instance
		 critID = NS+"incl_"+trialID;
		 critInstance = ontModel.createIndividual(critID,ontModel.getOntClass(NS+"Inclusion_Criteria"));
	 }
	 else{//exclusion criteria
		
		//create criteria instance
		 critID = NS+"excl_"+trialID;
		 
		 critInstance= ontModel.createIndividual(critID,ontModel.getOntClass(NS+"Exclusion_Criteria")); 
	 } 	
	
	//add data property assertions for criteria instance
 	critInstance.addProperty(ontModel.getOntProperty(NS+"has_critID"),critID);
 	critInstance.addProperty(ontModel.getOntProperty(NS+"has_crit_text"),crit.toString());
		 
	// add property to connect criteria with the trial
	critInstance.addProperty(ontModel.getOntProperty(NS+"belongs_to"),ontModel.getIndividual(NS+trialID) );	
	
	}

	//calls the methods of class CreateMutationAssertions
	private static void createMutationAssertions(OntModel ontModel, String trialID, StringBuilder crit, int i) {
		
		Individual mut_phrase=null;
		
		String critID= null; // needed to relate mutation phrase to this criteria
		//String excl_crit=  NS+"excl_"+trialID;
		
		//call mutation detector on the criteria and create mutation object
		MutationDetector mutDetector = new MutationDetector();
		mutDetector.findMutationMentions(ontModel,trialID, mut_phrase, crit.toString(), NS, i);
		
		
		 
	}

/*
 * Rules (based on observation) to parse and structure the sentences in trials: 
 * 1. Bullet point indicates new sentences. 
 * 2. \\n followed by words implies continuation of sentences. These \\n must be removed. 
 * 3. In case bullets are absent - '.' followed by \\n indicates beginning of new line.  
 * 
 * 
 * 
 */
	

	private static StringBuilder prepare(StringBuilder crit) {
		
		//remove white spaces and replace bullets with '.'
		StringBuilder noSpecChar = new StringBuilder( crit.toString().replaceAll("\\s-","~").replaceAll("\\n", "").replaceAll("( )+", " ").replaceAll("(\\.)+","."));
		
		//System.out.println(noSpecChar);
		return noSpecChar;
	}
	
	

}

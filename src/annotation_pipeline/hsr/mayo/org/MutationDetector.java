package annotation_pipeline.hsr.mayo.org;

import java.awt.Label;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;

public class MutationDetector {
	
	//can read the regular expressions/rules from a text config file - better coding standard. 
	static final String TRANS =  "t[\\s]*\\(([1-9]|1[0-9]|2[0-2])(;)?(:)?([1-9]|1[0-9]|2[0-2])\\)[\\s]*(\\([qp][0-9][0-9](.[1-9])?(;)?[qp][0-9][0-9]\\))?";
	static final String DEL = "del[\\s]*\\([0-9][0-9]?[pq]\\)";
	static final String INV = "inv[\\s]*(\\()?[0-9][0-9](\\))?(\\([pq][0-9][0-9](.[1-9])?(;)?[qp][0-9][0-9]\\))?";
	
	static int mutation_counter=0;
	
	String mutationPhraseText;
	enum MutationWithRegularNotation{TRANSLOCATION, INVERSION, DELETION,POINTMUTATION};
	enum MutationWithIrregularNotation{DUPLICATION,TRISOMY,COMPLEX};
	
	public void findMutationMentions(OntModel model, String trialID, Individual mut_phrase, String criteria,String NS,int i ){
		
		
		//call rule-based mutation mentions - translocation, deletion, inversion
		 findMutationMentionsWithRegularNotation(model, trialID,mut_phrase, criteria,NS,i);
		
		//TODO Find phrase based mentions
		
		//TODO Find pointmutations
	}

	//for finding translocation, deletion, inversion
	private void findMutationMentionsWithRegularNotation(OntModel model, String trialID, Individual mut_phrase, String criteria, String NS, int i) {
		boolean matchFound = false;
		
		ArrayList<Pattern> patterns = new ArrayList<Pattern>();
		
		//encode regex for capturing different types of mutation mentions
		
		//patterns - v.0.0.0
		patterns.add(Pattern.compile(TRANS,Pattern.CASE_INSENSITIVE));// for translocation
		patterns.add(Pattern.compile(DEL,Pattern.CASE_INSENSITIVE)); // for deletions
		patterns.add(Pattern.compile(INV,Pattern.CASE_INSENSITIVE)); // for inversions
		//patterns - v.0.0.0s
		
		Matcher matcher;
		//int mutation_counter=0;
		String critID=null;
		
		for (Pattern p : patterns) {
			matcher = p.matcher(criteria);
			while (matcher.find()) {
				
					
					//create instance of mutation_phrase
					if(i==1){
						 
						critID= NS+"incl_"+trialID;
						mut_phrase= model.createIndividual(NS+"mutphrase_incl_"+trialID,model.getOntClass(NS+"Mutation_Phrase"));	
						mut_phrase.addProperty(model.getOntProperty(NS+"is_mutation_phrase_for"),model.getIndividual(critID));
					}
					else{
						critID= NS+"excl_"+trialID;
						mut_phrase= model.createIndividual(NS+"mutphrase_excl_"+trialID,model.getOntClass(NS+"Mutation_Phrase"));	
						mut_phrase.addProperty(model.getOntProperty(NS+"is_mutation_phrase_for"),model.getIndividual(critID));
					}
					
					//find the sentence number - count the number of periods before the match start offset 
					int countPeriods = 0; //since each criteria starts with "."
					int lastPeriodLoc = 0;
					
					int j=0;
					for (; j < criteria.length() && j< matcher.start(); j++){
						
					    if (criteria.charAt(j) == '~'){
					    	{
					    		countPeriods++; //holds count of sentence
					    		lastPeriodLoc=j;
					    	}
					    }
					}
					//System.out.println("start:"+lastPeriodLoc);
					//find index of next period
					int k = lastPeriodLoc+1;
					for( ;k<criteria.length();k++){
					
						if(criteria.charAt(k)== '~')
							break;
					}
					
					//System.out.println("end:"+k);
					//capture the sentence where this mutation mention occurs - capture substring between 2 period locations - j and k.
					String sentenceContent = criteria.substring(lastPeriodLoc+1,k);
					//System.out.println(sentenceContent);
					
				//create sentence instance
				Individual sentenceInstance = model.createIndividual(NS+"Sentence"+countPeriods,model.getOntClass(NS+"Sentence"));
				//add content of sentence
				sentenceInstance.addProperty(model.getOntProperty(NS+"has_content"), sentenceContent);
				
				
				//depending on the match, create instance of corresponding Mutation_Type and 
				//add value to has_mutation_type property
				switch(p.pattern()){
				
				case TRANS:	Individual trans_indv= model.createIndividual(NS+"TRANS_"+mutation_counter,model.getOntClass(NS+"Translocation"));
							trans_indv.addProperty(model.getOntProperty(NS+"hasMutation_Label"),matcher.group());
							trans_indv.addProperty(model.getProperty(NS+"occurs_in_sentence"),sentenceInstance );
							trans_indv.addProperty(model.getOntProperty(NS+"is_contained_in"), mut_phrase);
							mut_phrase.addProperty(model.getOntProperty(NS+"contains_mutation"),trans_indv);
							mutation_counter++;
							break;
							
				case DEL:	Individual del_indv= model.createIndividual(NS+"DEL_"+mutation_counter,model.getOntClass(NS+"Deletion"));
							del_indv.addProperty(model.getOntProperty(NS+"hasMutation_Label"),matcher.group()); 
							del_indv.addProperty(model.getProperty(NS+"occurs_in_sentence"),sentenceInstance );
							del_indv.addProperty(model.getOntProperty(NS+"is_contained_in"), mut_phrase);
							mut_phrase.addProperty(model.getOntProperty(NS+"contains_mutation"),del_indv);
							mutation_counter++;
							break;
							
				case INV: 	Individual inv_indv= model.createIndividual(NS+"INV_"+mutation_counter,model.getOntClass(NS+"Inversion"));
							inv_indv.addProperty(model.getOntProperty(NS+"hasMutation_Label"),matcher.group()); 
							inv_indv.addProperty(model.getProperty(NS+"occurs_in_sentence"),sentenceInstance );
							inv_indv.addProperty(model.getOntProperty(NS+"is_contained_in"), mut_phrase);
							mut_phrase.addProperty(model.getOntProperty(NS+"contains_mutation"),inv_indv);
							mutation_counter++;
							break;
				}
				
			
				//create annotation assertions - sentence and document
				//mutation_phrase appears in a sentence and the sentence appears in the document
				
				
				
				
				
				//create document instance
				Individual docInstance = model.getIndividual(NS+"Doc_"+trialID);
				sentenceInstance.addProperty(model.getOntProperty(NS+"occurs_in_doc"), docInstance);
				
				
				
			}
			
		}
		
		
		
	}
	
	

}


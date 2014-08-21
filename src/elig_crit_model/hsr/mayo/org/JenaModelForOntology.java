package elig_crit_model.hsr.mayo.org;

import com.hp.hpl.jena.rdf.model.*;


public class JenaModelForOntology {

	//Namespace declarations
	static final String ns = "http://www.semanticweb.org/sambhawa/ontologies/2014/6/Mutations_In_LinkedCT.owl#";
	static final String rdfs_ns = "http://www.w3.org/2000/01/rdf-schema#";
	static final String rdf_ns= "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	// Jena model for the ontology
	  public Model model;
	  
	  // properties    
	  public Property has_incl_crit;
	  public Property has_excl_crit;
	  public Property has_critID;
	  public Property has_crit_text;
	  public Property has_mutation_phrase;
	  public Property has_mutation_phraseID ;
	  public Property has_mutation_phrase_label;
	  public Property has_mutation_type;
	  public Property has_trialID;
	  public Property belongs_To_Trial;
	    
	    //classes
	  public Resource Eligibility_Criteria; 
	  public  Resource Exclusion_Criteria; 
	  public  Resource Inclusion_Criteria;
	  public  Resource Mutation_Phrase;
	  public  Resource Mutation_Type ;
	  public  Resource Mutation_With_Regular_Notation;
	  public Resource Mutation_With_Irregular_Notation;
	  public  Resource Translocation;
	  public  Resource Point_Mutation;
	  public  Resource Inversion;
	  public  Resource Deletion;
	  public Resource Trial; 
	  
	//constructor to create Jena model
	public JenaModelForOntology(){
		
		// Create an empty Model
	    model = ModelFactory.createDefaultModel();
	    
	    //set the namespace for the ontology
	    model.setNsPrefix("CTOntology", ns);
	    
	    // Create the types of Property we need to describe relationships in our ontology    
	     has_incl_crit = model.createProperty(ns+"has_incl_crit");
	     has_excl_crit = model.createProperty(ns+"has_excl_crit"); 
	     has_critID = model.createProperty(ns+"has_critID"); 
	     has_crit_text = model.createProperty(ns+"has_crit_text");
	     has_mutation_phrase = model.createProperty(ns+"has_mutation_phrase");
	     has_mutation_phraseID = model.createProperty(ns+"has_mutation_phraseID");
	     has_mutation_phrase_label = model.createProperty(rdfs_ns+"label");
	     has_mutation_type = model.createProperty(ns+"has_mutation_type");
	     has_trialID = model.createProperty(ns+"has_trialID");
	    
	     
	    // Create resources for the classes in our ontology
	     Trial = model.createResource(ns+"Trial");
	     Eligibility_Criteria = model.createResource(ns+"Eligibility_Criteria"); 
	     Exclusion_Criteria = model.createResource(ns+"Exclusion_Criteria"); 
	     Inclusion_Criteria = model.createResource(ns+"Inclusion_Criteria");
	     Mutation_Phrase = model.createResource(ns+"Mutation_Phrase");
	     Mutation_Type = model.createResource(ns+"Mutation_Type");
	     Mutation_With_Regular_Notation = model.createResource(ns+"Mutation_With_Regular_Notation");
	     Mutation_With_Irregular_Notation = model.createResource(ns+"Mutation_With_Irregular_Notation");
	     Translocation = model.createResource(ns+"Translocation");
	     Point_Mutation = model.createResource(ns+"Point_Mutation");
	     Inversion = model.createResource(ns+"Inversion");
	     Deletion = model.createResource(ns+"Deletion");
	    
	    
	    
	}  
	
}

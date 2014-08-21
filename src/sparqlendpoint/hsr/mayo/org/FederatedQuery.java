package sparqlendpoint.hsr.mayo.org;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class FederatedQuery {
	public static void main(String args[]){

		InputStream in;
		Model myModel =null;
		try {
			in = new FileInputStream(new File("Mutations_In_LinkedCT_abox.owl"));
			// Create an empty in-memory model and populate it from the graph
			 myModel = ModelFactory.createMemModelMaker().createModel("mutationmodel");
			myModel.read(in,null);
			in.close();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		//String cond_label = "Acute Lymphoblastic Leukemia";
//		Scanner scanner = new Scanner(System.in);
//	    String cond_label = scanner.nextLine();


		// Create a new query
		String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
							 "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
							 "PREFIX mutCT: <http://www.semanticweb.org/sambhawa/ontologies/2014/6/Mutations_In_LinkedCT.owl#> \n" +
							 "PREFIX linkedct: <http://data.linkedct.org/vocab/resource/>\n"+
							 
							 "SELECT ?trial ?trial_title ?cond_label ?interv_label ?mut_label ?mut_class ?status\n" +
							 "FROM <http://www.semanticweb.org/sambhawa/ontologies/2014/6/Mutations_In_LinkedCT.owl> \n"+
							 "WHERE {\n" +
							 "      ?mut_phrase mutCT:contains_mutation ?mut_type. \n" +
							 "		?mut_type rdf:type ?mut_class. \n"+
							 "      ?mut_type mutCT:hasMutation_Label ?mut_label.  \n"+
							 "      ?mut_phrase mutCT:is_mutation_phrase_for ?criteria. \n"+
							 "      ?criteria mutCT:belongs_to ?trial. \n" +
							 "      ?trial mutCT:hasLinkedCT_URI \"http://data.linkedct.org/data/trial/nct00932412\".\n"+
							 
							 "		SERVICE <http://4store.mayo.edu:8080/sparql/?soft-limit=-1> { \n"+
							 "		?trial_ct linkedct:id_info_nct_id  \"NCT00932412\". \n"+
							 "		?trial_ct linkedct:trial_condition ?cond.\n"+
							 "		?cond rdfs:label ?cond_label.\n"+
							 "		?trial_ct linkedct:brief_title ?trial_title. \n"+
							 "		?trial_ct linkedct:trial_intervention ?interv. \n"+
							 "		?interv rdfs:label ?interv_label. \n"+
							 "		?trial_ct linkedct:overall_status ?status } \n"+
							 "      }";
		
		//System.out.println(queryString);

		Query query = QueryFactory.create(queryString);

		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, myModel);
		ResultSet results = qe.execSelect();

		// Output query results	
		ResultSetFormatter.out(System.out, results, query);

		// Important - free up resources used running the query
		qe.close();
		
		}
}

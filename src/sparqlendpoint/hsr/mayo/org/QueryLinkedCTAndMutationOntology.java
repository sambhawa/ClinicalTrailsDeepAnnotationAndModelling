package sparqlendpoint.hsr.mayo.org;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class QueryLinkedCTAndMutationOntology {

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

	


	// Create a new query
	String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
						 "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
						 "PREFIX mutCT: <http://www.semanticweb.org/sambhawa/ontologies/2014/6/Mutations_In_LinkedCT.owl#> " +
						 "SELECT ?mut_label ?mut_type ?trial " +
						 "WHERE {" +
						 "      ?mut_phrase mutCT:contains_mutation ?mut_type. " +
						 "      ?mut_type mutCT:hasMutation_Label ?mut_label.   "+
						 "      ?mut_phrase mutCT:is_mutation_phrase_for ?criteria."+
						 "      ?criteria mutCT:belongs_to ?trial. " +
						 "	   ?trial mutCT:hasLinkedCT_URI \"http://data.linkedct.org/data/trial/nct00932412\""+
						 "      }";
	
	System.out.println(queryString);

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

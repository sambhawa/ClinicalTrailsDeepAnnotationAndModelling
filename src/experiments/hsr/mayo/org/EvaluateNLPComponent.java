package experiments.hsr.mayo.org;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.tdb.store.Hash;



public class EvaluateNLPComponent {

	/**
	 * @param 
	 * args[1] = file1
	 * args[2] = file2
	 * args[3] = mutation-category ("s"- structural abnormality, "p" - point mutation, "ph" - phrase-based mutation). 
	 */
	static Map<String,Set<String>> mutationMapGT = new HashMap<String, Set<String>>();
	static Map<String,Set<String>> mutationMapOP = new HashMap<String, Set<String>>();
	
	static int countTP;
	
	public static void main(String[] args) {
		
		System.out.println("This works");
		if(args.length<3){
			System.out.println("Please pass file path for ground truth, file path for output file and mutation category as arguments. Rerun.");
			System.exit(-1);
		}
		
		String file1 = args[0];
		String file2 = args[1];
		System.out.println("GT:"+file1);
		System.out.println("OP:"+file2);
		
		//Step 1: pass Ground truth and system-output paths and find diff for various types of mutation types: structural-abnormality (words), point-mutations(word), 
		//mutation phrases (sentence-level)
		
		findDiffStrAbn(file1,file2);
		
		
		//Step 2: compute precision, recall and f-score for each category of mutation mention - word level and sentence level 
	}

	private static void findDiffStrAbn(String file1, String file2) {
		
		//1. parse each comma-separated files, skip token 2 of every row (don't care about "i" or "e" criteria for now)  and don't add duplicate labels. 
		//2. create a map <trialID, Set<mutation_labels>> for each file. While parsing, ignore white spaces. 
		//3. compute difference (both ways?) between sets corresponding to the same trialID(ignore whitespace)
		//4. output any set which differs for the same trialID
		//5. count matches
		
		int countMutationsINGT = parseFileToCreateMap(file1,mutationMapGT);
		int countMutationsINOP = parseFileToCreateMap(file2,mutationMapOP);
		
		System.out.println("count of mutations in GT: "+countMutationsINGT);
		System.out.println("count of mutations in OP: "+countMutationsINOP);
		
		//compute difference between each set
		int GTminuOP = computeDiffSets(mutationMapGT,mutationMapOP);
		System.out.println("GT - OP= "+GTminuOP);
		
		System.out.println("=======================================================");
		
		int OPminuGT = computeDiffSets(mutationMapOP,mutationMapGT);
		System.out.println("OP - GT= "+OPminuGT);
		
		System.out.println("TP:"+ countTP+" FP: "+OPminuGT+" FN: "+GTminuOP);
		
	}

	private static int computeDiffSets(Map<String, Set<String>> mutationMap1, Map<String, Set<String>> mutationMap2) {
		
		//iterate thru the map such that for each trialID, we extract the two sets, add them together and then remove the map2. 
		//this return how many extra elements are in map1 which are not in map2. Print these extra elements out for the corresponding trialID.
		int countDiff =0 ;
		for (Map.Entry<String, Set<String>> entry : mutationMap1.entrySet())
		{
			
		   if(mutationMap2.containsKey(entry.getKey())){
			   
			   //get the corresponding set for map2
			   Set<String> mutationSet1 = mutationMap1.get(entry.getKey());
			   Set<String> mutationSet2 = mutationMap2.get(entry.getKey());
			   
			   Set<String> diffSet = new HashSet<String>(mutationSet1);
			   diffSet.removeAll(mutationSet2);
			   
			   if(diffSet.size()==0){
				   countTP += mutationSet1.size();
				   //System.out.println("Matched for:"+entry.getKey());
				   }
			   
			   else {
				  countDiff+=diffSet.size();
				  //Iterating over HashSet using Iterator in Java
			        Iterator<String> itr = diffSet.iterator();
			        System.out.print(entry.getKey()+": ");
			        while(itr.hasNext()){
			            System.out.print(itr.next()+",");
			        }
			        System.out.println();
			   }
			   
			   
		   }
			   
		}
		
		return countDiff;
		
	}

	private static int parseFileToCreateMap(String filePath, Map<String, Set<String>> mutationMap) {
		
		int countMutationsINmap = 0;
		
		try {
			
			BufferedReader rdr = new BufferedReader(new FileReader(filePath));
			
			String line;			
			while((line=rdr.readLine())!=null){
				
				//clean up the output
				//line.replaceAll(",,",",").replaceAll(",\r","").replaceAll("( )+", "");
				
				String[] wordArray = line.split(","); //comma is the seperator of tokens in both files
				Set<String> mutationSet;
				if(mutationMap.containsKey(wordArray[0])){ //if map for this trialID already exists
					mutationSet = mutationMap.get(wordArray[0]);
					
				}
				else //if map for this trialID doesn't exist, create new entry
					mutationSet = new HashSet<String>();
				
				for(int i=2;i<wordArray.length;i++){ //we begin at i=2 because we want to skip over the criteria ("i" or "e") token.
					mutationSet.add(wordArray[i]); //note that the HashSet wouldn't insert a duplicate label 
				}
				
				mutationMap.put(wordArray[0],mutationSet);

				countMutationsINmap+=mutationSet.size();
			}
			
		rdr.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return countMutationsINmap;
		
	}
	
	

	
}

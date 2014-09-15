package experiments.hsr.mayo.org;


//pass Ground truth and system-output paths and find diff for various types of mutation types: structural-abnormality (words), point-mutations(word), 
//mutation phrases (sentence-level)
public class FindDiff {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if(args.length<2){
			System.out.println("Please pass file path for ground truth and system output as arguments. Rerun.");
			System.exit(-1);
		}
		
				
	}

}

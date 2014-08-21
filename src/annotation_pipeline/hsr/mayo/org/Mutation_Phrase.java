package annotation_pipeline.hsr.mayo.org;

public class Mutation_Phrase {
	
	String mutationPhraseText;
	enum MutationWithRegularNotation{TRANSLOCATION, INVERSION, DELETION,POINTMUTATION};
	enum MutationWithIrregularNotation{DUPLICATION,TRISOMY,COMPLEX};
	
	
	public Mutation_Phrase(String text, String mutationType){
		
		mutationPhraseText = text;
		if(mutationType.equals("")){
			
		}
	}
	
	

}

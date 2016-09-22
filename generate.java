import java.util.*;
import java.io.*;


class Word {
	String w;
	String spec;
	public Word(String w, String spec){
		w = w;
		spec = spec;
	}
}

class Sequence {
	List<Word> words;
	float p;
}


public class generate {

	public static void main(String[] args) {
		//
	}

	String generate(String startingWord, List<String> sentenceSpec, 
					String searchStrategy, String graph){
		return "sdf";
	}

	Sequence addWordToSequence(Sequence s, String word, String partOfSpeech, float probability) {
		s.words.add(new Word(word, partOfSpeech));
		s.p = s.p * probability;
		return s;
	}

	
}
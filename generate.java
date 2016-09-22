import java.util.*;
import java.io.*;


class Word {
	String w;
	String spec;
	public Word(String w, String spec){
		w = w;
		spec = spec;
	}
    public int hashCode() {
    	return w.hashCode() + spec.hashCode();
    }
    public boolean equals(Object other) {
    	if (other instanceof Word) {
    		Word otherWord = (Word) other;
    		return this.w == otherWord.w && this.spec == otherWord.spec;
    	}

    	return false;
    }
}

class Sequence {
	List<Word> words;
	float p;
	public Sequence(List<Word> words, float p){
		words = words;
		p = p;
	}
}

public class generate {
	// a word map to a two word sequence with probability
	Map<Word, Sequence> input = new HashMap(); 

	public static void main(String[] args) {
		//
	}

	void parse(String input){
		String[] lines = string.split("\n");
		for (int i = 0; i < lines.length; i++){
		    String[] parts = lines.split("\\\\");
		    String[] startWord = parts[0].split("\\");
		    String[] endWord = parts[1].split("\\");
		    Float p = Float.parseFloat(parts[2]);
		    Word startingWord = Word(startWord[0],startWord[1]);
		    Word endingWord = Word(endWord[0],endWord[1]);
		    List<Word> sequenceWords = new List<Word>;
		    sequenceWords.add(startingWord);
		    sequenceWords.add(endingWord);
			this.input.add(startingWord, Sequence(sequenceWords, p))
		}
	}

	String generate(String startingWord, List<String> sentenceSpec, 
					String searchStrategy, String graph){
	    parse(graph);

	    
	}

	Sequence addWordToSequence(Sequence s, String word, String partOfSpeech, float probability) {
		s.words.add(new Word(word, partOfSpeech));
		s.p = s.p * probability;
		return s;
	}

	
}
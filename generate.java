import java.util.*;
import java.io.*;
import java.nio.file.*;

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
	Word lastWord(){
		return this.words.get(this.words.size()-1);
	}
	Sequence sequenceByAppendingWord(Word w, float p){
		List<Word> newWords = new ArrayList<Word>(this.words);
		newWords.add(w);
		return new Sequence(newWords, this.p * p);
	}
}

class SentenceGenerator {
	// a word map to a two word sequence with probability
	Map<Word, List<Sequence>> inputMap = new LinkedHashMap<Word, List<Sequence>>() {
		public List<Sequence> get(Word key) {
		    List<Sequence> list = super.get(key);
		    if (list == null && key instanceof Word)
		       super.put(key, list = new ArrayList<Sequence>());
		    return list;
		}
	};
	ArrayDeque<Sequence> processing = new ArrayDeque<Sequence>();

	public static void main(String[] args) {
		SentenceGenerator sg = new SentenceGenerator();
		try{
			String input = new String(Files.readAllBytes(Paths.get("input")));
			System.out.println(sg.generate("benjamin", Arrays.asList("NNP","VBD","DT", "NN"), "bfs", input));
		}catch(IOException e){
		  e.printStackTrace();
		}
	}

	void parse(String input, String startingWord){
		String[] lines = input.split("\\n");
		for (int i = 0; i < lines.length; i++){
		    String[] parts = lines[i].split("\\\\\\\\");
		    // System.out.println(parts);
		    String[] startWord = parts[0].split("\\\\");
		    String[] endWord = parts[1].split("\\\\");
		    Float p = Float.parseFloat(parts[2]);
		    Word headWord = new Word(startWord[0],startWord[1]);
		    if (this.processing.size() == 0 && headWord.w == startingWord) {
		    	this.processing.add(new Sequence(Arrays.asList(headWord), 1));
		    }
		    Word endingWord = new Word(endWord[0],endWord[1]);
			this.inputMap.get(headWord).add(new Sequence(Arrays.asList(headWord, endingWord), p));
		}
	}

	String generate(String startingWord, List<String> sentenceSpec, 
					String searchStrategy, String graph){
	    parse(graph, startingWord);

	    while( this.processing.getFirst().words.size() != sentenceSpec.size() ){
	    	Sequence current = this.processing.pollFirst();
	    	Word lastWord = current.lastWord();
	    	List<Sequence> nextWords = this.inputMap.get(lastWord);
	    	for(Sequence wordSeq : nextWords) {
		    	this.processing.add(current.sequenceByAppendingWord(wordSeq.lastWord(), wordSeq.p));
			}
	    }

	    Sequence maxSeq = new Sequence(new ArrayList<Word>(), 0);
	    for(Sequence wordSeq : this.processing) {
	    	if (wordSeq.p > maxSeq.p){
				maxSeq = wordSeq;
	    	}
		}

		String rtn = "";
	    for (Word w : maxSeq.words) {
	    	rtn = rtn + " " + w.w;
	    }
	    return rtn.substring(0, rtn.length()-1 );
	}

	Sequence addWordToSequence(Sequence s, String word, String partOfSpeech, float probability) {
		s.words.add(new Word(word, partOfSpeech));
		s.p = s.p * probability;
		return s;
	}

	
}
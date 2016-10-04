import java.util.*;
import java.io.*;
import java.nio.file.*;

class Word {
	String w;
	String spec;
	public Word(String w, String spec){
		this.w = w;this.spec = spec;
	}
}

class Sequence {
	List<Word> words;
	float p;

	public Sequence(List<Word> words, float p){
		this.words = words;
		this.p = p;
	}

	int length(){return this.words.size();}

	Word firstWord(){return this.words.get(0);}

	Word lastWord(){return this.words.get(this.words.size()-1);}

	Sequence sequenceByAppendingWord(Word w, float p){
		List<Word> newWords = new ArrayList<Word>(this.words);
		newWords.add(w);
		return new Sequence(newWords, this.p * p);
	}

	String toStr(){
		String a = "";
		for (Word m : words) a = a + m.w + " ";
		return a.trim();
	}
	Boolean isValidSentence(Sequence s, List<String> sentenceSpec){
		if (s.words.size() != sentenceSpec.size()) return false;
		for (int i = 0; i < s.words.size(); i++){
			if (s.words.get(i).spec != sentenceSpec.get(i)) return false;
		}
		return true;
	}
}

class SentenceGenerator {
	public static void main(String[] args) {
		try{
			String input = new String(Files.readAllBytes(Paths.get("input")));
			System.out.println(generate("hans", Arrays.asList("NNP","VBD","DT", "NN"), "bfs", input));
		}catch(IOException e){
		  e.printStackTrace();
		}
	}

	private static String generate(String startingWord, List<String> sentenceSpec, 
								   String searchStrategy, String graph){
		List<Sequence> input = parse(graph);
		String s = "";
		switch (searchStrategy) {
			case "bfs": s = bfs(startingWord, sentenceSpec, input); break;
			case "dfs": s = dfs(startingWord, sentenceSpec, input); break;
			case "hs": s = hs(startingWord, sentenceSpec, input); break;
		}
		return s;	
	}      

	private static List<Sequence> parse(String input){
		List<Sequence> pi = new ArrayList<Sequence>();

		String[] lines = input.split("\\n");
		for (int i = 0; i < lines.length; i++){
			String[] words = lines[i].split("/");
			Word w1 = new Word(words[0], words[1]);
			Word w2 = new Word(words[3], words[4]);
			List l = new ArrayList();
			l.add(w1);l.add(w2);	
			Sequence s = new Sequence(l,Float.parseFloat(words[6]));
			pi.add(s);
		}
		return pi;
	}

	private static String bfs(String startingWord, List<String> sentenceSpec, List<Sequence> input){
		int visitedNodes = 1;
		Word first = new Word(startingWord, sentenceSpec.get(0));
		Word sec = new Word(startingWord, sentenceSpec.get(0));
		Sequence best = new Sequence(Arrays.asList(first), 0);
		
		Queue<Sequence> q = new LinkedList<Sequence>();
		q.add(new Sequence(Arrays.asList(first), 1));
		while (!q.isEmpty()){
			Sequence current = q.remove();
			if ((current.length() == sentenceSpec.size()) && (current.p >= best.p)) best = current;
			else {
				for (Sequence m : input){
					if (current.lastWord().w.equals(m.firstWord().w) &&
						current.lastWord().spec.equals(m.firstWord().spec) &&
						m.lastWord().spec.equals(sentenceSpec.get(current.length()))) {
							Sequence newSeq = current.sequenceByAppendingWord(m.lastWord(), m.p);
							if (newSeq.length() < sentenceSpec.size()) q.add(newSeq);
							else if (newSeq.p > best.p) best = newSeq;
					}
					visitedNodes++;
				}
			}
		}
		return "\""+best.toStr()+"\" with probability:"+best.p+" total nodes cosidered: "+visitedNodes;
	}

	private static String dfs(String startingWord, List<String> sentenceSpec, List<Sequence> input){
		return "";
	}

	private static String hs(String startingWord, List<String> sentenceSpec, List<Sequence> input){
		return "";
	}


		
		
		
			/*
			// a word map to a two word sequence with probability
			Map<Word, List<Sequence>> inputMap = new LinkedHashMap<Word, List<Sequence>>() {
				public List<Sequence> get(Word key) {
				    List<Sequence> list = super.get(key);
				    if (list == null && key instanceof Word)
		)		       super.put(key, list = new ArrayList<Sequence>());
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
	}*/

}
/*
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

}*/
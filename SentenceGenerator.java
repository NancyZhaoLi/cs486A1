import java.util.*;
import java.io.*;
import java.nio.file.*;

class Word {
	String w;
	String spec;
	int dfsLoc;
	float dfsP;
	public Word(String w, String spec){
		this.w = w;this.spec = spec;this.dfsLoc=0;this.dfsP=1;
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
			System.out.println(generate("benjamin", Arrays.asList("NNP","VBD","DT","NN"), "dfs", input));
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
							visitedNodes++;
					}
				}
			}
		}
		return "\""+best.toStr()+"\" with probability:"+best.p+" total nodes cosidered: "+visitedNodes;
	}

	private static String dfs(String startingWord, List<String> sentenceSpec, List<Sequence> input){
		int visitedNodes = 1;
		Word first = new Word(startingWord, sentenceSpec.get(0));
		Stack<Word> maxS;
		float maxP = 0;
		Stack<Word> s = new Stack<Word>();
		s.add(first);
		
		while (!s.isEmpty()){
			if (s.size() < sentenceSpec.size()){
				Word m = s.pop();
				if (m.dfsLoc < input.size()){
					for (int i = m.dfsLoc; i < input.size(); i++){
						Sequence ith = input.get(i);
						if (m.w.equals(ith.firstWord().w) && m.spec.equals(ith.firstWord().spec) &&
							ith.lastWord().spec.equals(sentenceSpec.get(s.size()+1))){
							m.dfsLoc = i+1;
						    m.dfsP *= ith.p;
							s.add(m);
							s.add(ith.lastWord());
							break;
						}
					}
				}
			}
			else {
				float p = 1;
				for (Word w: s){p*=w.dfsP;}
				//System.out.println(p);
				if (Float.compare(maxP, p) < 0) {
					maxP = p;
					maxS = s;
					System.out.println(maxP);
					for (Word w: s)System.out.println(w.w);
				}
				break;
			}
			//s.pop();
			//for ()
		}
		return "";
	}

	private static String hs(String startingWord, List<String> sentenceSpec, List<Sequence> input){
		return "";
	}
}

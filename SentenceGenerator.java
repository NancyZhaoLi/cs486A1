import java.util.*;
import java.io.*;
import java.nio.file.*;

class Word {
	String w;
	String spec;

	//For DFS
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
		String s = "Failed to generate";
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
		Stack<Word> s = new Stack<Word>();
		Stack<Word> maxS = s;
		float maxP = 0;
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
						    m.dfsP = ith.p;
							s.add(m);
							s.add(ith.lastWord());
							visitedNodes++;
							break;
						}
					}
				}
			}
			else {
				float p = 1;
				for (Word w: s){p*=w.dfsP;}
				
				if (Float.compare(maxP, p) < 0) {
					maxP = p;
					maxS = (Stack<Word>)s.clone();
				}
				s.pop();
			}
		}
		String rt = "\"";
		for (Word w: maxS) rt = rt+w.w+" ";
		return rt+"\" with probability:"+maxP+" total nodes cosidered: "+visitedNodes;
	}

	private class HeuristicEntry {
		
	}
	private class HeuristicComparator implements Comparator<String>{
	    @Override
	    public int compare(String x, String y) {
	        // Assume neither string is null. Real code should
	        // probably be more robust
	        // You could also just return x.length() - y.length(),
	        // which would be more efficient.
	        if (x.length() < y.length())
	        {
	            return -1;
	        }
	        if (x.length() > y.length())
	        {
	            return 1;
	        }
	        return 0;
	    }
	}
	private static String hs(String startingWord, List<String> sentenceSpec, List<Sequence> input){
		int visitedNodes = 1;
		Word first = new Word(startingWord, sentenceSpec.get(0));

		Comparator<String> comparator = new StringLengthComparator();
        PriorityQueue<String> queue = new PriorityQueue<String>(10, comparator);
        queue.add("short");
        queue.add("very long indeed");
        queue.add("medium");
        while (queue.size() != 0) {
            System.out.println(queue.remove());
        }
		return "";
	}
}

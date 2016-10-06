import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.lang.Math;

class Word {
	String w;
	String spec;

	//For DFS
	int dfsLoc;
	double dfsP;

	public Word(String w, String spec){
		this.w = w;
		this.spec = spec;
	}
}

class Sequence {
	List<Word> words;
	double p;

	public Sequence(List<Word> words, double p){
		this.words = words;
		this.p = p;
	}

	int size(){return this.words.size();}

	Word firstWord(){return this.words.get(0);}

	Word lastWord(){return this.words.get(this.words.size()-1);}

	Sequence sequenceByAppendingWord(Word w, double p){
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

class SequenceMap {
	private HashMap<String, Double> maxProbabilityForSingleWord = new HashMap<String, Double>();
	private HashMap<String, ArrayList<Sequence>> data = new HashMap<String, ArrayList<Sequence>>();
	public double maxProbability = 0;

	public ArrayList<Sequence> get(Word key) {
	    ArrayList<Sequence> list = data.get(key.w+key.spec);
	    if (list == null)
	       data.put(key.w+key.spec, list = new ArrayList<Sequence>());
	    return list;
	}

	public Double getMaxProbabilityFor(Word key) {
	    if (maxProbabilityForSingleWord.get(key.w+key.spec) == null){
	    	maxProbabilityForSingleWord.put(key.w+key.spec, Double.valueOf(0));
	    }
	    return maxProbabilityForSingleWord.get(key.w+key.spec);
	}

	public void addSequenceToWord(Word key, Sequence value){
		get(key).add(value);
		if (getMaxProbabilityFor(key) < value.p){
			maxProbabilityForSingleWord.put(key.w+key.spec, Double.valueOf(value.p));
		}
		if (maxProbability < value.p){
			maxProbability = value.p;
		}
	}
}

class SentenceGenerator {
	// a word map to a two word sequence with probability
	SequenceMap inputMap = new SequenceMap();

	public static void main(String[] args) {
		SentenceGenerator sg = new SentenceGenerator();
		try{
			String input = new String(Files.readAllBytes(Paths.get("input")));
			sg.parse(input); // load the input file
			System.out.println(sg.generate("benjamin", Arrays.asList("NNP","VBD","DT","NN"), "bfs"));
			System.out.println(sg.generate("benjamin", Arrays.asList("NNP","VBD","DT","NN"), "dfs"));
			System.out.println(sg.generate("benjamin", Arrays.asList("NNP","VBD","DT","NN"), "hs"));

			System.out.println(sg.generate("a", Arrays.asList("DT","NN","VBD","NNP"), "bfs"));
			System.out.println(sg.generate("a", Arrays.asList("DT","NN","VBD","NNP"), "dfs"));
			System.out.println(sg.generate("a", Arrays.asList("DT","NN","VBD","NNP"), "hs"));

			System.out.println(sg.generate("benjamin", Arrays.asList("NNP","VBD","DT","JJS","NN"), "bfs"));
			System.out.println(sg.generate("benjamin", Arrays.asList("NNP","VBD","DT","JJS","NN"), "dfs"));
			System.out.println(sg.generate("benjamin", Arrays.asList("NNP","VBD","DT","JJS","NN"), "hs"));

			System.out.println(sg.generate("a", Arrays.asList("DT","NN","VBD","NNP","IN","DT","NN"), "bfs"));
			System.out.println(sg.generate("a", Arrays.asList("DT","NN","VBD","NNP","IN","DT","NN"), "dfs"));
			System.out.println(sg.generate("a", Arrays.asList("DT","NN","VBD","NNP","IN","DT","NN"), "hs"));

		}catch(IOException e){
		    e.printStackTrace();
		}
	}

	String generate(String startingWord, List<String> sentenceSpec, String searchStrategy){
		String s = "Failed to generate";
		switch (searchStrategy) {
			case "bfs": s = bfs(startingWord, sentenceSpec); break;
			case "dfs": s = dfs(startingWord, sentenceSpec); break;
			case "hs": s = hs(startingWord, sentenceSpec); break;
		}
		return s;
	}      

	void parse(String input){
		String[] lines = input.split("\\n");
		for (int i = 0; i < lines.length; i++){
			String[] words = lines[i].split("/");
			Word w1 = new Word(words[0], words[1]);
			Word w2 = new Word(words[3], words[4]);
			Sequence s = new Sequence(Arrays.asList(w1, w2), Double.parseDouble(words[6]));
			inputMap.addSequenceToWord(w1, s);
		}
	}

	String bfs(String startingWord, List<String> sentenceSpec){
		int visitedNodes = 1;
		Word first = new Word(startingWord, sentenceSpec.get(0));
		Sequence best = new Sequence(Arrays.asList(first), 0);
		
		Queue<Sequence> q = new LinkedList<Sequence>();
		q.add(new Sequence(Arrays.asList(first), 1));
		while (!q.isEmpty()){
			Sequence current = q.remove();
			if (current.size() == sentenceSpec.size()){
				if (current.p >= best.p){
					best = current;
				}
			} else {
				for (Sequence m : inputMap.get(current.lastWord())) {
					if (m.lastWord().spec.equals(sentenceSpec.get(current.size()))) {
						q.add(current.sequenceByAppendingWord(m.lastWord(), m.p));
						visitedNodes++;
					}
				}
			}
		}
		return "\""+best.toStr()+"\" with probability:"+best.p+" total nodes cosidered: "+visitedNodes;
	}

	String dfs(String startingWord, List<String> sentenceSpec){
		int visitedNodes = 1;
		Word first = new Word(startingWord, sentenceSpec.get(0));
		Sequence best = new Sequence(Arrays.asList(first), 0);

		Stack<Sequence> q = new Stack<Sequence>();
		q.add(new Sequence(Arrays.asList(first), 1));
		while (!q.isEmpty()){
			Sequence current = q.pop();
			if (current.size() == sentenceSpec.size()){
				if (current.p >= best.p){
					best = current;
				}
			} else {
				for (Sequence m : inputMap.get(current.lastWord())) {
					if (m.lastWord().spec.equals(sentenceSpec.get(current.size()))) {
						q.add(current.sequenceByAppendingWord(m.lastWord(), m.p));
						visitedNodes++;
					}
				}
			}
		}
		return "\""+best.toStr()+"\" with probability:"+best.p+" total nodes cosidered: "+visitedNodes;
	}

	double hfn(Sequence x, List<String> sentenceSpec){
		// our heuristic is assuming that the distance to the goal is 
		// (# of nodes) * (the max prabability)  
		double base = x.p;
		if (sentenceSpec.size() >= x.size() + 1) {
			base *= inputMap.getMaxProbabilityFor(x.lastWord());
		}
		if (sentenceSpec.size() >= x.size() + 2) {
			base *= Math.pow(inputMap.maxProbability, ((double)sentenceSpec.size() - x.size() - 1));
		}
		return base;
	}

	String hs(String startingWord, List<String> sentenceSpec){
		int visitedNodes = 1;
		Word first = new Word(startingWord, sentenceSpec.get(0));
		Sequence best = new Sequence(Arrays.asList(first), 0);

		Comparator<Sequence> comparator = new Comparator<Sequence>(){
		    @Override
		    public int compare(Sequence x, Sequence y) {
		    	double hx = hfn(x, sentenceSpec);
		    	double hy = hfn(y, sentenceSpec);
		        return Double.compare(hy,hx);
		    }
		};

        PriorityQueue<Sequence> q = new PriorityQueue<Sequence>(comparator);
		q.add(new Sequence(Arrays.asList(first), 1));
		while (!q.isEmpty()){
			Sequence current = q.remove();
			if (current.size() == sentenceSpec.size()){
				best = current;
				break;
			} else {
				for (Sequence m : inputMap.get(current.lastWord())) {
					if (m.lastWord().spec.equals(sentenceSpec.get(current.size()))) {
						Sequence newSeq = current.sequenceByAppendingWord(m.lastWord(), m.p);
						q.add(newSeq);
						visitedNodes++;
					}
				}
			}
		}
		return "\""+best.toStr()+"\" with probability:"+best.p+" total nodes cosidered: "+visitedNodes;
	}
}

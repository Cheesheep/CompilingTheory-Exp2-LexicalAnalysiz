package part03_test;

import java.util.*;


public class NFA {
	/*
	 * 一个文本文件（格式自定义）
	输出：读入文件中的DFA/NFA，创建对应的DFA/NFA对象，再写回另一文件中。
	
	示例：如果输入是“f(S,a)=A, f(A,b)=B, {B}” //假设默认开始状态是S，
	那么输出是：K={S，A，B}；Σ={a,b}；f(S,a)=A, f(A,b)=B；S；Z={B}

	 * */
	int startState;
	int endState;
	StateCode stateCode;
	static char epsilon = 12345;
	
	// (S, a) -> A
	// (S, a) -> B
	// => (S,a) -> {A, B}
	HashMap<Pair, ArrayList<Integer>> transferMat = new HashMap<>();
	ArrayList<Integer> stateList = new ArrayList<>();
	ArrayList<Character> msgList = new ArrayList<>();
	
	private void addEdge(int srcState, int dstState, char msg) {
		Pair stateMsg = new Pair(srcState, msg);
		ArrayList<Integer> dstStateList = new ArrayList<>();
		if (transferMat.containsKey(stateMsg)) {
			dstStateList = transferMat.get(stateMsg);
		}
		dstStateList.add(dstState);
		transferMat.put(stateMsg, dstStateList);
	}
	private HashMap<Pair, ArrayList<Integer>> collectTransferMat(HashMap<Pair, ArrayList<Integer>> other) {
		Set<Pair> stateMsgKeys = other.keySet();
		for(Pair stateMsg: stateMsgKeys) {
			ArrayList<Integer> stateList = other.get(stateMsg);
			transferMat.put(stateMsg, stateList);
		}
		return transferMat;
	}
	private ArrayList<Integer> collectStateList(ArrayList<Integer> stateList) {
		this.stateList.addAll(stateList);
		return stateList;
	}
	private ArrayList<Character> collectMsgList(ArrayList<Character> msgList) {
		this.msgList.addAll(msgList);
		return this.msgList;
	}
	
	public NFA(StateCode stateCode) {
		this.stateCode = stateCode;
	}
	
	//factory method: 
	public NFA loadFromFunExp(String fileName) {
		
		// TODO:
		//input f(S,a)=A, f(A,b)=B, {B}
		//build the a NFA object including
		// setting the 
		// startState = 'S'
		// endState = B			//{B}
		// and build the transferMat
		// for example, putting f(S,a)=A, f(A,b)=B
		// into the transferMat
		// transferMat.put(new Pair()
		return null;
	}
	private String getMsgList() {
		String tmp = "";
		int i = 0;
		int size = msgList.size();
		for(; i<size; ++i) {
			char cs = (char) msgList.get(i);
			if (i == 0) {
				tmp += cs;
				continue;
			}
			tmp += ", " + cs;
		}
		return tmp;
	}
	private String getTransferList() {
		//output: f(S,a)=A, f(A,b)=B；
		return "";
	}
	public void write(String fileName) {
		
		// K={S，A，B}；Σ={a,b}；f(S,a)=A, f(A,b)=B；S；Z={B}
		String tmp = "K= {";
		tmp += stateCode.getCharStateList(); // S，A，B
		tmp += "}; ";
		
		//Σ={a,b}
		tmp += "Σ={";
		tmp += this.getMsgList();
		tmp += "}; ";
		
		//f(S,a)=A, f(A,b)=B；
		tmp += this.getTransferList();
		
		//S；
		tmp += stateCode.queryCharState(this.startState);
		tmp += "; ";
		
		//Z={B}
		tmp += "Z={";
		tmp += stateCode.queryCharState(this.endState);
		tmp += "}";
		
		//TODO: write tmp into the file with the fileName
	}
	
	
	public NFA loadFromRegularExp(String regularExp) {
		//a(b|aa)*b
		//a o (b + a o a)^* o b
		//  ) | o * ( 
		//operandQueue: [abaa]
		//operatorQueue: [( | o ]  lookAhead=)
		// pop operatorQueue o, pop aa from operandQueue
		// NFA(a) o NFA(a) --> NFA, push NFA into operandQueue
		// operandQueue[a b NFA(aa) ]
		//operatorQueue: [( | ]  lookAhead=)
		//pop | call NFA(b).or(NFA(aa))
		
		Stack<NFA> operandQue = new Stack<NFA>();
		Stack<NFA> 
		
		int i = 0;
		int size = regularExp.length();
		for(; i<size; ++i) {
			char x = regularExp.charAt(i);
			if (Character.isAlphabetic(x)) {
				NFA 
			}
		}
		
	}
	
	private NFA create(char regularExp) {
		//a
		NFA nfa = new NFA(this.stateCode);
		nfa.startState = this.stateCode.getNewStateId(null);
		nfa.endState = this.stateCode.getNewStateId(null);
		nfa.addEdge(nfa.startState, nfa.endState, regularExp);
		return nfa;
	}
	private NFA createEpsilon() {
		//epsilon
		NFA nfa = new NFA(this.stateCode);
		nfa.startState = this.stateCode.getNewStateId(null);
		nfa.endState = nfa.startState;
		return nfa;
	}
	private NFA createEmpty() {
		//empty regular
		NFA nfa = new NFA(this.stateCode);
		nfa.startState = this.stateCode.getNewStateId(null);
		nfa.endState = this.stateCode.getNewStateId(null);
		return nfa;
	}
	
	private NFA connect(NFA other) {
		//r1 o r2
		//this o other
		NFA nfa = new NFA(this.stateCode);
		nfa.startState = this.startState;
		nfa.endState = other.endState;
		nfa.transferMat = this.collectTransferMat(other.transferMat);
		nfa.stateList = this.collectStateList(other.stateList);
		nfa.msgList = this.collectMsgList(other.msgList);
		nfa.addEdge(this.endState, other.startState, NFA.epsilon);
		return nfa;
	}
	
	private NFA or(NFA other) {
		//r1 | r2
		//return NFA(r1) union NFA(r2)
		NFA nfa = new NFA(this.stateCode);
		
		//create new start/end state
		nfa.startState = this.stateCode.getNewStateId(null);
		nfa.endState = this.stateCode.getNewStateId(null);
		
		nfa.transferMat = this.collectTransferMat(other.transferMat);
		nfa.stateList = this.collectStateList(other.stateList);
		nfa.msgList = this.collectMsgList(other.msgList);
		
		//create new epsilon edge
		nfa.addEdge(nfa.startState, this.startState, NFA.epsilon);
		nfa.addEdge(nfa.startState, other.startState, NFA.epsilon);
		nfa.addEdge(other.endState, nfa.endState, NFA.epsilon);
		nfa.addEdge(this.endState, nfa.endState, NFA.epsilon);
		
		return nfa;
	}
	
}

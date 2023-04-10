package part03;

import java.util.*;


public class NFA {
	/*
	 * 一个文本文件（格式自定义）
	输出：读入文件中的DFA/NFA，创建对应的DFA/NFA对象，再写回另一文件中。
	
	示例：如果输入是“f(S,a)=A, f(A,b)=B, {B}” //假设默认开始状态是S，
	那么输出是：K={S，A，B}；Σ={a,b}；f(S,a)=A, f(A,b)=B；S；Z={B}

	 * */
	String RegularExpression;
	int startState;
	int endState;
	char epsilon = 'ε';
	// (S, a) -> A
	// (S, a) -> B
	// => (S,a) -> {A, B}
	StateCode stateCode;
	HashMap<Pair, ArrayList<Integer>> transferMat = new HashMap<>();
	//记录所有产生的状态如A、B等
	ArrayList<Integer> stateList = new ArrayList<>();
	//转移信息如a，b等小写字母或者数字
	ArrayList<Character> msgList = new ArrayList<>();
	
	private void addEdge(int srcState, int dstState, char msg) {
		Pair stateMsg = new Pair(srcState, msg);
		ArrayList<Integer> dstStateList = new ArrayList<>();
		if (transferMat.containsKey(stateMsg)) {//该状态已经有一个指向的状态，例如(S , a) -> A
			dstStateList = transferMat.get(stateMsg);
		}
		dstStateList.add(dstState);
		transferMat.put(stateMsg, dstStateList);
	}
	//将另一个NFA当中的信息全部拷贝加到当前的NFA上
	private HashMap<Pair, ArrayList<Integer>> collectTransferMat(HashMap<Pair, ArrayList<Integer>> transferMat) {
		Set<Pair> stateMsgKeys = transferMat.keySet();
		for(Pair stateMsg: stateMsgKeys) {
			ArrayList<Integer> stateList = transferMat.get(stateMsg);
			this.transferMat.put(stateMsg, stateList);
		}
		return this.transferMat;
	}
	private ArrayList<Integer> collectStateList(ArrayList<Integer> stateList) {
		this.stateList.addAll(stateList);
		return this.stateList;
	}
	private ArrayList<Character> collectMsgList(ArrayList<Character> msgList) {
		this.msgList.addAll(msgList);
		return this.msgList;
	}
	//构造函数
	public NFA(StateCode stateCode) {
		this.stateCode = stateCode;
	}
	
	//第二题的的输出，根据输入获得所有输出:
	public NFA loadFromFunExp(String fileName) {
		
		// TODO:  读入状态转换表，然后输出NFA
		return null;
	}
	public String getStateList() {
		String tmp = "";
		Collections.sort(stateList);
		for(int i = 0;i < stateList.size();i++){
			//需要先将整型转换成数字，表达状态
			tmp += stateCode.queryCharState(stateList.get(i));
			if(i != stateList.size() - 1)
				tmp += ", ";
		}
		return tmp;
	}
	String getMsgList() {
		String tmp = "";
		//对arraylist进行去重
		int i = 0;
		int size = msgList.size();
		for(; i<size; ++i) {
			char cs = msgList.get(i);
			if (i == 0) {
				tmp += cs;
				continue;
			}
			tmp += ", " + cs;
		}
		return tmp;
	}
	String getTransferList() {
		String tmp = "";
		int count = 0;
		//output: f(S,a)=A, f(A,b)=B；
		for (Map.Entry<Pair, ArrayList<Integer>> entry : transferMat.entrySet()) {
			Pair key = entry.getKey();
			ArrayList<Integer> value = entry.getValue();
			tmp += "f" + key.toString() + "= {" + stateCode.getCharStateList(value) + "}, ";
			if(count++ > 4){
				tmp += '\n';
				count = 0;
			}
		}
		return tmp;
	}

	//操作符运算
	private NFA create(char regularExp) {
		//a
		NFA nfa = new NFA(this.stateCode);
		generateNewState(nfa);
		nfa.msgList.add(regularExp);
		nfa.addEdge(nfa.startState, nfa.endState, regularExp);
		return nfa;
	}
	
	private NFA connect(NFA other) {
		//r1 o r2 连接符：‘-’
		//r1.connect(r2)
		NFA nfa = new NFA(this.stateCode);
		nfa.startState = this.startState;
		nfa.endState = other.endState;
		nfa.transferMat = this.collectTransferMat(other.transferMat);
		nfa.stateList = this.collectStateList(other.stateList);
		nfa.msgList = this.collectMsgList(other.msgList);
		nfa.addEdge(this.endState, other.startState, epsilon);
		return nfa;
	}
	
	private NFA or(NFA other) {
		//r1 | r2
		//return NFA(r1) union NFA(r2)
		NFA nfa = new NFA(this.stateCode);
		//create new start/end state
		//分支的起点和终点
		generateNewState(nfa);
		//将新的两个都加进去
		nfa.collectTransferMat(this.collectTransferMat(other.transferMat));
		nfa.collectStateList(this.collectStateList(other.stateList));
		nfa.collectMsgList(this.collectMsgList(other.msgList));
		
		//create new epsilon edge
		nfa.addEdge(nfa.startState, this.startState, epsilon);
		nfa.addEdge(nfa.startState, other.startState, epsilon);
		nfa.addEdge(other.endState, nfa.endState, epsilon);
		nfa.addEdge(this.endState, nfa.endState, epsilon);
		
		return nfa;
	}
	private NFA closure(){
		// a* 闭包操作
		NFA nfa = new NFA(this.stateCode);
		//create new start / end state
		generateNewState(nfa);
		nfa.collectTransferMat(this.transferMat);
		nfa.collectStateList(this.stateList);
		nfa.collectMsgList(this.msgList);
		//增加epsilon边缘
		nfa.addEdge(this.endState,this.startState,epsilon);//回溯
		nfa.addEdge(nfa.startState,this.startState,epsilon);
		nfa.addEdge(this.endState,nfa.endState,epsilon);
		nfa.addEdge(nfa.startState, nfa.endState,epsilon);//允许为空直接跳跃

		return nfa;
	}
	private void generateNewState(NFA nfa){
		nfa.startState = this.stateCode.getNewStateId();
		nfa.endState = this.stateCode.getNewStateId();
		nfa.stateList.add(nfa.startState); //新增状态列表
		nfa.stateList.add(nfa.endState);
	}
	//根据给出的转移式输出到所有NFA相关的信息
	public String generateFile() {
		//output： K={S，A，B}；Σ={a,b}；f(S,a)=A, f(A,b)=B；S；Z={B}
		String tmp = "K= {";
		tmp += this.getStateList(); // S，A，B
		tmp += "}; ";
		//Σ={a,b}
		tmp += "Σ={";
		tmp += this.getMsgList();
		tmp += "}; \n";
		//f(S,a)=A, f(A,b)=B；
		tmp += this.getTransferList() + "; \n";
		//S；
		tmp += stateCode.queryCharState(this.startState);
		tmp += "; ";
		//Z={B}
		tmp += "Z={";
		tmp += stateCode.queryCharState(this.endState);
		tmp += "}";
		return tmp;
	}


	public void loadFromRegularExp(String regularExp) {
		//a(b|aa)*b
		//a o (b + a o a)^* o b
		//使用栈来实现后缀表达式的运算
		RegularExpression = regularExp;
		Stack<NFA> nfaStack = new Stack<>();
		//将正则表达式转换成后缀表达式
		String postfix_RE = ReversePolish.infixToPostfix(regularExp);

		int size = postfix_RE.length();
		for(int i=0; i<size; ++i) {
			char token = postfix_RE.charAt(i);
			if (Character.isLetterOrDigit(token)) {
				//是Unicode则生成一个NFA并且入栈
				nfaStack.push(create(token));
			}else {
				NFA n1 = nfaStack.pop();//将栈顶取出一个元素
				//是操作符，则出栈并且开始运算
				switch (token) {
					case '*' -> {
						nfaStack.push(n1.closure());//闭包运算后再放回去
					}
					case '-' -> {
						NFA n2 = nfaStack.pop();
						nfaStack.push(n2.connect(n1));
					}
					case '|' -> {
						NFA n2 = nfaStack.pop();
						nfaStack.push(n2.or(n1));
					}
				}
			}
		}
		NFA stackTop = nfaStack.peek();
		//先给msgList去重
		Set<Character> set = new HashSet<>(stackTop.msgList);
		stackTop.msgList.clear();
		stackTop.msgList.addAll(set);
		this.transferMat = stackTop.transferMat;
		this.stateList = stackTop.stateList;
		this.msgList = stackTop.msgList;
		this.startState = stackTop.startState;
		this.endState = stackTop.endState;
	}
	//去除空字符连接
	public void removeEpsilon(){
		//用Iterator去遍历就可以解决在循环当中会改变transferMat的值导致某次循环的指针变为空的问题
		Iterator<Map.Entry<Pair,ArrayList<Integer>>> transIterator = transferMat.entrySet().iterator();
		while (transIterator.hasNext()){
			Map.Entry<Pair,ArrayList<Integer>> entry = transIterator.next();
			Pair pair = entry.getKey();
			ArrayList<Integer> dstStates = entry.getValue();
			if(pair.getMsg() == epsilon){
				transIterator.remove(); //去掉带有空转移的状态
				for(Integer dst:dstStates){
					//有可能终止状态或起始状态被替代掉，需要及时更新
					if(dst == endState)
						endState = pair.getState();
					else if(dst == startState)
						startState = pair.getState();
					stateList.remove(dst);
				}
				//重新遍历剩下的元素，并且进行替换
				for(Map.Entry<Pair, ArrayList<Integer>> entry1 :transferMat.entrySet()){
					Pair pair1 = entry1.getKey();
					ArrayList<Integer> dstStates1 = entry1.getValue();
					//源地址若有空转移的目标则替换掉
					pair1.replaceState(dstStates,pair.getState());
					//同理，替换目标地址
					for (int i = 0; i < dstStates1.size(); i++) {
						if(dstStates.contains(dstStates1.get(i)))
							dstStates1.set(i, pair.getState());//替换
					}
					//用set对替换后的目标状态进行去重
					Set<Integer> set = new HashSet<>(dstStates1);
					dstStates1.clear();
					dstStates1.addAll(set);
				}
			}
		}
	}

}

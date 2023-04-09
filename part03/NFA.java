package part03;

import java.util.*;


public class NFA {
	/*
	 * һ���ı��ļ�����ʽ�Զ��壩
	����������ļ��е�DFA/NFA��������Ӧ��DFA/NFA������д����һ�ļ��С�
	
	ʾ������������ǡ�f(S,a)=A, f(A,b)=B, {B}�� //����Ĭ�Ͽ�ʼ״̬��S��
	��ô����ǣ�K={S��A��B}����={a,b}��f(S,a)=A, f(A,b)=B��S��Z={B}

	 * */
	int startState;
	int endState;
	char epsilon = '��';
	
	// (S, a) -> A
	// (S, a) -> B
	// => (S,a) -> {A, B}
	StateCode stateCode;
	HashMap<Pair, ArrayList<Integer>> transferMat = new HashMap<>();
	//��¼���в�����״̬��A��B��
	ArrayList<Integer> stateList = new ArrayList<>();
	//ת����Ϣ��a��b��Сд��ĸ��������
	ArrayList<Character> msgList = new ArrayList<>();
	
	private void addEdge(int srcState, int dstState, char msg) {
		Pair stateMsg = new Pair(srcState, msg);
		ArrayList<Integer> dstStateList = new ArrayList<>();
		if (transferMat.containsKey(stateMsg)) {//��״̬�Ѿ���һ��ָ���״̬������(S , a) -> A
			dstStateList = transferMat.get(stateMsg);
		}
		dstStateList.add(dstState);
		transferMat.put(stateMsg, dstStateList);
	}
	//����һ��NFA���е���Ϣȫ�������ӵ���ǰ��NFA��
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
	//���캯��
	public NFA(StateCode stateCode) {
		this.stateCode = stateCode;
	}
	
	//�ڶ���ĵ�����������������������:
	public NFA loadFromFunExp(String fileName) {
		
		// TODO:  ����״̬ת����Ȼ�����NFA
		return null;
	}
	public String getStateList() {
		String tmp = "";
		Collections.sort(stateList);
		for(int i = 0;i < stateList.size();i++){
			//��Ҫ�Ƚ�����ת�������֣����״̬
			tmp += stateCode.queryCharState(stateList.get(i));
			if(i != stateList.size() - 1)
				tmp += ", ";
		}
		return tmp;
	}
	private String getMsgList() {
		String tmp = "";
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
	private String getTransferList() {
		String tmp = "";
		int count = 0;
		//output: f(S,a)=A, f(A,b)=B��
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

	//����������
	private NFA create(char regularExp) {
		//a
		NFA nfa = new NFA(this.stateCode);
		generateNewState(nfa);
		nfa.msgList.add(regularExp);
		nfa.addEdge(nfa.startState, nfa.endState, regularExp);
		return nfa;
	}
	
	private NFA connect(NFA other) {
		//r1 o r2 ���ӷ�����-��
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
		//��֧�������յ�
		generateNewState(nfa);
		//���µ��������ӽ�ȥ
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
		// a* �հ�����
		NFA nfa = new NFA(this.stateCode);
		//create new start / end state
		generateNewState(nfa);
		nfa.collectTransferMat(this.transferMat);
		nfa.collectStateList(this.stateList);
		nfa.collectMsgList(this.msgList);
		//����epsilon��Ե
		nfa.addEdge(this.endState,this.startState,epsilon);//����
		nfa.addEdge(nfa.startState,this.startState,epsilon);
		nfa.addEdge(this.endState,nfa.endState,epsilon);
		nfa.addEdge(nfa.startState, nfa.endState,epsilon);//����Ϊ��ֱ����Ծ

		return nfa;
	}
	private void generateNewState(NFA nfa){
		nfa.startState = this.stateCode.getNewStateId();
		nfa.endState = this.stateCode.getNewStateId();
		nfa.stateList.add(nfa.startState); //����״̬�б�
		nfa.stateList.add(nfa.endState);
	}
	//���ݸ�����ת��ʽ���������NFA��ص���Ϣ
	public String generateFile() {
		//output�� K={S��A��B}����={a,b}��f(S,a)=A, f(A,b)=B��S��Z={B}
		String tmp = "K= {";
		tmp += this.getStateList(); // S��A��B
		tmp += "}; ";
		//��={a,b}
		tmp += "��={";
		tmp += this.getMsgList();
		tmp += "}; \n";
		//f(S,a)=A, f(A,b)=B��
		tmp += this.getTransferList() + "; \n";
		//S��
		tmp += stateCode.queryCharState(this.startState);
		tmp += "; ";
		//Z={B}
		tmp += "Z={";
		tmp += stateCode.queryCharState(this.endState);
		tmp += "}";
		return tmp;
	}


	public NFA loadFromRegularExp(String regularExp) {
		//a(b|aa)*b
		//a o (b + a o a)^* o b
		//ʹ��ջ��ʵ�ֺ�׺���ʽ������
		Stack<NFA> nfaStack = new Stack<>();
		//��������ʽת���ɺ�׺���ʽ
		String postfix_RE = ReversePolish.infixToPostfix(regularExp);

		int size = postfix_RE.length();
		for(int i=0; i<size; ++i) {
			char token = postfix_RE.charAt(i);
			if (Character.isLetterOrDigit(token)) {
				//��Unicode������һ��NFA������ջ
				nfaStack.push(create(token));
			}else {
				NFA n1 = nfaStack.pop();//��ջ��ȡ��һ��Ԫ��
				//�ǲ����������ջ���ҿ�ʼ����
				switch (token) {
					case '*' -> {
						nfaStack.push(n1.closure());//�հ�������ٷŻ�ȥ
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
		return nfaStack.peek();
	}

}

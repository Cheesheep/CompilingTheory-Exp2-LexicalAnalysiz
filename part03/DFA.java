package part03;

import java.lang.reflect.Array;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @className: DFA
 * @description: TODO �̳�NFA�����������Ļ����϶���һ��״̬��
 * @author: fs956
 * @date: 2023/04/09 17:47
 * @Company: Copyright? [����] by [���߻����]
 **/
public class DFA extends NFA{
    //msgList��Ϊ��ͷ��
    // stateFormat �� key ��Ӧ��״̬��Ϊ��ͷ
    Map< Integer,ArrayList<HashSet<Integer>>> stateFormat = new HashMap<>();
    //�ñ�����ӳ�����ڶ�״̬���²�����״̬
    Map<Integer,HashSet<Integer>> newStateMap = new HashMap<>();
    //���ڴ������״̬���в����ĸ��µ�״̬
    Map<Integer,HashSet<Integer>> newerStateMap = new HashMap<>();
    //���ڴ�������Ѿ������ı���
    Map<Integer,HashSet<Integer>> saveAllStateMap = new HashMap<>();

    //��Ҫ��Ϊһ���������Ʒ���ʹ��
    ArrayList<HashSet<Integer>> theFormat;//DFA���ж����ֹ״̬

    ArrayList<Integer> endState = new ArrayList<>();

    public DFA(NFA nfa) {
        super(nfa.stateCode);
        this.RegularExpression = nfa.RegularExpression;
        this.msgList.addAll(nfa.msgList);//��Ϣ�б���һ�µ�
        this.stateList.addAll(nfa.stateList);
        this.startState = nfa.startState;
        this.endState.add(nfa.endState);
    }
    //��ȷ�����㷨�����µ�״̬��DFA
    //����������״̬�����������DFA��Ϣ
    public void generateStateFormat(NFA nfa){
        //��������ȿ��ٿռ�
        for(Integer state: nfa.stateList){
            theFormat = new ArrayList<>();
            for (int i = 0; i < msgList.size(); i++) {
                theFormat.add(new HashSet<>());
            }
            stateFormat.put(state,theFormat);
        }
        //��ʼ������¼����
        for(Map.Entry<Pair,ArrayList<Integer>> entry:nfa.transferMat.entrySet()){
            Pair pair = entry.getKey();
            ArrayList<Integer> dstStates = entry.getValue();
            theFormat = stateFormat.get(pair.getState()); //��ȡ״̬��λ�ã�Ҳ������һ��,����A
            int index = msgList.indexOf(pair.getMsg());//��ȡ��Ϣ��λ�ã�Ҳ������һ�У�����a
            //ȷ�����к�Ϳ��ԶԱ���������
            HashSet<Integer> nowStates = theFormat.get(index);
            nowStates.addAll(dstStates);//��˼��������A״̬��a�����Ŀ��״̬��¼ΪB
            //�����е�״̬����saveAllStateMap����
            HashSet<Integer> set = new HashSet<>();
            set.add(pair.getState());
            saveAllStateMap.put(pair.getState(), set);
            if(nowStates.size() == 2){//����2��Ϊ�˷�ֹ���������ϵ�ʱ�򣬻��ظ�����
                //������ж��Ŀ����f��B��b��={A��C}����Ҫ���µ�״̬��ӳ��
                Integer newState = stateCode.getNewStateId();
                //�����״̬��������ֹ״̬
                if(nowStates.contains(endState.get(0)))
                    endState.add(newState);
                stateList.add(newState);
                newStateMap.put(newState,nowStates);
            }
        }
        saveAllStateMap.putAll(newStateMap);
        generateNewStateFormat();
        changeOldStates();//�滻����״̬�ı�ʾ
    }
    private void generateNewStateFormat(){
        //���µ�״̬��������ݣ���������ֲ������µ�״̬����Ҫ�ݹ����
        //������ʹ�ñ�������������Ϊ��Ҫ���Ѿ������״̬������������ظ��ݹ������µ�״̬
        for (Map.Entry<Integer, HashSet<Integer>> entry : newStateMap.entrySet()) {
            //�൱��ѭ��ÿһ���µı������
            Integer newState = entry.getKey();
            HashSet<Integer> oldStates = entry.getValue();//�ɵ�״̬
            theFormat = new ArrayList<>();//�����µı��ռ�
            for (int i = 0; i < msgList.size(); i++) {
                theFormat.add(new HashSet<>());
            }
            for (int i = 0; i < theFormat.size(); i++) {
                //���������е����е�Ԫ��
                HashSet<Integer> tmpStates = theFormat.get(i);
                for (Integer oldState : oldStates){
                    //���Ҿ�״̬��Ӧ�ĵ�Ԫ�񣬲������µĵ�Ԫ��
                    ArrayList<HashSet<Integer>> oldFormat = stateFormat.get(oldState);
                    tmpStates.addAll(oldFormat.get(i));
                }
                int key = isNewState(tmpStates);
                //����-1˵����״̬������
                if(key == -1){
                    Integer _newState = stateCode.getNewStateId();
                    //�����״̬��������ֹ״̬
                    if(tmpStates.contains(endState.get(0)))
                        endState.add(_newState);
                    stateList.add(_newState);
                    newerStateMap.put(_newState, tmpStates);
                }else if(key != -2){//������-2��Ϊ�գ���Ϊ����˵�����ظ���״̬
                    //��Ȼ���ظ���״̬�����ָ��֮ǰ�Ѿ���������״̬����
                    //�����Ϳ���ͨ��ָ�뽫��ͬ��Ԫ������ͬ��һ��״̬ͳһ��ʾ
                    theFormat.set(i,saveAllStateMap.get(key));
                }
            }
            stateFormat.put(newState, theFormat);//�����һ�к�����ӵ�����
        }
        newStateMap.clear();//����Ѿ��������״̬
        newStateMap.putAll(newerStateMap);
        saveAllStateMap.putAll(newerStateMap);
        newerStateMap.clear();
        if(!newStateMap.isEmpty()) //����δ�������״̬��ݹ����
            generateNewStateFormat();
    }
    private void changeOldStates(){
        //���ɵ�״̬ȫ��ת�����µ�״̬
        for(Map.Entry<Integer,HashSet<Integer>> entry:saveAllStateMap.entrySet()){
            Integer newState = entry.getKey();
            HashSet<Integer> oldState = entry.getValue();
            oldState.clear();
            oldState.add(newState);
        }
    }
    //�����жϵ�ǰ��״̬�Ƿ��Ѿ�����
    private Integer isNewState(HashSet<Integer> nowStates){
        if(nowStates.isEmpty())
            return -2;
        for (Integer key: saveAllStateMap.keySet()){
            HashSet<Integer> set = saveAllStateMap.get(key);
            if(set.equals(nowStates)) //������ȵ�set
                return key;
        }
        return -1;
    }
    public void addFormatDataToDFA(){
        //��������������ж�ȡ
        for (Map.Entry<Integer,ArrayList<HashSet<Integer>>> entry
                :stateFormat.entrySet()){
            int srcState = entry.getKey();
            theFormat = entry.getValue();
            //����ÿһ���ڵ�ÿ������
            for(int i=0;i < theFormat.size();i++){
                HashSet<Integer> data = theFormat.get(i);
                if(!data.isEmpty()) //�жϸ�״̬Ǩ���Ƿ����
                    transferMat.put(new Pair(srcState,msgList.get(i)),new ArrayList<>(data));
            }
        }
    }
    public boolean parseString(String str){
        int nowState = this.startState;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);//��ǰ�ַ�����
            int msgIndex = this.msgList.indexOf(ch);//�ҵ������е�����
            if(msgIndex == -1) //û��msg��������ַ�ƥ��
                return false;
            //�������ڵĵ�Ԫ�񣬵�ǰ�и��ݵ�ǰ״̬��ȡ
            Iterator<Integer> ite = stateFormat.get(nowState).get(msgIndex).iterator();
            //�жϷ��ʵĵ�Ԫ������Ԫ��
            if(ite.hasNext())
                nowState = ite.next();//���������һ��ѭ��
                // ��Ϊ��ֹ״̬��˵��ƥ��ʧ�ܣ����Զ����޷�ʶ����ַ���
            else return false;
        }
        return endState.contains(nowState);
    }
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
        for(Integer ed:endState){
            tmp += stateCode.queryCharState(ed) + ", ";
        }
        tmp += "}";
        return tmp;
    }
    void showStateFormat(){
        System.out.println("DFA State Format");
        System.out.print("   ");
        //�����ͷ
        for(Character c:msgList){
            System.out.print(c + "  ");
        }
        System.out.println("\n ----------------------");
        for (Map.Entry<Integer,ArrayList<HashSet<Integer>>> entry
                :stateFormat.entrySet()){
            if(saveAllStateMap.containsKey(entry.getKey()))
            {
                HashSet<Integer> oldStates = saveAllStateMap.get(entry.getKey());
                String info = "";
                for(int i:oldStates)
                    info += stateCode.queryCharState(i);
                System.out.print(info + "  ");
            }
            else
                System.out.print(stateCode.queryCharState(entry.getKey())+ "  ");//�����ͷ״̬
            theFormat = entry.getValue();
            for(HashSet<Integer> data:theFormat){
                String info = "";
                for(int i:data)
                    info += stateCode.queryCharState(i);
                System.out.print("{" +
                        info +
                        "} ");
            }
            System.out.println();
        }
        System.out.println("\n---------------------\n");
    }

}

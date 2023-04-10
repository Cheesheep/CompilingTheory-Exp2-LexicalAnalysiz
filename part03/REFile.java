package part03;

import com.sun.jdi.connect.Connector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @className: REFile
 * @description: TODO 对正则表达式的文件进行处理
 * @author: fs956
 * @date: 2023/04/09 1:46
 * @Company: Copyright© [日期] by [作者或个人]
 **/
public class REFile {
    StateCode stateCode = new StateCode();
    NFA nfa = new NFA(stateCode);
    DFA dfa;
    BufferedReader reader;
    FileWriter fw;
    void loadFile(String input,String output) throws Exception {
        FileReader fr = new FileReader(input);
        fw = new FileWriter(output);
        reader = new BufferedReader(fr);
    }
    void getNFA() throws IOException {
        String line = reader.readLine();
        for (int count = 0; line != null; line = reader.readLine(),count++) {
            nfa.loadFromRegularExp(line);//生成对应的NFA
            //生成NFA状态机后输出
            fw.write("the " + count + " NFA: \n");
            fw.write("Reverse to PostFix: " + ReversePolish.infixToPostfix(line) + "\n");
            fw.write(nfa.generateFile() + "\n ----------------\n");
            stateCode = new StateCode();//重置，输出新的NFA
            nfa = new NFA(stateCode);
        }
        fw.close();
    }
    public void getDFA() throws IOException {
        //从NFA转到DFA
        String line = reader.readLine();
        for (int count = 0; line != null; line = reader.readLine(),count++) {
            nfa.loadFromRegularExp(line);//生成对应的NFA
            removeEpsilon();//先去掉空字符
            NFAToDFA();
            //生成DFA状态机后输出
            fw.write("the " + count + " DFA: \n");
            fw.write( nfa.generateFile() +"\n ----------------\n");
            stateCode = new StateCode();//重置，输出新的NFA
            nfa = new NFA(stateCode);
        }
        fw.close();
    }
    private void removeEpsilon(){
        //用Iterator去遍历就可以解决在循环当中会改变transferMat的值导致某次循环的指针变为空的问题
        Iterator<Map.Entry<Pair,ArrayList<Integer>>> transIterator = nfa.transferMat.entrySet().iterator();
        while (transIterator.hasNext()){
            Map.Entry<Pair,ArrayList<Integer>> entry = transIterator.next();
            Pair pair = entry.getKey();
            ArrayList<Integer> dstStates = entry.getValue();
            if(pair.getMsg() == nfa.epsilon){
                transIterator.remove(); //去掉带有空转移的状态
                for(Integer dst:dstStates)
                    nfa.stateList.remove(dst);
                //重新遍历剩下的元素，并且进行替换
                for(Map.Entry<Pair, ArrayList<Integer>> entry1 :nfa.transferMat.entrySet()){
                    Pair pair1 = entry1.getKey();
                    ArrayList<Integer> dstStates1 = entry1.getValue();
                    //替换掉源地址
                    pair1.replaceState(dstStates,pair.getState());
                    //替换目标地址
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
    private void NFAToDFA(){
        //使用确定化算法将NFA转换成DFA
        dfa = new DFA(nfa.stateCode);
        dfa.generateStateFormat(nfa);
        dfa.showStateFormat();
    }
}

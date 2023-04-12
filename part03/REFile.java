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
    Map<String,DFA> dfaList = new HashMap<>();
    BufferedReader reader;
    FileWriter fw;
    void loadFile(String input,String output) throws Exception {
        FileReader fr = new FileReader(input);
        fw = new FileWriter(output);
        reader = new BufferedReader(fr);
    }
    public NFA getNFA() throws IOException {
        String line = reader.readLine();
        for (int count = 0; line != null; line = reader.readLine(),count++) {
            nfa.loadFromRegularExp(line);//生成对应的NFA
            //生成NFA状态机后输出
            fw.write("the " + (count+1) + " NFA: \n");
            fw.write("Reverse to PostFix: " + ReversePolish.infixToPostfix(line) + "\n");
            fw.write(nfa.generateFile() + "\n ----------------\n");
            stateCode = new StateCode();//重置，输出新的NFA
            nfa = new NFA(stateCode);
        }
        fw.close();
        return nfa;
    }
    public Map<String, DFA> getDFA() throws IOException {
        //从NFA转到DFA
        String line = reader.readLine();
        for (int count = 0; line != null; line = reader.readLine(),count++) {
            nfa.loadFromRegularExp(line);//生成对应的NFA
            nfa.removeEpsilon();//先去掉空字符
            NFAToDFA();
            dfaList.put("Type"+(count+1),dfa);
            //生成DFA状态机后输出
            fw.write("the " + (count+1) + " DFA: \n");
            fw.write( dfa.generateFile() +"\n ----------------\n");
            stateCode = new StateCode();//重置，输出新的NFA
            nfa = new NFA(stateCode);
        }
        fw.close();
        return dfaList;
    }

    private void NFAToDFA(){
        //使用确定化算法将NFA转换成DFA
        dfa = new DFA(nfa);
        dfa.generateStateFormat(nfa);//生成状态迁移表
        dfa.showStateFormat();//打印到控制台
        dfa.addFormatDataToDFA();
    }
}

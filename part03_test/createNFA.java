package part03_test;

import java.io.*;

/**
 * @className: createNFA
 * @description: TODO 类描述
 * @author: fs956
 * @date: 2023/04/07 16:46
 * @Company: Copyright© [日期] by [作者或个人]
 **/
public class createNFA {
    StateCode stateCode = new StateCode();
    NFA nfa = new NFA(stateCode);
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
            stateCode = new StateCode();//重置，输出新的NFA
            nfa = new NFA(stateCode);
            //生成NFA状态机后输出
            fw.write("第 " + count + " 个NFA状态机：\n");
            fw.write(nfa.generateFile() + "\n ----------------");
            fw.close();
        }
        fw.close();
    }

    public static void main(String[] args) throws Exception {
        createNFA c = new createNFA();
        c.loadFile("RegularExpression.txt","NFAResult.txt");
        c.getNFA();
    }
}

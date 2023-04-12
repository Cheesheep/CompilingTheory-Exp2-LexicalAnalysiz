package part03;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

/**
 * @className: scanString
 * @description: TODO 类描述
 * @author: fs956
 * @date: 2023/04/09 14:36
 * @Company: Copyright© [日期] by [作者或个人]
 **/
public class scanString {
    Map<String,DFA> dfaList;
    BufferedReader reader;
    public void loadFile(String REFile,String StringFile) throws Exception {
        REFile reFile = new REFile();
        reFile.loadFile(REFile,"part03/DFAResult.txt");
        //获取所有dfa列表
        dfaList = reFile.getDFA();
        FileReader fr = new FileReader(StringFile);
        reader = new BufferedReader(fr);
    }
    public void scan() throws IOException {
        String line = reader.readLine();
        //读取每一行的内容
        for(;line != null;line = reader.readLine()){
            if(line.equals(""))
                continue;
            System.out.print(line);
            getType(line);
            System.out.println();
        }
    }
    private void getType(String line){
        for(Map.Entry<String ,DFA> entry:dfaList.entrySet()){
            DFA dfa = entry.getValue();
            if(dfa.parseString(line))//解析是否成功
            {
                System.out.print("  " + entry.getKey());
            }
        }
    }
    public void printResult(){
        for(Map.Entry<String ,DFA> entry:dfaList.entrySet()){
            DFA dfa = entry.getValue();
            System.out.println(entry.getKey() + ": " + dfa.RegularExpression);
        }
        System.out.println("----------");
    }

    public static void main(String[] args) throws Exception {
        scanString scanString = new scanString();
        scanString.loadFile("part03/RegularExpression.txt","part03/StringFile.txt");
        scanString.printResult();
        scanString.scan();
    }
}

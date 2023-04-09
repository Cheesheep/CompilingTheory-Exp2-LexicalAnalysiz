package part03;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * @className: createDFA
 * @description: TODO 将NFA转换成DFA，这里主要是将空转移去掉
 * @author: fs956
 * @date: 2023/04/09 0:27
 * @Company: Copyright© [日期] by [作者或个人]
 **/
public class createDFA {
    public static void main(String[] args) throws Exception {
        REFile ref = new REFile();
        ref.loadFile("part03/RegularExpression.txt","part03/DFAResult.txt");
        ref.getDFA();
        System.out.println("Run Success! Please check DFAResult.txt !");
    }
}

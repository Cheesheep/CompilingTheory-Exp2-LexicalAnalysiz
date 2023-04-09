package part03;

import java.io.*;

/**
 * @className: createNFA
 * @description: TODO 根据正规式文件生成对应的NFA
 * @author: fs956
 * @date: 2023/04/07 16:46
 * @Company: Copyright© [日期] by [作者或个人]
 **/
public class createNFA {

    public static void main(String[] args) throws Exception {
        REFile ref = new REFile();
        ref.loadFile("part03/RegularExpression.txt","part03/NFAResult.txt");
        ref.getNFA();
        System.out.println("Run Success! Please check NFAResult.txt !");

    }
}

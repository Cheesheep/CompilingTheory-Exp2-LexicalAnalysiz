package part03;

import jdk.jfr.DataAmount;

import java.util.*;

/**
 * @className: createNFA
 * @description: TODO 类描述
 * @author: fs956
 * @date: 2023/03/21 16:56
 * @Company: Copyright© [日期] by [作者或个人]
 **/

class StatePair{
    char BeginState;
    char InputChar; //输入字符
    char EndState;
}

public class createNFA {
    String RegularExpression;
    StringBuilder formerGroup,nowGroup,nextGroup;
    List<String> Groups = new ArrayList<>();
    List<Character> bracketStack = new ArrayList<>();
    createNFA(){

    }
    void getNewState(){}
    /*
    * 对当前正则式进行并集的分类
    * */
    void classifyGroup(String RE){
        int size = RE.length();
        char nowWord,nextWord;
        for (int i = 0; i < size; i++) {
            nowWord = RE.charAt(i);
            nextWord = (i == size - 1) ? '@' : RE.charAt(i + 1);
            if(nowWord == '('){

            }

        }
    }

    
    public static void main(String[] args) {
        String []FS = {
                "(ab)*(a* | B*)(ba)*",
                "a * b",
                "(a|b)*abb",
                "a|ba*",
                "1(0|1)*01"
        };
        createNFA func = new createNFA();
        func.classifyGroup(FS[1]);
    }

}


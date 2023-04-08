package part03_test;

/**
 * @className: InfixToPostfix
 * @description: TODO 类描述
 * @author: fs956
 * @date: 2023/04/07 16:41
 * @Company: Copyright© [日期] by [作者或个人]
 **/
import javax.swing.plaf.nimbus.State;
import java.util.*;

public class ReversePolish {
    static Stack<Character> operatorStack = new Stack<>();
    public static String infixToPostfix(String infix) {
        //优先级高的入栈的时候
        Map<Character, Integer> precedence = new HashMap<>();
        precedence.put(')', 4);
        precedence.put('(', 4);
        precedence.put('|', 2);//连接符
        precedence.put('-', 1);
        precedence.put('*', 0);
        infix = infix.replaceAll(" ","");//先去掉所有空格
        infix = addConnectSymbol(infix);//增加连接符
        StringBuilder postfix = new StringBuilder();

        char nowWord; //nowWord是当前字符，last是当前栈顶的字符
        for (int i = 0; i < infix.length();i++) {
            nowWord = infix.charAt(i);
            if(precedence.containsKey(nowWord)){
                //判断是否是操作符
                if(operatorStack.isEmpty())
                    operatorStack.push(nowWord);
                else if (precedence.get(nowWord) > precedence.get(operatorStack.peek())
                 && nowWord != '(') {//特殊的，如果是右括号则前面的暂不处理
                    //如果下一个操作符的优先级较高，
                    // 则要将当前栈的操作符输出直到空或者当前优先级更高
                    do {
                        postfix.append(operatorStack.pop());
                    } while (!operatorStack.isEmpty() &&
                            precedence.get(nowWord) > precedence.get(operatorStack.peek()));
                    //这里会有可能将（）入栈，是为了方便用算符优先级表示
                    operatorStack.push(nowWord);
                }
                else{
                    operatorStack.push(nowWord);
                }
                if(nowWord == ')')
                {
                    //清除 （ ）
                    operatorStack.pop();
                    operatorStack.pop();
                }

            }else {
                //是字母或者数字则直接输出
                postfix.append(nowWord);
            }
        }
        while (!operatorStack.isEmpty()){
            postfix.append(operatorStack.pop());
        }
        return postfix.toString().replaceAll("[()]","");
    }
    //给连接符一个标记，方便计算
    private static String addConnectSymbol(String infix){
        String ex_infix = "";
        char[] infixArr = infix.toCharArray();
        for (int i = 0; i < infix.length(); i++) {
            char nowWord = infixArr[i];
            char nextWord = (i == infix.length() - 1) ?'@':infixArr[i+1];
            ex_infix += nowWord;
            if(nowWord == '*' || nowWord == ')' || Character.isLetterOrDigit(nowWord)){
                if(Character.isLetterOrDigit(nextWord)
                        ||nextWord == '(')
                    ex_infix += '-';
            }
        }
        System.out.println("addConnect:\n"+ex_infix);
        return ex_infix;
    }

    public static void main(String[] args) {
        String infix[] = {"a (b|a a )* b",
                "a*b(b|(ab)*c)ca",
                "a*b(b|(ab)*c)|ca",
                "a*b(b|(ab)*c|ca)",
                        "a*b",
                "((0|1)(010|11)*) | ((0|101)*)*"
        };
        String _infix = infix[0];
        String postfix = infixToPostfix(_infix);
        System.out.println("ReversePolish:\n"+postfix);  // aa-b|*b-a-
        StateCode st = new StateCode();
        NFA nfa = new NFA(st);
        nfa = nfa.loadFromRegularExp(_infix);
        System.out.println("NFA STATES:\n" + nfa.generateFile());
    }
}


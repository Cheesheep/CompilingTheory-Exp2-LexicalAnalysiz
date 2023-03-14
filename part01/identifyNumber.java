package part01;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

/*
* 1.	输入：一个文本文件（源代码文件）
输出：将源代码中的无符号数识别出来并输出到另一个文件中
示例：如果输入是“123*abc+def/99.2+9.9E+c”，
* 那么输出是：（数字, 123），（其它，*abc+def/），（数字，99.2），（其它，+），（异常，9.9E+c）
说明：其它是非数字打头的字符串；异常是数字打头，但最后却是不符合定义的无符号数。
* */

public class identifyNumber {
    String line = null;
    FileWriter writer;
    private StringBuilder Numbers = new StringBuilder();
    private char NowWord,NextWord;//获得每个句子中的每一个字符
    boolean isException = false; //判断该串数字后面是否有异常

    private Solution solution = new Solution();
    private HashSet<Character> symbol = new HashSet<>();

    identifyNumber(){
        symbol.add('+');
        symbol.add('-');
        symbol.add('.');
        symbol.add('E');
        symbol.add('e');
    }
    void identify(String input,String output) throws Exception{
        BufferedReader reader = new BufferedReader( new FileReader(input));
        this.writer = new FileWriter(output);
        line = reader.readLine();
        for (int count=1;line != null; line = reader.readLine(),count++) {
            writer.write("------第 " + count + " 行-----\n");
            //TODO 识别每一行的内容
            parseToken();

            writer.write("\n");
        }
    }
    void parseToken() throws IOException {
        int size = line.length();
        int state = 0;//自动机开始状态默认为0
        for (int i = 0; i < size; i++) {
            NowWord = line.charAt(i);
            NextWord = (i == size - 1 )? '@':line.charAt(i + 1);
            //先判断数字
            state = solution.getNowState(state,NowWord);
            if(state == -1){

            }
            else if(state == -2){ //没有进入err
            }
            else{
                Numbers.append(NowWord);
            }

            /*
            *
            *
            * */
            if(Character.isDigit(NowWord))
            {
                Numbers.append(NowWord);
                if(!Character.isDigit(NextWord) && !symbol.contains(NextWord)){
                    //下一个不是数字,也不是特殊字符. + - e E
                    if(solution.isUnsignedNumber(Numbers.toString())){
                        //判断前面的是否是正确形式的无符号数
                        this.writer.write("(数字, " + Numbers + " )\n");
                        Numbers  = new StringBuilder();//清空数字存储
                    }
                    else
                        isException = true; //判断为异常
                }
            }
        }
    }


    public static void main(String[] args) throws Exception {
        identifyNumber identifyNumber = new identifyNumber();
        identifyNumber.identify("part01/Test.txt","Output01.txt");
    }
}


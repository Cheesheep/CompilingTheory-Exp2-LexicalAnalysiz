package part01;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
    private StringBuilder Others = new StringBuilder();
    boolean isException = false; //判断该串数字后面是否有异常

    private final Automate automate = new Automate(); //用于获取当前的自动机的状态
    private int state = 1;//存放当前识别到的状态

    void identify(String input,String output) throws Exception{
        //文件读写流
        BufferedReader reader = new BufferedReader( new FileReader(input));
        this.writer = new FileWriter(output);
        line = reader.readLine();
        for (int count=1;line != null; line = reader.readLine(),count++) {
            writer.write("------第 " + count + " 行-----\n");
            //TODO 识别每一行的内容
            parseToken();

            writer.write("\n");
        }
        writer.close();
    }
    void parseToken() throws IOException {
        int size = line.length();
        for (int i = 0; i < size; i++) {
            //获取当前字符和下一个字符
            char nowWord = line.charAt(i);
            char nextWord = (i == size - 1) ? '@' : line.charAt(i + 1);
            //先判断是否进入异常判断
            if(isException){
                Numbers.append(nowWord);
                //如果这个下一个是数字或者是结尾，则作为异常的结束
                if(Character.isDigit(nextWord) || nextWord == '@'){
                    this.writer.write("(异常, " + Numbers + " )\n");
                    isException = false;
                    Numbers = new StringBuilder();
                    state = 1;
                }
            }
            //没有异常的时候先识别数字，或者此时正在识别数字，也就是numbers长度不为0
            else if(Character.isDigit(nowWord) || Numbers.length() != 0){
                Numbers.append(nowWord);
                //TODO 获取下一个字符自动机会去到的状态
                state = automate.getNowState(state, nextWord);
                if(state == -1){//进入其他
                    this.writer.write("(数字, " + Numbers + " )\n");
                    Numbers = new StringBuilder();//清空number
                    state = 1;
                }
                else if(state == -2){ //进入异常
                    isException = true;
                }
            }
            else {
                Others.append(nowWord);
                //如果下一个是数字，或者是结尾，就可以输出其他字符了
                if(Character.isDigit(nextWord) || nextWord == '@'){
                    //下一个是数字则清空
                    this.writer.write("(其他, " + Others + " )\n");
                    Others = new StringBuilder();
                }
            }
        }
    }


    public static void main(String[] args) throws Exception {
        identifyNumber identifyNumber = new identifyNumber();
        identifyNumber.identify("part01/Test.txt","part01/Output01.txt");
        System.out.println("识别成功！");
    }
}


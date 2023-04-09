package part01;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/*
2.	假设：用字符“ABCDEFGHIJK”（大写）分别表示数字0..9和.、E、+、-，那么，字符串“BCLD”表示数字“12E3”=12000；
输入：一个文本文件
输出：将隐藏在文本文件中的有效无符号数识别出来。
示例：如果输入是“BCD*abc+def/JJKC+JKJL+c”，那么输出是：（数字, 123）、（数字，99.2），无效（异常）的无符号数不输出

* */

public class identifyUpperCase {
    String line = null;
    FileWriter writer;
    //解密对应表
    private final HashMap<Character,Character> encryption = new HashMap<>();

    identifyUpperCase(){
        encryption.put('K','.');
        encryption.put('L','E');
        encryption.put('M','+');
        encryption.put('N','-');
    }
    void decode(String input,String output) throws Exception{
        BufferedReader reader = new BufferedReader( new FileReader(input));
        this.writer = new FileWriter(output);
        line = reader.readLine();
        //TODO 识别每一行的内容
        for (int count=1;line != null; line = reader.readLine(),count++) {
            //TODO 将line里面的大写字符全部解密
            upperToInteger();
        }
        writer.close();
    }

    private void upperToInteger() throws IOException {
        char[] arr = line.toCharArray();//先转换成字符数组方便修改

        for (int i = 0; i < line.length(); i++) {
            if(Character.isUpperCase(line.charAt(i))){
                if(arr[i] <= 'J')
                    arr[i] = Integer.toString(arr[i] - 'A').charAt(0);
                else
                    arr[i] = encryption.get(line.charAt(i));
            }
        }
        writer.write(arr);
        writer.write('\n');
    }


    public static void main(String[] args) throws Exception {
        //先进行解码
        identifyUpperCase identifyUpperCase = new identifyUpperCase();
        identifyUpperCase.decode("part01/Test02.txt","part01/Decode.txt");
        //再将解码后的文件正常放入无符号数识别。
        identifyNumber identifyNumber = new identifyNumber();
        identifyNumber.identify("part01/Decode.txt","part01/Output02.txt");
        System.out.println("识别成功！");
    }
}



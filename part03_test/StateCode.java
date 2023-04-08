package part03_test;

import part03.stateCode;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @className: StateCode
 * @description: TODO 类描述
 * @author: fs956
 * @date: 2023/03/28 16:48
 * @Company: Copyright© [日期] by [作者或个人]
 **/
public class StateCode {
    int seq = 0;
    char state = 'A';
    int getNewStateId(){return seq++;}
    String queryCharState(int seqId){//转换成对于的编码
        if(seqId <= 25){

            return (char)(state + seqId) + "";
        }
        else {
            return ((char)(state + (seqId - 25)) + "").repeat(2);
        }
    }
    //返回所有状态拼接的字符串
    public String getCharStateList(ArrayList<Integer> stateList) {
        String tmp = "";
        for(Integer state : stateList){
            tmp += queryCharState(state) + " ,";
        }
        return tmp;
    }

    public static void main(String[] args) {
        StateCode stateCode = new StateCode();
        for (int i = 0; i < 29; i++) {
            System.out.println(stateCode.queryCharState(stateCode.seq));

        }
    }


}

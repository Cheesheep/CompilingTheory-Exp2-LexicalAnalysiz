package part03;

import java.util.ArrayList;

/**
 * @className: Pair
 * @description: TODO 输出状态转移信息对
 * @author: fs956
 * @date: 2023/04/06 21:29
 * @Company: Copyright© [日期] by [作者或个人]
 **/
public class Pair {
    private Integer state;   //(S
    private char msg;		 // 'a'

    public Pair(Integer state, char msg) {
        this.state = state;
        this.msg = msg;
    }

    public int hashCode() {
        return state + (int) msg;
    }
    public boolean equals(Object other) {
        if (this == other) return true;
        Pair p = (Pair) other;
        return this.state == p.state &&
                this.msg == p.msg;
    }

    //for debug
    public String toString() {
        // (S, a)
        StateCode st = new StateCode();
        return "(" + st.queryCharState(this.state) + ", " + this.msg + ")";
    }
    //将空转移当中的目标状态更换掉
    public void replaceState(ArrayList<Integer> arrayList,Integer state) {
        if(arrayList.contains(this.state))
            this.state = state;
    }

    public Integer getState() {
        return state;
    }

    public char getMsg() {
        return msg;
    }
}

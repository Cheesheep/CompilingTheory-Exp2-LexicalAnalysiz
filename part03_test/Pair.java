package part03_test;

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
        return "(" + this.state + ", " + this.msg + ")";
    }
}

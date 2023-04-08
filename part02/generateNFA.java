package part02;

import part03_test.NFA;
import part03_test.StateCode;

import javax.swing.plaf.nimbus.State;
import java.util.Stack;

/**
 * @className: generateNFA
 * @description: 根据状态转移表生成NFA的信息
 * @author: fs956
 * @date: 2023/04/08 9:40
 * @Company: Copyright© [日期] by [作者或个人]
 **/
public class generateNFA {
    public static void main(String[] args) {
        StateCode stateCode = new StateCode();
        NFA nfa = new NFA(stateCode);
        nfa.loadFromFunExp("transferFile");


    }
}

package part03;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @className: DFA
 * @description: TODO 继承NFA，并且在它的基础上多了一个状态表
 * @author: fs956
 * @date: 2023/04/09 17:47
 * @Company: Copyright© [日期] by [作者或个人]
 **/
public class DFA extends NFA{
    //msgList作为行头，
    // stateFormat 的 key 对应的状态作为列头
    Map< Integer,ArrayList<ArrayList<Integer>>> stateFormat = new HashMap<>();
    Map<Integer,ArrayList<Integer>> newStateFormat = new HashMap<>();
    ArrayList<ArrayList<Integer>> theFormat;

    public DFA(StateCode stateCode) {
        super(stateCode);
    }
    //用确定化算法生成新的状态机DFA
    //这里先生成状态表，最后再生成DFA信息
    public void generateStateFormat(NFA nfa){
        this.msgList = nfa.msgList;//信息列表是一致的
        //创建表格先将每行对应的状态定好
        for(Integer state: nfa.stateList){
            theFormat = new ArrayList<>();
            stateFormat.put(state,theFormat);
        }
        //开始给表格记录内容
        for(Map.Entry<Pair,ArrayList<Integer>> entry:nfa.transferMat.entrySet()){
            Pair pair = entry.getKey();
            ArrayList<Integer> dstStates = entry.getValue();
            theFormat = stateFormat.get(pair.getState()); //获取状态的位置，也就是哪一行,例如A
            int index = msgList.indexOf(pair.getMsg());//获取信息的位置，也就是哪一列，例如a
            //确定行列后就可以对表格进行输入
            ArrayList<Integer> nowStates = theFormat.get(index);
            nowStates.addAll(dstStates);//意思就是例如A状态的a输入的目标状态记录为B
            if(nowStates.size() > 1){
                //如果含有多个目标如f（B，b）={A，C}，则要用新的状态来映射
                Integer newState = stateCode.getNewStateId();
                newStateFormat.put(newState,nowStates);
            }
        }
        //TODO ：要找出有两个状态以上的字符，要生成新的状态行

    }
}

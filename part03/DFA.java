package part03;

import java.lang.reflect.Array;
import java.lang.reflect.Parameter;
import java.util.*;

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
    //该表用来映射由于多状态而新产生的状态
    Map<Integer,ArrayList<Integer>> newStateMap = new HashMap<>();
    //主要作为一个变量名称方便使用
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
            for (int i = 0; i < msgList.size(); i++) {
                theFormat.add(new ArrayList<>());
            }
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
            if(nowStates.size() == 2){//等于2是为了防止有两个以上的时候，会重复生成
                //如果含有多个目标如f（B，b）={A，C}，则要用新的状态来映射
                Integer newState = stateCode.getNewStateId();
                stateList.add(newState);
                newStateMap.put(stateCode.getNewStateId(),nowStates);
            }
        }
        //TODO ：要找出有两个状态以上的字符，要生成新的状态行
        for (Map.Entry<Integer,ArrayList<Integer>> entry: newStateMap.entrySet()){
            Integer newState = entry.getKey();
            ArrayList<Integer> oldStates = entry.getValue();//旧的状态
            theFormat = new ArrayList<>();//开拓新的表格空间
            for (int i = 0; i < msgList.size(); i++) {
                theFormat.add(new ArrayList<>());
            }
            for(Integer oldState:oldStates){
                //将旧状态会产生的目标状态迁移过来
                ArrayList<ArrayList<Integer>> oldFormat = stateFormat.get(oldState);
                for(int i=0;i < theFormat.size();i++){
                    ArrayList<Integer> tmp = theFormat.get(i);
                    tmp.addAll(oldFormat.get(i));
                    //if(tmp.size() == )
                }
            }
            stateFormat.put(newState,theFormat);
        }
    }
    void showStateFormat(){
        System.out.println("DFA State Format");
        System.out.print("   ");
        for(Character c:msgList){
            System.out.print(c + "  ");
        }
        System.out.println("\n ----------------------");
        for (Map.Entry<Integer,ArrayList<ArrayList<Integer>>> entry
                :stateFormat.entrySet()){
            if(newStateMap.containsKey(entry.getKey()))
            {
                ArrayList<Integer> oldStates = newStateMap.get(entry.getKey());
                String info = "";
                for(int i:oldStates)
                    info += stateCode.queryCharState(i);
                System.out.print(info + "  ");
            }
            else
                System.out.print(stateCode.queryCharState(entry.getKey())+ "  ");
            theFormat = entry.getValue();
            for(ArrayList<Integer> data:theFormat){
                String info = "";
                for(int i:data)
                    info += stateCode.queryCharState(i);
                System.out.print("{" +
                        info +
                        "} ");
            }
            System.out.println();
        }
        System.out.println("\n---------------------\n");
    }
}

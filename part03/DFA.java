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
    Map< Integer,ArrayList<HashSet<Integer>>> stateFormat = new HashMap<>();
    //该表用来映射由于多状态而新产生的状态
    Map<Integer,HashSet<Integer>> newStateMap = new HashMap<>();
    //用于存放在新状态当中产生的更新的状态
    Map<Integer,HashSet<Integer>> newerStateMap = new HashMap<>();
    //用于存放所有已经产生的变量
    Map<Integer,HashSet<Integer>> saveAllStateMap = new HashMap<>();

    //主要作为一个变量名称方便使用
    ArrayList<HashSet<Integer>> theFormat;

    public DFA(StateCode stateCode) {
        super(stateCode);
    }
    //用确定化算法生成新的状态机DFA
    //这里先生成状态表，最后再生成DFA信息
    public void generateStateFormat(NFA nfa){
        this.msgList.addAll(nfa.msgList);//信息列表是一致的
        this.stateList.addAll(nfa.stateList); //
        //创建表格先将每行对应的状态定好
        for(Integer state: nfa.stateList){
            theFormat = new ArrayList<>();
            for (int i = 0; i < msgList.size(); i++) {
                theFormat.add(new HashSet<>());
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
            HashSet<Integer> nowStates = theFormat.get(index);
            nowStates.addAll(dstStates);//意思就是例如A状态的a输入的目标状态记录为B
            HashSet<Integer> set = new HashSet<>();
            set.add(pair.getState());
            saveAllStateMap.put(pair.getState(), set);
            if(nowStates.size() == 2){//等于2是为了防止有两个以上的时候，会重复生成
                //如果含有多个目标如f（B，b）={A，C}，则要用新的状态来映射
                Integer newState = stateCode.getNewStateId();
                stateList.add(newState);
                newStateMap.put(newState,nowStates);
            }
        }
        saveAllStateMap.putAll(newStateMap);
        generateNewStateFormat();
        changeOldStates();//替换掉旧状态的表示
    }
    private void changeOldStates(){
        //将旧的状态全部转换成新的状态
        for(Map.Entry<Integer,HashSet<Integer>> entry:saveAllStateMap.entrySet()){
            Integer newState = entry.getKey();
            HashSet<Integer> oldState = entry.getValue();
            oldState.clear();
            oldState.add(newState);
        }
    }
    private void generateNewStateFormat(){
        //给新的状态行添加数据，并且如果又产生了新的状态，需要递归调用
        //这里是使用备份来操作，因为需要将已经处理的状态清除掉，避免重复递归生成新的状态
        for (Map.Entry<Integer, HashSet<Integer>> entry : newStateMap.entrySet()) {
            //相当于循环每一行新的表格数据
            Integer newState = entry.getKey();
            HashSet<Integer> oldStates = entry.getValue();//旧的状态
            theFormat = new ArrayList<>();//开拓新的表格空间
            for (int i = 0; i < msgList.size(); i++) {
                theFormat.add(new HashSet<>());
            }
            for (int i = 0; i < theFormat.size(); i++) {
                //遍历该新行的所有单元格
                HashSet<Integer> tmpStates = theFormat.get(i);
                for (Integer oldState : oldStates){
                    //查找旧状态对应的单元格，并给到新的单元格
                    ArrayList<HashSet<Integer>> oldFormat = stateFormat.get(oldState);
                    tmpStates.addAll(oldFormat.get(i));
                }
                int key = isNewState(tmpStates);
                //返回-1说明该状态不存在
                if(key == -1){
                    Integer _newState = stateCode.getNewStateId();
                    stateList.add(_newState);
                    newerStateMap.put(_newState, tmpStates);
                }else if(key != -2){//若返回-2则为空，不为空则说明是重复的状态
                    //TODO 既然是重复的状态则可以指向之前已经存下来的状态变量
                    //TODO 这样就可以通过指针将不同单元格中相同的一组状态统一表示
                    theFormat.set(i,saveAllStateMap.get(key));
                }
            }
            //填好这一行后，将其放入
            stateFormat.put(newState, theFormat);
        }
        newStateMap.clear();//清除已经处理过的状态
        newStateMap.putAll(newerStateMap);
        saveAllStateMap.putAll(newerStateMap);
        newerStateMap.clear();
        if(!newStateMap.isEmpty()) //还有未处理的新状态则递归调用
            generateNewStateFormat();
    }
    //用于判断当前的状态是否已经存在
    private Integer isNewState(HashSet<Integer> nowStates){
        if(nowStates.isEmpty())
            return -2;
        for (Integer key: saveAllStateMap.keySet()){
            HashSet<Integer> set = saveAllStateMap.get(key);
            if(set.equals(nowStates)) //存在相等的set
                return key;
        }
        return -1;
    }
    public void addFormatDataToDFA(){
        //遍历整个表格，逐行读取
        for (Map.Entry<Integer,ArrayList<HashSet<Integer>>> entry
                :stateFormat.entrySet()){
            int srcState = entry.getKey();
            theFormat = entry.getValue();
            //遍历每一行内的每个数据
            for(int i=0;i < theFormat.size();i++){
                HashSet<Integer> data = theFormat.get(i);
                if(!data.isEmpty()){ //判断该状态迁移是否存在
                    transferMat.put(new Pair(srcState,msgList.get(i)),new ArrayList<>(data));
                }
            }
        }
    }
    void showStateFormat(){
        System.out.println("DFA State Format");
        System.out.print("   ");
        //输出列头
        for(Character c:msgList){
            System.out.print(c + "  ");
        }
        System.out.println("\n ----------------------");
        for (Map.Entry<Integer,ArrayList<HashSet<Integer>>> entry
                :stateFormat.entrySet()){
            if(saveAllStateMap.containsKey(entry.getKey()))
            {
                HashSet<Integer> oldStates = saveAllStateMap.get(entry.getKey());
                String info = "";
                for(int i:oldStates)
                    info += stateCode.queryCharState(i);
                System.out.print(info + "  ");
            }
            else
                System.out.print(stateCode.queryCharState(entry.getKey())+ "  ");//输出行头状态
            theFormat = entry.getValue();
            for(HashSet<Integer> data:theFormat){
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

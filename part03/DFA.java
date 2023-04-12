package part03;

import java.lang.reflect.Array;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @className: DFA
 * @description: TODO 继承NFA，并且在它的基础上多了一个状态表
 * @author: fs956
 * @date: 2023/04/09 17:47
 * @Company: Copyright? [日期] by [作者或个人]
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
    ArrayList<HashSet<Integer>> theFormat;//DFA会有多个终止状态

    ArrayList<Integer> endState = new ArrayList<>();

    public DFA(NFA nfa) {
        super(nfa.stateCode);
        this.RegularExpression = nfa.RegularExpression;
        this.msgList.addAll(nfa.msgList);//信息列表是一致的
        this.stateList.addAll(nfa.stateList);
        this.startState = nfa.startState;
        this.endState.add(nfa.endState);
    }
    //用确定化算法生成新的状态机DFA
    //这里先生成状态表，最后再生成DFA信息
    public void generateStateFormat(NFA nfa){
        //创建表格，先开辟空间
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
            //将已有的状态存入saveAllStateMap当中
            HashSet<Integer> set = new HashSet<>();
            set.add(pair.getState());
            saveAllStateMap.put(pair.getState(), set);
            if(nowStates.size() == 2){//等于2是为了防止有两个以上的时候，会重复生成
                //如果含有多个目标如f（B，b）={A，C}，则要用新的状态来映射
                Integer newState = stateCode.getNewStateId();
                //如果旧状态包含了终止状态
                if(nowStates.contains(endState.get(0)))
                    endState.add(newState);
                stateList.add(newState);
                newStateMap.put(newState,nowStates);
            }
        }
        saveAllStateMap.putAll(newStateMap);
        generateNewStateFormat();
        changeOldStates();//替换掉旧状态的表示
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
                    //如果旧状态包含了终止状态
                    if(tmpStates.contains(endState.get(0)))
                        endState.add(_newState);
                    stateList.add(_newState);
                    newerStateMap.put(_newState, tmpStates);
                }else if(key != -2){//若返回-2则为空，不为空则说明是重复的状态
                    //既然是重复的状态则可以指向之前已经存下来的状态变量
                    //这样就可以通过指针将不同单元格中相同的一组状态统一表示
                    theFormat.set(i,saveAllStateMap.get(key));
                }
            }
            stateFormat.put(newState, theFormat);//填好这一行后将其添加到表当中
        }
        newStateMap.clear();//清除已经处理过的状态
        newStateMap.putAll(newerStateMap);
        saveAllStateMap.putAll(newerStateMap);
        newerStateMap.clear();
        if(!newStateMap.isEmpty()) //还有未处理的新状态则递归调用
            generateNewStateFormat();
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
                if(!data.isEmpty()) //判断该状态迁移是否存在
                    transferMat.put(new Pair(srcState,msgList.get(i)),new ArrayList<>(data));
            }
        }
    }
    public boolean parseString(String str){
        int nowState = this.startState;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);//当前字符输入
            int msgIndex = this.msgList.indexOf(ch);//找到所在列的索引
            if(msgIndex == -1) //没有msg和输入的字符匹配
                return false;
            //访问所在的单元格，当前行根据当前状态获取
            Iterator<Integer> ite = stateFormat.get(nowState).get(msgIndex).iterator();
            //判断访问的单元格还有无元素
            if(ite.hasNext())
                nowState = ite.next();//有则进入下一次循环
                // 不为终止状态，说明匹配失败，该自动机无法识别该字符串
            else return false;
        }
        return endState.contains(nowState);
    }
    public String generateFile() {
        //output： K={S，A，B}；Σ={a,b}；f(S,a)=A, f(A,b)=B；S；Z={B}
        String tmp = "K= {";
        tmp += this.getStateList(); // S，A，B
        tmp += "}; ";
        //Σ={a,b}
        tmp += "Σ={";
        tmp += this.getMsgList();
        tmp += "}; \n";
        //f(S,a)=A, f(A,b)=B；
        tmp += this.getTransferList() + "; \n";
        //S；
        tmp += stateCode.queryCharState(this.startState);
        tmp += "; ";
        //Z={B}
        tmp += "Z={";
        for(Integer ed:endState){
            tmp += stateCode.queryCharState(ed) + ", ";
        }
        tmp += "}";
        return tmp;
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

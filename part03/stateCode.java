package part03;

/**
 * @className: stateCode
 * @description: TODO 类描述
 * @author: fs956
 * @date: 2023/04/06 17:35
 * @Company: Copyright© [日期] by [作者或个人]
 **/
public class stateCode {
    int seq = 0;
    char state = 'A';
    String getNewState(){
        seq++;
        if(seq <= 25){

            return (char)(state + seq) + "";
        }
        else {
            return ((char)(state + (seq - 25)) + "").repeat(2);
        }
    }

    public static void main(String[] args) {
        stateCode stateCode = new stateCode();
        for (int i = 0; i < 29; i++) {
            System.out.println(stateCode.getNewState());
            
        }
    }


}


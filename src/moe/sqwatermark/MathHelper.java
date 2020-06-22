package moe.sqwatermark;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MathHelper {

    /**
     * 数组求和
     */
    public static int sum(int[] array) {
        int sum = 0;
        for (int value : array) {
            sum += value;
        }
        return sum;
    }

    /**
     * 按照权重产生随机索引值
     * @param weight 权重(浮点数)
     * @return 索引值
     */
    public static int random(double[] weight) {
        int[] weightInt = new int[weight.length];
        for (int i = 0; i < weight.length; i++) {
            weightInt[i] = (int) Math.ceil(weight[i] * 100);
        }
        return random(weightInt);
    }

    /**
     * 按照权重产生随机索引值
     * @param weight 权重(整数类型)
     * @return 索引值
     */
    public static int random(int[] weight) {
        List<Integer> weightTmp = new ArrayList<>(weight.length + 1);
        weightTmp.add(0);
        Integer sum = 0;
        for(Integer d : weight){
            sum += d;
            weightTmp.add(sum);
        }
        Random random = new Random();
        int rand = random.nextInt(sum);
        int index = 0;
        for(int i = weightTmp.size()-1; i >0; i--){
            if( rand >= weightTmp.get(i)){
                index = i;
                break;
            }
        }
        return index;
    }

}

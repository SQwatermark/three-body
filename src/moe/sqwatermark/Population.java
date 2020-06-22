package moe.sqwatermark;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * 种群
 */
public class Population extends ArrayList<byte[]> {

    //当前代
    public int generation = 1;
    //参数取值精度
    private double delta = 0.001;
    //定义参数取值范围（二进制数的实际取值范围会更大）
    private double[][] boundaryList = {{-50, 50}, {-50, 50}, {-1, 1}, {-1, 1}, {-50, 50}, {-50, 50}, {-1, 1}, {-1, 1}};
    //染色体交叉率
    private double pc = 0.8;
    //染色体变异率
    private double pm = 0.05;
    //染色体每一段的编码长度（每一段对应一个参数）
    public int[] encodeLength;
    //一条染色体的总长度
    public int totalEncodeLength;

    public ArrayList<Double> maxValues;

    Random random = new Random();

    private Population() {
        //使用Population.newPopulation
    }


    /**
     * 种群进化
     *
     * @param generation 代
     */
    public Population evolution(int generation) {
        for (int i = 0; i < generation; i++) {
            this.nextGeneration();
        }
        return this;
    }

    /**
     * 随机生成一个新种群
     *
     * @param size 种群规模
     * @return 种群
     */
    public static Population getNewPopulation(int size) {
        Random random = new Random();
        Population population = new Population();
        population.maxValues = new ArrayList<>();

        Problem.logger.info("此次实验的参数为：" + "\n" + "    delta " + population.delta + "\n" + "    boundaryList " + Arrays.deepToString(population.boundaryList) + "\n" + "    pc " + population.pc + "\n" + "    pm " + population.pm + "\n" + "    种群规模 " + size);

        population.encodeLength = population.getEncodeLength();
        population.totalEncodeLength = MathHelper.sum(population.encodeLength);
        //一条染色体的总长度
        int lengthChromosome = 0;
        for (int length : population.encodeLength) {
            lengthChromosome += length;
        }

        for (int i = 0; i < size; i++) {
            //生成一条染色体
            byte[] chromosome = new byte[lengthChromosome];
            //随机生成染色体的每一位
            for (int i1 = 0; i1 < lengthChromosome; i1++) {
                chromosome[i1] = (byte) random.nextInt(2);
            }
            //将染色体添加到种群
            population.add(i, chromosome);
        }

        //打印初始种群
        Problem.logger.info("初始种群为:");
        for (byte[] bytes : population) {
            String s = Arrays.toString(bytes);
            s = s.replace(" ", "");
            s = s.replace(",", "");
            Problem.logger.info(s);
        }
        return population;
    }

    //种群进入下一代
    public void nextGeneration() {
        double[] fitness = this.getFitness();
        ArrayList<byte[]> nextGeneration = new ArrayList<>();
        generation++; //种群增长一代
        //按权重选择新个体
        for (int i = 0; i < this.size(); i++) {
            nextGeneration.add(i, this.get(MathHelper.random(fitness)).clone());
        }
        this.clear();
        this.addAll(nextGeneration);
        //交叉
        this.crossover();
        //变异
        this.mutation();
        Problem.logger.info("第" + generation + "代种群为");
        for (byte[] bytes : this) {
            String s = Arrays.toString(bytes);
            s = s.replace(" ", "");
            s = s.replace(",", "");
            Problem.logger.info(s);
        }
    }

    /**
     * 获取每条染色体的每一部分的长度
     *
     * @return 编码长度
     */
    public int[] getEncodeLength() {
        int[] lengths = new int[boundaryList.length];
        for (int i = 0; i < boundaryList.length; i++) {
            double min = boundaryList[i][0];
            double max = boundaryList[i][1];
            int length = 0;
            do {
                length++;
            } while ((max - min) / delta > Math.pow(2, length));
            lengths[i] = length;
        }
        return lengths;
    }

    //交叉
    public void crossover() {
        //计算进行交叉的染色体数目
        int number = (int) (this.size() * pc);
        if (number % 2 == 1) number--;
        //打乱染色体顺序
        Collections.shuffle(this);
        int pairs = number / 2;
        for (int i = 0; i < pairs; i++) {
            //随机选择一个交叉点进行交叉
            int crossPointIndex = random.nextInt(this.get(i).length);
            byte crossPoint1 = this.get(i)[crossPointIndex];
            byte crossPoint2 = this.get(i + pairs)[crossPointIndex];
            this.get(i)[crossPointIndex] = crossPoint2;
            this.get(i + pairs)[crossPointIndex] = crossPoint1;
        }
    }

    //变异
    public void mutation() {
        int total = totalEncodeLength * this.size();
        //计算进行变异的基因数目
        int number = (int) (total * pm);
        for (int i = 0; i < number; i++) {
            int index = random.nextInt(total);
            int indexChromosome = index / totalEncodeLength;
            int indexGene = index % totalEncodeLength;
            int k = this.get(indexChromosome)[indexGene];
            this.get(indexChromosome)[indexGene] = (byte) (k == 0 ? 1 : 0);
//            Problem.logger.info(indexChromosome + " " + indexGene);
//            for (byte[] bytes : this) {
//                String s = Arrays.toString(bytes);
//                s = s.replace(" ", "");
//                s = s.replace(",", "");
//                Problem.logger.info(s);
        }
    }

    /**
     * 获取种群中每个个体的适应度
     * @return 适应度
     */
    public double[] getFitness() {
        double[] fitness = new double[this.size()];
        for (int i = 0; i < this.size(); i++) {
            fitness[i] = new World(decode(this.get(i))).getLifetime();
        }
        double max = 0;
        if (Arrays.stream(fitness).max().isPresent()) max = Arrays.stream(fitness).max().getAsDouble();
        Problem.logger.info("第" + generation + "代种群的适应度：" + Arrays.toString(fitness));
        Problem.logger.info("第" + generation + "代种群的最大值：" + max);
        maxValues.add(max);
        return fitness;
    }

    /**
     * 解码，从染色体获取天体参数
     * @param chromosome 染色体
     * @return 参数
     */
    public double[] decode(byte[] chromosome) {
        double[] result = new double[encodeLength.length];
        //已经被分割的染色体长度
        int usedLength = 0;
        //分割染色体，计算每个小块表示的参数
        for (int i = 0; i < encodeLength.length; i++) {
            //从整条染色体截取小块染色体
            byte[] part = new byte[encodeLength[i]];
            if (encodeLength[i] >= 0) System.arraycopy(chromosome, usedLength, part, 0, encodeLength[i]);
            usedLength += encodeLength[i];
            //从染色体小块计算出天体的参数
            int byteToInt = 0; //从小块染色体的二进制值获取整数
            for (int i1 = 0; i1 < part.length; i1++) {
                byteToInt += part[i1] * Math.pow(2, i1);
            }
            result[i] = boundaryList[i][0] + delta * (byteToInt);
        }
        return result;
    }

}

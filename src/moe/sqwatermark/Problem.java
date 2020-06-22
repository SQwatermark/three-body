package moe.sqwatermark;

import org.apache.log4j.Logger;

import java.util.Arrays;

public class Problem {

    public static Logger logger = Logger.getLogger("three-body");

    public static void main(String[] args) {
        Population population = Population.getNewPopulation(20);
        System.out.println(Arrays.toString(population.decode("0011100110011010111100000110010001100011111111100101000000101101110001111100100110011000111000001101011111011011")));
//        logger.info("每一代的最大适应度为" + population.maxValues.toString());
//        logger.info("所有代中最大适应度为" + Collections.max(population.maxValues));
    }

}
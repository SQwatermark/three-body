package moe.sqwatermark;

import org.apache.log4j.Logger;

import java.util.Arrays;

public class Problem {

    public static Logger logger = Logger.getLogger("three-body");

    public static void main(String[] args) {
        Population population = Population.getNewPopulation(0).evolution(0);
        //System.out.println(Arrays.toString(population.decode("10100101000011111111010110001011000100110101110110001000100010100110111101101110111001010111110100010011000010001101011000111011110010111101")));
//        logger.info("每一代的最大适应度为" + population.maxValues.toString());
//        logger.info("所有代中最大适应度为" + Collections.max(population.maxValues));
        logger.info(new World(population.decode("01100010011001100000001000111000011101111100010011001000110001000011010010010101100010111110010010010001111111000100101010000001000100000111"), 1).getLifetime());
    }

}
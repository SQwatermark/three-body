package moe.sqwatermark;

import org.apache.log4j.Logger;

import java.util.Arrays;

public class Problem {

    public static Logger logger = Logger.getLogger("three-body");

    public static void main(String[] args) {
        Population population = Population.getNewPopulation(0).evolution(0);
        logger.info(Arrays.toString(population.decode("10110011011011010010000100001100000001111101011001010011111010000000011111100111110010111100111101010101000101111101011111010001101100000111")));
    }

}
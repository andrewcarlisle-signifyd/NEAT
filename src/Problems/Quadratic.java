package Problems;

import NEAT.Environment;
import NEAT.GenePool;
import NEAT.Genome;
import NEAT.config.NEATConfig;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * defines the quadratic problem
 * the goal here is for the algorithm to produce the values of y = x^2 for -2<=x<=2
 */
public class Quadratic implements Environment {

    @Override
    public void evaluateFitness(ArrayList<Genome> population) {
        // TODO remember to change the NEATConfig INPUTS to 1
        quadratic(population);
    }

    /**
     * defines the problem and the fitness (telling the algo if it is hot or cold)
     *
     * @param population the population of genomes
     */
    private void quadratic(ArrayList<Genome> population) {
        for (Genome genome: population) {
            float fitness = 0;
            genome.setFitness(0);
            for (int i = -2; i < 3; i++) {
                double inputs[] = {i};
                double output[] = genome.evaluateNetwork(inputs);
                double expected = (i * i) / 4.0;
                fitness +=  (1 - Math.abs(expected - output[0]));
            }
            fitness = fitness * fitness;
            genome.setFitness(fitness);
        }

    }

    /**
     * main method for quadratic problem
     * @param arg0
     */
    public static void main (String arg0[]) {
        Quadratic quadratic = new Quadratic();

        GenePool genepool = new GenePool();
        genepool.initializePool();

        Genome bestGenome = new Genome();
        int generation = 0;

        // loop until break conditions are met
        while (true) {
            // attempt problem with the current generation of genomes
            genepool.evaluateFitness(quadratic);
            bestGenome = genepool.getTopGenome();

            // points threshold for correctness is based on number of parameters to evaluate p -> p^2
            // in this case 5 (-2,-2,0,1,2) so 25 is max points, correctness is percent of how close we are
            double correctness = 100 - 100 * (25f - bestGenome.getPoints()) / 25f;

            // print current solution every 100 generations
            if (generation % 100 == 0) {
                printGenomeResults(bestGenome, generation, correctness);
            }

            // if correctness is good enough print result and break
            if (correctness > NEATConfig.CORRECTNESS_THRESHOLD) {
                printGenomeResults(bestGenome, generation, correctness);
                printGenomeExtraResults(bestGenome);
                break;
            }

            // break after 5000 generations if no suitable solution is found
            if (generation > 5000) {
                System.out.println("could not find a good enough solution");
                break;
            }

            // make the new generation
            genepool.breedNewGeneration();
            generation++;
        }
    }

    /**
     * print results for the given genome and generation
     *
     * @param genome the genome
     * @param generation the generation
     */
    private static void printGenomeResults(Genome genome, int generation, double correctness) {
        System.out.print("Generation : " + generation + " - result: (");
        DecimalFormat df = new DecimalFormat("#.###");
        // ask the current genome for its solution and print that
        for (int i = -2; i < 3; i++) {
            double inputs[] = {i};
            double output[] = genome.evaluateNetwork(inputs);
            if (i == 2) {
                System.out.print(df.format(output[0] * 4));
            } else {
                System.out.print(df.format(output[0] * 4) + ", ");
            }
        }
        System.out.println(") - correctness : " + df.format(correctness) + "%");
    }

    /**
     * print extra results for the given genome and generation
     *
     * @param genome the genome
     */
    private static void printGenomeExtraResults(Genome genome) {
        System.out.println("");
        System.out.println("Testing best genome with a new problem -> (-3,-2,-1,0,1,2,3)");
        System.out.print("result: (");
        DecimalFormat df = new DecimalFormat("#.###");
        double fitness = 0;

        // ask the current genome for its solution and print that
        for (int i = -3; i <= 3; i++) {
            double inputs[] = {i};
            double output[] = genome.evaluateNetwork(inputs);
            double expected = (i * i) / 9.0;
            fitness +=  (1 - Math.abs(expected - output[0]));
            if (i == 3) {
                System.out.print(df.format(output[0] * 9));
            } else {
                System.out.print(df.format(output[0] * 9) + ", ");
            }
        }
        double correctness = 100 - 100 * (49f - fitness * fitness) / 49f;
        System.out.println(") - correctness : " + df.format(correctness) + "%");
    }
}

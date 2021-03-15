package Problems;

import NEAT.Environment;
import NEAT.GenePool;
import NEAT.Genome;
import NEAT.config.NEATConfig;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * defines the XOR problem
 * given two binery inputs the alog must produce a result indicating if the input are different
 * eg input 1,1 -> 0
 *    input 0,1 -> 1
 */
public class XOR implements Environment {

    @Override
    public void evaluateFitness(ArrayList<Genome> population) {
        // TODO remember to change the NEATConfig INPUTS to 2
        xor(population);
    }

    /**
     * defines the problem and the fitness (telling the algo if it is hot or cold)
     *
     * @param population the population of genomes
     */
    private void xor(ArrayList<Genome> population) {
        for (Genome gene: population) {
            float fitness = 0;
            gene.setFitness(0);
            for (int i = 0; i < 2; i++)
                for (int j = 0; j < 2; j++) {
                    double inputs[] = {i, j};
                    double output[] = gene.evaluateNetwork(inputs);
                    int expected = i^j;
                    fitness +=  (1 - Math.abs(expected - output[0]));
                }
            fitness = fitness * fitness;

            gene.setFitness(fitness);

        }
    }

    /**
     * main method for xorproblem
     * @param arg0
     */
    public static void main (String arg0[]) {
        XOR quadratic = new XOR();

        GenePool genepool = new GenePool();
        genepool.initializePool();

        Genome bestGenome = new Genome();
        int generation = 0;

        // loop until break conditions are met
        while(true) {
            // attempt problem with the current generation of genomes
            genepool.evaluateFitness(quadratic);
            bestGenome = genepool.getTopGenome();

            // points threshold for correctness is based on number of parameters to evaluate p -> p^2
            // in this case 4 (0,0) (0,1) (1,0) (1,1) so 16 is max points, correctness is percent of how close we are
            double correctness = 100 - 100 * (16f - bestGenome.getPoints()) / 16f;

            // print current solution every 50 generations
            if (generation % 50 == 0) {
                printGenomeResults(bestGenome, generation, correctness);
            }

            // if correctness is good enough print result and break
            if (correctness > NEATConfig.CORRECTNESS_THRESHOLD) {
                printGenomeResults(bestGenome, generation, correctness);
                break;
            }

            // break after 2500 generations if no suitable solution is found
            if (generation > 2500) {
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
        System.out.print("Generation : " + generation + " result: (");
        DecimalFormat df = new DecimalFormat("#.###");
        // ask the current genome for its solution and print that
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++) {
                double inputs[] = {i, j};
                double output[] = genome.evaluateNetwork(inputs);
                int expected = i^j;
                if (i == 1 && j == 1) {
                    System.out.print(df.format(output[0]));
                } else {
                    System.out.print(df.format(output[0]) + ", ");
                }
        }
        System.out.println(") correctness : " + df.format(correctness) + "%");
    }
}

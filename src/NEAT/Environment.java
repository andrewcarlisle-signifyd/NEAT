package NEAT;

import java.util.ArrayList;

/**
 * to be implemented by classes with can assign fitness to genomes
 * is defined by the problem to solve in machine learning
 * is akin to the environment in biological evolution
 */
public interface Environment {

    /**
     * assign fitness to all genomes
     *
     * @param population
     */
    void evaluateFitness(ArrayList<Genome> population);
}

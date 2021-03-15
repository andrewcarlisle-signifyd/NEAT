package NEAT;

import NEAT.config.NEATConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * class which defines properties and function of a species
 * genomes are categorised into species based on similarity (distance function)
 * in machine learning context a species is a "way of thinking"
 */
public class Species implements Comparable{
    private ArrayList<Genome> genomes = new ArrayList<>();
    private double bestFitness = 0;
    private int staleness = 0;
    Random rand = new Random();

    public Species() {
        super();
    }

    public Species(Genome bestGenome) {
        super();
        this.genomes.add(bestGenome);
    }

    /**
     * sets the normalised fitness for all genomes
     */
    public void calculateGenomeNormalisedFitness() {
        for (Genome genome : genomes) {
            genome.setNormalisedFitness(genome.getFitness() / genomes.size());
        }
    }

    /**
     * get the total normalised fitness across all genomes
     *
     * @return total normalised fitness across all genomes
     */
    public double getTotalNormalisedFitness() {
        double totalNormalisedFitness = 0;
        for (Genome genome : genomes) {
            totalNormalisedFitness += genome.getNormalisedFitness();
        }

        return totalNormalisedFitness;
    }

    /**
     * kills off the weaker 50% of genomes
     */
    public void killWeakGenomes() {
        sortGenomes();
        int surviveCount = (int) Math.ceil(genomes.size() / 2f);

        ArrayList<Genome> survivors = new ArrayList<>();
        for (int i = 0; i < surviveCount; i++) {
            survivors.add(new Genome(genomes.get(i)));
        }
        genomes = survivors;
    }

    /**
     * selects random parents and breeds a child from them
     *
     * @return child genome
     */
    public Genome breedChild() {
        Genome child;
        if (rand.nextFloat() < NEATConfig.CROSSOVER_CHANCE) {
            Genome parent1 = genomes.get(rand.nextInt(genomes.size()));
            Genome parent2 = genomes.get(rand.nextInt(genomes.size()));
            child = Genome.breed(parent1, parent2);
        } else {
            child = genomes.get(rand.nextInt(genomes.size()));
        }
        child = new Genome(child);
        child.mutate();
        return child;
    }

    public ArrayList<Genome> getGenomes() {
        return genomes;
    }

    public Genome getBestGenome() {
        sortGenomes();
        return genomes.get(0);
    }

    public double getBestFitness() {
        bestFitness = getBestGenome().getFitness();
        return bestFitness;
    }

    private void  sortGenomes(){
        Collections.sort(genomes, Collections.reverseOrder());
    }

    public void setBestFitness(double bestFitness) {
        this.bestFitness = bestFitness;
    }

    public int getStaleness() {
        return staleness;
    }

    public void setStaleness(int staleness) {
        this.staleness = staleness;
    }

    @Override
    public int compareTo(Object object) {
        Species singleSpecies = (Species) object;
        double best1 = getBestFitness();
        double best2 = singleSpecies.getBestFitness();

        if (best1 == best2) {
            return 0;
        } else if (best1 > best2) {
            return 1;
        } else {
            return -1;
        }
    }
}

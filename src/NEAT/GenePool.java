package NEAT;

import NEAT.config.NEATConfig;

import java.util.ArrayList;
import java.util.Collections;

/**
 * class which defines properties and functions of the gene pool
 */
public class GenePool {
    private ArrayList<Species> species = new ArrayList<>();
    private int generations = 0;
    private double bestFitness;
    private int poolStaleness = 0;

    /**
     * create a new gene pool with new genomes
     */
    public void initializePool() {
        for (int i = 0; i < NEATConfig.POPULATION; i++) {
            addToSpecies(new Genome());
        }
    }

    /**
     * identify which species a genome belongs to and add to it
     * will create a new species if no matching one can be found
     *
     * @param genome the genome
     */
    public void addToSpecies(Genome genome) {
        // loop through existing species and add new genome if it is the same species
        for (Species singleSpecies : species) {
            if (singleSpecies.getGenomes().size() == 0) {
                continue;
            }
            Genome genome0 = singleSpecies.getGenomes().get(0);
            if (Genome.isSameSpecies(genome, genome0)) {
                singleSpecies.getGenomes().add(genome);
                return;
            }
        }
        // make and add as a new species
        Species childSpecies = new Species();
        childSpecies.getGenomes().add(genome);
        species.add(childSpecies);
    }

    /**
     * evaluate the fitness of all genomes
     *
     * @param environment the environment the genomes live in
     */
    public void evaluateFitness(Environment environment){
        ArrayList<Genome> allGenome = new ArrayList<>();
        for(Species singleSpecies: species){
            for(Genome genome: singleSpecies.getGenomes()){
                allGenome.add(genome);
            }
        }
        environment.evaluateFitness(allGenome);
        rankGlobally();
    }

    /**
     * sorts genomes by fitness and assigns points
     */
    private void rankGlobally() {
        ArrayList<Genome> allGenome = new ArrayList<>();

        // get all existing genomes
        for(Species singleSpecies : species){
            for(Genome genome : singleSpecies.getGenomes()){
                allGenome.add(genome);
            }
        }

        // sort existing genomes by points
        Collections.sort(allGenome);

        // set points equal to fitness and rest fitness
        for (int i = 0 ; i < allGenome.size(); i++) {
            allGenome.get(i).setPoints(allGenome.get(i).getFitness());
            allGenome.get(i).setFitness(i);
        }
    }

    /**
     * get the best genome amongst all species
     * @return
     */
    public Genome getTopGenome(){
        ArrayList<Genome> allGenome = new ArrayList<>();

        // get all existing genomes
        for(Species singleSpecies: species){
            for(Genome genome: singleSpecies.getGenomes()){
                allGenome.add(genome);
            }
        }

        // sort existing genomes by fitness
        Collections.sort(allGenome,Collections.reverseOrder());

        // return the best genome
        return allGenome.get(0);
    }

    /**
     * get the total normalised fitness for all species
     *
     * @return the total normalised fitness for all species
     */
    public double calculateGlobalNormalisedFitness() {
        double total = 0;
        for (Species singleSpecies : species) {
            total += singleSpecies.getTotalNormalisedFitness();
        }
        return total;
    }

    public void calculateGenomeNormalisedFitness(){
        for (Species singleSpecies: species) {
            singleSpecies.calculateGenomeNormalisedFitness();
        }
    }

    /**
     * kill off weak 50% of genomes across all species
     */
    public void killWeakGenomesFromSpecies() {
        for(Species singleSpecies: species){
            singleSpecies.killWeakGenomes();
        }
    }

    /**
     * decides if species are not improving for too long and kills them off if so
     */
    public void removeStaleSpecies() {
        ArrayList<Species> survived = new ArrayList<>();

        // if fitness is improving then reset staleness
        if (bestFitness < getBestFitness()){
            poolStaleness = 0;
        }

        // if a species has generated a stronger genome then species staleness is reset
        for(Species singleSpecies: species){
            Genome bestGenome  = singleSpecies.getBestGenome();
            if (bestGenome.getFitness() > singleSpecies.getBestFitness()){
                singleSpecies.setBestFitness(bestGenome.getFitness());
                singleSpecies.setStaleness(0);
            } else {
                // if there is no stronger genome then increment staleness
                singleSpecies.setStaleness(singleSpecies.getStaleness() + 1);
            }

            // if species has not been stale for too long then add to survivors
            if (singleSpecies.getStaleness() < NEATConfig.STALE_SPECIES || singleSpecies.getBestFitness() >= this.getBestFitness()) {
                survived.add(singleSpecies);
            }
        }

        // sort survivors by fitness
        Collections.sort(survived,Collections.reverseOrder());

        // if the total staleness for the entire gene pool is too high then kill off weaker species
        if (poolStaleness > NEATConfig.STALE_POOL) {
            for(int i = survived.size(); i > 1 ; i--)
                survived.remove(i);
        }

        species = survived;
        poolStaleness++;
    }

    /**
     * make the new generation
     *
     * @return the new generation of genomes
     */
    public ArrayList<Genome> breedNewGeneration() {
        ArrayList<Genome> children = new ArrayList<>();
        ArrayList<Species> survived = new ArrayList<>();

        // get fitness
        calculateGenomeNormalisedFitness();
        double globalNormalisedFitness = calculateGlobalNormalisedFitness();
        double carryOver = 0;

        // kill off the weak
        killWeakGenomesFromSpecies();
        removeStaleSpecies();

        for (Species singleSpecies : species) {
            // find how many children the new generation will have for this species
            double newChildren = NEATConfig.POPULATION * (singleSpecies.getTotalNormalisedFitness() / globalNormalisedFitness) ;
            int newChildrenInteger = (int) newChildren;
            carryOver += newChildren - newChildrenInteger;

            if (carryOver > 1) {
                newChildrenInteger++;
                carryOver -= 1;
            }

            if (newChildrenInteger < 1) {
                continue;
            }

            // add the strongest genome to the new generation
            survived.add(new Species(singleSpecies.getBestGenome()));

            // make new child genomes and add to species
            for (int i = 1; i < newChildrenInteger; i++) {
                Genome child = singleSpecies.breedChild();
                children.add(child);
            }
        }

        // add the children to the new generation
        species = survived;
        for (Genome child: children) {
            addToSpecies(child);
        }

        generations++;
        return children;
    }

    /**
     * find the highest values of fitness across all genomes
     *
     * @return the highest values of fitness across all genomes
     */
    public double getBestFitness(){
        double bestFitness = 0;
        Genome bestGenome = null;

        for(Species singleSpecies : species){
            bestGenome = singleSpecies.getBestGenome();
            if (bestGenome.getFitness() > bestFitness) {
                bestFitness = bestGenome.getFitness();
            }
        }
        return bestFitness;
    }
}

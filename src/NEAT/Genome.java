package NEAT;

import NEAT.config.NEATConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import javax.management.RuntimeErrorException;

/**
 * class which defines the properties and functionality of a genome
 */
public class Genome implements Comparable {
    private static Random rand = new Random();
    private double fitness;
    private double points;
    private double normalisedFitness;
    private ArrayList<GeneConnection> geneConnectionList = new ArrayList<>();
    private TreeMap<Integer, GeneNode> nodes = new TreeMap<>();
    private HashMap<MutationKeys, Float> mutationRates = new HashMap<>();

    /**
     * enum to identify mutation constants
     */
    private enum MutationKeys {
        STEPS,
        PERTURB_CHANCE,
        WEIGHT_CHANCE,
        WEIGHT_MUTATION_CHANCE,
        NODE_MUTATION_CHANCE,
        CONNECTION_MUTATION_CHANCE,
        BIAS_CONNECTION_MUTATION_CHANCE,
        DISABLE_MUTATION_CHANCE,
        ENABLE_MUTATION_CHANCE
    }

    public Genome(){
        this.mutationRates.put(MutationKeys.STEPS, NEATConfig.STEPS);
        this.mutationRates.put(MutationKeys.PERTURB_CHANCE, NEATConfig.PERTURB_CHANCE);
        this.mutationRates.put(MutationKeys.WEIGHT_CHANCE,NEATConfig.WEIGHT_CHANCE);
        this.mutationRates.put(MutationKeys.WEIGHT_MUTATION_CHANCE, NEATConfig.WEIGHT_MUTATION_CHANCE);
        this.mutationRates.put(MutationKeys.NODE_MUTATION_CHANCE , NEATConfig.NODE_MUTATION_CHANCE);
        this.mutationRates.put(MutationKeys.CONNECTION_MUTATION_CHANCE , NEATConfig.CONNECTION_MUTATION_CHANCE);
        this.mutationRates.put(MutationKeys.BIAS_CONNECTION_MUTATION_CHANCE , NEATConfig.BIAS_CONNECTION_MUTATION_CHANCE);
        this.mutationRates.put(MutationKeys.DISABLE_MUTATION_CHANCE , NEATConfig.DISABLE_MUTATION_CHANCE);
        this.mutationRates.put(MutationKeys.ENABLE_MUTATION_CHANCE , NEATConfig.ENABLE_MUTATION_CHANCE);
    }

    /**
     * copy constructor
     *
     * @param child the child genome
     */
    public Genome(Genome child) {
        for (GeneConnection connection : child.geneConnectionList){
            this.geneConnectionList.add(new GeneConnection(connection));
        }
        this.fitness = child.fitness;
        this.normalisedFitness = child.normalisedFitness;
        this.mutationRates = (HashMap<MutationKeys, Float>) child.mutationRates.clone();
    }

    /**
     * breed a child from given parents
     *
     * @param parent1 parent 1 genome
     * @param parent2 parent 2 genome
     * @return child genome
     */
    public static Genome breed(Genome parent1, Genome parent2) {
        Genome child = new Genome();

        // logic assumes parent1 is the fitter
        // if parent2 is fitter than parent1 then switch parents
        if (parent1.fitness < parent2.fitness) {
            Genome temp = parent1;
            parent1 = parent2;
            parent2 = temp;
        }

        // get the geneMap for each parent
        TreeMap<Integer, GeneConnection> geneMap1 = new TreeMap<>();
        TreeMap<Integer, GeneConnection> geneMap2 = new TreeMap<>();

        for(GeneConnection connection: parent1.geneConnectionList){
            geneMap1.put(connection.getInnovation(), connection);
        }

        for(GeneConnection connection: parent2.geneConnectionList){
            geneMap2.put(connection.getInnovation(), connection);
        }

        // get all unique innovations between parents
        Set<Integer> parentInnovations1 = geneMap1.keySet();
        Set<Integer> parentInnovations2 = geneMap2.keySet();
        Set<Integer> allInnovations = new HashSet<Integer>(parentInnovations1);
        allInnovations.addAll(parentInnovations2);

        // loop through innovations and decide if child should inherit them
        for (int innovation : allInnovations) {
            GeneConnection trait;

            // if both parents have the same innovation the child inherits from one of them
            if (geneMap1.containsKey(innovation) && geneMap2.containsKey(innovation)) {
                if (rand.nextBoolean()) {
                    trait = new GeneConnection(geneMap1.get(innovation));
                } else {
                    trait = new GeneConnection(geneMap2.get(innovation));
                }

                // if trait is recessive for one parent and not the other, child has 75% chance of recessive
                if ((geneMap1.get(innovation).isEnabled() != geneMap2.get(innovation).isEnabled())) {
                    if (rand.nextFloat() < 0.75f) {
                        trait.setEnabled(false);
                    } else {
                        trait.setEnabled(true);
                    }
                }

                // if only one parent has the innovation and parents are equally fit, child inherits
            } else if (parent1.getFitness() == parent2.getFitness()) {
                if (geneMap1.containsKey(innovation)) {
                    trait = geneMap1.get(innovation);
                } else {
                    trait = geneMap2.get(innovation);
                }

                if (rand.nextBoolean()) {
                    continue;
                }

                // if parents are not equally fit the child gets from parent 1, assumed to be the fittest
            } else {
                trait = geneMap1.get(innovation);
            }

            child.geneConnectionList.add(trait);
        }

        return child;
    }

    /**
     * decide if two genomes belong to the same species
     *
     * @param genome1 genome 1
     * @param genome2 genome 2
     * @return boolean indicating if the genomes are of the same species
     */
    public static boolean isSameSpecies(Genome genome1, Genome genome2){
        int matching = 0;
        int disjoint = 0;
        int excess = 0;
        float weight = 0;
        int lowMaxInnovation;
        float delta = 0;

        // populate geneMap for each genome
        TreeMap<Integer, GeneConnection> geneMap1 = new TreeMap<>();
        TreeMap<Integer, GeneConnection> geneMap2 = new TreeMap<>();

        for (GeneConnection connection: genome1.geneConnectionList) {
            geneMap1.put(connection.getInnovation(), connection);
        }

        for (GeneConnection connection: genome2.geneConnectionList) {
            geneMap2.put(connection.getInnovation(), connection);
        }

        // find the lowest max innovation
        if (geneMap1.isEmpty() || geneMap2.isEmpty()) {
            lowMaxInnovation = 0;
        } else {
            lowMaxInnovation = Math.min(geneMap1.lastKey(), geneMap2.lastKey());
        }

        // find all innovations between genomes
        Set<Integer> genomeInnovation1 = geneMap1.keySet();
        Set<Integer> genomeInnovation2 = geneMap2.keySet();
        Set<Integer> allInnovations = new HashSet<Integer>(genomeInnovation1);
        allInnovations.addAll(genomeInnovation2);

        // loop through innovations and check if both parents have the same traits
        for (int innovation : allInnovations) {
            // if both parents have the same innovation then increment the matching count and weight difference
            if (geneMap1.containsKey(innovation) && geneMap2.containsKey(innovation)) {
                matching ++;
                weight += Math.abs(geneMap1.get(innovation).getWeight() - geneMap2.get(innovation).getWeight());
            } else {
                // for genes that belong to just one parent check if they are excess or disjoint genes
                if (innovation < lowMaxInnovation) {
                    disjoint++;
                } else {
                    excess++;
                }
            }

        }

        int total = matching + disjoint + excess ;

        // calculate the distance between the two genomes
        if(total > 0) {
            delta = (NEATConfig.EXCESS_COEFFICENT * excess + NEATConfig.DISJOINT_COEFFICENT * disjoint) / total + (NEATConfig.WEIGHT_COEFFICENT * weight) / matching;
        }

        // decide based on distance and config if the two genomes are the same species
        return delta < NEATConfig.COMPATIBILITY_THRESHOLD;
    }

    /**
     * entry point for the thinking - method that actually takes the inputs and returns the output
     *
     * @param inputs the inputs to the process
     * @return outputs for the given input
     */
    public double[] evaluateNetwork(double[] inputs) {
        double output[] = new double[NEATConfig.OUTPUTS];
        generateNetwork();

        // set the inputs as the nodes
        for (int i = 0; i < NEATConfig.INPUTS; i++) {
            nodes.get(i).setValue(inputs[i]);
        }

        for (Map.Entry<Integer, GeneNode> mapEntry : nodes.entrySet()) {
            float sum = 0;
            int key = mapEntry.getKey();
            GeneNode node = mapEntry.getValue();

            if (key > NEATConfig.INPUTS) {
                for (GeneConnection connection : node.getIncomingConnection()) {
                    if (connection.isEnabled()) {
                        sum += nodes.get(connection.getIntoNode()).getValue() * connection.getWeight();
                    }
                }
                node.setValue(sigmoid(sum));
            }
        }

        for (int i = 0; i < NEATConfig.OUTPUTS; i++) {
            output[i] = nodes.get(NEATConfig.INPUTS + NEATConfig.HIDDEN_NODES + i).getValue();
        }
        return output;
    }

    /**
     * generate a neural network of nodes
     */
    private void generateNetwork() {
        nodes.clear();

        //  loop through input layer and add nodes
        for (int i = 0; i < NEATConfig.INPUTS; i++) {
            nodes.put(i, new GeneNode(0));
        }
        nodes.put(NEATConfig.INPUTS, new GeneNode(1));

        // output layer
        for (int i = NEATConfig.INPUTS + NEATConfig.HIDDEN_NODES; i < NEATConfig.INPUTS + NEATConfig.HIDDEN_NODES + NEATConfig.OUTPUTS; i++) {
            nodes.put(i, new GeneNode(0));
        }

        // hidden layer - add any nodes required by the current connections
        for (GeneConnection connection : geneConnectionList) {
            if (!nodes.containsKey(connection.getIntoNode())) {
                nodes.put(connection.getIntoNode(), new GeneNode(0));
            }
            if (!nodes.containsKey(connection.getOutNode())) {
                nodes.put(connection.getOutNode(), new GeneNode(0));
            }
            nodes.get(connection.getOutNode()).getIncomingConnection().add(connection);
        }
    }

    /**
     * calculate the sigmoid function ->  1 / (1 + exp(-x))
     *
     * @param x value
     * @return the sigmoid of x
     */
    private float sigmoid(float x) {
        return (float) (1 / (1 + Math.exp(-4.9 * x)));
    }

    /**
     * perform mutations
     */
    public void mutate() {
        // mutate the mutation rates by 5%
        for (Map.Entry<MutationKeys, Float> entry : mutationRates.entrySet()) {
            if (rand.nextBoolean()) {
                mutationRates.put(entry.getKey(), 0.95f * entry.getValue());
            } else {
                mutationRates.put(entry.getKey(), 1.05263f * entry.getValue());
            }
        }

        // mutate the actual geneConnections
        if (rand.nextFloat() <= mutationRates.get(MutationKeys.WEIGHT_MUTATION_CHANCE)) {
            mutateWeight();
        }
        if (rand.nextFloat() <= mutationRates.get(MutationKeys.CONNECTION_MUTATION_CHANCE)) {
            mutateAddConnection(false);
        }
        if (rand.nextFloat() <= mutationRates.get(MutationKeys.BIAS_CONNECTION_MUTATION_CHANCE)) {
            mutateAddConnection(true);
        }
        if (rand.nextFloat() <= mutationRates.get(MutationKeys.NODE_MUTATION_CHANCE)) {
            mutateAddNode();
        }
        if (rand.nextFloat() <= mutationRates.get(MutationKeys.DISABLE_MUTATION_CHANCE)) {
            disableMutate();
        }
        if (rand.nextFloat() <= mutationRates.get(MutationKeys.ENABLE_MUTATION_CHANCE)) {
            enableMutate();
        }
    }

    /**
     * mutate the weight on the connections
     */
    private void mutateWeight() {
        for (GeneConnection connection : geneConnectionList) {
            if (rand.nextFloat() < NEATConfig.WEIGHT_CHANCE) {
                if (rand.nextFloat() < NEATConfig.PERTURB_CHANCE) {
                    // perform a perturbation on the existing weight - add a random amount to it
                    connection.setWeight(connection.getWeight() + (2 * rand.nextFloat() - 1) * NEATConfig.STEPS);
                } else {
                    // set a new random weight
                    connection.setWeight(4 * rand.nextFloat() - 2);
                }
            }
        }
    }

    /**
     * mutation which can add a new connection
     *
     * @param forceBais
     */
    private void mutateAddConnection(boolean forceBais) {
        generateNetwork();

        // define random parameters
        int i = 0;
        int j = 0;
        int random2 = rand.nextInt(nodes.size() - NEATConfig.INPUTS - 1) + NEATConfig.INPUTS + 1;
        int random1 = rand.nextInt(nodes.size());
        if (forceBais) {
            random1 = NEATConfig.INPUTS;
        }
        int node1 = -1;
        int node2 = -1;

        // select 2 random nodes
        for (int k : nodes.keySet()) {
            if (random1 == i) {
                node1 = k;
                break;
            }
            i++;
        }

        for (int k : nodes.keySet()) {
            if (random2 == j) {
                node2 = k;
                break;
            }
            j++;
        }

        if (node1 >= node2) {
            return;
        }

        for (GeneConnection connection : nodes.get(node2).getIncomingConnection()) {
            if (connection.getIntoNode() == node1)
                return;
        }

        if (node1 < 0 || node2 < 0) {
            throw new RuntimeErrorException(null);
        }

        // add a connection between the two random nodes
        geneConnectionList.add(new GeneConnection(node1, node2, InnovationCounter.newInnovation(), 4 * rand.nextFloat() - 2, true));

    }

    /**
     * mutation which picks a random connection and can add a new node in the middle of it
     */
    private void mutateAddNode() {
        generateNetwork();
        if (geneConnectionList.size() > 0) {
            int timeoutCount = 0;
            GeneConnection randomConnection = geneConnectionList.get(rand.nextInt(geneConnectionList.size()));

            // loop until we find an enabled connection or we run out of hidden nodes
            while (!randomConnection.isEnabled()) {
                randomConnection = geneConnectionList.get(rand.nextInt(geneConnectionList.size()));
                timeoutCount++;
                if (timeoutCount > NEATConfig.HIDDEN_NODES) {
                    return;
                }
            }

            // disable the random connection and add a new connection
            // essentially adds a new node in the middle of the existing connection
            int nextNode = nodes.size() - NEATConfig.OUTPUTS;
            randomConnection.setEnabled(false);
            geneConnectionList.add(new GeneConnection(randomConnection.getIntoNode(), nextNode, InnovationCounter.newInnovation(), 1, true));
            geneConnectionList.add(new GeneConnection(nextNode, randomConnection.getOutNode(), InnovationCounter.newInnovation(), randomConnection.getWeight(), true));
        }
    }

    /**
     * mutation which picks a random connection and disables it - makes it recessive
     */
    private void disableMutate() {
        if (geneConnectionList.size() > 0) {
            GeneConnection randomConnection = geneConnectionList.get(rand.nextInt(geneConnectionList.size()));
            randomConnection.setEnabled(false);
        }
    }

    /**
     * mutation which picks a random connection and enables it - makes it dominant
     */
    private void enableMutate() {
        if (geneConnectionList.size() > 0) {
            GeneConnection randomCon = geneConnectionList.get(rand.nextInt(geneConnectionList.size()));
            randomCon.setEnabled(true);
        }
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getNormalisedFitness() {
        return normalisedFitness;
    }

    public void setNormalisedFitness(double normalisedFitness) {
        this.normalisedFitness = normalisedFitness;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    @Override
    public int compareTo(Object object) {
        Genome genome = (Genome) object;
        if (fitness == genome.fitness) {
            return 0;
        } else if (fitness > genome.fitness) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return "Genome{" +
                "fitness=" + fitness +
                ", geneConnectionList=" + geneConnectionList +
                ", nodeGenes=" + nodes +
                '}';
    }
}

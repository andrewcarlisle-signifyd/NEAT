package NEAT.config;

/**
 * contains static configuration
 */
public class NEATConfig {

    /**
     * correctness percent threshold that the result must reach
     */
    public static final double CORRECTNESS_THRESHOLD = 95f;

    /**
     * the number of input parameters
     */
    public static final int INPUTS = 1;

    /**
     * the number of output parameters
     */
    public static final int OUTPUTS = 1;

    /**
     * number of hidden nodes
     */
    public static final int HIDDEN_NODES = 1000000;

    /**
     * size of initial genome population
     */
    public static final int POPULATION = 300;

    /**
     * compatibility for two genomes to be the same species
     */
    public static final float COMPATIBILITY_THRESHOLD = 1f;

    /**
     * weighting of excess nodes to distance function (used for species identification)
     */
    public static final float EXCESS_COEFFICENT = 2;

    /**
     * weighting of dijoined nodes to distance function (used for species identification)
     */
    public static final float DISJOINT_COEFFICENT = 2;

    /**
     * weighting of excess nodes to distance function (used for species identification)
     */
    public static final float WEIGHT_COEFFICENT = 0.4f;

    /**
     * max allowed value of staleness for a species
     */
    public static final float STALE_SPECIES = 15;

    /**
     *
     */
    public static final float STEPS = 0.1f;

    /**
     * chance of a connection weight perturbation during mutation
     */
    public static final float PERTURB_CHANCE = 0.9f;

    /**
     * chance of a connection weight mutation
     */
    public static final float WEIGHT_CHANCE = 0.3f;

    /**
     * chance of a connection weight mutation
     */
    public static final float WEIGHT_MUTATION_CHANCE = 0.9f;

    /**
     * chance of a node mutation
     */
    public static final float NODE_MUTATION_CHANCE = 0.03f;

    /**
     * chance of a connection mutation
     */
    public static final float CONNECTION_MUTATION_CHANCE = 0.05f;

    /**
     * chance of a bias connection mutation
     */
    public static final float BIAS_CONNECTION_MUTATION_CHANCE = 0.15f;

    /**
     * chance for a mutation which disables a connection
     */
    public static final float DISABLE_MUTATION_CHANCE = 0.1f;

    /**
     * chance for a mutation which enables a connection
     */
    public static final float ENABLE_MUTATION_CHANCE = 0.2f ;

    /**
     * change for breeding to occur
     */
    public static final float CROSSOVER_CHANCE = 0.75f;

    /**
     * maximum values of staleness for the gene pool
     */
    public static final int STALE_POOL = 20 ;
}

package NEAT;

/**
 * POJO which defines a connection between two nodes (neurons)
 */
public class GeneConnection {

    private int intoNode;
    private int outNode;
    private int innovation;
    private double weight;
    private boolean enabled;

    public GeneConnection(int intoNode, int outNode, int innovation, double weight, boolean enabled) {
        this.intoNode = intoNode;
        this.outNode = outNode;
        this.innovation = innovation;
        this.weight = weight;
        this.enabled = enabled;
    }

    /**
     * copy constructor
     *
     * @param connectionGene
     */
    public GeneConnection(GeneConnection connectionGene){
        if (connectionGene != null) {
            this.intoNode = connectionGene.getIntoNode();
            this.outNode = connectionGene.getOutNode();
            this.innovation = connectionGene.getInnovation();
            this.weight = connectionGene.getWeight();
            this.enabled = connectionGene.isEnabled();
        }
    }

    public int getIntoNode() {
        return intoNode;
    }

    public int getOutNode() {
        return outNode;
    }

    public int getInnovation() {
        return innovation;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    @Override
    public String toString() {
        return intoNode +","+outNode+","+weight+","+enabled;
    }
}

package NEAT;

import java.util.ArrayList;

/**
 * POJO which defines a node (neuron)
 * stores information about connections
 */
public class GeneNode {

    private double value;

    private ArrayList<GeneConnection> incomingConnection = new ArrayList<>();

    public GeneNode(double value) {
        super();
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public ArrayList<GeneConnection> getIncomingConnection() {
        return incomingConnection;
    }
}

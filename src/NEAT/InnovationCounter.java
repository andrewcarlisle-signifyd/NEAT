package NEAT;

/**
 * class which exposes a utility function for incrementing the innovation counter
 */
public class InnovationCounter {

    private static int innovation = 0;

    /**
     * increments the innovation counter
     *
     * @return incremented innovation
     */
    public static int newInnovation() {
        innovation++;
        return innovation;
    }
}

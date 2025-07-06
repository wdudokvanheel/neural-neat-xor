package nl.wdudokvanheel.neat.xor;

import nl.wdudokvanheel.neural.neat.genome.Genome;
import nl.wdudokvanheel.neural.network.Network;
import nl.wdudokvanheel.neural.util.AbstractCreature;

public class XORCreature extends AbstractCreature {
    private final Network network;

    public XORCreature(Genome genome) {
        super(genome);
        this.network = new Network(genome);
    }

    public Network getNetwork() {
        return network;
    }
}
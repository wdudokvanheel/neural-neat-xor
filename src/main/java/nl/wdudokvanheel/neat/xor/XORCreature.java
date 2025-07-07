package nl.wdudokvanheel.neat.xor;

import nl.wdudokvanheel.neural.neat.genome.Genome;
import nl.wdudokvanheel.neural.network.Network;
import nl.wdudokvanheel.neural.util.AbstractCreatureInterface;

public class XORCreature extends AbstractCreatureInterface<XORCreature> {
    private final Network network;

    public XORCreature(Genome genome) {
        super(genome);
        this.network = new Network(genome);
    }

    public Network getNetwork() {
        return network;
    }
}
package nl.wdudokvanheel.neat.xor;

import nl.wdudokvanheel.neural.core.Network;
import nl.wdudokvanheel.neural.neat.AbstractCreature;
import nl.wdudokvanheel.neural.neat.model.Genome;

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
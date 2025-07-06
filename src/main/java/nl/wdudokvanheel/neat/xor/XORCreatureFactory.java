package nl.wdudokvanheel.neat.xor;

import nl.wdudokvanheel.neural.CreatureFactory;
import nl.wdudokvanheel.neural.neat.model.Genome;

public class XORCreatureFactory implements CreatureFactory<XORCreature> {
    @Override
    public XORCreature createNewCreature(Genome genome) {
        return new XORCreature(genome);
    }
}
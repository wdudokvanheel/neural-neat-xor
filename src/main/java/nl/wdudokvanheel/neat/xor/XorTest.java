package nl.wdudokvanheel.neat.xor;

import nl.wdudokvanheel.neural.core.Network;
import nl.wdudokvanheel.neural.neat.NeatEvolution;
import nl.wdudokvanheel.neural.neat.model.*;
import nl.wdudokvanheel.neural.neat.service.InnovationService;

import java.util.Random;

public class XorTest {
    private final Random r = new Random();

    public NeatConfiguration getConfiguration() {
        NeatConfiguration cfg = new NeatConfiguration();
        cfg.populationSize = 1000;
        cfg.targetSpecies = 50;
        cfg.newCreaturesPerGeneration = 0.0;
        cfg.mutateAddNeuronProbability = 0.03;
        cfg.mutateWeightProbability = 0.80;
        cfg.minimumSpeciesSizeForChampionCopy = 1;
        cfg.copyChampionsAllSpecies = true;
        cfg.adjustSpeciesThreshold = false;
        cfg.setInitialLinks = true;
        cfg.speciesThreshold = 7.5;
        cfg.multipleMutationsPerGenome = true;
        cfg.mutateAddConnectionProbability = 0.05;
        cfg.mutateToggleConnectionProbability = 0.02;
        cfg.mutateRandomizeWeightsProbability = 0.1;
        cfg.mutateWeightPerturbationPower = 0.5;
        cfg.eliminateStagnantSpecies = true;
        return cfg;
    }

    public int run() {
        XORCreatureFactory f = new XORCreatureFactory();
        NeatContext ctx = NeatEvolution.createContext(f, getConfiguration());
        Genome g = blueprint(ctx.innovationService);
        XORCreature bp = new XORCreature(g);
        NeatEvolution.generateInitialPopulation(ctx, bp);

        for (int gen = 0; gen < Main.MAX_GENERATIONS; gen++) {
            scorePop(ctx);

            for (Creature cr : ctx.creatures) {
                XORCreature xc = (XORCreature) cr;
                if (canSolveXor(xc.getNetwork())) {
                    return gen;
                }
            }

            NeatEvolution.nextGeneration(ctx);
        }

        return Main.MAX_GENERATIONS;
    }

    private Genome blueprint(InnovationService innovation) {
        Genome g = new Genome();
        int i0 = innovation.getInputNodeInnovationId(0);
        int i1 = innovation.getInputNodeInnovationId(1);
        int bias = innovation.getInputNodeInnovationId(2);
        int o = innovation.getOutputNodeInnovationId(0);
        g.addNeuron(new NeuronGene(NeuronGeneType.INPUT, i0));
        g.addNeuron(new NeuronGene(NeuronGeneType.INPUT, i1));
        g.addNeuron(new NeuronGene(NeuronGeneType.INPUT, bias));
        g.addNeuron(new NeuronGene(NeuronGeneType.OUTPUT, o).setLayer(1));
        g.addConnection(new ConnectionGene(
                innovation.getConnectionInnovationId(i0, o), i0, o, w()));
        g.addConnection(new ConnectionGene(
                innovation.getConnectionInnovationId(i1, o), i1, o, w()));
        g.addConnection(new ConnectionGene(
                innovation.getConnectionInnovationId(bias, o), bias, o, w()));
        return g;
    }

    private double fitness(Network net) {
        double[][] patterns = {
                {0, 0, 1},
                {0, 1, 1},
                {1, 0, 1},
                {1, 1, 1}
        };
        double[] targets = {0, 1, 1, 0};

        double totalError = 0.0;
        for (int i = 0; i < 4; i++) {
            net.resetNeuronValues();
            net.setInput(patterns[i]);
            double out = net.getOutput();
            totalError += Math.abs(targets[i] - out);
        }

        return 4.0 - totalError;
    }

    private boolean canSolveXor(Network net) {
        double[][] in = {{0, 0, 1}, {0, 1, 1}, {1, 0, 1}, {1, 1, 1}};
        double[] target = {0, 1, 1, 0};
        for (int i = 0; i < 4; i++) {
            net.resetNeuronValues();
            net.setInput(in[i]);
            double out = net.getOutput();
            if ((out >= 0.5) != (target[i] == 1.0)) {
                return false;
            }
        }
        return true;
    }

    private void scorePop(NeatContext c) {
        for (Creature cr : c.creatures) {
            XORCreature xc = (XORCreature) cr;
            xc.setFitness(fitness(xc.getNetwork()));
        }
    }

    private double w() {
        return r.nextDouble(-1, 1);
    }
}

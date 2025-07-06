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
        cfg.speciesThreshold = 7.5;
        cfg.adjustSpeciesThreshold = false;
        cfg.newCreaturesPerGeneration = 0.0;
        cfg.minimumSpeciesSizeForChampionCopy = 1;
        cfg.copyChampionsAllSpecies = true;

        cfg.setInitialLinks = true;
        cfg.eliminateStagnantSpecies = true;

        cfg.multipleMutationsPerGenome = true;
        cfg.mutateAddConnectionProbability = 0.05;
        cfg.mutateAddNeuronProbability = 0.03;
        cfg.mutateRandomizeWeightsProbability = 0.1;
        cfg.mutateWeightProbability = 0.80;
        cfg.mutateWeightPerturbationPower = 0.5;
        cfg.mutateToggleConnectionProbability = 0.02;
        return cfg;
    }

    public int run() {
        XORCreatureFactory f = new XORCreatureFactory();
        NeatContext ctx = NeatEvolution.createContext(f, getConfiguration());
        Genome g = blueprint(ctx.innovationService);
        XORCreature bp = new XORCreature(g);
        NeatEvolution.generateInitialPopulation(ctx, bp);

        for (int gen = 0; gen < Main.MAX_GENERATIONS; gen++) {
            scorePopulation(ctx);

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

    private void scorePopulation(NeatContext c) {
        for (Creature cr : c.creatures) {
            XORCreature xc = (XORCreature) cr;
            xc.setFitness(fitness(xc.getNetwork()));
        }
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

    private Genome blueprint(InnovationService innovation) {
        Genome genome = new Genome();
        int input0 = innovation.getInputNodeInnovationId(0);
        int input1 = innovation.getInputNodeInnovationId(1);
        int bias = innovation.getInputNodeInnovationId(2);
        int output = innovation.getOutputNodeInnovationId(0);

        genome.addNeuron(new NeuronGene(NeuronGeneType.INPUT, input0));
        genome.addNeuron(new NeuronGene(NeuronGeneType.INPUT, input1));
        genome.addNeuron(new NeuronGene(NeuronGeneType.INPUT, bias));
        genome.addNeuron(new NeuronGene(NeuronGeneType.OUTPUT, output));

        genome.addConnection(new ConnectionGene(innovation.getConnectionInnovationId(input0, output), input0, output, randomWeight()));
        genome.addConnection(new ConnectionGene(innovation.getConnectionInnovationId(input1, output), input1, output, randomWeight()));
        genome.addConnection(new ConnectionGene(innovation.getConnectionInnovationId(bias, output), bias, output, randomWeight()));

        return genome;
    }

    private double randomWeight() {
        return r.nextDouble(-1, 1);
    }
}

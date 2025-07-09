package nl.wdudokvanheel.neat.xor;

import nl.wdudokvanheel.neural.neat.NeatConfiguration;
import nl.wdudokvanheel.neural.neat.NeatContext;
import nl.wdudokvanheel.neural.neat.NeatEvolution;
import nl.wdudokvanheel.neural.neat.genome.Genome;
import nl.wdudokvanheel.neural.neat.genome.InputNeuronGene;
import nl.wdudokvanheel.neural.neat.genome.OutputNeuronGene;
import nl.wdudokvanheel.neural.neat.service.GenomeBuilder;
import nl.wdudokvanheel.neural.neat.service.InnovationService;
import nl.wdudokvanheel.neural.network.Network;

import java.util.Random;

public class XorTest {
    private final Random r = new Random();

    public NeatConfiguration createConfiguration() {
        NeatConfiguration cfg = new NeatConfiguration();
        cfg.populationSize = 1000;
        cfg.speciesThreshold = 7.5;
        cfg.adjustSpeciesThreshold = false;
        cfg.newCreaturesPerGeneration = 0.0;
        cfg.minimumSpeciesSizeForChampionCopy = 1;
        cfg.copyChampionsAllSpecies = true;

        cfg.randomizeInitialLinks = true;
        cfg.initialLinkActiveProbability = 1.0;
        cfg.initialLinkWeight = 1.0;
        cfg.eliminateStagnantSpecies = false;

        cfg.multipleMutationsPerGenome = true;
        cfg.mutateAddConnectionProbability = 0.20;
        cfg.mutateAddNeuronProbability = 0.10;
        cfg.mutateRandomizeWeightsProbability = 0.1;
        cfg.mutateWeightProbability = 0.80;
        cfg.mutateWeightPerturbationPower = 0.5;
        cfg.mutateToggleConnectionProbability = 0.02;
        return cfg;
    }

    public int run() {
        XORCreatureFactory factory = new XORCreatureFactory();
        NeatConfiguration configuration = createConfiguration();
        NeatContext<XORCreature> context = NeatEvolution.createContext(factory, configuration);

        Genome blueprintGenome = blueprint(context.innovationService);
        XORCreature blueprint = new XORCreature(blueprintGenome);
        NeatEvolution.generateInitialPopulation(context, blueprint);

        for (int generation = 0; generation < Main.MAX_GENERATIONS; generation++) {
            scorePopulation(context);

            for (XORCreature creature : context.creatures) {
                if (canSolveXor(creature.getNetwork())) {
                    return generation;
                }
            }

            NeatEvolution.nextGeneration(context);
        }

        return Main.MAX_GENERATIONS;
    }

    private void scorePopulation(NeatContext<XORCreature> c) {
        for (XORCreature creature : c.creatures) {
            double fitness = fitness(creature.getNetwork());
            creature.setFitness(fitness);
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
        GenomeBuilder builder = new GenomeBuilder(innovation);
        InputNeuronGene[] inputs = builder.addInputNeurons(3);
        OutputNeuronGene[] outputs = builder.addOutputNeurons(1);

        builder.addConnection(inputs[0], outputs[0], randomWeight());
        builder.addConnection(inputs[1], outputs[0], randomWeight());
        builder.addConnection(inputs[2], outputs[0], randomWeight());

        return builder.getGenome();
    }

    private double randomWeight() {
        return r.nextDouble(-1, 1);
    }
}

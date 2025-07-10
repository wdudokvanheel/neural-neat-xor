# NEAT XOR Example

## Overview

This module applies the NEAT library to solve the XOR problem by evolving both network topology and weights.

## How It Works

1. **Configuration**
   In `XorTest.createConfiguration()`, set population size, mutation rates, speciation thresholds, etc.
2. **Initialization**

    * Build a “blueprint” genome with 3 inputs (including bias) and 1 output
    * Fully connect with random weights
    * Generate an initial population from this blueprint
3. **Evolution Loop**

    * **Scoring**: Test each network on the four XOR patterns. Fitness = 4 − total absolute error.
    * **Check**: If any network classifies all patterns correctly (output ≥ 0.5 matches target), stop.
    * **Next Generation**: Apply NEAT’s speciation, crossover, and mutation (add connection/node, weight perturbation).
4. **Parallel Runs & Statistics**
   `Main` launches independent evolutions, records the generation count when XOR is solved, and computes summary statistics.

## Running the Example

```bash
mvn clean compile exec:java \
  -Dexec.mainClass=nl.wdudokvanheel.neat.xor.Main
```

*Tweak parameters in `XorTest` (population size, mutation probabilities, max generations)*

## Latest Results

| Metric              |    Value |
| ------------------- |---------:|
| Runs                |  100 000 |
| Success rate        | 100.00 % |
| Average generations |    19.91 |
| Min generations     |        2 |
| Max generations     |      130 |
| Median generations  |    19.00 |
| StdDev generations  |     5.75 |

## Project Structure

```
nl.wdudokvanheel.neat.xor
├── XORCreature.java          # Wraps a genome in a runnable network
├── XORCreatureFactory.java   # Factory for new XOR creatures
├── XorTest.java              # Main file, contains NEAT configuration, evolution loop, fitness scoring
└── Main.java                 # Parallel runs, collects and reports statistics
```

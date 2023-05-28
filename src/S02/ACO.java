import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ACO {
    private final double[][] graph;
    private final double[][] trails;
    private final List<Ant> ants = new ArrayList<>();
    private final double[] probabilities;
    public StringBuilder stringBuilder = new StringBuilder();
    private int currentIndex;

    private int[] bestTourOrder;
    private double bestTourLength;

    public ACO() {
        graph = generateRandomDistanceMatrix(Configuration.INSTANCE.numberOfCities);
        trails = new double[Configuration.INSTANCE.numberOfCities][Configuration.INSTANCE.numberOfCities];
        probabilities = new double[Configuration.INSTANCE.numberOfCities];

        for (int i = 0; i < Configuration.INSTANCE.numberOfCities; i++) {
            ants.add(new Ant(Configuration.INSTANCE.numberOfCities));
        }
    }

    private double[][] generateRandomDistanceMatrix(int n) {
        double[][] distanceMatrix = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0;
                } else {
                    distanceMatrix[i][j] = Math.abs(Configuration.INSTANCE.randomGenerator.nextInt(100) + 1);
                }
            }
        }

        return distanceMatrix;
    }

    public double[][] getGraph() {
        return graph;
    }

    public void run() {
        long runtimeStart = System.currentTimeMillis();

        setupAnts();
        clearTrails();

        for (int i = 0; i < Configuration.INSTANCE.maximumIterations; i++) {
            moveAnts();
            updateTrails();
            updateBest();
        }

        stringBuilder.append("\nbest tour length : ").append((bestTourLength - Configuration.INSTANCE.numberOfCities));
        stringBuilder.append("\nbest tour order  : ").append(Arrays.toString(bestTourOrder));
        stringBuilder.append("\nruntime          : ").append(System.currentTimeMillis() - runtimeStart).append(" ms");

        System.out.println(stringBuilder);
    }

    private void setupAnts() {
        for (int i = 0; i < Configuration.INSTANCE.numberOfAnts; i++) {
            for (Ant ant : ants) {
                ant.clear();
                ant.visitCity(-1, Configuration.INSTANCE.randomGenerator.nextInt(Configuration.INSTANCE.numberOfCities));
            }
        }
        currentIndex = 0;
    }

    private void moveAnts() {
        for (int i = currentIndex; i < Configuration.INSTANCE.numberOfCities - 1; i++) {
            for (Ant ant : ants) {
                ant.visitCity(currentIndex, selectNextCity(ant));
            }
            currentIndex++;
        }
    }

    private int selectNextCity(Ant ant) {
        int t = Configuration.INSTANCE.randomGenerator.nextInt(Configuration.INSTANCE.numberOfCities - currentIndex);
        if (Configuration.INSTANCE.randomGenerator.nextDouble() < Configuration.INSTANCE.randomFactor) {
            int cityIndex = -999;

            for (int i = 0; i < Configuration.INSTANCE.numberOfCities; i++) {
                if (i == t && !ant.visited(i)) {
                    cityIndex = i;
                    break;
                }
            }

            if (cityIndex != -999) {
                return cityIndex;
            }
        }

        calculateProbabilities(ant);

        double randomNumber = Configuration.INSTANCE.randomGenerator.nextDouble();
        double total = 0;

        for (int i = 0; i < Configuration.INSTANCE.numberOfCities; i++) {
            total += probabilities[i];
            if (total >= randomNumber) {
                return i;
            }
        }

        throw new RuntimeException("runtime exception : other cities");
    }

    public void calculateProbabilities(Ant ant) {
        int i = ant.trail[currentIndex];
        double pheromone = 0.0;

        for (int l = 0; l < Configuration.INSTANCE.numberOfCities; l++) {
            if (!ant.visited(l)) {
                pheromone += Math.pow(trails[i][l], Configuration.INSTANCE.alpha) * Math.pow(1.0 / graph[i][l], Configuration.INSTANCE.beta);
            }
        }

        for (int j = 0; j < Configuration.INSTANCE.numberOfCities; j++) {
            if (ant.visited(j)) {
                probabilities[j] = 0.0;
            } else {
                double numerator = Math.pow(trails[i][j], Configuration.INSTANCE.alpha) * Math.pow(1.0 / graph[i][j], Configuration.INSTANCE.beta);
                probabilities[j] = numerator / pheromone;
            }
        }
    }

    private void updateTrails() {
        for (int i = 0; i < Configuration.INSTANCE.numberOfCities; i++) {
            for (int j = 0; j < Configuration.INSTANCE.numberOfCities; j++) {
                trails[i][j] *= Configuration.INSTANCE.evaporation;
            }
        }

        for (Ant ant : ants) {
            double contribution = Configuration.INSTANCE.q / ant.trailLength(graph);
            for (int i = 0; i < Configuration.INSTANCE.numberOfCities - 1; i++) {
                trails[ant.trail[i]][ant.trail[i + 1]] += contribution;
            }
            trails[ant.trail[Configuration.INSTANCE.numberOfCities - 1]][ant.trail[0]] += contribution;
        }
    }

    private void updateBest() {
        if (bestTourOrder == null) {
            bestTourOrder = ants.get(0).trail;
            bestTourLength = ants.get(0).trailLength(graph);
        }

        for (Ant ant : ants) {
            if (ant.trailLength(graph) < bestTourLength) {
                bestTourLength = ant.trailLength(graph);
                bestTourOrder = ant.trail.clone();
            }
        }
    }

    private void clearTrails() {
        for (int i = 0; i < Configuration.INSTANCE.numberOfCities; i++) {
            for (int j = 0; j < Configuration.INSTANCE.numberOfCities; j++) {
                trails[i][j] = Configuration.INSTANCE.initialPheromoneValue;
            }
        }
    }
}
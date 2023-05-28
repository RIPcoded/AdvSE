public class Application {
    public static void main(String... args) {
        ACO aco = new ACO();
        aco.run();

        System.out.println();
        Evaluation.branchAndBound(aco.getGraph());
    }
}

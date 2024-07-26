public class test {
    public ElitisticListPopulation(final int populationLimit, final double elitismRate) {
        super(populationLimit);
        this.elitismRate = elitismRate;
    }
    public ElitisticListPopulation(final List<Chromosome> chromosomes,
                                   final int populationLimit,
                                   final double elitismRate) {
        super(chromosomes, populationLimit);
        this.elitismRate = elitismRate;
    }
}
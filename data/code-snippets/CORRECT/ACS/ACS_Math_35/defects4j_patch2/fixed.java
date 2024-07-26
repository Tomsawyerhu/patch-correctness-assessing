public class test {
    public ElitisticListPopulation(final int populationLimit, final double elitismRate) {
        super(populationLimit);
        if (elitismRate>(double)1.0){
                throw new OutOfRangeException(null,null,null);
        }
        if (elitismRate<(double)0.0){
                throw new OutOfRangeException(null,null,null);
        }
        this.elitismRate = elitismRate;
    }
    public ElitisticListPopulation(final List<Chromosome> chromosomes,
                                   final int populationLimit,
                                   final double elitismRate) {
        super(chromosomes, populationLimit);
        if (elitismRate>(double)1.0){
                throw new OutOfRangeException(null,null,null);
        }
        if (elitismRate<(double)0.0){
                throw new OutOfRangeException(null,null,null);
        }
        this.elitismRate = elitismRate;
    }
}
public class test {
    public CMAESOptimizer(int lambda) {
        this(lambda, null, DEFAULT_MAXITERATIONS, DEFAULT_STOPFITNESS,
             DEFAULT_ISACTIVECMA, DEFAULT_DIAGONALONLY,
             DEFAULT_CHECKFEASABLECOUNT, DEFAULT_RANDOMGENERATOR, false);
    }
}
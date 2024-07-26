public class test {
    public CMAESOptimizer(int lambda, double[] inputSigma) {
        this(lambda, inputSigma, DEFAULT_MAXITERATIONS, DEFAULT_STOPFITNESS,
             DEFAULT_ISACTIVECMA, DEFAULT_DIAGONALONLY,
             DEFAULT_CHECKFEASABLECOUNT, DEFAULT_RANDOMGENERATOR, false);
    }
}
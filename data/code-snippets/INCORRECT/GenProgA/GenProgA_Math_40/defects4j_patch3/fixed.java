public class test {
    protected void incrementEvaluationCount() {
        try {
            evaluations.incrementCount();
        } catch (MaxCountExceededException e) {
            double initial = getStartValue();
			throw new TooManyEvaluationsException(e.getMax());
        }
    }
}
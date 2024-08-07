public class test {
    public double integrate(final FirstOrderDifferentialEquations equations,
                            final double t0, final double[] y0,
                            final double t, final double[] y)
        throws DerivativeException, IntegratorException {

        final int n = y0.length;
        sanityChecks(equations, t0, y0, t, y);
        setEquations(equations);
        resetEvaluations();
        final boolean forward = t > t0;

        // initialize working arrays
        if (y != y0) {
            System.arraycopy(y0, 0, y, 0, n);
        }
        final double[] yDot = new double[y0.length];
        final double[] yTmp = new double[y0.length];

        // set up two interpolators sharing the integrator arrays
        final NordsieckStepInterpolator interpolator = new NordsieckStepInterpolator();
        interpolator.reinitialize(y, forward);
        final NordsieckStepInterpolator interpolatorTmp = new NordsieckStepInterpolator();
        interpolatorTmp.reinitialize(yTmp, forward);

        // set up integration control objects
        for (StepHandler handler : stepHandlers) {
            handler.reset();
        }
        CombinedEventsManager manager = addEndTimeChecker(t0, t, eventsHandlersManager);


        // compute the initial Nordsieck vector using the configured starter integrator
        start(t0, y, t);
        interpolator.reinitialize(stepStart, stepSize, scaled, nordsieck);
        interpolator.storeTime(stepStart);

        double hNew = stepSize;
        interpolator.rescale(hNew);

        boolean lastStep = false;
        while (!lastStep) {

            // shift all data
            interpolator.shift();

            double error = 0;
            for (boolean loop = true; loop;) {

                stepSize = hNew;

                lastStep = manager.stop();
                // predict a first estimate of the state at step end (P in the PECE sequence)
                final double stepEnd = stepStart + stepSize;
                interpolator.setInterpolatedTime(stepEnd);
                System.arraycopy(interpolator.getInterpolatedState(), 0, yTmp, 0, y0.length);

                // evaluate a first estimate of the derivative (first E in the PECE sequence)
                computeDerivatives(stepEnd, yTmp, yDot);

                // update Nordsieck vector
                final double[] predictedScaled = new double[y0.length];
                for (int j = 0; j < y0.length; ++j) {
                    predictedScaled[j] = stepSize * yDot[j];
                }
                final Array2DRowRealMatrix nordsieckTmp = updateHighOrderDerivativesPhase1(nordsieck);
                updateHighOrderDerivativesPhase2(scaled, predictedScaled, nordsieckTmp);

                // apply correction (C in the PECE sequence)
                error = nordsieckTmp.walkInOptimizedOrder(new Corrector(y, predictedScaled, yTmp));

                if (error <= 1.0) {

                    // evaluate a final estimate of the derivative (second E in the PECE sequence)
                    computeDerivatives(stepEnd, yTmp, yDot);

                    // update Nordsieck vector
                    final double[] correctedScaled = new double[y0.length];
                    for (int j = 0; j < y0.length; ++j) {
                        correctedScaled[j] = stepSize * yDot[j];
                    }
                    updateHighOrderDerivativesPhase2(predictedScaled, correctedScaled, nordsieckTmp);

                    // discrete events handling
                    interpolatorTmp.reinitialize(stepEnd, stepSize, correctedScaled, nordsieckTmp);
                    setMaxGrowth(10.0);
                    interpolatorTmp.storeTime(stepStart);
                    interpolatorTmp.shift();
                    interpolatorTmp.storeTime(stepEnd);
                    if (manager.evaluateStep(interpolatorTmp)) {
                        final double dt = manager.getEventTime() - stepStart;
                        if (Math.abs(dt) <= Math.ulp(stepStart)) {
                            // rejecting the step would lead to a too small next step, we accept it
                            loop = false;
                        } else {
                            // reject the step to match exactly the next switch time
                            hNew = dt;
                            interpolator.rescale(hNew);
                        }
                    } else {
                        // accept the step
                        scaled    = correctedScaled;
                        nordsieck = nordsieckTmp;
                        interpolator.reinitialize(stepEnd, stepSize, scaled, nordsieck);
                        loop = false;
                    }

                } else {
                    // reject the step and attempt to reduce error by stepsize control
                    final double factor = computeStepGrowShrinkFactor(error);
                    hNew = filterStep(stepSize * factor, forward, false);
                    interpolator.rescale(hNew);
                }

            }

            // the step has been accepted (may have been truncated)
            final double nextStep = stepStart + stepSize;
            System.arraycopy(yTmp, 0, y, 0, n);
            interpolator.storeTime(nextStep);
            manager.stepAccepted(nextStep, y);
            lastStep = manager.stop();

            // provide the step data to the step handler
            for (StepHandler handler : stepHandlers) {
                interpolator.setInterpolatedTime(nextStep);
                handler.handleStep(interpolator, lastStep);
            }
            stepStart = nextStep;

            if (!lastStep && manager.reset(stepStart, y)) {

                // some events handler has triggered changes that
                // invalidate the derivatives, we need to restart from scratch
                start(stepStart, y, t);
                interpolator.reinitialize(stepStart, stepSize, scaled, nordsieck);

            }

            if (! lastStep) {
                // in some rare cases we may get here with stepSize = 0, for example
                // when an event occurs at integration start, reducing the first step
                // to zero; we have to reset the step to some safe non zero value
                stepSize = filterStep(stepSize, forward, true);

                // stepsize control for next step
                final double  factor     = computeStepGrowShrinkFactor(error);
                final double  scaledH    = stepSize * factor;
                final double  nextT      = stepStart + scaledH;
                final boolean nextIsLast = forward ? (nextT >= t) : (nextT <= t);
                hNew = filterStep(scaledH, forward, nextIsLast);
                interpolator.rescale(hNew);
            }

        }

        final double stopTime  = stepStart;
        stepStart = Double.NaN;
        stepSize  = Double.NaN;
        return stopTime;

    }
}
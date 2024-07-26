public class test {
    protected double acceptStep(final AbstractStepInterpolator interpolator,
                                final double[] y, final double[] yDot, final double tEnd)
        throws MaxCountExceededException, DimensionMismatchException, NoBracketingException {

            double previousT = interpolator.getGlobalPreviousTime();
            final double currentT = interpolator.getGlobalCurrentTime();

            // initialize the events states if needed
            if (! statesInitialized) {
                for (EventState state : eventsStates) {
                    state.reinitializeBegin(interpolator);
                }
                statesInitialized = true;
            }

            // search for next events that may occur during the step
            final int orderingSign = interpolator.isForward() ? +1 : -1;
            SortedSet<EventState> occuringEvents = new TreeSet<EventState>(new Comparator<EventState>() {

                /** {@inheritDoc} */
                public int compare(EventState es0, EventState es1) {
                    return orderingSign * Double.compare(es0.getEventTime(), es1.getEventTime());
                }

            });

            for (final EventState state : eventsStates) {
                if (state.evaluateStep(interpolator)) {
                    // the event occurs during the current step
                    occuringEvents.add(state);
                }
            }

            while (!occuringEvents.isEmpty()) {

                // handle the chronologically first event
                final Iterator<EventState> iterator = occuringEvents.iterator();
                final EventState currentEvent = iterator.next();
                iterator.remove();

                // restrict the interpolator to the first part of the step, up to the event
                final double eventT = currentEvent.getEventTime();
                interpolator.setSoftPreviousTime(previousT);
                interpolator.setSoftCurrentTime(eventT);

                // get state at event time
                interpolator.setInterpolatedTime(eventT);
                final double[] eventY = interpolator.getInterpolatedState().clone();

                // advance all event states to current time
                currentEvent.stepAccepted(eventT, eventY);
                isLastStep = currentEvent.stop();

                // handle the first part of the step, up to the event
                for (final StepHandler handler : stepHandlers) {
                    handler.handleStep(interpolator, isLastStep);
                }

                if (isLastStep) {
                    // the event asked to stop integration
                    System.arraycopy(eventY, 0, y, 0, y.length);
                    for (final EventState remaining : occuringEvents) {
                        remaining.stepAccepted(eventT, eventY);
                    }
                    return eventT;
                }

                boolean needReset = currentEvent.reset(eventT, eventY);
                if (needReset) {
                    // some event handler has triggered changes that
                    // invalidate the derivatives, we need to recompute them
                    System.arraycopy(eventY, 0, y, 0, y.length);
                    computeDerivatives(eventT, y, yDot);
                    resetOccurred = true;
                    for (final EventState remaining : occuringEvents) {
                        remaining.stepAccepted(eventT, eventY);
                    }
                    return eventT;
                }

                // prepare handling of the remaining part of the step
                previousT = eventT;
                interpolator.setSoftPreviousTime(eventT);
                interpolator.setSoftCurrentTime(currentT);

                // check if the same event occurs again in the remaining part of the step
                if (currentEvent.evaluateStep(interpolator)) {
                    // the event occurs during the current step
                    occuringEvents.add(currentEvent);
                }

            }

            // last part of the step, after the last event
            interpolator.setInterpolatedTime(currentT);
            final double[] currentY = interpolator.getInterpolatedState();
            for (final EventState state : eventsStates) {
                state.stepAccepted(currentT, currentY);
                isLastStep = isLastStep || state.stop();
            }
            isLastStep = isLastStep || Precision.equals(currentT, tEnd, 1);

            // handle the remaining part of the step, after all events if any
            for (StepHandler handler : stepHandlers) {
                handler.handleStep(interpolator, isLastStep);
            }

            return currentT;

    }
    public boolean evaluateStep(final StepInterpolator interpolator)
        throws MaxCountExceededException, NoBracketingException {

        try {
            forward = interpolator.isForward();
            final double t1 = interpolator.getCurrentTime();
            final double dt = t1 - t0;
            if (FastMath.abs(dt) < convergence) {
                // we cannot do anything on such a small step, don't trigger any events
                return false;
            }
            final int    n = FastMath.max(1, (int) FastMath.ceil(FastMath.abs(dt) / maxCheckInterval));
            final double h = dt / n;

            final UnivariateFunction f = new UnivariateFunction() {
                public double value(final double t) throws LocalMaxCountExceededException {
                    try {
                        interpolator.setInterpolatedTime(t);
                        return handler.g(t, interpolator.getInterpolatedState());
                    } catch (MaxCountExceededException mcee) {
                        throw new LocalMaxCountExceededException(mcee);
                    }
                }
            };

            double ta = t0;
            double ga = g0;
            for (int i = 0; i < n; ++i) {

                // evaluate handler value at the end of the substep
                final double tb = t0 + (i + 1) * h;
                interpolator.setInterpolatedTime(tb);
                final double gb = handler.g(tb, interpolator.getInterpolatedState());

                // check events occurrence
                if (g0Positive ^ (gb >= 0)) {
                    // there is a sign change: an event is expected during this step

                    // variation direction, with respect to the integration direction
                    increasing = gb >= ga;

                    // find the event time making sure we select a solution just at or past the exact root
                    final double root;
                    if (solver instanceof BracketedUnivariateSolver<?>) {
                        @SuppressWarnings("unchecked")
                        BracketedUnivariateSolver<UnivariateFunction> bracketing =
                                (BracketedUnivariateSolver<UnivariateFunction>) solver;
                        root = forward ?
                               bracketing.solve(maxIterationCount, f, ta, tb, AllowedSolution.RIGHT_SIDE) :
                               bracketing.solve(maxIterationCount, f, tb, ta, AllowedSolution.LEFT_SIDE);
                    } else {
                        final double baseRoot = forward ?
                                                solver.solve(maxIterationCount, f, ta, tb) :
                                                solver.solve(maxIterationCount, f, tb, ta);
                        final int remainingEval = maxIterationCount - solver.getEvaluations();
                        BracketedUnivariateSolver<UnivariateFunction> bracketing =
                                new PegasusSolver(solver.getRelativeAccuracy(), solver.getAbsoluteAccuracy());
                        root = forward ?
                               UnivariateSolverUtils.forceSide(remainingEval, f, bracketing,
                                                                   baseRoot, ta, tb, AllowedSolution.RIGHT_SIDE) :
                               UnivariateSolverUtils.forceSide(remainingEval, f, bracketing,
                                                                   baseRoot, tb, ta, AllowedSolution.LEFT_SIDE);
                    }

                    if ((!Double.isNaN(previousEventTime)) &&
                        (FastMath.abs(root - ta) <= convergence) &&
                        (FastMath.abs(root - previousEventTime) <= convergence)) {
                        // we have either found nothing or found (again ?) a past event,
                        // retry the substep excluding this value
                        ta = forward ? ta + convergence : ta - convergence;
                        ga = f.value(ta);
                        --i;
                    } else if (Double.isNaN(previousEventTime) ||
                               (FastMath.abs(previousEventTime - root) > convergence)) {
                        pendingEventTime = root;
                        pendingEvent = true;
                        return true;
                    } else {
                        // no sign change: there is no event for now
                        ta = tb;
                        ga = gb;
                    }

                } else {
                    // no sign change: there is no event for now
                    ta = tb;
                    ga = gb;
                }

            }

            // no event during the whole step
            pendingEvent     = false;
            pendingEventTime = Double.NaN;
            return false;

        } catch (LocalMaxCountExceededException lmcee) {
            throw lmcee.getException();
        }

    }
}
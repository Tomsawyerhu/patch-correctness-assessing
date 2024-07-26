public class test {
    protected BaseOptimizer(ConvergenceChecker<PAIR> checker) {
        this.checker = checker;

        evaluations = new Incrementor(0, new MaxEvalCallback());
        iterations = new Incrementor(0, new MaxIterCallback());
    }
    protected PointValuePair doOptimize() {
        final ConvergenceChecker<PointValuePair> checker = getConvergenceChecker();
        final double[] point = getStartPoint();
        final GoalType goal = getGoalType();
        final int n = point.length;
        double[] r = computeObjectiveGradient(point);
        if (goal == GoalType.MINIMIZE) {
            for (int i = 0; i < n; i++) {
                r[i] = -r[i];
            }
        }

        // Initial search direction.
        double[] steepestDescent = preconditioner.precondition(point, r);
        double[] searchDirection = steepestDescent.clone();

        double delta = 0;
        for (int i = 0; i < n; ++i) {
            delta += r[i] * searchDirection[i];
        }

        PointValuePair current = null;
        int iter = 0;
        int maxEval = getMaxEvaluations();
        while (true) {
            ++iter;

            final double objective = computeObjectiveValue(point);
            PointValuePair previous = current;
            current = new PointValuePair(point, objective);
            if (previous != null) {
                if (checker.converged(iter, previous, current)) {
                    // We have found an optimum.
                    return current;
                }
            }

            // Find the optimal step in the search direction.
            final UnivariateFunction lsf = new LineSearchFunction(point, searchDirection);
            final double uB = findUpperBound(lsf, 0, initialStep);
            // XXX Last parameters is set to a value close to zero in order to
            // work around the divergence problem in the "testCircleFitting"
            // unit test (see MATH-439).
            final double step = solver.solve(maxEval, lsf, 0, uB, 1e-15);
            maxEval -= solver.getEvaluations(); // Subtract used up evaluations.

            // Validate new point.
            for (int i = 0; i < point.length; ++i) {
                point[i] += step * searchDirection[i];
            }

            r = computeObjectiveGradient(point);
            if (goal == GoalType.MINIMIZE) {
                for (int i = 0; i < n; ++i) {
                    r[i] = -r[i];
                }
            }

            // Compute beta.
            final double deltaOld = delta;
            final double[] newSteepestDescent = preconditioner.precondition(point, r);
            delta = 0;
            for (int i = 0; i < n; ++i) {
                delta += r[i] * newSteepestDescent[i];
            }

            final double beta;
            switch (updateFormula) {
            case FLETCHER_REEVES:
                beta = delta / deltaOld;
                break;
            case POLAK_RIBIERE:
                double deltaMid = 0;
                for (int i = 0; i < r.length; ++i) {
                    deltaMid += r[i] * steepestDescent[i];
                }
                beta = (delta - deltaMid) / deltaOld;
                break;
            default:
                // Should never happen.
                throw new MathInternalError();
            }
            steepestDescent = newSteepestDescent;

            // Compute conjugate search direction.
            if (iter % n == 0 ||
                beta < 0) {
                // Break conjugation: reset search direction.
                searchDirection = steepestDescent.clone();
            } else {
                // Compute new conjugate search direction.
                for (int i = 0; i < n; ++i) {
                    searchDirection[i] = steepestDescent[i] + beta * searchDirection[i];
                }
            }
        }
    }
    protected PointValuePair doOptimize() {
         // -------------------- Initialization --------------------------------
        isMinimize = getGoalType().equals(GoalType.MINIMIZE);
        final FitnessFunction fitfun = new FitnessFunction();
        final double[] guess = getStartPoint();
        // number of objective variables/problem dimension
        dimension = guess.length;
        initializeCMA(guess);
        iterations = 0;
        double bestValue = fitfun.value(guess);
        push(fitnessHistory, bestValue);
        PointValuePair optimum
            = new PointValuePair(getStartPoint(),
                                 isMinimize ? bestValue : -bestValue);
        PointValuePair lastResult = null;

        // -------------------- Generation Loop --------------------------------

        generationLoop:
        for (iterations = 1; iterations <= maxIterations; iterations++) {

            // Generate and evaluate lambda offspring
            final RealMatrix arz = randn1(dimension, lambda);
            final RealMatrix arx = zeros(dimension, lambda);
            final double[] fitness = new double[lambda];
            // generate random offspring
            for (int k = 0; k < lambda; k++) {
                RealMatrix arxk = null;
                for (int i = 0; i < checkFeasableCount + 1; i++) {
                    if (diagonalOnly <= 0) {
                        arxk = xmean.add(BD.multiply(arz.getColumnMatrix(k))
                                         .scalarMultiply(sigma)); // m + sig * Normal(0,C)
                    } else {
                        arxk = xmean.add(times(diagD,arz.getColumnMatrix(k))
                                         .scalarMultiply(sigma));
                    }
                    if (i >= checkFeasableCount ||
                        fitfun.isFeasible(arxk.getColumn(0))) {
                        break;
                    }
                    // regenerate random arguments for row
                    arz.setColumn(k, randn(dimension));
                }
                copyColumn(arxk, 0, arx, k);
                try {
                    fitness[k] = fitfun.value(arx.getColumn(k)); // compute fitness
                } catch (TooManyEvaluationsException e) {
                    break generationLoop;
                }
            }
            // Sort by fitness and compute weighted mean into xmean
            final int[] arindex = sortedIndices(fitness);
            // Calculate new xmean, this is selection and recombination
            final RealMatrix xold = xmean; // for speed up of Eq. (2) and (3)
            final RealMatrix bestArx = selectColumns(arx, MathArrays.copyOf(arindex, mu));
            xmean = bestArx.multiply(weights);
            final RealMatrix bestArz = selectColumns(arz, MathArrays.copyOf(arindex, mu));
            final RealMatrix zmean = bestArz.multiply(weights);
            final boolean hsig = updateEvolutionPaths(zmean, xold);
            if (diagonalOnly <= 0) {
                updateCovariance(hsig, bestArx, arz, arindex, xold);
            } else {
                updateCovarianceDiagonalOnly(hsig, bestArz);
            }
            // Adapt step size sigma - Eq. (5)
            sigma *= Math.exp(Math.min(1, (normps/chiN - 1) * cs / damps));
            final double bestFitness = fitness[arindex[0]];
            final double worstFitness = fitness[arindex[arindex.length - 1]];
            if (bestValue > bestFitness) {
                bestValue = bestFitness;
                lastResult = optimum;
                optimum = new PointValuePair(fitfun.repair(bestArx.getColumn(0)),
                                             isMinimize ? bestFitness : -bestFitness);
                if (getConvergenceChecker() != null &&
                    lastResult != null) {
                    if (getConvergenceChecker().converged(iterations, optimum, lastResult)) {
                        break generationLoop;
                    }
                }
            }
            // handle termination criteria
            // Break, if fitness is good enough
            if (stopFitness != 0) { // only if stopFitness is defined
                if (bestFitness < (isMinimize ? stopFitness : -stopFitness)) {
                    break generationLoop;
                }
            }
            final double[] sqrtDiagC = sqrt(diagC).getColumn(0);
            final double[] pcCol = pc.getColumn(0);
            for (int i = 0; i < dimension; i++) {
                if (sigma * Math.max(Math.abs(pcCol[i]), sqrtDiagC[i]) > stopTolX) {
                    break;
                }
                if (i >= dimension - 1) {
                    break generationLoop;
                }
            }
            for (int i = 0; i < dimension; i++) {
                if (sigma * sqrtDiagC[i] > stopTolUpX) {
                    break generationLoop;
                }
            }
            final double historyBest = min(fitnessHistory);
            final double historyWorst = max(fitnessHistory);
            if (iterations > 2 &&
                Math.max(historyWorst, worstFitness) -
                Math.min(historyBest, bestFitness) < stopTolFun) {
                break generationLoop;
            }
            if (iterations > fitnessHistory.length &&
                historyWorst - historyBest < stopTolHistFun) {
                break generationLoop;
            }
            // condition number of the covariance matrix exceeds 1e14
            if (max(diagD) / min(diagD) > 1e7) {
                break generationLoop;
            }
            // user defined termination
            if (getConvergenceChecker() != null) {
                final PointValuePair current
                    = new PointValuePair(bestArx.getColumn(0),
                                         isMinimize ? bestFitness : -bestFitness);
                if (lastResult != null &&
                    getConvergenceChecker().converged(iterations, current, lastResult)) {
                    break generationLoop;
                    }
                lastResult = current;
            }
            // Adjust step size in case of equal function values (flat fitness)
            if (bestValue == fitness[arindex[(int)(0.1+lambda/4.)]]) {
                sigma = sigma * Math.exp(0.2 + cs / damps);
            }
            if (iterations > 2 && Math.max(historyWorst, bestFitness) -
                Math.min(historyBest, bestFitness) == 0) {
                sigma = sigma * Math.exp(0.2 + cs / damps);
            }
            // store best in history
            push(fitnessHistory,bestFitness);
            fitfun.setValueRange(worstFitness-bestFitness);
            if (generateStatistics) {
                statisticsSigmaHistory.add(sigma);
                statisticsFitnessHistory.add(bestFitness);
                statisticsMeanHistory.add(xmean.transpose());
                statisticsDHistory.add(diagD.transpose().scalarMultiply(1E5));
            }
        }
        return optimum;
    }
    protected PointValuePair doOptimize() {
        checkParameters();

        final GoalType goal = getGoalType();
        final double[] guess = getStartPoint();
        final int n = guess.length;

        final double[][] direc = new double[n][n];
        for (int i = 0; i < n; i++) {
            direc[i][i] = 1;
        }

        final ConvergenceChecker<PointValuePair> checker
            = getConvergenceChecker();

        double[] x = guess;
        double fVal = computeObjectiveValue(x);
        double[] x1 = x.clone();
        int iter = 0;
        while (true) {
            ++iter;

            double fX = fVal;
            double fX2 = 0;
            double delta = 0;
            int bigInd = 0;
            double alphaMin = 0;

            for (int i = 0; i < n; i++) {
                final double[] d = MathArrays.copyOf(direc[i]);

                fX2 = fVal;

                final UnivariatePointValuePair optimum = line.search(x, d);
                fVal = optimum.getValue();
                alphaMin = optimum.getPoint();
                final double[][] result = newPointAndDirection(x, d, alphaMin);
                x = result[0];

                if ((fX2 - fVal) > delta) {
                    delta = fX2 - fVal;
                    bigInd = i;
                }
            }

            // Default convergence check.
            boolean stop = 2 * (fX - fVal) <=
                (relativeThreshold * (FastMath.abs(fX) + FastMath.abs(fVal)) +
                 absoluteThreshold);

            final PointValuePair previous = new PointValuePair(x1, fX);
            final PointValuePair current = new PointValuePair(x, fVal);
            if (!stop) { // User-defined stopping criteria.
                if (checker != null) {
                    stop = checker.converged(iter, previous, current);
                }
            }
            if (stop) {
                if (goal == GoalType.MINIMIZE) {
                    return (fVal < fX) ? current : previous;
                } else {
                    return (fVal > fX) ? current : previous;
                }
            }

            final double[] d = new double[n];
            final double[] x2 = new double[n];
            for (int i = 0; i < n; i++) {
                d[i] = x[i] - x1[i];
                x2[i] = 2 * x[i] - x1[i];
            }

            x1 = x.clone();
            fX2 = computeObjectiveValue(x2);

            if (fX > fX2) {
                double t = 2 * (fX + fX2 - 2 * fVal);
                double temp = fX - fVal - delta;
                t *= temp * temp;
                temp = fX - fX2;
                t -= delta * temp * temp;

                if (t < 0.0) {
                    final UnivariatePointValuePair optimum = line.search(x, d);
                    fVal = optimum.getValue();
                    alphaMin = optimum.getPoint();
                    final double[][] result = newPointAndDirection(x, d, alphaMin);
                    x = result[0];

                    final int lastInd = n - 1;
                    direc[bigInd] = direc[lastInd];
                    direc[lastInd] = result[1];
                }
            }
        }
    }
    protected PointValuePair doOptimize() {
        checkParameters();

        // Indirect call to "computeObjectiveValue" in order to update the
        // evaluations counter.
        final MultivariateFunction evalFunc
            = new MultivariateFunction() {
                public double value(double[] point) {
                    return computeObjectiveValue(point);
                }
            };

        final boolean isMinim = getGoalType() == GoalType.MINIMIZE;
        final Comparator<PointValuePair> comparator
            = new Comparator<PointValuePair>() {
            public int compare(final PointValuePair o1,
                               final PointValuePair o2) {
                final double v1 = o1.getValue();
                final double v2 = o2.getValue();
                return isMinim ? Double.compare(v1, v2) : Double.compare(v2, v1);
            }
        };

        // Initialize search.
        simplex.build(getStartPoint());
        simplex.evaluate(evalFunc, comparator);

        PointValuePair[] previous = null;
        int iteration = 0;
        final ConvergenceChecker<PointValuePair> checker = getConvergenceChecker();
        while (true) {
            if (iteration > 0) {
                boolean converged = true;
                for (int i = 0; i < simplex.getSize(); i++) {
                    PointValuePair prev = previous[i];
                    converged = converged &&
                        checker.converged(iteration, prev, simplex.getPoint(i));
                }
                if (converged) {
                    // We have found an optimum.
                    return simplex.getPoint(0);
                }
            }

            // We still need to search.
            previous = simplex.getPoints();
            simplex.iterate(evalFunc, comparator);

			++iteration;
        }
    }
    public PointVectorValuePair doOptimize() {
        checkParameters();

        final ConvergenceChecker<PointVectorValuePair> checker
            = getConvergenceChecker();

        // Computation will be useless without a checker (see "for-loop").
        if (checker == null) {
            throw new NullArgumentException();
        }

        final double[] targetValues = getTarget();
        final int nR = targetValues.length; // Number of observed data.

        final RealMatrix weightMatrix = getWeight();
        // Diagonal of the weight matrix.
        final double[] residualsWeights = new double[nR];
        for (int i = 0; i < nR; i++) {
            residualsWeights[i] = weightMatrix.getEntry(i, i);
        }

        final double[] currentPoint = getStartPoint();
        final int nC = currentPoint.length;

        // iterate until convergence is reached
        PointVectorValuePair current = null;
        int iter = 0;
        for (boolean converged = false; !converged;) {
            ++iter;

            // evaluate the objective function and its jacobian
            PointVectorValuePair previous = current;
            // Value of the objective function at "currentPoint".
            final double[] currentObjective = computeObjectiveValue(currentPoint);
            final double[] currentResiduals = computeResiduals(currentObjective);
            final RealMatrix weightedJacobian = computeWeightedJacobian(currentPoint);
            current = new PointVectorValuePair(currentPoint, currentObjective);

            // build the linear problem
            final double[]   b = new double[nC];
            final double[][] a = new double[nC][nC];
            for (int i = 0; i < nR; ++i) {

                final double[] grad   = weightedJacobian.getRow(i);
                final double weight   = residualsWeights[i];
                final double residual = currentResiduals[i];

                // compute the normal equation
                final double wr = weight * residual;
                for (int j = 0; j < nC; ++j) {
                    b[j] += wr * grad[j];
                }

                // build the contribution matrix for measurement i
                for (int k = 0; k < nC; ++k) {
                    double[] ak = a[k];
                    double wgk = weight * grad[k];
                    for (int l = 0; l < nC; ++l) {
                        ak[l] += wgk * grad[l];
                    }
                }
            }

            try {
                // solve the linearized least squares problem
                RealMatrix mA = new BlockRealMatrix(a);
                DecompositionSolver solver = useLU ?
                        new LUDecomposition(mA).getSolver() :
                        new QRDecomposition(mA).getSolver();
                final double[] dX = solver.solve(new ArrayRealVector(b, false)).toArray();
                // update the estimated parameters
                for (int i = 0; i < nC; ++i) {
                    currentPoint[i] += dX[i];
                }
            } catch (SingularMatrixException e) {
                throw new ConvergenceException(LocalizedFormats.UNABLE_TO_SOLVE_SINGULAR_PROBLEM);
            }

            // Check convergence.
            if (previous != null) {
                converged = checker.converged(iter, previous, current);
                if (converged) {
                    setCost(computeCost(currentResiduals));
                    return current;
                }
            }
        }
        // Must never happen.
        throw new MathInternalError();
    }
    protected PointVectorValuePair doOptimize() {
        checkParameters();

        final int nR = getTarget().length; // Number of observed data.
        final double[] currentPoint = getStartPoint();
        final int nC = currentPoint.length; // Number of parameters.

        // arrays shared with the other private methods
        solvedCols  = FastMath.min(nR, nC);
        diagR       = new double[nC];
        jacNorm     = new double[nC];
        beta        = new double[nC];
        permutation = new int[nC];
        lmDir       = new double[nC];

        // local point
        double   delta   = 0;
        double   xNorm   = 0;
        double[] diag    = new double[nC];
        double[] oldX    = new double[nC];
        double[] oldRes  = new double[nR];
        double[] oldObj  = new double[nR];
        double[] qtf     = new double[nR];
        double[] work1   = new double[nC];
        double[] work2   = new double[nC];
        double[] work3   = new double[nC];

        final RealMatrix weightMatrixSqrt = getWeightSquareRoot();

        // Evaluate the function at the starting point and calculate its norm.
        double[] currentObjective = computeObjectiveValue(currentPoint);
        double[] currentResiduals = computeResiduals(currentObjective);
        PointVectorValuePair current = new PointVectorValuePair(currentPoint, currentObjective);
        double currentCost = computeCost(currentResiduals);

        // Outer loop.
        lmPar = 0;
        boolean firstIteration = true;
        int iter = 0;
        final ConvergenceChecker<PointVectorValuePair> checker = getConvergenceChecker();
        while (true) {
            ++iter;
            final PointVectorValuePair previous = current;

            // QR decomposition of the jacobian matrix
            qrDecomposition(computeWeightedJacobian(currentPoint));

            weightedResidual = weightMatrixSqrt.operate(currentResiduals);
            for (int i = 0; i < nR; i++) {
                qtf[i] = weightedResidual[i];
            }

            // compute Qt.res
            qTy(qtf);

            // now we don't need Q anymore,
            // so let jacobian contain the R matrix with its diagonal elements
            for (int k = 0; k < solvedCols; ++k) {
                int pk = permutation[k];
                weightedJacobian[k][pk] = diagR[pk];
            }

            if (firstIteration) {
                // scale the point according to the norms of the columns
                // of the initial jacobian
                xNorm = 0;
                for (int k = 0; k < nC; ++k) {
                    double dk = jacNorm[k];
                    if (dk == 0) {
                        dk = 1.0;
                    }
                    double xk = dk * currentPoint[k];
                    xNorm  += xk * xk;
                    diag[k] = dk;
                }
                xNorm = FastMath.sqrt(xNorm);

                // initialize the step bound delta
                delta = (xNorm == 0) ? initialStepBoundFactor : (initialStepBoundFactor * xNorm);
            }

            // check orthogonality between function vector and jacobian columns
            double maxCosine = 0;
            if (currentCost != 0) {
                for (int j = 0; j < solvedCols; ++j) {
                    int    pj = permutation[j];
                    double s  = jacNorm[pj];
                    if (s != 0) {
                        double sum = 0;
                        for (int i = 0; i <= j; ++i) {
                            sum += weightedJacobian[i][pj] * qtf[i];
                        }
                        maxCosine = FastMath.max(maxCosine, FastMath.abs(sum) / (s * currentCost));
                    }
                }
            }
            if (maxCosine <= orthoTolerance) {
                // Convergence has been reached.
                setCost(currentCost);
                return current;
            }

            // rescale if necessary
            for (int j = 0; j < nC; ++j) {
                diag[j] = FastMath.max(diag[j], jacNorm[j]);
            }

            // Inner loop.
            for (double ratio = 0; ratio < 1.0e-4;) {

                // save the state
                for (int j = 0; j < solvedCols; ++j) {
                    int pj = permutation[j];
                    oldX[pj] = currentPoint[pj];
                }
                final double previousCost = currentCost;
                double[] tmpVec = weightedResidual;
                weightedResidual = oldRes;
                oldRes    = tmpVec;
                tmpVec    = currentObjective;
                currentObjective = oldObj;
                oldObj    = tmpVec;

                // determine the Levenberg-Marquardt parameter
                determineLMParameter(qtf, delta, diag, work1, work2, work3);

                // compute the new point and the norm of the evolution direction
                double lmNorm = 0;
                for (int j = 0; j < solvedCols; ++j) {
                    int pj = permutation[j];
                    lmDir[pj] = -lmDir[pj];
                    currentPoint[pj] = oldX[pj] + lmDir[pj];
                    double s = diag[pj] * lmDir[pj];
                    lmNorm  += s * s;
                }
                lmNorm = FastMath.sqrt(lmNorm);
                // on the first iteration, adjust the initial step bound.
                if (firstIteration) {
                    delta = FastMath.min(delta, lmNorm);
                }

                // Evaluate the function at x + p and calculate its norm.
                currentObjective = computeObjectiveValue(currentPoint);
                currentResiduals = computeResiduals(currentObjective);
                current = new PointVectorValuePair(currentPoint, currentObjective);
                currentCost = computeCost(currentResiduals);

                // compute the scaled actual reduction
                double actRed = -1.0;
                if (0.1 * currentCost < previousCost) {
                    double r = currentCost / previousCost;
                    actRed = 1.0 - r * r;
                }

                // compute the scaled predicted reduction
                // and the scaled directional derivative
                for (int j = 0; j < solvedCols; ++j) {
                    int pj = permutation[j];
                    double dirJ = lmDir[pj];
                    work1[j] = 0;
                    for (int i = 0; i <= j; ++i) {
                        work1[i] += weightedJacobian[i][pj] * dirJ;
                    }
                }
                double coeff1 = 0;
                for (int j = 0; j < solvedCols; ++j) {
                    coeff1 += work1[j] * work1[j];
                }
                double pc2 = previousCost * previousCost;
                coeff1 = coeff1 / pc2;
                double coeff2 = lmPar * lmNorm * lmNorm / pc2;
                double preRed = coeff1 + 2 * coeff2;
                double dirDer = -(coeff1 + coeff2);

                // ratio of the actual to the predicted reduction
                ratio = (preRed == 0) ? 0 : (actRed / preRed);

                // update the step bound
                if (ratio <= 0.25) {
                    double tmp =
                        (actRed < 0) ? (0.5 * dirDer / (dirDer + 0.5 * actRed)) : 0.5;
                        if ((0.1 * currentCost >= previousCost) || (tmp < 0.1)) {
                            tmp = 0.1;
                        }
                        delta = tmp * FastMath.min(delta, 10.0 * lmNorm);
                        lmPar /= tmp;
                } else if ((lmPar == 0) || (ratio >= 0.75)) {
                    delta = 2 * lmNorm;
                    lmPar *= 0.5;
                }

                // test for successful iteration.
                if (ratio >= 1.0e-4) {
                    // successful iteration, update the norm
                    firstIteration = false;
                    xNorm = 0;
                    for (int k = 0; k < nC; ++k) {
                        double xK = diag[k] * currentPoint[k];
                        xNorm += xK * xK;
                    }
                    xNorm = FastMath.sqrt(xNorm);

                    // tests for convergence.
                    if (checker != null) {
                        // we use the vectorial convergence checker
                        if (checker.converged(iter, previous, current)) {
                            setCost(currentCost);
                            return current;
                        }
                    }
                } else {
                    // failed iteration, reset the previous values
                    currentCost = previousCost;
                    for (int j = 0; j < solvedCols; ++j) {
                        int pj = permutation[j];
                        currentPoint[pj] = oldX[pj];
                    }
                    tmpVec    = weightedResidual;
                    weightedResidual = oldRes;
                    oldRes    = tmpVec;
                    tmpVec    = currentObjective;
                    currentObjective = oldObj;
                    oldObj    = tmpVec;
                    // Reset "current" to previous values.
                    current = new PointVectorValuePair(currentPoint, currentObjective);
                }

                // Default convergence criteria.
                if ((FastMath.abs(actRed) <= costRelativeTolerance &&
                     preRed <= costRelativeTolerance &&
                     ratio <= 2.0) ||
                    delta <= parRelativeTolerance * xNorm) {
                    setCost(currentCost);
                    return current;
                }

                // tests for termination and stringent tolerances
                // (2.2204e-16 is the machine epsilon for IEEE754)
                if ((FastMath.abs(actRed) <= 2.2204e-16) && (preRed <= 2.2204e-16) && (ratio <= 2.0)) {
                    throw new ConvergenceException(LocalizedFormats.TOO_SMALL_COST_RELATIVE_TOLERANCE,
                                                   costRelativeTolerance);
                } else if (delta <= 2.2204e-16 * xNorm) {
                    throw new ConvergenceException(LocalizedFormats.TOO_SMALL_PARAMETERS_RELATIVE_TOLERANCE,
                                                   parRelativeTolerance);
                } else if (maxCosine <= 2.2204e-16)  {
                    throw new ConvergenceException(LocalizedFormats.TOO_SMALL_ORTHOGONALITY_TOLERANCE,
                                                   orthoTolerance);
                }
            }
        }
    }
}
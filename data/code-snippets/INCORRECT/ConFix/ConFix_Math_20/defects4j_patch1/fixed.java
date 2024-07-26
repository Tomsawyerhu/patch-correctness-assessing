public class test {
    private void initializeCMA(double[] guess) {
        if (lambda <= 0) {
            lambda = this.maxIterations + 4 + (int) (3. * Math.log(dimension));
        }
        // initialize sigma
        double[][] sigmaArray = new double[guess.length][1];
        for (int i = 0; i < guess.length; i++) {
            final double range =  (boundaries == null) ? 1.0 : boundaries[1][i] - boundaries[0][i];
            sigmaArray[i][0]   = ((inputSigma == null) ? 0.3 : inputSigma[i]) / range;
        }
        RealMatrix insigma = new Array2DRowRealMatrix(sigmaArray, false);
        sigma = max(insigma); // overall standard deviation

        // initialize termination criteria
        stopTolUpX = 1e3 * max(insigma);
        stopTolX = 1e-11 * max(insigma);
        stopTolFun = 1e-12;
        stopTolHistFun = 1e-13;

        // initialize selection strategy parameters
        mu = lambda / 2; // number of parents/points for recombination
        logMu2 = Math.log(mu + 0.5);
        weights = log(sequence(1, mu, 1)).scalarMultiply(-1.).scalarAdd(logMu2);
        double sumw = 0;
        double sumwq = 0;
        for (int i = 0; i < mu; i++) {
            double w = weights.getEntry(i, 0);
            sumw += w;
            sumwq += w * w;
        }
        weights = weights.scalarMultiply(1. / sumw);
        mueff = sumw * sumw / sumwq; // variance-effectiveness of sum w_i x_i

        // initialize dynamic strategy parameters and constants
        cc = (4. + mueff / dimension) /
                (dimension + 4. + 2. * mueff / dimension);
        cs = (mueff + 2.) / (dimension + mueff + 3.);
        damps = (1. + 2. * Math.max(0, Math.sqrt((mueff - 1.) /
                (dimension + 1.)) - 1.)) *
                Math.max(0.3, 1. - dimension /
                        (1e-6 + Math.min(maxIterations, getMaxEvaluations() /
                                lambda))) + cs; // minor increment
        ccov1 = 2. / ((dimension + 1.3) * (dimension + 1.3) + mueff);
        ccovmu = Math.min(1 - ccov1, 2. * (mueff - 2. + 1. / mueff) /
                ((dimension + 2.) * (dimension + 2.) + mueff));
        ccov1Sep = Math.min(1, ccov1 * (dimension + 1.5) / 3.);
        ccovmuSep = Math.min(1 - ccov1, ccovmu * (dimension + 1.5) / 3.);
        chiN = Math.sqrt(dimension) *
                (1. - 1. / (4. * dimension) + 1 / (21. * dimension * dimension));
        // intialize CMA internal values - updated each generation
        xmean = MatrixUtils.createColumnRealMatrix(guess); // objective
                                                           // variables
        diagD = insigma.scalarMultiply(1. / sigma);
        diagC = square(diagD);
        pc = zeros(dimension, 1); // evolution paths for C and sigma
        ps = zeros(dimension, 1); // B defines the coordinate system
        normps = ps.getFrobeniusNorm();

        B = eye(dimension, dimension);
        D = ones(dimension, 1); // diagonal D defines the scaling
        BD = times(B, repmat(diagD.transpose(), dimension, 1));
        C = B.multiply(diag(square(D)).multiply(B.transpose())); // covariance
        historySize = 10 + (int) (3. * 10. * dimension / lambda);
        fitnessHistory = new double[historySize]; // history of fitness values
        for (int i = 0; i < historySize; i++) {
            fitnessHistory[i] = Double.MAX_VALUE;
        }
    }
}
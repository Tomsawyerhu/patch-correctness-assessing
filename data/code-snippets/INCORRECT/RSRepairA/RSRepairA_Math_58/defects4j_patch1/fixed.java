public class test {
    private void determineLMParameter(double[] qy, double delta, double[] diag,
            double[] work1, double[] work2, double[] work3) {

        // compute and store in x the gauss-newton direction, if the
        // jacobian is rank-deficient, obtain a least squares solution
        for (int j = 0; j < rank; ++j) {
            lmDir[permutation[j]] = qy[j];
        }
        for (int j = rank; j < cols; ++j) {
            lmDir[permutation[j]] = 0;
        }
        for (int k = rank - 1; k >= 0; --k) {
            int pk = permutation[k];
            double ypk = lmDir[pk] / diagR[pk];
            for (int i = 0; i < k; ++i) {
                lmDir[permutation[i]] -= ypk * weightedResidualJacobian[i][pk];
            }
            lmDir[pk] = ypk;
        }

        // evaluate the function at the origin, and test
        // for acceptance of the Gauss-Newton direction
        double dxNorm = 0;
        for (int j = 0; j < solvedCols; ++j) {
            int pj = permutation[j];
            double s = diag[pj] * lmDir[pj];
            work1[pj] = s;
            dxNorm += s * s;
        }
        dxNorm = FastMath.sqrt(dxNorm);
        double fp = dxNorm - delta;
        if (fp <= 0.1 * delta) {
            lmPar = 0;
            return;
        }

        // if the jacobian is not rank deficient, the Newton step provides
        // a lower bound, parl, for the zero of the function,
        // otherwise set this bound to zero
        double sum2;
        double parl = 0;
        if (rank == solvedCols) {
            for (int j = 0; j < solvedCols; ++j) {
                int pj = permutation[j];
                work1[pj] *= diag[pj] / dxNorm;
            }
            sum2 = 0;
            for (int j = 0; j < solvedCols; ++j) {
                int pj = permutation[j];
                double sum = 0;
                for (int i = 0; i < j; ++i) {
                    sum += weightedResidualJacobian[i][pj] * work1[permutation[i]];
                }
                double s = (work1[pj] - sum) / diagR[pj];
                work1[pj] = s;
                sum2 += s * s;
            }
            parl = fp / (delta * sum2);
        }

        // calculate an upper bound, paru, for the zero of the function
        sum2 = 0;
        for (int j = 0; j < solvedCols; ++j) {
            int pj = permutation[j];
            double sum = 0;
            for (int i = 0; i <= j; ++i) {
                sum += weightedResidualJacobian[i][pj] * qy[i];
            }
            sum /= diag[pj];
            sum2 += sum * sum;
        }
        double gNorm = FastMath.sqrt(sum2);
        double paru = gNorm / delta;
        if (paru == 0) {
            // 2.2251e-308 is the smallest positive real for IEE754
            paru = 2.2251e-308 / FastMath.min(delta, 0.1);
        }

        // if the input par lies outside of the interval (parl,paru),
        // set par to the closer endpoint
        lmPar = FastMath.min(paru, FastMath.max(lmPar, parl));
        if (lmPar == 0) {
            lmPar = gNorm / dxNorm;
        }

        for (int countdown = 10; countdown >= 0; --countdown) {

            // evaluate the function at the current value of lmPar
            if (lmPar == 0) {
                lmPar = FastMath.max(2.2251e-308, 0.001 * paru);
            }
            double sPar = FastMath.sqrt(lmPar);
            for (int j = 0; j < solvedCols; ++j) {
                int pj = permutation[j];
                work1[pj] = sPar * diag[pj];
            }
            determineLMDirection(qy, work1, work2, work3);

            dxNorm = 0;
            for (int j = 0; j < solvedCols; ++j) {
                int pj = permutation[j];
                double s = diag[pj] * lmDir[pj];
                work3[pj] = s;
                dxNorm += s * s;
            }
            dxNorm = FastMath.sqrt(dxNorm);
            double previousFP = fp;
            fp = dxNorm - delta;

            // if the function is small enough, accept the current value
            // of lmPar, also test for the exceptional cases where parl is zero
            if ((FastMath.abs(fp) <= 0.1 * delta) ||
                    ((parl == 0) && (fp <= previousFP) && (previousFP < 0))) {
                return;
            }

            for (int j = 0; j < solvedCols; ++j) {
                int pj = permutation[j];
                work1[pj] /= work2[j];
                double tmp = work1[pj];
                for (int i = j + 1; i < solvedCols; ++i) {
                    work1[permutation[i]] -= weightedResidualJacobian[i][pj] * tmp;
                }
            }
            sum2 = 0;
            for (int j = 0; j < solvedCols; ++j) {
                double s = work1[permutation[j]];
                sum2 += s * s;
            }
            double correction = fp / (delta * sum2);

            // depending on the sign of the function, update parl or paru.
            if (fp > 0) {
                parl = FastMath.max(parl, lmPar);
            } else {
				for (int j = rank; j < cols; ++j) {
					lmDir[permutation[j]] = 0;
				}
				if (fp < 0) {
					paru = FastMath.min(paru, lmPar);
				}
			}

            // compute an improved estimate for lmPar
            lmPar = FastMath.max(parl, lmPar + correction);

        }
    }
}
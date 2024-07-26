public class test {
    private void processGeneralBlock(final int n)
        throws InvalidMatrixException {

        // check decomposed matrix data range
        double sumOffDiag = 0;
        for (int i = 0; i < n - 1; ++i) {
            final int fourI = 4 * i;
            final double ei = work[fourI + 2];
            sumOffDiag += ei;
        }

        if (sumOffDiag == 0) {
            // matrix is already diagonal
            return;
        }

        // initial checks for splits (see Parlett & Marques section 3.3)
        flipIfWarranted(n, 2);

        // two iterations with Li's test for initial splits
        initialSplits(n);

        // initialize parameters used by goodStep
        tType = 0;
        dMin1 = 0;
        dMin2 = 0;
        dN    = 0;
        dN1   = 0;
        dN2   = 0;
        tau   = 0;

        // process split segments
        int i0 = 0;
        int n0 = n;
        while (n0 > 0) {

            // retrieve shift that was temporarily stored as a negative off-diagonal element
            sigma    = (n0 == n) ? 0 : -work[4 * n0 - 2];
            sigmaLow = 0;

            // find start of a new split segment to process
            double offDiagMin = (i0 == n0) ? 0 : work[4 * n0 - 6];
            double offDiagMax = 0;
            double diagMax    = work[4 * n0 - 4];
            double diagMin    = diagMax;
            i0 = 0;
            for (int i = 4 * (n0 - 2); i >= 0; i -= 4) {
                if (work[i + 2] <= 0) {
                    i0 = 1 + i / 4;
                    break;
                }
                if (diagMin >= 4 * offDiagMax) {
                    diagMin    = Math.min(diagMin, work[i + 4]);
                    offDiagMax = Math.max(offDiagMax, work[i + 2]);
                }
                diagMax    = Math.max(diagMax, work[i] + work[i + 2]);
                offDiagMin = Math.min(offDiagMin, work[i + 2]);
            }
            work[4 * n0 - 2] = offDiagMin;

            // lower bound of Gershgorin disk
            dMin = -Math.max(0, diagMin - 2 * Math.sqrt(diagMin * offDiagMax));

            pingPong = 0;
            int maxIter = 30 * (n0 - i0);
            for (int k = 0; i0 < n0; ++k) {
                if (k >= maxIter) {
                    throw new InvalidMatrixException(new MaxIterationsExceededException(maxIter));
                }

                // perform one step
                n0 = goodStep(i0, n0);
                pingPong = 1 - pingPong;

                // check for new splits after "ping" steps
                // when the last elements of qd array are very small
                if ((pingPong == 0) && (n0 - i0 > 3) &&
                    (work[4 * n0 - 1] <= TOLERANCE_2 * diagMax) &&
                    (work[4 * n0 - 2] <= TOLERANCE_2 * sigma)) {
                    int split  = i0 - 1;
                    diagMax    = work[4 * i0];
                    offDiagMin = work[4 * i0 + 2];
                    double previousEMin = work[4 * i0 + 3];
                    for (int i = 4 * i0; i < 4 * n0 - 11; i += 4) {
                        if ((work[i + 3] <= TOLERANCE_2 * work[i]) &&
                            (work[i + 2] <= TOLERANCE_2 * sigma)) {
                            // insert a split
                            work[i + 2]  = -sigma;
                            split        = i / 4;
                            diagMax      = 0;
                            offDiagMin   = work[i + 6];
                            previousEMin = work[i + 7];
                        } else {
                            diagMax      = Math.max(diagMax, work[i + 4]);
                            offDiagMin   = Math.min(offDiagMin, work[i + 2]);
                            previousEMin = Math.min(previousEMin, work[i + 3]);
                        }
                    }
                    work[4 * n0 - 2] = offDiagMin;
                    work[4 * n0 - 1] = previousEMin;
                    i0 = split + 1;
                }
            }

        }

    }
    private void computeShiftIncrement(final int start, final int end, final int deflated) {

        final double cnst1 = 0.563;
        final double cnst2 = 1.010;
        final double cnst3 = 1.05;

        // a negative dMin forces the shift to take that absolute value
        // tType records the type of shift.
        if (dMin <= 0.0) {
            tau = -dMin;
            tType = -1;
            return;
        }

        int nn = 4 * end + pingPong - 1;
        switch (deflated) {

        case 0 : // no realEigenvalues deflated.
            if (dMin == dN || dMin == dN1) {

                double b1 = Math.sqrt(work[nn - 3]) * Math.sqrt(work[nn - 5]);
                double b2 = Math.sqrt(work[nn - 7]) * Math.sqrt(work[nn - 9]);
                double a2 = work[nn - 7] + work[nn - 5];

                if (dMin == dN && dMin1 == dN1) {
                    // cases 2 and 3.
                    final double gap2 = dMin2 - a2 - dMin2 * 0.25;
                    final double gap1 = a2 - dN - ((gap2 > 0.0 && gap2 > b2) ? (b2 / gap2) * b2 : (b1 + b2));
                    if (gap1 > 0.0 && gap1 > b1) {
                        tau   = Math.max(dN - (b1 / gap1) * b1, 0.5 * dMin);
                        tType = -2;
                    } else {
                        double s = 0.0;
                        if (dN > b1) {
                            s = dN - b1;
                        }
                        if (a2 > (b1 + b2)) {
                            s = Math.min(s, a2 - (b1 + b2));
                        }
                        tau   = Math.max(s, 0.333 * dMin);
                        tType = -3;
                    }
                } else {
                    // case 4.
                    tType = -4;
                    double s = 0.25 * dMin;
                    double gam;
                    int np;
                    if (dMin == dN) {
                        gam = dN;
                        a2 = 0.0;
                        if (work[nn - 5]  >  work[nn - 7]) {
                            return;
                        }
                        b2 = work[nn - 5] / work[nn - 7];
                        np = nn - 9;
                    } else {
                        np = nn - 2 * pingPong;
                        b2 = work[np - 2];
                        gam = dN1;
                        if (work[np - 4]  >  work[np - 2]) {
                            return;
                        }
                        a2 = work[np - 4] / work[np - 2];
                        if (work[nn - 9]  >  work[nn - 11]) {
                            return;
                        }
                        b2 = work[nn - 9] / work[nn - 11];
                        np = nn - 13;
                    }

                    // approximate contribution to norm squared from i < nn-1.
                    a2 = a2 + b2;
                    for (int i4 = np; i4 >= 4 * start + 2 + pingPong; i4 -= 4) {
                        if(b2 == 0.0) {
                            break;
                        }
                        b1 = b2;
                        if (work[i4]  >  work[i4 - 2]) {
                            return;
                        }
                        b2 = b2 * (work[i4] / work[i4 - 2]);
                        a2 = a2 + b2;
                        if (100 * Math.max(b2, b1) < a2 || cnst1 < a2) {
                            break;
                        }
                    }
                    a2 = cnst3 * a2;

                    // rayleigh quotient residual bound.
                    if (a2 < cnst1) {
                        s = gam * (1 - Math.sqrt(a2)) / (1 + a2);
                    }
                    tau = s;

                }
            } else if (dMin == dN2) {

                // case 5.
                tType = -5;
                double s = 0.25 * dMin;

                // compute contribution to norm squared from i > nn-2.
                final int np = nn - 2 * pingPong;
                double b1 = work[np - 2];
                double b2 = work[np - 6];
                final double gam = dN2;
                if (work[np - 8] > b2 || work[np - 4] > b1) {
                    return;
                }
                double a2 = (work[np - 8] / b2) * (1 + work[np - 4] / b1);

                // approximate contribution to norm squared from i < nn-2.
                if (end - start > 2) {
                    b2 = work[nn - 13] / work[nn - 15];
                    a2 = a2 + b2;
                    for (int i4 = nn - 17; i4 >= 4 * start + 2 + pingPong; i4 -= 4) {
                        if (b2 == 0.0) {
                            break;
                        }
                        b1 = b2;
                        if (work[i4]  >  work[i4 - 2]) {
                            return;
                        }
                        b2 = b2 * (work[i4] / work[i4 - 2]);
                        a2 = a2 + b2;
                        if (100 * Math.max(b2, b1) < a2 || cnst1 < a2)  {
                            break;
                        }
                    }
                    a2 = cnst3 * a2;
                }

                if (a2 < cnst1) {
                    tau = gam * (1 - Math.sqrt(a2)) / (1 + a2);
                } else {
                    tau = s;
                }

            } else {

                // case 6, no information to guide us.
                if (tType == -6) {
                    g += 0.333 * (1 - g);
                } else if (tType == -18) {
                    g = 0.25 * 0.333;
                } else {
                    g = 0.25;
                }
                tau   = g * dMin;
                tType = -6;

            }
            break;

        case 1 : // one eigenvalue just deflated. use dMin1, dN1 for dMin and dN.
            if (dMin1 == dN1 && dMin2 == dN2) {

                // cases 7 and 8.
                tType = -7;
                double s = 0.333 * dMin1;
                if (work[nn - 5] > work[nn - 7]) {
                    return;
                }
                double b1 = work[nn - 5] / work[nn - 7];
                double b2 = b1;
                if (b2 != 0.0) {
                    for (int i4 = 4 * end - 10 + pingPong; i4 >= 4 * start + 2 + pingPong; i4 -= 4) {
                        final double oldB1 = b1;
                        if (work[i4] > work[i4 - 2]) {
                            return;
                        }
                        b1 = b1 * (work[i4] / work[i4 - 2]);
                        b2 = b2 + b1;
                        if (100 * Math.max(b1, oldB1) < b2) {
                            break;
                        }
                    }
                }
                b2 = Math.sqrt(cnst3 * b2);
                final double a2 = dMin1 / (1 + b2 * b2);
                final double gap2 = 0.5 * dMin2 - a2;
                if (gap2 > 0.0 && gap2 > b2 * a2) {
                    tau = Math.max(s, a2 * (1 - cnst2 * a2 * (b2 / gap2) * b2));
                } else {
                    tau = Math.max(s, a2 * (1 - cnst2 * b2));
                    tType = -8;
                }
            } else {

                // case 9.
                tau = 0.25 * dMin1;
                if (dMin1 == dN1) {
                    tau = 0.5 * dMin1;
                }
                tType = -9;
            }
            break;

        case 2 : // two realEigenvalues deflated. use dMin2, dN2 for dMin and dN.

            // cases 10 and 11.
            if (dMin2 == dN2 && 2 * work[nn - 5] < work[nn - 7]) {
                tType = -10;
                final double s = 0.333 * dMin2;
                if (work[nn - 5] > work[nn - 7]) {
                    return;
                }
                double b1 = work[nn - 5] / work[nn - 7];
                double b2 = b1;
                if (b2 != 0.0){
                    for (int i4 = 4 * end - 9 + pingPong; i4 >= 4 * start + 2 + pingPong; i4 -= 4) {
                        if (work[i4] > work[i4 - 2]) {
                            return;
                        }
                        b1 *= work[i4] / work[i4 - 2];
                        b2 += b1;
                        if (100 * b1 < b2) {
                            break;
                        }
                    }
                }
                b2 = Math.sqrt(cnst3 * b2);
                final double a2 = dMin2 / (1 + b2 * b2);
                final double gap2 = work[nn - 7] + work[nn - 9] -
                Math.sqrt(work[nn - 11]) * Math.sqrt(work[nn - 9]) - a2;
                if (gap2 > 0.0 && gap2 > b2 * a2) {
                    tau = Math.max(s, a2 * (1 - cnst2 * a2 * (b2 / gap2) * b2));
                } else {
                    tau = Math.max(s, a2 * (1 - cnst2 * b2));
                }
            } else {
                tau   = 0.25 * dMin2;
                tType = -11;
            }
            break;

        default : // case 12, more than two realEigenvalues deflated. no information.
            tau   = 0.0;
            tType = -12;
        }

    }
    private void computeGershgorinCircles() {

        final int m     = main.length;
        final int lowerStart = 4 * m;
        final int upperStart = 5 * m;
        lowerSpectra = Double.POSITIVE_INFINITY;
        upperSpectra = Double.NEGATIVE_INFINITY;
        double eMax = 0;

        double eCurrent = 0;
        for (int i = 0; i < m - 1; ++i) {

            final double dCurrent = main[i];
            final double ePrevious = eCurrent;
            eCurrent = Math.abs(secondary[i]);
            eMax = Math.max(eMax, eCurrent);
            final double radius = ePrevious + eCurrent;

            final double lower = dCurrent - radius;
            work[lowerStart + i] = lower;
            lowerSpectra = Math.min(lowerSpectra, lower);

            final double upper = dCurrent + radius;
            work[upperStart + i] = upper;
            upperSpectra = Math.max(upperSpectra, upper);

        }

        final double dCurrent = main[m - 1];
        final double lower = dCurrent - eCurrent;
        work[lowerStart + m - 1] = lower;
        lowerSpectra = Math.min(lowerSpectra, lower);
        final double upper = dCurrent + eCurrent;
        work[upperStart + m - 1] = upper;
        minPivot = MathUtils.SAFE_MIN * Math.max(1.0, eMax * eMax);

    }
}
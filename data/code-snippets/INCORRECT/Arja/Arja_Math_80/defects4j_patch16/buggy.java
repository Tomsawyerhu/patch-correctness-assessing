public class test {
    private int goodStep(final int start, final int end) {

        g = 0.0;

        // step 1: accepting realEigenvalues
        int deflatedEnd = end;
        for (boolean deflating = true; deflating;) {

            if (start >= deflatedEnd) {
                // the array has been completely deflated
                return deflatedEnd;
            }

            final int k = 4 * deflatedEnd + pingPong - 1;

            if ((start == deflatedEnd - 1) ||
                ((start != deflatedEnd - 2) &&
                 ((work[k - 5] <= TOLERANCE_2 * (sigma + work[k - 3])) ||
                  (work[k - 2 * pingPong - 4] <= TOLERANCE_2 * work[k - 7])))) {

                // one eigenvalue found, deflate array
                work[4 * deflatedEnd - 4] = sigma + work[4 * deflatedEnd - 4 + pingPong];
                deflatedEnd -= 1;

            } else if ((start == deflatedEnd - 2) ||
                (work[k - 9] <= TOLERANCE_2 * sigma) ||
                (work[k - 2 * pingPong - 8] <= TOLERANCE_2 * work[k - 11])) {

                // two realEigenvalues found, deflate array
                if (work[k - 3] > work[k - 7]) {
                    final double tmp = work[k - 3];
                    work[k - 3] = work[k - 7];
                    work[k - 7] = tmp;
                }

                if (work[k - 5] > TOLERANCE_2 * work[k - 3]) {
                    double t = 0.5 * ((work[k - 7] - work[k - 3]) + work[k - 5]);
                    double s = work[k - 3] * (work[k - 5] / t);
                    if (s <= t) {
                        s = work[k - 3] * work[k - 5] / (t * (1 + Math.sqrt(1 + s / t)));
                    } else {
                        s = work[k - 3] * work[k - 5] / (t + Math.sqrt(t * (t + s)));
                    }
                    t = work[k - 7] + (s + work[k - 5]);
                    work[k - 3] *= work[k - 7] / t;
                    work[k - 7]  = t;
                }
                work[4 * deflatedEnd - 8] = sigma + work[k - 7];
                work[4 * deflatedEnd - 4] = sigma + work[k - 3];
                deflatedEnd -= 2;
            } else {

                // no more realEigenvalues found, we need to iterate
                deflating = false;

            }

        }

        final int l = 4 * deflatedEnd + pingPong - 1;

        // step 2: flip array if needed
        if ((dMin <= 0) || (deflatedEnd < end)) {
            if (flipIfWarranted(deflatedEnd, 1)) {
                dMin2 = Math.min(dMin2, work[l - 1]);
                work[l - 1] =
                    Math.min(work[l - 1],
                             Math.min(work[3 + pingPong], work[7 + pingPong]));
                work[l - 2 * pingPong] =
                    Math.min(work[l - 2 * pingPong],
                             Math.min(work[6 + pingPong], work[6 + pingPong]));
                qMax  = Math.max(qMax, Math.max(work[3 + pingPong], work[7 + pingPong]));
                dMin  = -0.0;
            }
        }

        if ((dMin < 0) ||
            (MathUtils.SAFE_MIN * qMax < Math.min(work[l - 1],
                                                  Math.min(work[l - 9],
                                                           dMin2 + work[l - 2 * pingPong])))) {
            // step 3: choose a shift
            computeShiftIncrement(start, deflatedEnd, end - deflatedEnd);

            // step 4a: dqds
            for (boolean loop = true; loop;) {

                // perform one dqds step with the chosen shift
                dqds(start, deflatedEnd);

                // check result of the dqds step
                if ((dMin >= 0) && (dMin1 > 0)) {
                    // the shift was good
                    updateSigma(tau);
                    return deflatedEnd;
                } else if ((dMin < 0.0) &&
                           (dMin1 > 0.0) &&
                           (work[4 * deflatedEnd - 5 - pingPong] < TOLERANCE * (sigma + dN1)) &&
                           (Math.abs(dN) < TOLERANCE * sigma)) {
                   // convergence hidden by negative DN.
                    work[4 * deflatedEnd - 3 - pingPong] = 0.0;
                    dMin = 0.0;
                    updateSigma(tau);
                    return deflatedEnd;
                } else if (dMin < 0.0) {
                    // tau too big. Select new tau and try again.
                    if (tType < -22) {
                        // failed twice. Play it safe.
                        tau = 0.0;
                    } else if (dMin1 > 0.0) {
                        // late failure. Gives excellent shift.
                        tau = (tau + dMin) * (1.0 - 2.0 * MathUtils.EPSILON);
                        tType -= 11;
                    } else {
                        // early failure. Divide by 4.
                        tau *= 0.25;
                        tType -= 12;
                    }
                } else if (Double.isNaN(dMin)) {
                    tau = 0.0;
                } else {
                    // possible underflow. Play it safe.
                    loop = false;
                }
            }

        }

        // perform a dqd step (i.e. no shift)
        dqd(start, deflatedEnd);

        return deflatedEnd;

    }
    private boolean flipIfWarranted(final int n, final int step) {
        if (1.5 * work[pingPong] < work[4 * (n - 1) + pingPong]) {
            // flip array
            int j = 4 * n - 1;
            for (int i = 0; i < j; i += 4) {
                for (int k = 0; k < 4; k += step) {
                    final double tmp = work[i + k];
                    work[i + k] = work[j - k];
                    work[j - k] = tmp;
                }
                j -= 4;
            }
            return true;
        }
        return false;
    }
}
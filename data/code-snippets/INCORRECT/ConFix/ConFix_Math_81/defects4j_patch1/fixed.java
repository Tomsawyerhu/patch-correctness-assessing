public class test {
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
                double b1 = work[np - 6 - 2];
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
}
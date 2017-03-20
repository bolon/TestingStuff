package com.example.mobile.dagger2test.function.presenter;

/**
 * Created by dyned on 3/2/17.
 */

public class Something {
    public Something() {

    }

    public double multiply(double x, double y) {
        if (x > 1000000 | x == 0 | y == 0)
            throw new IllegalArgumentException("illegal args, must > 0");
        else
            return x * y;
    }
}

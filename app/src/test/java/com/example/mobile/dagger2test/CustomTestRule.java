package com.example.mobile.dagger2test;

import android.util.Log;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Created by dyned on 3/2/17.
 */

public class CustomTestRule implements TestRule {
    Statement base;
    Description description;

    @Override
    public Statement apply(Statement base, Description description) {
        this.base = base;
        this.description = description;

        return new CustomStatement();
    }

    private class CustomStatement extends Statement {

        @Override
        public void evaluate() throws Throwable {
            //Log.w("using custom rule", "start in " + description.getMethodName());

            System.out.println("using custom rule start in " + description.getMethodName());
            try {
                base.evaluate();
            } finally {
                //Log.w("using custom rule", "end");
            }
        }
    }

}

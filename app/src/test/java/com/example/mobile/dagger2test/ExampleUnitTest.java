package com.example.mobile.dagger2test;

import com.example.mobile.dagger2test.function.presenter.Something;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void doSomething() {
        Something s = new Something();

        assertEquals("expected 10 x 10 = 100", 100, s.multiply(10, 10), 0);
    }
}
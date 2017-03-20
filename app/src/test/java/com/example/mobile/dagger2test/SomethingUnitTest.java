package com.example.mobile.dagger2test;

import android.test.AndroidTestCase;

import com.example.mobile.dagger2test.function.presenter.Something;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.Assert.assertEquals;

/**
 * Created by dyned on 3/2/17.
 */

public class SomethingUnitTest{
    Something s;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Rule
    public CustomTestRule customTestRule = new CustomTestRule();

    @Before
    public void initTestEnv(){
        s = new Something();
    }

    @Test
    public void multiplyIsCorrect() {
        assertEquals("expected 10 x 10 = 100", 100, s.multiply(10, 10), 0);
        assertEquals("expected 10 x 100 = 1000", 1000, s.multiply(10, 100), 0);
        assertEquals("expected 10 x 1000 = 10000", 10000, s.multiply(10, 1000), 0);
        assertEquals("expected 10 x 10000 = 100000", 100000, s.multiply(10, 10000), 0);
    }

    @Test
    public void multiplyYIncorrect1() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("illegal args?");
        s.multiply(1000001, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void multiplyXIncorrect() {
        s.multiply(0, 100);
    }
}

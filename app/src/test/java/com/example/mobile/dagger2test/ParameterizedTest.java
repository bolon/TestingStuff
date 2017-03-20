package com.example.mobile.dagger2test;

import android.util.Log;

import com.example.mobile.dagger2test.function.presenter.Something;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import timber.log.Timber;

import static junit.framework.Assert.assertEquals;

/**
 * Created by dyned on 3/13/17.
 */

@RunWith(Parameterized.class)
public class ParameterizedTest {
    @Parameterized.Parameter
    public int m1;

    @Parameterized.Parameter(value = 1)
    public int m2;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][]{
                {1, 2}, {5, 3}, {7, 8}, {8, 3}, {6, 8}
        };

        return Arrays.asList(data);
    }

    @Test
    public void multiplyTest(){
        Something s = new Something();

        assertEquals("expected 10 x 10 = 100", m1 * m2, s.multiply(m1, m2), 0);
        System.out.println(m1 + "," + m2);
    }
}

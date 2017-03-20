package com.example.mobile.dagger2test;

import android.support.annotation.Nullable;
import android.util.Log;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.*;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Iterator;

import timber.log.Timber;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by Fernando on 3/20/17.
 */

public class UsingMockitoTest {
    @Mock
    ClassDBMock classDBMock;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    /**
     * verify if the argument passed between dependent class is correct
     */
    @Test
    public void testQueryClassDB() {
        ClassNeedDB classNeedDB = new ClassNeedDB(classDBMock);
        boolean check = classNeedDB.doQueryFromHere("* from tbl_1");
        assertTrue(check);
        verify(classDBMock).doSomething1("* from tbl_1");
    }

    /**
     * return custom value from mocked class
     */
    @Test
    public void testQueryClassDBWithCustomReturn() {
        String param = "test2";
        ClassDBMock classDBMock2 = Mockito.mock(ClassDBMock.class);

        when(classDBMock2.doSomething2(param))
                .thenReturn(param + "_customized");

        assertEquals("Expected result = test2_customized", param + "_customized", classDBMock2.doSomething2(param));
    }

    /**
     * Demonstrate how to mock object with multiple emitted value (maybe useful in mock observable?)
     */
    @Test
    public void testReturnMultipleValue() {
        Iterator iterator = Mockito.mock(Iterator.class);
        when(iterator.next())
                .thenReturn("sada")
                .thenReturn("dua")
                .thenReturn("toru");

        String s = iterator.next() + " " + iterator.next() + " " + iterator.next();

        assertEquals("Expected = sada dua toru", "sada dua toru", s);
    }

    /**
     * Demonstrate how to mock object with return value based on parameter
     */
    @Test
    public void testCustomParam() {
        ClassDBMock cm = Mockito.mock(ClassDBMock.class);
        when(cm.doSomething2("1")).thenReturn("sada");
        when(cm.doSomething2("2")).thenReturn("dua");

        assertEquals("Expected = sada", "sada", cm.doSomething2("1"));
    }

    /**
     * Demonstrate checking type of param.
     * In this case return true if parameter inputted is boolean.
     */
    @Test
    public void testTypeOfParam() {
        ClassDBMock cm = Mockito.mock(ClassDBMock.class);
        when(cm.doSomething2(isA(Boolean.class))).thenReturn(true);

        assertEquals(true, cm.doSomething2(true));
    }

    /**
     * Below demonstrate the ability of mockito to keep track method calls and the parameter.
     */
    @Test
    public void testBehaviorClass() {
        ClassDBMock cm = Mockito.mock(ClassDBMock.class);

        cm.doSomething2(false);
        cm.doSomething1("asd");
        cm.doSomething2(true);
        cm.doSomething2(true);

        verify(cm, never()).doSomething2("test2");
        verify(cm, atLeastOnce()).doSomething1("asd");
        verify(cm, times(2)).doSomething2(true);
        verify(cm, atMost(1)).doSomething2(false);
    }

    class ClassDBMock {
        ClassDBMock() {
        }

        void doSomething1(@Nullable String param1) {
            Timber.i("hi " + param1);
        }

        String doSomething2(@Nullable String param1) {
            return param1;
        }

        boolean doSomething2(boolean b) {
            return b;
        }
    }

    class ClassNeedDB {
        ClassDBMock classDBMock;

        ClassNeedDB(ClassDBMock classDBMock) {
            this.classDBMock = classDBMock;
        }

        boolean doQueryFromHere(String param) {
            classDBMock.doSomething1(param);

            return true;
        }
    }
}

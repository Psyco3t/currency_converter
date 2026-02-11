package com.example.currencyconverter;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Ordering;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.mock.*;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.Assert.*;


import android.content.Context;

import androidx.annotation.NonNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;



/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
/*public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
}*/


@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class DataLayerTest{
    String apiKey = BuildConfig.exchangeRate_API_key;
    String url = "https://v6.exchangerate-api.com/v6/"+ apiKey +"/latest/EUR";
    @Mock
    private Context mockContext;
    @Mock
    private Requests requests;

    /*@Before
    public void setUp()
    {
        when(mockContext.getString(R.string.app_name)).thenReturn("Test String");
    }*/
    @Test
    public void RequestsTest(){
        //note to self: KILL ME NOW!!
        Requests requests = mock(Requests.class);
        try {
            requests.GetLatestRate(url);
            verify(requests, atLeastOnce()).GetLatestRate(url);
        } catch (IOException e) {
            fail();
        }
    }
    @Test
    public void WriteRatesTest(){
        Requests requests = mock(Requests.class);
        when(requests.InvokeWriteRates()).thenReturn(true);
        boolean result = requests.InvokeWriteRates();
        assertTrue(result);
    }

    @Test
    public void AllRatesTest(){
        Requests requests = mock(Requests.class);
        JSONObject rates = requests.AllRates(mockContext);
        assertEquals(rates,requests.AllRates(mockContext));

        when(requests.AllRates(mockContext)).thenReturn(null);
        JSONObject ratesNull = requests.AllRates(mockContext);
        JSONObject nullObj = null;

        assertEquals(nullObj, ratesNull);

        verify(requests,atLeastOnce()).AllRates(mockContext);
    }

}
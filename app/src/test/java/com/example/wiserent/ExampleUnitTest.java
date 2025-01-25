package com.example.wiserent;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void testSetGetAddress() {
        // 1. Set up conditions
        Property property = new Property("1", "רמבם 11", 1000.0, "מושיקו");

        // 2. Call the function under test
        property.setAddress("רמבם 5");

        // 3. Assertions
        assertEquals("רמבם 5", property.getAddress());
    }
    @Test
    public void testSetGetRenterId() {
        // 1. Set up conditions
        Property property = new Property("1", "אביבים 2", 1000.0, "ציפורה");

        // 2. Call the function under test
        property.setRenterId("ציפורה");

        // 3. Assertions
        assertEquals("ציפורה", property.getRenterId());
    }
    @Test
    public void testGetRentAmount() {
        // 1. Set up conditions
        Property property = new Property("1", "החרמון 8", 1000.0, "שלג");

        // 2. Call the function under test (getRentAmount)
        double rentAmount = property.getRentAmount();

        // 3. Assertions
        assertEquals(1000.0, rentAmount, 0.0); // 0.0 is the delta for floating-point comparison
    }


}
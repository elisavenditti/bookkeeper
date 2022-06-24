package org.apache.bookkeeper.util;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.*;

@RunWith(Parameterized.class)
public class TestAvailability extends TestCase {


    private AvailabilityOfEntriesOfLedger availabilityOfEntriesOfLedger; // tested object
    private long firstParam;
    private boolean expectedResult;                // expected result


    @Parameters
    public static Collection<Object[]> data(){

        long lastConfirmed = mockSimulationGetLastConfirmed();

        return Arrays.asList(new Object[][]{
                {-1L, false},
                {0, false},
                {lastConfirmed, false},
                {lastConfirmed+1, false},
                {-2L, false},
                {1, false},
                {lastConfirmed-1, true}
        });
    }

    private static long mockSimulationGetLastConfirmed(){
        return 10L;
    }


    public TestAvailability(long startEntryId, boolean expectedResult){

        // method params configuration
        this.firstParam = startEntryId;
        this.expectedResult = expectedResult;

        // SUT configuration
        long[] containedInBookie0 = {5, 7, 9};
        configure(containedInBookie0);

    }
    private void configure(long[] containedInBookie0){
        PrimitiveIterator.OfLong primitiveIterator = Arrays.stream(containedInBookie0).iterator();
        this.availabilityOfEntriesOfLedger = new AvailabilityOfEntriesOfLedger(primitiveIterator);
    }



    @Test
    public void testAvailabilityOfEntries(){

        boolean actual = this.availabilityOfEntriesOfLedger.isEntryAvailable(this.firstParam);
        System.out.println(actual);
        assertEquals(this.expectedResult, actual);

    }

}


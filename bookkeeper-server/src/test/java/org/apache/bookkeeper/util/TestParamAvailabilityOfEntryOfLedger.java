package org.apache.bookkeeper.util;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.*;

@RunWith(Parameterized.class)
public class TestParamAvailabilityOfEntryOfLedger extends TestCase {

    @Parameters
    public static Collection<Object[]> data(){
        // Return a set (testedInstance, firstParam, expectedResult)
        long lastConfirmed = mockSimulationGetLastConfirmed();
        BitSet expectedEntries = new BitSet((int) lastConfirmed);
        boolean available;
        for(int i=0; i<lastConfirmed; i++){                                   // expected entries = [1,3,5,7,9]
            if(i%2==0) available = false;
            else available = true;
            if(available) expectedEntries.set(i);
        }

        List<Long> unavailable = new ArrayList<>();                 // valore dedotto dalle specifiche
        unavailable.add(1L);
        unavailable.add(3L);


        List<Long> exception = new ArrayList<>();                   // per come l'ho implementata significa che è eccezione
        exception.add(-1L);

        BitSet excedingBitset = new BitSet((int)lastConfirmed+1);
        for(int k=0; k<=lastConfirmed; k++){
            if(k%2==0 || k==lastConfirmed) available = false;
            else available = true;
            if(available) expectedEntries.set(k);
        }
        return Arrays.asList(new Object[][]{
                {-1L, -2L, new BitSet(), new ArrayList<>()},
                {0, lastConfirmed, expectedEntries, unavailable},
                {lastConfirmed, lastConfirmed, excedingBitset, new ArrayList<>()},
                {lastConfirmed+1, lastConfirmed+1,null, exception}
        });
    }

    private static long mockSimulationGetLastConfirmed(){
        return 10L;
    }

    private final AvailabilityOfEntriesOfLedger availabilityOfEntriesOfLedger; // tested object
    private final long firstParam;
    private final long secondParam;
    private final BitSet expectedInBookie;                  // provided input
    private final List<Long> expectedResult;                // expected result

    public TestParamAvailabilityOfEntryOfLedger(long startEntryId, long lastEntryId, BitSet expectedEntries, List<Long> expectedResult){

        // method patams configuration
        this.firstParam = startEntryId;
        this.secondParam = lastEntryId;
        this.expectedResult = new ArrayList<>();
        this.expectedResult.addAll(expectedResult);
        this.expectedInBookie = expectedEntries;

        // SUT configuration
        long[] containedInBookie0 = {5, 7, 9};               // HARDCODED
        PrimitiveIterator.OfLong primitiveIterator = Arrays.stream(containedInBookie0).iterator();
        this.availabilityOfEntriesOfLedger = new AvailabilityOfEntriesOfLedger(primitiveIterator);

    }



    @Test
    public void testAvailabilityOfEntries(){
        List<Long> actual;
        try {
            actual = this.availabilityOfEntriesOfLedger.getUnavailableEntries(this.firstParam, this.secondParam, this.expectedInBookie);
        } catch (NullPointerException e){
            actual = new ArrayList<>();
            actual.add(-1L);
        }

        System.out.println(actual);
        assertEquals(this.expectedResult, actual);

    }



}

package org.apache.bookkeeper.util;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.*;

@RunWith(Parameterized.class)
public class TestParamAvailabilityOfEntryOfLedger extends TestCase {


    private AvailabilityOfEntriesOfLedger availabilityOfEntriesOfLedger; // tested object
    private long firstParam;
    private long secondParam;
    private BitSet expectedInBookie;                  // provided input
    private List<Long> expectedResult;                // expected result


    @Parameters
    public static Collection<Object[]> data(){
        // Return a set (testedInstance, firstParam, expectedResult)
        long lastConfirmed = mockSimulationGetLastConfirmed();
        BitSet expectedEntries = new BitSet((int) lastConfirmed);
        boolean available;
        for(int i=0; i<lastConfirmed; i++){                          // expected entries = [1,3,5,7,9]
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
            if(k%2==0) available = false;
            else available = true;
            if(available) excedingBitset.set(k);
        }

        BitSet allEntriesPresentBitset = new BitSet((int) lastConfirmed);
        for(int k=0; k<lastConfirmed; k++)
            allEntriesPresentBitset.set(k);
        List<Long> allEntriesPresent = new ArrayList<>();
        for(int k=0; k<lastConfirmed;k++){
            if(k!=5 && k!=7 && k!=9)allEntriesPresent.add((long)k);
        }

        BitSet lastAvailableBitset = new BitSet((int) lastConfirmed);
        lastAvailableBitset.set((int)lastConfirmed-1);
        //List<Long> lastAvailable = new ArrayList<>();
        //lastAvailable.add(lastConfirmed);


        return Arrays.asList(new Object[][]{
                {-1L, -2L, new BitSet(), new ArrayList<>()},
                {0, lastConfirmed, expectedEntries, unavailable},
                {lastConfirmed, lastConfirmed, excedingBitset, new ArrayList<>()},
                {lastConfirmed+1, lastConfirmed+1, null, exception},
                {0, lastConfirmed, null, exception},
                {1L, lastConfirmed, expectedEntries.get(1, (int)lastConfirmed), unavailable},
                {1L, lastConfirmed-1, expectedEntries.get(1, (int)lastConfirmed), unavailable},
                {1L, 2L, expectedEntries.get(1, (int)lastConfirmed), entryBetween(unavailable, 1L, 2L)},
                {lastConfirmed-1, lastConfirmed, expectedEntries.get((int)lastConfirmed-1, (int)lastConfirmed), entryBetween(unavailable, lastConfirmed-1, lastConfirmed)},
                {0, lastConfirmed+1, excedingBitset, unavailable},
                {0, lastConfirmed+1,expectedEntries.get(0,2), entryBetween(unavailable, 0, 2)},
                {0, lastConfirmed, allEntriesPresentBitset, allEntriesPresent },
                {0, lastConfirmed, new BitSet((int)lastConfirmed), new ArrayList<>()},
                {0, lastConfirmed, lastAvailableBitset, new ArrayList<>()}//lastAvailable}
        });
    }

    private static List<Long> entryBetween(List<Long> unavailable, long startEntryId, long lastEntryId){
        ArrayList<Long> filter = new ArrayList<>();

        for(Long l: unavailable){
            if(l<startEntryId || l>lastEntryId) continue;
            else
                filter.add(l);
        }
       return filter;

    }

    private static long mockSimulationGetLastConfirmed(){
        return 10L;
    }



    public TestParamAvailabilityOfEntryOfLedger(long startEntryId, long lastEntryId, BitSet expectedEntries,
                                                List<Long> expectedResult){

        // method params configuration
        this.firstParam = startEntryId;
        this.secondParam = lastEntryId;
        this.expectedResult = new ArrayList<>();
        this.expectedResult.addAll(expectedResult);
        this.expectedInBookie = expectedEntries;

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

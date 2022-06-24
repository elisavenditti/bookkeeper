package org.apache.bookkeeper.util;
import org.apache.bookkeeper.client.LedgerHandle;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import junit.framework.TestCase;
import org.junit.Test;

import java.security.GeneralSecurityException;
import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class TestParamAvailabilityOfEntryOfLedger extends TestCase {


    private AvailabilityOfEntriesOfLedger availabilityOfEntriesOfLedger;
    private long firstParam;
    private long secondParam;
    private BitSet expectedInBookie;
    private List<Long> expectedResult;


    @Parameters
    public static Collection<Object[]> data(){

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

        BitSet excedingBitset = new BitSet((int)lastConfirmed+2);   // sono presenti tutte le entry
        excedingBitset.flip(0, (int)lastConfirmed+3);   // l'ultimo indice è esclusivo

        BitSet excedingBitsetZero = new BitSet((int)lastConfirmed+1);   // sono assenti tutte le entry

        BitSet bitsetTrueDim2 = new BitSet(2);
        bitsetTrueDim2.set(0);
        bitsetTrueDim2.set(1);

        BitSet allEntriesPresentBitset = new BitSet((int) lastConfirmed+1);
        for(int k=0; k<lastConfirmed; k++)
            allEntriesPresentBitset.set(k);
        List<Long> allEntriesPresent = new ArrayList<>();
        for(int k=0; k<lastConfirmed+2;k++){
            if(k!=5 && k!=7 && k!=9)allEntriesPresent.add((long)k);
        }

        List<Long> tenAbsent = new ArrayList<>();
        tenAbsent.add(10L);

        BitSet lastAvailableBitset = new BitSet((int) lastConfirmed);
        lastAvailableBitset.set((int)lastConfirmed-1);


        long[] containedInBookie0 = {5, 7, 9};
        long[] containedInBookie1 = {};

        List<Long> outlierNotAvailable = new ArrayList<>();
        outlierNotAvailable.add(lastConfirmed+1);
        outlierNotAvailable.add(lastConfirmed+2);

        return Arrays.asList(new Object[][]{
                {-1L, -2L, new BitSet(), new ArrayList<>(), containedInBookie0},
                {0, lastConfirmed, expectedEntries, unavailable, containedInBookie0},
                {lastConfirmed, lastConfirmed, excedingBitsetZero, new ArrayList<>(), containedInBookie0},
                {lastConfirmed+1, lastConfirmed+1, null, exception, containedInBookie0},
                {0, lastConfirmed, null, exception, containedInBookie0},

                // prima iterazione: jacoco
                {1L, lastConfirmed, expectedEntries.get(1, (int)lastConfirmed+1), unavailable, containedInBookie0},
                {1L, lastConfirmed-1, expectedEntries.get(1, (int)lastConfirmed), unavailable, containedInBookie0},
                {1L, 2L, expectedEntries.get(1, 3), entryBetween(unavailable, 1L, 2L), containedInBookie0},
                {lastConfirmed-1, lastConfirmed, expectedEntries.get((int)lastConfirmed-1, (int)lastConfirmed+1), entryBetween(unavailable, lastConfirmed-1, lastConfirmed), containedInBookie0},

                // seconda iterazione: jacoco e badua
                {0, lastConfirmed+1, excedingBitset, allEntriesPresent, containedInBookie0},
                {0, lastConfirmed+1,expectedEntries.get(0,2), entryBetween(unavailable, 0, 2), containedInBookie0},
                {1L,2L, expectedEntries.get(0,1), new ArrayList<>(), containedInBookie1},

                // terza iterazione: badua
                {lastConfirmed+1,lastConfirmed+2, excedingBitset.get(0,2), outlierNotAvailable, containedInBookie0},
                {11L,12L, excedingBitsetZero.get(0,2), new ArrayList<>(), containedInBookie0},

                // quarta iterazione: pit
                // test sul caso limite ... non si riscontrano problemi
                {9L, 10L, bitsetTrueDim2, tenAbsent, containedInBookie0}
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

        LedgerHandle ledger = mock(LedgerHandle.class);
        when(ledger.getLastAddConfirmed()).thenReturn(10L);
        long lastConfirmed = ledger.getLastAddConfirmed();
        System.out.println("[MOCK] "+ lastConfirmed);
        return lastConfirmed;
    }



    public TestParamAvailabilityOfEntryOfLedger(long startEntryId, long lastEntryId, BitSet expectedEntries,
                                                List<Long> expectedResult, long[] containedInBookie){

        // method params configuration
        this.firstParam = startEntryId;
        this.secondParam = lastEntryId;
        this.expectedResult = new ArrayList<>();
        this.expectedResult.addAll(expectedResult);
        this.expectedInBookie = expectedEntries;

        // SUT configuration
        configure(containedInBookie);

    }
    private void configure(long[] containedInBookie){
        PrimitiveIterator.OfLong primitiveIterator = Arrays.stream(containedInBookie).iterator();
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

package org.apache.bookkeeper.util;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import junit.framework.TestCase;
import org.apache.bookkeeper.client.RoundRobinDistributionSchedule;
import org.apache.bookkeeper.proto.BookieServer;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
@RunWith(Parameterized.class)
public class TestNetworkTopologyImpl extends TestCase{



    @Parameters
    public static Collection<Object[]> data(){
        // Crea l'input fornito all'oggetto testato e descrive l'output atteso
        // Ritorna un insieme (testedInstance, firstParam, expectedResult)

        return Arrays.asList(new Object[][]{{1, "{\"value\":123}"}});
    }


    private /*final*/ AvailabilityOfEntriesOfLedger availabilityOfEntriesOfLedger; // tested object
    private /*final*/ BitSet firstParam;                 // provided input
    private /*final*/ List<Long> expectedResult;           // expected result



    public TestNetworkTopologyImpl(int availabilityOfEntriesOfLedger, String first/*, List<Long> expectedResult*/){
        configure();    //mettici i parametri
    }


    private void configure() {

        // set-up (cosa dovrebbe esserci in un bookie)
        RoundRobinDistributionSchedule scheduler = new RoundRobinDistributionSchedule(1, 1, 2);
        BitSet expectedInBookie0 = scheduler.getEntriesStripedToTheBookie(1, 0, 9);
        System.out.println(expectedInBookie0.length());
        this.firstParam = expectedInBookie0;
        for (int i = 0; i < expectedInBookie0.length(); i++) {
            System.out.println(i + ") " + expectedInBookie0.get(i));
        }

        long[] containedInBookie0 = {5, 7, 9};
        PrimitiveIterator.OfLong primitiveIterator = Arrays.stream(containedInBookie0).iterator();
        this.availabilityOfEntriesOfLedger = new AvailabilityOfEntriesOfLedger(primitiveIterator);


        this.expectedResult = new ArrayList<>();
        this.expectedResult.add(1L);
        this.expectedResult.add(3L);

    }


    @Test
    public void testAvailabilityOfEntries(){

/*
        // set-up (cosa dovrebbe esserci in un bookie)
        RoundRobinDistributionSchedule scheduler = new RoundRobinDistributionSchedule(1,1,2);
        BitSet expectedInBookie0 = scheduler.getEntriesStripedToTheBookie(1, 0,9);
        System.out.println(expectedInBookie0.length());

        for(int i=0; i<expectedInBookie0.length();i++){
            System.out.println(i+") "+expectedInBookie0.get(i));
        }

        // simulando l'indisponibilità delle prime due entry dedicate al bookie 0 ho disponibili --> [5,7,9]
        long[] containedInBookie0 = {5,7,9};
        PrimitiveIterator.OfLong primitiveIterator = Arrays.stream(containedInBookie0).iterator();
        AvailabilityOfEntriesOfLedger sut = new AvailabilityOfEntriesOfLedger(primitiveIterator);
        List<Long> expected = new ArrayList<>();
        expected.add(1L);
        expected.add(3L);*/

        List<Long> actual = this.availabilityOfEntriesOfLedger.getUnavailableEntries(0, 10, this.firstParam);
        assertEquals(this.expectedResult, actual);


    }


//
//
//    @Test
//    public void testHardLink() throws IOException {
////        File f = new File("C:\\Users\\Elisa Venditti\\Desktop\\ISW2\\De Angelis\\Lezione 16 DeAngelis DomainPartitioningForTestGeneration ParteSeconda.pdf");
////        System.out.println(HardLink.getLinkCount(f));
////        File ff = new File("ciao");
////        System.out.println(ff.exists());
//        BitSet b = new BitSet(1);
//        System.out.println(b.get(0));
//
//    }
//
//
//    @Test
//    public void testUnavailableEntries()
//    {
//        // entry disponibili nel Ledger. Scritti così è come identificare un bookie
//        // in cui i vari array sono ledger e i numeri sono gli entry ID
////        long[][] availableEntries = {
////                { 1, 2},
////                { 0, 1, 2 },
////                { 1, 2, 3, 5, 6, 7, 8 },
////                { 1, 5 },
////                { 3 },
////                { 1, 2, 4, 5, 7, 8 },
////                {},
////                { 1, 2, 3, 5, 6, 11, 12, 13, 14, 15, 16, 17, 100, 1000, 1001, 10000, 20000, 20001 }
////        };
//
//
//
//        // quali entry dovrebbero contenere i vari ledger del bookie
////        long[][] expectedToContainEntries = {
////                { 1, 2},
////                { 0, 1, 2, 3, 5 },
////                { 1, 2, 5, 7, 8 },
////                { 2, 7 },
////                { 3 },
////                { 1, 5, 7, 8, 9, 10 },
////                { 0, 1, 2, 3, 4, 5 },
////                { 4, 18, 1002, 19999, 20003 }
////        };
//
//        // quindi queste sono le entry che non sono disponibili
//        // nei vari ledger del bookie
////        long[][] unavailableEntries = {
////                { },
////                { 3, 5 },
////                { },
////                { 2, 7 },
////                { },
////                { 9, 10 },
////                { 0, 1, 2, 3, 4, 5 },
////                { 4, 18, 1002, 19999, 20003 }
////        };
//
//
//
//        long[] availableEntriesTempArray  = { 1,5 };
//        long[] expectedToContainEntriesTempArray  = { 4,7};
//        long[] unavailableEntriesTempArray  = { 4, 7 };
//        List<Long> unavailableEntriesTempList = new ArrayList<>();
//        for (int j = 0; j < unavailableEntriesTempArray.length; j++) {
//            unavailableEntriesTempList.add(unavailableEntriesTempArray[j]);
//        }
//        PrimitiveIterator.OfLong primitiveIterator = Arrays.stream(availableEntriesTempArray).iterator();
//        AvailabilityOfEntriesOfLedger availabilityOfEntriesOfLedger = new AvailabilityOfEntriesOfLedger(primitiveIterator);
//
//        long startEntryId;
//        long lastEntryId;
//        if (expectedToContainEntriesTempArray[0] == 0) {
//            startEntryId = expectedToContainEntriesTempArray[0];
//            lastEntryId = expectedToContainEntriesTempArray[expectedToContainEntriesTempArray.length - 1];
//        } else {
//            startEntryId = expectedToContainEntriesTempArray[0] - 1;
//            lastEntryId = expectedToContainEntriesTempArray[expectedToContainEntriesTempArray.length - 1] + 1;
//        }
//        BitSet expectedToContainEntriesBitSet = new BitSet((int) (lastEntryId - startEntryId + 1));
//        for (int ind = 0; ind < expectedToContainEntriesTempArray.length; ind++) {
//            int entryId = (int) expectedToContainEntriesTempArray[ind];
//            expectedToContainEntriesBitSet.set(entryId - (int) startEntryId);
//        }
//
//        List<Long> actualUnavailableEntries = availabilityOfEntriesOfLedger.getUnavailableEntries(startEntryId,
//                lastEntryId, expectedToContainEntriesBitSet);
//        assertEquals("Unavailable Entries", unavailableEntriesTempList, actualUnavailableEntries);
//
//
//    }





    @Test
    public void testGetLinkCount(){



    }


















}

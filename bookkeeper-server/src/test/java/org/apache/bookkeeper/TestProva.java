package org.apache.bookkeeper;
import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.bookkeeper.net.BookieId;
import org.junit.*;
import org.apache.bookkeeper.bookie.BookieImpl;

import java.net.UnknownHostException;

import static org.apache.bookkeeper.bookie.BookieImpl.getBookieId;
import static org.junit.Assert.assertEquals;

public class TestProva {

    @Test
    void testProva(){
        ServerConfiguration s = new ServerConfiguration();
        try {
            BookieId id = getBookieId(s);
            assertEquals(id.getId(), id.getId());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

    }

}

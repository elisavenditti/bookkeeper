package org.apache.bookkeeper.util;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RunWith(Parameterized.class)
public class TestHardLink extends TestCase{

    @Parameters
    public static Collection<Object[]> data(){
        // Return a set (testedInstance, firstParam, expectedResult)
        File file = null;
        File notExisting = null;
        String path = null;
        try {
            path = new File(".\\").getCanonicalPath();
            System.out.println(path);
            int len = "bookkeeper\\bookkeeper-server".length();
            int totalLen = path.length();
            String subPath = path.substring(0, totalLen-len);
            subPath = subPath + "prova.txt";
            System.out.println(subPath);

            Path p = Paths.get(subPath);
            Files.createFile(p);
            file = new File(subPath);
            notExisting = new File(subPath.replace('a', 'b'));
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Arrays.asList(new Object[][]{
                {null,-2},
                {file, 1},
                {notExisting, -1}
        });
    }

    private final HardLink hardLink; // tested object
    private final File firstParam;
    private final int expectedResult;

    public TestHardLink(File firstParam, int expectedResult) {
        this.firstParam = firstParam;
        this.expectedResult =  expectedResult;
        this.hardLink = new HardLink();
    }


    @Test
    public void testHardLink(){

        int actual = 0;
        try {

            actual = HardLink.getLinkCount(this.firstParam);


        } catch(FileNotFoundException e){
            actual = -1;
        } catch (IOException e) {
            actual = -2;
        }

        System.out.println(actual);
        assertEquals(this.expectedResult, actual);



    }

}

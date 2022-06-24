package org.apache.bookkeeper.util;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
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
        File fileNotReadable = null;
        File notExisting = null;
        String path = null;
        String subPath = null;
        String subpath2 = null;
        try {
            path = new File(".\\").getCanonicalPath();
            System.out.println(path);
            int len = "bookkeeper\\bookkeeper-server".length();
            int totalLen = path.length();
            subPath = path.substring(0, totalLen-len);
            subpath2 = subPath+"notRead.txt";
            subPath = subPath + "file.txt";
            System.out.println(subPath);

            Path p = Paths.get(subPath);
            Path p2 = Paths.get(subpath2);
            Files.createFile(p);
            Files.createFile(p2);

        } catch(FileAlreadyExistsException ignore){
            System.out.println("cretion skipped");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        file = new File(subPath);
        fileNotReadable = new File(subpath2);
        fileNotReadable.setReadable(false,false);
        fileNotReadable.setExecutable(false,false);
        String notExistingPathString = subPath.replace('e', 'b');
        System.out.println(notExistingPathString);
        notExisting = new File(notExistingPathString);
        return Arrays.asList(new Object[][]{
                {null,-2},
                {file, 1},
                {notExisting, -1},
                {fileNotReadable,1}
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

        int actual;
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

    @AfterClass
    public static void reset() throws IOException {
        String path = new File(".\\").getCanonicalPath();
        int len = "bookkeeper\\bookkeeper-server".length();
        int totalLen = path.length();
        String subPath = path.substring(0, totalLen-len);
        String subPath1 = subPath + "notRead.txt";
        String subPath2 = subPath + "file.txt";

        Path p1 = Paths.get(subPath1);
        Files.delete(p1);
        Path p2= Paths.get(subPath2);
        Files.delete(p2);
    }

}

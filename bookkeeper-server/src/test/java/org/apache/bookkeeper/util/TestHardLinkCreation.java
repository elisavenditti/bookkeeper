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
import java.nio.file.*;
import java.util.*;

@RunWith(Parameterized.class)
public class TestHardLinkCreation extends TestCase{
    private HardLink hardLink; // tested object
    private File firstParam;
    private static File secondParam;
    private int expectedResult;

    @Parameters
    public static Collection<Object[]> data(){

        File file = null;
        File fileNotReadable = null;
        String path = null;
        String subPath = null;
        String subPath1 = null;
        String subPath2 = null;
        String subpath3 = null;
        try {
            path = new File(".\\").getCanonicalPath();
            System.out.println(path);
            int len = "bookkeeper\\bookkeeper-server".length();
            int totalLen = path.length();
            subPath = path.substring(0, totalLen-len);
            subPath1 = subPath + "prova.txt";
            subpath3 = subPath + "noRead.txt";
            System.out.println(subPath);

            subPath2 = subPath + "ciao.txt";
            Path p = Paths.get(subPath1);
            Files.createFile(p);

        } catch(FileAlreadyExistsException ignore){
            System.out.println("creation skipped");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        file = new File(subPath1);
        fileNotReadable = new File(subpath3);
        fileNotReadable.setReadable(false,false);
        fileNotReadable.setExecutable(false,false);
        return Arrays.asList(new Object[][]{

//                {file, new File(subPath1+"\"\"&&ls>\"C:\\Users\\Elisa Venditti\\Desktop\\killed.txt"), -2},



                {null, file, -2},
                {file, new File(subPath2), 0},
                {file, null, -2},
                {file,file,-2},
                {new File("non esiste"), null, -1},
                {new File("non esiste"), file, -1},
                {file, new File(subPath2), -2},
                {file, fileNotReadable,-2}
                //{file, new File("non\"\"> esiste"), -2}

        });
    }

    private void configure(File firstParam, File secondParam, int expectedResult){
        this.firstParam = firstParam;
        this.secondParam = secondParam;
        this.expectedResult =  expectedResult;
        this.hardLink = new HardLink();

    }

    public TestHardLinkCreation(File firstParam, File secondParam, int expectedResult) {
        configure(firstParam, secondParam, expectedResult);
    }


    @Test
    public void testHardLinkCreation(){

        int actual = 0;
        int prev=0;
        int next=0;

        try {
            if(this.firstParam!=null)
                prev = HardLink.getLinkCount(this.firstParam);

            HardLink.createHardLink(this.firstParam, this.secondParam);
            next = HardLink.getLinkCount(this.firstParam);

        } catch(FileNotFoundException e){
            actual = -1;

        } catch (IOException e) {
            actual = -2;
            System.out.println(e.getMessage());
        }

        int exp, act;

        if(actual!=0){
            exp = this.expectedResult;
            act = actual;
        }
        else{
            exp = prev+1;
            act = next;
        }

        assertEquals(exp, act);

    }

    @AfterClass
    public static void reset() throws IOException {
        String path = new File(".\\").getCanonicalPath();
        int len = "bookkeeper\\bookkeeper-server".length();
        int totalLen = path.length();
        String subPath = path.substring(0, totalLen-len);
        String subPath1 = subPath + "prova.txt";
        String subPath2 = subPath + "ciao.txt";
        String subpath3 = subPath + "noRead.txt";

        Path p1 = Paths.get(subPath1);
        Path p2 = Paths.get(subPath2);
        Path p3 = Paths.get(subpath3);
        try {
            Files.delete(p1);
            Files.delete(p2);
            Files.delete(p3);
        } catch(NoSuchFileException e){
            System.out.println("delete skipped");
        }
    }

}


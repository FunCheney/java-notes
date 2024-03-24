package com.fchen.jdk.byteCode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ExceptionTest {
    public void throwZero(int i) {
        if (i == 0) {
            throw new RuntimeException("zero");
        }
    }

    public void throwOne(int i) throws RuntimeException {
        if (i == 1) {
            throw new RuntimeException("one");
        }
    }

    public void throwArithmetic() {
        int i = 10;
        int h = 10 / 0;
        System.out.println(h);
    }

    public void tryCatch1() {
        try {
            File file = new File("c");
            FileInputStream fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (RuntimeException e){
            e.printStackTrace();
        }
    }
}

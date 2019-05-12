package com.fchen.concurrency.example.synccontainer;

import com.fchen.concurrency.annoations.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;
import java.util.Vector;

/**
 * @Classname ArrayListRelation
 * @Description
 * @Date 2019/5/12 16:10
 * @Author by Chen
 */
@Slf4j
@NotThreadSafe
public class VectorRelation2 {
    private static Vector<Integer> vector = new Vector<>();

    public static void main(String[] args) throws Exception{

        while (true){
            for (int  i = 0; i < 10; i++){
                vector.add(i);
            }
            Thread t1 = new Thread(){
                public void run(){
                    for (int  i = 0; i < 10; i++){
                        vector.remove(i);
                    }
                }
            };

            Thread t2 = new Thread(){
                public void run(){
                    for (int  i = 0; i < 10; i++){
                        vector.get(i);
                    }
                }
            };

            t1.start();
            t2.start();
        }
    }
}

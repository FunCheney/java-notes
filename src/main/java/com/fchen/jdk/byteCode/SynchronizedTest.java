package com.fchen.jdk.byteCode;
 /**
 * 指令9：同步控制指令
 */
public class SynchronizedTest {

    private int i = 0;
    public synchronized void add(){
        i++;
    }


    private Object obj = new Object();
    public void subtract(){

        synchronized (obj){
            i--;
        }
    }
}
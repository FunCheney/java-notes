package com.fchen.concurrency.example.theadsafe;

/**
 * @Classname Sequence
 * @Description
 * @Date
 * @Author
 */
public class Sequence {
    private int value;

    private int getNext(){
        return value++;
    }

    public static void main(String[] args) {
        Sequence s = new Sequence();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    System.out.println();
                }
            }
        });
    }
}

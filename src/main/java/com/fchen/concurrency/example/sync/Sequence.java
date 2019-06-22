package com.fchen.concurrency.example.sync;

/**
 * @Classname Sequence
 * @Description synchronized 字节码实现原理
 * @Date 2019/6/9 16:57
 * @Author by Chen
 */
public class Sequence {
     private int value;
     public int getNext(){
//         synchronized (Sequence.class){
//             return value ++;
//         }
         return value ++;
     }

    public static void main(String[] args) {
        Sequence s = new Sequence();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    int next = s.getNext();
                    System.out.println(next);
                }
            }
        }).start();

    }
}

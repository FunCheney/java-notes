package com.fchen.concurrency.example.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

/**
 * @Classname LockStudy2
 * @Description lock
 * @Date 2019/4/28 12:49
 * @Author by Fchen
 */
@Slf4j
public class LockStudy3 {
 class Point {
     private double x, y;
     private final StampedLock sl = new StampedLock();

      void move(double deltaX, double deltaY) { // an exclusively locked method
      long stamp = sl.writeLock();
      try {
        x += deltaX;
        y += deltaY;
      } finally {
        sl.unlockWrite(stamp);
      }
    }

    //乐观锁案例
     double distanceFromOrigin() { // A read-only method
      long stamp = sl.tryOptimisticRead(); //获得一个乐观读锁
      double currentX = x, currentY = y; //将两个字段写入本地局部变量
      if (!sl.validate(stamp)) { //检查乐观读锁后是否有其他的锁发生
         stamp = sl.readLock(); //如果没有再次获得一个悲观锁
         try {
           currentX = x;  //将两个字段读入本地局部变量
           currentY = y;
         } finally {
            sl.unlockRead(stamp);
         }
      }
      return Math.sqrt(currentX * currentX + currentY * currentY);
    }
    //悲观锁案例
     void moveIfAtOrigin(double newX, double newY) { // upgrade
      // Could instead start with optimistic, not read mode
      long stamp = sl.readLock();
      try {
        while (x == 0.0 && y == 0.0) {  //循环检查当前状态是否符合
          long ws = sl.tryConvertToWriteLock(stamp); //将读锁转换为写锁
          if (ws != 0L) {  //确认转写锁是否成功
            stamp = ws; //如果成功替换票据
            x = newX; // 进行状态改变
            y = newY;
            break;
          }
          else {  //如果不成功转换为写锁
            sl.unlockRead(stamp); //显式的释放度锁
            stamp = sl.writeLock(); //显式的进行写锁 然后在通过循环再试
          }
        }
      } finally {
        sl.unlock(stamp); //释放读锁或写锁
      }
    }
  }



}

package com.fchen.jdk.byteCode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ArithmeticTest {

    @Test
    public void method1 (){

    }



    public static void main(String[] args) {
        int[] nums = {1, 2, 4, 6, 9};
        List<List<Integer>> result = findCombinations(nums, 20);
        System.out.println("All combinations that add up to 20:");
        for (List<Integer> combination : result) {
            System.out.println(combination);
        }
    }

    public static List<List<Integer>> findCombinations(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        findCombinations(nums, target, 0, new ArrayList<>(), result);
        return result;
    }

    public static void findCombinations(int[] nums, int target, int index, List<Integer> current, List<List<Integer>> result) {
        if (target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = index; i < nums.length; i++) {
            if (target - nums[i] >= 0) {
                current.add(nums[i]);
                findCombinations(nums, target - nums[i], i, current, result);
                current.remove(current.size() - 1);
            }
        }
    }

}

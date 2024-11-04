package com.algorithm;

import java.util.Arrays;

public class SortHelper {

    public static int[] rawArray = new int[]{23, 67, 12, 89, 34, 76, 45, 90, 11, 24, 57, 3};

    public static void printArray() {
        System.out.println(Arrays.toString(rawArray));
    }

    public static void swap(int[] arr, int m, int n) {
        int temp = arr[m];
        arr[m] = arr[n];
        arr[n] = temp;
    }

}

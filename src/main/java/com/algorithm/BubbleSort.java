package com.algorithm;

public class BubbleSort {

    public static void bubbleSort(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (arr[j] > arr[j + 1]) SortHelper.swap(arr, j, j + 1);
            }
        }
    }

    public static void main(String[] args) {
        bubbleSort(SortHelper.rawArray);
        SortHelper.printArray();
    }
}

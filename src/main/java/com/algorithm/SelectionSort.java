package com.algorithm;

public class SelectionSort {

    public static void selectionSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[minIndex]) minIndex = j;
            }
            SortHelper.swap(arr, i, minIndex);
        }
    }

    public static void main(String[] args) {
        selectionSort(SortHelper.rawArray);
        SortHelper.printArray();
    }
}

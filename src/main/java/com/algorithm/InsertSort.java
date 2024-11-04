package com.algorithm;

public class InsertSort {


    public static void insertSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int currentValue = arr[i];
            int sortedLastIndex = i - 1;
            while (sortedLastIndex >= 0 && currentValue < arr[sortedLastIndex]) {
                arr[sortedLastIndex + 1] = arr[sortedLastIndex];
                sortedLastIndex--;
            }
            arr[sortedLastIndex + 1] = currentValue;
        }
    }

    public static void main(String[] args) {
        insertSort(SortHelper.rawArray);
        SortHelper.printArray();
    }
}

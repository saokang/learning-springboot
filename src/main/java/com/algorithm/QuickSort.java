package com.algorithm;

public class QuickSort {

    public static void quicksort(int[] arr, int left, int right) {
        if (left < right) {
            int mid = partition(arr, left, right);
            quicksort(arr, left, mid - 1);
            quicksort(arr, mid + 1, right);
        }
    }

    private static int partition(int[] arr, int left, int right) {
        int pivot = arr[left];
        while (left < right) {
            while (left < right && arr[right] > pivot) right--;
            arr[left] = arr[right];
            while (left < right && arr[left] < pivot) left++;
            arr[right] = arr[left];
        }
        arr[left] = pivot;
        return left;
    }

    public static void main(String[] args) {
        quicksort(SortHelper.rawArray, 0, SortHelper.rawArray.length - 1);
        SortHelper.printArray();
    }
}

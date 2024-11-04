package com.algorithm;

/**
 * 最大公约数
 */
public class GreatestCommonDivisor {

    public static int gcd(int max, int min) {
        return min > 0 ? gcd(min, max % min) : max;
    }

    public static int lcm(int max, int min) {
        return max * min / gcd(max, min);
    }

    public static void main(String[] args) {
        System.out.println(gcd(12, 24));
        System.out.println(gcd(7, 24));
        System.out.println(gcd(7, 28));
        System.out.println(lcm(7, 28));
    }
}

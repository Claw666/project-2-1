package edu.maastricht.ginrummy.melding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//TODO Cache the x most recent Result in a dictionary for every n that has been calculated
public class Permutations {

    private static int [] result;
    private static int k;
    private static int max;

    private final static Map<Integer, ArrayList<ArrayList<Integer>>> cache = new HashMap<>();

    public static ArrayList<ArrayList<Integer>> createPermutations(int n){

        if(cache.containsKey(n))
        {
            return cache.get(n);
        }

        k = 0;
        result =new int [factorial(n)*n];
        max = n;
        k = 0;
        int  [] array = new int[n];
        for(int i=0; i<n; i++){
            array[i]= i;
        }
        allPermutations(array.length,array);


        ArrayList<ArrayList<Integer>> superList = new ArrayList<ArrayList<Integer>>();
        for(int i=0; i<result.length/n;i++){
            int b= i*n;
            ArrayList<Integer> permutation = new ArrayList<>();
            for(int j=0; j<n;j++){
                permutation.add(result[b+j]);
            }
         superList.add(permutation);
        }

        cache.put(n, superList);
        return superList;
    }

    public static void allPermutations(int n, int a[])
    {
        if (n == 1) {
            getResult(a, k);
            k = k+10;
        }
        else
        {
            for (int i = 0; i < n - 1; i++) {
                allPermutations(n - 1, a);
                if (n % 2 == 0) {
                    swap(a, i, n - 1);
                } else {
                    swap(a, 0, n - 1);
                }
            }
            allPermutations(n - 1, a);
        }
    }

    private static void swap(int[] input, int i, int j) {
        int tmp = input[i];
        input[i] = input[j];
        input[j] = tmp;
    }


   private static void getResult(int [] input, int k){
        for(int i =k; i<max; i++){
            result[i] = input[i-k];
        }
        max= max+10;
    }
    private static int factorial(int n)
    {
        int result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}








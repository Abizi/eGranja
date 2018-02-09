package com.turkeytech.egranja.util;


public class Test {

    public static void main(String[] args) {
        String y = "ff";
        String x = "ff";

        if (x == y){
            System.out.println(true);
        } else {
            System.out.println(false);
        }

        if (x.equalsIgnoreCase(y)){
            System.out.println(true);
        }else {
            System.out.println(false);
        }
    }
}

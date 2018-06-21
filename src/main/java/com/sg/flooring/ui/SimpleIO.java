/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooring.ui;

import java.util.Scanner;

/**
 *
 * @author mcmu0
 */
public class SimpleIO implements UserIO {

    private Scanner sc = new Scanner(System.in);
    String temp;

    @Override
    public void print(String message) {
        System.out.println(message);
    }

    @Override
    public double readDouble(String prompt) {
        boolean good = false;
        while (!good) {
            print(prompt);
            temp = sc.nextLine();
            if (temp.isEmpty()) { //check if there is input
                good = false;
            } else { //if there is input, exit while loop and parse the data
                good = true;
            }
        }
        return Double.parseDouble(temp);
    }

    @Override
    public double readDouble(String prompt, double min, double max) {
        boolean inRange = false;
        boolean good = false;
        double value = 0;
        while (!inRange) {
            inRange = false;
            good = false;
            
            value = readDouble(prompt);
            if (value >= min && value <= max) {
                inRange = true;
            } else {
                inRange = false;
            }
        }
        return value;
    }

    @Override
    public float readFloat(String prompt) {
        boolean good = false;
        while (!good) {
            print(prompt);
            temp = sc.nextLine();
            if (temp.isEmpty()) { //check if there is input
                print("Please try again.");
                good = false;
            } else { //if there is input, exit while loop and parse the data
                good = true;
            }
        }
        return Float.parseFloat(temp);

    }

    @Override
    public float readFloat(String prompt, float min, float max) {
        boolean inRange = false;
        boolean good = false;
        float value = 0;
        while (!inRange) {
            inRange = false;
            good = false;
            
            value = readFloat(prompt);
            if (value >= min && value <= max) {
                inRange = true;
            } else {
                inRange = false;
            }
        }
        return value;
    }

    @Override
    public int readInt(String prompt) {
        boolean good = false;
        while (!good) {
            print(prompt);
            temp = sc.nextLine();
            if (temp.isEmpty()) { //check if there is input
                good = false;
            } else { //if there is input, exit while loop and parse the data
                good = true;
            }
        }
        return Integer.parseInt(temp);
    }

    @Override
    public int readInt(String prompt, int min, int max) {
        boolean inRange = false;
        boolean good = false;
        int value = 0;
        while (!inRange) {
            inRange = false;
            good = false;
            
            value = readInt(prompt);
            if (value >= min && value <= max) {
                inRange = true;
            } else {
                inRange = false;
            }
        }
        return value;
    }

    @Override
    public long readLong(String prompt) {
        boolean good = false;
        while (!good) {
            print(prompt);
            temp = sc.nextLine();
            if (temp.isEmpty()) { //check if there is input
                good = false;
            } else { //if there is input, exit while loop and parse the data
                good = true;
            }
        }
        return Long.parseLong(temp);
    }

    @Override
    public long readLong(String prompt, long min, long max) {
        boolean inRange = false;
        boolean good = false;
        long value = 0;
        while (!inRange) {
            inRange = false;
            good = false;
            
            value = readLong(prompt);
            if (value >= min && value <= max) {
                inRange = true;
            } else {
                inRange = false;
            }
        }
        return value;
    }

    @Override
    public String readString(String prompt) {
        boolean good = false;
        while (!good) {
            print(prompt);
            temp = sc.nextLine();
            if (temp.isEmpty()) { //check if there is input
                good = false;
            } else { //if there is input, exit while loop and parse the data
                good = true;
            }
        }
        return temp;
    }

}

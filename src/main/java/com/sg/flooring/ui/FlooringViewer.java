/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooring.ui;

import com.sg.flooring.dto.Order;
import com.sg.flooring.dto.Product;
import com.sg.flooring.dto.State;
import java.math.RoundingMode;
import java.util.List;

/**
 *
 * @author mcmu0
 */
public class FlooringViewer {
    UserIO io;

    public FlooringViewer(UserIO io) {
        this.io = io;
    }

    public int getMenuSelection() {
        printHomeMenu();
        return io.readInt("Enter Operation", 1, 6);
    }

    private void printHomeMenu() {
        io.print("Flooring Menu");
        io.print("1 - Display Orders");
        io.print("2 - Add Order");
        io.print("3 - Edit Order");
        io.print("4 - Remove Order");
        io.print("5 - Search Orders");
        io.print("6 - Exit");
    }

    public void modeBanner(String mode) {
        io.print("");
        io.print("");
        io.print("****************** " + mode + " MODE ******************");
    }

    public void unknownCommandBanner() {
        io.readString("Unknown Command Entered. Hit Enter to continue.");
    }

    public void displayBanner(String msg) {
        io.print("---- " + msg +" ----");
    }

    public void displayErrorMessage(String message) {
        io.print(message);
    }

    public void displayProducts(List<Product> products) {
        for (Product p : products) {
            io.print(p.getType());
        }
    }

    public void displayStates(List<State> taxTable) {
        for (State s : taxTable) {
            io.print(s.getCode());
        }
    }

    public void displayOrderNumber(int orderNumber) {
        io.print("Current order number is: " + orderNumber);
    }

    public String getString(String prompt) {
        return io.readString(prompt);
    }

    public void displayOrdersBanner(String dateString) {
        io.print("Displaying orders for Date: " + dateString);
    }

    public void displayOrders(List<Order> orders) {
        orders.stream()
                .sorted((o1, o2) -> o1.getOrderNumber().substring(11, 15)
                        .compareTo(o2.getOrderNumber().substring(11, 15)))
                .forEach(o -> printOrder(o));
    }

    public void printOrder(Order o) {
        //I agree, this may be cheating a bit (taking the easy, this is my own code)
        //but this is sure more readable to one long string
        System.out.printf("%s: Cust:%s ST:%s Prod:%s Area:%s MCost:%s LCost:%s \n",
                            o.getOrderNumber(),
                            o.getCustomerName(),
                            o.getState(),
                            o.getProduct(),
                            o.getArea().setScale(2, RoundingMode.HALF_UP),
                            o.getMaterial().setScale(2, RoundingMode.HALF_UP),
                            o.getLabor().setScale(2, RoundingMode.HALF_UP));
        System.out.printf("\t MSub:%s LSub:%s Taxes:%s Total:%s \n\n",
                            o.getMaterialSubTotal().setScale(2, RoundingMode.HALF_UP),
                            o.getLaborSubTotal().setScale(2, RoundingMode.HALF_UP),
                            o.getTax().setScale(2, RoundingMode.HALF_UP),
                            o.getTotal().setScale(2, RoundingMode.HALF_UP));
    }

    public void displayFileNames(List<String> files) {
        files.forEach((f) -> io.print(f));
    }

    public void displaySuccessBanner(String msg) {
        io.print("<<<" + msg + ">>>");
    }

    public int getEditSelection() {
        printEditMenu();
        return io.readInt("Enter the choice", 1, 7);
    }

    private void printEditMenu() {
        io.print("Edit Menu");
        io.print("1 - Edit Customer Name");
        io.print("2 - Edit Customer State");
        io.print("3 - Edit Prodcut");
        io.print("4 - Edit Material Cost");
        io.print("5 - Edit Labor Cost");
        io.print("6 - Edit Area");
        io.print("7 - Exit");
    }

    public int getSearchSelection() {
        printSearchMenu();
        return io.readInt("How do you want to search", 1, 4);
    }

    private void printSearchMenu() {
        io.print("1 - Search by customer name");
        io.print("2 - Search by state");
        io.print("3 - Search by product type");
        io.print("4 - Search by total");
    }

    public void printString(String msg) {
        io.print(msg);
    }
}

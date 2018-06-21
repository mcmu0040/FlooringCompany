/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooring.controller;

import com.sg.flooring.dao.FlooringDaoException;
import com.sg.flooring.dto.Order;
import com.sg.flooring.dto.Product;
import com.sg.flooring.dto.State;
import com.sg.flooring.service.FlooringService;
import com.sg.flooring.ui.FlooringViewer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author mcmu0
 */
public class FlooringController {

    private FlooringService service;
    private FlooringViewer view;

    private boolean productionMode;
    private List<Product> products;
    private List<State> taxTable;

    public FlooringController(FlooringService service, FlooringViewer view) {
        this.service = service;
        this.view = view;
    }

    public void run() {
        boolean done = false;

        try {
            intializeSystem();
            do {
                modeBanner();
                int menuSelection = getMenuSelection();

                switch (menuSelection) {
                    case 1:
                        displayOrders();
                        break;
                    case 2:
                        addOrder();
                        break;
                    case 3:
                        view.displayBanner("Edit Order");
                        editOrder();
                        break;
                    case 4:
                        view.displayBanner("Remove Order");
                        removeOrder();
                        view.displaySuccessBanner("Successfully Removed order");
                        break;
                    case 5:
                        view.displayBanner("Search Orders");
                        searchOrders();
                        break;
                    case 6:
                        done = true;
                        break;
                    default:
                        unknownCommand();
                        break;
                }
            } while (!done);
        } catch (FlooringDaoException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    private void intializeSystem() throws FlooringDaoException {
        productionMode = service.initializeSystem();
        products = service.getProductList();
        taxTable = service.getTaxTable();
    }

    private int getMenuSelection() {
        int result = 0;
        boolean valid = false;
        while (!valid) {
            try {
                result = view.getMenuSelection();
                valid = true;
            } catch (NumberFormatException e) {
                view.printString("Please enter a number");
            }
        }
        return result; 
    }

    private void modeBanner() {
        if (productionMode) {
            view.modeBanner("PRODUCTION");
        } else {
            view.modeBanner("TRAINING");
        }
    }

    private void unknownCommand() {
        view.unknownCommandBanner();
    }

    private void displayProductCodes() {
        view.displayBanner("Product Codes");
        view.displayProducts(products);
    }

    private void displayStates() {
        view.displayBanner("States");
        view.displayStates(taxTable);
    }

    private void addOrder() throws FlooringDaoException {
        boolean moreOrders = true;

        while (moreOrders) {
            int orderNumber;
            view.displayBanner("Add a new order");
            Order currentOrder;
            orderNumber = service.getNextOrderNumber();
            view.displayOrderNumber(orderNumber);
            LocalDate today = LocalDate.now();
            String orderString = today.toString() + "-" + orderNumber;
            //ordernumber format YYYY-MM-DD-iiii
            //will allow to have unique ordernumbers no matter the day

            currentOrder = new Order(orderString);

            String customerName = view.getString("Enter customer's name (first then last)");
            currentOrder.setCustomerName(customerName);

            String stateCode = getStateCode();
            currentOrder.setState(getState(stateCode));

            String product = getProductType();
            currentOrder.setProduct(getProduct(product));

            String area = getNumber("Enter customer area");

            currentOrder.setArea(new BigDecimal(area));

            currentOrder.calcAllCosts();

            view.printOrder(currentOrder);

            String goodOrder = view.getString("Is this order correct? (Y/N)");

            //if confirmed a good order, and is in productionMode, then add to file
            if (goodOrder.equalsIgnoreCase("y")) {
                if (productionMode) {
                    service.addOrder(currentOrder, today);
                }
            }

            String choice = view.getString("Add another order? (Y/N)");
            if (choice.equalsIgnoreCase("y")) {
                //do nothing, moreOrders is ture
            } else {
                moreOrders = false;
            }

        }
    }

    private LocalDate displayOrders() throws FlooringDaoException {
        //if orders == null, then display message

        view.displayBanner("Current Order Fies");
        List<String> files = service.getExistingFiles();
        view.displayFileNames(files);

        LocalDate date = LocalDate.now();
        String dateString = "";
        boolean valid = false;

        List<Order> orders = new ArrayList<>();

        while (!valid) {
            boolean validDate = false;
            while (!validDate) {
                dateString = view.getString("Enter date to display (YYYY-MM-DD)");
                validDate = service.validateDate(dateString);
                if (!validDate) {
                    view.printString("Please enter a date in YYYY-MM-DD format");
                }
            }

            date = LocalDate.parse(dateString);
            view.displayOrdersBanner(date.toString());
            try {
                orders = service.getOrders(date);
                valid = true;
            } catch (FlooringDaoException e) { //catch here in case they enter a date for a file that DNE
                valid = false;
                view.displayErrorMessage(e.getMessage());
            }
        }

        view.displayOrders(orders);
        return date;
    }

    private Order removeOrder() throws FlooringDaoException {
        LocalDate date = displayOrders();

        String lastFour = "";
        boolean valid = false;
        while (!valid) {
            lastFour = view.getString("Enter last 4 digits of the order you want");
            valid = service.validateOrderNumber(lastFour, date);
        }

        //here, valid order and date
        if (productionMode) {
            return service.removeOrder(lastFour, date);
        } else {
            //not production mode, just return the requested for order
            return service.getTrainingOrder(lastFour, date);
        }
    }

    private void editOrder() throws FlooringDaoException {
        Order currentOrder = removeOrder();
        //removed order has orderNumber which has the date info
        //get the date and use that to add the order back into the correct file
        String orderNumber = currentOrder.getOrderNumber();
        String orderDate = orderNumber.substring(0, 10);

        DateTimeFormatter formatter;
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
        LocalDate date = LocalDate.parse(orderDate, formatter);

        boolean doMore = true;

        while (doMore) {
            //can edit customer name, state, product type, material cost, labor cost
            //tax is tied to state, can't change the tax rate based off our inputs
            //if we update product, change the material and labor cost
            //should be able to update material and labor, to give special pricing
            //upon exiting, then calculate new costs and save

            boolean valid = false;
            int choice = getEditSelection();
            switch (choice) {
                case 1:
                    currentOrder.setCustomerName(view.getString("Enter name"));
                    break;
                case 2:
                    String stateCode = getStateCode();
                    currentOrder.setState(getState(stateCode));
                    break;
                case 3:
                    String product = getProductType();
                    currentOrder.setProduct(getProduct(product));
                    break;
                case 4:
                    valid = false;
                    String materialCost = "";
                    while (!valid) {
                        materialCost = view.getString("Current material cost is: $"
                                + currentOrder.getMaterial().setScale(2, RoundingMode.HALF_UP)
                                + "per sqft. Enter new material cost.");
                        valid = service.validateIsNumber(materialCost);
                    }
                    currentOrder.setMaterial(new BigDecimal(materialCost));
                    break;
                case 5:
                    valid = false;
                    String laborCost = "";
                    while (!valid) {
                        laborCost = view.getString("Current labor cost is: $"
                                + currentOrder.getLabor().setScale(2, RoundingMode.HALF_UP)
                                + "per sqft. Enter new labor cost.");
                        valid = service.validateIsNumber(laborCost);
                    }
                    currentOrder.setLabor(new BigDecimal(laborCost));
                    break;
                case 6:
                    valid = false;
                    String area = "";
                    while (!valid) {
                        area = view.getString("Enter area");
                        valid = service.validateIsNumber(area);
                    }
                    currentOrder.setArea(new BigDecimal(area));
                    break;
                case 7:
                    //exit, update all calcs
                    currentOrder.calcAllCosts();
                    view.printOrder(currentOrder);
                    doMore = false;
                    break;
                default:
                    break;
            }
        }
        if (productionMode) {
            service.addOrder(currentOrder, date);
        }
    }

    private String getStateCode() {
        boolean valid = false;
        String stateCode = "";
        while (!valid) {
            displayStates();
            stateCode = view.getString("Enter customer's state").toUpperCase();
            valid = service.validateState(stateCode, taxTable);
            if (!valid) {
                view.printString("Please enter a valid state.");
            }
        }
        return stateCode;
    }

    private String getNumber(String prompt) {
        boolean valid = false;
        String number = "";
        while (!valid) {
            number = view.getString(prompt);
            valid = service.validateIsNumber(number);
            if (!valid) {
                view.printString("Please enter a valid number");
            }
        }
        return number;
    }

    private String getProductType() {
        boolean valid = false;
        String product = "";
        while (!valid) {
            displayProductCodes();
            product = view.getString("Enter product type");
            valid = service.validateProduct(product, products);
            if (!valid) {
                view.printString("Please enter a valid product");
            }
        }
        return product;
    }

    private State getState(String stateCode) {
        for (State s : taxTable) {
            if (s.getCode().equalsIgnoreCase(stateCode)) {
                return s;
            }
        }
        return null;
    }

    private Product getProduct(String product) {
        for (Product p : products) {
            if (p.getType().equalsIgnoreCase(product)) {
                return p;
            }
        }
        return null;
    }

    private void searchOrders() throws FlooringDaoException {
        int choice = getSearchSelection();
        List<Order> searches = new ArrayList<>();

        switch (choice) {
            case 1:
                String name = view.getString("Enter customer name to search for (partials accepted)");
                searches = service.searchBy(name);
                displaySearchReslts(searches);
                break;
            case 2:
                String stateCode = getStateCode();
                State state = getState(stateCode);
                searches = service.searchBy(state);
                displaySearchReslts(searches);
                break;
            case 3:
                String type = getProductType();
                Product product = getProduct(type);
                searches = service.searchBy(product);
                displaySearchReslts(searches);
                break;
            case 4:
                BigDecimal min = BigDecimal.ONE;
                BigDecimal max = BigDecimal.ZERO;
                while (min.compareTo(max) >= 0) { //while min is >= max, keep asking
                    min = new BigDecimal(getNumber("Enter min total value"));
                    max = new BigDecimal(getNumber("Enter max total value"));
                    if (min.compareTo(max) >= 0) {
                        view.printString("Please make sure min total is less than max total");
                    }
                }
                searches = service.searchBy(min, max);
                displaySearchReslts(searches);
                break;
            default:
                break;
        }
    }

    private void displaySearchReslts(List<Order> searches) {
        if (searches.size() > 0) {
            view.displayOrders(searches);
        } else {
            view.printString("No search matches.");
        }
    }

    private int getEditSelection() {
        int result = 0;
        boolean valid = false;
        while (!valid) {
            try {
                result = view.getEditSelection();
                valid = true;
            } catch (NumberFormatException e) {
                view.printString("Please enter a number");
            }
        }
        return result;
    }

    private int getSearchSelection() {
        int result = 0;
        boolean valid = false;
        while (!valid) {
            try {
                result = view.getSearchSelection();
                valid = true;
            } catch (NumberFormatException e) {
                view.printString("Please enter a number");
            }
        }
        return result;
    }
}

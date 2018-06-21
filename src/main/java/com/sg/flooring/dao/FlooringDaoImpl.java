/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooring.dao;

import com.sg.flooring.dto.Order;
import com.sg.flooring.dto.Product;
import com.sg.flooring.dto.State;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mcmu0
 */
public class FlooringDaoImpl implements FlooringDao {

    //selectedOrders since it will hold the orders for the selected data
    private Map<String, Order> selectedOrders = new HashMap<>();
    private List<State> taxTable = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    private Scanner scanner;
    private boolean productionMode = false;
    
    //default order number for a new day. Still need to check order files to see the next one for the day
    //order numbers start at 1000 for ease of use and ensures consistent string length 
    private int nextOrderNumber = 1000;
    //temp used in case a add order was started but user didn't want to use it, using temp to reset orderNumber
    private int temp = 1000;

    //file handling variables/constants
    private String filePointer; //used to control what order file to load and save to
    private static final String TAXES_FILE = "taxes.txt";
    private static final String PRODUCTS_FILE = "products.txt";
    private static final String CONFIG_FILE = "config.txt";
    private static final String DELIMITER = ",";
    private static final String FILE_PREFIX = "Orders_";
    private static final String FILE_TYPE = ".txt";
    private static final String FILE_PATH = "./Orders/";

    private void loadTaxes() throws FlooringDaoException {
        try {
            scanner = new Scanner(new BufferedReader(new FileReader(TAXES_FILE)));
        } catch (FileNotFoundException e) {
            throw new FlooringDaoException("-_- Could not load tax data into memory.", e);
        }

        String currentLine;
        String[] currentTokens;

        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();
            currentTokens = currentLine.split(DELIMITER);
            State state = new State(currentTokens[0].toUpperCase(), //2-letter state code, assumes a good file
                    new BigDecimal(currentTokens[1])); //tax rate
            taxTable.add(state);
        }
        scanner.close();
    }

    private void loadProducts() throws FlooringDaoException {
        try {
            scanner = new Scanner(new BufferedReader(new FileReader(PRODUCTS_FILE)));
        } catch (FileNotFoundException e) {
            throw new FlooringDaoException("-_- Could not load product data into memory.", e);
        }

        String currentLine;
        String[] currentTokens;

        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();
            currentTokens = currentLine.split(DELIMITER);
            Product product = new Product(currentTokens[0], //type
                    new BigDecimal(currentTokens[1]), //material costs
                    new BigDecimal(currentTokens[2])); //labor costs
            products.add(product);
        }
        scanner.close();
    }

    private void loadConfig() throws FlooringDaoException {
        try {
            scanner = new Scanner(new BufferedReader(new FileReader(CONFIG_FILE)));
        } catch (FileNotFoundException e) {
            throw new FlooringDaoException("-_- Could not load configuration into memory.", e);
        }

        String currentLine = scanner.nextLine(); //expect one-line in config file

        if ("PRODUCTION".equals(currentLine)) {
            productionMode = true;
        }
    }

    @Override
    public boolean initializeSystem() throws FlooringDaoException {
        loadTaxes();
        loadProducts();
        loadConfig();
        loadInitialOrderFile(); 
        //loads order file for today's date, creates the file if it doesn't exist
        //also used to check/set order number

        return productionMode; //to be used in controller to controll reading and writing
    }

    @Override
    public List<Product> getProductList() {
        return products;
    }

    @Override
    public List<State> getTaxTable() {
        return taxTable;
    }

    @Override
    public int getNextOrderNumber() throws FlooringDaoException {
        loadInitialOrderFile();
        nextOrderNumber++;
        return nextOrderNumber;
    }

    private void updateFilePointer(LocalDate date) {
        //expeted file name format Orders_MMDDYYYY.txt
        //used to create a file name in the proper format
        String dateString = date.toString();
        String year = dateString.substring(0, 4);
        String month = dateString.substring(5, 7);
        String day = dateString.substring(8, 10);

        filePointer = FILE_PREFIX + month + day + year + FILE_TYPE;
    }

    @Override
    public void addOrder(Order currentOrder, LocalDate date) throws FlooringDaoException {
        //update FilePointer
        //load date's order file
        //if today's file DNE, creat it
        //put in new order
        //save the file and reset filePointer
        updateFilePointer(date);
        selectedOrders.clear(); //clears the map so we don't just add more orders
        loadOrders();
        selectedOrders.put(currentOrder.getOrderNumber(), currentOrder);
        saveOrders();
        filePointer = "default.txt";//reset pointer incase operation mistake
    }

    private void loadOrders() throws FlooringDaoException {
        //check if file exists at location filePointer
        //if not, throw FlooringDoaExceptiong (FNF)

        File file = new File(FILE_PATH + filePointer);

        try {
            scanner = new Scanner(new BufferedReader(new FileReader(file)));
        } catch (FileNotFoundException e) {
            throw new FlooringDaoException("-_- That order file does not exist.", e);
        }

        String currentLine;
        String[] currentTokens;

        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();
            currentTokens = currentLine.split(DELIMITER);
            Order currentOrder = new Order(currentTokens[0]);
            currentOrder.setCustomerName(currentTokens[1]);

            //instanstiate the State object
            State currentState = new State(currentTokens[2], new BigDecimal(currentTokens[3]));
            currentOrder.setState(currentState);
            
            //instantiate the Product object
            Product currentProduct = new Product(currentTokens[4], new BigDecimal(currentTokens[6]), new BigDecimal(currentTokens[7]));
            currentOrder.setProduct(currentProduct);

            currentOrder.setArea(new BigDecimal(currentTokens[5]));
            currentOrder.setMaterialSubTotal(new BigDecimal(currentTokens[8]));
            currentOrder.setLaborSubTotal(new BigDecimal(currentTokens[9]));
            currentOrder.setTax(new BigDecimal(currentTokens[10]));
            currentOrder.setTotal(new BigDecimal(currentTokens[11]));

            selectedOrders.put(currentOrder.getOrderNumber(), currentOrder);
        }
        scanner.close();
    }

    private void loadInitialOrderFile() throws FlooringDaoException {
        //attempts to create a new file for today. 
        //if file already exists, no further action needs to be taken

        LocalDate today = LocalDate.now();
        updateFilePointer(today);

        File file = new File(FILE_PATH + filePointer);

        try {
            if (!file.createNewFile()) {
                //today's file exists, must read file to get new tracker number
                selectedOrders.clear();
                loadOrders();
                //check every order number in selectedOrder
                //set currentOrder to highest value
                temp = 1000;
                Set<String> keys = selectedOrders.keySet();
                keys.stream().map((k)
                        -> k.substring(11, 15)).map((kSub)
                        -> Integer.parseInt(kSub)).filter((kInt)
                        -> (kInt > temp)).forEachOrdered((kInt) -> {
                    temp = kInt;
                });
                nextOrderNumber = temp;
            } else {
                //file didn't exist, currentOrder tracker is fine to reset
            }
        } catch (IOException e) {
            throw new FlooringDaoException("-_- Error creating today's file.", e);
        }
    }

    private void saveOrders() throws FlooringDaoException {
        PrintWriter out;

        //assumes filePointer is pointing at a good file
        File file = new File(FILE_PATH + filePointer);

        try {
            out = new PrintWriter(new FileWriter(file));
        } catch (IOException e) {
            throw new FlooringDaoException("-_- Error saving current order(s).", e);
        }

        Collection<Order> currentOrders = selectedOrders.values();

        for (Order ord : currentOrders) {
            out.println(ord.getOrderNumber() + DELIMITER
                    + ord.getCustomerName() + DELIMITER
                    + ord.getState() + DELIMITER
                    + ord.getTaxRate().setScale(2, RoundingMode.HALF_UP) + DELIMITER
                    + ord.getProduct() + DELIMITER
                    + ord.getArea().setScale(2, RoundingMode.HALF_UP) + DELIMITER
                    + ord.getMaterial().setScale(2, RoundingMode.HALF_UP) + DELIMITER
                    + ord.getLabor().setScale(2, RoundingMode.HALF_UP) + DELIMITER
                    + ord.getMaterialSubTotal().setScale(2, RoundingMode.HALF_UP) + DELIMITER
                    + ord.getLaborSubTotal().setScale(2, RoundingMode.HALF_UP) + DELIMITER
                    + ord.getTax().setScale(2, RoundingMode.HALF_UP) + DELIMITER
                    + ord.getTotal().setScale(2, RoundingMode.HALF_UP)
            );
            out.flush();
        }
        out.close();
    }

    @Override
    public List<Order> getOrders(LocalDate date) throws FlooringDaoException {
        updateFilePointer(date);
        selectedOrders.clear();
        loadOrders();
        return new ArrayList(selectedOrders.values());
    }

    @Override
    public List<String> getExistingFiles() throws FlooringDaoException {
        List<String> results = new ArrayList<String>();

        File[] files = new File(FILE_PATH).listFiles();

        for (File f : files) {
            if (f.isFile()) {
                if (!f.getName().contains("07131978")) { //filters out the testing file
                    results.add(f.getName());
                }
            }
        }
        return results;
    }

    @Override
    public Order removeOrder(String lastFour, LocalDate date) throws FlooringDaoException {
        //load selectedOrders for appropriate date
        getOrders(date);

        //reforms order number
        String orderNumber = date.toString() + "-" + lastFour;
        Order removedOrder = selectedOrders.remove(orderNumber);
        //save file after removing to ensure deletion holds
        saveOrders();
        return removedOrder;
    }

    @Override
    public Order getTrainingOrder(String lastFour, LocalDate date) throws FlooringDaoException {
        //gets an order based on date and lasFour, returns the requested order but does not write to the file
        getOrders(date);

        String orderNumber = date.toString() + "-" + lastFour;

        return selectedOrders.get(orderNumber);
    }

    private void loadAllOrders() throws FlooringDaoException {
        //used for laoding all orders for searching functions
        //get a list of all file names
        //use those names to update the filePointer
        //loadOrder to add the file to selectedOrders
        selectedOrders.clear();;
        List<String> files = getExistingFiles();

        for (String f : files) {
            filePointer = f;
            loadOrders();
        }
    }

    @Override
    public List<Order> searchBy(String name) throws FlooringDaoException {
        List<Order> results = new ArrayList<>();
        loadAllOrders();

        List<Order> orders = new ArrayList(selectedOrders.values());

        orders.stream()
                .filter((o) -> (o.getCustomerName().toLowerCase().contains(name.toLowerCase())))
                .forEachOrdered((o) -> results.add(o));
        return results;
    }

    @Override
    public List<Order> searchBy(State code) throws FlooringDaoException {
        List<Order> results = new ArrayList<>();
        loadAllOrders();

        List<Order> orders = new ArrayList(selectedOrders.values());

        orders.stream()
                .filter((o) -> (o.getState().equals(code.getCode())))
                .forEachOrdered((o) -> results.add(o));
        return results;
    }

    @Override
    public List<Order> searchBy(Product type) throws FlooringDaoException {
        List<Order> results = new ArrayList<>();
        loadAllOrders();

        List<Order> orders = new ArrayList(selectedOrders.values());

        orders.stream()
                .filter((o) -> (o.getProduct().equals(type.getType())))
                .forEachOrdered((o) -> results.add(o));
        return results;
    }

    @Override
    public List<Order> searchBy(BigDecimal min, BigDecimal max) throws FlooringDaoException {
        List<Order> results = new ArrayList<>();
        loadAllOrders();

        List<Order> orders = new ArrayList(selectedOrders.values());

        for (Order o : orders) {
            if (o.getTotal().compareTo(min) >= 0) { //total >= min
                if (o.getTotal().compareTo(max) <= 0) { //and <= max
                    results.add(o);
                }
            }
        }
        return results;
    }
}

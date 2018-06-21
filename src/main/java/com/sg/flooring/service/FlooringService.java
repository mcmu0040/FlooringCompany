/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooring.service;

import com.sg.flooring.dao.FlooringDao;
import com.sg.flooring.dao.FlooringDaoException;
import com.sg.flooring.dto.Order;
import com.sg.flooring.dto.Product;
import com.sg.flooring.dto.State;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author mcmu0
 */
public class FlooringService {
    FlooringDao dao;

    public FlooringService(FlooringDao dao) {
        this.dao = dao;
    }

    public boolean initializeSystem() throws FlooringDaoException {
        return dao.initializeSystem();
    }

    public List<Product> getProductList() {
        return dao.getProductList();
    }

    public List<State> getTaxTable() {
        return dao.getTaxTable();
    }

    public int getNextOrderNumber() throws FlooringDaoException {
        return dao.getNextOrderNumber();
    }

    public void addOrder(Order currentOrder, LocalDate date) throws FlooringDaoException {
        //have checked valid state and product data in other steps
        //order should be a good order
        dao.addOrder(currentOrder, date);
    }

    public boolean validateState(String stateCode, List<State> taxTable) {
        for (State s : taxTable) {
            if (s.getCode().equals(stateCode)) {
                return true;
            } 
        }
        return false;
    }

    public boolean validateProduct(String product, List<Product> products) {
        for (Product p : products) {
            if (p.getType().equalsIgnoreCase(product)) {
                return true;
            }
        }
        return false;
    }

    public List<Order> getOrders(LocalDate date) throws FlooringDaoException {
        //expects a valid date is passed
        return dao.getOrders(date);
    }

    public boolean validateIsNumber(String number) {
        try {
            double num = Double.parseDouble(number);
            if (num <= 0) {
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        } 
    }

    public boolean validateDate(String dateString) {
        DateTimeFormatter formatter;
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
        try {
            LocalDate.parse(dateString, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public List<String> getExistingFiles() throws FlooringDaoException {
        return dao.getExistingFiles();
    }

    public boolean validateOrderNumber(String lastFour, LocalDate date) throws FlooringDaoException {
        List<Order> orders = dao.getOrders(date);
        
        //for every order, look at unique portion and determine if that order exists for the request
        for (Order o : orders) {
            if (o.getOrderNumber().substring(11, 15).equals(lastFour)) {
                return true;
            }
        }
        return false;
    }

    public Order removeOrder(String lastFour, LocalDate date) throws FlooringDaoException {
        return dao.removeOrder(lastFour, date);
    }

    public Order getTrainingOrder(String lastFour, LocalDate date) throws FlooringDaoException {
        return dao.getTrainingOrder(lastFour, date);
    }

    public List<Order> searchBy(String name) throws FlooringDaoException {
        return dao.searchBy(name);
    }

    public List<Order> searchBy(State code) throws FlooringDaoException {
        return dao.searchBy(code);
    }

    public List<Order> searchBy(Product type) throws FlooringDaoException {
        return dao.searchBy(type);
    }

    public List<Order> searchBy(BigDecimal min, BigDecimal max) throws FlooringDaoException {
        return dao.searchBy(min, max);
    }
}

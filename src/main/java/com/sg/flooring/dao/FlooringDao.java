/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooring.dao;

import com.sg.flooring.dto.Order;
import com.sg.flooring.dto.Product;
import com.sg.flooring.dto.State;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author mcmu0
 */
public interface FlooringDao {
    
    //loads data files and returns if true if we are in productionMode
    public boolean initializeSystem() throws FlooringDaoException;

    public List<Product> getProductList();

    public List<State> getTaxTable();

    public int getNextOrderNumber() throws FlooringDaoException;

    public void addOrder(Order currentOrder, LocalDate date) throws FlooringDaoException;

    public List<Order> getOrders(LocalDate date) throws FlooringDaoException;

    public List<String> getExistingFiles() throws FlooringDaoException;

    public Order removeOrder(String lastFour, LocalDate date) throws FlooringDaoException;

    public Order getTrainingOrder(String lastFour, LocalDate date) throws FlooringDaoException;

    public List<Order> searchBy(String name) throws FlooringDaoException;

    public List<Order> searchBy(State code)throws FlooringDaoException;
    
    public List<Order> searchBy(Product type) throws FlooringDaoException;

    public List<Order> searchBy(BigDecimal min, BigDecimal max) throws FlooringDaoException;
}

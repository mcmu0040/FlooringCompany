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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mcmu0
 */
public class FlooringDaoStubImpl implements FlooringDao {
    
    private Product product;
    private List<Product> productList = new ArrayList<>();
    private State state;
    private List<State> stateList = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();
    private boolean productionMode = false;
    private int nextOrderNumber;

    public FlooringDaoStubImpl() throws Exception {
        initializeSystem();
        
        Order onlyOrder = new Order("2018-05-30-9999");
        onlyOrder.setProduct(product);
        onlyOrder.setState(state);
        onlyOrder.setArea(new BigDecimal("1000"));
        onlyOrder.calcAllCosts();
        orders.add(onlyOrder);
    }
    
    @Override
    public boolean initializeSystem() throws FlooringDaoException {
        //put in product
        //put in state
        //set nextOrderNumber
        //set productionMode
        product = new Product("Wax", new BigDecimal("1"), new BigDecimal("1"));
        productList.add(product);
        
        state = new State("MN", new BigDecimal("1"));
        stateList.add(state);
        
        nextOrderNumber = 1000;
        
        return true;
    }

    @Override
    public List<Product> getProductList() {
        return productList;
    }

    @Override
    public List<State> getTaxTable() {
        return stateList;
    }

    @Override
    public int getNextOrderNumber() {
        return nextOrderNumber + 1;
    }

    @Override
    public void addOrder(Order currentOrder, LocalDate date) throws FlooringDaoException {
        //repeatedly adds teh same order
        orders.add(orders.get(0));
    }

    @Override
    public List<Order> getOrders(LocalDate date) throws FlooringDaoException {
        return orders;
    }

    @Override
    public List<String> getExistingFiles() throws FlooringDaoException {
        List<String> files = new ArrayList<>();
        files.add("filename");
        return files;
    }

    @Override
    public Order removeOrder(String lastFour, LocalDate date) throws FlooringDaoException {
        return orders.remove(0);
    }

    @Override
    public Order getTrainingOrder(String lastFour, LocalDate date) throws FlooringDaoException {
        return orders.get(0);
    }

    @Override
    public List<Order> searchBy(String name) throws FlooringDaoException {
        //not going to do it
        return null;
    }

    @Override
    public List<Order> searchBy(State code) throws FlooringDaoException {
        //not going to do it
        return null;
    }
    
    @Override
    public List<Order> searchBy(Product type) throws FlooringDaoException {
        //not going to do it
        return null;
    }
    
    @Override
    public List<Order> searchBy(BigDecimal min, BigDecimal max) throws FlooringDaoException {
        //not going to do it
        return null;
    }
}

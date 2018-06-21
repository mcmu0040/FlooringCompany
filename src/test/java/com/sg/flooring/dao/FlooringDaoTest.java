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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mcmu0
 */
public class FlooringDaoTest {

    private FlooringDao dao = new FlooringDaoImpl();
    //use 07-13-1978 as testing file
    private LocalDate date = LocalDate.parse("1978-07-13");
    private Order order1;
    private Order order2;

    public FlooringDaoTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        //get orders ready for use
        //expect empty file
        order1 = createOrder("1001");
        order2 = createOrder("1002");
    }

    @After
    public void tearDown() throws Exception {
        //remove any orders from file
        List<Order> orders = dao.getOrders(date);
        if (orders != null) {
            for (Order o : orders) {
                String lastFour = o.getOrderNumber().substring(11, 15);
                dao.removeOrder(lastFour, date);
            }
        }
        //expect to leave testing file empty after every test
    }

    //used to create orders for testing
    private Order createOrder(String num) {
        Order newOrder;
        Product product = new Product("Wood", new BigDecimal("1"), new BigDecimal("1"));
        State state = new State("MN", new BigDecimal("1"));

        String orderNumber = date.toString() + "-" + num;

        newOrder = new Order(orderNumber);
        newOrder.setProduct(product);
        newOrder.setState(state);
        newOrder.setArea(new BigDecimal("100"));

        newOrder.calcAllCosts();

        return newOrder;
    }

    /**
     * Test of initializeSystem method, of class FlooringDao.
     */
    @Test
    public void testInitializeSystem() throws Exception {
        //test assumes config is in production mode
        boolean test = dao.initializeSystem();
        assertTrue(test);
    }

    /**
     * Test of getProductList method, of class FlooringDao.
     */
    @Test
    public void testGetProductList() throws Exception {
        dao.initializeSystem();
        List<Product> prod = dao.getProductList();
        
        assertNotEquals(0, prod.size());
    }

    /**
     * Test of getTaxTable method, of class FlooringDao.
     */
    @Test
    public void testGetTaxTable() throws Exception {
        dao.initializeSystem();
        List<State> state = dao.getTaxTable();
        
        assertNotEquals(0, state.size());
    }

    /**
     * Test of getNextOrderNumber method, of class FlooringDao.
     */
    @Test
    public void testGetNextOrderNumber() throws Exception {
        int test = dao.getNextOrderNumber();
        //based on how many orders on in current day's file, unknowalbe
        //check >= 1001 (lowest number expected)
        
        assertTrue(test >= 1001);
    }

    /**
     * Test of addOrder method, of class FlooringDao.
     */
    @Test
    public void testAddOrder() throws Exception {
        List<Order> orders; // = dao.getOrders(date);
        //assertEquals(0, orders.size());

        dao.addOrder(order1, date);
        orders = dao.getOrders(date);
        assertEquals(1, orders.size());

        dao.addOrder(order2, date);
        orders = dao.getOrders(date);
        assertEquals(2, orders.size());
    }

    /**
     * Test of getOrders method, of class FlooringDao.
     */
    @Test
    public void testGetOrders() throws Exception {
        //tested in add orders
    }

    /**
     * Test of getExistingFiles method, of class FlooringDao.
     */
    @Test
    public void testGetExistingFiles() throws Exception {
        List<String> files = dao.getExistingFiles();
        
        //check to see that there are some files, but can't say exactly how many files
        //because it will change depedning on day it is tested
        //so check not = 0
        
        assertNotEquals(0, files.size());
    }

    /**
     * Test of removeOrder method, of class FlooringDao.
     */
    @Test
    public void testRemoveOrder() throws Exception {
        List<Order> orders;
        Order test;
        dao.addOrder(order1, date);
        dao.addOrder(order2, date);
        
        orders = dao.getOrders(date);
        assertEquals(2, orders.size());
        
        test = dao.removeOrder("1001", date);
        orders = dao.getOrders(date);
        assertEquals(1, orders.size());
        assertTrue("Wood".equals(test.getProduct())); //check product type with expected value
    }

    /**
     * Test of getTrainingOrder method, of class FlooringDao.
     */
    @Test
    public void testGetTrainingOrder() throws Exception {
        dao.addOrder(order1, date);
        dao.addOrder(order2, date);
        
        List<Order> orders;
        Order test;
        
        orders = dao.getOrders(date);
        assertEquals(2, orders.size());
        
        test = dao.getTrainingOrder("1002", date);
        
        //verify order not removed from system
        orders = dao.getOrders(date);
        assertEquals(2, orders.size());
    }

    

}

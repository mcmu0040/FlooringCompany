/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooring.service;

import com.sg.flooring.dao.FlooringDao;
import com.sg.flooring.dao.FlooringDaoStubImpl;
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
public class FlooringServiceTest {
    
    private FlooringService service;
    
    public FlooringServiceTest() throws Exception {
        FlooringDao dao = new FlooringDaoStubImpl();
        
        service = new FlooringService(dao);
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws Exception {
        //ensure there is only 1 order in daoStub before beginning
        List<Order> orders = service.getOrders(LocalDate.MAX); //dates do not matter for daoStub
        if (orders.size() > 1) {
            for (int i = 0; orders.size() == 1; i++) {
                service.removeOrder("", LocalDate.MAX);
            }
        }
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of initializeSystem method, of class FlooringService.
     */
    @Test
    public void testInitializeSystem() throws Exception {
        assertTrue(service.initializeSystem());
    }

    /**
     * Test of getProductList method, of class FlooringService.
     */
    @Test
    public void testGetProductList() {
        List<Product> test = service.getProductList();
        assertEquals(1, test.size());
        assertTrue("Wax".equals(test.get(0).getType()));
    }

    /**
     * Test of getTaxTable method, of class FlooringService.
     */
    @Test
    public void testGetTaxTable() {
        List<State> test = service.getTaxTable();
        assertEquals(1, test.size());
        assertTrue("MN".equals(test.get(0).getCode()));
    }

    /**
     * Test of getNextOrderNumber method, of class FlooringService.
     */
    @Test
    public void testGetNextOrderNumber() throws Exception {
        assertEquals(1001, service.getNextOrderNumber());
    }

    /**
     * Test of addOrder method, of class FlooringService.
     */
    @Test
    public void testAddOrder() throws Exception {
        List<Order> orders;
        
        orders = service.getOrders(LocalDate.MAX);
        assertEquals(1, orders.size());
        
        service.addOrder(orders.get(0), LocalDate.MAX);
        
        orders = service.getOrders(LocalDate.MAX);
        assertEquals(2, orders.size());
    }

    /**
     * Test of validateState method, of class FlooringService.
     */
    @Test
    public void testValidateState() {
        List<State> states = service.getTaxTable();
        
        assertTrue(service.validateState("MN", states));
        assertFalse(service.validateState("MI", states));
    }

    /**
     * Test of validateProduct method, of class FlooringService.
     */
    @Test
    public void testValidateProduct() {
        List<Product> products = service.getProductList();
        
        assertTrue(service.validateProduct("Wax", products));
        assertFalse(service.validateProduct("Wood", products));
    }

    /**
     * Test of getOrders method, of class FlooringService.
     */
    @Test
    public void testGetOrders() throws Exception {
        //tested in addOrders
    }

    /**
     * Test of validateIsNumber method, of class FlooringService.
     */
    @Test
    public void testValidateIsNumber() {
        assertTrue(service.validateIsNumber("1"));
        assertFalse(service.validateIsNumber("Fruit Salad"));
    }

    /**
     * Test of validateDate method, of class FlooringService.
     */
    @Test
    public void testValidateDate() {
        assertTrue(service.validateDate("2018-05-30"));
        assertFalse(service.validateDate("05-30-2018"));
        assertFalse(service.validateDate("Fruit Salad"));
    }

    /**
     * Test of getExistingFiles method, of class FlooringService.
     */
    @Test
    public void testGetExistingFiles() throws Exception {
        List<String> files = service.getExistingFiles();
        
        assertEquals(1, files.size());
        assertTrue("filename".equals(files.get(0)));
    }

    /**
     * Test of validateOrderNumber method, of class FlooringService.
     */
    @Test
    public void testValidateOrderNumber() throws Exception {
        assertTrue(service.validateOrderNumber("9999", LocalDate.MAX));
        assertFalse(service.validateOrderNumber("1000", LocalDate.MAX));
    }

    /**
     * Test of removeOrder method, of class FlooringService.
     */
    @Test
    public void testRemoveOrder() throws Exception {
        List<Order> orders;
        
        orders = service.getOrders(LocalDate.MAX);
        assertEquals(1, orders.size());
        
        service.addOrder(orders.get(0), LocalDate.MAX);
        service.addOrder(orders.get(0), LocalDate.MAX);
        orders = service.getOrders(LocalDate.MAX);
        assertEquals(3, orders.size());
        
        service.removeOrder("", LocalDate.MAX);
        orders = service.getOrders(LocalDate.MAX);
        assertEquals(2, orders.size());
        
        service.removeOrder("", LocalDate.MAX);
        orders = service.getOrders(LocalDate.MAX);
        assertEquals(1, orders.size());
    }

    /**
     * Test of getTrainingOrder method, of class FlooringService.
     */
    @Test
    public void testGetTrainingOrder() throws Exception {
        List<Order> orders;
        
        orders = service.getOrders(LocalDate.MAX);
        assertEquals(1, orders.size());
        
        service.addOrder(orders.get(0), LocalDate.MAX);
        orders = service.getOrders(LocalDate.MAX);
        assertEquals(2, orders.size());
        
        //getTrainingOrder used in place of remove order while in training mode
        //make sure orders size doesn't change
        
        service.getTrainingOrder("", LocalDate.MAX);
        orders = service.getOrders(LocalDate.MAX);
        assertEquals(2, orders.size());
        
        service.getTrainingOrder("", LocalDate.MAX);
        orders = service.getOrders(LocalDate.MAX);
        assertEquals(2, orders.size());
    }
    
    @Test
    public void testCalcs() throws Exception {
        List <Order> orders = service.getOrders(LocalDate.MAX);
        Order order = orders.get(0);
        
        assertEquals(new BigDecimal("1"), order.getLabor());
        assertEquals(new BigDecimal("1"), order.getMaterial());
        assertEquals(new BigDecimal("1"), order.getTaxRate());
        assertEquals(new BigDecimal("1000"), order.getLaborSubTotal());
        assertEquals(new BigDecimal("1000"), order.getMaterialSubTotal());
        assertEquals(new BigDecimal("20"), order.getTax());
        assertEquals(new BigDecimal("2020"), order.getTotal());
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooring.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author mcmu0
 */
public class Order {
    private String orderNumber;
    
    private State state;
    private Product product;
    
    private String customerName;
    private BigDecimal area;
    private BigDecimal materialSubTotal; //final cost due to materials
    private BigDecimal laborSubTotal; //final cost due to labor
    private BigDecimal tax; //
    private BigDecimal total; //final total caost, material+labor+tax
    
    //assume area, material and labor are entered properly, can always calc other costs
    //orderNumber should be the only thing that is immutable upon creation

    public Order(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    
    //get and set everything except ordernumber, this will be useful during file reading
    //when creating a new order, set subtotals once all costs are updated by calling calcAllCosts
    
    public void setState(State state) {
        this.state = state;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public void calcAllCosts() {
        materialSubTotal = area.multiply(getMaterial());
        laborSubTotal = area.multiply(getLabor());
        tax = getTaxRate().multiply(materialSubTotal.add(laborSubTotal)).divide(new BigDecimal("100"));
        total = tax.add(materialSubTotal.add(laborSubTotal));
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public void setMaterial(BigDecimal material) {
        product.setMaterial(material);
    }

    public void setLabor(BigDecimal labor) {
        product.setLabor(labor);
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getState() {
        return state.getCode();
    }

    public BigDecimal getTaxRate() {
        return state.getRate();
    }

    public String getProduct() {
        return product.getType();
    }

    public BigDecimal getArea() {
        return area;
    }

    public BigDecimal getMaterial() {
        return product.getMaterial();
    }

    public BigDecimal getLabor() {
        return product.getLabor();
    }

    public BigDecimal getMaterialSubTotal() {
        return materialSubTotal;
    }

    public BigDecimal getLaborSubTotal() {
        return laborSubTotal;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setMaterialSubTotal(BigDecimal materialSubTotal) {
        this.materialSubTotal = materialSubTotal;
    }

    public void setLaborSubTotal(BigDecimal laborSubTotal) {
        this.laborSubTotal = laborSubTotal;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Order{ordNum=" + orderNumber + ", ST=" + state.getCode() + ", PROD=" + product.getType() + ", TOT=" + total.setScale(2, RoundingMode.HALF_UP) + '}';
    }
}

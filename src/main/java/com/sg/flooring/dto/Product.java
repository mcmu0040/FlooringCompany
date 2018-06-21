/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooring.dto;

import java.math.BigDecimal;

/**
 *
 * @author mcmu0
 */
public class Product {
    private String type;
    
    //material and labor are stored as per sq foot costs
    private BigDecimal material;
    private BigDecimal labor;

    public Product(String type, BigDecimal material, BigDecimal labor) {
        this.type = type;
        this.material = material;
        this.labor = labor;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getMaterial() {
        return material;
    }

    public BigDecimal getLabor() {
        return labor;
    }

    public void setMaterial(BigDecimal material) {
        this.material = material;
    }

    public void setLabor(BigDecimal labor) {
        this.labor = labor;
    }
}

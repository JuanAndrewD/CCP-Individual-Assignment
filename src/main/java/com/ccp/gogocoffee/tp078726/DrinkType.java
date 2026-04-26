/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ccp.gogocoffee.tp078726;

/**
 *
 * @author juana
 */
public enum DrinkType {
    CAPPUCCINO(9, "Cappuccino"),
    ESPRESSO(6, "Espresso"),
    JUICE(7, "Juice");
    
    private final int price;
    private final String name;
    
    DrinkType(int price, String name) {
        this.price = price;
        this.name = name;
    }
    
    public int getPrice() { return price; }
    public String getName() { return name; }
}

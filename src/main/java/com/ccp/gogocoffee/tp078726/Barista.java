/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ccp.gogocoffee.tp078726;

import java.util.Random;

/**
 *
 * @author juana
 */

public class Barista extends Thread {
    
    private final String name;
    private final Cafe cafe;
    private final Random random = new Random();
    
    public Barista(String baristaName, Cafe cafe) {
        this.name = baristaName;
        this.cafe = cafe;
        setName("Thread-Barista-" + baristaName);
    }
    
    @Override
    public void run() {
        System.out.println(getName() + ": Barista " + name + " started.");
        
        // Keep running while cafe is open OR customers are still waiting
        while (cafe.isRunning() || cafe.hasWaitingCustomers()) {
            try {
                // Returns null when no customer arrives within the window.
                Customer customer = cafe.getNextCustomer(1000);
                
                if (customer == null) {
                    System.out.println(getName() + ": " + name
                            + " is sleeping (no customers waiting).");
                    continue;
                }
                
                System.out.println(getName() + ": " + name
                        + " wakes up and takes order from "
                        + customer.getCustomerName());
                
                prepareDrink(customer, customer.getOrderedDrink());
                
                // Notify the customer thread that its drink is ready
                customer.drinkReady();
                
            } catch (InterruptedException e) {
                System.out.println(getName() + ": " + name + " shutting down.");
                break;
            }
        }
        
        System.out.println(getName() + ": " + name + " has finished for the day.");
    }
    
    private void prepareDrink(Customer customer, DrinkType drink) throws InterruptedException {
        Resource res = cafe.getResource();
        
        switch (drink) {
            case CAPPUCCINO:
                // If frother fails, espresso is still released (no resource leak).
                if (res.acquireEspresso(name)) {
                    try {
                        if (res.acquireMilkFrother(name)) {
                            simulateWork(800); // preparation time
                            System.out.println(getName() + ": " + name
                                    + " prepared Cappuccino for "
                                    + customer.getCustomerName());
                            res.releaseMilkFrother(name);
                            cafe.recordSale(drink);
                        } else {
                            System.out.println(getName() + ": " + name
                                    + " could not acquire Milk Frother, order delayed.");
                        }
                    } finally {
                        res.releaseEspresso(name); // always released
                    }
                }
                break;
                
            case ESPRESSO:
                if (res.acquireEspresso(name)) {
                    try {
                        simulateWork(600);
                        System.out.println(getName() + ": " + name
                                + " prepared Espresso for "
                                + customer.getCustomerName());
                        cafe.recordSale(drink);
                    } finally {
                        res.releaseEspresso(name);
                    }
                }
                break;
                
            case JUICE:
                if (res.acquireJuiceTap(name)) {
                    try {
                        simulateWork(400);
                        System.out.println(getName() + ": " + name
                                + " prepared Juice for "
                                + customer.getCustomerName());
                        cafe.recordSale(drink);
                    } finally {
                        res.releaseJuiceTap(name);
                    }
                }
                break;
        }
    }
    
    private void simulateWork(long ms) throws InterruptedException {
        Thread.sleep(ms + random.nextInt(400));
    }
}
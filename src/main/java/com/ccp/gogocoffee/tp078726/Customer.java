/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ccp.gogocoffee.tp078726;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author juana
 */

public class Customer extends Thread {
    
    private static final AtomicLong idGenerator = new AtomicLong(1);
    
    private final long id;
    private final String customerName;   // logical name, e.g. "Customer1"
    private final Cafe cafe;
    private final DrinkType orderedDrink;
    private final long arrivalTime;
    private final Random random = new Random();
    
    public Customer(Cafe cafe) {
        this.id = idGenerator.getAndIncrement();
        this.customerName = "Customer" + id;
        this.cafe = cafe;
        this.arrivalTime = System.currentTimeMillis();
        this.orderedDrink = chooseDrink();
        setName("Thread-" + customerName); // sets the Thread name via Thread.setName()
    }
    
    private DrinkType chooseDrink() {
        int r = random.nextInt(100);
        if (r < 70) return DrinkType.CAPPUCCINO;
        if (r < 90) return DrinkType.ESPRESSO;
        return DrinkType.JUICE;
    }
    
    @Override
    public void run() {
        System.out.println(getName() + ": " + customerName + " arrives at the cafe.");
        
        if (!cafe.enterCafe(this)) {
            // Cafe too full, customer leaves immediately
            return;
        }
        
        try {
            // Join the ordering queue and wait for a barista
            System.out.println(getName() + ": " + customerName
                    + " is waiting in line to order.");
            cafe.addToOrderingQueue(this);
            
            // Block until the barista calls drinkReady() (or 30s safety timeout)
            synchronized (this) {
                wait(30000);
            }
            
            // Drink is ready, find a seat
            if (cafe.getResource().acquireChair(customerName)) {
                System.out.println(getName() + ": " + customerName
                        + " sits down at a table.");
                startDrinking();
                cafe.getResource().releaseChair();
            } else {
                System.out.println(getName() + ": " + customerName
                        + " could not find a seat and leaves with drink.");
            }
            
        } catch (InterruptedException e) {
            System.out.println(getName() + ": " + customerName + " was interrupted.");
        } finally {
            cafe.leaveCafe(this);
        }
    }
    
    private void startDrinking() throws InterruptedException {
        System.out.println(getName() + ": " + customerName
                + " starts sipping " + orderedDrink.getName());
        int drinkTime = 3000 + random.nextInt(4000); // 3-6 seconds
        Thread.sleep(drinkTime);
        System.out.println(getName() + ": " + customerName
                + " finished drinking and is about to leave.");
    }
    
    // Barista thread to wake this customer when its drink is ready.
    public void drinkReady() {
        synchronized (this) {
            notify();
        }
    }
    
    public String getCustomerName() { return customerName; }
    
    public DrinkType getOrderedDrink() { return orderedDrink; }
    
    public long getArrivalTime() { return arrivalTime; }
}
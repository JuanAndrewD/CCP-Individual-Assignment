/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ccp.gogocoffee.tp078726;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author juana
 */

public class Customer extends Thread {
    private static final AtomicLong idGenerator = new AtomicLong(1);
    private final long id;
    private final String name;
    private final Cafe cafe;
    private final DrinkType orderedDrink;
    private final long arrivalTime;
    private final Random random = new Random();

    public Customer(Cafe cafe) {
        this.id = idGenerator.getAndIncrement();
        this.name = "Customer" + id;
        this.cafe = cafe;
        this.arrivalTime = System.currentTimeMillis();
        this.orderedDrink = chooseDrink();
        setName("Thread-" + name);
    }

    private DrinkType chooseDrink() {
        int r = random.nextInt(100);
        if (r < 70) return DrinkType.CAPPUCCINO;
        if (r < 90) return DrinkType.ESPRESSO;
        return DrinkType.JUICE;
    }

    @Override
    public void run() {
        System.out.println(getName() + ": " + name + " arrives at the cafe.");

        if (!cafe.enterCafe(this)) {
            return;
        }

        try {
            // Stand in line
            System.out.println(getName() + ": " + name + " is waiting in line to order.");
            cafe.addToOrderingQueue(this);

            // Wait for barista to prepare drink (simplified via synchronization)
            synchronized (this) {
                wait(30000); // max 30s timeout for safety
            }

            // Drink is ready - sit down
            if (cafe.getResource().acquireChair(name)) {
                System.out.println(getName() + ": " + name + " sits down at a table.");
                startDrinking();
                cafe.getResource().releaseChair();
            }

        } catch (InterruptedException e) {
            System.out.println(getName() + ": " + name + " was interrupted.");
        } finally {
            cafe.leaveCafe(this);
        }
    }

    private void startDrinking() throws InterruptedException {
        System.out.println(getName() + ": " + name + " starts sipping " + orderedDrink.getName());
        Thread.sleep(3000 + random.nextInt(4000)); // 3-6 seconds
        System.out.println(getName() + ": " + name + " finished drinking and leaves.");
    }

    public void drinkReady() {
        synchronized (this) {
            notify();
        }
    }

    public DrinkType getOrderedDrink() { return orderedDrink; }
    public long getArrivalTime() { return arrivalTime; }
    public String getName() { return name; }
}
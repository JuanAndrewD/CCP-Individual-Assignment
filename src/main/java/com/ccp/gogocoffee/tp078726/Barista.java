/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ccp.gogocoffee.tp078726;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author juana
 */
public class Barista {
    private final String name;
    private final Cafe cafe;
    private final Random random = new Random();

    public Barista(String name, Cafe cafe) {
        this.name = name;
        this.cafe = cafe;
        setName("Thread-Barista-" + name);
    }

    @Override
    public void run() {
        System.out.println(getName() + ": Barista " + name + " started and going to sleep.");

        while (true) {  // Will be terminated gracefully via shutdown
            try {
                synchronized (this) {
                    while (!cafe.hasWaitingCustomers()) {
                        System.out.println(getName() + ": " + name + " is sleeping (no customers).");
                        wait();  // Wait to be notified by customer arrival
                    }
                }

                Customer customer = cafe.getNextCustomer();
                System.out.println(getName() + ": " + name + " wakes up and takes order from " + customer.getName());

                DrinkType drink = customer.getOrderedDrink();
                prepareDrink(customer, drink);

                customer.drinkReady();  // Notify customer that drink is ready

            } catch (InterruptedException e) {
                // Graceful shutdown
                System.out.println(getName() + ": " + name + " shutting down.");
                break;
            }
        }
    }

    private void prepareDrink(Customer customer, DrinkType drink) throws InterruptedException {
        Resource res = cafe.getResource();

        switch (drink) {
            case CAPPUCCINO:
                if (res.acquireEspresso(name) && res.acquireMilkFrother(name)) {
                    simulateWork(800); // preparation time
                    System.out.println(getName() + ": " + name + " prepared Cappuccino for " + customer.getName());
                    res.releaseEspresso(name);
                    res.releaseMilkFrother(name);
                    cafe.recordSale(drink);
                }
                break;

            case ESPRESSO:
                if (res.acquireEspresso(name)) {
                    simulateWork(600);
                    System.out.println(getName() + ": " + name + " prepared Espresso for " + customer.getName());
                    res.releaseEspresso(name);
                    cafe.recordSale(drink);
                }
                break;

            case JUICE:
                if (res.acquireJuiceTap(name)) {
                    simulateWork(400);
                    System.out.println(getName() + ": " + name + " prepared Juice for " + customer.getName());
                    res.releaseJuiceTap(name);
                    cafe.recordSale(drink);
                }
                break;
        }
    }

    private void simulateWork(long ms) throws InterruptedException {
        Thread.sleep(ms + random.nextInt(400));
    }

    // Called by Customer thread when a new customer arrives
    public synchronized void wakeUp() {
        notify();
    }
}

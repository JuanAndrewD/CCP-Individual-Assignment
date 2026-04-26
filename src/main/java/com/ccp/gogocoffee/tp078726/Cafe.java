/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ccp.gogocoffee.tp078726;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author juana
 */

public class Cafe {
    
    private final Resource resource = new Resource();
    private final CustomerQueue orderingQueue = new CustomerQueue();
    
    private final AtomicInteger customersInCafe   = new AtomicInteger(0);
    private final AtomicInteger totalCustomersServed = new AtomicInteger(0);
    private final AtomicInteger cappuccinoCount   = new AtomicInteger(0);
    private final AtomicInteger espressoCount     = new AtomicInteger(0);
    private final AtomicInteger juiceCount        = new AtomicInteger(0);
    private final AtomicInteger totalSales        = new AtomicInteger(0);
    
    private final AtomicBoolean running = new AtomicBoolean(true);
    
    private static final int MAX_WAITING = 5;
    
    // Customer entry / exit 
    public boolean enterCafe(Customer customer) {
        if (customersInCafe.get() >= MAX_WAITING) {
            System.out.printf("%s: %s leaves immediately, cafe too full (>%d waiting)%n",
                    Thread.currentThread().getName(),
                    customer.getCustomerName(),
                    MAX_WAITING);
            return false;
        }
        customersInCafe.incrementAndGet();
        System.out.printf("%s: %s enters the cafe (customers inside: %d)%n",
                Thread.currentThread().getName(),
                customer.getCustomerName(),
                customersInCafe.get());
        return true;
    }
    
    public void leaveCafe(Customer customer) {
        customersInCafe.decrementAndGet();
        System.out.printf("%s: %s leaves the cafe (customers inside: %d)%n",
                Thread.currentThread().getName(),
                customer.getCustomerName(),
                customersInCafe.get());
    }
    
    // Ordering queue
    public void addToOrderingQueue(Customer customer) {
        orderingQueue.add(customer);
    }
    
    public Customer getNextCustomer() throws InterruptedException {
        return orderingQueue.take();
    }
    
    public Customer getNextCustomer(long timeoutMs) throws InterruptedException {
        return orderingQueue.poll(timeoutMs, TimeUnit.MILLISECONDS);
    }
    
    public boolean hasWaitingCustomers() {
        return !orderingQueue.isEmpty();
    }
    
    // Lifecycle
    
    // Returns true while the cafe is still accepting / serving customers.
    public boolean isRunning() {
        return running.get();
    }
    
    public void shutdown() {
        running.set(false);
        System.out.println("Main: Cafe is now closed.");
    }
    
    // Resources & sales
    public Resource getResource() {
        return resource;
    }
 
    public void recordSale(DrinkType drink) {
        totalSales.addAndGet(drink.getPrice());
        switch (drink) {
            case CAPPUCCINO -> cappuccinoCount.incrementAndGet();
            case ESPRESSO   -> espressoCount.incrementAndGet();
            case JUICE      -> juiceCount.incrementAndGet();
        }
        totalCustomersServed.incrementAndGet();
    }
    
    // Reporting
    
    public void printFinalStats() {
        System.out.println("\n=== GO GO COFFEE CAFE DAILY REPORT ===");
        System.out.println("Total Customers Served : " + totalCustomersServed.get());
        System.out.println("Cappuccino : " + cappuccinoCount.get()
                + "  (RM" + (cappuccinoCount.get() * 9) + ")");
        System.out.println("Espresso   : " + espressoCount.get()
                + "  (RM" + (espressoCount.get() * 6) + ")");
        System.out.println("Juice      : " + juiceCount.get()
                + "  (RM" + (juiceCount.get() * 7) + ")");
        System.out.println("Total Sales: RM" + totalSales.get());
        System.out.println("=========================================");
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ccp.gogocoffee.tp078726;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author juana
 */
public class Cafe {
private final Resource resource = new Resource();
    private final CustomerQueue orderingQueue = new CustomerQueue();
    private final AtomicInteger customersInCafe = new AtomicInteger(0);
    private final AtomicInteger totalCustomersServed = new AtomicInteger(0);
    private final AtomicInteger cappuccinoCount = new AtomicInteger(0);
    private final AtomicInteger espressoCount = new AtomicInteger(0);
    private final AtomicInteger juiceCount = new AtomicInteger(0);
    private final AtomicInteger totalSales = new AtomicInteger(0);

    private final ReentrantLock shutdownLock = new ReentrantLock();
    private final Condition allDone = shutdownLock.newCondition();
    private final AtomicBoolean running = new AtomicBoolean(true);

    private static final int MAX_WAITING = 5;

    public boolean enterCafe(Customer customer) {
        if (customersInCafe.get() >= MAX_WAITING) {
            System.out.printf("%s: %s leaves - cafe too full (>5 waiting)%n", 
                Thread.currentThread().getName(), customer.getName());
            return false;
        }
        customersInCafe.incrementAndGet();
        System.out.printf("%s: %s enters the cafe%n", Thread.currentThread().getName(), customer.getName());
        return true;
    }

    public void leaveCafe(Customer customer) {
        customersInCafe.decrementAndGet();
        System.out.printf("%s: %s leaves the cafe%n", Thread.currentThread().getName(), customer.getName());
        checkShutdown();
    }

    public void addToOrderingQueue(Customer customer) {
        orderingQueue.add(customer);
        notifyBaristas();
    }

    public Customer getNextCustomer() throws InterruptedException {
        return orderingQueue.take();
    }

    public boolean hasWaitingCustomers() {
        return !orderingQueue.isEmpty();
    }

    public void notifyBaristas() {
        // Baristas use wait/notify or BlockingQueue
    }

    public Resource getResource() {
        return resource;
    }

    public void recordSale(DrinkType drink) {
        totalSales.addAndGet(drink.getPrice());
        switch (drink) {
            case CAPPUCCINO -> cappuccinoCount.incrementAndGet();
            case ESPRESSO -> espressoCount.incrementAndGet();
            case JUICE -> juiceCount.incrementAndGet();
        }
        totalCustomersServed.incrementAndGet();
    }

    public void printFinalStats() {
        System.out.println("\n=== GO GO COFFEE CAFE - DAILY REPORT ===");
        System.out.println("Total Customers Served: " + totalCustomersServed.get());
        System.out.println("Cappuccino: " + cappuccinoCount.get() + " (RM" + (cappuccinoCount.get()*9) + ")");
        System.out.println("Espresso:   " + espressoCount.get() + " (RM" + (espressoCount.get()*6) + ")");
        System.out.println("Juice:      " + juiceCount.get() + " (RM" + (juiceCount.get()*7) + ")");
        System.out.println("Total Sales: RM" + totalSales.get());
        System.out.println("========================================");
    }

    private void checkShutdown() {
        if (totalCustomersServed.get() >= 20 && customersInCafe.get() == 0) {
            shutdownLock.lock();
            try {
                allDone.signalAll();
            } finally {
                shutdownLock.unlock();
            }
        }
    }

    public void waitForCompletion() throws InterruptedException {
        shutdownLock.lock();
        try {
            while (totalCustomersServed.get() < 20 || customersInCafe.get() > 0) {
                allDone.await(30, TimeUnit.SECONDS);
            }
        } finally {
            shutdownLock.unlock();
        }
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ccp.gogocoffee.tp078726;

import java.util.concurrent.PriorityBlockingQueue;

/**
 *
 * @author juana
 */
public class CustomerQueue {
    private final PriorityBlockingQueue<Customer> waitingQueue = 
        new PriorityBlockingQueue<>(20, (c1, c2) -> Long.compare(c1.getArrivalTime(), c2.getArrivalTime()));

    public void add(Customer customer) {
        waitingQueue.offer(customer);
    }

    public Customer take() throws InterruptedException {
        return waitingQueue.take();
    }

    public boolean isEmpty() {
        return waitingQueue.isEmpty();
    }

    public int size() {
        return waitingQueue.size();
    }
}

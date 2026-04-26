/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.ccp.gogocoffee.tp078726;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author juana
 */
public class Main {
    
    public static void main(String[] args) throws InterruptedException {
        Cafe cafe = new Cafe();
        
        // Create 3 barista threads.
        Barista b1 = new Barista("Alice", cafe);
        Barista b2 = new Barista("Bob", cafe);
        Barista b3 = new Barista("Charlie", cafe);
        
        b1.start();
        b2.start();
        b3.start();
        
        List<Customer> customers = new ArrayList<>();
        Random random = new Random();
        
        System.out.println("=== GoGo Coffee Cafe Simulation Started ===");
        
        // Spawn 20 customers with random inter-arrival delay (0, 1 or 2 seconds)
        for (int i = 0; i < 20; i++) {
            Customer c = new Customer(cafe);
            customers.add(c);
            c.start();
            Thread.sleep(random.nextInt(3) * 1000L);
        }
        
        for (Customer c : customers) {
            c.join();
        }
        
        // Tell barista threads the day is over so they exit their poll loops.
        cafe.shutdown();
        
        // Give baristas time to finish their current poll cycle and print exit msg
        Thread.sleep(1500);
        
        cafe.printFinalStats();
        System.out.println("\nSimulation completed successfully.");
    }
}
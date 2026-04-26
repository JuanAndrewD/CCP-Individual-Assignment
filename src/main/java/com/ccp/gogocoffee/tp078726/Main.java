/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.ccp.gogocoffee.tp078726;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author juana
 */

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Cafe cafe = new Cafe();

        // Create 3 baristas
        Barista b1 = new Barista("Alice", cafe);
        Barista b2 = new Barista("Bob", cafe);
        Barista b3 = new Barista("Charlie", cafe);

        b1.start();
        b2.start();
        b3.start();

        List<Customer> customers = new ArrayList<>();
        Random random = new Random();

        System.out.println("=== GoGo Coffee Cafe Simulation Started ===");

        // Generate 20 customers with random arrival
        for (int i = 0; i < 20; i++) {
            Customer c = new Customer(cafe);
            customers.add(c);
            c.start();

            // Random arrival delay: 0, 1 or 2 seconds
            Thread.sleep(random.nextInt(3) * 1000);
        }

        // Wait for all customers to be processed
        cafe.waitForCompletion();

        // Graceful shutdown of baristas (they will exit loop naturally)
        Thread.sleep(2000);

        cafe.printFinalStats();

        System.out.println("\nSimulation completed successfully.");
    }
}
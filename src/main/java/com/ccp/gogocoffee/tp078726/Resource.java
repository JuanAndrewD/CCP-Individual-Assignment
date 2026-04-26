/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ccp.gogocoffee.tp078726;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author juana
 */
public class Resource {
    private final Semaphore espressoMachine = new Semaphore(1, true);
    private final Semaphore milkFrother = new Semaphore(1, true);
    private final Semaphore juiceTap = new Semaphore(1, true);

    private final ReentrantLock tableLock = new ReentrantLock();
    private final Semaphore chairs = new Semaphore(10, true); // 5 tables × 2 chairs

    public boolean acquireEspresso(String baristaName) throws InterruptedException {
        log(baristaName, "attempting to acquire Espresso machine...");
        if (espressoMachine.tryAcquire(5, TimeUnit.SECONDS)) {
            log(baristaName, "acquired Espresso machine.");
            return true;
        }
        return false;
    }

    public void releaseEspresso(String baristaName) {
        espressoMachine.release();
        log(baristaName, "released Espresso machine.");
    }

    public boolean acquireMilkFrother(String baristaName) throws InterruptedException {
        log(baristaName, "attempting to acquire Milk Frothing machine...");
        if (milkFrother.tryAcquire(5, TimeUnit.SECONDS)) {
            log(baristaName, "acquired Milk Frothing machine.");
            return true;
        }
        return false;
    }

    public void releaseMilkFrother(String baristaName) {
        milkFrother.release();
        log(baristaName, "released Milk Frothing machine.");
    }

    public boolean acquireJuiceTap(String baristaName) throws InterruptedException {
        log(baristaName, "attempting to acquire Juice Tap...");
        if (juiceTap.tryAcquire(5, TimeUnit.SECONDS)) {
            log(baristaName, "acquired Juice Tap.");
            return true;
        }
        return false;
    }

    public void releaseJuiceTap(String baristaName) {
        juiceTap.release();
        log(baristaName, "released Juice Tap.");
    }

    public boolean acquireChair(String customerName) throws InterruptedException {
        return chairs.tryAcquire(10, TimeUnit.SECONDS);
    }

    public void releaseChair() {
        chairs.release();
    }

    private void log(String threadName, String msg) {
        System.out.printf("%s: %s%n", Thread.currentThread().getName(), msg);
    }
}

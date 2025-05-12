/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.testutils;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

public class CmdbTestUtils {

    public static <T> T waitFor(Supplier<T> supplier, Predicate<T> predicate) {
        for (int i = 0; i < 10; i++) {
            T t = supplier.get();
            if (predicate.apply(t)) {
                return t;
            }
            sleepSafe(1000);
        }
        throw new AssertionError();
    }

    public static void sleepSafe(int millis) {//TODO warning, duplicated code
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
        }
    }
}

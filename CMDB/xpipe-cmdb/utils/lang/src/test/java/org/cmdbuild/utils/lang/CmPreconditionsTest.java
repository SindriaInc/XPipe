/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.utils.lang;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 * @author afelice
 */
public class CmPreconditionsTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * Test of checkSingleElement method, of class CmPreconditions.
     */
    @Test
    public void testCheckSingleElement_NoElems() {
        System.out.println("checkSingleElement_NoElems");

        // act&assert:
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("attr (0) must not be greater than size (1)");
        CmPreconditions.checkSingleElement(emptyList(), "attr");
    }

    /**
     * Test of checkSingleElement method, of class CmPreconditions.
     */
    @Test
    public void testCheckSingleElement() {
        System.out.println("checkSingleElement");

        // act:
        CmPreconditions.checkSingleElement(asList("1"), "attr");

        // assert:
        // Nothing to assert
    }

    /**
     * Test of checkSingleElement method, of class CmPreconditions.
     */
    @Test
    public void testCheckSingleElement_TooManyElems() {
        System.out.println("checkSingleElement_TooManyElems");

        // act&assert:
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("attr (2) must not be greater than size (1)");
        CmPreconditions.checkSingleElement(asList("1", "2"), "attr");
    }

    /**
     * Test of checkSingleElement method, of class CmPreconditions.
     */
    @Test
    public void testCheckNumElement_TooManyElems() {
        System.out.println("checkNumElement_TooManyElems");

        // act&assert:
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("attr (3) must not be greater than size (2)");
        CmPreconditions.checkNumElements(asList("1", "2", "3"), 2, "attr");
    }

}

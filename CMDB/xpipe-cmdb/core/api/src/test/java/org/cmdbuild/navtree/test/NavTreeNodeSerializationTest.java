/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.navtree.test;

import org.cmdbuild.navtree.NavTreeNode;
import org.cmdbuild.navtree.NavTreeNodeImpl;
import static org.cmdbuild.navtree.NavTreeNodeSubclassViewMode.SVM_SUBCLASSES;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavTreeNodeSerializationTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testNavTreeNodeSerialization1() {
        NavTreeNode node1 = NavTreeNodeImpl.builder()
                .withId("_test")
                .withTargetClassName("MyClass")
                .build();
        String value = toJson(node1);
        logger.info("value = {}", value);
        NavTreeNode node2 = fromJson(value, NavTreeNode.class);
        assertEquals(node1.getId(), node2.getId());
        assertEquals(node1.getTargetClassName(), node2.getTargetClassName());
        assertThat(node2, samePropertyValuesAs(node1));
    }

    @Test
    public void testNavTreeNodeSerialization2() {
        NavTreeNode node1 = NavTreeNodeImpl.builder()
                .withId("_test")
                .withTargetClassName("MyClass")
                .withSubclassViewMode(SVM_SUBCLASSES)
                .withSubclassDescriptions(map("One", "uno", "Two", "due"))
                .build();
        String value = toJson(node1);
        logger.info("value = {}", value);
        NavTreeNode node2 = fromJson(value, NavTreeNode.class);
        assertEquals(node1.getId(), node2.getId());
        assertEquals(node1.getTargetClassName(), node2.getTargetClassName());
        assertThat(node2, samePropertyValuesAs(node1));
    }

}

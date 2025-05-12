/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonList;
import java.util.Iterator;
import java.util.List;
import static java.util.function.Function.identity;
import org.cmdbuild.utils.lang.CmCollectionUtils.FluentList;
import static org.cmdbuild.utils.lang.CmCollectionUtils.RenameMode.RM_RENAME_ALL;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Test;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.renameDuplicates;

public class CmdbuildCollectionUtilsTest {

    @Test
    public void testRenameDuplicates() {
        assertEquals(list(), renameDuplicates(RM_RENAME_ALL, list(), identity(), (s, n) -> n));
        assertEquals(list("one", "two", "three"), renameDuplicates(RM_RENAME_ALL, list("one", "two", "three"), identity(), (s, n) -> n));
        assertEquals(list("one", "two_1", "three"), renameDuplicates(RM_RENAME_ALL, list("two"), list("one", "two", "three"), identity(), (s, n) -> n));
        assertEquals(list("two_1", "two_2"), renameDuplicates(RM_RENAME_ALL, list("two", "two"), identity(), (s, n) -> n));
        assertEquals(list("file_1.txt", "file_2.txt"), renameDuplicates(RM_RENAME_ALL, list("file.txt", "file.txt"), identity(), (s, n) -> n));
        assertEquals(list("one", "two_1", "two_2"), renameDuplicates(RM_RENAME_ALL, list("one", "two", "two"), identity(), (s, n) -> n));
        assertEquals(list("one", "two_1_1", "two_2", "two_1_2"), renameDuplicates(RM_RENAME_ALL, list("one", "two", "two", "two_1"), identity(), (s, n) -> n));
        assertEquals(list("one", "two_1", "two_2", "three_1", "three_2", "four"), renameDuplicates(RM_RENAME_ALL, list("one", "two", "two", "three", "three", "four"), identity(), (s, n) -> n));
    }

    @Test
    public void testRenameDuplicates2() {
        assertEquals(list(), renameDuplicates(list(), identity(), (s, n) -> n));
        assertEquals(list("one", "two", "three"), renameDuplicates(list("one", "two", "three"), identity(), (s, n) -> n));
        assertEquals(list("one", "two_1", "three"), renameDuplicates(list("two"), list("one", "two", "three"), identity(), (s, n) -> n));
        assertEquals(list("two", "two_1"), renameDuplicates(list("two", "two"), identity(), (s, n) -> n));
        assertEquals(list("file.txt", "file_1.txt"), renameDuplicates(list("file.txt", "file.txt"), identity(), (s, n) -> n));
        assertEquals(list("one", "two", "two_1"), renameDuplicates(list("one", "two", "two"), identity(), (s, n) -> n));
        assertEquals(list("one", "two", "two_1", "two_1_1"), renameDuplicates(list("one", "two", "two", "two_1"), identity(), (s, n) -> n));
        assertEquals(list("one", "two", "two_1", "three", "three_1", "four"), renameDuplicates(list("one", "two", "two", "three", "three", "four"), identity(), (s, n) -> n));
    }

    @Test
    public void testOnlyElementCollector1() {
        String element = list("a").stream().collect(onlyElement("message = %s %s", "arg1", "arg2"));
        assertEquals("a", element);
    }

    @Test(expected = NullPointerException.class)
    public void testOnlyElementCollector2() {
        list().stream().collect(onlyElement("message = %s %s", "arg1", "arg2"));
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOnlyElementCollector3() {
        list("a", "b", "c").stream().collect(onlyElement("message = %s %s", "arg1", "arg2"));
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOnlyElementCollector4() {
        list("a", "b", "c", "d", "e", "f", "g").stream().collect(onlyElement("message = %s %s", "arg1", "arg2"));
        fail();
    }

    @Test
    public void testToList() {
        Iterable<String> iterable = () -> new Iterator<String>() {
            boolean hasNext = true;

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public String next() {
                try {
                    return "myValue";
                } finally {
                    hasNext = false;
                }
            }
        };

        List<String> list = toList(iterable);

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(iterable.iterator().next(), list.get(0));
    }

    @Test
    public void testFluentListEmpty() {
        FluentList<String> list = list();

        assertEquals(0, list.size());
    }

    @Test
    public void testFluentListSingleton() {
        FluentList<String> list = list("anny");

        assertEquals(1, list.size());
        assertEquals("anny", list.get(0));
    }

    @Test
    public void testFluentListAdd1() {
        FluentList<String> list = CmCollectionUtils.<String>list().with("anny");

        assertEquals(1, list.size());
        assertEquals("anny", list.get(0));
    }

    @Test
    public void testFluentListAdd2() {
        FluentList<String> list = listOf(String.class).with("anny");

        assertEquals(1, list.size());
        assertEquals("anny", list.get(0));
    }

    @Test
    public void testFluentListAddMany() {
        FluentList<String> list = list("1").with("2").with(singletonList("3")).with("4", "5").with(newArrayList("6", "7"));

        assertEquals(7, list.size());
        assertEquals(newArrayList("1", "2", "3", "4", "5", "6", "7"), newArrayList(list));
    }
}

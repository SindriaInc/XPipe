/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.utils.lang;

import static java.util.Arrays.asList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 *
 * @author ataboga
 */
public class CmPredicatesUtils {

    public static <T> Predicate<T> alwaysTrue() {
        return T -> Boolean.TRUE;
    }

    public static <T> Predicate<T> alwaysFalse() {
        return T -> Boolean.FALSE;
    }

    public static <T> Predicate<T> isNull() {
        return Objects::isNull;
    }

    public static <T> Predicate<T> notNull() {
        return Objects::nonNull;
    }

    public static <T> Predicate<T> and(Predicate<T>... predicates) {
        return new AndPredicate(asList(predicates));
    }

    public static <T> Predicate<T> or(Predicate<T>... predicates) {
        return new OrPredicate(asList(predicates));
    }

    private static class AndPredicate<T> implements Predicate<T> {

        private final List<Predicate<T>> predicates;

        private AndPredicate(List<Predicate<T>> predicates) {
            this.predicates = predicates;
        }

        @Override
        public boolean test(T t) {
            for (Predicate<T> predicate : predicates) {
                if (predicate.negate().test(t)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            return predicates.hashCode() + 0xdca1d20d;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof AndPredicate andPredicate) {
                return predicates.equals(andPredicate.predicates);
            }
            return false;
        }
    }

    private static class OrPredicate<T> implements Predicate<T> {

        private final List<Predicate<T>> predicates;

        private OrPredicate(List<Predicate<T>> components) {
            this.predicates = components;
        }

        @Override
        public boolean test(T t) {
            for (Predicate<T> predicate : predicates) {
                if (predicate.test(t)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            return predicates.hashCode() + 0xbe586e7e;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof OrPredicate orPredicate) {
                return predicates.equals(orPredicate.predicates);
            }
            return false;
        }
    }
}

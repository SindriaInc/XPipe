package org.cmdbuild.common.utils.guava;

import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.Map.Entry;

import org.apache.commons.lang3.builder.Builder;

import com.google.common.base.Function;

public final class Functions {

	private enum StringFunctions implements Function<String, String> {

		TRIM {

			@Override
			protected String doApply(final String input) {
				return input.trim();
			}

		}, //
		;

		@Override
		public String apply(final String input) {
			return doApply(input);
		}

		protected abstract String doApply(final String input);

	}

	private static class ToKeyFunction<K, V> implements Function<Entry<? extends K, ? extends V>, K> {

		public static <K, V> ToKeyFunction<K, V> newInstance() {
			return new ToKeyFunction<>();
		}

		private ToKeyFunction() {
			// use factory method
		}

		@Override
		public K apply(final Entry<? extends K, ? extends V> input) {
			return input.getKey();
		}

	}

	private static class ToValueFunction<K, V> implements Function<Entry<? extends K, ? extends V>, V> {

		public static <K, V> ToValueFunction<K, V> newInstance() {
			return new ToValueFunction<>();
		}

		private ToValueFunction() {
			// use factory method
		}

		@Override
		public V apply(final Entry<? extends K, ? extends V> input) {
			return input.getValue();
		}

	}

	private static class BuildFunction<T> implements Function<Builder<? extends T>, T> {

		public static <T> BuildFunction<T> newInstance() {
			return new BuildFunction<>();
		}

		private BuildFunction() {
			// use factory method
		}

		@Override
		public T apply(final Builder<? extends T> input) {
			return input.build();
		}

	}

	public static Function<String, String> trim() {
		return StringFunctions.TRIM;
	}

	public static <K, V> Function<Entry<? extends K, ? extends V>, K> toKey() {
		return ToKeyFunction.newInstance();
	}

	public static <K, V> Function<Entry<? extends K, ? extends V>, V> toValue() {
		return ToValueFunction.newInstance();
	}

	public static <T> Function<Builder<? extends T>, T> build() {
		return BuildFunction.newInstance();
	}

	private static class Set<T> implements Function<Iterable<? extends T>, java.util.Set<T>> {
		@Override
		public java.util.Set<T> apply(final Iterable<? extends T> input) {
			return newLinkedHashSet(input);
		}
	}

	@SuppressWarnings("rawtypes")
	private static final Set SET = new Set<>();

	@SuppressWarnings("unchecked")
	public static <T> Function<Iterable<? extends T>, java.util.Set<T>> set() {
		return SET;
	}

	private Functions() {
		// prevents instantiation
	}

}

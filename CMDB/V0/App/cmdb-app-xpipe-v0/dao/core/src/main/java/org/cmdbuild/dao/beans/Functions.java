package org.cmdbuild.dao.beans;

import com.google.common.base.Function;

public class Functions {

	private static class ToAttributeValue<T extends DatabaseRecordValues, V> implements Function<T, V> {

		private final String name;
		private final Class<V> type;

		private ToAttributeValue(final String name, final Class<V> type) {
			this.name = name;
			this.type = type;
		}

		@Override
		public V apply(final T input) {
			return input.get(name, type);
		}

	}

	public static <T extends DatabaseRecordValues, V> Function<T, V> toAttributeValue(final String name, final Class<V> type) {
		return new ToAttributeValue<T, V>(name, type);
	}

	private static class ToId<T extends DatabaseRecord> implements Function<T, Long> {

		private ToId() {
		}

		@Override
		public Long apply(final T input) {
			return input.getId();
		}

	}

	@SuppressWarnings("rawtypes")
	private static final ToId ID = new ToId<>();

	@SuppressWarnings("unchecked")
	public static <T extends DatabaseRecord> Function<T, Long> toId() {
		return ID;
	}

	private Functions() {
		// prevents instantiation
	}

}

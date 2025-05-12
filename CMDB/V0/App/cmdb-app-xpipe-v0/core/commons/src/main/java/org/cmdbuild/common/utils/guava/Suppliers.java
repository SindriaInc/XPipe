package org.cmdbuild.common.utils.guava;

import com.google.common.base.Supplier;

public class Suppliers {

	private static class ExceptionSupplier<T> implements Supplier<T> {

		private final RuntimeException delegate;

		public ExceptionSupplier(final RuntimeException delegate) {
			this.delegate = delegate;
		}

		@Override
		public T get() {
			throw delegate;
		}

	}

	public static <T> Supplier<T> exception(final RuntimeException exception) {
		return new ExceptionSupplier<T>(exception);
	}

	private Suppliers() {
		// prevents instantiation
	}

}

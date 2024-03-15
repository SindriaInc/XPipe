/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import java.util.function.Consumer;

public class CmConsumerUtils {

	private final static Consumer NOOP_CONSUMER = x -> {
	};

	public static <T> Consumer<T> noop() {
		return NOOP_CONSUMER;
	}

}

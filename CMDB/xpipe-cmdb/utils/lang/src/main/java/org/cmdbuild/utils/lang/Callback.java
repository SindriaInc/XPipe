/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

/**
 * @param <T>
 */
public interface Callback<T> {

	public static Callback NOP_COLLECTOR = (Callback) (Object t) -> {
		//nop
	};

	public void apply(T t);

	public static <T> Callback<T> nop() {
		return NOP_COLLECTOR;
	}

}

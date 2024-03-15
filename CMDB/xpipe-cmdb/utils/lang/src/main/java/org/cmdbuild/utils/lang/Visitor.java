/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

/**
 * @param <T>
 */
@Deprecated //use java consumer //TODO remove this
public interface Visitor<T> {

	public static Visitor NOP_VISITOR = (Visitor) (Object t) -> {
		//nop
	};

	public void visit(T t);

	public static <T> Visitor<T> nop() {
		return NOP_VISITOR;
	}

}

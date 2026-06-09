package org.test;

import com.loon.core.input.LInputFactory;
import com.loon.core.input.LTouchCollection;

/** Work around jar {@code LInputFactory.getTouchState()} crash on empty touches. */
final class TouchInput {

	private TouchInput() {
	}

	static LTouchCollection getState() {
		try {
			return LInputFactory.getTouchState();
		} catch (RuntimeException e) {
			return new LTouchCollection();
		}
	}
}

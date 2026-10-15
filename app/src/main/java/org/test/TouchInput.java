package org.test;

import com.loon.core.input.LInputFactory;
import com.loon.core.input.LTouchCollection;

/** One touch snapshot per frame; a gesture cannot cross a screen change. */
final class TouchInput {

	private static LTouchCollection frameState = new LTouchCollection();
	private static boolean frameRead;
	private static long suppressedGestureId = -1;

	private TouchInput() {
	}

	static void beginFrame() {
		// Sample only when input is handled, after the screen's Update.
		frameRead = false;
	}

	static void endFrame() {
		// Advance touches even while a transition blocks all input handlers.
		readFrame();
	}

	private static void readFrame() {
		if (frameRead) {
			return;
		}
		frameRead = true;
		LTouchCollection state = readState();
		if (state == null) {
			// A failed read is not proof that the fingers were released.
			frameState = new LTouchCollection();
			return;
		}
		if (suppressedGestureId == state.getGestureId()) {
			// Consume the release too; only a subsequent gesture may reach the new screen.
			if (!state.AnyTouch()) {
				suppressedGestureId = -1;
			}
			frameState = new LTouchCollection();
		} else {
			suppressedGestureId = -1;
			frameState = state;
		}
	}

	static void discardCurrentGesture() {
		// A screen may close during Update, before any input was handled.
		readFrame();
		if (frameState.AnyTouch()) {
			suppressedGestureId = frameState.getGestureId();
		}
		frameState = new LTouchCollection();
	}

	static LTouchCollection getState() {
		readFrame();
		return new LTouchCollection(frameState);
	}

	private static LTouchCollection readState() {
		try {
			return LInputFactory.getTouchState();
		} catch (RuntimeException e) {
			return null;
		}
	}
}

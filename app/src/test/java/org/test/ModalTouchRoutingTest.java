package org.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;

import com.loon.core.geom.Vector2f;
import com.loon.core.input.LInput;
import com.loon.core.input.LInputFactory;
import com.loon.core.input.LTouchCollection;
import com.loon.core.input.LTouchLocationState;
import com.loon.core.timer.GameTime;

public class ModalTouchRoutingTest {

	private ScreenManager manager;
	private LTouchCollection rawTouches;
	private GameTime time;

	@Before
	public void setUp() throws Exception {
		LInputFactory.resetTouch();
		TouchInput.beginFrame();
		TouchInput.discardCurrentGesture();
		TouchInput.beginFrame();
		Field field = LInputFactory.class.getDeclaredField("touchCollection");
		field.setAccessible(true);
		rawTouches = (LTouchCollection) field.get(null);
		manager = new ScreenManager(null);
		time = new GameTime();
		time.update(1f / 30f);
	}

	@Test
	public void popupOwnsInputEvenWhenItDoesNotHandleTheTap() {
		ProbeScreen scene = new ProbeScreen(false);
		ProbeScreen popup = new ProbeScreen(true);
		manager.AddScreen(scene);
		manager.AddScreen(popup);
		press(190, 500);
		manager.update(time);
		assertEquals(1, popup.touches);
		assertEquals(0, scene.inputCalls);
		assertEquals(1, scene.updates);
		assertTrue(scene.focusBlocked);
	}

	@Test
	public void closingPopupConsumesPressDragAndReleaseBeforeSceneCanSelect() {
		ProbeScreen scene = new ProbeScreen(false);
		ProbeScreen popup = new ProbeScreen(true);
		popup.onTouch = popup::ExitScreen;
		manager.AddScreen(scene);
		manager.AddScreen(popup);
		press(190, 500);
		manager.update(time);
		assertEquals(0, scene.inputCalls);
		manager.update(time); // Still held after closing.
		move(320, 460);
		manager.update(time);
		release(320, 460);
		manager.update(time);
		assertEquals(0, scene.touches);
		press(320, 460);
		manager.update(time);
		assertEquals(1, scene.touches);
	}

	@Test
	public void replacementConfirmationRequiresANewGesture() {
		ProbeScreen scene = new ProbeScreen(false);
		ProbeScreen popup = new ProbeScreen(true);
		ProbeScreen confirmation = new ProbeScreen(true);
		popup.onTouch = () -> {
			popup.ExitScreen();
			manager.AddScreen(confirmation);
		};
		manager.AddScreen(scene);
		manager.AddScreen(popup);
		press(190, 620);
		manager.update(time);
		manager.update(time);
		move(110, 470);
		manager.update(time);
		release(110, 470);
		manager.update(time);
		assertEquals(0, confirmation.touches);
		assertEquals(0, scene.inputCalls);
		press(110, 470);
		manager.update(time);
		assertEquals(1, confirmation.touches);
	}

	@Test
	public void modalRemovedDuringUpdateStillBlocksLowerScreenForThatFrame() {
		ProbeScreen scene = new ProbeScreen(false);
		ProbeScreen popup = new ProbeScreen(true);
		popup.closeInUpdate = true;
		manager.AddScreen(scene);
		manager.AddScreen(popup);
		press(100, 100);
		manager.update(time);
		assertEquals(0, popup.inputCalls);
		assertEquals(0, scene.inputCalls);
		assertTrue(scene.focusBlocked);
	}

	@Test
	public void fadingPopupBlocksInputUntilItIsGone() {
		ProbeScreen scene = new ProbeScreen(false);
		ProbeScreen popup = new ProbeScreen(true);
		manager.AddScreen(scene);
		manager.AddScreen(popup);
		manager.update(time);
		popup.setTransitionOffTime(0.5f);
		popup.ExitScreen();
		press(100, 100);
		manager.update(time);
		assertEquals(ScreenState.TransitionOff, popup.getScreenState());
		assertEquals(0, scene.inputCalls);
		assertEquals(0, popup.touches);
	}

	@Test
	public void snapshotCanBeReadTwiceWithoutAdvancingTouchState() {
		press(125, 235);
		TouchInput.beginFrame();
		LTouchCollection first = TouchInput.getState();
		LTouchCollection second = TouchInput.getState();
		assertEquals(LTouchLocationState.Pressed, first.get(0).getPrevState());
		assertEquals(LTouchLocationState.Pressed, second.get(0).getPrevState());
		assertEquals(125f, second.get(0).getPosition().x, 0f);
	}

	@Test
	public void multipleHeldFingersStaySuppressedUntilAllAreReleased() {
		press(100, 100);
		rawTouches.add(1, 200, 200);
		TouchInput.beginFrame();
		TouchInput.discardCurrentGesture();
		release(100, 100);
		TouchInput.beginFrame();
		assertTrue(TouchInput.getState().isEmpty());
		rawTouches.update(1, LTouchLocationState.Dragged, 220, 220);
		TouchInput.beginFrame();
		assertTrue(TouchInput.getState().isEmpty());
		rawTouches.update(1, LTouchLocationState.Released, 220, 220);
		TouchInput.beginFrame();
		assertTrue(TouchInput.getState().isEmpty());
		press(300, 300);
		TouchInput.beginFrame();
		assertFalse(TouchInput.getState().isEmpty());
	}

	@Test
	public void aFreshTapBetweenFramesIsNotSwallowedAfterClosingPopup() {
		ProbeScreen scene = new ProbeScreen(false);
		ProbeScreen popup = new ProbeScreen(true);
		popup.onTouch = popup::ExitScreen;
		manager.AddScreen(scene);
		manager.AddScreen(popup);
		press(190, 500);
		manager.update(time);
		release(190, 500);
		press(320, 460); // A complete new gesture before the next render frame.
		manager.update(time);
		assertEquals(1, scene.touches);
	}

	@Test
	public void removingABackgroundScreenDoesNotCancelForegroundDragging() {
		ProbeScreen background = new ProbeScreen(false);
		ProbeScreen foreground = new ProbeScreen(false);
		manager.AddScreen(background);
		manager.AddScreen(foreground);
		press(190, 500);
		manager.update(time);
		manager.RemoveScreen(background);
		move(220, 470);
		manager.update(time);
		assertEquals(2, foreground.touches);
	}

	@Test
	public void aFingerJoiningTheSuppressedGestureDoesNotUnlockInput() {
		press(100, 100);
		TouchInput.beginFrame();
		TouchInput.discardCurrentGesture();
		rawTouches.add(1, 200, 200);
		release(100, 100);
		TouchInput.beginFrame();
		assertTrue(TouchInput.getState().isEmpty());
		rawTouches.update(1, LTouchLocationState.Released, 200, 200);
		TouchInput.beginFrame();
		assertTrue(TouchInput.getState().isEmpty());
		press(300, 300);
		TouchInput.beginFrame();
		assertEquals(1, TouchInput.getState().size());
	}

	@Test
	public void reusingAReleasedPointerIdPreservesOtherHeldFingers() {
		press(100, 100);
		rawTouches.add(1, 200, 200);
		TouchInput.beginFrame();
		long gestureId = TouchInput.getState().getGestureId();
		TouchInput.discardCurrentGesture();
		release(100, 100);
		press(300, 300); // Pointer 1 is still down: this is the same gesture.
		assertEquals(2, rawTouches.size());
		assertEquals(gestureId, rawTouches.getGestureId());
		TouchInput.beginFrame();
		assertTrue(TouchInput.getState().isEmpty());
	}

	@Test
	public void cancellationAllowsTheNextGestureWithoutAnExtraTap() {
		press(100, 100);
		TouchInput.beginFrame();
		TouchInput.discardCurrentGesture();
		LInputFactory.resetTouch();
		press(200, 200);
		TouchInput.beginFrame();
		assertEquals(1, TouchInput.getState().size());
	}

	@Test
	public void ordinaryDraggingRetainsPressMoveAndRelease() {
		ProbeScreen scene = new ProbeScreen(false);
		manager.AddScreen(scene);
		press(190, 500);
		manager.update(time);
		assertEquals(LTouchLocationState.Pressed,
				TouchInput.getState().get(0).getPrevState());
		move(220, 470);
		manager.update(time);
		assertEquals(220f, TouchInput.getState().get(0).getPosition().x, 0f);
		release(220, 470);
		manager.update(time);
		assertEquals(LTouchLocationState.Released,
				TouchInput.getState().get(0).getState());
		assertEquals(3, scene.touches);
		manager.update(time);
		assertTrue(TouchInput.getState().isEmpty());
	}

	@Test
	public void failedInputReadDoesNotReleaseGestureSuppression() throws Exception {
		press(100, 100);
		TouchInput.beginFrame();
		TouchInput.discardCurrentGesture();
		Field field = LInputFactory.class.getDeclaredField("touchCollection");
		field.setAccessible(true);
		try {
			field.set(null, new LTouchCollection() {
				@Override
				public Object[] toArray() {
					throw new IllegalStateException("Interrupted touch snapshot");
				}
			});
			TouchInput.beginFrame();
			assertTrue(TouchInput.getState().isEmpty());
		} finally {
			field.set(null, rawTouches);
		}
		TouchInput.beginFrame();
		assertTrue(TouchInput.getState().isEmpty());
	}

	@Test
	public void moveArrivingDuringUpdateIsReadInTheSameFrame() {
		ProbeScreen scene = new ProbeScreen(false);
		manager.AddScreen(scene);
		press(240, 400);
		manager.update(time);
		scene.onUpdate = () -> move(280, 400);
		manager.update(time);
		assertEquals(280f, TouchInput.getState().get(0).getPosition().x, 0f);
	}

	@Test
	public void releaseArrivingDuringUpdateIsReadInTheSameFrame() {
		ProbeScreen scene = new ProbeScreen(false);
		manager.AddScreen(scene);
		press(240, 400);
		manager.update(time);
		scene.onUpdate = () -> release(280, 400);
		manager.update(time);
		assertEquals(LTouchLocationState.Released,
				TouchInput.getState().get(0).getState());
		assertEquals(280f, TouchInput.getState().get(0).getPosition().x, 0f);
	}

	@Test
	public void newPressDuringUpdateDoesNotReceiveThePreviousRelease() {
		ProbeScreen scene = new ProbeScreen(false);
		manager.AddScreen(scene);
		press(240, 400);
		manager.update(time);
		release(240, 400);
		scene.onUpdate = () -> press(50, 880);
		manager.update(time);
		LTouchCollection state = TouchInput.getState();
		assertEquals(1, state.size());
		assertEquals(LTouchLocationState.Pressed, state.get(0).getPrevState());
		assertFalse(state.get(0).getState() == LTouchLocationState.Released);
		assertEquals(50f, state.get(0).getPosition().x, 0f);
	}

	@Test
	public void popupRemovedBeforeHandlingInputStillConsumesTheHeldGesture() {
		ProbeScreen scene = new ProbeScreen(false);
		ProbeScreen popup = new ProbeScreen(true);
		popup.closeInUpdate = true;
		manager.AddScreen(scene);
		manager.AddScreen(popup);
		press(190, 500);
		manager.update(time);
		move(320, 460);
		manager.update(time);
		release(320, 460);
		manager.update(time);
		assertEquals(0, scene.touches);
		press(320, 460);
		manager.update(time);
		assertEquals(1, scene.touches);
	}

	@Test
	public void framesWithoutInputHandlersStillAdvanceTouchState() {
		ProbeScreen scene = new ProbeScreen(false);
		ProbeScreen popup = new ProbeScreen(true);
		manager.AddScreen(scene);
		manager.AddScreen(popup);
		manager.update(time);
		popup.setTransitionOffTime(0.5f);
		popup.ExitScreen();
		press(100, 100);
		manager.update(time);
		assertEquals(0, scene.inputCalls);
		assertEquals(LTouchLocationState.Dragged, rawTouches.get(0).getState());
	}

	@Test
	public void menuUsesEachTouchPositionAndActivatesOnlyOneEntry() {
		ProbeMenu menu = new ProbeMenu();
		manager.AddScreen(menu);
		press(110, 110); // Global Touch coordinates are not set by this input source.
		rawTouches.add(1, 210, 210);
		manager.update(time);
		assertEquals(1, menu.activations);
		assertEquals(0, menu.selected);
	}

	private void press(float x, float y) { rawTouches.add(0, x, y); }
	private void move(float x, float y) { rawTouches.update(0, LTouchLocationState.Dragged, x, y); }
	private void release(float x, float y) { rawTouches.update(0, LTouchLocationState.Released, x, y); }

	private static final class ProbeScreen extends GameScreen {
		int inputCalls, touches, updates;
		boolean focusBlocked, closeInUpdate;
		Runnable onTouch, onUpdate;

		ProbeScreen(boolean popup) { setIsPopup(popup); }

		@Override
		public void Update(GameTime time, boolean otherScreenHasFocus, boolean covered) {
			updates++;
			focusBlocked = otherScreenHasFocus;
			if (onUpdate != null) onUpdate.run();
			super.Update(time, otherScreenHasFocus, covered);
			if (closeInUpdate) ExitScreen();
		}

		@Override
		public void HandleInput(GameTime time, LInput input) {
			inputCalls++;
			if (!TouchInput.getState().isEmpty()) {
				touches++;
				if (onTouch != null) onTouch.run();
			}
		}
	}

	private static final class ProbeMenu extends MenuScreen {
		int activations, selected = -1;

		ProbeMenu() {
			super("", null, ScreenType.ConfirmScreen);
			setTransitionOnTime(0f);
			for (int i = 0; i < 2; i++) {
				MenuEntry entry = new MenuEntry("");
				entry.setuseButtonBackground(false);
				entry.setPosition(new Vector2f(100 + i * 100, 100 + i * 100));
				entry.setnoButtonBackgroundSize(new Vector2f(50, 50));
				getMenuEntries().add(entry);
			}
		}

		@Override
		protected void OnSelectEntry(int index) { activations++; selected = index; }
	}
}

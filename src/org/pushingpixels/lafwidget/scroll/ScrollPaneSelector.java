/*
 * Copyright (c) 2005-2010 Laf-Widget Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of Laf-Widget Kirill Grouchnikov nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.pushingpixels.lafwidget.scroll;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import org.pushingpixels.lafwidget.LafWidgetRepository;
import org.pushingpixels.lafwidget.LafWidgetUtilities2;
import org.pushingpixels.lafwidget.animation.AnimationConfigurationManager;
import org.pushingpixels.lafwidget.preview.PreviewPainter;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.UIThreadTimelineCallbackAdapter;

/**
 * ScrollPaneSelector is a little utility class that provides a means to quickly
 * scroll both vertically and horizontally on a single mouse click, by dragging
 * a selection rectangle over a "thumbnail" of the scrollPane's viewport view.
 * <p>
 * Once the selector is installed on a given JScrollPane instance, a little
 * button appears as soon as at least one of its scroll bars is made visible.
 * <p>
 * Contributed by the original author under BSD license. Also appears in the <a
 * href="https://jdnc-incubator.dev.java.net">JDNC Incubator</a>.
 * 
 * @author weebib (Pierre LE LANNIC)
 * @author Kirill Grouchnikov (animations).
 */
public class ScrollPaneSelector extends JComponent {
	// static final fields
	private static final double MAX_SIZE = 200;
	// private static final Icon LAUNCH_SELECTOR_ICON = new Icon() {
	// public void paintIcon(Component c, Graphics g, int x, int y) {
	// Color tmpColor = g.getColor();
	// g.setColor(Color.BLACK);
	// g.drawRect(2, 2, 10, 10);
	// g.drawRect(4, 5, 6, 4);
	// g.setColor(tmpColor);
	// }
	//
	// public int getIconWidth() {
	// return 15;
	// }
	//
	// public int getIconHeight() {
	// return 15;
	// }
	// };
	// private static Map theInstalledScrollPaneSelectors = new HashMap();
	private static final String COMPONENT_ORIENTATION = "componentOrientation";

	// private static final String HAS_BEEN_UNINSTALLED =
	// "lafwidget.internal.scrollPaneSelector.hasBeenUninstalled";

	// member fields
	private LayoutManager theFormerLayoutManager;
	private JScrollPane theScrollPane;
	private JComponent theComponent;
	private JPopupMenu thePopupMenu;
	private boolean toRestoreOriginal;
	private JButton theButton;
	private BufferedImage theImage;
	private Rectangle theStartRectangle;
	private Rectangle theRectangle;
	private Point theStartPoint;
	private Point thePrevPoint;
	private double theScale;
	private PropertyChangeListener propertyChangeListener;
	private ContainerAdapter theViewPortViewListener;

	// -- Constructor ------
	ScrollPaneSelector() {
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		theScrollPane = null;
		theImage = null;
		theStartRectangle = null;
		theRectangle = null;
		theStartPoint = null;
		theScale = 0.0;
		theButton = new JButton();
		LafWidgetRepository.getRepository().getLafSupport().markButtonAsFlat(
				theButton);
		theButton.setFocusable(false);
		theButton.setFocusPainted(false);

		MouseInputListener mil = new MouseInputAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Point p = e.getPoint();
				SwingUtilities.convertPointToScreen(p, theButton);
				display(p);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (!thePopupMenu.isVisible())
					return;
				toRestoreOriginal = false;
				thePopupMenu.setVisible(false);
				theStartRectangle = theRectangle;
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (theStartPoint == null)
					return;

				if (!thePopupMenu.isShowing())
					return;

				Point newPoint = e.getPoint();
				SwingUtilities.convertPointToScreen(newPoint, (Component) e
						.getSource());

				Rectangle popupScreenRect = new Rectangle(thePopupMenu
						.getLocationOnScreen(), thePopupMenu.getSize());
				if (!popupScreenRect.contains(newPoint))
					return;

				int deltaX = (int) ((newPoint.x - thePrevPoint.x) / theScale);
				int deltaY = (int) ((newPoint.y - thePrevPoint.y) / theScale);
				scroll(deltaX, deltaY, false);

				thePrevPoint = newPoint;
			}
		};
		theButton.addMouseListener(mil);
		theButton.addMouseMotionListener(mil);
		setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		thePopupMenu = new JPopupMenu();
		thePopupMenu.setLayout(new BorderLayout());
		thePopupMenu.add(this, BorderLayout.CENTER);
		propertyChangeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (theScrollPane == null)
					return;
				if ("componentOrientation".equals(evt.getPropertyName())) {
					theScrollPane.setCorner(JScrollPane.LOWER_LEADING_CORNER,
							null);
					theScrollPane.setCorner(JScrollPane.LOWER_TRAILING_CORNER,
							theButton);
				}
			}
		};
		theViewPortViewListener = new ContainerAdapter() {
			@Override
			public void componentAdded(ContainerEvent e) {
				if (thePopupMenu.isVisible())
					thePopupMenu.setVisible(false);
				Component comp = theScrollPane.getViewport().getView();
				theComponent = (comp instanceof JComponent) ? (JComponent) comp
						: null;
			}
		};
		thePopupMenu.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if ("visible".equals(evt.getPropertyName())) {
					if (!thePopupMenu.isVisible()) {
						setCursor(Cursor
								.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						if (toRestoreOriginal) {
							int deltaX = (int) ((thePrevPoint.x - theStartPoint.x) / theScale);
							int deltaY = (int) ((thePrevPoint.y - theStartPoint.y) / theScale);
							scroll(-deltaX, -deltaY, true);
						}
					}
				}
			}
		});
	}

	// -- JComponent overriden methods ------
	@Override
	public Dimension getPreferredSize() {
		if (theImage == null || theRectangle == null)
			return new Dimension();
		Insets insets = getInsets();
		return new Dimension(theImage.getWidth(null) + insets.left
				+ insets.right, theImage.getHeight(null) + insets.top
				+ insets.bottom);
	}

	@Override
	protected void paintComponent(Graphics g1D) {
		if (theImage == null || theRectangle == null)
			return;
		Graphics2D g = (Graphics2D) g1D.create();

		Insets insets = getInsets();
		int xOffset = insets.left;
		int yOffset = insets.top;
		int availableWidth = getWidth() - insets.left - insets.right;
		int availableHeight = getHeight() - insets.top - insets.bottom;
		g.drawImage(theImage, xOffset, yOffset, null);

		Color tmpColor = g.getColor();
		Area area = new Area(new Rectangle(xOffset, yOffset, availableWidth,
				availableHeight));
		area.subtract(new Area(theRectangle));
		g.setColor(new Color(200, 200, 200, 128));
		g.fill(area);
		g.setColor(Color.BLACK);
		g.draw(theRectangle);
		g.setColor(tmpColor);

		g.dispose();
	}

	// -- Private methods ------
	void installOnScrollPane(JScrollPane aScrollPane) {
		if (theScrollPane != null)
			uninstallFromScrollPane();
		theScrollPane = aScrollPane;
		theFormerLayoutManager = theScrollPane.getLayout();
		theScrollPane.setLayout(new TweakedScrollPaneLayout());
		theScrollPane.firePropertyChange("layoutManager", false, true);
		theScrollPane.addPropertyChangeListener(COMPONENT_ORIENTATION,
				propertyChangeListener);
		theScrollPane.getViewport().addContainerListener(
				theViewPortViewListener);
		theScrollPane.setCorner(JScrollPane.LOWER_TRAILING_CORNER, theButton);
		Component comp = theScrollPane.getViewport().getView();
		theComponent = (comp instanceof JComponent) ? (JComponent) comp : null;

		this.theButton.setIcon(LafWidgetRepository.getRepository()
				.getLafSupport().getSearchIcon(
						UIManager.getInt("ScrollBar.width") - 3,
						theScrollPane.getComponentOrientation()));

		theScrollPane.doLayout();
	}

	void uninstallFromScrollPane() {
		if (theScrollPane == null)
			return;
		if (thePopupMenu.isVisible())
			thePopupMenu.setVisible(false);
		theScrollPane.setCorner(JScrollPane.LOWER_TRAILING_CORNER, null);
		theScrollPane.removePropertyChangeListener(COMPONENT_ORIENTATION,
				propertyChangeListener);
		theScrollPane.getViewport().removeContainerListener(
				theViewPortViewListener);
		theScrollPane.setLayout(theFormerLayoutManager);
		theScrollPane.firePropertyChange("layoutManager", true, false);
		theScrollPane = null;
	}

	private void display(Point aPointOnScreen) {
		if (theComponent == null)
			return;

		PreviewPainter previewPainter = LafWidgetUtilities2
				.getComponentPreviewPainter(theScrollPane);
		if (!previewPainter.hasPreview(theComponent.getParent(), theComponent,
				0))
			return;

		// if (previewPainter == null) {
		// double compWidth = theComponent.getWidth();
		// double compHeight = theComponent.getHeight();
		// double scaleX = MAX_SIZE / compWidth;
		// double scaleY = MAX_SIZE / compHeight;
		// theScale = Math.min(scaleX, scaleY);
		// theImage = new BufferedImage(
		// (int) (theComponent.getWidth() * theScale),
		// (int) (theComponent.getHeight() * theScale),
		// BufferedImage.TYPE_INT_RGB);
		//
		// Graphics2D g = theImage.createGraphics();
		// g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		// RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		// g.scale(theScale, theScale);
		// theComponent.paint(g);
		// g.dispose();
		// } else {
		Dimension pDimension = previewPainter.getPreviewWindowDimension(
				theComponent.getParent(), theComponent, 0);
		double compWidth = theComponent.getWidth();
		double compHeight = theComponent.getHeight();
		double scaleX = pDimension.getWidth() / compWidth;
		double scaleY = pDimension.getHeight() / compHeight;
		theScale = Math.min(scaleX, scaleY);
		theImage = new BufferedImage(
				(int) (theComponent.getWidth() * theScale), (int) (theComponent
						.getHeight() * theScale), BufferedImage.TYPE_INT_RGB);

		Graphics2D g = theImage.createGraphics();
		previewPainter.previewComponent(null, theComponent, 0, g, 0, 0,
				theImage.getWidth(), theImage.getHeight());
		g.dispose();
		// }

		theStartRectangle = theComponent.getVisibleRect();
		Insets insets = getInsets();
		theStartRectangle.x = (int) (theScale * theStartRectangle.x + insets.left);
		theStartRectangle.y = (int) (theScale * theStartRectangle.y + insets.right);
		theStartRectangle.width *= theScale;
		theStartRectangle.height *= theScale;
		theRectangle = theStartRectangle;

		Dimension pref = thePopupMenu.getPreferredSize();
		Point buttonLocation = theButton.getLocationOnScreen();
		Point popupLocation = new Point(
				(theButton.getWidth() - pref.width) / 2,
				(theButton.getHeight() - pref.height) / 2);
		Point centerPoint = new Point(buttonLocation.x + popupLocation.x
				+ theRectangle.x + theRectangle.width / 2, buttonLocation.y
				+ popupLocation.y + theRectangle.y + theRectangle.height / 2);
		try {
			// Attempt to move the mouse pointer to the center of the selector's
			// rectangle.
			new Robot().mouseMove(centerPoint.x, centerPoint.y);
			theStartPoint = centerPoint;
		} catch (Exception e) {
			// Since we cannot move the cursor, we'll move the popup instead.
			theStartPoint = aPointOnScreen;
			popupLocation.x += theStartPoint.x - centerPoint.x;
			popupLocation.y += theStartPoint.y - centerPoint.y;
		}
		thePrevPoint = new Point(theStartPoint);
		toRestoreOriginal = true;
		thePopupMenu.show(theButton, popupLocation.x, popupLocation.y);
	}

	private void moveRectangle(int aDeltaX, int aDeltaY) {
		if (theStartRectangle == null)
			return;

		Insets insets = getInsets();
		Rectangle newRect = new Rectangle(theStartRectangle);
		newRect.x += aDeltaX;
		newRect.y += aDeltaY;
		newRect.x = Math.min(Math.max(newRect.x, insets.left), getWidth()
				- insets.right - newRect.width);
		newRect.y = Math.min(Math.max(newRect.y, insets.right), getHeight()
				- insets.bottom - newRect.height);
		Rectangle clip = new Rectangle();
		Rectangle.union(theRectangle, newRect, clip);
		clip.grow(2, 2);
		theRectangle = newRect;
		paintImmediately(clip);
	}

	private void syncRectangle() {
		JViewport viewport = this.theScrollPane.getViewport();
		Rectangle viewRect = viewport.getViewRect();

		Insets insets = getInsets();
		Rectangle newRect = new Rectangle();
		newRect.x = (int) (theScale * viewRect.x + insets.left);
		newRect.y = (int) (theScale * viewRect.y + insets.top);
		newRect.width = (int) (viewRect.width * theScale);
		newRect.height = (int) (viewRect.height * theScale);

		Rectangle clip = new Rectangle();
		Rectangle.union(theRectangle, newRect, clip);
		clip.grow(2, 2);
		theRectangle = newRect;

		// System.out.println(viewRect + "-->" + theRectangle);
		paintImmediately(clip);
	}

	private void scroll(final int aDeltaX, final int aDeltaY, boolean toAnimate) {
		if (theComponent == null)
			return;
		final Rectangle oldRectangle = theComponent.getVisibleRect();
		final Rectangle newRectangle = new Rectangle(oldRectangle.x + aDeltaX,
				oldRectangle.y + aDeltaY, oldRectangle.width,
				oldRectangle.height);

		// Animate scrolling
		if (toAnimate) {
			Timeline scrollTimeline = new Timeline(theComponent);
			AnimationConfigurationManager.getInstance().configureTimeline(
					scrollTimeline);
			scrollTimeline.addCallback(new UIThreadTimelineCallbackAdapter() {
				@Override
				public void onTimelineStateChanged(TimelineState oldState,
						TimelineState newState, float durationFraction,
						float timelinePosition) {
					if ((oldState == TimelineState.DONE)
							&& (newState == TimelineState.IDLE)) {
						theComponent.scrollRectToVisible(newRectangle);
						syncRectangle();
					}
				}

				@Override
				public void onTimelinePulse(float durationFraction,
						float timelinePosition) {
					int x = (int) (oldRectangle.x + timelinePosition * aDeltaX);
					int y = (int) (oldRectangle.y + timelinePosition * aDeltaY);
					theComponent.scrollRectToVisible(new Rectangle(x, y,
							oldRectangle.width, oldRectangle.height));
					syncRectangle();
				}
			});
			scrollTimeline.play();
		} else {
			theComponent.scrollRectToVisible(newRectangle);
			syncRectangle();
		}
	}
}
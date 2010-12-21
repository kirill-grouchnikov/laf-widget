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
package org.pushingpixels.lafwidget.desktop;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import org.pushingpixels.lafwidget.*;

/**
 * Adds internal frame thumbnail preview on desktop icon mouse hover.
 * 
 * @author Kirill Grouchnikov
 */
public class DesktopIconHoverPreviewWidget extends
		LafWidgetAdapter<JDesktopIcon> {
	/**
	 * The component that initiates the desktop icon preview (when the mouse
	 * hover above it).
	 */
	protected JComponent compToHover;

	/**
	 * Listens on the changes to the ancestor.
	 */
	protected PropertyChangeListener internalFramePropertyListener;

	/**
	 * Snapshot map.
	 */
	// private static WeakHashMap snapshots = new WeakHashMap();
	private BufferedImage snapshot;

	/**
	 * Preview window (activated on hover).
	 */
	private JWindow previewWindow;

	/**
	 * Indicates whether the corresponding desktop icon is dragged.
	 */
	private boolean isInDrag;

	/**
	 * Mouse handler for the {@link #compToHover}.
	 */
	protected TitleMouseHandler titleMouseHandler;

	/**
	 * Mouse handler for showing / hiding the preview window.
	 * 
	 * @author Kirill Grouchnikov.
	 */
	protected class TitleMouseHandler extends MouseInputAdapter {
		@Override
		public void mouseEntered(MouseEvent e) {
			if (DesktopIconHoverPreviewWidget.this.isInDrag)
				return;
			// final BufferedImage previewImage = DesktopIconHoverPreviewWidget
			// .getSnapshot(DesktopIconHoverPreviewWidget.this.desktopIcon
			// .getInternalFrame());
			BufferedImage previewImage = snapshot;
			if (previewImage != null) {
				DesktopIconHoverPreviewWidget.this.previewWindow
						.getContentPane().removeAll();
				JLabel previewLabel = new JLabel(new ImageIcon(previewImage));
				// previewLabel.setBorder(new SubstanceBorder());
				DesktopIconHoverPreviewWidget.this.previewWindow
						.getContentPane()
						.add(previewLabel, BorderLayout.CENTER);
				DesktopIconHoverPreviewWidget.this.previewWindow.setSize(
						previewImage.getWidth(), previewImage.getHeight());
				DesktopIconHoverPreviewWidget.this.syncPreviewWindow(true);
				DesktopIconHoverPreviewWidget.this.previewWindow
						.setVisible(true);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			DesktopIconHoverPreviewWidget.this.isInDrag = false;
			DesktopIconHoverPreviewWidget.this.previewWindow.dispose();// setVisible(false);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			DesktopIconHoverPreviewWidget.this.previewWindow.dispose();// setVisible(false);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			DesktopIconHoverPreviewWidget.this.isInDrag = false;
			DesktopIconHoverPreviewWidget.this.syncPreviewWindow(true);
			DesktopIconHoverPreviewWidget.this.previewWindow.setVisible(true);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			DesktopIconHoverPreviewWidget.this.isInDrag = true;
			if (DesktopIconHoverPreviewWidget.this.previewWindow.isVisible()) {
				DesktopIconHoverPreviewWidget.this.syncPreviewWindow(false);
				DesktopIconHoverPreviewWidget.this.previewWindow.dispose();// setVisible(false);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidgetAdapter#installComponents()
	 */
	@Override
	public void installComponents() {
		this.previewWindow = new JWindow();
		this.previewWindow.getContentPane().setLayout(new BorderLayout());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidgetAdapter#installListeners()
	 */
	@Override
	public void installListeners() {
		this.internalFramePropertyListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if ("ancestor".equals(evt.getPropertyName())) {
					updateSnapshot(jcomp.getInternalFrame());
				}
			}
		};
		jcomp.getInternalFrame().addPropertyChangeListener(
				this.internalFramePropertyListener);

		this.titleMouseHandler = new TitleMouseHandler();

		LafWidgetSupport lafSupport = LafWidgetRepository.getRepository()
				.getLafSupport();
		this.compToHover = lafSupport.getComponentForHover(jcomp);

		if (this.compToHover != null) {
			this.compToHover.addMouseMotionListener(this.titleMouseHandler);
			this.compToHover.addMouseListener(this.titleMouseHandler);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidgetAdapter#uninstallListeners()
	 */
	@Override
	public void uninstallListeners() {
		jcomp.getInternalFrame().removePropertyChangeListener(
				this.internalFramePropertyListener);
		this.internalFramePropertyListener = null;

		if (this.compToHover != null) {
			this.compToHover.removeMouseMotionListener(this.titleMouseHandler);
			this.compToHover.removeMouseListener(this.titleMouseHandler);
		}
		this.titleMouseHandler = null;
	}

	/**
	 * Synchronizes the preview window.
	 * 
	 * @param toShow
	 *            Indication whether the preview window is shown.
	 */
	private void syncPreviewWindow(boolean toShow) {
		if (toShow) {
			int x = jcomp.getLocationOnScreen().x;
			int y = jcomp.getLocationOnScreen().y;

			this.previewWindow.setLocation(x, y
					- this.previewWindow.getHeight());

		}
	}

	/**
	 * Updates the snapshot of the specified internal frame.
	 * 
	 * @param frame
	 *            Internal frame.
	 */
	private void updateSnapshot(JInternalFrame frame) {
		if (!frame.isShowing())
			return;
		// Draw the current state of the internal frame to a
		// temp image (w/o border and decorations). It would be nice
		// to use Robot, but this frame may be partially obscured,
		// so we take our chances that the frame will be properly
		// drawn by the user code.
		int frameWidth = frame.getWidth();
		int frameHeight = frame.getHeight();

		int dx = 0;
		int dy = 0;
		// Now we need to remove the border and the title pane :)
		Border internalFrameBorder = UIManager
				.getBorder("InternalFrame.border");
		Insets borderInsets = internalFrameBorder.getBorderInsets(frame);
		dx += borderInsets.left;
		dy += borderInsets.top;
		frameWidth -= (borderInsets.left + borderInsets.right);
		frameHeight -= (borderInsets.top + borderInsets.bottom);

		BasicInternalFrameUI frameUI = (BasicInternalFrameUI) frame.getUI();
		JComponent frameTitlePane = frameUI.getNorthPane();

		if (frameTitlePane != null) {
			dy += frameTitlePane.getHeight();
			frameHeight -= frameTitlePane.getHeight();
		}

		// fix for defect 112 - checking frame height and width
		if ((frameWidth > 0) && (frameHeight > 0)) {
			// draw frame (note the canvas translation)
			BufferedImage tempCanvas = new BufferedImage(frameWidth,
					frameHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics tempCanvasGraphics = tempCanvas.getGraphics();
			tempCanvasGraphics.translate(-dx, -dy);
			Map<Component, Boolean> dbSnapshot = new HashMap<Component, Boolean>();
			LafWidgetUtilities.makePreviewable(frame, dbSnapshot);
			frame.paint(tempCanvasGraphics);
			LafWidgetUtilities.restorePreviewable(frame, dbSnapshot);

			int maxWidth = UIManager.getInt("DesktopIcon.width");
			int maxHeight = maxWidth;

			// check if need to scale down
			double coef = Math.min((double) maxWidth / (double) frameWidth,
					(double) maxHeight / (double) frameHeight);
			if (coef < 1.0) {
				int sdWidth = (int) (coef * frameWidth);
				// int sdHeight = (int) (coef * frameHeight);
				// BufferedImage scaledDown = new BufferedImage(sdWidth,
				// sdHeight,
				// BufferedImage.TYPE_INT_ARGB);
				// Graphics g = scaledDown.getGraphics();
				// g.drawImage(tempCanvas, 0, 0, sdWidth, sdHeight, 0, 0,
				// frameWidth, frameHeight, null);
				BufferedImage scaledDown = LafWidgetUtilities.createThumbnail(
						tempCanvas, sdWidth);
				// System.out.println("Putting " + frame.hashCode() + "
				// -> " + scaledDown.hashCode());
				snapshot = scaledDown;
				// DesktopIconHoverPreviewWidget.snapshots.put(frame,
				// scaledDown);
			} else {
				// System.out.println("Putting " + frame.hashCode() + "
				// -> " + snapshot.hashCode());
				snapshot = tempCanvas;
				// DesktopIconHoverPreviewWidget.snapshots.put(frame,
				// tempCanvas);
			}
		}
	}

	/**
	 * Returns the snapshot of the specified internal frame.
	 * 
	 * @param frame
	 *            Internal frame.
	 * @return The snapshot of the specified internal frame.
	 */
	public synchronized BufferedImage getSnapshot(JInternalFrame frame) {
		// BufferedImage result = (BufferedImage)
		// DesktopIconHoverPreviewWidget.snapshots
		// .get(frame);
		// if (result != null)
		// return result;
		// // frame.setVisible(true);
		// // updateSnapshot(frame);
		// // frame.setVisible(false);
		// // return snapshots.get(frame);
		// return null;
		return this.snapshot;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidget#requiresCustomLafSupport()
	 */
	public boolean requiresCustomLafSupport() {
		return false;
	}
}

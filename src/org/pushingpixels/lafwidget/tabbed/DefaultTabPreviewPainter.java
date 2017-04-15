/*
 * Copyright (c) 2005-2016 Laf-Widget Kirill Grouchnikov. All Rights Reserved.
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
package org.pushingpixels.lafwidget.tabbed;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JTabbedPane;

import org.pushingpixels.lafwidget.LafWidgetUtilities;
import org.pushingpixels.lafwidget.contrib.intellij.UIUtil;

/**
 * Default implementation of the tab preview painter. The tab preview is a
 * scaled-down (as necessary) thumbnail of the relevant tab.
 * 
 * @author Kirill Grouchnikov
 */
public class DefaultTabPreviewPainter extends TabPreviewPainter {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.tabbed.TabPreviewPainter#hasPreview(javax.swing.JTabbedPane,
	 *      int)
	 */
	public boolean hasPreview(JTabbedPane tabPane, int tabIndex) {
		return (tabPane.getComponentAt(tabIndex) != null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.tabbed.TabPreviewPainter#isSensitiveToEvents(javax.swing.JTabbedPane,
	 *      int)
	 */
	public boolean isSensitiveToEvents(JTabbedPane tabPane, int tabIndex) {
		return tabPane.isEnabledAt(tabIndex);
	}

	@Override
	public void previewTab(JTabbedPane tabPane, int tabIndex, BufferedImage bufferedImage,
			int x, int y, int w, int h) {
		Component tabComponent = tabPane.getComponentAt(tabIndex);
		if (tabComponent == null)
			return;
		// if (!tabComponent.isShowing())
		// return;
		int compWidth = tabComponent.getWidth();
		int compHeight = tabComponent.getHeight();

		if ((compWidth > 0) && (compHeight > 0)) {
			// draw tab component
			BufferedImage tempCanvas = new BufferedImage(compWidth, compHeight,
					BufferedImage.TYPE_INT_ARGB);
			Graphics tempCanvasGraphics = tempCanvas.getGraphics();
			tabComponent.paint(tempCanvasGraphics);

			// check if need to scale down
			double coef = Math.min((double) w / (double) compWidth, (double) h
					/ (double) compHeight);
			// fix for issue 177 in Substance - disabled tabs painted in
			// 50% opacity.
			Graphics2D g2 = (Graphics2D) bufferedImage.createGraphics();
			int scaleFactor = UIUtil.getScaleFactor();
			if (!tabPane.isEnabledAt(tabIndex)) {
				g2.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, 0.5f));
			}
			if (coef < 1.0) {
				int sdWidth = (int) (coef * compWidth);
				int sdHeight = (int) (coef * compHeight);
				int dx = (w - sdWidth) / 2;
				int dy = (h - sdHeight) / 2;

				BufferedImage thumbnail = LafWidgetUtilities.createThumbnail(tempCanvas, sdWidth);
				g2.drawImage(thumbnail, dx, dy, thumbnail.getWidth() / scaleFactor,
						thumbnail.getHeight() / scaleFactor, null);

			} else {
				// System.out.println("Putting " + frame.hashCode() + "
				// -> " + snapshot.hashCode());
				g2.drawImage(tempCanvas, 0, 0, null);
			}
			g2.dispose();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.tabbed.TabPreviewPainter#hasPreviewWindow(javax.swing.JTabbedPane,
	 *      int)
	 */
	public boolean hasPreviewWindow(JTabbedPane tabPane, int tabIndex) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.tabbed.TabPreviewPainter#hasOverviewDialog(javax.swing.JTabbedPane)
	 */
	public boolean hasOverviewDialog(JTabbedPane tabPane) {
		return true;
	}
}

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
package org.pushingpixels.lafwidget.preview;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.JViewport;

import org.pushingpixels.lafwidget.LafWidgetUtilities;

/**
 * Default implementation of the component preview painter. The component
 * preview is a scaled-down (as necessary) thumbnail of the relevant component.
 * 
 * @author Kirill Grouchnikov
 */
public class DefaultPreviewPainter extends PreviewPainter {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.PreviewPainter#hasPreview(java.awt.Container,
	 *      java.awt.Component, int)
	 */
	public boolean hasPreview(Container parent, Component component,
			int componentIndex) {
		return (component != null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.preview.PreviewPainter#previewComponent(java.awt.Container,
	 *      java.awt.Component, int, java.awt.Graphics, int, int, int, int)
	 */
	public void previewComponent(Container parent, Component component,
			int componentIndex, Graphics g, int x, int y, int w, int h) {
		if (component == null)
			return;
		int compWidth = component.getWidth();
		int compHeight = component.getHeight();

		if ((compWidth > 0) && (compHeight > 0)) {
			// draw component
			BufferedImage tempCanvas = new BufferedImage(compWidth, compHeight,
					BufferedImage.TYPE_INT_ARGB);
			Graphics tempCanvasGraphics = tempCanvas.getGraphics();
			component.paint(tempCanvasGraphics);

			// check if need to scale down
			double coef = Math.min((double) w / (double) compWidth, (double) h
					/ (double) compHeight);
			if (coef < 1.0) {
				int sdWidth = (int) (coef * compWidth);
				int sdHeight = (int) (coef * compHeight);
				int dx = x + (w - sdWidth) / 2;
				int dy = y + (h - sdHeight) / 2;

				g.drawImage(LafWidgetUtilities.createThumbnail(tempCanvas,
						sdWidth), dx, dy, null);

			} else {
				g.drawImage(tempCanvas, x, y, null);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.PreviewPainter#hasPreviewWindow(java.awt.Container,
	 *      java.awt.Component, int)
	 */
	public boolean hasPreviewWindow(Container parent, Component component,
			int componentIndex) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.preview.PreviewPainter#getPreviewWindowDimension(java.awt.Container,
	 *      java.awt.Component, int)
	 */
	public Dimension getPreviewWindowDimension(Container parent,
			Component component, int componentIndex) {
		Dimension superResult = super.getPreviewWindowDimension(parent,
				component, componentIndex);
		if (parent instanceof JViewport) {
			Rectangle viewportRect = ((JViewport) parent).getViewRect();
			int width = Math.min(viewportRect.width / 3, superResult.width);
			int height = Math.min(viewportRect.height / 3, superResult.height);
			return new Dimension(width, height);
		}
		return superResult;
	}
}

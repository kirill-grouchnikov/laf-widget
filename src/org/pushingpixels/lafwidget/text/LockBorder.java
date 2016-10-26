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
package org.pushingpixels.lafwidget.text;

import java.awt.*;

import javax.swing.Icon;
import javax.swing.JViewport;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;

import org.pushingpixels.lafwidget.*;

/**
 * Border with "lock" indication.
 * 
 * @author Kirill Grouchnikov
 */
public class LockBorder implements Border, UIResource {
	/**
	 * The original (decorated) border.
	 */
	private Border originalBorder;

	/**
	 * Constructs a new lock border.
	 * 
	 * @param originalBorder
	 *            The original border.
	 */
	public LockBorder(Border originalBorder) {
		if (originalBorder != null)
			this.originalBorder = originalBorder;
		else
			this.originalBorder = new EmptyBorder(0, 0, 0, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.border.Border#getBorderInsets(java.awt.Component)
	 */
	public Insets getBorderInsets(Component c) {
		LafWidgetSupport lafSupport = LafWidgetRepository.getRepository()
				.getLafSupport();
		Icon lockIcon = lafSupport.getLockIcon(c);
		if (lockIcon == null)
			lockIcon = LafWidgetUtilities.getSmallLockIcon();

		Insets origInsets = this.originalBorder.getBorderInsets(c);

		if (c.getComponentOrientation().isLeftToRight()) {
			return new Insets(origInsets.top, Math.max(origInsets.left,
					lockIcon.getIconWidth() + 2), origInsets.bottom,
					origInsets.right);
		} else {
			// support for RTL
			return new Insets(origInsets.top, origInsets.left,
					origInsets.bottom, Math.max(origInsets.right, lockIcon
							.getIconWidth() + 2));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.border.Border#isBorderOpaque()
	 */
	public boolean isBorderOpaque() {
		return this.originalBorder.isBorderOpaque();
	}

	public Border getOriginalBorder() {
		return originalBorder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.border.Border#paintBorder(java.awt.Component,
	 * java.awt.Graphics, int, int, int, int)
	 */
	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		this.originalBorder.paintBorder(c, g, x, y, width, height);
		LafWidgetSupport lafSupport = LafWidgetRepository.getRepository()
				.getLafSupport();
		Icon lockIcon = lafSupport.getLockIcon(c);
		if (lockIcon == null)
			lockIcon = LafWidgetUtilities.getSmallLockIcon();

		int offsetY = 0;
		if (c.getParent() instanceof JViewport) {
			// enhancement 9 - show the lock icon of components
			// in JScrollPane so that it is visible in the bottom
			// corner of the scroll pane
			JViewport viewport = (JViewport) c.getParent();
			// have to set to simple scroll mode since the default (blit)
			// results in visual artifacts due to optimized buffer-copy
			// painting.
			if (viewport.getScrollMode() != JViewport.SIMPLE_SCROLL_MODE) {
				viewport.setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
			}
			Rectangle viewRect = viewport.getViewRect();
			offsetY = c.getHeight() - viewRect.y - viewRect.height;
		}

		if (c.getComponentOrientation().isLeftToRight()) {
			lockIcon.paintIcon(c, g, x, y + height - lockIcon.getIconHeight()
					- offsetY);
		} else {
			// support for RTL
			lockIcon.paintIcon(c, g, x + width - lockIcon.getIconWidth(), y
					+ height - lockIcon.getIconHeight() - offsetY);
		}
	}
}

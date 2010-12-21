/*
 * Copyright (c) 2005-2008 Laf-Widget Kirill Grouchnikov. All Rights Reserved.
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
package test.check.asm;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;

import org.pushingpixels.lafwidget.utils.RenderingUtils;

public class TestUI extends BasicButtonUI {
	public void __update(Graphics g, JComponent c) {
		super.update(g, c);
	}

	@Override
	public void update(Graphics g, JComponent c) {
		Graphics2D graphics = (Graphics2D) g.create();
		RenderingUtils.installDesktopHints(graphics, c);
		this.__update(graphics, c);
		graphics.dispose();
	}
	//
	// public void updateOld(Graphics g, JComponent c) {
	// Graphics2D graphics = (Graphics2D) g;
	// Composite old = graphics.getComposite();
	// float oldAlpha = 1.0f;
	// if (old instanceof AlphaComposite) {
	// AlphaComposite ac = (AlphaComposite) old;
	// if (ac.getRule() == AlphaComposite.SRC_OVER)
	// oldAlpha = ac.getAlpha();
	// }
	// graphics
	// .setComposite(LafWidgetUtilities.getAlphaComposite(c, oldAlpha));
	//
	// Map oldRenderingHints = RenderingUtils.installDesktopHints(graphics, c);
	//
	// this.__update(graphics, c);
	// graphics.setComposite(old);
	// if (oldRenderingHints != null) {
	// graphics.addRenderingHints(oldRenderingHints);
	// }
	// }
}

/*
 * Copyright (c) 2001-2006 JGoodies Karsten Lentzsch. All Rights Reserved.
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
 *  o Neither the name of JGoodies Karsten Lentzsch nor the names of 
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
package org.pushingpixels.lafwidget.utils;

import java.awt.*;
import java.awt.print.PrinterGraphics;
import java.util.*;

import javax.swing.CellRendererPane;
import javax.swing.SwingUtilities;

public class RenderingUtils {
	private static final String PROP_DESKTOPHINTS = "awt.font.desktophints";

	private static Map<String, Map> desktopHintsCache = new HashMap<String, Map>();

	public static Map installDesktopHintsOld(Graphics2D g2, Component c) {
		if (SwingUtilities.getAncestorOfClass(CellRendererPane.class, c) != null) {
			return null;
		}

		Map oldRenderingHints = null;
		Map desktopHints = desktopHints(g2);
		if (desktopHints != null && !desktopHints.isEmpty()) {
			oldRenderingHints = new HashMap(desktopHints.size());
			RenderingHints.Key key;
			for (Iterator i = desktopHints.keySet().iterator(); i.hasNext();) {
				key = (RenderingHints.Key) i.next();
				oldRenderingHints.put(key, g2.getRenderingHint(key));
			}
			g2.addRenderingHints(desktopHints);
			if (c != null) {
				Font font = c.getFont();
				if (font != null) {
					if (font.getSize() > 15) {
						g2.setRenderingHint(
								RenderingHints.KEY_TEXT_ANTIALIASING,
								RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					}
				}
			}
		} else {
			// the following is temporary until the Apple VM 6.0 supports the
			// desktop AA hinting settings.
			if (LookUtils.IS_JAVA_6 && LookUtils.IS_OS_MAC) {
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			}
		}
		return oldRenderingHints;
	}

	public static void installDesktopHints(Graphics2D g2, Component c) {
		if (SwingUtilities.getAncestorOfClass(CellRendererPane.class, c) != null) {
			return;
		}

		Map desktopHints = desktopHints(g2);
		if (desktopHints != null && !desktopHints.isEmpty()) {
			g2.addRenderingHints(desktopHints);
			if (c != null) {
				Font font = c.getFont();
				if (font != null) {
					if (font.getSize() > 15) {
						g2.setRenderingHint(
								RenderingHints.KEY_TEXT_ANTIALIASING,
								RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					}
				}
			}
		} else {
			// the following is temporary until the Apple VM 6.0 supports the
			// desktop AA hinting settings.
			if (LookUtils.IS_JAVA_6 && LookUtils.IS_OS_MAC) {
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			}
		}
	}

	private static Map desktopHints(Graphics2D g2) {
		if (isPrinting(g2)) {
			return null;
		}
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		GraphicsDevice device = g2.getDeviceConfiguration().getDevice();
		String deviceId = device.getIDstring();
		if (!desktopHintsCache.containsKey(deviceId)) {
			Map desktopHints = (Map) toolkit
					.getDesktopProperty(PROP_DESKTOPHINTS + '.'
							+ device.getIDstring());
			if (desktopHints == null) {
				desktopHints = (Map) toolkit
						.getDesktopProperty(PROP_DESKTOPHINTS);
			}
			// It is possible to get a non-empty map but with disabled AA.
			if (desktopHints != null) {
				Object aaHint = desktopHints
						.get(RenderingHints.KEY_TEXT_ANTIALIASING);
				if ((aaHint == RenderingHints.VALUE_TEXT_ANTIALIAS_OFF)
						|| (aaHint == RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT)) {
					desktopHints = null;
				}
			}

			if (desktopHints == null)
				desktopHints = new HashMap();

			desktopHintsCache.put(deviceId, desktopHints);
		}

		return desktopHintsCache.get(deviceId);
	}

	private static boolean isPrinting(Graphics g) {
		return g instanceof PrintGraphics || g instanceof PrinterGraphics;
	}
}

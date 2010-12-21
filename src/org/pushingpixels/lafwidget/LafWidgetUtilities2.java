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
package org.pushingpixels.lafwidget;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import org.pushingpixels.lafwidget.preview.PreviewPainter;
import org.pushingpixels.lafwidget.tabbed.TabPreviewPainter;
import org.pushingpixels.lafwidget.text.PasswordStrengthChecker;

/**
 * Various utility functions.
 * 
 * @author Kirill Grouchnikov
 * @author Romain Guy
 */
public class LafWidgetUtilities2 {
	/**
	 * Private constructor. Is here to enforce using static methods only.
	 */
	private LafWidgetUtilities2() {
	}

	/**
	 * Returns the preview painter for the specified tabbed pane.
	 * 
	 * @param tabbedPane
	 *            Tabbed pane.
	 * @return Preview painter for the specified tabbed pane.
	 */
	public static TabPreviewPainter getTabPreviewPainter(JTabbedPane tabbedPane) {
		if (tabbedPane == null)
			return null;

		// check property on tabbed pane
		Object tabProp = tabbedPane
				.getClientProperty(LafWidget.TABBED_PANE_PREVIEW_PAINTER);
		if (tabProp instanceof TabPreviewPainter)
			return (TabPreviewPainter) tabProp;

		return null;
	}

	/**
	 * Returns the preview painter for the specified component.
	 * 
	 * @param comp
	 *            Component.
	 * @return Preview painter for the specified component.
	 * @since 2.1
	 */
	public static PreviewPainter getComponentPreviewPainter(Component comp) {
		if (comp == null)
			return null;

		// check property on component
		if (comp instanceof JComponent) {
			Object compProp = ((JComponent) comp)
					.getClientProperty(LafWidget.COMPONENT_PREVIEW_PAINTER);
			if (compProp instanceof PreviewPainter)
				return (PreviewPainter) compProp;
		}

		// check property on parent
		Container parent = comp.getParent();
		if (parent instanceof JComponent) {
			Object parentProp = ((JComponent) parent)
					.getClientProperty(LafWidget.COMPONENT_PREVIEW_PAINTER);
			if (parentProp instanceof PreviewPainter)
				return (PreviewPainter) parentProp;
		}

		Object globProp = UIManager.get(LafWidget.COMPONENT_PREVIEW_PAINTER);
		if (globProp instanceof PreviewPainter)
			return (PreviewPainter) globProp;

		return null;
	}

	/**
	 * Returns the password strength checker for the specified password field.
	 * 
	 * @param jpf
	 *            Password field.
	 * @return Password strength checker for the specified password field. The
	 *         result can be <code>null</code>.
	 */
	public static PasswordStrengthChecker getPasswordStrengthChecker(
			JPasswordField jpf) {
		Object obj = jpf.getClientProperty(LafWidget.PASSWORD_STRENGTH_CHECKER);
		if ((obj != null) && (obj instanceof PasswordStrengthChecker))
			return (PasswordStrengthChecker) obj;
		return null;
	}
}

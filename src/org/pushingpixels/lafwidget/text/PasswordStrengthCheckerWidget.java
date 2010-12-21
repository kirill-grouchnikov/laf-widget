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
package org.pushingpixels.lafwidget.text;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPasswordField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;

import org.pushingpixels.lafwidget.*;
import org.pushingpixels.lafwidget.utils.LafConstants.PasswordStrength;

/**
 * Adds password strength indication on password fields.
 * 
 * @author Kirill Grouchnikov
 */
public class PasswordStrengthCheckerWidget extends
		LafWidgetAdapter<JPasswordField> {
	/**
	 * Listens on changes to {@link LafWidget#PASSWORD_STRENGTH_CHECKER}
	 * property.
	 */
	protected PropertyChangeListener strengthCheckerListener;

	/**
	 * Border with password strength indication.
	 * 
	 * @author Kirill Grouchnikov
	 */
	private static class StrengthCheckedBorder implements Border {
		/**
		 * Gutter width.
		 */
		public static final int GUTTER_WIDTH = 5;

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.border.Border#isBorderOpaque()
		 */
		public boolean isBorderOpaque() {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.border.Border#getBorderInsets(java.awt.Component)
		 */
		public Insets getBorderInsets(Component c) {
			JPasswordField jpf = (JPasswordField) c;
			if (LafWidgetUtilities2.getPasswordStrengthChecker(jpf) == null) {
				return new Insets(0, 0, 0, 0);
			} else {
				if (c.getComponentOrientation().isLeftToRight())
					return new Insets(0, 0, 0,
							StrengthCheckedBorder.GUTTER_WIDTH);
				else
					return new Insets(0, StrengthCheckedBorder.GUTTER_WIDTH, 0,
							0);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.border.Border#paintBorder(java.awt.Component,
		 *      java.awt.Graphics, int, int, int, int)
		 */
		public void paintBorder(Component c, Graphics g, int x, int y,
				int width, int height) {
			JPasswordField jpf = (JPasswordField) c;
			PasswordStrengthChecker passwordStrengthChecker = LafWidgetUtilities2
					.getPasswordStrengthChecker(jpf);
			if (passwordStrengthChecker == null)
				return;

			PasswordStrength strength = passwordStrengthChecker.getStrength(jpf
					.getPassword());
			LafWidgetSupport lafSupport = LafWidgetRepository.getRepository()
					.getLafSupport();
			if (c.getComponentOrientation().isLeftToRight())
				lafSupport.paintPasswordStrengthMarker(g, x + width
						- StrengthCheckedBorder.GUTTER_WIDTH, y,
						StrengthCheckedBorder.GUTTER_WIDTH, height, strength);
			else
				lafSupport.paintPasswordStrengthMarker(g, x, y,
						StrengthCheckedBorder.GUTTER_WIDTH, height, strength);

			String tooltip = passwordStrengthChecker.getDescription(strength);
			jpf.setToolTipText(tooltip);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidgetAdapter#installListeners()
	 */
	@Override
	public void installListeners() {
		this.strengthCheckerListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (LafWidget.PASSWORD_STRENGTH_CHECKER.equals(evt
						.getPropertyName())) {
					Object newValue = evt.getNewValue();
					Object oldValue = evt.getOldValue();
					if ((newValue != null)
							&& (newValue instanceof PasswordStrengthChecker)
							&& (!(oldValue instanceof PasswordStrengthChecker))) {
						jcomp
								.setBorder(new BorderUIResource.CompoundBorderUIResource(
										jcomp.getBorder(),
										new StrengthCheckedBorder()));
					} else {
						// restore core border
						Border coreBorder = UIManager
								.getBorder("PasswordField.border");
						jcomp.setBorder(coreBorder);
						jcomp.setToolTipText(null);
					}
				}
			}
		};
		this.jcomp.addPropertyChangeListener(this.strengthCheckerListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidgetAdapter#uninstallListeners()
	 */
	@Override
	public void uninstallListeners() {
		this.jcomp.removePropertyChangeListener(this.strengthCheckerListener);
		this.strengthCheckerListener = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidgetAdapter#installDefaults()
	 */
	@Override
	public void installDefaults() {
		super.installDefaults();

		// check if the property is already set - can happen on LAF change
		Object checker = this.jcomp
				.getClientProperty(LafWidget.PASSWORD_STRENGTH_CHECKER);
		if ((checker != null) && (checker instanceof PasswordStrengthChecker)) {
			this.jcomp.setBorder(new BorderUIResource.CompoundBorderUIResource(
					this.jcomp.getBorder(), new StrengthCheckedBorder()));
		}
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

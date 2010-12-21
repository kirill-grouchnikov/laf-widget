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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import org.pushingpixels.lafwidget.*;

/**
 * Adds visual indication on non-editable text components.
 * 
 * @author Kirill Grouchnikov
 */
public class LockBorderWidget extends LafWidgetAdapter {
	/**
	 * Listens on all properties to decide whether a lock border should be shown
	 * / hidden.
	 */
	protected PropertyChangeListener propertyChangeListener;

	/**
	 * <code>true</code> if this widget is uninstalling. Fix for defect 7.
	 */
	protected boolean isUninstalling = false;

	/**
	 * Name for client property that stores the original border.
	 */
	public static String ORIGINAL_BORDER = "lafwidget.internal.originalBorder";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidgetAdapter#installListeners()
	 */
	@Override
	public void installListeners() {
		this.propertyChangeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				// fix for defect 5 - infinite event chain.
				if ("border".equals(evt.getPropertyName()))
					return;
				if (LockBorderWidget.ORIGINAL_BORDER.equals(evt
						.getPropertyName()))
					return;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						// fix for defect 7 - not removing lock border
						// on LAF switch
						if (isUninstalling)
							return;
						LafWidgetSupport lafSupport = LafWidgetRepository
								.getRepository().getLafSupport();
						boolean hasLockIcon = lafSupport.hasLockIcon(jcomp);
						if (hasLockIcon) {
							installLockBorder();
						} else {
							restoreOriginalBorder();
						}
					}

				});
			}
		};
		this.jcomp.addPropertyChangeListener(this.propertyChangeListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidgetAdapter#uninstallListeners()
	 */
	@Override
	public void uninstallListeners() {
		this.jcomp.removePropertyChangeListener(this.propertyChangeListener);
		this.propertyChangeListener = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidgetAdapter#uninstallUI()
	 */
	@Override
	public void uninstallUI() {
		// fix for issue 7 - restoring original border on LAF switch.
		this.isUninstalling = true;
		Border original = (Border) this.jcomp
				.getClientProperty(LockBorderWidget.ORIGINAL_BORDER);
		if (original != null) {
			this.jcomp.setBorder(original);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidgetAdapter#installDefaults()
	 */
	@Override
	public void installDefaults() {
		super.installDefaults();
		LafWidgetSupport lafSupport = LafWidgetRepository.getRepository()
				.getLafSupport();
		boolean hasLockIcon = lafSupport.hasLockIcon(this.jcomp);
		if (hasLockIcon) {
			Border currBorder = this.jcomp.getBorder();
			this.jcomp.putClientProperty(LockBorderWidget.ORIGINAL_BORDER,
					currBorder);
			this.jcomp.setBorder(new LockBorder(currBorder));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidgetAdapter#uninstallDefaults()
	 */
	@Override
	public void uninstallDefaults() {
		// fix for issue 7 - restoring original border on LAF switch.
		this.isUninstalling = true;
		this.jcomp.putClientProperty(LockBorderWidget.ORIGINAL_BORDER, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidget#requiresCustomLafSupport()
	 */
	public boolean requiresCustomLafSupport() {
		return false;
	}

	/**
	 * Installs the lock border on the associated component.
	 */
	private void installLockBorder() {
		if (jcomp.getClientProperty(LockBorderWidget.ORIGINAL_BORDER) instanceof Border) {
			// already installed
			return;
		}
		// need to install
		Border currBorder = jcomp.getBorder();
		if (currBorder != null) {
			jcomp.putClientProperty(LockBorderWidget.ORIGINAL_BORDER,
					currBorder);
			jcomp.setBorder(new LockBorder(currBorder));
		}
	}

	/**
	 * Restores the original border on the associated component.
	 */
	private void restoreOriginalBorder() {
		if (jcomp.getClientProperty(LockBorderWidget.ORIGINAL_BORDER) instanceof Border) {
			// revert to original
			Border originalBorder = (Border) jcomp
					.getClientProperty(LockBorderWidget.ORIGINAL_BORDER);
			jcomp.setBorder(originalBorder);
			jcomp.putClientProperty(LockBorderWidget.ORIGINAL_BORDER, null);
		} else {
			// already uninstalled
		}
	}
}

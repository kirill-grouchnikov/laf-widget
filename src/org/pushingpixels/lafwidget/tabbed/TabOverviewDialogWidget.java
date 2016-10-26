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

import java.awt.Insets;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import org.pushingpixels.lafwidget.*;

/**
 * Adds tab overview dialog to tabbed panes.
 * 
 * @author Kirill Grouchnikov
 */
public class TabOverviewDialogWidget extends LafWidgetAdapter<JTabbedPane> {
	/**
	 * Tab overview button.
	 */
	protected TabOverviewButton overviewButton;

	/**
	 * Listens on changes to relevant tabbed pane properties.
	 */
	protected PropertyChangeListener propertyListener;

	/**
	 * Listens on tabs being added or removed.
	 */
	protected ContainerListener containerListener;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidgetAdapter#installComponents()
	 */
	@Override
	public void installComponents() {
		this.overviewButton = new TabOverviewButton(this.jcomp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidgetAdapter#installDefaults()
	 */
	@Override
	public void installDefaults() {
		TabPreviewPainter previewPainter = LafWidgetUtilities2
				.getTabPreviewPainter(this.jcomp);
		if ((previewPainter != null)
				&& previewPainter.hasOverviewDialog(this.jcomp)) {

			LafWidgetSupport lafSupport = LafWidgetRepository.getRepository()
					.getLafSupport();

			Insets currTabAreaInsets = lafSupport.getTabAreaInsets(this.jcomp);
			if (currTabAreaInsets == null)
				currTabAreaInsets = UIManager
						.getInsets("TabbedPane.tabAreaInsets");

			Insets tabAreaInsets = new Insets(currTabAreaInsets.top,
					LafWidgetRepository.getRepository().getLafSupport()
							.getLookupButtonSize()
							+ 2 + currTabAreaInsets.left,
					currTabAreaInsets.bottom, currTabAreaInsets.right);
			lafSupport.setTabAreaInsets(this.jcomp, tabAreaInsets);

			this.jcomp.add(this.overviewButton);
			this.overviewButton.setVisible(true);
			this.jcomp.setComponentZOrder(this.overviewButton, 0);
			this.overviewButton.updateLocation(this.jcomp, tabAreaInsets);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidgetAdapter#uninstallComponents()
	 */
	@Override
	public void uninstallComponents() {
		if (this.overviewButton.getParent() == this.jcomp)
			this.jcomp.remove(this.overviewButton);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidgetAdapter#installListeners()
	 */
	@Override
	public void installListeners() {
		this.propertyListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				LafWidgetSupport lafSupport = LafWidgetRepository
						.getRepository().getLafSupport();

				Insets lafInsets = lafSupport
						.getTabAreaInsets(TabOverviewDialogWidget.this.jcomp);
				final Insets currTabAreaInsets = (lafInsets == null) ? UIManager
						.getInsets("TabbedPane.tabAreaInsets")
						: lafInsets;

				if (LafWidget.TABBED_PANE_PREVIEW_PAINTER.equals(evt
						.getPropertyName())) {
					TabPreviewPainter previewPainter = LafWidgetUtilities2
							.getTabPreviewPainter(TabOverviewDialogWidget.this.jcomp);

					if ((previewPainter != null)
							&& previewPainter
									.hasOverviewDialog(TabOverviewDialogWidget.this.jcomp)) {
						Insets tabAreaInsets = new Insets(
								currTabAreaInsets.top, LafWidgetRepository
										.getRepository().getLafSupport()
										.getLookupButtonSize()
										+ 2 + currTabAreaInsets.left,
								currTabAreaInsets.bottom,
								currTabAreaInsets.right);
						lafSupport.setTabAreaInsets(
								TabOverviewDialogWidget.this.jcomp,
								tabAreaInsets);
						TabOverviewDialogWidget.this.jcomp
								.add(TabOverviewDialogWidget.this.overviewButton);
						TabOverviewDialogWidget.this.overviewButton
								.setVisible(true);
						// jtp.setComponentZOrder(overviewButton, 0);
						TabOverviewDialogWidget.this.overviewButton
								.updateLocation(
										TabOverviewDialogWidget.this.jcomp,
										tabAreaInsets);
					} else {
						TabOverviewDialogWidget.this.jcomp
								.remove(TabOverviewDialogWidget.this.overviewButton);
					}
				}
				if ("tabPlacement".equals(evt.getPropertyName())
						|| "componentOrientation".equals(evt.getPropertyName())
						|| "tabAreaInsets".equals(evt.getPropertyName())) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							if (TabOverviewDialogWidget.this.overviewButton
									.getParent() == TabOverviewDialogWidget.this.jcomp)
								TabOverviewDialogWidget.this.overviewButton
										.updateLocation(
												TabOverviewDialogWidget.this.jcomp,
												currTabAreaInsets);
						}
					});
				}
			}
		};
		this.jcomp.addPropertyChangeListener(this.propertyListener);

		this.containerListener = new ContainerAdapter() {
			@Override
			public void componentAdded(ContainerEvent e) {
				syncOverviewButtonVisibility();
			}

			@Override
			public void componentRemoved(ContainerEvent e) {
				syncOverviewButtonVisibility();
			}

			/**
			 * Syncs the visibility of the tab overview button.
			 */
			private void syncOverviewButtonVisibility() {
				if (overviewButton.getParent() != jcomp)
					return;
				// fix for issue 12 - hide the overview button when
				// there are no tabs
				overviewButton.setVisible(jcomp.getTabCount() > 0);
			}
		};
		this.jcomp.addContainerListener(this.containerListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidgetAdapter#uninstallListeners()
	 */
	@Override
	public void uninstallListeners() {
		this.jcomp.removePropertyChangeListener(this.propertyListener);
		this.propertyListener = null;

		this.jcomp.removeContainerListener(this.containerListener);
		this.containerListener = null;
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

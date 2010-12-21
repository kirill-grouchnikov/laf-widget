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

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.pushingpixels.lafwidget.LafWidgetAdapter;
import org.pushingpixels.lafwidget.LafWidgetUtilities;

/**
 * Adds edit context menu on text components.
 * 
 * @author Kirill Grouchnikov
 */
public class EditContextMenuWidget extends LafWidgetAdapter<JTextComponent> {
	/**
	 * Mouse listener for showing the edit context menu.
	 */
	protected MouseListener menuMouseListener;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidget#requiresCustomLafSupport()
	 */
	public boolean requiresCustomLafSupport() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidgetAdapter#installListeners()
	 */
	@Override
	public void installListeners() {
		this.menuMouseListener = new MouseAdapter() {
			// fix for issue 8 - use mousePressed instead of
			// mouseClicked so that it will be triggered on Linux.
			@Override
			public void mousePressed(MouseEvent e) {
				this.handleMouseEvent(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				this.handleMouseEvent(e);
			}

			private void handleMouseEvent(MouseEvent e) {
				if (!LafWidgetUtilities.hasTextEditContextMenu(jcomp))
					return;
				if (!e.isPopupTrigger())
					return;

				// request focus
				jcomp.requestFocus(true);

				JPopupMenu editMenu = new JPopupMenu();
				editMenu.add(new CutAction());
				editMenu.add(new CopyAction());
				editMenu.add(new PasteAction());
				editMenu.addSeparator();
				editMenu.add(new DeleteAction());
				editMenu.add(new SelectAllAction());

				Point pt = SwingUtilities.convertPoint(e.getComponent(), e
						.getPoint(), jcomp);
				editMenu.show(jcomp, pt.x, pt.y);
			}
		};
		jcomp.addMouseListener(this.menuMouseListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidgetAdapter#uninstallListeners()
	 */
	@Override
	public void uninstallListeners() {
		jcomp.removeMouseListener(this.menuMouseListener);
		this.menuMouseListener = null;
	}

	/**
	 * <code>Paste</code> action.
	 * 
	 * @author Kirill Grouchnikov
	 */
	private class PasteAction extends AbstractAction {
		/**
		 * Creates new <code>Paste</code> action.
		 */
		public PasteAction() {
			super(LafWidgetUtilities.getResourceBundle(jcomp).getString(
					"EditMenu.paste"), new ImageIcon(
					EditContextMenuWidget.class.getClassLoader().getResource(
							"org/pushingpixels/lafwidget/text/edit-paste.png")));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(ActionEvent e) {
			jcomp.paste();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.AbstractAction#isEnabled()
		 */
		@Override
		public boolean isEnabled() {
			if (jcomp.isEditable() && jcomp.isEnabled()) {
				Transferable contents = Toolkit.getDefaultToolkit()
						.getSystemClipboard().getContents(this);
				return contents.isDataFlavorSupported(DataFlavor.stringFlavor);
			} else
				return false;
		}
	}

	/**
	 * <code>Select All</code> action.
	 * 
	 * @author Kirill Grouchnikov
	 */
	private class SelectAllAction extends AbstractAction {
		/**
		 * Creates new <code>Select All</code> action.
		 */
		public SelectAllAction() {
			super(
					LafWidgetUtilities.getResourceBundle(jcomp).getString(
							"EditMenu.selectAll"),
					new ImageIcon(
							EditContextMenuWidget.class
									.getClassLoader()
									.getResource(
											"org/pushingpixels/lafwidget/text/edit-select-all.png")));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(ActionEvent e) {
			jcomp.selectAll();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.AbstractAction#isEnabled()
		 */
		@Override
		public boolean isEnabled() {
			return jcomp.isEnabled() && (jcomp.getDocument().getLength() > 0);
		}
	}

	/**
	 * <code>Delete</code> action.
	 * 
	 * @author Kirill Grouchnikov
	 */
	private class DeleteAction extends AbstractAction {
		/**
		 * Creates new <code>Delete</code> action.
		 */
		public DeleteAction() {
			super(
					LafWidgetUtilities.getResourceBundle(jcomp).getString(
							"EditMenu.delete"),
					new ImageIcon(
							EditContextMenuWidget.class
									.getClassLoader()
									.getResource(
											"org/pushingpixels/lafwidget/text/edit-delete.png")));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(ActionEvent e) {
			jcomp.replaceSelection(null);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.AbstractAction#isEnabled()
		 */
		@Override
		public boolean isEnabled() {
			return jcomp.isEditable() && jcomp.isEnabled()
					&& (jcomp.getSelectedText() != null);
		}
	}

	/**
	 * <code>Cut</code> action.
	 * 
	 * @author Kirill Grouchnikov
	 */
	private class CutAction extends AbstractAction {
		/**
		 * Creates new <code>Cut</code> action.
		 */
		public CutAction() {
			super(LafWidgetUtilities.getResourceBundle(jcomp).getString(
					"EditMenu.cut"), new ImageIcon(EditContextMenuWidget.class
					.getClassLoader().getResource(
							"org/pushingpixels/lafwidget/text/edit-cut.png")));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(ActionEvent e) {
			jcomp.cut();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.AbstractAction#isEnabled()
		 */
		@Override
		public boolean isEnabled() {
			return jcomp.isEditable() && jcomp.isEnabled()
					&& (jcomp.getSelectedText() != null);
		}
	}

	/**
	 * <code>Copy</code> action.
	 * 
	 * @author Kirill Grouchnikov
	 */
	private class CopyAction extends AbstractAction {
		/**
		 * Creates new <code>Copy</code> action.
		 */
		public CopyAction() {
			super(LafWidgetUtilities.getResourceBundle(jcomp).getString(
					"EditMenu.copy"), new ImageIcon(EditContextMenuWidget.class
					.getClassLoader().getResource(
							"org/pushingpixels/lafwidget/text/edit-copy.png")));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(ActionEvent e) {
			jcomp.copy();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.AbstractAction#isEnabled()
		 */
		@Override
		public boolean isEnabled() {
			return jcomp.isEnabled() && (jcomp.getSelectedText() != null);
		}
	}
}

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

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.*;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.JTextComponent;

import org.pushingpixels.lafwidget.desktop.DesktopIconHoverPreviewWidget;
import org.pushingpixels.lafwidget.menu.MenuSearchWidget;
import org.pushingpixels.lafwidget.tabbed.TabHoverPreviewWidget;
import org.pushingpixels.lafwidget.tabbed.TabOverviewDialogWidget;
import org.pushingpixels.lafwidget.text.LockBorderWidget;
import org.pushingpixels.lafwidget.text.PasswordStrengthCheckerWidget;
import org.pushingpixels.lafwidget.utils.LafConstants;
import org.pushingpixels.lafwidget.utils.LafConstants.PasswordStrength;

/**
 * LAF-specific support for widgets. Each LAF should override relevant functions
 * based on the internal implementation. Note that if
 * {@link LafWidgetRepository#setLafSupport(LafWidgetSupport)} is called with a
 * custom implementation, that implementation should not throw exceptions in any
 * function.
 * 
 * @author Kirill Grouchnikov
 */
public class LafWidgetSupport {
	/**
	 * Returns the component for desktop icon hover (internal frame preview)
	 * functionality. Is used in the {@link DesktopIconHoverPreviewWidget}
	 * widget.
	 * 
	 * @param desktopIcon
	 *            Desktop icon.
	 * @return The component for desktop icon hover (internal frame preview)
	 *         functionality.
	 */
	public JComponent getComponentForHover(JDesktopIcon desktopIcon) {
		return desktopIcon;
	}

	/**
	 * Returns indication whether the menu search functionality should be
	 * installed on the specified menu bar. Is used in the
	 * {@link MenuSearchWidget} widget.
	 * 
	 * @param menuBar
	 *            Menu bar.
	 * @return <code>true</code> if the menu search functionality should be
	 *         installed on the specified menu bar, <code>false</code>
	 *         otherwise.
	 */
	public boolean toInstallMenuSearch(JMenuBar menuBar) {
		return (MenuSearchWidget.getMenuItemCount(menuBar) > 40);
	}

	/**
	 * Returns indication whether additional functionality should be installed
	 * on the specified component.
	 * 
	 * @param comp
	 *            Component.
	 * @return <code>true</code> if additional functionality should be installed
	 *         on the specified component, <code>false</code> otherwise.
	 */
	public boolean toInstallExtraElements(Component comp) {
		return true;
	}

	/**
	 * Returns the search icon that matches the specified parameters. Is used in
	 * the {@link MenuSearchWidget} widget.
	 * 
	 * @param dimension
	 *            Search icon dimension.
	 * @param componentOrientation
	 *            The orientation for the search icon. Should be considered in
	 *            the implementation code for proper RTL support.
	 * @return The search icon that matches the specified parameters.
	 */
	public Icon getSearchIcon(int dimension,
			ComponentOrientation componentOrientation) {
		return LafWidgetUtilities.getSearchIcon(dimension, componentOrientation
				.isLeftToRight());
	}

	/**
	 * Returns the icon that matches the specified number. This function is used
	 * in {@link MenuSearchWidget} to set icons on menu search results. See
	 * default implementation in {@link LafWidgetUtilities#getHexaMarker(int)}
	 * that returns binary-based icons for numbers from 0 to 15. Is used in the
	 * {@link MenuSearchWidget} widget.
	 * 
	 * @param number
	 *            Number.
	 * @return The icon that matches the specified number.
	 */
	public Icon getNumberIcon(int number) {
		return LafWidgetUtilities.getHexaMarker(number);
	}

	/**
	 * Marks the specified button as <code>flat</code>. A flat button doesn't
	 * show its background unless selected, armed, pressed or (possibly) hovered
	 * over. Some LAFs have flat buttons on toolbars. Is used in
	 * {@link MenuSearchWidget} and {@link TabOverviewDialogWidget} widgets.
	 * 
	 * @param button
	 *            Button to mark as flat.
	 */
	public void markButtonAsFlat(AbstractButton button) {
	}

	/**
	 * Returns the index of the rollover tab in the specified tabbed pane. Is
	 * used in the {@link TabHoverPreviewWidget} widget.
	 * 
	 * @param tabbedPane
	 *            Tabbed pane.
	 * @return The index of the rollover tab in the specified tabbed pane.
	 * @throws UnsupportedOperationException
	 *             In the base implementation.
	 */
	public int getRolloverTabIndex(JTabbedPane tabbedPane) {
		TabbedPaneUI ui = tabbedPane.getUI();
		if (ui instanceof BasicTabbedPaneUI) {
			try {
				Class<?> clazz = ui.getClass();
				while (clazz != null) {
					try {
						Method mtd = clazz.getDeclaredMethod("getRolloverTab",
								new Class[0]);
						if (mtd != null) {
							mtd.setAccessible(true);
							int result = (Integer) mtd
									.invoke(ui, new Object[0]);
							return result;
						}
					} catch (NoSuchMethodException nsme) {
					}
					clazz = clazz.getSuperclass();
				}
			} catch (Throwable t) {
				// ignore all fall through
			}
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * Sets the tab area insets for the specified tabbed pane. Is used in the
	 * {@link TabOverviewDialogWidget} widget.
	 * 
	 * @param tabbedPane
	 *            Tabbed pane.
	 * @param tabAreaInsets
	 *            Tab area insets.
	 * @throws UnsupportedOperationException
	 *             In the base implementation.
	 */
	public void setTabAreaInsets(JTabbedPane tabbedPane, Insets tabAreaInsets) {
		Insets old = this.getTabAreaInsets(tabbedPane);
		TabbedPaneUI ui = tabbedPane.getUI();
		if (ui instanceof BasicTabbedPaneUI) {
			try {
				Class<?> clazz = ui.getClass();
				while (clazz != null) {
					try {
						Field fld = clazz.getDeclaredField("tabAreaInsets");
						if (fld != null) {
							fld.setAccessible(true);
							fld.set(ui, tabAreaInsets);
							// Fire a property change event so that the tabbed
							// pane can revalidate itself
							LafWidgetUtilities.firePropertyChangeEvent(
									tabbedPane, "tabAreaInsets", old,
									tabAreaInsets);
							return;
						}
					} catch (NoSuchFieldException nsfe) {
					}
					clazz = clazz.getSuperclass();
				}
			} catch (Throwable t) {
				// ignore all fall through
			}
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the tab area insets for the specified tabbed pane.Is used in the
	 * {@link TabOverviewDialogWidget} widget.
	 * 
	 * @param tabbedPane
	 *            Tabbed pane.
	 * @return The tab area insets for the specified tabbed pane.
	 */
	public Insets getTabAreaInsets(JTabbedPane tabbedPane) {
		TabbedPaneUI ui = tabbedPane.getUI();
		if (ui instanceof BasicTabbedPaneUI) {
			try {
				Class<?> clazz = ui.getClass();
				while (clazz != null) {
					try {
						Field fld = clazz.getDeclaredField("tabAreaInsets");
						if (fld != null) {
							fld.setAccessible(true);
							Insets result = (Insets) fld.get(ui);
							return result;
						}
					} catch (NoSuchFieldException nsfe) {
					}
					clazz = clazz.getSuperclass();
				}
			} catch (Throwable t) {
				// ignore all fall through
			}
		}
		Insets result = UIManager.getInsets("TabbedPane.tabAreaInsets");
		if (result == null)
			result = new Insets(0, 0, 0, 0);
		return result;
	}

	/**
	 * Returns the tab rectangle for the specified tab in a tabbed pane.Is used
	 * in the {@link TabHoverPreviewWidget} widget.
	 * 
	 * @param tabbedPane
	 *            Tabbed pane.
	 * @param tabIndex
	 *            Index of a tab.
	 * @return The tab rectangle for the specified parameters.
	 * @throws UnsupportedOperationException
	 *             In the base implementation.
	 */
	public Rectangle getTabRectangle(JTabbedPane tabbedPane, int tabIndex) {
		TabbedPaneUI ui = tabbedPane.getUI();
		if (ui instanceof BasicTabbedPaneUI) {
			try {
				Class<?> clazz = ui.getClass();
				while (clazz != null) {
					try {
						Field fld = clazz.getDeclaredField("rects");
						if (fld != null) {
							fld.setAccessible(true);
							Rectangle[] rects = (Rectangle[]) fld.get(ui);
							return rects[tabIndex];
						}
					} catch (NoSuchFieldException nsfe) {
					}
					clazz = clazz.getSuperclass();
				}
			} catch (Throwable t) {
				// ignore all fall through
			}
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * Paints password strength marker. Is used in the
	 * {@link PasswordStrengthCheckerWidget} widget. The default implementation
	 * uses orange color for {@link LafConstants.PasswordStrength#WEAK}
	 * passwords, yellow color for {@link LafConstants.PasswordStrength#MEDIUM}
	 * passwords and green color for
	 * {@link LafConstants.PasswordStrength#STRONG} passwords.
	 * 
	 * @param g
	 *            Graphics context.
	 * @param x
	 *            X coordinate for the marker.
	 * @param y
	 *            Y coordinate for the marker.
	 * @param width
	 *            Marker width.
	 * @param height
	 *            Marker height.
	 * @param pStrength
	 *            Password strength.
	 */
	public void paintPasswordStrengthMarker(Graphics g, int x, int y,
			int width, int height, PasswordStrength pStrength) {
		Graphics2D g2 = (Graphics2D) g.create();
		if (pStrength == PasswordStrength.WEAK)
			g2.setColor(Color.orange);
		if (pStrength == PasswordStrength.MEDIUM)
			g2.setColor(Color.yellow);
		if (pStrength == PasswordStrength.STRONG)
			g2.setColor(Color.green);
		g2.fillRect(x, y, width, height);
		g2.dispose();
	}

	/**
	 * Checks whether the specified component should show a lock icon. Is used
	 * in the {@link LockBorderWidget} widget.
	 * 
	 * @param comp
	 *            Component.
	 * @return <code>true</code> if the specified component should show a lock
	 *         icon, <code>false</code> otherwise.
	 */
	public boolean hasLockIcon(Component comp) {
		// check the HAS_LOCK_ICON property
		boolean isEditableTextComponent = (comp instanceof JTextComponent) ? ((JTextComponent) comp)
				.isEditable()
				: false;
		if (comp instanceof JComponent) {
			if (!isEditableTextComponent
					&& Boolean.TRUE.equals(((JComponent) comp)
							.getClientProperty(LafWidget.HAS_LOCK_ICON)))
				return true;
			if (Boolean.FALSE.equals(((JComponent) comp)
					.getClientProperty(LafWidget.HAS_LOCK_ICON)))
				return false;
		}
		if (!isEditableTextComponent
				&& Boolean.TRUE.equals(UIManager.get(LafWidget.HAS_LOCK_ICON)))
			return true;

		return false;
	}

	/**
	 * Returns the lock icon. Is used in {@link LockBorderWidget} widget.
	 * 
	 * @return Lock icon. Should be sufficiently small (preferrably not more
	 *         than 5-6 pixels wide).
	 */
	public Icon getLockIcon(Component c) {
		return LafWidgetUtilities.getSmallLockIcon();
	}

	/**
	 * Returns the arrow icon (the icon used in combo box drop button, scroll
	 * bar buttons etc.).
	 * 
	 * @param orientation
	 *            One of {@link SwingConstants#NORTH} or
	 *            {@link SwingConstants#SOUTH}.
	 * @return Arrow icon.
	 */
	public Icon getArrowIcon(int orientation) {
		return null;
	}

	/**
	 * Returns the size of the lookup icon. Override to handle high DPI mode.
	 * 
	 * @return The size of the lookup icon.
	 */
	public int getLookupIconSize() {
		return 14;
	}

	/**
	 * Returns the size of the lookup button. Override to handle high DPI mode.
	 * 
	 * @return The size of the lookup button.
	 */
	public int getLookupButtonSize() {
		return 16;
	}
}

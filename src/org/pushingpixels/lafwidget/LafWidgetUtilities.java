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
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import org.pushingpixels.lafwidget.animation.AnimationConfigurationManager;
import org.pushingpixels.lafwidget.animation.AnimationFacet;

/**
 * Various utility functions.
 * 
 * @author Kirill Grouchnikov
 * @author Romain Guy
 */
public class LafWidgetUtilities {
	/**
	 * Name for the internal client property that marks a component as
	 * previewable.
	 */
	public static final String PREVIEW_MODE = "lafwidgets.internal.previewMode";

	/**
	 * Private constructor. Is here to enforce using static methods only.
	 */
	private LafWidgetUtilities() {
	}

	/**
	 * Retrieves transparent image of specified dimension.
	 * 
	 * @param width
	 *            Image width.
	 * @param height
	 *            Image height.
	 * @return Transparent image of specified dimension.
	 */
	public static BufferedImage getBlankImage(int width, int height) {
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);

		// get graphics and set hints
		Graphics2D graphics = (Graphics2D) image.getGraphics().create();
		graphics.setColor(new Color(0, 0, 0, 0));
		graphics.setComposite(AlphaComposite.Src);
		graphics.fillRect(0, 0, width, height);
		graphics.dispose();

		return image;
	}

	/**
	 * Creates a compatible image (for efficient processing and drawing).
	 * 
	 * @param image
	 *            The original image.
	 * @return Compatible version of the original image.
	 * @author Romain Guy
	 */
	public static BufferedImage createCompatibleImage(BufferedImage image) {
		GraphicsEnvironment e = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice d = e.getDefaultScreenDevice();
		GraphicsConfiguration c = d.getDefaultConfiguration();
		BufferedImage compatibleImage = c.createCompatibleImage(image
				.getWidth(), image.getHeight());
		Graphics g = compatibleImage.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return compatibleImage;
	}

	/**
	 * Creates a thumbnail of the specified width.
	 * 
	 * @param image
	 *            The original image.
	 * @param requestedThumbWidth
	 *            The width of the resulting thumbnail.
	 * @return Thumbnail of the specified width.
	 * @author Romain Guy
	 */
	public static BufferedImage createThumbnail(BufferedImage image,
			int requestedThumbWidth) {
		float ratio = (float) image.getWidth() / (float) image.getHeight();
		int width = image.getWidth();
		BufferedImage thumb = image;

		do {
			width /= 2;
			if (width < requestedThumbWidth) {
				width = requestedThumbWidth;
			}

			BufferedImage temp = new BufferedImage(width,
					(int) (width / ratio), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = temp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.drawImage(thumb, 0, 0, temp.getWidth(), temp.getHeight(), null);
			g2.dispose();

			thumb = temp;
		} while (width != requestedThumbWidth);

		return thumb;
	}

	/**
	 * Returns search icon.
	 * 
	 * @param dimension
	 *            Icon dimension.
	 * @param leftToRight
	 *            Indicates the orientation of the resulting icon.
	 * @return Search icon.
	 */
	public static Icon getSearchIcon(int dimension, boolean leftToRight) {
		BufferedImage result = LafWidgetUtilities.getBlankImage(dimension,
				dimension);

		Graphics2D graphics = (Graphics2D) result.getGraphics().create();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		graphics.setColor(Color.black);

		graphics.setStroke(new BasicStroke(1.5f));
		if (leftToRight) {
			int xc = (int) (0.6 * dimension);
			int yc = (int) (0.45 * dimension);
			int r = (int) (0.3 * dimension);

			graphics.drawOval(xc - r, yc - r, 2 * r, 2 * r);

			graphics.setStroke(new BasicStroke(3.0f));
			GeneralPath handle = new GeneralPath();
			handle.moveTo((float) (xc - r / Math.sqrt(2.0)), (float) (yc + r
					/ Math.sqrt(2.0)));
			handle.lineTo(1.8f, dimension - 2.2f);
			graphics.draw(handle);
		} else {
			int xc = (int) (0.4 * dimension);
			int yc = (int) (0.45 * dimension);
			int r = (int) (0.3 * dimension);

			graphics.drawOval(xc - r, yc - r, 2 * r, 2 * r);

			graphics.setStroke(new BasicStroke(3.0f));
			GeneralPath handle = new GeneralPath();
			handle.moveTo((float) (xc + r / Math.sqrt(2.0)), (float) (yc + r
					/ Math.sqrt(2.0)));
			handle.lineTo(dimension - 2.5f, dimension - 2.2f);
			graphics.draw(handle);
		}

		graphics.dispose();
		return new ImageIcon(result);
	}

	/**
	 * Returns small icon representation of the specified integer value. The
	 * remainder of dividing the integer by 16 is translated to four circles
	 * arranged in 2*2 grid.
	 * 
	 * @param value
	 *            Integer value to represent.
	 * @return Icon representation of the specified integer value.
	 */
	public static Icon getHexaMarker(int value) {
		BufferedImage result = LafWidgetUtilities.getBlankImage(9, 9);

		value %= 16;
		Color offColor = Color.gray;
		Color onColor = Color.black;

		boolean bit1 = ((value & 0x1) != 0);
		boolean bit2 = ((value & 0x2) != 0);
		boolean bit3 = ((value & 0x4) != 0);
		boolean bit4 = ((value & 0x8) != 0);

		Graphics2D graphics = (Graphics2D) result.getGraphics().create();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		graphics.setColor(bit1 ? onColor : offColor);
		graphics.fillOval(5, 5, 4, 4);
		graphics.setColor(bit2 ? onColor : offColor);
		graphics.fillOval(5, 0, 4, 4);
		graphics.setColor(bit3 ? onColor : offColor);
		graphics.fillOval(0, 5, 4, 4);
		graphics.setColor(bit4 ? onColor : offColor);
		graphics.fillOval(0, 0, 4, 4);

		graphics.dispose();
		return new ImageIcon(result);
	}

	/**
	 * Makes the specified component and all its descendants previewable.
	 * 
	 * @param comp
	 *            Component.
	 * @param dbSnapshot
	 *            The "snapshot" map that will contain the original
	 *            double-buffer status of the specified component and all its
	 *            descendants. Key is {@link JComponent}, value is
	 *            {@link Boolean}.
	 */
	public static void makePreviewable(Component comp,
			Map<Component, Boolean> dbSnapshot) {
		if (comp instanceof JComponent) {
			JComponent jcomp = (JComponent) comp;
			// if (jcomp.getParent() instanceof CellRendererPane) {
			// System.out.println(jcomp.getClass().getSimpleName() + ":"
			// + jcomp.hashCode());
			// }
			dbSnapshot.put(jcomp, Boolean.valueOf(jcomp.isDoubleBuffered()));
			jcomp.setDoubleBuffered(false);
			jcomp.putClientProperty(LafWidgetUtilities.PREVIEW_MODE,
					Boolean.TRUE);
		}
		if (comp instanceof Container) {
			Container cont = (Container) comp;
			for (int i = 0; i < cont.getComponentCount(); i++)
				LafWidgetUtilities.makePreviewable(cont.getComponent(i),
						dbSnapshot);
		}
	}

	/**
	 * Restores the regular (non-previewable) status of the specified component
	 * and all its descendants.
	 * 
	 * @param comp
	 *            Component.
	 * @param dbSnapshot
	 *            The "snapshot" map that contains the original double-buffer
	 *            status of the specified component and all its descendants. Key
	 *            is {@link JComponent}, value is {@link Boolean}.
	 */
	public static void restorePreviewable(Component comp,
			Map<Component, Boolean> dbSnapshot) {
		if (comp instanceof JComponent) {
			JComponent jcomp = (JComponent) comp;
			if (dbSnapshot.containsKey(comp)) {
				jcomp.setDoubleBuffered(dbSnapshot.get(comp));
				jcomp.putClientProperty(LafWidgetUtilities.PREVIEW_MODE, null);
			} else {
				// this can happen in case the application has
				// renderers (combos, ...). Take the property from the parent
				Component parent = comp.getParent();
				if (parent instanceof JComponent) {
					jcomp.setDoubleBuffered(dbSnapshot.get(parent));
					jcomp.putClientProperty(LafWidgetUtilities.PREVIEW_MODE,
							null);
				}
				// System.out.println("Not found");
				// Component c = jcomp;
				// while (c != null) {
				// System.out.println("\t" + c.getClass().getSimpleName()
				// + ":" + c.hashCode());
				// c = c.getParent();
				// }
			}
		}
		if (comp instanceof Container) {
			Container cont = (Container) comp;
			for (int i = 0; i < cont.getComponentCount(); i++)
				LafWidgetUtilities.restorePreviewable(cont.getComponent(i),
						dbSnapshot);
		}
	}

	/**
	 * Returns a lock icon.
	 * 
	 * @return Lock icon.
	 */
	public static Icon getSmallLockIcon() {
		BufferedImage result = LafWidgetUtilities.getBlankImage(6, 8);

		Color fore = Color.black;
		Color fill = new Color(208, 208, 48);

		Graphics2D graphics = (Graphics2D) result.getGraphics().create();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);

		graphics.setColor(fill);
		graphics.fillRect(1, 3, 4, 4);
		graphics.setColor(fore);
		graphics.drawLine(0, 3, 0, 7);
		graphics.drawLine(5, 3, 5, 7);
		graphics.drawLine(0, 7, 5, 7);
		graphics.drawLine(1, 2, 4, 2);
		graphics.drawLine(1, 1, 1, 2);
		graphics.drawLine(4, 1, 4, 2);
		graphics.drawLine(2, 0, 3, 0);
		graphics.drawLine(2, 4, 3, 4);
		graphics.drawLine(2, 5, 3, 5);

		graphics.dispose();
		return new ImageIcon(result);
	}

	/**
	 * Checks whether the specified text component has
	 * "select all on focus gain" property.
	 * 
	 * @param textComp
	 *            Text component.
	 * @return <code>true</code> if the specified text component has "select all
	 *         on focus gain" property, <code>false</code> otherwise.
	 */
	public static boolean hasTextFocusSelectAllProperty(JTextComponent textComp) {
		Component comp = textComp;
		while (comp != null) {
			if (comp instanceof JComponent) {
				Object textFocusSelectAllProperty = ((JComponent) comp)
						.getClientProperty(LafWidget.TEXT_SELECT_ON_FOCUS);
				if (Boolean.TRUE.equals(textFocusSelectAllProperty))
					return true;
				if (Boolean.FALSE.equals(textFocusSelectAllProperty))
					return false;
			}
			comp = comp.getParent();
		}
		return (Boolean.TRUE.equals(UIManager
				.get(LafWidget.TEXT_SELECT_ON_FOCUS)));
	}

	/**
	 * Checks whether the specified text component has "flip select on escape"
	 * property.
	 * 
	 * @param textComp
	 *            Text component.
	 * @return <code>true</code> if the specified text component has "flip
	 *         select on escape" property, <code>false</code> otherwise.
	 */
	public static boolean hasTextFlipSelectOnEscapeProperty(
			JTextComponent textComp) {
		Object textFocusSelectAllProperty = textComp
				.getClientProperty(LafWidget.TEXT_FLIP_SELECT_ON_ESCAPE);
		return (Boolean.TRUE.equals(textFocusSelectAllProperty));
	}

	/**
	 * Checks whether the specified text component has edit context menu
	 * property.
	 * 
	 * @param textComp
	 *            Text component.
	 * @return <code>true</code> if the specified text component has edit
	 *         context menu property, <code>false</code> otherwise.
	 */
	public static boolean hasTextEditContextMenu(JTextComponent textComp) {
		Object textEditContextMenuProperty = textComp
				.getClientProperty(LafWidget.TEXT_EDIT_CONTEXT_MENU);
		if (Boolean.TRUE.equals(textEditContextMenuProperty))
			return true;
		if (Boolean.FALSE.equals(textEditContextMenuProperty))
			return false;
		return (Boolean.TRUE.equals(UIManager
				.get(LafWidget.TEXT_EDIT_CONTEXT_MENU)));
	}

	/**
	 * Checks whether the specified scroll pane supports auto scroll.
	 * 
	 * @param scrollPane
	 *            Scroll pane component.
	 * @return <code>true</code> if the specified scroll pane supports auto
	 *         scroll, <code>false</code> otherwise.
	 */
	public static boolean hasAutoScroll(JScrollPane scrollPane) {
		Object compProperty = scrollPane
				.getClientProperty(LafWidget.AUTO_SCROLL);
		if (Boolean.TRUE.equals(compProperty))
			return true;
		if (Boolean.FALSE.equals(compProperty))
			return false;
		return (Boolean.TRUE.equals(UIManager.get(LafWidget.AUTO_SCROLL)));
	}

	/**
	 * Checks whether the specified tree component has automatic drag and drop
	 * support.
	 * 
	 * @param tree
	 *            Tree component.
	 * @return <code>true</code> if the specified text component has automatic
	 *         drag and drop support, <code>false</code> otherwise.
	 */
	public static boolean hasAutomaticDnDSupport(JTree tree) {
		Object dndProperty = tree
				.getClientProperty(LafWidget.TREE_AUTO_DND_SUPPORT);
		if (Boolean.TRUE.equals(dndProperty))
			return true;
		if (Boolean.FALSE.equals(dndProperty))
			return false;
		return (Boolean.TRUE.equals(UIManager
				.get(LafWidget.TREE_AUTO_DND_SUPPORT)));
	}

	/**
	 * Checks whether the label lookup should use component-specific locale on
	 * the specified component.
	 * 
	 * @param jcomp
	 *            Component.
	 * @return <code>true</code> if the custom labels should be looked up based
	 *         on the component locale as returned by
	 *         {@link JComponent#getLocale()}, <code>false</code> if the custom
	 *         labels should be looked up based on the global locale as returned
	 *         by {@link Locale#getDefault()}.
	 */
	public static boolean toIgnoreGlobalLocale(JComponent jcomp) {
		if (jcomp == null)
			return false;
		return Boolean.TRUE.equals(jcomp
				.getClientProperty(LafWidget.IGNORE_GLOBAL_LOCALE));
	}

	/**
	 * Returns the resource bundle for the specified component.
	 * 
	 * @param jcomp
	 *            Component.
	 * @return Resource bundle for the specified component.
	 */
	public static ResourceBundle getResourceBundle(JComponent jcomp) {
		if (toIgnoreGlobalLocale(jcomp)) {
			return LafWidgetRepository.getLabelBundle(jcomp.getLocale());
		} else {
			return LafWidgetRepository.getLabelBundle();
		}
	}

	/**
	 * Checks whether the specified component has been configured (specifically
	 * or globally) to have no animations of the specific facet. Can be used to
	 * cull unnecessary code in animation listeners on large tables and lists.
	 * 
	 * @param comp
	 *            Component.
	 * @param animationFacet
	 *            Animation facet.
	 * @return <code>true</code> if the specified component has been configured
	 *         (specifically or globally) to have no animations of the specific
	 *         facet, <code>false</code> otherwise.
	 */
	public static boolean hasNoAnimations(Component comp,
			AnimationFacet animationFacet) {
		return !AnimationConfigurationManager.getInstance().isAnimationAllowed(
				animationFacet, comp);
	}

	/**
	 * Returns the current icon for the specified button. This method is <b>for
	 * internal use only</b>.
	 * 
	 * @param b
	 *            Button.
	 * @return Icon for the specified button.
	 */
	public static Icon getIcon(AbstractButton b) {
		Icon icon = b.getIcon();
		if (icon == null)
			return null;
		ButtonModel model = b.getModel();
		Icon tmpIcon = null;

		if (icon != null) {
			if (!model.isEnabled()) {
				if (model.isSelected()) {
					tmpIcon = b.getDisabledSelectedIcon();
				} else {
					tmpIcon = b.getDisabledIcon();
				}
			} else if (model.isPressed() && model.isArmed()) {
				tmpIcon = b.getPressedIcon();
			} else if (b.isRolloverEnabled() && model.isRollover()) {
				if (model.isSelected()) {
					tmpIcon = b.getRolloverSelectedIcon();
				} else {
					tmpIcon = b.getRolloverIcon();
				}
			} else if (model.isSelected()) {
				tmpIcon = b.getSelectedIcon();
			}

			if (tmpIcon != null) {
				icon = tmpIcon;
			}
		}
		return icon;
	}

	public static boolean toIgnoreAnimations(Component comp) {
		if (comp instanceof JMenuItem)
			return false;
		return (SwingUtilities.getAncestorOfClass(CellRendererPane.class, comp) != null);
	}

	/**
	 * Tests UI threading violations on changing the state the specified
	 * component.
	 * 
	 * @param comp
	 *            Component.
	 * @throws UiThreadingViolationException
	 *             If the component is changing state off Event Dispatch Thread.
	 */
	public static void testComponentStateChangeThreadingViolation(Component comp) {
		if (!SwingUtilities.isEventDispatchThread()) {
			UiThreadingViolationException uiThreadingViolationError = new UiThreadingViolationException(
					"Component state change must be done on Event Dispatch Thread");
			uiThreadingViolationError.printStackTrace(System.err);
			throw uiThreadingViolationError;
		}
	}

	/**
	 * Fires the matching property change event on the specific component.
	 * 
	 * @param component
	 *            Component.
	 * @param propertyName
	 *            Property name.
	 * @param oldValue
	 *            Old property value.
	 * @param newValue
	 *            New property value.
	 */
	public static void firePropertyChangeEvent(JComponent component,
			String propertyName, Object oldValue, Object newValue) {
		PropertyChangeEvent pce = new PropertyChangeEvent(component,
				propertyName, oldValue, newValue);
		for (PropertyChangeListener general : component
				.getPropertyChangeListeners()) {
			general.propertyChange(pce);
		}
		for (PropertyChangeListener specific : component
				.getPropertyChangeListeners(propertyName)) {
			specific.propertyChange(pce);
		}
	}

	/**
	 * Returns the composite to use for painting the specified component. The
	 * result should be set on the {@link Graphics2D} before any custom
	 * rendering is done. This method can be used by application painting code
	 * and by look-and-feel delegates.
	 * 
	 * @param c
	 *            Component.
	 * @param translucency
	 *            The translucency of the original painting.
	 * @param g
	 *            The original graphics context.
	 * @return The composite to use for painting the specified component.
	 */
	public static Composite getAlphaComposite(Component c, float translucency,
			Graphics g) {
		float xFactor = 1.0f;
		if (g instanceof Graphics2D) {
			Graphics2D g2d = (Graphics2D) g;
			Composite existingComposite = g2d.getComposite();
			if (existingComposite instanceof AlphaComposite) {
				AlphaComposite ac = (AlphaComposite) existingComposite;
				if (ac.getRule() == AlphaComposite.SRC_OVER)
					xFactor = ac.getAlpha();
			}
		}
		float finalAlpha = translucency * xFactor;
		if (finalAlpha < 0.0f) {
			finalAlpha = 0.0f;
		}
		if (finalAlpha > 1.0f) {
			finalAlpha = 1.0f;
		}
		if (finalAlpha == 1.0f) {
			return AlphaComposite.SrcOver;
		}
		return AlphaComposite.SrcOver.derive(finalAlpha);
	}

	public static Composite getAlphaComposite(Component c, float translucency) {
		return getAlphaComposite(c, translucency, null);
	}

	/**
	 * Returns the composite to use for painting the specified component. The
	 * result should be set on the {@link Graphics2D} before any custom
	 * rendering is done. This method can be used by application painting code
	 * and by look-and-feel delegates.
	 * 
	 * @param c
	 *            Component.
	 * @return The composite to use for painting the specified component.
	 */
	public static Composite getAlphaComposite(Component c, Graphics g) {
		return getAlphaComposite(c, 1.0f, g);
	}

	/**
	 * Returns the composite to use for painting the specified component. The
	 * result should be set on the {@link Graphics2D} before any custom
	 * rendering is done. This method can be used by application painting code
	 * and by look-and-feel delegates.
	 * 
	 * @param c
	 *            Component.
	 * @return The composite to use for painting the specified component.
	 */
	public static Composite getAlphaComposite(Component c) {
		return getAlphaComposite(c, 1.0f, null);
	}
}

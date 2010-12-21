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
package org.pushingpixels.lafwidget.tree.dnd;

import java.awt.*;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.border.Border;

/**
 * DnDBorderFactory is responsible for creating node borders used under
 * different drag and drop operations.
 * 
 * @author Antonio Vieiro (antonio@antonioshome.net), $Author: kirillcool $
 */
class DnDBorderFactory {
	/**
	 * DropAllowedBorder is a Border that indicates that something is being
	 * droped on top of a valid node.
	 * 
	 * @author Antonio Vieiro (antonio@antonioshome.net), $Author: kirillcool $
	 */
	static class DropAllowedBorder implements Border {
		private static Insets insets = new Insets(0, 0, 3, 0);
		private ImageIcon plusIcon;

		/**
		 * Creates a new instance of DropAllowedBorder
		 */
		public DropAllowedBorder() {
			URL iconURL = DropAllowedBorder.class
					.getResource("icons/drop-on-leaf.png");
			if (iconURL != null)
				this.plusIcon = new ImageIcon(iconURL);
		}

		public void paintBorder(Component c, Graphics g, int x, int y,
				int width, int height) {
			int yh = y + height - 1;
			if (this.plusIcon != null) {
				this.plusIcon.paintIcon(c, g, x + 8, yh - 8);
			}
			yh -= 4;
			g.setColor(Color.DARK_GRAY);
			g.drawLine(x + 24, yh, x + 48, yh);
		}

		public Insets getBorderInsets(Component c) {
			return DropAllowedBorder.insets;
		}

		public boolean isBorderOpaque() {
			return false;
		}
	}

	/**
	 * OffsetBorder is a Border that contains an offset. This is used to
	 * "separate" the node under the drop.
	 * 
	 * @author Antonio Vieiro (antonio@antonioshome.net), $Author: kirillcool $
	 */
	class OffsetBorder implements Border {
		private Insets insets = new Insets(5, 0, 0, 0);

		public void paintBorder(Component c, Graphics g, int x, int y,
				int width, int height) {
			// empty
		}

		public Insets getBorderInsets(Component c) {
			return this.insets;
		}

		public boolean isBorderOpaque() {
			return false;
		}

	}

	/**
	 * DropOnNodeBorder is a Border that indicates that something cannot be
	 * dropped here.
	 * 
	 * @author Antonio Vieiro (antonio@antonioshome.net), $Author: kirillcool $
	 */
	class DropNotAllowedBorder implements Border {
		private Insets insets = new Insets(0, 0, 0, 0);
		private ImageIcon plusIcon;

		/**
		 * Creates a new instance of DropOnNodeBorder
		 */
		public DropNotAllowedBorder() {
			URL iconURL = DnDBorderFactory.class
					.getResource("icons/drop-not-allowed.png");
			if (iconURL != null)
				this.plusIcon = new ImageIcon(iconURL);
		}

		public void paintBorder(Component c, Graphics g, int x, int y,
				int width, int height) {
			if (this.plusIcon != null) {
				this.plusIcon.paintIcon(c, g, x, y);
			}
			// g.setColor( Color.RED );
			// g.drawRect( x, y, width-1, height-1 );
		}

		public Insets getBorderInsets(Component c) {
			return this.insets;
		}

		public boolean isBorderOpaque() {
			return false;
		}

	}

	/**
	 * Creates a new instance of DnDBorderFactory
	 */
	public DnDBorderFactory() {
		this.setDropAllowedBorder(new DropAllowedBorder()); // DropOnFolderBorder()
		// );
		this.setDropNotAllowedBorder(new DropNotAllowedBorder());
		this.setOffsetBorder(new OffsetBorder());
		this.setEmptyBorder(BorderFactory.createEmptyBorder());
	}

	/**
	 * Holds value of property dropAllowedBorder.
	 */
	private Border dropAllowedBorder;

	/**
	 * Getter for property dropAllowedBorder.
	 * 
	 * @return Value of property dropAllowedBorder.
	 */
	public Border getDropAllowedBorder() {
		return this.dropAllowedBorder;
	}

	/**
	 * Setter for property dropAllowedBorder.
	 * 
	 * @param dropAllowedBorder
	 *            New value of property dropAllowedBorder.
	 */
	public void setDropAllowedBorder(Border dropAllowedBorder) {
		this.dropAllowedBorder = dropAllowedBorder;
	}

	/**
	 * Holds value of property dropNotAllowedBorder.
	 */
	private Border dropNotAllowedBorder;

	/**
	 * Getter for property dropNotAllowedBorder.
	 * 
	 * @return Value of property dropNotAllowedBorder.
	 */
	public Border getDropNotAllowedBorder() {
		return this.dropNotAllowedBorder;
	}

	/**
	 * Setter for property dropNotAllowedBorder.
	 * 
	 * @param dropNotAllowedBorder
	 *            New value of property dropNotAllowedBorder.
	 */
	public void setDropNotAllowedBorder(Border dropNotAllowedBorder) {
		this.dropNotAllowedBorder = dropNotAllowedBorder;
	}

	/**
	 * Holds value of property offsetBorder.
	 */
	private Border offsetBorder;

	/**
	 * Getter for property offsetBorder.
	 * 
	 * @return Value of property offsetBorder.
	 */
	public Border getOffsetBorder() {
		return this.offsetBorder;
	}

	/**
	 * Setter for property offsetBorder.
	 * 
	 * @param offsetBorder
	 *            New value of property offsetBorder.
	 */
	public void setOffsetBorder(Border offsetBorder) {
		this.offsetBorder = offsetBorder;
	}

	private Border emptyBorder;

	public Border getEmptyBorder() {
		return this.emptyBorder;
	}

	public void setEmptyBorder(Border anEmptyBorder) {
		this.emptyBorder = anEmptyBorder;
	}
}

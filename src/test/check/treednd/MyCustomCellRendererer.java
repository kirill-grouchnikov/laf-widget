/*
 * $Id: MyCustomCellRendererer.java 261 2009-11-26 06:05:37Z kirillcool $
 * Read the "license.txt" file for licensing information.
 * (C) Antonio Vieiro. All rights reserved.
 */

package test.check.treednd;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * MyCustomCellRendererer is a cell renderer that shows some (ugly, sorry) icons
 * 
 * @author Antonio Vieiro (antonio@antonioshome.net), $Author: kirillcool $
 */
class MyCustomCellRendererer extends DefaultTreeCellRenderer {
	ImageIcon orangeIcon;
	ImageIcon appleIcon;

	public MyCustomCellRendererer() {
		this.orangeIcon = new ImageIcon(this.getClass().getResource(
				"orange.png"));
		this.appleIcon = new ImageIcon(this.getClass().getResource("apple.png"));
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);

		if (value.toString().startsWith("orange"))
			this.setIcon(this.orangeIcon);
		else if (value.toString().startsWith("apple"))
			this.setIcon(this.appleIcon);
		else
			this.setIcon(null);

		return this;
	}

}

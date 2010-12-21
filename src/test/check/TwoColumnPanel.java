/*
 * Copyright (c) 2005-2006 Substance Kirill Grouchnikov. All Rights Reserved.
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
 *  o Neither the name of Substance Kirill Grouchnikov nor the names of 
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
package test.check;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class TwoColumnPanel extends JPanel {
	private static class ComponentRow {
		public Component left;

		public Component right;

		public ComponentRow(Component left, Component right) {
			this.left = left;
			this.right = right;
		}
	}

	private ArrayList rows;

	private TwoColumnLayout layout;

	public TwoColumnPanel() {
		super();
		this.rows = new ArrayList();
		this.layout = new TwoColumnLayout();
		this.setLayout(this.layout);
	}

	public void addRow(Component left, Component right) {
		if ((left == null) && (right == null))
			return;
		ComponentRow newRow = new ComponentRow(left, right);
		if (left != null)
			this.add(left);
		if (right != null)
			this.add(right);
		this.rows.add(newRow);
	}

	private class TwoColumnLayout implements LayoutManager {
		private int verticalSpacing;

		private int horizontalSpacing;

		private int minimumLeftWidth;

		private int minimumRightWidth;

		public int getHorizontalSpacing() {
			return this.horizontalSpacing;
		}

		public void setHorizontalSpacing(int horizontalSpacing) {
			this.horizontalSpacing = horizontalSpacing;
		}

		public int getMinimumLeftWidth() {
			return this.minimumLeftWidth;
		}

		public void setMinimumLeftWidth(int minimumLeftWidth) {
			this.minimumLeftWidth = minimumLeftWidth;
		}

		public int getMinimumRightWidth() {
			return this.minimumRightWidth;
		}

		public void setMinimumRightWidth(int minimumRightWidth) {
			this.minimumRightWidth = minimumRightWidth;
		}

		public int getVerticalSpacing() {
			return this.verticalSpacing;
		}

		public void setVerticalSpacing(int verticalSpacing) {
			this.verticalSpacing = verticalSpacing;
		}

		public TwoColumnLayout() {
			this.horizontalSpacing = 3;
			this.verticalSpacing = 3;
			this.minimumLeftWidth = 0;
			this.minimumRightWidth = 0;
		}

		public void addLayoutComponent(String name, Component comp) {
		}

		public void layoutContainer(Container parent) {
			Insets insets = parent.getInsets();
			int y = this.horizontalSpacing + insets.top;
			int leftPref = this.getPreferredLeftWidth();
			int rightPref = this.getPreferredRightWidth();
			double coef = (double) (parent.getWidth() - insets.left
					- insets.right - 3 * this.verticalSpacing)
					/ (double) (leftPref + rightPref);
			int leftFinal = (int) (coef * leftPref);
			int rightFinal = (int) (coef * rightPref);
			int leftStart = insets.left + this.verticalSpacing;
			int rightStart = leftStart + leftFinal + this.verticalSpacing;

			for (Iterator it = TwoColumnPanel.this.rows.iterator(); it
					.hasNext();) {
				ComponentRow row = (ComponentRow) it.next();

				Component left = row.left;
				int hLeft = (row.left == null) ? 0
						: left.getPreferredSize().height;
				Component right = row.right;
				int hRight = (row.right == null) ? 0
						: right.getPreferredSize().height;

				int h = Math.max(hLeft, hRight);

				if (left != null) {
					left.setBounds(leftStart, y + (h - hLeft) / 2, leftFinal,
							hLeft);
				}
				if (right != null) {
					right.setBounds(rightStart, y + (h - hRight) / 2,
							rightFinal, hRight);
				}
				y += (this.horizontalSpacing + Math.max(hLeft, hRight));
			}
		}

		public Dimension minimumLayoutSize(Container parent) {
			return this.preferredLayoutSize(parent);
		}

		public Dimension preferredLayoutSize(Container parent) {
			int width = 3 * this.verticalSpacing + this.getPreferredLeftWidth()
					+ this.getPreferredRightWidth();
			int height = this.horizontalSpacing;
			for (Iterator it = TwoColumnPanel.this.rows.iterator(); it
					.hasNext();) {
				ComponentRow row = (ComponentRow) it.next();
				int hLeft = (row.left == null) ? 0 : row.left
						.getPreferredSize().height;
				int hRight = (row.right == null) ? 0 : row.right
						.getPreferredSize().height;
				height += (this.horizontalSpacing + Math.max(hLeft, hRight));
			}
			return new Dimension(width, height);
		}

		public void removeLayoutComponent(Component comp) {
		}

		private int getPreferredLeftWidth() {
			int maxPreferredWidth = 0;
			for (Iterator it = TwoColumnPanel.this.rows.iterator(); it
					.hasNext();) {
				ComponentRow row = (ComponentRow) it.next();
				if (row.left == null)
					continue;
				maxPreferredWidth = Math.max(maxPreferredWidth, row.left
						.getPreferredSize().width);
			}
			return Math.max(maxPreferredWidth, this.minimumLeftWidth);
		}

		private int getPreferredRightWidth() {
			int maxPreferredWidth = 0;
			for (Iterator it = TwoColumnPanel.this.rows.iterator(); it
					.hasNext();) {
				ComponentRow row = (ComponentRow) it.next();
				if (row.right == null)
					continue;
				maxPreferredWidth = Math.max(maxPreferredWidth, row.right
						.getPreferredSize().width);
			}
			return Math.max(maxPreferredWidth, this.minimumRightWidth);
		}

	}

	public void setHorizontalSpacing(int horizontalSpacing) {
		this.layout.setHorizontalSpacing(horizontalSpacing);
	}

	public void setMinimumLeftWidth(int minimumLeftWidth) {
		this.layout.setMinimumLeftWidth(minimumLeftWidth);
	}

	public void setMinimumRightWidth(int minimumRightWidth) {
		this.layout.setMinimumRightWidth(minimumRightWidth);
	}

	public void setVerticalSpacing(int verticalSpacing) {
		this.layout.setVerticalSpacing(verticalSpacing);
	}

	public static void main(String[] args) {
		TwoColumnPanel panel = new TwoColumnPanel();
		JPasswordField jpf1 = new JPasswordField("password", 10);
		panel.addRow(new JLabel("Enabled"), jpf1);
		JPasswordField jpf2 = new JPasswordField("password", 10);
		jpf2.setEditable(false);
		panel.addRow(new JLabel("Non-editable"), jpf2);
		JPasswordField jpf3 = new JPasswordField("password", 10);
		jpf3.setEnabled(false);
		panel.addRow(new JLabel("Disabled"), jpf3);
		JPasswordField jpf4 = new JPasswordField("password", 10);
		jpf4.setEchoChar((char) 0);
		panel.addRow(new JLabel("Echo char 0"), jpf4);

		JPasswordField jpf5 = new JPasswordField("password", 10);
		panel.addRow(new JLabel("With strength check"), jpf5);
		panel.setBorder(new TitledBorder("Password field"));

		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 300);
		frame.setLocation(300, 300);
		frame.setVisible(true);
	}
}

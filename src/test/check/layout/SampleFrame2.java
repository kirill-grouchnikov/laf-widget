/*
 * Copyright (c) 2005-2008 Laf-Widget Kirill Grouchnikov. All Rights Reserved.
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
package test.check.layout;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class SampleFrame2 extends JFrame {
	JButton b1, b2, b3;

	JButton b4, b5, b6;

	public SampleFrame2() {
		super("Transition test");

		this.getContentPane().setLayout(new BorderLayout());

		JPanel buttons = new JPanel(new FlowLayout());
		this.getContentPane().add(buttons, BorderLayout.SOUTH);

		// for the first movie, change the following line to
		// use the BorderLayout
		final JPanel mainPanel = new JPanel(new FlowLayout());
		final JPanel mainPanel2 = new JPanel(new FlowLayout());

		final JPanel centerPanel = new JPanel(new GridLayout(2, 1));
		centerPanel.add(mainPanel);
		centerPanel.add(mainPanel2);

		this.b1 = new JButton("1");
		this.b2 = new JButton("2");
		this.b3 = new JButton("3");

		this.b4 = new JButton("4");
		this.b5 = new JButton("5");
		this.b6 = new JButton("6");

		final JButton add = new JButton("add");
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						mainPanel.add(b1, BorderLayout.WEST);
						mainPanel.add(b2, BorderLayout.CENTER);
						mainPanel.add(b3, BorderLayout.EAST);
						mainPanel.revalidate();
						mainPanel2.add(b4, BorderLayout.WEST);
						mainPanel2.add(b5, BorderLayout.CENTER);
						mainPanel2.add(b6, BorderLayout.EAST);
						mainPanel2.revalidate();
						add.setEnabled(false);
					}
				});
			}
		});
		buttons.add(add);

		final JCheckBox cb = new JCheckBox("border layout");
		cb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (cb.isSelected()) {
					mainPanel.setLayout(new BorderLayout());
					mainPanel2.setLayout(new BorderLayout());
				} else {
					mainPanel.setLayout(new FlowLayout());
					mainPanel2.setLayout(new FlowLayout());
				}
				mainPanel.revalidate();
				mainPanel.doLayout();
				mainPanel.repaint();
				mainPanel2.revalidate();
			}
		});
		// buttons.add(cb);

		this.getContentPane().add(centerPanel, BorderLayout.CENTER);

		final JCheckBox cb1 = new JCheckBox("1");
		cb1.setSelected(true);
		final JCheckBox cb2 = new JCheckBox("2");
		cb2.setSelected(true);
		final JCheckBox cb3 = new JCheckBox("3");
		cb3.setSelected(true);
		final JCheckBox cb4 = new JCheckBox("4");
		cb4.setSelected(true);
		final JCheckBox cb5 = new JCheckBox("5");
		cb5.setSelected(true);
		final JCheckBox cb6 = new JCheckBox("6");
		cb6.setSelected(true);
		buttons.add(cb1);
		buttons.add(cb2);
		buttons.add(cb3);
		buttons.add(cb4);
		buttons.add(cb5);
		buttons.add(cb6);

		JButton showHide = new JButton("Toggle");
		showHide.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				b1.setVisible(cb1.isSelected());
				b2.setVisible(cb2.isSelected());
				b3.setVisible(cb3.isSelected());
				b4.setVisible(cb4.isSelected());
				b5.setVisible(cb5.isSelected());
				b6.setVisible(cb6.isSelected());
				mainPanel.doLayout();
				mainPanel2.doLayout();
			}
		});
		buttons.add(showHide);

		this.setSize(400, 200);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new SampleFrame2().setVisible(true);
			}
		});
	}
}

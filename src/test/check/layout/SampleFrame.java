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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class SampleFrame extends JFrame {
	JButton b1, b2, b3;

	public SampleFrame() {
		super("Transition test");

		this.getContentPane().setLayout(new BorderLayout());

		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout());
		this.getContentPane().add(buttons, BorderLayout.SOUTH);

		// for the first movie, change the following line to
		// use the BorderLayout
		final JPanel mainPanel = new JPanel(new BorderLayout());
		this.b1 = new JButton("1");
		this.b2 = new JButton("2");
		this.b3 = new JButton("3");

		final JButton add = new JButton("add");
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						mainPanel.add(b1, BorderLayout.WEST);
						mainPanel.add(b2, BorderLayout.CENTER);
						mainPanel.add(b3, BorderLayout.EAST);
						mainPanel.revalidate();
						add.setEnabled(false);
					}
				});
			}
		});
		buttons.add(add);

		this.getContentPane().add(mainPanel, BorderLayout.CENTER);

		final JCheckBox cb1 = new JCheckBox("1");
		cb1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				b1.setVisible(!b1.isVisible());
				mainPanel.revalidate();
			}
		});
		buttons.add(cb1);

		final JCheckBox cb2 = new JCheckBox("2");
		cb2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				b2.setVisible(!b2.isVisible());
				mainPanel.revalidate();
			}
		});
		buttons.add(cb2);

		final JCheckBox cb3 = new JCheckBox("3");
		cb3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				b3.setVisible(!b3.isVisible());
				mainPanel.revalidate();
			}
		});
		buttons.add(cb3);

		final JCheckBox cb13 = new JCheckBox("13");
		cb13.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				b1.setVisible(!b1.isVisible());
				b3.setVisible(!b3.isVisible());
				mainPanel.revalidate();
			}
		});
		buttons.add(cb13);

		this.setSize(300, 100);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new SampleFrame().setVisible(true);
			}
		});
	}
}

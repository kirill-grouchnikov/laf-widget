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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import org.pushingpixels.lafwidget.LafWidget;
import org.pushingpixels.lafwidget.tabbed.DefaultTabPreviewPainter;
import org.pushingpixels.lafwidget.tabbed.TabOverviewDialog;
import org.pushingpixels.lafwidget.text.PasswordStrengthChecker;
import org.pushingpixels.lafwidget.utils.LafConstants.PasswordStrength;
import org.pushingpixels.lafwidget.utils.LafConstants.TabOverviewKind;

public class SampleFrame extends JFrame {
	protected JButton prev;

	private static class MyListModel extends AbstractListModel {
		protected List model;

		public MyListModel() {
			super();
			this.model = new ArrayList();
			this.model.add("Ohio State [Buckeyes]");
			this.model.add("Auburn [Tigers]");
			this.model.add("University of South California [Trojans]");
			this.model.add("West Virginia [Mountaineers]");
			this.model.add("Florida [Gators]");
			this.model.add("Michigan [Wolverines]");
			this.model.add("Texas [Longhorns]");
			this.model.add("Louisville [Cardinals]");
			this.model.add("Louisiana State University [Tigers]");
			this.model.add("Georgia [Bulldogs]");
			this.model.add("Virginia Tech [Hokies]");
			this.model.add("Notre Dame [Fighting Irish]");
			this.model.add("Iowa [Hawkeyes]");
			this.model.add("Oregon [Ducks]");
			this.model.add("Tennessee [Volunteers]");
			this.model.add("Oklahoma [Sooners]");
			this.model.add("Texas Christian University [Horned Frogs]");
		}

		public Object getElementAt(int index) {
			return this.model.get(index);
		}

		public int getSize() {
			return this.model.size();
		}
	}

	public SampleFrame() {
		super("Test application");
		this.setLayout(new BorderLayout());
		final JTabbedPane tabbed = new JTabbedPane();

		this.add(tabbed, BorderLayout.CENTER);
		tabbed.putClientProperty(LafWidget.TABBED_PANE_PREVIEW_PAINTER,
				new DefaultTabPreviewPainter() {
					@Override
					public TabOverviewKind getOverviewKind(JTabbedPane tabPane) {
						return TabOverviewKind.ROUND_CAROUSEL;
					}
				});

		JPanel transPanel = new JPanel();
		transPanel.setLayout(new BorderLayout());

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 1));
		transPanel.add(buttons, BorderLayout.SOUTH);

		// for the first movie, change the following line to
		// use the BorderLayout
		final JPanel mainPanel = new JPanel(new FlowLayout());
		final JPanel mainPanel2 = new JPanel(new FlowLayout());

		final JPanel centerPanel = new JPanel(new GridLayout(2, 1));
		centerPanel.add(mainPanel);
		centerPanel.add(mainPanel2);

		final JButton b1 = new JButton("button 1");
		final JButton b2 = new JButton("button 2");
		final JButton b3 = new JButton("button 3");

		final JButton b4 = new JButton("button 4");
		final JButton b5 = new JButton("button 5");
		final JButton b6 = new JButton("button 6");

		final JButton add1 = new JButton("add");
		final JButton add2 = new JButton("add");
		add1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						mainPanel.add(b1);
						mainPanel.add(b2);
						mainPanel.add(b3);
						mainPanel.revalidate();
						add1.setVisible(false);
					}
				});
			}
		});
		add2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						mainPanel2.add(b4);
						mainPanel2.add(b5);
						mainPanel2.add(b6);
						mainPanel2.revalidate();
						add2.setVisible(false);
					}
				});
			}
		});
		mainPanel.add(add1);
		mainPanel2.add(add2);

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

		transPanel.add(centerPanel, BorderLayout.CENTER);

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

		tabbed.addTab("Regular", transPanel);

		JPanel samplePanel = new JPanel(new BorderLayout());
		TwoColumnPanel stuff = new TwoColumnPanel();
		stuff.setVerticalSpacing(4);
		stuff.setHorizontalSpacing(2);

		JCheckBox cbes = new JCheckBox("Check box");
		cbes.setSelected(true);
		JPasswordField pf1 = new JPasswordField("aa");
		JRadioButton rb1 = new JRadioButton("Radio button");
		rb1.setSelected(true);
		JPasswordField pf2 = new JPasswordField("aaaaaaaaa");

		PasswordStrengthChecker psc = new PasswordStrengthChecker() {
			public PasswordStrength getStrength(char[] password) {
				if (password == null)
					return PasswordStrength.WEAK;
				int length = password.length;
				if (length < 3)
					return PasswordStrength.WEAK;
				if (length < 6)
					return PasswordStrength.MEDIUM;
				return PasswordStrength.STRONG;
			}

			public String getDescription(PasswordStrength strength) {
				if (strength == PasswordStrength.WEAK)
					return "<html>This password is <b>way</b> too weak</html>";
				if (strength == PasswordStrength.MEDIUM)
					return "<html>Come on, you can do<br> a little better than that</html>";
				if (strength == PasswordStrength.STRONG)
					return "OK";
				return null;
			}
		};
		pf1.putClientProperty(LafWidget.PASSWORD_STRENGTH_CHECKER, psc);
		pf2.putClientProperty(LafWidget.PASSWORD_STRENGTH_CHECKER, psc);

		stuff.addRow(cbes, rb1);
		stuff.addRow(pf1, pf2);
		JComboBox combo = new JComboBox(new Object[] { "item1" });
		combo.setSelectedIndex(0);
		combo.setEditable(true);
		JTextField text = new JTextField("Text field");
		text.setEditable(false);
		stuff.addRow(combo, text);
		stuff.setPreferredSize(new Dimension(stuff.getPreferredSize().width,
				stuff.getPreferredSize().height + 100));
		stuff.setBorder(null);

		stuff.setOpaque(false);
		JScrollPane scroll = new JScrollPane(stuff,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		scroll.setOpaque(false);
		scroll.getViewport().setOpaque(false);
		samplePanel.add(scroll, BorderLayout.CENTER);

		JPanel buttons2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		prev = new JButton("prev");
		JButton cancel = new JButton("cancel");
		cancel.setEnabled(false);
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TabOverviewDialog.getOverviewDialog(tabbed).setVisible(true);
			}
		});
		buttons2.add(prev);
		buttons2.add(cancel);
		buttons2.add(ok);

		samplePanel.add(buttons2, BorderLayout.SOUTH);

		tabbed.addTab("Sample", samplePanel);

		JPanel samplePanel2 = new JPanel(new BorderLayout());
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
		DefaultMutableTreeNode son1 = new DefaultMutableTreeNode("son1");
		DefaultMutableTreeNode son2 = new DefaultMutableTreeNode("son2");
		DefaultMutableTreeNode son3 = new DefaultMutableTreeNode("son3");
		DefaultMutableTreeNode gson11 = new DefaultMutableTreeNode("gson11");
		DefaultMutableTreeNode gson12 = new DefaultMutableTreeNode("gson12");
		DefaultMutableTreeNode gson21 = new DefaultMutableTreeNode("gson21");
		DefaultMutableTreeNode gson22 = new DefaultMutableTreeNode("gson22");
		DefaultMutableTreeNode gson31 = new DefaultMutableTreeNode("gson31");
		DefaultMutableTreeNode gson32 = new DefaultMutableTreeNode("gson32");
		DefaultMutableTreeNode ggson111 = new DefaultMutableTreeNode("ggson111");
		DefaultMutableTreeNode ggson112 = new DefaultMutableTreeNode("ggson112");
		DefaultMutableTreeNode ggson113 = new DefaultMutableTreeNode("ggson113");

		gson11.add(ggson111);
		gson11.add(ggson112);
		gson11.add(ggson113);
		son1.add(gson11);
		son1.add(gson12);
		son2.add(gson21);
		son2.add(gson22);
		son3.add(gson31);
		son3.add(gson32);
		root.add(son1);
		root.add(son2);
		root.add(son3);

		JTree tree = new JTree(root);
		tree.setBorder(new EmptyBorder(0, 0, 0, 0));
		JScrollPane jspTree = new JScrollPane(tree);
		// TransitionLayoutManager.getInstance().track(jspTree, true);
		// jspTree.setBorder(new EmptyBorder(0, 0, 0, 0));

		JList list = new JList(new MyListModel());
		list.setBorder(new EmptyBorder(0, 0, 0, 0));
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane jspList = new JScrollPane(list);
		// jspList.setBorder(new EmptyBorder(0, 0, 0, 0));

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jspTree,
				jspList);
		split.setDividerLocation(130);
		samplePanel2.add(split, BorderLayout.CENTER);
		tabbed.add("Renderers", samplePanel2);

		tabbed.setSelectedComponent(samplePanel);
		tabbed.setOpaque(false);
		tabbed.setBorder(new EmptyBorder(0, 3, 3, 3));

		this.setResizable(true);
		this.getRootPane().setDefaultButton(ok);

		JMenuBar jmb = new JMenuBar();
		JMenu menu = new JMenu("menu");
		menu.add(new JMenuItem("test item 1"));
		menu.add(new JCheckBoxMenuItem("test item 2"));
		menu.add(new JRadioButtonMenuItem("test item 3"));
		menu.addSeparator();
		menu.add(new JMenuItem("test menu item 4"));
		menu.add(new JCheckBoxMenuItem("test menu item 5"));
		menu.add(new JRadioButtonMenuItem("test menu item 6"));
		jmb.add(menu);

		JMenu menu2 = new JMenu("big");
		for (int i = 0; i < 35; i++)
			menu2.add(new JMenuItem("menu item " + i));
		jmb.add(menu2);

		this.setJMenuBar(jmb);
	}

	public static void main(String[] args) throws Exception {
		JFrame.setDefaultLookAndFeelDecorated(true);
		SampleFrame sf = new SampleFrame();
		sf.setSize(300, 200);
		sf.setLocationRelativeTo(null);
		sf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		sf.setVisible(true);
		// h(sf, 0);
	}
}

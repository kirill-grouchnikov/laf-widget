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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class LafChanger implements ActionListener {
	private JFrame frame;

	private String lafClassName;

	public static JMenuItem getMenuItem(JFrame frame, String lafName,
			String lafClassName) {
		JMenuItem result = new JMenuItem(lafName);
		result.addActionListener(new LafChanger(frame, lafClassName));
		return result;
	}

	public LafChanger(JFrame frame, String lafClassName) {
		super();
		this.frame = frame;
		this.lafClassName = lafClassName;
	}

	public void actionPerformed(ActionEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				boolean was_wm_decorated = !frame.isUndecorated();

				try {
					UIManager.setLookAndFeel(lafClassName);
					SwingUtilities.updateComponentTreeUI(frame);
				} catch (ClassNotFoundException cnfe) {
					out("LAF main class '" + lafClassName + "' not found");
				} catch (Exception exc) {
					exc.printStackTrace();
				}

				if (System.getProperty("substancelaf.useDecorations") != null) {
					boolean is_wm_decorated = !UIManager.getLookAndFeel()
							.getSupportsWindowDecorations();
					if (is_wm_decorated != was_wm_decorated) {
						out("Changing decoration policy\n");
						frame.setVisible(false);
						frame.dispose();
						frame.setUndecorated(!is_wm_decorated);
						frame.pack();
						frame.setVisible(true);
						was_wm_decorated = !frame.isUndecorated();
					}
				}
			}
		});
	}

	public static void out(Object obj) {
		try {
			System.out.println(obj);
		} catch (Exception exc) {
			// ignore - is thrown on Mac in WebStart (security access)
		}
	}
}
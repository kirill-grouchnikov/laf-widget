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
package org.pushingpixels.lafwidget.ant;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Utility functions.
 * 
 * @author Kirill Grouchnikov
 */
public class Utils {
	/**
	 * Maps the LAF names.
	 */
	protected Map<String, String> lafMap;

	/**
	 * Singleton.
	 */
	protected static Utils instance = new Utils();

	/**
	 * IDs of all UI delegates.
	 */
	public static final String[] UI_IDS = new String[] { "ButtonUI",
			"CheckBoxUI", "CheckBoxMenuItemUI", "ColorChooserUI", "ComboBoxUI",
			"DesktopIconUI", "DesktopPaneUI", "EditorPaneUI",
			"FormattedTextFieldUI", "InternalFrameUI", "LabelUI", "ListUI",
			"MenuUI", "MenuBarUI", "MenuItemUI", "OptionPaneUI", "PanelUI",
			"PasswordFieldUI", "PopupMenuUI", "PopupMenuSeparatorUI",
			"ProgressBarUI", "RadioButtonUI", "RadioButtonMenuItemUI",
			"RootPaneUI", "ScrollBarUI", "ScrollPaneUI", "SplitPaneUI",
			"SliderUI", "SeparatorUI", "SpinnerUI", "ToolBarSeparatorUI",
			"TabbedPaneUI", "TableUI", "TableHeaderUI", "TextAreaUI",
			"TextFieldUI", "TextPaneUI", "ToggleButtonUI", "ToolBarUI",
			"ToolTipUI", "TreeUI", "ViewportUI" };

	/**
	 * Constructor.
	 */
	private Utils() {
		this.lafMap = new HashMap<String, String>();
		this.lafMap.put(BasicLookAndFeel.class.getName(),
				"javax.swing.plaf.basic.Basic");
		this.lafMap.put(MetalLookAndFeel.class.getName(),
				"javax.swing.plaf.metal.Metal");
	}

	/**
	 * Returns instance.
	 * 
	 * @return Instance.
	 */
	public static Utils getUtils() {
		return Utils.instance;
	}

	/**
	 * Returns fully-qualified class name for the UI delegate based on the
	 * specified parameters.
	 * 
	 * @param uiKey
	 *            UI key.
	 * @param lafClassName
	 *            Class name of the LAF.
	 * @return Fully-qualified class name for the UI delegate. The LAF hierarchy
	 *         is searched starting from the specified class name and up. For
	 *         example, if the second parameter points to
	 *         {@link MetalLookAndFeel}, the metal delegate classname is
	 *         returned if exists; otherwise the basic delegate classname is
	 *         returned.
	 */
	public String getUIDelegate(String uiKey, String lafClassName) {
		try {
			lafClassName = lafClassName.replace('/', '.');
			return this.getUIDelegate(uiKey, Class.forName(lafClassName));
		} catch (ClassNotFoundException cnfe) {
			throw new AugmentException(
					"Class '" + lafClassName + "' not found", cnfe);
		}
	}

	/**
	 * Returns fully-qualified class name for the UI delegate based on the
	 * specified parameters.
	 * 
	 * @param uiKey
	 *            UI key.
	 * @param origLafClazz
	 *            LAF class.
	 * @return Fully-qualified class name for the UI delegate. The LAF hierarchy
	 *         is searched starting from the specified class and up. For
	 *         example, if the second parameter points to
	 *         {@link MetalLookAndFeel}, the metal delegate classname is
	 *         returned if exists; otherwise the basic delegate classname is
	 *         returned.
	 */
	public String getUIDelegate(String uiKey, Class<?> origLafClazz) {
		Class<?> lafClazz = origLafClazz;
		while (lafClazz != null) {
			String prefix = (String) this.lafMap.get(lafClazz.getName());
			if (prefix != null) {
				String fullClassName = prefix + uiKey;
				Class<?> delegateClazz = null;
				try {
					delegateClazz = Class.forName(fullClassName);
				} catch (ClassNotFoundException cnfe) {
				}
				if (delegateClazz != null)
					return fullClassName;
			}
			lafClazz = lafClazz.getSuperclass();
		}
		throw new AugmentException("No match for '" + uiKey + "' in '"
				+ origLafClazz.getName() + "' hierarchy");
	}

	/**
	 * Returns JNI-compliant description of the specified class (type). For
	 * example, for <code>JButton[]</code> this function will return
	 * <code>[Ljavax/swing/JButton;</code>.
	 * 
	 * @param clazz
	 *            Class.
	 * @return JNI-compliant class (type) description.
	 */
	public static String getTypeDesc(Class<?> clazz) {
		if (clazz.isArray())
			return "[" + Utils.getTypeDesc(clazz.getComponentType());
		if (clazz == void.class)
			return "V";
		if (clazz == boolean.class)
			return "Z";
		if (clazz == byte.class)
			return "B";
		if (clazz == char.class)
			return "C";
		if (clazz == short.class)
			return "S";
		if (clazz == int.class)
			return "I";
		if (clazz == long.class)
			return "J";
		if (clazz == float.class)
			return "F";
		if (clazz == double.class)
			return "D";
		return "L" + clazz.getName().replace('.', '/') + ";";
	}

	/**
	 * Returns JNI-compliant description of the specified method. For example,
	 * for <code>void installUI(JButton button)</code> this function will
	 * return <code>(Ljavax/swing/JButton;)V</code>.
	 * 
	 * @param method
	 *            Method.
	 * @return JNI-compliant method description.
	 */
	public static String getMethodDesc(Method method) {
		StringBuffer result = new StringBuffer();
		result.append("(");
		Class<?>[] paramClasses = method.getParameterTypes();
		for (int i = 0; i < paramClasses.length; i++) {
			Class<?> paramClass = paramClasses[i];
			result.append(Utils.getTypeDesc(paramClass));
		}
		result.append(")");
		result.append(Utils.getTypeDesc(method.getReturnType()));
		return result.toString();
	}

	/**
	 * Test app.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		for (Map.Entry<String, String> entry : Utils.instance.lafMap.entrySet()) {
			//Map.Entry entry = (Map.Entry) it.next();
			String lafClassName = entry.getKey();
			System.out.println(lafClassName);
			String prefix = entry.getValue();
			for (int i = 0; i < Utils.UI_IDS.length; i++) {
				String uiClassName = prefix + Utils.UI_IDS[i];
				try {
					Class<?> uiClazz = Class.forName(uiClassName);
					System.out.println("\t" + Utils.UI_IDS[i]);
					Constructor<?>[] ctrs = uiClazz.getDeclaredConstructors();
					for (int j = 0; j < ctrs.length; j++) {
						Constructor<?> ctr = ctrs[j];
						Class<?>[] ctrArgs = ctr.getParameterTypes();
						System.out.print("\t\t" + ctrArgs.length + " args : ");
						for (int k = 0; k < ctrArgs.length; k++)
							System.out.print(ctrArgs[k].getName() + ",");
						System.out.println();
					}
				} catch (ClassNotFoundException cnfe) {
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}

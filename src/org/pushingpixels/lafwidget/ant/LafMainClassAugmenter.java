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

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

import javax.swing.UIDefaults;

import org.objectweb.asm.*;

/**
 * Augments the main LAF classes with laf-widget behaviour. Is based on sample
 * adapter from ASM distribution.
 * 
 * @author Kirill Grouchnikov
 */
public class LafMainClassAugmenter {
	/**
	 * Method name to augment.
	 */
	protected static String METHOD_NAME = "initClassDefaults";

	/**
	 * Verbosity indication.
	 */
	private boolean isVerbose;

	/**
	 * Names of delegates to add.
	 */
	private String[] delegatesToAdd;

	/**
	 * Class adapter that augments the UI functionality.
	 * 
	 * @author Kirill Grouchnikov
	 */
	protected class AugmentClassAdapter extends ClassAdapter implements Opcodes {
		/**
		 * Contains all method names.
		 */
		private Set<String> existingMethods;

		/**
		 * Prefix for delegate methods that will be added.
		 */
		private String prefix;

		/**
		 * The original method.
		 */
		// private Method originalMethod;
		/**
		 * Class name of the super class.
		 */
		private String superClassName;

		/**
		 * Creates a new augmentor.
		 * 
		 * @param cv
		 *            Class visitor to recreate the non-augmented methods.
		 * @param existingMethods
		 *            Existing methods.
		 * @param originalMethod
		 *            The original method.
		 */
		public AugmentClassAdapter(ClassVisitor cv,
				Set<String> existingMethods, Method originalMethod) {
			super(cv);
			this.existingMethods = existingMethods;
			// this.originalMethod = originalMethod;
		}

		/**
		 * Returns the superclass name.
		 * 
		 * @return The superclass name.
		 */
		public String getSuperClassName() {
			return this.superClassName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.objectweb.asm.ClassAdapter#visit(int, int, java.lang.String,
		 *      java.lang.String, java.lang.String, java.lang.String[])
		 */
		@Override
		public void visit(final int version, final int access,
				final String name, final String signature,
				final String superName, final String[] interfaces) {
			this.superClassName = superName;
			this.prefix = "__" + name.replaceAll("/", "__") + "__";
			super
					.visit(version, access, name, signature, superName,
							interfaces);

			// We have two separate cases for the "initClassDefaults" function
			// that we want to augment:
			//
			// 1. The current .class has both function and the __ version -
			// already has been augmented. Can be ignored.
			//
			// 2. The current .class has function but doesn't have the __
			// version. Than, the original function has already been renamed to
			// __ (in the visitMethod). We need to create a new version for this
			// function that performs pre-logic, calls __ and performs the
			// post-logic.
			boolean hasOriginal = this.existingMethods
					.contains(LafMainClassAugmenter.METHOD_NAME);
			boolean hasDelegate = this.existingMethods.contains(this.prefix
					+ LafMainClassAugmenter.METHOD_NAME);

			// String methodSignature =
			// Utils.getMethodDesc(this.originalMethod);
			// int paramCount = this.originalMethod.getParameterTypes().length;
			if (LafMainClassAugmenter.this.isVerbose)
				System.out.println("..." + LafMainClassAugmenter.METHOD_NAME
						+ " " + "(Ljavax/swing/UIDefaults;)V"
						+ " : delegate - " + hasDelegate + ", 1 params");

			if (!hasDelegate) {
				this.augmentInitClassDefaultsMethod(!hasOriginal, name,
						superName);
			}
		}

		/**
		 * Augments the <code>initClassDefaults</code> method.
		 * 
		 * @param toSynthOriginal
		 *            if <code>true</code>, a forwarding method will be
		 *            generated.
		 * @param className
		 *            Class name.
		 * @param superClassName
		 *            Super class name (relevant for generating empty
		 *            implementation).
		 */
		public void augmentInitClassDefaultsMethod(boolean toSynthOriginal,
				String className, String superClassName) {

			if (toSynthOriginal) {
				if (LafMainClassAugmenter.this.isVerbose) {
					System.out
							.println("... Creating empty 'initClassDefaults' forwarding to super '"
									+ superClassName + "'");
				}
				MethodVisitor mv = this.cv.visitMethod(ACC_PROTECTED,
						this.prefix + "initClassDefaults",
						"(Ljavax/swing/UIDefaults;)V", null, null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitMethodInsn(INVOKESPECIAL, superClassName,
						"initClassDefaults", "(Ljavax/swing/UIDefaults;)V");
				mv.visitInsn(RETURN);
				mv.visitMaxs(2, 2);
				mv.visitEnd();
			}

			MethodVisitor mv = this.cv.visitMethod(Opcodes.ACC_PROTECTED,
					"initClassDefaults", "(Ljavax/swing/UIDefaults;)V", null,
					null);
			mv.visitCode();
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitVarInsn(Opcodes.ALOAD, 1);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className, this.prefix
					+ "initClassDefaults", "(Ljavax/swing/UIDefaults;)V");

			String packageName = className.replace('/', '.');
			int lastDotIndex = packageName.lastIndexOf('.');
			if (lastDotIndex >= 0) {
				packageName = packageName.substring(0, lastDotIndex);
			} else {
				packageName = "";
			}

			for (int i = 0; i < LafMainClassAugmenter.this.delegatesToAdd.length; i++) {
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				String delegateKey = LafMainClassAugmenter.this.delegatesToAdd[i];
				String delegateValue = packageName + ".__Forwarding__"
						+ delegateKey;

				if (LafMainClassAugmenter.this.isVerbose) {
					System.out.println("...Putting '" + delegateKey + "' -> '"
							+ delegateValue + "'");
				}

				mv.visitLdcInsn(delegateKey);
				mv.visitLdcInsn(delegateValue);
				mv
						.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
								"javax/swing/UIDefaults", "put",
								"(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
				mv.visitInsn(Opcodes.POP);
			}

			mv.visitInsn(Opcodes.RETURN);
			mv.visitMaxs(3, 2);
			mv.visitEnd();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.objectweb.asm.ClassAdapter#visitMethod(int,
		 *      java.lang.String, java.lang.String, java.lang.String,
		 *      java.lang.String[])
		 */
		@Override
		public MethodVisitor visitMethod(final int access, final String name,
				final String desc, final String signature,
				final String[] exceptions) {
			// System.out.println("Visiting " + name + ":" + desc + ":"
			// + signature);
			if (LafMainClassAugmenter.METHOD_NAME.equals(name)) {
				// possible candidate for weaving. Check if has __ already
				if (!this.existingMethods.contains(this.prefix + name)) {
					// effectively renames the existing method prepending __
					// to the name
					return this.cv.visitMethod(access, this.prefix + name,
							desc, signature, exceptions);
				}
			}
			// preserve the existing method as is
			return this.cv.visitMethod(access, name, desc, signature,
					exceptions);
		}
	}

	/**
	 * Augments a single class with additional UI behaviour.
	 * 
	 * @param dir
	 *            Root directory for the library that contains the class.
	 * @param name
	 *            Fully-qualified class name.
	 * @throws AugmentException
	 *             If the augmentation process failed.
	 * @return The superclass name.
	 */
	protected synchronized String augmentClass(String dir, final String name) {
		if (this.isVerbose)
			System.out.println("Working on LAF main class " + name);

		// gets an input stream to read the bytecode of the class
		String resource = dir + File.separator + name.replace('.', '/')
				+ ".class";

		Method origMethod = null;
		try {
			ClassLoader cl = new URLClassLoader(new URL[] { new File(dir)
					.toURL() }, LafMainClassAugmenter.class.getClassLoader());
			Class<?> clazz = cl.loadClass(name);
			origMethod = clazz.getDeclaredMethod(
					LafMainClassAugmenter.METHOD_NAME,
					new Class[] { UIDefaults.class });
		} catch (NoSuchMethodException nsme) {
			origMethod = null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AugmentException(name, e);
		}

		Set<String> existingMethods = null;
		InputStream is = null;
		try {
			is = new FileInputStream(resource);
			ClassReader cr = new ClassReader(is);
			// ClassWriter cw = new ClassWriter(false);
			InfoClassVisitor infoAdapter = new InfoClassVisitor();
			cr.accept(infoAdapter, false);
			existingMethods = infoAdapter.getMethods();
		} catch (Exception e) {
			e.printStackTrace();
			throw new AugmentException(name, e);
		} finally {
			try {
				is.close();
			} catch (IOException ioe) {
			}
		}

		// Augment the class (overriding the existing file).
		byte[] b;
		String superClassName = null;
		try {
			is = new FileInputStream(resource);
			ClassReader cr = new ClassReader(is);
			ClassWriter cw = new ClassWriter(false);
			AugmentClassAdapter cv = new AugmentClassAdapter(cw,
					existingMethods, origMethod);
			cr.accept(cv, false);
			b = cw.toByteArray();
			superClassName = cv.getSuperClassName();
		} catch (Exception e) {
			e.printStackTrace();
			throw new AugmentException(name, e);
		} finally {
			try {
				is.close();
			} catch (IOException ioe) {
			}
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(resource);
			fos.write(b);
			if (this.isVerbose)
				System.out.println("Updated resource " + resource);
		} catch (Exception e) {
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ioe) {
				}
			}
		}

		return superClassName;
	}

	/**
	 * Creates a new augmenter.
	 */
	public LafMainClassAugmenter() {
		super();
	}

	/**
	 * Processes a single file or a directory, augmenting all main LAF classes.
	 * 
	 * @param toStrip
	 *            The leading prefix to strip from the file names. Is used to
	 *            create fully-qualified class name.
	 * @param file
	 *            File resource (can point to a single file or to a directory).
	 * @param mainClassName
	 *            The class name of the main LAF class.
	 * @throws AugmentException
	 *             If the augmentation process failed.
	 */
	public void process(String toStrip, File file, String mainClassName) {
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			for (int i = 0; i < children.length; i++) {
				this.process(toStrip, children[i], mainClassName);
			}
		} else {
			String className = file.getAbsolutePath().substring(
					toStrip.length() + 1);
			className = className.replace(File.separatorChar, '.');
			className = className.substring(0, className.length() - 6);
			if (mainClassName.equals(className)) {
				String superClassName = this.augmentClass(toStrip, className);
				// Create forwarding delegates
				for (int i = 0; i < this.delegatesToAdd.length; i++) {
					String uiKey = this.delegatesToAdd[i];
					String uiSuperClassName = Utils.getUtils().getUIDelegate(
							uiKey, superClassName);

					Class<?>[] uiSuperCtrParams = null;
					try {
						Class<?> uiSuperClazz = Class.forName(uiSuperClassName);
						Constructor<?>[] uiSuperCtrs = uiSuperClazz
								.getDeclaredConstructors();
						if (uiSuperCtrs.length != 1)
							throw new AugmentException(
									"Unsupported base UI class "
											+ uiSuperClassName
											+ " - not exactly one ctr");
						Constructor<?> uiSuperCtr = uiSuperCtrs[0];
						uiSuperCtrParams = uiSuperCtr.getParameterTypes();
						if (uiSuperCtrParams.length > 1)
							throw new AugmentException(
									"Unsupported base UI class "
											+ uiSuperClassName + " - "
											+ uiSuperCtrParams.length
											+ " parameters");
					} catch (ClassNotFoundException cnfe) {
						throw new AugmentException(
								"Failed locating base UI class", cnfe);
					}

					int lastDotIndex = className.lastIndexOf('.');
					String packageName = (lastDotIndex >= 0) ? className
							.substring(0, lastDotIndex) : "";

					String uiClassName = "__Forwarding__" + uiKey;

					String resource = toStrip
							+ File.separator
							+ (packageName + File.separator + uiClassName)
									.replace('.', File.separatorChar)
							+ ".class";

					System.out.println("...Creating forwarding delegate");
					System.out.println("...... at '" + resource + "'");
					System.out.println("...... with class name '" + uiClassName
							+ "'");
					System.out.println("...... package '" + packageName + "'");
					System.out.println("...... super impl '" + uiSuperClassName
							+ "'");

					byte[] b = null;
					if (uiSuperCtrParams.length == 0) {
						b = UiDelegateWriterEmptyCtr.createClass(packageName,
								uiClassName, uiSuperClassName);
					} else {
						b = UiDelegateWriterOneParamCtr.createClass(
								packageName, uiClassName, uiSuperClassName,
								Utils.getTypeDesc(uiSuperCtrParams[0]));
					}
					FileOutputStream fos = null;
					try {
						fos = new FileOutputStream(resource);
						fos.write(b);
						if (this.isVerbose)
							System.out.println("...Created resource "
									+ resource);
					} catch (Exception e) {
					} finally {
						if (fos != null) {
							try {
								fos.close();
							} catch (IOException ioe) {
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Sets the verbosity.
	 * 
	 * @param isVerbose
	 *            New value for augmentation process verbosity.
	 */
	public void setVerbose(boolean isVerbose) {
		this.isVerbose = isVerbose;
	}

	/**
	 * Sets the list of delegates that need to be added.
	 * 
	 * @param delegatesToAdd
	 *            The list of delegates that need to be added.
	 */
	public void setDelegatesToAdd(String[] delegatesToAdd) {
		this.delegatesToAdd = delegatesToAdd;
	}

	/**
	 * Test methods.
	 * 
	 * @param args
	 * @throws AugmentException
	 */
	public static void main(final String args[]) throws AugmentException {
		if (args.length == 0) {
			System.out
					.println("Usage : java ... LafMainClassAugmenter [-verbose]");
			System.out
					.println("\t -main main_class_name -dir class_directory ");
			System.out.println("\t -delegates delegate_ui_ids");
			return;
		}

		LafMainClassAugmenter augmenter = new LafMainClassAugmenter();
		int argNum = 0;
		String mainLafClassName = null;
		String startDir = null;
		while (argNum < args.length) {
			String currArg = args[argNum];
			if ("-verbose".equals(currArg)) {
				augmenter.setVerbose(true);
				argNum++;
				continue;
			}
			if ("-main".equals(currArg)) {
				argNum++;
				mainLafClassName = args[argNum];
				argNum++;
				continue;
			}
			if ("-dir".equals(currArg)) {
				argNum++;
				startDir = args[argNum];
				argNum++;
				continue;
			}
			if ("-delegates".equals(currArg)) {
				argNum++;
				augmenter.setDelegatesToAdd(args[argNum].split(";"));
				argNum++;
				continue;
			}
			break;
		}

		File starter = new File(startDir);
		augmenter.process(starter.getAbsolutePath(), starter, mainLafClassName);
	}
}

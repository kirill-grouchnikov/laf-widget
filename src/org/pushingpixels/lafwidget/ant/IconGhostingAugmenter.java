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

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import javax.swing.JComponent;

import org.objectweb.asm.*;

/**
 * Augments the button UI classes with icon ghosting painting. Is based on
 * sample adapter from ASM distribution.
 * 
 * @author Kirill Grouchnikov
 */
public class IconGhostingAugmenter {
	/**
	 * Verbosity indication.
	 */
	private boolean isVerbose;

	/**
	 * Adapter for augmenting a single class.
	 * 
	 * @author Kirill Grouchnikov.
	 */
	protected class AugmentClassAdapter extends ClassAdapter implements Opcodes {
		/**
		 * Contains all method names.
		 */
		private Set<String> existingMethods;

		/**
		 * Contains all field names.
		 */
		private Set<String> existingFields;

		/**
		 * Method to augment.
		 */
		private Method methodToAugment;

		// private Method methodInstallListeners;
		//
		// private Method methodUninstallListeners;

		/**
		 * Prefix for delegate methods that will be added.
		 */
		private String prefix;

		/**
		 * Creates a new augmentor.
		 * 
		 * @param cv
		 *            Class visitor to recreate the non-augmented methods.
		 * @param existingMethods
		 *            Contains all method names.
		 * @param existingFields
		 *            Contains all field names.
		 * @param methodToAugment
		 *            Method to augment.
		 */
		public AugmentClassAdapter(final ClassVisitor cv,
				Set<String> existingMethods, Set<String> existingFields,
				Method methodToAugment// ,
		// Method methodInstallListeners, Method methodUninstallListeners
		) {
			super(cv);
			this.existingMethods = existingMethods;
			this.existingFields = existingFields;
			this.methodToAugment = methodToAugment;
			// this.methodInstallListeners = methodInstallListeners;
			// this.methodUninstallListeners = methodUninstallListeners;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.objectweb.asm.ClassAdapter#visit(int, int, java.lang.String,
		 * java.lang.String, java.lang.String, java.lang.String[])
		 */
		@Override
		public void visit(final int version, final int access,
				final String name, final String signature,
				final String superName, final String[] interfaces) {
			this.prefix = "__" + name.replaceAll("/", "__") + "__icon__";
			super
					.visit(version, access, name, signature, superName,
							interfaces);

			// Check if need to add the iconGhostingMarker field (boolean)
			if (!this.existingFields.contains("iconGhostingMarker")) {
				FieldVisitor fv = this.visitField(Opcodes.ACC_PROTECTED,
						"iconGhostingMarker", "Z", null, null);
				fv.visitEnd();

				// this.visitInnerClass(name + "$1", null, null, 0);
				//
				// fv = this.visitField(ACC_PRIVATE, "ghostModelChangeListener",
				// "Lorg/pushingpixels/lafwidget/utils/GhostingListener;", null,
				// null);
				// fv.visitEnd();
				// fv = this.visitField(ACC_PROTECTED, "ghostPropertyListener",
				// "Ljava/beans/PropertyChangeListener;", null, null);
				// fv.visitEnd();
				//
				// MethodVisitor mv = this
				// .visitMethod(
				// ACC_STATIC + ACC_SYNTHETIC,
				// "access$0",
				// "(L"
				// + name
				// + ";)Lorg/pushingpixels/lafwidget/utils/GhostingListener;",
				// null, null);
				// mv.visitCode();
				// mv.visitVarInsn(ALOAD, 0);
				// mv.visitFieldInsn(GETFIELD, name, "ghostModelChangeListener",
				// "Lorg/pushingpixels/lafwidget/utils/GhostingListener;");
				// mv.visitInsn(ARETURN);
				// mv.visitMaxs(1, 1);
				// mv.visitEnd();
				//
				// mv = this
				// .visitMethod(
				// ACC_STATIC + ACC_SYNTHETIC,
				// "access$1",
				// "(L"
				// + name
				// + ";Lorg/pushingpixels/lafwidget/utils/GhostingListener;)V",
				// null, null);
				// mv.visitCode();
				// mv.visitVarInsn(ALOAD, 0);
				// mv.visitVarInsn(ALOAD, 1);
				// mv.visitFieldInsn(PUTFIELD, name, "ghostModelChangeListener",
				// "Lorg/pushingpixels/lafwidget/utils/GhostingListener;");
				// mv.visitInsn(RETURN);
				// mv.visitMaxs(2, 2);
				// mv.visitEnd();
				//
			}

			// We have three separate cases for the method that we
			// want to augment:
			//
			// 1. The current .class has both function and the __ version -
			// already has been augmented. Can be ignored.
			//
			// 2. The current .class has function but doesn't have the __
			// version. Than, the original function has already been renamed to
			// __ (in the visitMethod). We need to create a new version for this
			// function that performs pre-logic, calls __ and performs the
			// post-logic.
			//
			// 3. The current .class doesn't have neither the function nor
			// the __ version. In this case we need to create the __ version
			// that calls super (with the original name) and the function that
			// performs pre-logic, calls __ and performs the post-logic.

			Method[] toAugment = new Method[] { this.methodToAugment };
			// this.methodInstallListeners, this.methodUninstallListeners };
			for (Method currMethodToAugment : toAugment) {
				String methodName = currMethodToAugment.getName();
				boolean hasOriginal = this.existingMethods.contains(methodName);
				boolean hasDelegate = this.existingMethods.contains(this.prefix
						+ methodName);

				String methodSignature = Utils
						.getMethodDesc(currMethodToAugment);
				int paramCount = currMethodToAugment.getParameterTypes().length;
				if (IconGhostingAugmenter.this.isVerbose)
					System.out.println("... Augmenting " + methodName + " "
							+ methodSignature + " : original - " + hasOriginal
							+ ", delegate - " + hasDelegate + ", " + paramCount
							+ " params");

				if (!hasDelegate) {
					// if (methodName.equals("installListeners")) {
					// this.augmentInstallListeners(!hasOriginal, name,
					// superName, methodName, methodSignature);
					// } else {
					// if (methodName.equals("uninstallListeners")) {
					// this.augmentUninstallListeners(!hasOriginal, name,
					// superName, methodName, methodSignature);
					// } else {
					this.augmentPaintIconMethod(!hasOriginal, name, superName,
							methodName, methodSignature);
					// }
					// }
				}
			}
		}

		/**
		 * Augments the <code>paintIcon</code> method that is assumed to always
		 * have three parameters.
		 * 
		 * @param toSynthOriginal
		 *            Indication whether we need to create an empty (only call
		 *            to super()) implementation.
		 * @param className
		 *            Class name.
		 * @param superClassName
		 *            Super class name (relevant for generating empty
		 *            implementation).
		 * @param methodName
		 *            Method name.
		 * @param methodDesc
		 *            Function signature (using JNI style declaration). Example
		 *            for <code>void installUI(JButton button)</code>:
		 *            <code>(Ljavax/swing/JButton;)V</code>.
		 */
		public void augmentPaintIconMethod(boolean toSynthOriginal,
				String className, String superClassName, String methodName,
				String methodDesc) {
			// Some ASM woodoo. The code below was generated by using
			// ASMifierClassVisitor.
			if (toSynthOriginal) {
				MethodVisitor mv = this.cv.visitMethod(Opcodes.ACC_PUBLIC,
						this.prefix + methodName, methodDesc, null, null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitVarInsn(ALOAD, 2);
				mv.visitVarInsn(ALOAD, 3);
				mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superClassName,
						methodName, methodDesc);
				mv.visitInsn(RETURN);
				mv.visitMaxs(4, 4);
				mv.visitEnd();
			}

			MethodVisitor mv = this.cv.visitMethod(ACC_PROTECTED, methodName,
					methodDesc, null, null);

			mv.visitCode();
			mv.visitVarInsn(ALOAD, 2);
			mv.visitLdcInsn("icon.bounds");
			mv.visitTypeInsn(NEW, "java/awt/Rectangle");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitMethodInsn(INVOKESPECIAL, "java/awt/Rectangle", "<init>",
					"(Ljava/awt/Rectangle;)V");
			mv.visitMethodInsn(INVOKEVIRTUAL, "javax/swing/JComponent",
					"putClientProperty",
					"(Ljava/lang/Object;Ljava/lang/Object;)V");
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/Graphics", "create",
					"()Ljava/awt/Graphics;");
			mv.visitTypeInsn(CHECKCAST, "java/awt/Graphics2D");
			mv.visitVarInsn(ASTORE, 4);
			mv.visitVarInsn(ALOAD, 4);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitTypeInsn(CHECKCAST, "javax/swing/AbstractButton");
			mv.visitVarInsn(ALOAD, 3);
			mv
					.visitMethodInsn(
							INVOKESTATIC,
							"org/pushingpixels/lafwidget/animation/effects/GhostPaintingUtils",
							"paintGhostIcon",
							"(Ljava/awt/Graphics2D;Ljavax/swing/AbstractButton;Ljava/awt/Rectangle;)V");
			mv.visitVarInsn(ALOAD, 4);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/Graphics2D", "dispose",
					"()V");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitMethodInsn(INVOKEVIRTUAL, className, this.prefix
					+ methodName, methodDesc);
			mv.visitInsn(RETURN);
			mv.visitMaxs(5, 5);
			mv.visitEnd();
		}

		/**
		 * Augments the <code>installListeners</code> method.
		 * 
		 * @param toSynthOriginal
		 *            Indication whether we need to create an empty (only call
		 *            to super()) implementation.
		 * @param className
		 *            Class name.
		 * @param superClassName
		 *            Super class name (relevant for generating empty
		 *            implementation).
		 * @param methodName
		 *            Method name.
		 * @param functionDesc
		 *            Function signature (using JNI style declaration). Example
		 *            for <code>void installUI(JButton button)</code>:
		 *            <code>(Ljavax/swing/JButton;)V</code>.
		 */
		public void augmentInstallListeners(boolean toSynthOriginal,
				String className, String superClassName, String methodName,
				String functionDesc) {
			// Some ASM woodoo. The code below was generated by using
			// ASMifierClassVisitor.
			if (toSynthOriginal) {
				if (isVerbose) {
					System.out.println("... Creating empty '" + methodName
							+ functionDesc + "' forwarding to super '"
							+ superClassName + "'");
				}
				MethodVisitor mv = this.cv.visitMethod(Opcodes.ACC_PUBLIC,
						this.prefix + methodName, functionDesc, null, null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitMethodInsn(INVOKESPECIAL, superClassName,
						"installListeners", "(Ljavax/swing/AbstractButton;)V");
				mv.visitInsn(RETURN);
				mv.visitMaxs(2, 2);
				mv.visitEnd();
			}

			if (isVerbose) {
				System.out.println("... Augmenting '" + methodName
						+ functionDesc + "'");
			}
			MethodVisitor mv = this.cv.visitMethod(Opcodes.ACC_PUBLIC,
					methodName, functionDesc, null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, className, prefix
					+ "installListeners", functionDesc);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitTypeInsn(NEW, className + "$1");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKESPECIAL, className + "$1", "<init>", "(L"
					+ className + ";Ljavax/swing/AbstractButton;)V");
			mv.visitFieldInsn(PUTFIELD, className, "ghostPropertyListener",
					"Ljava/beans/PropertyChangeListener;");
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className, "ghostPropertyListener",
					"Ljava/beans/PropertyChangeListener;");
			mv.visitMethodInsn(INVOKEVIRTUAL, "javax/swing/AbstractButton",
					"addPropertyChangeListener",
					"(Ljava/beans/PropertyChangeListener;)V");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitTypeInsn(NEW,
					"org/pushingpixels/lafwidget/utils/GhostingListener");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "javax/swing/AbstractButton",
					"getModel", "()Ljavax/swing/ButtonModel;");
			mv.visitMethodInsn(INVOKESPECIAL,
					"org/pushingpixels/lafwidget/utils/GhostingListener",
					"<init>",
					"(Ljava/awt/Component;Ljavax/swing/ButtonModel;)V");
			mv.visitFieldInsn(PUTFIELD, className, "ghostModelChangeListener",
					"Lorg/pushingpixels/lafwidget/utils/GhostingListener;");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className, "ghostModelChangeListener",
					"Lorg/pushingpixels/lafwidget/utils/GhostingListener;");
			mv.visitMethodInsn(INVOKEVIRTUAL,
					"org/pushingpixels/lafwidget/utils/GhostingListener",
					"registerListeners", "()V");
			mv.visitInsn(RETURN);
			mv.visitMaxs(5, 2);
			mv.visitEnd();
		}

		/**
		 * Augments the <code>uninstallListeners</code> method.
		 * 
		 * @param toSynthOriginal
		 *            Indication whether we need to create an empty (only call
		 *            to super()) implementation.
		 * @param className
		 *            Class name.
		 * @param superClassName
		 *            Super class name (relevant for generating empty
		 *            implementation).
		 * @param methodName
		 *            Method name.
		 * @param functionDesc
		 *            Function signature (using JNI style declaration). Example
		 *            for <code>void installUI(JButton button)</code>:
		 *            <code>(Ljavax/swing/JButton;)V</code>.
		 */
		public void augmentUninstallListeners(boolean toSynthOriginal,
				String className, String superClassName, String methodName,
				String functionDesc) {
			// Some ASM woodoo. The code below was generated by using
			// ASMifierClassVisitor.
			if (toSynthOriginal) {
				if (isVerbose) {
					System.out.println("... Creating empty '" + methodName
							+ functionDesc + "' forwarding to super '"
							+ superClassName + "'");
				}
				MethodVisitor mv = this.cv.visitMethod(Opcodes.ACC_PUBLIC,
						this.prefix + methodName, functionDesc, null, null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, 1);
				mv
						.visitMethodInsn(INVOKESPECIAL, superClassName,
								"uninstallListeners",
								"(Ljavax/swing/AbstractButton;)V");
				mv.visitInsn(RETURN);
				mv.visitMaxs(2, 2);
				mv.visitEnd();

			}

			if (isVerbose) {
				System.out.println("... Augmenting '" + methodName
						+ functionDesc + "'");
			}
			MethodVisitor mv = this.cv.visitMethod(Opcodes.ACC_PUBLIC,
					methodName, functionDesc, null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className, "ghostPropertyListener",
					"Ljava/beans/PropertyChangeListener;");
			mv.visitMethodInsn(INVOKEVIRTUAL, "javax/swing/AbstractButton",
					"removePropertyChangeListener",
					"(Ljava/beans/PropertyChangeListener;)V");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(ACONST_NULL);
			mv.visitFieldInsn(PUTFIELD, className, "ghostPropertyListener",
					"Ljava/beans/PropertyChangeListener;");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className, "ghostModelChangeListener",
					"Lorg/pushingpixels/lafwidget/utils/GhostingListener;");
			mv.visitMethodInsn(INVOKEVIRTUAL,
					"org/pushingpixels/lafwidget/utils/GhostingListener",
					"unregisterListeners", "()V");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(ACONST_NULL);
			mv.visitFieldInsn(PUTFIELD, className, "ghostModelChangeListener",
					"Lorg/pushingpixels/lafwidget/utils/GhostingListener;");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, className, prefix
					+ "uninstallListeners", "(Ljavax/swing/AbstractButton;)V");
			mv.visitInsn(RETURN);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.objectweb.asm.ClassAdapter#visitMethod(int,
		 * java.lang.String, java.lang.String, java.lang.String,
		 * java.lang.String[])
		 */
		@Override
		public MethodVisitor visitMethod(final int access, final String name,
				final String desc, final String signature,
				final String[] exceptions) {

			Set<String> toAugment = new HashSet<String>();
			toAugment.add(this.methodToAugment.getName());
			// toAugment.add(this.methodInstallListeners.getName());
			// toAugment.add(this.methodUninstallListeners.getName());
			if (toAugment.contains(name)) {
				// possible candidate for weaving. Check if has __ already
				if (!this.existingMethods.contains(this.prefix + name)) {
					// effectively renames the existing method prepending __
					// to the name
					if (IconGhostingAugmenter.this.isVerbose)
						System.out.println("... renaming '" + name + "(" + desc
								+ ")' to '" + (this.prefix + name) + "'");
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
	 * @param paintIconMethodName
	 *            Method name.
	 * @throws AugmentException
	 *             If the augmentation process failed.
	 */
	protected synchronized void augmentClass(String dir, final String name,
			final String paintIconMethodName) {
		if (this.isVerbose)
			System.out
					.println("Working on " + name + "." + paintIconMethodName);
		// gets an input stream to read the bytecode of the class
		String resource = dir + File.separator + name.replace('.', '/')
				+ ".class";

		Method methodToAugment = null;
		Method methodInstallListeners = null;
		Method methodUninstallListeners = null;
		try {
			ClassLoader cl = new URLClassLoader(new URL[] { new File(dir)
					.toURL() }, IconGhostingAugmenter.class.getClassLoader());
			Class<?> clazz = cl.loadClass(name);
			// Start iterating over all methods and see what do we
			// need to augment
			while (clazz != null) {
				Method[] methods = clazz.getDeclaredMethods();
				for (int i = 0; i < methods.length; i++) {
					Method method = methods[i];
					if ((methodInstallListeners == null)
							&& (method.getName().equals("installListeners"))) {
						methodInstallListeners = method;
						continue;
					}
					if ((methodUninstallListeners == null)
							&& (method.getName().equals("uninstallListeners"))) {
						methodUninstallListeners = method;
						continue;
					}
					if (!paintIconMethodName.equals(method.getName()))
						continue;
					Class<?>[] params = method.getParameterTypes();
					boolean paramsOk = (params.length == 3);
					if (paramsOk) {
						paramsOk = paramsOk && (params[0] == Graphics.class);
						paramsOk = paramsOk
								&& (JComponent.class
										.isAssignableFrom(params[1]));
						paramsOk = paramsOk && (params[2] == Rectangle.class);
						if (isVerbose) {
							System.out.println("Method params are "
									+ params[0].getName() + ":"
									+ params[1].getName() + ":"
									+ params[2].getName() + " - " + paramsOk);
						}
					}
					if (!paramsOk)
						continue;
					if (methodToAugment == null) {
						methodToAugment = method;
					}
				}
				clazz = clazz.getSuperclass();
			}
		} catch (Exception e) {
			throw new AugmentException(name, e);
		}

		Set<String> existingMethods = null;
		Set<String> existingFields = null;
		InputStream is = null;
		try {
			is = new FileInputStream(resource);
			ClassReader cr = new ClassReader(is);
			InfoClassVisitor infoAdapter = new InfoClassVisitor();
			cr.accept(infoAdapter, false);
			existingMethods = infoAdapter.getMethods();
			existingFields = infoAdapter.getFields();
		} catch (Exception e) {
			throw new AugmentException(name, e);
		} finally {
			try {
				is.close();
			} catch (IOException ioe) {
			}
		}

		// See if the 'iconGhostingMarker' field is already defined. In this
		// case the class is *not* augmented
		if (existingFields.contains("iconGhostingMarker")) {
			if (this.isVerbose)
				System.out
						.println("Not augmenting resource, field 'iconGhostingMarker' is present");
			return;
		}

		// Augment the class (overriding the existing file).
		byte[] b;
		try {
			is = new FileInputStream(resource);
			ClassReader cr = new ClassReader(is);
			ClassWriter cw = new ClassWriter(false);
			ClassVisitor cv = new AugmentClassAdapter(cw, existingMethods,
					existingFields, methodToAugment// , methodInstallListeners,
			// methodUninstallListeners);
			);
			cr.accept(cv, false);
			b = cw.toByteArray();
		} catch (Exception e) {
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
	}

	/**
	 * Processes a single file or a directory, augmenting all relevant classes.
	 * 
	 * @param toStrip
	 *            The leading prefix to strip from the file names. Is used to
	 *            create fully-qualified class name.
	 * @param file
	 *            File resource (can point to a single file or to a directory).
	 * @param ids
	 *            List of class-method pairs to augment.
	 * @throws AugmentException
	 *             If the augmentation process failed.
	 */
	public void process(String toStrip, File file, List<IconGhostingType> ids)
			throws AugmentException {
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			for (int i = 0; i < children.length; i++) {
				this.process(toStrip, children[i], ids);
			}
		} else {
			String currClassName = file.getAbsolutePath().substring(
					toStrip.length() + 1);
			currClassName = currClassName.replace(File.separatorChar, '.');
			currClassName = currClassName.substring(0,
					currClassName.length() - 6);
			for (IconGhostingType igt : ids) {
				// System.out.println(currClassName + ":" + igt.getClassName());
				if (currClassName.equals(igt.getClassName())) {
					this.augmentClass(toStrip, igt.getClassName(), igt
							.getMethodName());
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
	 * Test method.
	 * 
	 * @param args
	 * @throws AugmentException
	 */
	public static void main(final String args[]) throws AugmentException {
		if (args.length == 0) {
			System.out
					.println("Usage : java ... IconGhostingDelegateAugmenter [-verbose] [-pattern class_pattern] file_resource");
			System.out
					.println("\tIf -verbose option is specified, the augmenter prints out its actions.");
			System.out
					.println("\tIf -class option is specified, its value is used as class name to augment.");
			System.out
					.println("\tIf -method option is specified, its value is used as method name to augment.");
			System.out
					.println("\tThe last parameter can point to either a file or a directory. "
							+ "The directory should be the root directory for classes.");
			return;
		}

		IconGhostingAugmenter uiDelegateAugmenter = new IconGhostingAugmenter();

		int argNum = 0;
		String className = null;
		String methodName = null;
		while (true) {
			String currArg = args[argNum];
			if ("-verbose".equals(currArg)) {
				uiDelegateAugmenter.setVerbose(true);
				argNum++;
				continue;
			}
			if ("-class".equals(currArg)) {
				argNum++;
				className = args[argNum];
				argNum++;
				continue;
			}
			if ("-method".equals(currArg)) {
				argNum++;
				methodName = args[argNum];
				argNum++;
				continue;
			}
			break;
		}

		File starter = new File(args[argNum]);
		IconGhostingType igt = new IconGhostingType();
		igt.setClassName(className);
		igt.setMethodName(methodName);
		uiDelegateAugmenter.process(starter.getAbsolutePath(), starter, Arrays
				.asList(igt));
	}
}

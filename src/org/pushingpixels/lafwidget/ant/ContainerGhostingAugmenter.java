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
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import org.objectweb.asm.*;

/**
 * Augments the UI classes with ghosting painting. Is based on sample adapter
 * from ASM distribution.
 * 
 * @author Kirill Grouchnikov
 */
public class ContainerGhostingAugmenter {
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

		/**
		 * <code>true</code> if the code needs to be injected after the call to
		 * the original implementation.
		 */
		private boolean toInjectAfterOriginal;

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
		 * @param toInjectAfterOriginal
		 *            <code>true</code> if the code needs to be injected after
		 *            the call to the original implementation.
		 */
		public AugmentClassAdapter(final ClassVisitor cv,
				Set<String> existingMethods, Set<String> existingFields,
				Method methodToAugment, boolean toInjectAfterOriginal) {
			super(cv);
			this.existingMethods = existingMethods;
			this.existingFields = existingFields;
			this.methodToAugment = methodToAugment;
			this.toInjectAfterOriginal = toInjectAfterOriginal;
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
			this.prefix = "__" + name.replaceAll("/", "__") + "__container__";
			super
					.visit(version, access, name, signature, superName,
							interfaces);

			// Check if need to add the containerGhostingMarker field (boolean)
			if (!this.existingFields.contains("containerGhostingMarker")) {
				FieldVisitor fv = this.visitField(Opcodes.ACC_PROTECTED,
						"containerGhostingMarker", "Z", null, null);
				fv.visitEnd();
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

			String methodName = methodToAugment.getName();
			boolean hasOriginal = this.existingMethods.contains(methodName);
			boolean hasDelegate = this.existingMethods.contains(this.prefix
					+ methodName);

			String methodSignature = Utils.getMethodDesc(methodToAugment);
			int paramCount = methodToAugment.getParameterTypes().length;
			if (ContainerGhostingAugmenter.this.isVerbose)
				System.out.println("... Augmenting " + methodName + " "
						+ methodSignature + " : original - " + hasOriginal
						+ ", delegate - " + hasDelegate + ", " + paramCount
						+ " params");

			if (!hasDelegate) {
				if (toInjectAfterOriginal) {
					this.augmentUpdateMethodAfter(!hasOriginal, name,
							superName, methodSignature);
				} else {
					this.augmentUpdateMethodBefore(!hasOriginal, name,
							superName, methodSignature);
				}
			}
		}

		/**
		 * Augments the <code>update</code> method that is assumed to always
		 * have two parameters, injecting the ghosting code <b>before</b> the
		 * original implementation.
		 * 
		 * @param toSynthOriginal
		 *            Indication whether we need to create an empty (only call
		 *            to super()) implementation.
		 * @param className
		 *            Class name.
		 * @param superClassName
		 *            Super class name (relevant for generating empty
		 *            implementation).
		 * @param methodDesc
		 *            Function signature (using JNI style declaration). Example
		 *            for <code>void installUI(JButton button)</code>:
		 *            <code>(Ljavax/swing/JButton;)V</code>.
		 */
		public void augmentUpdateMethodBefore(boolean toSynthOriginal,
				String className, String superClassName, String methodDesc) {
			// Some ASM woodoo. The code below was generated by using
			// ASMifierClassVisitor.
			if (toSynthOriginal) {
				MethodVisitor mv = this.cv.visitMethod(Opcodes.ACC_PUBLIC,
						this.prefix + "update",
						"(Ljava/awt/Graphics;Ljavax/swing/JComponent;)V", null,
						null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitVarInsn(ALOAD, 2);
				mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superClassName,
						"update",
						"(Ljava/awt/Graphics;Ljavax/swing/JComponent;)V");
				mv.visitInsn(RETURN);
				mv.visitMaxs(3, 3);
				mv.visitEnd();
			}

			MethodVisitor mv = this.cv.visitMethod(ACC_PROTECTED, "update",
					methodDesc, null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 2);
			mv.visitVarInsn(ALOAD, 1);
			mv
					.visitMethodInsn(
							INVOKESTATIC,
							"org/pushingpixels/lafwidget/animation/effects/GhostPaintingUtils",
							"paintGhostImages",
							"(Ljava/awt/Component;Ljava/awt/Graphics;)V");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitMethodInsn(INVOKEVIRTUAL, className,
					this.prefix + "update",
					"(Ljava/awt/Graphics;Ljavax/swing/JComponent;)V");
			mv.visitInsn(RETURN);
			mv.visitMaxs(3, 3);
			mv.visitEnd();
		}

		/**
		 * Augments the <code>update</code> method that is assumed to always
		 * have two parameters, injecting the ghosting code <b>after</b> the
		 * original implementation.
		 * 
		 * @param toSynthOriginal
		 *            Indication whether we need to create an empty (only call
		 *            to super()) implementation.
		 * @param className
		 *            Class name.
		 * @param superClassName
		 *            Super class name (relevant for generating empty
		 *            implementation).
		 * @param methodDesc
		 *            Function signature (using JNI style declaration). Example
		 *            for <code>void installUI(JButton button)</code>:
		 *            <code>(Ljavax/swing/JButton;)V</code>.
		 */
		public void augmentUpdateMethodAfter(boolean toSynthOriginal,
				String className, String superClassName, String methodDesc) {
			// Some ASM woodoo. The code below was generated by using
			// ASMifierClassVisitor.
			if (toSynthOriginal) {
				MethodVisitor mv = this.cv.visitMethod(Opcodes.ACC_PUBLIC,
						this.prefix + "update",
						"(Ljava/awt/Graphics;Ljavax/swing/JComponent;)V", null,
						null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitVarInsn(ALOAD, 2);
				mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superClassName,
						"update",
						"(Ljava/awt/Graphics;Ljavax/swing/JComponent;)V");
				mv.visitInsn(RETURN);
				mv.visitMaxs(3, 3);
				mv.visitEnd();
			}

			MethodVisitor mv = this.cv.visitMethod(ACC_PROTECTED, "update",
					methodDesc, null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitMethodInsn(INVOKEVIRTUAL, className,
					this.prefix + "update",
					"(Ljava/awt/Graphics;Ljavax/swing/JComponent;)V");
			mv.visitVarInsn(ALOAD, 2);
			mv.visitVarInsn(ALOAD, 1);
			mv
					.visitMethodInsn(
							INVOKESTATIC,
							"org/pushingpixels/lafwidget/animation/effects/GhostPaintingUtils",
							"paintGhostImages",
							"(Ljava/awt/Component;Ljava/awt/Graphics;)V");
			mv.visitInsn(RETURN);
			mv.visitMaxs(3, 3);
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
			if (methodToAugment.getName().equals(name)) {
				// possible candidate for weaving. Check if has __ already
				if (!this.existingMethods.contains(this.prefix + name)) {
					// effectively renames the existing method prepending __
					// to the name
					if (ContainerGhostingAugmenter.this.isVerbose)
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
	 * Augments a single class with image ghosting UI behaviour.
	 * 
	 * @param dir
	 *            Root directory for the library that contains the class.
	 * @param name
	 *            Fully-qualified class name.
	 * @param toInjectAfterOriginal
	 *            <code>true</code> if the code needs to be injected after the
	 *            call to the original implementation.
	 * @throws AugmentException
	 *             If the augmentation process failed.
	 */
	protected synchronized void augmentClass(String dir, final String name,
			final boolean toInjectAfterOriginal) {
		if (this.isVerbose)
			System.out.println("Working on " + name + ".update() ["
					+ (toInjectAfterOriginal ? "after]" : "before]"));
		// gets an input stream to read the bytecode of the class
		String resource = dir + File.separator + name.replace('.', '/')
				+ ".class";

		Method methodToAugment = null;
		try {
			ClassLoader cl = new URLClassLoader(new URL[] { new File(dir)
					.toURL() }, ContainerGhostingAugmenter.class
					.getClassLoader());
			Class<?> clazz = cl.loadClass(name);
			// Start iterating over all methods and see what do we
			// need to augment
			while (clazz != null) {
				if (methodToAugment != null)
					break;
				Method[] methods = clazz.getDeclaredMethods();
				for (int i = 0; i < methods.length; i++) {
					Method method = methods[i];
					if (!"update".equals(method.getName()))
						continue;
					methodToAugment = method;
					break;
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

		// See if the 'containerGhostingMarker' field is already defined. In
		// this
		// case the class is *not* augmented
		if (existingFields.contains("containerGhostingMarker")) {
			if (this.isVerbose)
				System.out
						.println("Not augmenting resource, field 'containerGhostingMarker' is present");
			return;
		}

		// Augment the class (overriding the existing file).
		byte[] b;
		try {
			is = new FileInputStream(resource);
			ClassReader cr = new ClassReader(is);
			ClassWriter cw = new ClassWriter(false);
			ClassVisitor cv = new AugmentClassAdapter(cw, existingMethods,
					existingFields, methodToAugment, toInjectAfterOriginal);
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
	public void process(String toStrip, File file,
			List<ContainerGhostingType> ids) throws AugmentException {
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
			for (ContainerGhostingType igt : ids) {
				// System.out.println(currClassName + ":" + igt.getClassName());
				if (currClassName.equals(igt.getClassName())) {
					this.augmentClass(toStrip, igt.getClassName(), igt
							.isToInjectAfterOriginal());
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
					.println("\tIf -class option if specified, its value is used as class name to augment.");
			System.out
					.println("\tIf -before option if specified, the code is injected before the original code.");
			System.out
					.println("\tThe last parameter can point to either a file or a directory. "
							+ "The directory should be the root directory for classes.");
			return;
		}

		ContainerGhostingAugmenter uiDelegateAugmenter = new ContainerGhostingAugmenter();

		int argNum = 0;
		String className = null;
		boolean isAfter = true;
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
			if ("-before".equals(currArg)) {
				argNum++;
				isAfter = false;
				continue;
			}
			break;
		}

		File starter = new File(args[argNum]);
		ContainerGhostingType igt = new ContainerGhostingType();
		igt.setClassName(className);
		igt.setToInjectAfterOriginal(isAfter);
		uiDelegateAugmenter.process(starter.getAbsolutePath(), starter, Arrays
				.asList(igt));
	}
}

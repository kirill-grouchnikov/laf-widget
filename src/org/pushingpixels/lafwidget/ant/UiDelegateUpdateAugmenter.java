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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Augments the UI classes with laf-widget behaviour. Is based on sample adapter
 * from ASM distribution.
 * 
 * @author Kirill Grouchnikov
 */
public class UiDelegateUpdateAugmenter {
	/**
	 * Verbosity indication.
	 */
	private boolean isVerbose;

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
		 * The <code>update</code> method to augment.
		 */
		private Method updateMethod;

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
		 * @param updateMethod
		 *            The <code>update</code> method to augment.
		 */
		public AugmentClassAdapter(final ClassVisitor cv,
				Set<String> existingMethods, Method updateMethod) {
			super(cv);
			this.existingMethods = existingMethods;
			this.updateMethod = updateMethod;
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
			this.prefix = "__" + name.replaceAll("/", "__") + "__";
			super
					.visit(version, access, name, signature, superName,
							interfaces);
			// We have three separate cases for each function that we
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

			boolean hasOriginal = this.existingMethods.contains("update");
			boolean hasDelegate = this.existingMethods.contains(this.prefix
					+ "update");

			String methodSignature = Utils.getMethodDesc(this.updateMethod);
			int paramCount = this.updateMethod.getParameterTypes().length;
			if (UiDelegateUpdateAugmenter.this.isVerbose)
				System.out.println("... Augmenting update " + methodSignature
						+ " : original - " + hasOriginal + ", delegate - "
						+ hasDelegate + ", " + paramCount + " params");

			if (!hasDelegate) {
				this.augmentUpdateMethod(!hasOriginal, name, superName,
						methodSignature);
			}
		}

		/**
		 * Augments the <code>update</code> method that is assumed to always
		 * have two parameters.
		 * 
		 * @param toSynthOriginal
		 *            Indication whether we need to create an empty (only call
		 *            to super()) implementation.
		 * @param className
		 *            Class name.
		 * @param superClassName
		 *            Super class name (relevant for generating empty
		 *            implementation).
		 * @param functionDesc
		 *            Function signature (using JNI style declaration). Example
		 *            for <code>void installUI(JButton button)</code>:
		 *            <code>(Ljavax/swing/JButton;)V</code>.
		 */
		public void augmentUpdateMethod(boolean toSynthOriginal,
				String className, String superClassName, String functionDesc) {
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

			MethodVisitor mv = this.cv.visitMethod(ACC_PUBLIC, "update",
					"(Ljava/awt/Graphics;Ljavax/swing/JComponent;)V", null,
					null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/Graphics", "create",
					"()Ljava/awt/Graphics;");
			mv.visitTypeInsn(CHECKCAST, "java/awt/Graphics2D");
			mv.visitVarInsn(ASTORE, 3);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitMethodInsn(INVOKESTATIC,
					"org/pushingpixels/lafwidget/utils/RenderingUtils",
					"installDesktopHints",
					"(Ljava/awt/Graphics2D;Ljava/awt/Component;)V");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitMethodInsn(INVOKEVIRTUAL, className,
					this.prefix + "update",
					"(Ljava/awt/Graphics;Ljavax/swing/JComponent;)V");
			mv.visitVarInsn(ALOAD, 3);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/Graphics2D", "dispose",
					"()V");
			mv.visitInsn(RETURN);
			mv.visitMaxs(3, 4);
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
			if ("update".equals(name)) {
				// possible candidate for weaving. Check if has __ already
				if (!this.existingMethods.contains(this.prefix + name)) {
					// effectively renames the existing method prepending __
					// to the name
					if (UiDelegateUpdateAugmenter.this.isVerbose)
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
	 * @throws AugmentException
	 *             If the augmentation process failed.
	 */
	protected synchronized void augmentClass(String dir, final String name) {
		if (this.isVerbose)
			System.out.println("Working on " + name);
		// gets an input stream to read the bytecode of the class
		String resource = dir + File.separator + name.replace('.', '/')
				+ ".class";

		Method updateMethod = null;
		try {
			ClassLoader cl = new URLClassLoader(new URL[] { new File(dir)
					.toURL() }, UiDelegateUpdateAugmenter.class
					.getClassLoader());
			Class<?> clazz = cl.loadClass(name);
			if (!ComponentUI.class.isAssignableFrom(clazz)) {
				if (this.isVerbose)
					System.out
							.println("Not augmenting resource, doesn't extend ComponentUI");
				return;
			}
			// Start iterating over all methods and see what do w
			// need to augment
			while (clazz != null) {
				try {
					updateMethod = clazz.getDeclaredMethod("update",
							Graphics.class, JComponent.class);
				} catch (NoSuchMethodException nsme) {
				}
				if (updateMethod != null)
					break;
				clazz = clazz.getSuperclass();
			}
		} catch (Exception e) {
			throw new AugmentException(name, e);
		}

		Set<String> existingMethods = null;
		InputStream is = null;
		try {
			is = new FileInputStream(resource);
			ClassReader cr = new ClassReader(is);
			InfoClassVisitor infoAdapter = new InfoClassVisitor();
			cr.accept(infoAdapter, false);
			existingMethods = infoAdapter.getMethods();
		} catch (Exception e) {
			throw new AugmentException(name, e);
		} finally {
			try {
				is.close();
			} catch (IOException ioe) {
			}
		}

		// Augment the class (overriding the existing file).
		byte[] b;
		try {
			is = new FileInputStream(resource);
			ClassReader cr = new ClassReader(is);
			ClassWriter cw = new ClassWriter(false);
			ClassVisitor cv = new AugmentClassAdapter(cw, existingMethods,
					updateMethod);
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
	 * Creates the new augmenter.
	 */
	public UiDelegateUpdateAugmenter() {
		super();
	}

	/**
	 * Processes a single file or a directory, augmenting all relevant classes.
	 * 
	 * @param toStrip
	 *            The leading prefix to strip from the file names. Is used to
	 *            create fully-qualified class name.
	 * @param file
	 *            File resource (can point to a single file or to a directory).
	 * @param pattern
	 *            Pattern to apply to the file name (of the single file). If the
	 *            file name matches the pattern, the relevant class is
	 *            augmented.
	 * @throws AugmentException
	 *             If the augmentation process failed.
	 */
	public void process(String toStrip, File file, Pattern pattern)
			throws AugmentException {
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			for (int i = 0; i < children.length; i++) {
				this.process(toStrip, children[i], pattern);
			}
		} else {
			Matcher m = pattern.matcher(file.getName());
			if (m.matches()) {
				String className = file.getAbsolutePath().substring(
						toStrip.length() + 1);
				className = className.replace(File.separatorChar, '.');
				this.augmentClass(toStrip, className.substring(0, className
						.length() - 6));
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
					.println("Usage : java ... UiDelegateUpdateAugmenter [-verbose] [-pattern class_pattern] file_resource");
			System.out
					.println("\tIf -verbose option is specified, the augmenter prints out its actions.");
			System.out
					.println("\tIf -pattern option is specified, its value is used as a wildcard "
							+ "for matching the classes for augmentation.");
			System.out
					.println("\tThe last parameter can point to either a file or a directory. "
							+ "The directory should be the root directory for classes.");
			return;
		}

		UiDelegateUpdateAugmenter uiDelegateAugmenter = new UiDelegateUpdateAugmenter();

		int argNum = 0;
		String pattern = ".*UI\u002Eclass";
		while (true) {
			String currArg = args[argNum];
			if ("-verbose".equals(currArg)) {
				uiDelegateAugmenter.setVerbose(true);
				argNum++;
				continue;
			}
			if ("-pattern".equals(currArg)) {
				argNum++;
				pattern = args[argNum];
				argNum++;
				continue;
			}
			break;
		}

		Pattern p = Pattern.compile(pattern);

		File starter = new File(args[argNum]);
		uiDelegateAugmenter.process(starter.getAbsolutePath(), starter, p);
	}
}

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

import java.io.File;
import java.util.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

/**
 * Ant task for augmenting LAF classes with image ghosting functionality. Is
 * based on JiBX ant task (BSD-licensed).
 * 
 * @author Kirill Grouchnikov.
 */
public class AugmentContainerGhostingTask extends Task {
	/**
	 * Verbosty indication.
	 */
	private boolean m_verbose;

	/**
	 * Classpath.
	 */
	private Path m_classpath;

	/**
	 * Fileset.
	 */
	private List<FileSet> m_fileSet;

	/**
	 * List of delegates to update.
	 */
	private List<ContainerGhostingType> m_delegatesToUpdate;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.Task#init()
	 */
	public void init() throws BuildException {
		super.init();
		this.m_fileSet = new ArrayList<FileSet>();
		this.m_delegatesToUpdate = new ArrayList<ContainerGhostingType>();
	}

	/**
	 * Returns an array of paths of all the files specified by the
	 * &lt;classpath&gt; or &lt;classpathset&gt; tags. Note that the
	 * &lt;classpath&gt; and &lt;classpathset&gt; tags cannot both be specified.
	 * 
	 * @return Array of file paths.
	 */
	private String[] getPaths() {
		String[] pathArray = null;
		if (this.m_classpath != null) {
			//
			// If a <classpath> tag has been set, m_classpath will
			// not be null. In this case, just return the array of
			// paths directly.
			//
			pathArray = this.m_classpath.list();
		} else {
			//
			// Store the directory paths specified by each of the
			// <classpathset> tags.
			//
			pathArray = new String[this.m_fileSet.size()];

			for (int i = 0; i < this.m_fileSet.size(); i++) {
				FileSet fileSet = this.m_fileSet.get(i);
				File directory = fileSet.getDir(this.getProject());
				pathArray[i] = directory.getAbsolutePath();
			}
		}
		return pathArray;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException {
		try {
			// System.out.println(this.m_verbose + ", " + this.m_pattern);
			//			
			String[] pathArr = this.getPaths();
			ContainerGhostingAugmenter augmenter = new ContainerGhostingAugmenter();
			if (this.m_verbose)
				augmenter.setVerbose(this.m_verbose);

			for (int i = 0; i < pathArr.length; i++) {
				augmenter.process(pathArr[0], new File(pathArr[0]),
						this.m_delegatesToUpdate);
			}
		} catch (AugmentException ae) {
			throw new BuildException(ae);
		}
		// process(this.m_classpath, new File(args[0]), p);
	}

	/**
	 * @param fSet
	 */
	public void addClassPathSet(FileSet fSet) {
		this.m_fileSet.add(fSet);
	}

	/**
	 * Adds information on a single class-method pair for injecting the
	 * container ghosting code.
	 * 
	 * @param containerGhosting
	 *            Class-method pair for injecting the container ghosting code.
	 */
	public void addContainerGhosting(ContainerGhostingType containerGhosting) {
		this.m_delegatesToUpdate.add(containerGhosting);
	}

	/**
	 * Returns the current classpath.
	 * 
	 * @return Current classpath.
	 */
	public Path getClasspath() {
		return this.m_classpath;
	}

	/**
	 * Sets the classpath for this task. Multiple calls append the new classpath
	 * to the current one, rather than overwriting it.
	 * 
	 * @param classpath
	 *            The new classpath as a Path object.
	 */
	public void setClasspath(Path classpath) {
		if (this.m_classpath == null) {
			this.m_classpath = classpath;
		} else {
			this.m_classpath.append(classpath);
		}
	}

	/**
	 * Creates the classpath for this task and returns it. If the classpath has
	 * already been created, the method just returns that one.
	 * 
	 * @return The created classpath.
	 */
	public Path createClasspath() {
		if (this.m_classpath == null) {
			this.m_classpath = new Path(this.getProject());
		}

		return this.m_classpath;
	}

	/**
	 * @param bool
	 */
	public void setVerbose(boolean bool) {
		this.m_verbose = bool;
	}
}

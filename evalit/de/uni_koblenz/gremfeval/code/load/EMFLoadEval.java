/*
 * grEMF
 *
 * Copyright (C) 2006-2012 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 *
 * For bug reports, documentation and further information, visit
 *
 *                         https://github.com/jgralab/gremf
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */
package de.uni_koblenz.gremfeval.code.load;

import java.io.File;
import java.util.concurrent.Callable;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import de.uni_koblenz.gremf.resource.GrEMFResourceFactoryImpl;

public class EMFLoadEval implements Callable<Resource> {

	private final boolean withGrEMF;

	public EMFLoadEval(boolean withGrEMF) {
		this.withGrEMF = withGrEMF;
	}

	@Override
	public Resource call() {
		ResourceSet rs = new ResourceSetImpl();
		if (this.withGrEMF) {
			rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
					.put("*", new GrEMFResourceFactoryImpl());
		} else {
			rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
					.put("*", new XMIResourceFactoryImpl());
		}
		Resource schema = rs.getResource(
				URI.createURI(System.getProperty("user.dir") + File.separator
						+ "models" + File.separator + "java.gremf"), true);
		if (!this.withGrEMF) {
			// register packages
			TreeIterator<EObject> i = schema.getAllContents();
			while (i.hasNext()) {
				EObject eObj = i.next();
				if (eObj instanceof EPackage) {
					rs.getPackageRegistry().put(((EPackage) eObj).getNsURI(),
							eObj);
				}
			}
		}
		return rs
				.getResource(
						URI.createURI(System.getProperty("user.dir")
								+ File.separator + "models" + File.separator
								+ "model1.javamodel"), true);
	}
}

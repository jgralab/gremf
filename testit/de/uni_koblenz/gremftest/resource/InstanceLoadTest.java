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
package de.uni_koblenz.gremftest.resource;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.junit.Before;
import org.junit.Test;

import de.uni_koblenz.gremf.resource.GrEMFResourceFactoryImpl;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIOException;

public class InstanceLoadTest {

	private String dir;

	@Before
	public void setUp() {
		this.dir = System.getProperty("user.dir") + File.separator + "models"
				+ File.separator;
	}

	@Test
	public void test2Resources() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new GrEMFResourceFactoryImpl());

		Resource instance = rs.getResource(
				URI.createURI(this.dir + "model1.a"), true);

		TreeIterator<EObject> i = instance.getAllContents();
		while (i.hasNext()) {
			EObject eObj = i.next();
			System.out.println(eObj);
			System.out.println(eObj.eClass());
		}

	}

	@Test
	public void testConceptualEdge() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new GrEMFResourceFactoryImpl());

		rs.getResource(URI.createURI(this.dir + "concept.gremf"), true);

		Resource instance = rs.getResource(
				URI.createURI(this.dir + "model1.concept"), true);

		TreeIterator<EObject> i = instance.getAllContents();
		while (i.hasNext()) {
			EObject eObj = i.next();
			System.out.println(eObj);
			System.out.println(eObj.eClass());
		}

	}

	@Test
	public void testExtendedInstance1() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new GrEMFResourceFactoryImpl());

		rs.getResource(URI.createURI(this.dir + "universityCourses.gremf"),
				true);

		Resource instance = rs.getResource(
				URI.createURI(this.dir + "model1.universityCourses"), true);

		TreeIterator<EObject> i = instance.getAllContents();
		while (i.hasNext()) {
			EObject eObj = i.next();
			System.out.println(eObj);
			System.out.println(eObj.eClass());
		}
		try {
			((Graph) instance.getContents().get(0)).save(this.dir + "generated"
					+ File.separator + "model2.universityCourses");
		} catch (GraphIOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testExtendedInstance2() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new GrEMFResourceFactoryImpl());

		rs.getResource(URI.createURI(this.dir + "universityExt.gremf"), true);

		Resource instance = rs.getResource(
				URI.createURI(this.dir + "model1.universityExt"), true);

		TreeIterator<EObject> i = instance.getAllContents();
		while (i.hasNext()) {
			EObject eObj = i.next();
			System.out.println(eObj);
			System.out.println(eObj.eContainer());
			System.out.println(eObj.eContents());
			System.out.println("-----");

		}
		try {
			((Graph) instance.getContents().get(0)).save(this.dir + "generated"
					+ File.separator + "model2.universityExt");
		} catch (GraphIOException e) {
			e.printStackTrace();
		}
		instance.setURI(URI.createURI(this.dir + "generated" + File.separator
				+ "model4.universityExt"));
		try {
			instance.save(((XMLResource) instance).getDefaultSaveOptions());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testExtendedInstance3() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new GrEMFResourceFactoryImpl());

		rs.getResource(URI.createURI(this.dir + "bookStore.gremf"), true);

		Resource instance = rs.getResource(
				URI.createURI(this.dir + "model1.bookStore"), true);

		TreeIterator<EObject> i = instance.getAllContents();
		while (i.hasNext()) {
			EObject eObj = i.next();
			System.out.println(eObj);
			System.out.println(eObj.eClass());
			System.out.println(eObj.eContainer());
			System.out.println(eObj.eResource());
		}
		try {
			((Graph) instance.getContents().get(0)).save(this.dir + "generated"
					+ File.separator + "model2.bookStore");
		} catch (GraphIOException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testExtendedInstance4() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new GrEMFResourceFactoryImpl());

		rs.getResource(URI.createURI(this.dir + "java.gremf"), true);

		Resource instance = rs.getResource(
				URI.createURI(this.dir + "model1.javamodel"), true);

		TreeIterator<EObject> i = instance.getAllContents();
		while (i.hasNext()) {
			EObject eObj = i.next();
			System.out.println(eObj);
			System.out.println(eObj.eClass());
			System.out.println(eObj.eContainer());
			System.out.println(eObj.eResource());
		}
		try {
			((Graph) instance.getContents().get(0)).save(this.dir + "generated"
					+ File.separator + "model1.javamodel");
		} catch (GraphIOException e) {
			e.printStackTrace();
		}
		System.err.println(((Graph) instance.getContents().get(0)).getVCount());
		System.err.println(((Graph) instance.getContents().get(0)).getECount());

	}

}

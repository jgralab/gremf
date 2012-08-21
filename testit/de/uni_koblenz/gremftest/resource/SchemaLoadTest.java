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
import java.util.Collections;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Before;
import org.junit.Test;

import de.uni_koblenz.gremf.resource.GrEMFResourceFactoryImpl;

public class SchemaLoadTest {

	private String dir;

	@Before
	public void setUp() {
		this.dir = System.getProperty("user.dir") + File.separator + "models"
				+ File.separator;
		System.out.println(this.dir);
	}

	@Test
	public void test2Resources1() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new GrEMFResourceFactoryImpl());

		Resource a = rs.getResource(URI.createURI(this.dir + "a.gremf"), true);

		TreeIterator<EObject> i = a.getAllContents();
		while (i.hasNext()) {
			EObject eObj = i.next();
			System.out.println(eObj);
		}
	}

	@Test
	public void test2Resources2() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new GrEMFResourceFactoryImpl());

		Resource a = rs.createResource(URI.createURI(this.dir + "a.gremf"));

		rs.createResource(URI.createURI(this.dir + "b.gremf"));
		try {
			a.load(Collections.EMPTY_MAP);
		} catch (IOException e) {
			e.printStackTrace();
		}

		TreeIterator<EObject> i = a.getAllContents();
		while (i.hasNext()) {
			EObject eObj = i.next();
			System.out.println(eObj);
		}
	}

	@Test
	public void testConceptualEdgeClass() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new GrEMFResourceFactoryImpl());

		Resource concept = rs.getResource(
				URI.createURI(this.dir + "concept.gremf"), true);

		TreeIterator<EObject> i = concept.getAllContents();
		while (i.hasNext()) {
			EObject eObj = i.next();
			System.out.println(eObj + ": " + eObj.getClass());
		}
	}
	
	@Test
	public void testIncompatibleEdgeClass() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new GrEMFResourceFactoryImpl());

		Resource toytrain = rs.getResource(URI.createURI(this.dir + "toytrain.gremf"), true);

		TreeIterator<EObject> i = toytrain.getAllContents();
		while (i.hasNext()) {
			EObject eObj = i.next();
			System.out.println(eObj + ": " + eObj.getClass());
		}

	}

	@Test
	public void testExtendedSchema1() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new GrEMFResourceFactoryImpl());

		Resource concept = rs.getResource(
				URI.createURI(this.dir + "extlibrary.gremf"), true);

		TreeIterator<EObject> i = concept.getAllContents();
		while (i.hasNext()) {
			EObject eObj = i.next();
			System.out.println(eObj + ": " + eObj.getClass());
		}

	}

	@Test
	public void testExtendedSchema2() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new GrEMFResourceFactoryImpl());

		Resource schema = rs.getResource(
				URI.createURI(this.dir + "universityExt.gremf"), true);

		TreeIterator<EObject> i = schema.getAllContents();
		while (i.hasNext()) {
			EObject eObj = i.next();
			System.out.println(eObj);
			System.out.println(eObj.eClass());
		}

	}

	@Test
	public void testExtendedSchema3() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new GrEMFResourceFactoryImpl());

		Resource schema = rs.getResource(
				URI.createURI(this.dir + "bookStore.gremf"), true);

		TreeIterator<EObject> i = schema.getAllContents();
		while (i.hasNext()) {
			EObject eObj = i.next();
			System.out.println(eObj);
			System.out.println(eObj.eClass());
		}

	}
}

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
package de.uni_koblenz.gremftest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.junit.Before;
import org.junit.Test;

import de.uni_koblenz.gremf.GrEMFFacade;
import de.uni_koblenz.gremf.impl.GrEMFVertexImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFPackageImpl;

public class GrEMFGraphImplTest {
	private Resource schema;
	private Resource instance;

	private EPackage bookStorePkg;
	private EClass graphClass;
	private EClass bookStoreCls;
	private EClass bookCls;
	private EEnum categoryEnum;
	private EReference bookStore_Books;
	private EReference bookStore_soldBooks;
	private EReference bookStore_grEMF_soldBooks;
	private EAttribute book_name;
	private EAttribute book_isbn;
	private EAttribute book_authors;
	private EAttribute book_category;
	private EAttribute bookStore_location;

	private EObject bookStoreEOb;
	private EObject bookEOb1;
	private EObject bookEOb2;
	private EObject bookEOb3;
	private EObject sellsBookEOb1;
	private EObject graph;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		ResourceSet load_resourceSet = new ResourceSetImpl();

		load_resourceSet.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put("gremf", GrEMFFacade.getGrEMFResourceFactory());
		load_resourceSet.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put("bookStore", GrEMFFacade.getGrEMFResourceFactory());

		this.schema = load_resourceSet.getResource(
				URI.createURI(System.getProperty("user.dir") + File.separator
						+ "models" + File.separator + "bookStore.gremf"), true);

		EPackage p = (EPackage) this.schema.getContents().get(0);
		load_resourceSet.getPackageRegistry().put(p.getNsURI(), p);
		load_resourceSet.getPackageRegistry().put(
				p.getESubpackages().get(0).getNsURI(),
				p.getESubpackages().get(0));

		EPackage rootPkg = (EPackage) this.schema.getContents().get(0);

		this.bookStorePkg = rootPkg.getESubpackages().get(0);
		this.graphClass = (EClass) ((GrEMFPackageImpl) this.bookStorePkg)
				.getSchema().getGraphClass();
		this.bookStoreCls = (EClass) this.bookStorePkg
				.getEClassifier("BookStore");
		this.bookCls = (EClass) this.bookStorePkg.getEClassifier("Book");
		this.categoryEnum = (EEnum) this.bookStorePkg
				.getEClassifier("Category");

		this.bookStore_Books = (EReference) this.bookStoreCls
				.getEStructuralFeature("books");
		this.bookStore_soldBooks = (EReference) this.bookStoreCls
				.getEStructuralFeature("soldBooks");
		this.bookStore_grEMF_soldBooks = (EReference) this.bookStoreCls
				.getEStructuralFeature("grEMF_soldBooks");
		this.book_name = (EAttribute) this.bookCls
				.getEStructuralFeature("name");
		this.book_isbn = (EAttribute) this.bookCls
				.getEStructuralFeature("isbn");
		this.book_authors = (EAttribute) this.bookCls
				.getEStructuralFeature("authors");
		this.book_category = (EAttribute) this.bookCls
				.getEStructuralFeature("category");
		this.bookStore_location = (EAttribute) this.bookStoreCls
				.getEStructuralFeature("location");

		// ----------------

		this.bookStoreEOb = EcoreUtil.create(this.bookStoreCls);
		this.bookStoreEOb.eSet(this.bookStore_location, "Koblenz");

		this.bookEOb1 = EcoreUtil.create(this.bookCls);
		this.bookEOb1.eSet(this.book_name,
				"Honor Harrington - A short victorious war");
		this.bookEOb1.eSet(this.book_isbn, 12345);
		this.bookEOb1.eSet(this.book_category,
				this.categoryEnum.getEEnumLiteral("SciFi"));
		((List<String>) this.bookEOb1.eGet(this.book_authors))
				.add("David Weber");

		this.bookEOb2 = EcoreUtil.create(this.bookCls);
		this.bookEOb2.eSet(this.book_name,
				"Honor Harrington - Honor among enemies");
		this.bookEOb2.eSet(this.book_category,
				this.categoryEnum.getEEnumLiteral("SciFi"));
		((List<String>) this.bookEOb2.eGet(this.book_authors))
				.add("David Weber");

		this.bookEOb3 = EcoreUtil.create(this.bookCls);
		this.bookEOb3.eSet(this.book_name, "Fall of Giants");
		this.bookEOb3.eSet(this.book_isbn, 13346);
		this.bookEOb3.eSet(this.book_category,
				this.categoryEnum.getEEnumLiteral("Thriller"));
		((List<String>) this.bookEOb3.eGet(this.book_authors))
				.add("Ken Follet");
		((List<String>) this.bookEOb3.eGet(this.book_authors))
				.add("Pan Macmillan");

		((List<EObject>) this.bookStoreEOb.eGet(this.bookStore_Books))
				.add(this.bookEOb1);
		((List<EObject>) this.bookStoreEOb.eGet(this.bookStore_Books))
				.add(this.bookEOb2);
		((List<EObject>) this.bookStoreEOb.eGet(this.bookStore_Books))
				.add(this.bookEOb3);

		((List<EObject>) this.bookStoreEOb.eGet(this.bookStore_grEMF_soldBooks))
				.add(this.bookEOb1);
		this.sellsBookEOb1 = ((List<EObject>) this.bookStoreEOb
				.eGet(this.bookStore_soldBooks)).get(0);

		this.graph = (EObject) ((GrEMFVertexImpl) this.bookStoreEOb).getGraph();

		this.instance = new XMIResourceImpl();
		this.instance.getContents().add(this.bookStoreEOb);
		this.instance.getContents().add(this.graph);

	}

	@Test
	public void testEClass() {
		assertEquals(this.graphClass, this.graph.eClass());
	}

	@Test
	public void testEResource() {
		assertEquals(this.instance, this.graph.eResource());
	}

	@Test
	public void testEContainer() {
		assertEquals(null, this.graph.eContainer());
	}

	@Test
	public void testEContainingFeature() {
		assertEquals(null, this.graph.eContainingFeature());
	}

	@Test
	public void testEContainmentFeature() {
		assertEquals(null, this.graph.eContainmentFeature());
	}

	@Test
	public void testEContents() {
		assertEquals(new BasicEList<EObject>(), this.graph.eContents());
	}

	@Test
	public void testEAllContents() {
		assertEquals(new BasicEList<EObject>(), this.graph.eAllContents());
	}

	@Test
	public void testECrossReferences() {
		assertEquals(5, this.graph.eCrossReferences().size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEGet() {
		Iterable<EObject> vertices = (Iterable<EObject>) this.graph
				.eGet(this.graphClass.getEStructuralFeature("vertices"));

		EObject[] expectedList = { this.bookStoreEOb, this.bookEOb1,
				this.bookEOb2, this.bookEOb3 };
		int i = 0;
		for (EObject o : vertices) {
			assertEquals(expectedList[i], o);
			i++;
		}

		Iterable<EObject> edges = (Iterable<EObject>) this.graph
				.eGet(this.graphClass.getEStructuralFeature("edges"));
		assertTrue(edges.iterator().hasNext());
		assertEquals(this.sellsBookEOb1, edges.iterator().next());

	}

	@Test
	public void testEIsSet() {
		assertTrue(this.graph.eIsSet(this.graphClass
				.getEStructuralFeature("vertices")));
		assertTrue(this.graph.eIsSet(this.graphClass
				.getEStructuralFeature("edges")));
	}

	@Test
	public void testEContainerFeatureID() {
		assertEquals(0,
				((InternalEObject) this.sellsBookEOb1).eContainerFeatureID());
	}

	@Test
	public void testEDirectResource() {
		assertEquals(this.instance,
				((InternalEObject) this.graph).eDirectResource());
	}
}

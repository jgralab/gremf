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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

public class GrEMFVertexImplTest {
	private Resource schema;
	private Resource instance;

	private EPackage bookStorePkg;
	private EClass bookStoreCls;
	private EClass bookCls;
	private EClass sellsCls;
	private EEnum categoryEnum;
	private EReference bookStore_Books;
	private EReference sells_seller;
	private EReference sells_object;
	private EReference bookStore_soldBooks;
	private EReference bookStore_grEMF_soldBooks;
	private EReference book_chapters;
	private EAttribute sells_price;
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

		this.bookStoreCls = (EClass) this.bookStorePkg
				.getEClassifier("BookStore");
		this.bookCls = (EClass) this.bookStorePkg.getEClassifier("Book");
		this.sellsCls = (EClass) this.bookStorePkg.getEClassifier("Sells");
		this.categoryEnum = (EEnum) this.bookStorePkg
				.getEClassifier("Category");

		this.bookStore_Books = (EReference) this.bookStoreCls
				.getEStructuralFeature("books");
		this.bookStore_soldBooks = (EReference) this.bookStoreCls
				.getEStructuralFeature("soldBooks");
		this.bookStore_grEMF_soldBooks = (EReference) this.bookStoreCls
				.getEStructuralFeature("grEMF_soldBooks");
		this.sells_seller = (EReference) this.sellsCls
				.getEStructuralFeature("sellsSubject");
		this.sells_object = (EReference) this.sellsCls
				.getEStructuralFeature("sellsObject");
		this.book_name = (EAttribute) this.bookCls
				.getEStructuralFeature("name");
		this.book_isbn = (EAttribute) this.bookCls
				.getEStructuralFeature("isbn");
		this.book_authors = (EAttribute) this.bookCls
				.getEStructuralFeature("authors");
		this.book_category = (EAttribute) this.bookCls
				.getEStructuralFeature("category");
		this.book_chapters = (EReference) this.bookCls
				.getEStructuralFeature("chapters");
		this.bookStore_location = (EAttribute) this.bookStoreCls
				.getEStructuralFeature("location");
		this.sells_price = (EAttribute) this.sellsCls
				.getEStructuralFeature("price");

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

		this.instance = new XMIResourceImpl();
		this.instance.getContents().add(this.bookStoreEOb);
		// this.instance.getContents().add(this.sellsBookEOb1);
	}

	@Test
	public void testEClass() {
		assertEquals(this.bookStoreCls, this.bookStoreEOb.eClass());
		assertEquals(this.bookCls, this.bookEOb1.eClass());
	}

	@Test
	public void testEResource() {
		assertEquals(this.instance, this.bookStoreEOb.eResource());
		assertEquals(this.instance, this.bookEOb1.eResource());
	}

	@Test
	public void testEContainer() {
		assertEquals(null, this.bookStoreEOb.eContainer());
		assertEquals(this.bookStoreEOb, this.bookEOb1.eContainer());
	}

	@Test
	public void testEContainingFeature() {
		assertEquals(this.bookStore_Books, this.bookEOb1.eContainingFeature());
		assertEquals(null, this.bookStoreEOb.eContainingFeature());
	}

	@Test
	public void testEContainmentFeature() {
		assertEquals(this.bookStore_Books, this.bookEOb1.eContainmentFeature());
		assertEquals(null, this.bookStoreEOb.eContainmentFeature());
	}

	@Test
	public void testEContents() {
		assertEquals(3, this.bookStoreEOb.eContents().size());
		assertEquals(true, this.bookStoreEOb.eContents()
				.contains(this.bookEOb1));
		assertEquals(true, this.bookStoreEOb.eContents()
				.contains(this.bookEOb2));
		assertEquals(true, this.bookStoreEOb.eContents()
				.contains(this.bookEOb3));
		assertEquals(0, this.bookEOb1.eContents().size());
	}

	@Test
	public void testEAllContents() {
		Iterator<EObject> cont = this.bookStoreEOb.eAllContents();
		assertEquals(true, cont.hasNext());
		assertEquals(this.bookEOb1, cont.next());
		assertEquals(this.bookEOb2, cont.next());
		assertEquals(this.bookEOb3, cont.next());
	}

	@Test
	public void testECrossReferences() {
		// different from EMF because of additional EReference
		assertEquals(4, this.bookStoreEOb.eCrossReferences().size());
		assertEquals(this.bookEOb1, this.bookStoreEOb.eCrossReferences().get(0));
		assertEquals(this.sellsBookEOb1, this.bookStoreEOb.eCrossReferences()
				.get(1));
		assertEquals(true, this.bookEOb1.eCrossReferences().isEmpty());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEGet() {
		assertEquals("Koblenz", this.bookStoreEOb.eGet(this.bookStore_location));
		assertEquals(this.bookEOb1,
				((List<EObject>) this.bookStoreEOb.eGet(this.bookStore_Books))
						.get(0));
		assertEquals(3,
				((List<EObject>) this.bookStoreEOb.eGet(this.bookStore_Books))
						.size());
		assertEquals(this.categoryEnum.getEEnumLiteral("SciFi"),
				this.bookEOb1.eGet(this.book_category));
		assertEquals(2,
				((List<String>) this.bookEOb3.eGet(this.book_authors)).size());
		assertEquals(true,
				((List<String>) this.bookEOb3.eGet(this.book_authors))
						.contains("Ken Follet"));
		assertEquals(true,
				((List<String>) this.bookEOb3.eGet(this.book_authors))
						.contains("Pan Macmillan"));
		assertEquals(this.bookStoreEOb,
				this.sellsBookEOb1.eGet(this.sells_seller));
		assertEquals(this.bookEOb1, this.sellsBookEOb1.eGet(this.sells_object));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testESet() {
		EObject newBook1 = EcoreUtil.create(this.bookCls);
		EObject newBook2 = EcoreUtil.create(this.bookCls);
		ArrayList<EObject> books = new ArrayList<EObject>();
		books.add(newBook1);
		books.add(newBook2);

		this.bookStoreEOb.eSet(this.bookStore_Books, books);

		assertEquals(2,
				((List<EObject>) this.bookStoreEOb.eGet(this.bookStore_Books))
						.size());
		assertEquals(newBook1,
				((List<EObject>) this.bookStoreEOb.eGet(this.bookStore_Books))
						.get(0));
		assertEquals(newBook2,
				((List<EObject>) this.bookStoreEOb.eGet(this.bookStore_Books))
						.get(1));

		newBook1.eSet(this.book_name, "Big Book");
		newBook1.eSet(this.book_category,
				this.categoryEnum.getEEnumLiteral("Thriller"));

		assertEquals("Big Book", newBook1.eGet(this.book_name));
		assertEquals(this.categoryEnum.getEEnumLiteral("Thriller"),
				newBook1.eGet(this.book_category));

		Set<String> authors = new TreeSet<String>();
		authors.add("Florian");
		authors.add("Tom");
		authors.add("Simon");
		newBook1.eSet(this.book_authors, authors);

		assertEquals(3,
				((List<String>) newBook1.eGet(this.book_authors)).size());
		assertEquals(true,
				((List<String>) newBook1.eGet(this.book_authors))
						.contains("Florian"));
		assertEquals(true,
				((List<String>) newBook1.eGet(this.book_authors))
						.contains("Tom"));
		assertEquals(true,
				((List<String>) newBook1.eGet(this.book_authors))
						.contains("Simon"));

		this.sellsBookEOb1.eSet(this.sells_price, 9.99);
		assertEquals(9.99, this.sellsBookEOb1.eGet(this.sells_price));

	}

	@Test
	public void testEIsSet() {
		assertEquals(true, this.bookStoreEOb.eIsSet(this.bookStore_location));
		assertEquals(true, this.bookStoreEOb.eIsSet(this.bookStore_Books));
		assertEquals(true, this.bookEOb1.eIsSet(this.book_isbn));
		assertEquals(false, this.bookEOb2.eIsSet(this.book_isbn));
		assertEquals(false, this.bookEOb1.eIsSet(this.book_chapters));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEUnset() {
		this.bookEOb1.eUnset(this.book_name);
		assertEquals(null, this.bookEOb1.eGet(this.book_name));

		this.bookEOb3.eUnset(this.book_category);
		assertEquals(this.categoryEnum.getDefaultValue(),
				this.bookEOb1.eGet(this.book_category));

		this.bookStoreEOb.eUnset(this.bookStore_Books);
		assertEquals(true,
				((List<EObject>) this.bookStoreEOb.eGet(this.bookStore_Books))
						.isEmpty());
	}

	@Test
	public void testEContainerFeatureID() {
		assertEquals(-3,
				((InternalEObject) this.bookEOb1).eContainerFeatureID());
		assertEquals(-3,
				((InternalEObject) this.bookEOb2).eContainerFeatureID());
		assertEquals(-3,
				((InternalEObject) this.bookEOb3).eContainerFeatureID());
		assertEquals(0,
				((InternalEObject) this.bookStoreEOb).eContainerFeatureID());
		assertEquals(0,
				((InternalEObject) this.sellsBookEOb1).eContainerFeatureID());
	}

	@Test
	public void testEDirectResource() {
		assertEquals(this.instance,
				((InternalEObject) this.bookStoreEOb).eDirectResource());
		assertEquals(null, ((InternalEObject) this.bookEOb1).eDirectResource());
	}
}

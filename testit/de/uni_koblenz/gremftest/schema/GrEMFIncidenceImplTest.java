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
package de.uni_koblenz.gremftest.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_koblenz.gremf.resource.GrEMFResourceFactoryImpl;

public class GrEMFIncidenceImplTest {
	private EPackage bookStorePkg;
	private EClass bookStoreCls;
	private EClass bookCls;
	private EClass sellsCls;
	private EReference bookStore_Books;
	private EReference sells_seller;
	private EReference bookStore_soldBooks;
	private EReference bookStore_grEMFSoldBooks;

	private static Resource schema;

	@BeforeClass
	public static void setUpBeforeClass() {
		ResourceSet load_resourceSet = new ResourceSetImpl();

		load_resourceSet.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put("gremf", new GrEMFResourceFactoryImpl());
		schema = load_resourceSet.getResource(
				URI.createURI(System.getProperty("user.dir") + File.separator
						+ "models" + File.separator + "bookStore.gremf"), true);

	}

	@Before
	public void setUp() {
		EPackage rootPkg = (EPackage) schema.getContents().get(0);

		this.bookStorePkg = rootPkg.getESubpackages().get(0);

		this.bookStoreCls = (EClass) this.bookStorePkg
				.getEClassifier("BookStore");
		this.bookCls = (EClass) this.bookStorePkg.getEClassifier("Book");
		this.sellsCls = (EClass) this.bookStorePkg.getEClassifier("Sells");

		this.bookStore_Books = (EReference) this.bookStoreCls
				.getEStructuralFeature("books");

		this.bookStore_soldBooks = (EReference) this.bookStoreCls
				.getEStructuralFeature("soldBooks");
		this.sells_seller = (EReference) this.sellsCls
				.getEStructuralFeature("sellsSubject");
		this.bookStore_grEMFSoldBooks = (EReference) this.bookStoreCls
				.getEStructuralFeature("grEMF_soldBooks");

	}

	@After
	public void tearDown() {
		this.bookStorePkg = null;

		this.bookStoreCls = null;
		this.bookCls = null;

		this.bookStore_Books = null;
		this.bookStore_soldBooks = null;
		this.sells_seller = null;

	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EStructuralFeature
	// --------------------------------------------------------------------------

	@Test
	public void testGetEContainingClass() {
		assertEquals(this.bookStoreCls,
				this.bookStore_Books.getEContainingClass());
		assertEquals(this.bookStoreCls,
				this.bookStore_soldBooks.getEContainingClass());
		assertEquals(this.bookStoreCls,
				this.bookStore_grEMFSoldBooks.getEContainingClass());
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.ETypedElement
	// --------------------------------------------------------------------------

	@Test
	public void testGetLowerBound() {
		assertEquals(0, this.bookStore_Books.getLowerBound());
		assertEquals(1, this.sells_seller.getLowerBound());
		assertEquals(1, this.bookStore_soldBooks.getLowerBound());
	}

	@Test
	public void testGetUpperBound() {
		assertEquals(-1, this.bookStore_Books.getUpperBound());
		assertEquals(1, this.sells_seller.getUpperBound());
		assertEquals(-1, this.bookStore_soldBooks.getUpperBound());
	}

	@Test
	public void testIsMany() {
		assertEquals(true, this.bookStore_Books.isMany());
		assertEquals(false, this.sells_seller.isMany());
		assertEquals(true, this.bookStore_soldBooks.isMany());
	}

	@Test
	public void testIsRequired() {
		assertEquals(false, this.bookStore_Books.isRequired());
		assertEquals(true, this.sells_seller.isRequired());
		assertEquals(true, this.bookStore_soldBooks.isRequired());
	}

	@Test
	public void testGetEType() {
		assertEquals(this.bookCls, this.bookStore_Books.getEType());
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.ENamedElement
	// --------------------------------------------------------------------------

	@Test
	public void testGetName() {
		assertEquals("books", this.bookStore_Books.getName());
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EObject
	// --------------------------------------------------------------------------

	@Test
	public void testEClass() {
		assertEquals(EcorePackage.Literals.EREFERENCE,
				this.bookStore_Books.eClass());
	}

	@Test
	public void testEResource() {
		assertEquals(schema, this.bookStore_Books.eResource());
	}

	@Test
	public void testEContainer() {
		assertEquals(this.bookStoreCls, this.bookStore_Books.eContainer());
	}

	@Test
	public void testEContainingFeature() {
		assertEquals(EcorePackage.Literals.ECLASS__ESTRUCTURAL_FEATURES,
				this.bookStore_Books.eContainingFeature());
	}

	@Test
	public void testEContainmentFeature() {
		assertEquals(EcorePackage.Literals.ECLASS__ESTRUCTURAL_FEATURES,
				this.bookStore_Books.eContainmentFeature());
	}

	@Test
	public void testECrossReferences() {
		assertEquals(2, this.bookStore_Books.eCrossReferences().size());
		assertEquals(this.bookCls,
				this.bookStore_Books.eCrossReferences().get(0));
		assertEquals(this.bookCls,
				this.bookStore_Books.eCrossReferences().get(1));
	}

	@Test
	public void testEGet() {
		assertEquals(
				new BasicEList<EAnnotation>(),
				this.bookStore_Books
						.eGet(EcorePackage.Literals.EMODEL_ELEMENT__EANNOTATIONS));
		assertEquals("books",
				this.bookStore_Books
						.eGet(EcorePackage.Literals.ENAMED_ELEMENT__NAME));
		assertEquals(true,
				this.bookStore_Books
						.eGet(EcorePackage.Literals.ETYPED_ELEMENT__ORDERED));
		assertEquals(
				0,
				this.bookStore_Books
						.eGet(EcorePackage.Literals.ETYPED_ELEMENT__LOWER_BOUND));
		assertEquals(
				-1,
				this.bookStore_Books
						.eGet(EcorePackage.Literals.ETYPED_ELEMENT__UPPER_BOUND));
		assertEquals(true,
				this.bookStore_Books
						.eGet(EcorePackage.Literals.ETYPED_ELEMENT__MANY));
		assertEquals(false,
				this.bookStore_Books
						.eGet(EcorePackage.Literals.ETYPED_ELEMENT__REQUIRED));
		assertEquals(this.bookCls,
				this.bookStore_Books
						.eGet(EcorePackage.Literals.ETYPED_ELEMENT__ETYPE));
		assertEquals(
				true,
				this.bookStore_Books
						.eGet(EcorePackage.Literals.ESTRUCTURAL_FEATURE__CHANGEABLE));
		assertEquals(
				this.bookStoreCls,
				this.bookStore_Books
						.eGet(EcorePackage.Literals.ESTRUCTURAL_FEATURE__ECONTAINING_CLASS));
		assertEquals(true,
				this.bookStore_Books
						.eGet(EcorePackage.Literals.EREFERENCE__CONTAINMENT));
		assertEquals(false,
				this.bookStore_Books
						.eGet(EcorePackage.Literals.EREFERENCE__CONTAINER));
		assertEquals(null,
				this.bookStore_Books
						.eGet(EcorePackage.Literals.EREFERENCE__EOPPOSITE));
		assertEquals(
				this.bookCls,
				this.bookStore_Books
						.eGet(EcorePackage.Literals.EREFERENCE__EREFERENCE_TYPE));
	}

	@Test
	public void testEIsSet() {
		// default values
		assertEquals(
				false,
				this.bookStore_Books
						.eIsSet(EcorePackage.Literals.EMODEL_ELEMENT__EANNOTATIONS));
		assertEquals(true,
				this.bookStore_Books
						.eIsSet(EcorePackage.Literals.ENAMED_ELEMENT__NAME));
		assertEquals(false,
				this.bookStore_Books
						.eIsSet(EcorePackage.Literals.ETYPED_ELEMENT__ORDERED));
		assertEquals(
				false,
				this.bookStore_Books
						.eIsSet(EcorePackage.Literals.ETYPED_ELEMENT__LOWER_BOUND));
		assertEquals(
				true,
				this.bookStore_Books
						.eIsSet(EcorePackage.Literals.ETYPED_ELEMENT__UPPER_BOUND));
		assertEquals(true,
				this.bookStore_Books
						.eIsSet(EcorePackage.Literals.ETYPED_ELEMENT__MANY));
		assertEquals(false,
				this.bookStore_Books
						.eIsSet(EcorePackage.Literals.ETYPED_ELEMENT__REQUIRED));
		assertEquals(true,
				this.bookStore_Books
						.eIsSet(EcorePackage.Literals.ETYPED_ELEMENT__ETYPE));
		assertEquals(
				false,
				this.bookStore_Books
						.eIsSet(EcorePackage.Literals.ESTRUCTURAL_FEATURE__CHANGEABLE));
		assertEquals(
				true,
				this.bookStore_Books
						.eIsSet(EcorePackage.Literals.ESTRUCTURAL_FEATURE__ECONTAINING_CLASS));
		assertEquals(true,
				this.bookStore_Books
						.eIsSet(EcorePackage.Literals.EREFERENCE__CONTAINMENT));
		assertEquals(false,
				this.bookStore_Books
						.eIsSet(EcorePackage.Literals.EREFERENCE__CONTAINER));
		assertEquals(false,
				this.bookStore_Books
						.eIsSet(EcorePackage.Literals.EREFERENCE__EOPPOSITE));
		assertEquals(
				true,
				this.bookStore_Books
						.eIsSet(EcorePackage.Literals.EREFERENCE__EREFERENCE_TYPE));
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EReference
	// --------------------------------------------------------------------------

	@Test
	public void testIsContainment() {
		assertEquals(true, this.bookStore_Books.isContainment());
		assertEquals(false, this.bookStore_soldBooks.isContainment());
		assertEquals(false, this.sells_seller.isContainment());
	}

	@Test
	public void testIsContainer() {
		assertEquals(false, this.bookStore_Books.isContainer());
		assertEquals(false, this.bookStore_soldBooks.isContainer());
		assertEquals(false, this.sells_seller.isContainer());
	}

	@Test
	public void testGetEOpposite() {
		assertEquals(null, this.bookStore_Books.getEOpposite());
		assertEquals(this.bookStore_soldBooks, this.sells_seller.getEOpposite());
		assertEquals(this.sells_seller, this.bookStore_soldBooks.getEOpposite());
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.InternalEObject
	// --------------------------------------------------------------------------

	@Test
	public void testEURIFragmentSegment() {
		try {
			((InternalEObject) this.bookStore_Books).eURIFragmentSegment(
					this.bookStore_Books, this.bookStore_Books.eClass()
							.getEPackage().getEFactoryInstance());
			fail();
		} catch (NullPointerException e) {

		}
	}

	@Test
	public void testEContainerFeatureID() {
		assertEquals(EcorePackage.ESTRUCTURAL_FEATURE__ECONTAINING_CLASS,
				((InternalEObject) this.bookStore_Books).eContainerFeatureID());
	}

	@Test
	public void testEInternalContainer() {
		assertEquals(this.bookStoreCls,
				((InternalEObject) this.bookStore_Books).eInternalContainer());
	}

	@Test
	public void testEInternalResource() {
		assertEquals(schema,
				((InternalEObject) this.bookStore_Books).eInternalResource());
	}

	@Test
	public void testEDirectResource() {
		assertNull(((InternalEObject) this.bookStore_Books).eDirectResource());
	}
}

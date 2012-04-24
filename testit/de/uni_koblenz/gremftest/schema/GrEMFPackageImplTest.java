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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
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
import de.uni_koblenz.gremf.util.EObjectUtil;

public class GrEMFPackageImplTest {

	private EPackage bookStorePkg;
	private EPackage abstractPkg;
	private EClass bookStoreCls;
	private EClass bookCls;
	private EEnum categoryEnum;
	private EClass sellerCls;
	private EClass storeCls;

	private static Resource schema;

	@BeforeClass
	public static void setUpBeforeClass() {
		ResourceSet load_resourceSet = new ResourceSetImpl();

		load_resourceSet.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put("*", new GrEMFResourceFactoryImpl());

		schema = load_resourceSet.getResource(
				URI.createURI(System.getProperty("user.dir") + File.separator
						+ "models" + File.separator + "bookStore.gremf"), true);
	}

	@Before
	public void setUp() {
		EPackage rootPkg = (EPackage) schema.getContents().get(0);

		this.bookStorePkg = rootPkg.getESubpackages().get(0);
		this.abstractPkg = this.bookStorePkg.getESubpackages().get(0);

		this.bookStoreCls = (EClass) this.bookStorePkg
				.getEClassifier("BookStore");
		this.bookCls = (EClass) this.bookStorePkg.getEClassifier("Book");
		this.sellerCls = (EClass) this.abstractPkg.getEClassifier("Seller");
		this.storeCls = (EClass) this.abstractPkg.getEClassifier("Store");
		this.categoryEnum = (EEnum) this.bookStorePkg
				.getEClassifier("Category");

	}

	@After
	public void tearDown() {
		this.bookStorePkg = null;
		this.abstractPkg = null;

		this.bookStoreCls = null;
		this.bookCls = null;
		this.sellerCls = null;
		this.storeCls = null;
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.ENamedElement
	// --------------------------------------------------------------------------

	@Test
	public void testGetName() {
		assertEquals("bookstorepackage", this.bookStorePkg.getName());
		assertEquals("abstracttypespackage", this.abstractPkg.getName());
	}

	// setter: special grEMF behavior

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EObject
	// --------------------------------------------------------------------------

	@Test
	public void testEClass() {
		assertEquals(EcorePackage.Literals.EPACKAGE, this.bookStorePkg.eClass());
		assertEquals(EcorePackage.Literals.EPACKAGE, this.abstractPkg.eClass());
	}

	@Test
	public void testEResource() {
		assertEquals(schema, this.bookStorePkg.eResource());
		assertEquals(schema, this.abstractPkg.eResource());
	}

	@Test
	public void testEContainer() {
		assertEquals(this.bookStorePkg, this.abstractPkg.eContainer());
	}

	@Test
	public void testEContainingFeature() {
		assertEquals(EcorePackage.Literals.EPACKAGE__ESUBPACKAGES,
				this.abstractPkg.eContainingFeature());
	}

	@Test
	public void testEContainmentFeature() {
		assertEquals(EcorePackage.Literals.EPACKAGE__ESUBPACKAGES,
				this.abstractPkg.eContainmentFeature());
	}

	@Test
	public void testEContents() {
		assertEquals(7, this.bookStorePkg.eContents().size());
		assertTrue(this.bookStorePkg.eContents().contains(this.bookStoreCls));
		assertTrue(this.bookStorePkg.eContents().contains(this.bookCls));
		assertTrue(this.bookStorePkg.eContents().contains(this.categoryEnum));
		assertTrue(this.bookStorePkg.eContents().contains(this.abstractPkg));

		assertEquals(4, this.abstractPkg.eContents().size());
		assertTrue(this.abstractPkg.eContents().contains(this.storeCls));
		assertTrue(this.abstractPkg.eContents().contains(this.sellerCls));
	}

	@Test
	public void testEAllContents() {
		TreeIterator<EObject> expected = this.bookStorePkg.eAllContents();

		TreeIterator<EObject> allContents = EObjectUtil
				.eAllContents(this.bookStorePkg);

		while (expected.hasNext()) {
			assertTrue(allContents.hasNext());
			assertEquals(expected.next(), allContents.next());
		}
	}

	@Test
	public void testECrossReferences() {
		assertEquals(1, this.bookStorePkg.eCrossReferences().size());
		assertEquals(this.bookStorePkg.getEFactoryInstance(), this.bookStorePkg
				.eCrossReferences().get(0));

		assertEquals(1, this.abstractPkg.eCrossReferences().size());
		assertEquals(this.abstractPkg.getEFactoryInstance(), this.abstractPkg
				.eCrossReferences().get(0));
	}

	@Test
	public void testEReflective() {
		// null pointer exception behavior is the same for eGet, eSet, eIsSet
		// and eUnset
		try {
			this.abstractPkg
					.eUnset(EcorePackage.Literals.EPACKAGE__ESUPER_PACKAGE);
			fail();
		} catch (NullPointerException e) {

		}
	}

	// invoke: special grEMF behavior

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.ENotifier
	// --------------------------------------------------------------------------

	// special grEMF behavior

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EPackage
	// --------------------------------------------------------------------------

	@Test
	public void testGetNsURI() {
		assertEquals("http://de.uni_koblenz.gremf/bookstore",
				this.bookStorePkg.getNsURI());
		assertEquals("http://de.uni_koblenz.gremf/bookstore/abstracttypes",
				this.abstractPkg.getNsURI());
	}

	@Test
	public void testGetNsPrefix() {
		assertEquals("bookstore", this.bookStorePkg.getNsPrefix());
		assertEquals("abstracttypes", this.abstractPkg.getNsPrefix());
	}

	// setter: special grEMF behavior

	// getter eFactoryInstance: special grEMF behavior

	@Test
	public void testGetEClassifiers() {
		assertEquals(6, this.bookStorePkg.getEClassifiers().size());
		assertTrue(this.bookStorePkg.getEClassifiers().contains(
				this.bookStoreCls));
		assertTrue(this.bookStorePkg.getEClassifiers().contains(this.bookCls));
		assertTrue(this.bookStorePkg.getEClassifiers().contains(
				this.categoryEnum));

		assertEquals(4, this.abstractPkg.getEClassifiers().size());
		assertTrue(this.abstractPkg.getEClassifiers().contains(this.storeCls));
		assertTrue(this.abstractPkg.getEClassifiers().contains(this.sellerCls));
	}

	@Test
	public void testGetEClassifier() {
		assertEquals(this.bookStoreCls,
				this.bookStorePkg.getEClassifier("BookStore"));
		assertEquals(this.bookCls, this.bookStorePkg.getEClassifier("Book"));
		assertEquals(this.categoryEnum,
				this.bookStorePkg.getEClassifier("Category"));

		assertEquals(this.storeCls, this.abstractPkg.getEClassifier("Store"));
		assertEquals(this.sellerCls, this.abstractPkg.getEClassifier("Seller"));

		assertNull(this.bookStorePkg.getEClassifier("Store"));
		assertNull(this.abstractPkg.getEClassifier("BookStore"));
	}

	@Test
	public void testGetESubpackages() {
		assertEquals(1, this.bookStorePkg.getESubpackages().size());
		assertTrue(this.bookStorePkg.getESubpackages().contains(
				this.abstractPkg));

		assertEquals(0, this.abstractPkg.getESubpackages().size());
	}

	@Test
	public void testGetESuperPackage() {
		assertEquals(this.bookStorePkg, this.abstractPkg.getESuperPackage());
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EInternalObject
	// --------------------------------------------------------------------------

	// most methods: special grEMF behavior

	@Test
	public void testEContainerFeatureID() {
		assertEquals(EcorePackage.EPACKAGE__ESUPER_PACKAGE,
				((InternalEObject) this.abstractPkg).eContainerFeatureID());
	}

	@Test
	public void testEInternalResource() {
		assertEquals(schema,
				((InternalEObject) this.bookStorePkg).eInternalResource());
		assertEquals(schema,
				((InternalEObject) this.abstractPkg).eInternalResource());
	}

	@Test
	public void testEDirectResource() {
		assertNull(((InternalEObject) this.bookStorePkg).eDirectResource());
		assertNull(((InternalEObject) this.abstractPkg).eDirectResource());
	}
}

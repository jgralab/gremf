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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.ECrossReferenceEList;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_koblenz.gremf.resource.GrEMFResourceFactoryImpl;
import de.uni_koblenz.gremf.util.EObjectUtil;

public class GrEMFVertexClassImplTest {

	private EPackage bookStorePkg;
	private EPackage abstractPkg;
	private EClass bookStoreCls;
	private EClass bookCls;
	private EClass sellerCls;
	private EClass storeCls;
	private EAttribute bookStoreLocation;
	private EAttribute storeOwner;
	private EReference bookStore_Books;

	private EObject bookStoreObject;

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

		this.storeOwner = (EAttribute) this.storeCls
				.getEStructuralFeature("owner");
		this.bookStoreLocation = (EAttribute) this.bookStoreCls
				.getEStructuralFeature("location");
		this.bookStore_Books = (EReference) this.bookStoreCls
				.getEStructuralFeature("books");

		this.bookStoreObject = this.bookStorePkg.getEFactoryInstance().create(
				this.bookStoreCls);
		this.bookStoreObject.eSet(this.bookStoreLocation, "56070");
	}

	@After
	public void tearDown() {
		this.bookStorePkg = null;
		this.abstractPkg = null;

		this.bookStoreCls = null;
		this.bookCls = null;
		this.sellerCls = null;
		this.storeCls = null;

		this.storeOwner = null;
		this.bookStoreLocation = null;
		this.bookStore_Books = null;

		this.bookStoreObject = null;
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EClassifier
	// --------------------------------------------------------------------------

	@Test
	public void testGetEPackage() {
		assertEquals(this.bookStorePkg, this.bookStoreCls.getEPackage());
	}

	@Test
	public void testIsInstance() {
		assertTrue(this.bookStoreCls.isInstance(this.bookStoreObject));
		assertFalse(this.bookCls.isInstance(this.bookStoreObject));
	}

	@Test
	public void testGetDefaultValue() {
		assertNull(this.bookStoreCls.getDefaultValue());
	}

	// unsupported features: instance mapping, generics

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.ENamedElement
	// --------------------------------------------------------------------------

	@Test
	public void testGetName() {
		assertEquals("BookStore", this.bookStoreCls.getName());
	}

	// setter: unsupported schema change

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EObject
	// --------------------------------------------------------------------------
	@Test
	public void testEClass() {
		assertEquals(EcorePackage.Literals.ECLASS, this.bookStoreCls.eClass());
	}

	@Test
	public void testEResource() {
		assertEquals(schema, this.bookStoreCls.eResource());
	}

	@Test
	public void testEContainer() {
		assertEquals(this.bookStorePkg, this.bookStoreCls.eContainer());
		assertEquals(this.bookStorePkg, this.bookCls.eContainer());
	}

	@Test
	public void testEContainingFeature() {
		assertEquals(EcorePackage.Literals.EPACKAGE__ECLASSIFIERS,
				this.bookStoreCls.eContainingFeature());
		assertEquals(EcorePackage.Literals.EPACKAGE__ECLASSIFIERS,
				this.bookCls.eContainingFeature());
	}

	@Test
	public void testEContainmentFeature() {
		assertEquals(EcorePackage.Literals.EPACKAGE__ECLASSIFIERS,
				this.bookStoreCls.eContainmentFeature());
		assertEquals(EcorePackage.Literals.EPACKAGE__ECLASSIFIERS,
				this.bookCls.eContainmentFeature());
	}

	@Test
	public void testEContents() {
		// strange org.eclipse.emf.ecore.impl.EGenericTypeImpl
		EList<EObject> expected = this.bookStoreCls.eContents();

		EContentsEList<Object> contents = EContentsEList
				.createEContentsEList(this.bookStoreCls);

		for (Object o : contents) {
			assertTrue(expected.contains(o));
		}
	}

	@Test
	public void testEAllContents() {
		TreeIterator<EObject> expected = this.bookStoreCls.eAllContents();

		TreeIterator<EObject> allContents = EObjectUtil
				.eAllContents(this.bookStoreCls);

		while (expected.hasNext()) {
			assertTrue(allContents.hasNext());
			assertEquals(expected.next(), allContents.next());
		}
	}

	@Test
	public void testECrossReferences() {
		// strange org.eclipse.emf.ecore.impl.EGenericTypeImpl
		EList<EObject> expected = this.bookStoreCls.eCrossReferences();
		EList<EObject> refs = ECrossReferenceEList
				.createECrossReferenceEList(this.bookStoreCls);

		for (EObject o : refs) {
			assertTrue(expected.contains(o));
		}
	}

	@Test
	public void testEIsProxy() {
		assertFalse(this.bookStoreCls.eIsProxy());
	}

	@Test
	public void testEGet() {
		assertEquals(this.bookStoreLocation,
				this.bookStoreCls
						.eGet(EcorePackage.Literals.ECLASS__EID_ATTRIBUTE));

		try {
			this.bookCls.eGet(this.bookStoreLocation);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testEReflective() {
		// null pointer exception behavior is the same for eGet, eSet, eIsSet,
		// eUnset
		try {
			this.bookStoreCls
					.eUnset(EcorePackage.Literals.ECLASS__EALL_ATTRIBUTES);
			fail();
		} catch (NullPointerException e) {
		}
		try {
			this.bookStoreCls
					.eUnset(EcorePackage.Literals.ECLASS__EALL_CONTAINMENTS);
			fail();
		} catch (NullPointerException e) {
		}
		try {
			this.bookStoreCls
					.eUnset(EcorePackage.Literals.ECLASS__EALL_GENERIC_SUPER_TYPES);
			fail();
		} catch (NullPointerException e) {
		}
		try {
			this.bookStoreCls
					.eUnset(EcorePackage.Literals.ECLASS__EALL_OPERATIONS);
			fail();
		} catch (NullPointerException e) {
		}
		try {
			this.bookStoreCls
					.eUnset(EcorePackage.Literals.ECLASS__EALL_REFERENCES);
			fail();
		} catch (NullPointerException e) {
		}
		try {
			this.bookStoreCls
					.eUnset(EcorePackage.Literals.ECLASS__EALL_STRUCTURAL_FEATURES);
			fail();
		} catch (NullPointerException e) {
		}
		try {
			this.bookStoreCls
					.eUnset(EcorePackage.Literals.ECLASS__EALL_SUPER_TYPES);
			fail();
		} catch (NullPointerException e) {
		}
		try {
			this.bookStoreCls
					.eUnset(EcorePackage.Literals.ECLASS__EID_ATTRIBUTE);
			fail();
		} catch (NullPointerException e) {
		}
		try {
			this.bookStoreCls
					.eUnset(EcorePackage.Literals.ECLASSIFIER__EPACKAGE);
			fail();
		} catch (NullPointerException e) {
		}
	}

	// unsupported: operations

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.Notifier
	// --------------------------------------------------------------------------

	// grEMF specific behavior

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EClass
	// --------------------------------------------------------------------------

	@Test
	public void testIsAbstract() {
		// an abstract class
		assertTrue(this.storeCls.isAbstract());
		// grEMF specific: no distinction between abstract and interface
		assertTrue(this.sellerCls.isAbstract());
		// an normal class
		assertFalse(this.bookStoreCls.isAbstract());
	}

	@Test
	public void testIsInterface() {
		// an interface
		assertTrue(this.sellerCls.isInterface());
		// an abstract class
		assertFalse(this.storeCls.isInterface());
		// an normal class
		assertFalse(this.bookStoreCls.isInterface());
	}

	// setter: unsupported schema change

	@Test
	public void testGetESuperTypes() {
		assertEquals(1, this.bookStoreCls.getESuperTypes().size());
		assertTrue(this.bookStoreCls.getESuperTypes().contains(this.storeCls));
	}

	@Test
	public void testGetEAllSuperTypes() {
		assertEquals(2, this.bookStoreCls.getEAllSuperTypes().size());
		assertTrue(this.bookStoreCls.getEAllSuperTypes()
				.contains(this.storeCls));
		assertTrue(this.bookStoreCls.getEAllSuperTypes().contains(
				this.sellerCls));
	}

	@Test
	public void testIsSuperTypeOf() {
		// interface implementation
		assertTrue(this.sellerCls.isSuperTypeOf(this.storeCls));
		// class extension
		assertTrue(this.storeCls.isSuperTypeOf(this.bookStoreCls));
		// indirect
		assertTrue(this.sellerCls.isSuperTypeOf(this.bookStoreCls));

		assertFalse(this.sellerCls.isSuperTypeOf(this.bookCls));
	}

	@Test
	public void testGetEAttributes() {
		assertEquals(1, this.bookStoreCls.getEAttributes().size());
		assertTrue(this.bookStoreCls.getEAttributes().contains(
				this.bookStoreLocation));
	}

	@Test
	public void testGetEAllAttributes() {
		assertEquals(2, this.bookStoreCls.getEAllAttributes().size());
		assertTrue(this.bookStoreCls.getEAllAttributes().contains(
				this.bookStoreLocation));
		assertTrue(this.bookStoreCls.getEAllAttributes().contains(
				this.storeOwner));
	}

	@Test
	public void testGetEIDAttribute() {
		assertEquals(this.bookStoreLocation,
				this.bookStoreCls.getEIDAttribute());
	}

	@Test
	public void testGetEReferences() {
		assertEquals(3, this.bookStoreCls.getEReferences().size());
		assertTrue(this.bookStoreCls.getEReferences().contains(
				this.bookStore_Books));
	}

	@Test
	public void testGetEAllReferences() {
		assertEquals(5, this.bookStoreCls.getEAllReferences().size());
		assertTrue(this.bookStoreCls.getEAllReferences().contains(
				this.bookStore_Books));
	}

	@Test
	public void testGetEAllContainments() {
		assertEquals(1, this.bookStoreCls.getEAllContainments().size());
		assertTrue(this.bookStoreCls.getEAllContainments().contains(
				this.bookStore_Books));
	}

	@Test
	public void testGetEStructuralFeatures() {
		assertEquals(4, this.bookStoreCls.getEStructuralFeatures().size());
		assertTrue(this.bookStoreCls.getEStructuralFeatures().contains(
				this.bookStoreLocation));
		assertTrue(this.bookStoreCls.getEStructuralFeatures().contains(
				this.bookStore_Books));
	}

	@Test
	public void testGetEAllStructuralFeatures() {
		assertEquals(7, this.bookStoreCls.getEAllStructuralFeatures().size());
		assertTrue(this.bookStoreCls.getEAllStructuralFeatures().contains(
				this.bookStoreLocation));
		assertTrue(this.bookStoreCls.getEAllStructuralFeatures().contains(
				this.bookStore_Books));
		assertTrue(this.bookStoreCls.getEAllStructuralFeatures().contains(
				this.storeOwner));
	}

	@Test
	public void testGetEStructuralFeatureString() {
		// get an attribute
		assertEquals(this.bookStoreLocation,
				this.bookStoreCls.getEStructuralFeature(this.bookStoreLocation
						.getName()));
		// get a reference
		assertEquals(this.bookStore_Books,
				this.bookStoreCls.getEStructuralFeature(this.bookStore_Books
						.getName()));
		// get an superattribute
		assertEquals(this.storeOwner,
				this.bookStoreCls.getEStructuralFeature(this.storeOwner
						.getName()));
		// try a feature that is not contained
		assertNull(this.bookStoreCls.getEStructuralFeature("foo"));
	}

	@Test
	public void testGetFeatureCount() {
		// related to eAllStructuralFeatures
		assertEquals(7, this.bookStoreCls.getFeatureCount());
	}

	@Test
	public void testGetFeatureID() {
		// related to eAllStructuralFeatures
		assertEquals(0, this.bookStoreCls.getFeatureID(this.storeOwner));
		assertEquals(2, this.bookStoreCls.getFeatureID(this.bookStoreLocation));
		assertEquals(3, this.bookStoreCls.getFeatureID(this.bookStore_Books));

		assertEquals(-1, this.bookCls.getFeatureID(this.bookStoreLocation));
	}

	// unsupported: operations

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.InternalEObject
	// --------------------------------------------------------------------------

	// most methods: special grEMF behavior

	@Test
	public void testEContainerFeatureID() {
		assertEquals(
				EcorePackage.Literals.ECLASSIFIER__EPACKAGE.getFeatureID(),
				((InternalEObject) this.bookCls).eContainerFeatureID());
	}

	@Test
	public void testEInternalContainer() {
		assertEquals(this.bookStorePkg,
				((InternalEObject) this.bookCls).eInternalContainer());
	}

	@Test
	public void testEDirectResource() {
		assertNull(((InternalEObject) this.bookCls).eDirectResource());
	}

	@Test
	public void testEInternalResource() {
		assertEquals(schema,
				((InternalEObject) this.bookCls).eInternalResource());
	}

	@Test
	public void testEURIFragmentSegment() {
		assertEquals(this.bookStoreLocation.getName(),
				((InternalEObject) this.bookStoreCls).eURIFragmentSegment(null,
						this.bookStoreLocation));

		assertEquals(this.bookStoreLocation.getName(),
				((InternalEObject) this.bookCls).eURIFragmentSegment(null,
						this.bookStoreLocation));

		try {
			((InternalEObject) this.bookCls).eURIFragmentSegment(null,
					this.bookStorePkg.getEFactoryInstance());
			fail();
		} catch (NullPointerException e) {
		}

	}

	@Test
	public void testEObjectForURIFragmentSegment() {
		String uriFragment = ((InternalEObject) this.bookCls)
				.eURIFragmentSegment(null, this.bookStoreLocation);

		assertEquals(this.bookStoreLocation,
				((InternalEObject) this.bookStoreCls)
						.eObjectForURIFragmentSegment(uriFragment));

		assertNull(((InternalEObject) this.bookCls)
				.eObjectForURIFragmentSegment(uriFragment));

	}

	@Test
	public void testEProxyURI() {
		assertNull(((InternalEObject) this.bookStoreCls).eProxyURI());
	}
}

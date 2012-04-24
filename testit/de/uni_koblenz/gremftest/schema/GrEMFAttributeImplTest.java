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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
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
import de.uni_koblenz.gremftest.notifications.TestAdapter;

public class GrEMFAttributeImplTest {

	private EPackage bookStorePkg;
	private EPackage abstractPkg;
	private EClass bookStoreCls;
	private EClass bookCls;
	private EClass storeCls;
	private EEnum category;
	private EAttribute bookStoreLocation;
	private EAttribute storeOwner;
	private EAttribute bookISBN;
	private EAttribute bookAuthors;
	private EAttribute bookRatings;
	private EAttribute bookCategory;

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
		this.storeCls = (EClass) this.abstractPkg.getEClassifier("Store");
		this.category = (EEnum) this.bookStorePkg.getEClassifier("Category");

		this.storeOwner = (EAttribute) this.storeCls
				.getEStructuralFeature("owner");
		this.bookStoreLocation = (EAttribute) this.bookStoreCls
				.getEStructuralFeature("location");

		this.bookISBN = (EAttribute) this.bookCls.getEStructuralFeature("isbn");
		this.bookAuthors = (EAttribute) this.bookCls
				.getEStructuralFeature("authors");
		this.bookRatings = (EAttribute) this.bookCls
				.getEStructuralFeature("ratings");
		this.bookCategory = (EAttribute) this.bookCls
				.getEStructuralFeature("category");
	}

	@After
	public void tearDown() {
		this.bookStorePkg = null;
		this.abstractPkg = null;

		this.bookStoreCls = null;
		this.bookCls = null;
		this.storeCls = null;
		this.category = null;

		this.storeOwner = null;
		this.bookStoreLocation = null;

		this.bookISBN = null;
		this.bookAuthors = null;
		this.bookRatings = null;
		this.bookCategory = null;
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EStructuralFeature
	// --------------------------------------------------------------------------

	// boolean values: special grEMF behavior

	@Test
	public void testGetDefaultValueLiteral() {
		assertEquals(null, this.storeOwner.getDefaultValueLiteral());
		assertEquals("0000000000000", this.bookISBN.getDefaultValueLiteral());
	}

	@Test
	public void testGetDefaultValue() {
		assertEquals(null, this.storeOwner.getDefaultValue());
		assertEquals(0, this.bookISBN.getDefaultValue());
	}

	// setter: special grEMF behavior

	@Test
	public void testGetEContainingClass() {
		assertEquals(this.storeCls, this.storeOwner.getEContainingClass());
		assertEquals(this.bookCls, this.bookISBN.getEContainingClass());
	}

	@Test
	public void testGetFeatureID() {
		assertEquals(0, this.bookAuthors.getFeatureID());
		assertEquals(2, this.bookISBN.getFeatureID());

	}

	// unsupported: instance mapping

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.ETypedElement
	// --------------------------------------------------------------------------

	// setter: special grEMF behavior

	@Test
	public void testIsOrdered() {
		assertTrue(this.bookISBN.isOrdered());
		assertFalse(this.bookAuthors.isOrdered());
	}

	@Test
	public void testIsUnique() {
		assertTrue(this.bookAuthors.isUnique());
		assertTrue(this.bookISBN.isUnique());
		assertFalse(this.bookRatings.isUnique());
	}

	// lower bound: special grEMF behavior; always 0

	@Test
	public void testGetUpperBound() {
		assertEquals(EStructuralFeature.UNBOUNDED_MULTIPLICITY,
				this.bookAuthors.getUpperBound());
		assertEquals(1, this.bookISBN.getUpperBound());
	}

	@Test
	public void testIsMany() {
		assertTrue(this.bookAuthors.isMany());
		assertFalse(this.bookISBN.isMany());
	}

	@Test
	public void testGetEType() {
		assertEquals(EcorePackage.eINSTANCE.getEString(),
				this.bookAuthors.getEType());
		assertEquals(EcorePackage.eINSTANCE.getEInt(), this.bookISBN.getEType());
		assertEquals(this.category, this.bookCategory.getEType());
	}

	// unsupported: generics

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.ENamedElement
	// --------------------------------------------------------------------------

	@Test
	public void testGetName() {
		assertEquals("owner", this.storeOwner.getName());
		assertEquals("isbn", this.bookISBN.getName());
	}

	// setter: unsupported schema change d

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EObject
	// --------------------------------------------------------------------------

	@Test
	public void testEClass() {
		assertEquals(EcorePackage.Literals.EATTRIBUTE, this.storeOwner.eClass());
	}

	@Test
	public void testEResource() {
		assertEquals(schema, this.storeOwner.eResource());
	}

	@Test
	public void testEContainer() {
		assertEquals(this.storeCls, this.storeOwner.eContainer());
	}

	@Test
	public void testEContainingFeature() {
		assertEquals(EcorePackage.Literals.ECLASS__ESTRUCTURAL_FEATURES,
				this.storeOwner.eContainingFeature());
	}

	@Test
	public void testEContainmentFeature() {
		assertEquals(EcorePackage.Literals.ECLASS__ESTRUCTURAL_FEATURES,
				this.storeOwner.eContainmentFeature());
	}

	@Test
	public void testEContents() {
		// strange EGenericTypeImpl
		EList<Object> expected = EContentsEList
				.createEContentsEList(this.storeOwner);

		for (Object o : expected) {
			assertTrue(this.storeOwner.eContents().contains(o));
		}
	}

	@Test
	public void testEAllContents() {
		TreeIterator<EObject> expected = this.storeOwner.eAllContents();

		TreeIterator<EObject> allContents = EObjectUtil
				.eAllContents(this.storeOwner);

		while (expected.hasNext()) {
			assertTrue(allContents.hasNext());
			assertEquals(expected.next(), allContents.next());
		}
	}

	@Test
	public void testECrossReferences() {
		EList<Object> expected = ECrossReferenceEList
				.createECrossReferenceEList(this.storeOwner);

		for (Object o : expected) {
			assertTrue(this.storeOwner.eCrossReferences().contains(o));
		}
	}

	@Test
	public void testEReflective() {
		// null pointer exception behavior is the same for eGet, eSet, eIsSet
		// and eUnset
		try {
			this.storeOwner
					.eUnset(EcorePackage.Literals.ESTRUCTURAL_FEATURE__ECONTAINING_CLASS);
			fail();
		} catch (NullPointerException e) {
		}
		try {
			this.storeOwner.eUnset(EcorePackage.Literals.ETYPED_ELEMENT__MANY);
			fail();
		} catch (NullPointerException e) {
		}
		try {
			this.storeOwner
					.eUnset(EcorePackage.Literals.ETYPED_ELEMENT__REQUIRED);
			fail();
		} catch (NullPointerException e) {
		}
	}

	// unsupported: proxies, operations

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EAttribute
	// --------------------------------------------------------------------------

	@Test
	public void testIsID() {
		assertTrue(this.bookStoreLocation.isID());
		assertFalse(this.storeOwner.isID());
	}

	// setter: unsupported schema change

	@Test
	public void testGetEAttributeType() {
		assertEquals(EcorePackage.eINSTANCE.getEString(),
				this.bookAuthors.getEAttributeType());
		assertEquals(EcorePackage.eINSTANCE.getEInt(),
				this.bookISBN.getEAttributeType());
		assertEquals(this.category, this.bookCategory.getEAttributeType());
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.Notifier
	// --------------------------------------------------------------------------

	// special grEMF behavior

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.InternalEObject
	// --------------------------------------------------------------------------

	// most methods: special grEMF behavior

	@Test
	public void testEURIFragmentSegment() {
		assertEquals(this.bookStoreLocation.getName(),
				((InternalEObject) this.bookISBN).eURIFragmentSegment(null,
						this.bookStoreLocation));

		assertEquals(this.bookStoreLocation.getName(),
				((InternalEObject) this.bookISBN).eURIFragmentSegment(null,
						this.bookStoreLocation));

		try {
			((InternalEObject) this.bookISBN).eURIFragmentSegment(null,
					this.bookStorePkg.getEFactoryInstance());
			fail();
		} catch (NullPointerException e) {
		}
	}

	@Test
	public void testEObjectForURIFragmentSegment() {
		String uriFragment = ((InternalEObject) this.bookISBN)
				.eURIFragmentSegment(null, this.bookStoreLocation);

		assertNull(((InternalEObject) this.bookISBN)
				.eObjectForURIFragmentSegment(uriFragment));
	}

	@Test
	public void testEContainerFeatureID() {
		assertEquals(EcorePackage.ESTRUCTURAL_FEATURE__ECONTAINING_CLASS,
				((InternalEObject) this.bookISBN).eContainerFeatureID());
	}

	@Test
	public void testEInternalContainer() {
		assertEquals(this.bookCls,
				((InternalEObject) this.bookISBN).eInternalContainer());
	}

	@Test
	public void testEInternalResource() {
		assertEquals(schema,
				((InternalEObject) this.bookISBN).eInternalResource());
	}

	@Test
	public void testEDirectResource() {
		assertNull(((InternalEObject) this.bookISBN).eDirectResource());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void test() {
		EPackage pack = EcoreFactory.eINSTANCE.createEPackage();
		pack.setName("pack1");

		EClass eclass1 = EcoreFactory.eINSTANCE.createEClass();
		eclass1.setName("EClass1");
		pack.getEClassifiers().add(eclass1);

		EClass eclass2 = EcoreFactory.eINSTANCE.createEClass();
		eclass2.setName("EClass2");
		pack.getEClassifiers().add(eclass2);

		EClass eclass3 = EcoreFactory.eINSTANCE.createEClass();
		eclass3.setName("Eclass3");
		pack.getEClassifiers().add(eclass3);

		eclass1.getESuperTypes().add(eclass2);

		EAttribute eat1 = EcoreFactory.eINSTANCE.createEAttribute();
		eclass2.getEStructuralFeatures().add(eat1);

		EAttribute eat = EcoreFactory.eINSTANCE.createEAttribute();

		eat.setEType(EcorePackage.eINSTANCE.getEString());

		eclass1.getEStructuralFeatures().add(eat);

		System.out.println(eat.getEContainingClass());
		System.out.println(eat.getContainerClass());
		System.out.println("container: " + eat.eContainer());
		System.out.println(eat.eContainingFeature());
		System.out.println(eat.eContainmentFeature());
		System.out.println(eat.eContents());

		eat.setUnique(true);

		eat.setUpperBound(-1);

		EReference ref3to1 = EcoreFactory.eINSTANCE.createEReference();
		eclass3.getEStructuralFeatures().add(ref3to1);
		ref3to1.setName("myRef");
		ref3to1.setUpperBound(-1);
		// ref.setUnique(true);
		ref3to1.setEType(eclass1);

		EReference ref1to2 = EcoreFactory.eINSTANCE.createEReference();
		eclass1.getEStructuralFeatures().add(ref1to2);
		ref1to2.setName("nextRef");
		ref1to2.setUpperBound(-1);
		ref1to2.setEType(eclass2);

		EReference ref2to1 = EcoreFactory.eINSTANCE.createEReference();
		eclass2.getEStructuralFeatures().add(ref2to1);
		ref2to1.setName("nextRefOPP");
		ref2to1.setUpperBound(-1);
		ref2to1.setEType(eclass1);
		ref1to2.setEOpposite(ref2to1);

		// ---
		System.err.println("Instances:  --------------------------------");
		EObject ob1 = pack.getEFactoryInstance().create(eclass1);
		Collection<String> od = new ArrayList<String>();
		od.add("hugo");
		od.add("helga");
		od.add("hugo");
		ob1.eSet(eat, od);

		System.out.println(ob1.eGet(eat));
		System.out.println(ob1.eGet(eat).getClass());

		System.out.println(eclass1.getEAllAttributes().getClass());
		System.out.println(eclass1.getEAllAttributes().get(0));
		System.out.println(eclass1.getEAllAttributes().get(1));

		System.out.println(EcorePackage.eINSTANCE.getEClass());
		System.out.println(eclass1.eClass());

		System.out.println(EcorePackage.eINSTANCE.getEAttribute());
		System.out.println(eat.eClass());

		EObject ob3 = pack.getEFactoryInstance().create(eclass3);

		ob1.eAdapters().add(new TestAdapter());
		ob3.eAdapters().add(new TestAdapter());

		ArrayList<EObject> l = new ArrayList<EObject>();
		l.add(ob1);
		ob3.eSet(ref3to1, l);
		((List) ob3.eGet(ref3to1)).add(ob1);
		((List) ob3.eGet(ref3to1)).add(ob1);

		System.out.println(ob3.eGet(ref3to1).getClass() + " "
				+ ((List) ob3.eGet(ref3to1)).size());

		System.out.println(ref3to1.getEKeys());
		System.out.println(((InternalEObject) ob1).eContainerFeatureID());

		EObject ob2 = pack.getEFactoryInstance().create(eclass2);

		ArrayList<EObject> list2 = new ArrayList<EObject>();
		list2.add(ob2);
		ob1.eSet(ref1to2, list2);

		System.out.println(ob3.eCrossReferences());

		System.out.println(((InternalEObject) ob3).eURIFragmentSegment(ref3to1,
				ob1));
		System.out.println(((InternalEObject) ob1).eProxyURI());

	}
}

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
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.schema.EdgeClass;

public class GrEMFEdgeClassImplTest {

	private EPackage bookStorePkg;
	private EPackage abstractPkg;
	private EClass bookCls;
	private EClass sellsCls;
	private EClass tradesCls;
	private EAttribute sellsPrice;

	private Graph graph;
	private EObject sellsObject;

	private static Resource schema, instance;

	@BeforeClass
	public static void setUpBeforeClass() {
		ResourceSet load_resourceSet = new ResourceSetImpl();

		load_resourceSet.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put("*", new GrEMFResourceFactoryImpl());

		schema = load_resourceSet.getResource(
				URI.createURI(System.getProperty("user.dir") + File.separator
						+ "models" + File.separator + "bookStore.gremf"), true);

		instance = load_resourceSet
				.getResource(
						URI.createURI(System.getProperty("user.dir")
								+ File.separator + "models" + File.separator
								+ "model1.bookStore"), true);
	}

	@Before
	public void setUp() {
		EPackage rootPkg = (EPackage) schema.getContents().get(0);

		this.bookStorePkg = rootPkg.getESubpackages().get(0);
		this.abstractPkg = this.bookStorePkg.getESubpackages().get(0);

		this.bookCls = (EClass) this.bookStorePkg.getEClassifier("Book");
		this.sellsCls = (EClass) this.bookStorePkg.getEClassifier("Sells");
		this.tradesCls = (EClass) this.abstractPkg.getEClassifier("Trades");

		this.sellsPrice = (EAttribute) this.sellsCls
				.getEStructuralFeature("price");

		this.graph = (Graph) instance.getContents().get(0);
		this.sellsObject = (EObject) this.graph
				.edges((EdgeClass) this.sellsCls).iterator().next();
	}

	@After
	public void tearDown() {
		this.bookStorePkg = null;
		this.abstractPkg = null;

		this.bookCls = null;
		this.sellsCls = null;

		this.sellsPrice = null;

		this.sellsObject = null;
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EClassifier
	// --------------------------------------------------------------------------

	@Test
	public void testGetEPackage() {
		assertEquals(this.bookStorePkg, this.sellsCls.getEPackage());
	}

	@Test
	public void testIsInstance() {
		assertTrue(this.sellsCls.isInstance(this.sellsObject));
		assertFalse(this.bookCls.isInstance(this.sellsObject));
	}

	@Test
	public void testGetClassifierID() {
		System.out.println(this.sellsCls.getClassifierID());
	}

	@Test
	public void testGetDefaultValue() {
		assertNull(this.sellsCls.getDefaultValue());
	}

	// unsupported features: instance mapping, generics

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.ENamedElement
	// --------------------------------------------------------------------------

	@Test
	public void testGetName() {
		assertEquals("Sells", this.sellsCls.getName());
	}

	// setter: unsupported schema change

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EObject
	// --------------------------------------------------------------------------
	@Test
	public void testEClass() {
		assertEquals(EcorePackage.Literals.ECLASS, this.sellsCls.eClass());
	}

	@Test
	public void testEResource() {
		assertEquals(schema, this.sellsCls.eResource());
	}

	@Test
	public void testEContainer() {
		assertEquals(this.bookStorePkg, this.sellsCls.eContainer());
	}

	@Test
	public void testEContainingFeature() {
		assertEquals(EcorePackage.Literals.EPACKAGE__ECLASSIFIERS,
				this.sellsCls.eContainingFeature());
	}

	@Test
	public void testEContainmentFeature() {
		assertEquals(EcorePackage.Literals.EPACKAGE__ECLASSIFIERS,
				this.sellsCls.eContainmentFeature());
	}

	@Test
	public void testEContents() {
		// strange org.eclipse.emf.ecore.impl.EGenericTypeImpl in eContents
		EList<EObject> expected = this.sellsCls.eContents();

		EContentsEList<Object> contents = EContentsEList
				.createEContentsEList(this.sellsCls);

		for (Object o : contents) {
			assertTrue(expected.contains(o));
		}
	}

	@Test
	public void testEAllContents() {
		TreeIterator<EObject> expected = this.sellsCls.eAllContents();

		TreeIterator<EObject> allContents = EObjectUtil
				.eAllContents(this.sellsCls);

		while (expected.hasNext()) {
			assertTrue(allContents.hasNext());
			assertEquals(expected.next(), allContents.next());
		}
	}

	@Test
	public void testECrossReferences() {
		// strange org.eclipse.emf.ecore.impl.EGenericTypeImpl
		EList<EObject> expected = this.sellsCls.eCrossReferences();
		EList<EObject> implemented = ECrossReferenceEList
				.createECrossReferenceEList(this.sellsCls);

		for (EObject o : expected) {
			assertTrue(implemented.contains(o));
		}
	}

	@Test
	public void testEIsProxy() {
		assertFalse(this.sellsCls.eIsProxy());
	}

	@Test
	public void testEGet() {
		try {
			this.sellsCls.eGet(this.sellsPrice);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testEReflective() {
		// null pointer exception behavior is the same for eGet, eSet, eIsSet
		// and eUnset
		try {
			this.sellsCls.eUnset(EcorePackage.Literals.ECLASS__EALL_ATTRIBUTES);
			fail();
		} catch (NullPointerException e) {
		}
		try {
			this.sellsCls
					.eUnset(EcorePackage.Literals.ECLASS__EALL_CONTAINMENTS);
			fail();
		} catch (NullPointerException e) {
		}
		try {
			this.sellsCls
					.eUnset(EcorePackage.Literals.ECLASS__EALL_GENERIC_SUPER_TYPES);
			fail();
		} catch (NullPointerException e) {
		}
		try {
			this.sellsCls.eUnset(EcorePackage.Literals.ECLASS__EALL_OPERATIONS);
			fail();
		} catch (NullPointerException e) {
		}
		try {
			this.sellsCls.eUnset(EcorePackage.Literals.ECLASS__EALL_REFERENCES);
			fail();
		} catch (NullPointerException e) {
		}
		try {
			this.sellsCls
					.eUnset(EcorePackage.Literals.ECLASS__EALL_STRUCTURAL_FEATURES);
			fail();
		} catch (NullPointerException e) {
		}
		try {
			this.sellsCls
					.eUnset(EcorePackage.Literals.ECLASS__EALL_SUPER_TYPES);
			fail();
		} catch (NullPointerException e) {
		}
		try {
			this.sellsCls.eUnset(EcorePackage.Literals.ECLASS__EID_ATTRIBUTE);
			fail();
		} catch (NullPointerException e) {
		}
		try {
			this.sellsCls.eUnset(EcorePackage.Literals.ECLASSIFIER__EPACKAGE);
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
		assertFalse(this.sellsCls.isAbstract());
	}

	@Test
	public void testIsInterface() {
		assertFalse(this.sellsCls.isInterface());
	}

	// setter: unsupported schema change

	@Test
	public void testGetESuperTypes() {
		assertEquals(1, this.sellsCls.getESuperTypes().size());
		assertTrue(this.sellsCls.getESuperTypes().contains(this.tradesCls));
	}

	@Test
	public void testGetEAllSuperTypes() {
		assertEquals(1, this.sellsCls.getEAllSuperTypes().size());
		assertTrue(this.sellsCls.getEAllSuperTypes().contains(this.tradesCls));
	}

	@Test
	public void testIsSuperTypeOf() {
		assertTrue(this.tradesCls.isSuperTypeOf(this.sellsCls));
	}

	@Test
	public void testGetEAttributes() {
		assertEquals(1, this.sellsCls.getEAttributes().size());
		assertTrue(this.sellsCls.getEAttributes().contains(this.sellsPrice));
	}

	@Test
	public void testGetEAllAttributes() {
		assertEquals(1, this.sellsCls.getEAllAttributes().size());
		assertTrue(this.sellsCls.getEAllAttributes().contains(this.sellsPrice));
	}

	@Test
	public void testGetEReferences() {
		assertEquals(2, this.sellsCls.getEReferences().size());
	}

	@Test
	public void testGetEAllReferences() {
		assertEquals(4, this.sellsCls.getEAllReferences().size());
	}

	@Test
	public void testGetEAllContainments() {
		assertEquals(0, this.sellsCls.getEAllContainments().size());
	}

	@Test
	public void testGetEStructuralFeatures() {
		assertEquals(3, this.sellsCls.getEStructuralFeatures().size());
	}

	@Test
	public void testGetEAllStructuralFeatures() {
		assertEquals(5, this.sellsCls.getEAllStructuralFeatures().size());
	}

	@Test
	public void testGetEStructuralFeatureString() {
		// get an attribute
		assertEquals(this.sellsPrice,
				this.sellsCls.getEStructuralFeature(this.sellsPrice.getName()));
		// try a feature that is not contained
		assertNull(this.sellsCls.getEStructuralFeature("foo"));
	}

	@Test
	public void testGetFeatureCount() {
		// related to eAllStructuralFeatures
		System.out.println(this.sellsCls.getFeatureCount());
	}

	@Test
	public void testGetFeatureID() {
		// related to eAllStructuralFeatures
		assertEquals(2, this.sellsCls.getFeatureID(this.sellsPrice));
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
				((InternalEObject) this.sellsCls).eContainerFeatureID());
	}

	@Test
	public void testEInternalContainer() {
		assertEquals(this.bookStorePkg,
				((InternalEObject) this.sellsCls).eInternalContainer());
	}

	@Test
	public void testEDirectResource() {
		assertNull(((InternalEObject) this.sellsCls).eDirectResource());
	}

	@Test
	public void testEInternalResource() {
		assertEquals(schema,
				((InternalEObject) this.sellsCls).eInternalResource());
	}

	@Test
	public void testEURIFragmentSegment() {
		assertEquals(this.sellsPrice.getName(),
				((InternalEObject) this.sellsCls).eURIFragmentSegment(null,
						this.sellsPrice));

		try {
			((InternalEObject) this.sellsCls).eURIFragmentSegment(null,
					this.abstractPkg.getEFactoryInstance());
			fail();
		} catch (NullPointerException e) {
		}

	}

	@Test
	public void testEObjectForURIFragmentSegment() {
		String uriFragment = ((InternalEObject) this.sellsCls)
				.eURIFragmentSegment(null, this.sellsPrice);

		assertEquals(this.sellsPrice,
				((InternalEObject) this.sellsCls)
						.eObjectForURIFragmentSegment(uriFragment));

		assertNull(((InternalEObject) this.bookCls)
				.eObjectForURIFragmentSegment(uriFragment));

	}

	@Test
	public void testEProxyURI() {
		assertNull(((InternalEObject) this.sellsCls).eProxyURI());
	}
}

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
package de.uni_koblenz.gremftest.emftoolbox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_koblenz.gremf.impl.GrEMFEdgeImpl;
import de.uni_koblenz.gremf.impl.GrEMFGraphImpl;
import de.uni_koblenz.gremf.impl.GrEMFVertexImpl;
import de.uni_koblenz.gremf.resource.GrEMFResourceFactoryImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFIncidenceClassImpl;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.AggregationKind;

public class EcoreUtilTest {

	private ResourceSet rs;

	private Resource r;
	private GrEMFGraphImpl g;

	private EList<GrEMFVertexImpl> vertices;
	private EList<GrEMFVertexImpl> aggregatedVertices;
	private EList<GrEMFVertexImpl> linkedVertices;
	private EList<GrEMFEdgeImpl> edges;
	private GrEMFEdgeImpl aggregationEdge;
	private GrEMFEdgeImpl linkEdge;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		// init resource set and resources
		this.rs = new ResourceSetImpl();
		this.rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new GrEMFResourceFactoryImpl());

		this.rs.getResource(
				URI.createURI(System.getProperty("user.dir") + File.separator
						+ "models" + File.separator + "bookStore.gremf"), true);
		this.r = this.rs
				.getResource(
						URI.createURI(System.getProperty("user.dir")
								+ File.separator + "models" + File.separator
								+ "model1.bookStore"), true);

		// get graph
		this.g = ((GrEMFGraphImpl) this.r.getContents().get(0));
		Iterable<Edge> edges = (Iterable<Edge>) this.g.eGet(this.g.eClass()
				.getEStructuralFeature("edges"));
		Iterable<Vertex> vertices = (Iterable<Vertex>) this.g.eGet(this.g
				.eClass().getEStructuralFeature("vertices"));

		this.vertices = new BasicEList<GrEMFVertexImpl>();
		for (Vertex v : vertices) {
			this.vertices.add((GrEMFVertexImpl) v);
		}

		this.edges = new BasicEList<GrEMFEdgeImpl>();
		for (Edge e : edges) {
			this.edges.add((GrEMFEdgeImpl) e);
		}

		this.linkedVertices = new BasicEList<GrEMFVertexImpl>(2);
		for (Edge e : edges) {
			if (e.getAggregationKind() == AggregationKind.NONE) {
				if (e.getAlpha().compareTo(e.getOmega()) <= 0) {
					this.linkedVertices.add((GrEMFVertexImpl) e.getAlpha());
					this.linkedVertices.add((GrEMFVertexImpl) e.getOmega());
				} else {
					this.linkedVertices.add((GrEMFVertexImpl) e.getOmega());
					this.linkedVertices.add((GrEMFVertexImpl) e.getAlpha());
				}
				this.linkEdge = (GrEMFEdgeImpl) e;
				break;
			}
		}

		this.aggregatedVertices = new BasicEList<GrEMFVertexImpl>(2);
		for (Edge e : edges) {
			if ((e.getAggregationKind() == AggregationKind.COMPOSITE)) {
				this.aggregatedVertices.add((GrEMFVertexImpl) e.getAlpha());
				this.aggregatedVertices.add((GrEMFVertexImpl) e.getOmega());
				this.aggregationEdge = (GrEMFEdgeImpl) e;
				break;
			}
		}

		System.out.println(this.vertices);
		System.out.println(this.edges);
		System.out.println(this.linkedVertices);
		System.out.println(this.linkEdge);
		System.out.println(this.aggregatedVertices);
		System.out.println(this.aggregationEdge);
	}

	@After
	public void tearDown() {
		this.r = null;
		this.g = null;
		this.vertices = null;
		this.aggregatedVertices = null;
		this.linkedVertices = null;
		this.edges = null;
		this.aggregationEdge = null;
		this.linkEdge = null;
	}

	@Test
	public void testResolveEObjectResourceSet() {
		assertEquals(this.linkEdge, EcoreUtil.resolve(this.linkEdge, this.rs));
		assertEquals(this.aggregationEdge,
				EcoreUtil.resolve(this.aggregationEdge, this.rs));

		assertEquals(this.linkedVertices.get(0),
				EcoreUtil.resolve(this.linkedVertices.get(0), this.rs));
		assertEquals(this.aggregatedVertices.get(0),
				EcoreUtil.resolve(this.aggregatedVertices.get(0), this.rs));
	}

	@Test
	public void testResolveEObjectResource() {
		assertEquals(this.linkEdge, EcoreUtil.resolve(this.linkEdge, this.r));
		assertEquals(this.aggregationEdge,
				EcoreUtil.resolve(this.aggregationEdge, this.r));

		assertEquals(this.linkedVertices.get(0),
				EcoreUtil.resolve(this.linkedVertices.get(0), this.r));
		assertEquals(this.aggregatedVertices.get(0),
				EcoreUtil.resolve(this.aggregatedVertices.get(0), this.r));
	}

	@Test
	public void testResolveEObjectEObject() {
		assertEquals(this.linkEdge, EcoreUtil.resolve(this.linkEdge, this.r));
		assertEquals(this.aggregationEdge,
				EcoreUtil.resolve(this.aggregationEdge, this.r));

		assertEquals(this.linkedVertices.get(0),
				EcoreUtil.resolve(this.linkedVertices.get(0), this.r));
		assertEquals(this.aggregatedVertices.get(0),
				EcoreUtil.resolve(this.aggregatedVertices.get(0), this.r));
	}

	@Test
	public void testResolveAll() {
		// callable
		EcoreUtil.resolveAll(this.rs);
		EcoreUtil.resolveAll(this.r);
		EcoreUtil.resolveAll(this.linkedVertices.get(0));
		EcoreUtil.resolveAll(this.linkEdge);
	}

	@Test
	public void testCopy() {
		GrEMFVertexImpl copyVertex = EcoreUtil.copy(this.aggregatedVertices
				.get(0));
		assertEquals(
				this.aggregatedVertices.get(0).getAttributedElementClass(),
				copyVertex.getAttributedElementClass());

		assertFalse(this.aggregatedVertices.get(0).equals(copyVertex));
		assertTrue(this.aggregatedVertices.get(0).compareTo(copyVertex) < 0);

		// composition references are not copied
		for (Edge e : this.aggregatedVertices.get(0).incidences()) {
			assertTrue((e.getAggregationKind() == AggregationKind.COMPOSITE)
					|| e.isNormal());
		}
		assertFalse(copyVertex.incidences().iterator().hasNext());
		assertNull(copyVertex.eContainer());

		GrEMFVertexImpl copy2Vertex = EcoreUtil
				.copy(this.linkedVertices.get(0));
		assertEquals(this.linkedVertices.get(0).getAttributedElementClass(),
				copy2Vertex.getAttributedElementClass());

		assertFalse(this.linkedVertices.get(0).equals(copy2Vertex));
		assertTrue(this.linkedVertices.get(0).compareTo(copy2Vertex) < 0);

		assertEquals(this.linkedVertices.get(0).incidences().iterator()
				.hasNext(), copy2Vertex.incidences().iterator().hasNext());
		assertNull(copy2Vertex.eContainer());
	}

	@Test
	public void testGetRootContainer() {
		assertEquals(
				EcoreUtil.getRootContainer(this.aggregatedVertices.get(0)),
				EcoreUtil.getRootContainer(this.aggregatedVertices.get(1)));
	}

	@Test
	public void testIsAncestorResourceEObject() {
		assertTrue(EcoreUtil.isAncestor(this.aggregatedVertices.get(0),
				this.aggregatedVertices.get(1)));
		assertTrue(EcoreUtil.isAncestor(this.r, this.aggregatedVertices.get(0)));
		assertTrue(EcoreUtil
				.isAncestor(this.rs, this.aggregatedVertices.get(0)));
	}

	@Test
	public void testFilterDescendants() {
		List<EObject> containment = new ArrayList<EObject>(2);
		containment.add(this.aggregatedVertices.get(0));
		containment.add(this.aggregatedVertices.get(1));

		assertEquals(1, EcoreUtil.filterDescendants(containment).size());
		assertTrue(EcoreUtil.filterDescendants(containment).contains(
				this.aggregatedVertices.get(0)));

		List<EObject> link = new ArrayList<EObject>(2);
		link.add(this.linkedVertices.get(0));
		link.add(this.linkedVertices.get(1));

		assertEquals(1, EcoreUtil.filterDescendants(link).size());
		assertTrue(EcoreUtil.filterDescendants(link).contains(
				this.linkedVertices.get(0)));
	}

	@Test
	public void testGetAllContents() {
		TreeIterator<EObject> i = EcoreUtil.getAllContents(this.vertices);
		while (i.hasNext()) {
			System.out.println(i.next());
		}
		System.out.println("-----");
		i = EcoreUtil.getAllContents(this.linkedVertices.get(0), true);
		while (i.hasNext()) {
			System.out.println(i.next());
		}
		System.out.println("-----");
		i = EcoreUtil.getAllContents(this.r, true);
		while (i.hasNext()) {
			System.out.println(i.next());
		}
		System.out.println("-----");
		i = EcoreUtil.getAllContents(this.rs, true);
		while (i.hasNext()) {
			System.out.println(i.next());
		}
	}

	@Test
	public void testGetAllProperContents() {
		TreeIterator<EObject> i = EcoreUtil.getAllProperContents(this.vertices,
				true);
		while (i.hasNext()) {
			System.out.println(i.next());
		}
		System.out.println("-----");
		i = EcoreUtil.getAllProperContents(this.linkedVertices.get(0), true);
		while (i.hasNext()) {
			System.out.println(i.next());
		}
		System.out.println("-----");
		i = EcoreUtil.getAllProperContents(this.r, true);
		while (i.hasNext()) {
			System.out.println(i.next());
		}
		System.out.println("-----");
		i = EcoreUtil.getAllProperContents(this.rs, true);
		while (i.hasNext()) {
			System.out.println(i.next());
		}
	}

	@Test
	public void testGetIdentification() {
		// callable
		EcoreUtil.getIdentification(this.linkedVertices.get(0));
	}

	@Test
	public void testGetURI() {
		// callable
		EcoreUtil.getURI(this.linkedVertices.get(0));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testIndexOf() {
		assertEquals(0,
				EcoreUtil.indexOf(this.vertices, this.linkedVertices.get(0), 0));
		assertEquals(-1,
				EcoreUtil.indexOf(this.vertices, this.linkedVertices.get(0), 1));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSetEList() {
		int size = this.vertices.size();
		List<GrEMFVertexImpl> prototype = new ArrayList<GrEMFVertexImpl>(size);
		for (int i = size - 1; i >= 0; i--) {
			prototype.add(this.vertices.get(i));
		}
		EcoreUtil.setEList(this.vertices, prototype);
		assertTrue(this.vertices.equals(prototype));
	}

	@Test
	public void testRemoveSettingObject() {
		GrEMFVertexImpl bookStore = this.linkedVertices.get(0);
		EAttribute attr = (EAttribute) bookStore.eClass()
				.getEStructuralFeature(0);
		Object value = bookStore.eGet(attr);
		EcoreUtil.remove(bookStore.eSetting(attr), value);
		assertNull(bookStore.eGet(attr));

		EReference ref = (EReference) bookStore.eClass().getEStructuralFeature(
				2);
		value = ((List<?>) bookStore.eGet(ref)).get(0);
		int size = ((List<?>) bookStore.eGet(ref)).size();
		EcoreUtil.remove(bookStore.eSetting(ref), value);
		assertEquals(size - 1, ((List<?>) bookStore.eGet(ref)).size());
		assertFalse(((List<?>) bookStore.eGet(ref)).contains(value));
	}

	@Test
	public void testReplaceSettingObjectObject() {
		GrEMFVertexImpl bookStore = this.linkedVertices.get(0);
		EAttribute attr = (EAttribute) bookStore.eClass()
				.getEStructuralFeature(0);
		Object newValue = "foo";
		Object value = bookStore.eGet(attr);
		EcoreUtil.replace(bookStore.eSetting(attr), value, newValue);
		assertEquals(newValue, bookStore.eGet(attr));

		EReference ref = (EReference) bookStore.eClass().getEStructuralFeature(
				2);
		newValue = EcoreUtil.copy(this.linkedVertices.get(1));
		value = ((List<?>) bookStore.eGet(ref)).get(0);
		int size = ((List<?>) bookStore.eGet(ref)).size();
		EcoreUtil.replace(bookStore.eSetting(ref), value, newValue);
		assertEquals(size, ((List<?>) bookStore.eGet(ref)).size());
		assertFalse(((List<?>) bookStore.eGet(ref)).contains(value));
		assertTrue(((List<?>) bookStore.eGet(ref)).contains(newValue));
	}

	@Test
	public void testReplaceEObjectEStructuralFeatureObjectObject() {
		EStructuralFeature feature = this.aggregatedVertices.get(1).eClass()
				.getEAllAttributes().get(0);

		Object oldValue = this.aggregatedVertices.get(1).eGet(feature);
		Object newValue = "N.N.";

		EcoreUtil.replace(this.aggregatedVertices.get(1), feature, oldValue,
				newValue);

		assertEquals(newValue, this.aggregatedVertices.get(1).eGet(feature));
	}

	@Test
	public void testReplaceEObjectEObject() {
		EcoreUtil.replace(this.linkedVertices.get(0),
				EcoreUtil.copy(this.linkedVertices.get(0)));
	}

	@Test
	public void testRemoveEObjectEStructuralFeatureObject() {
		EcoreUtil.remove(this.aggregatedVertices.get(0),
				(GrEMFIncidenceClassImpl) this.aggregationEdge
						.getAttributedElementClass().getTo(),
				this.aggregatedVertices.get(1));

		for (Edge e : this.aggregatedVertices.get(0).incidences()) {
			if (e.equals(this.aggregationEdge)) {
				fail();
			}
		}
	}

	@Test
	public void testRemoveEObject() {
		try {
			EcoreUtil.remove(this.aggregatedVertices.get(1));
			fail();
		} catch (UnsupportedOperationException e) {
		}
		try {
			EcoreUtil.remove(this.aggregationEdge);
			fail();
		} catch (UnsupportedOperationException e) {
		}
		EcoreUtil.remove(this.linkEdge);
		assertNull(this.linkEdge.eContainer());
	}

	@Test
	public void testDeleteEObject() {
		EcoreUtil.delete(this.aggregatedVertices.get(1));

		for (Vertex v : this.g.vertices()) {
			if (v.equals(this.aggregatedVertices.get(1))) {
				fail();
			}
		}
		for (Edge e : this.g.edges()) {
			if (e.equals(this.aggregationEdge)) {
				fail();
			}
		}
	}

	@Test
	public void testCreate() {
		GrEMFVertexImpl bookStore = this.linkedVertices.get(0);
		EAttribute attr = (EAttribute) bookStore.eClass()
				.getEStructuralFeature(0);
		EObject eObj = EcoreUtil.create(bookStore.eClass());
		eObj.eSet(attr, "foo");
		assertEquals("foo", eObj.eGet(attr));
	}

	@Test
	public void testGetID() {
		// callable: has an ID attribute
		System.out.println(EcoreUtil.getID(this.linkedVertices.get(0)));

		// callable: has not an ID attribute
		System.out.println(EcoreUtil.getID(this.linkedVertices.get(1)));
	}

	@Test
	public void testDocumentation() {
		// vertex class
		EcoreUtil.setDocumentation(this.aggregatedVertices.get(1).eClass(),
				"This is a vertex class");
		assertEquals("This is a vertex class",
				EcoreUtil.getDocumentation(this.aggregatedVertices.get(1)
						.eClass()));

		// edge class
		EcoreUtil.setDocumentation(this.aggregationEdge.eClass(),
				"This is a edge class");
		assertEquals("This is a edge class",
				EcoreUtil.getDocumentation(this.aggregationEdge.eClass()));
	}

	@Test
	public void testConstraints() {
		List<String> constraints = new ArrayList<String>(2);
		constraints.add("constraints");
		constraints.add("can be added");

		// vertex class
		EcoreUtil.setConstraints(this.aggregatedVertices.get(1).eClass(),
				constraints);
		assertTrue(EcoreUtil.getConstraints(
				this.aggregatedVertices.get(1).eClass())
				.contains("constraints"));
		assertTrue(EcoreUtil.getConstraints(
				this.aggregatedVertices.get(1).eClass()).contains("can"));
		assertTrue(EcoreUtil.getConstraints(
				this.aggregatedVertices.get(1).eClass()).contains("be"));
		assertTrue(EcoreUtil.getConstraints(
				this.aggregatedVertices.get(1).eClass()).contains("added"));

		// edge class
		EcoreUtil.setConstraints(this.aggregationEdge.eClass(), constraints);
		assertTrue(EcoreUtil.getConstraints(this.aggregationEdge.eClass())
				.contains("constraints"));
		assertTrue(EcoreUtil.getConstraints(this.aggregationEdge.eClass())
				.contains("can"));
		assertTrue(EcoreUtil.getConstraints(this.aggregationEdge.eClass())
				.contains("be"));
		assertTrue(EcoreUtil.getConstraints(this.aggregationEdge.eClass())
				.contains("added"));
	}

	@Test
	public void testGenerateUUID() {
		// callable
		EcoreUtil.generateUUID();
	}

	@Test
	public void testFreeze() {
		// callable: catched cast error
		EcoreUtil.freeze(this.aggregatedVertices.get(1).eClass().getEPackage());
	}

	@Test
	public void testComputeDiagnostic() {
		// callable
		EcoreUtil.computeDiagnostic(this.r, true);
	}

}

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
package de.uni_koblenz.gremftest.collection;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.junit.Before;
import org.junit.Test;

import de.uni_koblenz.gremf.GrEMFIO;
import de.uni_koblenz.gremf.impl.GrEMFVertexImpl;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;

public class GrEMFIncidenceListProxyTest {

	private EPackage pack;
	private EClass eclass1;
	private EClass eclass2;
	private EReference ref1to2;
	private EReference ref2to1;

	private EObject ob1;

	static int variant = 1;

	@Before
	public void setUp() throws GraphIOException {
		if (variant == 0) {
			this.pack = EcoreFactory.eINSTANCE.createEPackage();
			this.pack.setName("testPackage");
			this.pack.setNsPrefix("test");
			this.pack.setNsURI("org.test");

			this.eclass1 = EcoreFactory.eINSTANCE.createEClass();
			this.eclass1.setName("EClass1");
			this.pack.getEClassifiers().add(this.eclass1);

			this.eclass2 = EcoreFactory.eINSTANCE.createEClass();
			this.eclass2.setName("EClass2");
			this.pack.getEClassifiers().add(this.eclass2);

			this.ref1to2 = EcoreFactory.eINSTANCE.createEReference();
			this.ref1to2.setName("eclass2");
			this.ref1to2.setUpperBound(-1);
			this.ref1to2.setEType(this.eclass2);
			this.ref1to2.setUnique(false);
			this.eclass1.getEStructuralFeatures().add(this.ref1to2);

			this.ref2to1 = EcoreFactory.eINSTANCE.createEReference();
			this.ref2to1.setName("eclass1");
			this.ref2to1.setUpperBound(-1);
			this.ref2to1.setEType(this.eclass1);
			this.ref2to1.setUnique(false);
			this.eclass2.getEStructuralFeatures().add(this.ref2to1);

			this.ref1to2.setEOpposite(this.ref2to1);
			this.ref2to1.setEOpposite(this.ref1to2);

			// ----

			this.ob1 = this.pack.getEFactoryInstance().create(this.eclass1);

		} else {
			Schema schema = GrEMFIO.loadSchemaFromFile(System
					.getProperty("user.dir")
					+ File.separator
					+ "models"
					+ File.separator + "grEMFListTestSchema.tg");
			Graph g = schema.createGraph(ImplementationType.GENERIC);
			this.pack = (EPackage) schema.getDefaultPackage();
			this.eclass1 = (EClass) schema.getGraphClass().getVertexClass(
					"EClass1");
			this.eclass2 = (EClass) schema.getGraphClass().getVertexClass(
					"EClass2");
			this.ref1to2 = (EReference) schema.getGraphClass()
					.getEdgeClass("EdgeClass1").getTo();
			this.ref2to1 = (EReference) schema.getGraphClass()
					.getEdgeClass("EdgeClass1").getFrom();

			GrEMFVertexImpl vertex = g.createVertex((VertexClass) this.eclass1);
			this.ob1 = vertex;
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSize() {
		assertEquals(0, ((List<EObject>) this.ob1.eGet(this.ref1to2)).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAdd() {
		int i = ((List<EObject>) this.ob1.eGet(this.ref1to2)).size();
		((List<EObject>) this.ob1.eGet(this.ref1to2)).add(this.pack
				.getEFactoryInstance().create(this.eclass2));
		assertEquals((i + 1),
				((List<EObject>) this.ob1.eGet(this.ref1to2)).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddAll() {
		int i = ((List<EObject>) this.ob1.eGet(this.ref1to2)).size();
		ArrayList<EObject> list = new ArrayList<EObject>();
		list.add(this.pack.getEFactoryInstance().create(this.eclass2));
		list.add(this.pack.getEFactoryInstance().create(this.eclass2));
		list.add(this.pack.getEFactoryInstance().create(this.eclass2));
		list.add(this.pack.getEFactoryInstance().create(this.eclass2));
		((List<EObject>) this.ob1.eGet(this.ref1to2)).addAll(list);
		assertEquals(i + list.size(),
				((List<EObject>) this.ob1.eGet(this.ref1to2)).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddIndex() {
		ArrayList<EObject> list = new ArrayList<EObject>();
		list.add(this.pack.getEFactoryInstance().create(this.eclass2));
		EObject item1 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item1);
		list.add(this.pack.getEFactoryInstance().create(this.eclass2));
		list.add(this.pack.getEFactoryInstance().create(this.eclass2));
		((List<EObject>) this.ob1.eGet(this.ref1to2)).addAll(list);

		assertEquals(4, ((List<EObject>) this.ob1.eGet(this.ref1to2)).size());
		assertEquals(item1,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).get(1));

		EObject itemNew = this.pack.getEFactoryInstance().create(this.eclass2);
		((List<EObject>) this.ob1.eGet(this.ref1to2)).add(1, itemNew);

		assertEquals(itemNew,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).get(1));
		assertEquals(item1,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).get(2));
		assertEquals(5, ((List<EObject>) this.ob1.eGet(this.ref1to2)).size());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddAllIndex() {
		ArrayList<EObject> list = new ArrayList<EObject>();
		list.add(this.pack.getEFactoryInstance().create(this.eclass2));
		list.add(this.pack.getEFactoryInstance().create(this.eclass2));
		EObject item2 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item2);
		list.add(this.pack.getEFactoryInstance().create(this.eclass2));
		((List<EObject>) this.ob1.eGet(this.ref1to2)).addAll(list);

		ArrayList<EObject> listToInsert = new ArrayList<EObject>();
		EObject itemNew2 = this.pack.getEFactoryInstance().create(this.eclass2);
		listToInsert.add(itemNew2);
		listToInsert.add(this.pack.getEFactoryInstance().create(this.eclass2));
		listToInsert.add(this.pack.getEFactoryInstance().create(this.eclass2));

		((List<EObject>) this.ob1.eGet(this.ref1to2)).addAll(2, listToInsert);

		assertEquals(itemNew2,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).get(2));
		assertEquals(item2,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).get(5));
		assertEquals(7, ((List<EObject>) this.ob1.eGet(this.ref1to2)).size());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testClear() {
		ArrayList<EObject> list = new ArrayList<EObject>();
		list.add(this.pack.getEFactoryInstance().create(this.eclass2));
		list.add(this.pack.getEFactoryInstance().create(this.eclass2));
		list.add(this.pack.getEFactoryInstance().create(this.eclass2));
		list.add(this.pack.getEFactoryInstance().create(this.eclass2));
		((List<EObject>) this.ob1.eGet(this.ref1to2)).addAll(list);

		((List<EObject>) this.ob1.eGet(this.ref1to2)).clear();

		assertEquals(0, ((List<EObject>) this.ob1.eGet(this.ref1to2)).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testContains() {
		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject item0 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item0);
		EObject item1 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item1);
		EObject item2 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item2);
		EObject item3 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item3);
		((List<EObject>) this.ob1.eGet(this.ref1to2)).addAll(list);

		assertEquals(true,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).contains(item0));
		assertEquals(true,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).contains(item1));
		assertEquals(true,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).contains(item3));
		assertEquals(false,
				((List<EObject>) this.ob1.eGet(this.ref1to2))
						.contains(this.pack.getEFactoryInstance().create(
								this.eclass2)));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testContainsAll() {
		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject item0 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item0);
		EObject item1 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item1);
		EObject item2 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item2);
		EObject item3 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item3);
		((List<EObject>) this.ob1.eGet(this.ref1to2)).addAll(list);

		ArrayList<EObject> testList1 = new ArrayList<EObject>();
		testList1.add(item0);
		testList1.add(item2);

		assertEquals(true,
				((List<EObject>) this.ob1.eGet(this.ref1to2))
						.containsAll(testList1));

		ArrayList<EObject> testList2 = new ArrayList<EObject>();
		testList2.add(item0);
		testList2.add(item2);
		testList2.add(this.pack.getEFactoryInstance().create(this.eclass2));

		assertEquals(false,
				((List<EObject>) this.ob1.eGet(this.ref1to2))
						.containsAll(testList2));

		ArrayList<EObject> testList3 = new ArrayList<EObject>();
		testList3.add(this.pack.getEFactoryInstance().create(this.eclass2));

		assertEquals(false,
				((List<EObject>) this.ob1.eGet(this.ref1to2))
						.containsAll(testList3));

		ArrayList<EObject> testList4 = new ArrayList<EObject>();

		assertEquals(true,
				((List<EObject>) this.ob1.eGet(this.ref1to2))
						.containsAll(testList4));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGet() {
		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject item0 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item0);
		EObject item1 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item1);
		EObject item2 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item2);
		EObject item3 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item3);
		((List<EObject>) this.ob1.eGet(this.ref1to2)).addAll(list);

		assertEquals(item0,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).get(0));
		assertEquals(item1,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).get(1));
		assertEquals(item2,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).get(2));
		assertEquals(item3,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).get(3));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testIndexOf() {
		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject item0 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item0);
		EObject item1 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item1);
		EObject item2 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item2);
		EObject item3 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item3);
		((List<EObject>) this.ob1.eGet(this.ref1to2)).addAll(list);

		assertEquals(0,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).indexOf(item0));
		assertEquals(1,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).indexOf(item1));
		assertEquals(2,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).indexOf(item2));
		assertEquals(3,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).indexOf(item3));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testIsEmpty() {
		assertEquals(true,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).isEmpty());

		((List<EObject>) this.ob1.eGet(this.ref1to2)).add(this.pack
				.getEFactoryInstance().create(this.eclass2));

		assertEquals(false,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).isEmpty());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testIterator() {
		assertEquals(false,
				(((List<EObject>) this.ob1.eGet(this.ref1to2)).iterator()
						.hasNext()));

		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject item0 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item0);
		EObject item1 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item1);
		EObject item2 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item2);
		EObject item3 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item3);
		((List<EObject>) this.ob1.eGet(this.ref1to2)).addAll(list);

		Iterator<?> it = ((List<EObject>) this.ob1.eGet(this.ref1to2))
				.iterator();

		assertEquals(item0, it.next());
		assertEquals(item1, it.next());
		assertEquals(item2, it.next());
		assertEquals(item3, it.next());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testLastIndexOf() {
		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject item0 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item0);
		EObject item1 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item1);
		EObject item2 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item2);
		EObject item3 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item3);
		list.add(item1);
		list.add(item2);
		((List<EObject>) this.ob1.eGet(this.ref1to2)).addAll(list);

		assertEquals(4,
				((List<EObject>) this.ob1.eGet(this.ref1to2))
						.lastIndexOf(item1));
		assertEquals(5,
				((List<EObject>) this.ob1.eGet(this.ref1to2))
						.lastIndexOf(item2));
		assertEquals(0,
				((List<EObject>) this.ob1.eGet(this.ref1to2))
						.lastIndexOf(item0));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testListIterator() {
		assertEquals(false,
				(((List<EObject>) this.ob1.eGet(this.ref1to2)).listIterator()
						.hasNext()));

		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject item0 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item0);
		EObject item1 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item1);
		EObject item2 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item2);
		EObject item3 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item3);
		((List<EObject>) this.ob1.eGet(this.ref1to2)).addAll(list);

		Iterator<?> it = ((List<EObject>) this.ob1.eGet(this.ref1to2))
				.listIterator();

		assertEquals(item0, it.next());
		assertEquals(item1, it.next());
		assertEquals(item2, it.next());
		assertEquals(item3, it.next());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testListIteratorIndex() {
		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject item0 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item0);
		EObject item1 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item1);
		EObject item2 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item2);
		EObject item3 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item3);
		((List<EObject>) this.ob1.eGet(this.ref1to2)).addAll(list);

		Iterator<?> it = ((List<EObject>) this.ob1.eGet(this.ref1to2))
				.listIterator(2);

		assertEquals(item2, it.next());
		assertEquals(item3, it.next());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRemove() {
		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject item0 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item0);
		EObject item1 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item1);
		EObject item2 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item2);
		EObject item3 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item3);
		((List<EObject>) this.ob1.eGet(this.ref1to2)).addAll(list);

		((List<EObject>) this.ob1.eGet(this.ref1to2)).remove(item2);

		assertEquals(false,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).contains(item2));
		assertEquals(3, ((List<EObject>) this.ob1.eGet(this.ref1to2)).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRemoveIndex() {
		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject item0 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item0);
		EObject item1 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item1);
		EObject item2 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item2);
		EObject item3 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item3);
		((List<EObject>) this.ob1.eGet(this.ref1to2)).addAll(list);

		((List<EObject>) this.ob1.eGet(this.ref1to2)).remove(2);

		assertEquals(false,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).contains(item2));
		assertEquals(3, ((List<EObject>) this.ob1.eGet(this.ref1to2)).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRemoveAll() {
		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject item0 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item0);
		EObject item1 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item1);
		EObject item2 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item2);
		EObject item3 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item3);
		((List<EObject>) this.ob1.eGet(this.ref1to2)).addAll(list);

		ArrayList<EObject> listToRemove = new ArrayList<EObject>();
		listToRemove.add(item0);
		listToRemove.add(item2);

		((List<EObject>) this.ob1.eGet(this.ref1to2)).removeAll(listToRemove);

		assertEquals(0,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).indexOf(item1));

		assertEquals(2, ((List<EObject>) this.ob1.eGet(this.ref1to2)).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRetainAll() {
		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject item0 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item0);
		EObject item1 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item1);
		EObject item2 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item2);
		EObject item3 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item3);
		((List<EObject>) this.ob1.eGet(this.ref1to2)).addAll(list);

		ArrayList<EObject> listToRetain = new ArrayList<EObject>();
		listToRetain.add(item0);
		listToRetain.add(item2);
		((List<EObject>) this.ob1.eGet(this.ref1to2)).retainAll(listToRetain);

		assertEquals(1,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).indexOf(item2));

		assertEquals(2, ((List<EObject>) this.ob1.eGet(this.ref1to2)).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSetIndex() {
		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject item0 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item0);
		EObject item1 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item1);
		EObject item2 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item2);
		EObject item3 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item3);
		((List<EObject>) this.ob1.eGet(this.ref1to2)).addAll(list);

		EObject itemReplace = this.pack.getEFactoryInstance().create(
				this.eclass2);
		((List<EObject>) this.ob1.eGet(this.ref1to2)).set(1, itemReplace);

		assertEquals(itemReplace,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).get(1));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSubList() {
		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject item0 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item0);
		EObject item1 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item1);
		EObject item2 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item2);
		EObject item3 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item3);
		EObject item4 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item4);
		EObject item5 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item5);
		((List<EObject>) this.ob1.eGet(this.ref1to2)).addAll(list);

		List<EObject> sublist = ((List<EObject>) this.ob1.eGet(this.ref1to2))
				.subList(1, 4);

		assertEquals(3, sublist.size());
		assertEquals(item1, sublist.get(0));
		assertEquals(item3, sublist.get(2));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testToArray() {
		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject item0 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item0);
		EObject item1 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item1);
		EObject item2 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item2);
		EObject item3 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item3);
		EObject item4 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item4);
		EObject item5 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item5);
		((List<EObject>) this.ob1.eGet(this.ref1to2)).addAll(list);

		Object[] array = ((List<EObject>) this.ob1.eGet(this.ref1to2))
				.toArray();
		assertEquals(6, array.length);
		assertEquals(item0, array[0]);
		assertEquals(item3, array[3]);
		assertEquals(item5, array[5]);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMove() {
		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject item0 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item0);
		EObject item1 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item1);
		EObject item2 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item2);
		EObject item3 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item3);
		EObject item4 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item4);
		EObject item5 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item5);
		((List<EObject>) this.ob1.eGet(this.ref1to2)).addAll(list);

		((EList<EObject>) this.ob1.eGet(this.ref1to2)).move(0, item5);

		assertEquals(item0,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).get(1));
		assertEquals(item5,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).get(0));
		assertEquals(item4,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).get(5));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMoveIndex() {
		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject item0 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item0);
		EObject item1 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item1);
		EObject item2 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item2);
		EObject item3 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item3);
		EObject item4 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item4);
		EObject item5 = this.pack.getEFactoryInstance().create(this.eclass2);
		list.add(item5);
		((List<EObject>) this.ob1.eGet(this.ref1to2)).addAll(list);

		((EList<EObject>) this.ob1.eGet(this.ref1to2)).move(0, 5);

		assertEquals(item0,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).get(1));
		assertEquals(item5,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).get(0));
		assertEquals(item4,
				((List<EObject>) this.ob1.eGet(this.ref1to2)).get(5));
	}

}

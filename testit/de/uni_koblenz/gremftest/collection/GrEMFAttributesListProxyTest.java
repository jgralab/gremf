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
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.Before;
import org.junit.Test;

import de.uni_koblenz.gremf.GrEMFIO;
import de.uni_koblenz.gremf.impl.GrEMFVertexImpl;
import de.uni_koblenz.gremftest.notifications.TestAdapter;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;

public class GrEMFAttributesListProxyTest {

	private EPackage pack;
	private EClass eclass;
	private EAttribute attribute;
	private EObject ob;

	static int variant = 1;

	@Before
	public void setUp() throws GraphIOException {

		if (variant == 0) {

			this.pack = EcoreFactory.eINSTANCE.createEPackage();
			this.pack.setName("testPackage");
			this.pack.setNsPrefix("test");
			this.pack.setNsURI("org.test");

			this.eclass = EcoreFactory.eINSTANCE.createEClass();
			this.eclass.setName("EClass1");
			this.pack.getEClassifiers().add(this.eclass);

			this.attribute = EcoreFactory.eINSTANCE.createEAttribute();
			this.attribute.setName("testAttribute");
			this.attribute.setEType(EcorePackage.eINSTANCE.getEString());
			this.attribute.setUpperBound(-1);
			this.attribute.setUnique(false);
			this.eclass.getEStructuralFeatures().add(this.attribute);

			this.ob = this.pack.getEFactoryInstance().create(this.eclass);
			this.ob.eAdapters().add(new TestAdapter());

		} else {
			Schema schema = GrEMFIO.loadSchemaFromFile(System
					.getProperty("user.dir")
					+ File.separator
					+ "models"
					+ File.separator + "attributeListTestSchema.tg");
			Graph g = schema.createGraph(ImplementationType.GENERIC);
			this.pack = (EPackage) schema.getDefaultPackage();
			this.eclass = (EClass) schema.getGraphClass().getVertexClass(
					"EClass1");
			this.attribute = (EAttribute) this.eclass
					.getEStructuralFeature("testAttribute");
			GrEMFVertexImpl vertex = g.createVertex((VertexClass) this.eclass);
			this.ob = vertex;
			ArrayList<String> list = new ArrayList<String>();
			list.add("Anna");
			list.add("Ben");
			list.add("Connie");
			list.add("David");

		}

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSize() {
		assertEquals(0, ((List<String>) this.ob.eGet(this.attribute)).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAdd() {
		int i = ((List<String>) this.ob.eGet(this.attribute)).size();
		((List<String>) this.ob.eGet(this.attribute)).add("Erwin");
		assertEquals((i + 1),
				((List<String>) this.ob.eGet(this.attribute)).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddAll() {
		int i = ((List<String>) this.ob.eGet(this.attribute)).size();
		ArrayList<String> list = new ArrayList<String>();
		list.add("Anna");
		list.add("Ben");
		list.add("Connie");
		list.add("David");
		((List<String>) this.ob.eGet(this.attribute)).addAll(list);
		assertEquals(i + list.size(),
				((List<String>) this.ob.eGet(this.attribute)).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddIndex() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Anna");
		list.add("Ben");
		list.add("Connie");
		list.add("David");
		((List<String>) this.ob.eGet(this.attribute)).addAll(list);

		((List<String>) this.ob.eGet(this.attribute)).add(1, "Erwin");

		assertEquals("Erwin",
				((List<String>) this.ob.eGet(this.attribute)).get(1));
		assertEquals("Ben",
				((List<String>) this.ob.eGet(this.attribute)).get(2));
		assertEquals(5, ((List<String>) this.ob.eGet(this.attribute)).size());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddAllIndex() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Anna");
		list.add("Ben");
		list.add("Connie");
		list.add("David");
		((List<String>) this.ob.eGet(this.attribute)).addAll(list);

		ArrayList<String> listToInsert = new ArrayList<String>();
		listToInsert.add("Erwin");
		listToInsert.add("Frank");
		listToInsert.add("Gerd");

		((List<String>) this.ob.eGet(this.attribute)).addAll(2, listToInsert);

		assertEquals("Erwin",
				((List<String>) this.ob.eGet(this.attribute)).get(2));
		assertEquals("Connie",
				((List<String>) this.ob.eGet(this.attribute)).get(5));
		assertEquals(7, ((List<String>) this.ob.eGet(this.attribute)).size());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testClear() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Anna");
		list.add("Ben");
		list.add("Connie");
		list.add("David");
		((List<String>) this.ob.eGet(this.attribute)).addAll(list);

		((List<String>) this.ob.eGet(this.attribute)).clear();

		assertEquals(0, ((List<String>) this.ob.eGet(this.attribute)).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testContains() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Anna");
		list.add("Ben");
		list.add("Connie");
		list.add("David");
		((List<String>) this.ob.eGet(this.attribute)).addAll(list);

		assertEquals(true,
				((List<String>) this.ob.eGet(this.attribute)).contains("Anna"));
		assertEquals(true,
				((List<String>) this.ob.eGet(this.attribute)).contains("Ben"));
		assertEquals(true,
				((List<String>) this.ob.eGet(this.attribute)).contains("David"));
		assertEquals(false,
				((List<String>) this.ob.eGet(this.attribute)).contains("Erwin"));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testContainsAll() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Anna");
		list.add("Ben");
		list.add("Connie");
		list.add("David");
		((List<String>) this.ob.eGet(this.attribute)).addAll(list);

		ArrayList<String> testList1 = new ArrayList<String>();
		testList1.add("Anna");
		testList1.add("Connie");

		assertEquals(true,
				((List<String>) this.ob.eGet(this.attribute))
						.containsAll(testList1));

		ArrayList<String> testList2 = new ArrayList<String>();
		testList2.add("Anna");
		testList2.add("Connie");
		testList2.add("Erwin");

		assertEquals(false,
				((List<String>) this.ob.eGet(this.attribute))
						.containsAll(testList2));

		ArrayList<String> testList3 = new ArrayList<String>();
		testList3.add("Erwin");

		assertEquals(false,
				((List<String>) this.ob.eGet(this.attribute))
						.containsAll(testList3));

		ArrayList<String> testList4 = new ArrayList<String>();

		assertEquals(true,
				((List<String>) this.ob.eGet(this.attribute))
						.containsAll(testList4));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGet() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Anna");
		list.add("Ben");
		list.add("Connie");
		list.add("David");
		((List<String>) this.ob.eGet(this.attribute)).addAll(list);

		assertEquals("Anna",
				((List<String>) this.ob.eGet(this.attribute)).get(0));
		assertEquals("Ben",
				((List<String>) this.ob.eGet(this.attribute)).get(1));
		assertEquals("Connie",
				((List<String>) this.ob.eGet(this.attribute)).get(2));
		assertEquals("David",
				((List<String>) this.ob.eGet(this.attribute)).get(3));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testIndexOf() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Anna");
		list.add("Ben");
		list.add("Connie");
		list.add("David");
		((List<String>) this.ob.eGet(this.attribute)).addAll(list);

		assertEquals(0,
				((List<String>) this.ob.eGet(this.attribute)).indexOf("Anna"));
		assertEquals(1,
				((List<String>) this.ob.eGet(this.attribute)).indexOf("Ben"));
		assertEquals(2,
				((List<String>) this.ob.eGet(this.attribute)).indexOf("Connie"));
		assertEquals(3,
				((List<String>) this.ob.eGet(this.attribute)).indexOf("David"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testIsEmpty() {
		assertEquals(true,
				((List<String>) this.ob.eGet(this.attribute)).isEmpty());

		((List<String>) this.ob.eGet(this.attribute)).add("Anna");

		assertEquals(false,
				((List<String>) this.ob.eGet(this.attribute)).isEmpty());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testIterator() {
		assertEquals(false,
				(((List<String>) this.ob.eGet(this.attribute)).iterator()
						.hasNext()));

		ArrayList<String> list = new ArrayList<String>();
		list.add("Anna");
		list.add("Ben");
		list.add("Connie");
		list.add("David");
		((List<String>) this.ob.eGet(this.attribute)).addAll(list);

		Iterator<?> it = ((List<String>) this.ob.eGet(this.attribute))
				.iterator();

		assertEquals("Anna", it.next());
		assertEquals("Ben", it.next());
		assertEquals("Connie", it.next());
		assertEquals("David", it.next());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testLastIndexOf() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Anna");
		list.add("Ben");
		list.add("Connie");
		list.add("David");
		list.add("Ben");
		list.add("Connie");
		((List<String>) this.ob.eGet(this.attribute)).addAll(list);

		assertEquals(4,
				((List<String>) this.ob.eGet(this.attribute))
						.lastIndexOf("Ben"));
		assertEquals(5,
				((List<String>) this.ob.eGet(this.attribute))
						.lastIndexOf("Connie"));
		assertEquals(0,
				((List<String>) this.ob.eGet(this.attribute))
						.lastIndexOf("Anna"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testListIterator() {
		assertEquals(false,
				(((List<String>) this.ob.eGet(this.attribute)).listIterator()
						.hasNext()));

		ArrayList<String> list = new ArrayList<String>();
		list.add("Anna");
		list.add("Ben");
		list.add("Connie");
		list.add("David");
		((List<String>) this.ob.eGet(this.attribute)).addAll(list);

		Iterator<?> it = ((List<String>) this.ob.eGet(this.attribute))
				.listIterator();

		assertEquals("Anna", it.next());
		assertEquals("Ben", it.next());
		assertEquals("Connie", it.next());
		assertEquals("David", it.next());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testListIteratorIndex() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Anna");
		list.add("Ben");
		list.add("Connie");
		list.add("David");
		((List<String>) this.ob.eGet(this.attribute)).addAll(list);

		Iterator<?> it = ((List<String>) this.ob.eGet(this.attribute))
				.listIterator(2);

		assertEquals("Connie", it.next());
		assertEquals("David", it.next());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRemove() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Anna");
		list.add("Ben");
		list.add("Connie");
		list.add("David");
		((List<String>) this.ob.eGet(this.attribute)).addAll(list);

		((List<String>) this.ob.eGet(this.attribute)).remove("Connie");

		assertEquals(false,
				((List<String>) this.ob.eGet(this.attribute))
						.contains("Connie"));
		assertEquals(3, ((List<String>) this.ob.eGet(this.attribute)).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRemoveIndex() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Anna");
		list.add("Ben");
		list.add("Connie");
		list.add("David");
		((List<String>) this.ob.eGet(this.attribute)).addAll(list);

		((List<String>) this.ob.eGet(this.attribute)).remove(2);

		assertEquals(false,
				((List<String>) this.ob.eGet(this.attribute))
						.contains("Connie"));
		assertEquals(3, ((List<String>) this.ob.eGet(this.attribute)).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRemoveAll() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Anna");
		list.add("Ben");
		list.add("Connie");
		list.add("David");
		((List<String>) this.ob.eGet(this.attribute)).addAll(list);

		ArrayList<String> listToRemove = new ArrayList<String>();
		listToRemove.add("Anna");
		listToRemove.add("Connie");

		((List<String>) this.ob.eGet(this.attribute)).removeAll(listToRemove);

		assertEquals(0,
				((List<String>) this.ob.eGet(this.attribute)).indexOf("Ben"));

		assertEquals(2, ((List<String>) this.ob.eGet(this.attribute)).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRetainAll() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Anna");
		list.add("Ben");
		list.add("Connie");
		list.add("David");
		list.add("Erwin");
		list.add("Frank");
		((List<String>) this.ob.eGet(this.attribute)).addAll(list);

		ArrayList<String> listToRetain = new ArrayList<String>();
		listToRetain.add("Anna");
		listToRetain.add("Connie");
		((List<String>) this.ob.eGet(this.attribute)).retainAll(listToRetain);

		assertEquals(1,
				((List<String>) this.ob.eGet(this.attribute)).indexOf("Connie"));

		assertEquals(2, ((List<String>) this.ob.eGet(this.attribute)).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSetIndex() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Anna");
		list.add("Ben");
		list.add("Connie");
		list.add("David");
		((List<String>) this.ob.eGet(this.attribute)).addAll(list);

		((List<String>) this.ob.eGet(this.attribute)).set(1, "Bob");

		assertEquals("Bob",
				((List<String>) this.ob.eGet(this.attribute)).get(1));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSubList() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Anna");
		list.add("Ben");
		list.add("Connie");
		list.add("David");
		list.add("Erwin");
		list.add("Frank");
		((List<String>) this.ob.eGet(this.attribute)).addAll(list);

		List<String> sublist = ((List<String>) this.ob.eGet(this.attribute))
				.subList(1, 4);

		assertEquals(3, sublist.size());
		assertEquals("Ben", sublist.get(0));
		assertEquals("David", sublist.get(2));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testToArray() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Anna");
		list.add("Ben");
		list.add("Connie");
		list.add("David");
		list.add("Erwin");
		list.add("Frank");
		((List<String>) this.ob.eGet(this.attribute)).addAll(list);

		Object[] array = ((List<String>) this.ob.eGet(this.attribute))
				.toArray();
		assertEquals(6, array.length);
		assertEquals("Anna", array[0]);
		assertEquals("David", array[3]);
		assertEquals("Frank", array[5]);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMove() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Anna");
		list.add("Ben");
		list.add("Connie");
		list.add("David");
		list.add("Erwin");
		list.add("Frank");
		((List<String>) this.ob.eGet(this.attribute)).addAll(list);

		((EList<String>) this.ob.eGet(this.attribute)).move(1, "Frank");

		assertEquals("Anna",
				((List<String>) this.ob.eGet(this.attribute)).get(0));
		assertEquals("Frank",
				((List<String>) this.ob.eGet(this.attribute)).get(1));
		assertEquals("Erwin",
				((List<String>) this.ob.eGet(this.attribute)).get(5));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMoveIndex() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Anna");
		list.add("Ben");
		list.add("Connie");
		list.add("David");
		list.add("Erwin");
		list.add("Frank");
		((List<String>) this.ob.eGet(this.attribute)).addAll(list);

		((EList<String>) this.ob.eGet(this.attribute)).move(0, 5);

		assertEquals("Anna",
				((List<String>) this.ob.eGet(this.attribute)).get(1));
		assertEquals("Frank",
				((List<String>) this.ob.eGet(this.attribute)).get(0));
		assertEquals("Erwin",
				((List<String>) this.ob.eGet(this.attribute)).get(5));
	}
}

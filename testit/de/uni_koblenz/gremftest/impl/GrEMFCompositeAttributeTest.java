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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.Before;
import org.junit.Test;
import org.pcollections.ArrayPMap;
import org.pcollections.ArrayPSet;
import org.pcollections.ArrayPVector;
import org.pcollections.PMap;
import org.pcollections.PSet;
import org.pcollections.PVector;

import de.uni_koblenz.gremf.GrEMFIO;
import de.uni_koblenz.gremf.impl.GrEMFVertexImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFAttributeImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFEnumDomainImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFRecordDomainImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFVertexClassImpl;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.schema.Schema;

public class GrEMFCompositeAttributeTest {

	private GrEMFEnumDomainImpl dayOfWeekED;
	private GrEMFEnumDomainImpl buildingED;
	private GrEMFEnumDomainImpl categoryED;
	private GrEMFRecordDomainImpl slotRD;
	private GrEMFVertexClassImpl courseVC;
	private GrEMFAttributeImpl course_categories;
	private GrEMFAttributeImpl course_slots;
	private GrEMFAttributeImpl course_toDoLists;

	private Graph graph;
	private GrEMFVertexImpl courseVertex;

	@Before
	public void setUp() throws GraphIOException {
		Schema schema = GrEMFIO.loadSchemaFromFile(System
				.getProperty("user.dir")
				+ File.separator
				+ "models"
				+ File.separator + "compositeAttributeTestSchema.tg");
		this.dayOfWeekED = (GrEMFEnumDomainImpl) schema.getDomain("DayOfWeek");
		this.buildingED = (GrEMFEnumDomainImpl) schema.getDomain("Building");
		this.categoryED = (GrEMFEnumDomainImpl) schema.getDomain("Category");
		this.slotRD = (GrEMFRecordDomainImpl) schema.getDomain("Slot");
		this.courseVC = schema.getAttributedElementClass("Course");

		this.course_categories = (GrEMFAttributeImpl) this.courseVC
				.getAttribute("categories");
		this.course_slots = (GrEMFAttributeImpl) this.courseVC
				.getAttribute("slots");
		this.course_toDoLists = (GrEMFAttributeImpl) this.courseVC
				.getAttribute("toDoLists");

		this.graph = schema.createGraph(ImplementationType.GENERIC);
		this.courseVertex = this.graph.createVertex(this.courseVC);
		this.courseVertex.setAttribute("name", "Business Processes");

		ArrayPSet<Object> cateSet = ArrayPSet.empty();
		cateSet = (ArrayPSet<Object>) cateSet.plus(this.graph.getEnumConstant(
				this.categoryED, "COMPUTERS"));
		cateSet = (ArrayPSet<Object>) cateSet.plus(this.graph.getEnumConstant(
				this.categoryED, "ECONOMICS"));
		this.courseVertex.setAttribute("categories", cateSet);

		PMap<String, Object> recComp = ArrayPMap.empty();
		recComp = recComp.plus("building",
				this.graph.getEnumConstant(this.buildingED, "B"));
		recComp = recComp.plus("dayOfWeek",
				this.graph.getEnumConstant(this.dayOfWeekED, "MONDAY"));
		recComp = recComp.plus("hours", 2);
		recComp = recComp.plus("roomNumber", 16);
		recComp = recComp.plus("startHour", 10);
		Record slot1Rec = this.graph.createRecord(this.slotRD, recComp);

		PMap<String, Object> recComp2 = ArrayPMap.empty();
		recComp2 = recComp2.plus("building",
				this.graph.getEnumConstant(this.buildingED, "A"));
		recComp2 = recComp2.plus("dayOfWeek",
				this.graph.getEnumConstant(this.dayOfWeekED, "FRIDAY"));
		recComp2 = recComp2.plus("hours", 2);
		recComp2 = recComp2.plus("roomNumber", 308);
		recComp2 = recComp2.plus("startHour", 12);
		Record slot2Rec = this.graph.createRecord(this.slotRD, recComp2);

		ArrayPVector<Object> slotList = ArrayPVector.empty();
		slotList = (ArrayPVector<Object>) slotList.plus(slot1Rec);
		slotList = (ArrayPVector<Object>) slotList.plus(slot2Rec);
		this.courseVertex.setAttribute("slots", slotList);

		PVector<String> subList1 = ArrayPVector.empty();
		subList1 = subList1.plus("List1 ToDo1");
		subList1 = subList1.plus("List1 ToDo2");
		subList1 = subList1.plus("List1 ToDo3");

		PVector<String> subList2 = ArrayPVector.empty();
		subList2 = subList2.plus("List2 ToDo1");
		subList2 = subList2.plus("List2 ToDo2");
		subList2 = subList2.plus("List2 ToDo3");
		subList2 = subList2.plus("List2 ToDo4");

		PVector<String> subList3 = ArrayPVector.empty();
		subList3 = subList3.plus("List3 ToDo1");

		PVector<PVector<String>> listOfLists = ArrayPVector.empty();
		listOfLists = listOfLists.plus(subList1);
		listOfLists = listOfLists.plus(subList2);
		listOfLists = listOfLists.plus(subList3);

		this.courseVertex.setAttribute("toDoLists", listOfLists);

	}

	@Test
	public void testEGetAndGet() {
		System.out.println(this.courseVertex.eGet(this.course_categories));
		assertEquals(this.categoryED.getEEnumLiteral("COMPUTERS"),
				((List<?>) this.courseVertex.eGet(this.course_categories))
						.get(0));
		assertEquals(this.categoryED.getEEnumLiteral("ECONOMICS"),
				((List<?>) this.courseVertex.eGet(this.course_categories))
						.get(1));
		assertEquals(this.categoryED.getEEnumLiteral("COMPUTERS"),
				((List<?>) this.courseVertex.eGet(this.course_categories))
						.iterator().next());

	}

	@Test
	public void testEGetAndGet2() {
		System.out
				.println(((List<?>) this.courseVertex.eGet(this.course_slots))
						.get(0).getClass());
		assertTrue(((List<?>) this.courseVertex.eGet(this.course_slots)).get(0) instanceof Map);
		@SuppressWarnings("unchecked")
		Map<String, ?> map = (Map<String, ?>) ((List<?>) this.courseVertex
				.eGet(this.course_slots)).get(0);
		assertEquals(this.buildingED.getEEnumLiteral("B"), map.get("building"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAdd() {
		Map<String, Object> newSlot = new HashMap<String, Object>();
		newSlot.put("building", this.buildingED.getEEnumLiteral("K"));
		newSlot.put("dayOfWeek", this.dayOfWeekED.getEEnumLiteral("WEDNESDAY"));
		newSlot.put("hours", 2);
		newSlot.put("roomNumber", 1);
		newSlot.put("startHour", 8);
		((List<Object>) this.courseVertex.eGet(this.course_slots)).add(newSlot);
		assertTrue(((List<Object>) this.courseVertex.eGet(this.course_slots))
				.contains(newSlot));
	}

	@Test
	public void testSet1() {
		GrEMFVertexImpl o = (GrEMFVertexImpl) EcoreUtil.create(this.courseVC);
		HashSet<Object> newValue = new HashSet<Object>();
		newValue.add(this.categoryED.getEEnumLiteral("MATHEMATICS"));
		o.eSet(this.course_categories, newValue);
		assertEquals("MATHEMATICS", ((PSet<?>) o.getAttribute("categories"))
				.iterator().next());
	}

	@Test
	public void testSet2() {
		GrEMFVertexImpl o = (GrEMFVertexImpl) EcoreUtil.create(this.courseVC);
		ArrayList<Object> newValue = new ArrayList<Object>();

		Map<String, Object> newSlot = new HashMap<String, Object>();
		newSlot.put("building", this.buildingED.getEEnumLiteral("K"));
		newSlot.put("dayOfWeek", this.dayOfWeekED.getEEnumLiteral("WEDNESDAY"));
		newSlot.put("hours", 2);
		newSlot.put("roomNumber", 1);
		newSlot.put("startHour", 8);

		Map<String, Object> newSlot2 = new HashMap<String, Object>();
		newSlot2.put("building", this.buildingED.getEEnumLiteral("A"));
		newSlot2.put("dayOfWeek", this.dayOfWeekED.getEEnumLiteral("WEDNESDAY"));
		newSlot2.put("hours", 1);
		newSlot2.put("roomNumber", 115);
		newSlot2.put("startHour", 9);

		newValue.add(newSlot);
		newValue.add(newSlot2);
		o.eSet(this.course_slots, newValue);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetListOfList() {
		PVector<String> subList1 = ArrayPVector.empty();
		subList1 = subList1.plus("List1 ToDo1");
		subList1 = subList1.plus("List1 ToDo2");
		subList1 = subList1.plus("List1 ToDo3");

		PVector<String> subList2 = ArrayPVector.empty();
		subList2 = subList2.plus("List2 ToDo1");
		subList2 = subList2.plus("List2 ToDo2");
		subList2 = subList2.plus("List2 ToDo3");
		subList2 = subList2.plus("List2 ToDo4");

		PVector<String> subList3 = ArrayPVector.empty();
		subList3 = subList3.plus("List3 ToDo1");

		PVector<PVector<String>> listOfLists = ArrayPVector.empty();
		listOfLists = listOfLists.plus(subList1);
		listOfLists = listOfLists.plus(subList2);
		listOfLists = listOfLists.plus(subList3);

		assertEquals(listOfLists, this.courseVertex.eGet(this.course_toDoLists));
		assertEquals(subList1,
				((List<String>) this.courseVertex.eGet(this.course_toDoLists))
						.get(0));
		assertEquals(subList2,
				((List<String>) this.courseVertex.eGet(this.course_toDoLists))
						.get(1));
		assertEquals(subList3,
				((List<String>) this.courseVertex.eGet(this.course_toDoLists))
						.get(2));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddToListOfLists() {

		PVector<String> subList1 = ArrayPVector.empty();
		subList1 = subList1.plus("List1 ToDo1");
		subList1 = subList1.plus("List1 ToDo2");
		subList1 = subList1.plus("List1 ToDo3");

		PVector<String> subList2 = ArrayPVector.empty();
		subList2 = subList2.plus("List2 ToDo1");
		subList2 = subList2.plus("List2 ToDo2");
		subList2 = subList2.plus("List2 ToDo3");
		subList2 = subList2.plus("List2 ToDo4");

		PVector<String> subList3 = ArrayPVector.empty();
		subList3 = subList3.plus("List3 ToDo1");

		PVector<String> subList4 = ArrayPVector.empty();
		subList4 = subList4.plus("List4 ToDo1");
		subList4 = subList4.plus("List4 ToDo2");

		PVector<PVector<String>> listOfLists = ArrayPVector.empty();
		listOfLists = listOfLists.plus(subList1);
		listOfLists = listOfLists.plus(subList2);
		listOfLists = listOfLists.plus(subList3);
		listOfLists = listOfLists.plus(subList4);

		((List<List<String>>) this.courseVertex.eGet(this.course_toDoLists))
				.add(subList4);

		assertEquals(listOfLists, this.courseVertex.eGet(this.course_toDoLists));
		assertEquals(subList1,
				((List<String>) this.courseVertex.eGet(this.course_toDoLists))
						.get(0));
		assertEquals(subList2,
				((List<String>) this.courseVertex.eGet(this.course_toDoLists))
						.get(1));
		assertEquals(subList3,
				((List<String>) this.courseVertex.eGet(this.course_toDoLists))
						.get(2));
		assertEquals(subList4,
				((List<String>) this.courseVertex.eGet(this.course_toDoLists))
						.get(3));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSetListOfLists() {
		PVector<String> subList1 = ArrayPVector.empty();
		subList1 = subList1.plus("NewList1 ToDo1");
		subList1 = subList1.plus("NewList1 ToDo2");

		PVector<String> subList2 = ArrayPVector.empty();
		subList2 = subList2.plus("NewList2 ToDo1");
		subList2 = subList2.plus("NewList2 ToDo2");
		subList2 = subList2.plus("NewList2 ToDo3");

		PVector<PVector<String>> listOfLists = ArrayPVector.empty();
		listOfLists = listOfLists.plus(subList1);
		listOfLists = listOfLists.plus(subList2);

		this.courseVertex.eSet(this.course_toDoLists, listOfLists);

		assertEquals(listOfLists, this.courseVertex.eGet(this.course_toDoLists));
		assertEquals(subList1,
				((List<String>) this.courseVertex.eGet(this.course_toDoLists))
						.get(0));
		assertEquals(subList2,
				((List<String>) this.courseVertex.eGet(this.course_toDoLists))
						.get(1));
	}
}

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
package de.uni_koblenz.gremftest.notifications;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
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
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.schema.Schema;

public class NotificationAttributeListProxyTest {
	private EPackage ePackage;
	private EClass eClassPerson;
	private EClass eClassTask;
	private EAttribute eAttributeName;
	private EAttribute eAttributeSubTasks;

	private EObject personObject;
	private EObject taskObject;

	static int variant = 1;

	@Before
	public void setUp() throws GraphIOException {

		if (variant == 0) {

			this.ePackage = EcoreFactory.eINSTANCE.createEPackage();
			this.ePackage.setName("test");
			this.ePackage.setNsPrefix("test");
			this.ePackage.setNsURI("http://test.org");

			this.eClassPerson = EcoreFactory.eINSTANCE.createEClass();
			this.eClassPerson.setName("Person");
			this.ePackage.getEClassifiers().add(this.eClassPerson);

			this.eAttributeName = EcoreFactory.eINSTANCE.createEAttribute();
			this.eAttributeName.setName("name");
			this.eAttributeName.setEType(EcorePackage.eINSTANCE.getEString());
			this.eClassPerson.getEStructuralFeatures().add(this.eAttributeName);

			this.eClassTask = EcoreFactory.eINSTANCE.createEClass();
			this.eClassTask.setName("Task");
			this.ePackage.getEClassifiers().add(this.eClassTask);

			this.eAttributeSubTasks = EcoreFactory.eINSTANCE.createEAttribute();
			this.eAttributeSubTasks.setName("subTasks");
			this.eAttributeSubTasks.setEType(EcorePackage.eINSTANCE
					.getEString());
			this.eAttributeSubTasks.setUpperBound(-1);
			this.eClassTask.getEStructuralFeatures().add(
					this.eAttributeSubTasks);

			// --

			this.personObject = this.ePackage.getEFactoryInstance().create(
					this.eClassPerson);
			this.taskObject = this.ePackage.getEFactoryInstance().create(
					this.eClassTask);

		} else {
			Schema schema = GrEMFIO.loadSchemaFromFile(System
					.getProperty("user.dir")
					+ File.separator
					+ "models"
					+ File.separator + "notificationTestSchema.tg");
			this.ePackage = (EPackage) schema.getDefaultPackage();
			this.eClassPerson = (EClass) this.ePackage.getEClassifier("Person");
			this.eClassTask = (EClass) this.ePackage.getEClassifier("Task");
			this.eAttributeName = (EAttribute) this.eClassPerson
					.getEStructuralFeature("name");
			this.eAttributeSubTasks = (EAttribute) this.eClassTask
					.getEStructuralFeature("subTasks");

			this.personObject = this.ePackage.getEFactoryInstance().create(
					this.eClassPerson);
			this.taskObject = this.ePackage.getEFactoryInstance().create(
					this.eClassTask);
		}
	}

	@Test
	public void testSetSingleAttribute() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Set single Attribute");
		this.personObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.SET, -1,
						this.personObject, this.eAttributeName, null, "Hugo"));

		this.personObject.eSet(this.eAttributeName, "Hugo");

		assertEquals("Hugo", this.personObject.eGet(this.eAttributeName));

		System.out.println();
	}

	@Test
	public void testSetSingleAttribute2() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Set single Attribute that is already set");
		this.personObject.eSet(this.eAttributeName, "Anna");

		this.personObject
				.eAdapters()
				.add(new SingleNotificationTestAdapter(Notification.SET, -1,
						this.personObject, this.eAttributeName, "Anna", "Hugo"));

		this.personObject.eSet(this.eAttributeName, "Hugo");

		assertEquals("Hugo", this.personObject.eGet(this.eAttributeName));

		System.out.println();
	}

	@Test
	public void testSetSingleAttribute3() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Set single Attribute that is already set to null");
		this.personObject.eSet(this.eAttributeName, "Anna");

		this.personObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.SET, -1,
						this.personObject, this.eAttributeName, "Anna", null));

		this.personObject.eSet(this.eAttributeName, null);

		assertEquals(null, this.personObject.eGet(this.eAttributeName));

		System.out.println();
	}

	@Test
	public void testUnSetSingleAttribute() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Unset single Attribute");

		this.personObject.eSet(this.eAttributeName, "Hugo");

		this.personObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.SET, -1,
						this.personObject, this.eAttributeName, "Hugo", null));

		this.personObject.eUnset(this.eAttributeName);
		assertEquals(null, this.personObject.eGet(this.eAttributeName));

		System.out.println();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddOneElementToListAttribute() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Add one Element to an List Attribute");

		this.taskObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.ADD, 0,
						this.taskObject, this.eAttributeSubTasks, null,
						"subTask1"));

		((List<String>) this.taskObject.eGet(this.eAttributeSubTasks))
				.add("subTask1");

		assertEquals("subTask1",
				((List<String>) this.taskObject.eGet(this.eAttributeSubTasks))
						.get(0));

		System.out.println();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddAllElementsToListAttribute1() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Add List of two elements to an List Attribute");

		ArrayList<String> list = new ArrayList<String>();
		list.add("subTask1");
		list.add("subTask2");

		this.taskObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.ADD_MANY, 0,
						this.taskObject, this.eAttributeSubTasks, null, list));

		((List<String>) this.taskObject.eGet(this.eAttributeSubTasks))
				.addAll(list);

		assertEquals("subTask1",
				((List<EObject>) this.taskObject.eGet(this.eAttributeSubTasks))
						.get(0));

		System.out.println();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddAllElementsToListAttribute2() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Add List of one element to an List Attribute");

		this.taskObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.ADD, 0,
						this.taskObject, this.eAttributeSubTasks, null,
						"subTask1"));

		ArrayList<String> list = new ArrayList<String>();
		list.add("subTask1");
		((List<String>) this.taskObject.eGet(this.eAttributeSubTasks))
				.addAll(list);

		assertEquals("subTask1",
				((List<EObject>) this.taskObject.eGet(this.eAttributeSubTasks))
						.get(0));

		System.out.println();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddAllElementsToListAttributeIndex1() {
		System.out
				.println("-----------------------------------------------------");
		System.out
				.println("Add List of two elements to an List Attribute after Index");

		((List<String>) this.taskObject.eGet(this.eAttributeSubTasks))
				.add("subTask1");
		((List<String>) this.taskObject.eGet(this.eAttributeSubTasks))
				.add("subTask2");
		((List<String>) this.taskObject.eGet(this.eAttributeSubTasks))
				.add("subTask3");

		ArrayList<String> list = new ArrayList<String>();
		list.add("subTask4");
		list.add("subTask5");

		this.taskObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.ADD_MANY, 1,
						this.taskObject, this.eAttributeSubTasks, null, list));

		((List<String>) this.taskObject.eGet(this.eAttributeSubTasks)).addAll(
				1, list);

		System.out.println();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSetListToListAttribute1() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Set List of two elements to an List Attribute");

		ArrayList<String> list = new ArrayList<String>();
		list.add("subTask1");
		list.add("subTask2");

		this.taskObject.eAdapters().add(
				new DoubleNotificationTestAdapter(Notification.REMOVE_MANY, -1,
						this.taskObject, this.eAttributeSubTasks,
						new ArrayList<String>(), null, Notification.ADD_MANY,
						0, null, list));

		this.taskObject.eSet(this.eAttributeSubTasks, list);

		assertEquals("subTask1",
				((List<String>) this.taskObject.eGet(this.eAttributeSubTasks))
						.get(0));

		System.out.println();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSetListToListAttribute2() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Set List of one element to an List Attribute");

		ArrayList<String> list = new ArrayList<String>();
		list.add("subTask1");

		this.taskObject.eAdapters().add(
				new DoubleNotificationTestAdapter(Notification.REMOVE_MANY, -1,
						this.taskObject, this.eAttributeSubTasks,
						new ArrayList<String>(), null, Notification.ADD, 0,
						null, "subTask1"));

		this.taskObject.eSet(this.eAttributeSubTasks, list);

		assertEquals("subTask1",
				((List<String>) this.taskObject.eGet(this.eAttributeSubTasks))
						.get(0));

		System.out.println();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRemoveOneElementOfListAttribute() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Remove one Element of an List Attribute");

		ArrayList<String> list = new ArrayList<String>();
		list.add("subTask1");
		list.add("subTask2");

		this.taskObject.eSet(this.eAttributeSubTasks, list);

		this.taskObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.REMOVE, 1,
						this.taskObject, this.eAttributeSubTasks, "subTask2",
						null));

		((List<String>) this.taskObject.eGet(this.eAttributeSubTasks))
				.remove("subTask2");

		assertEquals("subTask1",
				((List<String>) this.taskObject.eGet(this.eAttributeSubTasks))
						.get(0));

		System.out.println();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRemoveManyElementOfListAttribute1() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Remove many Elements of an List Attribute");

		ArrayList<String> list = new ArrayList<String>();
		list.add("subTask1");
		list.add("subTask2");
		list.add("subTask3");
		list.add("subTask4");
		this.taskObject.eSet(this.eAttributeSubTasks, list);

		ArrayList<String> listToRemove = new ArrayList<String>();
		listToRemove.add("subTask2");
		listToRemove.add("subTask4");

		this.taskObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.REMOVE_MANY, 1,
						this.taskObject, this.eAttributeSubTasks, listToRemove,
						new int[] { 1, 3 }));

		((List<String>) this.taskObject.eGet(this.eAttributeSubTasks))
				.removeAll(listToRemove);

		assertEquals("subTask3",
				((List<String>) this.taskObject.eGet(this.eAttributeSubTasks))
						.get(1));

		System.out.println();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRemoveManyElementOfListAttribute2() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Remove List with one Element of an List Attribute");

		ArrayList<String> list = new ArrayList<String>();
		list.add("subTask1");
		list.add("subTask2");
		list.add("subTask3");
		list.add("subTask4");
		this.taskObject.eSet(this.eAttributeSubTasks, list);

		this.taskObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.REMOVE, 1,
						this.taskObject, this.eAttributeSubTasks, "subTask2",
						null));

		ArrayList<String> listToRemove = new ArrayList<String>();
		listToRemove.add("subTask2");

		((List<String>) this.taskObject.eGet(this.eAttributeSubTasks))
				.removeAll(listToRemove);

		assertEquals("subTask3",
				((List<EObject>) this.taskObject.eGet(this.eAttributeSubTasks))
						.get(1));

		System.out.println();
	}

	@Test
	public void testSetToNullList1() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Unset List Attribute");

		ArrayList<String> list = new ArrayList<String>();
		list.add("subTask1");
		list.add("subTask2");
		list.add("subTask3");
		list.add("subTask4");
		this.taskObject.eSet(this.eAttributeSubTasks, list);

		this.taskObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.REMOVE_MANY, -1,
						this.taskObject, this.eAttributeSubTasks, list, null));
		this.taskObject.eUnset(this.eAttributeSubTasks);

		System.out.println();
	}

	@Test
	public void testSetToNullList2() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Unset List Attribute with one element");

		ArrayList<String> list = new ArrayList<String>();
		list.add("subTask1");
		this.taskObject.eSet(this.eAttributeSubTasks, list);

		this.taskObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.REMOVE, 0,
						this.taskObject, this.eAttributeSubTasks, "subTask1",
						null));
		this.taskObject.eUnset(this.eAttributeSubTasks);

		System.out.println();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMove() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Move List Attribute");

		ArrayList<String> list = new ArrayList<String>();
		list.add("subTask1");
		list.add("subTask2");
		list.add("subTask3");
		list.add("subTask4");
		this.taskObject.eSet(this.eAttributeSubTasks, list);

		this.taskObject
				.eAdapters()
				.add(new SingleNotificationTestAdapter(Notification.MOVE, 2,
						this.taskObject, this.eAttributeSubTasks, 3, "subTask4"));
		((EList<String>) this.taskObject.eGet(this.eAttributeSubTasks)).move(2,
				"subTask4");

		System.out.println();
	}

	@Test
	public void testClear() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Clear List Attribute");

		ArrayList<String> list = new ArrayList<String>();
		list.add("subTask1");
		list.add("subTask2");
		list.add("subTask3");
		list.add("subTask4");
		this.taskObject.eSet(this.eAttributeSubTasks, list);

		this.taskObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.REMOVE_MANY, -1,
						this.taskObject, this.eAttributeSubTasks, list, null));

		((Collection<?>) this.taskObject.eGet(this.eAttributeSubTasks)).clear();

		System.out.println();
	}

	@Test
	public void testRetain() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Retain some entrys of List Attribute");

		ArrayList<String> list = new ArrayList<String>();
		list.add("subTask1");
		list.add("subTask2");
		list.add("subTask3");
		list.add("subTask4");
		this.taskObject.eSet(this.eAttributeSubTasks, list);

		ArrayList<String> toRetain = new ArrayList<String>();
		toRetain.add("subTask1");
		toRetain.add("subTask3");

		this.taskObject.eAdapters().add(
				new DoubleNotificationTestAdapter(Notification.REMOVE, 3,
						this.taskObject, this.eAttributeSubTasks, "subTask4",
						null, Notification.REMOVE, 1, "subTask2", null));

		// test operation
		((Collection<?>) this.taskObject.eGet(this.eAttributeSubTasks))
				.retainAll(toRetain);

		System.out.println();
	}
}

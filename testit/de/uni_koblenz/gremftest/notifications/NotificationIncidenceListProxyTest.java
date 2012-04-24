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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.junit.Before;
import org.junit.Test;

import de.uni_koblenz.gremf.GrEMFIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.schema.Schema;

//import de.uni_koblenz.jgralabtest.eca.useractions.PrintNewAndOldAttributeValueAction;

public class NotificationIncidenceListProxyTest {

	private EPackage ePackage;
	private EClass eClassPerson;
	private EClass eClassAddress;
	private EClass eClassTask;
	private EReference eReferenceTasks;
	private EReference eReferenceAddress;
	private EReference eReferenceInhabitants;

	private EObject personObject;

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

			this.eClassAddress = EcoreFactory.eINSTANCE.createEClass();
			this.eClassAddress.setName("Address");
			this.ePackage.getEClassifiers().add(this.eClassAddress);

			this.eClassTask = EcoreFactory.eINSTANCE.createEClass();
			this.eClassTask.setName("Task");
			this.ePackage.getEClassifiers().add(this.eClassTask);

			this.eReferenceTasks = EcoreFactory.eINSTANCE.createEReference();
			this.eReferenceTasks.setName("tasks");
			this.eReferenceTasks.setEType(this.eClassTask);
			this.eReferenceTasks.setUpperBound(-1);
			this.eClassPerson.getEStructuralFeatures()
					.add(this.eReferenceTasks);

			this.eReferenceAddress = EcoreFactory.eINSTANCE.createEReference();
			this.eReferenceAddress.setName("address");
			this.eReferenceAddress.setEType(this.eClassAddress);
			this.eReferenceAddress.setLowerBound(1);
			this.eReferenceAddress.setUpperBound(1);
			this.eClassPerson.getEStructuralFeatures().add(
					this.eReferenceAddress);

			this.eReferenceInhabitants = EcoreFactory.eINSTANCE
					.createEReference();
			this.eReferenceInhabitants.setName("inhabitants");
			this.eReferenceInhabitants.setEType(this.eClassPerson);
			this.eReferenceInhabitants.setLowerBound(0);
			this.eReferenceInhabitants.setUpperBound(-1);
			this.eClassAddress.getEStructuralFeatures().add(
					this.eReferenceInhabitants);

			this.eReferenceAddress.setEOpposite(this.eReferenceInhabitants);
			this.eReferenceInhabitants.setEOpposite(this.eReferenceAddress);

			// ---

			this.personObject = this.ePackage.getEFactoryInstance().create(
					this.eClassPerson);
		} else {
			Schema schema = GrEMFIO.loadSchemaFromFile(System
					.getProperty("user.dir")
					+ File.separator
					+ "models"
					+ File.separator + "notificationTestSchema.tg");
			this.ePackage = (EPackage) schema.getDefaultPackage();
			this.eClassPerson = (EClass) this.ePackage.getEClassifier("Person");
			this.eClassTask = (EClass) this.ePackage.getEClassifier("Task");
			this.eClassAddress = (EClass) this.ePackage
					.getEClassifier("Address");
			this.eReferenceTasks = (EReference) this.eClassPerson
					.getEStructuralFeature("tasks");
			this.eReferenceAddress = (EReference) this.eClassPerson
					.getEStructuralFeature("address");
			this.eReferenceInhabitants = this.eReferenceAddress.getEOpposite();

			this.personObject = this.ePackage.getEFactoryInstance().create(
					this.eClassPerson);
		}
	}

	@Test
	public void testSetSingleAttribute() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Set single Attribute");
		EObject addressObject = this.ePackage.getEFactoryInstance().create(
				this.eClassAddress);
		addressObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.ADD, 0,
						addressObject, this.eReferenceInhabitants, null,
						this.personObject));

		this.personObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.SET, -1,
						this.personObject, this.eReferenceAddress, null,
						addressObject));

		// test operation
		this.personObject.eSet(this.eReferenceAddress, addressObject);

		assertEquals(addressObject,
				this.personObject.eGet(this.eReferenceAddress));

		System.out.println();
	}

	@Test
	public void testSetSingleAttribute2() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Set single Attribute that is already set");
		EObject oldAddressObject = this.ePackage.getEFactoryInstance().create(
				this.eClassAddress);
		this.personObject.eSet(this.eReferenceAddress, oldAddressObject);

		oldAddressObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.REMOVE, 0,
						oldAddressObject, this.eReferenceInhabitants,
						this.personObject, null));

		EObject addressObject = this.ePackage.getEFactoryInstance().create(
				this.eClassAddress);
		addressObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.ADD, 0,
						addressObject, this.eReferenceInhabitants, null,
						this.personObject));

		this.personObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.SET, -1,
						this.personObject, this.eReferenceAddress,
						oldAddressObject, addressObject));

		// test operation
		this.personObject.eSet(this.eReferenceAddress, addressObject);

		assertEquals(addressObject,
				this.personObject.eGet(this.eReferenceAddress));

		System.out.println();
	}

	@Test
	public void testSetSingleAttribute3() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Set single Attribute that is already set to null");
		EObject oldAddressObject = this.ePackage.getEFactoryInstance().create(
				this.eClassAddress);
		this.personObject.eSet(this.eReferenceAddress, oldAddressObject);

		oldAddressObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.REMOVE, 0,
						oldAddressObject, this.eReferenceInhabitants,
						this.personObject, null));

		this.personObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.SET, -1,
						this.personObject, this.eReferenceAddress,
						oldAddressObject, null));

		// test operation
		this.personObject.eSet(this.eReferenceAddress, null);

		assertEquals(null, this.personObject.eGet(this.eReferenceAddress));

		System.out.println();
	}

	@Test
	public void testUnSetSingleAttribute() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Unset single Attribute");

		EObject oldAddressObject = this.ePackage.getEFactoryInstance().create(
				this.eClassAddress);
		this.personObject.eSet(this.eReferenceAddress, oldAddressObject);

		oldAddressObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.REMOVE, 0,
						oldAddressObject, this.eReferenceInhabitants,
						this.personObject, null));

		this.personObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.SET, -1,
						this.personObject, this.eReferenceAddress,
						oldAddressObject, null));

		// test operation
		this.personObject.eUnset(this.eReferenceAddress);

		assertEquals(null, this.personObject.eGet(this.eReferenceAddress));

		System.out.println();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddOneElementToListAttribute() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Add one Element to an List Attribute");

		EObject taskObject1 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);

		this.personObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.ADD, 0,
						this.personObject, this.eReferenceTasks, null,
						taskObject1));

		((List<EObject>) this.personObject.eGet(this.eReferenceTasks))
				.add(taskObject1);

		assertEquals(taskObject1,
				((List<EObject>) this.personObject.eGet(this.eReferenceTasks))
						.get(0));

		System.out.println();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddAllElementsToListAttribute1() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Add List of two elements to an List Attribute");

		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject task1 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task1);
		EObject task2 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task2);

		this.personObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.ADD_MANY, 0,
						this.personObject, this.eReferenceTasks, null, list));

		((List<EObject>) this.personObject.eGet(this.eReferenceTasks))
				.addAll(list);

		assertEquals(task1,
				((List<EObject>) this.personObject.eGet(this.eReferenceTasks))
						.get(0));

		System.out.println();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddAllElementsToListAttribute2() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Add List of one element to an List Attribute");

		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject task1 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task1);

		this.personObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.ADD, 0,
						this.personObject, this.eReferenceTasks, null, task1));

		((List<EObject>) this.personObject.eGet(this.eReferenceTasks))
				.addAll(list);

		assertEquals(task1,
				((List<EObject>) this.personObject.eGet(this.eReferenceTasks))
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

		((List<EObject>) this.personObject.eGet(this.eReferenceTasks))
				.add(this.ePackage.getEFactoryInstance()
						.create(this.eClassTask));
		((List<EObject>) this.personObject.eGet(this.eReferenceTasks))
				.add(this.ePackage.getEFactoryInstance()
						.create(this.eClassTask));
		((List<EObject>) this.personObject.eGet(this.eReferenceTasks))
				.add(this.ePackage.getEFactoryInstance()
						.create(this.eClassTask));

		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject task1 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task1);
		EObject task2 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task2);

		this.personObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.ADD_MANY, 1,
						this.personObject, this.eReferenceTasks, null, list));

		// test operation
		((List<EObject>) this.personObject.eGet(this.eReferenceTasks)).addAll(
				1, list);

		System.out.println();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSetListToListAttribute1() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Set List of two elements to an List Attribute");

		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject task1 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task1);
		EObject task2 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task2);

		this.personObject.eAdapters().add(
				new DoubleNotificationTestAdapter(Notification.REMOVE_MANY, -1,
						this.personObject, this.eReferenceTasks,
						new ArrayList<EObject>(), null, Notification.ADD_MANY,
						0, null, list));

		// test operation
		this.personObject.eSet(this.eReferenceTasks, list);

		assertEquals(task1,
				((List<EObject>) this.personObject.eGet(this.eReferenceTasks))
						.get(0));

		System.out.println();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSetListToListAttribute2() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Set List of one element to an List Attribute");

		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject task1 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task1);

		this.personObject.eAdapters().add(
				new DoubleNotificationTestAdapter(Notification.REMOVE_MANY, -1,
						this.personObject, this.eReferenceTasks,
						new ArrayList<EObject>(), null, Notification.ADD, 0,
						null, task1));

		// test operation
		this.personObject.eSet(this.eReferenceTasks, list);

		assertEquals(task1,
				((List<EObject>) this.personObject.eGet(this.eReferenceTasks))
						.get(0));

		System.out.println();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRemoveOneElementOfListAttribute() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Remove one Element of an List Attribute");

		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject task1 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task1);
		EObject task2 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task2);

		this.personObject.eSet(this.eReferenceTasks, list);

		this.personObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.REMOVE, 0,
						this.personObject, this.eReferenceTasks, task1, null));

		// test operation
		((List<EObject>) this.personObject.eGet(this.eReferenceTasks))
				.remove(task1);

		assertEquals(task2,
				((List<EObject>) this.personObject.eGet(this.eReferenceTasks))
						.get(0));

		System.out.println();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRemoveManyElementOfListAttribute1() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Remove many Elements of an List Attribute");

		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject task1 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task1);
		EObject task2 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task2);
		EObject task3 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task3);
		EObject task4 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task4);
		EObject task5 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task5);
		EObject task6 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task6);
		EObject task7 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task7);
		this.personObject.eSet(this.eReferenceTasks, list);

		ArrayList<EObject> listToRemove = new ArrayList<EObject>();
		listToRemove.add(task2);
		listToRemove.add(task4);
		listToRemove.add(task7);

		int[] removedIndices = { 1, 3, 6 };

		this.personObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.REMOVE_MANY, 1,
						this.personObject, this.eReferenceTasks, listToRemove,
						removedIndices));

		((List<EObject>) this.personObject.eGet(this.eReferenceTasks))
				.removeAll(listToRemove);

		assertEquals(task3,
				((List<EObject>) this.personObject.eGet(this.eReferenceTasks))
						.get(1));

		System.out.println();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRemoveManyElementOfListAttribute2() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Remove List with one Element of an List Attribute");

		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject task1 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task1);
		EObject task2 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task2);
		EObject task3 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task3);
		EObject task4 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task4);
		this.personObject.eSet(this.eReferenceTasks, list);

		this.personObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.REMOVE, 1,
						this.personObject, this.eReferenceTasks, task2, null));

		ArrayList<EObject> listToRemove = new ArrayList<EObject>();
		listToRemove.add(task2);

		((List<EObject>) this.personObject.eGet(this.eReferenceTasks))
				.removeAll(listToRemove);

		assertEquals(task3,
				((List<EObject>) this.personObject.eGet(this.eReferenceTasks))
						.get(1));

		System.out.println();
	}

	@Test
	public void testSetToNullList1() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Unset List Attribute");

		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject task1 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task1);
		EObject task2 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task2);
		EObject task3 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task3);
		EObject task4 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task4);
		this.personObject.eSet(this.eReferenceTasks, list);

		this.personObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.REMOVE_MANY, -1,
						this.personObject, this.eReferenceTasks, list, null));

		this.personObject.eUnset(this.eReferenceTasks);

		System.out.println();
	}

	@Test
	public void testSetToNullList2() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Unset List Attribute with one element");

		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject task1 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task1);
		this.personObject.eSet(this.eReferenceTasks, list);

		this.personObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.REMOVE, 0,
						this.personObject, this.eReferenceTasks, task1, null));

		this.personObject.eUnset(this.eReferenceTasks);

		System.out.println();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMove() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Move List Attribute");

		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject task1 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task1);
		EObject task2 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task2);
		EObject task3 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task3);
		EObject task4 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task4);
		this.personObject.eSet(this.eReferenceTasks, list);

		this.personObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.MOVE, 2,
						this.personObject, this.eReferenceTasks, 3, task4));

		((EList<EObject>) this.personObject.eGet(this.eReferenceTasks)).move(2,
				task4);

		System.out.println();
	}

	@Test
	public void testClear() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Clear List Attribute");

		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject task1 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task1);
		EObject task2 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task2);
		EObject task3 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task3);
		EObject task4 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task4);
		this.personObject.eSet(this.eReferenceTasks, list);

		this.personObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.REMOVE_MANY, -1,
						this.personObject, this.eReferenceTasks, list, null));

		((Collection<?>) this.personObject.eGet(this.eReferenceTasks)).clear();

		System.out.println();
	}

	@Test
	public void testClear2() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Clear List Attribute");

		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject task1 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task1);
		this.personObject.eSet(this.eReferenceTasks, list);

		this.personObject.eAdapters().add(
				new SingleNotificationTestAdapter(Notification.REMOVE, 0,
						this.personObject, this.eReferenceTasks, task1, null));

		((Collection<?>) this.personObject.eGet(this.eReferenceTasks)).clear();

		System.out.println();
	}

	@Test
	public void testRetain() {
		System.out
				.println("-----------------------------------------------------");
		System.out.println("Retain some entrys of List Attribute");

		ArrayList<EObject> list = new ArrayList<EObject>();
		EObject task1 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task1);
		EObject task2 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task2);
		EObject task3 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task3);
		EObject task4 = this.ePackage.getEFactoryInstance().create(
				this.eClassTask);
		list.add(task4);
		this.personObject.eSet(this.eReferenceTasks, list);

		ArrayList<EObject> toRetain = new ArrayList<EObject>();
		toRetain.add(task1);
		toRetain.add(task3);

		this.personObject.eAdapters().add(
				new DoubleNotificationTestAdapter(Notification.REMOVE, 3,
						this.personObject, this.eReferenceTasks, task4, null,
						Notification.REMOVE, 1, task2, null));

		// test operation
		((Collection<?>) this.personObject.eGet(this.eReferenceTasks))
				.retainAll(toRetain);

		System.out.println();
	}

	// ///-----------------------------------------

	// @Test
	// public void ecaTest() {
	// System.out
	// .println("-----------------------------------------------------");
	// System.out.println("ECA Test");
	// EventDescription<VertexClass> ev = new
	// ChangeAnyAttributeEventDescription<VertexClass>(
	// EventTime.AFTER);
	// Action<VertexClass> pa = new
	// PrintNewAndOldAttributeValueAction<VertexClass>();
	// ECARule<VertexClass> rule = new ECARule<VertexClass>(ev, pa);
	//
	// // Create a new Schema with the name "UniversitySchema" and the package
	// // prefix "de.uni_koblenz.jgralab.testschemas"
	// Schema schema = new SchemaImpl("UniversitySchema",
	// "de.uni_koblenz.jgralab.testschemas");
	//
	// // Create a GraphClass with the simpleName "UniversityGraph" for the new
	// // Schema
	// GraphClass graphClass = schema.createGraphClass("UniversityGraph");
	//
	// // Crate an abstract VertexClass "Person" in the Package "persons" with
	// // Attribute "name"
	// VertexClass personVC = graphClass.createVertexClass("persons.Person");
	// personVC.setAbstract(true);
	// personVC.addAttribute("name", schema.getStringDomain());
	//
	// schema.createAttribute("name", schema.getStringDomain(), personVC,
	// "Jane Doe");
	//
	// // Create a VertexClass "Student" in the Package "persons" with the
	// // VertexClass "Person" as superclass and the Attribute
	// // "matriculationNumber"
	// VertexClass studentVC = graphClass.createVertexClass("persons.Student");
	// studentVC.addSuperClass(personVC);
	// studentVC
	// .addAttribute("matriculationNumber", schema.getIntegerDomain());
	//
	// // Create an EnumDomain "DayOfWeek" in the Package "organisation" with
	// // the literals "MONDAY" ... "FRIDAY"
	// EnumDomain dayOfWeekED = schema
	// .createEnumDomain("organisation.DayOfWeek");
	// dayOfWeekED.addConst("MONDAY");
	// dayOfWeekED.addConst("TUESDAY");
	// dayOfWeekED.addConst("WEDNESDAY");
	// dayOfWeekED.addConst("THURSDAY");
	// dayOfWeekED.addConst("FRIDAY");
	//
	// // Create an EnumDomain "Building" in the Package "organisation" with
	// // the literals "A" ... "H", "K", "M"
	// EnumDomain buildingED = schema
	// .createEnumDomain("organisation.Building");
	// buildingED.addConst("A");
	// buildingED.addConst("B");
	// buildingED.addConst("C");
	// buildingED.addConst("D");
	// buildingED.addConst("E");
	// buildingED.addConst("F");
	// buildingED.addConst("G");
	// buildingED.addConst("H");
	// buildingED.addConst("K");
	// buildingED.addConst("M");
	//
	// // Create a RecordDomain "Slot" in the Package "organisation" with the
	// // components "dayOfWeek", "startHour", "hours", "building" and
	// // "roomNumber"
	// RecordDomain slotRD = schema.createRecordDomain("organisation.Slot");
	// slotRD.addComponent("dayOfWeek", dayOfWeekED);
	// slotRD.addComponent("startHour", schema.getIntegerDomain());
	// slotRD.addComponent("hours", schema.getIntegerDomain());
	// slotRD.addComponent("building", buildingED);
	// slotRD.addComponent("roomNumber", schema.getIntegerDomain());
	//
	// // Create a RecordDomain "Date" in the Package "organisation" with the
	// // components "day", "month" and "year"
	// RecordDomain dateRD = schema.createRecordDomain("organisation.Date");
	// dateRD.addComponent("day", schema.getIntegerDomain());
	// dateRD.addComponent("month", schema.getIntegerDomain());
	// dateRD.addComponent("year", schema.getIntegerDomain());
	//
	// // Create a VertexClass "Exam" in the Package "courses" with the
	// // Attributes "date" and "slot"
	// VertexClass examVC = graphClass.createVertexClass("courses.Exam");
	// examVC.addAttribute("date", dateRD);
	// examVC.addAttribute("slot", slotRD);
	//
	// // Create an EdgeClass "WritesExam" in the Package "courses" from the
	// // VertexClass "Student" to the VertexClass "Exam"
	// EdgeClass writesExamEC = graphClass.createEdgeClass(
	// "courses.WritesExam", studentVC, 0, Integer.MAX_VALUE,
	// "examinee", AggregationKind.NONE, examVC, 0, Integer.MAX_VALUE,
	// "exam", AggregationKind.NONE);
	// writesExamEC.addAttribute("mark", schema.getIntegerDomain());
	//
	// schema.finish();
	//
	// Graph graph = schema.createGraph(ImplementationType.GENERIC);
	// ECARuleManager manager = (ECARuleManager) graph.getECARuleManager();
	// manager.addECARule(rule);
	//
	// EventDescription<EdgeClass> event4 = new CreateAnyEdgeEventDescription(
	// EventTime.BEFORE);
	// Action<EdgeClass> action4 = new PrintEventSpecsAction<EdgeClass>();
	// ECARule<EdgeClass> rule4 = new ECARule<EdgeClass>(event4, action4);
	// manager.addECARule(rule4);
	//
	// EventDescription<EdgeClass> event5 = new CreateAnyEdgeEventDescription(
	// EventTime.AFTER);
	// Action<EdgeClass> action5 = new PrintEventSpecsAction<EdgeClass>();
	// ECARule<EdgeClass> rule5 = new ECARule<EdgeClass>(event5, action5);
	// manager.addECARule(rule5);
	//
	// EventDescription<EdgeClass> event6 = new DeleteAnyEdgeEventDescription(
	// EventTime.AFTER);
	// Action<EdgeClass> action6 = new PrintEventSpecsAction<EdgeClass>();
	// ECARule<EdgeClass> rule6 = new ECARule<EdgeClass>(event6, action6);
	// manager.addECARule(rule6);
	//
	// Vertex person1 = graph.createVertex(studentVC);
	// System.out.println("change name:");
	// person1.setAttribute("name", "Hugo");
	//
	// Vertex vExam = graph.createVertex(examVC);
	// System.out.println("mark bef");
	// Edge eWritesExam = graph.createEdge(writesExamEC, person1, vExam);
	// System.out.println("mark aft");
	// eWritesExam.setAttribute("mark", 2);
	//
	// eWritesExam.delete();
	// person1.delete();
	//
	// }
}

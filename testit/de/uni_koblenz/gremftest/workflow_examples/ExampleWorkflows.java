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
package de.uni_koblenz.gremftest.workflow_examples;

import java.io.File;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Test;

import de.uni_koblenz.gremf.GrEMFIO;
import de.uni_koblenz.gremf.collection.GrEMFIncidencesListProxy;
import de.uni_koblenz.gremf.impl.GrEMFVertexImpl;
import de.uni_koblenz.gremf.resource.GrEMFResourceFactoryImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFPackageImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFSchemaImpl;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.schema.Schema;

public class ExampleWorkflows {

	/*
	 * Various examples of typical work flows as test cases
	 */

	private EPackage bookStoreEPackage;
	private EClass bookStoreEClass;
	private EClass bookEClass;
	private EAttribute bookStoreLocation;
	private EAttribute storeOwner;
	private EReference bookStore_Books;
	private Resource r_schema;
	private EAttribute book_Name;
	private EAttribute book_ISBN;

	private void loadBookStore() {
		ResourceSet load_resourceSet = new ResourceSetImpl();

		load_resourceSet.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put("*", new GrEMFResourceFactoryImpl());

		this.r_schema = load_resourceSet.getResource(
				URI.createURI(System.getProperty("user.dir") + File.separator
						+ "models" + File.separator + "bookStore.gremf"), true);

		EPackage rootPkg = (EPackage) this.r_schema.getContents().get(0);

		this.bookStoreEPackage = rootPkg.getESubpackages().get(0);

		this.bookStoreEClass = (EClass) this.bookStoreEPackage
				.getEClassifier("BookStore");
		this.bookEClass = (EClass) this.bookStoreEPackage
				.getEClassifier("Book");

		this.storeOwner = (EAttribute) this.bookStoreEClass
				.getEStructuralFeature("owner");
		this.bookStoreLocation = (EAttribute) this.bookStoreEClass
				.getEStructuralFeature("location");
		this.bookStore_Books = (EReference) this.bookStoreEClass
				.getEStructuralFeature("books");

		this.book_Name = (EAttribute) this.bookEClass
				.getEStructuralFeature("name");

		this.book_ISBN = (EAttribute) this.bookEClass
				.getEStructuralFeature("isbn");

	}

	@Test
	public void workflow1() {
		this.loadBookStore();

		EFactory bookStoreFactory = this.bookStoreEPackage
				.getEFactoryInstance();

		// Open up a bookstore, checking if its actually a GrEMF object

		EObject theBookstore = bookStoreFactory.create(this.bookStoreEClass);
		System.out.println(theBookstore);
		System.out.println(theBookstore.getClass());
		assert (theBookstore.getClass().getCanonicalName() == "de.uni_koblenz.gremf.impl.GrEMFVertexImpl");

		// Set owner attribute of the store
		theBookstore.eSet(this.storeOwner, "Bert McStore-Owher");
		System.out.println(theBookstore);

		// Set location of the store
		theBookstore.eSet(this.bookStoreLocation,
				"Foo Street 1. in Foobar-City");

		// Adding 10 books with names and ISBN to the store
		BasicEList<EObject> books = new BasicEList<EObject>();
		for (int i = 0; i < 10; i++) {
			EObject book = bookStoreFactory.create(this.bookEClass);
			book.eSet(this.book_Name, "Testing Stuff, Volume: " + (i + 1));
			book.eSet(this.book_ISBN, 123456789 + i);
			books.add(book);
			System.out.println(book);
		}
		theBookstore.eSet(this.bookStore_Books, books);
		System.out.println(theBookstore);
	}

	@Test
	public void workflow2() {

		Schema schema = null;
		Graph g = null;
		try {
			schema = GrEMFIO.loadSchemaFromFile("models/university_graph.tg");
			g = GraphIO.loadGraphFromFile("models/university_graph.tg", schema,
					ImplementationType.GENERIC, null);
		} catch (GraphIOException e) {
			e.printStackTrace();
		}

		EPackage personPkg = (GrEMFPackageImpl) schema.getPackage("persons");
		EFactory personFactory = personPkg.getEFactoryInstance();

		EClass studentEClass = (EClass) personPkg.getEClassifier("Student");
		EAttribute student_matriculationNumber = (EAttribute) studentEClass
				.getEStructuralFeature("matriculationNumber");

		EPackage courses = (GrEMFPackageImpl) schema.getPackage("courses");

		EClass lectureEClass = (EClass) courses.getEClassifier("Lecture");
		for (EStructuralFeature f : lectureEClass.getEAllStructuralFeatures()) {
			System.out.println(f);
		}
		EReference lectureAttendees = (EReference) lectureEClass
				.getEStructuralFeature("attendee");

		EObject progLecture = (GrEMFVertexImpl) g.getVertex(6);
		GrEMFIncidencesListProxy attendees = (GrEMFIncidencesListProxy) progLecture
				.eGet(lectureAttendees);

		((GrEMFSchemaImpl) schema).setCurrentGraph(g);

		EObject student1 = personFactory.create(studentEClass);

		student1.eSet(student_matriculationNumber, 10815);

		attendees.add(student1);

		EObject student2 = personFactory.create(studentEClass);

		student2.eSet(student_matriculationNumber, 10816);

		attendees.add(student2);

		g.deleteVertex((GrEMFVertexImpl) student2);

	}
}

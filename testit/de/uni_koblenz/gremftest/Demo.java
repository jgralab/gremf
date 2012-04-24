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
package de.uni_koblenz.gremftest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Test;
import org.pcollections.ArrayPMap;

import de.uni_koblenz.gremf.GrEMFIO;
import de.uni_koblenz.gremf.resource.GrEMFResourceFactoryImpl;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.EnumDomain;
import de.uni_koblenz.jgralab.schema.RecordDomain;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;

public class Demo {

	@Test
	public void loadModelWithJGraLabAPI() throws GraphIOException {
		Schema schema = GrEMFIO.loadSchemaFromFile(System
				.getProperty("user.dir")
				+ File.separator
				+ "models"
				+ File.separator + "university_graph.tg");

		this.printSchemaJGraLab(schema);

		Graph g = GraphIO.loadGraphFromFile(System.getProperty("user.dir")
				+ File.separator + "models" + File.separator
				+ "university_graph.tg", schema, ImplementationType.GENERIC,
				null);

		this.printModelJGraLab(g);
	}

	private void printModelJGraLab(Graph g) {
		System.out
				.println("----------------- Printing Graph ---------------------");

		System.out.println("Graph: " + g + " : " + g.getClass());
		for (Vertex v : g.vertices()) {
			System.out.println(" - " + v + " : " + v.getClass());
			for (Attribute a : v.getAttributedElementClass().getAttributeList()) {
				System.out.println("    + " + a.getName() + " : "
						+ v.getAttribute(a.getName()));
			}
		}
		for (Edge e : g.edges()) {
			System.out.println(" - " + e + " : " + e.getClass());
			for (Attribute a : e.getAttributedElementClass().getAttributeList()) {
				System.out.println("    + " + a.getName() + " : "
						+ e.getAttribute(a.getName()));
			}
		}

		System.out.println();
	}

	private void printSchemaJGraLab(Schema schem) {
		System.out
				.println("----------------- Printing Schema -------------------");
		System.out.println("Schema: ");
		System.out.println(" - Qualified name: " + schem.getQualifiedName());
		System.out.println(" - Class of Schema: " + schem.getClass());
		System.out.println();

		System.out.println("GraphClass: ");
		System.out.println(" - " + schem.getGraphClass() + " : "
				+ schem.getGraphClass().getClass());
		System.out.println(" - Attributes: ");
		for (Attribute at : schem.getGraphClass().getAttributeList()) {
			System.out.println("    + " + at.getName() + " : " + at.getClass()
					+ " with Domain of Class " + at.getDomain().getClass());
		}
		System.out.println();

		System.out.println("VertexClasses:");
		for (VertexClass vc : schem.getGraphClass().getVertexClasses()) {
			System.out.println(" - " + vc + " : " + vc.getClass());
			for (Attribute at : vc.getAttributeList()) {
				System.out.println("    + " + at.getName() + " : "
						+ at.getClass() + " with Domain of Class "
						+ at.getDomain().getClass());
			}
		}
		System.out.println();

		System.out.println("EdgeClasses:");
		for (EdgeClass ec : schem.getGraphClass().getEdgeClasses()) {
			System.out.println(" - " + ec + " : " + ec.getClass());
			for (Attribute at : ec.getAttributeList()) {
				System.out.println("    + " + at.getName() + " : "
						+ at.getClass() + " with Domain of Class "
						+ at.getDomain().getClass());
			}
		}
		System.out.println();

		System.out.println("Packages");
		Vector<de.uni_koblenz.jgralab.schema.Package> packages = new Vector<de.uni_koblenz.jgralab.schema.Package>();
		for (de.uni_koblenz.jgralab.schema.Package p : packages) {
			System.out.println(" - " + p + " : " + p.getClass());
		}

		System.out.println("\n\n");
	}

	public void getPackages(
			Vector<de.uni_koblenz.jgralab.schema.Package> packages,
			de.uni_koblenz.jgralab.schema.Package defpack) {
		packages.add(defpack);
		for (de.uni_koblenz.jgralab.schema.Package p : defpack.getSubPackages()) {
			this.getPackages(packages, p);
		}
	}

	@Test
	public void loadModelWithEMFAPI() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new GrEMFResourceFactoryImpl());

		Resource schemaResource = rs.getResource(
				URI.createURI(System.getProperty("user.dir") + File.separator
						+ "models" + File.separator + "universityExt.gremf"),
				true);
		this.printSchemaEMF(schemaResource);

		Resource instance = rs.getResource(
				URI.createURI(System.getProperty("user.dir") + File.separator
						+ "models" + File.separator + "model1.universityExt"),
				true);

		this.printModelEMF(instance);

	}

	private void printModelEMF(Resource instance) {
		System.out
				.println("----------------- Printing model with EMF -------------------");
		TreeIterator<EObject> i = instance.getAllContents();
		System.out.println("EObjects in resource: ");
		while (i.hasNext()) {
			EObject eObj = i.next();
			System.out.println(" - " + eObj + " : " + eObj.getClass());
			for (EStructuralFeature feat : eObj.eClass()
					.getEAllStructuralFeatures()) {
				System.out.println("   + " + feat.getName() + " = "
						+ eObj.eGet(feat));
			}
		}
		System.out.println();
	}

	private void printSchemaEMF(Resource resource) {
		System.out
				.println("----------------- Printing Ecore model -------------------");
		EPackage pack = (EPackage) resource.getContents().get(0);
		this.printEPackage(pack);
		System.out.println();
	}

	private void printEPackage(EPackage pack) {
		System.out.println("EPackage " + pack.getName());
		for (EClassifier ec : pack.getEClassifiers()) {
			if (ec instanceof EClass) {
				System.out.println(" - EClass: " + ec + " : " + ec.getClass());
				for (EAttribute eat : ((EClass) ec).getEAttributes()) {
					System.out.println("    + " + eat.getName() + " : "
							+ eat.getClass() + " with EDataType "
							+ eat.getEType());
				}
				for (EReference eref : ((EClass) ec).getEReferences()) {
					System.out.println("    + " + eref.getName() + " : "
							+ eref.getClass() + " links to " + eref.getEType());
				}
			} else {
				System.out.println(" - EDataType: " + ec + " : "
						+ ec.getClass());
			}
		}

		for (EPackage sub : pack.getESubpackages()) {
			this.printEPackage(sub);
		}
	}

	@Test
	public void buildModelWithJGraLabAPI() throws GraphIOException {
		Schema schema = GrEMFIO.loadSchemaFromFile(System
				.getProperty("user.dir")
				+ File.separator
				+ "models"
				+ File.separator + "university_graph.tg");
		Graph graph = schema.createGraph(ImplementationType.GENERIC);

		Vertex vLecture1 = graph.createVertex(schema.getGraphClass()
				.getVertexClass("courses.Lecture"));
		vLecture1.setAttribute("semester", graph.getEnumConstant(
				(EnumDomain) schema.getDomain("organisation.Semester"), "SS"));
		vLecture1.setAttribute("year", 2012);

		Vertex vStudent1 = graph.createVertex(schema.getGraphClass()
				.getVertexClass("persons.Student"));
		vStudent1.setAttribute("name", "Hugo");
		vStudent1.setAttribute("matriculationNumber", 12455);

		graph.createEdge(
				schema.getGraphClass().getEdgeClass("courses.AttendsLecture"),
				vStudent1, vLecture1);

		Vertex vExam1 = graph.createVertex(schema.getGraphClass()
				.getVertexClass("courses.Exam"));
		ArrayPMap<String, Object> datecomps = ArrayPMap.empty();
		datecomps = datecomps.plus("day", 2);
		datecomps = datecomps.plus("month", 3);
		datecomps = datecomps.plus("year", 2012);
		vExam1.setAttribute("date",
				graph.createRecord(
						(RecordDomain) schema.getDomain("organisation.Date"),
						datecomps));

		EdgeClass writesExamEC = schema.getGraphClass().getEdgeClass(
				"courses.WritesExam");
		Edge eWritesExam = graph.createEdge(writesExamEC, vStudent1, vExam1);
		eWritesExam.setAttribute("mark", 2);

		this.printModelJGraLab(graph);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void buildModelWithEMFAPI() throws IOException {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new GrEMFResourceFactoryImpl());

		Resource schemaResource = rs.getResource(
				URI.createURI(System.getProperty("user.dir") + File.separator
						+ "models" + File.separator + "universityExt.gremf"),
				true);

		EPackage rootPackage = (EPackage) schemaResource.getContents().get(0);
		EPackage administrationPackage = null;
		EPackage peoplePackage = null;
		EPackage connectionPackage = null;

		for (EPackage sub : rootPackage.getESubpackages().get(0)
				.getESubpackages()) {
			if (sub.getName().equals("administration")) {
				administrationPackage = sub;
			} else if (sub.getName().equals("people")) {
				peoplePackage = sub;
			} else if (sub.getName().equals("connection")) {
				connectionPackage = sub;
			}
		}

		EClass studentEClass = (EClass) peoplePackage.getEClassifier("Student");
		EClass personEClass = (EClass) peoplePackage.getEClassifier("Person");
		EObject eobStudent1 = peoplePackage.getEFactoryInstance().create(
				studentEClass);
		eobStudent1.eSet(
				studentEClass.getEStructuralFeature("matriculationnumber"),
				123456l);
		eobStudent1.eSet(personEClass.getEStructuralFeature("name"), "Hugo");

		EClass courseEClass = (EClass) administrationPackage
				.getEClassifier("Course");
		EObject eobCourse1 = administrationPackage.getEFactoryInstance()
				.create(courseEClass);
		eobCourse1.eSet(courseEClass.getEStructuralFeature("title"),
				"A very interesting course");
		eobCourse1.eSet(courseEClass.getEStructuralFeature("startOfCourse"),
				new Date(323423));
		eobCourse1.eSet(courseEClass.getEStructuralFeature("endOfCourse"),
				new Date(12345678));

		((EList<EObject>) eobStudent1.eGet(studentEClass
				.getEStructuralFeature("grEMF_courseVisit"))).add(eobCourse1);

		EObject eobVisits = ((EList<EObject>) eobStudent1.eGet(studentEClass
				.getEStructuralFeature("courseVisit"))).get(0);

		EEnum motEEnum = (EEnum) connectionPackage.getEClassifier("Motivation");
		EClass visitsEClass = (EClass) connectionPackage
				.getEClassifier("Visits");
		eobVisits.eSet(visitsEClass.getEStructuralFeature("motivation"),
				motEEnum.getEEnumLiteral("HIGH_INTEREST"));

		EClass lectureEClass = (EClass) administrationPackage
				.getEClassifier("Lecture");
		EObject eobLecture1 = administrationPackage.getEFactoryInstance()
				.create(lectureEClass);
		eobLecture1.eSet(lectureEClass.getEStructuralFeature("title"),
				"Something around");
		ArrayList<String> caplist = new ArrayList<String>();
		caplist.add("Cap1");
		caplist.add("Cap2");
		caplist.add("Cap3");
		eobLecture1.eSet(lectureEClass.getEStructuralFeature("captions"),
				caplist);
		((EList<String>) eobLecture1.eGet(lectureEClass
				.getEStructuralFeature("captions"))).add("Cap4");

		eobCourse1.eSet(courseEClass.getEStructuralFeature("lecture"),
				eobLecture1);

		Resource instance = rs.createResource(URI.createURI(System
				.getProperty("user.dir")
				+ File.separator
				+ "models"
				+ File.separator
				+ "generated"
				+ File.separator
				+ "modelTest_universityExt.xmi"));
		instance.getContents().add(eobStudent1);
		instance.getContents().add(eobCourse1);
		instance.getContents().add(eobVisits);
		instance.getContents().add(eobLecture1);
		this.printModelEMF(instance);
		instance.save(null);

	}
}

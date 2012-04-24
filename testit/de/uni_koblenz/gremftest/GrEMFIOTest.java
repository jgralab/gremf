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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Test;

import de.uni_koblenz.gremf.GrEMFIO;
import de.uni_koblenz.gremf.impl.GrEMFVertexImpl;
import de.uni_koblenz.gremf.resource.GrEMFResourceFactoryImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFPackageImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFSchemaImpl;
import de.uni_koblenz.gremftest.notifications.TestAdapter;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;

public class GrEMFIOTest {

	@Test
	public void schemaTest() throws GraphIOException {

		System.out.println("-----------------------------------------");
		System.out.println();
		System.out.println("Test load Schema and print Classes of Elements\n");

		Schema schem = GrEMFIO.loadSchemaFromFile(System
				.getProperty("user.dir")
				+ File.separator
				+ "models"
				+ File.separator + "university_graph.tg");

		System.out.println(schem.getQualifiedName());
		System.out.println(((GrEMFPackageImpl) schem.getDefaultPackage())
				.getNsPrefix());
		System.out.println(((GrEMFPackageImpl) schem.getDefaultPackage())
				.getNsURI());

		System.out.println(schem + " : " + schem.getClass());
		System.out.println(schem.getGraphClass() + " : "
				+ schem.getGraphClass().getClass());
		for (VertexClass vc : schem.getGraphClass().getVertexClasses()) {
			System.out.println(vc + " : " + vc.getClass());
			for (Attribute a : vc.getAttributeList()) {
				System.out.println("   - " + a + " : " + a.getClass()
						+ "     :      " + a.getDomain().getClass());
			}
		}
		for (EdgeClass ec : schem.getGraphClass().getEdgeClasses()) {
			System.out.println(ec + " : " + ec.getClass());
		}
		Vector<de.uni_koblenz.jgralab.schema.Package> packages = new Vector<de.uni_koblenz.jgralab.schema.Package>();
		for (de.uni_koblenz.jgralab.schema.Package p : packages) {
			System.out.println(p + " : " + p.getClass());
		}
		System.out.println();
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
	public void instanceTest() throws GraphIOException {
		System.out.println("-----------------------------------------");
		System.out.println();
		System.out.println("Test load Graph and print Classes of Elements\n");
		Schema schema = GrEMFIO
				.loadSchemaFromFile("models/university_graph.tg");
		System.out.println(schema.getClass());
		Graph g = GraphIO.loadGraphFromFile("models/university_graph.tg",
				schema, ImplementationType.GENERIC, null);

		System.out.println(((GrEMFPackageImpl) schema.getDefaultPackage())
				.getNsURI());
		System.out.println(g.getClass());
		for (Vertex v : g.vertices()) {
			System.out.println(v + "  :::  " + v.getClass());
			for (Attribute a : v.getAttributedElementClass().getAttributeList()) {
				System.out.println("   - " + a.getName() + " : "
						+ a.getDomain() + " : " + a.getDomain().getClass());
			}
		}
		for (Edge e : g.edges()) {
			System.out.println(e + "   :::  " + e.getClass());
			System.out.println(e.getReversedEdge() + "   :::  "
					+ e.getReversedEdge().getClass());
		}

		GrEMFVertexImpl vertex = (GrEMFVertexImpl) g.getFirstVertex(schema
				.getGraphClass().getVertexClasses().get(0));
		System.out.println("Cross References of " + vertex + ": "
				+ vertex.eCrossReferences());
		System.out.println();
	}

	@Test
	public void testSchemaTraversingEMF() throws GraphIOException {
		System.out.println("-----------------------------------------");
		System.out.println();
		System.out.println("Test traverse schema with EMF methods\n");

		Schema schem = GrEMFIO.loadSchemaFromFile("models/university_graph.tg");

		for (VertexClass v : schem.getGraphClass().getVertexClasses()) {

			EClass eclass = (EClass) v;
			System.out.println(eclass.getName());
			for (EStructuralFeature feature : eclass
					.getEAllStructuralFeatures()) {
				System.out.println(" -- " + feature.getName() + " : "
						+ feature.getEType());
			}
		}
		for (EdgeClass ec : schem.getGraphClass().getEdgeClasses()) {
			if (ec instanceof EClass) {

				EClass eclass = (EClass) ec;
				System.out.println(eclass.getName());
				for (EStructuralFeature feature : eclass
						.getEAllStructuralFeatures()) {
					System.out.println(" -- " + feature.getName() + " : "
							+ feature.getEType());
				}
			}
		}

		System.out.println();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInstanceTraversingEMF() throws GraphIOException {
		System.out.println("-----------------------------------------");
		System.out.println();
		System.out.println("Test traversing Graph with EMF methods\n");
		Schema schema = GrEMFIO
				.loadSchemaFromFile("models/university_graph.tg");
		System.out.println(schema.getClass());
		Graph g = GraphIO.loadGraphFromFile("models/university_graph.tg",
				schema, ImplementationType.GENERIC, null);

		EObject graphEObject = (EObject) g;

		for (EObject o : (Iterable<EObject>) graphEObject.eGet(graphEObject
				.eClass().getEStructuralFeature("vertices"))) {
			System.out.println(o);
			for (EStructuralFeature feature : o.eClass()
					.getEAllStructuralFeatures()) {
				System.out.println("  -- " + feature.getName() + " : "
						+ o.eGet(feature));
			}
		}

		for (Edge e : g.edges()) {
			if (e.getAttributedElementClass() instanceof EClass) {
				EObject o = (EObject) e;
				System.out.println(o);
				for (EStructuralFeature feature : o.eClass()
						.getEAllStructuralFeatures()) {
					System.out.println("  -- " + feature.getName() + " : "
							+ o.eGet(feature));
				}
			}
		}

		System.out.println();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUseEMF() throws GraphIOException {
		System.out.println("-----------------------------------------");
		System.out.println();
		System.out.println("Test use EMF methods to build graph\n");
		Schema schema = GrEMFIO
				.loadSchemaFromFile("models/university_graph.tg");

		EPackage pack = (EPackage) (schema.getPackage("courses"));

		EClass lectureEClass = (EClass) pack.getEClassifier("Lecture");

		System.out.println(schema.isFinished());
		EObject ob = pack.getEFactoryInstance().create(lectureEClass);

		System.out.println(ob + "  :  " + ob.getClass());
		System.out.println(((GrEMFVertexImpl) ob).getGraph());

		EPackage personsPack = (EPackage) (schema.getPackage("persons"));

		EClass professorEClass = (EClass) personsPack
				.getEClassifier("Professor");

		EObject prof = personsPack.getEFactoryInstance()
				.create(professorEClass);

		System.out.println(prof + "  :  " + prof.getClass());

		EStructuralFeature lecture = professorEClass
				.getEStructuralFeature("lecture");

		((List<EObject>) prof.eGet(lecture)).add(ob);

		EObject lect2 = pack.getEFactoryInstance().create(lectureEClass);

		((List<EObject>) prof.eGet(lecture)).add(lect2);

		EObject lect3 = pack.getEFactoryInstance().create(lectureEClass);

		((List<EObject>) prof.eGet(lecture)).add(lect3);

		System.out.println(prof.eGet(lecture));

		System.out.println(((List<EObject>) prof.eGet(lecture)).subList(0, 1));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEMF2() throws GraphIOException {
		Schema schema = GrEMFIO.loadSchemaFromFile("models/citymapschema.tg");

		EPackage pack = (EPackage) (schema.getDefaultPackage());

		EClass inersec = (EClass) pack.getEClassifier("Intersection");

		EObject ob = pack.getEFactoryInstance().create(inersec);
		ob.eAdapters().add(new TestAdapter());

		EClass superJunc = (EClass) pack.getEClassifier("Junction");

		EStructuralFeature feat = superJunc.getEStructuralFeature("test");
		System.out.println(feat + " : " + feat.getClass());

		System.out.println(ob.eGet(feat));

		ArrayList<String> list = new ArrayList<String>();
		list.add("hut");
		list.add("tut");
		list.add("guck");
		ob.eSet(feat, list);

		System.out.println(ob.eGet(feat));

		((List<String>) ob.eGet(feat)).add("hans");
		System.out.println(ob.eGet(feat));

		((List<String>) ob.eGet(feat)).add(1, "indTest");
		System.out.println(ob.eGet(feat));

		((List<String>) ob.eGet(feat)).remove(0);
		System.out.println(ob.eGet(feat));

		((List<String>) ob.eGet(feat)).remove("indTest");
		System.out.println(ob.eGet(feat));

		GraphIO.saveGraphToFile(((GrEMFVertexImpl) ob).getGraph(), "models"
				+ File.separator + "generated" + File.separator
				+ "citymaptestgraph.tg", null);

	}

	@Test
	public void testLoadWithDate() throws GraphIOException {
		Schema schema = GrEMFIO
				.loadSchemaFromFile("models/citymapgraph_modified.tg");

		GraphIO.loadGraphFromFile("models/citymapgraph_modified.tg", schema,
				ImplementationType.GENERIC, null);

	}

	@Test
	public void testUniversitySchema() throws GraphIOException {
		ResourceSet load_resourceSet = new ResourceSetImpl();
		load_resourceSet.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put("gremf", new GrEMFResourceFactoryImpl());

		Resource schemaRes = load_resourceSet.getResource(
				URI.createURI("models" + File.separator + "university.gremf"),
				true);

		GrEMFSchemaImpl schema = (GrEMFSchemaImpl) ((GrEMFPackageImpl) schemaRes
				.getContents().get(0)).getSchema();

		Graph g = schema.createGraph(ImplementationType.GENERIC);
		g.save("models/generated/jgraviewuni.tg");

	}
}

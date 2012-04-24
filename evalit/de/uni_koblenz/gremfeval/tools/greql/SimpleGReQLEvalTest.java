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
package de.uni_koblenz.gremfeval.tools.greql;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_koblenz.gremf.resource.GrEMFResourceFactoryImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFPackageImpl;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;

public class SimpleGReQLEvalTest {

	public static void main(String[] args) throws IOException {
		ResourceSet load_resourceSet = new ResourceSetImpl();

		load_resourceSet.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put("ecore", new EcoreResourceFactoryImpl());

		load_resourceSet.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put("gremf", new EcoreResourceFactoryImpl());
		Resource res = load_resourceSet.getResource(
				URI.createURI(System.getProperty("user.dir") + File.separator
						+ "models" + File.separator + "java.ecore"), true);

		for (EObject o : res.getContents()) {
			clearEOperations(o);

		}

		Resource second = load_resourceSet.createResource(URI.createURI(System
				.getProperty("user.dir")
				+ File.separator
				+ "models"
				+ File.separator + "java_woEOperations.gremf"));
		System.out.println(second);
		ArrayList<EObject> c = new ArrayList<EObject>();
		for (EObject o : res.getContents()) {
			c.add(o);
		}
		for (EObject o : c) {
			second.getContents().add(o);
		}
		// res.setURI();
		second.save(null);

	}

	private static void clearEOperations(EObject o) {
		if (o instanceof EClass) {
			((EClass) o).getEOperations().clear();
		}

		for (EObject c : o.eContents()) {
			clearEOperations(c);
		}
	}

	private static Graph graph;

	private static Resource schemaResource;
	private static Resource instanceResource;

	private static GreqlEvaluator evaluator;

	@BeforeClass
	public static void setUp() throws IOException {

		// EMF -> JGraLab

		Float f1 = 1.3f;
		Double d1 = (double) f1;

		Float f2 = 1.3f;
		Object ob2 = f2;
		double d2 = (Float) ob2;

		Float f3 = 1.3f;
		Object ob3 = f3;
		double d3 = ((Number) ob3).doubleValue();

		// JGraLab -> EMF

		double do1 = 1.3d;
		Float fl1 = (float) do1;

		double do2 = 1.3d;
		Object obj2 = do2;
		Float flo2 = (float) ((double) ((Double) obj2));

		double do3 = 1.3d;
		Object obj3 = do3;
		Float flo3 = ((Number) obj3).floatValue();

		//

		System.err.println(f1 + " " + d1);
		System.err.println(f2 + " " + ob2 + " " + d2);
		System.err.println(f3 + " " + ob3 + " " + d3);

		System.err.println(do1 + " " + fl1);
		System.err.println(do2 + " " + obj2 + " " + flo2);
		System.err.println(do3 + " " + obj3 + " " + flo3);

		// ----------------------------------------

		ResourceSet load_resourceSet = new ResourceSetImpl();

		load_resourceSet.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put("gremf", new GrEMFResourceFactoryImpl());
		load_resourceSet.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put("javamodel", new GrEMFResourceFactoryImpl());

		schemaResource = load_resourceSet.getResource(
				URI.createURI(System.getProperty("user.dir") + File.separator
						+ "models" + File.separator + "java.gremf"), true);

		EPackage p = (EPackage) schemaResource.getContents().get(0);
		load_resourceSet.getPackageRegistry().put(p.getNsURI(), p);

		System.out.println(((GrEMFPackageImpl) p).getSchema()
				.getPackagePrefix());
		System.out.println(((GrEMFPackageImpl) p).getSchema()
				.getQualifiedName());
		System.out.println(((GrEMFPackageImpl) p).getSchema().getName());
		System.out.println(((GrEMFPackageImpl) p).getSchema().getGraphClass()
				.getQualifiedName());

		instanceResource = load_resourceSet
				.getResource(
						URI.createURI(System.getProperty("user.dir")
								+ File.separator + "models" + File.separator
								+ "model1.javamodel"), true);
		graph = (Graph) instanceResource.getContents().get(0);

		evaluator = new GreqlEvaluator("", graph, new HashMap<String, Object>());

	}

	@Test
	public void testCountOfVariables() {
		System.out.println("Test count of variables");
		evaluator.setQuery("count(V{variables.Variable})");
		evaluator.startEvaluation();
		System.out.println(evaluator.getResult());
	}

	@Test
	public void testInterfaces() {
		System.out.println("Test get list of interfaces");
		evaluator.setQuery("V{classifiers.Interface}");
		evaluator.startEvaluation();
		@SuppressWarnings("unchecked")
		Collection<EObject> list = (Collection<EObject>) evaluator.getResult();
		System.out.println(list.size() + " interfaces found");

	}

	@Test
	public void testInterfacesOfGrEMF() {
		System.out.println("Test find all interfaces starting with GrEMF");
		evaluator.setQuery("from v:V{classifiers.Interface} "
				+ "with isDefined(v.name) and startsWith(\"GrEMF\", v.name) "
				+ "reportList v, v.name end");
		evaluator.startEvaluation();
		System.out.println(evaluator.getResult());
	}

	@Test
	public void testInterfacesInGrEMFPackages() {
		System.out
				.println("Test find all interfaces in a compilation unit starting with de.uni_koblenz.gremf");
		evaluator
				.setQuery("from v:V{classifiers.Interface} "
						+ "with isDefined(theElement(v--<>)) and startsWith(\"de.uni_koblenz.gremf\",theElement(v--<>).name) "
						+ "reportList v.name, theElement(v--<>).name end");
		evaluator.startEvaluation();
		System.out.println(evaluator.getResult());
	}

	@Test
	public void testInterfacesSuperTypes() {
		System.out.println("Test find all interfaces and their superclasses");
		evaluator
				.setQuery("from v:V{classifiers.Interface} "
						+ "with isDefined(theElement(v--<>)) and startsWith(\"de.uni_koblenz.gremf\",theElement(v--<>).name) "
						+ "reportList v.name, theElement(v--<>).name, v<>--{extends}<>--{classifierReferences}-->{target} end");
		evaluator.startEvaluation();
		System.out.println(evaluator.getResult());
	}

	@Test
	public void testWikiExample() {
		System.out.println("Test find all interfaces and their superclasses");
		evaluator.setQuery("from " + "i:V{classifiers.Interface} " + "with "
				+ "isDefined(i.name) " + "and "
				+ "startsWith(\"GrEMF\",i.name) " + "reportList " + "i, "
				+ "i(<>--{extends}<>--{classifierReferences}-->{target})* "
				+ "end");
		evaluator.startEvaluation();
		for (Object o : ((List<?>) evaluator.getResult())) {
			System.out.println("- " + o);
		}
	}

	@Test
	public void testWikiExample2() {
		System.out.println("Test find all interfaces and their superclasses");
		evaluator
				.setQuery("from "
						+ "i:V{classifiers.Interface} "
						+ "with "
						+ "isDefined(i.name) "
						+ "and "
						+ "startsWith(\"GrEMF\",i.name) "
						+ "reportList "
						+ "i.name, "
						+ " theElement(i<>--{extends}<>--{classifierReferences}-->{target}).name "
						+ "end");
		evaluator.startEvaluation();
		for (Object o : ((List<?>) evaluator.getResult())) {
			System.out.println("- " + o);
		}
	}
}

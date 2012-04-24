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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.Before;
import org.junit.Test;

import de.uni_koblenz.gremf.GrEMFIO;
import de.uni_koblenz.gremf.resource.TgResourceImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFVertexClassImpl;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.schema.Schema;

public class GrEMFVertexImplTestContainment {

	private EClass ownerEC;
	private EClass childEC;
	private EAttribute ownerAtt;
	private EAttribute childAtt;
	private EReference owner2childRef;
	private EReference child2ownerRef;

	private Resource res;

	private EObject ownerObj;
	private EObject childObj1;
	private EObject childObj2;

	private static int version = 1;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws GraphIOException {
		Schema s = null;
		if (version == 0) {
			EPackage pack = EcoreFactory.eINSTANCE.createEPackage();

			this.ownerEC = EcoreFactory.eINSTANCE.createEClass();
			this.ownerEC.setName("Person");
			this.ownerAtt = EcoreFactory.eINSTANCE.createEAttribute();
			this.ownerAtt.setName("name");
			this.ownerAtt.setEType(EcorePackage.eINSTANCE.getEString());
			this.ownerEC.getEStructuralFeatures().add(this.ownerAtt);
			pack.getEClassifiers().add(this.ownerEC);

			this.childEC = EcoreFactory.eINSTANCE.createEClass();
			this.childEC.setName("Task");
			this.childAtt = EcoreFactory.eINSTANCE.createEAttribute();
			this.childAtt.setName("subTasks");
			this.childAtt.setEType(EcorePackage.eINSTANCE.getEString());
			this.childAtt.setUpperBound(-1);
			this.childEC.getEStructuralFeatures().add(this.childAtt);
			pack.getEClassifiers().add(this.childEC);

			this.owner2childRef = EcoreFactory.eINSTANCE.createEReference();
			this.owner2childRef.setName("tasks");
			this.owner2childRef.setContainment(true);
			this.owner2childRef.setUpperBound(-1);
			this.owner2childRef.setEType(this.childEC);
			this.ownerEC.getEStructuralFeatures().add(this.owner2childRef);

			this.child2ownerRef = EcoreFactory.eINSTANCE.createEReference();
			this.child2ownerRef.setName("persons");
			this.child2ownerRef.setEType(this.ownerEC);
			this.childEC.getEStructuralFeatures().add(this.child2ownerRef);

			this.owner2childRef.setEOpposite(this.child2ownerRef);
			this.child2ownerRef.setEOpposite(this.owner2childRef);
		} else {
			s = GrEMFIO.loadSchemaFromFile(System.getProperty("user.dir")
					+ File.separator + "models" + File.separator
					+ "containmentTestSchema.tg");
			GrEMFVertexClassImpl vc = s.getAttributedElementClass("Person");
			this.ownerEC = vc;
			vc = s.getAttributedElementClass("Task");
			this.childEC = vc;
			this.ownerAtt = (EAttribute) this.ownerEC
					.getEStructuralFeature("name");
			this.childAtt = (EAttribute) this.childEC
					.getEStructuralFeature("subTasks");
			this.owner2childRef = (EReference) this.ownerEC
					.getEStructuralFeature("tasks");
			this.child2ownerRef = (EReference) this.childEC
					.getEStructuralFeature("persons");

		}
		this.ownerObj = EcoreUtil.create(this.ownerEC);
		this.childObj1 = EcoreUtil.create(this.childEC);
		this.childObj2 = EcoreUtil.create(this.childEC);

		this.ownerObj.eSet(this.ownerAtt, "Hugo");
		ArrayList<String> subTasks = new ArrayList<String>();
		subTasks.add("SubTask1");
		subTasks.add("SubTask2");
		this.childObj1.eSet(this.childAtt, subTasks);

		subTasks.add("SubTask3");
		this.childObj2.eSet(this.childAtt, subTasks);

		((List<EObject>) this.ownerObj.eGet(this.owner2childRef))
				.add(this.childObj1);

		((List<EObject>) this.ownerObj.eGet(this.owner2childRef))
				.add(this.childObj2);

		this.res = new TgResourceImpl(
				URI.createFileURI("models/containmentTestSchema.tg"), s);
		// Resource res = new XMIResourceImpl(URI.createFileURI("d"));
		this.res.getContents().add(this.ownerObj);
		//
		// Resource res2 = new XMIResourceImpl(URI.createFileURI("igit"));
		// res2.getContents().add(this.childObj1);
	}

	@Test
	public void testEContents() {
		assertEquals(2, this.ownerObj.eContents().size());
		assertEquals(true, this.ownerObj.eContents().contains(this.childObj1));
		assertEquals(true, this.ownerObj.eContents().contains(this.childObj2));
	}

	@Test
	public void testEAllContents() {
		Iterator<?> it = this.ownerObj.eAllContents();
		int i = 0;
		while (it.hasNext()) {
			it.next();
			i++;
		}
		assertEquals(2, i);
	}

	@Test
	public void testECrossReferences() {
		assertEquals(true, this.ownerObj.eCrossReferences().isEmpty());
		assertEquals(true, this.childObj1.eCrossReferences().isEmpty());
	}

	@Test
	public void testEInternalContainer() {
		assertEquals(this.ownerObj,
				((InternalEObject) this.childObj1).eInternalContainer());
		assertEquals(this.ownerObj,
				((InternalEObject) this.childObj2).eInternalContainer());
	}

	@Test
	public void testEResource() {
		assertEquals(this.res, this.childObj1.eResource());
		assertEquals(this.res, this.childObj2.eResource());
		assertEquals(this.res, this.ownerObj.eResource());
	}

}

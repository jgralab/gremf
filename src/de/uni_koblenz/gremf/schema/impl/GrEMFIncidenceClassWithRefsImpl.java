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
package de.uni_koblenz.gremf.schema.impl;

import org.eclipse.emf.common.notify.impl.NotificationChainImpl;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;

import de.uni_koblenz.jgralab.schema.AggregationKind;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.IncidenceDirection;
import de.uni_koblenz.jgralab.schema.VertexClass;

public class GrEMFIncidenceClassWithRefsImpl extends GrEMFIncidenceClassImpl {

	public GrEMFIncidenceClassWithRefsImpl(EdgeClass edgeCls,
			VertexClass vrtxCls, String rolename, int min, int max,
			IncidenceDirection dir, AggregationKind aggr) {
		super(edgeCls, vrtxCls, rolename, min, max, dir, aggr);

		this.toEdgeClass = EcoreFactory.eINSTANCE.createEReference();
		this.toEdgeClass.setEType((EClassifier) edgeCls);
		((InternalEObject) this.toEdgeClass).eBasicSetContainer(
				(InternalEObject) vrtxCls,
				EcorePackage.ESTRUCTURAL_FEATURE__ECONTAINING_CLASS,
				new NotificationChainImpl());

		this.fromEdgeClass = EcoreFactory.eINSTANCE.createEReference();
		if (dir.equals(IncidenceDirection.IN)) {
			this.fromEdgeClass.setName("target" + edgeCls.getSimpleName());
		} else {
			this.fromEdgeClass.setName("source" + edgeCls.getSimpleName());
		}
		this.fromEdgeClass.setLowerBound(1);
		this.fromEdgeClass.setUpperBound(1);
		this.fromEdgeClass.setEType((EClassifier) vrtxCls);

		((InternalEObject) this.fromEdgeClass).eBasicSetContainer(
				(InternalEObject) edgeCls,
				EcorePackage.ESTRUCTURAL_FEATURE__ECONTAINING_CLASS,
				new NotificationChainImpl());
		this.fromEdgeClass.setEOpposite(this.toEdgeClass);
		this.toEdgeClass.setEOpposite(this.fromEdgeClass);

		this.fromEdgeClass.setChangeable(false);
		this.toEdgeClass.setChangeable(false);
		this.fromEdgeClass.setDerived(true);
		this.toEdgeClass.setDerived(true);

	}

	private EReference toEdgeClass;
	private EReference fromEdgeClass;

	public EReference getToEdgeClass() {
		return this.toEdgeClass;
	}

	public EReference getFromEdgeClass() {
		return this.fromEdgeClass;
	}

	public void clearToEdgeClass() {
		this.toEdgeClass = null;
	}

	@Override
	public boolean isTransient() {
		// this is a generated EReference (EMF view)
		return true;
	}

	@Override
	public boolean isContainment() {
		// this is a generated EReference (EMF view)
		return false;
	}

	@Override
	public boolean isContainer() {
		// this is a generated EReference (EMF view)
		return false;
	}
}

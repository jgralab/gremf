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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import de.uni_koblenz.gremf.GrEMFSchemaType;
import de.uni_koblenz.gremf.exception.FreeFloatingEdgeException;
import de.uni_koblenz.gremf.exception.UnsupportedSchemaChangeException;
import de.uni_koblenz.gremf.impl.GrEMFVertexImpl;
import de.uni_koblenz.gremf.util.EModelElementUtil;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.schema.Domain;

public class GrEMFFactoryImpl implements EFactory, GrEMFSchemaType {

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	// --------------------------------------------------------------------------
	// ---- Ecore
	// --------------------------------------------------------------------------
	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

	private GrEMFPackageImpl gremfpackage;

	private EList<EAnnotation> eAnnotations;

	public GrEMFFactoryImpl(GrEMFPackageImpl p) {
		this.gremfpackage = p;
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EModelElement
	// --------------------------------------------------------------------------

	@Override
	public EList<EAnnotation> getEAnnotations() {
		if (this.eAnnotations == null) {
			this.eAnnotations = new BasicEList<EAnnotation>();
		}
		return this.eAnnotations;
	}

	@Override
	public EAnnotation getEAnnotation(String source) {
		return EModelElementUtil.getEAnnotation(source, this.eAnnotations);
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EObject
	// --------------------------------------------------------------------------

	@Override
	public EClass eClass() {
		return EcorePackage.Literals.EFACTORY;
	}

	@Override
	public Resource eResource() {
		return null;
	}

	@Override
	public EObject eContainer() {
		return null;
	}

	@Override
	public EStructuralFeature eContainingFeature() {
		return null;
	}

	@Override
	public EReference eContainmentFeature() {
		return null;
	}

	@Override
	public EList<EObject> eContents() {
		return new BasicEList<EObject>();
	}

	@Override
	public TreeIterator<EObject> eAllContents() {
		return EcoreUtil.getAllContents(this, true);
	}

	@Override
	public boolean eIsProxy() {
		return false;
	}

	@Override
	public EList<EObject> eCrossReferences() {
		EList<EObject> list = new BasicEList<EObject>();
		list.add(this.getEPackage());
		return list;
	}

	@Override
	public Object eGet(EStructuralFeature feature) {
		if (feature.equals(EcorePackage.Literals.EFACTORY__EPACKAGE)) {
			return this.getEPackage();
		} else if (feature
				.equals(EcorePackage.Literals.EMODEL_ELEMENT__EANNOTATIONS)) {
			return this.getEAnnotations();
		}
		throw new IllegalArgumentException("The feature '" + feature.getName()
				+ "' is not a valid feature");
	}

	@Override
	public Object eGet(EStructuralFeature feature, boolean resolve) {
		return this.eGet(feature);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void eSet(EStructuralFeature feature, Object newValue) {
		if (feature.equals(EcorePackage.Literals.EMODEL_ELEMENT__EANNOTATIONS)) {
			this.getEAnnotations().clear();
			this.getEAnnotations().addAll(
					(Collection<? extends EAnnotation>) newValue);
		} else if (feature
				.equals(EcorePackage.Literals.EMODEL_ELEMENT__EANNOTATIONS)) {
			throw new UnsupportedOperationException(
					"Changing the EPackage of an EFactory is not allowed");
		}
		throw new IllegalArgumentException("The feature '" + feature.getName()
				+ "' is not a valid feature");
	}

	@Override
	public boolean eIsSet(EStructuralFeature feature) {
		if (feature.equals(EcorePackage.Literals.EFACTORY__EPACKAGE)) {
			return this.getEPackage() != null;
		} else if (feature
				.equals(EcorePackage.Literals.EMODEL_ELEMENT__EANNOTATIONS)) {
			return (this.getEAnnotations() != null)
					&& !this.getEAnnotations().isEmpty();
		}
		throw new IllegalArgumentException("The feature '" + feature.getName()
				+ "' is not a valid feature");
	}

	@Override
	public void eUnset(EStructuralFeature feature) {
		if (feature.equals(EcorePackage.Literals.EFACTORY__EPACKAGE)) {
			throw new UnsupportedOperationException(
					"Changing the EPackage of an EFactory is not allowed");
		} else if (feature
				.equals(EcorePackage.Literals.EMODEL_ELEMENT__EANNOTATIONS)) {
			this.getEAnnotations().clear();
		} else {
			throw new IllegalArgumentException("The feature '"
					+ feature.getName() + "' is not a valid feature");
		}

	}

	@Override
	public Object eInvoke(EOperation operation, EList<?> arguments)
			throws InvocationTargetException {
		throw new UnsupportedOperationException(
				"EOperations are not supported by grEMF.");
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.ENotifier
	// --------------------------------------------------------------------------

	@Override
	public EList<Adapter> eAdapters() {
		return ECollections.unmodifiableEList(new BasicEList<Adapter>());
	}

	@Override
	public boolean eDeliver() {
		return false;
	}

	@Override
	public void eSetDeliver(boolean deliver) {
		// No Schema changes allowed - so no Notifications necessary
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public void eNotify(Notification notification) {
		// No Schema changes allowed - so no Notifications necessary
		throw new UnsupportedSchemaChangeException();
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EFactory
	// --------------------------------------------------------------------------

	@Override
	public EPackage getEPackage() {
		return this.gremfpackage;
	}

	@Override
	public void setEPackage(EPackage value) {
		this.gremfpackage = (GrEMFPackageImpl) value;
	}

	@Override
	public EObject create(EClass eClass) {
		if ((this.getEPackage() != eClass.getEPackage()) || eClass.isAbstract()) {
			throw new IllegalArgumentException("The class '" + eClass.getName()
					+ "' is not a valid classifier");
		}
		GrEMFSchemaImpl s = (GrEMFSchemaImpl) ((GrEMFPackageImpl) this
				.getEPackage()).getSchema();
		if (eClass instanceof GrEMFGraphClassImpl) {
			if (s.getCurrentGraph() == null) {
				return (EObject) s.createGraph(ImplementationType.GENERIC);
			} else {
				return (EObject) s.getCurrentGraph();
			}
		} else if (eClass instanceof GrEMFVertexClassImpl) {
			if (s.getCurrentGraph() == null) {
				s.createGraph(ImplementationType.GENERIC);
			}
			GrEMFVertexImpl vertex = s.getCurrentGraph().createVertex(
					(GrEMFVertexClassImpl) eClass);
			return vertex;
		} else if (eClass instanceof GrEMFEdgeClassImpl) {
			throw new FreeFloatingEdgeException();
		} else {
			throw new IllegalArgumentException("The class '" + eClass.getName()
					+ "' is not a valid classifier");
		}
	}

	@Override
	public Object createFromString(EDataType eDataType, String literalValue) {
		if ((this.getEPackage() != eDataType.getEPackage())) {
			throw new IllegalArgumentException("The EDataType '"
					+ eDataType.getName() + "' is not a valid classifier");
		}
		try {
			return ((Domain) eDataType)
					.parseGenericAttribute(GraphIO.createStringReader(
							literalValue,
							((GrEMFPackageImpl) this.getEPackage()).getSchema()));
		} catch (GraphIOException e) {
			e.printStackTrace();
		}
		return EcoreUtil.createFromString(eDataType, literalValue);
	}

	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		if ((this.getEPackage() != eDataType.getEPackage())) {
			throw new IllegalArgumentException("The EDataType '"
					+ eDataType.getName() + "' is not a valid classifier");
		}
		try {
			GraphIO io = GraphIO.createStringWriter(((GrEMFPackageImpl) this
					.getEPackage()).getSchema());
			if (eDataType instanceof EEnum) {
				return instanceValue.toString();
			}
			((Domain) eDataType).serializeGenericAttribute(io, instanceValue);
			return io.getStringWriterResult();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GraphIOException e) {
			e.printStackTrace();
		}
		return EcoreUtil.convertToString(eDataType, instanceValue);
	}

}

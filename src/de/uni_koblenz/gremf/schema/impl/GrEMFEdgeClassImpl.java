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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.BasicNotifierImpl.EAdapterList;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Internal;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData.EClassifierExtendedMetaData;
import org.eclipse.emf.ecore.util.BasicInternalEList;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.ECrossReferenceEList;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.emf.ecore.util.EcoreUtil;

import de.uni_koblenz.gremf.GrEMFSchemaType;
import de.uni_koblenz.gremf.collection.GrEMFStructuralFeaturesList;
import de.uni_koblenz.gremf.exception.InvalidFeatureException;
import de.uni_koblenz.gremf.exception.UnsupportedFeature;
import de.uni_koblenz.gremf.exception.UnsupportedFeatureException;
import de.uni_koblenz.gremf.exception.UnsupportedSchemaChangeException;
import de.uni_koblenz.gremf.util.EClassUtil;
import de.uni_koblenz.gremf.util.EModelElementUtil;
import de.uni_koblenz.gremf.util.EObjectUtil;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.schema.AggregationKind;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.IncidenceDirection;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.schema.impl.EdgeClassImpl;
import de.uni_koblenz.jgralab.schema.impl.GraphClassImpl;
import de.uni_koblenz.jgralab.schema.impl.PackageImpl;

public class GrEMFEdgeClassImpl extends EdgeClassImpl implements EClass,
		EClassifierExtendedMetaData.Holder, InternalEObject, GrEMFSchemaType {

	private EList<EAnnotation> eAnnotations;
	private EList<EAttribute> eAttributes, eAllAttributes;
	private EList<EReference> eReferences, eAllReferences, eAllContainments;
	private EList<EStructuralFeature> eFeatures, eAllFeatures;
	private EList<EOperation> eOperations;
	private EList<ETypeParameter> eTypeParameters;
	private EList<EClass> eSuperTypes, eAllSuperTypes;

	private int classifierID;

	protected GrEMFEdgeClassImpl(String simpleName, Package pkg,
			GraphClass aGraphClass, VertexClass from, int fromMin, int fromMax,
			String fromRoleName, AggregationKind aggrFrom, VertexClass to,
			int toMin, int toMax, String toRoleName, AggregationKind aggrTo) {
		super(simpleName, (PackageImpl) pkg, (GraphClassImpl) aGraphClass,
				from, fromMin, fromMax, fromRoleName, aggrFrom, to, toMin,
				toMax, toRoleName, aggrTo);

		this.eAnnotations = new BasicInternalEList<EAnnotation>(
				EAnnotation.class);

		this.eOperations = new EcoreEList.UnmodifiableEList<EOperation>(this,
				EcorePackage.Literals.ECLASS__EOPERATIONS, 0,
				new BasicEList<EOperation>(0).data());
		this.eTypeParameters = new EcoreEList.UnmodifiableEList<ETypeParameter>(
				this, EcorePackage.Literals.ECLASSIFIER__ETYPE_PARAMETERS, 0,
				new BasicEList<ETypeParameter>(0).data());

	}

	@Override
	protected IncidenceClass createIncidenceClass(VertexClass vrtxCls,
			String rolename, int min, int max, IncidenceDirection dir,
			AggregationKind aggr) {
		return new GrEMFIncidenceClassWithRefsImpl(this, vrtxCls, rolename,
				min, max, dir, aggr);
	}

	@Override
	public Attribute createAttribute(String name, Domain dom) {
		return this.createAttribute(new GrEMFAttributeImpl(name, dom, this,
				null));
	}

	@Override
	public Attribute createAttribute(String name, Domain dom,
			String defaultValue) {
		return this.createAttribute(new GrEMFAttributeImpl(name, dom, this,
				defaultValue));
	}

	public void setClassifierID(int value) {
		if (!super.getSchema().isFinished()) {
			this.classifierID = value;
		} else {
			throw new UnsupportedSchemaChangeException();
		}
	}

	@Override
	protected void finish() {
		super.finish();
		if (!((GrEMFPackageImpl) this.getPackage()).isFinished()) {
			((GrEMFPackageImpl) this.getPackage()).finish();
		}

		this.eAttributes = this.getEAttributes();
		this.eAllAttributes = this.getEAllAttributes();
		this.eReferences = this.getEReferences();
		this.eAllReferences = this.getEAllReferences();
		this.eAllContainments = this.getEAllContainments();
		this.eFeatures = this.getEStructuralFeatures();
		this.eAllFeatures = this.getEAllStructuralFeatures();
		this.eSuperTypes = this.getESuperTypes();
		this.eAllSuperTypes = this.getEAllSuperTypes();
	}

	@Override
	public void delete() {
		throw new UnsupportedSchemaChangeException();
	}

	// -------- Ecore ------------------------

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EClassifier
	// --------------------------------------------------------------------------

	@Override
	public EPackage getEPackage() {
		return (EPackage) super.getPackage();
	}

	@Override
	public boolean isInstance(Object object) {
		if (object instanceof EObject) {
			EClass meta = ((EObject) object).eClass();
			return this.getName().equals(meta.getName())
					&& this.getEPackage().equals(meta.getEPackage());
		}
		return false;
	}

	@Override
	public int getClassifierID() {
		return this.classifierID;
	}

	@Override
	public Object getDefaultValue() {
		return null;
	}

	@Override
	public String getInstanceClassName() {
		// not supported
		return null;
	}

	@Override
	public void setInstanceClassName(String value) {
		throw new UnsupportedFeatureException(
				UnsupportedFeature.INSTANCE_MAPPING);
	}

	@Override
	public Class<?> getInstanceClass() {
		// not supported
		return null;
	}

	@Override
	public void setInstanceClass(Class<?> value) {
		throw new UnsupportedFeatureException(
				UnsupportedFeature.INSTANCE_MAPPING);
	}

	@Override
	public String getInstanceTypeName() {
		// not supported
		return null;
	}

	@Override
	public void setInstanceTypeName(String value) {
		throw new UnsupportedFeatureException(
				UnsupportedFeature.INSTANCE_MAPPING);
	}

	@Override
	public EList<ETypeParameter> getETypeParameters() {
		// not supported
		return this.eTypeParameters;
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.ENamedElement
	// --------------------------------------------------------------------------

	@Override
	public String getName() {
		return super.getSimpleName();
	}

	@Override
	public void setName(String value) {
		throw new UnsupportedSchemaChangeException();
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EModelElement
	// --------------------------------------------------------------------------

	@Override
	public EList<EAnnotation> getEAnnotations() {
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
		return EcorePackage.Literals.ECLASS;
	}

	@Override
	public Resource eResource() {
		return this.getEPackage().eResource();
	}

	@Override
	public EObject eContainer() {
		return (GrEMFPackageImpl) this.getPackage();
	}

	@Override
	public EStructuralFeature eContainingFeature() {
		return EcorePackage.Literals.EPACKAGE__ECLASSIFIERS;
	}

	@Override
	public EReference eContainmentFeature() {
		return EcorePackage.Literals.EPACKAGE__ECLASSIFIERS;
	}

	@Override
	public EList<EObject> eContents() {
		return EContentsEList.createEContentsEList(this);
	}

	@Override
	public TreeIterator<EObject> eAllContents() {
		return EObjectUtil.eAllContents(this);
	}

	@Override
	public boolean eIsProxy() {
		return false;
	}

	@Override
	public EList<EObject> eCrossReferences() {
		return ECrossReferenceEList.createECrossReferenceEList(this);
	}

	@Override
	public Object eGet(EStructuralFeature feature) {
		return this.eGet(feature, true);
	}

	@Override
	public Object eGet(EStructuralFeature feature, boolean resolve) {
		if (EObjectUtil.getFeatureID(this, feature) < 0) {
			throw new InvalidFeatureException(feature);
		}
		return this.eGet(feature, resolve, true);
	}

	@Override
	public void eSet(EStructuralFeature feature, Object newValue) {
		if (EObjectUtil.getFeatureID(this, feature) < 0) {
			throw new InvalidFeatureException(feature);
		}
		this.eSet(feature.getFeatureID(), newValue);
	}

	@Override
	public boolean eIsSet(EStructuralFeature feature) {
		if (EObjectUtil.getFeatureID(this, feature) < 0) {
			throw new InvalidFeatureException(feature);
		}
		return this.eIsSet(feature.getFeatureID());
	}

	@Override
	public void eUnset(EStructuralFeature feature) {
		if (EObjectUtil.getFeatureID(this, feature) < 0) {
			throw new InvalidFeatureException(feature);
		}
		this.eUnset(feature.getFeatureID());

	}

	@Override
	public Object eInvoke(EOperation operation, EList<?> arguments)
			throws InvocationTargetException {
		throw new UnsupportedFeatureException(UnsupportedFeature.OPERATION);
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.Notifier
	// --------------------------------------------------------------------------

	private EAdapterList<Adapter> eAdapters;

	@Override
	public EList<Adapter> eAdapters() {
		if (this.eAdapters == null) {
			this.eAdapters = new EAdapterList<Adapter>(this);
		}
		return this.eAdapters;
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
	// org.eclipse.emf.ecore.EClass
	// --------------------------------------------------------------------------

	@Override
	public boolean isInterface() {
		return false;
	}

	@Override
	public void setInterface(boolean value) {
		if (!super.getSchema().isFinished()) {
			// nop
		} else {
			throw new UnsupportedSchemaChangeException();
		}

	}

	@Override
	public EList<EClass> getESuperTypes() {
		if ((this.eSuperTypes == null) && !super.schema.isFinished()) {
			BasicEList<EClass> eSuperTypes = new BasicEList<EClass>(super
					.getDirectSuperClasses().size());
			for (AttributedElementClass<EdgeClass, Edge> superType : super
					.getDirectSuperClasses()) {
				eSuperTypes.add((GrEMFEdgeClassImpl) superType);
			}
			eSuperTypes.shrink();
			return new EcoreEList.UnmodifiableEList<EClass>(this,
					EcorePackage.Literals.ECLASS__ESUPER_TYPES,
					eSuperTypes.size(), eSuperTypes.data());
		} else {
			return this.eSuperTypes;
		}
	}

	@Override
	public EList<EClass> getEAllSuperTypes() {
		if ((this.eAllSuperTypes == null) && !super.schema.isFinished()) {
			BasicEList<EClass> eAllSuperTypes = new BasicEList<EClass>(super
					.getAllSuperClasses().size());
			for (AttributedElementClass<EdgeClass, Edge> superType : super
					.getAllSuperClasses()) {
				eAllSuperTypes.add((GrEMFEdgeClassImpl) superType);
			}
			eAllSuperTypes.shrink();
			return new EcoreEList.UnmodifiableEList<EClass>(this,
					EcorePackage.Literals.ECLASS__EALL_SUPER_TYPES,
					eAllSuperTypes.size(), eAllSuperTypes.data());
		} else {
			return this.eAllSuperTypes;
		}
	}

	@Override
	public boolean isSuperTypeOf(EClass someClass) {
		return super.getAllSubClasses().contains(someClass);
	}

	@Override
	public EList<EAttribute> getEAttributes() {
		if ((this.eAttributes == null) && !super.schema.isFinished()) {
			BasicEList<EAttribute> eAttrs = new BasicEList<EAttribute>(
					super.getOwnAttributeCount());
			for (Attribute a : super.getOwnAttributeList()) {
				eAttrs.add((GrEMFAttributeImpl) a);
			}
			eAttrs.shrink();
			return new EcoreEList.UnmodifiableEList<EAttribute>(this,
					EcorePackage.Literals.ECLASS__EATTRIBUTES, eAttrs.size(),
					eAttrs.data());
		} else {
			return this.eAttributes;
		}
	}

	@Override
	public EList<EAttribute> getEAllAttributes() {
		if ((this.eAllAttributes == null) && !super.schema.isFinished()) {
			BasicEList<EAttribute> eAllAttrs = new BasicEList<EAttribute>(
					super.getAttributeCount());
			for (Attribute attr : super.getAttributeList()) {
				eAllAttrs.add((GrEMFAttributeImpl) attr);
			}
			eAllAttrs.shrink();
			return new EcoreEList.UnmodifiableEList<EAttribute>(this,
					EcorePackage.Literals.ECLASS__EALL_ATTRIBUTES,
					eAllAttrs.size(), eAllAttrs.data());
		} else {
			return this.eAllAttributes;
		}
	}

	@Override
	public EAttribute getEIDAttribute() {
		for (EAttribute eAttr : this.getEAllAttributes()) {
			if (eAttr.isID()) {
				return eAttr;
			}
		}
		return null;
	}

	@Override
	public EList<EReference> getEReferences() {
		if ((this.eReferences == null) && !super.schema.isFinished()) {
			BasicEList<EReference> eRefs = new BasicEList<EReference>(2);
			// eRefs.add((GrEMFIncidenceClassImpl) super.getFrom());
			// eRefs.add((GrEMFIncidenceClassImpl) super.getTo());
			// if(this.getFrom() instanceof GrEMFIncidenceClassWithRefsImpl){
			eRefs.add(((GrEMFIncidenceClassWithRefsImpl) this.getFrom())
					.getFromEdgeClass());
			eRefs.add(((GrEMFIncidenceClassWithRefsImpl) this.getTo())
					.getFromEdgeClass());
			// }
			return new EcoreEList.UnmodifiableEList<EReference>(this,
					EcorePackage.Literals.ECLASS__EREFERENCES, eRefs.size(),
					eRefs.data());
		} else {
			return this.eReferences;
		}
	}

	@Override
	public EList<EReference> getEAllReferences() {
		if ((this.eAllReferences == null) && !super.schema.isFinished()) {
			BasicEList<EReference> eAllRefs = new BasicEList<EReference>((super
					.getAllSuperClasses().size() * 2) + 2);
			// own from and to incidences
			// eAllRefs.add((GrEMFIncidenceClassImpl) super.getFrom());
			// eAllRefs.add((GrEMFIncidenceClassImpl) super.getTo());
			eAllRefs.add(((GrEMFIncidenceClassWithRefsImpl) super.getFrom())
					.getFromEdgeClass());
			eAllRefs.add(((GrEMFIncidenceClassWithRefsImpl) super.getTo())
					.getFromEdgeClass());
			for (AttributedElementClass<EdgeClass, Edge> superType : super
					.getAllSuperClasses()) {

				EdgeClass edgeCls = (EdgeClass) superType;
				// eAllRefs.add((GrEMFIncidenceClassImpl) edgeCls.getFrom());
				// eAllRefs.add((GrEMFIncidenceClassImpl) edgeCls.getTo());
				eAllRefs.add(((GrEMFIncidenceClassWithRefsImpl) edgeCls
						.getFrom()).getFromEdgeClass());
				eAllRefs.add(((GrEMFIncidenceClassWithRefsImpl) edgeCls.getTo())
						.getFromEdgeClass());
			}
			eAllRefs.shrink();
			return new EcoreEList.UnmodifiableEList<EReference>(this,
					EcorePackage.Literals.ECLASS__EALL_REFERENCES,
					eAllRefs.size(), eAllRefs.data());
		} else {
			return this.eAllReferences;
		}
	}

	@Override
	public EList<EReference> getEAllContainments() {
		if ((this.eAllContainments == null) && !super.schema.isFinished()) {
			BasicEList<EReference> eAllConts = new BasicEList<EReference>(super
					.getAllSuperClasses().size() + 1);
			// own from and to incidences
			if (super.getFrom().getAggregationKind() == AggregationKind.COMPOSITE) {
				eAllConts.add((GrEMFIncidenceClassImpl) super.getTo());
			} else if (super.getTo().getAggregationKind() == AggregationKind.COMPOSITE) {
				eAllConts.add((GrEMFIncidenceClassImpl) super.getFrom());
			}
			for (AttributedElementClass<EdgeClass, Edge> superType : super
					.getAllSuperClasses()) {
				EdgeClass edgeCls = (EdgeClass) superType;

				if (edgeCls.getFrom().getAggregationKind() == AggregationKind.COMPOSITE) {
					eAllConts.add((GrEMFIncidenceClassImpl) edgeCls.getTo());
				} else if (edgeCls.getTo().getAggregationKind() == AggregationKind.COMPOSITE) {
					eAllConts.add((GrEMFIncidenceClassImpl) edgeCls.getFrom());
				}
			}
			eAllConts.shrink();
			return new EcoreEList.UnmodifiableEList<EReference>(this,
					EcorePackage.Literals.ECLASS__EALL_CONTAINMENTS,
					eAllConts.size(), eAllConts.data());
		} else {
			return this.eAllContainments;
		}
	}

	@Override
	public EList<EStructuralFeature> getEStructuralFeatures() {
		if ((this.eFeatures == null) && !super.schema.isFinished()) {
			BasicEList<EStructuralFeature> eFeatures = new BasicEList<EStructuralFeature>(
					super.getOwnAttributeCount() + 2);
			eFeatures.addAll(this.getEAttributes());
			eFeatures.addAll(this.getEReferences());
			eFeatures.shrink();
			return new GrEMFStructuralFeaturesList(this, eFeatures);
		} else {
			return this.eFeatures;
		}
	}

	@Override
	public EList<EStructuralFeature> getEAllStructuralFeatures() {
		if ((this.eAllFeatures == null) && !super.schema.isFinished()) {
			BasicEList<EStructuralFeature> eAllFeatures = new BasicEList<EStructuralFeature>(
					super.getAttributeCount()
							+ (super.getAllSuperClasses().size() * 2));
			eAllFeatures.addAll(this.getEAllAttributes());
			eAllFeatures.addAll(this.getEAllReferences());
			eAllFeatures.shrink();
			return new GrEMFStructuralFeaturesList(this, eAllFeatures);
		} else {
			return this.eAllFeatures;
		}
	}

	@Override
	public EStructuralFeature getEStructuralFeature(int featureID) {
		if ((featureID >= 0)
				&& (featureID < this.getEAllStructuralFeatures().size())) {
			return this.getEAllStructuralFeatures().get(featureID);
		} else {
			return null;
		}
		// int i = 0;
		// for (EStructuralFeature eFeature : this.getEAllStructuralFeatures())
		// {
		// if (featureID == i) {
		// return eFeature;
		// }
		// i++;
		// }
		// return null;
	}

	@Override
	public EStructuralFeature getEStructuralFeature(String featureName) {
		// for (EStructuralFeature eFeature : this.getEAllStructuralFeatures())
		// {
		int size = this.getEAllStructuralFeatures().size();
		for (int i = 0; i < size; i++) {
			EStructuralFeature eFeature = this.getEAllStructuralFeatures().get(
					i);
			if (featureName.equals(eFeature.getName())) {
				return eFeature;
			}
		}
		return null;
	}

	@Override
	public int getFeatureCount() {
		return this.getEAllStructuralFeatures().size();
	}

	@Override
	public int getFeatureID(EStructuralFeature feature) {
		return EClassUtil.getFeatureID(this, feature);
	}

	@Override
	public EList<EGenericType> getEGenericSuperTypes() {
		// not supported
		return null;
	}

	@Override
	public EList<EGenericType> getEAllGenericSuperTypes() {
		// not supported
		return null;
	}

	@Override
	public EList<EOperation> getEOperations() {
		// not supported, but null inflicts errors
		return this.eOperations;
	}

	@Override
	public EList<EOperation> getEAllOperations() {
		// not supported, but null inflicts errors
		return this.getEOperations();
	}

	@Override
	public int getOperationCount() {
		// not supported
		return -1;
	}

	@Override
	public EOperation getEOperation(int operationID) {
		// not supported
		return null;
	}

	@Override
	public int getOperationID(EOperation operation) {
		// not supported
		return -1;
	}

	@Override
	public EOperation getOverride(EOperation operation) {
		// not supported
		return null;
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.InternalEObject
	// --------------------------------------------------------------------------

	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
		return derivedFeatureID;
	}

	@Override
	public int eContainerFeatureID() {
		if (this.getPackage() != null) {
			return EcorePackage.Literals.ECLASSIFIER__EPACKAGE.getFeatureID();
		} else {
			return 0;
		}
	}

	@Override
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
		return baseFeatureID;
	}

	@Override
	public InternalEObject eInternalContainer() {
		return (InternalEObject) this.eContainer();
	}

	@Override
	public Internal eDirectResource() {
		// there's always a package => there cannot be a direct resource
		return null;
	}

	@Override
	public Internal eInternalResource() {
		return (Resource.Internal) this.eResource();
	}

	@Override
	public Object eGet(EStructuralFeature feature, boolean resolve,
			boolean coreType) {
		return this.eGet(feature.getFeatureID(), resolve, coreType);
	}

	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		return EClassUtil.eGet(this, featureID, resolve, coreType);
	}

	@Override
	public boolean eIsSet(int featureID) {
		return EClassUtil.eIsSet(this, featureID);
	}

	@Override
	public void eSet(int featureID, Object newValue) {
		EClassUtil.eSet(this, featureID, newValue);

	}

	@Override
	public void eUnset(int featureID) {
		EClassUtil.eUnset(this, featureID);

	}

	@Override
	public boolean eNotificationRequired() {
		return (this.eAdapters().size() > 0) && this.eDeliver();
	}

	@Override
	public String eURIFragmentSegment(EStructuralFeature eStructuralFeature,
			EObject eObject) {
		if (eObject instanceof ENamedElement) {
			return ((ENamedElement) eObject).getName();
		} else if (eObject instanceof EAnnotation) {
			return "%"
					+ URI.encodeSegment(((EAnnotation) eObject).getSource(),
							false) + "%";
		}
		throw new NullPointerException();
	}

	@Override
	public EObject eObjectForURIFragmentSegment(String uriFragmentSegment) {
		if (uriFragmentSegment.charAt(0) == '%') {
			return this.getEAnnotation(URI.decode(uriFragmentSegment).replace(
					"%", ""));
		} else {
			return this.getEStructuralFeature(uriFragmentSegment);
		}
	}

	@Override
	public void eSetProxyURI(URI uri) {
		throw new UnsupportedFeatureException(UnsupportedFeature.PROXY);

	}

	@Override
	public URI eProxyURI() {
		return null;
	}

	@Override
	public EObject eResolveProxy(InternalEObject proxyObject) {
		return EcoreUtil
				.resolve(proxyObject, this.eResource().getResourceSet());
	}

	@Override
	public InternalEObject.EStore eStore() {
		// default behavior; see EClassImpl
		return null;
	}

	@Override
	public void eSetStore(InternalEObject.EStore store) {
		// default behavior; see EClassImpl
		throw new UnsupportedOperationException();

	}

	@Override
	public EStructuralFeature.Setting eSetting(final EStructuralFeature eFeature) {
		if (this.getFeatureID(eFeature) < 0) {
			throw new InvalidFeatureException();
		}
		return new EStructuralFeature.Setting() {

			@Override
			public EObject getEObject() {
				return GrEMFEdgeClassImpl.this;
			}

			@Override
			public Object get(boolean resolve) {
				return GrEMFEdgeClassImpl.this.eGet(eFeature, resolve);
			}

			@Override
			public EStructuralFeature getEStructuralFeature() {
				return eFeature;
			}

			@Override
			public void set(Object newValue) {
				GrEMFEdgeClassImpl.this.eSet(eFeature, newValue);
			}

			@Override
			public boolean isSet() {
				return GrEMFEdgeClassImpl.this.eIsSet(eFeature);
			}

			@Override
			public void unset() {
				GrEMFEdgeClassImpl.this.eUnset(eFeature);
			}
		};
	}

	@Override
	public void eSetClass(EClass eClass) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public NotificationChain eSetResource(Resource.Internal resource,
			NotificationChain msgs) {
		// class is always in package
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd,
			int featureID, Class<?> baseClass, NotificationChain msgs) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd,
			int featureID, Class<?> baseClass, NotificationChain msgs) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public NotificationChain eBasicRemoveFromContainer(NotificationChain msgs) {
		// class is always in package
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public NotificationChain eBasicSetContainer(InternalEObject newContainer,
			int newContainerFeatureId, NotificationChain msgs) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public int eDerivedOperationID(int baseOperationID, Class<?> baseClass) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public Object eInvoke(int operationID, EList<?> arguments)
			throws InvocationTargetException {
		throw new UnsupportedFeatureException(UnsupportedFeature.OPERATION);
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.util.BasicExtendedMetaData.EClassifierExtendedMetaData.Holder
	// --------------------------------------------------------------------------

	private EClassifierExtendedMetaData md;

	@Override
	public EClassifierExtendedMetaData getExtendedMetaData() {
		return this.md;
	}

	@Override
	public void setExtendedMetaData(
			EClassifierExtendedMetaData eClassifierExtendedMetaData) {
		this.md = eClassifierExtendedMetaData;

	}

}

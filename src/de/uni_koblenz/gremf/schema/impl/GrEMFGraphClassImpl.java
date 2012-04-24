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
import org.eclipse.emf.common.notify.impl.NotificationChainImpl;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EReferenceImpl;
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
import de.uni_koblenz.jgralab.schema.AggregationKind;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;
import de.uni_koblenz.jgralab.schema.impl.GraphClassImpl;
import de.uni_koblenz.jgralab.schema.impl.SchemaImpl;

public class GrEMFGraphClassImpl extends GraphClassImpl implements EClass,
		EClassifierExtendedMetaData.Holder, InternalEObject, GrEMFSchemaType {

	private EList<EAnnotation> eAnnotations;
	private EList<EAttribute> eAttributes;
	private EList<EReference> eReferences, eAllContainments;
	private EList<EStructuralFeature> eFeatures;
	private EList<EOperation> eOperations;
	private EList<ETypeParameter> eTypeParameters;
	private EList<EClass> eSuperTypes;

	private URI proxyURI;

	private EReference vertices;
	private EReference edges;

	GrEMFGraphClassImpl(String gcName, SchemaImpl schema) {
		super(gcName, schema);
		this.initVerticesAttribute();
		this.initEdgesAttribute();

		this.eAnnotations = new BasicInternalEList<EAnnotation>(
				EAnnotation.class);
		this.eAllContainments = new EcoreEList.UnmodifiableEList<EReference>(
				this, EcorePackage.Literals.ECLASS__EALL_CONTAINMENTS, 0,
				new BasicEList<EReference>(0).data());
		this.eOperations = new EcoreEList.UnmodifiableEList<EOperation>(this,
				EcorePackage.Literals.ECLASS__EOPERATIONS, 0,
				new BasicEList<EOperation>(0).data());
		this.eTypeParameters = new EcoreEList.UnmodifiableEList<ETypeParameter>(
				this, EcorePackage.Literals.ECLASSIFIER__ETYPE_PARAMETERS, 0,
				new BasicEList<ETypeParameter>(0).data());
		this.eSuperTypes = new EcoreEList.UnmodifiableEList<EClass>(this,
				EcorePackage.Literals.ECLASS__ESUPER_TYPES, 0,
				new BasicEList<EClass>(0).data());
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

	private void initVerticesAttribute() {

		EDataType vDataType = EcoreFactory.eINSTANCE.createEDataType();
		vDataType.setInstanceClass(GrEMFVertexClassImpl.class);

		this.vertices = EcoreFactory.eINSTANCE.createEReference();
		this.vertices.setName("vertices");

		this.vertices.setLowerBound(0);
		this.vertices.setUpperBound(EStructuralFeature.UNBOUNDED_MULTIPLICITY);

		this.vertices.setEType(vDataType);

		this.vertices.setTransient(true);

		((EReferenceImpl) this.vertices).setFeatureID(super
				.getOwnAttributeCount());

		((InternalEObject) this.vertices).eBasicSetContainer(this,
				EcorePackage.ESTRUCTURAL_FEATURE__ECONTAINING_CLASS,
				new NotificationChainImpl());
	}

	private void initEdgesAttribute() {

		EDataType eDataType = EcoreFactory.eINSTANCE.createEDataType();
		eDataType.setInstanceClass(GrEMFEdgeClassImpl.class);

		this.edges = EcoreFactory.eINSTANCE.createEReference();
		this.edges.setName("edges");

		this.edges.setLowerBound(0);
		this.edges.setUpperBound(EStructuralFeature.UNBOUNDED_MULTIPLICITY);

		this.edges.setEType(eDataType);

		this.edges.setTransient(true);

		((EReferenceImpl) this.edges)
				.setFeatureID(super.getOwnAttributeCount() + 1);

		((InternalEObject) this.edges).eBasicSetContainer(this,
				EcorePackage.ESTRUCTURAL_FEATURE__ECONTAINING_CLASS,
				new NotificationChainImpl());
	}

	// --- JGraLab

	@Override
	public GrEMFVertexClassImpl createVertexClass(String qualifiedName) {
		String[] qn = SchemaImpl.splitQualifiedName(qualifiedName);
		Package parent = ((GrEMFSchemaImpl) this.getSchema())
				.createPackageWithParents(qn[0]);
		GrEMFVertexClassImpl vc = new GrEMFVertexClassImpl(qn[1], parent, this);
		if (this.getDefaultVertexClass() != null) {
			vc.addSuperClass(this.getDefaultVertexClass());
		}
		return vc;
	}

	@Override
	public GrEMFEdgeClassImpl createEdgeClass(String qualifiedName,
			VertexClass from, int fromMin, int fromMax, String fromRoleName,
			AggregationKind aggrFrom, VertexClass to, int toMin, int toMax,
			String toRoleName, AggregationKind aggrTo) {
		if (!(aggrFrom == AggregationKind.NONE)
				&& !(aggrTo == AggregationKind.NONE)) {
			throw new SchemaException(
					"At least one end of each class must be of AggregationKind NONE at EdgeClass "
							+ qualifiedName);
		}
		String[] qn = SchemaImpl.splitQualifiedName(qualifiedName);
		Package parent = ((GrEMFSchemaImpl) this.getSchema())
				.createPackageWithParents(qn[0]);
		GrEMFEdgeClassImpl ec = new GrEMFEdgeClassImpl(qn[1], parent, this,
				from, fromMin, fromMax, fromRoleName, aggrFrom, to, toMin,
				toMax, toRoleName, aggrTo);
		if (this.getDefaultEdgeClass() != null) {
			ec.addSuperClass(this.getDefaultEdgeClass());
		}
		return ec;
	}

	public GrEMFRefEdgeClassImpl createRefEdgeClass(String qualifiedName,
			VertexClass from, int fromMin, int fromMax, String fromRoleName,
			AggregationKind aggrFrom, VertexClass to, int toMin, int toMax,
			String toRoleName, AggregationKind aggrTo) {
		if (!(aggrFrom == AggregationKind.NONE)
				&& !(aggrTo == AggregationKind.NONE)) {
			throw new SchemaException(
					"At least one end of each class must be of AggregationKind NONE at EdgeClass "
							+ qualifiedName);
		}
		String[] qn = SchemaImpl.splitQualifiedName(qualifiedName);
		Package parent = ((GrEMFSchemaImpl) this.getSchema())
				.createPackageWithParents(qn[0]);
		GrEMFRefEdgeClassImpl rc = new GrEMFRefEdgeClassImpl(qn[1], parent,
				this, from, fromMin, fromMax, fromRoleName, aggrFrom, to,
				toMin, toMax, toRoleName, aggrTo);
		if (this.getDefaultEdgeClass() != null) {
			rc.addSuperClass(this.getDefaultEdgeClass());
		}
		return rc;
	}

	@Override
	protected void finish() {
		super.finish();
		if (!((GrEMFPackageImpl) this.getPackage()).isFinished()) {
			((GrEMFPackageImpl) this.getPackage()).finish();
		}

		this.eAttributes = this.getEAttributes();
		this.eReferences = this.getEReferences();
		this.eFeatures = this.getEStructuralFeatures();
	}

	@Override
	protected void initializeDefaultVertexClass() {
		super.initializeDefaultVertexClass();
	}

	@Override
	protected void initializeDefaultEdgeClass() {
		super.initializeDefaultEdgeClass();
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
		return object instanceof GrEMFGraphClassImpl;
	}

	@Override
	public int getClassifierID() {
		return this.getEPackage().getEClassifiers().indexOf(this);
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
		// not supported
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
		// not supported
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
		// not supported
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
		return EcorePackage.Literals.ECLASSIFIER__EPACKAGE;
	}

	@Override
	public EReference eContainmentFeature() {
		return EcorePackage.Literals.ECLASSIFIER__EPACKAGE;
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
	public EList<EObject> eCrossReferences() {
		return ECrossReferenceEList.createECrossReferenceEList(this);
	}

	@Override
	public boolean eIsProxy() {
		return this.proxyURI != null;
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
		return this.eGet(feature, true, true);
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
	public boolean isAbstract() {
		return false;
	}

	@Override
	public void setAbstract(boolean value) {
		if (!super.getSchema().isFinished()) {
			// nothing to do
		} else {
			throw new UnsupportedSchemaChangeException();
		}
	}

	@Override
	public boolean isInterface() {
		return false;
	}

	@Override
	public void setInterface(boolean value) {
		if (!super.getSchema().isFinished()) {
			// nothing to do
		} else {
			throw new UnsupportedSchemaChangeException();
		}
	}

	@Override
	public EList<EClass> getESuperTypes() {
		return this.eSuperTypes;
	}

	@Override
	public EList<EClass> getEAllSuperTypes() {
		return this.getESuperTypes();
	}

	@Override
	public boolean isSuperTypeOf(EClass someClass) {
		// GraphClass is no super type
		return false;
	}

	@Override
	public EList<EAttribute> getEAttributes() {
		if ((this.eAttributes == null) && !super.schema.isFinished()) {
			BasicEList<EAttribute> eAttrs = new BasicEList<EAttribute>(
					super.getOwnAttributeCount());
			for (Attribute attr : super.getOwnAttributeList()) {
				eAttrs.add((GrEMFAttributeImpl) attr);
			}
			return new EcoreEList.UnmodifiableEList<EAttribute>(this,
					EcorePackage.Literals.ECLASS__EATTRIBUTES, eAttrs.size(),
					eAttrs.data());
		} else {
			return this.eAttributes;
		}

	}

	@Override
	public EList<EAttribute> getEAllAttributes() {
		return this.getEAttributes();
	}

	@Override
	public EAttribute getEIDAttribute() {
		return null;
	}

	@Override
	public EList<EReference> getEReferences() {
		if ((this.eReferences == null) && !super.schema.isFinished()) {
			BasicEList<EReference> eRefs = new BasicEList<EReference>(2);
			eRefs.add(this.vertices);
			eRefs.add(this.edges);
			return new EcoreEList.UnmodifiableEList<EReference>(this,
					EcorePackage.Literals.ECLASS__EREFERENCES, eRefs.size(),
					eRefs.data());
		}
		return this.eReferences;
	}

	@Override
	public EList<EReference> getEAllReferences() {
		return this.getEReferences();
	}

	@Override
	public EList<EReference> getEAllContainments() {
		return this.eAllContainments;
	}

	@Override
	public EList<EStructuralFeature> getEStructuralFeatures() {
		if ((this.eFeatures == null) && !super.schema.isFinished()) {
			BasicEList<EStructuralFeature> eFeatures = new BasicEList<EStructuralFeature>(
					super.getOwnAttributeCount() + 2);
			eFeatures.addAll(this.getEAttributes());
			eFeatures.addAll(this.getEReferences());
			return new GrEMFStructuralFeaturesList(this, eFeatures);
		} else {
			return this.eFeatures;
		}
	}

	@Override
	public EList<EStructuralFeature> getEAllStructuralFeatures() {
		return this.getEStructuralFeatures();
	}

	@Override
	public int getFeatureCount() {
		return this.getEStructuralFeatures().size();
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
		return this.eContainingFeature().getFeatureID();
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
		return null;
	}

	@Override
	public EObject eObjectForURIFragmentSegment(String uriFragmentSegment) {
		return this.getEStructuralFeature(uriFragmentSegment);
	}

	@Override
	public void eSetProxyURI(URI uri) {
		this.proxyURI = uri;

	}

	@Override
	public URI eProxyURI() {
		return this.proxyURI;
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
				return GrEMFGraphClassImpl.this;
			}

			@Override
			public Object get(boolean resolve) {
				return GrEMFGraphClassImpl.this.eGet(eFeature, resolve);
			}

			@Override
			public EStructuralFeature getEStructuralFeature() {
				return eFeature;
			}

			@Override
			public void set(Object newValue) {
				GrEMFGraphClassImpl.this.eSet(eFeature, newValue);
			}

			@Override
			public boolean isSet() {
				return GrEMFGraphClassImpl.this.eIsSet(eFeature);
			}

			@Override
			public void unset() {
				GrEMFGraphClassImpl.this.eUnset(eFeature);
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

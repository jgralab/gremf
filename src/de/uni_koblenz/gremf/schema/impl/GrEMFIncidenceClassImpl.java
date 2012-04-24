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
import java.util.Set;

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
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData.EStructuralFeatureExtendedMetaData;
import org.eclipse.emf.ecore.util.BasicInternalEList;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.ECrossReferenceEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap;

import de.uni_koblenz.gremf.GrEMFSchemaType;
import de.uni_koblenz.gremf.exception.InvalidFeatureException;
import de.uni_koblenz.gremf.exception.UnsupportedFeature;
import de.uni_koblenz.gremf.exception.UnsupportedFeatureException;
import de.uni_koblenz.gremf.exception.UnsupportedSchemaChangeException;
import de.uni_koblenz.gremf.util.EModelElementUtil;
import de.uni_koblenz.gremf.util.EObjectUtil;
import de.uni_koblenz.jgralab.schema.AggregationKind;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.IncidenceDirection;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.schema.impl.IncidenceClassImpl;

public class GrEMFIncidenceClassImpl extends IncidenceClassImpl implements
		EReference, EStructuralFeatureExtendedMetaData.Holder,
		EStructuralFeature.Internal, InternalEObject, GrEMFSchemaType {

	private EList<EAnnotation> eAnnotations;

	private boolean isChangeable = true;
	private boolean isOrdered = true;

	private int featureID;

	/**
	 * determines whether this <code>IncidenceClass</code> is invisible as
	 * <code>EReference</code> in EMF
	 */
	private boolean invisible;

	public GrEMFIncidenceClassImpl(EdgeClass edgeCls, VertexClass vrtxCls,
			String rolename, int min, int max, IncidenceDirection dir,
			AggregationKind aggr) {
		super(edgeCls, vrtxCls, rolename, min, max, dir, aggr);

		this.eAnnotations = new BasicInternalEList<EAnnotation>(
				EAnnotation.class);

	}

	@Override
	public String toString() {
		return "incidence class " + super.getRolename() + " at "
				+ super.getVertexClass();
	}

	public boolean isInvisible() {
		return this.invisible;
	}

	/**
	 * Can be used as long as the <code>schema</code> is unfinished to set
	 * <code>isInvisible</code>. This value determines whether this
	 * <code>IncidenceClass</code> is invisible as <code>EReference</code> in
	 * EMF
	 */
	public void setInvisibile(boolean value) {
		if (!super.getVertexClass().getSchema().isFinished()) {
			this.invisible = value;
		}
	}

	public void setFeatureID(int value) {
		if (!super.getVertexClass().getSchema().isFinished()) {
			this.featureID = value;
		} else {
			throw new UnsupportedSchemaChangeException();
		}
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EStructuralFeature
	// --------------------------------------------------------------------------

	@Override
	public boolean isTransient() {
		return false;
	}

	@Override
	public void setTransient(boolean value) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public boolean isVolatile() {
		return false;
	}

	@Override
	public void setVolatile(boolean value) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public boolean isChangeable() {
		return this.isChangeable;
	}

	@Override
	public void setChangeable(boolean value) {
		if (!this.getEdgeClass().getSchema().isFinished()) {
			this.isChangeable = value;
		} else {
			throw new UnsupportedSchemaChangeException();
		}

	}

	@Override
	public String getDefaultValueLiteral() {
		return null;
	}

	@Override
	public void setDefaultValueLiteral(String value) {
		throw new UnsupportedOperationException(
				"No default values of GrEMFReferenceClasses supported by grEMF.");
	}

	@Override
	public Object getDefaultValue() {
		return null;
	}

	@Override
	public void setDefaultValue(Object value) {
		throw new UnsupportedOperationException(
				"No default values of GrEMFReferenceClasses supported by grEMF.");
	}

	@Override
	public boolean isUnsettable() {
		return true; // Unset an EReference means deleting all Edges, is ok for
						// normal Edges
	}

	@Override
	public void setUnsettable(boolean value) {
		throw new UnsupportedSchemaChangeException();

	}

	@Override
	public boolean isDerived() {
		return false; // default, no derived values
	}

	@Override
	public void setDerived(boolean value) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public EClass getEContainingClass() {
		return (EClass) this.getOpposite().getVertexClass();
	}

	@Override
	public int getFeatureID() {
		return this.featureID;
	}

	@Override
	public Class<?> getContainerClass() {
		return null;
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.ETypedElement
	// --------------------------------------------------------------------------

	@Override
	public boolean isOrdered() {
		return this.isOrdered;
	}

	@Override
	public void setOrdered(boolean value) {
		if (this.getEdgeClass().getSchema().isFinished()) {
			throw new UnsupportedSchemaChangeException();
		}
		this.isOrdered = value;

	}

	@Override
	public boolean isUnique() {
		return false;
	}

	@Override
	public void setUnique(boolean value) {
		if (this.getEdgeClass().getSchema().isFinished()) {
			throw new UnsupportedSchemaChangeException();
		}
	}

	@Override
	public int getLowerBound() {
		return this.getMin();
	}

	@Override
	public void setLowerBound(int value) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public int getUpperBound() {
		if (this.getMax() == Integer.MAX_VALUE) {
			return -1;
		}
		return this.getMax();
	}

	@Override
	public void setUpperBound(int value) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public boolean isMany() {
		return this.getMax() > 1;
	}

	@Override
	public boolean isRequired() {
		return this.getMin() > 0;
	}

	@Override
	public EClassifier getEType() {
		return (GrEMFVertexClassImpl) this.getVertexClass();
	}

	@Override
	public void setEType(EClassifier value) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public EGenericType getEGenericType() {
		return null;
	}

	@Override
	public void setEGenericType(EGenericType value) {
		throw new UnsupportedFeatureException(UnsupportedFeature.GENERICS);
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.ENamedElement
	// --------------------------------------------------------------------------

	@Override
	public String getName() {
		if ((this.getRolename() == null) || this.getRolename().equals("")) {
			Set<IncidenceClass> set = this.getSubsettedIncidenceClasses();

			String rolename = this.getEdgeClass().getSimpleName().toLowerCase();

			for (IncidenceClass inc : set) {

				if (!this.getRedefinedIncidenceClasses().contains(inc)
						&& !inc.getEdgeClass().isDefaultGraphElementClass()) {

					rolename += "_"
							+ ((GrEMFIncidenceClassWithRefsImpl) inc).getName();
					return rolename; // return first rolename of superclass
				}
			}
			// return this.getRolename();
			return rolename;
		}

		return this.getRolename();
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
		return EcorePackage.Literals.EREFERENCE;
	}

	@Override
	public Resource eResource() {
		return this.getEContainingClass().eResource();
	}

	@Override
	public EObject eContainer() {
		return (EObject) this.getOpposite().getVertexClass();
	}

	@Override
	public EStructuralFeature eContainingFeature() {
		return EcorePackage.Literals.ECLASS__ESTRUCTURAL_FEATURES;
	}

	@Override
	public EReference eContainmentFeature() {
		return EcorePackage.Literals.ECLASS__ESTRUCTURAL_FEATURES;
	}

	@Override
	public EList<EObject> eContents() {
		// That's not true, EGenericTypes are here, but we don't support that
		return EContentsEList.emptyContentsEList();
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
		return ECrossReferenceEList.createECrossReferenceEList(this);
	}

	@Override
	public Object eGet(EStructuralFeature feature) {
		return this.eGet(feature, true, true);
	}

	@Override
	public Object eGet(EStructuralFeature feature, boolean resolve) {
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
	// org.eclipse.emf.ecore.ENotifier
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
	// org.eclipse.emf.ecore.EReference
	// --------------------------------------------------------------------------

	@Override
	public boolean isContainment() {
		return this.getAggregationKind().equals(AggregationKind.COMPOSITE);
	}

	@Override
	public void setContainment(boolean value) {
		throw new UnsupportedOperationException(
				"Changes of the schema are not allowed by grEMF.");

	}

	@Override
	public boolean isContainer() {
		return this.getOpposite().getAggregationKind()
				.equals(AggregationKind.COMPOSITE);
	}

	@Override
	public boolean isResolveProxies() {
		return true; // no proxies, so always resolved
	}

	@Override
	public void setResolveProxies(boolean value) {
		throw new UnsupportedOperationException(
				"Changes of the schema are not allowed by grEMF.");
	}

	@Override
	public EReference getEOpposite() {
		if (((GrEMFIncidenceClassImpl) this.getOpposite()).invisible) {
			return null;
		}
		return (EReference) this.getOpposite();
	}

	@Override
	public void setEOpposite(EReference value) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public EClass getEReferenceType() {
		return (GrEMFVertexClassImpl) this.getVertexClass();
	}

	@Override
	public EList<EAttribute> getEKeys() {
		return new BasicEList<EAttribute>();
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EInternalObject
	// --------------------------------------------------------------------------

	@Override
	public boolean eNotificationRequired() {
		return false;
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
			return null;
		}
	}

	@Override
	public void eSetClass(EClass eClass) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public Setting eSetting(final EStructuralFeature eFeature) {
		if (EObjectUtil.getFeatureID(this, eFeature) < 0) {
			throw new InvalidFeatureException();
		}
		return new EStructuralFeature.Setting() {

			@Override
			public EObject getEObject() {
				return GrEMFIncidenceClassImpl.this;
			}

			@Override
			public Object get(boolean resolve) {
				return GrEMFIncidenceClassImpl.this.eGet(eFeature, resolve);
			}

			@Override
			public EStructuralFeature getEStructuralFeature() {
				return eFeature;
			}

			@Override
			public void set(Object newValue) {
				GrEMFIncidenceClassImpl.this.eSet(eFeature, newValue);
			}

			@Override
			public boolean isSet() {
				return GrEMFIncidenceClassImpl.this.eIsSet(eFeature);
			}

			@Override
			public void unset() {
				GrEMFIncidenceClassImpl.this.eUnset(eFeature);
			}
		};
	}

	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
		return derivedFeatureID;
	}

	@Override
	public int eContainerFeatureID() {
		return EcorePackage.ESTRUCTURAL_FEATURE__ECONTAINING_CLASS;
	}

	@Override
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
		return baseFeatureID;
	}

	@Override
	public int eDerivedOperationID(int baseOperationID, Class<?> baseClass) {
		throw new UnsupportedFeatureException(UnsupportedFeature.OPERATION);
	}

	@Override
	public NotificationChain eSetResource(
			org.eclipse.emf.ecore.resource.Resource.Internal resource,
			NotificationChain notifications) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd,
			int featureID, Class<?> baseClass, NotificationChain notifications) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd,
			int featureID, Class<?> baseClass, NotificationChain notifications) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public NotificationChain eBasicSetContainer(InternalEObject newContainer,
			int newContainerFeatureID, NotificationChain notifications) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public NotificationChain eBasicRemoveFromContainer(
			NotificationChain notifications) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public URI eProxyURI() {
		return null;
	}

	@Override
	public void eSetProxyURI(URI uri) {
		throw new UnsupportedFeatureException(UnsupportedFeature.PROXY);
	}

	@Override
	public EObject eResolveProxy(InternalEObject proxy) {
		return EcoreUtil.resolve(proxy, this);
	}

	@Override
	public InternalEObject eInternalContainer() {
		return (InternalEObject) this.eContainer();
	}

	@Override
	public org.eclipse.emf.ecore.resource.Resource.Internal eInternalResource() {
		return (Resource.Internal) this.eResource();
	}

	@Override
	public org.eclipse.emf.ecore.resource.Resource.Internal eDirectResource() {
		return null; // EReferences are always in EClasses
	}

	@Override
	public EStore eStore() {
		return null;
	}

	@Override
	public void eSetStore(EStore store) {
		throw new UnsupportedOperationException(); // -> BasicEObjectImpl
	}

	@Override
	public Object eGet(EStructuralFeature eFeature, boolean resolve,
			boolean coreType) {
		if (EObjectUtil.getFeatureID(this, eFeature) < 0) {
			throw new InvalidFeatureException(eFeature);
		}
		return this.eGet(eFeature.getFeatureID(), true, true);
	}

	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case EcorePackage.EREFERENCE__EANNOTATIONS:
			return this.getEAnnotations();
		case EcorePackage.EREFERENCE__NAME:
			return this.getName();
		case EcorePackage.EREFERENCE__ORDERED:
			return this.isOrdered();
		case EcorePackage.EREFERENCE__UNIQUE:
			return this.isUnique();
		case EcorePackage.EREFERENCE__LOWER_BOUND:
			return this.getLowerBound();
		case EcorePackage.EREFERENCE__UPPER_BOUND:
			return this.getUpperBound();
		case EcorePackage.EREFERENCE__MANY:
			return this.isMany();
		case EcorePackage.EREFERENCE__REQUIRED:
			return this.isRequired();
		case EcorePackage.EREFERENCE__ETYPE:
			return this.getEType();
		case EcorePackage.EREFERENCE__EGENERIC_TYPE:
			return this.getEGenericType();
		case EcorePackage.EREFERENCE__CHANGEABLE:
			return this.isChangeable();
		case EcorePackage.EREFERENCE__VOLATILE:
			return this.isVolatile();
		case EcorePackage.EREFERENCE__TRANSIENT:
			return this.isTransient();
		case EcorePackage.EREFERENCE__DEFAULT_VALUE_LITERAL:
			return this.getDefaultValueLiteral();
		case EcorePackage.EREFERENCE__DEFAULT_VALUE:
			return this.getDefaultValue();
		case EcorePackage.EREFERENCE__UNSETTABLE:
			return this.isUnsettable();
		case EcorePackage.EREFERENCE__DERIVED:
			return this.isDerived();
		case EcorePackage.EREFERENCE__ECONTAINING_CLASS:
			return this.getEContainingClass();
		case EcorePackage.EREFERENCE__CONTAINMENT:
			return this.isContainment();
		case EcorePackage.EREFERENCE__CONTAINER:
			return this.isContainer();
		case EcorePackage.EREFERENCE__RESOLVE_PROXIES:
			return this.isResolveProxies();
		case EcorePackage.EREFERENCE__EOPPOSITE:
			return this.getEOpposite();
		case EcorePackage.EREFERENCE__EREFERENCE_TYPE:
			return this.getEReferenceType();
		case EcorePackage.EREFERENCE__EKEYS:
			return this.getEKeys();
		}
		throw new InvalidFeatureException();
	}

	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case EcorePackage.EREFERENCE__EANNOTATIONS:
		case EcorePackage.EREFERENCE__NAME:
		case EcorePackage.EREFERENCE__ORDERED:
		case EcorePackage.EREFERENCE__UNIQUE:
		case EcorePackage.EREFERENCE__LOWER_BOUND:
		case EcorePackage.EREFERENCE__UPPER_BOUND:
		case EcorePackage.EREFERENCE__ETYPE:
		case EcorePackage.EREFERENCE__EGENERIC_TYPE:
		case EcorePackage.EREFERENCE__CHANGEABLE:
		case EcorePackage.EREFERENCE__VOLATILE:
		case EcorePackage.EREFERENCE__TRANSIENT:
		case EcorePackage.EREFERENCE__DEFAULT_VALUE_LITERAL:
		case EcorePackage.EREFERENCE__UNSETTABLE:
		case EcorePackage.EREFERENCE__DERIVED:
		case EcorePackage.EREFERENCE__CONTAINMENT:
		case EcorePackage.EREFERENCE__RESOLVE_PROXIES:
		case EcorePackage.EREFERENCE__EOPPOSITE:
		case EcorePackage.EREFERENCE__EKEYS:
			throw new UnsupportedSchemaChangeException();
		}
		throw new InvalidFeatureException();
	}

	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case EcorePackage.EREFERENCE__EANNOTATIONS:
		case EcorePackage.EREFERENCE__NAME:
		case EcorePackage.EREFERENCE__ORDERED:
		case EcorePackage.EREFERENCE__UNIQUE:
		case EcorePackage.EREFERENCE__LOWER_BOUND:
		case EcorePackage.EREFERENCE__UPPER_BOUND:
		case EcorePackage.EREFERENCE__ETYPE:
		case EcorePackage.EREFERENCE__EGENERIC_TYPE:
		case EcorePackage.EREFERENCE__CHANGEABLE:
		case EcorePackage.EREFERENCE__VOLATILE:
		case EcorePackage.EREFERENCE__TRANSIENT:
		case EcorePackage.EREFERENCE__DEFAULT_VALUE_LITERAL:
		case EcorePackage.EREFERENCE__UNSETTABLE:
		case EcorePackage.EREFERENCE__DERIVED:
		case EcorePackage.EREFERENCE__CONTAINMENT:
		case EcorePackage.EREFERENCE__RESOLVE_PROXIES:
		case EcorePackage.EREFERENCE__EOPPOSITE:
		case EcorePackage.EREFERENCE__EKEYS:
			throw new UnsupportedSchemaChangeException();
		}
		throw new InvalidFeatureException();

	}

	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case EcorePackage.EREFERENCE__EANNOTATIONS:
			return (this.eAnnotations != null) && !this.eAnnotations.isEmpty();
		case EcorePackage.EREFERENCE__NAME:
			return this.getName() != null;
		case EcorePackage.EREFERENCE__ORDERED:
			return !this.isOrdered();
		case EcorePackage.EREFERENCE__UNIQUE:
			return !this.isUnique();
		case EcorePackage.EREFERENCE__LOWER_BOUND:
			return this.getLowerBound() != 0;
		case EcorePackage.EREFERENCE__UPPER_BOUND:
			return this.getUpperBound() != 1;
		case EcorePackage.EREFERENCE__MANY:
			return this.isMany();
		case EcorePackage.EREFERENCE__REQUIRED:
			return this.getLowerBound() > 0;
		case EcorePackage.EREFERENCE__ETYPE:
			return true; // Omega is the eType
		case EcorePackage.EREFERENCE__EGENERIC_TYPE:
			return false; // no generics in jgralab
		case EcorePackage.EREFERENCE__CHANGEABLE:
			return !this.isChangeable;
		case EcorePackage.EREFERENCE__VOLATILE:
			return this.isVolatile();
		case EcorePackage.EREFERENCE__TRANSIENT:
			return this.isTransient(); // but only for EMF View, really support?
		case EcorePackage.EREFERENCE__DEFAULT_VALUE_LITERAL:
			return false; // no default value for EReferences possible
		case EcorePackage.EREFERENCE__DEFAULT_VALUE:
			return false;// no default value for EReferences possible
		case EcorePackage.EREFERENCE__UNSETTABLE:
			return false; // can't forbid it
		case EcorePackage.EREFERENCE__DERIVED:
			return false; // can't derive
		case EcorePackage.EREFERENCE__ECONTAINING_CLASS:
			return true; // Alpha is the eContainingClass
		case EcorePackage.EREFERENCE__CONTAINMENT:
			return this.isContainment();
		case EcorePackage.EREFERENCE__CONTAINER:
			return this.isContainer();
		case EcorePackage.EREFERENCE__RESOLVE_PROXIES:
			return true; // no proxies in jgralab
		case EcorePackage.EREFERENCE__EOPPOSITE:
			return this.getEOpposite() != null;
		case EcorePackage.EREFERENCE__EREFERENCE_TYPE:
			return true; // Omega is the eReferenceType
		case EcorePackage.EREFERENCE__EKEYS:
			return (this.getEKeys() != null) && !this.getEKeys().isEmpty();
		}
		throw new InvalidFeatureException();
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

	private EStructuralFeatureExtendedMetaData md;

	@Override
	public EStructuralFeatureExtendedMetaData getExtendedMetaData() {
		return this.md;
	}

	@Override
	public void setExtendedMetaData(
			EStructuralFeatureExtendedMetaData eStructuralFeatureExtendedMetaData) {
		this.md = eStructuralFeatureExtendedMetaData;
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EStructuralFeature.Internal
	// --------------------------------------------------------------------------

	@Override
	public EStructuralFeature.Internal.SettingDelegate getSettingDelegate() {
		// not supported
		return null;
	}

	@Override
	public void setSettingDelegate(
			EStructuralFeature.Internal.SettingDelegate settingDelegate) {
		throw new UnsupportedFeatureException(UnsupportedFeature.DELEGATION);

	}

	@Override
	public boolean isFeatureMap() {
		return false;
	}

	@Override
	public FeatureMap.Entry.Internal getFeatureMapEntryPrototype() {
		// not supported
		return null;
	}

	@Override
	public void setFeatureMapEntryPrototype(FeatureMap.Entry.Internal prototype) {
		throw new UnsupportedFeatureException(UnsupportedFeature.FEATURE_MAP);

	}

	@Override
	public boolean isID() {
		return false;
	}

}

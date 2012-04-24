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
import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.BasicNotifierImpl.EAdapterList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
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
import de.uni_koblenz.gremf.schema.GrEMFDomain;
import de.uni_koblenz.gremf.util.EModelElementUtil;
import de.uni_koblenz.gremf.util.EObjectUtil;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.CollectionDomain;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.ListDomain;
import de.uni_koblenz.jgralab.schema.SetDomain;
import de.uni_koblenz.jgralab.schema.impl.AttributeImpl;

public class GrEMFAttributeImpl extends AttributeImpl implements EAttribute,
		EStructuralFeatureExtendedMetaData.Holder, EStructuralFeature.Internal,
		InternalEObject, GrEMFSchemaType {

	private EList<EAnnotation> eAnnotations;

	private int featureID;

	private boolean id;

	public GrEMFAttributeImpl(String name, Domain domain,
			AttributedElementClass<?, ?> aec, String defaultValue) {
		super(name, domain, aec, defaultValue);

		this.eAnnotations = new BasicInternalEList<EAnnotation>(
				EAnnotation.class);
	}

	public void setFeatureID(int value) {
		if (!super.getDomain().getSchema().isFinished()) {
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
	public void setTransient(boolean newTransient) {
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
		return true;
	}

	@Override
	public void setChangeable(boolean value) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public String getDefaultValueLiteral() {
		return super.getDefaultValueAsString();
	}

	@Override
	public void setDefaultValueLiteral(String value) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public Object getDefaultValue() {
		try {
			if (this.getDefaultValueAsString() == null) {
				return ((GrEMFDomain) this.getDomain()).getDefaultValue();
			}
			Object o = this.getDomain().parseGenericAttribute(
					GraphIO.createStringReader(this.getDefaultValueLiteral(),
							this.getAttributedElementClass().getSchema()));
			return ((GrEMFDomain) this.getDomain()).getEMFValue(o);
		} catch (GraphIOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void setDefaultValue(Object value) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public boolean isUnsettable() {
		return false;
	}

	@Override
	public void setUnsettable(boolean value) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public boolean isDerived() {
		return false;
	}

	@Override
	public void setDerived(boolean value) {
		throw new UnsupportedSchemaChangeException();

	}

	@Override
	public EClass getEContainingClass() {
		return (EClass) this.getAttributedElementClass();
	}

	@Override
	public int getFeatureID() {
		return this.featureID;
	}

	@Override
	public Class<?> getContainerClass() {
		return null; // no instance classes
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.ETypedElement
	// --------------------------------------------------------------------------

	@Override
	public boolean isOrdered() {
		if (this.getDomain() instanceof ListDomain) {
			return true;
		} else if (this.getDomain().isComposite()) {
			return false;
		} else if (this.getDomain().isPrimitive()) {
			// EMF default is true
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void setOrdered(boolean value) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public boolean isUnique() {
		if (this.getDomain() instanceof SetDomain) {
			return true;
		} else if (this.getDomain().isComposite()) {
			return false;
		} else if (this.getDomain().isPrimitive()) {
			// EMF default is true
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void setUnique(boolean value) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public int getLowerBound() {
		return 0;
	}

	@Override
	public void setLowerBound(int value) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public int getUpperBound() {
		if (this.getDomain() instanceof CollectionDomain) {
			return EStructuralFeature.UNBOUNDED_MULTIPLICITY;
		} else {
			return 1;
		}
	}

	@Override
	public void setUpperBound(int value) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public boolean isMany() {
		return this.getDomain() instanceof CollectionDomain;
	}

	@Override
	public boolean isRequired() {
		return false;
	}

	@Override
	public EClassifier getEType() {
		if (this.getDomain() instanceof GrEMFDomain) {
			return ((GrEMFDomain) this.getDomain()).getEMFDataType();
		}
		throw new RuntimeException(this.getDomain() + " : "
				+ this.getDomain().getClass() + " is not a GrEMFDomain");
	}

	@Override
	public void setEType(EClassifier value) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public EGenericType getEGenericType() {
		return null;// no Generics in JGraLab
	}

	@Override
	public void setEGenericType(EGenericType value) {
		throw new UnsupportedSchemaChangeException();
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.ENamedElement
	// --------------------------------------------------------------------------

	@Override
	public String getName() {
		return super.getName();
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
		return EcorePackage.Literals.EATTRIBUTE;
	}

	@Override
	public Resource eResource() {
		return this.eContainer().eResource();
	}

	@Override
	public EObject eContainer() {
		return (EObject) this.getAttributedElementClass();
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
		return EContentsEList.createEContentsEList(this);
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
	// org.eclipse.emf.ecore.EAttribute
	// --------------------------------------------------------------------------

	@Override
	public boolean isID() {
		return this.id;
	}

	@Override
	public void setID(boolean newID) {
		if (!super.getDomain().getSchema().isFinished()) {
			this.id = newID;
		} else {
			throw new UnsupportedSchemaChangeException();
		}
	}

	@Override
	public EDataType getEAttributeType() {
		return ((GrEMFDomain) this.getDomain()).getEMFDataType();
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
	// org.eclipse.emf.ecore.InternalEObject
	// --------------------------------------------------------------------------

	@Override
	public boolean eNotificationRequired() {
		return (this.eAdapters().size() > 0) && this.eDeliver();
	}

	@Override
	public String eURIFragmentSegment(EStructuralFeature eFeature,
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
				return GrEMFAttributeImpl.this;
			}

			@Override
			public Object get(boolean resolve) {
				return GrEMFAttributeImpl.this.eGet(eFeature, resolve);
			}

			@Override
			public EStructuralFeature getEStructuralFeature() {
				return eFeature;
			}

			@Override
			public void set(Object newValue) {
				GrEMFAttributeImpl.this.eSet(eFeature, newValue);
			}

			@Override
			public boolean isSet() {
				return GrEMFAttributeImpl.this.eIsSet(eFeature);
			}

			@Override
			public void unset() {
				GrEMFAttributeImpl.this.eUnset(eFeature);
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
		return EcoreUtil.resolve(proxy, this.eResource().getResourceSet());
	}

	@Override
	public InternalEObject eInternalContainer() {
		return (InternalEObject) this.eContainer();
	}

	@Override
	public Resource.Internal eInternalResource() {
		return (Resource.Internal) this.eResource();
	}

	@Override
	public Resource.Internal eDirectResource() {
		return null; // Attribute always in an EClass
	}

	@Override
	public EStore eStore() {
		return null; // -> BasicEObjectImpl
	}

	@Override
	public void eSetStore(EStore store) {
		throw new UnsupportedOperationException(); // -> BasicEObjectImpl

	}

	@Override
	public Object eGet(EStructuralFeature feature, boolean resolve,
			boolean coreType) {
		return this.eGet(feature.getFeatureID(), resolve, coreType);
	}

	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {

		switch (featureID) {
		case EcorePackage.EATTRIBUTE__NAME:
			return this.getName();
		case EcorePackage.EATTRIBUTE__CHANGEABLE:
			return this.isChangeable();
		case EcorePackage.EATTRIBUTE__DEFAULT_VALUE:
			return this.getDefaultValue();
		case EcorePackage.EATTRIBUTE__DEFAULT_VALUE_LITERAL:
			return this.getDefaultValueLiteral();
		case EcorePackage.EATTRIBUTE__DERIVED:
			return this.isDerived();
		case EcorePackage.EATTRIBUTE__EANNOTATIONS:
			return this.getEAnnotations();
		case EcorePackage.EATTRIBUTE__EATTRIBUTE_TYPE:
			return this.getEAttributeType();
		case EcorePackage.EATTRIBUTE__ECONTAINING_CLASS:
			return this.getContainerClass();
		case EcorePackage.EATTRIBUTE__EGENERIC_TYPE:
			return this.getEGenericType();
		case EcorePackage.EATTRIBUTE__ETYPE:
			return this.getEType();
		case EcorePackage.EATTRIBUTE__ID:
			return this.isID();
		case EcorePackage.EATTRIBUTE__LOWER_BOUND:
			return this.getLowerBound();
		case EcorePackage.EATTRIBUTE__MANY:
			return this.isMany();
		case EcorePackage.EATTRIBUTE__ORDERED:
			return this.isOrdered();
		case EcorePackage.EATTRIBUTE__REQUIRED:
			return this.isRequired();
		case EcorePackage.EATTRIBUTE__TRANSIENT:
			return this.isTransient();
		case EcorePackage.EATTRIBUTE__UNIQUE:
			return this.isUnique();
		case EcorePackage.EATTRIBUTE__UNSETTABLE:
			return this.isUnsettable();
		case EcorePackage.EATTRIBUTE__UPPER_BOUND:
			return this.getUpperBound();
		case EcorePackage.EATTRIBUTE__VOLATILE:
			return this.isVolatile();
		}
		throw new InvalidFeatureException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case EcorePackage.EATTRIBUTE__EANNOTATIONS:
			this.getEAnnotations().clear();
			this.getEAnnotations().addAll(
					(Collection<? extends EAnnotation>) newValue);
		case EcorePackage.EATTRIBUTE__ID:
			this.setID((Boolean) newValue);
			return;
		case EcorePackage.EATTRIBUTE__ECONTAINING_CLASS:
		case EcorePackage.EATTRIBUTE__MANY:
		case EcorePackage.EATTRIBUTE__REQUIRED:
			// in EMF not settable; EMF behavior
			throw new NullPointerException();
		default:
			throw new UnsupportedSchemaChangeException();
		}
	}

	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case EcorePackage.EATTRIBUTE__EANNOTATIONS:
			this.getEAnnotations().clear();
			return;
		case EcorePackage.EATTRIBUTE__ID:
			this.setID((Boolean) EcorePackage.Literals.EATTRIBUTE__ID
					.getDefaultValue());
			return;
		case EcorePackage.EATTRIBUTE__ECONTAINING_CLASS:
		case EcorePackage.EATTRIBUTE__MANY:
		case EcorePackage.EATTRIBUTE__REQUIRED:
			// in EMF not settable; EMF behavior
			throw new NullPointerException();
		default:
			throw new UnsupportedSchemaChangeException();
		}
	}

	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case EcorePackage.EATTRIBUTE__EANNOTATIONS:
			return (this.eAnnotations != null) && !this.eAnnotations.isEmpty();
		case EcorePackage.EATTRIBUTE__NAME:
			if (EcorePackage.Literals.ENAMED_ELEMENT__NAME.getDefaultValue() == null) {
				return this.getName() != null;
			} else {
				return !EcorePackage.Literals.ENAMED_ELEMENT__NAME
						.getDefaultValue().equals(this.getName());
			}
		case EcorePackage.EATTRIBUTE__ORDERED:
			return this.isOrdered() != (Boolean) EcorePackage.Literals.ETYPED_ELEMENT__ORDERED
					.getDefaultValue();
		case EcorePackage.EATTRIBUTE__UNIQUE:
			return this.isUnique() != (Boolean) EcorePackage.Literals.ETYPED_ELEMENT__UNIQUE
					.getDefaultValue();
		case EcorePackage.EATTRIBUTE__LOWER_BOUND:
			return this.getLowerBound() != (Integer) EcorePackage.Literals.ETYPED_ELEMENT__LOWER_BOUND
					.getDefaultValue();
		case EcorePackage.EATTRIBUTE__UPPER_BOUND:
			return this.getUpperBound() != (Integer) EcorePackage.Literals.ETYPED_ELEMENT__UPPER_BOUND
					.getDefaultValue();
		case EcorePackage.EATTRIBUTE__MANY:
			return this.isMany() != (Boolean) EcorePackage.Literals.ETYPED_ELEMENT__MANY
					.getDefaultValue();
		case EcorePackage.EATTRIBUTE__REQUIRED:
			return this.isRequired() != (Boolean) EcorePackage.Literals.ETYPED_ELEMENT__REQUIRED
					.getDefaultValue();
		case EcorePackage.EATTRIBUTE__ETYPE:
			return this.getEType() != (EClassifier) EcorePackage.Literals.ETYPED_ELEMENT__ETYPE
					.getDefaultValue();
		case EcorePackage.EATTRIBUTE__EGENERIC_TYPE:
			return this.getEGenericType() != (EGenericType) EcorePackage.Literals.ETYPED_ELEMENT__EGENERIC_TYPE
					.getDefaultValue();
		case EcorePackage.EATTRIBUTE__CHANGEABLE:
			return this.isChangeable() != (Boolean) EcorePackage.Literals.ESTRUCTURAL_FEATURE__CHANGEABLE
					.getDefaultValue();
		case EcorePackage.EATTRIBUTE__VOLATILE:
			return this.isVolatile() != (Boolean) EcorePackage.Literals.ESTRUCTURAL_FEATURE__VOLATILE
					.getDefaultValue();
		case EcorePackage.EATTRIBUTE__TRANSIENT:
			return this.isTransient() != (Boolean) EcorePackage.Literals.ESTRUCTURAL_FEATURE__TRANSIENT
					.getDefaultValue();
		case EcorePackage.EATTRIBUTE__DEFAULT_VALUE_LITERAL:
			if (EcorePackage.Literals.ESTRUCTURAL_FEATURE__DEFAULT_VALUE_LITERAL
					.getDefaultValue() == null) {
				return this.getDefaultValueLiteral() != null;
			} else {
				return !EcorePackage.Literals.ESTRUCTURAL_FEATURE__DEFAULT_VALUE_LITERAL
						.getDefaultValue()
						.equals(this.getDefaultValueLiteral());
			}
		case EcorePackage.EATTRIBUTE__DEFAULT_VALUE:
			if (EcorePackage.Literals.ESTRUCTURAL_FEATURE__DEFAULT_VALUE
					.getDefaultValue() == null) {
				return this.getDefaultValue() != null;
			} else {
				return !EcorePackage.Literals.ESTRUCTURAL_FEATURE__DEFAULT_VALUE
						.getDefaultValue().equals(this.getDefaultValue());
			}
		case EcorePackage.EATTRIBUTE__UNSETTABLE:
			return this.isUnsettable() != (Boolean) EcorePackage.Literals.ESTRUCTURAL_FEATURE__UNSETTABLE
					.getDefaultValue();
		case EcorePackage.EATTRIBUTE__DERIVED:
			return this.isDerived() != (Boolean) EcorePackage.Literals.ESTRUCTURAL_FEATURE__DERIVED
					.getDefaultValue();
		case EcorePackage.EATTRIBUTE__ECONTAINING_CLASS:
			if (EcorePackage.Literals.ESTRUCTURAL_FEATURE__ECONTAINING_CLASS
					.getDefaultValue() == null) {
				return this.getEContainingClass() != null;
			} else {
				return !EcorePackage.Literals.ESTRUCTURAL_FEATURE__ECONTAINING_CLASS
						.getDefaultValue().equals(this.getEContainingClass());
			}
		case EcorePackage.EATTRIBUTE__ID:
			return (this.isID()) != (Boolean) EcorePackage.Literals.EATTRIBUTE__ID
					.getDefaultValue();
		case EcorePackage.EATTRIBUTE__EATTRIBUTE_TYPE:
			if (EcorePackage.Literals.EATTRIBUTE__EATTRIBUTE_TYPE
					.getDefaultValue() == null) {
				return this.getEAttributeType() != null;
			} else {
				return !EcorePackage.Literals.EATTRIBUTE__EATTRIBUTE_TYPE
						.getDefaultValue().equals(this.getEAttributeType());
			}
		}
		throw new InvalidFeatureException();
	}

	// EInternalObject
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
	public boolean isResolveProxies() {
		return false;
	}

	@Override
	public boolean isContainer() {
		return false;
	}

	@Override
	public boolean isContainment() {
		return false;
	}

	@Override
	public EReference getEOpposite() {
		return null;
	}

}

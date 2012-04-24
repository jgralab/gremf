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
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.BasicNotifierImpl.EAdapterList;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.BasicInternalEList;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.ECrossReferenceEList;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.emf.ecore.util.EcoreUtil;

import de.uni_koblenz.gremf.GrEMFSchemaType;
import de.uni_koblenz.gremf.exception.InvalidFeatureException;
import de.uni_koblenz.gremf.exception.UnsupportedFeature;
import de.uni_koblenz.gremf.exception.UnsupportedFeatureException;
import de.uni_koblenz.gremf.exception.UnsupportedSchemaChangeException;
import de.uni_koblenz.gremf.schema.GrEMFDomain;
import de.uni_koblenz.gremf.util.EModelElementUtil;
import de.uni_koblenz.gremf.util.EObjectUtil;
import de.uni_koblenz.jgralab.schema.impl.EnumDomainImpl;
import de.uni_koblenz.jgralab.schema.impl.PackageImpl;

public class GrEMFEnumDomainImpl extends EnumDomainImpl implements GrEMFDomain,
		EEnum, InternalEObject, GrEMFSchemaType {

	private EList<EAnnotation> eAnnotations;
	private EList<ETypeParameter> eTypeParameters;

	private BasicEList<EEnumLiteral> literals = new BasicEList<EEnumLiteral>();

	private int classifierID;

	public GrEMFEnumDomainImpl(String simpleName, PackageImpl parent,
			List<String> enumComponents) {
		super(simpleName, parent, enumComponents);
		for (String s : enumComponents) {
			EEnumLiteral lit = EcoreFactory.eINSTANCE.createEEnumLiteral();
			lit.setLiteral(s);
			lit.setName(s);
			this.literals.add(lit);
		}
		this.eAnnotations = new BasicInternalEList<EAnnotation>(
				EAnnotation.class);
		this.eTypeParameters = new EcoreEList.UnmodifiableEList<ETypeParameter>(
				this, EcorePackage.Literals.ECLASSIFIER__ETYPE_PARAMETERS, 0,
				new BasicEList<ETypeParameter>(0).data());
	}

	@Override
	public void addConst(String con) {
		super.addConst(con);
		if (this.literals == null) {
			this.literals = new BasicEList<EEnumLiteral>();
		}
		EEnumLiteral lit = EcoreFactory.eINSTANCE.createEEnumLiteral();
		lit.setLiteral(con);
		lit.setName(con);
		this.literals.add(lit);
	}

	@Override
	public EDataType getEMFDataType() {
		return this;
	}

	@Override
	public Object getEMFValue(Object jgralabValue) {
		return this.getEEnumLiteral((String) jgralabValue);
	}

	@Override
	public Object getJGraLabValue(Object emfValue) {
		if (emfValue instanceof EEnumLiteral) {
			return ((EEnumLiteral) emfValue).getLiteral();
		} else {
			return emfValue;
		}
	}

	public void setClassifierID(int value) {
		if (!super.getSchema().isFinished()) {
			this.classifierID = value;
		} else {
			throw new UnsupportedSchemaChangeException();
		}
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EDataType
	// --------------------------------------------------------------------------

	@Override
	public boolean isSerializable() {
		return true; // Enums are serializable
	}

	@Override
	public void setSerializable(boolean value) {
		throw new UnsupportedOperationException(
				"No Ecore model changes allowed.");
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EClassifier
	// --------------------------------------------------------------------------

	@Override
	public String getInstanceClassName() {
		return null;
	}

	@Override
	public void setInstanceClassName(String value) {
		throw new UnsupportedOperationException(
				"No Ecore model changes allowed.");
	}

	@Override
	public Class<?> getInstanceClass() {
		return null;
	}

	@Override
	public void setInstanceClass(Class<?> value) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public Object getDefaultValue() {
		return this.getELiterals().get(0);
	}

	@Override
	public String getInstanceTypeName() {
		return null;
	}

	@Override
	public void setInstanceTypeName(String value) {
		throw new UnsupportedSchemaChangeException();
	}

	@Override
	public EPackage getEPackage() {
		return (EPackage) this.getPackage();
	}

	@Override
	public EList<ETypeParameter> getETypeParameters() {
		return this.eTypeParameters;
	}

	@Override
	public boolean isInstance(Object object) {
		// only ok for generic, for standard it has to be a real enum
		if (object instanceof String) {
			String constantName = (String) object;
			for (String cn : this.getConsts()) {
				if (cn.equals(constantName)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int getClassifierID() {
		return this.classifierID;
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.ENamedElement
	// --------------------------------------------------------------------------

	@Override
	public String getName() {
		return this.getSimpleName();
	}

	@Override
	public void setName(String value) {
		throw new UnsupportedOperationException(
				"No Ecore model changes allowed.");
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
		return EcorePackage.Literals.EENUM;
	}

	@Override
	public Resource eResource() {
		return this.getEPackage().eResource();
	}

	@Override
	public EObject eContainer() {
		return this.getEPackage();
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
		return EcoreUtil.getAllContents(this, true);
	}

	@Override
	public boolean eIsProxy() {
		return false; // no proxies in jgralab
	}

	@Override
	public EList<EObject> eCrossReferences() {
		return ECrossReferenceEList.createECrossReferenceEList(this);
	}

	@Override
	public Object eGet(EStructuralFeature feature) {
		if (EObjectUtil.getFeatureID(this, feature) < 0) {
			throw new InvalidFeatureException(feature);
		}
		return this.eGet(feature.getFeatureID(), true, true);
	}

	@Override
	public Object eGet(EStructuralFeature feature, boolean resolve) {
		return this.eGet(feature);
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
		throw new UnsupportedOperationException(
				"Eoperations are not supported by grEMF.");
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.Notifier
	// --------------------------------------------------------------------------

	private EAdapterList<Adapter> eAdapters;

	@Override
	public EList<Adapter> eAdapters() {
		if (this.eAdapters == null) {
			this.eAdapters = new EAdapterList<Adapter>(this); // new
			// GrEMFUnmodifiableAdapterList(this);
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

	@Override
	public boolean eNotificationRequired() {
		return false;
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.InternalEObject
	// --------------------------------------------------------------------------

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
		return null;
	}

	@Override
	public EObject eObjectForURIFragmentSegment(String uriFragmentSegment) {
		if (uriFragmentSegment.charAt(0) == '%') {
			return this.getEAnnotation(URI.decode(uriFragmentSegment).replace(
					"%", ""));
		} else {
			return this.getEEnumLiteral(uriFragmentSegment);
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
				return GrEMFEnumDomainImpl.this;
			}

			@Override
			public Object get(boolean resolve) {
				return GrEMFEnumDomainImpl.this.eGet(eFeature, resolve);
			}

			@Override
			public EStructuralFeature getEStructuralFeature() {
				return eFeature;
			}

			@Override
			public void set(Object newValue) {
				GrEMFEnumDomainImpl.this.eSet(eFeature, newValue);
			}

			@Override
			public boolean isSet() {
				return GrEMFEnumDomainImpl.this.eIsSet(eFeature);
			}

			@Override
			public void unset() {
				GrEMFEnumDomainImpl.this.eUnset(eFeature);
			}
		};
	}

	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
		return derivedFeatureID;
	}

	@Override
	public int eContainerFeatureID() {
		if (this.getPackage() != null) {
			return EcorePackage.EDATA_TYPE__EPACKAGE;
		}
		return 0;
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
		// Enum is always in package
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
		throw new UnsupportedFeatureException(UnsupportedFeature.PROXY);
	}

	@Override
	public InternalEObject eInternalContainer() {
		return (InternalEObject) this.eContainer();
	}

	@Override
	public org.eclipse.emf.ecore.resource.Resource.Internal eInternalResource() {
		return (org.eclipse.emf.ecore.resource.Resource.Internal) this
				.eResource();
	}

	@Override
	public org.eclipse.emf.ecore.resource.Resource.Internal eDirectResource() {
		// Enum Domain always in Package, not directly in a Resource
		return null;
	}

	@Override
	public EStore eStore() {
		return null;
	}

	@Override
	public void eSetStore(EStore store) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object eGet(EStructuralFeature eFeature, boolean resolve,
			boolean coreType) {
		return this.eGet(eFeature);
	}

	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case EcorePackage.EENUM__EANNOTATIONS:
			return this.getEAnnotations();
		case EcorePackage.EENUM__NAME:
			return this.getName();
		case EcorePackage.EENUM__INSTANCE_CLASS_NAME:
			return this.getInstanceClassName();
		case EcorePackage.EENUM__INSTANCE_CLASS:
			return this.getInstanceClass();
		case EcorePackage.EENUM__DEFAULT_VALUE:
			return this.getDefaultValue();
		case EcorePackage.EENUM__INSTANCE_TYPE_NAME:
			return this.getInstanceTypeName();
		case EcorePackage.EENUM__EPACKAGE:
			return this.getEPackage();
		case EcorePackage.EENUM__ETYPE_PARAMETERS:
			return this.getETypeParameters();
		case EcorePackage.EENUM__SERIALIZABLE:
			return this.isSerializable();
		case EcorePackage.EENUM__ELITERALS:
			return this.getELiterals();
		}
		throw new InvalidFeatureException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case EcorePackage.EENUM__EANNOTATIONS:
			this.getEAnnotations().clear();
			this.getEAnnotations().addAll(
					(Collection<? extends EAnnotation>) newValue);
		case EcorePackage.EENUM__EPACKAGE:
			// in EMF not settable; EMF behavior
			throw new NullPointerException();
		default:
			throw new UnsupportedSchemaChangeException();
		}
	}

	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case EcorePackage.EENUM__EANNOTATIONS:
			this.getEAnnotations().clear();
			return;
		case EcorePackage.EENUM__EPACKAGE:
			// in EMF not settable; EMF behavior
			throw new NullPointerException();
		default:
			throw new UnsupportedSchemaChangeException();
		}

	}

	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case EcorePackage.EENUM__EANNOTATIONS:
			return (this.eAnnotations != null) && !this.eAnnotations.isEmpty();
		case EcorePackage.EENUM__NAME:
			return this.getName() != null;
		case EcorePackage.EENUM__INSTANCE_CLASS_NAME:
			return false; // no instance class name cause we don't generate
			// Ecore code
		case EcorePackage.EENUM__INSTANCE_CLASS:
			return false; // no instance class cause we don't generate Ecore
			// code
		case EcorePackage.EENUM__DEFAULT_VALUE:
			return true; // default value
		case EcorePackage.EENUM__INSTANCE_TYPE_NAME:
			return false; // no instance type name cause we don't generate Ecore
			// code
		case EcorePackage.EENUM__EPACKAGE:
			return this.getPackage() != null;
		case EcorePackage.EENUM__ETYPE_PARAMETERS:
			return false; // no Type Parameters supported
		case EcorePackage.EENUM__SERIALIZABLE:
			return false; // default ist true
		case EcorePackage.EENUM__ELITERALS:
			return (this.getELiterals() != null)
					&& !this.getELiterals().isEmpty();
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
	// org.eclipse.emf.ecore.EEnum
	// --------------------------------------------------------------------------

	@Override
	public EList<EEnumLiteral> getELiterals() {
		return new EcoreEList.UnmodifiableEList<EEnumLiteral>(this,
				EcorePackage.Literals.EENUM__ELITERALS, this.literals.size(),
				this.literals.data());
	}

	@Override
	public EEnumLiteral getEEnumLiteral(String name) {
		if (this.getConsts().contains(name)) {
			for (EEnumLiteral literal : this.getELiterals()) {
				if (literal.getName().equals(name)) {
					return literal;
				}
			}
		}
		return null;
	}

	@Override
	public EEnumLiteral getEEnumLiteral(int value) {
		if ((this.getELiterals().size() > value) && (0 <= value)) {
			return this.getELiterals().get(value);
		}
		return null;
	}

	@Override
	public EEnumLiteral getEEnumLiteralByLiteral(String literal) {
		for (EEnumLiteral lit : this.getELiterals()) {
			if (lit.getLiteral().equals(literal)) {
				return lit;
			}
		}
		return null;
	}

}

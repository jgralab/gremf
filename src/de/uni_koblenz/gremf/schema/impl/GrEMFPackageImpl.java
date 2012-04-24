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
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Internal;
import org.eclipse.emf.ecore.util.BasicInternalEList;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.ECrossReferenceEList;
import org.eclipse.emf.ecore.util.EcoreEList;

import de.uni_koblenz.gremf.GrEMFSchemaType;
import de.uni_koblenz.gremf.exception.InvalidFeatureException;
import de.uni_koblenz.gremf.exception.UnsupportedFeature;
import de.uni_koblenz.gremf.exception.UnsupportedFeatureException;
import de.uni_koblenz.gremf.exception.UnsupportedSchemaChangeException;
import de.uni_koblenz.gremf.util.EModelElementUtil;
import de.uni_koblenz.gremf.util.EObjectUtil;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.schema.impl.PackageImpl;
import de.uni_koblenz.jgralab.schema.impl.SchemaImpl;

public class GrEMFPackageImpl extends PackageImpl implements EPackage,
		InternalEObject, GrEMFSchemaType {

	private EFactory efactory;
	private EList<EAnnotation> eAnnotations;
	private EList<EClassifier> eClassifiers;
	private EList<EPackage> eSubpackages;
	private EPackage eSuperPackage;

	private Resource resource;
	private String nsPrefix;
	private String nsURI;

	private boolean finished;

	protected GrEMFPackageImpl(String simpleName, Package parentPackage,
			Schema schema) {
		super(simpleName, (PackageImpl) parentPackage, (SchemaImpl) schema);
		this.efactory = new GrEMFFactoryImpl(this);
		this.eAnnotations = new BasicInternalEList<EAnnotation>(
				EAnnotation.class);
		this.nsPrefix = super.schema.getPackagePrefix() + "."
				+ this.getQualifiedName().toLowerCase();
		this.nsURI = "http://" + this.nsPrefix + ".com";
	}

	protected void finish() {
		this.eClassifiers = this.getEClassifiers();
		this.eSubpackages = this.getESubpackages();
		this.eSuperPackage = this.getESuperPackage();
		this.finished = true;
	}

	protected boolean isFinished() {
		return this.finished;
	}

	@Override
	public void delete() {
		throw new UnsupportedSchemaChangeException();
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
		return EcorePackage.Literals.EPACKAGE;
	}

	@Override
	public Resource eResource() {
		if (this.getESuperPackage() != null) {
			return this.getESuperPackage().eResource();
		}
		return this.resource;
	}

	@Override
	public EObject eContainer() {
		if (this.getESuperPackage() != null) {
			return this.getESuperPackage();
		} else {
			return null;
		}
	}

	@Override
	public EStructuralFeature eContainingFeature() {
		if (this.getESuperPackage() != null) {
			return EcorePackage.Literals.EPACKAGE__ESUBPACKAGES;
		} else {
			return null;
		}
	}

	@Override
	public EReference eContainmentFeature() {
		if (this.getESuperPackage() != null) {
			return EcorePackage.Literals.EPACKAGE__ESUBPACKAGES;
		} else {
			return null;
		}

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
	// org.eclipse.emf.ecore.EPackage
	// --------------------------------------------------------------------------

	@Override
	public String getNsURI() {
		return this.nsURI;
	}

	@Override
	public void setNsURI(String value) {
		if (!super.getSchema().isFinished()) {
			this.nsURI = value;
		} else {
			throw new UnsupportedSchemaChangeException();
		}

	}

	@Override
	public String getNsPrefix() {
		return this.nsPrefix;
	}

	@Override
	public void setNsPrefix(String value) {
		if (!super.getSchema().isFinished()) {
			this.nsPrefix = value;
		} else {
			throw new UnsupportedSchemaChangeException();
		}
	}

	@Override
	public EFactory getEFactoryInstance() {
		return this.efactory;
	}

	@Override
	public void setEFactoryInstance(EFactory value) {
		this.efactory = value;
	}

	@Override
	public EList<EClassifier> getEClassifiers() {
		if ((this.eClassifiers == null) && !super.getSchema().isFinished()) {
			BasicEList<EClassifier> classifiers = new BasicEList<EClassifier>();
			for (VertexClass vrtxCls : super.getVertexClasses()) {
				classifiers.add((GrEMFVertexClassImpl) vrtxCls);

			}
			for (EdgeClass edgeCls : super.getEdgeClasses()) {
				if ((edgeCls instanceof GrEMFEdgeClassImpl)) {
					classifiers.add((GrEMFEdgeClassImpl) edgeCls);
				}
			}
			for (Domain d : super.getDomains()) {
				if (d instanceof EEnum) {
					classifiers.add((EEnum) d);
				} else if (d instanceof GrEMFRecordDomainImpl) {
					classifiers.add(((GrEMFRecordDomainImpl) d)
							.getEMFDataType());
				}
			}
			return new EcoreEList.UnmodifiableEList<EClassifier>(this,
					EcorePackage.Literals.EPACKAGE__ECLASSIFIERS,
					classifiers.size(), classifiers.data());
		} else {
			return this.eClassifiers;
		}

	}

	@Override
	public EClassifier getEClassifier(String name) {
		// for (EClassifier eClassifier : this.getEClassifiers()) {
		int size = this.getEClassifiers().size();
		for (int i = 0; i < size; i++) {
			EClassifier eClassifier = this.getEClassifiers().get(i);
			if (eClassifier.getName().equals(name)) {
				return eClassifier;
			}
		}
		return null;
	}

	@Override
	public EList<EPackage> getESubpackages() {
		if ((this.eSubpackages == null) && !super.getSchema().isFinished()) {
			BasicEList<EPackage> subpackages = new BasicEList<EPackage>(super
					.getSubPackages().size());

			for (Package pkg : super.getSubPackages()) {
				subpackages.add((GrEMFPackageImpl) pkg);
			}

			return new EcoreEList.UnmodifiableEList<EPackage>(this,
					EcorePackage.Literals.EPACKAGE__ESUBPACKAGES,
					subpackages.size(), subpackages.data());
		} else {
			return this.eSubpackages;
		}
	}

	@Override
	public EPackage getESuperPackage() {
		if ((this.eSuperPackage == null) && !super.getSchema().isFinished()) {
			EPackage p = this.findSuperPackage(this.getSchema()
					.getDefaultPackage());
			return p;
		} else {
			return this.eSuperPackage;
		}
	}

	private EPackage findSuperPackage(Package current) {
		if (current.getSubPackages().contains(this)) {
			return (EPackage) current;
		}

		for (Package p : current.getSubPackages()) {
			EPackage res = this.findSuperPackage(p);
			if (res != null) {
				return res;
			}
		}
		return null;

	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EInternalObject
	// --------------------------------------------------------------------------

	@Override
	public NotificationChain eSetResource(Internal resource,
			NotificationChain notifications) {
		if (this.getESuperPackage() == null) {
			this.resource = resource;
			return notifications;
		} else {
			throw new UnsupportedSchemaChangeException();

		}
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
			return this.getEClassifier(uriFragmentSegment);
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
				return GrEMFPackageImpl.this;
			}

			@Override
			public Object get(boolean resolve) {
				return GrEMFPackageImpl.this.eGet(eFeature, resolve);
			}

			@Override
			public EStructuralFeature getEStructuralFeature() {
				return eFeature;
			}

			@Override
			public void set(Object newValue) {
				GrEMFPackageImpl.this.eSet(eFeature, newValue);
			}

			@Override
			public boolean isSet() {
				return GrEMFPackageImpl.this.eIsSet(eFeature);
			}

			@Override
			public void unset() {
				GrEMFPackageImpl.this.eUnset(eFeature);
			}
		};
	}

	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
		return derivedFeatureID;
	}

	@Override
	public int eContainerFeatureID() {
		if (this.getESuperPackage() != null) {
			return EcorePackage.EPACKAGE__ESUPER_PACKAGE;
		} else {
			return 0;
		}
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
	public Resource.Internal eInternalResource() {
		return (Resource.Internal) this.eResource();
	}

	@Override
	public Resource.Internal eDirectResource() {
		if (this.getESuperPackage() != null) {
			return null;
		} else {
			return (Resource.Internal) this.eResource();
		}
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
		return this.eGet(eFeature.getFeatureID(), resolve, coreType);
	}

	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case EcorePackage.EPACKAGE__EANNOTATIONS:
			return this.getEAnnotations();
		case EcorePackage.EPACKAGE__ECLASSIFIERS:
			return this.getEClassifiers();
		case EcorePackage.EPACKAGE__EFACTORY_INSTANCE:
			return this.getEFactoryInstance();
		case EcorePackage.EPACKAGE__ESUBPACKAGES:
			return this.getESubpackages();
		case EcorePackage.EPACKAGE__ESUPER_PACKAGE:
			return this.getESuperPackage();
		case EcorePackage.EPACKAGE__NAME:
			return this.getName();
		case EcorePackage.EPACKAGE__NS_PREFIX:
			return this.getNsPrefix();
		case EcorePackage.EPACKAGE__NS_URI:
			return this.getNsURI();
		}

		throw new InvalidFeatureException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {

		switch (featureID) {
		case EcorePackage.EPACKAGE__EANNOTATIONS:
			this.getEAnnotations().clear();
			this.getEAnnotations().addAll(
					(Collection<? extends EAnnotation>) newValue);
			return;
		case EcorePackage.EPACKAGE__NS_URI:
			this.setNsURI((String) newValue);
			return;
		case EcorePackage.EPACKAGE__NS_PREFIX:
			this.setNsPrefix((String) newValue);
			return;
		case EcorePackage.EPACKAGE__EFACTORY_INSTANCE:
			this.setEFactoryInstance((EFactory) newValue);
			return;
		case EcorePackage.EPACKAGE__ESUPER_PACKAGE:
			// in EMF not settable
			throw new NullPointerException();
		default:
			throw new UnsupportedSchemaChangeException();
		}

	}

	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case EcorePackage.EPACKAGE__EANNOTATIONS:
			return (this.eAnnotations != null) && !this.eAnnotations.isEmpty();
		case EcorePackage.EPACKAGE__NAME:
			if (EcorePackage.Literals.ENAMED_ELEMENT__NAME.getDefaultValue() == null) {
				return this.getName() != null;
			} else {
				return !EcorePackage.Literals.ENAMED_ELEMENT__NAME
						.getDefaultValue().equals(this.getName());
			}
		case EcorePackage.EPACKAGE__NS_URI:
			if (EcorePackage.Literals.EPACKAGE__NS_URI.getDefaultValue() == null) {
				return this.getNsURI() != null;
			} else {
				return !EcorePackage.Literals.EPACKAGE__NS_URI
						.getDefaultValue().equals(this.getNsURI());
			}
		case EcorePackage.EPACKAGE__NS_PREFIX:
			if (EcorePackage.Literals.EPACKAGE__NS_PREFIX.getDefaultValue() == null) {
				return this.getNsURI() != null;
			} else {
				return !EcorePackage.Literals.EPACKAGE__NS_PREFIX
						.getDefaultValue().equals(this.getNsURI());
			}
		case EcorePackage.EPACKAGE__EFACTORY_INSTANCE:
			if (EcorePackage.Literals.EPACKAGE__EFACTORY_INSTANCE
					.getDefaultValue() == null) {
				return this.getEFactoryInstance() != null;
			} else {
				return !EcorePackage.Literals.EPACKAGE__EFACTORY_INSTANCE
						.getDefaultValue().equals(this.getEFactoryInstance());
			}
		case EcorePackage.EPACKAGE__ECLASSIFIERS:
			EList<EClassifier> cs = this.getEClassifiers();
			return (cs != null) && !cs.isEmpty();
		case EcorePackage.EPACKAGE__ESUBPACKAGES:
			EList<EPackage> ps = this.getESubpackages();
			return (ps != null) && !ps.isEmpty();
		case EcorePackage.EPACKAGE__ESUPER_PACKAGE:
			if (EcorePackage.Literals.EPACKAGE__ESUPER_PACKAGE
					.getDefaultValue() == null) {
				return this.getESuperPackage() != null;
			} else {
				return !EcorePackage.Literals.EPACKAGE__ESUPER_PACKAGE
						.getDefaultValue().equals(this.getESuperPackage());
			}
		}
		return false;
	}

	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case EcorePackage.EPACKAGE__EANNOTATIONS:
			this.getEAnnotations().clear();
			return;
		case EcorePackage.EPACKAGE__NS_URI:
			this.setNsURI((String) EcorePackage.Literals.EPACKAGE__NS_URI
					.getDefaultValue());
			return;
		case EcorePackage.EPACKAGE__NS_PREFIX:
			this.setNsPrefix((String) EcorePackage.Literals.EPACKAGE__NS_PREFIX
					.getDefaultValue());
			return;
		case EcorePackage.EPACKAGE__EFACTORY_INSTANCE:
			this.setEFactoryInstance((EFactory) EcorePackage.Literals.EPACKAGE__EFACTORY_INSTANCE
					.getDefaultValue());
			return;
		case EcorePackage.EPACKAGE__ESUPER_PACKAGE:
			// in EMF not settable; EMF behavior
			throw new NullPointerException();
		default:
			throw new UnsupportedSchemaChangeException();
		}
	}

	@Override
	public Object eInvoke(int operationID, EList<?> arguments)
			throws InvocationTargetException {
		throw new UnsupportedFeatureException(UnsupportedFeature.OPERATION);
	}

}

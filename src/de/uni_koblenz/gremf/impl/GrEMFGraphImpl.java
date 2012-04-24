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
package de.uni_koblenz.gremf.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.BasicNotifierImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EReferenceImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Internal;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.ECrossReferenceEList;
import org.eclipse.emf.ecore.util.EcoreUtil;

import de.uni_koblenz.gremf.GrEMFInstanceType;
import de.uni_koblenz.gremf.collection.GrEMFAttributesListProxy;
import de.uni_koblenz.gremf.collection.GrEMFGraphObjectListProxy;
import de.uni_koblenz.gremf.exception.InvalidFeatureException;
import de.uni_koblenz.gremf.exception.SchemaIncompatibilityException;
import de.uni_koblenz.gremf.exception.UnsupportedFeature;
import de.uni_koblenz.gremf.exception.UnsupportedFeatureException;
import de.uni_koblenz.gremf.notification.PreventNotifyCondition;
import de.uni_koblenz.gremf.schema.GrEMFDomain;
import de.uni_koblenz.gremf.schema.impl.GrEMFAttributeImpl;
import de.uni_koblenz.gremf.util.EObjectUtil;
import de.uni_koblenz.jgralab.impl.generic.GenericGraphImpl;
import de.uni_koblenz.jgralab.schema.CollectionDomain;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.VertexClass;

public class GrEMFGraphImpl extends GenericGraphImpl implements EObject,
		InternalEObject, GrEMFInstanceType {

	protected GrEMFGraphImpl(GraphClass type, String id, int vmax, int emax) {
		super(type, id, vmax, emax);
	}

	private PreventNotifyCondition<VertexClass> cond;
	private PreventNotifyCondition<EdgeClass> edgeClassCond;

	public PreventNotifyCondition<VertexClass> getVertexClassCondition() {
		return this.cond;
	}

	void setVertexClassCondition(PreventNotifyCondition<VertexClass> c) {
		this.cond = c;
	}

	public PreventNotifyCondition<EdgeClass> getEdgeClassCondition() {
		return this.edgeClassCond;
	}

	void setEdgeClassCondition(PreventNotifyCondition<EdgeClass> c) {
		this.edgeClassCond = c;
	}

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	// --------------------------------------------------------------------------
	// ---- Ecore
	// --------------------------------------------------------------------------
	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

	private EList<Adapter> eAdapters;
	private boolean deliverNotifications;
	private Resource eResource;

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.ENotifier
	// --------------------------------------------------------------------------

	@Override
	public EList<Adapter> eAdapters() {
		if (this.eAdapters == null) {
			this.eAdapters = new BasicNotifierImpl.EAdapterList<Adapter>(this);
		}
		return this.eAdapters;
	}

	@Override
	public boolean eDeliver() {
		return this.deliverNotifications;
	}

	@Override
	public void eSetDeliver(boolean deliver) {
		this.deliverNotifications = deliver;
	}

	@Override
	public void eNotify(Notification notification) {
		for (Adapter adapt : this.eAdapters) {
			adapt.notifyChanged(notification);
		}

	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.EObject
	// --------------------------------------------------------------------------

	@Override
	public EClass eClass() {
		return (EClass) this.getGraphClass();
	}

	@Override
	public Resource eResource() {
		return this.eResource;
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
		if (!EObjectUtil.isCorrectFeature(this, feature)) {
			throw new InvalidFeatureException(feature);
		}
		// Attribute
		else if (feature instanceof GrEMFAttributeImpl) {
			return this.eGetAttribute((GrEMFAttributeImpl) feature);
		}
		// EAttribute vertices
		else if ((feature instanceof EReferenceImpl)
				&& (feature.getName().equals("vertices") || feature.getName()
						.equals("edges"))) {
			return new GrEMFGraphObjectListProxy(this, (EReference) feature);
		} else {
			throw new InvalidFeatureException(feature);
		}
	}

	@Override
	public Object eGet(EStructuralFeature feature, boolean resolve) {
		return this.eGet(feature);
	}

	private Object eGetAttribute(GrEMFAttributeImpl feature) {
		// - Collection Attribute
		if (feature.getUpperBound() != 1) {
			return new GrEMFAttributesListProxy<Object>(feature, this);
		} else {
			return ((GrEMFDomain) feature.getDomain()).getEMFValue(this
					.getAttribute(feature.getName()));
		}
	}

	private void eSetAttribute(GrEMFAttributeImpl feature, Object newValue) {
		this.setAttribute(feature.getName(),
				((GrEMFDomain) feature.getDomain()).getJGraLabValue(newValue));
	}

	@Override
	public void eSet(EStructuralFeature feature, Object newValue) {
		if (!EObjectUtil.isCorrectFeature(this, feature)) {
			throw new InvalidFeatureException(feature);
		}
		// Attribute
		else if (feature instanceof GrEMFAttributeImpl) {
			this.eSetAttribute((GrEMFAttributeImpl) feature, newValue);
		}
		// The special EAttribute vertices is not allowed to change via eSet
		else if (feature instanceof EReferenceImpl) {
			throw new UnsupportedOperationException(
					"no changes of vertices and edges allowed");
		} else {
			throw new InvalidFeatureException(feature);
		}
	}

	@Override
	public boolean eIsSet(EStructuralFeature feature) {
		if (!EObjectUtil.isCorrectFeature(this, feature)) {
			throw new InvalidFeatureException(feature);
		}
		// Attribute
		else if (feature instanceof GrEMFAttributeImpl) {
			return !this.getAttribute(feature.getName()).equals(
					feature.getDefaultValue());
		}
		// The special EAttributes vertices / edges are always set
		else if (feature instanceof EReferenceImpl) {
			return true;
		} else {
			throw new InvalidFeatureException(feature);
		}
	}

	@Override
	public void eUnset(EStructuralFeature feature) {
		if (!EObjectUtil.isCorrectFeature(this, feature)) {
			throw new InvalidFeatureException(feature);
		}
		// Attribute
		else if (feature instanceof GrEMFAttributeImpl) {
			this.eUnsetAttribute((GrEMFAttributeImpl) feature);
		}
		// The special EAttributes vertices / edges can not become unset
		else if (feature instanceof EReferenceImpl) {
			throw new UnsupportedOperationException(
					"no changes of vertices and edges allowed");
		} else {
			throw new InvalidFeatureException(feature);
		}
	}

	private void eUnsetAttribute(GrEMFAttributeImpl feature) {
		this.getVertexClassCondition().setCondition(false);
		Object oldValue = this.getAttribute(feature.getName());
		this.eSet(feature, feature.getDefaultValue());

		if (this.eNotificationRequired()) {
			if (feature.getDomain() instanceof CollectionDomain) {
				if (((Collection<?>) oldValue).size() > 1) {
					this.eNotify(new ENotificationImpl(this,
							Notification.REMOVE_MANY, feature, oldValue,
							feature.getDefaultValue()));
				} else {
					this.eNotify(new ENotificationImpl(this,
							Notification.REMOVE, feature,
							((Collection<?>) oldValue).iterator().next(), null,
							0));
				}
			} else {
				this.eNotify(new ENotificationImpl(this, Notification.SET,
						feature, oldValue, feature.getDefaultValue()));
			}
		}
		this.getVertexClassCondition().setCondition(false);
	}

	@Override
	public Object eInvoke(EOperation operation, EList<?> arguments)
			throws InvocationTargetException {
		throw new UnsupportedFeatureException(UnsupportedFeature.OPERATION);
	}

	@Override
	public boolean eNotificationRequired() {
		return (this.deliverNotifications && (this.eAdapters != null));
	}

	@Override
	public String eURIFragmentSegment(EStructuralFeature eFeature,
			EObject eObject) {
		// no EReferences in Graph
		throw new InvalidFeatureException(eFeature);
	}

	@Override
	public EObject eObjectForURIFragmentSegment(String uriFragmentSegment) {
		// no EReferences in Graph
		throw new InvalidFeatureException();
	}

	@Override
	public void eSetClass(EClass eClass) {
		throw new UnsupportedFeatureException(
				UnsupportedFeature.INSTANCE_TYPE_CHANGE);
	}

	@Override
	public Setting eSetting(final EStructuralFeature eFeature) {
		if (!EObjectUtil.isCorrectFeature(this, eFeature)) {
			throw new InvalidFeatureException();
		}
		return new EStructuralFeature.Setting() {

			@Override
			public EObject getEObject() {
				return GrEMFGraphImpl.this;
			}

			@Override
			public Object get(boolean resolve) {
				return GrEMFGraphImpl.this.eGet(eFeature, resolve);
			}

			@Override
			public EStructuralFeature getEStructuralFeature() {
				return eFeature;
			}

			@Override
			public void set(Object newValue) {
				GrEMFGraphImpl.this.eSet(eFeature, newValue);
			}

			@Override
			public boolean isSet() {
				return GrEMFGraphImpl.this.eIsSet(eFeature);
			}

			@Override
			public void unset() {
				GrEMFGraphImpl.this.eUnset(eFeature);
			}
		};
	}

	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
		return derivedFeatureID;
	}

	@Override
	public int eContainerFeatureID() {
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
	public NotificationChain eSetResource(Internal resource,
			NotificationChain notifications) {
		this.eResource = resource;
		return notifications;
	}

	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd,
			int featureID, Class<?> baseClass, NotificationChain notifications) {
		throw new SchemaIncompatibilityException();
	}

	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd,
			int featureID, Class<?> baseClass, NotificationChain notifications) {
		throw new SchemaIncompatibilityException();
	}

	@Override
	public NotificationChain eBasicSetContainer(InternalEObject newContainer,
			int newContainerFeatureID, NotificationChain notifications) {
		throw new SchemaIncompatibilityException();
	}

	@Override
	public NotificationChain eBasicRemoveFromContainer(
			NotificationChain notifications) {
		throw new SchemaIncompatibilityException();
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
		return EcoreUtil.resolve(proxy, this.eResource.getResourceSet());
	}

	@Override
	public InternalEObject eInternalContainer() {
		return (InternalEObject) this.eContainer();
	}

	@Override
	public Internal eInternalResource() {
		return (Internal) this.eResource;
	}

	@Override
	public Internal eDirectResource() {
		return (Internal) this.eResource; // Graph is always directly in a
											// Resource
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
		EStructuralFeature feature = this.eClass().getEStructuralFeature(
				featureID);
		return this.eGet(feature);
	}

	@Override
	public void eSet(int featureID, Object newValue) {
		EStructuralFeature feature = this.eClass().getEStructuralFeature(
				featureID);
		this.eSet(feature, newValue);
	}

	@Override
	public void eUnset(int featureID) {
		EStructuralFeature feature = this.eClass().getEStructuralFeature(
				featureID);
		this.eUnset(feature);
	}

	@Override
	public boolean eIsSet(int featureID) {
		EStructuralFeature feature = this.eClass().getEStructuralFeature(
				featureID);
		return this.eIsSet(feature);
	}

	@Override
	public Object eInvoke(int operationID, EList<?> arguments)
			throws InvocationTargetException {
		throw new UnsupportedFeatureException(UnsupportedFeature.OPERATION);
	}

}

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
import java.util.List;

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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Internal;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.ECrossReferenceEList;
import org.eclipse.emf.ecore.util.EcoreUtil;

import de.uni_koblenz.gremf.GrEMFInstanceType;
import de.uni_koblenz.gremf.collection.GrEMFAttributesListProxy;
import de.uni_koblenz.gremf.exception.InvalidFeatureException;
import de.uni_koblenz.gremf.exception.SchemaIncompatibilityException;
import de.uni_koblenz.gremf.exception.UnsupportedFeature;
import de.uni_koblenz.gremf.exception.UnsupportedFeatureException;
import de.uni_koblenz.gremf.schema.GrEMFDomain;
import de.uni_koblenz.gremf.schema.impl.GrEMFAttributeImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFIncidenceClassWithRefsImpl;
import de.uni_koblenz.gremf.util.EObjectUtil;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.ReversedEdgeBaseImpl;
import de.uni_koblenz.jgralab.impl.generic.GenericEdgeImpl;
import de.uni_koblenz.jgralab.schema.CollectionDomain;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;

public class GrEMFEdgeImpl extends GenericEdgeImpl implements EObject,
		InternalEObject, GrEMFInstanceType {

	public GrEMFEdgeImpl(EdgeClass type, int anId, Graph graph, Vertex alpha,
			Vertex omega) {
		super(type, anId, graph, alpha, omega);
	}

	@Override
	protected ReversedEdgeBaseImpl createReversedEdge() {
		return new GrEMFReversedEdgeImpl(this, this.graph);
	}

	private GrEMFIncidenceClassWithRefsImpl getFrom() {
		return (GrEMFIncidenceClassWithRefsImpl) super
				.getAttributedElementClass().getFrom();
	}

	private GrEMFIncidenceClassWithRefsImpl getTo() {
		return (GrEMFIncidenceClassWithRefsImpl) super
				.getAttributedElementClass().getTo();

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
		return (EClass) this.getAttributedElementClass();
	}

	@Override
	public Resource eResource() {
		if (this.eResource != null) {
			return this.eResource;
		}
		EObject eContainer = this.eContainer();
		if (eContainer != null) {
			return eContainer.eResource();
		}
		return null;
	}

	@Override
	public EObject eContainer() {
		if ((this.getFrom().getToEdgeClass() != null)
				&& this.getFrom().getToEdgeClass().isContainment()) {
			return (EObject) this.getAlpha();
		} else if ((this.getTo().getToEdgeClass() != null)
				&& this.getTo().getToEdgeClass().isContainment()) {
			return (EObject) this.getOmega();
		} else {
			return null;
		}
	}

	@Override
	public EReference eContainingFeature() {
		if ((this.getFrom().getToEdgeClass() != null)
				&& this.getFrom().getToEdgeClass().isContainment()) {
			return this.getFrom().getToEdgeClass();
		} else if ((this.getTo().getToEdgeClass() != null)
				&& this.getTo().getToEdgeClass().isContainment()) {
			return this.getTo().getToEdgeClass();
		} else {
			return null;
		}
	}

	@Override
	public EReference eContainmentFeature() {
		return this.eContainingFeature();
	}

	@Override
	public EList<EObject> eContents() {
		// no Contents in an Edge
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
		// EReference
		else if (feature instanceof EReference) {
			EReference ref = (EReference) feature;
			// Reference points directly to alpha
			if (ref.equals(this.getFrom().getFromEdgeClass())) {
				return this.getAlpha();
			}
			// Reference is of SuperClass
			for (IncidenceClass inc : this.getAttributedElementClass()
					.getFrom().getSubsettedIncidenceClasses()) {
				if ((inc instanceof GrEMFIncidenceClassWithRefsImpl)
						&& ((GrEMFIncidenceClassWithRefsImpl) inc)
								.getFromEdgeClass().equals(ref)) {
					return this.getAlpha();
				}
			}
			// No alpha, must be omega
			return this.getOmega();
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
		// Resetting of EReferences not allowed
		else if (feature instanceof EReference) {
			throw new UnsupportedOperationException(
					"It ist not allowed to reset an edge's reference.");
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
			if ((this.getAttribute(feature.getName()) == null)
					|| this.getAttribute(feature.getName()).equals(
							feature.getDefaultValue())) {
				return false;
			} else {
				return true;
			}
		}
		// EReferences are set because an Edge must have alpha and omega
		else if (feature instanceof EReference) {
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
		// Unsetting of EReferences not allowed, edge must have alpha and
		// omega
		else if (feature instanceof EReference) {
			throw new UnsupportedOperationException(
					"It ist not allowed to unset alpha or omega of an Edge.");
		} else {
			throw new InvalidFeatureException(feature);
		}
	}

	private void eUnsetAttribute(GrEMFAttributeImpl feature) {
		((GrEMFGraphImpl) this.getGraph()).getVertexClassCondition()
				.setCondition(false);
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
		((GrEMFGraphImpl) this.getGraph()).getVertexClassCondition()
				.setCondition(false);
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
		if (!EObjectUtil.isCorrectFeature(this, eFeature)) {
			throw new InvalidFeatureException(eFeature);
		}
		if (eFeature.getUpperBound() > 1) {
			return '@' + eFeature.getName() + '.'
					+ ((List<?>) this.eGet(eFeature)).indexOf(eObject);
		} else {
			return "@" + eFeature.getName();
		}
	}

	@Override
	public EObject eObjectForURIFragmentSegment(String uriFragmentSegment) {
		// only works for EReferences - but no clear error message while trying
		// with EAttributes
		int dot = uriFragmentSegment.indexOf(".");
		if (dot >= 0) {
			String featureName = uriFragmentSegment.substring(1, dot);
			int index = Integer.parseInt(uriFragmentSegment.substring(dot + 1));
			EStructuralFeature feature = this.eClass().getEStructuralFeature(
					featureName);
			return (EObject) ((List<?>) this.eGet(feature)).get(index);
		} else {
			String featureName = uriFragmentSegment.substring(1);
			EStructuralFeature feature = this.eClass().getEStructuralFeature(
					featureName);
			return (EObject) this.eGet(feature);
		}
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
				return GrEMFEdgeImpl.this;
			}

			@Override
			public Object get(boolean resolve) {
				return GrEMFEdgeImpl.this.eGet(eFeature, resolve);
			}

			@Override
			public EStructuralFeature getEStructuralFeature() {
				return eFeature;
			}

			@Override
			public void set(Object newValue) {
				GrEMFEdgeImpl.this.eSet(eFeature, newValue);
			}

			@Override
			public boolean isSet() {
				return GrEMFEdgeImpl.this.eIsSet(eFeature);
			}

			@Override
			public void unset() {
				GrEMFEdgeImpl.this.eUnset(eFeature);
			}
		};
	}

	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
		return derivedFeatureID;
	}

	@Override
	public int eContainerFeatureID() {
		return 0; // no container
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
		// Originally called after adding an EReference instance with an
		// eOpposite EReference
		// Notification is not given to the user, so I think we can skip
		// implementing this method
		// If that make problems later, we can handle it then
		throw new UnsupportedOperationException();
	}

	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd,
			int featureID, Class<?> baseClass, NotificationChain notifications) {
		// Originally called after removing an EReference instance with an
		// eOpposite EReference
		// Notification is not given to the user, so I think we can skip
		// implementing this method
		// If that make problems later, we can handle it then
		throw new UnsupportedOperationException();
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
		if (this.eContainer() != null) {
			return null;
		} else {
			return (Internal) this.eResource;
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
		throw new UnsupportedOperationException();
	}

}

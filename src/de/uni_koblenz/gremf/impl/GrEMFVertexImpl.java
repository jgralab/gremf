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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.BasicNotifierImpl;
import org.eclipse.emf.common.util.BasicEList;
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
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.emf.ecore.util.EcoreUtil;

import de.uni_koblenz.gremf.GrEMFInstanceType;
import de.uni_koblenz.gremf.GrEMFSchemaType;
import de.uni_koblenz.gremf.collection.GrEMFAttributesListProxy;
import de.uni_koblenz.gremf.collection.GrEMFIncidencesListProxy;
import de.uni_koblenz.gremf.exception.InvalidFeatureException;
import de.uni_koblenz.gremf.exception.UnsupportedFeature;
import de.uni_koblenz.gremf.exception.UnsupportedFeatureException;
import de.uni_koblenz.gremf.notification.PreventNotifyCondition;
import de.uni_koblenz.gremf.schema.GrEMFDomain;
import de.uni_koblenz.gremf.schema.impl.GrEMFAttributeImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFEdgeClassImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFIncidenceClassImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFIncidenceClassWithRefsImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFVertexClassImpl;
import de.uni_koblenz.gremf.util.EObjectUtil;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.generic.GenericVertexImpl;
import de.uni_koblenz.jgralab.schema.CollectionDomain;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.IncidenceDirection;

public class GrEMFVertexImpl extends GenericVertexImpl implements EObject,
		InternalEObject, GrEMFInstanceType {

	protected GrEMFVertexImpl(GrEMFVertexClassImpl type, int id, Graph graph) {
		super(type, id, graph);
	}

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	// --------------------------------------------------------------------------
	// ---- Ecore
	// --------------------------------------------------------------------------
	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

	private EList<Adapter> eAdapters;
	private boolean deliverNotifications = true;
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
		GrEMFVertexClassImpl vc = (GrEMFVertexClassImpl) this
				.getAttributedElementClass();
		// for (EReference r : vc.getEAllContainers()) {
		for (int i = 0, size = vc.getEAllContainers().size(); i < size; i++) {
			EReference r = vc.getEAllContainers().get(i);
			if (r instanceof IncidenceClass) {
				IncidenceClass inc = (IncidenceClass) r;
				Iterator<Edge> edges;
				if (inc.getDirection() == IncidenceDirection.IN) {
					edges = this.incidences(inc.getEdgeClass(),
							EdgeDirection.IN).iterator();
				} else {
					edges = this.incidences(inc.getEdgeClass(),
							EdgeDirection.OUT).iterator();
				}
				if (edges.hasNext()) {
					if (inc.getEdgeClass() instanceof GrEMFEdgeClassImpl) {
						return (EObject) edges.next().getNormalEdge();
					} else if (inc.getDirection() == IncidenceDirection.IN) {
						return (EObject) edges.next().getAlpha();
					} else {
						return (EObject) edges.next().getOmega();
					}
				}
			}
		}
		return null;
	}

	@Override
	public EReference eContainingFeature() {

		GrEMFVertexClassImpl vc = (GrEMFVertexClassImpl) this
				.getAttributedElementClass();
		// for (EReference r : vc.getEAllContainers()) {
		for (int i = 0, size = vc.getEAllContainers().size(); i < size; i++) {
			EReference r = vc.getEAllContainers().get(i);
			if (r instanceof IncidenceClass) {
				IncidenceClass inc = (IncidenceClass) r;
				Iterator<Edge> edges;
				if (inc.getDirection() == IncidenceDirection.IN) {
					edges = this.incidences(inc.getEdgeClass(),
							EdgeDirection.IN).iterator();
				} else {
					edges = this.incidences(inc.getEdgeClass(),
							EdgeDirection.OUT).iterator();
				}
				if (edges.hasNext()) {
					if (inc.getDirection() == IncidenceDirection.IN) {
						if (inc.getEdgeClass() instanceof GrEMFEdgeClassImpl) {
							return ((GrEMFIncidenceClassWithRefsImpl) inc
									.getEdgeClass().getTo()).getFromEdgeClass();
						} else {
							return (EReference) inc.getEdgeClass().getTo();
						}
					} else {
						if (inc.getEdgeClass() instanceof GrEMFEdgeClassImpl) {
							return ((GrEMFIncidenceClassWithRefsImpl) inc
									.getEdgeClass().getFrom())
									.getFromEdgeClass();
						} else {
							return (EReference) inc.getEdgeClass().getFrom();
						}
					}
				}

			}
		}
		return null;
	}

	@Override
	public EReference eContainmentFeature() {
		return this.eContainingFeature();
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
		if (!EObjectUtil.isCorrectFeature(this, feature)) {
			throw new InvalidFeatureException(feature);
		}
		// Attribute
		else if (feature instanceof GrEMFAttributeImpl) {
			return this.eGetAttribute((GrEMFAttributeImpl) feature);
		}
		// Reference
		else if (feature instanceof GrEMFIncidenceClassImpl) {
			// - Non Collection Reference
			if (feature.getUpperBound() == 1) {
				IncidenceDirection dir = ((GrEMFIncidenceClassImpl) feature)
						.getDirection();
				Iterator<Edge> edges;
				if (dir == IncidenceDirection.OUT) {
					edges = this.incidences(
							((GrEMFIncidenceClassImpl) feature).getEdgeClass(),
							EdgeDirection.IN).iterator();
				} else {
					edges = this.incidences(
							((GrEMFIncidenceClassImpl) feature).getEdgeClass(),
							EdgeDirection.OUT).iterator();
				}
				if (edges.hasNext()) {
					if (dir == IncidenceDirection.OUT) {
						return edges.next().getAlpha();
					} else {
						return edges.next().getOmega();
					}
				} else {
					return null;
				}
			} else {
				// - Collection Reference
				return new GrEMFIncidencesListProxy(this,
						(GrEMFIncidenceClassImpl) feature);
			}
		}
		// Additonal EReferences for EClassEdgeClass
		else if (feature instanceof EReference) {
			EdgeClass ec = (EdgeClass) ((EReference) feature).getEOpposite()
					.getEContainingClass();
			GrEMFIncidenceClassWithRefsImpl from = (GrEMFIncidenceClassWithRefsImpl) ec
					.getFrom();
			EdgeDirection dir;
			if (feature.equals(from.getToEdgeClass())) {
				dir = EdgeDirection.OUT;
			} else {
				dir = EdgeDirection.IN;
			}
			BasicEList<EObject> list = new BasicEList<EObject>();
			for (Edge e : this.incidences(ec, dir)) {
				if (e instanceof GrEMFReversedEdgeImpl) {
					list.add((EObject) e.getReversedEdge());
				} else {
					list.add((EObject) e);
				}
			}
			return new EcoreEList.UnmodifiableEList<EReference>(this, feature,
					list.size(), list.data());

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

	@SuppressWarnings("unchecked")
	@Override
	public void eSet(EStructuralFeature feature, Object newValue) {
		if (!EObjectUtil.isCorrectFeature(this, feature)) {
			throw new InvalidFeatureException(feature);
		}

		// Attribute
		else if (feature instanceof GrEMFAttributeImpl) {
			this.eSetAttribute((GrEMFAttributeImpl) feature, newValue);
		}

		// EReference
		else if (feature instanceof GrEMFIncidenceClassImpl) {
			GrEMFIncidenceClassImpl inc = ((GrEMFIncidenceClassImpl) feature);
			EdgeClass ec = inc.getEdgeClass();
			PreventNotifyCondition<EdgeClass> cond = ((GrEMFGraphImpl) this.graph)
					.getEdgeClassCondition();
			cond.setCondition(false);

			Object oldValue = this.eGet(feature);
			if (oldValue instanceof GrEMFIncidencesListProxy) {
				oldValue = new ArrayList<EObject>(
						((Collection<EObject>) oldValue));
			}

			// IncidenceDirection.OUT
			if (inc.getDirection().equals(IncidenceDirection.OUT)) {
				this.eSetOutIncidence(feature, newValue, ec, oldValue);
			}
			// IncidenceDirection.IN
			else {
				this.eSetInIncidence(feature, newValue, ec, oldValue);
			}
			cond.setCondition(true);
		}// - end EReference
		else {
			throw new InvalidFeatureException(feature);
		}
	}

	private void eSetInIncidence(EStructuralFeature feature, Object newValue,
			EdgeClass ec, Object oldValue) {
		// - delete old Edges
		List<Edge> toDelete = new ArrayList<Edge>();
		for (Edge e : this.incidences(ec, EdgeDirection.OUT)) {
			toDelete.add(e);
		}
		for (Edge e : toDelete) {
			e.delete();
		}

		// - create one Edge if newValue is a Vertex
		if (feature.getUpperBound() == 1) {
			if (newValue != null) {
				this.getGraph().createEdge(ec, this, (Vertex) newValue);
			}
			this.notifySetSingleEdge(feature, newValue, oldValue);
		}
		// - create Edges due to the number of elements in newValue
		else {
			if (newValue != null) {
				for (Object v : (List<?>) newValue) {
					this.getGraph().createEdge(ec, this, (Vertex) v);
				}
			}
			this.notifySetManyEdges(feature, newValue, oldValue);
		}
	}

	private void eSetOutIncidence(EStructuralFeature feature, Object newValue,
			EdgeClass ec, Object oldValue) {
		// - delete old Edges
		ArrayList<Edge> toDelete = new ArrayList<Edge>();
		for (Edge e : this.incidences(ec, EdgeDirection.IN)) {
			toDelete.add(e);
		}
		for (Edge e : toDelete) {
			e.delete();
		}
		// - create one Edge if newValue is a Vertex
		if (feature.getUpperBound() == 1) {
			this.getGraph().createEdge(ec, (Vertex) newValue, this);
			this.notifySetSingleEdge(feature, newValue, oldValue);
		}
		// - create Edges due to the number of elements in newValue
		else {
			if (newValue != null) {
				for (Object v : (List<?>) newValue) {
					this.getGraph().createEdge(ec, (Vertex) v, this);
				}
			}
			this.notifySetManyEdges(feature, newValue, oldValue);

		}
	}

	private void notifySetSingleEdge(EStructuralFeature feature,
			Object newValue, Object oldValue) {
		if (this.eNotificationRequired()) {
			this.eNotify(new ENotificationImpl(this, Notification.SET, feature,
					oldValue, newValue));
		}
	}

	private void notifySetManyEdges(EStructuralFeature feature,
			Object newValue, Object oldValue) {
		if (this.eNotificationRequired()) {
			this.eNotify(new ENotificationImpl(this, Notification.REMOVE_MANY,
					feature, oldValue, null));
			if (newValue != null) {
				if (((List<?>) newValue).size() > 1) {
					this.eNotify(new ENotificationImpl(this,
							Notification.ADD_MANY, feature, null, newValue, 0));
				} else {
					this.eNotify(new ENotificationImpl(this, Notification.ADD,
							feature, null, ((List<?>) newValue).get(0), 0));
				}
			}
		}
	}

	private void eSetAttribute(GrEMFAttributeImpl feature, Object newValue) {
		this.setAttribute(feature.getName(),
				((GrEMFDomain) feature.getDomain()).getJGraLabValue(newValue));
	}

	@Override
	public boolean eIsSet(EStructuralFeature feature) {
		if (!EObjectUtil.isCorrectFeature(this, feature)) {
			throw new InvalidFeatureException(feature);
		}
		// Attribute
		else if (feature instanceof GrEMFAttributeImpl) {
			Object attributeValue = this.getAttribute(feature.getName());
			if (attributeValue == null) {
				return false;
			} else {
				return !attributeValue.equals(feature.getDefaultValue());
			}

		}
		// EReference
		else if (feature instanceof GrEMFIncidenceClassImpl) {
			GrEMFIncidenceClassImpl inc = (GrEMFIncidenceClassImpl) feature;
			if (inc.getDirection() == IncidenceDirection.OUT) {
				return this.incidences(inc.getEdgeClass(), EdgeDirection.IN)
						.iterator().hasNext();
			} else {
				return this.incidences(inc.getEdgeClass(), EdgeDirection.OUT)
						.iterator().hasNext();
			}
			// if (inc.getDirection() == IncidenceDirection.OUT) {
			// return this.getDegree(inc.getEdgeClass(), EdgeDirection.IN) > 0;
			// } else {
			// return this.getDegree(inc.getEdgeClass(), EdgeDirection.OUT) > 0;
			// }
		}
		// additional EReferences for EdgeClasses
		else if (feature instanceof EReference) {
			Object o = this.eGet(feature);
			if (o == null) {
				return false;
			} else if ((o instanceof Collection)
					&& ((Collection<?>) o).isEmpty()) {
				return false;
			}
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
		// EReference
		else if (feature instanceof GrEMFIncidenceClassImpl) {
			GrEMFIncidenceClassImpl inc = (GrEMFIncidenceClassImpl) feature;
			List<Edge> toDelete = new ArrayList<Edge>();
			List<EObject> oldValueList = new ArrayList<EObject>();
			if (inc.getDirection().equals(IncidenceDirection.OUT)) {
				for (Edge e : this.incidences(inc.getEdgeClass(),
						EdgeDirection.IN)) {
					toDelete.add(e);
					oldValueList.add((EObject) e.getAlpha());
				}
			} else {
				for (Edge e : this.incidences(inc.getEdgeClass(),
						EdgeDirection.OUT)) {
					toDelete.add(e);
					oldValueList.add((EObject) e.getOmega());
				}
			}
			PreventNotifyCondition<EdgeClass> cond = ((GrEMFGraphImpl) this.graph)
					.getEdgeClassCondition();

			cond.setCondition(false);

			for (Edge e : toDelete) {
				e.delete();
			}
			this.notifyUnsetEdges(feature, toDelete, oldValueList);
			cond.setCondition(true);
		} else {
			throw new InvalidFeatureException(feature);
		}

	}

	private void notifyUnsetEdges(EStructuralFeature feature,
			List<Edge> toDelete, List<EObject> oldValueList) {
		if (this.eNotificationRequired()) {
			if (feature.getUpperBound() == 1) {
				Notification not = new ENotificationImpl(this,
						Notification.SET, feature,
						oldValueList.isEmpty() ? null : oldValueList.get(0),
						null);
				this.eNotify(not);
			} else if ((toDelete.size() > 1)) {
				Notification not = new ENotificationImpl(this,
						Notification.REMOVE_MANY, feature, oldValueList, null);
				this.eNotify(not);
			} else if (toDelete.size() == 1) {
				Notification not = new ENotificationImpl(this,
						Notification.REMOVE, feature, oldValueList.get(0),
						null, 0);
				this.eNotify(not);
			}
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
		if (EObjectUtil.getFeatureID(this, eFeature) < 0) {
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
		if (EObjectUtil.getFeatureID(this, eFeature) < 0) {
			throw new InvalidFeatureException();
		}
		return new EStructuralFeature.Setting() {

			@Override
			public EObject getEObject() {
				return GrEMFVertexImpl.this;
			}

			@Override
			public Object get(boolean resolve) {
				return GrEMFVertexImpl.this.eGet(eFeature, resolve);
			}

			@Override
			public EStructuralFeature getEStructuralFeature() {
				return eFeature;
			}

			@Override
			public void set(Object newValue) {
				GrEMFVertexImpl.this.eSet(eFeature, newValue);
			}

			@Override
			public boolean isSet() {
				return GrEMFVertexImpl.this.eIsSet(eFeature);
			}

			@Override
			public void unset() {
				GrEMFVertexImpl.this.eUnset(eFeature);
			}
		};
	}

	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
		return derivedFeatureID;
	}

	@Override
	public int eContainerFeatureID() {

		GrEMFVertexClassImpl vc = (GrEMFVertexClassImpl) this
				.getAttributedElementClass();
		for (EReference r : vc.getEAllContainers()) {
			if (r instanceof IncidenceClass) {
				IncidenceClass inc = (IncidenceClass) r;
				Iterator<Edge> edges;
				if (inc.getDirection() == IncidenceDirection.IN) {
					edges = this.incidences(inc.getEdgeClass(),
							EdgeDirection.IN).iterator();
				} else {
					edges = this.incidences(inc.getEdgeClass(),
							EdgeDirection.OUT).iterator();
				}
				if (edges.hasNext()) {
					if (((GrEMFIncidenceClassImpl) inc.getOpposite())
							.isInvisible()) {
						return -1
								* ((GrEMFIncidenceClassImpl) inc)
										.getFeatureID();
					} else {
						return ((GrEMFIncidenceClassImpl) inc.getOpposite())
								.getFeatureID();
					}
				}
			}
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
	public NotificationChain eSetResource(Internal resource,
			NotificationChain notifications) {
		this.eResource = resource;
		for (Edge e : this.incidences()) {
			if ((e instanceof GrEMFInstanceType)
					&& (e.getAttributedElementClass() instanceof GrEMFSchemaType)) {

				InternalEObject obj;
				if (e.isNormal()) {
					obj = (InternalEObject) e;
				} else {
					obj = (InternalEObject) e.getReversedEdge();
				}
				if ((obj.eResource() == null) && (resource != null)) {
					resource.getContents().add(obj);
					obj.eSetResource(resource, notifications);
				}
			}

		}

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
		// throw new UnsupportedOperationException();
		return notifications;
	}

	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd,
			int featureID, Class<?> baseClass, NotificationChain notifications) {
		// Originally called after removing an EReference instance with an
		// eOpposite EReference
		// Notification is not given to the user, so I think we can skip
		// implementing this method
		// If that make problems later, we can handle it then
		// throw new UnsupportedOperationException();
		return notifications;
	}

	@Override
	public NotificationChain eBasicSetContainer(InternalEObject newContainer,
			int newContainerFeatureID, NotificationChain notifications) {
		// Originally called after adding an containment EReference instance
		// with an eOpposite EReference
		// Notification is not given to the user, so I think we can skip
		// implementing this method
		// If that make problems later, we can handle it then
		throw new UnsupportedOperationException();
	}

	@Override
	public NotificationChain eBasicRemoveFromContainer(
			NotificationChain notifications) {
		// Originally called after removing an containment EReference instance
		// with an eOpposite EReference
		// Notification is not given to the user, so I think we can skip
		// implementing this method
		// If that make problems later, we can handle it then
		throw new UnsupportedOperationException();
	}

	@Override
	public URI eProxyURI() {
		// I think, that is ok, because we don't support proxys
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
		throw new UnsupportedFeatureException(UnsupportedFeature.OPERATION);
	}

}

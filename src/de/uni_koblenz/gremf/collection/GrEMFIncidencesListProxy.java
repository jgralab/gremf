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
package de.uni_koblenz.gremf.collection;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.InternalEList;

import de.uni_koblenz.gremf.impl.GrEMFGraphImpl;
import de.uni_koblenz.gremf.impl.GrEMFVertexImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFIncidenceClassImpl;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.InternalEdge;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.IncidenceDirection;

public class GrEMFIncidencesListProxy implements EList<EObject>,
		InternalEList<EObject> {

	private GrEMFIncidenceClassImpl feature;
	private GrEMFVertexImpl gremfVertex;
	private EdgeClass edgeClass;
	private EdgeDirection edgeDirection;

	public GrEMFIncidencesListProxy(EObject o, EReference f) {
		this.gremfVertex = (GrEMFVertexImpl) o;
		this.feature = (GrEMFIncidenceClassImpl) f;
		this.edgeClass = this.feature.getEdgeClass();
		if (this.feature.getDirection().equals(IncidenceDirection.IN)) {
			this.edgeDirection = EdgeDirection.OUT;
		} else {
			this.edgeDirection = EdgeDirection.IN;
		}
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// java.util.List
	// --------------------------------------------------------------------------

	@Override
	public boolean add(EObject e) {
		this.createEdge(e);
		return true;
	}

	@Override
	public void add(int index, EObject element) {
		int degree = this.gremfVertex.getDegree(this.edgeClass,
				this.edgeDirection);
		if (index > degree) {
			throw new IndexOutOfBoundsException();
		}
		Edge e = this.createEdge(element);
		if (index != (degree - 1)) {
			// Edge edgeAtIndex = this.getEdge(index)
			Edge edgeAtIndex = this.getEdge(index).getNormalEdge();
			this.gremfVertex.putIncidenceBefore((InternalEdge) edgeAtIndex,
					(InternalEdge) e);
		}
	}

	@Override
	public boolean addAll(Collection<? extends EObject> c) {
		int size = c.size();
		if (size > 1) {
			this.disableECANotification();
		}
		for (EObject ob : c) {
			this.createEdge(ob);
			if (size > 1) {
				this.notifyEOppositeAdd(ob);
			}
		}
		if ((size > 1) && this.gremfVertex.eNotificationRequired()) {
			this.gremfVertex.eNotify(new ENotificationImpl(this.gremfVertex,
					Notification.ADD_MANY, this.feature, null, c, this.size()
							- size));
		}
		this.enableECANotification();

		return true;
	}

	@SuppressWarnings("unchecked")
	private void notifyEOppositeAdd(EObject ob) {
		if (((InternalEObject) ob).eNotificationRequired()) {
			if (this.feature.getEOpposite() != null) {
				ob.eNotify(new ENotificationImpl((InternalEObject) ob,
						Notification.ADD, this.feature.getEOpposite(), null,
						this.gremfVertex, ((List<EObject>) ob.eGet(this.feature
								.getEOpposite())).size()));
			}
		}
	}

	@Override
	public boolean addAll(int index, Collection<? extends EObject> c) {
		int oldDegree = this.gremfVertex.getDegree(this.edgeClass,
				this.edgeDirection);
		if (index > oldDegree) {
			throw new IndexOutOfBoundsException();
		} else if (index == oldDegree) {
			this.addAll(c);
		} else {
			this.disableECANotification();

			Edge edgeAtIndex = this.getEdge(index);
			for (EObject ob : c) {
				Edge e = this.createEdge(ob);
				this.gremfVertex.putIncidenceBefore((InternalEdge) edgeAtIndex,
						(InternalEdge) e);
				this.notifyEOppositeAdd(ob);
			}
			if (this.gremfVertex.eNotificationRequired()) {
				int size = c.size();
				if (size > 1) {
					this.gremfVertex.eNotify(new ENotificationImpl(
							this.gremfVertex, Notification.ADD_MANY,
							this.feature, null, c, index));
				} else if (size == 1) {
					this.gremfVertex.eNotify(new ENotificationImpl(
							this.gremfVertex, Notification.ADD, this.feature,
							null, c.iterator().next(), index));
				}
			}
			this.enableECANotification();
		}
		return true;
	}

	@Override
	public void clear() {
		ArrayList<EObject> list = new ArrayList<EObject>();
		ArrayList<Edge> toDelete = new ArrayList<Edge>();
		this.disableECANotification();
		if (this.feature.getDirection() == IncidenceDirection.IN) {
			for (Edge e : this.gremfVertex.incidences(this.edgeClass,
					this.edgeDirection)) {
				toDelete.add(e);
				list.add((EObject) e.getOmega());
			}
		} else {
			for (Edge e : this.gremfVertex.incidences(this.edgeClass,
					this.edgeDirection)) {
				toDelete.add(e);
				list.add((EObject) e.getAlpha());
			}
		}
		for (Edge e : toDelete) {
			e.delete();
		}
		if (this.gremfVertex.eNotificationRequired()) {
			int size = list.size();
			if (size > 1) {
				this.gremfVertex.eNotify(new ENotificationImpl(
						this.gremfVertex, Notification.REMOVE_MANY,
						this.feature, list, null));
			} else if (size == 1) {
				this.gremfVertex.eNotify(new ENotificationImpl(
						this.gremfVertex, Notification.REMOVE, this.feature,
						list.get(0), null, 0));
			}
		}
		this.enableECANotification();
	}

	@Override
	public boolean contains(Object o) {
		if (this.feature.getDirection() == IncidenceDirection.OUT) {
			for (Edge edge : this.gremfVertex.incidences(this.edgeClass,
					this.edgeDirection)) {
				if (edge.getAlpha().equals(o)) {
					return true;
				}
			}
		} else {
			for (Edge edge : this.gremfVertex.incidences(this.edgeClass,
					this.edgeDirection)) {
				if (edge.getOmega().equals(o)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!this.contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public EObject get(int index) {
		int n = 0;
		if (this.feature.getDirection() == IncidenceDirection.OUT) {
			for (Edge e : this.gremfVertex.incidences(this.edgeClass,
					this.edgeDirection)) {
				if (n == index) {
					return (EObject) e.getAlpha();
				}
				n++;
			}
		} else {
			for (Edge e : this.gremfVertex.incidences(this.edgeClass,
					this.edgeDirection)) {
				if (n == index) {
					return (EObject) e.getOmega();
				}
				n++;
			}
		}
		throw new IndexOutOfBoundsException();
	}

	@Override
	public int indexOf(Object o) {
		int n = 0;
		if (this.feature.getDirection() == IncidenceDirection.OUT) {
			for (Edge e : this.gremfVertex.incidences(this.edgeClass,
					this.edgeDirection)) {
				if (e.getAlpha().equals(o)) {
					return n;
				}
				n++;
			}
		} else {
			for (Edge e : this.gremfVertex.incidences(this.edgeClass,
					this.edgeDirection)) {
				if (e.getOmega().equals(o)) {
					return n;
				}
				n++;
			}
		}
		return -1;
	}

	@Override
	public boolean isEmpty() {
		return !this.gremfVertex.incidences(this.edgeClass, this.edgeDirection)
				.iterator().hasNext();
		// return this.gremfVertex.getDegree(this.edgeClass, this.edgeDirection)
		// < 1;
	}

	@Override
	public Iterator<EObject> iterator() {
		if (this.isEmpty()) {
			return (new BasicEList<EObject>()).iterator();
		}
		return this.createIncidenceList().iterator();
		// return (Iterator<EObject>) this.gremfVertex.adjacences(
		// this.feature.getRolename()).iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return this.createIncidenceList().lastIndexOf(o);
		// return this.gremfVertex.adjacences(this.feature.getRolename())
		// .lastIndexOf(o);
	}

	@Override
	public ListIterator<EObject> listIterator() {
		return this.listIterator(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ListIterator<EObject> listIterator(int index) {
		if (this.isEmpty()) {
			return (ListIterator<EObject>) ECollections.EMPTY_ELIST
					.listIterator();
		}
		return this.createIncidenceList().listIterator(index);
		// return (ListIterator<EObject>) this.gremfVertex.adjacences(
		// this.feature.getRolename()).listIterator(index);
	}

	private AbstractList<EObject> createIncidenceList() {
		if (this.feature.getDirection() == IncidenceDirection.OUT) {
			return this.createOutIncidenceList();
		} else {
			return this.createInIcidenceList();
		}
	}

	private AbstractList<EObject> createInIcidenceList() {
		AbstractList<EObject> incidences = new ArrayList<EObject>();
		for (Edge e : this.gremfVertex.incidences(this.edgeClass,
				this.edgeDirection)) {
			incidences.add((EObject) e.getOmega());
		}
		return incidences;
	}

	private AbstractList<EObject> createOutIncidenceList() {
		AbstractList<EObject> incidences = new ArrayList<EObject>();
		for (Edge e : this.gremfVertex.incidences(this.edgeClass,
				this.edgeDirection)) {
			incidences.add((EObject) e.getAlpha());
		}
		return incidences;
	}

	private int getEOppositeIndex(Edge e, GrEMFVertexImpl target) {
		if (this.feature.getDirection() == IncidenceDirection.OUT) {
			int i = 0;
			for (Edge edge : target.incidences(this.edgeClass,
					EdgeDirection.OUT)) {
				if (e.equals(edge)) {
					return i;
				}
				i++;
			}
		} else {
			int i = 0;
			for (Edge edge : target
					.incidences(this.edgeClass, EdgeDirection.IN)) {
				if (e.equals(edge)) {
					return i;
				}
				i++;
			}
		}
		return -1;
	}

	@Override
	public boolean remove(Object o) {
		if (this.feature.getDirection() == IncidenceDirection.OUT) {
			int n = 0;
			for (Edge e : this.gremfVertex.incidences(this.edgeClass,
					this.edgeDirection)) {
				if (e.getAlpha().equals(o)) {
					this.disableECANotification();
					int oppindex = this.getEOppositeIndex(e,
							(GrEMFVertexImpl) o);
					e.delete();
					this.notifyRemoveWithEOpp(o, n, oppindex);
					this.enableECANotification();
					return true;
				}
				n++;
			}
		} else {
			int n = 0;
			for (Edge e : this.gremfVertex.incidences(this.edgeClass,
					this.edgeDirection)) {
				if (e.getOmega().equals(o)) {
					this.disableECANotification();
					int oppindex = this.getEOppositeIndex(e,
							(GrEMFVertexImpl) o);
					e.delete();
					this.notifyRemoveWithEOpp(o, n, oppindex);
					this.enableECANotification();
					return true;
				}
				n++;
			}
		}
		return false;
	}

	private void notifyRemoveWithEOpp(Object o, int n, int oppindex) {
		if (this.gremfVertex.eNotificationRequired()) {
			this.gremfVertex.eNotify(new ENotificationImpl(this.gremfVertex,
					Notification.REMOVE, this.feature, o, null, n));
		}
		if (((InternalEObject) o).eNotificationRequired()) {
			if (this.feature.getEOpposite() != null) {
				((EObject) o).eNotify(new ENotificationImpl(
						(InternalEObject) o, Notification.REMOVE, this.feature
								.getEOpposite(), this.gremfVertex, null,
						oppindex));
			}
		}
	}

	@Override
	public EObject remove(int index) {
		int n = 0;
		if (this.feature.getDirection() == IncidenceDirection.OUT) {
			for (Edge e : this.gremfVertex.incidences(this.edgeClass,
					this.edgeDirection)) {
				if (index == n) {
					this.disableECANotification();
					Vertex alpha = e.getAlpha();
					int oppindex = this.getEOppositeIndex(e,
							(GrEMFVertexImpl) alpha);
					e.delete();
					this.notifyRemoveWithEOpp(alpha, n, oppindex);
					this.enableECANotification();
					return (EObject) alpha;
				}
				n++;
			}
		} else {
			for (Edge e : this.gremfVertex.incidences(this.edgeClass,
					this.edgeDirection)) {
				if (index == n) {
					this.disableECANotification();
					Vertex omega = e.getOmega();
					int oppindex = this.getEOppositeIndex(e,
							(GrEMFVertexImpl) omega);
					e.delete();
					this.notifyRemoveWithEOpp(omega, n, oppindex);
					this.enableECANotification();
					return (EObject) omega;
				}
				n++;
			}
		}
		throw new IndexOutOfBoundsException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		this.disableECANotification();

		boolean changed = false;
		ArrayList<Integer> deletedIndices = new ArrayList<Integer>();
		ArrayList<Edge> toDelete = new ArrayList<Edge>();
		ArrayList<EObject> deletedEObjects = new ArrayList<EObject>();
		int i = 0;
		if (this.feature.getDirection() == IncidenceDirection.IN) {
			for (Edge e : this.gremfVertex.incidences(this.edgeClass,
					this.edgeDirection)) {
				if (c.contains(e.getOmega())) {
					toDelete.add(e);
					deletedIndices.add(i);
					deletedEObjects.add((EObject) e.getOmega());
					changed = true;
				}
				i++;
			}
			for (Edge e : toDelete) {
				EObject target = (EObject) e.getOmega();
				int oppIndex = this.getEOppositeIndex(e,
						(GrEMFVertexImpl) target);
				e.delete();
				this.notifyEOppositeRemove(target, oppIndex);
			}
		} else {
			for (Edge e : this.gremfVertex.incidences(this.edgeClass,
					this.edgeDirection)) {
				if (c.contains(e.getAlpha())) {
					toDelete.add(e);
					deletedIndices.add(i);
					deletedEObjects.add((EObject) e.getAlpha());
					changed = true;
				}
				i++;
			}
			for (Edge e : toDelete) {
				EObject target = (EObject) e.getAlpha();
				int oppIndex = this.getEOppositeIndex(e,
						(GrEMFVertexImpl) target);
				e.delete();
				this.notifyEOppositeRemove(target, oppIndex);

			}
		}

		if (this.gremfVertex.eNotificationRequired()) {
			int[] intArray = new int[deletedIndices.size()];
			for (i = 0; i < intArray.length; i++) {
				intArray[i] = deletedIndices.get(i);
			}

			if (intArray.length > 1) {
				this.gremfVertex.eNotify(new ENotificationImpl(
						this.gremfVertex, Notification.REMOVE_MANY,
						this.feature, deletedEObjects, intArray, deletedIndices
								.get(0)));
			} else if (intArray.length == 1) {
				this.gremfVertex.eNotify(new ENotificationImpl(
						this.gremfVertex, Notification.REMOVE, this.feature,
						deletedEObjects.get(0), null, deletedIndices.get(0)));
			}
		}

		this.enableECANotification();
		return changed;
	}

	private void notifyEOppositeRemove(EObject target, int oppIndex) {
		if (((InternalEObject) target).eNotificationRequired()) {
			if (this.feature.getEOpposite() != null) {
				target.eNotify(new ENotificationImpl((InternalEObject) target,
						Notification.REMOVE, this.feature.getEOpposite(),
						this.gremfVertex, null, oppIndex));
			}
		}
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean changed = false;
		ArrayList<Edge> toDelete = new ArrayList<Edge>();
		ArrayList<Integer> deleteIndices = new ArrayList<Integer>();
		this.disableECANotification();

		if (this.feature.getDirection() == IncidenceDirection.OUT) {
			int i = 0;
			for (Edge e : this.gremfVertex.incidences(this.edgeClass,
					this.edgeDirection)) {
				if (!c.contains(e.getAlpha())) {
					toDelete.add(e);
					deleteIndices.add(i);
					changed = true;
				}
				i++;
			}
			for (i = toDelete.size() - 1; i >= 0; i--) {
				Edge e = toDelete.get(i);
				Vertex alpha = e.getAlpha();
				int oppIndex = this.getEOppositeIndex(e,
						(GrEMFVertexImpl) alpha);
				e.delete();
				this.notifyRemoveWithEOpp(alpha, deleteIndices.get(i), oppIndex);
			}
		} else {
			int i = 0;
			for (Edge e : this.gremfVertex.incidences(this.edgeClass,
					this.edgeDirection)) {
				if (!c.contains(e.getOmega())) {
					toDelete.add(e);
					deleteIndices.add(i);
					changed = true;
				}
				i++;
			}
			for (i = toDelete.size() - 1; i >= 0; i--) {
				Edge e = toDelete.get(i);
				Vertex omega = e.getOmega();
				int oppIndex = this.getEOppositeIndex(e,
						(GrEMFVertexImpl) omega);
				e.delete();
				this.notifyRemoveWithEOpp(omega, deleteIndices.get(i), oppIndex);
			}
		}
		this.enableECANotification();

		return changed;
	}

	@Override
	public EObject set(int index, EObject element) {
		this.disableECANotification();

		Edge e = this.getEdge(index);
		Edge newEdge = this.createEdge(element);
		this.gremfVertex.putIncidenceBefore((InternalEdge) e,
				(InternalEdge) newEdge);

		Vertex oldValue;
		Vertex newValue;
		if (this.feature.getDirection().equals(IncidenceDirection.OUT)) {
			oldValue = e.getAlpha();
			newValue = newEdge.getAlpha();
		} else {
			oldValue = e.getOmega();
			newValue = newEdge.getOmega();
		}
		int oppIndex = this.getEOppositeIndex(e, (GrEMFVertexImpl) oldValue);
		e.delete();
		if (this.gremfVertex.eNotificationRequired()) {
			this.gremfVertex.eNotify(new ENotificationImpl(this.gremfVertex,
					Notification.SET, this.feature, oldValue, newValue));
		}
		this.notifyEOppositeAdd((EObject) newValue);
		this.notifyEOppositeRemove((EObject) oldValue, oppIndex);
		this.enableECANotification();

		return (EObject) oldValue;

	}

	@Override
	public int size() {
		return this.gremfVertex.getDegree(this.edgeClass, this.edgeDirection);

		// return
		// this.gremfVertex.adjacences(this.feature.getRolename()).size();
	}

	@Override
	public List<EObject> subList(int fromIndex, int toIndex) {
		return this.createIncidenceList().subList(fromIndex, toIndex);
		// return (List<EObject>) this.gremfVertex.adjacences(
		// this.feature.getRolename()).subList(fromIndex, toIndex);
	}

	@Override
	public EObject[] toArray() {
		return this.createIncidenceList().toArray(new EObject[0]);
		// EObject[] array = this.gremfVertex.adjacences(
		// this.feature.getRolename()).toArray(new EObject[0]);
		// return array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] toArray(Object[] a) {
		return this.createIncidenceList().toArray(a);
		// return
		// this.gremfVertex.adjacences(this.feature.getRolename()).toArray(
		// a);
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.util.EList
	// --------------------------------------------------------------------------

	@Override
	public void move(int newPosition, EObject object) {
		if (!this.contains(object)) {
			throw new ArrayIndexOutOfBoundsException(-1);
		}
		this.move(newPosition, this.indexOf(object));
	}

	@Override
	public EObject move(int newPosition, int oldPosition) {
		if (this.contains((newPosition < 0) || (oldPosition < 0)
				|| (newPosition >= this.size()) || (newPosition >= this.size()))) {
			throw new ArrayIndexOutOfBoundsException(-1);
		}
		Edge old = this.getEdge(newPosition);
		Edge toBeMoved = this.getEdge(oldPosition);

		this.gremfVertex.putIncidenceBefore((InternalEdge) old,
				(InternalEdge) toBeMoved);

		if (this.gremfVertex.eNotificationRequired()) {
			if (this.feature.getDirection() == IncidenceDirection.IN) {
				this.gremfVertex.eNotify(new ENotificationImpl(
						this.gremfVertex, Notification.MOVE, this.feature,
						oldPosition, toBeMoved.getOmega(), newPosition));
			} else {
				this.gremfVertex.eNotify(new ENotificationImpl(
						this.gremfVertex, Notification.MOVE, this.feature,
						oldPosition, toBeMoved.getAlpha(), newPosition));
			}
		}

		if (this.feature.getDirection() == IncidenceDirection.IN) {
			return (EObject) toBeMoved.getOmega();
		} else {
			return (EObject) toBeMoved.getAlpha();
		}
	}

	// --------------------------------------------------------------------------
	// Additional Methods for convenience
	// to support others
	// --------------------------------------------------------------------------

	private void disableECANotification() {
		((GrEMFGraphImpl) this.gremfVertex.getGraph()).getEdgeClassCondition()
				.setCondition(false);
	}

	private void enableECANotification() {
		((GrEMFGraphImpl) this.gremfVertex.getGraph()).getEdgeClassCondition()
				.setCondition(true);
	}

	private Edge getEdge(int index) {
		int i = 0;
		for (Edge edge : this.gremfVertex.incidences(this.edgeClass,
				this.edgeDirection)) {
			if (i == index) {
				return edge;
			}
			i++;
		}
		return null;
	}

	private Edge createEdge(EObject e) {
		GrEMFIncidenceClassImpl inc = this.feature;
		if (inc.getDirection().equals(IncidenceDirection.OUT)) {
			return this.gremfVertex.getGraph().createEdge(this.edgeClass,
					(GrEMFVertexImpl) e, this.gremfVertex);
		} else {
			return this.gremfVertex.getGraph().createEdge(this.edgeClass,
					this.gremfVertex, (GrEMFVertexImpl) e);
		}
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// java.lang.Object
	// --------------------------------------------------------------------------

	@Override
	public String toString() {
		StringBuilder rep = new StringBuilder().append('[');
		if (this.feature.getDirection() == IncidenceDirection.IN) {
			for (Edge e : this.gremfVertex.incidences(
					this.feature.getEdgeClass(), EdgeDirection.OUT)) {
				rep.append(e.getOmega()).append(',').append(' ');
			}
		} else {
			for (Edge e : this.gremfVertex.incidences(
					this.feature.getEdgeClass(), EdgeDirection.IN)) {
				rep.append(e.getAlpha()).append(',').append(' ');
			}
		}
		return rep.append(']').toString();
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.util.InternalEList
	// --------------------------------------------------------------------------

	@Override
	public EObject basicGet(int index) {
		return this.get(index);
	}

	@Override
	public List<EObject> basicList() {
		return ECollections.unmodifiableEList(this);
	}

	@Override
	public Iterator<EObject> basicIterator() {
		return this.iterator();
	}

	@Override
	public ListIterator<EObject> basicListIterator() {
		return this.listIterator();
	}

	@Override
	public ListIterator<EObject> basicListIterator(int index) {
		return this.listIterator(index);
	}

	@Override
	public Object[] basicToArray() {
		return this.toArray();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] basicToArray(T[] array) {
		return (T[]) this.toArray(array);
	}

	@Override
	public int basicIndexOf(Object object) {
		return this.indexOf(object);
	}

	@Override
	public int basicLastIndexOf(Object object) {
		return this.lastIndexOf(object);
	}

	@Override
	public boolean basicContains(Object object) {
		return this.contains(object);
	}

	@Override
	public boolean basicContainsAll(Collection<?> collection) {
		return this.containsAll(collection);
	}

	@Override
	public NotificationChain basicRemove(Object object,
			NotificationChain notifications) {
		this.remove(object);
		return notifications;
	}

	@Override
	public NotificationChain basicAdd(EObject object,
			NotificationChain notifications) {
		this.add(object);
		return notifications;
	}

	@Override
	public void addUnique(EObject object) {
		this.add(object);
	}

	@Override
	public void addUnique(int index, EObject object) {
		this.add(index, object);
	}

	@Override
	public boolean addAllUnique(Collection<? extends EObject> collection) {
		return this.addAll(collection);
	}

	@Override
	public boolean addAllUnique(int index,
			Collection<? extends EObject> collection) {
		return this.addAll(index, collection);
	}

	@Override
	public EObject setUnique(int index, EObject object) {
		return this.set(index, object);
	}

}

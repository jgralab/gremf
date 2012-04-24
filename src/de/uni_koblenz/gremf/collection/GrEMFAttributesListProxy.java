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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.InternalEList;
import org.pcollections.ArrayPSet;
import org.pcollections.ArrayPVector;
import org.pcollections.PCollection;
import org.pcollections.PVector;

import de.uni_koblenz.gremf.impl.GrEMFGraphImpl;
import de.uni_koblenz.gremf.schema.GrEMFDomain;
import de.uni_koblenz.gremf.schema.impl.GrEMFAttributeImpl;
import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.schema.CollectionDomain;
import de.uni_koblenz.jgralab.schema.ListDomain;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class GrEMFAttributesListProxy<E> implements EList<E>, InternalEList<E> {

	private GrEMFAttributeImpl feature;
	private AttributedElement<?, ?> vertex;

	public GrEMFAttributesListProxy(GrEMFAttributeImpl atImpl,
			AttributedElement<?, ?> v) {
		this.feature = atImpl;
		this.vertex = v;
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// java.util.List
	// --------------------------------------------------------------------------

	@Override
	public boolean add(Object element) {
		this.disableECANotification();

		PCollection pcol = (PCollection) this.vertex.getAttribute(this.feature
				.getName());
		int size;
		if (pcol != null) {
			size = pcol.size();
		} else {
			// Instantiate because EMF can add to null
			size = 0;
			if (this.feature.getDomain() instanceof ListDomain) {
				pcol = ArrayPVector.empty();
			} else {
				pcol = ArrayPSet.empty();
			}
		}
		Object elementToAdd = this.getJGraLabAttributeValue(element);
		pcol = pcol.plus(elementToAdd);
		boolean changed = pcol.size() != size;
		this.vertex.setAttribute(this.feature.getName(), pcol);
		if (changed && ((InternalEObject) this.vertex).eNotificationRequired()) {
			Notification not = new ENotificationImpl(
					(InternalEObject) this.vertex, Notification.ADD,
					this.feature, null, elementToAdd, pcol.size() - 1);
			((InternalEObject) this.vertex).eNotify(not);
		}
		this.enableECANotification();
		return changed;
	}

	@Override
	public void add(int index, Object element) {
		PCollection<?> pcol = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));
		if (pcol.size() < index) {
			throw new IndexOutOfBoundsException();
		}
		element = this.getJGraLabAttributeValue(element);
		this.disableECANotification();
		PCollection<?> newValue = null;
		if (pcol instanceof PVector) {
			PVector vec = (PVector<?>) pcol;
			// PVector<?> e = vec.plus(index, element); // not implemented
			vec = ((PVector) pcol).subList(0, index);
			vec = vec.plus(element);
			vec = vec.plusAll(((PVector) pcol).subList(index, pcol.size()));
			newValue = vec;
		} else if (pcol instanceof ArrayPSet) {
			PVector vec = ((ArrayPSet) pcol).toPVector();
			// PVector<?> e = vec.plus(index, element);
			vec = ((PVector) pcol).subList(0, index);
			vec = vec.plus(element);
			vec = vec.plusAll(((PVector) pcol).subList(index, pcol.size()));
			newValue = ArrayPSet.empty().plusAll(vec);
		} else {
			throw new IllegalArgumentException("invalid datatype");
		}
		this.vertex.setAttribute(this.feature.getName(), newValue);

		if (((InternalEObject) this.vertex).eNotificationRequired()
				&& (newValue.size() != pcol.size())) {
			Notification not = new ENotificationImpl(
					(InternalEObject) this.vertex, Notification.ADD,
					this.feature, null, element, pcol.size());
			((InternalEObject) this.vertex).eNotify(not);
		}
		this.enableECANotification();
	}

	@Override
	public boolean addAll(Collection collection) {
		this.disableECANotification();
		PCollection<E> pcol = ((PCollection<E>) this.vertex
				.getAttribute(this.feature.getName()));
		int size;
		if (pcol != null) {
			size = pcol.size();
		} else {
			size = 0;
			if (this.feature.getDomain() instanceof ListDomain) {
				pcol = ArrayPVector.empty();
			} else {
				pcol = ArrayPSet.empty();
			}
		}
		collection = this.getCollectionJGraLabAttributeValue(collection);
		PCollection<E> pcolNew = pcol.plusAll(collection);
		this.vertex.setAttribute(this.feature.getName(), pcolNew);

		this.notifyAddAll(collection, pcolNew, pcol, size);
		this.enableECANotification();

		return size != pcolNew.size();
	}

	private void notifyAddAll(Collection listToAdd, PCollection<E> newValue,
			PCollection<E> oldValue, int index) {
		if (((InternalEObject) this.vertex).eNotificationRequired()) {
			if (((newValue.size() - oldValue.size()) > 1)) {
				Notification not = new ENotificationImpl(
						(InternalEObject) this.vertex, Notification.ADD_MANY,
						this.feature, null, listToAdd, index);
				((EObject) this.vertex).eNotify(not);

			} else if (((newValue.size() - oldValue.size()) == 1)) {
				Object el;
				if (newValue instanceof ArrayPVector) {
					el = ((ArrayPVector) newValue).get(newValue.size() - 1);
				} else {
					el = ((ArrayPSet) newValue).get(newValue.size() - 1);
				}
				Notification not = new ENotificationImpl(
						(InternalEObject) this.vertex, Notification.ADD,
						this.feature, null, el, index);
				((EObject) this.vertex).eNotify(not);
			}
		}
	}

	@Override
	public boolean addAll(int index, Collection collection) {

		PCollection oldValue = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));
		if (index > oldValue.size()) {
			throw new IndexOutOfBoundsException();
		}
		collection = this.getCollectionJGraLabAttributeValue(collection);
		this.disableECANotification();
		int size = oldValue.size();
		PCollection newValue;
		if (oldValue instanceof PVector) {
			PVector oldVector = (PVector<?>) oldValue;
			newValue = oldVector.plusAll(index, collection);
		} else if (oldValue instanceof ArrayPSet) {
			PVector oldVector = ((ArrayPSet) oldValue).toPVector();
			PVector newVector = oldVector.plusAll(index, collection);
			newValue = ArrayPSet.empty().plusAll(newVector);
		} else {
			throw new IllegalArgumentException("unexpected dataypte");
		}

		this.vertex.setAttribute(this.feature.getName(), newValue);

		this.notifyAddAll(collection, newValue, oldValue, index);
		return size != newValue.size();
	}

	@Override
	public void clear() {
		PCollection<?> pcol = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));
		this.disableECANotification();
		if (pcol instanceof PVector) {
			this.vertex.setAttribute(this.feature.getName(),
					ArrayPVector.empty());
		} else if (pcol instanceof ArrayPSet) {
			this.vertex.setAttribute(this.feature.getName(), ArrayPSet.empty());
		} else {
			throw new IllegalArgumentException("damn");
		}
		if ((pcol != null)
				&& ((InternalEObject) this.vertex).eNotificationRequired()) {
			if (pcol.size() > 1) {
				((EObject) this.vertex).eNotify(new ENotificationImpl(
						(InternalEObject) this.vertex,
						Notification.REMOVE_MANY, this.feature, pcol, null));
			} else if (pcol.size() == 1) {
				((EObject) this.vertex).eNotify(new ENotificationImpl(
						(InternalEObject) this.vertex, Notification.REMOVE,
						this.feature, pcol.iterator().next(), null));
			}
		}
		this.enableECANotification();
	}

	@Override
	public boolean contains(Object element) {
		PCollection<?> pcol = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));
		return pcol.contains(this.getJGraLabAttributeValue(element));
	}

	@Override
	public boolean containsAll(Collection collection) {
		PCollection<?> pcol = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));
		return pcol.containsAll(this
				.getCollectionJGraLabAttributeValue(collection));
	}

	@Override
	public E get(int index) {
		PCollection<?> pcol = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));
		if (pcol instanceof PVector) {
			return (E) this.getEMFAttributeValue(((PVector) pcol).get(index));
		} else if (pcol instanceof ArrayPSet) {
			return (E) this.getEMFAttributeValue(((ArrayPSet) pcol).get(index));
		} else {
			throw new IllegalArgumentException("invalid datatype : "
					+ pcol.getClass());
		}
	}

	@Override
	public int indexOf(Object element) {
		PCollection<?> pcol = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));
		if (pcol instanceof PVector) {
			return ((PVector) pcol).indexOf(this
					.getJGraLabAttributeValue(element));
		} else if (pcol instanceof ArrayPSet) {
			return ((ArrayPSet) pcol).indexOf(this
					.getJGraLabAttributeValue(element));
		} else {
			throw new IllegalArgumentException("invalid datatype");
		}
	}

	@Override
	public boolean isEmpty() {
		PCollection<?> pcol = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));
		if (pcol == null) {
			return true;
		}
		return pcol.isEmpty();
	}

	@Override
	public Iterator iterator() {
		PCollection<?> pcol = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));
		if (pcol == null) {
			if (this.feature.getDomain() instanceof ListDomain) {
				return ArrayPVector.empty().iterator();
			} else {
				return ArrayPSet.empty().iterator();
			}
		}
		return this.getCollectionEMFAttributeValue(pcol).iterator();
	}

	@Override
	public int lastIndexOf(Object element) {
		PCollection<?> pcol = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));
		if (pcol instanceof PVector) {
			return ((PVector) pcol).lastIndexOf(this
					.getJGraLabAttributeValue(element));
		} else if (pcol instanceof ArrayPSet) {
			return ((ArrayPSet) pcol).indexOf(this
					.getJGraLabAttributeValue(element));
		} else {
			throw new IllegalArgumentException("invalid datatype");
		}
	}

	@Override
	public ListIterator listIterator() {
		PCollection<?> pcol = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));
		if (pcol == null) {
			if (this.feature.getDomain() instanceof ListDomain) {
				return ArrayPVector.empty().listIterator();
			} else {
				return ArrayPSet.empty().toPVector().listIterator();
			}
		}
		if (pcol instanceof PVector) {
			return ((List) this.getCollectionEMFAttributeValue(pcol))
					.listIterator();
		} else if (pcol instanceof ArrayPSet) {
			return ((List) this
					.getCollectionEMFAttributeValue(((ArrayPSet) pcol)
							.toPVector())).listIterator();
		} else {
			throw new IllegalArgumentException("invalid datatype");
		}
	}

	@Override
	public ListIterator listIterator(int index) {
		PCollection<?> pcol = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));
		if (pcol instanceof PVector) {
			return ((List) this.getCollectionEMFAttributeValue(pcol))
					.listIterator(index);
		} else if (pcol instanceof ArrayPSet) {
			return ((List) this
					.getCollectionEMFAttributeValue(((ArrayPSet) pcol)
							.toPVector())).listIterator(index);
		} else {
			throw new IllegalArgumentException("invalid datatype");
		}
	}

	@Override
	public boolean remove(Object element) {
		PCollection<?> pcol = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));
		int size = pcol.size();
		pcol = pcol.minus(this.getJGraLabAttributeValue(element));
		this.vertex.setAttribute(this.feature.getName(), pcol);
		return size != pcol.size();
	}

	@Override
	public E remove(int index) {
		PCollection<?> pcol = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));
		if (pcol instanceof PVector) {
			E element = (E) ((PVector) pcol).get(index);
			pcol = ((PVector) pcol).minus(index);
			this.vertex.setAttribute(this.feature.getName(), pcol);
			return (E) this.getEMFAttributeValue(element);
		} else if (pcol instanceof ArrayPSet) {
			E element = (E) ((ArrayPSet) pcol).toPVector().get(index);
			pcol = ((ArrayPSet) pcol).toPVector().minus(index);
			this.vertex.setAttribute(this.feature.getName(), ArrayPSet.empty()
					.plusAll(pcol));
			return (E) this.getEMFAttributeValue(element);
		} else {
			throw new IllegalArgumentException("invalid datatype");
		}
	}

	@Override
	public boolean removeAll(Collection collection) {
		this.disableECANotification();
		PCollection pcol = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));
		int size = pcol.size();
		collection = this.getCollectionJGraLabAttributeValue(collection);
		PCollection pcolNew = pcol.minusAll(collection);
		this.vertex.setAttribute(this.feature.getName(), pcolNew);

		this.notifyRemoveAll(collection, pcolNew, pcol);

		this.enableECANotification();
		return size != pcolNew.size();
	}

	private void notifyRemoveAll(Collection listToRemove,
			PCollection<E> newValue, PCollection<E> oldValue) {
		if (((InternalEObject) this.vertex).eNotificationRequired()) {
			ArrayList<Integer> removedIndices = new ArrayList<Integer>();
			if (oldValue instanceof ArrayPVector) {
				for (int i = 0; i < oldValue.size(); i++) {
					if (!newValue.contains(((ArrayPVector) oldValue).get(i))) {
						removedIndices.add(i);
					}
				}
			} else {
				for (int i = 0; i < oldValue.size(); i++) {
					if (!newValue.contains(((ArrayPSet) oldValue).get(i))) {
						removedIndices.add(i);
					}
				}
			}
			int[] intar = this.copyToIntArray(removedIndices);

			if ((oldValue.size() - newValue.size()) > 1) {
				Notification not = new ENotificationImpl(
						(InternalEObject) this.vertex,
						Notification.REMOVE_MANY, this.feature, listToRemove,
						intar, intar[0]);
				((EObject) this.vertex).eNotify(not);
			} else if (((oldValue.size() - newValue.size()) == 1)) {
				Object el;
				if (oldValue instanceof ArrayPVector) {
					el = ((ArrayPVector) oldValue).get(intar[0]);
				} else {
					el = ((ArrayPSet) oldValue).get(intar[0]);
				}
				Notification not = new ENotificationImpl(
						(InternalEObject) this.vertex, Notification.REMOVE,
						this.feature, el, null, intar[0]);
				((EObject) this.vertex).eNotify(not);
			}
		}
	}

	@Override
	public boolean retainAll(Collection collection) {
		PCollection<?> pcol = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));
		boolean changed = false;
		collection = this.getCollectionJGraLabAttributeValue(collection);
		ArrayList<Integer> indices = new ArrayList<Integer>();
		ArrayList<Object> toDelete = new ArrayList<Object>();
		PCollection newValue;
		if (pcol instanceof ArrayPSet) {
			newValue = ArrayPSet.empty();
		} else {
			newValue = ArrayPVector.empty();
		}
		int i = 0;
		for (Object o : pcol) {
			if (!collection.contains(o)) {
				indices.add(i);
				toDelete.add(o);
				changed = true;
			} else {
				newValue = newValue.plus(o);
			}
			i++;
		}
		this.disableECANotification();
		this.vertex.setAttribute(this.feature.getName(), newValue);

		if (((InternalEObject) this.vertex).eNotificationRequired()) {
			for (i = indices.size() - 1; i >= 0; i--) {
				((EObject) this.vertex).eNotify(new ENotificationImpl(
						(InternalEObject) this.vertex, Notification.REMOVE,
						this.feature, toDelete.get(i), null, indices.get(i)));
			}
		}

		this.enableECANotification();
		return changed;
	}

	@Override
	public Object set(int index, Object element) {
		PCollection<?> pcol = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));
		this.disableECANotification();
		Object old;
		if (pcol instanceof PVector) {
			old = ((PVector) pcol).get(index);
			pcol = ((PVector) pcol).with(index,
					this.getJGraLabAttributeValue(element));
			this.vertex.setAttribute(this.feature.getName(), pcol);
		} else if (pcol instanceof ArrayPSet) {
			old = ((ArrayPSet) pcol).get(index);
			pcol = ((ArrayPSet) pcol).toPVector().with(index,
					this.getJGraLabAttributeValue(element));
			this.vertex.setAttribute(this.feature.getName(), ArrayPSet.empty()
					.plusAll(pcol));
		} else {
			throw new IllegalArgumentException("damn");
		}
		if (((InternalEObject) this.vertex).eNotificationRequired()) {
			((EObject) this.vertex).eNotify(new ENotificationImpl(
					(InternalEObject) this.vertex, Notification.SET,
					this.feature, old, element, index));
		}
		this.enableECANotification();
		return old;
	}

	@Override
	public int size() {
		PCollection<?> pcol = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));
		if (pcol == null) {
			return 0;
		}
		return pcol.size();
	}

	@Override
	public List subList(int indexStart, int indexEnd) {
		PCollection<?> pcol = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));
		if (pcol instanceof PVector) {
			return (List) this.getCollectionEMFAttributeValue(((PVector) pcol)
					.subList(indexStart, indexEnd));
		} else if (pcol instanceof ArrayPSet) {
			return (List) this
					.getCollectionEMFAttributeValue(((ArrayPSet) pcol)
							.toPVector().subList(indexStart, indexEnd));
		} else {
			throw new IllegalArgumentException("invalid datatype");
		}
	}

	@Override
	public Object[] toArray() {
		PCollection<?> pcol = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));
		if (pcol == null) {
			return new Object[0];
		}
		if (pcol instanceof PVector) {
			return ((List) this.getCollectionEMFAttributeValue(pcol)).toArray();
		} else if (pcol instanceof ArrayPSet) {
			return ((List) this.getCollectionEMFAttributeValue(pcol)).toArray();
		} else {
			throw new IllegalArgumentException("invalid datatype");
		}
	}

	@Override
	public Object[] toArray(Object[] array) {
		PCollection<?> pcol = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));
		if (pcol instanceof PVector) {
			return ((List) this.getCollectionEMFAttributeValue(pcol))
					.toArray(array);
		} else if (pcol instanceof ArrayPSet) {
			return ((List) this.getCollectionEMFAttributeValue(pcol))
					.toArray(array);
		} else {
			throw new IllegalArgumentException("invalid datatype");
		}
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.util.EList
	// --------------------------------------------------------------------------

	@Override
	public void move(int newPosition, Object object) {
		if (!this.contains(object)) {
			throw new ArrayIndexOutOfBoundsException(-1);
		}
		this.disableECANotification();
		PCollection<?> pcol = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));

		Object jgraObject = this.getJGraLabAttributeValue(object);

		int oldPosition;
		if (pcol instanceof List) {
			oldPosition = ((List) pcol).indexOf(jgraObject);
		} else {
			oldPosition = ((ArrayPSet) pcol).indexOf(jgraObject);
		}
		pcol = pcol.minus(jgraObject);

		PCollection newValue = null;
		if (pcol instanceof PVector) {
			PVector vec = (PVector<?>) pcol;
			// PVector<?> e = vec.plus(index, element); // not implemented
			vec = ((PVector) pcol).subList(0, newPosition);
			vec = vec.plus(jgraObject);
			vec = vec
					.plusAll(((PVector) pcol).subList(newPosition, pcol.size()));
			newValue = vec;
		} else if (pcol instanceof ArrayPSet) {
			PVector vec = ((ArrayPSet) pcol).toPVector();
			// PVector<?> e = vec.plus(index, element);
			vec = ((PVector) pcol).subList(0, newPosition);
			vec = vec.plus(jgraObject);
			vec = vec
					.plusAll(((PVector) pcol).subList(newPosition, pcol.size()));
			newValue = ArrayPSet.empty().plusAll(vec);
		} else {
			throw new IllegalArgumentException("invalid datatype");
		}
		this.vertex.setAttribute(this.feature.getName(), newValue);

		if (((InternalEObject) this.vertex).eNotificationRequired()) {
			((EObject) this.vertex).eNotify(new ENotificationImpl(
					(InternalEObject) this.vertex, Notification.MOVE,
					this.feature, oldPosition, object, newPosition));
		}
		this.enableECANotification();
	}

	@Override
	public E move(int newPosition, int oldPosition) {
		PCollection<?> pcol = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));
		if (pcol instanceof PVector) {
			E el = (E) ((PVector) pcol).get(oldPosition);
			this.move(newPosition, el);
			return el;
		} else if (pcol instanceof ArrayPSet) {
			E el = (E) ((ArrayPSet) pcol).get(oldPosition);
			this.move(newPosition, el);
			return el;
		} else {
			throw new IllegalArgumentException("invalid datatpye");
		}
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// java.lang.Object
	// --------------------------------------------------------------------------

	@Override
	public String toString() {
		PCollection<?> pcol = ((PCollection<?>) this.vertex
				.getAttribute(this.feature.getName()));
		if (pcol == null) {
			return "";
		} else if (pcol.isEmpty()) {
			return "[]";
		}
		return pcol.toString();

	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.util.InternalEList
	// --------------------------------------------------------------------------

	@Override
	public E basicGet(int index) {
		return this.get(index);
	}

	@Override
	public List<E> basicList() {
		return ECollections.unmodifiableEList(this);
	}

	@Override
	public Iterator<E> basicIterator() {
		return this.iterator();
	}

	@Override
	public ListIterator<E> basicListIterator() {
		return this.listIterator();
	}

	@Override
	public ListIterator<E> basicListIterator(int index) {
		return this.listIterator(index);
	}

	@Override
	public Object[] basicToArray() {
		return this.toArray();
	}

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
	public NotificationChain basicAdd(E object, NotificationChain notifications) {
		this.add(object);
		return notifications;
	}

	@Override
	public void addUnique(E object) {
		this.add(object);
	}

	@Override
	public void addUnique(int index, E object) {
		this.add(index, object);
	}

	@Override
	public boolean addAllUnique(Collection<? extends E> collection) {
		return this.addAll(collection);
	}

	@Override
	public boolean addAllUnique(int index, Collection<? extends E> collection) {
		return this.addAll(index, collection);
	}

	@Override
	public E setUnique(int index, E object) {
		return (E) this.set(index, object);
	}

	// --------------------------------------------------------------------------
	// Additional Methods for convenience
	// to support others
	// --------------------------------------------------------------------------

	private void disableECANotification() {
		this.getGraph().getVertexClassCondition().setCondition(false);
	}

	private void enableECANotification() {
		this.getGraph().getVertexClassCondition().setCondition(true);
	}

	private GrEMFGraphImpl getGraph() {
		if (this.vertex instanceof Graph) {
			return (GrEMFGraphImpl) this.vertex;
		} else {
			return (GrEMFGraphImpl) ((GraphElement<?, ?>) this.vertex)
					.getGraph();
		}
	}

	private int[] copyToIntArray(ArrayList<Integer> removedIndices) {
		int[] intar = new int[removedIndices.size()];
		for (int i = 0; i < intar.length; i++) {
			intar[i] = removedIndices.get(i);
		}
		return intar;
	}

	private Object getEMFAttributeValue(Object jgraAttValue) {
		GrEMFDomain baseDomain = ((GrEMFDomain) ((CollectionDomain) this.feature
				.getDomain()).getBaseDomain());
		return baseDomain.getEMFValue(jgraAttValue);
	}

	private Collection getCollectionEMFAttributeValue(Collection colJgraAttValue) {
		return (Collection) ((GrEMFDomain) this.feature.getDomain())
				.getEMFValue(colJgraAttValue);
	}

	private Object getJGraLabAttributeValue(Object emfAttValue) {
		GrEMFDomain baseDomain = ((GrEMFDomain) ((CollectionDomain) this.feature
				.getDomain()).getBaseDomain());
		return baseDomain.getJGraLabValue(emfAttValue);
	}

	private Collection getCollectionJGraLabAttributeValue(
			Collection colEMFAttValue) {
		return (Collection) ((GrEMFDomain) this.feature.getDomain())
				.getJGraLabValue(colEMFAttValue);
	}
}

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
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.InternalEList;

import de.uni_koblenz.gremf.GrEMFInstanceType;
import de.uni_koblenz.gremf.impl.GrEMFEdgeImpl;
import de.uni_koblenz.gremf.impl.GrEMFGraphImpl;
import de.uni_koblenz.gremf.impl.GrEMFVertexImpl;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Vertex;

public class GrEMFGraphObjectListProxy implements EList<EObject>,
		InternalEList<EObject> {

	private enum ObjectKind {
		VERTICES, EDGES
	}

	private EReference feature;
	private GrEMFGraphImpl g;
	private List<EObject> objectList;
	private ObjectKind kind;

	public GrEMFGraphObjectListProxy(GrEMFGraphImpl g, EReference f) {
		this.g = g;
		this.feature = f;
		this.kind = f.getName().equals("vertices") ? ObjectKind.VERTICES
				: ObjectKind.EDGES;
	}

	private List<EObject> getObjectList() {
		if (this.objectList == null) {
			if (this.kind == ObjectKind.VERTICES) {
				this.objectList = new ArrayList<EObject>(this.g.getVCount());
				for (Vertex v : this.g.vertices()) {
					if (v instanceof GrEMFInstanceType) {
						this.objectList.add((GrEMFVertexImpl) v);
					}
				}
			} else {
				this.objectList = new ArrayList<EObject>(this.g.getECount());
				for (Edge e : this.g.edges()) {
					if (e instanceof GrEMFInstanceType) {
						this.objectList.add((GrEMFEdgeImpl) e);
					}
				}
			}
		}
		return this.objectList;
	}

	private void resetObjectList() {
		this.objectList = null;
	}

	private GrEMFGraphImpl getGraph(EObject obj) {
		if (this.kind == ObjectKind.VERTICES) {
			return (GrEMFGraphImpl) ((GrEMFVertexImpl) obj).getGraph();
		} else if (this.kind == ObjectKind.EDGES) {
			return (GrEMFGraphImpl) ((GrEMFEdgeImpl) obj).getGraph();
		} else {
			return null;
		}
	}

	private int getId(EObject obj) {
		if (this.kind == ObjectKind.VERTICES) {
			return ((GrEMFVertexImpl) obj).getId();
		} else if (this.kind == ObjectKind.EDGES) {
			return ((GrEMFEdgeImpl) obj).getId();
		} else {
			return -1;
		}
	}

	private void addObject(EObject obj) {
		if (this.kind == ObjectKind.VERTICES) {
			this.g.addVertex((GrEMFVertexImpl) obj);
		} else {
			GrEMFEdgeImpl e = (GrEMFEdgeImpl) obj;
			this.g.addEdge(e, e.getAlpha(), e.getOmega());
		}
	}

	private void putBefore(EObject target, EObject moved) {
		if (this.kind == ObjectKind.VERTICES) {
			this.g.putVertexBefore((GrEMFVertexImpl) target,
					(GrEMFVertexImpl) moved);
		} else {
			this.g.putEdgeBeforeInGraph((GrEMFEdgeImpl) target,
					(GrEMFEdgeImpl) moved);
		}
	}

	private void putAfter(EObject target, EObject moved) {
		if (this.kind == ObjectKind.VERTICES) {
			this.g.putVertexAfter((GrEMFVertexImpl) target,
					(GrEMFVertexImpl) moved);
		} else {
			this.g.putEdgeAfterInGraph((GrEMFEdgeImpl) target,
					(GrEMFEdgeImpl) moved);
		}
	}

	private boolean containsObject(EObject obj) {
		if (this.kind == ObjectKind.VERTICES) {
			return this.g.containsVertex((GrEMFVertexImpl) obj);
		} else {
			return this.g.containsEdge((GrEMFEdgeImpl) obj);
		}
	}

	private void delete(EObject obj) {
		if (this.kind == ObjectKind.VERTICES) {
			((GrEMFVertexImpl) obj).delete();
		} else {
			((GrEMFEdgeImpl) obj).delete();
		}
	}

	private boolean handable(EObject obj) {
		if (this.kind == ObjectKind.VERTICES) {
			return obj instanceof GrEMFVertexImpl;
		} else {
			return obj instanceof GrEMFEdgeImpl;
		}
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// java.util.List
	// --------------------------------------------------------------------------

	@Override
	public boolean add(EObject obj) {
		boolean addable = this.handable(obj) && !this.contains(obj)
				&& this.getGraph(obj).equals(this.g);
		if (addable) {
			this.disableECANotification();
			this.addObject(obj);
			if (this.g.eNotificationRequired()) {
				this.g.eNotify(new ENotificationImpl(this.g, Notification.ADD,
						this.feature, null, obj, this.size() - 1));
			}
			this.enableECANotification();
		}
		this.resetObjectList();
		return addable;
	}

	@Override
	public void add(int index, EObject obj) {
		int size = this.size();
		boolean addable = this.handable(obj) && !this.contains(obj)
				&& this.getGraph(obj).equals(this.g) && (index <= size);
		if (addable) {
			this.disableECANotification();
			if (index != (size - 1)) {
				EObject objAtIndex = this.get(index);
				this.putBefore(objAtIndex, obj);
			} else {
				this.add(obj);
			}
			if (this.g.eNotificationRequired()) {
				this.g.eNotify(new ENotificationImpl(this.g, Notification.ADD,
						this.feature, null, obj, index));
			}
			this.enableECANotification();
		}
		this.resetObjectList();
		if (index > size) {
			throw new IndexOutOfBoundsException();
		}
	}

	@Override
	public boolean addAll(Collection<? extends EObject> collection) {
		boolean addable = true;
		for (EObject obj : collection) {
			if (!this.handable(obj) || this.contains(obj)
					|| !this.getGraph(obj).equals(this.g)) {
				addable = false;
				break;
			}
		}
		if (addable) {
			this.disableECANotification();
			for (EObject obj : collection) {
				this.addObject(obj);
			}

			if (this.g.eNotificationRequired()) {
				int collectionSize = collection.size();
				if (collectionSize > 1) {
					this.g.eNotify(new ENotificationImpl(this.g,
							Notification.ADD_MANY, this.feature, null,
							collection, this.size() - collectionSize));
				} else if (collectionSize == 1) {
					this.g.eNotify(new ENotificationImpl(this.g,
							Notification.ADD, this.feature, null, collection
									.iterator().next(), this.size()
									- collectionSize));
				}
			}
			this.enableECANotification();
		}
		this.resetObjectList();
		return addable;
	}

	@Override
	public boolean addAll(int index, Collection<? extends EObject> collection) {
		int size = this.size();
		boolean addable = index <= size;
		for (EObject obj : collection) {
			if (!this.handable(obj) || this.contains(obj)
					|| !this.getGraph(obj).equals(this.g)) {
				addable = false;
				break;
			}
		}
		if (addable) {
			this.disableECANotification();

			int i = index;
			for (EObject obj : collection) {
				if (i != (size - 1)) {
					EObject objAtIndex = this.get(i);
					this.putBefore(objAtIndex, obj);
				} else {
					this.add(obj);
				}
				i++;
			}
			if (this.g.eNotificationRequired()) {
				int collectionSize = collection.size();
				if (collectionSize > 1) {
					this.g.eNotify(new ENotificationImpl(this.g,
							Notification.ADD_MANY, this.feature, null,
							collection, index));
				} else if (collectionSize == 1) {
					this.g.eNotify(new ENotificationImpl(this.g,
							Notification.ADD, this.feature, null, collection
									.iterator().next(), index));
				}
			}
			this.enableECANotification();
		}
		this.resetObjectList();
		if (index > size) {
			throw new IndexOutOfBoundsException();
		}
		return addable;
	}

	@Override
	public void clear() {
		List<EObject> clear = this.getObjectList();
		this.disableECANotification();
		for (EObject obj : clear) {
			// not deleted through cascading remove
			if (this.containsObject(obj)) {
				this.remove(obj);
			}
		}
		if (this.g.eNotificationRequired()) {
			int size = clear.size();
			if (size > 1) {
				this.g.eNotify(new ENotificationImpl(this.g,
						Notification.REMOVE_MANY, this.feature, clear, null, 0));
			} else if (size == 1) {
				this.g.eNotify(new ENotificationImpl(this.g,
						Notification.REMOVE, this.feature, clear.get(0), null,
						0));
			}
		}
		this.enableECANotification();
		this.resetObjectList();
	}

	@Override
	public boolean contains(Object o) {
		return this.indexOf(o) > -1;
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		boolean contains = true;
		for (Object o : collection) {
			if (!this.contains(o)) {
				contains = false;
			}
		}
		this.resetObjectList();
		return contains;
	}

	@Override
	public EObject get(int index) {
		boolean gettable = (index > -1) && (index < this.size());
		EObject get = null;
		if (gettable) {
			get = this.getObjectList().get(index);
		}
		if (get == null) {
			throw new IndexOutOfBoundsException();
		}
		return get;
	}

	@Override
	public int indexOf(Object o) {
		int index = -1;
		if (o instanceof EObject) {
			int id = this.getId((EObject) o);
			for (int i = id - 1; i >= 0; i--) {
				try {
					Object compare = this.get(i);
					if (o.equals(compare)) {
						index = i;
						break;
					}
				} catch (IndexOutOfBoundsException e) {
				}
			}
		}
		return index;
	}

	@Override
	public int lastIndexOf(Object o) {
		return this.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return this.getObjectList().isEmpty();
	}

	@Override
	public Iterator<EObject> iterator() {
		return this.getObjectList().iterator();
	}

	@Override
	public ListIterator<EObject> listIterator() {
		return this.getObjectList().listIterator();
	}

	@Override
	public ListIterator<EObject> listIterator(int index) {
		return this.getObjectList().listIterator(index);
	}

	@Override
	public boolean remove(Object o) {
		int index = this.indexOf(o);
		boolean removable = index > -1;
		if (removable) {
			this.disableECANotification();
			EObject remove = this.get(index);
			this.delete(remove);
			Resource r = remove.eResource();
			if (r != null) {
				r.getContents().remove(remove);
			}

			if (this.g.eNotificationRequired()) {
				this.g.eNotify(new ENotificationImpl(this.g,
						Notification.REMOVE, this.feature, remove, null, index));
			}

			this.enableECANotification();
		}
		this.resetObjectList();
		return removable;
	}

	@Override
	public EObject remove(int index) {
		boolean removable = (index > -1) && (index < this.size());

		EObject remove = null;
		if (removable) {
			this.disableECANotification();
			remove = this.get(index);
			this.delete(remove);
			Resource r = remove.eResource();
			if (r != null) {
				r.getContents().remove(remove);
			}

			if (this.g.eNotificationRequired()) {
				this.g.eNotify(new ENotificationImpl(this.g,
						Notification.REMOVE, this.feature, remove, null, index));
			}

			this.enableECANotification();
		}
		this.resetObjectList();
		if (remove == null) {
			throw new IndexOutOfBoundsException();
		}
		return remove;

	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		boolean removable = true;
		int minIndex = -1;
		List<EObject> remove = new ArrayList<EObject>(collection.size());
		for (Object obj : collection) {
			int index = this.indexOf(obj);
			if (index < 0) {
				removable = false;
				break;
			} else {
				remove.add((EObject) obj);
			}
			if (minIndex > index) {
				minIndex = index;
			}
		}

		if (removable) {
			this.disableECANotification();
			for (EObject obj : remove) {
				// not deleted through cascading remove
				if (this.containsObject(obj)) {
					this.remove(obj);
				}

			}
			if (this.g.eNotificationRequired()) {

				int size = remove.size();
				if (size > 1) {
					this.g.eNotify(new ENotificationImpl(this.g,
							Notification.REMOVE_MANY, this.feature, remove,
							null, minIndex));
				} else if (size == 1) {
					this.g.eNotify(new ENotificationImpl(this.g,
							Notification.REMOVE, this.feature, remove.get(0),
							null, minIndex));
				}
			}

			this.enableECANotification();
		}

		this.resetObjectList();
		return removable;
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		boolean retainable = true;
		int minIndex = -1;
		List<EObject> remove = new ArrayList<EObject>();
		for (EObject obj : this.getObjectList()) {
			if (!collection.contains(obj)) {
				remove.add(obj);
				int index = this.indexOf(obj);
				if (minIndex > index) {
					minIndex = index;
				}
			}
		}
		retainable = !remove.isEmpty();

		if (retainable) {
			this.disableECANotification();
			for (EObject obj : remove) {
				// not deleted through cascading remove
				if (this.containsObject(obj)) {
					this.remove(obj);
				}
			}
			if (this.g.eNotificationRequired()) {

				int size = remove.size();
				if (size > 1) {
					this.g.eNotify(new ENotificationImpl(this.g,
							Notification.REMOVE_MANY, this.feature, remove,
							null, minIndex));
				} else if (size == 1) {
					this.g.eNotify(new ENotificationImpl(this.g,
							Notification.REMOVE, this.feature, remove.get(0),
							null, minIndex));
				}
			}

			this.enableECANotification();
		}

		this.resetObjectList();
		return retainable;
	}

	@Override
	public EObject set(int index, EObject obj) {
		boolean settable = this.handable(obj) && !this.contains(obj)
				&& this.getGraph(obj).equals(this.g) && (index > -1)
				&& (index < this.size());
		EObject set = null;
		if (settable) {
			this.disableECANotification();
			set = this.get(index);
			this.putAfter(set, obj);
			this.remove(index);

			if (this.g.eNotificationRequired()) {
				this.g.eNotify(new ENotificationImpl(this.g, Notification.SET,
						this.feature, set, obj, index));
			}
			this.enableECANotification();
		}

		if ((index < 0) || (index >= this.size())) {
			throw new IndexOutOfBoundsException();
		}
		return set;
	}

	@Override
	public int size() {
		return this.getObjectList().size();
	}

	@Override
	public List<EObject> subList(int fromIndex, int toIndex) {
		return this.getObjectList().subList(fromIndex, toIndex);
	}

	@Override
	public EObject[] toArray() {
		return this.getObjectList().toArray(new EObject[this.size()]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] toArray(Object[] a) {
		return this.getObjectList().toArray(a);
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// org.eclipse.emf.ecore.util.EList
	// --------------------------------------------------------------------------

	@Override
	public void move(int newPosition, EObject obj) {
		int index = this.indexOf(obj);
		if (index < 0) {
			throw new ArrayIndexOutOfBoundsException(-1);
		}
		this.move(newPosition, index);
	}

	@Override
	public EObject move(int newPosition, int oldPosition) {
		int illegalIndex = 0;
		int size = this.size();
		if ((newPosition < 0) || (newPosition >= size)) {
			illegalIndex = newPosition;
		}
		if ((oldPosition < 0) || (oldPosition >= size)) {
			illegalIndex = oldPosition;
		}
		if ((illegalIndex != 0) || ((illegalIndex == 0) && (size == 0))) {
			throw new ArrayIndexOutOfBoundsException(illegalIndex);
		}
		EObject old = this.get(newPosition);
		EObject move = this.get(oldPosition);

		this.putAfter(old, move);

		if (this.g.eNotificationRequired()) {
			this.g.eNotify(new ENotificationImpl(this.g, Notification.MOVE,
					this.feature, oldPosition, move, newPosition));
		}
		this.resetObjectList();
		return move;
	}

	// --------------------------------------------------------------------------
	// Additional Methods for convenience
	// to support others
	// --------------------------------------------------------------------------

	private void disableECANotification() {
		this.g.getVertexClassCondition().setCondition(false);
	}

	private void enableECANotification() {
		this.g.getVertexClassCondition().setCondition(true);
	}

	// --------------------------------------------------------------------------
	// Methods from interface
	// java.lang.Object
	// --------------------------------------------------------------------------

	@Override
	public String toString() {
		StringBuilder rep = new StringBuilder().append('[');
		for (EObject obj : this.getObjectList()) {
			rep.append(obj).append(',').append(' ');
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

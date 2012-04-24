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
package de.uni_koblenz.gremf.notification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.pcollections.ArrayPSet;

import de.uni_koblenz.gremf.schema.impl.GrEMFAttributeImpl;
import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.eca.Action;
import de.uni_koblenz.jgralab.eca.events.ChangeAttributeEvent;
import de.uni_koblenz.jgralab.eca.events.Event;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;

public class AttributeNotifyAction<AEC extends AttributedElementClass<AEC, ?>>
		implements Action<AEC> {

	@Override
	public void doAction(Event<AEC> event) {

		if (event instanceof ChangeAttributeEvent) {
			ChangeAttributeEvent<AEC> ev = (ChangeAttributeEvent<AEC>) event;

			AttributedElement<AEC, ?> ae = event.getElement();
			if (!((InternalEObject) ae).eNotificationRequired()) {
				return;
			}

			GrEMFAttributeImpl attribute = (GrEMFAttributeImpl) ae
					.getAttributedElementClass().getAttribute(
							ev.getAttributeName());

			if ((ev.getOldValue() == null) && (ev.getNewValue() == null)) {
				return; // no changes
			}
			if ((ev.getOldValue() != null)
					&& ev.getOldValue().equals(ev.getNewValue())) {
				// if value doesn't change, return
				return;
			}
			// LIST
			else if (ev.getNewValue() instanceof List) {
				this.notifyListChange(ev, ae, attribute);

			}
			// ARRAYPSET
			else if (ev.getNewValue() instanceof ArrayPSet) {
				this.notifySetChange(ev, ae, attribute);

			}
			// SINGLE VALUE
			else {
				this.notifySingleValueChange(ev, ae, attribute);
			}

		} else {
			throw new IllegalArgumentException();
		}

	}

	private void notifySingleValueChange(ChangeAttributeEvent<AEC> ev,
			AttributedElement<AEC, ?> ae, GrEMFAttributeImpl attribute) {
		Notification not = new ENotificationImpl((InternalEObject) ae,
				Notification.SET, attribute, ev.getOldValue(), ev.getNewValue());
		((EObject) ae).eNotify(not);
	}

	private void notifySetChange(ChangeAttributeEvent<AEC> ev,
			AttributedElement<AEC, ?> ae, GrEMFAttributeImpl attribute) {
		ArrayPSet<?> oldCol = (ArrayPSet<?>) ev.getOldValue();
		ArrayPSet<?> newCol = (ArrayPSet<?>) ev.getNewValue();

		// check what has changed, cause EMF only regards the real
		// changes

		// CLEAR
		if ((newCol == null) || newCol.isEmpty()) {
			if (oldCol.size() > 1) {
				Notification not1 = new ENotificationImpl((InternalEObject) ae,
						Notification.REMOVE_MANY, attribute, oldCol, null, 0);
				((EObject) ae).eNotify(not1);
			} else {
				Notification not1 = new ENotificationImpl((InternalEObject) ae,
						Notification.REMOVE, attribute, oldCol.get(0), null, 0);
				((EObject) ae).eNotify(not1);
			}
			return;
		}

		int i = this.compareSets(oldCol, newCol);

		// ADD
		if (i == this.ADD) {
			Notification not1 = new ENotificationImpl((InternalEObject) ae,
					Notification.ADD, attribute, null,
					((List<?>) ev.getNewValue()).get(((List<?>) ev
							.getNewValue()).size() - 1));
			((EObject) ae).eNotify(not1);
		}
		// REMOVE
		else if (i == this.REMOVE) {
			Object removed = null;
			for (Object o : oldCol) {
				if (!newCol.contains(o)) {
					removed = o;
					break;
				}
			}
			Notification not1 = new ENotificationImpl((InternalEObject) ae,
					Notification.REMOVE, attribute, removed, null);
			((EObject) ae).eNotify(not1);
		}
		// SET
		else {
			// -- REMOVE old elements
			Notification not1 = new ENotificationImpl((InternalEObject) ae,
					Notification.REMOVE_MANY, attribute, ev.getOldValue(), null);
			((EObject) ae).eNotify(not1);
			// -- ADD MANY elements
			Notification not2 = new ENotificationImpl((InternalEObject) ae,
					Notification.ADD_MANY, attribute, null, newCol, 0);
			((EObject) ae).eNotify(not2);
		}
	}

	private void notifyListChange(ChangeAttributeEvent<AEC> ev,
			AttributedElement<AEC, ?> ae, GrEMFAttributeImpl attribute) {
		List<?> oldCol = (List<?>) ev.getOldValue();
		List<?> newCol = (List<?>) ev.getNewValue();

		// check what has changed, cause EMF only regards the real
		// changes

		int i = this.compareLists(oldCol, newCol);
		// ADD one Element - simply add one element
		if (i == this.ADD) {
			Notification not1 = new ENotificationImpl((InternalEObject) ae,
					Notification.ADD, attribute, null,
					((List<?>) ev.getNewValue()).get(((List<?>) ev
							.getNewValue()).size() - 1));
			((EObject) ae).eNotify(not1);
		} else if (i == this.REMOVE) {
			Notification not1 = new ENotificationImpl((InternalEObject) ae,
					Notification.REMOVE, attribute,
					((List<?>) ev.getOldValue()).get(this.REMOVE), null,
					this.REMOVE);
			((EObject) ae).eNotify(not1);
		}
		// REMOVE + ADD MANY elements cause set was called
		else {
			@SuppressWarnings("unchecked")
			Collection<Object> oldList = (Collection<Object>) ev.getOldValue();
			if (oldList == null) {
				oldList = new ArrayList<Object>();
			}
			// REMOVE old elements
			Notification not1 = new ENotificationImpl((InternalEObject) ae,
					Notification.REMOVE_MANY, attribute, oldList, null);
			((EObject) ae).eNotify(not1);
			// ADD MANY elements
			if (newCol.size() == 1) {
				Notification not = new ENotificationImpl((InternalEObject) ae,
						Notification.ADD, attribute, null, newCol.get(0), 0);
				((EObject) ae).eNotify(not);
			} else {
				Notification not2 = new ENotificationImpl((InternalEObject) ae,
						Notification.ADD_MANY, attribute, null, newCol, 0);
				((EObject) ae).eNotify(not2);
			}
		}
	}

	private int REMOVE = 0;
	private int ADD = -1;
	private int SET = -2;

	private int compareSets(ArrayPSet<?> oldSet, ArrayPSet<?> newSet) {
		if ((newSet == null) || (oldSet == null)) {
			return this.SET;
		}
		if ((oldSet.size() + 1) == newSet.size()) { // possible ADD
			if (newSet.containsAll(oldSet)) {
				return this.ADD;
			} else {
				return this.SET;
			}
		} else if (oldSet.size() == (newSet.size() + 1)) { // possible REMOVE
			if (oldSet.containsAll(newSet)) {
				return this.REMOVE;
			} else {
				return this.SET;
			}
		} else {
			return this.SET;
		}
	}

	private int compareLists(List<?> oldList, List<?> newList) {
		if ((newList == null) || (oldList == null)) {
			return this.SET;
		}
		if ((oldList.size() + 1) == newList.size()) { // possible ADD
			for (int i = 0; i < oldList.size(); i++) {
				if (!oldList.get(i).equals(newList.get(i))) {
					return this.SET;
				}
			}
			return this.ADD;
		} else if (oldList.size() == (newList.size() + 1)) { // possible REMOVE
			int j = 0;
			int k = -5;
			boolean isSet = false;
			for (int i = 0; i < newList.size(); i++) {
				if ((newList.get(i) != oldList.get(j))) {
					if (i == j) {
						j++;
						k = i;
						if (newList.get(i) != oldList.get(j)) {
							isSet = true;
							break;
						}
					} else {
						isSet = true;
						break;
					}
				}
				j++;
			}
			if (isSet) {
				return this.SET;
			}
			if (k == -5) {
				this.REMOVE = newList.size();
				return this.REMOVE;
			} else {
				this.REMOVE = k;
				return this.REMOVE;
			}
		} else {
			return this.SET;
		}
	}
}

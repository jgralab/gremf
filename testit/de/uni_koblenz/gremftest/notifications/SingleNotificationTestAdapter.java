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
package de.uni_koblenz.gremftest.notifications;

import static org.junit.Assert.assertEquals;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EStructuralFeature;

public class SingleNotificationTestAdapter implements Adapter {

	private Notifier target;

	private int expectedEventType;
	private int expectedPostion;
	private Notifier expectedNotifier;
	private EStructuralFeature expectedFeature;
	private Object expectedOldValue;
	private Object expectedNewValue;

	public SingleNotificationTestAdapter(int evType, int pos, Notifier not,
			EStructuralFeature feat, Object oldv, Object newv) {
		this.expectedEventType = evType;
		this.expectedPostion = pos;
		this.expectedNotifier = not;
		this.expectedFeature = feat;
		this.expectedOldValue = oldv;
		this.expectedNewValue = newv;
	}

	@Override
	public void notifyChanged(Notification notification) {
		System.out.println(notification);
		assertEquals(this.expectedEventType, notification.getEventType());
		assertEquals(this.expectedPostion, notification.getPosition());
		assertEquals(this.expectedNotifier, notification.getNotifier());
		assertEquals(this.expectedFeature, notification.getFeature());
		assertEquals(this.expectedOldValue, notification.getOldValue());
		if (notification.getNewValue() instanceof int[]) {
			int[] ar = (int[]) notification.getNewValue();
			for (int i = 0; i < ar.length; i++) {
				assertEquals(((int[]) this.expectedNewValue)[i], ar[i]);
			}
		} else {
			assertEquals(this.expectedNewValue, notification.getNewValue());
		}
	}

	@Override
	public Notifier getTarget() {
		return this.target;
	}

	@Override
	public void setTarget(Notifier newTarget) {
		this.target = newTarget;
	}

	@Override
	public boolean isAdapterForType(Object type) {
		return true;
	}
}

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

public class DoubleNotificationTestAdapter implements Adapter {

	private Notifier target;

	private Notifier expectedNotifier;
	private EStructuralFeature expectedFeature;

	private int expectedEventType;
	private int expectedPostion;
	private Object expectedOldValue;
	private Object expectedNewValue;

	private int expectedEventType2;
	private int expectedPostion2;
	private Object expectedOldValue2;
	private Object expectedNewValue2;

	private boolean first = true;

	public DoubleNotificationTestAdapter(int evType, int pos, Notifier not,
			EStructuralFeature feat, Object oldv, Object newv, int evType2,
			int pos2, Object oldv2, Object newv2) {

		this.expectedNotifier = not;
		this.expectedFeature = feat;

		this.expectedEventType = evType;
		this.expectedPostion = pos;
		this.expectedOldValue = oldv;
		this.expectedNewValue = newv;

		this.expectedEventType2 = evType2;
		this.expectedPostion2 = pos2;
		this.expectedOldValue2 = oldv2;
		this.expectedNewValue2 = newv2;
	}

	@Override
	public void notifyChanged(Notification notification) {
		System.out.println(notification);

		assertEquals(this.expectedNotifier, notification.getNotifier());
		assertEquals(this.expectedFeature, notification.getFeature());

		if (this.first) {
			assertEquals(this.expectedEventType, notification.getEventType());
			assertEquals(this.expectedPostion, notification.getPosition());
			assertEquals(this.expectedOldValue, notification.getOldValue());
			if (notification.getNewValue() instanceof int[]) {
				int[] ar = (int[]) notification.getNewValue();
				for (int i = 0; i < ar.length; i++) {
					assertEquals(((int[]) this.expectedNewValue)[i], ar[i]);
				}
			} else {
				assertEquals(this.expectedNewValue, notification.getNewValue());
			}
			this.first = false;
		} else {
			assertEquals(this.expectedEventType2, notification.getEventType());
			assertEquals(this.expectedPostion2, notification.getPosition());
			assertEquals(this.expectedOldValue2, notification.getOldValue());
			if (notification.getNewValue() instanceof int[]) {
				int[] ar = (int[]) notification.getNewValue();
				for (int i = 0; i < ar.length; i++) {
					assertEquals(((int[]) this.expectedNewValue2)[i], ar[i]);
				}
			} else {
				assertEquals(this.expectedNewValue2, notification.getNewValue());
			}
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

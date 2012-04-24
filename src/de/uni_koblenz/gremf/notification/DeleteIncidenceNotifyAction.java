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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

import de.uni_koblenz.gremf.schema.impl.GrEMFIncidenceClassImpl;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.eca.Action;
import de.uni_koblenz.jgralab.eca.events.DeleteEdgeEvent;
import de.uni_koblenz.jgralab.eca.events.Event;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;

public class DeleteIncidenceNotifyAction<AEC extends AttributedElementClass<AEC, ?>>
		implements Action<AEC> {

	@Override
	public void doAction(Event<AEC> event) {
		Object edgeEvent = event;
		if (edgeEvent instanceof DeleteEdgeEvent) {
			DeleteEdgeEvent specEvent = (DeleteEdgeEvent) edgeEvent;
			Vertex alpha = specEvent.getAlpha();
			Vertex omega = specEvent.getOmega();

			if (((InternalEObject) alpha).eNotificationRequired()) {
				if (!((GrEMFIncidenceClassImpl) specEvent.getType().getTo())
						.isInvisible()) {
					Notification not1 = new ENotificationImpl(
							(InternalEObject) alpha, Notification.REMOVE,
							(EReference) specEvent.getType().getTo(), omega,
							null);
					((EObject) alpha).eNotify(not1);
				}
			}

			if (((InternalEObject) omega).eNotificationRequired()) {
				if (!((GrEMFIncidenceClassImpl) specEvent.getType().getFrom())
						.isInvisible()) {
					Notification not2 = new ENotificationImpl(
							(InternalEObject) omega, Notification.REMOVE,
							(EReference) specEvent.getType().getFrom(), alpha,
							null);
					((EObject) omega).eNotify(not2);
				}
			}

		} else {
			throw new RuntimeException("Unexpected Event, was "
					+ event.getClass() + " instead of " + DeleteEdgeEvent.class
					+ ".");
		}

	}

}

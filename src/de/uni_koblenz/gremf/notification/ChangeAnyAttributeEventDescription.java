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

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.eca.ECARule;
import de.uni_koblenz.jgralab.eca.events.ChangeAttributeEvent;
import de.uni_koblenz.jgralab.eca.events.ChangeAttributeEventDescription;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;

public class ChangeAnyAttributeEventDescription<AEC extends AttributedElementClass<AEC, ?>>
		extends ChangeAttributeEventDescription<AEC> {

	public ChangeAnyAttributeEventDescription(EventTime t) {
		super(t, "V{Vertex}", "");
	}

	@Override
	public void fire(AttributedElement<AEC, ?> element, String attributeName,
			Object oldValue, Object newValue) {
		// No tests, fire always
		int nested = this.getActiveECARules().get(0).getECARuleManager()
				.getNestedTriggerCalls();
		Graph graph = this.getActiveECARules().get(0).getECARuleManager()
				.getGraph();
		for (ECARule<AEC> rule : this.activeRules) {
			rule.trigger(new ChangeAttributeEvent<AEC>(nested, this.getTime(),
					graph, element, attributeName, oldValue, newValue));
		}

	}

}

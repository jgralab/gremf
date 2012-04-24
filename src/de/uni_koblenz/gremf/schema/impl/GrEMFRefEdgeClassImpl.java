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
package de.uni_koblenz.gremf.schema.impl;

import de.uni_koblenz.jgralab.schema.AggregationKind;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.IncidenceDirection;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.schema.impl.EdgeClassImpl;
import de.uni_koblenz.jgralab.schema.impl.GraphClassImpl;
import de.uni_koblenz.jgralab.schema.impl.PackageImpl;

public class GrEMFRefEdgeClassImpl extends EdgeClassImpl {

	protected GrEMFRefEdgeClassImpl(String simpleName, Package pkg,
			GraphClass aGraphClass, VertexClass from, int fromMin, int fromMax,
			String fromRoleName, AggregationKind aggrFrom, VertexClass to,
			int toMin, int toMax, String toRoleName, AggregationKind aggrTo) {
		super(simpleName, (PackageImpl) pkg, (GraphClassImpl) aGraphClass,
				from, fromMin, fromMax, fromRoleName, aggrFrom, to, toMin,
				toMax, toRoleName, aggrTo);
	}

	@Override
	protected IncidenceClass createIncidenceClass(VertexClass vrtxCls,
			String rolename, int min, int max, IncidenceDirection dir,
			AggregationKind aggr) {
		return new GrEMFIncidenceClassImpl(this, vrtxCls, rolename, min, max,
				dir, aggr);
	}

}

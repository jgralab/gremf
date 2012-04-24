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
package de.uni_koblenz.gremfeval.code.featurehandling;

import org.pcollections.PSequence;
import org.pcollections.PVector;

import de.uni_koblenz.gremfeval.code.find.JGraLabFindEval;
import de.uni_koblenz.gremfeval.code.load.JGraLabLoadEval;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;

public class JGraLabFeatureHandlingEval implements Runnable {

	private final boolean withGrEMF;

	public JGraLabFeatureHandlingEval(boolean withGrEMF) {
		this.withGrEMF = withGrEMF;
	}

	@Override
	public void run() {
		PVector<PSequence<Vertex>> vertices = new JGraLabFindEval(
				new JGraLabLoadEval(this.withGrEMF).call(), false).call();
		Vertex vertexA = vertices.get(0).get(0);
		Graph g = vertexA.getGraph();
		Schema schema = g.getSchema();

		VertexClass staticCls = schema
				.getAttributedElementClass("modifiers.Static");
		EdgeClass modifiableCls = schema
				.getAttributedElementClass("modifiers.AnnotableAndModifiableContainsAnnotationsAndModifiers");

		// attribute
		String name = vertexA.getAttribute("name");
		vertexA.setAttribute("name", name + "_test");

		// incidence / adjacence
		Vertex vStatic = g.createVertex(staticCls);
		vertexA.addAdjacence("annotationsAndModifiers", vStatic);

		boolean contains = false;
		for (Edge e : vertexA.incidences(modifiableCls)) {
			if (e.getOmega().equals(vStatic)) {
				contains = true;
			}
		}
		if (contains) {
			vStatic.delete();
		}

		// list attribute
		PVector<String> comments = vertexA
				.adjacences("annotationsAndModifiers").get(0)
				.getAttribute("comments");

		if (!comments.isEmpty()) {
			comments = comments.minusAll(comments);
		}
		comments.plus("/**\n* this is a new comment\n*/")
				.plus("/**\n* and this is another\n*/")
				.plus("/**\n* and yet another\n*/");

	}
}

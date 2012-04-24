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
package de.uni_koblenz.gremfeval.code.find;

import java.util.HashMap;
import java.util.concurrent.Callable;

import org.pcollections.PSequence;
import org.pcollections.PVector;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;

public class JGraLabFindEval implements Callable<PVector<PSequence<Vertex>>> {

	private final Graph g;
	private final boolean useOptimization;

	public JGraLabFindEval(Graph g, boolean useOptimization) {
		this.g = g;
		this.useOptimization = useOptimization;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PVector<PSequence<Vertex>> call() {
		GreqlEvaluator evaluator = new GreqlEvaluator("from "
				+ "i:V{classifiers.Interface} " + "with "
				+ "isDefined(i.name) " + "and "
				+ "startsWith(\"GrEMF\",i.name) " + "reportList " + "i, "
				+ "i(<>--{extends}<>--{classifierReferences}-->{target})* "
				+ "end", this.g, new HashMap<String, Object>());
		evaluator.setUseSavedOptimizedSyntaxGraph(this.useOptimization);
		evaluator.startEvaluation();
		return (PVector<PSequence<Vertex>>) evaluator.getResult();
	}
}

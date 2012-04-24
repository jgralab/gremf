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
package de.uni_koblenz.gremfeval.code.load;

import java.io.File;
import java.util.concurrent.Callable;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import de.uni_koblenz.gremf.resource.GrEMFResourceFactoryImpl;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.schema.Schema;

public class JGraLabLoadEval implements Callable<Graph> {

	private final boolean withGrEMF;

	public JGraLabLoadEval(boolean withGrEMF) {
		this.withGrEMF = withGrEMF;
	}

	@Override
	public Graph call() {
		if (this.withGrEMF) {

			ResourceSet rs = new ResourceSetImpl();

			rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
					.put("*", new GrEMFResourceFactoryImpl());

			return (Graph) rs
					.getResource(
							URI.createURI("models" + File.separator
									+ "model1_transformedwithecore2tg.tg"),
							true).getContents().get(0);
		} else {
			try {
				Schema schema = GraphIO.loadSchemaFromFile("models"
						+ File.separator + "model1_transformedwithecore2tg.tg");
				return GraphIO.loadGraphFromFile("models" + File.separator
						+ "model1_transformedwithecore2tg.tg", schema,
						ImplementationType.GENERIC, null);
			} catch (GraphIOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}

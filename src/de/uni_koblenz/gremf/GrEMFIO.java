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
package de.uni_koblenz.gremf;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import de.uni_koblenz.gremf.exception.SchemaLoadException;
import de.uni_koblenz.gremf.schema.impl.GrEMFGraphClassImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFIncidenceClassWithRefsImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFSchemaImpl;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.schema.Constraint;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;

/**
 * class for grEMF compatible loading and storing of schema and graphs (tg
 * format)
 */
public class GrEMFIO extends GraphIO {

	public static Schema loadSchemaFromFile(String filename)
			throws GraphIOException {
		InputStream in = null;
		try {
			if (filename.toLowerCase().endsWith(".gz")) {
				in = new GZIPInputStream(new FileInputStream(filename),
						BUFFER_SIZE);
			} else {
				in = new BufferedInputStream(new FileInputStream(filename),
						BUFFER_SIZE);
			}
			return loadSchemaFromStream(in);

		} catch (IOException ex) {
			throw new GraphIOException("Exception while loading schema from "
					+ filename, ex);
		} finally {
			if (in != null) {
				close(in);
			}
		}
	}

	public static Schema loadSchemaFromStream(InputStream in)
			throws GraphIOException {
		try {
			GrEMFIO io = new GrEMFIO();
			io.TGIn = in;
			io.tgfile();
			io.schema.finish();
			return io.schema;
		} catch (Exception e) {
			throw new GraphIOException("Exception while loading schema", e);
		}
	}

	protected GrEMFIO() {
		super();
	}

	private boolean hasSubclass(GraphElementClassData ecd) {
		String qualName = ecd.getQualifiedName();
		for (Entry<String, List<GraphElementClassData>> gcElements : this.edgeClassBuffer
				.entrySet()) {
			for (GraphElementClassData eData : gcElements.getValue()) {
				for (String supName : eData.directSuperClasses) {
					if (supName.equals(qualName)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	protected EdgeClass createEdgeClass(GraphElementClassData ecd, GraphClass gc)
			throws GraphIOException, SchemaException {

		EdgeClass ec;
		if (((ecd.attributes == null) || ecd.attributes.isEmpty())
				&& (!this.hasSubclass(ecd))
				&& (!ecd.isAbstract)
				&& ((ecd.directSuperClasses == null) || ecd.directSuperClasses
						.isEmpty())) {

			GrEMFGraphClassImpl grgc = (GrEMFGraphClassImpl) gc;

			ec = grgc.createRefEdgeClass(ecd.getQualifiedName(),
					gc.getVertexClass(ecd.fromVertexClassName),
					ecd.fromMultiplicity[0], ecd.fromMultiplicity[1],
					ecd.fromRoleName, ecd.fromAggregation,
					gc.getVertexClass(ecd.toVertexClassName),
					ecd.toMultiplicity[0], ecd.toMultiplicity[1],
					ecd.toRoleName, ecd.toAggregation);

		} else {
			ec = gc.createEdgeClass(ecd.getQualifiedName(),
					gc.getVertexClass(ecd.fromVertexClassName),
					ecd.fromMultiplicity[0], ecd.fromMultiplicity[1],
					ecd.fromRoleName, ecd.fromAggregation,
					gc.getVertexClass(ecd.toVertexClassName),
					ecd.toMultiplicity[0], ecd.toMultiplicity[1],
					ecd.toRoleName, ecd.toAggregation);

			GrEMFIncidenceClassWithRefsImpl incFrom = (GrEMFIncidenceClassWithRefsImpl) ec
					.getFrom();
			GrEMFIncidenceClassWithRefsImpl incTo = (GrEMFIncidenceClassWithRefsImpl) ec
					.getTo();
			if (((incTo.getRolename() == null) || incTo.getRolename()
					.equals(""))
					&& ((ecd.directSuperClasses == null) || ecd.directSuperClasses
							.isEmpty())) {
				incTo.setInvisibile(true);
				incFrom.clearToEdgeClass();
			} else {
				incFrom.getToEdgeClass().setName("grEMF_" + incTo.getName());
				incFrom.getToEdgeClass().setLowerBound(incTo.getLowerBound());
				incFrom.getToEdgeClass().setUpperBound(incTo.getUpperBound());
			}
			if (((incFrom.getRolename() == null) || incFrom.getRolename()
					.equals(""))
					&& ((ecd.directSuperClasses == null) || ecd.directSuperClasses
							.isEmpty())) {
				incFrom.setInvisibile(true);
				incTo.clearToEdgeClass();
			} else {
				incTo.getToEdgeClass().setName("grEMF_" + incFrom.getName());
				incTo.getToEdgeClass().setLowerBound(incFrom.getLowerBound());
				incTo.getToEdgeClass().setUpperBound(incFrom.getUpperBound());
			}
			if (incFrom.isInvisible() && incTo.isInvisible()) {
				throw new SchemaLoadException(
						"No EdgeClass without rolenames allowed. " + ec + " .");
			}

			this.addAttributes(ecd.attributes, ec);
		}
		for (Constraint constraint : ecd.constraints) {
			ec.addConstraint(constraint);
		}

		ec.setAbstract(ecd.isAbstract);

		this.GECsearch.put(ec, gc);
		return ec;
	}

	@Override
	protected Schema createSchema(String name, String prefix) {
		return new GrEMFSchemaImpl(name, prefix);
	}

}

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
package de.uni_koblenz.gremf.impl;

import de.uni_koblenz.gremf.GrEMFInstanceType;
import de.uni_koblenz.gremf.GrEMFType;
import de.uni_koblenz.gremf.notification.AddIncidenceNotifyAction;
import de.uni_koblenz.gremf.notification.AttributeNotifyAction;
import de.uni_koblenz.gremf.notification.ChangeAnyAttributeEventDescription;
import de.uni_koblenz.gremf.notification.CreateAnyEdgeEventDescription;
import de.uni_koblenz.gremf.notification.DeleteAnyEdgeEventDescription;
import de.uni_koblenz.gremf.notification.DeleteIncidenceNotifyAction;
import de.uni_koblenz.gremf.notification.PreventNotifyCondition;
import de.uni_koblenz.gremf.schema.impl.GrEMFRefEdgeClassImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFSchemaImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFVertexClassImpl;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.eca.Action;
import de.uni_koblenz.jgralab.eca.ECARule;
import de.uni_koblenz.jgralab.eca.ECARuleManager;
import de.uni_koblenz.jgralab.eca.events.EventDescription;
import de.uni_koblenz.jgralab.eca.events.EventDescription.EventTime;
import de.uni_koblenz.jgralab.impl.InternalGraph;
import de.uni_koblenz.jgralab.impl.generic.GenericGraphFactoryImpl;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;

public class GrEMFGraphFactoryImpl extends GenericGraphFactoryImpl implements
		GrEMFInstanceType {

	public GrEMFGraphFactoryImpl(Schema s) {
		super(s);
	}

	@Override
	public <G extends Graph> G createGraph(GraphClass gc, String id, int vMax,
			int eMax) {
		assert this.schema == gc.getSchema();
		@SuppressWarnings("unchecked")
		G graph = (G) new GrEMFGraphImpl(gc, id, vMax, eMax);
		graph.setGraphFactory(this);

		EventDescription<VertexClass> ev = new ChangeAnyAttributeEventDescription<VertexClass>(
				EventTime.AFTER);
		Action<VertexClass> ac = new AttributeNotifyAction<VertexClass>();
		PreventNotifyCondition<VertexClass> cond = new PreventNotifyCondition<VertexClass>();
		ECARule<VertexClass> rule = new ECARule<VertexClass>(ev, cond, ac);
		((ECARuleManager) graph.getECARuleManager()).addECARule(rule);

		EventDescription<EdgeClass> delEdE = new DeleteAnyEdgeEventDescription(
				EventTime.AFTER);
		PreventNotifyCondition<EdgeClass> delEdEcond = new PreventNotifyCondition<EdgeClass>();
		Action<EdgeClass> delEdA = new DeleteIncidenceNotifyAction<EdgeClass>();
		ECARule<EdgeClass> delEdRule = new ECARule<EdgeClass>(delEdE,
				delEdEcond, delEdA);
		((ECARuleManager) graph.getECARuleManager()).addECARule(delEdRule);

		EventDescription<EdgeClass> creEdE = new CreateAnyEdgeEventDescription(
				EventTime.AFTER);
		Action<EdgeClass> creEdA = new AddIncidenceNotifyAction<EdgeClass>();
		ECARule<EdgeClass> creEdRule = new ECARule<EdgeClass>(creEdE,
				delEdEcond, creEdA);
		((ECARuleManager) graph.getECARuleManager()).addECARule(creEdRule);

		((GrEMFGraphImpl) graph).setVertexClassCondition(cond);
		((GrEMFGraphImpl) graph).setEdgeClassCondition(delEdEcond);

		((GrEMFSchemaImpl) this.schema).setCurrentGraph(graph);

		return graph;
	}

	@Override
	public <V extends Vertex> V createVertex(VertexClass vc, int id, Graph g) {
		assert this.schema == vc.getSchema();
		if (!((InternalGraph) g).isLoading() && (g.hasECARuleManager())) {
			g.getECARuleManager().fireBeforeCreateVertexEvents(vc);
		}
		@SuppressWarnings("unchecked")
		V vertex = (V) new GrEMFVertexImpl((GrEMFVertexClassImpl) vc, id, g);
		if (!((InternalGraph) g).isLoading() && g.hasECARuleManager()) {
			g.getECARuleManager().fireAfterCreateVertexEvents(vertex);
		}
		return vertex;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E extends Edge> E createEdge(EdgeClass ec, int id, Graph g,
			Vertex alpha, Vertex omega) {
		assert this.schema == ec.getSchema();
		if (!((InternalGraph) g).isLoading() && (g.hasECARuleManager())) {
			g.getECARuleManager().fireBeforeCreateEdgeEvents(ec);
		}
		E edge;
		if (ec instanceof GrEMFType) {
			edge = (E) new GrEMFEdgeImpl(ec, id, g, alpha, omega);
		} else if (ec instanceof GrEMFRefEdgeClassImpl) {
			edge = (E) new GrEMFRefEdgeImpl(ec, id, g, alpha, omega);
		} else {
			edge = (E) super.createEdge(ec, id, g, alpha, omega);
		}
		if (!((InternalGraph) g).isLoading() && g.hasECARuleManager()) {
			g.getECARuleManager().fireAfterCreateEdgeEvents(edge);
		}

		return edge;
	}
}

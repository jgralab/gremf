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
package de.uni_koblenz.gremfeval.tools.gretl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.uni_koblenz.gremf.GrEMFIO;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.gretl.Context;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;

public class GReTLTest {

	private String targetFileName;
	private Context context;

	private Schema sourceSchema;
	private VertexClass familyVC;
	private VertexClass memberVC;
	private EdgeClass hasFatherEC;
	private EdgeClass hasMotherEC;
	private EdgeClass hasDaughterEC;
	private EdgeClass hasSonEC;

	private Graph sourceFamilyGraph;

	private Schema targetSchema;

	@Before
	public void setUp() {
		try {
			this.sourceSchema = GrEMFIO.loadSchemaFromFile(System
					.getProperty("user.dir")
					+ File.separator
					+ "models"
					+ File.separator + "familyschema.tg");
			this.familyVC = this.sourceSchema.getGraphClass().getVertexClass(
					"Family");
			this.memberVC = this.sourceSchema.getGraphClass().getVertexClass(
					"Member");
			this.hasFatherEC = this.sourceSchema.getGraphClass().getEdgeClass(
					"HasFather");
			this.hasMotherEC = this.sourceSchema.getGraphClass().getEdgeClass(
					"HasMother");
			this.hasDaughterEC = this.sourceSchema.getGraphClass()
					.getEdgeClass("HasDaughter");
			this.hasSonEC = this.sourceSchema.getGraphClass().getEdgeClass(
					"HasSon");

			this.initFamilyGraph();

			this.targetSchema = GrEMFIO.loadSchemaFromFile(System
					.getProperty("user.dir")
					+ File.separator
					+ "models"
					+ File.separator + "genealogy.tg");
		} catch (GraphIOException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void familyGraph2Genealogy() throws Exception {
		this.targetFileName = "familyGraph2Genealogy";

		System.out.println(">>> " + this.targetFileName);

		this.context = new Context(this.targetSchema);

		this.context.setSourceGraph(this.sourceFamilyGraph);
		Graph tg = new FamilyGraph2Genealogy(this.context).execute();
		assertNotNull(tg);
		assertEquals(21, tg.getECount());
		assertEquals(13, tg.getVCount());
		tg.save(System.getProperty("user.dir") + File.separator + "models"
				+ File.separator + "generated/genealogy.tg");
		this.context.printImgMappings();
	}

	private void initFamilyGraph() {
		Graph g = this.sourceSchema.createGraph(ImplementationType.GENERIC);

		// Family Smith (2 sons, 1 daughter)
		Vertex smith = g.createVertex(this.familyVC);
		smith.setAttribute("lastName", "Smith");
		smith.setAttribute("street", "Smith Avenue 4");
		smith.setAttribute("town", "Smithtown");

		Vertex steve = g.createVertex(this.memberVC);
		steve.setAttribute("firstName", "Steve");
		g.createEdge(this.hasFatherEC, smith, steve);
		steve.setAttribute("age", 66);

		Vertex stephanie = g.createVertex(this.memberVC);
		stephanie.setAttribute("firstName", "Stephanie");
		g.createEdge(this.hasMotherEC, smith, stephanie);
		stephanie.setAttribute("age", 66);

		Vertex stu = g.createVertex(this.memberVC);
		stu.setAttribute("firstName", "Stu");
		g.createEdge(this.hasSonEC, smith, stu);
		stu.setAttribute("age", 27);

		Vertex sven = g.createVertex(this.memberVC);
		sven.setAttribute("firstName", "Sven");
		g.createEdge(this.hasSonEC, smith, sven);
		sven.setAttribute("age", 31);

		Vertex stella = g.createVertex(this.memberVC);
		stella.setAttribute("firstName", "Stella");
		g.createEdge(this.hasDaughterEC, smith, stella);
		stella.setAttribute("age", 29);

		// Family Carter (3 daughters)
		Vertex carter = g.createVertex(this.familyVC);
		carter.setAttribute("lastName", "Carter");
		carter.setAttribute("street", "Carter Street 2");
		carter.setAttribute("town", "Cartertown");

		Vertex chris = g.createVertex(this.memberVC);
		chris.setAttribute("firstName", "Chris");
		g.createEdge(this.hasFatherEC, carter, chris);
		chris.setAttribute("age", 51);

		Vertex christy = g.createVertex(this.memberVC);
		christy.setAttribute("firstName", "Christy");
		g.createEdge(this.hasMotherEC, carter, christy);
		christy.setAttribute("age", 49);

		Vertex carol = g.createVertex(this.memberVC);
		carol.setAttribute("firstName", "Carol");
		g.createEdge(this.hasDaughterEC, carter, carol);
		carol.setAttribute("age", 25);

		Vertex conzuela = g.createVertex(this.memberVC);
		conzuela.setAttribute("firstName", "Conzuela");
		g.createEdge(this.hasDaughterEC, carter, conzuela);
		conzuela.setAttribute("age", 17);

		// Family Smith number 2 (1 daughter, 1 son)
		Vertex smith2 = g.createVertex(this.familyVC);
		smith2.setAttribute("lastName", "Smith");
		smith2.setAttribute("street", "Smithway 17");
		smith2.setAttribute("town", "Smithtown");

		Vertex dennis = g.createVertex(this.memberVC);
		dennis.setAttribute("firstName", "Dennis");
		g.createEdge(this.hasFatherEC, smith2, dennis);
		// Dennis Smith is a son of the Smith 1 Family
		g.createEdge(this.hasSonEC, smith, dennis);
		dennis.setAttribute("age", 37);

		Vertex debby = g.createVertex(this.memberVC);
		debby.setAttribute("firstName", "Debby");
		g.createEdge(this.hasMotherEC, smith2, debby);
		// Debby Doe is a daughter of the Carter Family
		g.createEdge(this.hasDaughterEC, carter, debby);
		debby.setAttribute("age", 33);

		Vertex diana = g.createVertex(this.memberVC);
		diana.setAttribute("firstName", "Diana");
		g.createEdge(this.hasDaughterEC, smith2, diana);
		diana.setAttribute("age", 9);

		Vertex doug = g.createVertex(this.memberVC);
		doug.setAttribute("firstName", "Doug");
		g.createEdge(this.hasSonEC, smith2, doug);
		doug.setAttribute("age", 12);

		this.sourceFamilyGraph = g;
	}
}

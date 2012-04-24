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
package de.uni_koblenz.gremftest.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.pcollections.ArrayPMap;
import org.pcollections.PMap;

import de.uni_koblenz.gremf.GrEMFIO;
import de.uni_koblenz.gremf.impl.GrEMFGraphImpl;
import de.uni_koblenz.gremf.impl.GrEMFVertexImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFAttributeImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFEnumDomainImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFGraphClassImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFSchemaImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFVertexClassImpl;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.schema.EnumDomain;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.RecordDomain;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.schema.impl.SchemaImpl;

public class GrEMFDefaultValueTest {

	private final static String path = System.getProperty("user.dir")
			+ File.separator + "models" + File.separator
			+ "DefaultValueTestSchema.tg";

	public static void main(String[] args) throws GraphIOException {
		Schema schema = new SchemaImpl("DefaultValueTestSchema",
				"de.uni_koblenz.gremftest.schemas");
		GraphClass gc = schema.createGraphClass("DefaultValueTestGraph");
		gc.createAttribute("location", schema.getStringDomain(), "\"Nowhere\"");
		gc.createAttribute("numberOfPeople", schema.getIntegerDomain(), "100");
		gc.createAttribute("price", schema.getDoubleDomain(), "11.11");
		gc.createAttribute("isThere", schema.getBooleanDomain(), "t");
		gc.createAttribute("id", schema.getLongDomain(), "123456789");

		VertexClass vc = gc.createVertexClass("Bill");

		EnumDomain ed = schema.createEnumDomain("Category");
		ed.addConst("FUN");
		ed.addConst("BUSINESS");
		ed.addConst("FAMILY");
		vc.createAttribute("category", ed, "BUSINESS");

		RecordDomain rd = schema.createRecordDomain("MetaData");
		rd.addComponent("author", schema.getStringDomain());
		rd.addComponent("day", schema.getIntegerDomain());
		rd.addComponent("month", schema.getIntegerDomain());
		rd.addComponent("year", schema.getIntegerDomain());

		vc.createAttribute("metaData", rd, "( \"Hugo Harry\" 1 1 2012 )");

		RecordDomain item = schema.createRecordDomain("Item");
		item.addComponent("articleNumber", schema.getLongDomain());
		item.addComponent("count", schema.getIntegerDomain());
		item.addComponent("description", schema.getStringDomain());
		item.addComponent("price", schema.getDoubleDomain());

		vc.createAttribute("items", schema.createListDomain(item),
				"[ ( 333 5 \"Bottle of Water\" 1.99 ) ( 454 2 \"Chewing gum\" 0.99 ) ]");

		RecordDomain address = schema.createRecordDomain("Address");
		address.addComponent("number", schema.getIntegerDomain());
		address.addComponent("plz", schema.getLongDomain());
		address.addComponent("street", schema.getStringDomain());
		address.addComponent("town", schema.getStringDomain());

		RecordDomain pird = schema.createRecordDomain("PersonInfo");
		pird.addComponent("address", address);
		pird.addComponent("name", schema.getStringDomain());
		pird.addComponent("tasks",
				schema.createListDomain(schema.getStringDomain()));

		vc.createAttribute(
				"recipient",
				pird,
				"( ( 3 56479 \"Fakenhahner Weg\" \"Seck\" ) \"Cornelia Heckelmann\" [ \"read\" \"redirect\" ] )");

		schema.finish();
		schema.save(path);
		GraphIO.loadSchemaFromFile(path);

		Graph g = schema.createGraph(ImplementationType.GENERIC);
		g.createVertex(vc);

	}

	private GrEMFSchemaImpl schema;
	private GrEMFVertexClassImpl vertexClass;
	private GrEMFGraphClassImpl graphClass;
	private GrEMFAttributeImpl gc_location;
	private GrEMFAttributeImpl gc_numberOfPeople;
	private GrEMFAttributeImpl gc_price;
	private GrEMFAttributeImpl gc_isThere;
	private GrEMFAttributeImpl gc_id;
	private GrEMFAttributeImpl vc_category;
	private GrEMFAttributeImpl vc_metaData;
	private GrEMFAttributeImpl vc_items;
	private GrEMFAttributeImpl vc_recipient;

	private GrEMFGraphImpl graph;
	private GrEMFVertexImpl vertex;

	@Before
	public void setUp() throws GraphIOException {
		this.schema = (GrEMFSchemaImpl) GrEMFIO.loadSchemaFromFile(path);
		this.graphClass = (GrEMFGraphClassImpl) this.schema.getGraphClass();
		this.vertexClass = (GrEMFVertexClassImpl) this.graphClass
				.getVertexClass("Bill");
		this.gc_location = (GrEMFAttributeImpl) this.graphClass
				.getAttribute("location");
		this.gc_numberOfPeople = (GrEMFAttributeImpl) this.graphClass
				.getAttribute("numberOfPeople");
		this.gc_price = (GrEMFAttributeImpl) this.graphClass
				.getAttribute("price");
		this.gc_isThere = (GrEMFAttributeImpl) this.graphClass
				.getAttribute("isThere");
		this.gc_id = (GrEMFAttributeImpl) this.graphClass.getAttribute("id");

		this.vc_category = (GrEMFAttributeImpl) this.vertexClass
				.getAttribute("category");
		this.vc_metaData = (GrEMFAttributeImpl) this.vertexClass
				.getAttribute("metaData");
		this.vc_items = (GrEMFAttributeImpl) this.vertexClass
				.getAttribute("items");
		this.vc_recipient = (GrEMFAttributeImpl) this.vertexClass
				.getAttribute("recipient");

		this.graph = (GrEMFGraphImpl) this.schema
				.createGraph(ImplementationType.GENERIC);
		this.vertex = this.graph.createVertex(this.vertexClass);

	}

	@Test
	public void testStringDefaultValue() {
		assertEquals("Nowhere", this.gc_location.getDefaultValue());
		assertEquals("Nowhere", this.graph.eGet(this.gc_location));
	}

	@Test
	public void testIntegerDefaultValue() {
		assertEquals(100, this.gc_numberOfPeople.getDefaultValue());
		assertEquals(100, this.graph.eGet(this.gc_numberOfPeople));
	}

	@Test
	public void testLongDefaultValue() {
		assertEquals(123456789L, this.gc_id.getDefaultValue());
		assertEquals(123456789L, this.graph.eGet(this.gc_id));
	}

	@Test
	public void testDoubleDefaultValue() {
		assertEquals(11.11, this.gc_price.getDefaultValue());
		assertEquals(11.11, this.graph.eGet(this.gc_price));
	}

	@Test
	public void testBooleanDefaultValue() {
		assertEquals(true, this.gc_isThere.getDefaultValue());
		assertEquals(true, this.graph.eGet(this.gc_isThere));
	}

	@Test
	public void testEnumDefaultValue() {
		GrEMFEnumDomainImpl ed = (GrEMFEnumDomainImpl) this.vc_category
				.getDomain();
		assertEquals(ed.getEEnumLiteral("BUSINESS"),
				this.vc_category.getDefaultValue());
		assertEquals(ed.getEEnumLiteral("BUSINESS"),
				this.vertex.eGet(this.vc_category));
	}

	@Test
	public void testRecordDefaultValue() {
		PMap<String, Object> values = ArrayPMap.empty();
		values = values.plus("author", "Hugo Harry");
		values = values.plus("day", 1);
		values = values.plus("month", 1);
		values = values.plus("year", 2012);

		assertEquals(values, this.vc_metaData.getDefaultValue());
		assertEquals(values, this.vertex.eGet(this.vc_metaData));
	}

	@Test
	public void testListOfRecordDefaultValue() {
		// "[ ( 333 5 \"Bottle of Water\" 1.99 ) ( 454 2 \"Chewing gum\" 0.99 ) ]");
		ArrayList<Object> list = new ArrayList<Object>(2);
		PMap<String, Object> map1 = ArrayPMap.empty();
		map1 = map1.plus("articleNumber", 333L);
		map1 = map1.plus("count", 5);
		map1 = map1.plus("description", "Bottle of Water");
		map1 = map1.plus("price", 1.99);
		list.add(map1);

		PMap<String, Object> map2 = ArrayPMap.empty();
		map2 = map2.plus("articleNumber", 454L);
		map2 = map2.plus("count", 2);
		map2 = map2.plus("description", "Chewing gum");
		map2 = map2.plus("price", 0.99);
		list.add(map2);

		assertEquals(list, this.vc_items.getDefaultValue());
		assertEquals(list, this.vertex.eGet(this.vc_items));
	}

	@Test
	public void testRecordInRecordDefaultValue() {
		// "( ( 3 56479 \"Fakenhahner Weg\" \"Seck\" ) \"Cornelia Heckelmann\" [ \"read\" \"redirect\" ] )"
		PMap<String, Object> outermap = ArrayPMap.empty();

		PMap<String, Object> innermap = ArrayPMap.empty();
		innermap = innermap.plus("number", 3);
		innermap = innermap.plus("plz", 56479L);
		innermap = innermap.plus("town", "Seck");
		innermap = innermap.plus("street", "Fakenhahner Weg");

		outermap = outermap.plus("address", innermap);
		outermap = outermap.plus("name", "Cornelia Heckelmann");

		ArrayList<String> innerList = new ArrayList<String>(2);
		innerList.add("read");
		innerList.add("redirect");

		outermap = outermap.plus("tasks", innerList);

		assertEquals(outermap, this.vc_recipient.getDefaultValue());
		assertEquals(outermap, this.vertex.eGet(this.vc_recipient));

	}
}

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EcorePackage;

import de.uni_koblenz.gremf.impl.GrEMFGraphFactoryImpl;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.schema.BooleanDomain;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.DoubleDomain;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.IntegerDomain;
import de.uni_koblenz.jgralab.schema.ListDomain;
import de.uni_koblenz.jgralab.schema.LongDomain;
import de.uni_koblenz.jgralab.schema.MapDomain;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.RecordDomain;
import de.uni_koblenz.jgralab.schema.RecordDomain.RecordComponent;
import de.uni_koblenz.jgralab.schema.SetDomain;
import de.uni_koblenz.jgralab.schema.StringDomain;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;
import de.uni_koblenz.jgralab.schema.impl.PackageImpl;
import de.uni_koblenz.jgralab.schema.impl.SchemaImpl;

public class GrEMFSchemaImpl extends SchemaImpl {

	private RecordDomain dateDomain;
	private IntegerDomain byteDomain;
	private IntegerDomain shortDomain;
	private DoubleDomain floatDomain;
	private StringDomain charDomain;

	private BooleanDomain booleanObjectDomain;
	private IntegerDomain byteObjectDomain;
	private IntegerDomain shortObjectDomain;
	private IntegerDomain integerObjectDomain;
	private LongDomain longObjectDomain;
	private StringDomain charObjectDomain;
	private DoubleDomain floatObjectDomain;
	private DoubleDomain doubleObjectDomain;
	private LongDomain bigIntegerDomain;
	private DoubleDomain bigDecimalDomain;

	public GrEMFSchemaImpl(String name, String packagePrefix) {
		super(name, packagePrefix);
	}

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	// --------------------------------------------------------------------------
	// ---- JGraLab
	// --------------------------------------------------------------------------
	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

	/**
	 * Creates a new {@link GraphClass} and saves it to the schema object
	 * 
	 * @param simpleName
	 *            the simple name of the graphclass in the schema
	 * @return the new graphclass
	 */
	@Override
	public GrEMFGraphClassImpl createGraphClass(String simpleName) {
		this.assertNotFinished();
		if (this.getGraphClass() != null) {
			throw new SchemaException(
					"Only one GraphClass (except DefaultGraphClass) is allowed in a Schema! '"
							+ this.getGraphClass().getQualifiedName()
							+ "' is already there.");
		}

		if (simpleName.contains(".")) {
			throw new SchemaException(
					"A GraphClass must always be in the default package!");
		}
		GrEMFGraphClassImpl gc = new GrEMFGraphClassImpl(simpleName, this);
		gc.initializeDefaultVertexClass();
		gc.initializeDefaultEdgeClass();

		return gc;
	}

	@Override
	protected PackageImpl createPackageWithParents(String qn) {
		return super.createPackageWithParents(qn);
	}

	@Override
	protected GrEMFPackageImpl createPackage(String sn, PackageImpl parentPkg) {
		super.assertNotFinished();
		return new GrEMFPackageImpl(sn, parentPkg, this);
	}

	@Override
	protected Package createDefaultPackage() {
		assert this.getDefaultPackage() == null : "DefaultPackage already created!";
		return new GrEMFPackageImpl("", null, this);
	}

	@Override
	public RecordDomain createRecordDomain(String qualifiedName,
			Collection<RecordComponent> recordComponents) {
		if (this.isFinished()) {
			throw new SchemaException("No changes to finished schema!");
		}
		if (qualifiedName.equals(GrEMFDateDomainImpl.DATEDOMAINNAME)) {
			return this.dateDomain;
		}
		String[] components = splitQualifiedName(qualifiedName);
		PackageImpl parent = this.createPackageWithParents(components[0]);
		String simpleName = components[1];
		RecordDomain rd = new GrEMFRecordDomainImpl(simpleName, parent,
				recordComponents);
		return rd;
	}

	public RecordDomain getDateDomain() {
		if (this.dateDomain == null) {
			this.dateDomain = new GrEMFDateDomainImpl(this);
		}
		return this.dateDomain;
	}

	// --------------- Domains -----------------

	public IntegerDomain getByteDomain() {
		if (this.byteDomain == null) {
			this.byteDomain = new GrEMFByteDomainImplProxy(this,
					EcorePackage.EBYTE, "ByteDomain");
		}
		return this.byteDomain;
	}

	public IntegerDomain getShortDomain() {
		if (this.shortDomain == null) {
			this.shortDomain = new GrEMFShortDomainImplProxy(this,
					EcorePackage.ESHORT, "ShortDomain");
		}
		return this.shortDomain;
	}

	public DoubleDomain getFloatDomain() {
		if (this.floatDomain == null) {
			this.floatDomain = new GrEMFFloatDomainImplProxy(this,
					EcorePackage.EFLOAT, "FloatDomain");
		}
		return this.floatDomain;
	}

	public StringDomain getCharDomain() {
		if (this.charDomain == null) {
			this.charDomain = new GrEMFCharacterDomainImplProxy(this,
					EcorePackage.ECHAR, "CharDomain");
		}
		return this.charDomain;
	}

	public BooleanDomain getBooleanObjectDomain() {
		if (this.booleanObjectDomain == null) {
			this.booleanObjectDomain = new GrEMFBooleanDomainImplProxy(this,
					"BooleanDomain");
		}
		return this.booleanObjectDomain;
	}

	public IntegerDomain getByteObjectDomain() {
		if (this.byteObjectDomain == null) {
			this.byteObjectDomain = new GrEMFByteDomainImplProxy(this,
					EcorePackage.EBYTE_OBJECT, "ByteObjectDomain");
		}
		return this.byteObjectDomain;
	}

	public IntegerDomain getShortObjectDomain() {
		if (this.shortObjectDomain == null) {
			this.shortObjectDomain = new GrEMFShortDomainImplProxy(this,
					EcorePackage.ESHORT_OBJECT, "ShortObjectDomain");
		}
		return this.shortObjectDomain;
	}

	public IntegerDomain getIntegerObjectDomain() {
		if (this.integerObjectDomain == null) {
			this.integerObjectDomain = new GrEMFIntegerDomainImplProxy(this,
					"IntegerObjectDomain");
		}
		return this.integerObjectDomain;
	}

	public LongDomain getLongObjectDomain() {
		if (this.longObjectDomain == null) {
			this.longObjectDomain = new GrEMFLongDomainImplProxy(this,
					"LongObjectDomain");
		}
		return this.longObjectDomain;
	}

	public StringDomain getCharObjectDomain() {
		if (this.charObjectDomain == null) {
			this.charObjectDomain = new GrEMFCharacterDomainImplProxy(this,
					EcorePackage.ECHARACTER_OBJECT, "CharObjectDomain");
		}
		return this.charObjectDomain;
	}

	public DoubleDomain getFloatObjectDomain() {
		if (this.floatObjectDomain == null) {
			this.floatObjectDomain = new GrEMFFloatDomainImplProxy(this,
					EcorePackage.EFLOAT_OBJECT, "FloatObjectDomain");
		}
		return this.floatObjectDomain;
	}

	public DoubleDomain getDoubleObjectDomain() {
		if (this.doubleObjectDomain == null) {
			this.doubleObjectDomain = new GrEMFDoubleDomainImplProxy(this,
					"DoubleObjectDomain");
		}
		return this.doubleObjectDomain;
	}

	public LongDomain getBigIntegerDomain() {
		if (this.bigIntegerDomain == null) {
			this.bigIntegerDomain = new GrEMFBigIntegerDomainImplProxy(this,
					"BigIntegerDomain");
		}
		return this.bigIntegerDomain;
	}

	public DoubleDomain getBigDecimalDomain() {
		if (this.bigDecimalDomain == null) {
			this.bigDecimalDomain = new GrEMFBigDecimalDomainImplProxy(this,
					"BigDoubleDomain");
		}
		return this.bigDecimalDomain;
	}

	@Override
	protected GrEMFBooleanDomainImpl createBooleanDomain() {
		if (this.getBooleanDomain() != null) {
			throw new SchemaException(
					"The BooleanDomain for this Schema was already created!");
		}
		return new GrEMFBooleanDomainImpl(this);
	}

	@Override
	protected GrEMFDoubleDomainImpl createDoubleDomain() {
		if (this.getDoubleDomain() != null) {
			throw new SchemaException(
					"The DoubleDomain for this Schema was already created!");
		}
		return new GrEMFDoubleDomainImpl(this);
	}

	@Override
	protected GrEMFIntegerDomainImpl createIntegerDomain() {
		if (this.getIntegerDomain() != null) {
			throw new SchemaException(
					"The IntegerDomain for this Schema was already created!");
		}
		return new GrEMFIntegerDomainImpl(this);
	}

	@Override
	protected GrEMFLongDomainImpl createLongDomain() {
		if (this.getLongDomain() != null) {
			throw new SchemaException(
					"The LongDomain for this Schema was already created!");
		}
		return new GrEMFLongDomainImpl(this);
	}

	@Override
	protected GrEMFStringDomainImpl createStringDomain() {
		if (this.getStringDomain() != null) {
			throw new SchemaException(
					"The StringDomain for this Schema was already created!");
		}
		return new GrEMFStringDomainImpl(this);
	}

	@Override
	public GrEMFEnumDomainImpl createEnumDomain(String qualifiedName) {
		return this.createEnumDomain(qualifiedName, new ArrayList<String>());
	}

	@Override
	public GrEMFEnumDomainImpl createEnumDomain(String qualifiedName,
			List<String> enumComponents) {
		String[] components = splitQualifiedName(qualifiedName);
		PackageImpl parent = this.createPackageWithParents(components[0]);
		String simpleName = components[1];
		return new GrEMFEnumDomainImpl(simpleName, parent, enumComponents);
	}

	@Override
	public ListDomain createListDomain(Domain baseDomain) {
		if (this.isFinished()) {
			throw new SchemaException("No changes to finished schema!");
		}
		String qn = "List<" + baseDomain.getQualifiedName() + ">";
		if (super.domains.containsKey(qn)) {
			return (ListDomain) this.domains.get(qn);
		}
		return new GrEMFListDomainImpl(this, baseDomain);
	}

	@Override
	public SetDomain createSetDomain(Domain baseDomain) {
		if (this.isFinished()) {
			throw new SchemaException("No changes to finished schema!");
		}
		String qn = "Set<" + baseDomain.getQualifiedName() + ">";
		if (this.domains.containsKey(qn)) {
			return (SetDomain) this.domains.get(qn);
		}
		return new GrEMFSetDomainImpl(this, baseDomain);
	}

	@Override
	public MapDomain createMapDomain(Domain keyDomain, Domain valueDomain) {
		if (this.isFinished()) {
			throw new SchemaException("No changes to finished schema!");
		}
		String qn = "Map<" + keyDomain.getQualifiedName() + ", "
				+ valueDomain.getQualifiedName() + ">";
		if (this.domains.containsKey(qn)) {
			return (MapDomain) this.domains.get(qn);
		}
		return new GrEMFMapDomainImpl(this, keyDomain, valueDomain);
	}

	@Override
	public boolean reopen() {
		throw new UnsupportedOperationException(
				"No schema changes allowed in grEMF");
	}

	// ///////////////////////
	// -- instances
	// //////////////////////

	private Graph defaultGraph;

	@Override
	public Graph createGraph(ImplementationType type) {
		Graph g = super.createGraph(type);
		this.defaultGraph = g;
		return g;
	}

	public Graph getCurrentGraph() {
		return this.defaultGraph;
	}

	public void setCurrentGraph(Graph g) {
		this.defaultGraph = g;
	}

	@Override
	public GraphFactory createDefaultGraphFactory(
			ImplementationType implementationType) {
		if (implementationType != ImplementationType.GENERIC) {
			throw new IllegalArgumentException(
					"Base implementation can't create a GraphFactory for implementation type "
							+ implementationType
							+ ". Only GENERIC is supported.");
		}
		return new GrEMFGraphFactoryImpl(this);
	}

}

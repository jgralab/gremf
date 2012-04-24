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

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcoreFactory;
import org.pcollections.ArrayPMap;
import org.pcollections.PMap;

import de.uni_koblenz.gremf.schema.GrEMFCollectionDomain;
import de.uni_koblenz.gremf.schema.GrEMFCompositeDomain;
import de.uni_koblenz.gremf.schema.GrEMFDomain;
import de.uni_koblenz.jgralab.impl.RecordImpl;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.impl.PackageImpl;
import de.uni_koblenz.jgralab.schema.impl.RecordDomainImpl;

public class GrEMFRecordDomainImpl extends RecordDomainImpl implements
		GrEMFCompositeDomain {

	private EDataType emfDataType;
	private boolean containsRecordOrEnumDomain;

	protected GrEMFRecordDomainImpl(String sn, Package pkg,
			Collection<RecordComponent> components) {
		super(sn, (PackageImpl) pkg, components);
		this.emfDataType = EcoreFactory.eINSTANCE.createEDataType();
		this.emfDataType.setName(sn);
		this.emfDataType.setInstanceClass(Map.class);
		this.emfDataType.setInstanceClassName("java.util.Map");
		this.emfDataType.setInstanceTypeName("java.util.Map");
		this.emfDataType.setSerializable(true);

		for (RecordComponent comp : components) {
			if ((comp.getDomain() instanceof GrEMFRecordDomainImpl)
					|| (comp.getDomain() instanceof GrEMFEnumDomainImpl)) {
				this.containsRecordOrEnumDomain = true;
			} else if (comp.getDomain() instanceof GrEMFCompositeDomain) {
				this.containsRecordOrEnumDomain |= ((GrEMFCompositeDomain) comp
						.getDomain()).containsRecordOrEnumDomain();
				if (comp.getDomain() instanceof GrEMFCollectionDomain) {
					((GrEMFCollectionDomain) comp.getDomain())
							.setComponentOfOtherDomain();
				}
			}
		}
	}

	@Override
	public Object getDefaultValue() {
		return null;
	}

	@Override
	public EDataType getEMFDataType() {
		return this.emfDataType;
	}

	@Override
	public Object getEMFValue(Object jgralabValue) {
		if (jgralabValue == null) {
			return jgralabValue;
		}
		if (this.containsRecordOrEnumDomain) {
			PMap<String, Object> jgraMap = ((RecordImpl) jgralabValue).toPMap();
			PMap<String, Object> newRec = ArrayPMap.empty();
			for (RecordComponent comp : this.getComponents()) {
				if (jgraMap.containsKey(comp.getName())) {
					newRec = newRec.plus(comp.getName(), ((GrEMFDomain) comp
							.getDomain()).getEMFValue(jgraMap.get(comp
							.getName())));
				}
			}
			return newRec;
		} else {
			return ((RecordImpl) jgralabValue).toPMap();
		}
	}

	@Override
	public Object getJGraLabValue(Object emfValue) {
		if (emfValue == null) {
			return null;
		}
		assert emfValue instanceof Map;
		@SuppressWarnings("unchecked")
		Map<String, ?> values = (Map<String, ?>) emfValue;
		RecordImpl record = RecordImpl.empty();
		for (RecordComponent c : this.getComponents()) {
			assert (values.containsKey(c.getName()));
			record = record.plus(c.getName(), ((GrEMFDomain) c.getDomain())
					.getJGraLabValue(values.get(c.getName())));
		}
		return record;
	}

	@Override
	public boolean containsRecordOrEnumDomain() {
		return this.containsRecordOrEnumDomain;
	}
}

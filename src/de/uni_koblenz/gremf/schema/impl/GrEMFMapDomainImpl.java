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

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcoreFactory;
import org.pcollections.ArrayPMap;
import org.pcollections.PMap;

import de.uni_koblenz.gremf.schema.GrEMFCollectionDomain;
import de.uni_koblenz.gremf.schema.GrEMFCompositeDomain;
import de.uni_koblenz.gremf.schema.GrEMFDomain;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.impl.MapDomainImpl;

public class GrEMFMapDomainImpl extends MapDomainImpl implements
		GrEMFCompositeDomain {

	private static EDataType emfDataType;
	private boolean containsRecordOrEnumDomain;

	protected GrEMFMapDomainImpl(Schema schema, Domain aKeyDomain,
			Domain aValueDomain) {
		super(schema, aKeyDomain, aValueDomain);
		if (emfDataType == null) {
			emfDataType = EcoreFactory.eINSTANCE.createEDataType();
			emfDataType.setInstanceClass(Map.class);
			emfDataType.setInstanceClassName("java.util.Map");
			emfDataType.setInstanceTypeName("java.util.Map");
			emfDataType.setName("java.util.Map");
			emfDataType.setSerializable(true);
		}
		if ((aKeyDomain instanceof GrEMFEnumDomainImpl)
				|| (aValueDomain instanceof GrEMFEnumDomainImpl)) {
			this.containsRecordOrEnumDomain = true;
		} else if ((aKeyDomain instanceof GrEMFRecordDomainImpl)
				|| (aValueDomain instanceof GrEMFRecordDomainImpl)) {
			this.containsRecordOrEnumDomain = true;
		} else if ((aKeyDomain instanceof GrEMFDateDomainImpl)
				|| (aValueDomain instanceof GrEMFDateDomainImpl)) {
			this.containsRecordOrEnumDomain = true;
		} else if (aKeyDomain instanceof GrEMFCompositeDomain) {
			this.containsRecordOrEnumDomain = ((GrEMFCompositeDomain) aKeyDomain)
					.containsRecordOrEnumDomain();
			if (aValueDomain instanceof GrEMFCompositeDomain) {
				this.containsRecordOrEnumDomain |= ((GrEMFCompositeDomain) aValueDomain)
						.containsRecordOrEnumDomain();
			}
		} else if (aValueDomain instanceof GrEMFCompositeDomain) {
			this.containsRecordOrEnumDomain = ((GrEMFCompositeDomain) aValueDomain)
					.containsRecordOrEnumDomain();
		}

		if (aKeyDomain instanceof GrEMFCollectionDomain) {
			((GrEMFCollectionDomain) aKeyDomain).setComponentOfOtherDomain();
		}
		if (aValueDomain instanceof GrEMFCollectionDomain) {
			((GrEMFCollectionDomain) aValueDomain).setComponentOfOtherDomain();
		}
	}

	@Override
	public EDataType getEMFDataType() {
		return emfDataType;
	}

	@Override
	public Object getDefaultValue() {
		return null;
	}

	@Override
	public Object getEMFValue(Object jgralabValue) {
		if ((jgralabValue != null) && this.containsRecordOrEnumDomain) {
			Map<?, ?> jgraMap = (Map<?, ?>) jgralabValue;
			PMap<Object, Object> map = ArrayPMap.empty();
			for (Entry<?, ?> e : jgraMap.entrySet()) {
				map = map.plus(((GrEMFDomain) this.getKeyDomain())
						.getEMFValue(e.getKey()), ((GrEMFDomain) this
						.getValueDomain()).getEMFValue(e.getValue()));
			}
			return map;
		} else {
			return jgralabValue;
		}
	}

	@Override
	public Object getJGraLabValue(Object emfValue) {
		if ((emfValue != null)
				&& (this.containsRecordOrEnumDomain || !(emfValue instanceof PMap))) {
			Map<?, ?> emfMap = (Map<?, ?>) emfValue;
			PMap<Object, Object> map = ArrayPMap.empty();
			for (Entry<?, ?> e : emfMap.entrySet()) {
				map = map.plus(((GrEMFDomain) this.getKeyDomain())
						.getJGraLabValue(e.getKey()), ((GrEMFDomain) this
						.getValueDomain()).getJGraLabValue(e.getValue()));
			}
			return map;
		} else {
			return emfValue;
		}

	}

	@Override
	public boolean containsRecordOrEnumDomain() {
		return this.containsRecordOrEnumDomain;
	}

}

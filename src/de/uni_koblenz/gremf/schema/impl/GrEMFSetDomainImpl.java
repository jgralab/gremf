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

import java.util.Set;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcoreFactory;
import org.pcollections.ArrayPSet;
import org.pcollections.PSet;

import de.uni_koblenz.gremf.schema.GrEMFCollectionDomain;
import de.uni_koblenz.gremf.schema.GrEMFCompositeDomain;
import de.uni_koblenz.gremf.schema.GrEMFDomain;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.impl.SetDomainImpl;

public class GrEMFSetDomainImpl extends SetDomainImpl implements
		GrEMFCollectionDomain {

	private EDataType emfDataType;

	private boolean isComponentOfOtherDomain;

	private boolean containsRecordOrEnumDomain;

	GrEMFSetDomainImpl(Schema schema, Domain aBaseDomain) {
		super(schema, aBaseDomain);
		if (aBaseDomain instanceof GrEMFEnumDomainImpl) {
			this.containsRecordOrEnumDomain = true;
		} else if (aBaseDomain instanceof GrEMFRecordDomainImpl) {
			this.containsRecordOrEnumDomain = true;
		} else if (aBaseDomain instanceof GrEMFDateDomainImpl) {
			this.containsRecordOrEnumDomain = true;
		} else if (aBaseDomain instanceof GrEMFCompositeDomain) {
			this.containsRecordOrEnumDomain = ((GrEMFCompositeDomain) aBaseDomain)
					.containsRecordOrEnumDomain();
			if (aBaseDomain instanceof GrEMFCollectionDomain) {
				((GrEMFCollectionDomain) aBaseDomain)
						.setComponentOfOtherDomain();
			}
		}
	}

	@Override
	public EDataType getEMFDataType() {
		if (this.isComponentOfOtherDomain) {
			return this.emfDataType;
		}
		return ((GrEMFDomain) this.getBaseDomain()).getEMFDataType();
	}

	@Override
	public Object getDefaultValue() {
		return null;
	}

	@Override
	public Object getEMFValue(Object jgralabValue) {
		if ((jgralabValue != null) && this.containsRecordOrEnumDomain) {
			Set<?> jgraset = (ArrayPSet<?>) jgralabValue;
			ArrayPSet<Object> set = ArrayPSet.empty();
			for (Object o : jgraset) {
				set = (ArrayPSet<Object>) set.plus(((GrEMFDomain) this
						.getBaseDomain()).getEMFValue(o));
			}
			return set;
		} else {
			return jgralabValue;
		}
	}

	@Override
	public Object getJGraLabValue(Object emfValue) {
		if ((emfValue != null)
				&& (this.containsRecordOrEnumDomain || !(emfValue instanceof PSet))) {
			Set<?> emflist = (Set<?>) emfValue;
			ArrayPSet<Object> list = ArrayPSet.empty();
			for (Object o : emflist) {
				list = (ArrayPSet<Object>) list.plus(((GrEMFDomain) this
						.getBaseDomain()).getJGraLabValue(o));
			}
			return list;
		} else {
			return emfValue;
		}
	}

	@Override
	public boolean containsRecordOrEnumDomain() {
		return this.containsRecordOrEnumDomain;
	}

	@Override
	public boolean isComponentOfOtherDomain() {
		return this.isComponentOfOtherDomain;
	}

	@Override
	public void setComponentOfOtherDomain() {
		this.isComponentOfOtherDomain = true;
		this.emfDataType = EcoreFactory.eINSTANCE.createEDataType();
		this.emfDataType.setInstanceClass(Set.class);
		this.emfDataType.setInstanceClassName("java.util.Set");
		this.emfDataType.setInstanceTypeName("java.util.Set");
		this.emfDataType.setName("java.util.Set");
		this.emfDataType.setSerializable(true);
	}

}

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

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcorePackage;

import de.uni_koblenz.gremf.schema.GrEMFDomain;
import de.uni_koblenz.jgralab.impl.RecordImpl;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.impl.PackageImpl;
import de.uni_koblenz.jgralab.schema.impl.RecordDomainImpl;

public class GrEMFDateDomainImpl extends RecordDomainImpl implements
		GrEMFDomain {

	public static final String DATEDOMAINNAME = "DateDomain";

	protected GrEMFDateDomainImpl(Schema schema) {
		super(
				DATEDOMAINNAME,
				(PackageImpl) schema.getDefaultPackage(),
				Arrays.asList(
						new RecordComponent("year", schema.getIntegerDomain()),
						new RecordComponent("month", schema.getIntegerDomain()),
						new RecordComponent("day", schema.getIntegerDomain()),
						new RecordComponent("hour", schema.getIntegerDomain()),
						new RecordComponent("minute", schema.getIntegerDomain()),
						new RecordComponent("second", schema.getIntegerDomain())));
	}

	@Override
	public Object getDefaultValue() {
		return null;
	}

	@Override
	public EDataType getEMFDataType() {
		return EcorePackage.eINSTANCE.getEDate();
	}

	@Override
	public Object getEMFValue(Object jgralabValue) {
		if (jgralabValue == null) {
			return jgralabValue;
		}
		Map<String, Object> map = ((RecordImpl) jgralabValue).toPMap();
		Calendar c = Calendar.getInstance();
		c.set((Integer) map.get("year"), (Integer) map.get("month"),
				(Integer) map.get("day"));
		return new Date(c.getTimeInMillis());
	}

	@Override
	public Object getJGraLabValue(Object emfValue) {
		if (emfValue == null) {
			return emfValue;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime((Date) emfValue);

		Map<String, Object> map = new HashMap<String, Object>(6);
		map.put("year", cal.get(Calendar.YEAR));
		map.put("month", cal.get(Calendar.MONTH));
		map.put("day", cal.get(Calendar.DAY_OF_MONTH));
		map.put("hour", cal.get(Calendar.HOUR_OF_DAY));
		map.put("minute", cal.get(Calendar.MINUTE));
		map.put("second", cal.get(Calendar.SECOND));

		RecordImpl record = RecordImpl.empty();
		for (RecordComponent c : this.getComponents()) {
			assert (map.containsKey(c.getName()));
			record = record.plus(c.getName(), map.get(c.getName()));
		}
		return record;
	}
}

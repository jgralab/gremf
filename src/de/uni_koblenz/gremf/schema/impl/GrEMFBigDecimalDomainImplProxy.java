package de.uni_koblenz.gremf.schema.impl;

import java.math.BigDecimal;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcorePackage;

import de.uni_koblenz.jgralab.schema.Schema;

public class GrEMFBigDecimalDomainImplProxy extends GrEMFDoubleDomainImplProxy {

	protected GrEMFBigDecimalDomainImplProxy(Schema schema, String name) {
		super(schema, name);
	}

	@Override
	public EDataType getEMFDataType() {
		return EcorePackage.eINSTANCE.getEBigDecimal();
	}

	@Override
	public Object getDefaultValue() {
		return 0.0;
	}

	@Override
	public Object getEMFValue(Object jgralabValue) {
		return BigDecimal.valueOf((Double) jgralabValue);
	}

	@Override
	public Object getJGraLabValue(Object emfValue) {
		if (emfValue == null) {
			return null;
		}
		return ((BigDecimal) emfValue).doubleValue();
	}
}

package de.uni_koblenz.gremf.schema.impl;

import java.math.BigInteger;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcorePackage;

import de.uni_koblenz.jgralab.schema.Schema;

public class GrEMFBigIntegerDomainImplProxy extends GrEMFLongDomainImplProxy {

	protected GrEMFBigIntegerDomainImplProxy(Schema schema, String name) {
		super(schema, name);
	}

	@Override
	public EDataType getEMFDataType() {
		return EcorePackage.eINSTANCE.getEBigInteger();
	}

	@Override
	public Object getDefaultValue() {
		return 0L;
	}

	@Override
	public Object getEMFValue(Object jgralabValue) {
		return BigInteger.valueOf((Long) jgralabValue);
	}

	@Override
	public Object getJGraLabValue(Object emfValue) {
		if (emfValue == null) {
			return null;
		}
		return ((BigInteger) emfValue).longValue();
	}
}

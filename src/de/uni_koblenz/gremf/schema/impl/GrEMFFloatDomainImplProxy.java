package de.uni_koblenz.gremf.schema.impl;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcorePackage;

import de.uni_koblenz.jgralab.schema.DoubleDomain;
import de.uni_koblenz.jgralab.schema.Schema;

public class GrEMFFloatDomainImplProxy extends GrEMFBasicDomainImplProxy
		implements DoubleDomain {

	private int originalEDataType;

	protected GrEMFFloatDomainImplProxy(Schema schema, int originalEDataType,
			String name) {
		super(schema.getDoubleDomain(), name);
		this.originalEDataType = originalEDataType;
	}

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	// --------------------------------------------------------------------------
	// ---- grEMF
	// --------------------------------------------------------------------------
	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

	// --------------------------------------------------------------------------
	// Methods from interface
	// de.uni_koblenz.gremf.schema.GrEMFDomain
	// --------------------------------------------------------------------------

	@Override
	public EDataType getEMFDataType() {
		if (this.originalEDataType == EcorePackage.EFLOAT) {
			return EcorePackage.eINSTANCE.getEFloat();
		} else {
			return EcorePackage.eINSTANCE.getEFloatObject();
		}
	}

	@Override
	public Object getDefaultValue() {
		return 0.0f;
	}

	@Override
	public Object getEMFValue(Object jgralabValue) {
		if (jgralabValue == null) {
			return null;
		}
		return ((Number) jgralabValue).floatValue();
	}

	@Override
	public Object getJGraLabValue(Object emfValue) {
		if (emfValue == null) {
			return null;
		}
		return ((Number) emfValue).doubleValue();
	}
}

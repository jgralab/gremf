package de.uni_koblenz.gremf.schema.impl;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcorePackage;

import de.uni_koblenz.jgralab.schema.IntegerDomain;
import de.uni_koblenz.jgralab.schema.Schema;

public class GrEMFByteDomainImplProxy extends GrEMFBasicDomainImplProxy
		implements IntegerDomain {

	private int originalEDataType;

	protected GrEMFByteDomainImplProxy(Schema schema, int originalEDataType,
			String name) {
		super(schema.getIntegerDomain(), name);
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
		if (this.originalEDataType == EcorePackage.EBYTE) {
			return EcorePackage.eINSTANCE.getEByte();
		} else {
			return EcorePackage.eINSTANCE.getEByteObject();
		}
	}

	@Override
	public Object getDefaultValue() {
		return 0;
	}

	@Override
	public Object getEMFValue(Object jgralabValue) {
		if (jgralabValue == null) {
			return null;
		}
		return ((Number) jgralabValue).byteValue();
	}

	@Override
	public Object getJGraLabValue(Object emfValue) {
		if (emfValue == null) {
			return null;
		}
		return ((Number) emfValue).intValue();
	}
}

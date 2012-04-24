package de.uni_koblenz.gremf.schema.impl;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcorePackage;

import de.uni_koblenz.gremf.schema.GrEMFDomain;
import de.uni_koblenz.jgralab.schema.LongDomain;
import de.uni_koblenz.jgralab.schema.Schema;

public class GrEMFLongDomainImplProxy extends GrEMFBasicDomainImplProxy
		implements GrEMFDomain, LongDomain {

	protected GrEMFLongDomainImplProxy(Schema schema, String name) {
		super(schema.getLongDomain(), name);
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
		return EcorePackage.eINSTANCE.getELongObject();
	}

	@Override
	public Object getDefaultValue() {
		return 0L;
	}

	@Override
	public Object getEMFValue(Object jgralabValue) {
		return jgralabValue;
	}

	@Override
	public Object getJGraLabValue(Object emfValue) {
		return emfValue;
	}
}

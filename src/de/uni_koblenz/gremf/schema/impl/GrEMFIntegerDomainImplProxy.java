package de.uni_koblenz.gremf.schema.impl;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcorePackage;

import de.uni_koblenz.gremf.schema.GrEMFDomain;
import de.uni_koblenz.jgralab.schema.IntegerDomain;
import de.uni_koblenz.jgralab.schema.Schema;

public class GrEMFIntegerDomainImplProxy extends GrEMFBasicDomainImplProxy
		implements GrEMFDomain, IntegerDomain {

	protected GrEMFIntegerDomainImplProxy(Schema schema, String name) {
		super(schema.getIntegerDomain(), name);
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
		return EcorePackage.eINSTANCE.getEIntegerObject();
	}

	@Override
	public Object getDefaultValue() {
		return 0;
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

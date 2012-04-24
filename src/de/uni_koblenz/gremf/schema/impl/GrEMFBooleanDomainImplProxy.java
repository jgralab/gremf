package de.uni_koblenz.gremf.schema.impl;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcorePackage;

import de.uni_koblenz.gremf.schema.GrEMFDomain;
import de.uni_koblenz.jgralab.schema.BooleanDomain;
import de.uni_koblenz.jgralab.schema.Schema;

public class GrEMFBooleanDomainImplProxy extends GrEMFBasicDomainImplProxy
		implements GrEMFDomain, BooleanDomain {

	protected GrEMFBooleanDomainImplProxy(Schema schema, String name) {
		super(schema.getBooleanDomain(), name);
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
		return EcorePackage.eINSTANCE.getEBooleanObject();
	}

	@Override
	public Boolean getDefaultValue() {
		return false;
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

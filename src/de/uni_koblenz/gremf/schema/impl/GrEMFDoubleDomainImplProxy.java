package de.uni_koblenz.gremf.schema.impl;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcorePackage;

import de.uni_koblenz.gremf.schema.GrEMFDomain;
import de.uni_koblenz.jgralab.schema.DoubleDomain;
import de.uni_koblenz.jgralab.schema.Schema;

public class GrEMFDoubleDomainImplProxy extends GrEMFBasicDomainImplProxy
		implements GrEMFDomain, DoubleDomain {

	protected GrEMFDoubleDomainImplProxy(Schema schema, String name) {
		super(schema.getDoubleDomain(), name);
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
		return EcorePackage.eINSTANCE.getEDoubleObject();
	}

	@Override
	public Object getDefaultValue() {
		return 0.0d;
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

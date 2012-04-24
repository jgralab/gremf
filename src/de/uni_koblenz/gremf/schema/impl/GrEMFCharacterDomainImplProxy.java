package de.uni_koblenz.gremf.schema.impl;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcorePackage;

import de.uni_koblenz.gremf.schema.GrEMFDomain;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.StringDomain;

public class GrEMFCharacterDomainImplProxy extends GrEMFBasicDomainImplProxy
		implements StringDomain, GrEMFDomain {

	private int originalEDataType;

	protected GrEMFCharacterDomainImplProxy(Schema schema,
			int originalEDataType, String name) {
		super(schema.getStringDomain(), name);
		this.originalEDataType = originalEDataType;
	}

	@Override
	public EDataType getEMFDataType() {
		if (this.originalEDataType == EcorePackage.ECHAR) {
			return EcorePackage.eINSTANCE.getEChar();
		} else {
			return EcorePackage.eINSTANCE.getECharacterObject();
		}
	}

	@Override
	public Object getDefaultValue() {
		return null;
	}

	@Override
	public Object getEMFValue(Object jgralabValue) {
		if (jgralabValue == null) {
			return null;
		}
		return ((String) jgralabValue).charAt(0);
	}

	@Override
	public Object getJGraLabValue(Object emfValue) {
		if (emfValue == null) {
			return null;
		}
		return ((Character) emfValue).toString();
	}
}

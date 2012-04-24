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
package de.uni_koblenz.gremf.util;

import java.util.Collection;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcorePackage;

import de.uni_koblenz.gremf.exception.InvalidFeatureException;
import de.uni_koblenz.gremf.exception.UnsupportedSchemaChangeException;

/**
 * Helper methods for interface EClass
 * 
 * @author jbaltzer
 * 
 */
public class EClassUtil {

	public static int getFeatureID(EClass eClass, EStructuralFeature feature) {
		for (EStructuralFeature sf : eClass.getEAllStructuralFeatures()) {
			if (sf.equals(feature)) {
				return sf.getFeatureID();
			}
		}
		return -1;
	}

	public static Object eGet(EClass c, int featureID, boolean resolve,
			boolean coreType) {
		switch (featureID) {
		case EcorePackage.ECLASS__EANNOTATIONS:
			return c.getEAnnotations();
		case EcorePackage.ECLASS__NAME:
			return c.getName();
		case EcorePackage.ECLASS__DEFAULT_VALUE:
			return c.getDefaultValue();
		case EcorePackage.ECLASS__EPACKAGE:
			return c.getEPackage();
		case EcorePackage.ECLASS__ABSTRACT:
			return c.isAbstract();
		case EcorePackage.ECLASS__INTERFACE:
			return c.isInterface();
		case EcorePackage.ECLASS__ESUPER_TYPES:
			return c.getESuperTypes();
		case EcorePackage.ECLASS__EALL_ATTRIBUTES:
			return c.getEAllAttributes();
		case EcorePackage.ECLASS__EALL_REFERENCES:
			return c.getEAllReferences();
		case EcorePackage.ECLASS__EREFERENCES:
			return c.getEReferences();
		case EcorePackage.ECLASS__EATTRIBUTES:
			return c.getEAttributes();
		case EcorePackage.ECLASS__EALL_CONTAINMENTS:
			return c.getEAllContainments();
		case EcorePackage.ECLASS__EALL_STRUCTURAL_FEATURES:
			return c.getEAllStructuralFeatures();
		case EcorePackage.ECLASS__EALL_SUPER_TYPES:
			return c.getEAllSuperTypes();
		case EcorePackage.ECLASS__EID_ATTRIBUTE:
			return c.getEIDAttribute();
		case EcorePackage.ECLASS__ESTRUCTURAL_FEATURES:
			return c.getEStructuralFeatures();
		case EcorePackage.ECLASS__INSTANCE_CLASS_NAME:
			return c.getInstanceClassName();
		case EcorePackage.ECLASS__INSTANCE_CLASS:
			return c.getInstanceClass();
		case EcorePackage.ECLASS__INSTANCE_TYPE_NAME:
			return c.getInstanceTypeName();
		case EcorePackage.ECLASS__EOPERATIONS:
			return c.getEOperations();
		case EcorePackage.ECLASS__EALL_OPERATIONS:
			return c.getEAllOperations();
		case EcorePackage.ECLASS__EGENERIC_SUPER_TYPES:
			return c.getEGenericSuperTypes();
		case EcorePackage.ECLASS__EALL_GENERIC_SUPER_TYPES:
			return c.getEAllGenericSuperTypes();
		case EcorePackage.ECLASS__ETYPE_PARAMETERS:
			return c.getETypeParameters();
		default:
			throw new InvalidFeatureException();
		}
	}

	@SuppressWarnings("unchecked")
	public static void eSet(EClass c, int featureID, Object newValue) {
		switch (featureID) {
		case EcorePackage.ECLASS__EANNOTATIONS:
			c.getEAnnotations().clear();
			c.getEAnnotations().addAll(
					(Collection<? extends EAnnotation>) newValue);
			return;
		case EcorePackage.ECLASS__ABSTRACT:
			c.setAbstract((Boolean) newValue);
			return;
		case EcorePackage.ECLASS__INTERFACE:
			c.setInterface((Boolean) newValue);
			return;
		case EcorePackage.ECLASS__EALL_ATTRIBUTES:
		case EcorePackage.ECLASS__EALL_CONTAINMENTS:
		case EcorePackage.ECLASS__EALL_GENERIC_SUPER_TYPES:
		case EcorePackage.ECLASS__EALL_OPERATIONS:
		case EcorePackage.ECLASS__EALL_REFERENCES:
		case EcorePackage.ECLASS__EALL_STRUCTURAL_FEATURES:
		case EcorePackage.ECLASS__EALL_SUPER_TYPES:
		case EcorePackage.ECLASS__EID_ATTRIBUTE:
		case EcorePackage.ECLASS__EPACKAGE:
			// in EMF not settable; EMF behavior
			throw new NullPointerException();
		default:
			throw new UnsupportedSchemaChangeException();
		}
	}

	public static boolean eIsSet(EClass c, int featureID) {
		switch (featureID) {
		case EcorePackage.ECLASS__EANNOTATIONS:
			EList<EAnnotation> eAnnos = c.getEAnnotations();
			return (eAnnos != null) && !eAnnos.isEmpty();
		case EcorePackage.ECLASS__NAME:
			if (EcorePackage.Literals.ENAMED_ELEMENT__NAME.getDefaultValue() == null) {
				return c.getName() != null;
			} else {
				return !EcorePackage.Literals.ENAMED_ELEMENT__NAME
						.getDefaultValue().equals(c.getName());
			}
		case EcorePackage.ECLASS__DEFAULT_VALUE:
			if (EcorePackage.Literals.ECLASSIFIER__DEFAULT_VALUE
					.getDefaultValue() == null) {
				return c.getDefaultValue() != null;
			} else {
				return !EcorePackage.Literals.ECLASSIFIER__DEFAULT_VALUE
						.getDefaultValue().equals(c.getDefaultValue());
			}
		case EcorePackage.ECLASS__EPACKAGE:
			return c.getEPackage() != null;
		case EcorePackage.ECLASS__ABSTRACT:
			return c.isAbstract() != (Boolean) EcorePackage.Literals.ECLASS__ABSTRACT
					.getDefaultValue();
		case EcorePackage.ECLASS__INTERFACE:
			return c.isInterface() != (Boolean) EcorePackage.Literals.ECLASS__INTERFACE
					.getDefaultValue();
		case EcorePackage.ECLASS__ESUPER_TYPES:
			EList<EClass> eSupers = c.getESuperTypes();
			return (eSupers != null) && !eSupers.isEmpty();
		case EcorePackage.ECLASS__EALL_SUPER_TYPES:
			EList<EClass> eAllSupers = c.getEAllSuperTypes();
			return (eAllSupers != null) && !eAllSupers.isEmpty();
		case EcorePackage.ECLASS__EATTRIBUTES:
			EList<EAttribute> eAttrs = c.getEAttributes();
			return (eAttrs != null) && !eAttrs.isEmpty();
		case EcorePackage.ECLASS__EALL_ATTRIBUTES:
			EList<EAttribute> eAllAttrs = c.getEAllAttributes();
			return (eAllAttrs != null) && !eAllAttrs.isEmpty();
		case EcorePackage.ECLASS__EREFERENCES:
			EList<EReference> eRefs = c.getEReferences();
			return (eRefs != null) && !eRefs.isEmpty();
		case EcorePackage.ECLASS__EALL_REFERENCES:
			EList<EReference> eAllRefs = c.getEAllReferences();
			return (eAllRefs != null) && !eAllRefs.isEmpty();
		case EcorePackage.ECLASS__EALL_CONTAINMENTS:
			EList<EReference> eAllConts = c.getEAllContainments();
			return (eAllConts != null) && !eAllConts.isEmpty();
		case EcorePackage.ECLASS__ESTRUCTURAL_FEATURES:
			EList<EStructuralFeature> eFeatures = c.getEStructuralFeatures();
			return (eFeatures != null) && !eFeatures.isEmpty();
		case EcorePackage.ECLASS__EALL_STRUCTURAL_FEATURES:
			EList<EStructuralFeature> eAllFeatures = c
					.getEAllStructuralFeatures();
			return (eAllFeatures != null) && !eAllFeatures.isEmpty();
		case EcorePackage.ECLASS__EID_ATTRIBUTE:
			return c.getEIDAttribute() != null;
		case EcorePackage.ECLASS__INSTANCE_CLASS_NAME:
			return c.getInstanceClassName() != null;
		case EcorePackage.ECLASS__INSTANCE_CLASS:
			return c.getInstanceClass() != null;
		case EcorePackage.ECLASS__INSTANCE_TYPE_NAME:
			return c.getInstanceTypeName() != null;
		case EcorePackage.ECLASS__EGENERIC_SUPER_TYPES:
			EList<EGenericType> eGenericSupers = c.getEGenericSuperTypes();
			return (eGenericSupers != null) && !eGenericSupers.isEmpty();
		case EcorePackage.ECLASS__EALL_GENERIC_SUPER_TYPES:
			EList<EGenericType> eAllGenericSupers = c
					.getEAllGenericSuperTypes();
			return (eAllGenericSupers != null) && !eAllGenericSupers.isEmpty();
		case EcorePackage.ECLASS__ETYPE_PARAMETERS:
			EList<ETypeParameter> eTypes = c.getETypeParameters();
			return (eTypes != null) && !eTypes.isEmpty();
		case EcorePackage.ECLASS__EOPERATIONS:
			EList<EOperation> eOps = c.getEOperations();
			return (eOps != null) && !eOps.isEmpty();
		case EcorePackage.ECLASS__EALL_OPERATIONS:
			EList<EOperation> eAllOps = c.getEOperations();
			return (eAllOps != null) && !eAllOps.isEmpty();
		default:
			throw new InvalidFeatureException();
		}
	}

	public static void eUnset(EClass c, int featureID) {
		switch (featureID) {
		case EcorePackage.ECLASS__EANNOTATIONS:
			c.getEAnnotations().clear();
			return;
		case EcorePackage.ECLASS__ABSTRACT:
			c.setAbstract((Boolean) EcorePackage.Literals.ECLASS__ABSTRACT
					.getDefaultValue());
			return;
		case EcorePackage.ECLASS__INTERFACE:
			c.setInterface((Boolean) EcorePackage.Literals.ECLASS__INTERFACE
					.getDefaultValue());
			return;
		case EcorePackage.ECLASS__EALL_ATTRIBUTES:
		case EcorePackage.ECLASS__EALL_CONTAINMENTS:
		case EcorePackage.ECLASS__EALL_GENERIC_SUPER_TYPES:
		case EcorePackage.ECLASS__EALL_OPERATIONS:
		case EcorePackage.ECLASS__EALL_REFERENCES:
		case EcorePackage.ECLASS__EALL_STRUCTURAL_FEATURES:
		case EcorePackage.ECLASS__EALL_SUPER_TYPES:
		case EcorePackage.ECLASS__EID_ATTRIBUTE:
		case EcorePackage.ECLASS__EPACKAGE:
			// in EMF not settable; EMF behavior
			throw new NullPointerException();
		default:
			throw new UnsupportedSchemaChangeException();
		}

	}
}

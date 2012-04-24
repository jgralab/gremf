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
package de.uni_koblenz.gremf.collection;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EClassImpl.FeatureSubsetSupplier;
import org.eclipse.emf.ecore.util.EcoreEList;

public class GrEMFStructuralFeaturesList extends
		EcoreEList.UnmodifiableEList.FastCompare<EStructuralFeature> implements
		FeatureSubsetSupplier {

	/**
	 * @generated
	 */
	private static final long serialVersionUID = 1139709796234275214L;

	private EStructuralFeature[] containments;
	private EStructuralFeature[] crossReferences;

	public GrEMFStructuralFeaturesList(InternalEObject owner,
			BasicEList<EStructuralFeature> eAllStructuralFeatures) {
		super(owner, EcorePackage.eINSTANCE.getEClass_EAllStructuralFeatures(),
				eAllStructuralFeatures.size(), eAllStructuralFeatures.data());
		this.init();
	}

	private void init() {
		BasicEList<EStructuralFeature> contains = new UniqueEList<EStructuralFeature>(
				this.size);
		BasicEList<EStructuralFeature> crossRefs = new UniqueEList<EStructuralFeature>(
				this.size);

		for (int i = 0; i < this.size; ++i) {
			EStructuralFeature eFeature = (EStructuralFeature) this.data[i];
			if (eFeature instanceof EReference) {
				EReference eReference = (EReference) eFeature;

				if (eReference.isContainment()) {
					contains.add(eReference);
				} else if (!eReference.isContainer()) {
					crossRefs.add(eReference);
				}
			}
		}
		contains.shrink();
		crossRefs.shrink();

		this.containments = new EStructuralFeature[contains.size()];
		this.crossReferences = new EStructuralFeature[crossRefs.size()];
		int i = 0;
		for (EStructuralFeature feature : contains) {
			this.containments[i++] = feature;
		}
		i = 0;
		for (EStructuralFeature feature : crossRefs) {
			this.crossReferences[i++] = feature;
		}
	}

	@Override
	public EStructuralFeature[] containments() {
		if (this.containments == null) {
			this.init();
		}
		return this.containments;
	}

	@Override
	public EStructuralFeature[] crossReferences() {
		if (this.crossReferences == null) {
			this.init();
		}
		return this.crossReferences;
	}

	@Override
	public EStructuralFeature[] features() {
		return (EStructuralFeature[]) this.data;
	}

	@Override
	public int indexOf(Object obj) {
		if (obj instanceof EStructuralFeature) {
			for (int i = 0; i < this.size; i++) {
				if (this.data[i] == obj) {
					return i;
				}
			}
		}
		return -1;
	}
}

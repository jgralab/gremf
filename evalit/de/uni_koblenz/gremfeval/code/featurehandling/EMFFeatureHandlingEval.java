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
package de.uni_koblenz.gremfeval.code.featurehandling;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import de.uni_koblenz.gremfeval.code.find.EMFFindEval;
import de.uni_koblenz.gremfeval.code.load.EMFLoadEval;

public class EMFFeatureHandlingEval implements Runnable {

	private final boolean withGrEMF;

	public EMFFeatureHandlingEval(boolean withGrEMF) {
		this.withGrEMF = withGrEMF;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		Map<EObject, List<EObject>> objects = new EMFFindEval(new EMFLoadEval(
				this.withGrEMF).call()).call();
		Iterator<EObject> iter = objects.keySet().iterator();
		EObject objA = iter.next();
		EPackage commonsPkg = objA.eResource().getResourceSet()
				.getPackageRegistry()
				.getEPackage("http://www.emftext.org/java/commons");

		EClass commetableCls = (EClass) commonsPkg
				.getEClassifier("Commentable");
		EStructuralFeature commentsAttr = commetableCls
				.getEStructuralFeature("comments");

		EPackage modifiersPkg = objA.eResource().getResourceSet()
				.getPackageRegistry()
				.getEPackage("http://www.emftext.org/java/modifiers");

		EClass modifiableCls = (EClass) modifiersPkg
				.getEClassifier("AnnotableAndModifiable");
		EStructuralFeature modifierRef = modifiableCls
				.getEStructuralFeature("annotationsAndModifiers");
		EClass finalCls = (EClass) modifiersPkg.getEClassifier("Final");

		EClass interfaceCls = objA.eClass();
		EStructuralFeature nameAttr = interfaceCls
				.getEStructuralFeature("name");

		// normal attribute
		String name = (String) objA.eGet(nameAttr);
		objA.eSet(nameAttr, name + "_test");

		// list reference
		List<EObject> modifiers = (List<EObject>) objA.eGet(modifierRef);
		EObject privateObj = modifiersPkg.getEFactoryInstance()
				.create(finalCls);
		modifiers.add(privateObj);
		if (modifiers.contains(privateObj)) {
			modifiers.remove(privateObj);
		}

		// list attribute
		List<String> comments = (List<String>) modifiers.get(0).eGet(
				commentsAttr);
		if (!comments.isEmpty()) {
			comments.clear();
		}
		comments.add("/**\n* this is a new comment\n*/");
		comments.add("/**\n* and this is another\n*/");
		comments.add("/**\n* and yet another\n*/");

	}
}

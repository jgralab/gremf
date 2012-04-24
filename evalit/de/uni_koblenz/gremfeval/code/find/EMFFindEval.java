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
package de.uni_koblenz.gremfeval.code.find;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;

public class EMFFindEval implements Callable<Map<EObject, List<EObject>>> {

	private Resource instance;

	public EMFFindEval(Resource instance) {
		this.instance = instance;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<EObject, List<EObject>> call() {

		EPackage classifiersPkg = this.instance.getResourceSet()
				.getPackageRegistry()
				.getEPackage("http://www.emftext.org/java/classifiers");

		EClass interfaceCls = (EClass) classifiersPkg
				.getEClassifier("Interface");
		EAttribute interfaceNameAttr = (EAttribute) interfaceCls
				.getEStructuralFeature("name");
		EReference extendsRef = (EReference) interfaceCls
				.getEStructuralFeature("extends");

		EPackage typesPkg = this.instance.getResourceSet().getPackageRegistry()
				.getEPackage("http://www.emftext.org/java/types");

		EClass namespaceClassifierReferenceCls = (EClass) typesPkg
				.getEClassifier("NamespaceClassifierReference");
		EReference classifierReferencesRef = (EReference) namespaceClassifierReferenceCls
				.getEStructuralFeature("classifierReferences");

		EClass classifierReferenceCls = (EClass) typesPkg
				.getEClassifier("ClassifierReference");
		EReference targetRef = (EReference) classifierReferenceCls
				.getEStructuralFeature("target");

		TreeIterator<EObject> iter = this.instance.getAllContents();
		Map<EObject, List<EObject>> report = new HashMap<EObject, List<EObject>>();
		while (iter.hasNext()) {
			EObject obj = iter.next();
			if (interfaceCls.isInstance(obj) && obj.eIsSet(interfaceNameAttr)) {
				String nameValue = (String) obj.eGet(interfaceNameAttr);
				if (nameValue.startsWith("GrEMF")) {
					List<EObject> list = new LinkedList<EObject>();
					list.add(obj);

					Queue<EObject> queue = new LinkedList<EObject>();
					if (obj.eIsSet(extendsRef)) {
						queue.offer(obj);
					}
					while (!queue.isEmpty()) {
						EObject next = queue.poll();
						for (EObject o1 : (List<EObject>) next.eGet(extendsRef)) {
							if (namespaceClassifierReferenceCls.isInstance(o1)
									&& o1.eIsSet(classifierReferencesRef)) {
								for (EObject o2 : (List<EObject>) o1
										.eGet(classifierReferencesRef)) {
									if (classifierReferenceCls.isInstance(o2)
											&& o2.eIsSet(targetRef)) {
										EObject o3 = (EObject) o2
												.eGet(targetRef);
										list.add(o3);
										if (interfaceCls.isInstance(o3)
												&& o3.eIsSet(extendsRef)) {
											queue.offer(o3);
										}
									}
								}
							}
						}
					}
					report.put(obj, list);
				}
			}
		}
		return report;
	}
}

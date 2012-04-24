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

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;

/**
 * Helper methods for interface EModelElement - because of the lack of
 * inheritance possibilities I don't want to copy functionality too often - this
 * class should contain the main functionality, and can be used by all classes
 * implementing EModelElement
 * 
 * 
 * @author kheckelmann
 * 
 */
public class EModelElementUtil {

	/**
	 * Copied from EModelElementImpl
	 */
	public static EAnnotation getEAnnotation(String source,
			EList<EAnnotation> eAnnotations) {
		if (eAnnotations != null) {
			if (eAnnotations instanceof BasicEList<?>) {
				int size = eAnnotations.size();
				if (size > 0) {
					EAnnotation[] eAnnotationArray = (EAnnotation[]) ((BasicEList<?>) eAnnotations)
							.data();
					if (source == null) {
						for (int i = 0; i < size; ++i) {
							EAnnotation eAnnotation = eAnnotationArray[i];
							if (eAnnotation.getSource() == null) {
								return eAnnotation;
							}
						}
					} else {
						for (int i = 0; i < size; ++i) {
							EAnnotation eAnnotation = eAnnotationArray[i];
							if (source.equals(eAnnotation.getSource())) {
								return eAnnotation;
							}
						}
					}
				}
			} else {
				if (source == null) {
					for (EAnnotation eAnnotation : eAnnotations) {
						if (eAnnotation.getSource() == null) {
							return eAnnotation;
						}
					}
				} else {
					for (EAnnotation eAnnotation : eAnnotations) {
						if (source.equals(eAnnotation.getSource())) {
							return eAnnotation;
						}
					}
				}
			}
		}

		return null;
	}

}

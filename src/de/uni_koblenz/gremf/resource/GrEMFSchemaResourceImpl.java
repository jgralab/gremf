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
package de.uni_koblenz.gremf.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.URIHandlerImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

/**
 * Resource implementation for grEMF:<br>
 * Based on the Ecore-style XMI resource implementation this resource
 * implementation deals with all grEMF specifics. Normally, instances are
 * created implicitly via a factory.
 * 
 * @see {@link de.uni_koblenz.gremf.resource.GrEMFResourceFactoryImpl
 *      GrEMFResourceFactoryImpl} (factory for grEMF resources)
 * @see {@link de.uni_koblenz.gremf.resource.GrEMFInstanceLoadImpl
 *      GrEMFLoadImpl} (load support for grEMF resources)
 * 
 */
public class GrEMFSchemaResourceImpl extends XMIResourceImpl {

	public GrEMFSchemaResourceImpl() {
		super();
		this.initDefaultOptions();

	}

	public GrEMFSchemaResourceImpl(URI uri) {
		super(uri);
		this.initDefaultOptions();

	}

	private void initDefaultOptions() {
		super.getDefaultLoadOptions().put(XMLResource.OPTION_RESOURCE_HANDLER,
				new GrEMFSchemaResourceHandler());
		super.getDefaultLoadOptions().put(
				XMLResource.OPTION_USE_DEPRECATED_METHODS, Boolean.TRUE);
		super.setEncoding("UTF-8");

		super.getDefaultSaveOptions().put(
				XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE, Boolean.TRUE);
		super.getDefaultSaveOptions().put(XMLResource.OPTION_LINE_WIDTH, 80);
		super.getDefaultSaveOptions().put(XMLResource.OPTION_URI_HANDLER,
				new URIHandlerImpl.PlatformSchemeAware());

	}

}

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
package de.uni_koblenz.gremf.plugin;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.uni_koblenz.gremf.resource.GrEMFResourceFactoryImpl;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

	// // The plug-in ID
	//	public static final String PLUGIN_ID = "grEMFPlugin"; //$NON-NLS-1$
	//
	// // The shared instance
	// private static Activator plugin;
	//
	// /**
	// * The constructor
	// */
	// public Activator() {
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// *
	// org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	// * )
	// */
	// @Override
	// public void start(BundleContext context) throws Exception {
	// super.start(context);
	// plugin = this;
	//
	// System.out.println("!INFO: start gremf plugin");
	//
	// // enable instance model check
	// Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*",
	// new GrEMFResourceFactoryImpl());
	//
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// *
	// org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	// * )
	// */
	// @Override
	// public void stop(BundleContext context) throws Exception {
	// plugin = null;
	// super.stop(context);
	// }
	//
	// /**
	// * Returns the shared instance
	// *
	// * @return the shared instance
	// */
	// public static Activator getDefault() {
	// return plugin;
	// }
	//
	// /**
	// * Returns an image descriptor for the image file at the given plug-in
	// * relative path
	// *
	// * @param path
	// * the path
	// * @return the image descriptor
	// */
	// public static ImageDescriptor getImageDescriptor(String path) {
	// return imageDescriptorFromPlugin(PLUGIN_ID, path);
	// }

	@Override
	public void start(BundleContext context) throws Exception {
		// enable instance model check
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*",
				new GrEMFResourceFactoryImpl());

	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// disable instance model check
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*",
				new XMIResourceFactoryImpl());

	}
}

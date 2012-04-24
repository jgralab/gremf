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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;

import de.uni_koblenz.gremf.GrEMFSchemaType;
import de.uni_koblenz.gremf.impl.GrEMFEdgeImpl;
import de.uni_koblenz.gremf.impl.GrEMFVertexImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFPackageImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFSchemaImpl;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.Schema;

/**
 * Resource implementation for grEMF:<br>
 * Enables the usage of JGraLab's tg files
 * 
 * @see {@link de.uni_koblenz.gremf.resource.GrEMFResourceFactoryImpl
 *      GrEMFResourceFactoryImpl} (factory for grEMF resources)
 * 
 */
public class TgResourceImpl extends ResourceImpl {

	private Schema schema;

	public TgResourceImpl(URI uri, Schema schema) {
		super(uri);
		this.schema = schema;
	}

	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) {

		// if there is a resource set, make sure the schema is added;
		if (this.getResourceSet() != null) {
			URI schemaURI = URI.createURI(this.uri.trimFileExtension()
					.toString() + "_schema.tg");
			if (this.getResourceSet().getResource(schemaURI, false) == null) {
				TgResourceImpl schema = (TgResourceImpl) this.getResourceSet()
						.createResource(schemaURI);
				schema.getContents().add(
						(EPackage) this.schema.getDefaultPackage());

				schema.getResourceSet()
						.getPackageRegistry()
						.put(((EPackage) this.schema.getDefaultPackage())
								.getNsURI(),
								this.schema.getDefaultPackage());
			}
		}

		try {
			Graph g = GraphIO.loadGraphFromFile(this.uri.path(), this.schema,
					ImplementationType.GENERIC, null);

			this.getContents().add((EObject) g);
			for (Vertex v : g.vertices()) {
				if (((EObject) v).eContainer() == null) {
					this.getContents().add((EObject) v);
				}
			}

		} catch (GraphIOException ex) {
			((GrEMFSchemaImpl) this.schema).setCurrentGraph(null);
			this.getContents().add((EObject) this.schema.getDefaultPackage());

		} finally {
			this.registerPackages(this.schema.getDefaultPackage(),
					this.schema.getDefaultPackage());
		}

	}

	private void registerPackages(Package p, Package defPack) {
		if (defPack instanceof GrEMFSchemaType) {
			EPackage.Registry.INSTANCE.put(((EPackage) p).getNsURI(), p);
		}
		for (Package sub : p.getSubPackages()) {
			this.registerPackages(sub, defPack);
		}
	}

	@Override
	protected void doSave(OutputStream outputStream, Map<?, ?> options)
			throws IOException {
		try {
			EObject topObj = this.getContents().get(0);
			System.err.println("LOG: save " + this + " : " + topObj);
			if (topObj instanceof Graph) {
				GraphIO.saveGraphToStream((Graph) topObj, new DataOutputStream(
						outputStream), null);
				this.setModified(false);
			} else if (topObj instanceof GrEMFVertexImpl) {
				GraphIO.saveGraphToStream(
						((GrEMFVertexImpl) topObj).getGraph(),
						new DataOutputStream(outputStream), null);
				this.setModified(false);
			} else if (topObj instanceof GrEMFEdgeImpl) {
				GraphIO.saveGraphToStream(((GrEMFEdgeImpl) topObj).getGraph(),
						new DataOutputStream(outputStream), null);
				this.setModified(false);
			} else if (topObj instanceof GrEMFPackageImpl) {
				// skip schema
			} else {
				throw new GraphIOException();
			}
		} catch (GraphIOException e) {
			throw new IOException(e.getMessage(), e.getCause());
		}

	}
}

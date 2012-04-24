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

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import de.uni_koblenz.gremf.GrEMFIO;
import de.uni_koblenz.gremf.GrEMFType;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.schema.Schema;

/**
 * Factory for creating grEMF specific resources<br>
 * This factory needs to be registered:
 * 
 * <pre>
 * {@code
 * <extension point="org.eclipse.emf.ecore.extension_parser"> 
 *   <parser type="*" class="de.uni_koblenz.gremf.resource.GrEMFResourceImpl"/> 
 * </extension>
 * }
 * </pre>
 * 
 * or stand-alone<br>
 * 
 * <pre>
 * {@code
 * Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
 *   .put( "*", new GrEMFResourceFactoryImpl());
 * }
 * </pre>
 * 
 * @see {@link de.uni_koblenz.gremf.resource.GrEMFSchemaResourceImpl
 *      GrEMFResourceImpl} (grEMF resource implementation)
 * 
 */
public class GrEMFResourceFactoryImpl extends ResourceFactoryImpl {

	private static String XML_NS_PREFIX = "xml";
	private static String XML_NS_URI = "http://www.w3.org/XML/1998/namespace";
	private static String XMI_NS_PREFIX = "xmi";
	private static String XMI_NS_URI = "http://www.omg.org/XMI";
	private static String XSI_NS_URI = "http://www.w3.org/2001/XMLSchema-instance";
	private static String XSI_SCHEMA_LOCATION = "schemaLocation";

	private enum ResourceType {
		GREMF_SCHEMA, GREMF_INSTANCE, TG, OTHER
	}

	/**
	 * known, i.e. already loaded, schemas mapped to their qualified name
	 */
	Map<String, Schema> knownSchemas;

	@Override
	public Resource createResource(URI uri) {
		URI localURI = CommonPlugin.asLocalURI(uri);

		switch (this.getResourceType(localURI)) {
		case GREMF_SCHEMA:
			return new GrEMFSchemaResourceImpl(localURI);
		case GREMF_INSTANCE:
			return new GrEMFInstanceResourceImpl(localURI);
		case TG:
			Schema schema = this.getSchema(localURI);
			// if (schema != null) { // saving in empty file has no schema but
			// graph knows it
			return new TgResourceImpl(localURI, schema);
			// }

		default:
			return new XMIResourceImpl(uri);
		}
	}

	private ResourceType getResourceType(URI uri) {
		if (uri.fileExtension() == null) {
			return ResourceType.OTHER;
		}

		if (uri.fileExtension().equals("gremf")) {
			return ResourceType.GREMF_SCHEMA;
		} else if (uri.fileExtension().equals("tg")) {
			return ResourceType.TG;
		} else {
			return this.investigateResourceFile(uri);
		}
	}

	/**
	 * Determines the type of the resource: OTHER or GREMF_INSTANCE<br>
	 * Therefore, the file is parsed until the type can be determined
	 * 
	 * @param uri
	 * @return
	 */
	private ResourceType investigateResourceFile(URI uri) {
		ResourceType type = ResourceType.OTHER;

		InputStream in = null;
		XMLStreamReader parser = null;

		try {
			in = new FileInputStream(uri.path());
			parser = XMLInputFactory.newInstance().createXMLStreamReader(in);
			while (parser.hasNext()) {
				if (parser.next() == XMLStreamConstants.START_ELEMENT) {
					if (!XMI_NS_PREFIX.equals(parser.getPrefix())
							&& !XMI_NS_URI.equals(parser.getNamespaceURI())
							&& !XML_NS_PREFIX.equals(parser.getPrefix())
							&& !XML_NS_URI.equals(parser.getNamespaceURI())
							&& (EPackage.Registry.INSTANCE.getEPackage(parser
									.getNamespaceURI()) instanceof GrEMFType)) {
						// current namespace belongs to a grEMF package
						type = ResourceType.GREMF_INSTANCE;
						break;
					}
					if (parser.getAttributeValue(XSI_NS_URI,
							XSI_SCHEMA_LOCATION) != null) {
						String[] parts = parser.getAttributeValue(XSI_NS_URI,
								XSI_SCHEMA_LOCATION).split(" ");
						for (int i = 0; (i + 1) < parts.length; i += 2) {
							if (parts[i + 1].matches("(.*).gremf")
									|| (EPackage.Registry.INSTANCE
											.getEPackage(parts[i]) instanceof GrEMFType)) {
								// schema location refers to grEMF schema
								type = ResourceType.GREMF_INSTANCE;
								break;
							}
						}
						break;
					}
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			close(parser);
			close(in);
		}
		return type;
	}

	private Schema getSchema(URI localURI) {
		try {

			if (this.knownSchemas == null) {
				this.knownSchemas = new HashMap<String, Schema>();
			}

			Schema schema = GrEMFIO.loadSchemaFromFile(localURI.path());
			// if already loaded, use the loaded schema for preserving the
			// object identities
			if (this.knownSchemas.containsKey(schema.getQualifiedName())) {
				schema = this.knownSchemas.get(schema.getQualifiedName());
			} else {
				this.knownSchemas.put(schema.getQualifiedName(), schema);
			}
			return schema;
		} catch (GraphIOException e) {
			e.printStackTrace();
			System.err.println("can not load " + localURI);
			return null;
		}
	}

	private static void close(Object closeable) {
		if (closeable == null) {
			return;
		}

		try {
			if (closeable instanceof Closeable) {
				((Closeable) closeable).close();
			} else if (closeable instanceof XMLStreamReader) {
				((XMLStreamReader) closeable).close();
			}
		} catch (IOException e) {
		} catch (XMLStreamException e) {
		}
	}
}

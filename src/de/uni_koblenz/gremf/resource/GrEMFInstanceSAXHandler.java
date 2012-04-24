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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.SAXXMIHandler;

import de.uni_koblenz.gremf.GrEMFInstanceType;
import de.uni_koblenz.gremf.GrEMFType;
import de.uni_koblenz.gremf.impl.GrEMFEdgeImpl;
import de.uni_koblenz.gremf.impl.GrEMFVertexImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFAttributeImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFEdgeClassImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFIncidenceClassWithRefsImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFVertexClassImpl;
import de.uni_koblenz.jgralab.Edge;

/**
 * Extended SAX handler for grEMF: <br>
 * The EMF mechanism is extended to support and handle all grEMF
 * characteristics, especially edge objects. The creation of these objects is
 * delayed until the very end.
 * 
 * @see {@link de.uni_koblenz.gremf.resource.GrEMFSchemaResourceImpl
 *      GrEMFResourceImpl} (grEMF resource implementation)
 * @see {@link de.uni_koblenz.gremf.resource.GrEMFInstanceLoadImpl
 *      GrEMFLoadImpl} (load support for grEMF resources)
 * 
 */
public class GrEMFInstanceSAXHandler extends SAXXMIHandler {

	/**
	 * Mapping of all occurring objects to an identifier which is only used for
	 * this creation process
	 */
	private Map<String, EObject> objs;

	/**
	 * Mapping of all objects with delayed edge object features to the
	 * identifier of these features.
	 */
	private Map<String, EObject> gaps;

	/**
	 * Mapping of all occurring edge objects to their identifier
	 */
	private Map<String, GrEMFEdgeClassImpl> edges;

	/**
	 * Mapping of all edge XML-attributes to the edge's identifier. The
	 * XML-attributes are stored as name:value pairs (list of a 2-element list)
	 */
	private Map<String, ArrayList<ArrayList<String>>> edgeAttrs;

	/**
	 * Stack of created identifiers which are removed during the object's
	 * creation
	 */
	private Stack<String> objIds;

	/**
	 * Stack of the parent elements' identifiers
	 */
	private Stack<String> parentIds;

	/**
	 * Identifier of the current top level element
	 */
	private String topId;

	/**
	 * Index of the current top level element
	 */
	private int topIndex;

	/**
	 * Indices of all features <i>(note: all XML elements which are not at top
	 * level are structural features)</i>
	 */
	private Map<String, Integer> featureIndices;

	/**
	 * level of parent element's XML-entry
	 */
	private int parentLevel;

	public GrEMFInstanceSAXHandler(XMLResource xmiResource, XMLHelper helper,
			Map<?, ?> options) {
		super(xmiResource, helper, options);
		this.objs = new HashMap<String, EObject>();
		this.gaps = new HashMap<String, EObject>();
		this.edges = new HashMap<String, GrEMFEdgeClassImpl>();
		this.edgeAttrs = new HashMap<String, ArrayList<ArrayList<String>>>();

		this.topIndex = 0;
		this.featureIndices = new HashMap<String, Integer>();

		this.objIds = new Stack<String>();
		this.parentIds = new Stack<String>();
	}

	@Override
	protected void createTopObject(String prefix, String name) {
		this.parentIds.clear();
		this.objIds.clear();

		// create id and push it to the stacks
		String id = createIdForTopElement(this.topIndex++);
		this.objIds.push(id);
		this.parentIds.push(id);

		// set values
		this.topId = id;
		this.parentLevel = this.elements.size();

		super.createTopObject(prefix, name);

	}

	@Override
	protected void handleFeature(String prefix, String name) {
		// pop all old elements (wrong level)
		for (int i = this.parentLevel - this.elements.size(); i >= 0; i--) {
			this.parentIds.pop();
		}

		// create unique feature name
		String featureName = createUniqueNameForFeature(this.parentIds.peek(),
				name);

		// update counter
		if (this.featureIndices.containsKey(featureName)) {
			this.featureIndices.put(featureName,
					this.featureIndices.get(featureName) + 1);
		} else {
			this.featureIndices.put(featureName, 0);
		}

		// set value
		this.parentLevel = this.elements.size();

		// look up feature
		EStructuralFeature feature = null;
		if (this.objects.peek() != null) {
			feature = this.objects.peek().eClass().getEStructuralFeature(name);
		}

		if ((feature != null) && !(feature instanceof GrEMFType)
				&& (feature instanceof EReference)) {
			this.handleToEdgeClassReference(feature, featureName);
		} else if ((feature == null)
				&& this.edges.containsKey(this.parentIds.peek())) {
			this.handleFromEdgeClassReference(name);

		} else {
			// create id
			String id = createIdForFeature(featureName,
					this.featureIndices.get(featureName),
					this.getFeature(this.objects.peek(), prefix, name, true)
							.getUpperBound() != 1);

			// push id
			this.objIds.push(id);
			this.parentIds.push(id);

			super.handleFeature(prefix, name);
		}
	}

	/**
	 * @param feature
	 */
	private void handleToEdgeClassReference(EStructuralFeature feature,
			String uniqueFeatureName) {
		// reference to an edge class
		GrEMFEdgeClassImpl edgeCls = (GrEMFEdgeClassImpl) feature.getEType();

		// create edge id
		String edgeId = createIdForFeature(this.parentIds.peek(),
				edgeCls.getName(), this.featureIndices.get(uniqueFeatureName),
				feature.getUpperBound() != 1);

		// get vertex id
		String vertexId = this.parentIds.peek();

		// handle reference of the parent object
		this.setValueFromId(this.objects.peek(), (EReference) edgeCls.getTo(),
				edgeId);

		// "create" edge
		this.objIds.push(edgeId);
		this.parentIds.push(edgeId);
		this.createGrEMFObject(edgeCls.getEPackage().getEFactoryInstance(),
				edgeCls, false);

		// handle this feature
		this.objects.push(null);
		this.types.push(OBJECT_TYPE);

		// add edge attribute
		ArrayList<String> valuePair = new ArrayList<String>(2);
		valuePair.add(((GrEMFIncidenceClassWithRefsImpl) edgeCls.getFrom())
				.getFromEdgeClass().getName());
		valuePair.add(vertexId);
		this.edgeAttrs.get(edgeId).add(valuePair);

	}

	/**
	 * @param name
	 */
	private void handleFromEdgeClassReference(String name) {
		// reference from an edge class to a vertex class
		GrEMFEdgeClassImpl edgeCls = this.edges.get(this.parentIds.peek());
		GrEMFVertexClassImpl vertexCls = (GrEMFVertexClassImpl) edgeCls
				.getEStructuralFeature(name).getEType();

		// get edge id
		String edgeId = this.parentIds.peek();

		// create vertex id
		String vertexId = createIdForFeature(this.parentIds.peek(),
				vertexCls.getName());

		// create vertex
		this.objIds.push(vertexId);
		this.parentIds.push(vertexId);
		this.processObject(this.createGrEMFObject(vertexCls.getEPackage()
				.getEFactoryInstance(), vertexCls, false));

		// handle reference of the vertex
		this.setValueFromId(this.objects.peek(),
				(EReference) edgeCls.getFrom(), edgeId);

		// add edge attribute
		ArrayList<String> valuePair = new ArrayList<String>(2);
		valuePair.add(((GrEMFIncidenceClassWithRefsImpl) edgeCls.getTo())
				.getFromEdgeClass().getName());
		valuePair.add(vertexId);
		this.edgeAttrs.get(edgeId).add(valuePair);

	}

	@Override
	protected void handleProxy(InternalEObject proxy, String uriLiteral) {
		// unite the objects in one resource
		if (proxy instanceof GrEMFInstanceType) {
			if (this.xmlResource.getContents().contains(proxy)) {
				this.xmlResource.getContents().add(proxy);
			}
		} else {
			super.handleProxy(proxy, uriLiteral);
		}
	}

	@Override
	protected EObject createObject(EFactory factory, EClassifier type,
			boolean documentRoot) {
		if (factory instanceof GrEMFType) {
			return this.createGrEMFObject(factory, type, documentRoot);
		} else {
			return super.createObject(factory, type, documentRoot);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected EObject createObjectFromFactory(EFactory factory, String typeName) {
		if (factory instanceof GrEMFType) {
			return this.createGrEMFObject(factory, factory.getEPackage()
					.getEClassifier(typeName), false);

		} else {
			return super.createObjectFromFactory(factory, typeName);
		}

	}

	private EObject createGrEMFObject(EFactory factory, EClassifier type,
			boolean documentRoot) {

		// get the object's id
		String objId = this.objIds.isEmpty() ? null : this.objIds.pop();
		if (objId == null) {
			System.err.println(this.parentIds.peek());
			System.err.println(this.objects);
			System.err.println(this.elements);
			System.exit(-1);
		}

		if ((objId != null) && this.objs.containsKey(objId)) {
			if (this.attribs != null) {
				this.handleObjectAttribs(this.objs.get(objId));
			}
			return this.objs.get(objId);
		} else {
			EObject newEObj;
			if (type instanceof GrEMFEdgeClassImpl) {
				// delaying edges
				this.edges.put(objId, (GrEMFEdgeClassImpl) type);

				// save attributes as localName:value pairs
				ArrayList<ArrayList<String>> attrs = new ArrayList<ArrayList<String>>(
						this.attribs.getLength());
				for (int i = 0; i < this.attribs.getLength(); i++) {
					ArrayList<String> valuePair = new ArrayList<String>(2);
					valuePair.add(this.attribs.getLocalName(i));
					valuePair.add(this.attribs.getValue(i));
					attrs.add(valuePair);
				}
				this.edgeAttrs.put(objId, attrs);
				newEObj = null;
			} else {
				newEObj = super.createObject(factory, type, documentRoot);
			}
			this.objs.put(objId, newEObj);
			return newEObj;
		}
	}

	@Override
	protected EStructuralFeature getFeature(EObject object, String prefix,
			String name, boolean isElement) {
		// look up
		EStructuralFeature feature = object.eClass()
				.getEStructuralFeature(name);

		if (feature instanceof GrEMFType) {
			// a valid grEMF feature (a GrEMFType and not null)
			return super.getFeature(object, prefix, name, isElement);
		} else {
			// use grEMF prefix
			return super.getFeature(object, prefix, "grEMF_" + name, isElement);
		}
	}

	@Override
	protected void setValueFromId(EObject object, EReference eReference,
			String ids) {
		if (object instanceof GrEMFInstanceType) {
			StringTokenizer tokenizer = new StringTokenizer(ids, " ");

			if (eReference instanceof GrEMFIncidenceClassWithRefsImpl) {
				while (tokenizer.hasMoreTokens()) {
					String id = decodeXMLId(this.topId, tokenizer.nextToken());
					id = createIdForFeature(id,
							((GrEMFIncidenceClassWithRefsImpl) eReference)
									.getFromEdgeClass().getName());
					this.gaps.put(id, object);
					this.objs.put(id, null);
				}

			} else {
				String type = "";
				while (tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken();
					if (token.indexOf(':') >= 0) {
						type = token + " ";
						if (tokenizer.hasMoreTokens()) {
							token = tokenizer.nextToken();
						}
					}
					token = type + decodeXMLId(this.topId, token);
					this.objIds.push(token);
					super.setValueFromId(object, eReference, token);
				}
			}
		} else {
			super.setValueFromId(object, eReference, ids);
		}

	}

	@Override
	protected void validateCreateObjectFromFactory(EFactory factory,
			String typeName, EObject newObject) {
		// no validation (null check) for grEMF types
		if (!(factory instanceof GrEMFType)) {
			super.validateCreateObjectFromFactory(factory, typeName, newObject);
		}
	}

	@Override
	public void endDocument() {
		// handle all edges
		for (String id : this.edges.keySet()) {
			this.handleEdge(this.edges.get(id), id);
		}
		super.endDocument();

	}

	/**
	 * Handles a edge: <br>
	 * The edge is set indirectly via the special grEMF references.
	 * 
	 * @param edgeCls
	 *            type of the edge
	 * @param edgeId
	 *            identifier of the edge
	 */
	private void handleEdge(GrEMFEdgeClassImpl edgeCls, String edgeId) {
		// get the incidence classes
		GrEMFIncidenceClassWithRefsImpl fromInc = (GrEMFIncidenceClassWithRefsImpl) edgeCls
				.getFrom();
		GrEMFIncidenceClassWithRefsImpl toInc = (GrEMFIncidenceClassWithRefsImpl) edgeCls
				.getTo();

		// get the attributes
		ArrayList<ArrayList<String>> attrs = this.edgeAttrs.get(edgeId);

		// create the identifiers
		String sourceId = createIdForFeature(edgeId, fromInc.getFromEdgeClass()
				.getName());
		String targetId = createIdForFeature(edgeId, toInc.getFromEdgeClass()
				.getName());

		GrEMFVertexImpl sourceObj = null;
		GrEMFVertexImpl targetObj = null;

		// get the connected vertices using the stored attributes
		for (int i = 0, size = attrs.size(); i < size; i++) {
			ArrayList<String> attr = attrs.get(i);
			EStructuralFeature feature = edgeCls.getEStructuralFeature(attr
					.get(0));

			if (feature.equals(fromInc.getFromEdgeClass())) {
				sourceObj = (GrEMFVertexImpl) this.objs.get(decodeXMLId(attr
						.get(1)));
			} else if (feature.equals(toInc.getFromEdgeClass())) {
				targetObj = (GrEMFVertexImpl) this.objs.get(decodeXMLId(attr
						.get(1)));
			}
		}

		// one object must exist
		if ((sourceObj == null) && (targetObj == null)) {
			throw new RuntimeException();
		}

		// register the connected vertices
		this.objs.put(sourceId, sourceObj);
		this.objs.put(targetId, targetObj);

		// "fill the gaps": create the edge indirectly (only once!)
		if (sourceObj != null) {
			this.fillGap(fromInc, sourceId);
		} else {
			this.fillGap(toInc, targetId);
		}

		// get edge object
		GrEMFEdgeImpl edge = null;
		if (sourceObj != null) {
			edge = this.getEdgeObject(edgeCls, sourceId);
		} else {
			edge = this.getEdgeObject(edgeCls, targetId);
		}

		// add all "real" attributes to the edge
		for (int i = 0, size = attrs.size(); i < size; i++) {
			EStructuralFeature feature = edgeCls.getEStructuralFeature(attrs
					.get(i).get(0));
			if (feature instanceof GrEMFAttributeImpl) {
				super.setAttribValue(edge, feature.getName(),
						attrs.get(i).get(1));
			}
		}

		// register edge
		this.objs.put(edgeId, edge);

		// clear gaps
		if (this.gaps.containsKey(sourceId)) {
			this.gaps.remove(sourceId);
		}
		if (this.gaps.containsKey(targetId)) {
			this.gaps.remove(targetId);
		}

		this.processTopObject(edge);
	}

	/**
	 * Gets the created edge object: <br>
	 * The "this" vertex is taken from the
	 * {@link de.uni_koblenz.gremf.resource.GrEMFInstanceSAXHandler#objs objs}
	 * map using the given id. The "that" vertex is taken from the
	 * {@link de.uni_koblenz.gremf.resource.GrEMFInstanceSAXHandler#gaps gaps}
	 * map using the given id.
	 * 
	 * @param edgeCls
	 *            type of the edge
	 * @param thisId
	 *            identifier used to determine "this" and "that"
	 * @return edge of type <code>edgeCls</code> that matches with "this" and
	 *         "that"
	 */
	private GrEMFEdgeImpl getEdgeObject(GrEMFEdgeClassImpl edgeCls,
			String thisId) {
		for (Edge e : ((GrEMFVertexImpl) this.objs.get(thisId))
				.incidences(edgeCls)) {
			// that matches and edge is not registered
			if (e.getThat().equals(this.gaps.get(thisId))
					&& !this.objs.containsValue(e)) {
				return (GrEMFEdgeImpl) e;
			}
		}
		return null;
	}

	/**
	 * Fills the gap with given id as key in the
	 * {@link de.uni_koblenz.gremf.resource.GrEMFInstanceSAXHandler#gaps gaps}
	 * map: <br>
	 * The reference of the object with the gap is set to the the object related
	 * to the given id in the
	 * {@link de.uni_koblenz.gremf.resource.GrEMFInstanceSAXHandler#objs objs}
	 * map.
	 * 
	 * @param ref
	 *            reference that is set
	 * @param id
	 *            identifier used to determine both objects
	 */
	private void fillGap(EReference ref, String id) {
		if (this.gaps.containsKey(id)) {
			this.setFeatureValue(this.gaps.get(id), ref, this.objs.get(id));
		}
	}

	/**
	 * Creates an identifier for a top element.
	 * 
	 * @param topIndex
	 *            index of this top element
	 * 
	 * @return #/ + [index of the top element]
	 */
	private static String createIdForTopElement(int topIndex) {
		return "gr#/" + topIndex;
	}

	/**
	 * Creates an identifier for a feature: <i>(note: all XML elements which are
	 * not at top level are structural features)</i> <br>
	 * Therefore, this call is delegated to
	 * {@link de.uni_koblenz.gremf.resource.GrEMFInstanceSAXHandler#createIdForFeature(String, String, int, boolean)
	 * createIdForFeature(String, String, int, boolean)} with default values for
	 * the additional parameters.
	 * 
	 * @param parentId
	 *            parent element's identifier
	 * @param name
	 *            (simple) feature name
	 * @return [unqiue simple name]
	 */
	private static String createIdForFeature(String parentId, String name) {
		return createIdForFeature(parentId, name, 0, false);
	}

	/**
	 * Creates an identifier for a feature: <i>(note: all XML elements which are
	 * not at top level are structural features)</i> <br>
	 * Therefore, this call is delegated to
	 * {@link de.uni_koblenz.gremf.resource.GrEMFInstanceSAXHandler#createIdFromUniqueName(String, int, boolean)
	 * createIdFromUniqueName(String, int, boolean)}.
	 * 
	 * @param uniqueName
	 *            (simple) feature name
	 * @param featureIndex
	 *            index of the current feature
	 * @param listFeature
	 *            true if the feature is a list feature; false otherwise
	 * @return [unique name] + . + [index of the feature], if
	 *         <code>listFeature</code> <br>
	 *         [unqiue simple name], otherwise
	 */
	private static String createIdForFeature(String uniqueName,
			int featureIndex, boolean listFeature) {
		return createIdFromUniqueName(uniqueName, featureIndex, listFeature);
	}

	/**
	 * Creates an identifier for a feature: <i>(note: all XML elements which are
	 * not at top level are structural features)</i> <br>
	 * Therefore, this call is delegated to
	 * {@link de.uni_koblenz.gremf.resource.GrEMFInstanceSAXHandler#createIdFromUniqueName(String, int, boolean)
	 * createIdFromUniqueName(String, int, boolean)} with a unique feature name
	 * created from the parent element's identifier and the simple name.
	 * 
	 * @param parentId
	 *            parent element's identifier
	 * @param name
	 *            (simple) feature name
	 * @param featureIndex
	 *            index of the current feature
	 * @param listFeature
	 *            true if the feature is a list feature; false otherwise
	 * @return [unique name] + . + [index of the feature], if
	 *         <code>listFeature</code> <br>
	 *         [unqiue simple name], otherwise
	 * @see {@link de.uni_koblenz.gremf.resource.GrEMFInstanceSAXHandler#createUniqueNameForFeature(String, String)
	 *      createUniqueNameForFeature(String, String)}
	 */
	private static String createIdForFeature(String parentId, String name,
			int featureIndex, boolean listFeature) {
		return createIdFromUniqueName(
				createUniqueNameForFeature(parentId, name), featureIndex,
				listFeature);
	}

	/**
	 * Creates an identifier from a unique name.
	 * 
	 * @param uName
	 *            unique name
	 * @param featureIndex
	 *            index of the current feature
	 * @param listFeature
	 *            true if the feature is a list feature; false otherwise
	 * @return [unique name] + . + [index of the feature], if
	 *         <code>listFeature</code> <br>
	 *         [unique name], otherwise
	 * @see {@link de.uni_koblenz.gremf.resource.GrEMFInstanceSAXHandler#createUniqueNameForFeature(String, String)
	 *      createUniqueNameForFeature(String, String)}
	 * 
	 */
	private static String createIdFromUniqueName(String uName,
			int featureIndex, boolean listFeature) {
		if (listFeature) {
			return uName + '.' + featureIndex;
		} else {
			return uName;
		}
	}

	/**
	 * Creates an unique name for a feature: <i>(note: all XML elements which
	 * are not at top level are structural features)</i> <br>
	 * 
	 * @param parentId
	 *            identifier of the parent element
	 * @param name
	 *            (simple) feature name
	 * @return [parent element's identifier] + /@ + [simple name]
	 */
	private static String createUniqueNameForFeature(String parentId,
			String name) {
		return parentId + "/@" + name;
	}

	/**
	 * Decodes an xml identifier:<br>
	 * Therefore, this call is delegated to
	 * {@link de.uni_koblenz.gremf.resource.GrEMFInstanceSAXHandler#decodeXMLId(Resource, String, String)
	 * createIdForFeature(Resource, String, String)} with null as values for the
	 * additional parameter.
	 * 
	 * @param resourceURI
	 *            uri of the containing resource
	 * @param id
	 *            xml identifier
	 * @return decoded identifier
	 */
	private static String decodeXMLId(String id) {
		return decodeXMLId(null, id);
	}

	/**
	 * Decodes an xml identifier:<br>
	 * The path is added to fragmented identifiers using either the resource or
	 * the top identifier. Full identifiers are not changed.
	 * 
	 * @param resourceURI
	 *            uri of the containing resource
	 * @param topId
	 *            identifier of the top element
	 * @param id
	 *            xml identifier
	 * @return decoded identifier
	 */
	private static String decodeXMLId(String topId, String id) {
		// handle different kinds of identifiers
		if (id.length() > 1) {
			char c0 = id.charAt(0);
			char c1 = id.charAt(1);
			if ((c0 == '/') && (c1 == '/')) {
				// "//identifier"
				return topId + id.substring(1);
			} else if ((c0 == '/') && Character.isDigit(c1)) {
				// "/0"
				return "gr#" + id;
			} else if ((c0 == '#') && (c1 == '/')) {
				// "#/identifier"
				return "gr" + id;
			} else if ((id.length() > 2) && (c0 == 'g') && (c1 == 'r')
					&& (id.charAt(2) == '#')) {
				return id;
			}
		}
		// handle other qualified identifier
		return "gr" + id.substring(id.indexOf('#'));
	}
}

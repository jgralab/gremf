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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.notify.impl.NotificationChainImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;

import de.uni_koblenz.gremf.exception.SchemaLoadException;
import de.uni_koblenz.gremf.schema.impl.GrEMFAttributeImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFEdgeClassImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFEnumDomainImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFGraphClassImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFIncidenceClassImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFIncidenceClassWithRefsImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFPackageImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFRefEdgeClassImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFSchemaImpl;
import de.uni_koblenz.gremf.schema.impl.GrEMFVertexClassImpl;
import de.uni_koblenz.jgralab.schema.AggregationKind;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * Part of the resource handler for loading EMF schemas into grEMF<br>
 * The EMF types are transformed into the corresponding grEMF types.
 * 
 * @see {@link de.uni_koblenz.gremf.resource.GrEMFSchemaResourceHandler
 *      GrEMFResourceHandler} (handler for grEMF resources)
 * 
 */
public class GrEMFSchemaTransformer {

	private Resource resource;

	private GrEMFSchemaImpl schema;
	private GrEMFGraphClassImpl graph;

	// marker sets
	private Set<EClass> vertexClasses;
	private Set<EClass> edgeClasses;
	private Set<EEnum> enumDomains;
	private Set<EAttribute> attributes;
	private Set<EReference> incidenceClasses;
	private Set<EPackage> packages;

	/**
	 * ascending order of the vertex class by the number of all supertypes
	 */
	private EClass[] vClassOrder;

	/**
	 * ascending order of the edge class by the number of all supertypes
	 */
	private EClass[] eClassOrder;

	GrEMFSchemaTransformer(Resource resource) {
		this.resource = resource;

		// init sets
		this.vertexClasses = new HashSet<EClass>();
		this.edgeClasses = new HashSet<EClass>();
		this.enumDomains = new HashSet<EEnum>();
		this.attributes = new HashSet<EAttribute>();
		this.incidenceClasses = new HashSet<EReference>();
		this.packages = new HashSet<EPackage>();

		// gather all associated content
		this.resolveAllContent();

		// init graph
		this.schema = new GrEMFSchemaImpl("Schema",
				createSchemaPackagePrefix(resource));
		this.graph = this.schema.createGraphClass("Graph"
				+ Character.toUpperCase(this.schema.getPackagePrefix()
						.charAt(0))
				+ this.schema.getPackagePrefix().substring(1));

	}

	/**
	 * Resolves all contents by visiting all object of involved resources. This
	 * includes proxy objects as well. If the resource of a proxy object is not
	 * present, it is tried to load the resource file via its URI.
	 */
	private void resolveAllContent() {
		this.resolveResourceContent(this.resource);
		for (EObject proxyObj : EcoreUtil.ProxyCrossReferencer.find(
				this.resource).keySet()) {

			String otherResourceURI = ((InternalEObject) proxyObj).eProxyURI()
					.path();

			// ignore the "default" or the same resource
			if (otherResourceURI.equals(this.resource.getURI().path())
					|| otherResourceURI.equals(EcorePackage.eINSTANCE
							.eResource().getURI().path())) {
				continue;
			}

			// lookup in resource set
			Resource otherResource = this.resource.getResourceSet()
					.getResource(URI.createURI(otherResourceURI), false);

			// or try to get the resource manually
			if (otherResource == null) {
				File proxySchema = new File(otherResourceURI);
				if (proxySchema.exists()) {
					otherResource = this.resource.getResourceSet()
							.createResource(
									URI.createURI(proxySchema.getPath()));
				} else {
					throw new SchemaLoadException(otherResource);
				}
			}

			// load normally: without specific ResourceHandler
			Map<Object, Object> options = ((GrEMFSchemaResourceImpl) otherResource)
					.getDefaultLoadOptions();
			((GrEMFSchemaResourceImpl) otherResource).getDefaultLoadOptions()
					.clear();

			// try to load the resource
			try {
				otherResource.load(Collections.EMPTY_MAP);
			} catch (IOException e) {
				throw new SchemaLoadException(otherResource);
			}
			((GrEMFSchemaResourceImpl) otherResource).getDefaultLoadOptions()
					.putAll(options);

			this.resolveResourceContent(otherResource);
		}

		// build class orders
		this.vClassOrder = buildOrder(this.vertexClasses);
		this.eClassOrder = buildOrder(this.edgeClasses);
	}

	/**
	 * Resolves the grEMF types of the contained EMF types of the given
	 * resource. Therefore, all contained elements are visited and added to the
	 * corresponding sets. Additionally, the transformation order of the vertex
	 * and edge classes is built. <br>
	 * Note that not all resolved edge classes necessarily are transformed to
	 * such type. Some may become vertex classes.
	 * 
	 * @param resource
	 *            input <code>Resource</code> whose content is resolved
	 */
	private void resolveResourceContent(Resource resource) {
		TreeIterator<EObject> i = resource.getAllContents();
		while (i.hasNext()) {
			EObject eObj = i.next();
			if (eObj instanceof EPackage) {
				// implicitly created via EClass and EEnum
				this.packages.add((EPackage) eObj);
			} else if (eObj instanceof EClass) {
				if (isVertexClassCertainly((EClass) eObj)) {
					this.vertexClasses.add((EClass) eObj);
				} else {
					this.edgeClasses.add((EClass) eObj);
				}
			} else if (eObj instanceof EEnum) {
				this.enumDomains.add((EEnum) eObj);
			} else if (eObj instanceof EAttribute) {
				EAttribute eAttr = (EAttribute) eObj;
				if (FeatureMapUtil.isFeatureMap(eAttr)) {
					this.resolveFeatureMap(new BasicFeatureMap(
							(InternalEObject) eAttr.getEContainingClass(),
							eAttr.getFeatureID()));
				} else {
					this.attributes.add(eAttr);
				}
			} else if (eObj instanceof EReference) {
				if (!this.incidenceClasses.contains(((EReference) eObj)
						.getEOpposite())) {
					this.incidenceClasses.add((EReference) eObj);
				}
			} else if (eObj instanceof EAnnotation) {
				// nothing to do; done via the annotated element
			}
		}

	}

	/**
	 * Resolves EMF's feature map as single features
	 * 
	 * @param map
	 *            feature map
	 */
	private void resolveFeatureMap(FeatureMap map) {
		for (FeatureMap.Entry e : map) {
			EStructuralFeature feature = e.getEStructuralFeature();
			if (feature instanceof EReference) {
				if (!this.incidenceClasses.contains(((EReference) feature)
						.getEOpposite())) {
					this.incidenceClasses.add((EReference) feature);
				}
			} else {
				this.attributes.add((EAttribute) feature);

			}
		}

	}

	/**
	 * Transforms the resolved EMF types to their corresponding grEMF types.
	 * 
	 * @see {@link #resolveAllContent}, {@link #resolveResourceContent}
	 */
	void transform() {
		for (EClass eCls : this.vClassOrder) {
			this.extractVertexClass(eCls);
		}
		for (EClass eCls : this.eClassOrder) {
			this.extractEdgeClass(eCls);
		}
		for (EEnum eEnum : this.enumDomains) {
			this.extractEnumDomain(eEnum);
		}
		for (EReference eRef : this.incidenceClasses) {
			this.extractIncidenceClass(eRef);
		}
		for (EAttribute eAttr : this.attributes) {
			this.extractAttributes(eAttr);
		}
		for (EPackage ePkg : this.packages) {
			this.extractPackages(ePkg);
		}
	}

	/**
	 * Finishes the transformation by: <br>
	 * <ul>
	 * <li>finishing the schema</li>
	 * <li>clearing the resource</li>
	 * <li>adding the transformed content to the resource</li>
	 * </ul>
	 * and
	 * <ul>
	 * <li>clearing all collections</li>
	 * </ul>
	 */
	void finish() {
		// get root package
		GrEMFPackageImpl rootPkg = (GrEMFPackageImpl) this.schema
				.getDefaultPackage();

		// handle root (=default) package
		rootPkg.setNsPrefix(this.schema.getPackagePrefix());
		rootPkg.setNsURI("http://" + rootPkg.getNsPrefix() + ".com");

		// register package
		EPackage.Registry.INSTANCE.put(rootPkg.getNsURI(), rootPkg);

		// modify resource
		this.resource.getContents().clear();
		this.resource.getContents().add(rootPkg);

		// tell package that it is in a resource
		rootPkg.eSetResource((Resource.Internal) this.resource,
				new NotificationChainImpl());

		// register package in resource set
		this.resource.getResourceSet().getPackageRegistry()
				.put(rootPkg.getNsURI(), rootPkg);

		// finish schema
		this.schema.finish();
	}

	private void extractPackages(EPackage ePkg) {

		GrEMFPackageImpl p = (GrEMFPackageImpl) this.schema
				.getPackage(getPackageName(ePkg));

		// copy all other properties
		copyProperties(ePkg, p);

		// add comments
		copyAnnotations(ePkg, p);

		// register package
		EPackage.Registry.INSTANCE.put(p.getNsURI(), p);

		if (this.resource.getResourceSet() != null) {
			this.resource.getResourceSet().getPackageRegistry()
					.put(p.getNsURI(), p);
		}
	}

	/**
	 * Extracts the grEMF <code>GrEMFVertexClassImpl</code> using the given EMF
	 * <code>EClass</code>.
	 * 
	 * Note that supertypes are extracted first.
	 * 
	 * <dt><b>Preconditions:</b>
	 * <dd>
	 * <code>this.isVertexClass(eCls) == true</code></dd></dt>
	 * 
	 * @param eCls
	 *            input <code>EClass</code>
	 */
	private void extractVertexClass(EClass eCls) {
		// get package prefix
		String pkgPrefix = getPackagePrefix(eCls);

		// create vertex class
		GrEMFVertexClassImpl vertexCls = this.graph
				.createVertexClass(createQualifiedName(pkgPrefix,
						eCls.getName()));

		// add supertypes
		for (EClass supertype : eCls.getESuperTypes()) {
			// supertype is already extracted
			vertexCls.addSuperClass(this.getVertexClass(supertype));
		}

		// copy all other properties
		copyProperties(eCls, vertexCls);

		// add comments
		copyAnnotations(eCls, vertexCls);
	}

	/**
	 * Extracts the grEMF <code>GrEMFEnumDomainImpl</code> using the given EMF
	 * <code>EEnum</code>.
	 * 
	 * @param eEnum
	 *            input <code>EEnum</code>
	 */
	private void extractEnumDomain(EEnum eEnum) {
		// get package
		String pkgPrefix = getPackagePrefix(eEnum);

		// create enum domain
		GrEMFEnumDomainImpl enumDom = this.schema
				.createEnumDomain(createQualifiedName(pkgPrefix,
						eEnum.getName()));

		// add literals
		for (EEnumLiteral eEnumLit : eEnum.getELiterals()) {
			enumDom.addConst(eEnumLit.getLiteral());
		}

		// copy attributes
		copyProperties(eEnum, enumDom);

		// add comments
		copyAnnotations(eEnum, enumDom);

	}

	/**
	 * Extracts the grEMF <code>GrEMFIncidenceClassImpl</code> using the given
	 * EMF <code>EReference</code>.
	 * 
	 * @param eRef
	 *            input <code>EReference</code>
	 */
	private void extractIncidenceClass(EReference eRef) {
		// scenario: v1-->v2

		// get vertex classes from this reference
		GrEMFVertexClassImpl vertexCls1 = this.getVertexClass(eRef
				.getEContainingClass());
		GrEMFVertexClassImpl vertexCls2 = this.getVertexClass(eRef
				.getEReferenceType());

		// get rolenames
		// v1-->v2, v1<--v2
		String v1RoleName = getRoleName(eRef.getEOpposite(),
				eRef.getEContainingClass(), eRef);
		// v1-->v2
		String v2RoleName = getRoleName(eRef, eRef.getEReferenceType(), eRef);

		// get aggregation kinds
		// v1-->v2, v1<-<>v2: eRef.isContainer() => v1:COMPOSITE
		AggregationKind v1Aggr = eRef.isContainer() ? AggregationKind.COMPOSITE
				: AggregationKind.NONE;
		// v1<>->v2: eRef.isContainment() => v2:COMPOSITE
		AggregationKind v2Aggr = eRef.isContainment() ? AggregationKind.COMPOSITE
				: AggregationKind.NONE;

		// get multiplicities
		int v1Min = getMinMultiplicity(eRef.getEOpposite());
		int v2Min = getMinMultiplicity(eRef);

		int v1Max = getMaxMultiplicity(eRef.getEOpposite(), v2Aggr);
		int v2Max = getMaxMultiplicity(eRef, v1Aggr);

		// required property
		if (eRef.isRequired() && (v1Min < 1)) {
			v1Min = 1;
		}
		if ((eRef.getEOpposite() != null) && eRef.getEOpposite().isRequired()
				&& (v2Min < 1)) {
			v2Min = 1;
		}

		GrEMFRefEdgeClassImpl edgeCls;
		if ((eRef.getEOpposite() == null)
				|| ((v1Aggr != AggregationKind.COMPOSITE) && ((v2Aggr == AggregationKind.COMPOSITE) || (vertexCls1
						.getName().compareTo(vertexCls2.getName()) <= 0)))) {
			edgeCls = this.createRefEdgeClass(vertexCls1, v1RoleName, v1Aggr,
					v1Min, v1Max, vertexCls2, v2RoleName, v2Aggr, v2Min, v2Max);

			processIncidenceClass((GrEMFIncidenceClassImpl) edgeCls.getFrom(),
					eRef.getEOpposite());
		} else {
			edgeCls = this.createRefEdgeClass(vertexCls2, v2RoleName, v2Aggr,
					v2Min, v2Max, vertexCls1, v1RoleName, v1Aggr, v1Min, v1Max);

			processIncidenceClass((GrEMFIncidenceClassImpl) edgeCls.getTo(),
					eRef.getEOpposite());

		}

		// copy other properties
		copyProperties(eRef, (GrEMFIncidenceClassImpl) edgeCls.getTo());
		copyProperties(eRef.getEOpposite(),
				(GrEMFIncidenceClassImpl) edgeCls.getFrom());

		// add comments
		copyAnnotations(eRef, (GrEMFIncidenceClassImpl) edgeCls.getTo());
		copyAnnotations(eRef.getEOpposite(),
				(GrEMFIncidenceClassImpl) edgeCls.getFrom());

	}

	/**
	 * Tries to extract the grEMF <code>GrEMFEdgeClassImpl</code> using the
	 * given EMF <code>EClass</code>. If it fails, a
	 * <code>GrEMFVertexClassImpl</code> is extracted. Note that the supertypes
	 * are extracted first.
	 * 
	 * <dt><b>Preconditions:</b>
	 * <dd>
	 * <code>this.isVertexClass(eCls) == false</code></dd></dt>
	 * 
	 * @param eCls
	 *            input <code>EClass</code>
	 */
	private void extractEdgeClass(EClass eCls) {
		// get the relevant references
		EReference[] edgeClsOuts = new EReference[2];
		EReference[] edgeClsIns = new EReference[2];
		int i = 0;
		for (EReference eRef : eCls.getEReferences()) {
			if ((eRef.getLowerBound() == 1) && (eRef.getUpperBound() == 1)) {
				edgeClsOuts[i] = eRef;
				edgeClsIns[i++] = eRef.getEOpposite();
			}
		}

		// get vertex classes from the end references
		GrEMFVertexClassImpl vertexCls1 = this.getVertexClass(edgeClsOuts[0]
				.getEReferenceType());
		GrEMFVertexClassImpl vertexCls2 = this.getVertexClass(edgeClsOuts[1]
				.getEReferenceType());

		// get rolenames
		String v1RoleName = "grEMF_"
				+ getRoleName(edgeClsIns[1], vertexCls1, edgeClsIns[0]);
		String v2RoleName = "grEMF_"
				+ getRoleName(edgeClsIns[0], vertexCls2, edgeClsIns[1]);

		// get aggregation kinds
		AggregationKind v1Aggr = getAggregationKind(edgeClsIns[1],
				edgeClsOuts[0]);
		AggregationKind v2Aggr = getAggregationKind(edgeClsIns[0],
				edgeClsOuts[1]);

		// at least one edge must be of type NONE
		if ((v1Aggr != AggregationKind.NONE)
				&& (v2Aggr != AggregationKind.NONE)) {
			// this is not an edge class
			this.extractVertexClass(eCls);
			return;
		}

		// get multiplicity
		int v1Min = getMinMultiplicity(edgeClsIns[1]);
		int v2Min = getMinMultiplicity(edgeClsIns[0]);

		int v1Max = getMaxMultiplicity(edgeClsIns[1], v2Aggr);
		int v2Max = getMaxMultiplicity(edgeClsIns[0], v1Aggr);

		// qualified name of this edge class
		String qualifiedName = getPackagePrefix(eCls) + "." + eCls.getName();

		// supertypes and subtypes must be compatible edge classes
		// check supertypes
		for (EClass supertype : eCls.getESuperTypes()) {// get package
			Package pkg = this.schema.getPackage(getPackagePrefix(supertype));
			if (pkg == null) {
				// error
			} else if (pkg.getEdgeClass(supertype.getName()) == null) {
				// this is not an edge class
				this.extractVertexClass(eCls);
				return;
			} else {
				// supertype is an edge class
				EdgeClass sEdgeCls = pkg.getEdgeClass(supertype.getName());
				if (!this.isCompatibleEdgeClass(vertexCls1, vertexCls2,
						sEdgeCls)) {
					// this is not an edge class
					this.extractVertexClass(eCls);
					return;
				}

			}

		}

		GrEMFEdgeClassImpl edgeCls;
		if ((v1Aggr != AggregationKind.COMPOSITE)
				&& ((v2Aggr == AggregationKind.COMPOSITE) || (vertexCls1
						.getName().compareTo(vertexCls2.getName()) <= 0))) {
			// v1 == from, v2 == to
			edgeCls = this.createEdgeClass(qualifiedName, vertexCls1,
					v1RoleName, v1Aggr, v1Min, v1Max, vertexCls2, v2RoleName,
					v2Aggr, v2Min, v2Max, edgeClsOuts, edgeClsIns, 0);

			processIncidenceClass((GrEMFIncidenceClassImpl) edgeCls.getFrom(),
					edgeClsIns[1]);

			processIncidenceClass((GrEMFIncidenceClassImpl) edgeCls.getTo(),
					edgeClsIns[0]);

		} else {
			// v2 == from, v1 == to
			edgeCls = this.createEdgeClass(qualifiedName, vertexCls2,
					v2RoleName, v2Aggr, v2Min, v2Max, vertexCls1, v1RoleName,
					v1Aggr, v1Min, v1Max, edgeClsOuts, edgeClsIns, 1);

			processIncidenceClass((GrEMFIncidenceClassImpl) edgeCls.getFrom(),
					edgeClsIns[0]);

			processIncidenceClass((GrEMFIncidenceClassImpl) edgeCls.getTo(),
					edgeClsIns[1]);
		}

		// add supertypes
		for (EClass supertype : eCls.getESuperTypes()) {
			// supertype is already extracted
			edgeCls.addSuperClass(this.getEdgeClass(supertype));

		}
		// copy all other properties
		copyProperties(eCls, edgeCls);

		// add comments
		copyAnnotations(eCls, edgeCls);

		// remove references from the sets if its an edge class
		this.incidenceClasses.remove(edgeClsOuts[0]);
		this.incidenceClasses.remove(edgeClsOuts[1]);
		this.incidenceClasses.remove(edgeClsIns[0]);
		this.incidenceClasses.remove(edgeClsIns[1]);

	}

	/**
	 * Determines whether an edge class with its both ends is compatible to a
	 * super edge class.
	 * 
	 * @param vCls1
	 *            one incident vertex class; one end
	 * @param vCls2
	 *            other incident vertex class; other end
	 * @param superEdgeCls
	 *            super edge class
	 * @return true, if vCls1 as "from" vertex class and vCls2 as "to" vertex
	 *         class are compatible to the super edge class<br>
	 *         true, if vCls1 as "to" vertex class and vCls2 as "from" vertex
	 *         class are compatible to the super edge class<br>
	 *         false, otherwise
	 */
	private boolean isCompatibleEdgeClass(GrEMFVertexClassImpl vCls1,
			GrEMFVertexClassImpl vCls2, EdgeClass superEdgeCls) {
		VertexClass sFrom = superEdgeCls.getFrom().getVertexClass();
		VertexClass sTo = superEdgeCls.getTo().getVertexClass();

		return (this.isCompatibleEnd(vCls1, sFrom) && this.isCompatibleEnd(
				vCls2, sTo))
				|| (this.isCompatibleEnd(vCls1, sTo) && this.isCompatibleEnd(
						vCls2, sFrom));

	}

	/**
	 * Determines whether an edge class end is compatible to the end of a super
	 * edge class.
	 * 
	 * @param end
	 *            end of a edge class
	 * @param superEnd
	 *            end of a super edge class
	 * @return true, if superEnd is a supertype of end or superEnd is equal to
	 *         end<br>
	 *         false, otherwise
	 */
	private boolean isCompatibleEnd(GrEMFVertexClassImpl end,
			VertexClass superEnd) {
		return superEnd.isSuperClassOf(end) || superEnd.equals(end);
	}

	/**
	 * Extracts the grEMF <code>GrEMFAttributeImpl</code> using the given EMF
	 * <code>EAttribute</code>.
	 * 
	 * @param eAttr
	 *            input <code>EAttribute</code>
	 */
	private void extractAttributes(EAttribute eAttr) {
		EcorePackage ecorePkg = EcorePackage.eINSTANCE;

		// get package
		Package pkg = this.schema.getPackage(getPackagePrefix(eAttr));

		// get base domain
		EDataType eDataTp = eAttr.getEAttributeType();
		Domain dom = this.getDomain(ecorePkg, pkg, eDataTp);

		// get container
		AttributedElementClass<?, ?> container = this.getVertexClass(eAttr
				.getEContainingClass());
		if (container == null) {
			container = this.getEdgeClass(eAttr.getEContainingClass());
		}

		// bounds and ordered property
		if (eAttr.isOrdered()
				&& !eAttr.isUnique()
				&& ((eAttr.getUpperBound() > 1) || (eAttr.getUpperBound() == EStructuralFeature.UNBOUNDED_MULTIPLICITY))) {
			dom = this.schema.createListDomain(dom);
		} else if (!eAttr.isOrdered()
				&& eAttr.isUnique()
				&& ((eAttr.getUpperBound() > 1) || (eAttr.getUpperBound() == EStructuralFeature.UNBOUNDED_MULTIPLICITY))) {
			dom = this.schema.createSetDomain(dom);
		} else if ((eAttr.getUpperBound() > 1)
				|| (eAttr.getUpperBound() == EStructuralFeature.UNBOUNDED_MULTIPLICITY)) {
			dom = this.schema.createListDomain(dom);
		}

		GrEMFAttributeImpl attr = (GrEMFAttributeImpl) container
				.createAttribute(eAttr.getName(), dom,
						eAttr.getDefaultValueLiteral());

		// copy properties and comments
		copyProperties(eAttr, attr);
		copyAnnotations(eAttr, attr);

	}

	private Domain getDomain(EcorePackage ecorePkg, Package pkg,
			EDataType eDataTp) {
		Domain dom = null;
		// primitives
		if (eDataTp.equals(ecorePkg.getEInt())) {
			dom = this.schema.getIntegerDomain();
		} else if (eDataTp.equals(ecorePkg.getEDouble())) {
			dom = this.schema.getDoubleDomain();
		} else if (eDataTp.equals(ecorePkg.getEBoolean())) {
			dom = this.schema.getBooleanDomain();
		} else if (eDataTp.equals(ecorePkg.getEByte())) {
			dom = this.schema.getByteDomain();
		} else if (eDataTp.equals(ecorePkg.getEShort())) {
			dom = this.schema.getShortDomain();
		} else if (eDataTp.equals(ecorePkg.getELong())) {
			dom = this.schema.getLongDomain();
		} else if (eDataTp.equals(ecorePkg.getEFloat())) {
			dom = this.schema.getFloatDomain();
		} else if (eDataTp.equals(ecorePkg.getEChar())) {
			dom = this.schema.getCharDomain();
		}
		// others: string, enum, date
		else if (eDataTp.equals(ecorePkg.getEString())) {
			dom = this.schema.getStringDomain();
		} else if (eDataTp instanceof EEnum) {
			dom = pkg.getDomain(eDataTp.getName());
		} else if (eDataTp.equals(ecorePkg.getEDate())) {
			dom = this.schema.getDateDomain();
		}
		// wrapper types
		else if (eDataTp.equals(ecorePkg.getEIntegerObject())) {
			dom = this.schema.getIntegerObjectDomain();
		} else if (eDataTp.equals(ecorePkg.getEDoubleObject())) {
			dom = this.schema.getDoubleObjectDomain();
		} else if (eDataTp.equals(ecorePkg.getEBooleanObject())) {
			dom = this.schema.getBooleanDomain();
		} else if (eDataTp.equals(ecorePkg.getEByteObject())) {
			dom = this.schema.getByteObjectDomain();
		} else if (eDataTp.equals(ecorePkg.getEShortObject())) {
			dom = this.schema.getShortObjectDomain();
		} else if (eDataTp.equals(ecorePkg.getELongObject())) {
			dom = this.schema.getLongObjectDomain();
		} else if (eDataTp.equals(ecorePkg.getEFloatObject())) {
			dom = this.schema.getFloatObjectDomain();
		} else if (eDataTp.equals(ecorePkg.getECharacterObject())) {
			dom = this.schema.getCharObjectDomain();
		}
		// big number types
		else if (eDataTp.equals(ecorePkg.getEBigInteger())) {
			dom = this.schema.getBigIntegerDomain();
		} else if (eDataTp.equals(ecorePkg.getEBigDecimal())) {
			dom = this.schema.getBigDecimalDomain();
		}
		return dom;
	}

	/**
	 * Gets the vertex class corresponding to the given <code>EClass</code>.
	 * 
	 * @param eCls
	 *            input <code>EClass</code>
	 * @return corresponding <code>VertexClass</code>
	 * @throws IllegalArgumentException
	 *             if the passed <code>EClass</code> does not exist
	 */
	private GrEMFVertexClassImpl getVertexClass(EClass eCls) {
		String pkgPrefix = getPackagePrefix(eCls);
		Package pkg = this.schema.getPackage(pkgPrefix);

		GrEMFVertexClassImpl end = null;
		if ((pkg != null) && pkg.containsNamedElement(eCls.getName())) {
			// must be true
			end = (GrEMFVertexClassImpl) pkg.getVertexClass(eCls.getName());
		} else {
			throw new IllegalArgumentException(
					"The passed class does not exist");
		}
		return end;
	}

	/**
	 * Gets the edge class corresponding to the given <code>EClass</code>.
	 * 
	 * @param eCls
	 *            input <code>EClass</code>
	 * @return corresponding <code>EdgeClass</code>
	 * @throws IllegalArgumentException
	 *             if the passed <code>EClass</code> does not exist
	 */
	private GrEMFEdgeClassImpl getEdgeClass(EClass eCls) {
		String pkgPrefix = getPackagePrefix(eCls);
		Package pkg = this.schema.getPackage(pkgPrefix);

		GrEMFEdgeClassImpl end = null;
		if ((pkg != null) && pkg.containsNamedElement(eCls.getName())) {
			// must be true
			end = (GrEMFEdgeClassImpl) pkg.getEdgeClass(eCls.getName());
		} else {
			throw new IllegalArgumentException(
					"The passed class does not exist");
		}
		return end;
	}

	/**
	 * Creates the edge class from all parameters and processes both incidence
	 * classes.
	 * 
	 * @return created edge class
	 * @see {@link #processIncidenceClass(GrEMFIncidenceClassWithRefsImpl, EReference, EReference)}
	 */
	private GrEMFEdgeClassImpl createEdgeClass(String qualifiedName,
			GrEMFVertexClassImpl from, String fromRoleName,
			AggregationKind fromAggr, int fromMin, int fromMax,
			GrEMFVertexClassImpl to, String toRoleName, AggregationKind toAggr,
			int toMin, int toMax, EReference[] edgeClsOuts,
			EReference[] edgeClsIns, int fromIndex) {

		// create edge class
		GrEMFEdgeClassImpl edgeCls = this.graph.createEdgeClass(qualifiedName,
				from, fromMin, fromMax, fromRoleName, fromAggr, to, toMin,
				toMax, toRoleName, toAggr);

		// process incidence classes
		processIncidenceClass(
				(GrEMFIncidenceClassWithRefsImpl) edgeCls.getFrom(),
				edgeClsOuts[fromIndex], edgeClsIns[fromIndex]);
		processIncidenceClass(
				(GrEMFIncidenceClassWithRefsImpl) edgeCls.getTo(),
				edgeClsOuts[(fromIndex + 1) % 2],
				edgeClsIns[(fromIndex + 1) % 2]);

		return edgeCls;
	}

	/**
	 * Creates an edge class based on an reference.
	 * 
	 * @return new edge class
	 */
	private GrEMFRefEdgeClassImpl createRefEdgeClass(GrEMFVertexClassImpl from,
			String fromRoleName, AggregationKind fromAggr, int fromMin,
			int fromMax, GrEMFVertexClassImpl to, String toRoleName,
			AggregationKind toAggr, int toMin, int toMax) {
		// create an name
		String qualifiedName = createQualifiedName(getPackagePrefix(from),
				createEdgeClassName(toAggr, toRoleName, from.getName()));

		return this.graph.createRefEdgeClass(qualifiedName, from, fromMin,
				fromMax, fromRoleName, fromAggr, to, toMin, toMax, toRoleName,
				toAggr);
	}

	/**
	 * Processes the given incidence class:<br>
	 * Attributes and values from the original references are set to the
	 * incidence's special references.
	 * 
	 * @param incCls
	 *            processed incidence class
	 * @param edgeClsOut
	 *            original reference from the edge class to the incident vertex
	 *            class
	 * @param edgeClsIn
	 *            original reference to the edge class from the incident vertex
	 *            class
	 */
	private static void processIncidenceClass(
			GrEMFIncidenceClassWithRefsImpl incCls, EReference edgeClsOut,
			EReference edgeClsIn) {
		// handle fromEdgeCls reference
		incCls.getFromEdgeClass().setName(edgeClsOut.getName());
		incCls.getFromEdgeClass().setContainment(edgeClsOut.isContainment());
		if (incCls.getFromEdgeClass() instanceof EStructuralFeatureImpl) {
			((EStructuralFeatureImpl) incCls.getFromEdgeClass())
					.setFeatureID(edgeClsOut.getFeatureID());
		}

		// handle toEdgeCls reference
		if (edgeClsIn == null) {
			incCls.clearToEdgeClass();
		} else {
			incCls.getToEdgeClass().setName(edgeClsIn.getName());
			incCls.getToEdgeClass().setLowerBound(edgeClsIn.getLowerBound());
			incCls.getToEdgeClass().setUpperBound(edgeClsIn.getUpperBound());
			incCls.getToEdgeClass().setContainment(edgeClsIn.isContainment());
			if (incCls.getToEdgeClass() instanceof EStructuralFeatureImpl) {
				((EStructuralFeatureImpl) incCls.getToEdgeClass())
						.setFeatureID(edgeClsIn.getFeatureID());
			}
		}

	}

	/**
	 * Processes the given incidence class:<br>
	 * If the incidence class describes a reference that not exists, it became
	 * invisible.
	 * 
	 * @param incCls
	 *            processed incidence class
	 * @param ref
	 *            reference which is described by the incidence class
	 */
	private static void processIncidenceClass(GrEMFIncidenceClassImpl incCls,
			EReference ref) {
		if (ref == null) {
			// non existing reference
			incCls.setInvisibile(true);
		}
		copyProperties(ref, incCls);
	}

	/**
	 * Determines whether the given EMF <code>EClass</code> is a grEMF
	 * <code>GrEMFVertexClassImpl</code> in any case. If so, return true,
	 * otherwise false. <br>
	 * It is a <code>GrEMFVertexClassImpl</code>, if <br>
	 * - there are no 2 references <br>
	 * - there are no 2 references stating the end of an edge <br>
	 * or <br>
	 * - there is no reference stating the multiplicity of an edge. <br>
	 * <br>
	 * Note that there can be other <code>EClasses</code> being also a
	 * <code>GrEMFVertexClassImpl</code>
	 * 
	 * @param eCls
	 *            input <code>EClass</code>
	 * @return true if <code>c</code> is a <code>GrEMFVertexClassImpl</code> in
	 *         any case
	 */
	private static boolean isVertexClassCertainly(EClass eCls) {
		EList<EReference> eRefs = eCls.getEReferences();
		// edge class: 2 references
		if (eRefs.size() != 2) {
			return true;
		}

		// resolve the end references
		int i = 0;
		int multiplicityRefs = 0;

		for (EReference eRef : eRefs) {
			if ((eRef.getLowerBound() == 1) && (eRef.getUpperBound() == 1)) {
				if (i == 2) {
					// found a third end
					return true;
				}
				i++;
				if (eRef.getEOpposite() != null) {
					multiplicityRefs++;
				}
			}
		}

		// edge class: two references as ends of the edge
		if ((i != 2)
				// edge class: one reference as multiplicity of the edge
				|| (multiplicityRefs < 1)
				// edge class: meaningful relationship between ends
				|| (eRefs.get(0).isContainer() && eRefs.get(1).isContainer())
				|| (eRefs.get(0).isContainment() && eRefs.get(1)
						.isContainment())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Builds an ascending order for the given classes based on their number of
	 * supertypes.
	 * 
	 * @param classes
	 *            input set of <code>EClasses</code>
	 * @return ascending order based on the numbers of supertypes
	 */
	private static EClass[] buildOrder(Set<EClass> classes) {
		// compute greatest offset index
		int max = 0;
		for(EClass eCls : classes) {
			int i = eCls.getEAllSuperTypes().size();
			if(i > max) {
				max = i;
			}
		}
		
		// offsets
		int[] offsets = new int[max + 1];

		// enter occurrences of number of supertypes
		for (EClass eCls : classes) {
			offsets[eCls.getEAllSuperTypes().size()]++;
		}
		// apply offset shift
		int offset = 0;
		for (int i = 0; i < offsets.length; i++) {
			int o = offsets[i];
			if (o != 0) {
				offsets[i] = offset;
			}
			offset += o;
		}

		EClass[] order = new EClass[offset];
		for (EClass eCls : classes) {
			int pos = offsets[eCls.getEAllSuperTypes().size()];
			while (order[pos] != null) {
				pos++;
			}
			order[pos] = eCls;
		}
		return order;
	}

	private static String createQualifiedName(String pkg, String name) {
		return pkg + '.' + name;
	}

	/**
	 * Builds the package prefix of the schema using the names of all root packages
	 * @param resource Resource of the schema model
	 * @return String concatenation of all the names in CamelCase
	 */
	private static String createSchemaPackagePrefix(Resource resource) {
		StringBuilder prefix = new StringBuilder();
		for (EObject eObj : resource.getContents()) {
			if (eObj instanceof ENamedElement) {
				prefix.append(
						Character.toUpperCase(((ENamedElement) eObj).getName()
								.charAt(0))).append(
						((ENamedElement) eObj).getName().substring(1));
			}
		}
		return Character.toLowerCase(prefix.charAt(0)) + prefix.substring(1);
	}

	/**
	 * Derives the package name of the given EMF <code>EPackage</code>.
	 * 
	 * @param ePkg
	 *            input <code>EPackage</code>
	 * @return package name of <code>ePkg</code>
	 */
	private static String getPackageName(EPackage ePkg) {
		return getQualifiedName(ePkg).toLowerCase();
	}

	/**
	 * Derives the package prefix of the given EMF <code>EClass</code>.
	 * 
	 * @param eCls
	 *            input <code>EClass</code>
	 * @return package prefix of <code>eCls</code>
	 */
	private static String getPackagePrefix(EClass eCls) {
		return getPackageName(eCls.getEPackage());
	}

	/**
	 * Derives the package prefix of the given EMF <code>EEnum</code>.
	 * 
	 * @param eNum
	 *            input <code>EEnum</code>
	 * @return package prefix of <code>eNum</code>
	 */
	private static String getPackagePrefix(EEnum eNum) {
		return getPackageName(eNum.getEPackage());
	}

	/**
	 * Derives the package prefix of the given EMF <code>EAttribute</code>.
	 * 
	 * @param eAttr
	 *            input <code>EAttribute</code>
	 * @param rootPkgName
	 *            name of the graph schema's root package
	 * @return package prefix of <code>eAttr</code>
	 */
	private static String getPackagePrefix(EAttribute eAttr) {
		return getPackagePrefix(eAttr.getEContainingClass());
	}

	/**
	 * Returns the qualified name of a package by iterating over all super
	 * packages
	 * 
	 * @param pkg
	 *            EMF package with a simple package name
	 * @return qualified package name of <code>pkg</code>
	 */
	private static String getQualifiedName(EPackage pkg) {
		StringBuilder pkgName = new StringBuilder();
		EPackage current = pkg;
		while (current != null) {
			pkgName.insert(0, current.getName());
			if (current.getESuperPackage() != null) {
				pkgName.insert(0, '.');
			}
			current = current.getESuperPackage();
		}
		return pkgName.toString();

	}

	/**
	 * Derives a rolename from the given EMF <code>EReference</code>. If it's
	 * null, use the given string (class name of the to vertex class and the
	 * rolename of from) instead.
	 * 
	 * @return rolenname derived from the parameters
	 */
	private static String getRoleName(EReference eRef, EClass to,
			EReference fromRef) {
		if (eRef != null) {
			return eRef.getName();
		} else {
			return Character.toLowerCase(to.getName().charAt(0))
					+ to.getName().substring(1) + "From"
					+ Character.toUpperCase(fromRef.getName().charAt(0))
					+ fromRef.getName().substring(1);
		}
	}

	/**
	 * Gets the aggregation kind of VC2's incidence class by investigating the
	 * both reference that describe one direction of an edge class.<br>
	 * scenario:
	 * 
	 * <pre>
	 * VC1 --part1--> EC --part2--> VC2
	 * </pre>
	 * 
	 * @param part1
	 *            reference to edge class ("egdeClsIn")
	 * @param part2
	 *            reference from edge class ("egdeClsOut")
	 * @return {@link AggregationKind#COMPOSITE}, if
	 *         <code>part1.isContainment() && part2.isContainment()</code><br>
	 *         {@link AggregationKind#NONE}, otherwise
	 */
	private static AggregationKind getAggregationKind(EReference part1,
			EReference part2) {
		if ((part1 != null) && part1.isContainment() && part2.isContainment()) {
			return AggregationKind.COMPOSITE;
		} else {
			return AggregationKind.NONE;
		}
	}

	/**
	 * Derives the minimal multiplicity from the given <code>EReference</code>.
	 * 
	 * @param eRef
	 *            input <code>EReference</code>
	 * @return 0 if eRef is null or its lower bound is unspecified;<br>
	 *         Ref's lower bound otherwise
	 */
	private static int getMinMultiplicity(EReference eRef) {
		if ((eRef == null)
				|| (eRef.getLowerBound() == EStructuralFeature.UNSPECIFIED_MULTIPLICITY)) {
			return 0;
		} else {
			return eRef.getLowerBound();
		}
	}

	/**
	 * Derives the minimal multiplicity from the given <code>EReference</code>.
	 * 
	 * @param eRef
	 *            input <code>EReference</code>
	 * @param aggr
	 *            AggregationKind of the opposite sid
	 * @return 1 if aggr is composite and eRef specifies no other upper bound
	 *         value;<br>
	 *         <code>Integer.MAX_VALUE</code> if aggr is not composite and eRef
	 *         specifies no other upper bound value; eRef's upper bound
	 *         otherwise
	 */
	private static int getMaxMultiplicity(EReference eRef, AggregationKind aggr) {
		if ((aggr == AggregationKind.COMPOSITE)
				&& ((eRef == null) || (eRef.getUpperBound() == EStructuralFeature.UNBOUNDED_MULTIPLICITY))) {
			return 1;
		} else if ((eRef == null)
				|| (eRef.getUpperBound() == EStructuralFeature.UNBOUNDED_MULTIPLICITY)) {
			return Integer.MAX_VALUE;
		} else {
			return eRef.getUpperBound();
		}
	}

	/**
	 * Creates a name for an edge class from the given aggregation kind on the
	 * from side, the name of the role on the to side and the name of the from
	 * vertex class.
	 * 
	 * @param aggrFrom
	 *            aggregation kind on the from side
	 * @return
	 */
	private static String createEdgeClassName(AggregationKind aggrFrom,
			String toRoleName, String fromClsName) {
		toRoleName = Character.toUpperCase(toRoleName.charAt(0))
				+ toRoleName.substring(1);
		if ((aggrFrom != AggregationKind.NONE)) {
			return fromClsName + "Contains" + toRoleName;
		} else {
			return fromClsName + "LinksTo" + toRoleName;
		}
	}

	/**
	 * Copies all annotations.
	 * 
	 * @param from
	 *            source of the annotations
	 * @param to
	 *            element that is annotated
	 */
	private static void copyAnnotations(EModelElement from, EModelElement to) {
		if ((from != null) && (to != null)) {
			to.getEAnnotations().addAll(from.getEAnnotations());
		}
	}

	/**
	 * Copies all properties from one <code>EPackage</code> to another one.
	 * 
	 * @param from
	 *            source of the properties
	 * @param to
	 *            <code>EPackage</code> that is provided with the properties
	 */
	private static void copyProperties(EPackage from, EPackage to) {
		if (from == null) {
			return;
		} else {
			to.setNsPrefix(from.getNsPrefix());
			to.setNsURI(from.getNsURI());
		}
	}

	/**
	 * Copies all properties from one <code>EClass</code> to another one.
	 * 
	 * @param from
	 *            source of the properties
	 * @param to
	 *            target that is provided with the properties
	 */
	private static void copyProperties(EClass from, GrEMFVertexClassImpl to) {
		if (from == null) {
			return;
		} else {
			if (from.isAbstract()) {
				to.setAbstract(true);
			}
			if (from.isInterface()) {
				to.setInterface(true);
			}
			to.setClassifierID(from.getClassifierID());
		}
	}

	/**
	 * Copies all properties from one <code>EClass</code> to another one.
	 * 
	 * @param from
	 *            source of the properties
	 * @param to
	 *            target that is provided with the properties
	 */
	private static void copyProperties(EClass from, GrEMFEdgeClassImpl to) {
		if (from == null) {
			return;
		} else {
			to.setClassifierID(from.getClassifierID());
		}
	}

	/**
	 * Copies all properties from one <code>EReference</code> to another one.
	 * 
	 * @param from
	 *            source of the properties
	 * @param to
	 *            target that is provided with the properties
	 */
	private static void copyProperties(EReference from,
			GrEMFIncidenceClassImpl to) {
		if (from == null) {
			return;
		} else {
			to.setFeatureID(from.getFeatureID());
		}

	}

	/**
	 * Copies all properties from one <code>EAttribute</code> to another one.
	 * 
	 * @param from
	 *            source of the properties
	 * @param to
	 *            target that is provided with the properties
	 */
	private static void copyProperties(EAttribute from, GrEMFAttributeImpl to) {
		if (from == null) {
			return;
		} else {
			if (from.isID()) {
				to.setID(true);
			}
			to.setFeatureID(from.getFeatureID());
		}

	}

	/**
	 * Copies all properties from one <code>EEnum</code> to another one.
	 * 
	 * @param from
	 *            source of the properties
	 * @param to
	 *            target that is provided with the properties
	 */
	private static void copyProperties(EEnum from, GrEMFEnumDomainImpl to) {
		if (from == null) {
			return;
		} else {
			to.setClassifierID(from.getClassifierID());
		}

	}

}

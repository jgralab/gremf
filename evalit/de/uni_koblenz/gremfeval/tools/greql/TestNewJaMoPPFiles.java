package de.uni_koblenz.gremfeval.tools.greql;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import de.uni_koblenz.gremf.GrEMFFacade;
import de.uni_koblenz.jgralab.Graph;

public class TestNewJaMoPPFiles {

	public static void main(String[] args) throws IOException {
		ResourceSet load_resourceSet = new ResourceSetImpl();

		load_resourceSet.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put("*", GrEMFFacade.getGrEMFResourceFactoryForEcoreFiles());

		Resource res = load_resourceSet.getResource(
				URI.createURI("models/java.ecore"), true);

		TreeIterator<EObject> iter = res.getAllContents();
		while (iter.hasNext()) {
			EObject eob = iter.next();
			if (eob instanceof EPackage) {
				load_resourceSet.getPackageRegistry().put(
						((EPackage) eob).getNsURI(), eob);
			}

		}

		Resource mod2 = load_resourceSet.getResource(
				URI.createURI("models/GrEMFAttributesListProxy.xmi"), true);

		for (EObject o : mod2.getContents()) {
			System.out.println("- " + o);
		}

		Graph g = (Graph) mod2.getContents().get(0);

	}

	
	// delete layout infos
	public static void main11(String[] args) throws IOException {
		ResourceSet load_resourceSet = new ResourceSetImpl();

		load_resourceSet.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put("ecore", new EcoreResourceFactoryImpl());
		load_resourceSet.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put("javamodel", new XMIResourceFactoryImpl());
		load_resourceSet.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put("xmi", new XMIResourceFactoryImpl());

		// load java.ecore
		Resource res = load_resourceSet.getResource(
				URI.createURI("models" + File.separator + "java.ecore"), true);

		// load layout.ecore
		Resource res2 = load_resourceSet.getResource(
				URI.createURI("models" + File.separator + "layout.ecore"), true);

		// register packages
		TreeIterator<EObject> iter = res.getAllContents();
		while (iter.hasNext()) {
			EObject eob = iter.next();
			if (eob instanceof EPackage) {
				load_resourceSet.getPackageRegistry().put(
						((EPackage) eob).getNsURI(), eob);
			}
		}

		iter = res2.getAllContents();
		while (iter.hasNext()) {
			EObject eob = iter.next();
			if (eob instanceof EPackage) {
				load_resourceSet.getPackageRegistry().put(
						((EPackage) eob).getNsURI(), eob);
			}
		}

		// Load instance
		Resource mod2 = load_resourceSet.getResource(
				URI.createURI("models" + File.separator
						+ "GrEMFAttributesListProxy.xmi"), true);

		EcoreUtil.resolveAll(load_resourceSet);

		
		
		// get java package
		EPackage javapack = (EPackage) res.getContents().get(0);
		
		// get commons package
		EPackage commonspack = null;
		for (EPackage ep : javapack.getESubpackages()) {
			if (ep.getName().equals("commons")) {
				commonspack = ep;
			}
		}
		
		// get EClass Commentable in java/commons
		EClass commentable = (EClass) commonspack.getEClassifier("Commentable");

		// delete all layout informations
		TreeIterator<EObject> it = mod2.getAllContents();
		ArrayList<EObject> list = new ArrayList<EObject>();
		while (it.hasNext()) {
			list.add(it.next());
		}
		Collection<EObject> found = EcoreUtil.getObjectsByType(list,
				commentable);

		for (EObject c : found) {
			c.eUnset(commentable.getEStructuralFeature("layoutInformations"));
		}

		// save the instance
		mod2.save(null);

		// delete the feature layoutInformations
		EStructuralFeature li = commentable
				.getEStructuralFeature("layoutInformations");
		commentable.getEStructuralFeatures().remove(li);

		// save java.ecore
		res.save(null);

	}
}

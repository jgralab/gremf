package de.uni_koblenz.gremf.schema.impl;

import java.io.IOException;
import java.util.List;

import org.pcollections.PSet;

import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.codegenerator.CodeBlock;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.BasicDomain;
import de.uni_koblenz.jgralab.schema.NamedElement;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.impl.DomainImpl;
import de.uni_koblenz.jgralab.schema.impl.PackageImpl;
import de.uni_koblenz.gremf.schema.GrEMFDomain;

public abstract class GrEMFBasicDomainImplProxy extends DomainImpl implements
		BasicDomain, GrEMFDomain {

	private BasicDomain jgralabDomain;

	protected GrEMFBasicDomainImplProxy(BasicDomain jgralabDomain, String name) {
		super(name, (PackageImpl) jgralabDomain.getPackage());
		this.jgralabDomain = jgralabDomain;
	}

	@Override
	public String getJavaAttributeImplementationTypeName(
			String schemaRootPackagePrefix) {
		return this.jgralabDomain
				.getJavaAttributeImplementationTypeName(schemaRootPackagePrefix);
	}

	@Override
	public String getJavaClassName(String schemaRootPackagePrefix) {
		return this.jgralabDomain.getJavaClassName(schemaRootPackagePrefix);
	}

	@Override
	public CodeBlock getReadMethod(String schemaPrefix, String variableName,
			String graphIoVariableName) {
		return this.jgralabDomain.getReadMethod(schemaPrefix, variableName,
				graphIoVariableName);
	}

	@Override
	public String getTGTypeName(Package pkg) {
		return this.jgralabDomain.getTGTypeName(pkg);
	}

	@Override
	public CodeBlock getWriteMethod(String schemaRootPackagePrefix,
			String variableName, String graphIoVariableName) {
		return this.jgralabDomain.getWriteMethod(schemaRootPackagePrefix,
				variableName, graphIoVariableName);
	}

	@Override
	public boolean isComposite() {
		return this.jgralabDomain.isComposite();
	}

	@Override
	public boolean isPrimitive() {
		return this.jgralabDomain.isPrimitive();
	}

	@Override
	public boolean isBoolean() {
		return this.jgralabDomain.isBoolean();
	}

	@Override
	public String getTransactionJavaAttributeImplementationTypeName(
			String schemaRootPackagePrefix) {
		return this.jgralabDomain
				.getTransactionJavaAttributeImplementationTypeName(schemaRootPackagePrefix);
	}

	@Override
	public String getTransactionJavaClassName(String schemaRootPackagePrefix) {
		return this.jgralabDomain
				.getTransactionJavaClassName(schemaRootPackagePrefix);
	}

	@Override
	public CodeBlock getTransactionReadMethod(String schemaPrefix,
			String variableName, String graphIoVariableName) {
		return this.jgralabDomain.getTransactionReadMethod(schemaPrefix,
				variableName, graphIoVariableName);
	}

	@Override
	public CodeBlock getTransactionWriteMethod(String schemaRootPackagePrefix,
			String variableName, String graphIoVariableName) {
		return this.jgralabDomain.getTransactionWriteMethod(
				schemaRootPackagePrefix, variableName, graphIoVariableName);
	}

	@Override
	public String getVersionedClass(String schemaRootPackagePrefix) {
		return this.jgralabDomain.getVersionedClass(schemaRootPackagePrefix);
	}

	@Override
	public String getInitialValue() {
		return this.jgralabDomain.getInitialValue();
	}

	@Override
	public Object parseGenericAttribute(GraphIO io) throws GraphIOException {
		return this.jgralabDomain.parseGenericAttribute(io);
	}

	@Override
	public void serializeGenericAttribute(GraphIO io, Object data)
			throws IOException {
		this.jgralabDomain.serializeGenericAttribute(io, data);
	}

	@Override
	public boolean isConformGenericValue(Object value) {
		return this.jgralabDomain.isConformGenericValue(value);
	}

	@Override
	public void delete() {
		this.jgralabDomain.delete();
	}

	@Override
	public PSet<Attribute> getAttributes() {
		return this.jgralabDomain.getAttributes();
	}

	@Override
	public void setQualifiedName(String newQName) {
		this.jgralabDomain.setQualifiedName(newQName);
	}

	@Override
	public Schema getSchema() {
		return this.jgralabDomain.getSchema();
	}

	@Override
	public String getUniqueName() {
		return this.jgralabDomain.getUniqueName();
	}

	@Override
	public void addComment(String comment) {
		this.jgralabDomain.addComment(comment);
	}

	@Override
	public List<String> getComments() {
		return this.jgralabDomain.getComments();
	}

	@Override
	public int compareTo(NamedElement arg0) {
		return this.jgralabDomain.compareTo(arg0);
	}

}

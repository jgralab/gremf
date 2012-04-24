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
package de.uni_koblenz.gremftest.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Before;
import org.junit.Test;

import de.uni_koblenz.gremf.GrEMFInstanceType;
import de.uni_koblenz.gremf.impl.GrEMFEdgeImpl;
import de.uni_koblenz.gremf.impl.GrEMFGraphImpl;
import de.uni_koblenz.gremf.resource.GrEMFResourceFactoryImpl;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.schema.Schema;

public class GrEMFGraphEdgeListProxyTest {

	private Schema schema;
	private GrEMFGraphImpl g;
	private GrEMFGraphImpl otherG;

	private EList<GrEMFEdgeImpl> objects;
	private GrEMFEdgeImpl obj;
	private GrEMFEdgeImpl otherObj;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("*", new GrEMFResourceFactoryImpl());

		rs.getResource(
				URI.createURI(System.getProperty("user.dir") + File.separator
						+ "models" + File.separator + "bookStore.gremf"), true);
		Resource r = rs
				.getResource(
						URI.createURI(System.getProperty("user.dir")
								+ File.separator + "models" + File.separator
								+ "model1.bookStore"), true);

		// get graph
		this.g = ((GrEMFGraphImpl) r.getContents().get(0));
		this.schema = this.g.getSchema();
		this.objects = (EList<GrEMFEdgeImpl>) this.g.eGet(this.g.eClass()
				.getEStructuralFeature("edges"));

		for (Edge obj : this.g.edges()) {
			if (obj instanceof GrEMFInstanceType) {
				this.obj = (GrEMFEdgeImpl) obj;
				break;
			}
		}

		this.otherG = (GrEMFGraphImpl) this.schema
				.createGraph(ImplementationType.GENERIC);
		this.otherObj = this.otherG.createEdge(
				this.obj.getAttributedElementClass(), this.obj.getAlpha(),
				this.obj.getOmega());
	}

	@Test
	public void testSize() {
		assertEquals(2, this.objects.size());
	}

	@Test
	public void testAdd() {
		int oldSize = this.objects.size();
		assertFalse(this.objects.add(this.obj));
		assertEquals(oldSize, this.objects.size());
		assertFalse(this.objects.add(this.otherObj));
		assertEquals(oldSize, this.objects.size());
	}

	@Test
	public void testAddAll() {
		int oldSize = this.objects.size();
		this.objects.addAll(new ArrayList<GrEMFEdgeImpl>(this.objects));
		assertEquals(oldSize, this.objects.size());
		List<GrEMFEdgeImpl> l = new ArrayList<GrEMFEdgeImpl>(1);
		l.add(this.otherObj);
		this.objects.addAll(l);
		assertEquals(oldSize, this.objects.size());
	}

	@Test
	public void testAddIndex() {
		int oldSize = this.objects.size();
		this.objects.add(1, this.obj);
		assertEquals(oldSize, this.objects.size());
		this.objects.add(1, this.otherObj);
		assertEquals(oldSize, this.objects.size());

		try {
			this.objects.add(this.objects.size() + 1, this.obj);
			fail();
		} catch (IndexOutOfBoundsException e) {
		}
	}

	@Test
	public void testAddAllIndex() {
		int oldSize = this.objects.size();
		this.objects.addAll(1, new ArrayList<GrEMFEdgeImpl>(this.objects));
		assertEquals(oldSize, this.objects.size());
		List<GrEMFEdgeImpl> l = new ArrayList<GrEMFEdgeImpl>(1);
		l.add(this.otherObj);
		this.objects.addAll(1, l);
		assertEquals(oldSize, this.objects.size());

		try {
			this.objects.addAll(this.objects.size() + 1,
					new ArrayList<GrEMFEdgeImpl>(this.objects));
			fail();
		} catch (IndexOutOfBoundsException e) {
		}
	}

	@Test
	public void testClear() {
		this.objects.clear();
		assertEquals(0, this.objects.size());
	}

	@Test
	public void testContains() {
		assertTrue(this.objects.contains(this.obj));
		assertFalse(this.objects.contains(this.otherObj));
	}

	@Test
	public void testContainsAll() {
		List<GrEMFEdgeImpl> l = new ArrayList<GrEMFEdgeImpl>(this.objects);
		assertTrue(this.objects.containsAll(l));
		l.add(this.otherObj);
		assertFalse(this.objects.containsAll(l));
	}

	@Test
	public void testGet() {
		assertEquals(this.obj, this.objects.get(0));

		try {
			this.objects.get(this.objects.size());
			fail();
		} catch (IndexOutOfBoundsException e) {
		}
	}

	@Test
	public void testIndexOf() {
		assertEquals(0, this.objects.indexOf(this.obj));
		assertEquals(-1, this.objects.indexOf(this.otherObj));
		assertEquals(1, this.objects.indexOf(this.objects.get(1)));
	}

	@Test
	public void testIsEmpty() {
		assertFalse(this.objects.isEmpty());
		this.objects.clear();
		assertTrue(this.objects.isEmpty());
	}

	@Test
	public void testRemove() {
		assertTrue(this.objects.remove(this.obj));
		int oldSize = this.objects.size();
		assertFalse(this.objects.contains(this.obj));
		assertFalse(this.objects.remove(this.otherObj));
		assertEquals(oldSize, this.objects.size());
	}

	@Test
	public void testRemoveIndex() {
		assertEquals(this.obj, this.objects.remove(0));
		try {
			this.objects.remove(this.objects.size());
			fail();
		} catch (IndexOutOfBoundsException e) {
		}
	}

	@Test
	public void testRemoveAll() {
		int oldSize = this.objects.size();
		List<GrEMFEdgeImpl> l = new ArrayList<GrEMFEdgeImpl>();
		l.add(this.otherObj);
		assertFalse(this.objects.removeAll(l));
		assertEquals(oldSize, this.objects.size());

		l.addAll(this.objects);
		assertFalse(this.objects.removeAll(l));
		assertEquals(oldSize, this.objects.size());

		assertTrue(this.objects.removeAll(new ArrayList<GrEMFEdgeImpl>(
				this.objects)));
		assertEquals(0, this.objects.size());
	}

	@Test
	public void testRetainAll() {
		int oldSize = this.objects.size();
		assertFalse(this.objects.retainAll(new ArrayList<GrEMFEdgeImpl>(
				this.objects)));
		assertEquals(oldSize, this.objects.size());

		List<GrEMFEdgeImpl> l = new ArrayList<GrEMFEdgeImpl>();
		l.add(this.obj);
		assertTrue(this.objects.retainAll(l));
		assertEquals(1, this.objects.size());

		l.clear();
		assertTrue(this.objects.retainAll(l));
		assertEquals(0, this.objects.size());
	}

	@Test
	public void testSetIndex() {
		assertNull(this.objects.set(1, this.obj));
		assertNull(this.objects.set(1, this.otherObj));
		try {
			this.objects.set(this.objects.size(), this.obj);
			fail();
		} catch (IndexOutOfBoundsException e) {
		}
	}

	@Test
	public void testMove() {
		this.objects.move(this.objects.size() - 1, this.obj);
		assertEquals(this.obj, this.objects.get(this.objects.size() - 1));

		try {
			this.objects.move(this.objects.size(), this.obj);
			fail();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try {
			this.objects.move(this.objects.size() - 1, this.otherObj);
			fail();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}

	@Test
	public void testMoveIndex() {
		assertEquals(
				this.obj,
				this.objects.move(this.objects.size() - 1,
						this.objects.indexOf(this.obj)));
		assertEquals(this.obj, this.objects.get(this.objects.size() - 1));

		try {
			this.objects.move(this.objects.size(),
					this.objects.indexOf(this.obj));
			fail();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		try {
			this.objects.move(this.objects.size() - 1,
					this.objects.indexOf(this.otherObj));
			fail();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}
}

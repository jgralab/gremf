# grEMF
grEMF provides an integration between the modeling framework EMF and the Java graph library JGraLab.

## Installation and building

### General information
The `gremf` project depends on the JGraLab project `jgralab` and the EMF subprojects `org.eclipse.emf.ecore` and `org.eclipse.emf.ecore.xmi`. Thus, there are indirect dependencies to the JGraLab project `common` and the EMF subproject `org.eclipse.emf.common`, but see the respective build instructions for a more detailed overview. The `gremf` project is based on specific versions of the mentioned dependencies:

* `jgralab` project: special branch, currently named `grEMF_test`
* EMF subprojects: build M6 of EMF 2.8, not yet available via the plugin's update site

Due to this dependencies it is assumed to checkout all relevant projects:

* [`jgralab`](https://github.com/jgralab/jgralab.git)
* [`commom`](https://github.com/jgralab/common.git)
* [`org.eclipse.emf.ecore`](git://git.eclipse.org/gitroot/emf/org.eclipse.emf.git)
* [`org.eclipse.emf.ecore.xmi`](git://git.eclipse.org/gitroot/emf/org.eclipse.emf.git)
* [`org.eclipse.emf.common`](git://git.eclipse.org/gitroot/emf/org.eclipse.emf.git)

Start the building by checking out the `gremf` project:

		git clone https://github.com/jgralab/gremf.git

Generally, there a two different ways of using grEMF and therefore several build mechanisms.

#### Building as library using Ant
*Note:* [Apache Ant](http://ant.apache.org/) is needed

Copy the contained template file `template.user.properties` to `user.properties` and enter the required information. An example file is given below:

		eclipsehome.dir=/Applications/eclipse
		emfprojects.dir=/Users/demo/projects/emf/org.eclipse.emf/plugins
		istprojects.dir=/Users/demo/projects/jgralab

Use the included Ant script to build the `gremf` project:

		ant -f grEMF.xml build

#### Building as library using Eclipse

All projects are EMF projects as well allowing to use Eclipse for building. Note that in this case the JGraLab project `jgralab4eclipse` must be used instead of the plain `jgralab` project. Additionally, all projects must be present in the same Eclipse workspace.

#### Building as plugin
*Note:* The [Eclipse Plugin Environment](http://www.eclipse.org/pde/) is needed

It is necessary to carry out all steps of the previous section first. Afterwards, checkout the feature project `de.uni_koblenz.gremf`, which is contained in the `feature` folder within the `gremf` project, as a separate project into the same workspace. Use the included feature file `feature.xml` to deploy a working plugin containing all needed dependencies. The generated output have be added to the `dropins` folder in your eclipse home directory.

## Documentation

### API Documentation

You can generate a full API documentation using the Ant script:

		ant -f grEMF.xml document

### Others

See the [wiki](https://github.com/jgralab/gremf/wiki) or the technical report for more information. 

## License

Copyright (C) 2007-2012 The JGraLab Team <ist@uni-koblenz.de>

Distributed under the General Public License (Version 3), with the following
additional grant:

    Additional permission under GNU GPL version 3 section 7

    If you modify this Program, or any covered work, by linking or combining it
    with Eclipse (or a modified version of that program or an Eclipse plugin),
    containing parts covered by the terms of the Eclipse Public License (EPL),
    the licensors of this Program grant you additional permission to convey the
    resulting work.  Corresponding Source for a non-source form of such a
    combination shall include the source code for the parts of JGraLab used as
    well as that of the covered work.


<!-- Local Variables:        -->
<!-- mode: markdown          -->
<!-- indent-tabs-mode: nil   -->
<!-- End:                    -->

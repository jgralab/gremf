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
package de.uni_koblenz.gremfeval.code.objecthandling;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import bb.util.Benchmark;

public class BenchmarkObjectHandling {

	public static void main(String[] args) {
		try {

			// backup stdout
			PrintStream stdOut = System.out;

			// replace stdout with a log stream
			PrintStream logStream = new PrintStream(new FileOutputStream(
					System.getProperty("user.dir") + File.separator + "evalit"
							+ File.separator + ".objecthandling_log.txt"));

			System.setOut(logStream);

			String file = System.getProperty("user.dir") + File.separator;
			// replace stdout with a log stream
			if (args.length > 0) {
				file += args[0];
			} else {
				file += "evalit";
			}

			PrintStream benchStream = new PrintStream(new FileOutputStream(file
					+ File.separator + "objecthandling_bechnmark.txt"));

			Benchmark.Params params = new Benchmark.Params();
			params.setNumberMeasurements(50);
			benchStream.print("EMF: ");
			benchStream.println(new Benchmark(new EMFObjectHandlingEval(false),
					params));
			benchStream.flush();
			benchStream.print("EMF view: ");
			benchStream.println(new Benchmark(new EMFObjectHandlingEval(true),
					params));
			benchStream.flush();
			benchStream.print("EMF view (with proxy): ");
			benchStream.println(new Benchmark(
					new EMFObjectHandlingWithProxyEval(), params));
			benchStream.flush();
			benchStream.print("JGraLab: ");
			benchStream.println(new Benchmark(new JGraLabObjectHandlingEval(
					false), params));
			benchStream.flush();
			benchStream.print("JGraLab view: ");
			benchStream.println(new Benchmark(new JGraLabObjectHandlingEval(
					true), params));
			benchStream.close();

			// reset stdout
			System.setOut(stdOut);
			logStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

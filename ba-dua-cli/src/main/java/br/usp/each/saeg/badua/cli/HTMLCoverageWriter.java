package br.usp.each.saeg.badua.cli;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jacoco.core.analysis.ICounter;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.FileMultiReportOutput;
import org.jacoco.report.ILanguageNames;
import org.jacoco.report.ISourceFileLocator;
import org.jacoco.report.MultiSourceFileLocator;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.HTMLGroupVisitor;
import org.jacoco.report.internal.html.IHTMLReportContext;
import org.jacoco.report.internal.html.ILinkable;
import org.jacoco.report.internal.html.index.ElementIndex;
import org.jacoco.report.internal.html.index.IIndexUpdate;
import org.jacoco.report.internal.html.resources.Resources;
import org.jacoco.report.internal.html.table.Table;

import org.kohsuke.args4j.Option;

import br.usp.each.saeg.badua.core.analysis.ClassCoverage;
import br.usp.each.saeg.badua.core.analysis.CoverageNode;
import br.usp.each.saeg.badua.core.analysis.MethodCoverage;
import br.usp.each.saeg.badua.core.analysis.SourceLineDefUseChain;
import br.usp.each.saeg.badua.report.html.ClassesPage;

public class HTMLCoverageWriter {

	@Option(name = "--sourcefiles", usage = "location of the source files", metaVar = "<path>")
	List<File> sourcefiles = new ArrayList<File>();
	@Option(name = "--encoding", usage = "source file encoding (by default platform encoding is used)", metaVar = "<charset>")
	String encoding;
	@Option(name = "--tabwith", usage = "tab stop width for the source pages (default 4)", metaVar = "<n>")
	int tabwidth = 4;

	private static Resources resources;
	private static ElementIndex index;
	private HTMLGroupVisitor groupHandler;

	private static ReportOutputFolder root;

	public static void write(final List<ClassCoverage> classes, final FileMultiReportOutput output) throws IOException {

		// Setting Root
		root = new ReportOutputFolder(output);
		resources = new Resources(root);
		resources.copyResources();
		index = new ElementIndex(root);

		output.close();
	}

	public void visitClasses(final List<ClassCoverage> classes, final ISourceFileLocator locator) throws IOException {
		final ClassesPage page = new ClassesPage(classes, null, getSourceLocator(), root, HTMLCoverageWriter.this);
		try {
			page.render();
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	private ISourceFileLocator getSourceLocator() {
		final MultiSourceFileLocator multi = new MultiSourceFileLocator(tabwidth);
		for (final File f : sourcefiles) {
			multi.add(new DirectorySourceFileLocator(f, encoding, tabwidth));
		}
		return multi;
	}

}

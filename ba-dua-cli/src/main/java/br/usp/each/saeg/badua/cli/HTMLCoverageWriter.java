package br.usp.each.saeg.badua.cli;

import br.usp.each.saeg.badua.core.analysis.ClassCoverage;
import br.usp.each.saeg.badua.core.analysis.CoverageNode;
import br.usp.each.saeg.badua.report.html.page.ProjectPage;
import br.usp.each.saeg.badua.report.html.resources.Resources;
import br.usp.each.saeg.badua.report.html.resources.Styles;
import br.usp.each.saeg.badua.report.html.table.*;

import org.jacoco.report.*;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.IHTMLReportContext;
import org.jacoco.report.internal.html.ILinkable;
import org.jacoco.report.internal.html.index.ElementIndex;
import org.jacoco.report.internal.html.index.IIndexUpdate;
//import org.jacoco.report.internal.html.resources.Resources;
//import org.jacoco.report.internal.html.resources.Styles;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HTMLCoverageWriter implements IHTMLReportContext {

	@Option(name = "--encoding", usage = "source file encoding (by default platform encoding is used)", metaVar = "<charset>")
	static String encoding;
	@Option(name = "--tabwith", usage = "tab stop width for the source pages (default 4)", metaVar = "<n>")
	static int tabwidth = 4;

	private Locale locale = Locale.getDefault();
	private String footerText = "";
	private String outputEncoding = "UTF-8";

	private Table table;

	private static Resources resources;
	private static ElementIndex index;

	private static ReportOutputFolder root;

	public static void write(final List<ClassCoverage> classes, final FileMultiReportOutput output, List<File> sourceFiles) throws IOException {

		// Setting Root
		root = new ReportOutputFolder(output);
		resources = new Resources(root);
		resources.copyResources();
		index = new ElementIndex(root);

		CoverageNode projectNode = new CoverageNode("Classes of Project");
		for (ClassCoverage c : classes)
		{
			projectNode.increment(c);
		}

		visitClasses(projectNode, classes, getSourceLocator(sourceFiles));

		output.close();
	}

	public static void visitClasses(final CoverageNode projectNode,
									final List<ClassCoverage> classes,
									final ISourceFileLocator locator) throws IOException {
		final ProjectPage page = new ProjectPage(projectNode, classes, null, locator, root);
		try {
			page.render();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static ISourceFileLocator getSourceLocator(List<File> sourceFiles) {
		final MultiSourceFileLocator multi = new MultiSourceFileLocator(tabwidth);
		for (final File f : sourceFiles) {
			multi.add(new DirectorySourceFileLocator(f, encoding, tabwidth));
		}

		return multi;
	}

	// === IHTMLReportContext ===

	public Resources getResources() {
		return resources;
	}

	public ILanguageNames getLanguageNames() {
		return null;
	}


	@Override
	/**
	 * Somente para não dar erro de herança*/
	public org.jacoco.report.internal.html.table.Table getTable() {
		return null;
	}

	/**
	 * Inicializador das tabelas da BADUA
	 * Todas as páginas, exceto a MethodPage, recebem a tabela geral. A MethodPage recebe a tabela de DUAs
	 * @param dua
	 * @return
	 */
	public Table getBaduaTable(boolean dua) {
		if (table == null) {
			table = dua ? createDuaTable() : createGeneralTable();
		}
		return table;
	}

	/**
	 * Método especifico para uso de tabela referente a listagem de outras páginas
	 * @return
	 */
	private Table createGeneralTable() {
		final Table t = new Table();
		t.add("Element", null, new LabelColumn());
		t.add("Missed DUAs", Styles.BAR, new BarColumn(locale));
		t.add("Cov.", Styles.CTR2, new PercentageColumn(locale));
		addMissedTotalColumns(t, "Methods");
		addMissedTotalColumns(t, "Classes");
		return t;
	}

	/**
	 * Método especifico para uso de tabela referente a listagem de item de DUA
	 * @return
	 */
	private Table createDuaTable(){
		final Table t = new Table(true);
		t.add("Var", null, new LabelColumn());
		t.add("Def Line", null, new LabelColumn());
		t.add("Use Line", null, new LabelColumn());
		t.add("Target Line", null, new LabelColumn());
		t.add("Status", null, new LabelColumn());
		return t;
	}

	private void addMissedTotalColumns(final Table table, final String label) {
		table.add(label, Styles.CTR1, CounterColumn.newTotal(label, locale));
		table.add("Missed", Styles.CTR1, CounterColumn.newMissed(label, locale));
	}

	public String getFooterText() {
		return footerText;
	}

	@Override
	public ILinkable getSessionsPage() {
		return null;
	}

	public String getOutputEncoding() {
		return outputEncoding;
	}

	public IIndexUpdate getIndexUpdate() {
		return index;
	}

	public Locale getLocale() {
		return locale;
	}

}

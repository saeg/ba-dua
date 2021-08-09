package br.usp.each.saeg.badua.report.html.page;

import br.usp.each.saeg.badua.cli.HTMLCoverageWriter;
import br.usp.each.saeg.badua.core.analysis.ClassCoverage;
import org.jacoco.report.ISourceFileLocator;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.HTMLElement;

import java.io.IOException;
import java.util.List;

/**
 * Pagina mostrando informações sobre o projeto
 * A pagina contém uma tabela que lista todas as classes cobertas
 */

public class ProjectPage extends TablePage{
	
	private final ISourceFileLocator locator;
	private List<ClassCoverage> classes;
	private HTMLCoverageWriter context = new HTMLCoverageWriter();

	public ProjectPage(final List<ClassCoverage> classes,
					   final ReportPage parent,
					   final ISourceFileLocator locator,
					   final ReportOutputFolder folder) {
		super(parent, folder, new HTMLCoverageWriter());
		this.classes = classes;
		this.locator = locator;
	}

	public void render() throws IOException {
		renderClasses();
		super.render();

	}

	private void renderClasses() throws IOException {
		for(ClassCoverage cc : classes) {

			//Getting Class Info
			final String className = cc.getName();
			final String folderName = className.length() == 0 ? "default"
					: className.replace('/', '.');

			//Rendering class' methods
			final ClassPage page = new ClassPage( cc.getMethods(), this, folder.subFolder(folderName), context );
			page.render();

			//Adding page to table
			addItem(page);

		}

	}

	protected String getOnload() {
		return "initialSort(['breadcrumb', 'coveragetable'])";
	}

	protected String getFileName() {
		return "index.html";
	}

	protected void content(HTMLElement body) throws IOException {
//		if (bundle.getPackages().isEmpty()) {
//			body.p().text("No class files specified.");
//		} else if (!bundle.containsCode()) {
//			body.p().text(
//					"None of the analyzed classes contain code relevant for code coverage.");
//		} else {
//			super.content(body);
//		}
	}

}

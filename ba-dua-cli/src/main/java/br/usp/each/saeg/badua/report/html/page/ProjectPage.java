package br.usp.each.saeg.badua.report.html.page;

import br.usp.each.saeg.badua.cli.HTMLCoverageWriter;
import br.usp.each.saeg.badua.core.analysis.ClassCoverage;
import org.jacoco.report.ISourceFileLocator;
import org.jacoco.report.internal.ReportOutputFolder;

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
		super(classes, parent, folder, new HTMLCoverageWriter());
		this.classes = classes;
		this.locator = locator;
	}

	public void render() throws IOException {

	}

	private void renderClasses() throws IOException {
		for(ClassCoverage cc : classes) {
			final String className = cc.getName();
//			final ClassPage page = new ClassPage(cc.getMethods(), this, );

		}

	}

	private void renderPackages() throws IOException {
//		for (final IPackageCoverage p : bundle.getPackages()) {
//			if (!p.containsCode()) {
//				continue;
//			}
//			final String packagename = p.getName();
//			final String foldername = packagename.length() == 0 ? "default"
//					: packagename.replace('/', '.');
//			final PackagePage page = new PackagePage(p, this, locator,
//					folder.subFolder(foldername), context);
//			page.render();
//			addItem(page);
//		}
	}
	
	protected String getOnload() {
		return "initialSort(['breadcrumb', 'coveragetable'])";
	}

	protected String getFileName() {
		return "index.html";
	}

//	protected void content(HTMLElement body) throws IOException {
//		if (bundle.getPackages().isEmpty()) {
//			body.p().text("No class files specified.");
//		} else if (!bundle.containsCode()) {
//			body.p().text(
//					"None of the analyzed classes contain code relevant for code coverage.");
//		} else {
//			super.content(body);
//		}
//	}

}

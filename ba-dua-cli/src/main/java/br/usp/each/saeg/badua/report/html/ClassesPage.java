package br.usp.each.saeg.badua.report.html;

import java.io.IOException;
import java.util.List;

import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.report.ISourceFileLocator;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.HTMLElement;
import org.jacoco.report.internal.html.page.ReportPage;
import org.jacoco.report.internal.html.page.TablePage;

import br.usp.each.saeg.badua.cli.HTMLCoverageWriter;
import br.usp.each.saeg.badua.core.analysis.ClassCoverage;

public class ClassesPage {
	
	private final ISourceFileLocator locator;
	private List<ClassCoverage> classes;

	public ClassesPage(final List<ClassCoverage> classes, final ReportPage parent,
			final ISourceFileLocator locator, final ReportOutputFolder folder,
			final HTMLCoverageWriter context) {
		this.classes = classes;
		this.locator = locator;
	}
	
	public void render() throws IOException {
//		renderPackages();
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

	protected void content(HTMLElement body) throws IOException {
		if (bundle.getPackages().isEmpty()) {
//			body.p().text("No class files specified.");
//		} else if (!bundle.containsCode()) {
//			body.p().text(
//					"None of the analyzed classes contain code relevant for code coverage.");
//		} else {
//			super.content(body);
//		}
	}

}

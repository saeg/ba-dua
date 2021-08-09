package br.usp.each.saeg.badua.report.html.page;

import br.usp.each.saeg.badua.cli.HTMLCoverageWriter;
import br.usp.each.saeg.badua.core.analysis.MethodCoverage;
import org.jacoco.report.internal.ReportOutputFolder;

import java.io.IOException;
import java.util.Collection;

public class ClassPage extends TablePage{

	private Collection<MethodCoverage> methods;

	public ClassPage(final Collection<MethodCoverage> methods,
					 final ReportPage parent,
					 final ReportOutputFolder folder,
					 final HTMLCoverageWriter context) {
		super(parent, folder, new HTMLCoverageWriter());
		this.methods = methods;
	}

	public void render() throws IOException{
		for(MethodCoverage mc : methods)
		{
			final String methodName = mc.getName();
			final String folderName = methodName.length() == 0 ? "default"
					: methodName.replace('/', '.');

			final MethodPage page = new MethodPage();
			page.render();

			addItem(page);
		}
		super.render();
	}

	protected String getOnload() {
		return "initialSort(['breadcrumb', 'coveragetable'])";
	}

	protected String getFileName() {
		return "index.html";
	}

	public String getLinkLabel() {
		return context.getLanguageNames().getPackageName(getNode().getName());
	}

}

package br.usp.each.saeg.badua.report.html.page;

import br.usp.each.saeg.badua.cli.HTMLCoverageWriter;
import br.usp.each.saeg.badua.core.analysis.MethodCoverage;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.ILinkable;
import org.jacoco.report.internal.html.page.ReportPage;

import java.util.List;

public class ClassPage {
	
	private final ILinkable sourcePage;
	private List<MethodCoverage> methods;

	public ClassPage(final List<MethodCoverage> methods, final ReportPage parent, final ReportOutputFolder folder, final HTMLCoverageWriter context, ILinkable sourcePage) {
				this.sourcePage = sourcePage;
				this.methods = methods;
	}

}

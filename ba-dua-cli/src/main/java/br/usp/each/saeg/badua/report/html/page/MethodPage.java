package br.usp.each.saeg.badua.report.html.page;

import br.usp.each.saeg.badua.cli.HTMLCoverageWriter;
import br.usp.each.saeg.badua.core.analysis.SourceLineDefUseChain;
import org.jacoco.report.internal.ReportOutputFolder;

import java.util.Collection;

public class MethodPage extends TablePage {

    private Collection<SourceLineDefUseChain> defUses;

    public MethodPage(final Collection<SourceLineDefUseChain> defUses,
                     final ReportPage parent,
                     final ReportOutputFolder folder,
                     final HTMLCoverageWriter context) {
        super(parent, folder, new HTMLCoverageWriter());
        this.defUses = defUses;
    }
}

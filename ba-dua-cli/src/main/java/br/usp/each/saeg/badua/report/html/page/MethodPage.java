package br.usp.each.saeg.badua.report.html.page;

import br.usp.each.saeg.badua.cli.HTMLCoverageWriter;
import br.usp.each.saeg.badua.core.analysis.MethodCoverage;
import br.usp.each.saeg.badua.core.analysis.SourceLineDefUseChain;
import org.jacoco.report.internal.ReportOutputFolder;

import java.io.IOException;
import java.util.Collection;

public class MethodPage extends TablePage {

    private Collection<SourceLineDefUseChain> defUses;

    /**
     * Construtor e é isso ai
     * @param methodNode
     * @param parent
     * @param folder
     */
    public MethodPage(final MethodCoverage methodNode,
                      final ReportPage parent,
                      final ReportOutputFolder folder) {
        super(methodNode, parent, folder, new HTMLCoverageWriter());
        this.defUses = methodNode.getDefUses();
    }

    /**
     * Renderização base
     * @throws IOException
     */
    public void render() throws IOException {
        renderDUAs();
        super.render();
    }

    /**
     * Renderização para as DUAs
     *
     * @throws IOException
     */
    private void renderDUAs() throws IOException {
//        for (SourceLineDefUseChain dua : defUses) {
//            System.out.println(dua.var);
//        }
    }

    @Override
    protected String getFileName() {
        String shortname = node.getName();
        return shortname + ".html";

    }

    @Override
    public String getLinkLabel() {
        return this.getFileName();
    }

    @Override
    public String getLinkStyle() {
        return null;
    }
}

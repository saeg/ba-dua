package br.usp.each.saeg.badua.report.html.page;

import br.usp.each.saeg.badua.cli.HTMLCoverageWriter;
import br.usp.each.saeg.badua.core.analysis.MethodCoverage;
import br.usp.each.saeg.badua.core.analysis.SourceLineDefUseChain;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.resources.Styles;

import java.io.IOException;
import java.util.Collection;

public class MethodPage extends TablePage {

    private Collection<SourceLineDefUseChain> defUses;
//    private final ILinkable sourcePage;

    /**
     * Construtor e é isso ai
     * @param methodNode
     * @param parent
     * @param folder
     */
    public MethodPage(final MethodCoverage methodNode,
                      final ReportPage parent,
                      final ReportOutputFolder folder) {
        super(methodNode, parent, folder, new HTMLCoverageWriter(), true);
        this.defUses = methodNode.getDefUses();
    }

    /**
     * Renderização base
     * @throws IOException
     */
    public void render() throws IOException {
        for (SourceLineDefUseChain dua : defUses) {
            addItem(new DuaItem(dua,null));
        }
        super.render();
    }

    /**
     * Renderização para as DUAs
     *
     * @throws IOException
     */

    @Override
    protected String getFileName() {
        String shortname = node.getName();
        return shortname + "()";

    }

    @Override
    public String getLinkLabel() {
        return this.getFileName();
    }

    @Override
    public String getLinkStyle() {
        return Styles.EL_METHOD;
    }
}

package br.usp.each.saeg.badua.report.html.page;

import br.usp.each.saeg.badua.core.analysis.CoverageNode;
import br.usp.each.saeg.badua.core.analysis.SourceLineDefUseChain;
import br.usp.each.saeg.badua.report.html.table.ITableItem;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.ILinkable;
import org.jacoco.report.internal.html.resources.Styles;

public class DuaItem implements ITableItem {

    private final SourceLineDefUseChain dua;
    private int counter = 0;
    private final ILinkable sourcePage;

    DuaItem(final SourceLineDefUseChain dua,
            final ILinkable sourcePage) {
        this.dua = dua;
        this.sourcePage = sourcePage;
    }

    public String getLinkLabel() {
        String field = "";

        switch (counter) {
            case 0:
                field = this.dua.var;
                break;
            case 1:
                field = Integer.toString(this.dua.def);
                break;
            case 2:
                field = Integer.toString(this.dua.use);
                break;
            case 3:
                field = (this.dua.target != SourceLineDefUseChain.NONE) ? Integer.toString(this.dua.target) : "-" ;
                break;
            case 4:
                field = (this.dua.covered) ? "Covered" : "Missed" ;
                break;
        }
        counter++;
        return field;
    }


    public String getLinkStyle() {
        return Styles.INFO;
    }

    public String getLink(final ReportOutputFolder base) {
        if (sourcePage == null) {
            return null;
        }
//        final String link = sourcePage.getLink(base);
        return "";
    }

    @Override
    public CoverageNode getNode() {
        return null;
    }
}

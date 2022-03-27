package br.usp.each.saeg.badua.report.html.page;

import br.usp.each.saeg.badua.core.analysis.CoverageNode;
import br.usp.each.saeg.badua.core.analysis.SourceLineDefUseChain;
import br.usp.each.saeg.badua.report.html.table.ITableItem;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.ILinkable;
import org.jacoco.report.internal.html.resources.Styles;

import java.util.HashMap;

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
        final String link = sourcePage.getLink(base) + this.generateUrlParams();
        return link;
    }

    private String generateUrlParams(){
        StringBuilder urlParams = new StringBuilder("?");

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("covered", String.valueOf(this.dua.covered));
        params.put("var", String.valueOf(this.dua.var));
        params.put("def", String.valueOf(this.dua.def));
        params.put("use", String.valueOf(this.dua.use));
        if(this.dua.target != this.dua.NONE) params.put("target", String.valueOf(this.dua.target));

        for(String key: params.keySet()) {
            urlParams.append(key + "=" + params.get(key) + "&");
        }

        return  urlParams.toString();
    }

    @Override
    public CoverageNode getNode() {
        return null;
    }
}

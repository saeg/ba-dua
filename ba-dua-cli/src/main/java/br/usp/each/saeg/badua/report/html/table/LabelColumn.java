package br.usp.each.saeg.badua.report.html.table;

import br.usp.each.saeg.badua.core.analysis.CoverageNode;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.HTMLElement;
import org.jacoco.report.internal.html.resources.Resources;

import java.io.IOException;
import java.util.List;

public class LabelColumn implements IColumnRenderer{

    public boolean init(final List<? extends ITableItem> items,
                        final CoverageNode total) {
        return true;
    }

    public void footer(final HTMLElement td,
                       final CoverageNode total,
                       final Resources resources,
                       final ReportOutputFolder base)
            throws IOException {
        td.text("Total");
    }

    public void item(final HTMLElement td,
                     final ITableItem item,
                     final Resources resources,
                     final ReportOutputFolder base)
            throws IOException {
        td.a(item, base);
    }
}

package br.usp.each.saeg.badua.report.html.table;

import br.usp.each.saeg.badua.core.analysis.CoverageNode;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.HTMLElement;
import org.jacoco.report.internal.html.resources.Resources;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BarColumn implements IColumnRenderer {


    private static final int WIDTH = 120;
    private final NumberFormat integerFormat;
    private int max;

    public BarColumn(final Locale locale){
        //Sem o uso de entity e sem o uso de comparator por enquanto
        this.integerFormat = NumberFormat.getIntegerInstance(locale);
    }

    public boolean init(List<? extends ITableItem> items,
                        CoverageNode cor) {
        this.max = 0;
        for (final ITableItem item : items) {
            final int count = item.getNode().getDUCounter().getTotalCount();
            if (count > this.max) {
                this.max = count;
            }
        }
        return true;

    }

    public void footer(HTMLElement td,
                       CoverageNode cor,
                       Resources resources,
                       ReportOutputFolder base)
            throws IOException {
        final ICounter counter = cor.getDUCounter();
        td.text(integerFormat.format(counter.getMissedCount()));
        td.text(" of ");
        td.text(integerFormat.format(counter.getTotalCount()));
    }

    public void item(HTMLElement td,
                     ITableItem item,
                     Resources resources,
                     ReportOutputFolder base)
            throws IOException {
        if (max >  0){
            final ICounter counter = item.getNode().getDUCounter();
            final int missed = counter.getMissedCount();
            bar(td, missed, Resources.REDBAR, resources, base);
            final int covered = counter.getCoveredCount();
            bar(td, covered, Resources.GREENBAR, resources, base);
        }
    }

    private void bar(final HTMLElement td,
                     final int count,
                     final String image,
                     final Resources resources,
                     final ReportOutputFolder base)
            throws IOException {
        final int width = count * WIDTH / max;
        if (width > 0) {
            td.img(resources.getLink(base, image), width, 10,
                    integerFormat.format(count));
        }
    }
}

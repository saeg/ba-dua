package br.usp.each.saeg.badua.report.html.table;

import br.usp.each.saeg.badua.core.analysis.CoverageNode;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.HTMLElement;
import org.jacoco.report.internal.html.resources.Resources;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PercentageColumn implements IColumnRenderer{

    //Sem entity e sem comparator
    private final NumberFormat percentageFormat;

    public PercentageColumn(final Locale locale) {
        this.percentageFormat = NumberFormat.getPercentInstance(locale);
    }

    public boolean init(List<? extends ITableItem> items,
                        CoverageNode cor) {
        return true;
    }

    public void footer(HTMLElement td,
                       CoverageNode cor,
                       Resources resources,
                       ReportOutputFolder base)
            throws IOException {
        cell(td, cor);

    }

    public void item(HTMLElement td,
                     ITableItem item,
                     Resources resources,
                     ReportOutputFolder base)
            throws IOException {
        cell(td, item.getNode());

    }

    private void cell(final HTMLElement td,
                      final CoverageNode node)
            throws IOException {
        final ICounter counter = node.getDUCounter();
        final int total = counter.getTotalCount();
        if (total == 0) {
            td.text("n/a");
        } else {
            td.text(percentageFormat.format(counter.getCoveredRatio()));
        }
    }

    private String format(double ratio) {
        return percentageFormat.format(
                BigDecimal.valueOf(ratio).setScale(2, RoundingMode.FLOOR));
    }
}

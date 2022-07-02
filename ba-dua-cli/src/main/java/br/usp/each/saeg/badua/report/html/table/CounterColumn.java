package br.usp.each.saeg.badua.report.html.table;

import br.usp.each.saeg.badua.core.analysis.ClassCoverage;
import br.usp.each.saeg.badua.core.analysis.CoverageNode;
import br.usp.each.saeg.badua.core.analysis.MethodCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.HTMLElement;
import org.jacoco.report.internal.html.resources.Resources;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public abstract  class CounterColumn implements IColumnRenderer {

    public static CounterColumn newTotal(final String label, final Locale locale) {
        return new CounterColumn(label, locale) {
            @Override
            protected int getValue(ICounter counter) {
                return counter.getTotalCount();
            }
        };
    }

    public static CounterColumn newMissed (final String label, final Locale locale) {
        return new CounterColumn(label, locale) {
            @Override
            protected int getValue(ICounter counter) {
                return counter.getMissedCount();
            }
        };
    }

    private ICounter getNodeCounter (final CoverageNode node) {
        ICounter counter;

        if (node instanceof ClassCoverage)
            counter = node.getMethodCounter();
        else if (node instanceof MethodCoverage)
            counter = node.getDUCounter();
        else
            counter = node.getClassCounter();

        return counter;
    }

//    private final String nodeType;
    private final NumberFormat integerFormat;


    protected CounterColumn(final String label, final Locale locale) {
//        this.nodeType = label;
        this.integerFormat = NumberFormat.getIntegerInstance(locale);
    }

    public boolean init(final List<? extends ITableItem> items,
                        final CoverageNode total) {
        for (final ITableItem i : items) {
            if (this.getNodeCounter(i.getNode()).getTotalCount() > 0) {
                return true;
            }
        }
        return false;
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
        cell(td,item.getNode());
    }



    private void cell(final HTMLElement td, final CoverageNode node)
            throws IOException {
        final int value = getValue(getNodeCounter(node));
        td.text(integerFormat.format(value));
    }

    /**
     * Retrieves the respective value from the counter.
     *
     * @param counter
     *            counter object
     * @return value of interest
     */
    protected abstract int getValue(ICounter counter);
}

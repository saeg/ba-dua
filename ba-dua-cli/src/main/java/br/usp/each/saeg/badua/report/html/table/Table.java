package br.usp.each.saeg.badua.report.html.table;

import br.usp.each.saeg.badua.core.analysis.CoverageNode;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.HTMLElement;
import org.jacoco.report.internal.html.resources.Resources;
import org.jacoco.report.internal.html.resources.Styles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Table {

    /**
     * Sem aderir o Comparator*/

    private final List<Column> columns;
//    private Comparator<ITableItem> defaultComparator;

    public Table() {
        this.columns = new ArrayList<Table.Column>();
    }

    /**
     * Adciona uma nova coluna com as propriedades passadas
     * */
    public void add(final String header,
                    final String style,
                    final IColumnRenderer renderer) {
        columns.add(new Column(columns.size(), header, style, renderer));
//        if (defaultSorting) {
//            if (defaultComparator != null) {
//                throw new IllegalStateException(
//                        "Default sorting only allowed for one column.");
//            }
//            this.defaultComparator = renderer.getComparator();
//        }
    }

    /**
     * Renderização real da tabela
     */
    public void render(final HTMLElement parent,
                       final List<? extends ITableItem> items,
                       final CoverageNode total,
                       final Resources resources,
                       final ReportOutputFolder base)
            throws IOException {

//        Sem sort de itens por enquanto
//        final List<? extends ITableItem> sortedItems = sort(items);

        final HTMLElement table = parent.table(Styles.COVERAGETABLE);
        table.attr("id", "coveragetable");
        header(table, items, total);
        footer(table, total, resources, base);
        body(table, items, resources, base);
    }

    private void header(final HTMLElement table,
                        final List<? extends ITableItem> items,
                        final CoverageNode total)
            throws IOException {

        final HTMLElement tr = table.thead().tr();
        for (final Column c : columns) {
            c.init(tr, items, total);
        }
    }

    private void footer(final HTMLElement table,
                        final CoverageNode total,
                        final Resources resources,
                        final ReportOutputFolder base)
            throws IOException {
        final HTMLElement tr = table.tfoot().tr();
        for (final Column c : columns) {
            c.footer(tr, total, resources, base);
        }
    }

    private void body(final HTMLElement table,
                      final List<? extends ITableItem> items,
                      final Resources resources,
                      final ReportOutputFolder base)
            throws IOException {
        final HTMLElement tbody = table.tbody();
        int idx = 0;
        for (final ITableItem item : items) {
            final HTMLElement tr = tbody.tr();
            for (final Column c : columns) {
                c.body(tr, idx, item, resources, base);
            }
            idx++;
        }
    }

    private static class Column {

        private final char idprefix;
        private final String header;
        private final IColumnRenderer renderer;
//        private final SortIndex<ITableItem> index;
        private final String style, headerStyle;

        private boolean visible;

        /**
         * Construtor da Coluna
         * Passando qual será o renderizador base dela*/
        Column(final int idx,
               final String header,
               final String style,
               final IColumnRenderer renderer) {
            this.idprefix = (char) ('a' + idx);
            this.header = header;
            this.renderer = renderer;
//            index = new SortIndex<ITableItem>(renderer.getComparator());
            this.style = style;
            this.headerStyle = Styles.combine(null, Styles.SORTABLE, style);
//            this.headerStyle = Styles.combine(
//                    defaultSorting ? Styles.DOWN : null, Styles.SORTABLE,
//                    style);
        }

        /**
         * Iniciador da coluna*/
        void init(final HTMLElement tr,
                  final List<? extends ITableItem> items,
                  final CoverageNode total)
                throws IOException {
            visible = renderer.init(items, total);
            if (visible) {
//                index.init(items);
                final HTMLElement td = tr.td(headerStyle);
                td.attr("id", String.valueOf(idprefix));
                td.attr("onclick", "toggleSort(this)");
                td.text(header);
            }
        }

        void footer(final HTMLElement tr,
                    final CoverageNode total,
                    final Resources resources,
                    final ReportOutputFolder base)
                throws IOException {
            if (visible) {
                renderer.footer(tr.td(style), total, resources, base);
            }
        }

        void body(final HTMLElement tr,
                  final int idx,
                  final ITableItem item,
                  final Resources resources,
                  final ReportOutputFolder base)
                throws IOException {
            if (visible) {
                final HTMLElement td = tr.td(style);
                td.attr("id",
                        idprefix);
//                td.attr("id",
//                        idprefix + String.valueOf(index.getPosition(idx)));
                renderer.item(td, item, resources, base);
            }
        }

    }

}

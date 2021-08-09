package br.usp.each.saeg.badua.report.html.table;

import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.HTMLElement;
import org.jacoco.report.internal.html.resources.Resources;
import org.jacoco.report.internal.html.resources.Styles;
import org.jacoco.report.internal.html.table.IColumnRenderer;
import org.jacoco.report.internal.html.table.ITableItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Table {

    private final List<Column> colunas;
    private Comparator<ITableItem> defaultComparator;

    public Table() {
        this.colunas = new ArrayList<Table.Column>();
    }

    public void add(final String header, final String style,
                    final IColumnRenderer renderer, final boolean defaultSorting) {
        colunas.add(new Column(colunas.size(), header, style, renderer,
                defaultSorting));
        if (defaultSorting) {
            if (defaultComparator != null) {
                throw new IllegalStateException(
                        "Default sorting only allowed for one column.");
            }
            this.defaultComparator = renderer.getComparator();
        }
    }

    public void render(final HTMLElement parent,
                       final List<? extends ITableItem> items,
                       final Resources resources,
                       final ReportOutputFolder base)
            throws IOException {
        final HTMLElement table = parent.table(Styles.COVERAGETABLE);
        table.attr("id", "coveragetable");
        header(table, items);
        footer(table, resources, base);
        body(table, items, resources, base);
    }

    private void header(final HTMLElement table,
                        final List<? extends ITableItem> items)
            throws IOException {
        final HTMLElement tr = table.thead().tr();
        for (final Column c : colunas) {
            c.init(tr, items);
        }
    }

    private void footer(final HTMLElement table,
                        final Resources resources,
                        final ReportOutputFolder base)
            throws IOException {
        final HTMLElement tr = table.tfoot().tr();
        for (final Column c : colunas) {
            c.footer(tr, resources, base);
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
            for (final Column c : colunas) {
                c.body(tr, idx, item, resources, base);
            }
            idx++;
        }
    }

    private static class Column {

        private final char idprefix;
        private final String header;
        private final Column renderer;
        //private final SortIndex<ITableItem> index;
        private final String style, headerStyle;

        private boolean visible;

        Column(final int idx, final String header, final String style,
               final Column renderer, final boolean defaultSorting) {
            this.idprefix = (char) ('a' + idx);
            this.header = header;
            this.renderer = renderer;
//            index = new SortIndex<ITableItem>(renderer.getComparator());
            this.style = style;
            this.headerStyle = Styles.combine(
                    defaultSorting ? Styles.DOWN : null, Styles.SORTABLE,
                    style);
        }

        void init(final HTMLElement tr,
                  final List<? extends ITableItem> items)
                throws IOException {
//            visible = renderer.init(items, total);
            if (visible) {
//                index.init(items);
                final HTMLElement td = tr.td(headerStyle);
                td.attr("id", String.valueOf(idprefix));
                td.attr("onclick", "toggleSort(this)");
                td.text(header);
            }
        }

        void footer(final HTMLElement tr,
                    final Resources resources, final ReportOutputFolder base)
                throws IOException {
            if (visible) {
                renderer.footer(tr.td(style), total, resources, base);
            }
        }

        void body(final HTMLElement tr, final int idx, final ITableItem item,
                  final Resources resources, final ReportOutputFolder base)
                throws IOException {
            if (visible) {
                final HTMLElement td = tr.td(style);
                td.attr("id",
                        idprefix + String.valueOf(index.getPosition(idx)));
                renderer.item(td, item, resources, base);
            }
        }

    }

}

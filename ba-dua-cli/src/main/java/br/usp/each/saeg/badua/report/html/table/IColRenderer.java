package br.usp.each.saeg.badua.report.html.table;

import org.jacoco.report.internal.html.table.IColumnRenderer;
import org.jacoco.report.internal.html.table.ITableItem;

import java.util.List;

public interface IColRenderer extends IColumnRenderer {

    boolean init(List<? extends ITableItem> items);

}

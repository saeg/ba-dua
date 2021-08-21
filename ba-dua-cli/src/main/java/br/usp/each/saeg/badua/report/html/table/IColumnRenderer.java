package br.usp.each.saeg.badua.report.html.table;

import br.usp.each.saeg.badua.core.analysis.CoverageNode;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.HTMLElement;
import org.jacoco.report.internal.html.resources.Resources;

import java.io.IOException;
import java.util.List;

/**
 * Renderização de uma coluna simples para a tabela de cobertura
 * Sem comparator*/
public interface IColumnRenderer {

    boolean init(List<? extends ITableItem> items,
                 CoverageNode cor);

    void footer(HTMLElement td,
                CoverageNode cor,
                Resources resources,
                ReportOutputFolder base)
            throws IOException;

    void item(HTMLElement td,
              ITableItem item,
              Resources resources,
              ReportOutputFolder base)
            throws IOException;
}

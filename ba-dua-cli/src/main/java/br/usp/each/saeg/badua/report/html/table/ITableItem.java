package br.usp.each.saeg.badua.report.html.table;

import br.usp.each.saeg.badua.core.analysis.CoverageNode;
import org.jacoco.report.internal.html.ILinkable;

//Ainda não sei oq essa classe importa especificamente
public interface ITableItem extends ILinkable {

    CoverageNode getNode();
}

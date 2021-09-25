package br.usp.each.saeg.badua.report.html.page;

import br.usp.each.saeg.badua.cli.HTMLCoverageWriter;
import br.usp.each.saeg.badua.core.analysis.CoverageNode;
import br.usp.each.saeg.badua.report.html.table.ITableItem;

import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.HTMLElement;
import org.jacoco.report.internal.html.resources.Resources;
import org.jacoco.report.internal.html.resources.Styles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class TablePage extends ReportPage implements ITableItem {
	
	private final List<ITableItem> items = new ArrayList<ITableItem>();
	protected CoverageNode node;

	/**
	 * Construtor de uma novo node para Tabelas
	 * @param node nó de cobertura referente
	 * @param parent pagina-pai
	 * @param folder pasta base
	 * @param context contexto
	 */
	public TablePage(final CoverageNode node,
					 final ReportPage parent,
					 final ReportOutputFolder folder,
					 final HTMLCoverageWriter context) {
		super(parent, folder, context); //Chamada pro ReportPage
		this.node = node;
	}

	/**
	 * Adição de itens para serem renderizados na tabela
	 * @param item
	 */
	public void addItem(final ITableItem item){
		items.add(item);
	}

	/**
	 * Componente HEAD do report com tabelas
	 * @param head	-> É isso ai
	 * @throws IOException
	 */
	protected void head(final HTMLElement head) throws IOException {
		super.head(head);
		head.script(context.getResources().getLink(folder, Resources.SORT_SCRIPT));
	}

	/**
	 * Renderização do body com a tabela
	 * @param body -> Tag do body
	 * @throws IOException
	 */
	protected void content(final HTMLElement body) throws IOException {
		context.getBaduaTable().render(body, items, node, context.getResources(), folder);
		// free memory, otherwise we will keep the complete page tree:
		items.clear();
	}

	//Rever isso aqui
	public String getLinkStyle() {
		if(isRootPage()) {
			return Styles.EL_REPORT;
		} else {
			//Fazer um switch pra retornar conforme o tipo de cobertura
			return null;
		}
	}

	public String getLinkLabel() {
		if (node == null) {
			return "Project Level";
		}
		return node.getName();
	}

	public CoverageNode getNode() {
		return node;
	}
}

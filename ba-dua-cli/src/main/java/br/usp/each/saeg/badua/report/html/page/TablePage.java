package br.usp.each.saeg.badua.report.html.page;

import br.usp.each.saeg.badua.cli.HTMLCoverageWriter;
import br.usp.each.saeg.badua.report.html.table.Table;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.HTMLElement;
import org.jacoco.report.internal.html.resources.Resources;
import org.jacoco.report.internal.html.table.ITableItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TablePage extends ReportPage implements ITableItem{
	
	private final List<ITableItem> items = new ArrayList<ITableItem>();

	public TablePage(final ReportPage parent,
					 final ReportOutputFolder folder,
					 final HTMLCoverageWriter context) {
		super(parent, folder, context);
	}

	public void addItem(final ITableItem item){
		items.add(item);
	}

	@Override
	protected void head(final HTMLElement head) throws IOException {
		super.head(head);
		head.script(context.getResources().getLink(folder, Resources.SORT_SCRIPT));
	}

	@Override
	protected void content(final HTMLElement body) throws IOException {
		Table table = context.getTable();
		context.getTable().render(body, items, context.getResources(), folder);
		// free memory, otherwise we will keep the complete page tree:
		items.clear();
	}

	@Override
	public String getLinkLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLinkStyle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getFileName() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	//Node just be like Page
	//We don't have nodes here
	public ICoverageNode getNode() {
		return null;
	}
}

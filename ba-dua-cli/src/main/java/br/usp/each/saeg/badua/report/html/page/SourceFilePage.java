package br.usp.each.saeg.badua.report.html.page;

import br.usp.each.saeg.badua.cli.HTMLCoverageWriter;
import br.usp.each.saeg.badua.core.analysis.CoverageNode;
import br.usp.each.saeg.badua.core.analysis.SourceLineDefUseChain;
import br.usp.each.saeg.badua.report.html.table.ITableItem;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.HTMLElement;
import org.jacoco.report.internal.html.resources.Resources;

import java.io.IOException;
import java.io.Reader;

import static java.lang.String.format;

public class SourceFilePage extends ReportPage implements ITableItem {

    private final Reader sourceReader;
    private final int tabWidth;
    private final SourceLineDefUseChain dua;

    public SourceFilePage(final Reader sourceReader,
                          final int tabWidth,
                          final ReportPage parent,
                          final ReportOutputFolder folder,
                          final SourceLineDefUseChain dua) {
        super(parent, folder, new HTMLCoverageWriter());
        this.dua = dua;
        this.sourceReader = sourceReader;
        this.tabWidth = tabWidth;
    }

    @Override
    protected void content(HTMLElement body) throws IOException {
        final SourceHighlighter hl = new SourceHighlighter(context.getLocale(), dua);
        hl.render(body, sourceReader);
        sourceReader.close();

    }

    @Override
    protected void head(final HTMLElement head) throws IOException {
        super.head(head);
        head.link("stylesheet", context.getResources().getLink(folder,
                Resources.PRETTIFY_STYLESHEET), "text/css");
        head.script(context.getResources().getLink(folder,
                Resources.PRETTIFY_SCRIPT));
    }

    @Override
    protected String getOnload() {
        return format("window['PR_TAB_WIDTH']=%d;prettyPrint()",
                Integer.valueOf(tabWidth));
    }


    @Override
    protected String getFileName() {
        return null;
    }


    @Override
    public CoverageNode getNode() {
        return null;
    }

    @Override
    public String getLinkLabel() {
        return null;
    }

    @Override
    public String getLinkStyle() {
        return null;
    }
}

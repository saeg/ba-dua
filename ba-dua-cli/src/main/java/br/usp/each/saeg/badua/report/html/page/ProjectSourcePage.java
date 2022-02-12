package br.usp.each.saeg.badua.report.html.page;

import br.usp.each.saeg.badua.cli.HTMLCoverageWriter;
import br.usp.each.saeg.badua.core.analysis.ClassCoverage;
import br.usp.each.saeg.badua.core.analysis.CoverageNode;
import br.usp.each.saeg.badua.core.analysis.MethodCoverage;
import br.usp.each.saeg.badua.core.analysis.SourceLineDefUseChain;
import org.jacoco.report.ISourceFileLocator;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.ILinkable;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectSourcePage extends TablePage{

    private final ISourceFileLocator locator;
    private final Map<String, ILinkable> sourceFilePages;
    private final ILinkable projectPage;
    private final List<ClassCoverage> classes;

    public ProjectSourcePage (final CoverageNode projectNode,
                              final ReportPage parent,
                              final ISourceFileLocator locator,
                              final List<ClassCoverage> classes,
                              final ReportOutputFolder folder,
                              final ILinkable projectPage) {
        super(projectNode, parent, folder, new HTMLCoverageWriter());
        this.locator = locator;
        this.projectPage = projectPage;
        this.sourceFilePages = new HashMap<String, ILinkable>();
        this.classes = classes;
    }

    ILinkable getSourceFilePage(final String name) {
        return sourceFilePages.get(name);
    }

    public void render() throws IOException {
        renderSourceFilePages();
        super.render();
    }

    private final void renderSourceFilePages() throws IOException {
        for (final ClassCoverage cc : classes) {
            final String[] sourcename = cc.getName().split("/");
            final Reader reader = locator.getSourceFile(sourcename[0],sourcename[1] + ".java");

            if (reader == null)
                System.out.println("Arquivo não encontrado");
            else {
                for (final MethodCoverage mc : cc.getMethods()) {
                    for (final SourceLineDefUseChain dua : mc.getDefUses()) {
                        final SourceFilePage sourcePage = new SourceFilePage(reader,
                                locator.getTabWidth(), this, folder, dua);
                        sourcePage.render();
                        sourceFilePages.put(sourcename[1] + ".java", sourcePage);
                        addItem(sourcePage);
                    }
                }
            }
        }
    }

    @Override
    protected String getFileName() {
        return "index.source.html";
    }

    @Override
    protected String getOnload() {
        return "initialSort(['breadcrumb', 'coveragetable'])";
    }

    public String getLinkLabel() {
        return context.getLanguageNames().getPackageName(getNode().getName());
    }




}

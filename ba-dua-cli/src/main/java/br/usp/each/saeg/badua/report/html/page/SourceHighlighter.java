package br.usp.each.saeg.badua.report.html.page;

import br.usp.each.saeg.badua.core.analysis.SourceLineDefUseChain;
import org.jacoco.report.internal.html.HTMLElement;
import org.jacoco.report.internal.html.resources.Styles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Locale;

public class SourceHighlighter{

    private final Locale locale;
    SourceLineDefUseChain dua;
    private String lang;

    public SourceHighlighter (final Locale locale, SourceLineDefUseChain dua) {
        this.locale = locale;
        lang = "java";
        this.dua = dua;
    }

    public void setLanguage(final String lang) {
        this.lang = lang;
    }

    public void render(final HTMLElement parent,
                       final Reader contents)
        throws IOException {
        final HTMLElement pre = parent.pre(Styles.SOURCE + " lang-" + lang + " linenums");
        final BufferedReader lineBuffer = new BufferedReader(contents);
        String line;
        int nr = 0;
        while ((line = lineBuffer.readLine()) != null) {
            nr++;
            renderCodeLine(pre, line, nr);
        }
    }

    /**
     * Renderização das linhas do código fonte pra página html
     *
     * @param pre       -> tag HTML
     * @param linesrc   -> linha obtida no buffer
     * @param lineNr    -> número da linha
     * @throws IOException
     */
    private void renderCodeLine(final HTMLElement pre,
                                final String linesrc,
                                final int lineNr)
        throws IOException {
        highlight(pre, lineNr).text(linesrc);
        pre.text("\n");
    }

    HTMLElement highlight(final HTMLElement pre,
                          final int lineNr)
            throws IOException {
        final String lineId = "L" + Integer.toString(lineNr);
        final String style;

        //Se a linha for relacionada a dua sob análise, já retorna
        if (!(lineNr == dua.def || lineNr == dua.use || lineNr == dua.target)) return pre;

        style = dua.covered? Styles.FULLY_COVERED : Styles.NOT_COVERED;
        return pre.span(style, lineId);
    }


}

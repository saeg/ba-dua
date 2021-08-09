package br.usp.each.saeg.badua.report.html.page;

import br.usp.each.saeg.badua.cli.HTMLCoverageWriter;

import org.jacoco.core.JaCoCo;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.HTMLElement;
import org.jacoco.report.internal.html.ILinkable;
import org.jacoco.report.internal.html.resources.Resources;
import org.jacoco.report.internal.html.resources.Styles;

import java.io.IOException;

public abstract class ReportPage implements ILinkable {

	private final ReportPage parent;
	protected final ReportOutputFolder folder;
	protected final HTMLCoverageWriter context;

	public ReportPage(final ReportPage parent, final ReportOutputFolder folder, final HTMLCoverageWriter context) {
		this.parent = parent;
		this.context = context;
		this.folder = folder;
	}

	protected final boolean isRootPage() {
		return parent == null;
	}

	public void render() throws IOException {
		final HTMLElement html = new HTMLElement(folder.createFile(getFileName()), context.getOutputEncoding());
		html.attr("lang", context.getLocale().getLanguage());
		head(html.head());
		body(html.body());
		html.close();
	}

	protected void head(final HTMLElement head) throws IOException {
		head.meta("Content-Type", "text/html;charset=UTF-8");
		head.link("stylesheet", context.getResources().getLink(folder, Resources.STYLESHEET), "text/css");
		head.link("shortcut icon", context.getResources().getLink(folder, "report.gif"), "image/gif");
		head.title().text(getLinkLabel());
	}

	private void body(final HTMLElement body) throws IOException {
		body.attr("onload", getOnload());
		final HTMLElement navigation = body.div(Styles.BREADCRUMB);
		navigation.attr("id", "breadcrumb");
		infoLinks(navigation.span(Styles.INFO));
		breadcrumb(navigation, folder);
		body.h1().text(getLinkLabel());
		content(body);
		footer(body);
	}

	/**
	 * Returns the onload handler for this page.
	 *
	 * @return handler or <code>null</code>
	 */
	protected String getOnload() {
		return null;
	}

	/**
	 * Inserts additional links on the top right corner.
	 *
	 * @param span parent element
	 * @throws IOException in case of IO problems with the report writer
	 */
	protected void infoLinks(final HTMLElement span) throws IOException {
		span.a(context.getSessionsPage(), folder);
	}

	private void breadcrumb(final HTMLElement div, final ReportOutputFolder base) throws IOException {
		breadcrumbParent(parent, div, base);
		div.span(getLinkStyle()).text(getLinkLabel());
	}

	private static void breadcrumbParent(final ReportPage page, final HTMLElement div, final ReportOutputFolder base)
			throws IOException {
		if (page != null) {
			breadcrumbParent(page.parent, div, base);
			div.a(page, base);
			div.text(" > ");
		}
	}

	private void footer(final HTMLElement body) throws IOException {
		final HTMLElement footer = body.div(Styles.FOOTER);
		final HTMLElement versioninfo = footer.span(Styles.RIGHT);
		versioninfo.text("Created with ");
		versioninfo.a(JaCoCo.HOMEURL).text("Ba-dua");
		versioninfo.text(" ");
		versioninfo.text(JaCoCo.VERSION);
		footer.text(context.getFooterText());
	}

	protected abstract String getFileName();

	protected abstract void content(final HTMLElement body) throws IOException;

	// === ILinkable ===

	public final String getLink(final ReportOutputFolder base) {
		return folder.getLink(base, getFileName());
	}
}

package br.usp.each.saeg.badua.report.html.page;

import br.usp.each.saeg.badua.cli.HTMLCoverageWriter;
import br.usp.each.saeg.badua.core.analysis.ClassCoverage;
import br.usp.each.saeg.badua.core.analysis.CoverageNode;
import org.jacoco.report.ISourceFileLocator;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.HTMLElement;

import java.io.IOException;
import java.util.List;

/**
 * Pagina mostrando informações sobre o projeto
 * A pagina contém uma tabela que lista todas as classes cobertas
 */

public class ProjectPage extends TablePage{
	
	private final ISourceFileLocator locator;
	private List<ClassCoverage> classes;


	/**
	 * Construtor da página do projeto
	 *
	 * @param classes	-> Lista de cobertura das classes do projeto
	 * @param parent	-> Página pai
	 * @param locator	-> Localização do código fonte
	 * @param folder	-> Pasta da página
	 */
	public ProjectPage(final CoverageNode projectNode,
					   final List<ClassCoverage> classes,
					   final ReportPage parent,
					   final ISourceFileLocator locator,
					   final ReportOutputFolder folder) {

		super(projectNode, parent, folder, new HTMLCoverageWriter());
		this.classes = classes;
		this.locator = locator;
	}

//	private CoverageNode createProjectNode (List<ClassCoverage> classes) {
//		CoverageNode sum = new CoverageNode("Project");
//		for (ClassCoverage c : classes){
//			sum.increment(c);
//		}
//		return sum;
//	}

	/**
	 * Renderização da pagina
	 * @throws IOException
	 */
	public void render() throws IOException {
		renderClasses();
		super.render();

	}

	/**
	 * Renderização das classes
	 * @throws IOException
	 */
	private void renderClasses() throws IOException {
		for(ClassCoverage cc : classes) {
			//Getting Class Info
			final String className = cc.getName();
			final String folderName = className.length() == 0 ? "default"
					: className.replace('/', '.');

			//Rendering class' methods
			final ClassPage page = new ClassPage( cc, this, folder.subFolder(folderName));
			page.render();

			//Adding page to table
			addItem(page);

		}

	}

	protected String getOnload() {
		return "initialSort(['breadcrumb', 'coveragetable'])";
	}

	/**
	 * Nome padrão para arquivos referentes a projectPage
	 * @return
	 */
	protected String getFileName() {
		return "index.html";
	}

	/**
	 * Renderização do conteudo NA página de report page
	 * Verifica se há cobertura pra ser renderizada
	 * @param body
	 * @throws IOException
	 */
	protected void content(HTMLElement body) throws IOException {
		if(classes.isEmpty()) {
			body.p().text("No class files specified.");
		} else {
			super.content(body);
		}
	}
}

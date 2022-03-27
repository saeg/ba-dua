function getParams(url) {

    const urlParams = new URLSearchParams(url);

    let duaProperties = {
        varName :   urlParams.get('var'),
        covered:    (urlParams.get('covered') === 'true'),
        lines: {
            def:        urlParams.get('def'),
            use:        urlParams.get('use'),
            target:     null
        }
    }

    if(urlParams.has('target')) duaProperties.lines.target = urlParams.get('target')

    return duaProperties
}

function highlightLines (lines, covered) {
    let tab = 1;
    let classCov = covered ? 'fc' : 'nc'

    for (let line in lines) {
        if (!line) continue

        line = `L${lines[line]}`
        let element = document.getElementById(line)
        console.log(classCov + covered)
        element.classList.add(classCov)
        element.tabIndex = tab
        tab++;
    }
}

function DuaHighlight() {
    const queryString = window.location.search;
    let dua = getParams(queryString)

    highlightLines(dua.lines, dua.covered)

    document.getElementById(`L${dua.lines.def}`).focus()
}

window.addEventListener('load', (event) => {
    DuaHighlight()
});

let editor = monaco.editor.create($("#code-editor")[0], {
    value: [
        '/* Program Here */',
        'var x = 2',
        '',
        'var y',
        '',
        'y = 3',
        '',
        'x = y',
        'x += 2',
        'if y == 9 {',
        '   y += 2',
        '}',
        'end x'
    ].join('\n'),
    theme: 'vs-dark',
    language: 'csharp',
    glyphMargin: true,
    fontSize: 15,
    quickSuggestions: {
        "other": false,
        "comments": false,
        "strings": false
    },
    automaticLayout: true
});

let model = editor.getModel();
let decorations = [];

decorations = editor.createDecorationsCollection([
    {
        range: new monaco.Range(1, 1, 1, 1),
        options: {
            glyphMarginClassName: "margin-glyph",
            stickiness: monaco.editor.TrackedRangeStickiness.NeverGrowsWhenTypingAtEdges,
        },
    },
]);

/**
 * Tracks the current line number when manually stepping through the program using the left and right buttons.
 * @type {number}
 */
let lineStepTracker = 1;

/**
 * Sets the position of the marker showing the line number when stepping through the code.
 * @param lineNumber
 */
function setCurrentLineMarker(lineNumber = lineStepTracker) {
    decorations.set([
        {
            range: new monaco.Range(lineNumber, lineNumber, lineNumber, lineNumber),
            options: {
                glyphMarginClassName: "margin-glyph",
                stickiness: monaco.editor.TrackedRangeStickiness.NeverGrowsWhenTypingAtEdges,
            },
        }]
    )
}

/**
 * Event Listeners.
 */
let stdoutVarHtml = $("#stdout"); // The Variables Section of the HTML.

$("#step-left-btn").click(stepBackwards);
$("#step-right-btn").click(stepForwards);

$("#start-btn").click(function () {
    $("#step-right-btn").removeClass("disabled");
    $("#step-left-btn").removeClass("disabled");
    reset();
});

// Capture the enter key being released to work around the editors weird glyph issue.
$(document).on('keyup', function (e) {
    if (e.which === 13) {
        setCurrentLineMarker(lineStepTracker);
    }
});


/**
 * This function is called when the LEFT (Step Backwards) button is clicked on the HTML page.
 */
/**
 * This function is called when the LEFT (Step Backwards) button is clicked on the HTML page.
 */
function stepBackwards() {
    // Decrement the line marker
    if (lineStepTracker === 1) {
        console.log("Start of file, calling reset to clean up stacks.");
        reset();
        return;
    } else {
        lineStepTracker--;
    }
    console.log(lineStepTracker);
    if(reverse() === "error") {
        return;
    }
    setCurrentLineMarker(); // Set the new line marker position.
    renderVariablesHTML();
}

/**
 * This function is called when the RIGHT (Step Forwards) button is clicked on the HTML page.
 */
function stepForwards() {
    // Increment the line marker.
    if (lineStepTracker === model.getLineCount()) {
        console.log("End Of File!")
        return
    } else {
        lineStepTracker++;
    }
    console.log(lineStepTracker);
    if(forward() === "error") {
        return;
    }
    setCurrentLineMarker(); // Set the new line marker position.
    renderVariablesHTML();


}


/**
 * Reads the input script and initialises stacks
 */
let script = [];
let inverse = [];

let vars = {};

function reset() {
    // Init the editor marker position and set to first line.
    lineStepTracker = 1;
    setCurrentLineMarker();

    // init stacks
    script = model.getLinesContent().reverse();
    inverse = [];
    vars = {};

    // Append a new line to the program if it does not exist.  This allows us to step over and read the final line.
    const maxLine = model.getLineCount();
    if (model.getLineContent(maxLine) !== "") {
        const range = new monaco.Range(maxLine + 1, 1, maxLine + 1, 1);
        const id = {major: 1, minor: 1};
        const text = "\n";
        const op = {identifier: id, range: range, text: text, forceMoveMarkers: true};
        editor.executeEdits("my-source", [op]);
    }
    renderVariablesHTML();

    // for (let i = 0; i < len; i++) {
    //     step(script, inverse, vars)
    // }


    //return script, inverse, vars;
}

// for individual stepping
function forward() {
    console.log(script)
    console.log(inverse)
    return step(script, inverse, vars, 'F');
}

function reverse() {
    console.log(script)
    console.log(inverse)
    return step(inverse, script, vars, 'R');
}


/**
 * will pop the next line off of the stack and execute it, pushing the inverse of the line onto the "inverse" stack
 *
 * e.g. x += 1 is executed and x -= 1 is added to the inverse stack
 *
 *
 * to make it run backwards, pass the inverse as the script and the script as the inverse.
 *
 * Syntax:
 *<pre>
 *     - var {variable name}
 *      - initialises a variable to 0
 *     - var {variable name} = {number}
 *      - initialises a variable to the provided value
 *     - {variable name} = {number}
 *      - sets given variable to a given value
 *     - {variable name} = {variable name}
 *      - sets a given variable to the value of another variable
 *     - end {variable name}
 *      - deinitialises variable
 *     - {variable name} += {number}
 *      - add a given number to the variable
 *      - subtraction can be used instead
 *     - {variable name} += {variable name}
 *       - adds the value of a given variable to the value of another variable
 *       - subtraction can be used instead
 *     - if {variable name} == {number} {
 *       - continues if the variable value is equal to the number, skips if not
 *     - if {variable name} == {variable names} {
 *       - continues if the variable value is equal to the variable value, skips if not
 *       - for conditionals, vaid operations are:
 *         - >
 *         - <
 *         - ==
 *         - >=
 *         - <=
 *         - true
 *         - false
 *     - comments can also be used.
 *
 * </pre>
 *
 * @param script : [] , the stack that will be popped off of to run the next command.
 * @param inverse : [] , the stack to push the inverted commands once executed.
 * @param vars : {} , This holds key pairs of all variable names & values.
 */
function step(script, inverse, vars, dir) {

    let line = script.pop();

    const lineSplit = line.trim().split(" ");

    switch (true) {

        // variable declaration by value
        case /\s*var [a-z]+ = [0-9]+/.test(line) && !(lineSplit[1] in vars):
            inverse.push("end " + lineSplit[1]);
            vars[lineSplit[1]] = Number(lineSplit[3]);
            break;

        // variable declaration
        case /\s*var [a-z]+$/.test(line) && !(lineSplit[1] in vars):
            inverse.push("end " + lineSplit[1]);
            vars[lineSplit[1]] = 0;
            break;

        // variable set by value
        case /\s*[a-z]+ = [0-9]+/.test(line) && lineSplit[0] in vars:
            inverse.push(lineSplit[0] + " = " + vars[lineSplit[0]]);
            vars[lineSplit[0]] = Number(lineSplit[2]);
            break;

        // variable set by variable
        case /\s*[a-z]+ = [a-z]+/.test(line) && lineSplit[2] in vars && lineSplit[0] in vars:
            inverse.push(lineSplit[0] + " = " + vars[lineSplit[0]]);
            vars[lineSplit[0]] = Number(vars[lineSplit[2]]);
            break;

        // secret case for releasing variables
        case /\s*end [a-z]+/.test(line) && lineSplit[1] in vars:
            inverse.push("var " + lineSplit[1] + " = " + vars[lineSplit[1]]);
            delete vars[lineSplit[1]];
            break;

        // add by value
        case /\s*[a-z]+ \+= [0-9]+/.test(line) && lineSplit[0] in vars:
            inverse.push(lineSplit[0] + " -= " + lineSplit[2]);
            vars[lineSplit[0]] += Number(lineSplit[2]);
            break;

        // subtraction by value
        case /\s*[a-z]+ -= [0-9]+/.test(line) && lineSplit[0] in vars:
            inverse.push(lineSplit[0] + " += " + lineSplit[2]);
            vars[lineSplit[0]] -= Number(lineSplit[2]);
            break;

        // addition by variable
        case /\s*[a-z]+ \+= [a-z]+/.test(line) && lineSplit[0] in vars && lineSplit[2] in vars:
            inverse.push(lineSplit[0] + " -= " + lineSplit[2]);
            vars[lineSplit[0]] += Number(vars[lineSplit[2]]);
            break;

        // subtraction by variable
        case /\s*[a-z]+ -= [a-z]+/.test(line) && lineSplit[0] in vars && lineSplit[2] in vars:
            inverse.push(lineSplit[0] + " += " + lineSplit[2]);
            vars[lineSplit[0]] -= Number(vars[lineSplit[2]]);
            break;

        // beginning of conditional
        case /\s*if [a-z]+ [<>=]+ [0-9]+ {$/.test(line) && lineSplit[1] in vars:
            let result;
            switch (lineSplit[2]) {
                case "==":
                    result = vars[lineSplit[1]] === Number(lineSplit[3]);
                    processIf(result);
                    break;

                case ">":
                    result = vars[lineSplit[1]] > Number(lineSplit[3]);
                    processIf(result);
                    break;

                case ">=":
                    result = vars[lineSplit[1]] >= Number(lineSplit[3]);
                    processIf(result);
                    break;

                case "<":
                    result = vars[lineSplit[1]] < Number(lineSplit[3]);
                    processIf(result);
                    break;

                case "<=":
                    result = vars[lineSplit[1]] <= Number(lineSplit[3]);
                    processIf(result);
                    break;

                default:
                    console.log("if statement invalid");
            }
            break;


        // end of conditional (})
        case /\s*}/.test(line):
            if (inverse[inverse.length - 1] === "}") {
                inverse.push("if false {");
                // Skip The If Block Here and sync margin marker.
                console.log("pushed if false")
            } else {
                inverse.push("if true {");
                // If Block True, reverse through as normal.
                console.log("pushed if true");
            }

            if (dir === 'R' && inverse[inverse.length - 1] === "if false {") {
                console.log("Detected a false if block")
                while ((model.getLineContent(lineStepTracker--)).trim().slice(0, 2).trim() !== "if") {
                }
                lineStepTracker++;
                console.log(inverse);
            }
            break;


        // conditional boolean
        case /\s*if true|false {/.test(line):
            if (dir === 'R') lineStepTracker--;

            inverse.push("}");
            if (lineSplit[1] === "false") {
                while (script[script.length - 1] !== "}") {
                    lineStepTracker++;
                }
                lineStepTracker++;
            }
            break;

        // comments
        case /\s*\/\*\s?[a-zA-Z0-9 ]+\s?\*\//.test(line):
            inverse.push("/* */");
            break;

        // Empty / breaking lines.
        case line.trim() === "":
            inverse.push("");
            break;

        // syntax error
        default:
            console.log("syntax error");
            $("#step-right-btn").addClass("disabled");
            $("#step-left-btn").addClass("disabled");
            stdoutVarHtml.html(`<p>Syntax Error at line: ${lineStepTracker-1}. Check and reload program.</p>`);
            return "error";

    }

    function processIf(result) {
        inverse.push("}");
        console.log("pushed if " + result)
        if (!result) {
            while (script[script.length - 1] !== "}") {
                script.pop();
                lineStepTracker++;
            }
        }
    }
}

/**
 * Renders the HTML for the section headed Variables.
 * Iterate stack(s) and create <p> paragraph </p> elements to insert into the html page.
 */
function renderVariablesHTML() {
    let htmlContent = "";
    if (Object.keys(vars).length === 0) {
        htmlContent = `<p>No variables initialised</p>`;
    } else {
        const keys = Object.keys(vars);
        for (let i = 0; i < keys.length; i++) {
            htmlContent += `<p>${keys[i]} = ${vars[keys[i]]}</p>`;
        }
    }
    stdoutVarHtml.html(htmlContent);
}

renderVariablesHTML();

/**
 * Unit tests
 */
function performTests() {
    // test variable declaration
    // the empty quotes are there to ensure the whole script can get inverted before comparison
    unitTest(["", "var variable"], ["end variable"]);
    unitTest(["", "var x = 3"], ["end x"]);

    // test setting
    unitTest(["", "", "x = 2", "var x"], ["end x", "x = 0"]);
    unitTest(["", "", "x = y", "var x = 2", "var y"], ["end y", "end x", "x = 2"]);

    // test addition
    unitTest(["", "", "x += 2", "var x"], ["end x", "x -= 2"]);
    unitTest(["", "", "x += y", "var x", "var y"], ["end y", "end x", "x -= y"]);

    // test subtraction
    unitTest(["", "", "x -= 2", "var x"], ["end x", "x += 2"]);
    unitTest(["", "", "x -= y", "var x", "var y"], ["end y", "end x", "x += y"]);

    // test ending variable
    unitTest(["", "", "end x", "var x = 4"], ["end x", "var x = 4"]);

    // conditionals
    unitTest(["", "", "", "}", "y = 4", "if y == 3 {", "var y = 3"], ["end y", "}", "y = 3", "if true {"]);
    unitTest(["", "", "}", "y = 4", "if y == 3 {", "var y = 2"], ["end y", "}", "if false {"]);

    unitTest(["", "", "", "}", "y = 4", "if y <= 5 {", "var y = 3"], ["end y", "}", "y = 3", "if true {"]);
    unitTest(["", "", "}", "y = 4", "if y <= 5 {", "var y = 6"], ["end y", "}", "if false {"]);

    unitTest(["", "", "", "}", "y = 4", "if y >= 5 {", "var y = 6"], ["end y", "}", "y = 6", "if true {"]);
    unitTest(["", "", "}", "y = 4", "if y >= 5 {", "var y = 4"], ["end y", "}", "if false {"]);

    unitTest(["", "", "", "}", "y = 4", "if y < 5 {", "var y = 3"], ["end y", "}", "y = 3", "if true {"]);
    unitTest(["", "", "}", "y = 4", "if y < 5 {", "var y = 6"], ["end y", "}", "if false {"]);

    unitTest(["", "", "", "}", "y = 4", "if y > 5 {", "var y = 6"], ["end y", "}", "y = 6", "if true {"]);
    unitTest(["", "", "}", "y = 4", "if y > 5 {", "var y = 4"], ["end y", "}", "if false {"]);


}

function unitTest(script, testOutput) {
    reset();
    const message = "Testing step function: ";

    for (let i = 0; i < script.length; i++) {
        step(script, inverse, vars, 'F');
    }

    if (arrayEquals(testOutput, inverse)) {
        console.log(message + "[ PASSED ]");
    }
    else {
        console.log(message + "[ FAILED ]");
        console.log("Arrays are not equal.")
        console.log(inverse);
        console.log(testOutput);
        return "Test Failed";
    }
    return "Success: Tests Passed";
}

/**
 * Unit test helper functions
 */
function arrayEquals(a, b) {
    return Array.isArray(a) &&
        Array.isArray(b) &&
        a.length === b.length &&
        a.every((value, index) => value === b[index]);
}
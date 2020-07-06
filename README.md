# ba-dua

[![Maven Central](https://img.shields.io/maven-central/v/br.usp.each.saeg/ba-dua.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/br.usp.each.saeg/ba-dua)
[![License](https://img.shields.io/github/license/saeg/ba-dua.svg?style=flat-square)](LICENSE)
[![DOI](https://zenodo.org/badge/4232/saeg/ba-dua.svg?style=flat-square)](http://dx.doi.org/10.5281/zenodo.11006)

ba-dua (Bitwise Algorithm - powered Definition-Use Association coverage) is an intra-procedural data-flow testing tool for Java programs.

This is an implementation of the bitwise algorithm for data-flow testing proposed in:
"An efficient bitwise algorithm for intra-procedural data-flow testing coverage". Published in Information Processing Letters. Volume 113 Issue 8, April, 2013. Pages 293-300.

The implementation is described in: "Data-flow Testing in the Large". Published in IEEE International Conference on Software Testing, Verification and Validation (ICST) 2014.

ba-dua is still an experimental tool. If you are looking for a general purpose coverage tool we recommend [JaCoCo](http://www.eclemma.org/jacoco/). JaCoCo is definitely the best coverage tool for Java. If you are looking for mutation test we recommend [PIT](http://pitest.org/). If you don't know what data-flow coverage is, you can read [this link](http://www.bullseye.com/coverage.html#other_dataFlow).

## Download

Version `>= 0.3.0` are available at [Maven central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22br.usp.each.saeg%22%20AND%20ba-dua). Older version can be found at [releases](https://github.com/saeg/ba-dua/releases) page.

## Examples

### Offline instrumentation

To instrument Java classes you should use the **instrument** program.

```
java -jar ba-dua-cli-VERSION-all.jar instrument
```

after instrumentation, you should run the instrumented classes with ba-dua JAR in the *classpath*.

### Agent instrumentation

You can instead of offline instrumentation use the Java agent. Java agent instrument classes as they are loaded by the JVM. Just include ba-dua-agent-rt-VERSION-all.jar in the JVM Java agent option.

### Reporting

After program execution a new file (coverage.ser) will be created in your current directory. You should use the **report** program to assess the coverage. For default, the program print out the coverage for each class `(# of covered DU within the class/# of DU within the class)`. There is an option `-show-methods` that will print out the coverage for each method `(# of covered DU within the method/# of DU within the method)`. Alternatively, you can export a XML report with the option `-xml`. If the classes were compiled with debug information (lines and variables) the XML report will include each actual def-use pair (DU) that are covered or not.

```
java -jar ba-dua-cli-VERSION-all.jar report
```

## License

ba-dua is licensed under the Eclipse Public License - v 1.0 (http://www.eclipse.org/legal/epl-v10.html)

## Notice

ba-dua JAR is distributed with some classes from ASM (http://asm.ow2.org), args4j (http://args4j.kohsuke.org) and JaCoCo (http://www.eclemma.org/jacoco/) embedded (shaded). The command line interface tools were inspired by the pull request #86 from JaCoCo.

- ASM is distributed under the BSD License.
- args4j is distributed under the MIT License.
- JaCoCo is distributed under the Eclipse Public License - v 1.0.

*Any other included library is of our own and is authorized to be distributed.*

During our implementation we relied in part on JaCoCo's code. Any similarity is no mere coincidence.

# ba-dua

[![Build Status](https://img.shields.io/travis/saeg/ba-dua.svg?style=flat-square)](https://travis-ci.org/saeg/ba-dua)
[![Coverage Status](https://img.shields.io/coveralls/saeg/ba-dua.svg?style=flat-square)](https://coveralls.io/r/saeg/ba-dua)
[![Release](https://img.shields.io/github/release/saeg/ba-dua.svg?style=flat-square)](https://github.com/saeg/ba-dua/releases/latest)
[![License](https://img.shields.io/github/license/saeg/ba-dua.svg?style=flat-square)](LICENSE)
[![DOI](https://zenodo.org/badge/4232/saeg/ba-dua.svg?style=flat-square)](http://dx.doi.org/10.5281/zenodo.11006)

ba-dua (Bitwise Algorithm - powered Definition-Use Association coverage) is an intra-procedural data-flow testing tool for Java programs.

This is an implementation of the bitwise algorithm for data-flow testing proposed in:
"An efficient bitwise algorithm for intra-procedural data-flow testing coverage". Published in Information Processing Letters. Volume 113 Issue 8, April, 2013. Pages 293-300.

The implementation is described in: "Data-flow Testing in the Large". Published in IEEE International Conference on Software Testing, Verification and Validation (ICST) 2014.

ba-dua is still an experimental tool. If you are looking for a general purpose coverage tool we recommend [JaCoCo](http://www.eclemma.org/jacoco/). JaCoCo is definitely the better coverage tool for Java. If you are looking for mutation test we recommend [PIT](http://pitest.org/). If you don't know what data-flow coverage is, you can read [this](http://www.bullseye.com/coverage.html#other_dataFlow) definitions.

## Examples

### Offline instrumentation

To instrument Java classes you should use the **instrument** program.

```
java -jar ba-dua-VERSION-all.jar instrument
```

after instrumentation, you should run the instrumented classes with ba-dua JAR in the *classpath*.

### Agent instrumentation

You can instead of offline instrumentation use the Java agent. Java agent instrument classes as they are loaded by the JVM. Just include ba-dua JAR in the JVM Java agent option.

### Reporting

After program execution a new file (coverage.ser) will be created in your current directory. You should use the **report** program to visualize the program coverage.

```
java -jar ba-dua-VERSION-all.jar report
```

## License

ba-dua is licensed under the Eclipse Public License - v 1.0 (http://www.eclipse.org/legal/epl-v10.html)

## Notice

ba-dua JAR is distributed with ASM (http://asm.ow2.org) and args4j (http://args4j.kohsuke.org) embedded.
We also included two classes (ContentTypeDetector and CRC64) from JaCoCo project (http://www.eclemma.org/jacoco/). The only change in this classes was the package declaration. The command line interface tools is inspired by the pull request #86 from JaCoCo.

- ASM is distributed under the BSD License.
- args4j is distributed under the MIT License.
- JaCoCo is distributed under the Eclipse Public License - v 1.0.

*Any other included library is of our own and is authorized to be distributed.*

During our implementation we relied in part on JaCoCo's code. Any similarity is no mere coincidence.

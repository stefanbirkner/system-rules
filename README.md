# System Rules

[![Build Status Linux](https://travis-ci.org/stefanbirkner/system-rules.svg?branch=master)](https://travis-ci.org/stefanbirkner/system-rules) [![Build Status Windows](https://ci.appveyor.com/api/projects/status/vr0c93rssc6mhetl/branch/master?svg=true)](https://ci.appveyor.com/project/stefanbirkner/system-rules)

System Rules is a collection of JUnit rules for testing code which uses
`java.lang.System`.


## Installation

System Rules is available from
[Maven Central](http://search.maven.org/).

    <dependency>
      <groupId>com.github.stefanbirkner</groupId>
      <artifactId>system-rules</artifactId>
      <version>1.16.1</version>
    </dependency>

Please don't forget to add the scope `test` if you're using System
Rules for tests only.


## Usage

System Rules' documentation is stored in the `gh-pages` branch and is
available online at
http://stefanbirkner.github.io/system-rules/index.html


## Contributing

You have three options if you have a feature request, found a bug or
simply have a question about System Rules.

* [Write an issue.](https://github.com/stefanbirkner/system-rules/issues/new)
* Create a pull request. (See [Understanding the GitHub Flow](https://guides.github.com/introduction/flow/index.html))
* [Write a mail to mail@stefan-birkner.de](mailto:mail@stefan-birkner.de)


## Development Guide

System Rules is build with [Maven](http://maven.apache.org/). If you
want to contribute code than

* Please write a test for your change.
* Ensure that you didn't break the build by running `mvn test`.
* Fork the repo and create a pull request. (See [Understanding the GitHub Flow](https://guides.github.com/introduction/flow/index.html))

The basic coding style is described in the
[EditorConfig](http://editorconfig.org/) file `.editorconfig`.

System Rules supports [Travis CI](https://travis-ci.org/) (Linux) and
[AppVeyor](http://www.appveyor.com/) (Windows) for continuous
integration. Your pull request will be automatically build by both CI
servers.


## Release Guide

* Select a new version according to the
  [Semantic Versioning 2.0.0 Standard](http://semver.org/).
* Set the new version in `pom.xml` and in the `Installation` section of
  this readme.
* Commit the modified `pom.xml` and `README.md`.
* Run `mvn clean deploy` with JDK 6 or 7.
* Add a tag for the release: `git tag system-rules-X.X.X`

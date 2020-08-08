---
layout: page
title: Testing Environment Variables with Java
headline: Testing Environment Variables with Java
---
Environment variables are used for providing arguments to applications. This
is not different for Java. For applications that run in Kubernetes clusters or
other containerized setups it even seems to be the usual way of configuring
applications.

How to test Java code that depends on environment variables is a recurring
theme. An example is the [Stack Overflow](https://stackoverflow.com) question
["How to test code dependent on environment variables using
JUnit?"](https://stackoverflow.com/questions/8168884/how-to-test-code-dependent-on-environment-variables-using-junit).
This article presents different options for dealing with this challenge.

Java application have two ways for accessing environment variables. They can
read a single variable with `System.getenv("name")` or a map with all variables
with `System.getenv()`. Unfortunately these are both class methods and
therefore we cannot replace them with test doubles easily.

## System Rules and System Lambda

## Environment Class

## Configuration library

## PowerMock

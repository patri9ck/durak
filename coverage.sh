#!/bin/sh

sbt clean
sbt clean coverage test
sbt coverageReport

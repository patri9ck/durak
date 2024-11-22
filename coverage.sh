#!/bin/sh

sbt clean coverage test
sbt coverageReport

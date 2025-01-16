@echo off
call sbt clean
call sbt clean coverage test
call sbt coverageReport
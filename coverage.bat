@echo off
call sbt clean coverage test
call sbt coverageReport
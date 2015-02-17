#!/usr/bin/env bash

./hadoop fs -rmr /pagenodes
./hadoop fs -rmr /pagerels
./hadoop fs -rmr /categorynodes
./hadoop fs -rmr /categoryrels
./hadoop fs -rmr /*.csv
./hadoop fs -rmr /categoryrels-stage
./hadoop fs -rmr /ontologynodes
./hadoop fs -rmr /ontologyrels-stage
./hadoop fs -rmr /ontologyrels

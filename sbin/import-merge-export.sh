#!/usr/bin/env bash

./hadoop fs -cat "/pagenodes/part-*" | ./hadoop fs -put - /page_nodes.csv
./hadoop fs -cat "/pagerels/part-*" | ./hadoop fs -put - /page_rels.csv
./hadoop fs -cat "/categorynodes/part-*" | ./hadoop fs -put - /category_nodes.csv
./hadoop fs -cat "/categoryrels/part-*" | ./hadoop fs -put - /category_rels.csv
./hadoop fs -cat "/ontologynodes/part-*" | ./hadoop fs -put - /ontology_nodes.csv
./hadoop fs -cat "/ontologyrels/part-*" | ./hadoop fs -put - /ontology_rels.csv

rm ~/neo4j-batch-importer/*_*.csv

./hadoop fs -copyToLocal /page_nodes.csv ~/neo4j-batch-importer/page_nodes.csv
./hadoop fs -copyToLocal /page_rels.csv ~/neo4j-batch-importer/page_rels.csv
./hadoop fs -copyToLocal /category_nodes.csv ~/neo4j-batch-importer/category_nodes.csv
./hadoop fs -copyToLocal /category_rels.csv ~/neo4j-batch-importer/category_rels.csv
./hadoop fs -copyToLocal /ontology_nodes.csv ~/neo4j-batch-importer/ontology_nodes.csv
./hadoop fs -copyToLocal /ontology_rels.csv ~/neo4j-batch-importer/ontology_rels.csv

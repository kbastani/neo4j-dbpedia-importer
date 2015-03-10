
Download DBpedia Neo4j Database Files
----------------

The results of the code used to import DBpedia into Neo4j are available as a Neo4j data store (ex. `path/to/neo4j/data/graph.db`):

* https://s3-us-west-1.amazonaws.com/neo4j-sample-datasets/dbpedia/dbpedia-store.tar.bz2

Extract the `graph.db` folder to your Neo4j `data` folder and ensure that your configurations allow storage upgrades.

Import DBpedia into Neo4j
======================

This is a Spark application written in Scala that processes flat file RDF dumps of DBpedia.org and generates CSV files
that are used to generate Neo4j data store files.

## File inputs

DBpedia URI mapped to Wikipedia URI:

    Download:   http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/wikipedia_links_en.nt.bz2
    File size:  bzip2 compressed archive (261 MB)
    Header:     DBPEDIA_RESOURCE_URI, RDF_TYPE, WIKIPEDIA_PAGE_URI

Wikipedia page link graph:

    Download:   http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/page_links_en.nt.bz2
    File size:  bzip2 compressed archive (1.2 GB)
    Header:     DBPEDIA_RESOURCE_SRC, RDF_TYPE, DBPEDIA_RESOURCE_DST

Page titles mapped to DBpedia URI:

    Download:   http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/labels_en.nt.bz2
    File size:  bzip2 compressed archive (155 MB)
    Header:     DBPEDIA_RESOURCE_URI, RDF_TYPE, PAGE_NAME

DBpedia categories mapped to pages:

    Download:   http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/article_categories_en.nt.bz2
    File size:  bzip2 compressed archive (178 MB)
    Header:     DBPEDIA_RESOURCE_URI, RDF_TYPE, DBPEDIA_CATEGORY_URI

DBpedia ontology mapped to pages:

    Download:   http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/instance_types_en.nt.bz2
    File size:  bzip2 compressed archive (117 MB)
    Header:     DBPEDIA_RESOURCE_URI, RDF_TYPE, DBPEDIA_ONTOLOGY_URI

File outputs
------------

These file outputs are created as partitioned directories in HDFS. Each node file represents a node record in Neo4j with its properties and label. Each relationship file represents a relationship record that connects two nodes together by their `id` property.

    HDFS dir:   /pagenodes
    Header:     dbpedia, id, l:label, wikipedia, title

    HDFS dir:   /pagerels
    Header:     start, end, type

    HDFS dir:   /categorynodes
    Header:     dbpedia, id, l:label

    HDFS dir:   /categoryrels
    Header:     start, end, type

    HDFS dir:   /ontologynodes
    Header:     dbpedia, id, l:label, wikipedia, title

    HDFS dir:   /ontologyrels
    Header:     start, end, type

HDFS file merge
---------------

To export the partitioned results from Hadoop 1.0.4, you can run the following
HDFS file system commands from the $HADOOP_HOME directory.

    File name:  page_nodes.csv
    Command:    bin/hadoop fs -cat "/pagenodes/part-" | bin/hadoop fs -put - /page_nodes.csv

    File name:  page_rels.csv
    Command:    bin/hadoop fs -cat "/pagerels/part-" | bin/hadoop fs -put - /page_rels.csv

    File name:  category_nodes.csv
    Command:    bin/hadoop fs -cat "/categorynodes/part-" | bin/hadoop fs -put - /category_nodes.csv

    File name:  category_rels.csv
    Command:    bin/hadoop fs -cat "/categoryrels/part-" | bin/hadoop fs -put - /category_rels.csv

    File name:  ontology_nodes.csv
    Command:    bin/hadoop fs -cat "/ontologynodes/part-" | bin/hadoop fs -put - /ontology_nodes.csv

    File name:  ontology_rels.csv
    Command:    bin/hadoop fs -cat "/ontologyrels/part-" | bin/hadoop fs -put - /ontology_rels.csv

HDFS file export
----------------

To copy the CSV files off of HDFS and onto your local file system, run the following command:

    bin/hadoop fs -copyToLocal /page_nodes.csv ~/neo4j-batch-importer/page_nodes.csv
    bin/hadoop fs -copyToLocal /page_rels.csv ~/neo4j-batch-importer/page_rels.csv
    bin/hadoop fs -copyToLocal /category_nodes.csv ~/neo4j-batch-importer/category_nodes.csv
    bin/hadoop fs -copyToLocal /category_rels.csv ~/neo4j-batch-importer/category_rels.csv
    bin/hadoop fs -copyToLocal /ontology_nodes.csv ~/neo4j-batch-importer/ontology_nodes.csv
    bin/hadoop fs -copyToLocal /ontology_rels.csv ~/neo4j-batch-importer/ontology_rels.csv

License
----------------

Apache license version 2.0

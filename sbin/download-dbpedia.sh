#!/usr/bin/env bash

wget http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/article_categories_en.nt.bz2
wget http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/labels_en.nt.bz2
wget http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/page_links_en.nt.bz2
wget http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/wikipedia_links_en.nt.bz2
wget http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/skos_categories_en.nt.bz2
wget http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/instance_types_en.nt.bz2

bzip2 -d wikipedia_links_en.nt.bz2
bzip2 -d labels_en.nt.bz2
bzip2 -d page_links_en.nt.bz2
bzip2 -d article_categories_en.nt.bz2
bzip2 -d skos_categories_en.nt.bz2
bzip2 -d instance_types_en.nt.bz2

/root/ephemeral-hdfs/bin/hadoop fs -copyFromLocal /data/wikipedia_links_en.nt /wikipedia_links_en.nt
/root/ephemeral-hdfs/bin/hadoop fs -copyFromLocal /data/labels_en.nt /labels_en.nt
/root/ephemeral-hdfs/bin/hadoop fs -copyFromLocal /data/page_links_en.nt /page_links_en.nt
/root/ephemeral-hdfs/bin/hadoop fs -copyFromLocal /data/article_categories_en.nt /article_categories_en.nt
/root/ephemeral-hdfs/bin/hadoop fs -copyFromLocal /data/skos_categories_en.nt /skos_categories_en.nt
/root/ephemeral-hdfs/bin/hadoop fs -copyFromLocal /data/instance_types_en.nt /instance_types_en.nt

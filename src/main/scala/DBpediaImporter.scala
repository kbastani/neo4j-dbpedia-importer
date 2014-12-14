/**
 * Copyright (C) 2014 Kenny Bastani
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD

/**
 * This is a Spark application that processes flat file RDF dumps of DBpedia.org and generates CSV files
 * that are used to generate Neo4j data store files.
 *
 * File inputs
 * -----------
 *
 * - DBpedia URI mapped to Wikipedia URI:
 *    Download:   http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/wikipedia_links_en.nt.bz2
 *    File size:  bzip2 compressed archive (261 MB)
 *    Header:     DBPEDIA_RESOURCE_URI, RDF_TYPE, WIKIPEDIA_PAGE_URI
 *
 * - Wikipedia page link graph:
 *    Download:   http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/page_links_en.nt.bz2
 *    File size:  bzip2 compressed archive (1.2 GB)
 *    Header:     DBPEDIA_RESOURCE_SRC, RDF_TYPE, DBPEDIA_RESOURCE_DST
 *
 * - Page titles mapped to DBpedia URI:
 *    Download:   http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/labels_en.nt.bz2
 *    File size:  bzip2 compressed archive (155 MB)
 *    Header:     DBPEDIA_RESOURCE_URI, RDF_TYPE, PAGE_NAME
 *
 * - DBpedia categories mapped to pages:
 *    Download:   http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/article_categories_en.nt.bz2
 *    File size:  bzip2 compressed archive (178 MB)
 *    Header:     DBPEDIA_RESOURCE_URI, RDF_TYPE, DBPEDIA_CATEGORY_URI
 *
 * File outputs
 * ------------
 *
 * -  HDFS dir:   /pagenodes
 *    Header:     dbpedia, id, l:label, wikipedia, title
 *
 * -  HDFS dir:   /pagerels
 *    Header:     start, end, type
 *
 * -  HDFS dir:   /categorynodes
 *    Header:     dbpedia, id, l:label
 *
 * -  HDFS dir:   /categoryrels
 *    Header:     start, end, type
 *
 * HDFS file merge
 * ---------------
 *
 * To export the partitioned results from Hadoop 1.0.4, you can run the following
 * HDFS file system commands from the $HADOOP_HOME directory.
 *
 * -  File name:  page_nodes.csv
 *    Command:    bin/hadoop fs -cat "/pagenodes/part-*" | bin/hadoop fs -put - /page_nodes.csv
 *
 * -  File name:  page_rels.csv
 *    Command:    bin/hadoop fs -cat "/pagerels/part-*" | bin/hadoop fs -put - /page_rels.csv
 *
 * -  File name:  category_nodes.csv
 *    Command:    bin/hadoop fs -cat "/categorynodes/part-*" | bin/hadoop fs -put - /category_nodes.csv
 *
 * -  File name:  category_rels.csv
 *    Command:    bin/hadoop fs -cat "/categoryrels/part-*" | bin/hadoop fs -put - /category_rels.csv
 *
 * HDFS file export
 * ----------------
 *
 * To copy the CSV files off of HDFS and onto your local file system, run the following command:
 *
 * -  Command: bin/hadoop fs -copyToLocal /page_nodes.csv ~/neo4j-batch-importer/page_nodes.csv
 * -  Command: bin/hadoop fs -copyToLocal /page_rels.csv ~/neo4j-batch-importer/page_rels.csv
 * -  Command: bin/hadoop fs -copyToLocal /category_nodes.csv ~/neo4j-batch-importer/category_nodes.csv
 * -  Command: bin/hadoop fs -copyToLocal /category_rels.csv ~/neo4j-batch-importer/category_rels.csv
 */
object DBpediaImporter {

  val conf = new SparkConf().setAppName("Simple Application").setMaster("local[8]")
  val sc = new SparkContext(conf)


  def main(args: Array[String]) {
    val wikiLinksFile = sc.textFile(Configuration.WIKI_LINKS_FILE_NAME)
    val wikiNamesFile = sc.textFile(Configuration.WIKI_NAMES_FILE_NAME)
    val pageLinksFile = sc.textFile(Configuration.PAGE_LINKS_FILE_NAME)

    val wikiLinksMap = processWikiLinks(wikiLinksFile)
    val wikiNamesMap = processWikiNames(wikiNamesFile)

    val pageNodeData = joinWikiNamesLinks(wikiLinksMap, wikiNamesMap)
    val pageNodeRows = generatePageNodes(pageNodeData)

    val pageNodeIndex = pageNodeData.map(r => {
      r._1
    }).zipWithUniqueId().collectAsMap()

    val pageLinkRelationshipData = processPageLinks(pageLinksFile)
    val pageLinkRelationshipRows = encodePageLinks(pageLinkRelationshipData, pageNodeIndex)

    pageNodeRows.saveAsTextFile(Configuration.HDFS_HOST + "pagenodes")
    pageLinkRelationshipRows.saveAsTextFile(Configuration.HDFS_HOST + "pagerels")
  }

  /**
   * Process Wikipedia Links RDF file
   * @param wikiLinksFile
   * @return Returns an RDD[String] map of filtered lines for import into Neo4j
   */
  def processWikiLinks(wikiLinksFile: RDD[String]): RDD[String] = {
    val wikiLinksMap = wikiLinksFile.filter(line =>
      line.contains(Configuration.PRIMARY_TOPIC_URL) &&
        !line.contains(Configuration.EXCLUDE_FILE_PATTERN))
      .map(e => {
      e.split("(?<=>)\\s(?=<)|\\s\\.$")
        .filter(a => {
        !a.contains()
      })
    })
      .map(uri => {
      (uri(1), uri(0))
    })
      .map(line => {
      line._1 + " " + line._2
    })

    wikiLinksMap
  }

  /**
   *
   * @param wikiNamesFile
   * @return
   */
  def processWikiNames(wikiNamesFile: RDD[String]): RDD[String] = {
    val wikiNamesMap = wikiNamesFile.filter(line => line.contains(Configuration.RDF_LABEL_URL))
      .filter(line => !line.contains(Configuration.EXCLUDE_FILE_PATTERN))
      .map(e => {
      e.split("(?<=>)\\s(?=<)|(?<=>)\\s(?=\\\")|@en\\s\\.$")
        .filter(a => { !a.contains(Configuration.RDF_LABEL_URL) })
    })
      .map(uri => { (uri(0), uri(1)) })
      .map(line => { line._1 + " " + line._2 })

    wikiNamesMap
  }

  /**
   *
   * @param wikiLinksMap
   * @param wikiNamesMap
   * @return
   */
  def joinWikiNamesLinks(wikiLinksMap: RDD[String], wikiNamesMap: RDD[String]): RDD[(String, String)] = {
    val joinedList = wikiLinksMap.union(wikiNamesMap)
      .map(line => {
      val items = line.split("^<|>\\s<|\\>\\s\\\"|\\\"$|>$")
        .filter(!_.isEmpty);

      val mapResult = if (items.length >= 2) (items(0), items(1)) else ("N/A", "N/A")

      mapResult
    }).filter(items => items._1 != "N/A").map(a => (a._1, a._2))

    joinedList
  }

  /**
   *
   * @param pageNodeData
   * @return
   */
  def generatePageNodes(pageNodeData: RDD[(String, String)]): RDD[String] = {
    val header = sc.parallelize(Seq(Configuration.PAGE_NODES_CSV_HEADER).toList)
    val rows = pageNodeData.zipWithUniqueId().map(e => {
      e._1._1 + "\t" + e._2 + "\t" + "Page\t" + e._1._2.toList.mkString("\t")
    })

    val result = header.union(rows)

    result
  }

  /**
   *
   * @param pageLinks
   * @param pageNodeIndex
   * @return
   */
  def encodePageLinks(pageLinks: RDD[(Array[String])], pageNodeIndex: scala.collection.Map[String, Long]): RDD[(Long, Long)] = {
    // Filter out bad links
    val encodedPageLinksResult = pageLinks.filter(uri => {
      !(pageNodeIndex.getOrElse(uri(0), -1) == -1 || pageNodeIndex.getOrElse(uri(1), -1) == -1)
    }).map(uri => {
      (pageNodeIndex(uri(0)), pageNodeIndex(uri(1)))
    })

    encodedPageLinksResult
  }

  /**
   *
   * @param pageLinksFile
   * @return
   */
  def processPageLinks(pageLinksFile: RDD[String]): RDD[(Array[String])] = {
    val pageLinks = pageLinksFile.filter(line =>
      line.contains(Configuration.WIKI_PAGE_LINK_URL) &&
        !line.contains(Configuration.EXCLUDE_FILE_PATTERN) &&
        !line.contains(Configuration.EXCLUDE_CATEGORY_PATTERN))
      .map(e => {
      e.split("^<|>\\s<|\\>\\s\\\"|>\\s\\.$")
        .filter(!_.isEmpty)
        .filter(a => { !a.contains(Configuration.WIKI_PAGE_LINK_URL) })
    })

    pageLinks
  }

  /**
   *
   * @param pageLinkResults
   * @return
   */
  def generatePageLinkRelationships(pageLinkResults: RDD[(String, String)]): RDD[String] = {
    val relHeader = sc.parallelize(Seq(Configuration.PAGE_LINKS_CSV_HEADER).toList)
    val relRows = pageLinkResults.map(line => { line._1 + "\t" + line._2 + "\tHAS_LINK" }).map(line => line.toString)
    val relResult = relHeader.union(relRows)

    relResult
  }
}

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
object Configuration {

  def HDFS_HOST: String = "hdfs://localhost:9000/"

  def PRIMARY_TOPIC_URL: String = "<http://xmlns.com/foaf/0.1/primaryTopic>"

  def RDF_LABEL_URL: String = "<http://www.w3.org/2000/01/rdf-schema#label>"

  def RDF_ONTOLOGY_URL: String = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>"

  def RDF_CATEGORY_URL: String = "<http://purl.org/dc/terms/subject>"

  def CATEGORY_SKOS_URL: String = "<http://www.w3.org/2004/02/skos/core#broader>"

  def WIKI_PAGE_LINK_URL: String = "http://dbpedia.org/ontology/wikiPageWikiLink"

  def EXCLUDE_FILE_PATTERN: String = "http://dbpedia.org/resource/File:"

  def EXCLUDE_CATEGORY_PATTERN: String = "http://dbpedia.org/resource/Category:"

  def WIKI_LINKS_FILE_NAME: String = HDFS_HOST + "wikipedia_links_en.nt"

  def WIKI_NAMES_FILE_NAME: String = HDFS_HOST + "labels_en.nt"

  def PAGE_LINKS_FILE_NAME: String = HDFS_HOST + "page_links_en.nt"

  def CATEGORIES_FILE_NAME: String = HDFS_HOST + "article_categories_en.nt"

  def CATEGORY_SKOS_FILE_NAME: String = HDFS_HOST + "skos_categories_en.nt"

  def INSTANCE_TYPES_FILE_NAME: String = HDFS_HOST + "instance_types_en.nt"

  def PAGE_NODES_CSV_HEADER: String = "dbpedia\tid\tl:label\twikipedia\ttitle";

  def CATEGORY_NODES_CSV_HEADER: String = "id\tl:label\tdbpedia\ttitle";

  def ONTOLOGY_NODES_CSV_HEADER: String = "id\tl:label\tdbpedia\ttitle";

  def PAGE_LINKS_CSV_HEADER: String = "start\tend\ttype";
}

# CreditCardSummary
Spring Boot API for parsing CSV and PDF monthly statements ( Only works with TD bank )


A simple spring boot app that processes Credit card statements in to a basic model and does some basic aggregations.

CSV files are read as is, But PDF's are encrypedted (Symcor does this) hence they are copied as Text files and parsed.

docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.10.2



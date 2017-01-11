# WikiPlag REST API

provided REST Interface to access the plagiarism and the database 

## Build and Run local

```bash
./sbt
> jetty:start
> browse
```

## Build Artifact
```bash
sbt clean assembly
```

## Run
https://spark.apache.org/docs/latest/quick-start.html

$ YOUR_SPARK_HOME/bin/spark-submit \
  --class "SimpleApp" \
  --master local[4] \
  target/scala-2.11/simple-project_2.11-1.0.jar
```bash
java -jar wikiplag_backend.jar
```
Browse to <localhost:8080/>

## Routes

##### GET /
test page

##### GET /wikiplag/document/:id
Gibt den Text eines Wikipediaartikels zurück.

Response Body
```json
{
  "doc_id": 1337,
  "title": "title",
  "text": "text..." 
}
```

##### POST /wikiplag/analyse
Führt eine Plagiatsanalyse durch und gibt eine Trefferliste zurück.

Request Body
```json
{
  "text": "text text text..." 
}
```

Response Body
```json
{
  "hits": [
    {
      "title": "title",
      "doc_id": 1337,
      "score": 100,
      "preview": "word <em>text1</em> word 2",
      "origin": "text <em>text1</em> text3 text2 text1" 
    },
    {
      "title": "title 2",
      "doc_id": 1338,
      "score": 100,
      "preview": "word <em>text1</em> word 2",
      "origin": "text <em>text1</em> text3 text2 text1" 
    },
    ...
  ]
}
```
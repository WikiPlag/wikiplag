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
  "tagged_input_text": "Das ist ein tolles <span class=\"input_plag\" id=\"0\">Plagiat. Bla bla.</span> Hier geht <span class=\"input_plag\" id=\"1\">der Text</span> weiter.",
  "plags": [
    {
      "id": 0,
      "wiki_excerpts": [
        {
          "title": "Hund",
          "id": 4,
          "start": 5,
          "end": 7,
          "excerpt": "[...] Test Test Bla Test Blubb <span class=\"wiki_plag\">Plagiat. Bla bla.</span> Hund Bla Test Bellen Blubb [...]" 
        },
        {
          "title": "Katze",
          "id": 9,
          "start": 20,
          "end": 22,
          "excerpt": "[...] Katze Bla Test Miau Blubb <span class=\"wiki_plag\">Plagiat. Bla bla.</span> Test Test Bla Test Blubb [...]" 
        }
      ]
    },
    {
      "id": 1,
      "wiki_excerpts": [
        {
          "title": "Giraffe",
          "id": 201,
          "start": 11,
          "end": 12,
          "excerpt": "[...] Test Test Bla Test Blubb <span class=\"wiki_plag\">der Text</span> Hund Bla Test Gelb Blubb [...]" 
        }
      ]
    }
  ]
}
```
# WikiPlag - Plattform zur Erkennung von Wikpediaplagiaten

## Übersicht

Projektstudiumsaufgabe im SS16 an der HTW Berlin, betreut von Prof. Dr.-Ing. Hendrik Gärtner

## Aufbau
*mongodb*, *forwardreferencetable*, *viewindex*, und *parser* sind Teil-Projekte. 

Sie werden im den *sparkapp* projekt als Backend verwendet und im *webapp* projekt als frontend.

*similarity*, *analysisJob*, *stopwordfinder* sind teilprojekt für einzelne aufgaben gewesen, die aktuell nicht verwendet werden.

## Build
```
sbt package assembly
```
Goal: package --> Webapp

Goal: assembly --> Backend fat-jar

### Artefakte

Als Ergeniss des Bauprozesses sind folgende Artefakte wichtig: 

sparkapp\target\scala-2.10\wikiplag_sparkapp.jar --> Frontend 

webapp\target\scala-2.10\webapp_2.10-0.0.1.war --> Backend (Wikipediadatensatz -> MongoDB Einträge)

## Try it
http://wikiplag.f4.htw-berlin.de/

## Links
[Redmine](https://studi.f4.htw-berlin.de/redmine/projects/0-wikiplag) 

[Aufgabe](http://puck.f4.htw-berlin.de/hgaertner/veranstaltungen/projektstudium/ss-2016/)

[sbt assembly plugin](https://github.com/sbt/sbt-assembly)

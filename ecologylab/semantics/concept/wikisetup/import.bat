set class=mwdumper.jar;mysql-connector-java-5.1.13-bin.jar
set data="C:\wikidata\enwiki-20100904-pages-articles.xml"

java -server -classpath %class% org.mediawiki.dumper.Dumper "--output=mysql://127.0.0.1/wikidb?user=wikiuser&password=formediawiki&characterEncoding=utf8" "--format=sql:1.5" %data%

pause

@echo NOTICE: you need to install cygwin to run this script

set wikidata_dir=C:/wikidata
set wikidata_date=20100904

set redirect_table=%wikidata_dir%/enwiki-%wikidata_date%-redirect.sql
set pagelinks_table=%wikidata_dir%/enwiki-%wikidata_date%-pagelinks.sql
set m_pagelinks_table=%wikidata_dir%/m-enwiki-%wikidata_date%-pagelinks.sql

rem grep -v "^/\*!40000 ALTER TABLE" %pagelinks_table% > %m_pagelinks_table%

rem mysql -f -B -n --default-character-set=utf8 -Dwikidb -uwikiuser -pformediawiki < %redirect_table%
mysql -f -B -n --default-character-set=utf8 -Dwikidb -uwikiuser -pformediawiki < %m_pagelinks_table%

pause


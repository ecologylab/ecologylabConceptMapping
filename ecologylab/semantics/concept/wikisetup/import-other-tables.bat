set wikidata_dir=C:/wikidata
set wikidata_date=20100904

set category_table=%wikidata_dir%/enwiki-%wikidata_date%-category.sql
set categorylinks_table=%wikidata_dir%/enwiki-%wikidata_date%-categorylinks.sql
set pagelinks_table=%wikidata_dir%/enwiki-%wikidata_date%-pagelinks.sql
set redirect_table=%wikidata_dir%/enwiki-%wikidata_date%-redirect.sql

del /q tmp_import.sql
echo set foreign_key_checks=0;					>> tmp_import.sql

echo select "importing %category_table% ..." as " ";		>> tmp_import.sql
echo source %category_table%;					>> tmp_import.sql

echo select "importing %categorylinks_table% ..." as " ";	>> tmp_import.sql
echo source %categorylinks_table%;				>> tmp_import.sql

echo select "importing %redirect_table% ..." as " ";		>> tmp_import.sql
echo source %redirect_table%;					>> tmp_import.sql

echo select "importing %pagelinks_table% ..." as " ";		>> tmp_import.sql
echo source %pagelinks_table%;					>> tmp_import.sql

echo set foreign_key_checks=1;					>> tmp_import.sql

pause

mysql -f -B -n --default-character-set=utf8 -Dwikidb -uwikiuser -pformediawiki < tmp_import.sql

del /q tmp_import.sql

pause

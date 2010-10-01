To setup a local mirror of Wikipedia:

1. Download and install XAMPP;

2. Download and install MediaWiki;

3. Download and compile MWDumper, officially maintained by MediaWiki; (You may want to apply a
   patch for bug 13721 first: https://bugzilla.wikimedia.org/show_bug.cgi?id=13721)

4. Download MySql Connector/J;

5. Download Wikipedia dump: pages-articles.xml.bz2;

6. Change variable max_allowed_packet to 32M or bigger in MySql configuration file and restart it;
    
7. Set variables referring to MWDumper, MySql Connector/J and Wikipedia pages-articles.xml in
   import.bat; correct MySql database name, user name and password in the connection string;
   
8. Run import.bat;

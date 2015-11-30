# fb-osmose-bridge
generates Osmose configuration files from data sources like Fishbase and SeaLifeBase

# usage

1. download osmose 3 update 2 from http://www.osmose-model.org/downloads
1. expand to ```[OSMOSE_INSTALL_DIR]```
1. download osmose configuration at https://fbob.herokuapp.com/osmose_config.zip
1. expand config.zip to ```[SOME_DIR]```
1. open a terminal and go to directory ```[OSMOSE_INSTALL_DIR]/dist```
1. run osmose simulation using ```java -jar osmose_stable_3.jar [SOME_DIR]/osm_all-parameters.csv .``` in the osmose install directory

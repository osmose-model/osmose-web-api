# fb-osmose-bridge
generates OSMOSE configuration files from data sources like Fishbase and SeaLifeBase

[![Build Status](https://travis-ci.org/jhpoelen/fb-osmose-bridge.png)](https://travis-ci.org/jhpoelen/fb-osmose-bridge)

# usage

1. download OSMOSE 3 update 2 from http://www.osmose-model.org/downloads
1. expand to ```[OSMOSE_INSTALL_DIR]```
1. download OSMOSE configuration at https://fbob.herokuapp.com/osmose_config.zip (static configuration) or https://fbob.herokuapp.com/osmose_config.zip?htlGroupName=ScomberomorusCavalla&htlGroupName=LutjanusCampechanus&htlGroupName=EpinephelusMorio (dynamic configuration with three named groups: _Scomberomorus cavalla_, _Lutjanus campechanus_ and _Epinephelus morio_.).
1. expand config.zip to ```[SOME_DIR]```
1. open a terminal and go to directory ```[OSMOSE_INSTALL_DIR]/dist```
1. run OSMOSE simulation using ```java -jar osmose_stable_3.jar [SOME_DIR]/osm_all-parameters.csv .``` in the OSMOSE install directory

output should look something like:

```
osmose[info] - *********************************
osmose[info] - Osmose model - Copyright 2013 IRD
osmose[info] - *********************************
osmose[info] - Osmose version Osmose 3 Update 2 (2015/03/01)
osmose[info] - Running configuration /Volumes/Data/Users/unencrypted/jorrit/Downloads/osmose_config/osm_all-parameters.csv
osmose[info] - Loading parameters from file /Volumes/Data/Users/unencrypted/jorrit/Downloads/osmose_config/osm_all-parameters.csv
osmose[info] -   Loading parameters from file /Volumes/Data/Users/unencrypted/jorrit/Downloads/osmose_config/osm_param-output.csv
```

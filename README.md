# osmose-web-api
Service that generates OSMOSE configuration files from data sources like Fishbase and SeaLifeBase. Please see our [wiki](https://github.com/jhpoelen/fb-osmose-bridge/wiki) for more information.


[![Build Status](https://travis-ci.org/jhpoelen/fb-osmose-bridge.png)](https://travis-ci.org/jhpoelen/fb-osmose-bridge)

# prerequisites
1. download OSMOSE 3 update 2 from http://www.osmose-model.org/downloads
1. expand to ```[OSMOSE_INSTALL_DIR]```

# usage

## generate configuration
1. (alt 1, static, for machines) download OSMOSE configuration at https://fbob.herokuapp.com/osmose_config.zip
1. (alt 2, single species focal groups, for machines) https://fbob.herokuapp.com/osmose_config.zip?focalGroupName=ScomberomorusCavalla&focalGroupName=LutjanusCampechanus&focalGroupName=EpinephelusMorio (dynamic configuration with three named groups: _Scomberomorus cavalla_, _Lutjanus campechanus_ and _Epinephelus morio_.).
1. (alt 3, defined focal and background groups, for machines) by sending http post request. For example ```curl -X POST --data @exampleConfig.json --header "Content-Type:application/json;charset=UTF-8" https://fbob.herokuapp.com/v2/osmose_config.zip > my_osmose_config.zip``` . An example of a request json can be found here [exampleConfig.json](https://raw.githubusercontent.com/jhpoelen/fb-osmose-bridge/master/src/test/resources/com/github/jhpoelen/fbob/exampleConfig.json).
1. (alt 4, for machines) use javascript library available at https://github.com/jhpoelen/fb-osmose-bridge-js to generate an osmose configuration for parameters defined in json.
1. (alt 5, for humans) use wizard at http://fin-casey.github.io and download generated configuration archive.
1. expand config.zip to ```[SOME_DIR]```

## run simulation
1. open a terminal and go to directory ```[OSMOSE_INSTALL_DIR]/dist```
1. run OSMOSE simulation using ```java -jar osmose_stable_3.jar [SOME_DIR]/osm_all-parameters.csv .``` in the OSMOSE install directory

The output should look something like:

```
osmose[info] - *********************************
osmose[info] - Osmose model - Copyright 2013 IRD
osmose[info] - *********************************
osmose[info] - Osmose version Osmose 3 Update 2 (2015/03/01)
osmose[info] - Running configuration /Volumes/Data/Users/unencrypted/jorrit/Downloads/osmose_config/osm_all-parameters.csv
osmose[info] - Loading parameters from file /Volumes/Data/Users/unencrypted/jorrit/Downloads/osmose_config/osm_all-parameters.csv
osmose[info] -   Loading parameters from file /Volumes/Data/Users/unencrypted/jorrit/Downloads/osmose_config/osm_param-output.csv
```

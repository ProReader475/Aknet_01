require: regions.csv
  name = Regions
  var = $Regions

init:
    if (!$global.$converters) {
        $global.$converters = {};
    }
    $global.$converters
        .RegionsConverter = function(parseTree) {
            var id = parseTree.Regions[0].value;
            return $Regions[id].value;
        };

patterns:
    $regions = $entity<Regions> || converter = $converters.RegionsConverter
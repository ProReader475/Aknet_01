require: adresses_b.csv
  name = adresses_b
  var = $adresses_b

init:
    if (!$global.$converters) {
        $global.$converters = {};
    }
    $global.$converters
        .adresses_bConverter = function(parseTree) {
            var id = parseTree.adresses_b[0].value;
            return $adresses_b[id].value;
        };

patterns:
    $adresses = $entity<adresses_b> || converter = $converters.adresses_bConverter
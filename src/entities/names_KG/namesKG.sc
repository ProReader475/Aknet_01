require: namesKG.csv
  name = NamesKG
  var = $NamesKG

init:
    if (!$global.$converters) {
        $global.$converters = {};
    }
    $global.$converters
        .NamesKGConverter = function(parseTree) {
            var id = parseTree.NamesKG[0].value;
            return $NamesKG[id].value;
        };

patterns:
    $namesKG = $entity<NamesKG> || converter = $converters.NamesKGConverter
require: surname.csv
    name = Surnames
    var = $Surnames

init:
    if (!$global.$converters) {
        $global.$converters = {};
    }
    $global.$converters
        .SurnamesConverter = function(parseTree) {
            var id = parseTree.Surnames[0].value;
            return $Surnames[id].value;
        };

patterns:
    $surnames = $entity<Surnames> || converter = $converters.SurnamesConverter
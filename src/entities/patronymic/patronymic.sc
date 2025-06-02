require: patronymic.csv
    name = Patronymics
    var = $Patronymics

init:
    if (!$global.$converters) {
        $global.$converters = {};
    }
    $global.$converters
        .PatronymicsConverter = function(parseTree) {
            var id = parseTree.Patronymics[0].value;
            return $Patronymics[id].value;
        };

patterns:
    $patronymicKG = $entity<Patronymics> || converter = $converters.PatronymicsConverter
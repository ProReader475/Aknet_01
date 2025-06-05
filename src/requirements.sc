require: patterns.sc
  module = sys.zb-common
require: common.js
  module = sys.zb-common

require: patterns/patterns.sc
  
require: entities/names_KG/namesKG.sc
require: entities/patronymic/patronymic.sc
require: entities/surnames/surname.sc
require: entities/adresses/adresses_b.sc
require: entities/regionsEntity/regions.sc

require: themes/authorization.sc
require: themes/generalStates.sc
require: themes/commonDiagnostics.sc
require: themes/balance.sc

require: scripts/init.js
require: scripts/api.js
require: scripts/functions.js
require: scripts/validPhoneNumber.js
require: scripts/helpers.js
require: scripts/addressSelection.js
require: scripts/clientpurpose.js
    type = scriptEs6
    name = purpose
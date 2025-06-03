// function AdressesOutput(searchResults) {
//   const data = searchResults.data || {};
//   const addresses = Object.values(data)
//     .map(entry => entry.customer && entry.customer.address)
//     .filter(Boolean); // убираем undefined/null

//   const uniqueAddresses = [...new Set(addresses)]; // убираем дубли

//   if (uniqueAddresses.length === 1) {
//     return {
//       addresses: uniqueAddresses[0],
//       buttons: [
//         { text: "Да", value: "yes" },
//         { text: "Нет", value: "no" }
//       ]
//     };
//   } else if (uniqueAddresses.length > 1) {
//     return {
//       addresses: uniqueAddresses,
//       buttons: uniqueAddresses.map(address => ({
//         text: address,
//         value: address
//       }))
//     };
//   } else {
//     return {
//       addresses: null,
//       buttons: []
//     };
//   }
// }

function AdressesOutput(results) {
  var data = results.data || {};
  var addresses = [];

  for (var key in data) {
    if (data.hasOwnProperty(key)) {
      var customer = data[key].customer;
      if (customer && customer.address) {
        addresses.push(customer.address);
      }
    }
  }

  // Удаление дублей
  var uniqueAddresses = [];
  for (var i = 0; i < addresses.length; i++) {
    if (uniqueAddresses.indexOf(addresses[i]) === -1) {
      uniqueAddresses.push(addresses[i]);
    }
  }

  if (uniqueAddresses.length === 1) {
    return {
      addresses: uniqueAddresses[0],
      buttons: [
        { text: "Да", value: "yes" },
        { text: "Нет", value: "no" }
      ]
    };
  } else if (uniqueAddresses.length > 1) {
    var buttons = [];
    for (var j = 0; j < uniqueAddresses.length; j++) {
      buttons.push({
        text: uniqueAddresses[j],
        value: uniqueAddresses[j]
      });
    }
    return {
      addresses: uniqueAddresses,
      buttons: buttons
    };
  } else {
    return {
      addresses: null,
      buttons: []
    };
  }
}






// export default { 
//     AdressesOutput
    
// };
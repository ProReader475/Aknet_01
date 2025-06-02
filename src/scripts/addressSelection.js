function AdressesOutput(searchResults) {
  const data = searchResults.data || {};
  const addresses = Object.values(data)
    .map(entry => entry.customer && entry.customer.address)
    .filter(Boolean); // убираем undefined/null

  const uniqueAddresses = [...new Set(addresses)]; // убираем дубли

  if (uniqueAddresses.length === 1) {
    return {
      addresses: uniqueAddresses[0],
      buttons: [
        { text: "Да", value: "yes" },
        { text: "Нет", value: "no" }
      ]
    };
  } else if (uniqueAddresses.length > 1) {
    return {
      addresses: uniqueAddresses,
      buttons: uniqueAddresses.map(address => ({
        text: address,
        value: address
      }))
    };
  } else {
    return {
      addresses: null,
      buttons: []
    };
  }
}



export default { 
    AdressesOutput
    
};
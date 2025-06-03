function AdressesOutput(searchResults) {
    if (!searchResults || searchResults.length === 0) {
        return { addresses: [], buttons: [] };
    }
    log("/// VAR searchResults " + toPrettyString(searchResults));
    var addresses = searchResults.map(function(item) {
        return item.customer.address;
    });
    log("/// VAR ADDRESS " + toPrettyString(addresses));

    if (addresses.length === 1) {
        return {
            addresses: addresses[0],
            buttons: [
                { text: "Да", value: "yes" },
                { text: "Нет", value: "no" }
            ]
        };
    } else {
        return {
            addresses: addresses,
            buttons: addresses.map(function(address) {
                return {
                    text: address,
                    value: address
                };
            })
        };
    }
}

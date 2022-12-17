import React from "react";

interface AppContextInterface {
    cart: Map<number, number>,
    addProductToCart: (productId: number) => void
    removeProductFromCart: (productId: number) => void
}

let cartContext = React.createContext<AppContextInterface | null>(null);
export default cartContext;
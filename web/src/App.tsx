import React, {useContext, useState} from 'react';
import './App.css';
import Login from "./pages/Login";
import Nav from "./components/Nav";
import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom";
import Products from "./pages/Products";
import axios from "axios";
import Context from "./context";
import Orders from "./pages/Orders";
import Refunds from "./pages/Refunds";
import cartContext from "./context/customerCtx"
import Cart from "./pages/Cart";
import People from "./pages/People";

axios.defaults.withCredentials = true
type ICredentials = {
    name: string
    role: string
}

function App() {

    let [role, setRole] = useState<string>('');
    let [cart, setCart] = useState<Map<number, number>>(new Map<number, number>);
    axios.get<ICredentials>("http://localhost:8080/whoami").then(response => {
        if (response.status == 200) {
            setRole(response.data.role)
        }
    });
    const addProduct = (productId: number) => {
        let oldVal = cart.get(productId);
        if (oldVal === undefined) {
            cart.set(productId,  1)
        } else {
            cart.set(productId,  oldVal + 1)
        }
        console.log(cart.get(productId))
    }
    const removeProduct = (productId: number) => {
        let oldVal = cart.get(productId);
        if (oldVal === undefined || oldVal == 0) {

        } else {
            cart.set(productId,  oldVal - 1)
        }
    }

    let defaultPage

    if (role) {
        defaultPage = <Navigate to="/products"/>
    } else {
        defaultPage = <Navigate to="/login"/>
    }

    return (
        <Context.Provider value={{role, setRole}}>
            <cartContext.Provider value={{cart: cart, addProductToCart: addProduct, removeProductFromCart: removeProduct}}>
                <BrowserRouter>
                    <Nav/>
                        <Routes>
                            <Route path="/" element={defaultPage}/>
                            <Route path="/products" element={<Products/>}/>
                            <Route path="/orders" element={<Orders/>}/>
                            <Route path="/refunds" element={<Refunds/>}/>
                            <Route path="/login" element={<Login/>}/>
                            <Route path="/cart" element={<Cart/>}/>
                            <Route path="/people" element={<People/>}/>
                        </Routes>
                </BrowserRouter>
        </cartContext.Provider>
        </Context.Provider>
    );
}

export default App;

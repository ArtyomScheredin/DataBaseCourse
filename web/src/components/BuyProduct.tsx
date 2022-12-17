import React, {useContext, useState} from 'react';
import Button from 'react-bootstrap/Button';
import {GrAdd} from "@react-icons/all-files/gr/GrAdd";
import cartContext from "../context/customerCtx";
import {AiOutlineMinus} from "@react-icons/all-files/ai/AiOutlineMinus";


type Product = {
    name: string
    category_id: number
    price: number
    quantity: number
    discontinued: boolean
}


const BuyProduct = (props: { product_id: number, max: number }) => {
    let cart = useContext(cartContext);
    const [value, refreshPage] = useState<boolean>(false)

    return (
        <>
            <p>{cart?.cart.get(props.product_id) === undefined ? 0 : cart?.cart.get(props.product_id)}</p>
            <Button className="" variant="warning" onClick={() => {
                if (cart?.cart.get(props.product_id) === undefined || cart?.cart.get(props.product_id) < props.max) {
                    cart?.addProductToCart(props.product_id)
                    refreshPage(!value)
                }
            }}>
                <GrAdd/>
            </Button>
            <Button className="ms-1" variant="warning" onClick={() => {
                cart?.removeProductFromCart(props.product_id)
                refreshPage(!value)
            }}>
                <AiOutlineMinus/>
            </Button>
        </>
    );
}

export default BuyProduct


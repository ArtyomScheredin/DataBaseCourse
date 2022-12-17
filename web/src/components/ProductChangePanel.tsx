import {Product} from "../pages/Products";
import React, {MouseEventHandler, useState} from "react";
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import {IoIosSave} from "@react-icons/all-files/io/IoIosSave";
import axios from "axios";

axios.defaults.withCredentials = true

function ProductChangePanel(props: { product: Product }) {
    let [price, setPrice] = useState<number>(props.product.price);
    let [quantity, setQuantity] = useState<number>(props.product.quantity);
    let [discontinued, setDiscontinued] = useState<boolean>(props.product.discontinued);

    const submitProductUpdate: MouseEventHandler<HTMLButtonElement> = (e) => {
        axios.put(`http://localhost:8080/products/${props.product.product_id}`, {},{
                params: {price: price, quantity: quantity, discontinued: discontinued},
            }
        )
    }

    return (<>
        <td><Form.Control
            type="text"
            placeholder={String(props.product.price)} onChange={(e) => {
            setPrice(parseInt(e.target.value))
        }}/></td>
        <td><Form.Control
            type="text"
            placeholder={String(props.product.quantity)} onChange={(e) => {
            setQuantity(parseInt(e.target.value))
        }}/></td>
        <td>{"было -> " + (props.product.discontinued ? 'да' : 'нет')}<Form.Check
            type="checkbox"
            onChange={(e) => {
                setDiscontinued(!discontinued)
            }}/></td>
        <td><Button variant="primary" onClick={submitProductUpdate}>
            <IoIosSave/>
        </Button></td>
    </>)
}

export default ProductChangePanel
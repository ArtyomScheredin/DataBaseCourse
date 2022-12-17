import React, {useContext, useEffect, useState} from 'react';
import {Button, Col, Container, Row, Table} from "react-bootstrap";
import cartContext from "../context/customerCtx";
import axios from "axios";

type CartEl = {
    key: number
    val: number
}
const Orders = () => {
        const cart = useContext(cartContext)
        const [val, refresh] = useState<boolean>(true)

        let ret: CartEl[] = []


            cart?.cart.forEach((key, val) => {

                    ret.push({
                        key, val
                    });})



        const createOrder = () => {
            if (cart !== undefined) {
                let cart2: Map<number, number> = cart.cart;
                let s = Object.fromEntries(cart2);
                axios.post("http://localhost:8080/orders", {...s})
                cart?.cart.clear()
                refresh(!val)
            }
        }

        return (
            <Container fluid>
                <Row>
                    <Col>
                        <Table striped bordered hover size="lg">
                            <thead>
                            <tr>
                                <th>товар</th>
                                <th>количество</th>
                            </tr>
                            </thead>
                            <tbody>
                            <p>{val}</p>
                            {ret.map((e: CartEl) => {
                                return <>
                                    <tr>
                                        <td>{e.key}</td>
                                        <td>{e.val}</td>
                                    </tr>
                                </>
                            })
                            }
                            </tbody>
                        </Table>
                        {cart?.cart?.size > 0 && <Button className="m-3 p-3" variant="primary" onClick={createOrder}>
                            купить
                        </Button>}
                    </Col>
                </Row>
            </Container>
        );
    }
;

export default Orders;

import React, {useContext, useEffect, useState} from 'react';
import {Button, Col, Container, Row, Table} from "react-bootstrap";
import Offcanvas from 'react-bootstrap/Offcanvas';
import axios from "axios";
import FiltersForm from "./ FiltersForm";
import ReviewModal from "../components/ModalReview";
import AddReviewModal from "../components/AddReviewModal";
import Context from "../context";
import ProductChangePanel from "../components/ProductChangePanel";
import AddProduct from "../components/AddProduct";
import BuyProduct from "../components/BuyProduct";
import RequestRefund from "../components/RequestRefund";

export type Order =
    {
        order_id: number
        customer_id: number
        order_date: number
        recieve_date: number
    }


const Orders = () => {
        const [orders, setOrders] = useState<Order[]>();
        let ctx = useContext(Context);

        useEffect(() => {
                let response = axios.get<Order[]>("http://localhost:8080/orders"
                )
                response.then(resp => {
                    setOrders(resp.data)
                    console.log(resp.data)
                })

            },[])


        return (
            <Container fluid>
                <Row>
                    <Col>
                        {ctx?.role === 'engineer' && <AddProduct product_id={1}/>}

                        <Table striped bordered hover size="lg">
                            <thead>
                            <tr>
                                <th>id</th>
                                <th>Дата заказа</th>
                                <th>Дата получения</th>
                            </tr>
                            </thead>
                            <tbody>
                            {orders?.map(o => {
                                return <tr>
                                    <td>{o.order_id}</td>
                                    <td>{o.order_date}</td>
                                    <td>{o.recieve_date == null ?  <RequestRefund order_id={o.order_id}/> : o.recieve_date}</td>
                                </tr>
                            })}
                            </tbody>
                        </Table>
                    </Col>
                </Row>
            </Container>
        );
    }
;

export default Orders;

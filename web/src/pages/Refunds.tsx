import React, {useContext, useEffect, useState} from 'react';
import {Col, Container, Row, Table} from "react-bootstrap";
import axios from "axios";
import Context from "../context";
import {Refund} from "../components/RequestRefund";
import ApproveRefund from "../components/ApproveRefund";
import Button from "react-bootstrap/Button";


const Refunds = () => {
        const [refunds, setRefunds] = useState<Refund[]>();
        const [refresh, setRefresh] = useState<boolean>(false)
        let ctx = useContext(Context);

        useEffect(() => {
        if (ctx.role == 'customer') {
            axios.get<Refund[]>('http://localhost:8080/orders/refund/my').then(response => {
                setRefunds(response.data)
            })
        } else if (ctx.role == 'manager') {
            axios.get<Refund[]>('http://localhost:8080/orders/refund/assigned').then(response => {
                setRefunds(response.data)
            })
        }}, [])


        return (
            <Container fluid>
                <Row>
                    <Col>
                        <Table striped bordered hover size="lg">
                            <thead>
                            <tr>
                                <th>id</th>
                                <th>описание</th>
                                <th>одобрен</th>
                                <th>номер заказа</th>
                                <th>номер ответственного сотрудника</th>
                            </tr>
                            </thead>
                            <tbody>
                            {refunds?.map(o => {
                                return <tr>
                                    <td>{o.refund_id}</td>
                                    <td>{o.description}</td>

                                    <td>{ctx.role === 'customer' ? o.approved ? 'нет' : 'да'
                                            : !o.approved ?
                                                <Button className="mt-1 me-5" variant="secondary" onClick={() => {
                                                    axios.put(`http://localhost:8080/orders/refund/${o.refund_id}`)
                                                    setRefresh(!refresh)
                                                }}>
                                                    Одобрить
                                                </Button> : 'да'}
                                    </td>
                                    <td>{o.order_id}</td>
                                    <td>{o.employee_id}</td>
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

export default Refunds;

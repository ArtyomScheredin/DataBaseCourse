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

export type Product =
    {
        product_id: number
        name: string
        category: string
        price: number
        quantity: number
        discontinued: boolean
    }


const Products = () => {
        const [products, setProducts] = useState<Product[]>();
        const [params, setParams] = useState<URLSearchParams>(new URLSearchParams)
        let ctx = useContext(Context);

        useEffect(() => {
                console.log(params)
                let response = axios.get<Product[]>("http://localhost:8080/products", {
                        params: params,
                    }
                )
                response.then(resp => {
                    setProducts(resp.data)
                })
            },
            [params])

        const [show, setShow] = useState(false);
        const handleClose = () => setShow(false);
        const handleShow = () => setShow(true);


        return (
            <Container fluid>
                <Row>
                    <Col><Offcanvas show={show} onHide={handleClose}>
                        <Offcanvas.Header closeButton>
                            <Offcanvas.Title>Выберите фильтры</Offcanvas.Title>
                        </Offcanvas.Header>
                        <Offcanvas.Body>
                            <FiltersForm submitParams={setParams} closeOffCanvas={handleClose}/>
                        </Offcanvas.Body>
                    </Offcanvas>
                        <Button className="m-3 p-3" variant="primary" onClick={handleShow}>
                            фильтры
                        </Button>
                        <Button className="m-3 p-3" variant="primary" onClick={() => {
                            setParams(new URLSearchParams())
                        }}>
                            сбросить фильтры
                        </Button>
                        {ctx?.role === 'engineer' && <AddProduct product_id={1}/>}

                        <Table striped bordered hover size="lg">
                            <thead>
                            <tr>
                                <th>id</th>
                                <th>имя</th>
                                <th>категории</th>
                                <th>цена</th>
                                <th>кол-во</th>
                                <th>производится</th>
                                {ctx?.role === 'engineer' && <th>сохранить</th>}
                                <th>отзывы</th>
                                {ctx?.role === 'customer' && <th></th>}
                            </tr>
                            </thead>
                            <tbody>
                            {products?.map(p => {
                                return <tr>
                                    <td>{p.product_id}</td>
                                    <td>{p.name}</td>
                                    <td>{p.category}</td>
                                    {ctx?.role === 'engineer' ? <ProductChangePanel product={p}/> : <>
                                        <td>{p.price}</td>
                                        <td>{p.quantity}</td>
                                        <td>{p.discontinued ? 'да' : 'нет'}</td>
                                    </>}
                                    <td>{ctx?.role === 'customer' && <AddReviewModal product_id={p.product_id}/>}
                                        <ReviewModal product={p}/>
                                        </td>
                                    {ctx?.role === 'customer' && <th><BuyProduct product_id={p.product_id} max={p.quantity}/></th>}
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

export default Products;

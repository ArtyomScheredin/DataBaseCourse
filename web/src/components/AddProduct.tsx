import React, {MouseEventHandler, useEffect, useState} from 'react';
import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import axios from "axios";
import {Form} from "react-bootstrap";
import {Category} from "../pages/ FiltersForm";

type Product = {
    name: string
    category_id: number
    price: number
    quantity: number
    discontinued: boolean
}


const AddProduct = (props: { product_id: number }) => {
    const [show, setShow] = useState(false);

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    let [name, setName] = useState<string>('');
    let [categoryId, setCategoryId] = useState<number>(1);
    let [price, setPrice] = useState<number>(0);
    let [quantity, setQuantity] = useState<number>(0);
    let [discontinued, setDiscontinued] = useState<boolean>(false);

    let [categories, setCategories] = useState<Category[]>([]);
    useEffect(() => {
        let response = axios.get<Category[]>("http://localhost:8080/products/categories");
        response.then(resp => {
            setCategories(resp.data)
        })
    }, [])

    const submitReview: MouseEventHandler<HTMLButtonElement> = (e) => {

        const product: Product = {name: name, category_id: categoryId, price: price, quantity, discontinued}
        axios.post(`http://localhost:8080/products`, JSON.stringify(product), {
            headers: {
                // Overwrite Axios's automatically set Content-Type
                'Content-Type': 'application/json'
            }
        })
        handleClose()
    }
    return (
        <>
            <Button className="m-3 p-3 ms-5" variant="warning" onClick={handleShow}>
                Добавить товар
            </Button>

            <Modal show={show} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Добавьте новый товар</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group className="mb-3" controlId="formBasicPassword">
                            <Form.Label>Имя</Form.Label>
                            <Form.Control type="text" placeholder="Введите имя"
                                          onChange={event => setName(event.target.value)}/>
                        </Form.Group>
                        <Form.Group className="mb-3" controlId="category">
                            <Form.Label>Категория</Form.Label>
                            <Form.Select aria-label="Default select example" onChange={e => {
                                setCategoryId(parseInt(e.target.value))
                            }}>
                                {categories?.map(e => <option value={e.category_id}>{e.name}</option>)}
                            </Form.Select>
                        </Form.Group>
                        <Form.Group className="mb-3" controlId="formBasicEmail">
                            <Form.Label>Цена - {price}</Form.Label>
                            <Form.Range step="1" max={20000} defaultValue={0}
                                        onChange={event => setPrice(parseInt(event.target.value))}/>
                        </Form.Group>
                        <Form.Group className="mb-3" controlId="formBasicEmail">
                            <Form.Label>Количество - {quantity}</Form.Label>
                            <Form.Range step="1" max={200} defaultValue={0}
                                        onChange={event => setQuantity(parseInt(event.target.value))}/>
                        </Form.Group>
                        <Form.Check type="checkbox" label="Ещё производится" onChange={(e) => {
                            setDiscontinued(!discontinued)
                        }}/>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="primary" onClick={submitReview}>
                        Добавить товар
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}

export default AddProduct


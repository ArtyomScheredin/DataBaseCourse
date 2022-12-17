import React, {MouseEventHandler, useState} from 'react';
import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import axios from "axios";

import {Form} from "react-bootstrap";
import {BsPencil} from "@react-icons/all-files/bs/BsPencil";

type Review = {
    rate: number
    description: string
}
const AddReviewModal = (props: { product_id: number }) => {
    const [show, setShow] = useState(false);
    const [rate, setRate] = useState<number>(5);
    const [description, setDescription] = useState<string>('');

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);


    const submitReview: MouseEventHandler<HTMLButtonElement> = (e) => {

        const review: Review = {rate: rate, description: description}
        axios.post(`http://localhost:8080/products/${props.product_id}/review`, JSON.stringify(review),{
            headers: {
                // Overwrite Axios's automatically set Content-Type
                'Content-Type': 'application/json'
            }
        })
        handleClose()
    }

    return (
        <>
            <Button className="ms-1 mt-1" variant="warning" onClick={handleShow}>
                <BsPencil/>
            </Button>

            <Modal show={show} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Добавьте отзыв на продукт {props.product_id}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group className="mb-3" controlId="formBasicEmail">
                            <Form.Label>Оценка - {rate}</Form.Label>
                            <Form.Range step="1" max={5} defaultValue={5} onChange={event => setRate(parseInt(event.target.value))}/>
                        </Form.Group>

                        <Form.Group className="mb-3" controlId="formBasicPassword">
                            <Form.Label>Отзыв</Form.Label>
                            <Form.Control type="text" placeholder="Мне очень понравился данный продукт..."
                                          onChange={event => setDescription(event.target.value)}/>
                        </Form.Group>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="primary" onClick={submitReview}>
                        Добавить отзыв
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}

export default AddReviewModal


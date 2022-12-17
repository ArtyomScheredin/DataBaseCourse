import React, {useEffect, useState} from 'react';
import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import {AiFillEye} from "@react-icons/all-files/ai/AiFillEye";
import Card from 'react-bootstrap/Card';
import axios from "axios";

type Review = {
    review_id: number
    rate: number
    description: string
    customer_id: number
    product_id: number
}
const ReviewModal = (props: { product_id: number }) => {
    let [reviews, setReviews] = useState<Review[]>();
    const [show, setShow] = useState(false);

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    useEffect(() => {
        if (show) {
            let response = axios.get<Review[]>(`http://localhost:8080/products/${props.product_id}/review`)
            response.then(resp => {
                setReviews(resp.data)
            })
        }
    }, [show])

    return (
        <>
            <Button className="ms-1 mt-1" variant="primary" onClick={handleShow}>
                <AiFillEye/>
            </Button>

            <Modal show={show} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Отзывы на продукт {props.product_id}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {reviews?.map((e) => <><Card className="m-2">
                    <Card.Header><h4 className="font-weight-bold mt-2">Оценка - {e.rate}</h4></Card.Header>
                    <Card.Body>
                        <blockquote className="blockquote mb-0">
                            <p>
                                {e.description}
                            </p>
                        </blockquote>
                    </Card.Body>
                </Card></>)}
                </Modal.Body>
                <Modal.Footer>
                </Modal.Footer>
            </Modal>
        </>
    );
}

export default ReviewModal


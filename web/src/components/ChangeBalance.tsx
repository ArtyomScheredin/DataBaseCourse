import React, {ChangeEvent, useState} from 'react';
import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import axios from "axios";
import Form from "react-bootstrap/Form";

type Review = {
    review_id: number
    rate: number
    description: string
    customer_id: number
    product_id: number
}
const ChangeBalance = () => {
    let [sum, setSum] = useState<number>(0);
    const [show, setShow] = useState(false);
    let [balance, setBalance] = useState<number>(0);

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);
    axios.get<number>("http://localhost:8080/balance").then(response => {
        setBalance(response.data)
    });

    const getMoney = () => {
        let newBalance = balance + sum;
        axios.put<number>("http://localhost:8080/balance", {}, {
            params: {newBalance: newBalance}
        })
        axios.get<number>("http://localhost:8080/balance").then(response => {
            setBalance(response.data)
        })
        handleClose()
    }


    return (
        <>
            <h6 style={{color: 'white'}} className="align-self-end  nav-item me-2 active">Ваш баланс: {balance}</h6>

            <Button className="mt-1 me-5" variant="secondary" onClick={handleShow}>
                Пополнить
            </Button>

            <Modal show={show} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Пополнить баланс</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form.Group className="mb-3" controlId="price-min">
                        <Form.Label>сумма пополнения - {sum}</Form.Label>
                        <Form.Control type="text"
                                    onChange={(e: ChangeEvent<HTMLInputElement>) => {
                                        setSum(parseInt(e.target.value))
                                    }}/>
                    </Form.Group>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="primary" type="button" onClick={getMoney}>
                        Пополнить
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}

export default ChangeBalance


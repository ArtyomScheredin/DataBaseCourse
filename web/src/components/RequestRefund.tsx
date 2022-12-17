import React, {useState} from 'react';
import Button from 'react-bootstrap/Button';
import axios from "axios";
import Form from "react-bootstrap/Form";
import Toast from 'react-bootstrap/Toast';
import {ToastContainer} from "react-bootstrap";

export type Refund = {
    refund_id: number
    order_id: number
    description: string
    approved: string
    employee_id: number
}

const RequestRefund = (props: { order_id: number }) => {
    const [showA, setShowA] = useState(false);
    const [description, setDescription] = useState<string>('');
    const [isRefunded, setRefunded] = useState<boolean>(false)

    const toggleShowA = () => setShowA(!showA);
    axios.get<Refund[]>("http://localhost:8080/orders/refund/my").then(response => {
        console.log(response.data)
        response.data.forEach(r => {
            if (r.order_id === props.order_id) {
                setRefunded(true)
            }
        })
    });

    const makeRefund = () => {
        axios.post(`http://localhost:8080/orders/${props.order_id}/refund`, {description});
        toggleShowA()
    }

    return (
        <>
            <p>ещё не получен</p>
            {isRefunded && <p>оформлен возврат</p>}
            {!showA && !isRefunded &&
                <> <Form.Group className="mb-3" controlId="name">
                    <Form.Control type="text" placeholder="Причина возврата"
                                  onChange={(e) => setDescription(e.target.value)}/>
                </Form.Group>
                    <Button className="ms-1 mt-1" variant="primary" onClick={makeRefund}>
                        возврат
                    </Button></>}
            <ToastContainer className="p-3" position={'top-end'}>
                <Toast show={showA} onClose={toggleShowA}>
                    <Toast.Header>
                        <img
                            src="holder.js/20x20?text=%20"
                            className="rounded me-2"
                            alt=""
                        />
                        <strong className="me-auto">Администратор</strong>
                        <small>1 секунду назад</small>
                    </Toast.Header>
                    <Toast.Body>Заявка на возврат оформлена</Toast.Body>
                </Toast>
            </ToastContainer>
        </>
    );
}

export default RequestRefund


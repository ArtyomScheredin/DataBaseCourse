import React, {Dispatch, SetStateAction} from 'react';
import Button from "react-bootstrap/Button";
import axios from "axios";


const ApproveRefund = (props: { refund_id: number, setRefresh: Dispatch<SetStateAction<boolean>>, refresh: boolean}) => {
    const approveRefund =

    return <>
        <Button className="mt-1 me-5" variant="secondary" onClick={() => {
            axios.put(`http://localhost:8080/orders/refund/${props.refund_id}`)
            props.setRefresh(!props.refresh)
        }}>
            Одобрить
        </Button>
    </>
}

export default ApproveRefund


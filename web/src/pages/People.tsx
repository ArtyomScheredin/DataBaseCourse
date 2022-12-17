import React, {useContext, useEffect, useState} from 'react';
import {Button, Col, Container, Row, Table} from "react-bootstrap";
import axios from "axios";
import Context from "../context";
import Form from "react-bootstrap/Form";

export type  PersonDto = {
    user_id: number
    login: string
    name: string
    blocked: boolean
    salary: number
}


const Orders = () => {
        const [person, setPerson] = useState<PersonDto[]>();
        const [refresh, setRefresh] = useState<boolean>(false);
        const [salary, setSalary] = useState<number>('');
        let ctx = useContext(Context);

        useEffect(() => {
            let response = axios.get<PersonDto[]>("http://localhost:8080/people"
            )
            response.then(resp => {
                setPerson(resp.data)
                console.log(resp.data)
            })
        }, [refresh])


        return (
            <Container fluid>
                <Row>
                    <Col>
                        <Table striped bordered hover size="lg">
                            <thead>
                            <tr>
                                <th>id</th>
                                <th>имя</th>
                                <th>зарплата</th>
                            </tr>
                            </thead>
                            <tbody>
                            {person?.map(o => {
                                return <tr>
                                    <td>{o.user_id}</td>
                                    <td>{o.name}</td>
                                    <td>{o.salary !== null && o.salary !== 0 ? <> <Form.Control
                                        type="text"
                                        placeholder={String(o.salary)} onChange={(e) => {
                                        setSalary(Number(e.target.value))
                                    }}/><Button onClick={
                                        () => {
                                            axios.put(`http://localhost:8080/${o.user_id}/salary`, {}, {params: {newSalary: salary}}
                                            )
                                            setRefresh(!refresh)
                                        }
                                    }>сохранить</Button> </> : <>клиент</>}</td>
                                    <td>{o.blocked ? <Button onClick={() => {
                                        axios.put(`http://localhost:8080/${o.user_id}/ban`, {}, {params: {banned: 'False'}}
                                        )
                                        setRefresh(!refresh)
                                    }
                                    }>разбанить</Button> : <Button onClick={() => {
                                        axios.put(`http://localhost:8080/${o.user_id}/ban`, {}, {params: {banned: 'True'}}
                                        )
                                        setRefresh(!refresh)
                                    }
                                    }>забанить</Button>}</td>
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

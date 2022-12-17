import React, {SyntheticEvent, useContext, useEffect, useState} from 'react';
import {Navigate, useNavigate} from "react-router-dom";
import axios from "axios";
import {useCookies} from "react-cookie";
import Context from "../context";


type ICredentials = {
    name: string
    role: string
}

const Login = () => {
    const [login, setLogin] = useState('');
    const [password, setPassword] = useState('');
    const [redirect, setRedirect] = useState(false);
    const [cookies, setCookie] = useCookies(["jwt"]);
    let ctx = useContext(Context);
    let navigate = useNavigate()

    const submit = async (e: SyntheticEvent) => {
        e.preventDefault();
        let axiosResponse = await axios.post("http://localhost:8080/auth",
            {'name': login, 'password': password},
            {
                headers: {
                    'Content-Type': 'application/json',
                    "Access-Control-Allow-Origin": "*"
                }
            });
        if (axiosResponse.status != 200) {
            alert("неправильный пароль")
        }
        setCookie("jwt", axiosResponse.data, {
            path: "/"
        });
        const {data} = await axios.get<ICredentials>("http://localhost:8080/whoami");
        ctx?.setRole(data.role)
        navigate("/products")
    }

    return (
        <form className="form-signing" onSubmit={submit}>
            <h1 className="h3 mb-3 fw-normal">Авторизуйтесь</h1>
            <input type="login" className="form-control" placeholder="Login" required
                   onChange={e => setLogin(e.target.value)}
            />

            <input type="password" className="form-control" placeholder="Password"  required
                   onChange={e => setPassword(e.target.value)}
            />

            <button className="w-100 btn btn-lg btn-primary" type="submit">Войти</button>
        </form>
    );
};

export default Login;

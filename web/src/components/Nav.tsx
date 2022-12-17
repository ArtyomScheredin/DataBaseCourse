import React, {useContext} from 'react';
import {Link} from "react-router-dom";
import Context from "../context";
import ChangeBalance from "./ChangeBalance";

type URL = {
    name: string,
    url: string
}

const Nav = () => {
    let ctx = useContext(Context);
    const resources: Map<string, URL[]> = new Map<string, URL[]>()
    resources.set('customer', [{name: 'Продукты', url: '/products'},
        {name: 'Заказы', url: '/orders'},
        {name: 'Возвраты', url: '/refunds'},
        {name: 'Корзина', url: '/cart'}
    ])
    resources.set('engineer', [{name: 'Продукты', url: '/products'}
    ])
    resources.set('manager', [{name: 'Продукты', url: '/products'},
        {name: 'Возвраты', url: '/refunds'},
        {name: 'Люди', url: '/people'}
    ])
    let menu;


    menu = <ul className="navbar-nav me-auto mb-2 mb-md-0">
        {ctx?.role === 'customer' && <><ChangeBalance/></>}

        {resources.get(ctx.role)?.map(e => {
            return <li className="nav-item active">
                <Link to={e.url}
                      className="nav-link">{e.name}</Link>
            </li>
        })}
        <li className="nav-item active">
            <Link to="/login" onClick={() => {
                document.cookie = "jwt= ; expires = Thu, 01 Jan 1970 00:00:00 GMT"
                ctx.setRole("")
            }
            } className="nav-link">Выйти</Link>
        </li>
    </ul>

    return (
        <nav className="navbar navbar-expand-md navbar-dark bg-dark mb-4">
            <div className="container-fluid">
                <Link to="/" className="navbar-brand">{ctx.role}</Link>
                <div>
                    {menu}
                </div>
            </div>
        </nav>
    );
};

export default Nav;

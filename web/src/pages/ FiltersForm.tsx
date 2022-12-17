import Form from 'react-bootstrap/Form';
import axios from "axios";
import {ChangeEvent, Dispatch, FormEventHandler, SetStateAction, useEffect, useState} from "react";
import {Button} from "react-bootstrap";

export type Category = {
    category_id: number
    name: string
}

interface IMyProps {
    submitParams: Dispatch<SetStateAction<URLSearchParams>>
    closeOffCanvas: () => void
}

const FilterForm: React.FC<IMyProps> = (props: IMyProps) => {

    let [name, setName] = useState<string | null>(null);
    let [category, setCategory] = useState<string | null>(null);
    let [minPrice, setMinPrice] = useState<number>(0);
    let [maxPrice, setMaxPrice] = useState<number>(1000);
    let [quantity, setQuantity] = useState<string | null>(null);
    let [discontinued, setDiscontinued] = useState<boolean | null>(null);
    let [sort, setSort] = useState<string | null>(null);

    let [categories, setCategories] = useState<Category[]>([]);
    useEffect(() => {
        let response = axios.get<Category[]>("http://localhost:8080/products/categories");
        response.then(resp => {
            setCategories(resp.data)
        })
    }, [])


    const FilterProducts: FormEventHandler<HTMLButtonElement> = (e) => {
        e.preventDefault()
        let params = new URLSearchParams();
        name != null && params.set('name', name.trim())
        params.set('price_min', String(minPrice))
        params.set('price_max', String(maxPrice))
        category != null && category !== 'null' && params.set('category', category)
        quantity != null && params.set('quantity_min', String(quantity))
        discontinued != null && params.set('discontinued', discontinued ? 'True' : 'False')
        sort != null && params.set("sort", sort)
        props.submitParams(params)
        props.closeOffCanvas()
    }

    return (
        <Form>
            <Form.Group className="mb-3" controlId="name">
                <Form.Label>Название продукта</Form.Label>
                <Form.Control type="text" placeholder="введите название продукта" onChange={(e) => setName(e.target.value)}/>
            </Form.Group>
            <Form.Group className="mb-3" controlId="category">
                <Form.Label>Категория</Form.Label>
                <Form.Select aria-label="Default select example" onChange={e => {
                    setCategory(e.target.value)
                }}>
                    <option value={'null'}>без категории</option>
                    {categories?.map(e => <option value={e.name}>{e.name}</option>)}
                </Form.Select>
            </Form.Group>
            <Form.Group className="mb-3" controlId="price-max">
                <Form.Label>Цена макс. {maxPrice}</Form.Label>
                <Form.Range max={1000} value={maxPrice} onChange={(e: ChangeEvent<HTMLInputElement>) => {
                    setMaxPrice(Number(e.target.value))
                    minPrice > maxPrice && setMinPrice(maxPrice)
                }}/>
            </Form.Group>
            <Form.Group className="mb-3" controlId="price-min">
                <Form.Label>Цена мин. {minPrice}</Form.Label>
                <Form.Range max={1000} value={minPrice}
                            onChange={(e: ChangeEvent<HTMLInputElement>) => {
                                setMinPrice(Number(e.target.value))
                                minPrice > maxPrice && setMaxPrice(minPrice)
                            }}/>
            </Form.Group>
            <Form.Select className="mb-3" aria-label="Default select example" onChange={event => {
                switch (event.target.value) {
                    case "": {
                        setSort(null)
                        break
                    }
                    case "1": {
                        setSort("descending")
                        break
                    }
                    case "2": {
                        setSort("ascending")
                        break
                    }
                }
            }}>
                <option value="">Без сортировки</option>
                <option value="1">По убыванию цены</option>
                <option value="2">По возрастанию цены</option>
            </Form.Select>
            <Form.Group className="mb-3" controlId="quantityMin">
                <Form.Label>Минимальное доступное количество</Form.Label>
                <Form.Control type="text" placeholder="введите число" onChange={(e) => setQuantity(e.target.value)}/>
            </Form.Group>
            <Form.Group className="mb-3" controlId="discontinued">
                <Form.Check type="checkbox" label="Ещё производится" onChange={(e) => {
                    setDiscontinued(discontinued == null ? true : !discontinued)
                    }}/>
            </Form.Group>

            <Button variant="primary" type="button" onClick={FilterProducts}>
                Отфильтровать
            </Button>
        </Form>
    );
}

export default FilterForm;
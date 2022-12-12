
--1 Имена работников с зарплатой выше 9000 по убыванию зарплаты
Select u.name, e.salary
from users as u
         JOIN employees e on u.user_id = e.user_id
WHERE salary > 9000
ORDER BY salary DESC;

--2 Количество продуктов на складе в каждой категории
Select pc.name, product_categories_count.cnt
from (Select count(name) as cnt, category_id
      from products
      GROUP BY category_id)
         as product_categories_count
         join product_categories pc using (category_id);

--3 Топ транжир
select o.customer_id, sum(pol.quantity * p.price)
from products_orders_link pol
         join products p on pol.product_id = p.product_id
         join orders o on pol.orders_id = o.order_id
group by o.customer_id
ORDER BY sum(pol.quantity * p.price) DESC;

--4 Жадные покупатели, которые ничего не заказали
SELECT user_id
from users
except
select customer_id
from orders
         LIMIT 10;

--5 Продукты, которые не сможет выкупить полностью ни один покупатель
SELECT name, quantity * price
from products
where not exists
    (select * from customers where customers.balance > (products.quantity * products.price));

--6 Вывести продукты, категорий speakers, displays, microphones
SELECT *
from products
where category_id in (1, 3, 5);

--7 Номер заказа и текст заявки на возврат если имеется
select orders.order_id, description
from orders
         left join refunds r on orders.order_id = r.refund_id;

--8 События, произошедшие после 2021-10-13
select order_id, 'ordered'
from orders
where order_date > '2021-10-13'
union
select user_id, 'employeed'
from employees
where employment_date > '2021-10-13';

--9 Покупатели, которые чаще всего оформляют возврат
select count(customer_id)
from orders
where exists(select order_id from refunds)
group by customer_id
order by count(customer_id);

--10. Продукты, которых больше всего в рамках своих категорий
select p.name, quantity, product_categories.name
from (select name, category_id, quantity, rank() over (partition by category_id order by quantity desc)
      from products) as p
         join product_categories using (category_id);






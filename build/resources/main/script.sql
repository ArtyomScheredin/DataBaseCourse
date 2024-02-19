CREATE OR REPLACE FUNCTION public.get_available_products_ids(
)
    RETURNS SETOF integer
    LANGUAGE 'sql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000

AS $BODY$
select product_id from products where discontinued!=true
$BODY$;

ALTER FUNCTION public.get_available_products_ids()
    OWNER TO postgres;

-- FUNCTION: public.get_user_info_by_name(text)

-- DROP FUNCTION IF EXISTS public.get_user_info_by_name(text);

CREATE OR REPLACE FUNCTION public.get_user_info_by_name(
    username text)
    RETURNS TABLE(login text, password text, role text, blocked text)
    LANGUAGE 'sql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000

AS $BODY$
SELECT login, password, coalesce(r.name, 'customer') as role, blocked FROM users
                                                                               left join employees e using (user_id)
                                                                               left join roles r using (role_id)
                                                                               left join customers c using (user_id) WHERE login=username;
$BODY$;

ALTER FUNCTION public.get_user_info_by_name(text)
    OWNER TO postgres;

-- FUNCTION: public.get_user_info_by_name(text)

-- DROP FUNCTION IF EXISTS public.get_user_info_by_name(text);

CREATE OR REPLACE FUNCTION public.get_user_info_by_name(
    username text)
    RETURNS TABLE(login text, password text, role text, blocked text)
    LANGUAGE 'sql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000

AS $BODY$
SELECT login, password, coalesce(r.name, 'customer') as role, blocked FROM users
                                                                               left join employees e using (user_id)
                                                                               left join roles r using (role_id)
                                                                               left join customers c using (user_id) WHERE login=username;
$BODY$;

ALTER FUNCTION public.get_user_info_by_name(text)
    OWNER TO postgres;

CREATE OR REPLACE FUNCTION public.calculate_quantity()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
declare
    oldvalue integer := 0;
    newvalue integer := 0;
BEGIN
    SELECT quantity from products where product_id=NEW.product_id limit 1 INTO oldvalue;
    newvalue := oldvalue - NEW.quantity;
    UPDATE products set quantity=newvalue where product_id=NEW.product_id;
    return new;
END;
$BODY$;

ALTER FUNCTION public.calculate_quantity()
    OWNER TO postgres;

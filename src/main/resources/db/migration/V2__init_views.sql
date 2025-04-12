create or replace view ACCOUNT_VIEW
as
select
            row_number() over (order by client_id) as id,
            a.id as account_id,
            concat(c.first_name, ' ', c.last_name) as full_name,
            c.id as client_id,
            a.account_name,
            a.balance,
            a.currency,
            a.is_active,
            o.number_of_operations,
            a.account_kind,
            o.latest_operation
from client c
         left join account a on c.id = a.client_id
         left join
     (
         select
             account_id,
             max(operation_date) as latest_operation,
             count(*) as number_of_operations
         from operations
         group by account_id
     ) o on o.account_id = a.id;

create or replace view CLIENT_VIEW
as
select
    c.id,
    c.last_name,
    c.first_name,
    c.registration_date,
    c.birth_date,
    c.is_active,
    c.email,
    coalesce(a.number_of_accounts, 0) as number_of_accounts
from client c
         left join
     (
         select client_id,
                coalesce(count(*), 0) as number_of_accounts
         from account
         where is_active = true
         group by client_id
     ) a on a.client_id = c.id;

create or replace view CURRENCY_RATES_VIEW
as
select
    cr.currency,
    cr.value,
    cr.char_code,
    lcru.value as last_update
from currency_rates cr, last_currency_rates_update lcru;

create or replace view OPERATION_VIEW
as
select
    o.id as operation_id,
    o.account_id,
    c.id as client_id,
    c.email as client_email,
    o.operation_date,
    o.transaction_sum,
    o.operation_kind,
    a.balance,
    a.account_name as account_name,
    a.currency as account_currency,
    o.currency_from,
    concat(c.last_name, ' ', c.first_name) as owner_full_name
from operations o
         join account a on a.id = o.account_id
         join client c on c.id = a.client_id;
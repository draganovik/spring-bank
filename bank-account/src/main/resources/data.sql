-- Initial data entries
insert into bank_accounts(id, email, quantity_EUR, quantity_RSD, quantity_USD, quantity_CHF, quantity_GBP)
values (random_uuid(), 'demo@mail.com', 2000, 150000, 400, 150, 300);

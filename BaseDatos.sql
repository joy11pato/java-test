
CREATE DATABASE "accounts-db";

CREATE DATABASE "customers-db";


INSERT INTO "catalogs" ("id", "description", "mnemonic", "name", "value", "catalog_parent") VALUES (1, 'List of accounts', 'ACCOUNTS', 'ACCOUNTS', 'CUENTAS', NULL);
INSERT INTO "catalogs" ("id", "description", "mnemonic", "name", "value", "catalog_parent") VALUES (2, 'Account to save money', 'SAVE_ACCOUNT', 'SAVE_ACCOUNT', 'Ahorro', 1);
INSERT INTO "catalogs" ("id", "description", "mnemonic", "name", "value", "catalog_parent") VALUES (3, 'Dayli transactions account', 'CHECKING_ACCOUNT', 'CHECKING_ACCOUNT', 'Corriente', 1);
INSERT INTO "catalogs" ("id", "description", "mnemonic", "name", "value", "catalog_parent") VALUES (10, 'List of movement transactions', 'MOVEMENTS', 'MOVEMENTS', 'MOVIMIENTOS', NULL);
INSERT INTO "catalogs" ("id", "description", "mnemonic", "name", "value", "catalog_parent") VALUES (11, 'Deposit money', 'DEPOSIT', 'DEPOSIT', 'Déposito', 10);
INSERT INTO "catalogs" ("id", "description", "mnemonic", "name", "value", "catalog_parent") VALUES (12, 'to withdraw money', 'WITHDRAW', 'WITHDRAW', 'Retiro', 10);



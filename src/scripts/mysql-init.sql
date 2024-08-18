DROP DATABASE IF EXISTS restdb;
DROP USER IF EXISTS `restadmin`@`%`;
--   utf8mb4 is the recommended character set for MySQL 5.7 and later
CREATE DATABASE IF NOT EXISTS restdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- restadmin is the db user that will be used by the REST API to access the database
CREATE USER IF NOT EXISTS `restadmin`@`%` IDENTIFIED WITH mysql_native_password BY 'password';
-- Grant all authorities on the restdb database to the restadmin user
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, REFERENCES, INDEX, ALTER, EXECUTE, CREATE VIEW, SHOW VIEW,
CREATE ROUTINE, ALTER ROUTINE, EVENT, TRIGGER ON `restdb`.* TO `restadmin`@`%`;
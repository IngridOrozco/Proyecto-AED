SELECT User, Host
FROM mysql.user
ORDER BY User, Host;
-- No elimina root solo la cuenta de la aplicacion SGAI.
DROP USER IF EXISTS sgai_app@localhost;

-- Eliminar la base de datos completa del proyecto.
DROP DATABASE IF EXISTS `sgai-soldano-db`;

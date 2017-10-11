#datos.sql
#Archivo batch para ingresar datos de prueba para la base de datos del proyecto de EBD

USE parquimetros;

INSERT INTO Conductores VALUES (1111111111, 'Carlos', 'Villar', 'Villarino 366', '2914821117', 1111);
INSERT INTO Conductores VALUES (1222222222, 'Fabian', 'Segovia', 'Tucuman 2177', '29148172640', 1222);
INSERT INTO Conductores VALUES (1333333333, 'Miguel', 'Perez', 'Undiano 36', '2914998398', 1333);
INSERT INTO Conductores VALUES (1444444444, 'Sebastian', 'Andrade', 'Alem 975', '2914477125', 1444);
INSERT INTO Conductores VALUES (1555555555, 'Andres', 'Quinteros', 'Paunero 1565', '2914983741', 1555);
INSERT INTO Conductores VALUES (1666666666, 'Fabian', 'Perez', 'Paunero 1565', '2914546645', 1666);
INSERT INTO Conductores VALUES (1777777777, 'Beatriz', 'Gutierrez', 'Villarino 366', '2914998697', 1777);
INSERT INTO Conductores VALUES (1888888888, 'Julieta', 'Gimenez', 'Colon 1080', '2914443412', 1888);

INSERT INTO Automoviles VALUES ('AAAAAA', 'Ford', 'Ka', 'Negro', 1111111111);
INSERT INTO Automoviles VALUES ('BBBBBB', 'Toyota', 'Etios', 'Rojo', 1222222222);
INSERT INTO Automoviles VALUES ('CCCCCC', 'Volkswagen', 'Amarok', 'Gris', 1333333333);
INSERT INTO Automoviles VALUES ('DDDDDD', 'Renault', 'Fluence', 'Blanco', 1444444444);
INSERT INTO Automoviles VALUES ('EEEEEE', 'Fiat', 'Toro', 'Rojo', 1555555555);
INSERT INTO Automoviles VALUES ('FFFFFF', 'Renault', 'Clio', 'Gris', 1666666666);
INSERT INTO Automoviles VALUES ('GGGGGG', 'Ford', 'Ecosport', 'Negro', 1777777777);
INSERT INTO Automoviles VALUES ('HHHHHH', 'Peugeot', '307', 'Negro', 1888888888);
INSERT INTO Automoviles VALUES ('IIIIII', 'Chevrolet', 'Agile', 'Gris', 1555555555);
INSERT INTO Automoviles VALUES ('JJJJJJ', 'Fiat', 'Toro', 'Rojo', 1222222222);
INSERT INTO Automoviles VALUES ('KKKKKK', 'Fiat', 'Palio', 'Azul', 1444444444);

INSERT INTO Tipos_tarjeta VALUES ('Regular', 0.00);
INSERT INTO Tipos_tarjeta VALUES ('Jubilado', 0.15);
INSERT INTO Tipos_tarjeta VALUES ('Gremio', 0.50);
INSERT INTO Tipos_tarjeta VALUES ('Estudiante', 0.40);
INSERT INTO Tipos_tarjeta VALUES ('Discapacidad', 1.00);

# hay automoviles con mas de 1 tarjeta de cada tipo, automoviles con 0 tarjetas 
#y automoviles con mas de 1 tarjeta del mismo tipo
INSERT INTO Tarjetas(saldo, tipo, patente) VALUES (066.75, 'Regular', 'AAAAAA');
INSERT INTO Tarjetas(saldo, tipo, patente) VALUES (144.25, 'Regular', 'BBBBBB');
INSERT INTO Tarjetas(saldo, tipo, patente) VALUES (095.25, 'Jubilado', 'CCCCCC');
INSERT INTO Tarjetas(saldo, tipo, patente) VALUES (178.50, 'Gremio', 'DDDDDD');
INSERT INTO Tarjetas(saldo, tipo, patente) VALUES (133.00, 'Discapacidad', 'EEEEEE');
INSERT INTO Tarjetas(saldo, tipo, patente) VALUES (422.99, 'Estudiante', 'FFFFFF');
INSERT INTO Tarjetas(saldo, tipo, patente) VALUES (234.12, 'Gremio', 'GGGGGG');
INSERT INTO Tarjetas(saldo, tipo, patente) VALUES (451.87, 'Discapacidad', 'HHHHHH');
INSERT INTO Tarjetas(saldo, tipo, patente) VALUES (849.25, 'Regular', 'IIIIII');
INSERT INTO Tarjetas(saldo, tipo, patente) VALUES (002.45, 'Jubilado', 'JJJJJJ');
INSERT INTO Tarjetas(saldo, tipo, patente) VALUES (000.00, 'Estudiante', 'KKKKKK');
INSERT INTO Tarjetas(saldo, tipo, patente) VALUES (999.00, 'Jubilado', 'BBBBBB');
INSERT INTO Tarjetas(saldo, tipo, patente) VALUES (513.00, 'Regular', 'AAAAAA');

INSERT INTO Inspectores VALUES (3111, 3111111111, 'Miguel', 'Sosa', md5('password'));
INSERT INTO Inspectores VALUES (3222, 3222222222, 'Alberto', 'Gonzales', md5('123456'));
INSERT INTO Inspectores VALUES (3333, 3333333333, 'Nicolas', 'Alvarez', md5('Tr0ub4dor&3'));
INSERT INTO Inspectores VALUES (3444, 3444444444, 'Pedro', 'Villar', md5('CorrectHorseBatteryStaple'));
INSERT INTO Inspectores VALUES (3555, 3555555555, 'Manuel', 'Acosta', md5('correct'));
INSERT INTO Inspectores VALUES (3666, 3666666666, 'Pepe', 'Landas', md5(''));

INSERT INTO Ubicaciones VALUES ('c1', 0, 999.99);
INSERT INTO Ubicaciones VALUES ('c2', 5, 75.00);
INSERT INTO Ubicaciones VALUES ('c3', 10, 400.99);
INSERT INTO Ubicaciones VALUES ('c4', 15, 50.00);
INSERT INTO Ubicaciones VALUES ('c5', 120, 00.00);

INSERT INTO Parquimetros VALUES (4111, 1, 'c1', 0);
INSERT INTO Parquimetros VALUES (4222, 2, 'c2', 5);
INSERT INTO Parquimetros VALUES (4333, 3, 'c3', 10);
INSERT INTO Parquimetros VALUES (4444, 4, 'c1', 0);
INSERT INTO Parquimetros VALUES (4555, 5, 'c4', 15);
INSERT INTO Parquimetros VALUES (4666, 6, 'c1', 0);
INSERT INTO Parquimetros VALUES (4777, 7, 'c3', 10);

#ubicacion c1
INSERT INTO Estacionamientos VALUES (1, 4111, '2017-9-11', '07:55:36', NULL, NULL);
INSERT INTO Estacionamientos VALUES (6, 4444, '2017-9-11', '11:05:44', NULL, NULL);
INSERT INTO Estacionamientos VALUES (10, 4777, '2017-9-11', '13:16:53', NULL, NULL);
INSERT INTO Estacionamientos VALUES (8, 4777, '2017-9-12', '13:17:53', NULL, NULL);
INSERT INTO Estacionamientos VALUES (3, 4777, '2017-9-12', '13:18:53', NULL, NULL);

#ubicacion con 1 parquimetro, todos libres
INSERT INTO Estacionamientos VALUES (5, 4222, '2017-9-11', '13:30:25', '2017-9-11', '14:24:12');

#ubicacion con 2 parquimetros, 1 libre y 1 vacio
INSERT INTO Estacionamientos VALUES (2, 4333, '2017-9-11', '14:00:01', NULL, NULL);
INSERT INTO Estacionamientos VALUES (3, 4777, '2017-9-11', '13:15:53', '2017-9-11', '15:10:00');
INSERT INTO Estacionamientos VALUES (4, 4777, '2017-9-11', '15:30:25', '2017-9-11', '15:35:00');

INSERT INTO Accede VALUES (3111, 4111, '2017-9-11', '9:24:36');
INSERT INTO Accede VALUES (3222, 4111, '2017-9-11', '16:24:11');
INSERT INTO Accede VALUES (3333, 4666, '2017-9-12', '18:42:41');
INSERT INTO Accede VALUES (3111, 4222, '2017-9-14', '18:55:12');
INSERT INTO Accede VALUES (3555, 4555, '2017-9-13', '18:59:42');

INSERT INTO Asociado_con(legajo, calle, altura, dia, turno) VALUES (3111, 'c1', 0, 'Lu', 'M');
INSERT INTO Asociado_con(legajo, calle, altura, dia, turno) VALUES (3222, 'c1', 0, 'Lu', 'T');
INSERT INTO Asociado_con(legajo, calle, altura, dia, turno) VALUES (3333, 'c4', 15, 'Ma', 'T');
INSERT INTO Asociado_con(legajo, calle, altura, dia, turno) VALUES (3111, 'c2', 5, 'Ju', 'T');
INSERT INTO Asociado_con(legajo, calle, altura, dia, turno) VALUES (3555, 'c4', 15, 'Mi', 'T');
INSERT INTO Asociado_con(legajo, calle, altura, dia, turno) VALUES (3666, 'c2', 5, 'Mi', 'T');

INSERT INTO Multa VALUES (611111, '2017-9-11', '07:04:21', 'AAAAAA',1);
INSERT INTO Multa VALUES (622222, '2017-9-11', '08:05:12', 'BBBBBB',1);
INSERT INTO Multa VALUES (633333, '2017-9-11', '12:22:22', 'CCCCCC',2);
INSERT INTO Multa VALUES (644444, '2017-9-11', '13:28:55', 'DDDDDD',2);
INSERT INTO Multa VALUES (655555, '2017-9-11', '18:05:43', 'EEEEEE',2);

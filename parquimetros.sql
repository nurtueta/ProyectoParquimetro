#parquimetros.sql 
#Archivo batch para crear la base de datos del proyecto de EBD

CREATE DATABASE parquimetros;

USE parquimetros;

#-------------------------------------------------------------------------
# Creación Tablas para las entidades

CREATE TABLE Conductores (
 dni INT UNSIGNED NOT NULL,
 nombre VARCHAR(30) NOT NULL,
 apellido VARCHAR(30) NOT NULL,
 direccion VARCHAR(30) NOT NULL,
 telefono VARCHAR(30),
 registro INT UNSIGNED NOT NULL,
 
 CONSTRAINT pk_Conductores
 PRIMARY KEY (dni)
) ENGINE=InnoDB;

CREATE TABLE Automoviles (
 patente CHAR(6) NOT NULL,
 marca VARCHAR(20) NOT NULL,
 modelo VARCHAR(20) NOT NULL,
 color VARCHAR (20) NOT NULL,
 dni INT UNSIGNED NOT NULL,
 
 CONSTRAINT pk_Automoviles
 PRIMARY KEY (patente),
 
 FOREIGN KEY (dni) REFERENCES Conductores (dni)
	ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Tipos_tarjeta (
 tipo VARCHAR(30) NOT NULL,
 descuento DECIMAL(3,2) UNSIGNED NOT NULL,
 
 CHECK (descuento <=1.00),

 CONSTRAINT pk_Tipos_tarjeta
 PRIMARY KEY (tipo)
) ENGINE=InnoDB;

CREATE TABLE Tarjetas (
 id_tarjeta INT UNSIGNED AUTO_INCREMENT NOT NULL,
 saldo DECIMAL(5,2) NOT NULL,
 tipo VARCHAR(30) NOT NULL,
 patente CHAR(6) NOT NULL,

 CONSTRAINT pk_Tarjetas
 PRIMARY KEY (id_tarjeta),
 
 CONSTRAINT FK_Tarjetas_tipo
 FOREIGN KEY (tipo) REFERENCES Tipos_tarjeta (tipo)
	ON DELETE RESTRICT ON UPDATE CASCADE,
 
 CONSTRAINT FK_Tarjetas_patente
 FOREIGN KEY (patente) REFERENCES Automoviles (patente)
	ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Inspectores (
 legajo INT UNSIGNED NOT NULL,
 dni INT UNSIGNED NOT NULL,
 nombre VARCHAR(30) NOT NULL,
 apellido VARCHAR(30) NOT NULL,
 password CHAR(32) NOT NULL,
 
 CONSTRAINT pk_Inspectores
 PRIMARY KEY (legajo)
) ENGINE=InnoDB;

CREATE TABLE Ubicaciones (
 calle VARCHAR(30) NOT NULL,
 altura INT UNSIGNED NOT NULL,
 tarifa DECIMAL(5,2) UNSIGNED NOT NULL,
 
 CONSTRAINT pk_Ubicaciones
 PRIMARY KEY (calle,altura)
 
) ENGINE=InnoDB;

CREATE TABLE Parquimetros (
 id_parq INT UNSIGNED NOT NULL,
 numero INT UNSIGNED NOT NULL,
 calle VARCHAR(30) NOT NULL,
 altura INT UNSIGNED NOT NULL,
 
 CONSTRAINT pk_Parquimetros
 PRIMARY KEY (id_parq),
 
 CONSTRAINT FK_Parquimetros
 FOREIGN KEY (calle,altura) REFERENCES Ubicaciones (calle,altura)
	ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;
#-------------------------------------------------------------------------
# Creación Tablas para las relaciones

CREATE TABLE Estacionamientos (
 id_tarjeta INT UNSIGNED NOT NULL,
 id_parq INT UNSIGNED NOT NULL,
 fecha_ent DATE NOT NULL,
 hora_ent TIME NOT NULL,
 fecha_sal DATE,
 hora_sal TIME,
 
 CONSTRAINT pk_Estacionamientos
 PRIMARY KEY (id_parq,fecha_ent,hora_ent),
 
 CONSTRAINT FK_Estacionamientos
 FOREIGN KEY (id_tarjeta) REFERENCES Tarjetas (id_tarjeta)
	ON DELETE RESTRICT ON UPDATE CASCADE,
 
 CONSTRAINT FK_Estacionamientos_id_parq
 FOREIGN KEY (id_parq) REFERENCES Parquimetros (id_parq)
	ON DELETE RESTRICT ON UPDATE CASCADE

) ENGINE=InnoDB;

CREATE TABLE Accede (
 legajo INT UNSIGNED NOT NULL,
 id_parq INT UNSIGNED NOT NULL,
 fecha DATE NOT NULL,
 hora TIME NOT NULL,
 
 CONSTRAINT pk_Accede
 PRIMARY KEY (id_parq,fecha,hora),

 CONSTRAINT FK_Accede_legajo
 FOREIGN KEY (legajo) REFERENCES Inspectores (legajo)
   ON DELETE RESTRICT ON UPDATE CASCADE,
 
 CONSTRAINT FK_Accede_id_parq
 FOREIGN KEY (id_parq) REFERENCES Parquimetros (id_parq)
   ON DELETE RESTRICT ON UPDATE RESTRICT
 
) ENGINE=InnoDB;

CREATE TABLE Asociado_con (
 id_asociado_con INT UNSIGNED AUTO_INCREMENT NOT NULL,
 legajo INT UNSIGNED NOT NULL,
 calle VARCHAR(30) NOT NULL,
 altura INT UNSIGNED NOT NULL,
 dia CHAR(2) NOT NULL,
 turno CHAR(1) NOT NULL,

 CHECK ( dia = 'Lu' OR dia = 'Ma' OR dia = 'Mi' 
 		OR dia = 'Ju' OR dia = 'Vi' OR dia = 'Sa' OR dia = 'Do'),

 CHECK ( turno = 'M' OR turno = 'T'),
 
 CONSTRAINT pk_Asociado_con
 PRIMARY KEY (id_asociado_con),
 
 CONSTRAINT FK_Asociado_con_legajo
 FOREIGN KEY (legajo) REFERENCES Inspectores (legajo)
    ON DELETE RESTRICT ON UPDATE CASCADE,
 
 CONSTRAINT FK_Asociado_con_calle
 FOREIGN KEY (calle,altura) REFERENCES Ubicaciones (calle,altura)
    ON DELETE RESTRICT ON UPDATE CASCADE
 
) ENGINE=InnoDB;

CREATE TABLE Multa (
 numero INT UNSIGNED AUTO_INCREMENT NOT NULL,
 fecha DATE NOT NULL,
 hora TIME NOT NULL,
 patente CHAR(6) NOT NULL,
 id_asociado_con INT UNSIGNED NOT NULL,

 CONSTRAINT pk_Multa
 PRIMARY KEY (numero),
 
 CONSTRAINT FK_Multa_patente
 FOREIGN KEY (patente) REFERENCES Automoviles (patente)
    ON DELETE RESTRICT ON UPDATE CASCADE,
 
 CONSTRAINT FK_Multa_id_asociado_con
 FOREIGN KEY (id_asociado_con) REFERENCES Asociado_con (id_asociado_con)
    ON DELETE RESTRICT ON UPDATE CASCADE
 
) ENGINE=InnoDB;

CREATE TABLE Ventas(
 id_tarjeta INT UNSIGNED NOT NULL,
 saldo DECIMAL(5,2) NOT NULL,
 tipo_tarjeta VARCHAR(30) NOT NULL,
 fecha DATE NOT NULL,
 hora TIME NOT NULL,

 CONSTRAINT pk_Ventas
 PRIMARY KEY (id_tarjeta)

)ENGINE=InnoDB;

#-------------------------------------------------------------------------
# Creación de vistas 

   CREATE VIEW estacionados as
   SELECT patente, calle, altura 
   FROM (Tarjetas JOIN Parquimetros JOIN Estacionamientos 
   ON Parquimetros.id_parq = Estacionamientos.id_parq AND Tarjetas.id_tarjeta = Estacionamientos.id_tarjeta)
   WHERE fecha_sal IS NULL;
#-------------------------------------------------------------------------
# Creacion de Stored Procedures
delimiter !
	CREATE PROCEDURE conectar(IN id_tarjeta INTEGER, IN id_parquimetro INTEGER)
		BEGIN
			DECLARE ntarjetas INTEGER UNSIGNED;
			DECLARE nestacionados INTEGER;
			DECLARE pat CHAR(6);
			DECLARE id_tarjeta2 INTEGER;
			DECLARE EXIT HANDLER FOR SQLEXCEPTION
				BEGIN
					SELECT 'SQLEXCEPTION!, transaccion abortada' AS resultado;
					ROLLBACK;
				END;
			START TRANSACTION;
				SET ntarjetas = (SELECT COUNT(*) FROM Tarjetas WHERE Tarjetas.id_tarjeta = id_tarjeta LOCK IN SHARE MODE);
				IF ntarjetas = 0 THEN
					SELECT 'N/A' AS operacion, 'error' AS resultado, 'no existe la tarjeta' AS causa;
				ELSE
					SET nestacionados = (SELECT COUNT(*) FROM estacionados JOIN Tarjetas 
						ON estacionados.patente = Tarjetas.patente WHERE Tarjetas.id_tarjeta = id_tarjeta LOCK IN SHARE MODE);
					IF nestacionados = 0 THEN 
						CALL apertura(id_tarjeta, id_parquimetro);
					ELSE
						SET pat = (SELECT patente FROM Tarjetas WHERE Tarjetas.id_tarjeta = id_tarjeta LIMIT 1 LOCK IN SHARE MODE);
						SELECT tarjetas.id_tarjeta INTO id_tarjeta2 FROM 
							tarjetas JOIN estacionamientos ON tarjetas.id_tarjeta = estacionamientos.id_tarjeta AND estacionamientos.fecha_sal IS NULL WHERE 
							tarjetas.patente = pat LIMIT 1 LOCK IN SHARE MODE;
						CALL cierre(id_tarjeta2, id_parquimetro);
					END IF;
				END IF;
			COMMIT;
		END; !

	CREATE PROCEDURE apertura(IN id_tarjeta INTEGER , in id_parq INTEGER)
		BEGIN
			DECLARE sal DECIMAL(5,2);
			DECLARE tar DECIMAL(5,2) UNSIGNED;
			DECLARE tiempo INTEGER UNSIGNED;
			DECLARE des DECIMAL(3,2) UNSIGNED;
			DECLARE nparquimetros INTEGER UNSIGNED;
			DECLARE EXIT HANDLER FOR SQLEXCEPTION
				BEGIN
					SELECT 'SQLEXCEPTION!, transaccion abortada' AS resultado;
					ROLLBACK;
				END;
			START TRANSACTION;
				SET nparquimetros = (SELECT COUNT(*) FROM Parquimetros WHERE Parquimetros.id_parq = id_parq LOCK IN SHARE MODE);
				IF nparquimetros = 0 THEN
					SELECT 'apertura' AS operacion, 'error' AS resultado, 'parquimetro inexistente' AS causa;
				ELSE
					SELECT descuento INTO des FROM (Tipos_tarjeta JOIN Tarjetas ON 
						Tipos_tarjeta.tipo = Tarjetas.tipo) WHERE 
						Tarjetas.id_tarjeta = id_tarjeta LIMIT 1 LOCK IN SHARE MODE;
					IF des = 1.00 THEN
						INSERT INTO Estacionamientos VALUES (id_tarjeta, id_parq, curdate(), curtime(), NULL, NULL);
						SELECT 'apertura' AS operacion, 'exito' AS resultado, 'inf' AS tiempo;
					ELSE
						SELECT saldo INTO sal FROM Tarjetas WHERE Tarjetas.id_tarjeta = id_tarjeta LIMIT 1 LOCK IN SHARE MODE;
						IF sal>0 THEN
							INSERT INTO Estacionamientos VALUES (id_tarjeta, id_parq, curdate(), curtime(), NULL, NULL);
							SELECT tarifa INTO tar FROM (Ubicaciones JOIN Parquimetros 
								ON Ubicaciones.calle = Parquimetros.calle 
								AND Ubicaciones.altura = Parquimetros.altura) 
								WHERE Parquimetros.id_parq = id_parq LIMIT 1 LOCK IN SHARE MODE;
							SET tiempo = sal/(tar*(1-des));
							SELECT 'apertura' AS operacion, 'exito' AS resultado, CONCAT(tiempo,' min.') AS tiempo;				
						ELSE
							SELECT 'apertura' AS operacion, 'error' AS resultado, 'sin saldo' AS causa;
						END IF;
					END IF;
				END IF;
			COMMIT;
		END; !

	CREATE PROCEDURE cierre(IN id_tarjeta INTEGER , in id_parq INTEGER)
		BEGIN
			DECLARE nparquimetros INTEGER UNSIGNED;
			DECLARE tiempo INTEGER UNSIGNED;
			DECLARE fent DATE;
			DECLARE hent TIME;	
			DECLARE vsaldo DECIMAL(5,2);
			DECLARE nsaldo DECIMAL(5,2);
			DECLARE tar DECIMAL(5,2) UNSIGNED;
			DECLARE des DECIMAL(3,2) UNSIGNED;
			DECLARE id_parq_ent INTEGER UNSIGNED;
			DECLARE EXIT HANDLER FOR SQLEXCEPTION
				BEGIN
					SELECT 'SQLEXCEPTION!, transaccion abortada' AS resultado;
					ROLLBACK;
				END;
			START TRANSACTION;
				SET nparquimetros = (SELECT COUNT(*) FROM Parquimetros WHERE Parquimetros.id_parq = id_parq LOCK IN SHARE MODE);
				IF nparquimetros = 0 THEN
					SELECT 'cierre' AS operacion, 'error' AS resultado, 'parquimetro inexistente' AS causa;
				ELSE
					SELECT fecha_ent INTO fent FROM Estacionamientos 
						WHERE Estacionamientos.id_tarjeta = id_tarjeta AND fecha_sal IS NULL AND hora_sal IS NULL LIMIT 1 LOCK IN SHARE MODE; 
					SELECT hora_ent INTO hent FROM Estacionamientos 
						WHERE Estacionamientos.id_tarjeta = id_tarjeta AND fecha_sal IS NULL AND hora_sal IS NULL LIMIT 1 LOCK IN SHARE MODE;
					SELECT id_parq INTO id_parq_ent FROM Estacionamientos
							WHERE Estacionamientos.id_tarjeta = id_tarjeta AND fecha_sal IS NULL AND hora_sal IS NULL LIMIT 1 LOCK IN SHARE MODE;
					SET tiempo = datediff(curdate(), fent)*24*60+time_to_sec(timediff(curtime(),hent))/60;
					UPDATE Estacionamientos SET fecha_sal=curdate(), hora_sal=curtime() 
						WHERE Estacionamientos.id_tarjeta = id_tarjeta AND fecha_sal IS NULL AND hora_sal IS NULL;
					SELECT descuento INTO des FROM (Tipos_tarjeta JOIN Tarjetas 
						ON Tipos_tarjeta.tipo = Tarjetas.tipo) 
						WHERE Tarjetas.id_tarjeta = id_tarjeta LIMIT 1 LOCK IN SHARE MODE;
					IF des<1 THEN
						SELECT saldo INTO vsaldo FROM Tarjetas where Tarjetas.id_tarjeta = id_tarjeta LIMIT 1 FOR UPDATE;
						SELECT tarifa INTO tar FROM (Ubicaciones JOIN Parquimetros 
							ON Ubicaciones.calle = Parquimetros.calle 
							AND Ubicaciones.altura = Parquimetros.altura) 
							WHERE Parquimetros.id_parq = id_parq_ent LIMIT 1 LOCK IN SHARE MODE;
						IF (vsaldo - tiempo*(tar*(1-des))) < -999.99 THEN
							SET nsaldo = -999.99;
						ELSE
							SET nsaldo = (vsaldo - tiempo*(tar*(1-des)));
						END IF;
						UPDATE Tarjetas SET saldo = nsaldo WHERE Tarjetas.id_tarjeta = id_tarjeta;
					END IF;
					SELECT saldo INTO nsaldo FROM Tarjetas where Tarjetas.id_tarjeta = id_tarjeta LIMIT 1 LOCK IN SHARE MODE;
					SELECT 'cierre' AS operacion, 'exito' AS resultado, CONCAT( '$',nsaldo) AS saldo;
				END IF;
			COMMIT;
		END; !
delimiter ;
#-------------------------------------------------------------------------
# Creacion de triggers
	delimiter !
	CREATE TRIGGER venta_update
	AFTER INSERT ON Tarjetas
	FOR EACH ROW
	BEGIN
		INSERT INTO Ventas(id_tarjeta, saldo, tipo_tarjeta, fecha, hora) values(new.id_tarjeta, new.saldo, new.tipo, curdate(), curtime());
	END; !
	delimiter ;
#-------------------------------------------------------------------------
# Creación de usuarios y otorgamiento de privilegios

	CREATE USER admin@localhost IDENTIFIED BY 'admin';	
	GRANT ALL PRIVILEGES ON parquimetros.* TO admin@localhost WITH GRANT OPTION;
	GRANT CREATE USER ON *.* TO admin@localhost;

	CREATE USER venta@'%' IDENTIFIED BY 'venta';	
    GRANT INSERT ON parquimetros.Tarjetas TO venta@'%';
	GRANT SELECT ON parquimetros.Automoviles To venta@'%';
	GRANT SELECT ON parquimetros.Tipos_tarjeta To venta@'%';
	GRANT SELECT ON parquimetros.Tarjetas To venta@'%';
	
	CREATE USER inspector@'%' IDENTIFIED BY 'inspector';	
	GRANT SELECT ON parquimetros.Inspectores TO inspector@'%';	
	GRANT SELECT ON parquimetros.estacionados TO inspector@'%';
	GRANT SELECT ON parquimetros.Multa TO inspector@'%';
	GRANT SELECT ON parquimetros.Accede TO inspector@'%';
	GRANT SELECT ON parquimetros.Parquimetros TO inspector@'%';
	GRANT SELECT ON parquimetros.Asociado_con TO inspector@'%';
	GRANT SELECT ON parquimetros.Automoviles TO inspector@'%';
	GRANT INSERT ON parquimetros.Multa TO inspector@'%';	
	GRANT INSERT ON parquimetros.Accede TO inspector@'%';


	CREATE USER parquimetro@'%' IDENTIFIED BY 'parq';
	GRANT SELECT ON parquimetros.Parquimetros TO parquimetro@'%';
	GRANT SELECT ON parquimetros.Estacionamientos TO parquimetro@'%';
	GRANT SELECT ON parquimetros.Tarjetas TO parquimetro@'%';
	GRANT INSERT ON parquimetros.Estacionamientos TO parquimetro@'%';
	GRANT execute ON PROCEDURE parquimetros.conectar TO parquimetro@'%';


#-------------------------------------------------------------------------
#fin
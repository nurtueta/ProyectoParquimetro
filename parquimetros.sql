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
#-------------------------------------------------------------------------
# Creación Tablas para las relaciones

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
#-------------------------------------------------------------------------
# Creación de vistas 

   CREATE VIEW estacionados AS
   SELECT patente, calle, altura 
   FROM (Tarjetas JOIN Parquimetros JOIN Estacionamientos 
   ON Parquimetros.id_parq = Estacionamientos.id_parq AND Tarjetas.id_tarjeta = Estacionamientos.id_tarjeta)
   WHERE fecha_sal IS NULL;
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
	GRANT INSERT ON parquimetros.Multa TO inspector@'%';	
	GRANT INSERT ON parquimetros.Accede TO inspector@'%';
	GRANT SELECT ON parquimetros.Multa TO inspector@'%';
	GRANT SELECT ON parquimetros.Accede TO inspector@'%';
	GRANT SELECT ON parquimetros.Parquimetros TO inspector@'%';
#-------------------------------------------------------------------------
#fin
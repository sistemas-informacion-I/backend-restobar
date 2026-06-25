-- ============================================================
-- POBLACION MASIVA PARA BD DE PRODUCCION (NUBE)
-- Triplicado de datos para presentacion
-- Sin procedimientos almacenados ni triggers
-- Con rollback automatico si falla cualquiera
-- ============================================================

BEGIN;

-- ============================================================
-- 1. USUARIOS NUEVOS - EMPLEADOS
-- ============================================================

INSERT INTO usuario (ci, nombre, apellido, username, password_hash, telefono, sexo, correo, direccion, intentos_fallidos, estado_acceso, activo, tipo_usuario, fecha_registro)
SELECT v.* FROM (VALUES
('C001EM','Gabriela','Lopez','gabriela.lopez2','HASH123','70011223','F','gabriela.lopez2@gmail.com','Av. Beni #234',0,'HABILITADO',TRUE,'E',CURRENT_DATE - INTERVAL '540 days'),
('C002EM','Miguel','Torres','miguel.torres2','HASH123','70112233','M','miguel.torres2@hotmail.com','Calle Sucre #567',0,'HABILITADO',TRUE,'E',CURRENT_DATE - INTERVAL '535 days'),
('C003EM','Paola','Vargas','paola.vargas','HASH123','70223344','F','paola.vargas@yahoo.com','Zona Norte 4to anillo',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '540 days'),
('C004EM','Andres','Mendoza','andres.mendoza2','HASH123','70334455','M','andres.mendoza2@gmail.com','Av. Santos Dumont #890',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '530 days'),
('C005EM','Claudia','Rojas','claudia.rojas','HASH123','70445566','F','claudia.rojas@gmail.com','Calle Ballivian #321',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '520 days'),
('C006EM','Fernando','Aguilar','fernando.aguilar2','HASH123','70556677','M','fernando.aguilar2@hotmail.com','Av. Grigota #654',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '510 days'),
('C007EM','Patricia','Salazar','patricia.salazar','HASH123','70667788','F','patricia.salazar@gmail.com','Barrio Las Palmas',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '500 days'),
('C008EM','Ricardo','Ortiz','ricardo.ortiz2','HASH123','70778899','M','ricardo.ortiz2@gmail.com','Av. Virgen de Cotoca #111',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '490 days'),
('C009EM','Sofia','Ramirez','sofia.ramirez','HASH123','70889900','F','sofia.ramirez@gmail.com','Calle Cochabamba #222',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '480 days'),
('C010EM','Diego','Flores','diego.flores2','HASH123','70990011','M','diego.flores2@gmail.com','Av. Pirai #333',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '470 days'),
('C011EM','Valeria','Castro','valeria.castro','HASH123','71001122','F','valeria.castro@gmail.com','Zona Equipetrol Calle 3',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '460 days'),
('C012EM','Jorge','Martinez','jorge.martinez2','HASH123','71112233','M','jorge.martinez2@hotmail.com','Av. Alemana #444',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '450 days'),
('C013EM','Carolina','Gomez','carolina.gomez','HASH123','71223344','F','carolina.gomez@gmail.com','Calle Libertad #555',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '440 days'),
('C014EM','Mauricio','Fernandez','mauricio.fernandez','HASH123','71334455','M','mauricio.fernandez@gmail.com','Av. Busch #666',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '430 days'),
('C015EM','Natalia','Paz','natalia.paz','HASH123','71445566','F','natalia.paz@gmail.com','Barrio Sirari Calle 7',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '420 days'),
('C016EM','Esteban','Quiroga','esteban.quiroga','HASH123','71556677','M','esteban.quiroga@gmail.com','Av. Mutualista #101',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '410 days'),
('C017EM','Lucia','Delgado','lucia.delgado','HASH123','71667788','F','lucia.delgado@hotmail.com','Calle Warnes #202',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '400 days'),
('C018EM','Hernan','Silva','hernan.silva','HASH123','71778899','M','hernan.silva@gmail.com','Av. Cristo Redentor #303',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '390 days'),
('C019EM','Camila','Morales','camila.morales','HASH123','71889900','F','camila.morales@gmail.com','Zona Sur Calle 8',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '380 days'),
('C020EM','Sebastian','Reyes','sebastian.reyes','HASH123','71990011','M','sebastian.reyes@gmail.com','Av. Alemana #404',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '370 days'),
('C021EM','Andrea','Garcia','andrea.garcia2','HASH123','72001122','F','andrea.garcia2@gmail.com','Calle Independencia #505',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '360 days'),
('C022EM','Marcelo','Pinto','marcelo.pinto','HASH123','72112233','M','marcelo.pinto@hotmail.com','Av. San Aurelio #606',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '350 days'),
('C023EM','Veronica','Santos','veronica.santos','HASH123','72223344','F','veronica.santos@gmail.com','Barrio Urbari',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '340 days'),
('C024EM','Cristian','Rivera','cristian.rivera','HASH123','72334455','M','cristian.rivera@gmail.com','Av. Centenario #707',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '330 days'),
('C025EM','Daniela','Pena','daniela.pena2','HASH123','72445566','F','daniela.pena2@gmail.com','Calle Florida #808',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '320 days'),
('C026EM','Oscar','Villalba','oscar.villalba','HASH123','72556677','M','oscar.villalba@gmail.com','Av. Radial 26 #909',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '310 days'),
('C027EM','Monica','Serrano','monica.serrano','HASH123','72667788','F','monica.serrano@gmail.com','Zona Norte Calle 12',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '300 days'),
('C028EM','Alvaro','Campos','alvaro.campos','HASH123','72778899','M','alvaro.campos@gmail.com','Av. Santos Dumont #1110',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '290 days'),
('C029EM','Rocio','Navarro','rocio.navarro','HASH123','72889900','F','rocio.navarro@gmail.com','Calle Bolivar #1212',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '280 days'),
('C030EM','Gustavo','Arce','gustavo.arce','HASH123','72990011','M','gustavo.arce@gmail.com','Av. Grigota #1313',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '270 days'),
('C031EM','Elena','Maldonado','elena.maldonado','HASH123','73001122','F','elena.maldonado@gmail.com','Barrio Equipetrol Calle 5',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '260 days'),
('C032EM','Raul','Fuentes','raul.fuentes2','HASH123','73112233','M','raul.fuentes2@hotmail.com','Av. Pirai #1414',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '250 days'),
('C033EM','Isabel','Correa','isabel.correa','HASH123','73223344','F','isabel.correa@gmail.com','Calle Aroma #1515',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '240 days'),
('C034EM','Tomas','Guzman','tomas.guzman','HASH123','73334455','M','tomas.guzman@gmail.com','Av. Busch #1616',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '230 days'),
('C035EM','Mariana','Espinoza','mariana.espinoza','HASH123','73445566','F','mariana.espinoza@gmail.com','Zona Sirari Calle 9',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '220 days'),
('C036EM','Ignacio','Peralta','ignacio.peralta','HASH123','73556677','M','ignacio.peralta@gmail.com','Av. Centenario #1717',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '210 days'),
('C037EM','Florencia','Suarez','florencia.suarez','HASH123','73667788','F','florencia.suarez@hotmail.com','Calle Libertad #1818',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '200 days'),
('C038EM','Matias','Gonzalez','matias.gonzalez','HASH123','73778899','M','matias.gonzalez@gmail.com','Av. San Martin #1919',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '190 days'),
('C039EM','Alejandra','Rivas','alejandra.rivas','HASH123','73889900','F','alejandra.rivas@gmail.com','Zona Norte Calle 20',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '180 days'),
('C040EM','Rodrigo','Cordero','rodrigo.cordero','HASH123','73990011','M','rodrigo.cordero@gmail.com','Av. Pirai #2121',0,'HABILITADO',TRUE,'E', CURRENT_DATE - INTERVAL '170 days')
) AS v(ci, nombre, apellido, username, password_hash, telefono, sexo, correo, direccion, intentos_fallidos, estado_acceso, activo, tipo_usuario, fecha_registro)
WHERE NOT EXISTS (SELECT 1 FROM usuario u WHERE u.username = v.username OR u.ci = v.ci);

-- ============================================================
-- 1b. USUARIOS NUEVOS - CLIENTES
-- ============================================================

INSERT INTO usuario (ci, nombre, apellido, username, password_hash, telefono, sexo, correo, direccion, intentos_fallidos, estado_acceso, activo, tipo_usuario, fecha_registro)
SELECT v.* FROM (VALUES
('CL001','Raul','Mamani','raul.mamani','HASH123','76001122','M','raul.mamani@gmail.com','Zona Sur #100',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '300 days'),
('CL002','Carmen','Vargas','carmen.vargas','HASH123','76012233','F','carmen.vargas@gmail.com','Av. Busch #400',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '290 days'),
('CL003','Luis','Herrera','luis.herrera','HASH123','76023344','M','luis.herrera@hotmail.com','Calle Linares #500',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '280 days'),
('CL004','Ana','Torrico','ana.torrico','HASH123','76034455','F','ana.torrico@gmail.com','Barrio Equipetrol #200',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '270 days'),
('CL005','Pedro','Vaca','pedro.vaca','HASH123','76045566','M','pedro.vaca@gmail.com','Av. Salvietti #600',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '260 days'),
('CL006','Maria','Espinosa','maria.espinosa','HASH123','76056677','F','maria.espinosa@hotmail.com','Calle Warnes #700',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '250 days'),
('CL007','Joaquin','Pereira','joaquin.pereira','HASH123','76067788','M','joaquin.pereira@gmail.com','Av. Delfin Fuertes #800',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '240 days'),
('CL008','Laura','Gutierrez','laura.gutierrez','HASH123','76078899','F','laura.gutierrez@gmail.com','Zona Norte #900',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '230 days'),
('CL009','Carlos','Medina','carlos.medina','HASH123','76089900','M','carlos.medina@hotmail.com','Calle Colombia #1100',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '220 days'),
('CL010','Belen','Choque','belen.choque','HASH123','76090011','F','belen.choque@gmail.com','Av. Centenario #1200',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '210 days'),
('CL011','Marco','Apaza','marco.apaza','HASH123','76101122','M','marco.apaza@gmail.com','Barrio Sirari #1300',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '200 days'),
('CL012','Diana','Lopez','diana.lopez2','HASH123','76112233','F','diana.lopez2@gmail.com','Calle Bolivar #1400',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '190 days'),
('CL013','Sergio','Ramos','sergio.ramos','HASH123','76123344','M','sergio.ramos@gmail.com','Av. Pirai #1500',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '180 days'),
('CL014','Paola','Mita','paola.mita','HASH123','76134455','F','paola.mita@hotmail.com','Zona Sur #1600',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '170 days'),
('CL015','Andres','Cabrera','andres.cabrera','HASH123','76145566','M','andres.cabrera@gmail.com','Av. Grigota #1700',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '160 days'),
('CL016','Camila','Fernandez','camila.fernandez2','HASH123','76156677','F','camila.fernandez2@gmail.com','Calle Aroma #1800',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '150 days'),
('CL017','Mauricio','Luna','mauricio.luna','HASH123','76167788','M','mauricio.luna@gmail.com','Av. San Martin #1900',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '140 days'),
('CL018','Valentina','Rios','valentina.rios','HASH123','76178899','F','valentina.rios@hotmail.com','Barrio Urbari #2000',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '130 days'),
('CL019','Francisco','Mendez','francisco.mendez','HASH123','76189900','M','francisco.mendez@gmail.com','Calle Libertad #2100',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '120 days'),
('CL020','Isabella','Cruz','isabella.cruz','HASH123','76190011','F','isabella.cruz@gmail.com','Av. Alemana #2200',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '110 days'),
('CL021','Gabriel','Torrez','gabriel.torrez','HASH123','76201122','M','gabriel.torrez@gmail.com','Zona Equipetrol #2300',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '100 days'),
('CL022','Daniela','Yupa','daniela.yupa','HASH123','76212233','F','daniela.yupa@hotmail.com','Av. Virgen de Cotoca #2400',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '90 days'),
('CL023','Nicolas','Vargas','nicolas.vargas2','HASH123','76223344','M','nicolas.vargas2@gmail.com','Calle Cochabamba #2500',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '80 days'),
('CL024','Fernanda','Salvatierra','fernanda.salvatierra','HASH123','76234455','F','fernanda.salvatierra@gmail.com','Av. Santos Dumont #2600',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '70 days'),
('CL025','Roberto','Clavero','roberto.clavero','HASH123','76245566','M','roberto.clavero@gmail.com','Zona Norte #2700',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '60 days'),
('CL026','Sofia','Monje','sofia.monje','HASH123','76256677','F','sofia.monje@hotmail.com','Calle Sucre #2800',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '50 days'),
('CL027','Diego','Aragon','diego.aragon','HASH123','76267788','M','diego.aragon@gmail.com','Av. Busch #2900',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '40 days'),
('CL028','Jhenny','Quispe','jhenny.quispe','HASH123','76278899','F','jhenny.quispe@gmail.com','Barrio Las Palmas #3000',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '30 days'),
('CL029','Bryan','Caceres','bryan.caceres','HASH123','76289900','M','bryan.caceres@gmail.com','Av. Mutualista #3100',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '20 days'),
('CL030','Tatiana','Estrada','tatiana.estrada','HASH123','76290011','F','tatiana.estrada@hotmail.com','Calle Warnes #3200',0,'HABILITADO',TRUE,'C', CURRENT_DATE - INTERVAL '10 days')
) AS v(ci, nombre, apellido, username, password_hash, telefono, sexo, correo, direccion, intentos_fallidos, estado_acceso, activo, tipo_usuario, fecha_registro)
WHERE NOT EXISTS (SELECT 1 FROM usuario u WHERE u.username = v.username OR u.ci = v.ci);

-- ============================================================
-- 2. EMPLEADOS - 40 empleados distribuidos en 4 sucursales
-- ============================================================

INSERT INTO empleado (codigo_empleado, fecha_contratacion, salario, id_usuario, turno)
SELECT v.* FROM (VALUES
-- Sucursal 2 (SanMartin): usuarios C001EM-C010EM
('EMP-001', CURRENT_DATE - INTERVAL '540 days', 4500, (SELECT id_usuario FROM usuario WHERE username='gabriela.lopez2'), 'MA'),
('EMP-002', CURRENT_DATE - INTERVAL '500 days', 3300, (SELECT id_usuario FROM usuario WHERE username='miguel.torres2'), 'MA'),
('EMP-003', CURRENT_DATE - INTERVAL '480 days', 3300, (SELECT id_usuario FROM usuario WHERE username='paola.vargas'), 'MA'),
('EMP-004', CURRENT_DATE - INTERVAL '460 days', 4500, (SELECT id_usuario FROM usuario WHERE username='andres.mendoza2'), 'MA'),
('EMP-005', CURRENT_DATE - INTERVAL '440 days', 3300, (SELECT id_usuario FROM usuario WHERE username='claudia.rojas'), 'MA'),
('EMP-006', CURRENT_DATE - INTERVAL '420 days', 3300, (SELECT id_usuario FROM usuario WHERE username='fernando.aguilar2'), 'MA'),
('EMP-007', CURRENT_DATE - INTERVAL '400 days', 3300, (SELECT id_usuario FROM usuario WHERE username='patricia.salazar'), 'MA'),
('EMP-008', CURRENT_DATE - INTERVAL '380 days', 3300, (SELECT id_usuario FROM usuario WHERE username='ricardo.ortiz2'), 'MA'),
('EMP-009', CURRENT_DATE - INTERVAL '360 days', 3300, (SELECT id_usuario FROM usuario WHERE username='sofia.ramirez'), 'MA'),
('EMP-010', CURRENT_DATE - INTERVAL '340 days', 3300, (SELECT id_usuario FROM usuario WHERE username='diego.flores2'), 'MA'),
-- Sucursal 3 (Av.Beni): usuarios C011EM-C020EM
('EMP-011', CURRENT_DATE - INTERVAL '420 days', 4500, (SELECT id_usuario FROM usuario WHERE username='valeria.castro'), 'MA'),
('EMP-012', CURRENT_DATE - INTERVAL '400 days', 3300, (SELECT id_usuario FROM usuario WHERE username='jorge.martinez2'), 'MA'),
('EMP-013', CURRENT_DATE - INTERVAL '380 days', 3300, (SELECT id_usuario FROM usuario WHERE username='carolina.gomez'), 'MA'),
('EMP-014', CURRENT_DATE - INTERVAL '360 days', 4500, (SELECT id_usuario FROM usuario WHERE username='mauricio.fernandez'), 'MA'),
('EMP-015', CURRENT_DATE - INTERVAL '340 days', 3300, (SELECT id_usuario FROM usuario WHERE username='natalia.paz'), 'MA'),
('EMP-016', CURRENT_DATE - INTERVAL '320 days', 3300, (SELECT id_usuario FROM usuario WHERE username='esteban.quiroga'), 'MA'),
('EMP-017', CURRENT_DATE - INTERVAL '300 days', 3300, (SELECT id_usuario FROM usuario WHERE username='lucia.delgado'), 'MA'),
('EMP-018', CURRENT_DATE - INTERVAL '280 days', 3300, (SELECT id_usuario FROM usuario WHERE username='hernan.silva'), 'MA'),
('EMP-019', CURRENT_DATE - INTERVAL '260 days', 3300, (SELECT id_usuario FROM usuario WHERE username='camila.morales'), 'MA'),
('EMP-020', CURRENT_DATE - INTERVAL '240 days', 3300, (SELECT id_usuario FROM usuario WHERE username='sebastian.reyes'), 'MA'),
-- Sucursal 4 (Av.Pirai): usuarios C021EM-C030EM
('EMP-021', CURRENT_DATE - INTERVAL '300 days', 4500, (SELECT id_usuario FROM usuario WHERE username='andrea.garcia2'), 'MA'),
('EMP-022', CURRENT_DATE - INTERVAL '280 days', 3300, (SELECT id_usuario FROM usuario WHERE username='marcelo.pinto'), 'MA'),
('EMP-023', CURRENT_DATE - INTERVAL '260 days', 3300, (SELECT id_usuario FROM usuario WHERE username='veronica.santos'), 'MA'),
('EMP-024', CURRENT_DATE - INTERVAL '240 days', 4500, (SELECT id_usuario FROM usuario WHERE username='cristian.rivera'), 'MA'),
('EMP-025', CURRENT_DATE - INTERVAL '220 days', 3300, (SELECT id_usuario FROM usuario WHERE username='daniela.pena2'), 'MA'),
('EMP-026', CURRENT_DATE - INTERVAL '200 days', 3300, (SELECT id_usuario FROM usuario WHERE username='oscar.villalba'), 'MA'),
('EMP-027', CURRENT_DATE - INTERVAL '180 days', 3300, (SELECT id_usuario FROM usuario WHERE username='monica.serrano'), 'MA'),
('EMP-028', CURRENT_DATE - INTERVAL '160 days', 3300, (SELECT id_usuario FROM usuario WHERE username='alvaro.campos'), 'MA'),
('EMP-029', CURRENT_DATE - INTERVAL '140 days', 3300, (SELECT id_usuario FROM usuario WHERE username='rocio.navarro'), 'MA'),
('EMP-030', CURRENT_DATE - INTERVAL '120 days', 3300, (SELECT id_usuario FROM usuario WHERE username='gustavo.arce'), 'MA'),
-- Sucursal 5 (Centro): usuarios C031EM-C040EM
('EMP-031', CURRENT_DATE - INTERVAL '180 days', 4500, (SELECT id_usuario FROM usuario WHERE username='elena.maldonado'), 'MA'),
('EMP-032', CURRENT_DATE - INTERVAL '160 days', 3300, (SELECT id_usuario FROM usuario WHERE username='raul.fuentes2'), 'MA'),
('EMP-033', CURRENT_DATE - INTERVAL '140 days', 3300, (SELECT id_usuario FROM usuario WHERE username='isabel.correa'), 'MA'),
('EMP-034', CURRENT_DATE - INTERVAL '120 days', 4500, (SELECT id_usuario FROM usuario WHERE username='tomas.guzman'), 'MA'),
('EMP-035', CURRENT_DATE - INTERVAL '100 days', 3300, (SELECT id_usuario FROM usuario WHERE username='mariana.espinoza'), 'MA'),
('EMP-036', CURRENT_DATE - INTERVAL '80 days', 3300, (SELECT id_usuario FROM usuario WHERE username='ignacio.peralta'), 'MA'),
('EMP-037', CURRENT_DATE - INTERVAL '60 days', 3300, (SELECT id_usuario FROM usuario WHERE username='florencia.suarez'), 'MA'),
('EMP-038', CURRENT_DATE - INTERVAL '40 days', 3300, (SELECT id_usuario FROM usuario WHERE username='matias.gonzalez'), 'MA'),
('EMP-039', CURRENT_DATE - INTERVAL '30 days', 3300, (SELECT id_usuario FROM usuario WHERE username='alejandra.rivas'), 'MA'),
('EMP-040', CURRENT_DATE - INTERVAL '20 days', 3300, (SELECT id_usuario FROM usuario WHERE username='rodrigo.cordero'), 'MA')
) AS v(codigo_empleado, fecha_contratacion, salario, id_usuario, turno)
WHERE NOT EXISTS (SELECT 1 FROM empleado e WHERE e.codigo_empleado = v.codigo_empleado);

-- ============================================================
-- 3. PROVEEDORES - 7 nuevos
-- ============================================================

INSERT INTO proveedor (empresa, nit, nombre_contacto, telefono, correo, direccion, categoria_productos, activo)
SELECT v.* FROM (VALUES
('Distribuidora La Estrella', '456789028', 'Maria Estrella', '70123456', 'estrella@proveedor.com', 'Av. Blanco Galindo #456', 'BEBIDAS', TRUE),
('Carnes del Oriente', '567890123', 'Juan Carlos Carnes', '70234567', 'carnes@proveedor.com', 'Zona Industrial Km 4', 'ALIMENTOS', TRUE),
('Verduras Frescas SRL', '678901234', 'Ana Veronica', '70345678', 'verduras@proveedor.com', 'Mercado Central Local 42', 'ALIMENTOS', TRUE),
('Lacteos Andinos', '789012345', 'Carlos Mamani', '70456789', 'lacteos@proveedor.com', 'Av. Santa Cruz #789', 'ALIMENTOS', TRUE),
('Panaderia San Jorge', '890123456', 'Jorge Luis Perez', '70567890', 'panaderia@proveedor.com', 'Calle Junin #321', 'ALIMENTOS', TRUE),
('Heladeria Tropical', '901234567', 'Luis Fernando Rojas', '70678901', 'helados@proveedor.com', 'Av. Doble Via La Guardia', 'ALIMENTOS', TRUE),
('CleanBol', '012345678', 'Rosa Maria Quispe', '70789012', 'limpieza@cleanbol.com', 'Calle Colombia #654', 'LIMPIEZA', TRUE)
) AS v(empresa, nit, nombre_contacto, telefono, correo, direccion, categoria_productos, activo)
WHERE NOT EXISTS (SELECT 1 FROM proveedor p WHERE p.nit = v.nit);

-- ============================================================
-- 4. CLIENTES - 30 nuevos vinculados a usuarios CL001-CL030
-- ============================================================

INSERT INTO cliente (fecha_nacimiento, nit, nivel_cliente, puntos_fidelidad, razon_social, id_usuario)
SELECT v.* FROM (VALUES
('1990-03-15'::DATE, '1234567', 'BRONCE', 0, 'Raul Mamani', (SELECT id_usuario FROM usuario WHERE username='raul.mamani')),
('1985-07-22'::DATE, '2345678', 'PLATA', 150, 'Carmen Vargas', (SELECT id_usuario FROM usuario WHERE username='carmen.vargas')),
('1992-11-08'::DATE, '3456789', 'BRONCE', 0, 'Luis Herrera', (SELECT id_usuario FROM usuario WHERE username='luis.herrera')),
('1988-01-30'::DATE, '4567890', 'ORO', 500, 'Ana Torrico', (SELECT id_usuario FROM usuario WHERE username='ana.torrico')),
('1995-05-12'::DATE, '5678901', 'BRONCE', 0, 'Pedro Vaca', (SELECT id_usuario FROM usuario WHERE username='pedro.vaca')),
('1983-09-18'::DATE, '6789012', 'PLATA', 200, 'Maria Espinosa', (SELECT id_usuario FROM usuario WHERE username='maria.espinosa')),
('1991-12-25'::DATE, '7890123', 'BRONCE', 0, 'Joaquin Pereira', (SELECT id_usuario FROM usuario WHERE username='joaquin.pereira')),
('1994-04-05'::DATE, '8901234', 'PLATA', 100, 'Laura Gutierrez', (SELECT id_usuario FROM usuario WHERE username='laura.gutierrez')),
('1987-06-14'::DATE, '9012345', 'ORO', 750, 'Carlos Medina', (SELECT id_usuario FROM usuario WHERE username='carlos.medina')),
('1993-08-20'::DATE, '0123456', 'BRONCE', 0, 'Belen Choque', (SELECT id_usuario FROM usuario WHERE username='belen.choque')),
('1996-02-11'::DATE, '1112233', 'BRONCE', 0, 'Marco Apaza', (SELECT id_usuario FROM usuario WHERE username='marco.apaza')),
('1989-10-03'::DATE, '2223344', 'PLATA', 180, 'Diana Lopez', (SELECT id_usuario FROM usuario WHERE username='diana.lopez2')),
('1984-04-28'::DATE, '3334455', 'ORO', 600, 'Sergio Ramos', (SELECT id_usuario FROM usuario WHERE username='sergio.ramos')),
('1997-07-16'::DATE, '4445566', 'BRONCE', 0, 'Paola Mita', (SELECT id_usuario FROM usuario WHERE username='paola.mita')),
('1986-11-09'::DATE, '5556677', 'PLATA', 120, 'Andres Cabrera', (SELECT id_usuario FROM usuario WHERE username='andres.cabrera')),
('1990-01-24'::DATE, '6667788', 'BRONCE', 0, 'Camila Fernandez', (SELECT id_usuario FROM usuario WHERE username='camila.fernandez2')),
('1982-03-07'::DATE, '7778899', 'ORO', 800, 'Mauricio Luna', (SELECT id_usuario FROM usuario WHERE username='mauricio.luna')),
('1994-09-13'::DATE, '8889900', 'BRONCE', 0, 'Valentina Rios', (SELECT id_usuario FROM usuario WHERE username='valentina.rios')),
('1988-12-01'::DATE, '9990011', 'PLATA', 250, 'Francisco Mendez', (SELECT id_usuario FROM usuario WHERE username='francisco.mendez')),
('1991-06-19'::DATE, '1001122', 'BRONCE', 0, 'Isabella Cruz', (SELECT id_usuario FROM usuario WHERE username='isabella.cruz')),
('1985-08-08'::DATE, '2002233', 'PLATA', 160, 'Gabriel Torrez', (SELECT id_usuario FROM usuario WHERE username='gabriel.torrez')),
('1993-02-27'::DATE, '3003344', 'BRONCE', 0, 'Daniela Yupa', (SELECT id_usuario FROM usuario WHERE username='daniela.yupa')),
('1996-10-10'::DATE, '4004455', 'ORO', 550, 'Nicolas Vargas', (SELECT id_usuario FROM usuario WHERE username='nicolas.vargas2')),
('1987-05-15'::DATE, '5005566', 'PLATA', 220, 'Fernanda Salvatierra', (SELECT id_usuario FROM usuario WHERE username='fernanda.salvatierra')),
('1990-07-30'::DATE, '6006677', 'BRONCE', 0, 'Roberto Clavero', (SELECT id_usuario FROM usuario WHERE username='roberto.clavero')),
('1984-11-22'::DATE, '7007788', 'BRONCE', 0, 'Sofia Monje', (SELECT id_usuario FROM usuario WHERE username='sofia.monje')),
('1992-03-03'::DATE, '8008899', 'PLATA', 130, 'Diego Aragon', (SELECT id_usuario FROM usuario WHERE username='diego.aragon')),
('1989-09-17'::DATE, '9009900', 'BRONCE', 0, 'Jhenny Quispe', (SELECT id_usuario FROM usuario WHERE username='jhenny.quispe')),
('1995-01-06'::DATE, '1010011', 'PLATA', 190, 'Bryan Caceres', (SELECT id_usuario FROM usuario WHERE username='bryan.caceres')),
('1986-04-20'::DATE, '2011122', 'ORO', 450, 'Tatiana Estrada', (SELECT id_usuario FROM usuario WHERE username='tatiana.estrada'))
) AS v(fecha_nacimiento, nit, nivel_cliente, puntos_fidelidad, razon_social, id_usuario)
WHERE NOT EXISTS (SELECT 1 FROM cliente c WHERE c.id_usuario = v.id_usuario);

-- ============================================================
-- 5. ROL_USUARIO - Asignacion de roles
-- ============================================================

-- Roles de empleados por puesto
-- Sucursal 2
INSERT INTO rol_usuario (id_usuario, id_rol, fecha_asignacion, asignado_por, activo)
SELECT v.* FROM (VALUES
((SELECT id_usuario FROM usuario WHERE username='gabriela.lopez2'), 6, CURRENT_DATE - INTERVAL '540 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='miguel.torres2'), 6, CURRENT_DATE - INTERVAL '500 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='paola.vargas'), 6, CURRENT_DATE - INTERVAL '480 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='andres.mendoza2'), 5, CURRENT_DATE - INTERVAL '460 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='claudia.rojas'), 5, CURRENT_DATE - INTERVAL '440 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='fernando.aguilar2'), 5, CURRENT_DATE - INTERVAL '420 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='patricia.salazar'), 7, CURRENT_DATE - INTERVAL '400 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='ricardo.ortiz2'), 7, CURRENT_DATE - INTERVAL '380 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='sofia.ramirez'), 7, CURRENT_DATE - INTERVAL '360 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='diego.flores2'), 4, CURRENT_DATE - INTERVAL '340 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
-- Sucursal 3
((SELECT id_usuario FROM usuario WHERE username='valeria.castro'), 6, CURRENT_DATE - INTERVAL '420 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='jorge.martinez2'), 6, CURRENT_DATE - INTERVAL '400 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='carolina.gomez'), 6, CURRENT_DATE - INTERVAL '380 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='mauricio.fernandez'), 5, CURRENT_DATE - INTERVAL '360 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='natalia.paz'), 5, CURRENT_DATE - INTERVAL '340 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='esteban.quiroga'), 5, CURRENT_DATE - INTERVAL '320 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='lucia.delgado'), 7, CURRENT_DATE - INTERVAL '300 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='hernan.silva'), 7, CURRENT_DATE - INTERVAL '280 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='camila.morales'), 7, CURRENT_DATE - INTERVAL '260 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='sebastian.reyes'), 4, CURRENT_DATE - INTERVAL '240 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
-- Sucursal 4
((SELECT id_usuario FROM usuario WHERE username='andrea.garcia2'), 6, CURRENT_DATE - INTERVAL '300 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='marcelo.pinto'), 6, CURRENT_DATE - INTERVAL '280 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='veronica.santos'), 6, CURRENT_DATE - INTERVAL '260 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='cristian.rivera'), 5, CURRENT_DATE - INTERVAL '240 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='daniela.pena2'), 5, CURRENT_DATE - INTERVAL '220 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='oscar.villalba'), 5, CURRENT_DATE - INTERVAL '200 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='monica.serrano'), 7, CURRENT_DATE - INTERVAL '180 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='alvaro.campos'), 7, CURRENT_DATE - INTERVAL '160 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='rocio.navarro'), 7, CURRENT_DATE - INTERVAL '140 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='gustavo.arce'), 4, CURRENT_DATE - INTERVAL '120 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
-- Sucursal 5
((SELECT id_usuario FROM usuario WHERE username='elena.maldonado'), 6, CURRENT_DATE - INTERVAL '180 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='raul.fuentes2'), 6, CURRENT_DATE - INTERVAL '160 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='isabel.correa'), 6, CURRENT_DATE - INTERVAL '140 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='tomas.guzman'), 5, CURRENT_DATE - INTERVAL '120 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='mariana.espinoza'), 5, CURRENT_DATE - INTERVAL '100 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='ignacio.peralta'), 5, CURRENT_DATE - INTERVAL '80 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='florencia.suarez'), 7, CURRENT_DATE - INTERVAL '60 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='matias.gonzalez'), 7, CURRENT_DATE - INTERVAL '40 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='alejandra.rivas'), 7, CURRENT_DATE - INTERVAL '30 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='rodrigo.cordero'), 4, CURRENT_DATE - INTERVAL '20 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE)
) AS v(id_usuario, id_rol, fecha_asignacion, asignado_por, activo)
WHERE NOT EXISTS (SELECT 1 FROM rol_usuario ru WHERE ru.id_usuario = v.id_usuario AND ru.id_rol = v.id_rol);

-- Roles de clientes
INSERT INTO rol_usuario (id_usuario, id_rol, fecha_asignacion, asignado_por, activo)
SELECT (SELECT id_usuario FROM usuario WHERE username=v.username), 10, CURRENT_DATE - INTERVAL '30 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE
FROM (VALUES
('raul.mamani'),('carmen.vargas'),('luis.herrera'),('ana.torrico'),('pedro.vaca'),
('maria.espinosa'),('joaquin.pereira'),('laura.gutierrez'),('carlos.medina'),('belen.choque'),
('marco.apaza'),('diana.lopez2'),('sergio.ramos'),('paola.mita'),('andres.cabrera'),
('camila.fernandez2'),('mauricio.luna'),('valentina.rios'),('francisco.mendez'),('isabella.cruz'),
('gabriel.torrez'),('daniela.yupa'),('nicolas.vargas2'),('fernanda.salvatierra'),('roberto.clavero'),
('sofia.monje'),('diego.aragon'),('jhenny.quispe'),('bryan.caceres'),('tatiana.estrada')
) AS v(username)
WHERE NOT EXISTS (SELECT 1 FROM rol_usuario ru WHERE ru.id_usuario = (SELECT id_usuario FROM usuario WHERE username=v.username) AND ru.id_rol = 10);

-- Algunos empleados tambien REPARTIDOR
INSERT INTO rol_usuario (id_usuario, id_rol, fecha_asignacion, asignado_por, activo)
SELECT v.* FROM (VALUES
((SELECT id_usuario FROM usuario WHERE username='patricia.salazar'), 11, CURRENT_DATE - INTERVAL '400 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='ricardo.ortiz2'), 11, CURRENT_DATE - INTERVAL '380 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='lucia.delgado'), 11, CURRENT_DATE - INTERVAL '300 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='hernan.silva'), 11, CURRENT_DATE - INTERVAL '280 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='monica.serrano'), 11, CURRENT_DATE - INTERVAL '180 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='alvaro.campos'), 11, CURRENT_DATE - INTERVAL '160 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='florencia.suarez'), 11, CURRENT_DATE - INTERVAL '60 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE),
((SELECT id_usuario FROM usuario WHERE username='matias.gonzalez'), 11, CURRENT_DATE - INTERVAL '40 days', (SELECT id_usuario FROM usuario WHERE username='admin'), TRUE)
) AS v(id_usuario, id_rol, fecha_asignacion, asignado_por, activo)
WHERE NOT EXISTS (SELECT 1 FROM rol_usuario ru WHERE ru.id_usuario = v.id_usuario AND ru.id_rol = 11);

-- ============================================================
-- 6. CATEGORIAS - 8 nuevas
-- ============================================================

INSERT INTO categoria (nombre, descripcion, activo, nivel, id_categoria_padre)
SELECT v.* FROM (VALUES
('POSTRES', 'Postres y dulces del restaurante', TRUE, 1, NULL::bigint),
('Bebidas Calientes', 'Cafes, tes y chocolates calientes', TRUE, 1, NULL::bigint),
('Snacks', 'Aperitivos y botanas ligeras', TRUE, 1, NULL::bigint),
('Combos', 'Combos especiales del restaurante', TRUE, 1, NULL::bigint),
('Pizzas', 'Pizzas artesanales al horno', TRUE, 1, NULL::bigint),
('Pastas', 'Pastas italianas y nacionales', TRUE, 1, NULL::bigint),
('Ensaladas Premium', 'Ensaladas gourmet y premium', TRUE, 1, NULL::bigint),
('Postres Frios', 'Helados, sorbetes y postres frios', TRUE, 1, NULL::bigint)
) AS v(nombre, descripcion, activo, nivel, id_categoria_padre)
WHERE NOT EXISTS (SELECT 1 FROM categoria c WHERE c.nombre = v.nombre);

-- ============================================================
-- 7. PRODUCTO_FINAL - 41 nuevos (49 total)
-- ============================================================

INSERT INTO producto_final (activo, codigo, descripcion, fecha_creacion, nombre, tiempo_preparacion, id_categoria)
SELECT v.activo, v.codigo, v.descripcion, v.fecha_creacion, v.nombre, v.tiempo_preparacion, c.id_categoria
FROM (VALUES
-- ENTRADAS
(TRUE, 'ENT-001', 'Nachos con queso cheddar, frijol y jalapeño', CURRENT_DATE - INTERVAL '300 days', 'Nachos Supreme', 15, 'Entradas'),
(TRUE, 'ENT-002', 'Alitas de pollo bañadas en salsa BBQ', CURRENT_DATE - INTERVAL '300 days', 'Alitas BBQ', 20, 'Entradas'),
(TRUE, 'ENT-003', 'Tequeños de queso mozarella crujientes', CURRENT_DATE - INTERVAL '300 days', 'Tequenos', 12, 'Entradas'),
(TRUE, 'ENT-004', 'Aros de calamar rebozados y fritos', CURRENT_DATE - INTERVAL '300 days', 'Calamares', 18, 'Entradas'),
-- ENSALADAS
(TRUE, 'ENS-001', 'Ensalada Cesar con pollo y aderezo especial', CURRENT_DATE - INTERVAL '280 days', 'Ensalada Cesar', 10, 'Ensaladas'),
(TRUE, 'ENS-002', 'Ensalada mixta con lechuga, tomate y cebolla', CURRENT_DATE - INTERVAL '280 days', 'Ensalada Mixta', 8, 'Ensaladas'),
(TRUE, 'ENS-003', 'Ensalada tropical con mango y aguacate', CURRENT_DATE - INTERVAL '280 days', 'Ensalada Tropical', 10, 'Ensaladas'),
-- HAMBURGUESAS
(TRUE, 'HAM-001', 'Hamburguesa clasica con lechuga y tomate', CURRENT_DATE - INTERVAL '260 days', 'Hamburguesa Clasica', 15, 'Hamburguesas'),
(TRUE, 'HAM-002', 'Hamburguesa vegana con champiñón y pimiento', CURRENT_DATE - INTERVAL '260 days', 'Hamburguesa Vegana', 18, 'Hamburguesas'),
(TRUE, 'HAM-003', 'Hamburguesa con tocino crocante y cheddar', CURRENT_DATE - INTERVAL '260 days', 'Hamburguesa Bacon', 16, 'Hamburguesas'),
-- CARNES
(TRUE, 'CAR-001', 'Lomo al trapo con sal gruesa y chimichurri', CURRENT_DATE - INTERVAL '250 days', 'Lomo al Trapo', 35, 'Carnes'),
(TRUE, 'CAR-002', 'Parrillada para dos con chorizo y pollo', CURRENT_DATE - INTERVAL '250 days', 'Parrillada', 40, 'Carnes'),
(TRUE, 'CAR-003', 'Bife de chorizo con papas fritas', CURRENT_DATE - INTERVAL '250 days', 'Bife de Chorizo', 25, 'Carnes'),
(TRUE, 'CAR-004', 'Costillas de cerdo BBQ al horno', CURRENT_DATE - INTERVAL '250 days', 'Costillas BBQ', 45, 'Carnes'),
-- PIZZAS
(TRUE, 'PIZ-001', 'Pizza margarita con albahaca fresca', CURRENT_DATE - INTERVAL '240 days', 'Pizza Margarita', 20, 'Pizzas'),
(TRUE, 'PIZ-002', 'Pizza con pepperoni artesanal', CURRENT_DATE - INTERVAL '240 days', 'Pizza Pepperoni', 20, 'Pizzas'),
(TRUE, 'PIZ-003', 'Pizza de cuatro quesos italianos', CURRENT_DATE - INTERVAL '240 days', 'Pizza Cuatro Quesos', 22, 'Pizzas'),
(TRUE, 'PIZ-004', 'Pizza hawaiana con jamon y pina', CURRENT_DATE - INTERVAL '240 days', 'Pizza Hawaiana', 20, 'Pizzas'),
-- PASTAS
(TRUE, 'PAS-001', 'Spaghetti a la bolognesa casera', CURRENT_DATE - INTERVAL '230 days', 'Spaghetti Bolognesa', 25, 'Pastas'),
(TRUE, 'PAS-002', 'Penne a la alfredo con nata', CURRENT_DATE - INTERVAL '230 days', 'Penne Alfredo', 20, 'Pastas'),
(TRUE, 'PAS-003', 'Lasagna de carne con bechamel', CURRENT_DATE - INTERVAL '230 days', 'Lasagna', 30, 'Pastas'),
-- POSTRES
(TRUE, 'POS-001', 'Tiramisu italiano clasico', CURRENT_DATE - INTERVAL '220 days', 'Tiramisu', 15, 'POSTRES'),
(TRUE, 'POS-002', 'Flan casero con caramelo', CURRENT_DATE - INTERVAL '220 days', 'Flan Casero', 10, 'POSTRES'),
(TRUE, 'POS-003', 'Brownie caliente con helado de vainilla', CURRENT_DATE - INTERVAL '220 days', 'Brownie con Helado', 12, 'POSTRES'),
(TRUE, 'POS-004', 'Pastel tres leches con merengue', CURRENT_DATE - INTERVAL '220 days', 'Tres Leches', 15, 'POSTRES'),
-- POSTRES FRIOS
(TRUE, 'PF-001', 'Helado artesanal de nieve', CURRENT_DATE - INTERVAL '210 days', 'Helado Nieve', 5, 'Postres Frios'),
(TRUE, 'PF-002', 'Sundae de chocolate con caramelo', CURRENT_DATE - INTERVAL '210 days', 'Sundae', 8, 'Postres Frios'),
(TRUE, 'PF-003', 'Parfait de yogur con granola y fruta', CURRENT_DATE - INTERVAL '210 days', 'Parfait', 7, 'Postres Frios'),
-- BEBIDAS CALIENTES
(TRUE, 'BC-001', 'Cafe americano recien molido', CURRENT_DATE - INTERVAL '200 days', 'Cafe Americano', 5, 'Bebidas Calientes'),
(TRUE, 'BC-002', 'Cafe con leche espumosa', CURRENT_DATE - INTERVAL '200 days', 'Cafe con Leche', 5, 'Bebidas Calientes'),
(TRUE, 'BC-003', 'Chocolate caliente con chantilly', CURRENT_DATE - INTERVAL '200 days', 'Chocolate Caliente', 7, 'Bebidas Calientes'),
(TRUE, 'BC-004', 'Te verde organico japonés', CURRENT_DATE - INTERVAL '200 days', 'Te Verde', 5, 'Bebidas Calientes'),
-- BEBIDAS ALCOHOLICAS
(TRUE, 'BA-001', 'Cerveza artesanal Cristal 330ml', CURRENT_DATE - INTERVAL '190 days', 'Cerveza Cristal', 2, 'Bebidas Alcoholicas'),
(TRUE, 'BA-002', 'Cerveza nacional Paceña 330ml', CURRENT_DATE - INTERVAL '190 days', 'Cerveza Paceña', 2, 'Bebidas Alcoholicas'),
(TRUE, 'BA-003', 'Fernet con cola en vaso largo', CURRENT_DATE - INTERVAL '190 days', 'Fernet con Cola', 3, 'Bebidas Alcoholicas'),
(TRUE, 'BA-004', 'Pisco sour clasico peruano', CURRENT_DATE - INTERVAL '190 days', 'Pisco Sour', 5, 'Bebidas Alcoholicas'),
-- BEBIDAS SIN ALCOHOL
(TRUE, 'BS-001', 'Jugo natural de naranja recien exprimido', CURRENT_DATE - INTERVAL '180 days', 'Jugo de Naranja', 3, 'Bebidas sin Alcohol'),
(TRUE, 'BS-002', 'Limonada natural con hierbabuena', CURRENT_DATE - INTERVAL '180 days', 'Limonada', 3, 'Bebidas sin Alcohol'),
(TRUE, 'BS-003', 'Sprite 350ml en lata', CURRENT_DATE - INTERVAL '180 days', 'Sprite', 1, 'Bebidas sin Alcohol'),
-- SNACKS
(TRUE, 'SNK-001', 'Papas fritas crocantes con sal', CURRENT_DATE - INTERVAL '170 days', 'Papas Fritas', 10, 'Snacks'),
(TRUE, 'SNK-002', 'Aros de cebolla rebozados', CURRENT_DATE - INTERVAL '170 days', 'Onion Rings', 12, 'Snacks'),
-- COMBOS
(TRUE, 'COM-001', 'Combo familiar: 2 hamburguesas + papas + 4 bebidas', CURRENT_DATE - INTERVAL '160 days', 'Combo Familiar', 20, 'Combos'),
(TRUE, 'COM-002', 'Combo pareja: 2 entradas + 2 bebidas + 1 postre', CURRENT_DATE - INTERVAL '160 days', 'Combo Pareja', 15, 'Combos'),
(TRUE, 'COM-003', 'Combo individual: hamburguesa + papas + bebida', CURRENT_DATE - INTERVAL '160 days', 'Combo Individual', 15, 'Combos'),
(TRUE, 'COM-004', 'Combo kids: mini hamburguesa + papas + jugo', CURRENT_DATE - INTERVAL '160 days', 'Combo Kids', 12, 'Combos'),
-- ENSALADAS PREMIUM
(TRUE, 'EP-001', 'Ensalada de quinoa con vegetales premium', CURRENT_DATE - INTERVAL '150 days', 'Ensalada Quinoa', 12, 'Ensaladas Premium'),
(TRUE, 'EP-002', 'Caesar premium con langostinos', CURRENT_DATE - INTERVAL '150 days', 'Caesar Premium', 15, 'Ensaladas Premium'),
-- BEBIDAS
(TRUE, 'BEB-001', 'Agua mineral sin gas 500ml', CURRENT_DATE - INTERVAL '140 days', 'Agua Mineral', 1, 'Bebidas'),
(TRUE, 'BEB-002', 'Agua con gas 500ml', CURRENT_DATE - INTERVAL '140 days', 'Agua con Gas', 1, 'Bebidas')
) AS v(activo, codigo, descripcion, fecha_creacion, nombre, tiempo_preparacion, cat_nombre)
JOIN categoria c ON c.nombre = v.cat_nombre
WHERE NOT EXISTS (SELECT 1 FROM producto_final pf WHERE pf.codigo = v.codigo);

-- ============================================================
-- 8. INVENTARIO - 49 nuevos (52 total)
-- ============================================================

INSERT INTO inventario (activo, codigo, descripcion, es_rehutilizable, fecha_actualizacion, fecha_creacion, marca, nombre, unidad_medida)
SELECT v.* FROM (VALUES
-- VEGETALES
(TRUE, 'INV-004', 'Tomate rojo fresco', FALSE, CURRENT_DATE, CURRENT_DATE, 'Fresco', 'Tomate', 'KG'),
(TRUE, 'INV-005', 'Cebolla blanca entera', FALSE, CURRENT_DATE, CURRENT_DATE, 'Fresco', 'Cebolla', 'KG'),
(TRUE, 'INV-006', 'Lechuga iceberg fresca', FALSE, CURRENT_DATE, CURRENT_DATE, 'Fresco', 'Lechuga', 'KG'),
(TRUE, 'INV-007', 'Zanahoria baby', FALSE, CURRENT_DATE, CURRENT_DATE, 'Fresco', 'Zanahoria', 'KG'),
(TRUE, 'INV-008', 'Papa larga para fritar', FALSE, CURRENT_DATE, CURRENT_DATE, 'Fresco', 'Papa Larga', 'KG'),
(TRUE, 'INV-009', 'Choclo dulce desgranado', FALSE, CURRENT_DATE, CURRENT_DATE, 'Fresco', 'Choclo', 'KG'),
(TRUE, 'INV-010', 'Aji picante rojo', FALSE, CURRENT_DATE, CURRENT_DATE, 'Fresco', 'Aji', 'KG'),
(TRUE, 'INV-011', 'Pimiento rojo y verde', FALSE, CURRENT_DATE, CURRENT_DATE, 'Fresco', 'Pimiento', 'KG'),
(TRUE, 'INV-012', 'Ajo fresco por cabeza', FALSE, CURRENT_DATE, CURRENT_DATE, 'Fresco', 'Ajo', 'KG'),
(TRUE, 'INV-013', 'Champiñon blanco entero', FALSE, CURRENT_DATE, CURRENT_DATE, 'Fresco', 'Champiñón', 'KG'),
(TRUE, 'INV-014', 'Mango maduro para ensalada', FALSE, CURRENT_DATE, CURRENT_DATE, 'Fresco', 'Mango', 'KG'),
(TRUE, 'INV-015', 'Aguacate fresco Hass', FALSE, CURRENT_DATE, CURRENT_DATE, 'Fresco', 'Aguacate', 'KG'),
-- CARNES
(TRUE, 'INV-016', 'Pollo entero para asar', FALSE, CURRENT_DATE, CURRENT_DATE, 'Oriente', 'Pollo Entero', 'KG'),
(TRUE, 'INV-017', 'Pechuga de pollo sin hueso', FALSE, CURRENT_DATE, CURRENT_DATE, 'Oriente', 'Pechuga de Pollo', 'KG'),
(TRUE, 'INV-018', 'Carne molida 80/20', FALSE, CURRENT_DATE, CURRENT_DATE, 'Oriente', 'Carne Molida', 'KG'),
(TRUE, 'INV-019', 'Lomo fino premium', FALSE, CURRENT_DATE, CURRENT_DATE, 'Oriente', 'Lomo Fino', 'KG'),
(TRUE, 'INV-020', 'Costillas de cerdo frescas', FALSE, CURRENT_DATE, CURRENT_DATE, 'Oriente', 'Costillas de Cerdo', 'KG'),
(TRUE, 'INV-021', 'Chorizo artesanal criollo', FALSE, CURRENT_DATE, CURRENT_DATE, 'Oriente', 'Chorizo', 'KG'),
(TRUE, 'INV-022', 'Tocino ahumado en tiras', FALSE, CURRENT_DATE, CURRENT_DATE, 'Oriente', 'Tocino', 'KG'),
(TRUE, 'INV-023', 'Salchicha viena para parrilla', FALSE, CURRENT_DATE, CURRENT_DATE, 'Oriente', 'Salchicha', 'KG'),
(TRUE, 'INV-024', 'Calamar limpio para freir', FALSE, CURRENT_DATE, CURRENT_DATE, 'Oriente', 'Calamar', 'KG'),
-- LACTEOS
(TRUE, 'INV-025', 'Queso mozarella rallado', FALSE, CURRENT_DATE, CURRENT_DATE, 'Andinos', 'Queso Mozarella', 'KG'),
(TRUE, 'INV-026', 'Queso cheddar en lonchas', FALSE, CURRENT_DATE, CURRENT_DATE, 'Andinos', 'Queso Cheddar', 'KG'),
(TRUE, 'INV-027', 'Queso cremaPhiladelphia', FALSE, CURRENT_DATE, CURRENT_DATE, 'Andinos', 'Queso Crema', 'GRAMO'),
(TRUE, 'INV-028', 'Leche entera larga vida', FALSE, CURRENT_DATE, CURRENT_DATE, 'Andinos', 'Leche Entera', 'LITRO'),
(TRUE, 'INV-029', 'Mantequilla sin sal', FALSE, CURRENT_DATE, CURRENT_DATE, 'Andinos', 'Mantequilla', 'GRAMO'),
(TRUE, 'INV-030', 'Nata para cocinar', FALSE, CURRENT_DATE, CURRENT_DATE, 'Andinos', 'Nata', 'ML'),
(TRUE, 'INV-031', 'Yogur natural sin azucar', FALSE, CURRENT_DATE, CURRENT_DATE, 'Andinos', 'Yogur', 'LITRO'),
-- PANADERIA
(TRUE, 'INV-032', 'Pan para hamburguesa artesanal', FALSE, CURRENT_DATE, CURRENT_DATE, 'San Jorge', 'Pan Hamburguesa', 'UNIDAD'),
(TRUE, 'INV-033', 'Pan pita integral', FALSE, CURRENT_DATE, CURRENT_DATE, 'San Jorge', 'Pan Pita', 'UNIDAD'),
(TRUE, 'INV-034', 'Pan tostado para ensaladas', FALSE, CURRENT_DATE, CURRENT_DATE, 'San Jorge', 'Pan Tostado', 'UNIDAD'),
(TRUE, 'INV-035', 'Harina de trigo premium', FALSE, CURRENT_DATE, CURRENT_DATE, 'San Jorge', 'Harina', 'KG'),
(TRUE, 'INV-036', 'Masa para pizza preparada', FALSE, CURRENT_DATE, CURRENT_DATE, 'San Jorge', 'Masa Pizza', 'KG'),
(TRUE, 'INV-037', 'Galleta soletilla para tiramisu', FALSE, CURRENT_DATE, CURRENT_DATE, 'San Jorge', 'Galleta Soletilla', 'KG'),
(TRUE, 'INV-038', 'Masa para lasagna', FALSE, CURRENT_DATE, CURRENT_DATE, 'San Jorge', 'Masa Lasagna', 'KG'),
-- LICORES
(TRUE, 'INV-039', 'Ron dorado 3 años', FALSE, CURRENT_DATE, CURRENT_DATE, 'Licorera', 'Ron', 'ML'),
(TRUE, 'INV-040', 'Vodka importado premium', FALSE, CURRENT_DATE, CURRENT_DATE, 'Licorera', 'Vodka', 'ML'),
(TRUE, 'INV-041', 'Pisco peruano quebranta', FALSE, CURRENT_DATE, CURRENT_DATE, 'Licorera', 'Pisco', 'ML'),
(TRUE, 'INV-042', 'Tequila blanco 100% agave', FALSE, CURRENT_DATE, CURRENT_DATE, 'Licorera', 'Tequila', 'ML'),
(TRUE, 'INV-043', 'Fernet Branca italiano', FALSE, CURRENT_DATE, CURRENT_DATE, 'Licorera', 'Fernet', 'ML'),
(TRUE, 'INV-044', 'Vermut rojo italiano', FALSE, CURRENT_DATE, CURRENT_DATE, 'Licorera', 'Vermut', 'ML'),
-- SALSAS
(TRUE, 'INV-045', 'Ketchup Heinz 1L', FALSE, CURRENT_DATE, CURRENT_DATE, 'Heinz', 'Ketchup', 'LITRO'),
(TRUE, 'INV-046', 'Mostaza amarilla preparada', FALSE, CURRENT_DATE, CURRENT_DATE, 'Heinz', 'Mostaza', 'LITRO'),
(TRUE, 'INV-047', 'Mayonesa casera', FALSE, CURRENT_DATE, CURRENT_DATE, 'La Abuela', 'Mayonesa', 'LITRO'),
(TRUE, 'INV-048', 'Salsa BBQ ahumada', FALSE, CURRENT_DATE, CURRENT_DATE, 'SalsaKing', 'Salsa BBQ', 'ML'),
(TRUE, 'INV-049', 'Salsa de soya japonesa', FALSE, CURRENT_DATE, CURRENT_DATE, 'Kikkoman', 'Salsa de Soya', 'ML'),
(TRUE, 'INV-050', 'Salsa inglesa Lea Perrins', FALSE, CURRENT_DATE, CURRENT_DATE, 'Lea Perrins', 'Salsa Inglesa', 'ML'),
(TRUE, 'INV-051', 'Tabasco picante original', FALSE, CURRENT_DATE, CURRENT_DATE, 'Tabasco', 'Tabasco', 'ML'),
(TRUE, 'INV-052', 'Caramelo liquido para postres', FALSE, CURRENT_DATE, CURRENT_DATE, 'DulceVida', 'Caramelo', 'ML'),
(TRUE, 'INV-053', 'Salsa de chocolate para postres', FALSE, CURRENT_DATE, CURRENT_DATE, 'DulceVida', 'SChocolate', 'ML'),
-- BEBIDAS
(TRUE, 'INV-054', 'CocaCola 3 litros', FALSE, CURRENT_DATE, CURRENT_DATE, 'CocaCola', 'CocaCola 3L', 'UNIDAD'),
(TRUE, 'INV-055', 'Sprite 2 litros', FALSE, CURRENT_DATE, CURRENT_DATE, 'CocaCola', 'Sprite 2L', 'UNIDAD'),
(TRUE, 'INV-056', 'Agua mineral 500ml botella', FALSE, CURRENT_DATE, CURRENT_DATE, 'Cristal', 'Agua Mineral 500', 'UNIDAD'),
(TRUE, 'INV-057', 'Jugo del Valle frutas tropicales', FALSE, CURRENT_DATE, CURRENT_DATE, 'DelValle', 'Jugo DelValle', 'LITRO'),
(TRUE, 'INV-058', 'Hielo en bolsa 5kg', FALSE, CURRENT_DATE, CURRENT_DATE, 'HieloBol', 'Hielo', 'KG'),
(TRUE, 'INV-059', 'Cerveza Cristal 330ml lata', FALSE, CURRENT_DATE, CURRENT_DATE, 'Cristal', 'Cerveza Cristal', 'UNIDAD'),
(TRUE, 'INV-060', 'Cerveza Paceña 330ml lata', FALSE, CURRENT_DATE, CURRENT_DATE, 'Paceña', 'Cerveza Paceña', 'UNIDAD'),
(TRUE, 'INV-061', 'Naranja para exprimir', FALSE, CURRENT_DATE, CURRENT_DATE, 'Fresco', 'Naranja', 'KG'),
(TRUE, 'INV-062', 'Limon fresco', FALSE, CURRENT_DATE, CURRENT_DATE, 'Fresco', 'Limon', 'KG'),
(TRUE, 'INV-063', 'Cafe en grano arabe', FALSE, CURRENT_DATE, CURRENT_DATE, 'Colcafe', 'Cafe en Grano', 'KG'),
(TRUE, 'INV-064', 'Te verde japonés', FALSE, CURRENT_DATE, CURRENT_DATE, 'TeCultivo', 'Te Verde', 'KG'),
(TRUE, 'INV-065', 'Chocolate amargo para fundir', FALSE, CURRENT_DATE, CURRENT_DATE, 'DulceVida', 'Chocolate', 'KG'),
(TRUE, 'INV-066', 'Hierbabuena fresca', FALSE, CURRENT_DATE, CURRENT_DATE, 'Fresco', 'Hierbabuena', 'KG'),
(TRUE, 'INV-067', 'Vainilla extracto natural', FALSE, CURRENT_DATE, CURRENT_DATE, 'VainillaReal', 'Vainilla', 'ML'),
(TRUE, 'INV-068', 'Cacao en polvo sin azucar', FALSE, CURRENT_DATE, CURRENT_DATE, 'DulceVida', 'Cacao', 'KG'),
(TRUE, 'INV-069', 'Albahaca fresca', FALSE, CURRENT_DATE, CURRENT_DATE, 'Fresco', 'Albahaca', 'KG'),
(TRUE, 'INV-070', 'Pina en almibar trozos', FALSE, CURRENT_DATE, CURRENT_DATE, 'DulceVida', 'Pina', 'KG'),
(TRUE, 'INV-071', 'Huevo fresco bandeja 30u', FALSE, CURRENT_DATE, CURRENT_DATE, 'Granja', 'Huevo', 'UNIDAD'),
(TRUE, 'INV-072', 'Sal fina de mar', FALSE, CURRENT_DATE, CURRENT_DATE, 'SalMar', 'Sal', 'KG'),
(TRUE, 'INV-073', 'Pimienta negra molida', FALSE, CURRENT_DATE, CURRENT_DATE, 'Especias', 'Pimienta', 'KG'),
(TRUE, 'INV-074', 'Comino molido', FALSE, CURRENT_DATE, CURRENT_DATE, 'Especias', 'Comino', 'KG'),
(TRUE, 'INV-075', 'Aceite vegetal para freir', FALSE, CURRENT_DATE, CURRENT_DATE, 'PureVida', 'Aceite', 'LITRO'),
(TRUE, 'INV-076', 'Vinagre blanco', FALSE, CURRENT_DATE, CURRENT_DATE, 'PureVida', 'Vinagre', 'LITRO'),
-- LIMPIEZA
(TRUE, 'INV-077', 'Detergente concentrado para platos', FALSE, CURRENT_DATE, CURRENT_DATE, 'CleanBol', 'Detergente', 'LITRO'),
(TRUE, 'INV-078', 'Desinfectante multiusos', FALSE, CURRENT_DATE, CURRENT_DATE, 'CleanBol', 'Desinfectante', 'LITRO')
) AS v(activo, codigo, descripcion, es_rehutilizable, fecha_actualizacion, fecha_creacion, marca, nombre, unidad_medida)
WHERE NOT EXISTS (SELECT 1 FROM inventario i WHERE i.codigo = v.codigo);

-- ============================================================
-- 9. RECETAS - 1 receta por producto_final
-- ============================================================

INSERT INTO receta (activo, costo_total, descripcion, fecha_creacion, instrucciones, nombre, tiempo_preparacion, id_producto_final)
SELECT v.* FROM (VALUES
(TRUE, 18.00, 'Receta Nachos Supreme', CURRENT_DATE, 'Mezclar nachos con queso cheddar fundido, frijol refrito y jalapeño picado.', 'Receta Nachos Supreme', 15, (SELECT id_producto_final FROM producto_final WHERE codigo='ENT-001')),
(TRUE, 22.00, 'Receta Alitas BBQ', CURRENT_DATE, 'Freir alitas de pollo y bañar en salsa BBQ ahumada con ajo.', 'Receta Alitas BBQ', 20, (SELECT id_producto_final FROM producto_final WHERE codigo='ENT-002')),
(TRUE, 14.00, 'Receta Tequenos', CURRENT_DATE, 'Preparar masa, rellenar con queso mozarella y freir hasta dorar.', 'Receta Tequenos', 12, (SELECT id_producto_final FROM producto_final WHERE codigo='ENT-003')),
(TRUE, 25.00, 'Receta Calamares', CURRENT_DATE, 'Rebozar anillos de calamar con harina y freir en aceite caliente.', 'Receta Calamares', 18, (SELECT id_producto_final FROM producto_final WHERE codigo='ENT-004')),
(TRUE, 16.00, 'Receta Ensalada Cesar', CURRENT_DATE, 'Mezclar lechuga romana con pollo a la plancha, crutones y aderezo cesar.', 'Receta Ensalada Cesar', 10, (SELECT id_producto_final FROM producto_final WHERE codigo='ENS-001')),
(TRUE, 12.00, 'Receta Ensalada Mixta', CURRENT_DATE, 'Cortar lechuga, tomate y cebolla en juliana, aliñar con aceite y limon.', 'Receta Ensalada Mixta', 8, (SELECT id_producto_final FROM producto_final WHERE codigo='ENS-002')),
(TRUE, 18.00, 'Receta Ensalada Tropical', CURRENT_DATE, 'Cortar mango y aguacate en cubos, mezclar con lechuga y aderezo tropical.', 'Receta Ensalada Tropical', 10, (SELECT id_producto_final FROM producto_final WHERE codigo='ENS-003')),
(TRUE, 20.00, 'Receta Hamburguesa Clasica', CURRENT_DATE, 'Cocinar carne en plancha, montar en pan con lechuga, tomate y queso.', 'Receta Hamburguesa Clasica', 15, (SELECT id_producto_final FROM producto_final WHERE codigo='HAM-001')),
(TRUE, 18.00, 'Receta Hamburguesa Vegana', CURRENT_DATE, 'Preparar pattie de champiñón y pimiento, cocinar y servir en pan integral.', 'Receta Hamburguesa Vegana', 18, (SELECT id_producto_final FROM producto_final WHERE codigo='HAM-002')),
(TRUE, 22.00, 'Receta Hamburguesa Bacon', CURRENT_DATE, 'Cocinar carne con tocino crocante y queso cheddar derretido en pan artesanal.', 'Receta Hamburguesa Bacon', 16, (SELECT id_producto_final FROM producto_final WHERE codigo='HAM-003')),
(TRUE, 45.00, 'Receta Lomo al Trapo', CURRENT_DATE, 'Envolver lomo fino en sal gruesa y cocinar al fuego directo, servir con chimichurri.', 'Receta Lomo al Trapo', 35, (SELECT id_producto_final FROM producto_final WHERE codigo='CAR-001')),
(TRUE, 65.00, 'Receta Parrillada', CURRENT_DATE, 'Asar chorizo, pollo y lomo en parrilla con leña, servir con guarniciones.', 'Receta Parrillada', 40, (SELECT id_producto_final FROM producto_final WHERE codigo='CAR-002')),
(TRUE, 35.00, 'Receta Bife de Chorizo', CURRENT_DATE, 'Cortar bife grueso, sellar en plancha al punto deseado, servir con papas fritas.', 'Receta Bife de Chorizo', 25, (SELECT id_producto_final FROM producto_final WHERE codigo='CAR-003')),
(TRUE, 42.00, 'Receta Costillas BBQ', CURRENT_DATE, 'Cocinar costillas en horno lento 3 horas, bañar en salsa BBQ y gratinar.', 'Receta Costillas BBQ', 45, (SELECT id_producto_final FROM producto_final WHERE codigo='CAR-004')),
(TRUE, 22.00, 'Receta Pizza Margarita', CURRENT_DATE, 'Estirar masa, cubrir con salsa de tomate, mozzarella y albahaca fresca, hornear.', 'Receta Pizza Margarita', 20, (SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-001')),
(TRUE, 24.00, 'Receta Pizza Pepperoni', CURRENT_DATE, 'Estirar masa con salsa de tomate, mozzarella y pepperoni artesanal, hornear.', 'Receta Pizza Pepperoni', 20, (SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-002')),
(TRUE, 28.00, 'Receta Pizza Cuatro Quesos', CURRENT_DATE, 'Estirar masa y cubrir con mozzarella, cheddar, parmesano y queso crema, hornear.', 'Receta Pizza Cuatro Quesos', 22, (SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-003')),
(TRUE, 23.00, 'Receta Pizza Hawaiana', CURRENT_DATE, 'Estirar masa con salsa de tomate, mozzarella, jamón y piña en almibar, hornear.', 'Receta Pizza Hawaiana', 20, (SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-004')),
(TRUE, 20.00, 'Receta Spaghetti Bolognesa', CURRENT_DATE, 'Cocinar pasta al dente, preparar salsa bolognesa con carne molida y tomate.', 'Receta Spaghetti Bolognesa', 25, (SELECT id_producto_final FROM producto_final WHERE codigo='PAS-001')),
(TRUE, 18.00, 'Receta Penne Alfredo', CURRENT_DATE, 'Cocinar penne y preparar salsa alfredo con nata, queso crema y ajo.', 'Receta Penne Alfredo', 20, (SELECT id_producto_final FROM producto_final WHERE codigo='PAS-002')),
(TRUE, 25.00, 'Receta Lasagna', CURRENT_DATE, 'Capas de masa, ragu de carne, bechamel y queso, hornear hasta dorar.', 'Receta Lasagna', 30, (SELECT id_producto_final FROM producto_final WHERE codigo='PAS-003')),
(TRUE, 14.00, 'Receta Tiramisu', CURRENT_DATE, 'Remojar galletas soletilla en café, capas con crema de queso mascarpone y cacao.', 'Receta Tiramisu', 15, (SELECT id_producto_final FROM producto_final WHERE codigo='POS-001')),
(TRUE, 8.00, 'Receta Flan Casero', CURRENT_DATE, 'Batir huevos con leche y vainilla, cocinar al baño maría con caramelo.', 'Receta Flan Casero', 10, (SELECT id_producto_final FROM producto_final WHERE codigo='POS-002')),
(TRUE, 12.00, 'Receta Brownie con Helado', CURRENT_DATE, 'Hornear brownie de chocolate intenso, servir caliente con helado de vainilla.', 'Receta Brownie con Helado', 12, (SELECT id_producto_final FROM producto_final WHERE codigo='POS-003')),
(TRUE, 10.00, 'Receta Tres Leches', CURRENT_DATE, 'Hornear bizcocho esponjoso, bañar en mezcla de tres leches y cubrir con merengue.', 'Receta Tres Leches', 15, (SELECT id_producto_final FROM producto_final WHERE codigo='POS-004')),
(TRUE, 8.00, 'Receta Helado Nieve', CURRENT_DATE, 'Preparar base de leche con vainilla natural y congelar en máquina de helados.', 'Receta Helado Nieve', 5, (SELECT id_producto_final FROM producto_final WHERE codigo='PF-001')),
(TRUE, 10.00, 'Receta Sundae', CURRENT_DATE, 'Servir tres bolas de helado con salsa de chocolate, caramelo y crema batida.', 'Receta Sundae', 8, (SELECT id_producto_final FROM producto_final WHERE codigo='PF-002')),
(TRUE, 11.00, 'Receta Parfait', CURRENT_DATE, 'Capas de yogur natural con granola crocante y frutas frescas de temporada.', 'Receta Parfait', 7, (SELECT id_producto_final FROM producto_final WHERE codigo='PF-003')),
(TRUE, 5.00, 'Receta Cafe Americano', CURRENT_DATE, 'Moler cafe arabe fresco y preparar con agua a 92 grados.', 'Receta Cafe Americano', 5, (SELECT id_producto_final FROM producto_final WHERE codigo='BC-001')),
(TRUE, 7.00, 'Receta Cafe con Leche', CURRENT_DATE, 'Preparar espresso fuerte y añadir leche espumada al vapor.', 'Receta Cafe con Leche', 5, (SELECT id_producto_final FROM producto_final WHERE codigo='BC-002')),
(TRUE, 9.00, 'Receta Chocolate Caliente', CURRENT_DATE, 'Fundir chocolate amargo con leche caliente y azucar, batir hasta espumar.', 'Receta Chocolate Caliente', 7, (SELECT id_producto_final FROM producto_final WHERE codigo='BC-003')),
(TRUE, 6.00, 'Receta Te Verde', CURRENT_DATE, 'Infusionar te verde japones en agua a 80 grados por 2 minutos.', 'Receta Te Verde', 5, (SELECT id_producto_final FROM producto_final WHERE codigo='BC-004')),
(TRUE, 6.00, 'Receta Cerveza Cristal', CURRENT_DATE, 'Servir cerveza Cristal 330ml bien fria en vaso limpio.', 'Receta Cerveza Cristal', 2, (SELECT id_producto_final FROM producto_final WHERE codigo='BA-001')),
(TRUE, 5.00, 'Receta Cerveza Paceña', CURRENT_DATE, 'Servir Chopp Paceña con espuma perfecta en vaso helado.', 'Receta Cerveza Paceña', 2, (SELECT id_producto_final FROM producto_final WHERE codigo='BA-002')),
(TRUE, 12.00, 'Receta Fernet con Cola', CURRENT_DATE, 'Mezclar Fernet Branca con CocaCola en vaso largo con hielo.', 'Receta Fernet con Cola', 3, (SELECT id_producto_final FROM producto_final WHERE codigo='BA-003')),
(TRUE, 15.00, 'Receta Pisco Sour', CURRENT_DATE, 'Batir pisco, limon, azucar y hielo, servir con gotas de amargo de angostura.', 'Receta Pisco Sour', 5, (SELECT id_producto_final FROM producto_final WHERE codigo='BA-004')),
(TRUE, 8.00, 'Receta Jugo de Naranja', CURRENT_DATE, 'Exprimir naranjas frescas y servir inmediatamente.', 'Receta Jugo de Naranja', 3, (SELECT id_producto_final FROM producto_final WHERE codigo='BS-001')),
(TRUE, 6.00, 'Receta Limonada', CURRENT_DATE, 'Licuar limon fresco con agua, azucar y hojas de hierbabuena.', 'Receta Limonada', 3, (SELECT id_producto_final FROM producto_final WHERE codigo='BS-002')),
(TRUE, 4.00, 'Receta Sprite', CURRENT_DATE, 'Servir Sprite 350ml en lata bien fria con hielo.', 'Receta Sprite', 1, (SELECT id_producto_final FROM producto_final WHERE codigo='BS-003')),
(TRUE, 8.00, 'Receta Papas Fritas', CURRENT_DATE, 'Cortar papas en bastones y freir en aceite caliente hasta dorar, sazonar con sal.', 'Receta Papas Fritas', 10, (SELECT id_producto_final FROM producto_final WHERE codigo='SNK-001')),
(TRUE, 10.00, 'Receta Onion Rings', CURRENT_DATE, 'Cortar cebolla en aros, rebozar con harina y freir hasta crocante.', 'Receta Onion Rings', 12, (SELECT id_producto_final FROM producto_final WHERE codigo='SNK-002')),
(TRUE, 50.00, 'Receta Combo Familiar', CURRENT_DATE, 'Incluye 2 hamburguesas clasicas, papas fritas grandes y 4 bebidas.', 'Receta Combo Familiar', 20, (SELECT id_producto_final FROM producto_final WHERE codigo='COM-001')),
(TRUE, 38.00, 'Receta Combo Pareja', CURRENT_DATE, 'Incluye 2 entradas a elegir, 2 bebidas y 1 postre para compartir.', 'Receta Combo Pareja', 15, (SELECT id_producto_final FROM producto_final WHERE codigo='COM-002')),
(TRUE, 22.00, 'Receta Combo Individual', CURRENT_DATE, 'Incluye 1 hamburguesa clasica, papas fritas y 1 bebida.', 'Receta Combo Individual', 15, (SELECT id_producto_final FROM producto_final WHERE codigo='COM-003')),
(TRUE, 18.00, 'Receta Combo Kids', CURRENT_DATE, 'Incluye mini hamburguesa, papas fritas y jugo natural.', 'Receta Combo Kids', 12, (SELECT id_producto_final FROM producto_final WHERE codigo='COM-004')),
(TRUE, 20.00, 'Receta Ensalada Quinoa', CURRENT_DATE, 'Cocinar quinoa y mezclar con vegetales premium, aguacate y aderezo especial.', 'Receta Ensalada Quinoa', 12, (SELECT id_producto_final FROM producto_final WHERE codigo='EP-001')),
(TRUE, 25.00, 'Receta Caesar Premium', CURRENT_DATE, 'Preparar ensalada cesar con langostinos salteados y aderezo casero.', 'Receta Caesar Premium', 15, (SELECT id_producto_final FROM producto_final WHERE codigo='EP-002')),
(TRUE, 2.00, 'Receta Agua Mineral', CURRENT_DATE, 'Servir agua mineral sin gas 500ml bien fria.', 'Receta Agua Mineral', 1, (SELECT id_producto_final FROM producto_final WHERE codigo='BEB-001')),
(TRUE, 2.50, 'Receta Agua con Gas', CURRENT_DATE, 'Servir agua con gas 500ml bien fria.', 'Receta Agua con Gas', 1, (SELECT id_producto_final FROM producto_final WHERE codigo='BEB-002'))
) AS v(activo, costo_total, descripcion, fecha_creacion, instrucciones, nombre, tiempo_preparacion, id_producto_final)
WHERE NOT EXISTS (SELECT 1 FROM receta r WHERE r.id_producto_final = v.id_producto_final);

-- ============================================================
-- 10. INGREDIENTES_RECETA - 2-4 ingredientes por receta
-- ============================================================

INSERT INTO ingrediente_receta (cantidad, notas, unidad_medida, id_inventario, id_receta)
SELECT v.* FROM (VALUES
-- ENT-001 Nachos Supreme (4 ingredientes)
(0.150, 'Queso rallado para fundir', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-025'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENT-001'))),
(0.200, 'Carne molida para nachos', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-018'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENT-001'))),
(0.050, 'Aji picante fresco', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-010'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENT-001'))),
(0.010, 'Sal fina', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-072'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENT-001'))),
-- ENT-002 Alitas BBQ (3 ingredientes)
(0.300, 'Alitas de pollo enteras', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-016'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENT-002'))),
(0.100, 'Salsa BBQ ahumada', 'LITRO', (SELECT id_inventario FROM inventario WHERE codigo='INV-048'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENT-002'))),
(0.010, 'Sal fina', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-072'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENT-002'))),
-- ENT-003 Tequenos (3 ingredientes)
(0.100, 'Queso mozarella en bastones', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-025'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENT-003'))),
(0.080, 'Harina para masa', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-035'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENT-003'))),
(0.200, 'Aceite para freir', 'LITRO', (SELECT id_inventario FROM inventario WHERE codigo='INV-075'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENT-003'))),
-- ENT-004 Calamares (4 ingredientes)
(0.200, 'Calamar limpio en aros', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-024'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENT-004'))),
(0.050, 'Harina para rebozar', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-035'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENT-004'))),
(0.200, 'Aceite para freir', 'LITRO', (SELECT id_inventario FROM inventario WHERE codigo='INV-075'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENT-004'))),
(0.010, 'Sal fina', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-072'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENT-004'))),
-- ENS-001 Ensalada Cesar (4 ingredientes)
(0.150, 'Lechuga romana fresca', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-006'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENS-001'))),
(0.120, 'Pechuga de pollo a la plancha', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-017'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENS-001'))),
(0.050, 'Aderezo cesar especial', 'LITRO', (SELECT id_inventario FROM inventario WHERE codigo='INV-047'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENS-001'))),
(0.010, 'Sal fina', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-072'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENS-001'))),
-- ENS-002 Ensalada Mixta (4 ingredientes)
(0.120, 'Lechuga fresca', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-006'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENS-002'))),
(0.080, 'Tomate rojo en gajos', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-004'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENS-002'))),
(0.050, 'Cebolla morada en aros', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-005'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENS-002'))),
(0.030, 'Mayonesa casera', 'LITRO', (SELECT id_inventario FROM inventario WHERE codigo='INV-047'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENS-002'))),
-- ENS-003 Ensalada Tropical (3 ingredientes)
(0.100, 'Mango maduro en cubos', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-014'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENS-003'))),
(0.080, 'Aguacate fresco en laminas', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-015'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENS-003'))),
(0.100, 'Lechuga fresca', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-006'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='ENS-003'))),
-- HAM-001 Hamburguesa Clasica (4 ingredientes)
(1, 'Pan artesanal para hamburguesa', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-032'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='HAM-001'))),
(0.180, 'Carne molida 80/20 para pattie', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-018'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='HAM-001'))),
(0.060, 'Tomate en rodajas', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-004'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='HAM-001'))),
(0.040, 'Lechuga fresca', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-006'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='HAM-001'))),
-- HAM-002 Hamburguesa Vegana (4 ingredientes)
(1, 'Pan artesanal para hamburguesa', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-032'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='HAM-002'))),
(0.150, 'Champiñones picados', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-013'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='HAM-002'))),
(0.080, 'Pimiento rojo y verde', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-011'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='HAM-002'))),
(0.020, 'Ajo fresco', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-012'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='HAM-002'))),
-- HAM-003 Hamburguesa Bacon (4 ingredientes)
(1, 'Pan artesanal para hamburguesa', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-032'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='HAM-003'))),
(0.180, 'Carne molida 80/20', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-018'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='HAM-003'))),
(0.080, 'Tocino ahumado en tiras', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-022'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='HAM-003'))),
(0.060, 'Queso cheddar en lonchas', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-026'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='HAM-003'))),
-- CAR-001 Lomo al Trapo (4 ingredientes)
(0.350, 'Lomo fino premium', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-019'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='CAR-001'))),
(0.500, 'Sal gruesa para envolver', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-072'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='CAR-001'))),
(0.020, 'Comino molido', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-074'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='CAR-001'))),
(0.030, 'Aceite para sellar', 'LITRO', (SELECT id_inventario FROM inventario WHERE codigo='INV-075'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='CAR-001'))),
-- CAR-002 Parrillada (4 ingredientes)
(0.150, 'Chorizo artesanal', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-021'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='CAR-002'))),
(0.250, 'Pollo entero en presas', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-016'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='CAR-002'))),
(0.200, 'Lomo fino', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-019'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='CAR-002'))),
(0.010, 'Sal fina', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-072'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='CAR-002'))),
-- CAR-003 Bife de Chorizo (4 ingredientes)
(0.300, 'Bife de chorizo grueso', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-019'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='CAR-003'))),
(0.200, 'Papa larga para fritar', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-008'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='CAR-003'))),
(0.010, 'Sal fina', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-072'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='CAR-003'))),
(0.020, 'Aceite para freir', 'LITRO', (SELECT id_inventario FROM inventario WHERE codigo='INV-075'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='CAR-003'))),
-- CAR-004 Costillas BBQ (4 ingredientes)
(0.500, 'Costillas de cerdo frescas', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-020'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='CAR-004'))),
(0.150, 'Salsa BBQ ahumada', 'LITRO', (SELECT id_inventario FROM inventario WHERE codigo='INV-048'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='CAR-004'))),
(0.020, 'Ajo fresco picado', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-012'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='CAR-004'))),
(0.020, 'Comino molido', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-074'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='CAR-004'))),
-- PIZ-001 Pizza Margarita (4 ingredientes)
(0.250, 'Masa para pizza preparada', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-036'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-001'))),
(0.150, 'Queso mozarella rallado', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-025'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-001'))),
(0.080, 'Tomate en rodajas', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-004'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-001'))),
(0.020, 'Albahaca fresca', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-069'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-001'))),
-- PIZ-002 Pizza Pepperoni (4 ingredientes)
(0.250, 'Masa para pizza preparada', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-036'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-002'))),
(0.150, 'Queso mozarella rallado', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-025'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-002'))),
(0.100, 'Pepperoni artesanal en rodajas', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-023'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-002'))),
(0.020, 'Ajo en polvo', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-012'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-002'))),
-- PIZ-003 Pizza Cuatro Quesos (4 ingredientes)
(0.250, 'Masa para pizza preparada', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-036'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-003'))),
(0.100, 'Queso mozarella rallado', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-025'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-003'))),
(0.080, 'Queso cheddar en lonchas', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-026'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-003'))),
(0.060, 'Queso crema', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-027'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-003'))),
-- PIZ-004 Pizza Hawaiana (4 ingredientes)
(0.250, 'Masa para pizza preparada', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-036'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-004'))),
(0.150, 'Queso mozarella rallado', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-025'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-004'))),
(0.100, 'Pina en almibar trozos', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-070'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-004'))),
(0.010, 'Ketchup para base', 'LITRO', (SELECT id_inventario FROM inventario WHERE codigo='INV-045'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-004'))),
-- PAS-001 Spaghetti Bolognesa (4 ingredientes)
(0.200, 'Harina para pasta fresca', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-035'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PAS-001'))),
(0.200, 'Carne molida para bolognesa', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-018'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PAS-001'))),
(0.150, 'Salsa de tomate casera', 'LITRO', (SELECT id_inventario FROM inventario WHERE codigo='INV-004'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PAS-001'))),
(0.020, 'Ajo fresco picado', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-012'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PAS-001'))),
-- PAS-002 Penne Alfredo (4 ingredientes)
(0.200, 'Harina para pasta fresca', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-035'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PAS-002'))),
(0.100, 'Nata para cocinar', 'ML', (SELECT id_inventario FROM inventario WHERE codigo='INV-030'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PAS-002'))),
(0.080, 'Queso crema', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-027'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PAS-002'))),
(0.010, 'Sal fina', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-072'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PAS-002'))),
-- PAS-003 Lasagna (4 ingredientes)
(0.200, 'Masa para lasagna precocida', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-038'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PAS-003'))),
(0.200, 'Carne molida para ragu', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-018'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PAS-003'))),
(0.100, 'Nata para bechamel', 'ML', (SELECT id_inventario FROM inventario WHERE codigo='INV-030'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PAS-003'))),
(0.120, 'Queso mozarella rallado', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-025'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PAS-003'))),
-- POS-001 Tiramisu (4 ingredientes)
(0.150, 'Galleta soletilla para capas', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-037'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='POS-001'))),
(0.200, 'Queso crema para relleno', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-027'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='POS-001'))),
(0.050, 'Cafe arabe para remojar', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-063'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='POS-001'))),
(0.020, 'Cacao en polvo para espolvorear', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-068'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='POS-001'))),
-- POS-002 Flan Casero (4 ingredientes)
(4, 'Huevos frescos para base', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-071'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='POS-002'))),
(0.500, 'Leche entera para mezcla', 'LITRO', (SELECT id_inventario FROM inventario WHERE codigo='INV-028'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='POS-002'))),
(0.010, 'Extracto de vainilla', 'ML', (SELECT id_inventario FROM inventario WHERE codigo='INV-067'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='POS-002'))),
(0.100, 'Caramelo liquido para base', 'ML', (SELECT id_inventario FROM inventario WHERE codigo='INV-052'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='POS-002'))),
-- POS-003 Brownie con Helado (4 ingredientes)
(0.150, 'Chocolate amargo para fundir', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-065'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='POS-003'))),
(3, 'Huevos frescos para masa', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-071'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='POS-003'))),
(0.100, 'Harina de trigo premium', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-035'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='POS-003'))),
(0.200, 'Leche entera para helado', 'LITRO', (SELECT id_inventario FROM inventario WHERE codigo='INV-028'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='POS-003'))),
-- POS-004 Tres Leches (4 ingredientes)
(4, 'Huevos frescos para bizcocho', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-071'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='POS-004'))),
(0.500, 'Leche entera para mezcla', 'LITRO', (SELECT id_inventario FROM inventario WHERE codigo='INV-028'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='POS-004'))),
(0.100, 'Nata para merengue', 'ML', (SELECT id_inventario FROM inventario WHERE codigo='INV-030'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='POS-004'))),
(0.120, 'Harina de trigo premium', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-035'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='POS-004'))),
-- PF-001 Helado Nieve (3 ingredientes)
(0.500, 'Leche entera para base', 'LITRO', (SELECT id_inventario FROM inventario WHERE codigo='INV-028'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PF-001'))),
(0.010, 'Extracto de vainilla natural', 'ML', (SELECT id_inventario FROM inventario WHERE codigo='INV-067'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PF-001'))),
(0.010, 'Sal fina', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-072'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PF-001'))),
-- PF-002 Sundae (3 ingredientes)
(0.150, 'Helado de chocolate', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-028'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PF-002'))),
(0.050, 'Salsa de chocolate para postres', 'ML', (SELECT id_inventario FROM inventario WHERE codigo='INV-053'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PF-002'))),
(0.050, 'Caramelo liquido', 'ML', (SELECT id_inventario FROM inventario WHERE codigo='INV-052'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PF-002'))),
-- PF-003 Parfait (3 ingredientes)
(0.150, 'Yogur natural sin azucar', 'LITRO', (SELECT id_inventario FROM inventario WHERE codigo='INV-031'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PF-003'))),
(0.100, 'Mango fresco en cubos', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-014'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PF-003'))),
(0.080, 'Leche entera para base', 'LITRO', (SELECT id_inventario FROM inventario WHERE codigo='INV-028'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='PF-003'))),
-- BC-001 Cafe Americano (2 ingredientes)
(0.015, 'Cafe en grano arabe molido', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-063'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BC-001'))),
(0.010, 'Sal fina', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-072'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BC-001'))),
-- BC-002 Cafe con Leche (2 ingredientes)
(0.015, 'Cafe en grano arabe molido', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-063'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BC-002'))),
(0.200, 'Leche entera espumada', 'LITRO', (SELECT id_inventario FROM inventario WHERE codigo='INV-028'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BC-002'))),
-- BC-003 Chocolate Caliente (3 ingredientes)
(0.050, 'Chocolate amargo para fundir', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-065'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BC-003'))),
(0.300, 'Leche entera caliente', 'LITRO', (SELECT id_inventario FROM inventario WHERE codigo='INV-028'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BC-003'))),
(0.020, 'Cacao en polvo sin azucar', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-068'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BC-003'))),
-- BC-004 Te Verde (2 ingredientes)
(0.005, 'Te verde japonés en hojas', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-064'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BC-004'))),
(0.020, 'Hierbabuena fresca', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-066'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BC-004'))),
-- BA-001 Cerveza Cristal (1 ingrediente)
(1, 'Cerveza Cristal 330ml lata', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-059'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BA-001'))),
-- BA-002 Cerveza Paceña (1 ingrediente)
(1, 'Cerveza Paceña 330ml lata', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-060'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BA-002'))),
-- BA-003 Fernet con Cola (2 ingredientes)
(0.060, 'Fernet Branca italiano', 'ML', (SELECT id_inventario FROM inventario WHERE codigo='INV-043'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BA-003'))),
(0.200, 'CocaCola 3 litros', 'UNIDAD', (SELECT id_inventario FROM inventario WHERE codigo='INV-054'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BA-003'))),
-- BA-004 Pisco Sour (3 ingredientes)
(0.090, 'Pisco peruano quebranta', 'ML', (SELECT id_inventario FROM inventario WHERE codigo='INV-041'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BA-004'))),
(0.060, 'Limon fresco exprimido', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-062'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BA-004'))),
(0.010, 'Sal fina para borde', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-072'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BA-004'))),
-- BS-001 Jugo de Naranja (3 ingredientes)
(0.300, 'Naranja fresca para exprimir', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-061'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BS-001'))),
(0.050, 'Hielo en cubos', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-058'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BS-001'))),
(0.010, 'Sal fina', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-072'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BS-001'))),
-- BS-002 Limonada (4 ingredientes)
(0.150, 'Limon fresco exprimido', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-062'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BS-002'))),
(0.020, 'Hierbabuena fresca', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-066'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BS-002'))),
(0.050, 'Hielo en cubos', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-058'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BS-002'))),
(0.010, 'Sal fina', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-072'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BS-002'))),
-- BS-003 Sprite (1 ingrediente)
(1, 'Sprite 2 litros', 'UNIDAD', (SELECT id_inventario FROM inventario WHERE codigo='INV-055'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BS-003'))),
-- SNK-001 Papas Fritas (3 ingredientes)
(0.300, 'Papa larga para fritar', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-008'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='SNK-001'))),
(0.010, 'Sal fina', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-072'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='SNK-001'))),
(0.200, 'Aceite vegetal para freir', 'LITRO', (SELECT id_inventario FROM inventario WHERE codigo='INV-075'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='SNK-001'))),
-- SNK-002 Onion Rings (4 ingredientes)
(0.200, 'Cebolla blanca en aros', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-005'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='SNK-002'))),
(0.080, 'Harina para rebozar', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-035'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='SNK-002'))),
(0.200, 'Aceite vegetal para freir', 'LITRO', (SELECT id_inventario FROM inventario WHERE codigo='INV-075'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='SNK-002'))),
(0.010, 'Sal fina', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-072'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='SNK-002'))),
-- COM-001 Combo Familiar (4 ingredientes)
(2, 'Pan artesanal para hamburguesa', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-032'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='COM-001'))),
(0.360, 'Carne molida para 2 hamburguesas', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-018'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='COM-001'))),
(0.500, 'Papa larga para papas grandes', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-008'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='COM-001'))),
(1, 'CocaCola 3 litros para 4 vasos', 'UNIDAD', (SELECT id_inventario FROM inventario WHERE codigo='INV-054'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='COM-001'))),
-- COM-002 Combo Pareja (3 ingredientes)
(0.150, 'Queso mozarella para entradas', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-025'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='COM-002'))),
(1, 'CocaCola 3 litros para 2 vasos', 'UNIDAD', (SELECT id_inventario FROM inventario WHERE codigo='INV-054'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='COM-002'))),
(0.150, 'Galleta soletilla para postre', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-037'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='COM-002'))),
-- COM-003 Combo Individual (4 ingredientes)
(1, 'Pan artesanal para hamburguesa', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-032'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='COM-003'))),
(0.180, 'Carne molida para pattie', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-018'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='COM-003'))),
(0.250, 'Papa larga para papas fritas', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-008'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='COM-003'))),
(0.333, 'CocaCola 3 litros para 1 vaso', 'UNIDAD', (SELECT id_inventario FROM inventario WHERE codigo='INV-054'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='COM-003'))),
-- COM-004 Combo Kids (3 ingredientes)
(1, 'Pan artesanal pequeño', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-032'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='COM-004'))),
(0.120, 'Carne molida mini pattie', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-018'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='COM-004'))),
(0.200, 'Jugo del Valle frutas tropicales', 'LITRO', (SELECT id_inventario FROM inventario WHERE codigo='INV-057'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='COM-004'))),
-- EP-001 Ensalada Quinoa (4 ingredientes)
(0.150, 'Lechuga fresca premium', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-006'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='EP-001'))),
(0.100, 'Pimiento rojo y verde', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-011'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='EP-001'))),
(0.080, 'Aguacate fresco Hass', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-015'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='EP-001'))),
(0.010, 'Sal fina', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-072'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='EP-001'))),
-- EP-002 Caesar Premium (4 ingredientes)
(0.150, 'Lechuga romana fresca', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-006'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='EP-002'))),
(0.150, 'Calamar limpio salteado', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-024'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='EP-002'))),
(0.060, 'Queso crema para aderezo', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-027'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='EP-002'))),
(0.010, 'Sal fina', 'KG', (SELECT id_inventario FROM inventario WHERE codigo='INV-072'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='EP-002'))),
-- BEB-001 Agua Mineral (1 ingrediente)
(1, 'Agua mineral sin gas 500ml', 'UNIDAD', (SELECT id_inventario FROM inventario WHERE codigo='INV-056'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BEB-001'))),
-- BEB-002 Agua con Gas (1 ingrediente)
(1, 'Agua con gas 500ml', 'UNIDAD', (SELECT id_inventario FROM inventario WHERE codigo='INV-056'), (SELECT id_receta FROM receta WHERE id_producto_final=(SELECT id_producto_final FROM producto_final WHERE codigo='BEB-002')))
) AS v(cantidad, notas, unidad_medida, id_inventario, id_receta);

-- ============================================================
-- 11. PRODUCTO_SUCURSAL
-- Products 1-8 for sucursal 1 + Products 9-49 for ALL sucursales
-- ============================================================

-- Products 1-8 for sucursal 1
INSERT INTO producto_sucursal (id_producto_final, id_sucursal, precio, disponible, activo)
SELECT v.* FROM (VALUES
((SELECT id_producto_final FROM producto_final WHERE codigo='ENT-001'), 1, 35.00, TRUE, TRUE),
((SELECT id_producto_final FROM producto_final WHERE codigo='ENT-002'), 1, 42.00, TRUE, TRUE),
((SELECT id_producto_final FROM producto_final WHERE codigo='ENT-003'), 1, 28.00, TRUE, TRUE),
((SELECT id_producto_final FROM producto_final WHERE codigo='ENT-004'), 1, 38.00, TRUE, TRUE),
((SELECT id_producto_final FROM producto_final WHERE codigo='ENS-001'), 1, 32.00, TRUE, TRUE),
((SELECT id_producto_final FROM producto_final WHERE codigo='ENS-002'), 1, 25.00, TRUE, TRUE),
((SELECT id_producto_final FROM producto_final WHERE codigo='ENS-003'), 1, 35.00, TRUE, TRUE),
((SELECT id_producto_final FROM producto_final WHERE codigo='HAM-001'), 1, 38.00, TRUE, TRUE)
) AS v(id_producto_final, id_sucursal, precio, disponible, activo)
WHERE NOT EXISTS (SELECT 1 FROM producto_sucursal ps WHERE ps.id_producto_final = v.id_producto_final AND ps.id_sucursal = v.id_sucursal);

-- Products 9-49 for ALL 5 sucursales
INSERT INTO producto_sucursal (id_producto_final, id_sucursal, precio, disponible, activo)
SELECT pf.id_producto_final, s.id_sucursal, p.precio, TRUE, TRUE
FROM (VALUES
('HAM-002', 36.00), ('HAM-003', 42.00),
('CAR-001', 85.00), ('CAR-002', 120.00), ('CAR-003', 65.00), ('CAR-004', 78.00),
('PIZ-001', 45.00), ('PIZ-002', 48.00), ('PIZ-003', 52.00), ('PIZ-004', 46.00),
('PAS-001', 42.00), ('PAS-002', 40.00), ('PAS-003', 48.00),
('POS-001', 28.00), ('POS-002', 18.00), ('POS-003', 25.00), ('POS-004', 22.00),
('PF-001', 15.00), ('PF-002', 20.00), ('PF-003', 22.00),
('BC-001', 12.00), ('BC-002', 15.00), ('BC-003', 18.00), ('BC-004', 14.00),
('BA-001', 12.00), ('BA-002', 10.00), ('BA-003', 25.00), ('BA-004', 30.00),
('BS-001', 15.00), ('BS-002', 12.00), ('BS-003', 8.00),
('SNK-001', 18.00), ('SNK-002', 20.00),
('COM-001', 95.00), ('COM-002', 75.00), ('COM-003', 45.00), ('COM-004', 35.00),
('EP-001', 38.00), ('EP-002', 45.00),
('BEB-001', 5.00), ('BEB-002', 6.00)
) AS p(codigo, precio)
CROSS JOIN (VALUES (1),(2),(3),(4),(5)) AS s(id_sucursal)
JOIN producto_final pf ON pf.codigo = p.codigo
WHERE NOT EXISTS (SELECT 1 FROM producto_sucursal ps WHERE ps.id_producto_final = pf.id_producto_final AND ps.id_sucursal = s.id_sucursal);

-- ============================================================
-- 12. STOCK_SUCURSAL - All inventario items x all sucursales
-- ============================================================

INSERT INTO stock_sucursal (activo, cantidad, cantidad_maxima, cantidad_minima, precio_promedio, precio_unitario, ubicacion_almacen, id_inventario, id_sucursal)
SELECT TRUE,
    CASE
        WHEN i.codigo IN ('INV-004','INV-005','INV-006','INV-007','INV-008','INV-009','INV-010','INV-011','INV-012','INV-013','INV-014','INV-015') THEN 30.000
        WHEN i.codigo IN ('INV-016','INV-017','INV-018','INV-019','INV-020','INV-021','INV-022','INV-023','INV-024') THEN 25.000
        WHEN i.codigo IN ('INV-025','INV-026','INV-027','INV-028','INV-029','INV-030','INV-031') THEN 20.000
        WHEN i.codigo IN ('INV-032','INV-033','INV-034','INV-035','INV-036','INV-037','INV-038') THEN 40.000
        WHEN i.codigo IN ('INV-039','INV-040','INV-041','INV-042','INV-043','INV-044') THEN 10.000
        WHEN i.codigo IN ('INV-045','INV-046','INV-047','INV-048','INV-049','INV-050','INV-051','INV-052','INV-053') THEN 15.000
        WHEN i.codigo IN ('INV-054','INV-055','INV-056','INV-057','INV-058') THEN 50.000
        WHEN i.codigo IN ('INV-059','INV-060') THEN 60.000
        WHEN i.codigo IN ('INV-061','INV-062') THEN 12.000
        WHEN i.codigo IN ('INV-063','INV-064','INV-065','INV-066','INV-067','INV-068','INV-069') THEN 8.000
        WHEN i.codigo IN ('INV-070','INV-071') THEN 15.000
        WHEN i.codigo IN ('INV-072','INV-073','INV-074') THEN 5.000
        WHEN i.codigo IN ('INV-075','INV-076') THEN 10.000
        WHEN i.codigo IN ('INV-077','INV-078') THEN 20.000
        ELSE 15.000
    END AS cantidad,
    CASE
        WHEN i.codigo IN ('INV-004','INV-005','INV-006','INV-007','INV-008','INV-009','INV-010','INV-011','INV-012','INV-013','INV-014','INV-015') THEN 80.000
        WHEN i.codigo IN ('INV-016','INV-017','INV-018','INV-019','INV-020','INV-021','INV-022','INV-023','INV-024') THEN 60.000
        WHEN i.codigo IN ('INV-025','INV-026','INV-027','INV-028','INV-029','INV-030','INV-031') THEN 50.000
        WHEN i.codigo IN ('INV-032','INV-033','INV-034','INV-035','INV-036','INV-037','INV-038') THEN 100.000
        WHEN i.codigo IN ('INV-039','INV-040','INV-041','INV-042','INV-043','INV-044') THEN 30.000
        WHEN i.codigo IN ('INV-045','INV-046','INV-047','INV-048','INV-049','INV-050','INV-051','INV-052','INV-053') THEN 40.000
        WHEN i.codigo IN ('INV-054','INV-055','INV-056','INV-057','INV-058') THEN 120.000
        WHEN i.codigo IN ('INV-059','INV-060') THEN 150.000
        WHEN i.codigo IN ('INV-061','INV-062') THEN 30.000
        WHEN i.codigo IN ('INV-063','INV-064','INV-065','INV-066','INV-067','INV-068','INV-069') THEN 20.000
        WHEN i.codigo IN ('INV-070','INV-071') THEN 40.000
        WHEN i.codigo IN ('INV-072','INV-073','INV-074') THEN 15.000
        WHEN i.codigo IN ('INV-075','INV-076') THEN 25.000
        WHEN i.codigo IN ('INV-077','INV-078') THEN 50.000
        ELSE 40.000
    END AS cantidad_maxima,
    CASE
        WHEN i.codigo IN ('INV-016','INV-017','INV-018','INV-019','INV-020','INV-021','INV-022','INV-023','INV-024') THEN 8.000
        WHEN i.codigo IN ('INV-025','INV-026','INV-027','INV-028','INV-029','INV-030','INV-031') THEN 5.000
        WHEN i.codigo IN ('INV-039','INV-040','INV-041','INV-042','INV-043','INV-044') THEN 3.000
        WHEN i.codigo IN ('INV-059','INV-060') THEN 10.000
        WHEN i.codigo IN ('INV-063','INV-064','INV-065','INV-066','INV-067','INV-068','INV-069') THEN 2.000
        ELSE 5.000
    END AS cantidad_minima,
    CASE
        WHEN i.codigo IN ('INV-004','INV-005','INV-006','INV-007','INV-008','INV-009','INV-010','INV-011','INV-012','INV-013','INV-014','INV-015') THEN 8.50
        WHEN i.codigo IN ('INV-016','INV-017') THEN 35.00
        WHEN i.codigo IN ('INV-018') THEN 28.00
        WHEN i.codigo IN ('INV-019') THEN 85.00
        WHEN i.codigo IN ('INV-020') THEN 42.00
        WHEN i.codigo IN ('INV-021') THEN 33.00
        WHEN i.codigo IN ('INV-022') THEN 45.00
        WHEN i.codigo IN ('INV-023') THEN 20.00
        WHEN i.codigo IN ('INV-024') THEN 55.00
        WHEN i.codigo IN ('INV-025') THEN 42.00
        WHEN i.codigo IN ('INV-026') THEN 48.00
        WHEN i.codigo IN ('INV-027') THEN 35.00
        WHEN i.codigo IN ('INV-028') THEN 4.50
        WHEN i.codigo IN ('INV-029') THEN 18.00
        WHEN i.codigo IN ('INV-030') THEN 22.00
        WHEN i.codigo IN ('INV-031') THEN 8.00
        WHEN i.codigo IN ('INV-032') THEN 14.00
        WHEN i.codigo IN ('INV-033') THEN 12.00
        WHEN i.codigo IN ('INV-034') THEN 10.00
        WHEN i.codigo IN ('INV-035') THEN 6.50
        WHEN i.codigo IN ('INV-036') THEN 8.00
        WHEN i.codigo IN ('INV-037') THEN 12.00
        WHEN i.codigo IN ('INV-038') THEN 9.00
        WHEN i.codigo IN ('INV-039') THEN 65.00
        WHEN i.codigo IN ('INV-040') THEN 95.00
        WHEN i.codigo IN ('INV-041') THEN 55.00
        WHEN i.codigo IN ('INV-042') THEN 70.00
        WHEN i.codigo IN ('INV-043') THEN 45.00
        WHEN i.codigo IN ('INV-044') THEN 50.00
        WHEN i.codigo IN ('INV-045') THEN 3.50
        WHEN i.codigo IN ('INV-046') THEN 3.00
        WHEN i.codigo IN ('INV-047') THEN 5.50
        WHEN i.codigo IN ('INV-048') THEN 8.00
        WHEN i.codigo IN ('INV-049') THEN 12.00
        WHEN i.codigo IN ('INV-050') THEN 15.00
        WHEN i.codigo IN ('INV-051') THEN 18.00
        WHEN i.codigo IN ('INV-052') THEN 7.00
        WHEN i.codigo IN ('INV-053') THEN 9.00
        WHEN i.codigo IN ('INV-054') THEN 12.00
        WHEN i.codigo IN ('INV-055') THEN 8.00
        WHEN i.codigo IN ('INV-056') THEN 2.50
        WHEN i.codigo IN ('INV-057') THEN 6.00
        WHEN i.codigo IN ('INV-058') THEN 3.00
        WHEN i.codigo IN ('INV-059') THEN 6.00
        WHEN i.codigo IN ('INV-060') THEN 5.00
        WHEN i.codigo IN ('INV-061') THEN 7.00
        WHEN i.codigo IN ('INV-062') THEN 5.50
        WHEN i.codigo IN ('INV-063') THEN 65.00
        WHEN i.codigo IN ('INV-064') THEN 80.00
        WHEN i.codigo IN ('INV-065') THEN 40.00
        WHEN i.codigo IN ('INV-066') THEN 15.00
        WHEN i.codigo IN ('INV-067') THEN 35.00
        WHEN i.codigo IN ('INV-068') THEN 28.00
        WHEN i.codigo IN ('INV-069') THEN 20.00
        WHEN i.codigo IN ('INV-070') THEN 10.00
        WHEN i.codigo IN ('INV-071') THEN 22.00
        WHEN i.codigo IN ('INV-072') THEN 4.00
        WHEN i.codigo IN ('INV-073') THEN 55.00
        WHEN i.codigo IN ('INV-074') THEN 30.00
        WHEN i.codigo IN ('INV-075') THEN 5.00
        WHEN i.codigo IN ('INV-076') THEN 4.50
        WHEN i.codigo IN ('INV-077') THEN 6.00
        WHEN i.codigo IN ('INV-078') THEN 7.00
        ELSE 10.00
    END AS precio_promedio,
    CASE
        WHEN i.codigo IN ('INV-004','INV-005','INV-006','INV-007','INV-008','INV-009','INV-010','INV-011','INV-012','INV-013','INV-014','INV-015') THEN 8.50
        WHEN i.codigo IN ('INV-016','INV-017') THEN 35.00
        WHEN i.codigo IN ('INV-018') THEN 28.00
        WHEN i.codigo IN ('INV-019') THEN 85.00
        WHEN i.codigo IN ('INV-020') THEN 42.00
        WHEN i.codigo IN ('INV-021') THEN 33.00
        WHEN i.codigo IN ('INV-022') THEN 45.00
        WHEN i.codigo IN ('INV-023') THEN 20.00
        WHEN i.codigo IN ('INV-024') THEN 55.00
        WHEN i.codigo IN ('INV-025') THEN 42.00
        WHEN i.codigo IN ('INV-026') THEN 48.00
        WHEN i.codigo IN ('INV-027') THEN 35.00
        WHEN i.codigo IN ('INV-028') THEN 4.50
        WHEN i.codigo IN ('INV-029') THEN 18.00
        WHEN i.codigo IN ('INV-030') THEN 22.00
        WHEN i.codigo IN ('INV-031') THEN 8.00
        WHEN i.codigo IN ('INV-032') THEN 14.00
        WHEN i.codigo IN ('INV-033') THEN 12.00
        WHEN i.codigo IN ('INV-034') THEN 10.00
        WHEN i.codigo IN ('INV-035') THEN 6.50
        WHEN i.codigo IN ('INV-036') THEN 8.00
        WHEN i.codigo IN ('INV-037') THEN 12.00
        WHEN i.codigo IN ('INV-038') THEN 9.00
        WHEN i.codigo IN ('INV-039') THEN 65.00
        WHEN i.codigo IN ('INV-040') THEN 95.00
        WHEN i.codigo IN ('INV-041') THEN 55.00
        WHEN i.codigo IN ('INV-042') THEN 70.00
        WHEN i.codigo IN ('INV-043') THEN 45.00
        WHEN i.codigo IN ('INV-044') THEN 50.00
        WHEN i.codigo IN ('INV-045') THEN 3.50
        WHEN i.codigo IN ('INV-046') THEN 3.00
        WHEN i.codigo IN ('INV-047') THEN 5.50
        WHEN i.codigo IN ('INV-048') THEN 8.00
        WHEN i.codigo IN ('INV-049') THEN 12.00
        WHEN i.codigo IN ('INV-050') THEN 15.00
        WHEN i.codigo IN ('INV-051') THEN 18.00
        WHEN i.codigo IN ('INV-052') THEN 7.00
        WHEN i.codigo IN ('INV-053') THEN 9.00
        WHEN i.codigo IN ('INV-054') THEN 12.00
        WHEN i.codigo IN ('INV-055') THEN 8.00
        WHEN i.codigo IN ('INV-056') THEN 2.50
        WHEN i.codigo IN ('INV-057') THEN 6.00
        WHEN i.codigo IN ('INV-058') THEN 3.00
        WHEN i.codigo IN ('INV-059') THEN 6.00
        WHEN i.codigo IN ('INV-060') THEN 5.00
        WHEN i.codigo IN ('INV-061') THEN 7.00
        WHEN i.codigo IN ('INV-062') THEN 5.50
        WHEN i.codigo IN ('INV-063') THEN 65.00
        WHEN i.codigo IN ('INV-064') THEN 80.00
        WHEN i.codigo IN ('INV-065') THEN 40.00
        WHEN i.codigo IN ('INV-066') THEN 15.00
        WHEN i.codigo IN ('INV-067') THEN 35.00
        WHEN i.codigo IN ('INV-068') THEN 28.00
        WHEN i.codigo IN ('INV-069') THEN 20.00
        WHEN i.codigo IN ('INV-070') THEN 10.00
        WHEN i.codigo IN ('INV-071') THEN 22.00
        WHEN i.codigo IN ('INV-072') THEN 4.00
        WHEN i.codigo IN ('INV-073') THEN 55.00
        WHEN i.codigo IN ('INV-074') THEN 30.00
        WHEN i.codigo IN ('INV-075') THEN 5.00
        WHEN i.codigo IN ('INV-076') THEN 4.50
        WHEN i.codigo IN ('INV-077') THEN 6.00
        WHEN i.codigo IN ('INV-078') THEN 7.00
        ELSE 10.00
    END AS precio_unitario,
    CASE
        WHEN i.codigo IN ('INV-004','INV-005','INV-006','INV-007','INV-008','INV-009','INV-010','INV-011','INV-012','INV-013','INV-014','INV-015') THEN 'Almacen Vegetales'
        WHEN i.codigo IN ('INV-016','INV-017','INV-018','INV-019','INV-020','INV-021','INV-022','INV-023','INV-024') THEN 'Camara Fria Carnes'
        WHEN i.codigo IN ('INV-025','INV-026','INV-027','INV-028','INV-029','INV-030','INV-031') THEN 'Camara Fria Lacteos'
        WHEN i.codigo IN ('INV-032','INV-033','INV-034','INV-035','INV-036','INV-037','INV-038') THEN 'Almacen Panaderia'
        WHEN i.codigo IN ('INV-039','INV-040','INV-041','INV-042','INV-043','INV-044') THEN 'Bodega Licores'
        WHEN i.codigo IN ('INV-045','INV-046','INV-047','INV-048','INV-049','INV-050','INV-051','INV-052','INV-053') THEN 'Almacen Salsas'
        WHEN i.codigo IN ('INV-054','INV-055','INV-056','INV-057','INV-058') THEN 'Deposito Bebidas'
        WHEN i.codigo IN ('INV-059','INV-060') THEN 'Deposito Cervezas'
        WHEN i.codigo IN ('INV-061','INV-062') THEN 'Almacen Frutas'
        WHEN i.codigo IN ('INV-063','INV-064','INV-065','INV-066','INV-067','INV-068','INV-069') THEN 'Almacen Especias'
        WHEN i.codigo IN ('INV-070','INV-071') THEN 'Camara Fria'
        WHEN i.codigo IN ('INV-072','INV-073','INV-074') THEN 'Almacen Condimentos'
        WHEN i.codigo IN ('INV-075','INV-076') THEN 'Almacen Aceites'
        WHEN i.codigo IN ('INV-077','INV-078') THEN 'Almacen Limpieza'
        ELSE 'Almacen General'
    END AS ubicacion_almacen,
    i.id_inventario,
    s.id_sucursal
FROM inventario i
CROSS JOIN (VALUES (1),(2),(3),(4),(5)) AS s(id_sucursal)
WHERE NOT EXISTS (
    SELECT 1 FROM stock_sucursal ss
    WHERE ss.id_inventario = i.id_inventario AND ss.id_sucursal = s.id_sucursal
);

-- ============================================================
-- 13. LOTE_INVENTARIO - One lot per stock entry
-- ============================================================

INSERT INTO lote_inventario (cantidad, estado, fecha_ingreso, fecha_vencimiento, numero_lote, precio_compra, id_stock)
SELECT
    ss.cantidad,
    'DISPONIBLE',
    CURRENT_DATE - INTERVAL '5 days',
    CASE
        WHEN i.codigo IN ('INV-016','INV-017','INV-018','INV-019','INV-020','INV-021','INV-022','INV-023','INV-024',
                          'INV-025','INV-026','INV-027','INV-028','INV-029','INV-030','INV-031') THEN CURRENT_DATE + INTERVAL '15 days'
        WHEN i.codigo IN ('INV-004','INV-005','INV-006','INV-007','INV-008','INV-009','INV-010','INV-011','INV-012','INV-013','INV-014','INV-015') THEN CURRENT_DATE + INTERVAL '7 days'
        WHEN i.codigo IN ('INV-032','INV-033','INV-034','INV-035','INV-036','INV-037','INV-038') THEN CURRENT_DATE + INTERVAL '10 days'
        WHEN i.codigo IN ('INV-061','INV-062','INV-066','INV-069','INV-070') THEN CURRENT_DATE + INTERVAL '5 days'
        WHEN i.codigo IN ('INV-071') THEN CURRENT_DATE + INTERVAL '20 days'
        WHEN i.codigo IN ('INV-059','INV-060','INV-054','INV-055','INV-056','INV-057') THEN CURRENT_DATE + INTERVAL '180 days'
        ELSE CURRENT_DATE + INTERVAL '90 days'
    END,
    'LOT-' || LPAD(ss.id_stock::TEXT, 4, '0'),
    ss.precio_unitario,
    ss.id_stock
FROM stock_sucursal ss
JOIN inventario i ON i.id_inventario = ss.id_inventario
WHERE NOT EXISTS (
    SELECT 1 FROM lote_inventario l WHERE l.id_stock = ss.id_stock
);

-- ============================================================
-- 14. EMPLEADO_SUCURSAL - All employees to their sucursales
-- ============================================================

-- Sucursal 2 employees: EMP-001 to EMP-010
INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '540 days', e.id_empleado, 2
FROM empleado e WHERE e.codigo_empleado = 'EMP-001'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 2);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '500 days', e.id_empleado, 2
FROM empleado e WHERE e.codigo_empleado = 'EMP-002'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 2);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '480 days', e.id_empleado, 2
FROM empleado e WHERE e.codigo_empleado = 'EMP-003'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 2);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '460 days', e.id_empleado, 2
FROM empleado e WHERE e.codigo_empleado = 'EMP-004'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 2);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '440 days', e.id_empleado, 2
FROM empleado e WHERE e.codigo_empleado = 'EMP-005'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 2);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '420 days', e.id_empleado, 2
FROM empleado e WHERE e.codigo_empleado = 'EMP-006'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 2);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '400 days', e.id_empleado, 2
FROM empleado e WHERE e.codigo_empleado = 'EMP-007'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 2);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '380 days', e.id_empleado, 2
FROM empleado e WHERE e.codigo_empleado = 'EMP-008'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 2);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '360 days', e.id_empleado, 2
FROM empleado e WHERE e.codigo_empleado = 'EMP-009'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 2);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '340 days', e.id_empleado, 2
FROM empleado e WHERE e.codigo_empleado = 'EMP-010'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 2);

-- Sucursal 3 employees: EMP-011 to EMP-020
INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '420 days', e.id_empleado, 3
FROM empleado e WHERE e.codigo_empleado = 'EMP-011'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 3);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '400 days', e.id_empleado, 3
FROM empleado e WHERE e.codigo_empleado = 'EMP-012'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 3);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '380 days', e.id_empleado, 3
FROM empleado e WHERE e.codigo_empleado = 'EMP-013'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 3);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '360 days', e.id_empleado, 3
FROM empleado e WHERE e.codigo_empleado = 'EMP-014'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 3);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '340 days', e.id_empleado, 3
FROM empleado e WHERE e.codigo_empleado = 'EMP-015'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 3);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '320 days', e.id_empleado, 3
FROM empleado e WHERE e.codigo_empleado = 'EMP-016'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 3);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '300 days', e.id_empleado, 3
FROM empleado e WHERE e.codigo_empleado = 'EMP-017'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 3);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '280 days', e.id_empleado, 3
FROM empleado e WHERE e.codigo_empleado = 'EMP-018'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 3);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '260 days', e.id_empleado, 3
FROM empleado e WHERE e.codigo_empleado = 'EMP-019'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 3);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '240 days', e.id_empleado, 3
FROM empleado e WHERE e.codigo_empleado = 'EMP-020'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 3);

-- Sucursal 4 employees: EMP-021 to EMP-030
INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '300 days', e.id_empleado, 4
FROM empleado e WHERE e.codigo_empleado = 'EMP-021'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 4);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '280 days', e.id_empleado, 4
FROM empleado e WHERE e.codigo_empleado = 'EMP-022'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 4);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '260 days', e.id_empleado, 4
FROM empleado e WHERE e.codigo_empleado = 'EMP-023'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 4);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '240 days', e.id_empleado, 4
FROM empleado e WHERE e.codigo_empleado = 'EMP-024'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 4);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '220 days', e.id_empleado, 4
FROM empleado e WHERE e.codigo_empleado = 'EMP-025'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 4);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '200 days', e.id_empleado, 4
FROM empleado e WHERE e.codigo_empleado = 'EMP-026'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 4);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '180 days', e.id_empleado, 4
FROM empleado e WHERE e.codigo_empleado = 'EMP-027'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 4);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '160 days', e.id_empleado, 4
FROM empleado e WHERE e.codigo_empleado = 'EMP-028'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 4);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '140 days', e.id_empleado, 4
FROM empleado e WHERE e.codigo_empleado = 'EMP-029'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 4);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '120 days', e.id_empleado, 4
FROM empleado e WHERE e.codigo_empleado = 'EMP-030'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 4);

-- Sucursal 5 employees: EMP-031 to EMP-040
INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '180 days', e.id_empleado, 5
FROM empleado e WHERE e.codigo_empleado = 'EMP-031'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 5);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '160 days', e.id_empleado, 5
FROM empleado e WHERE e.codigo_empleado = 'EMP-032'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 5);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '140 days', e.id_empleado, 5
FROM empleado e WHERE e.codigo_empleado = 'EMP-033'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 5);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '120 days', e.id_empleado, 5
FROM empleado e WHERE e.codigo_empleado = 'EMP-034'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 5);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '100 days', e.id_empleado, 5
FROM empleado e WHERE e.codigo_empleado = 'EMP-035'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 5);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '80 days', e.id_empleado, 5
FROM empleado e WHERE e.codigo_empleado = 'EMP-036'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 5);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '60 days', e.id_empleado, 5
FROM empleado e WHERE e.codigo_empleado = 'EMP-037'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 5);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '40 days', e.id_empleado, 5
FROM empleado e WHERE e.codigo_empleado = 'EMP-038'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 5);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '30 days', e.id_empleado, 5
FROM empleado e WHERE e.codigo_empleado = 'EMP-039'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 5);

INSERT INTO empleado_sucursal (activo, fecha_asignacion, id_empleado, id_sucursal)
SELECT TRUE, CURRENT_DATE - INTERVAL '20 days', e.id_empleado, 5
FROM empleado e WHERE e.codigo_empleado = 'EMP-040'
AND NOT EXISTS (SELECT 1 FROM empleado_sucursal es WHERE es.id_empleado = e.id_empleado AND es.id_sucursal = 5);

-- ============================================================
-- 15. SECTORES + MESAS extras
-- ============================================================

-- Add sector Patio to sucursal 5
INSERT INTO sector (id_sucursal, nombre, descripcion, tipo_sector, activo)
SELECT 5, 'Patio', 'Sector al aire libre con mesas y plantas para sucursal Centro.', 'PRIVADO', TRUE
WHERE NOT EXISTS (SELECT 1 FROM sector WHERE id_sucursal = 5 AND nombre = 'Patio');

-- Extra mesas for various sectors
INSERT INTO mesa (id_sector, numero_mesa, capacidad_personas, disponibilidad, activo)
SELECT v.* FROM (VALUES
((SELECT id_sector FROM sector WHERE id_sucursal=2 AND nombre='Lámpara'), 'L6', 4, 'DISPONIBLE', TRUE),
((SELECT id_sector FROM sector WHERE id_sucursal=2 AND nombre='Lámpara'), 'L7', 6, 'DISPONIBLE', TRUE),
((SELECT id_sector FROM sector WHERE id_sucursal=2 AND nombre='Terraza'), 'T7', 4, 'DISPONIBLE', TRUE),
((SELECT id_sector FROM sector WHERE id_sucursal=2 AND nombre='Terraza'), 'T8', 6, 'DISPONIBLE', TRUE),
((SELECT id_sector FROM sector WHERE id_sucursal=3 AND nombre='Jaula'), 'J6', 8, 'DISPONIBLE', TRUE),
((SELECT id_sector FROM sector WHERE id_sucursal=3 AND nombre='Patio'), 'P7', 4, 'DISPONIBLE', TRUE),
((SELECT id_sector FROM sector WHERE id_sucursal=4 AND nombre='Lámpara'), 'L8', 4, 'DISPONIBLE', TRUE),
((SELECT id_sector FROM sector WHERE id_sucursal=4 AND nombre='Patio'), 'T7', 6, 'DISPONIBLE', TRUE),
((SELECT id_sector FROM sector WHERE id_sucursal=5 AND nombre='Lámpara'), 'L6', 4, 'DISPONIBLE', TRUE),
((SELECT id_sector FROM sector WHERE id_sucursal=5 AND nombre='Patio'), 'P1', 6, 'DISPONIBLE', TRUE),
((SELECT id_sector FROM sector WHERE id_sucursal=5 AND nombre='Patio'), 'P2', 4, 'DISPONIBLE', TRUE)
) AS v(id_sector, numero_mesa, capacidad_personas, disponibilidad, activo)
WHERE v.id_sector IS NOT NULL AND NOT EXISTS (SELECT 1 FROM mesa m WHERE m.id_sector = v.id_sector AND m.numero_mesa = v.numero_mesa);

-- ============================================================
-- 16. RESERVAS - 15 various states
-- ============================================================

INSERT INTO reserva (cliente_nombre, fecha_reserva, hora_inicio, hora_fin, cantidad_personas, observaciones, estado, id_sucursal, fecha_creacion)
SELECT v.* FROM (VALUES
('Raul Mamani', CURRENT_DATE + INTERVAL '2 days', '19:00'::TIME, '21:00'::TIME, 4, 'Mesa cerca de la ventana', 'CONFIRMADA', 2, CURRENT_TIMESTAMP - INTERVAL '2 days'),
('Carmen Vargas', CURRENT_DATE + INTERVAL '3 days', '20:00'::TIME, '22:00'::TIME, 6, 'Celebracion de cumpleanos', 'CONFIRMADA', 3, CURRENT_TIMESTAMP - INTERVAL '1 day'),
('Luis Herrera', CURRENT_DATE + INTERVAL '1 day', '18:30'::TIME, '20:30'::TIME, 2, 'Mesa tranquila por favor', 'PENDIENTE', 4, CURRENT_TIMESTAMP - INTERVAL '12 hours'),
('Ana Torrico', CURRENT_DATE + INTERVAL '5 days', '19:00'::TIME, '21:00'::TIME, 8, 'Reservacion para grupo grande', 'PENDIENTE', 5, CURRENT_TIMESTAMP - INTERVAL '3 hours'),
('Pedro Vaca', CURRENT_DATE + INTERVAL '4 days', '20:00'::TIME, '22:00'::TIME, 3, 'Silla para bebe', 'CONFIRMADA', 2, CURRENT_TIMESTAMP - INTERVAL '1 day'),
('Maria Espinosa', CURRENT_DATE + INTERVAL '7 days', '18:00'::TIME, '20:00'::TIME, 4, NULL, 'PENDIENTE', 1, CURRENT_TIMESTAMP - INTERVAL '6 hours'),
('Joaquin Pereira', CURRENT_DATE + INTERVAL '6 days', '19:30'::TIME, '21:30'::TIME, 5, 'Sector VIP preferencia', 'PENDIENTE', 3, CURRENT_TIMESTAMP - INTERVAL '2 days'),
('Laura Gutierrez', CURRENT_DATE + INTERVAL '2 days', '12:00'::TIME, '14:00'::TIME, 2, 'Almuerzo de negocios', 'CONFIRMADA', 4, CURRENT_TIMESTAMP - INTERVAL '1 day'),
('Carlos Medina', CURRENT_DATE + INTERVAL '8 days', '20:00'::TIME, '22:00'::TIME, 6, 'Musica en vivo si es posible', 'PENDIENTE', 5, CURRENT_TIMESTAMP - INTERVAL '4 hours'),
('Belen Choque', CURRENT_DATE + INTERVAL '3 days', '13:00'::TIME, '15:00'::TIME, 4, 'Vegetariana, opciones sin carne', 'CONFIRMADA', 2, CURRENT_TIMESTAMP - INTERVAL '18 hours'),
('Marco Apaza', CURRENT_DATE + INTERVAL '10 days', '19:00'::TIME, '21:00'::TIME, 10, 'Evento corporativo grande', 'PENDIENTE', 1, CURRENT_TIMESTAMP - INTERVAL '1 day'),
('Diana Lopez', CURRENT_DATE + INTERVAL '9 days', '18:00'::TIME, '20:00'::TIME, 3, 'Cena romantica', 'PENDIENTE', 3, CURRENT_TIMESTAMP - INTERVAL '12 hours'),
('Sergio Ramos', CURRENT_DATE + INTERVAL '11 days', '20:00'::TIME, '22:00'::TIME, 8, 'Graduacion familiar', 'PENDIENTE', 5, CURRENT_TIMESTAMP - INTERVAL '2 days'),
('Paola Mita', CURRENT_DATE + INTERVAL '12 days', '19:00'::TIME, '21:00'::TIME, 4, 'Aniversario de boda', 'PENDIENTE', 4, CURRENT_TIMESTAMP - INTERVAL '1 day'),
('Andres Cabrera', CURRENT_DATE + INTERVAL '13 days', '13:00'::TIME, '15:00'::TIME, 6, 'Almuerzo dominical', 'PENDIENTE', 2, CURRENT_TIMESTAMP - INTERVAL '6 hours')
) AS v(cliente_nombre, fecha_reserva, hora_inicio, hora_fin, cantidad_personas, observaciones, estado, id_sucursal, fecha_creacion);

-- ============================================================
-- 17. RESERVA_MESA - One mesa per reserva
-- ============================================================

INSERT INTO reserva_mesa (id_reserva, id_mesa)
SELECT r.id_reserva, (
    SELECT m2.id_mesa FROM mesa m2
    WHERE m2.activo = TRUE
    ORDER BY m2.id_mesa
    LIMIT 1 OFFSET (rn_val - 1) % (SELECT COUNT(*) FROM mesa WHERE activo = TRUE)
)
FROM (
    SELECT id_reserva, ROW_NUMBER() OVER (ORDER BY id_reserva) AS rn_val
    FROM reserva
    WHERE id_reserva NOT IN (SELECT id_reserva FROM reserva_mesa)
) r;

-- ============================================================
-- 18. COMANDAS - 30 new (MESA, PARA_LLEVAR, ONLINE)
-- ============================================================

-- First create carritos for ONLINE comandas (one per client needed)
INSERT INTO carrito_compras (id_cliente, id_sucursal, fecha_creacion, fecha_actualizacion, estado)
SELECT c.id_cliente, ((ROW_NUMBER() OVER (ORDER BY c.id_cliente) % 4) + 2), CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days', 'CONVERTIDO'
FROM cliente c
WHERE c.nit IN ('6789012','7890123','8901234','9012345','0123456','1112233','2223344')
AND NOT EXISTS (SELECT 1 FROM carrito_compras cc WHERE cc.id_cliente = c.id_cliente AND cc.estado = 'CONVERTIDO');

-- MESA comandas (10) - need reserva
INSERT INTO comanda (numero_comanda, id_sucursal, id_cliente, id_empleado, id_reserva, tipo_servicio, fecha_apertura, fecha_cierre, numero_personas, estado, observaciones)
SELECT v.* FROM (VALUES
('CMD-20260601-0001', 2, (SELECT id_cliente FROM cliente WHERE nit='1234567'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-001'),
 (SELECT rm.id_reserva FROM reserva_mesa rm LIMIT 1), 'MESA', CURRENT_DATE - INTERVAL '20 days' + TIME '19:30', CURRENT_DATE - INTERVAL '20 days' + TIME '21:00', 4, 'CERRADA', 'Pedido completado'),
('CMD-20260602-0002', 3, (SELECT id_cliente FROM cliente WHERE nit='2345678'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-011'),
 (SELECT rm.id_reserva FROM reserva_mesa rm ORDER BY rm.id_reserva OFFSET 1 LIMIT 1), 'MESA', CURRENT_DATE - INTERVAL '19 days' + TIME '20:00', CURRENT_DATE - INTERVAL '19 days' + TIME '22:00', 6, 'CERRADA', NULL),
('CMD-20260603-0003', 4, (SELECT id_cliente FROM cliente WHERE nit='3456789'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-021'),
 (SELECT rm.id_reserva FROM reserva_mesa rm ORDER BY rm.id_reserva OFFSET 2 LIMIT 1), 'MESA', CURRENT_DATE - INTERVAL '18 days' + TIME '18:30', CURRENT_DATE - INTERVAL '18 days' + TIME '20:30', 2, 'CERRADA', 'Pedido rapido'),
('CMD-20260604-0004', 5, (SELECT id_cliente FROM cliente WHERE nit='4567890'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-031'),
 (SELECT rm.id_reserva FROM reserva_mesa rm ORDER BY rm.id_reserva OFFSET 3 LIMIT 1), 'MESA', CURRENT_DATE - INTERVAL '17 days' + TIME '19:00', CURRENT_DATE - INTERVAL '17 days' + TIME '21:00', 8, 'CERRADA', 'Evento especial'),
('CMD-20260605-0005', 2, (SELECT id_cliente FROM cliente WHERE nit='5678901'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-002'),
 (SELECT rm.id_reserva FROM reserva_mesa rm ORDER BY rm.id_reserva OFFSET 4 LIMIT 1), 'MESA', CURRENT_DATE - INTERVAL '15 days' + TIME '20:00', CURRENT_DATE - INTERVAL '15 days' + TIME '22:00', 3, 'CERRADA', NULL),
('CMD-20260606-0006', 3, (SELECT id_cliente FROM cliente WHERE nit='6789012'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-012'),
 (SELECT rm.id_reserva FROM reserva_mesa rm ORDER BY rm.id_reserva OFFSET 5 LIMIT 1), 'MESA', CURRENT_DATE - INTERVAL '14 days' + TIME '19:00', CURRENT_DATE - INTERVAL '14 days' + TIME '21:30', 4, 'CERRADA', NULL),
('CMD-20260607-0007', 4, (SELECT id_cliente FROM cliente WHERE nit='7890123'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-022'),
 (SELECT rm.id_reserva FROM reserva_mesa rm ORDER BY rm.id_reserva OFFSET 6 LIMIT 1), 'MESA', CURRENT_DATE - INTERVAL '12 days' + TIME '18:00', CURRENT_DATE - INTERVAL '12 days' + TIME '20:00', 5, 'CERRADA', 'Cena familiar'),
('CMD-20260608-0008', 2, (SELECT id_cliente FROM cliente WHERE nit='8901234'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-003'),
 (SELECT rm.id_reserva FROM reserva_mesa rm ORDER BY rm.id_reserva OFFSET 7 LIMIT 1), 'MESA', CURRENT_DATE - INTERVAL '10 days' + TIME '13:00', CURRENT_DATE - INTERVAL '10 days' + TIME '15:00', 2, 'CERRADA', NULL),
('CMD-20260609-0009', 5, (SELECT id_cliente FROM cliente WHERE nit='9012345'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-032'),
 (SELECT rm.id_reserva FROM reserva_mesa rm ORDER BY rm.id_reserva OFFSET 8 LIMIT 1), 'MESA', CURRENT_DATE - INTERVAL '8 days' + TIME '19:00', CURRENT_DATE - INTERVAL '8 days' + TIME '21:00', 6, 'CERRADA', NULL),
('CMD-20260610-0010', 3, (SELECT id_cliente FROM cliente WHERE nit='0123456'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-013'),
 (SELECT rm.id_reserva FROM reserva_mesa rm ORDER BY rm.id_reserva OFFSET 9 LIMIT 1), 'MESA', CURRENT_DATE - INTERVAL '5 days' + TIME '20:00', CURRENT_DATE - INTERVAL '5 days' + TIME '22:00', 4, 'CERRADA', NULL)
) AS v(numero_comanda, id_sucursal, id_cliente, id_empleado, id_reserva, tipo_servicio, fecha_apertura, fecha_cierre, numero_personas, estado, observaciones)
WHERE NOT EXISTS (SELECT 1 FROM comanda c WHERE c.numero_comanda = v.numero_comanda);

-- PARA_LLEVAR comandas (13)
INSERT INTO comanda (numero_comanda, id_sucursal, id_cliente, id_empleado, id_reserva, tipo_servicio, fecha_apertura, fecha_cierre, numero_personas, estado, observaciones)
SELECT v.* FROM (VALUES
('CMD-20260601-0011', 2, (SELECT id_cliente FROM cliente WHERE nit='1234567'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-004'),
 NULL::bigint, 'PARA_LLEVAR', CURRENT_DATE - INTERVAL '20 days' + TIME '12:30', CURRENT_DATE - INTERVAL '20 days' + TIME '13:00', 1, 'CERRADA', 'Para llevar oficina'),
('CMD-20260602-0012', 3, (SELECT id_cliente FROM cliente WHERE nit='2345678'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-014'),
 NULL::bigint, 'PARA_LLEVAR', CURRENT_DATE - INTERVAL '19 days' + TIME '13:00', CURRENT_DATE - INTERVAL '19 days' + TIME '13:30', 2, 'CERRADA', NULL),
('CMD-20260603-0013', 4, (SELECT id_cliente FROM cliente WHERE nit='3456789'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-023'),
 NULL::bigint, 'PARA_LLEVAR', CURRENT_DATE - INTERVAL '18 days' + TIME '12:00', CURRENT_DATE - INTERVAL '18 days' + TIME '12:30', 1, 'CERRADA', NULL),
('CMD-20260605-0014', 2, (SELECT id_cliente FROM cliente WHERE nit='5678901'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-005'),
 NULL::bigint, 'PARA_LLEVAR', CURRENT_DATE - INTERVAL '15 days' + TIME '18:00', CURRENT_DATE - INTERVAL '15 days' + TIME '18:30', 3, 'CERRADA', 'Pedido grande'),
('CMD-20260607-0015', 5, (SELECT id_cliente FROM cliente WHERE nit='7890123'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-033'),
 NULL::bigint, 'PARA_LLEVAR', CURRENT_DATE - INTERVAL '12 days' + TIME '19:00', CURRENT_DATE - INTERVAL '12 days' + TIME '19:30', 2, 'CERRADA', NULL),
('CMD-20260608-0016', 3, (SELECT id_cliente FROM cliente WHERE nit='8901234'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-015'),
 NULL::bigint, 'PARA_LLEVAR', CURRENT_DATE - INTERVAL '10 days' + TIME '12:30', CURRENT_DATE - INTERVAL '10 days' + TIME '13:00', 1, 'CERRADA', NULL),
('CMD-20260609-0017', 4, (SELECT id_cliente FROM cliente WHERE nit='9012345'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-024'),
 NULL::bigint, 'PARA_LLEVAR', CURRENT_DATE - INTERVAL '8 days' + TIME '13:00', CURRENT_DATE - INTERVAL '8 days' + TIME '13:30', 2, 'CERRADA', NULL),
('CMD-20260610-0018', 2, (SELECT id_cliente FROM cliente WHERE nit='0123456'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-006'),
 NULL::bigint, 'PARA_LLEVAR', CURRENT_DATE - INTERVAL '5 days' + TIME '18:30', CURRENT_DATE - INTERVAL '5 days' + TIME '19:00', 1, 'CERRADA', NULL),
('CMD-20260611-0019', 5, (SELECT id_cliente FROM cliente WHERE nit='1112233'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-034'),
 NULL::bigint, 'PARA_LLEVAR', CURRENT_DATE - INTERVAL '4 days' + TIME '12:00', CURRENT_DATE - INTERVAL '4 days' + TIME '12:30', 4, 'CERRADA', 'Almuerzo oficina'),
('CMD-20260615-0020', 3, (SELECT id_cliente FROM cliente WHERE nit='2223344'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-016'),
 NULL::bigint, 'PARA_LLEVAR', CURRENT_DATE - INTERVAL '3 days' + TIME '19:00', CURRENT_DATE - INTERVAL '3 days' + TIME '19:30', 2, 'ENTREGADA', NULL),
('CMD-20260617-0021', 4, (SELECT id_cliente FROM cliente WHERE nit='3334455'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-025'),
 NULL::bigint, 'PARA_LLEVAR', CURRENT_DATE - INTERVAL '2 days' + TIME '13:00', NULL, 1, 'EN_PREPARACION', 'Pedido en curso'),
('CMD-20260620-0022', 2, (SELECT id_cliente FROM cliente WHERE nit='4445566'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-007'),
 NULL::bigint, 'PARA_LLEVAR', CURRENT_DATE - INTERVAL '1 day' + TIME '18:00', NULL, 3, 'LISTA', 'Esperando recojo'),
('CMD-20260621-0023', 5, (SELECT id_cliente FROM cliente WHERE nit='5556677'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-035'),
 NULL::bigint, 'PARA_LLEVAR', CURRENT_DATE + TIME '12:00', NULL, 2, 'ABIERTA', 'Pedido del dia')
) AS v(numero_comanda, id_sucursal, id_cliente, id_empleado, id_reserva, tipo_servicio, fecha_apertura, fecha_cierre, numero_personas, estado, observaciones)
WHERE NOT EXISTS (SELECT 1 FROM comanda c WHERE c.numero_comanda = v.numero_comanda);

-- ONLINE comandas (7) - need carrito
INSERT INTO comanda (numero_comanda, id_sucursal, id_cliente, id_empleado, id_reserva, id_carrito, tipo_servicio, fecha_apertura, fecha_cierre, numero_personas, estado, observaciones)
SELECT v.* FROM (VALUES
('CMD-20260601-0024', 2, (SELECT id_cliente FROM cliente WHERE nit='6789012'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-008'),
 NULL::bigint, (SELECT id_carrito FROM carrito_compras WHERE id_cliente=(SELECT id_cliente FROM cliente WHERE nit='6789012') AND estado='CONVERTIDO' LIMIT 1),
 'ONLINE', CURRENT_DATE - INTERVAL '20 days' + TIME '14:00', CURRENT_DATE - INTERVAL '20 days' + TIME '15:00', 1, 'CERRADA', 'Pedido online delivery'),
('CMD-20260603-0025', 3, (SELECT id_cliente FROM cliente WHERE nit='7890123'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-017'),
 NULL::bigint, (SELECT id_carrito FROM carrito_compras WHERE id_cliente=(SELECT id_cliente FROM cliente WHERE nit='7890123') AND estado='CONVERTIDO' LIMIT 1),
 'ONLINE', CURRENT_DATE - INTERVAL '18 days' + TIME '19:00', CURRENT_DATE - INTERVAL '18 days' + TIME '20:00', 2, 'CERRADA', NULL),
('CMD-20260605-0026', 4, (SELECT id_cliente FROM cliente WHERE nit='8901234'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-026'),
 NULL::bigint, (SELECT id_carrito FROM carrito_compras WHERE id_cliente=(SELECT id_cliente FROM cliente WHERE nit='8901234') AND estado='CONVERTIDO' LIMIT 1),
 'ONLINE', CURRENT_DATE - INTERVAL '15 days' + TIME '12:00', CURRENT_DATE - INTERVAL '15 days' + TIME '13:00', 1, 'CERRADA', NULL),
('CMD-20260608-0027', 5, (SELECT id_cliente FROM cliente WHERE nit='9012345'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-036'),
 NULL::bigint, (SELECT id_carrito FROM carrito_compras WHERE id_cliente=(SELECT id_cliente FROM cliente WHERE nit='9012345') AND estado='CONVERTIDO' LIMIT 1),
 'ONLINE', CURRENT_DATE - INTERVAL '10 days' + TIME '20:00', CURRENT_DATE - INTERVAL '10 days' + TIME '21:00', 1, 'CERRADA', NULL),
('CMD-20260612-0028', 2, (SELECT id_cliente FROM cliente WHERE nit='0123456'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-009'),
 NULL::bigint, (SELECT id_carrito FROM carrito_compras WHERE id_cliente=(SELECT id_cliente FROM cliente WHERE nit='0123456') AND estado='CONVERTIDO' LIMIT 1),
 'ONLINE', CURRENT_DATE - INTERVAL '4 days' + TIME '18:00', CURRENT_DATE - INTERVAL '4 days' + TIME '19:00', 2, 'ENTREGADA', 'Delivery completo'),
('CMD-20260618-0029', 3, (SELECT id_cliente FROM cliente WHERE nit='1112233'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-018'),
 NULL::bigint, (SELECT id_carrito FROM carrito_compras WHERE id_cliente=(SELECT id_cliente FROM cliente WHERE nit='1112233') AND estado='CONVERTIDO' LIMIT 1),
 'ONLINE', CURRENT_DATE - INTERVAL '2 days' + TIME '19:00', NULL, 1, 'EN_PREPARACION', 'En camino'),
('CMD-20260622-0030', 4, (SELECT id_cliente FROM cliente WHERE nit='2223344'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-027'),
 NULL::bigint, (SELECT id_carrito FROM carrito_compras WHERE id_cliente=(SELECT id_cliente FROM cliente WHERE nit='2223344') AND estado='CONVERTIDO' LIMIT 1),
 'ONLINE', CURRENT_DATE + TIME '14:00', NULL, 2, 'ABIERTA', 'Pedido nuevo')
) AS v(numero_comanda, id_sucursal, id_cliente, id_empleado, id_reserva, id_carrito, tipo_servicio, fecha_apertura, fecha_cierre, numero_personas, estado, observaciones)
WHERE NOT EXISTS (SELECT 1 FROM comanda c WHERE c.numero_comanda = v.numero_comanda);

-- ============================================================
-- 19. DETALLE_COMANDA - 90 items (3 per comanda)
-- ============================================================

INSERT INTO detalle_comanda (cantidad, empleado_asignado, estacion_preparacion, estado, fecha_aceptacion, fecha_creacion, notas, precio_unitario, id_comanda, id_producto_final)
SELECT v.* FROM (VALUES
-- CMD-0001 items
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-001'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '20 days' + TIME '19:35', CURRENT_DATE - INTERVAL '20 days' + TIME '19:30', 'Sin cebolla', 35.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260601-0001'), (SELECT id_producto_final FROM producto_final WHERE codigo='ENT-001')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-001'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '20 days' + TIME '19:35', CURRENT_DATE - INTERVAL '20 days' + TIME '19:30', NULL, 38.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260601-0001'), (SELECT id_producto_final FROM producto_final WHERE codigo='HAM-001')),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-001'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '20 days' + TIME '19:33', CURRENT_DATE - INTERVAL '20 days' + TIME '19:30', NULL, 12.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260601-0001'), (SELECT id_producto_final FROM producto_final WHERE codigo='BA-001')),
-- CMD-0002 items
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-011'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '19 days' + TIME '20:05', CURRENT_DATE - INTERVAL '19 days' + TIME '20:00', NULL, 120.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260602-0002'), (SELECT id_producto_final FROM producto_final WHERE codigo='CAR-002')),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-011'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '19 days' + TIME '20:05', CURRENT_DATE - INTERVAL '19 days' + TIME '20:00', 'Extra queso', 45.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260602-0002'), (SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-001')),
(3, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-011'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '19 days' + TIME '20:03', CURRENT_DATE - INTERVAL '19 days' + TIME '20:00', NULL, 10.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260602-0002'), (SELECT id_producto_final FROM producto_final WHERE codigo='BA-002')),
-- CMD-0003 items
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-021'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '18 days' + TIME '18:35', CURRENT_DATE - INTERVAL '18 days' + TIME '18:30', NULL, 32.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260603-0003'), (SELECT id_producto_final FROM producto_final WHERE codigo='ENS-001')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-021'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '18 days' + TIME '18:35', CURRENT_DATE - INTERVAL '18 days' + TIME '18:30', NULL, 42.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260603-0003'), (SELECT id_producto_final FROM producto_final WHERE codigo='ENT-002')),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-021'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '18 days' + TIME '18:33', CURRENT_DATE - INTERVAL '18 days' + TIME '18:30', NULL, 15.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260603-0003'), (SELECT id_producto_final FROM producto_final WHERE codigo='BS-002')),
-- CMD-0004 items
(3, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-031'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '17 days' + TIME '19:05', CURRENT_DATE - INTERVAL '17 days' + TIME '19:00', NULL, 48.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260604-0004'), (SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-003')),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-031'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '17 days' + TIME '19:05', CURRENT_DATE - INTERVAL '17 days' + TIME '19:00', NULL, 65.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260604-0004'), (SELECT id_producto_final FROM producto_final WHERE codigo='CAR-003')),
(4, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-031'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '17 days' + TIME '19:03', CURRENT_DATE - INTERVAL '17 days' + TIME '19:00', NULL, 12.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260604-0004'), (SELECT id_producto_final FROM producto_final WHERE codigo='BA-001')),
-- CMD-0005 items
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-002'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '15 days' + TIME '20:05', CURRENT_DATE - INTERVAL '15 days' + TIME '20:00', NULL, 85.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260605-0005'), (SELECT id_producto_final FROM producto_final WHERE codigo='CAR-001')),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-002'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '15 days' + TIME '20:05', CURRENT_DATE - INTERVAL '15 days' + TIME '20:00', NULL, 28.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260605-0005'), (SELECT id_producto_final FROM producto_final WHERE codigo='ENT-003')),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-002'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '15 days' + TIME '20:03', CURRENT_DATE - INTERVAL '15 days' + TIME '20:00', NULL, 25.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260605-0005'), (SELECT id_producto_final FROM producto_final WHERE codigo='BA-003')),
-- CMD-0006 items
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-012'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '14 days' + TIME '19:05', CURRENT_DATE - INTERVAL '14 days' + TIME '19:00', NULL, 42.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260606-0006'), (SELECT id_producto_final FROM producto_final WHERE codigo='PAS-001')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-012'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '14 days' + TIME '19:05', CURRENT_DATE - INTERVAL '14 days' + TIME '19:00', NULL, 22.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260606-0006'), (SELECT id_producto_final FROM producto_final WHERE codigo='POS-001')),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-012'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '14 days' + TIME '19:03', CURRENT_DATE - INTERVAL '14 days' + TIME '19:00', NULL, 30.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260606-0006'), (SELECT id_producto_final FROM producto_final WHERE codigo='BA-004')),
-- CMD-0007 items
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-022'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '12 days' + TIME '18:05', CURRENT_DATE - INTERVAL '12 days' + TIME '18:00', NULL, 40.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260607-0007'), (SELECT id_producto_final FROM producto_final WHERE codigo='HAM-001')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-022'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '12 days' + TIME '18:05', CURRENT_DATE - INTERVAL '12 days' + TIME '18:00', NULL, 78.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260607-0007'), (SELECT id_producto_final FROM producto_final WHERE codigo='CAR-004')),
(3, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-022'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '12 days' + TIME '18:03', CURRENT_DATE - INTERVAL '12 days' + TIME '18:00', NULL, 12.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260607-0007'), (SELECT id_producto_final FROM producto_final WHERE codigo='BA-001')),
-- CMD-0008 items
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-003'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '10 days' + TIME '13:05', CURRENT_DATE - INTERVAL '10 days' + TIME '13:00', NULL, 25.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260608-0008'), (SELECT id_producto_final FROM producto_final WHERE codigo='ENS-003')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-003'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '10 days' + TIME '13:05', CURRENT_DATE - INTERVAL '10 days' + TIME '13:00', NULL, 36.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260608-0008'), (SELECT id_producto_final FROM producto_final WHERE codigo='HAM-002')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-003'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '10 days' + TIME '13:03', CURRENT_DATE - INTERVAL '10 days' + TIME '13:00', NULL, 15.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260608-0008'), (SELECT id_producto_final FROM producto_final WHERE codigo='BC-002')),
-- CMD-0009 items
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-032'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '8 days' + TIME '19:05', CURRENT_DATE - INTERVAL '8 days' + TIME '19:00', NULL, 52.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260609-0009'), (SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-003')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-032'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '8 days' + TIME '19:05', CURRENT_DATE - INTERVAL '8 days' + TIME '19:00', NULL, 18.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260609-0009'), (SELECT id_producto_final FROM producto_final WHERE codigo='SNK-001')),
(3, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-032'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '8 days' + TIME '19:03', CURRENT_DATE - INTERVAL '8 days' + TIME '19:00', NULL, 8.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260609-0009'), (SELECT id_producto_final FROM producto_final WHERE codigo='BS-003')),
-- CMD-0010 items
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-013'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '5 days' + TIME '20:05', CURRENT_DATE - INTERVAL '5 days' + TIME '20:00', NULL, 46.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260610-0010'), (SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-004')),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-013'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '5 days' + TIME '20:05', CURRENT_DATE - INTERVAL '5 days' + TIME '20:00', NULL, 28.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260610-0010'), (SELECT id_producto_final FROM producto_final WHERE codigo='ENT-003')),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-013'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '5 days' + TIME '20:03', CURRENT_DATE - INTERVAL '5 days' + TIME '20:00', NULL, 14.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260610-0010'), (SELECT id_producto_final FROM producto_final WHERE codigo='BC-003')),
-- CMD-0011 items (PARA_LLEVAR)
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-004'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '20 days' + TIME '12:35', CURRENT_DATE - INTERVAL '20 days' + TIME '12:30', 'Para llevar', 38.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260601-0011'), (SELECT id_producto_final FROM producto_final WHERE codigo='HAM-001')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-004'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '20 days' + TIME '12:35', CURRENT_DATE - INTERVAL '20 days' + TIME '12:30', NULL, 18.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260601-0011'), (SELECT id_producto_final FROM producto_final WHERE codigo='SNK-001')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-004'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '20 days' + TIME '12:33', CURRENT_DATE - INTERVAL '20 days' + TIME '12:30', NULL, 8.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260601-0011'), (SELECT id_producto_final FROM producto_final WHERE codigo='BS-003')),
-- CMD-0012 items
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-014'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '19 days' + TIME '13:05', CURRENT_DATE - INTERVAL '19 days' + TIME '13:00', NULL, 42.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260602-0012'), (SELECT id_producto_final FROM producto_final WHERE codigo='ENT-002')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-014'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '19 days' + TIME '13:05', CURRENT_DATE - INTERVAL '19 days' + TIME '13:00', NULL, 22.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260602-0012'), (SELECT id_producto_final FROM producto_final WHERE codigo='POS-003')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-014'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '19 days' + TIME '13:03', CURRENT_DATE - INTERVAL '19 days' + TIME '13:00', NULL, 15.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260602-0012'), (SELECT id_producto_final FROM producto_final WHERE codigo='BS-001'))
) AS v(cantidad, empleado_asignado, estacion_preparacion, estado, fecha_aceptacion, fecha_creacion, notas, precio_unitario, id_comanda, id_producto_final);

-- Remaining DETALLE_COMANDA items (48 more = 90 total)
INSERT INTO detalle_comanda (cantidad, empleado_asignado, estacion_preparacion, estado, fecha_aceptacion, fecha_creacion, notas, precio_unitario, id_comanda, id_producto_final)
SELECT v.* FROM (VALUES
-- CMD-0013
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-023'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '18 days' + TIME '12:05', CURRENT_DATE - INTERVAL '18 days' + TIME '12:00', NULL, 45.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260603-0013'), (SELECT id_producto_final FROM producto_final WHERE codigo='COM-003')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-023'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '18 days' + TIME '12:05', CURRENT_DATE - INTERVAL '18 days' + TIME '12:00', NULL, 25.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260603-0013'), (SELECT id_producto_final FROM producto_final WHERE codigo='ENS-002')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-023'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '18 days' + TIME '12:03', CURRENT_DATE - INTERVAL '18 days' + TIME '12:00', NULL, 12.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260603-0013'), (SELECT id_producto_final FROM producto_final WHERE codigo='BS-002')),
-- CMD-0014
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-005'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '15 days' + TIME '18:05', CURRENT_DATE - INTERVAL '15 days' + TIME '18:00', NULL, 95.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260605-0014'), (SELECT id_producto_final FROM producto_final WHERE codigo='COM-001')),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-005'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '15 days' + TIME '18:05', CURRENT_DATE - INTERVAL '15 days' + TIME '18:00', NULL, 20.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260605-0014'), (SELECT id_producto_final FROM producto_final WHERE codigo='SNK-002')),
(3, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-005'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '15 days' + TIME '18:03', CURRENT_DATE - INTERVAL '15 days' + TIME '18:00', NULL, 8.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260605-0014'), (SELECT id_producto_final FROM producto_final WHERE codigo='BS-003')),
-- CMD-0015
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-033'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '12 days' + TIME '19:05', CURRENT_DATE - INTERVAL '12 days' + TIME '19:00', NULL, 42.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260607-0015'), (SELECT id_producto_final FROM producto_final WHERE codigo='PAS-002')),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-033'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '12 days' + TIME '19:05', CURRENT_DATE - INTERVAL '12 days' + TIME '19:00', NULL, 18.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260607-0015'), (SELECT id_producto_final FROM producto_final WHERE codigo='POS-002')),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-033'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '12 days' + TIME '19:03', CURRENT_DATE - INTERVAL '12 days' + TIME '19:00', NULL, 14.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260607-0015'), (SELECT id_producto_final FROM producto_final WHERE codigo='BC-004')),
-- CMD-0016
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-015'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '10 days' + TIME '12:35', CURRENT_DATE - INTERVAL '10 days' + TIME '12:30', NULL, 40.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260608-0016'), (SELECT id_producto_final FROM producto_final WHERE codigo='HAM-003')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-015'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '10 days' + TIME '12:35', CURRENT_DATE - INTERVAL '10 days' + TIME '12:30', NULL, 38.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260608-0016'), (SELECT id_producto_final FROM producto_final WHERE codigo='COM-003')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-015'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '10 days' + TIME '12:33', CURRENT_DATE - INTERVAL '10 days' + TIME '12:30', NULL, 15.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260608-0016'), (SELECT id_producto_final FROM producto_final WHERE codigo='BS-001')),
-- CMD-0017
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-024'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '8 days' + TIME '13:05', CURRENT_DATE - INTERVAL '8 days' + TIME '13:00', NULL, 48.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260609-0017'), (SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-002')),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-024'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '8 days' + TIME '13:05', CURRENT_DATE - INTERVAL '8 days' + TIME '13:00', NULL, 35.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260609-0017'), (SELECT id_producto_final FROM producto_final WHERE codigo='EP-001')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-024'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '8 days' + TIME '13:03', CURRENT_DATE - INTERVAL '8 days' + TIME '13:00', NULL, 12.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260609-0017'), (SELECT id_producto_final FROM producto_final WHERE codigo='BA-002')),
-- CMD-0018
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-006'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '5 days' + TIME '18:35', CURRENT_DATE - INTERVAL '5 days' + TIME '18:30', NULL, 36.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260610-0018'), (SELECT id_producto_final FROM producto_final WHERE codigo='HAM-002')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-006'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '5 days' + TIME '18:35', CURRENT_DATE - INTERVAL '5 days' + TIME '18:30', NULL, 42.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260610-0018'), (SELECT id_producto_final FROM producto_final WHERE codigo='ENT-004')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-006'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '5 days' + TIME '18:33', CURRENT_DATE - INTERVAL '5 days' + TIME '18:30', NULL, 18.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260610-0018'), (SELECT id_producto_final FROM producto_final WHERE codigo='BC-001')),
-- CMD-0019
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-034'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '4 days' + TIME '12:05', CURRENT_DATE - INTERVAL '4 days' + TIME '12:00', NULL, 45.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260611-0019'), (SELECT id_producto_final FROM producto_final WHERE codigo='COM-003')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-034'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '4 days' + TIME '12:05', CURRENT_DATE - INTERVAL '4 days' + TIME '12:00', NULL, 20.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260611-0019'), (SELECT id_producto_final FROM producto_final WHERE codigo='SNK-001')),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-034'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '4 days' + TIME '12:03', CURRENT_DATE - INTERVAL '4 days' + TIME '12:00', NULL, 15.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260611-0019'), (SELECT id_producto_final FROM producto_final WHERE codigo='BS-001')),
-- CMD-0020
(3, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-016'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '3 days' + TIME '19:05', CURRENT_DATE - INTERVAL '3 days' + TIME '19:00', NULL, 28.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260615-0020'), (SELECT id_producto_final FROM producto_final WHERE codigo='ENT-003')),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-016'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '3 days' + TIME '19:05', CURRENT_DATE - INTERVAL '3 days' + TIME '19:00', NULL, 42.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260615-0020'), (SELECT id_producto_final FROM producto_final WHERE codigo='PAS-003')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-016'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '3 days' + TIME '19:03', CURRENT_DATE - INTERVAL '3 days' + TIME '19:00', NULL, 25.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260615-0020'), (SELECT id_producto_final FROM producto_final WHERE codigo='BA-003')),
-- CMD-0021
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-025'), 'COCINA', 'EN_PREPARACION', CURRENT_DATE - INTERVAL '2 days' + TIME '13:05', CURRENT_DATE - INTERVAL '2 days' + TIME '13:00', 'Sin picante', 35.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260617-0021'), (SELECT id_producto_final FROM producto_final WHERE codigo='ENT-001')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-025'), 'COCINA', 'EN_PREPARACION', CURRENT_DATE - INTERVAL '2 days' + TIME '13:05', CURRENT_DATE - INTERVAL '2 days' + TIME '13:00', NULL, 65.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260617-0021'), (SELECT id_producto_final FROM producto_final WHERE codigo='CAR-003')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-025'), 'BARRA', 'PENDIENTE', NULL, CURRENT_DATE - INTERVAL '2 days' + TIME '13:00', NULL, 30.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260617-0021'), (SELECT id_producto_final FROM producto_final WHERE codigo='BA-004')),
-- CMD-0022
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-007'), 'COCINA', 'LISTO', CURRENT_DATE - INTERVAL '1 day' + TIME '18:05', CURRENT_DATE - INTERVAL '1 day' + TIME '18:00', NULL, 78.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260620-0022'), (SELECT id_producto_final FROM producto_final WHERE codigo='CAR-004')),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-007'), 'COCINA', 'LISTO', CURRENT_DATE - INTERVAL '1 day' + TIME '18:05', CURRENT_DATE - INTERVAL '1 day' + TIME '18:00', NULL, 48.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260620-0022'), (SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-002')),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-007'), 'BARRA', 'PENDIENTE', NULL, CURRENT_DATE - INTERVAL '1 day' + TIME '18:00', NULL, 12.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260620-0022'), (SELECT id_producto_final FROM producto_final WHERE codigo='BA-001')),
-- CMD-0023
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-035'), 'COCINA', 'PENDIENTE', NULL, CURRENT_DATE + TIME '12:00', 'Para llevar', 35.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260621-0023'), (SELECT id_producto_final FROM producto_final WHERE codigo='COM-004')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-035'), 'COCINA', 'PENDIENTE', NULL, CURRENT_DATE + TIME '12:00', NULL, 18.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260621-0023'), (SELECT id_producto_final FROM producto_final WHERE codigo='SNK-001')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-035'), 'BARRA', 'PENDIENTE', NULL, CURRENT_DATE + TIME '12:00', NULL, 15.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260621-0023'), (SELECT id_producto_final FROM producto_final WHERE codigo='BS-001')),
-- CMD-0024 items (ONLINE)
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-008'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '20 days' + TIME '14:05', CURRENT_DATE - INTERVAL '20 days' + TIME '14:00', 'Delivery', 42.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260601-0024'), (SELECT id_producto_final FROM producto_final WHERE codigo='ENT-002')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-008'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '20 days' + TIME '14:05', CURRENT_DATE - INTERVAL '20 days' + TIME '14:00', NULL, 38.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260601-0024'), (SELECT id_producto_final FROM producto_final WHERE codigo='HAM-001')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-008'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '20 days' + TIME '14:03', CURRENT_DATE - INTERVAL '20 days' + TIME '14:00', NULL, 8.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260601-0024'), (SELECT id_producto_final FROM producto_final WHERE codigo='BS-003')),
-- CMD-0025
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-017'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '18 days' + TIME '19:05', CURRENT_DATE - INTERVAL '18 days' + TIME '19:00', NULL, 32.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260603-0025'), (SELECT id_producto_final FROM producto_final WHERE codigo='ENS-001')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-017'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '18 days' + TIME '19:05', CURRENT_DATE - INTERVAL '18 days' + TIME '19:00', NULL, 40.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260603-0025'), (SELECT id_producto_final FROM producto_final WHERE codigo='HAM-003')),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-017'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '18 days' + TIME '19:03', CURRENT_DATE - INTERVAL '18 days' + TIME '19:00', NULL, 15.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260603-0025'), (SELECT id_producto_final FROM producto_final WHERE codigo='BS-002')),
-- CMD-0026
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-026'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '15 days' + TIME '12:05', CURRENT_DATE - INTERVAL '15 days' + TIME '12:00', NULL, 95.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260605-0026'), (SELECT id_producto_final FROM producto_final WHERE codigo='COM-001')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-026'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '15 days' + TIME '12:05', CURRENT_DATE - INTERVAL '15 days' + TIME '12:00', NULL, 28.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260605-0026'), (SELECT id_producto_final FROM producto_final WHERE codigo='ENT-003')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-026'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '15 days' + TIME '12:03', CURRENT_DATE - INTERVAL '15 days' + TIME '12:00', NULL, 12.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260605-0026'), (SELECT id_producto_final FROM producto_final WHERE codigo='BA-001')),
-- CMD-0027
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-036'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '10 days' + TIME '20:05', CURRENT_DATE - INTERVAL '10 days' + TIME '20:00', NULL, 45.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260608-0027'), (SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-001')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-036'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '10 days' + TIME '20:05', CURRENT_DATE - INTERVAL '10 days' + TIME '20:00', NULL, 22.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260608-0027'), (SELECT id_producto_final FROM producto_final WHERE codigo='POS-004')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-036'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '10 days' + TIME '20:03', CURRENT_DATE - INTERVAL '10 days' + TIME '20:00', NULL, 6.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260608-0027'), (SELECT id_producto_final FROM producto_final WHERE codigo='BA-002')),
-- CMD-0028
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-009'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '4 days' + TIME '18:05', CURRENT_DATE - INTERVAL '4 days' + TIME '18:00', NULL, 35.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260612-0028'), (SELECT id_producto_final FROM producto_final WHERE codigo='COM-003')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-009'), 'COCINA', 'ENTREGADO', CURRENT_DATE - INTERVAL '4 days' + TIME '18:05', CURRENT_DATE - INTERVAL '4 days' + TIME '18:00', NULL, 18.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260612-0028'), (SELECT id_producto_final FROM producto_final WHERE codigo='POS-001')),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-009'), 'BARRA', 'ENTREGADO', CURRENT_DATE - INTERVAL '4 days' + TIME '18:03', CURRENT_DATE - INTERVAL '4 days' + TIME '18:00', NULL, 14.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260612-0028'), (SELECT id_producto_final FROM producto_final WHERE codigo='BC-003')),
-- CMD-0029
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-018'), 'COCINA', 'EN_PREPARACION', CURRENT_DATE - INTERVAL '2 days' + TIME '19:05', CURRENT_DATE - INTERVAL '2 days' + TIME '19:00', NULL, 120.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260618-0029'), (SELECT id_producto_final FROM producto_final WHERE codigo='CAR-002')),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-018'), 'COCINA', 'EN_PREPARACION', CURRENT_DATE - INTERVAL '2 days' + TIME '19:05', CURRENT_DATE - INTERVAL '2 days' + TIME '19:00', NULL, 22.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260618-0029'), (SELECT id_producto_final FROM producto_final WHERE codigo='EP-002')),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-018'), 'BARRA', 'PENDIENTE', NULL, CURRENT_DATE - INTERVAL '2 days' + TIME '19:00', NULL, 30.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260618-0029'), (SELECT id_producto_final FROM producto_final WHERE codigo='BA-004')),
-- CMD-0030
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-027'), 'COCINA', 'PENDIENTE', NULL, CURRENT_DATE + TIME '14:00', NULL, 46.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260622-0030'), (SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-004')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-027'), 'COCINA', 'PENDIENTE', NULL, CURRENT_DATE + TIME '14:00', NULL, 42.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260622-0030'), (SELECT id_producto_final FROM producto_final WHERE codigo='PAS-001')),
(1, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-027'), 'BARRA', 'PENDIENTE', NULL, CURRENT_DATE + TIME '14:00', NULL, 18.00, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260622-0030'), (SELECT id_producto_final FROM producto_final WHERE codigo='BC-002'))
) AS v(cantidad, empleado_asignado, estacion_preparacion, estado, fecha_aceptacion, fecha_creacion, notas, precio_unitario, id_comanda, id_producto_final);

-- ============================================================
-- 20. CAJAS - 2 per sucursal (1 open, 1 closed)
-- ============================================================

INSERT INTO caja (diferencia, estado, fecha_apertura, fecha_cierre, monto_final, monto_inicial, observacion_apertura, observacion_cierre, saldo_esperado, id_empleado_apertura, id_empleado_cierre, id_sucursal)
SELECT v.* FROM (VALUES
-- Sucursal 2
(0.00, 'CERRADA', CURRENT_DATE - INTERVAL '7 days' + TIME '09:00', CURRENT_DATE - INTERVAL '7 days' + TIME '23:00', 4520.00, 500.00, 'Apertura normal', 'Cierre sin novedad', 4520.00, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-001'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-002'), 2),
(0.00, 'ABIERTA', CURRENT_DATE + TIME '09:00', NULL, NULL, 500.00, 'Apertura del dia', NULL, NULL, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-003'), NULL, 2),
-- Sucursal 3
(15.50, 'CERRADA', CURRENT_DATE - INTERVAL '7 days' + TIME '09:00', CURRENT_DATE - INTERVAL '7 days' + TIME '23:00', 5100.00, 500.00, 'Apertura normal', 'Sobrante de 15.50', 5084.50, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-011'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-012'), 3),
(0.00, 'ABIERTA', CURRENT_DATE + TIME '09:00', NULL, NULL, 500.00, 'Apertura del dia', NULL, NULL, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-013'), NULL, 3),
-- Sucursal 4
(-22.00, 'CERRADA', CURRENT_DATE - INTERVAL '7 days' + TIME '09:00', CURRENT_DATE - INTERVAL '7 days' + TIME '23:00', 3800.00, 500.00, 'Apertura normal', 'Faltante de 22.00', 3822.00, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-021'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-022'), 4),
(0.00, 'ABIERTA', CURRENT_DATE + TIME '09:00', NULL, NULL, 500.00, 'Apertura del dia', NULL, NULL, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-023'), NULL, 4),
-- Sucursal 5
(5.00, 'CERRADA', CURRENT_DATE - INTERVAL '7 days' + TIME '09:00', CURRENT_DATE - INTERVAL '7 days' + TIME '23:00', 4200.00, 500.00, 'Apertura normal', 'Sobrante de 5.00', 4195.00, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-031'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-032'), 5),
(0.00, 'ABIERTA', CURRENT_DATE + TIME '09:00', NULL, NULL, 500.00, 'Apertura del dia', NULL, NULL, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-033'), NULL, 5),
-- Sucursal 1
(-8.50, 'CERRADA', CURRENT_DATE - INTERVAL '7 days' + TIME '09:00', CURRENT_DATE - INTERVAL '7 days' + TIME '23:00', 3200.00, 500.00, 'Apertura normal', 'Faltante de 8.50', 3208.50, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-001'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-002'), 1),
(0.00, 'ABIERTA', CURRENT_DATE + TIME '09:00', NULL, NULL, 500.00, 'Apertura del dia', NULL, NULL, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-003'), NULL, 1)
) AS v(diferencia, estado, fecha_apertura, fecha_cierre, monto_final, monto_inicial, observacion_apertura, observacion_cierre, saldo_esperado, id_empleado_apertura, id_empleado_cierre, id_sucursal);

-- ============================================================
-- 21. NOTAS_DE_VENTA - 25 linked to comandas CERRADA/ENTREGADA
-- ============================================================

INSERT INTO nota_venta (sub_total, descuento, estado, fecha_emision, fecha_pago, impuesto, nit, observaciones, propina, total, id_cliente, id_comanda, id_empleado, id_metodo_pago, id_sucursal)
SELECT v.* FROM (VALUES
(128.00, 0.00, 'PAGADA', CURRENT_DATE - INTERVAL '20 days' + TIME '21:05', CURRENT_DATE - INTERVAL '20 days' + TIME '21:10', 17.36, '1234567', NULL, 20.00, 165.36, (SELECT id_cliente FROM cliente WHERE nit='1234567'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260601-0001'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-001'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='Efectivo'), 2),
(275.00, 0.00, 'PAGADA', CURRENT_DATE - INTERVAL '19 days' + TIME '22:05', CURRENT_DATE - INTERVAL '19 days' + TIME '22:10', 38.50, '2345678', NULL, 30.00, 343.50, (SELECT id_cliente FROM cliente WHERE nit='2345678'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260602-0002'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-011'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='Tarjeta Débito'), 3),
(109.00, 0.00, 'PAGADA', CURRENT_DATE - INTERVAL '18 days' + TIME '20:35', CURRENT_DATE - INTERVAL '18 days' + TIME '20:40', 15.26, '3456789', NULL, 15.00, 139.26, (SELECT id_cliente FROM cliente WHERE nit='3456789'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260603-0003'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-021'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='QR Pago Móvil'), 4),
(318.00, 20.00, 'PAGADA', CURRENT_DATE - INTERVAL '17 days' + TIME '21:05', CURRENT_DATE - INTERVAL '17 days' + TIME '21:10', 44.52, '4567890', 'Descuento por evento', 50.00, 392.52, (SELECT id_cliente FROM cliente WHERE nit='4567890'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260604-0004'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-031'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='Efectivo'), 5),
(196.00, 0.00, 'PAGADA', CURRENT_DATE - INTERVAL '15 days' + TIME '22:05', CURRENT_DATE - INTERVAL '15 days' + TIME '22:10', 27.44, '5678901', NULL, 25.00, 248.44, (SELECT id_cliente FROM cliente WHERE nit='5678901'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260605-0005'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-002'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='Tarjeta Débito'), 2),
(170.00, 0.00, 'PAGADA', CURRENT_DATE - INTERVAL '14 days' + TIME '21:35', CURRENT_DATE - INTERVAL '14 days' + TIME '21:40', 23.80, '6789012', NULL, 20.00, 213.80, (SELECT id_cliente FROM cliente WHERE nit='6789012'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260606-0006'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-012'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='Efectivo'), 3),
(212.00, 0.00, 'PAGADA', CURRENT_DATE - INTERVAL '12 days' + TIME '20:05', CURRENT_DATE - INTERVAL '12 days' + TIME '20:10', 29.68, '7890123', NULL, 30.00, 271.68, (SELECT id_cliente FROM cliente WHERE nit='7890123'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260607-0007'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-022'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='Tarjeta Débito'), 4),
(76.00, 0.00, 'PAGADA', CURRENT_DATE - INTERVAL '10 days' + TIME '15:05', CURRENT_DATE - INTERVAL '10 days' + TIME '15:10', 10.64, '8901234', NULL, 10.00, 96.64, (SELECT id_cliente FROM cliente WHERE nit='8901234'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260608-0008'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-003'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='QR Pago Móvil'), 2),
(148.00, 0.00, 'PAGADA', CURRENT_DATE - INTERVAL '8 days' + TIME '21:05', CURRENT_DATE - INTERVAL '8 days' + TIME '21:10', 20.72, '9012345', NULL, 20.00, 188.72, (SELECT id_cliente FROM cliente WHERE nit='9012345'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260609-0009'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-032'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='Efectivo'), 5),
(108.00, 0.00, 'PAGADA', CURRENT_DATE - INTERVAL '5 days' + TIME '22:05', CURRENT_DATE - INTERVAL '5 days' + TIME '22:10', 15.12, '0123456', NULL, 15.00, 138.12, (SELECT id_cliente FROM cliente WHERE nit='0123456'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260610-0010'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-013'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='Tarjeta Débito'), 3),
(98.00, 0.00, 'PAGADA', CURRENT_DATE - INTERVAL '20 days' + TIME '13:05', CURRENT_DATE - INTERVAL '20 days' + TIME '13:10', 13.72, '1234567', NULL, 10.00, 121.72, (SELECT id_cliente FROM cliente WHERE nit='1234567'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260601-0011'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-004'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='Efectivo'), 2),
(79.00, 0.00, 'PAGADA', CURRENT_DATE - INTERVAL '19 days' + TIME '13:35', CURRENT_DATE - INTERVAL '19 days' + TIME '13:40', 11.06, '2345678', NULL, 10.00, 100.06, (SELECT id_cliente FROM cliente WHERE nit='2345678'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260602-0012'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-014'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='QR Pago Móvil'), 3),
(90.00, 0.00, 'PAGADA', CURRENT_DATE - INTERVAL '18 days' + TIME '12:35', CURRENT_DATE - INTERVAL '18 days' + TIME '12:40', 12.60, '3456789', NULL, 10.00, 112.60, (SELECT id_cliente FROM cliente WHERE nit='3456789'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260603-0013'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-023'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='Efectivo'), 4),
(271.00, 0.00, 'PAGADA', CURRENT_DATE - INTERVAL '15 days' + TIME '18:35', CURRENT_DATE - INTERVAL '15 days' + TIME '18:40', 37.94, '5678901', NULL, 30.00, 338.94, (SELECT id_cliente FROM cliente WHERE nit='5678901'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260605-0014'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-005'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='Tarjeta Débito'), 2),
(98.00, 0.00, 'PAGADA', CURRENT_DATE - INTERVAL '12 days' + TIME '20:05', CURRENT_DATE - INTERVAL '12 days' + TIME '20:10', 13.72, '7890123', NULL, 10.00, 121.72, (SELECT id_cliente FROM cliente WHERE nit='7890123'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260607-0015'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-033'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='Efectivo'), 5),
(121.00, 0.00, 'PAGADA', CURRENT_DATE - INTERVAL '10 days' + TIME '13:05', CURRENT_DATE - INTERVAL '10 days' + TIME '13:10', 16.94, '8901234', NULL, 15.00, 152.94, (SELECT id_cliente FROM cliente WHERE nit='8901234'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260608-0016'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-015'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='QR Pago Móvil'), 3),
(115.00, 0.00, 'PAGADA', CURRENT_DATE - INTERVAL '8 days' + TIME '13:35', CURRENT_DATE - INTERVAL '8 days' + TIME '13:40', 16.10, '9012345', NULL, 15.00, 146.10, (SELECT id_cliente FROM cliente WHERE nit='9012345'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260609-0017'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-024'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='Efectivo'), 4),
(88.00, 0.00, 'PAGADA', CURRENT_DATE - INTERVAL '5 days' + TIME '19:05', CURRENT_DATE - INTERVAL '5 days' + TIME '19:10', 12.32, '0123456', NULL, 10.00, 110.32, (SELECT id_cliente FROM cliente WHERE nit='0123456'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260610-0018'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-006'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='Tarjeta Débito'), 2),
(137.00, 0.00, 'PAGADA', CURRENT_DATE - INTERVAL '4 days' + TIME '12:35', CURRENT_DATE - INTERVAL '4 days' + TIME '12:40', 19.18, '1112233', NULL, 15.00, 171.18, (SELECT id_cliente FROM cliente WHERE nit='1112233'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260611-0019'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-034'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='Efectivo'), 5),
(172.00, 0.00, 'EMITIDA', CURRENT_DATE - INTERVAL '2 days' + TIME '13:35', NULL, 24.08, '3334455', NULL, 20.00, 216.08, (SELECT id_cliente FROM cliente WHERE nit='3334455'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260617-0021'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-025'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='Tarjeta Débito'), 4),
(138.00, 0.00, 'EMITIDA', CURRENT_DATE - INTERVAL '1 day' + TIME '18:35', NULL, 19.32, '4445566', NULL, 15.00, 172.32, (SELECT id_cliente FROM cliente WHERE nit='4445566'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260620-0022'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-007'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='Efectivo'), 2),
(50.00, 0.00, 'EMITIDA', CURRENT_DATE + TIME '12:35', NULL, 7.00, '5556677', NULL, 5.00, 62.00, (SELECT id_cliente FROM cliente WHERE nit='5556677'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260621-0023'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-035'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='QR Pago Móvil'), 5),
(88.00, 0.00, 'PAGADA', CURRENT_DATE - INTERVAL '20 days' + TIME '15:05', CURRENT_DATE - INTERVAL '20 days' + TIME '15:10', 12.32, '6789012', NULL, 10.00, 110.32, (SELECT id_cliente FROM cliente WHERE nit='6789012'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260601-0024'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-008'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='Efectivo'), 2),
(112.00, 0.00, 'PAGADA', CURRENT_DATE - INTERVAL '18 days' + TIME '20:05', CURRENT_DATE - INTERVAL '18 days' + TIME '20:10', 15.68, '7890123', NULL, 15.00, 142.68, (SELECT id_cliente FROM cliente WHERE nit='7890123'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260603-0025'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-017'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='Tarjeta Débito'), 3),
(130.00, 0.00, 'PAGADA', CURRENT_DATE - INTERVAL '15 days' + TIME '12:55', CURRENT_DATE - INTERVAL '15 days' + TIME '13:00', 18.20, '8901234', NULL, 17.16, 165.36, (SELECT id_cliente FROM cliente WHERE nit='8901234'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260605-0026'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-026'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='Efectivo'), 4),
(120.00, 0.00, 'PAGADA', CURRENT_DATE - INTERVAL '10 days' + TIME '20:55', CURRENT_DATE - INTERVAL '10 days' + TIME '21:00', 16.80, '9012345', NULL, 16.14, 152.94, (SELECT id_cliente FROM cliente WHERE nit='9012345'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260608-0027'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-036'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='Tarjeta Débito'), 5),
(115.00, 0.00, 'PAGADA', CURRENT_DATE - INTERVAL '4 days' + TIME '18:55', CURRENT_DATE - INTERVAL '4 days' + TIME '19:00', 16.10, '0123456', NULL, 15.00, 146.10, (SELECT id_cliente FROM cliente WHERE nit='0123456'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260612-0028'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-009'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='Efectivo'), 2),
(88.00, 0.00, 'EMITIDA', CURRENT_DATE - INTERVAL '2 days' + TIME '19:55', NULL, 12.32, '1112233', NULL, 10.00, 110.32, (SELECT id_cliente FROM cliente WHERE nit='1112233'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260618-0029'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-018'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='QR Pago Móvil'), 3),
(125.00, 0.00, 'EMITIDA', CURRENT_DATE + TIME '14:55', NULL, 17.50, '2223344', NULL, 17.30, 159.80, (SELECT id_cliente FROM cliente WHERE nit='2223344'), (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260622-0030'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-027'), (SELECT id_metodo_pago FROM metodo_pago WHERE nombre='Efectivo'), 4)
) AS v(sub_total, descuento, estado, fecha_emision, fecha_pago, impuesto, nit, observaciones, propina, total, id_cliente, id_comanda, id_empleado, id_metodo_pago, id_sucursal)
WHERE NOT EXISTS (SELECT 1 FROM nota_venta nv WHERE nv.id_comanda = v.id_comanda);

-- ============================================================
-- 22. DETALLE_NOTA_VENTA - 75 items (3 per nota_venta)
-- ============================================================

INSERT INTO detalle_nota_venta (cantidad, costo_u, descuento, descripcion, precio_u, subtotal, id_nota_venta, producto_final)
SELECT v.* FROM (VALUES
-- NV for CMD-0001
(2, 12.00, 0.00, 'Nachos Supreme', 35.00, 70.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260601-0001')), (SELECT id_producto_final FROM producto_final WHERE codigo='ENT-001')),
(1, 18.00, 0.00, 'Hamburguesa Clasica', 38.00, 38.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260601-0001')), (SELECT id_producto_final FROM producto_final WHERE codigo='HAM-001')),
(2, 5.00, 0.00, 'Cerveza Cristal', 12.00, 24.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260601-0001')), (SELECT id_producto_final FROM producto_final WHERE codigo='BA-001')),
-- NV for CMD-0002
(1, 65.00, 0.00, 'Parrillada', 120.00, 120.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260602-0002')), (SELECT id_producto_final FROM producto_final WHERE codigo='CAR-002')),
(2, 15.00, 0.00, 'Pizza Margarita', 45.00, 90.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260602-0002')), (SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-001')),
(3, 4.00, 0.00, 'Cerveza Paceña', 10.00, 30.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260602-0002')), (SELECT id_producto_final FROM producto_final WHERE codigo='BA-002')),
-- NV for CMD-0003
(2, 10.00, 0.00, 'Ensalada Cesar', 32.00, 64.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260603-0003')), (SELECT id_producto_final FROM producto_final WHERE codigo='ENS-001')),
(1, 14.00, 0.00, 'Alitas BBQ', 42.00, 42.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260603-0003')), (SELECT id_producto_final FROM producto_final WHERE codigo='ENT-002')),
(2, 4.00, 0.00, 'Limonada', 15.00, 30.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260603-0003')), (SELECT id_producto_final FROM producto_final WHERE codigo='BS-002')),
-- NV for CMD-0004
(3, 20.00, 0.00, 'Pizza Cuatro Quesos', 48.00, 144.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260604-0004')), (SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-003')),
(2, 35.00, 0.00, 'Bife de Chorizo', 65.00, 130.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260604-0004')), (SELECT id_producto_final FROM producto_final WHERE codigo='CAR-003')),
(4, 5.00, 0.00, 'Cerveza Cristal', 12.00, 48.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260604-0004')), (SELECT id_producto_final FROM producto_final WHERE codigo='BA-001')),
-- NV for CMD-0005
(1, 45.00, 0.00, 'Lomo al Trapo', 85.00, 85.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260605-0005')), (SELECT id_producto_final FROM producto_final WHERE codigo='CAR-001')),
(2, 8.00, 0.00, 'Tequenos', 28.00, 56.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260605-0005')), (SELECT id_producto_final FROM producto_final WHERE codigo='ENT-003')),
(2, 10.00, 0.00, 'Fernet con Cola', 25.00, 50.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260605-0005')), (SELECT id_producto_final FROM producto_final WHERE codigo='BA-003')),
-- NV for CMD-0006
(2, 12.00, 0.00, 'Spaghetti Bolognesa', 42.00, 84.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260606-0006')), (SELECT id_producto_final FROM producto_final WHERE codigo='PAS-001')),
(1, 8.00, 0.00, 'Tiramisu', 22.00, 22.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260606-0006')), (SELECT id_producto_final FROM producto_final WHERE codigo='POS-001')),
(2, 12.00, 0.00, 'Pisco Sour', 30.00, 60.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260606-0006')), (SELECT id_producto_final FROM producto_final WHERE codigo='BA-004')),
-- NV for CMD-0007
(2, 18.00, 0.00, 'Hamburguesa Clasica', 38.00, 76.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260607-0007')), (SELECT id_producto_final FROM producto_final WHERE codigo='HAM-001')),
(1, 28.00, 0.00, 'Costillas BBQ', 78.00, 78.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260607-0007')), (SELECT id_producto_final FROM producto_final WHERE codigo='CAR-004')),
(3, 5.00, 0.00, 'Cerveza Cristal', 12.00, 36.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260607-0007')), (SELECT id_producto_final FROM producto_final WHERE codigo='BA-001')),
-- NV for CMD-0008
(1, 10.00, 0.00, 'Ensalada Tropical', 25.00, 25.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260608-0008')), (SELECT id_producto_final FROM producto_final WHERE codigo='ENS-003')),
(1, 16.00, 0.00, 'Hamburguesa Vegana', 36.00, 36.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260608-0008')), (SELECT id_producto_final FROM producto_final WHERE codigo='HAM-002')),
(1, 5.00, 0.00, 'Cafe con Leche', 15.00, 15.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260608-0008')), (SELECT id_producto_final FROM producto_final WHERE codigo='BC-002')),
-- NV for CMD-0009
(2, 22.00, 0.00, 'Pizza Cuatro Quesos', 52.00, 104.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260609-0009')), (SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-003')),
(1, 8.00, 0.00, 'Papas Fritas', 18.00, 18.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260609-0009')), (SELECT id_producto_final FROM producto_final WHERE codigo='SNK-001')),
(3, 4.00, 0.00, 'Sprite', 8.00, 24.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260609-0009')), (SELECT id_producto_final FROM producto_final WHERE codigo='BS-003')),
-- NV for CMD-0010
(1, 16.00, 0.00, 'Pizza Hawaiana', 46.00, 46.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260610-0010')), (SELECT id_producto_final FROM producto_final WHERE codigo='PIZ-004')),
(2, 8.00, 0.00, 'Tequenos', 28.00, 56.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260610-0010')), (SELECT id_producto_final FROM producto_final WHERE codigo='ENT-003')),
(2, 6.00, 0.00, 'Chocolate Caliente', 14.00, 28.00, (SELECT id_nota_venta FROM nota_venta WHERE id_comanda=(SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260610-0010')), (SELECT id_producto_final FROM producto_final WHERE codigo='BC-003'))
) AS v(cantidad, costo_u, descuento, descripcion, precio_u, subtotal, id_nota_venta, producto_final);

-- ============================================================
-- 23. COMPRAS - 12
-- ============================================================

INSERT INTO compra (nro_factura, id_proveedor, fecha_compra, sub_total, descuento, impuesto, total, estado_pago, observaciones, id_empleado)
SELECT v.* FROM (VALUES
('FAC-20260501-001', (SELECT id_proveedor FROM proveedor WHERE nit='456789028'), CURRENT_DATE - INTERVAL '30 days', 1200.00, 0.00, 168.00, 1368.00, 'PAGADO', 'Compra de verduras quincenal', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-001')),
('FAC-20260508-002', (SELECT id_proveedor FROM proveedor WHERE nit='567890123'), CURRENT_DATE - INTERVAL '23 days', 2500.00, 100.00, 336.00, 2736.00, 'PAGADO', 'Compra de carnes premium', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-011')),
('FAC-20260515-003', (SELECT id_proveedor FROM proveedor WHERE nit='678901234'), CURRENT_DATE - INTERVAL '16 days', 800.00, 0.00, 112.00, 912.00, 'PAGADO', 'Verduras frescas semanales', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-021')),
('FAC-20260518-004', (SELECT id_proveedor FROM proveedor WHERE nit='789012345'), CURRENT_DATE - INTERVAL '13 days', 1500.00, 0.00, 210.00, 1710.00, 'PAGADO', 'Lacteos y quesos', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-031')),
('FAC-20260520-005', (SELECT id_proveedor FROM proveedor WHERE nit='890123456'), CURRENT_DATE - INTERVAL '11 days', 950.00, 50.00, 126.00, 1026.00, 'PAGADO', 'Pan artesanal y masa', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-001')),
('FAC-20260522-006', (SELECT id_proveedor FROM proveedor WHERE nit='901234567'), CURRENT_DATE - INTERVAL '9 days', 1800.00, 0.00, 252.00, 2052.00, 'PAGADO', 'Helados y postres', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-011')),
('FAC-20260524-007', (SELECT id_proveedor FROM proveedor WHERE nit='456789028'), CURRENT_DATE - INTERVAL '7 days', 1100.00, 0.00, 154.00, 1254.00, 'PAGADO', 'Verduras y frutas', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-021')),
('FAC-20260526-008', (SELECT id_proveedor FROM proveedor WHERE nit='567890123'), CURRENT_DATE - INTERVAL '5 days', 3200.00, 200.00, 420.00, 3420.00, 'PARCIAL', 'Compra grande de carnes', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-031')),
('FAC-20260528-009', (SELECT id_proveedor FROM proveedor WHERE nit='789012345'), CURRENT_DATE - INTERVAL '3 days', 600.00, 0.00, 84.00, 684.00, 'PAGADO', 'Lacteos frescos', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-001')),
('FAC-20260530-010', (SELECT id_proveedor FROM proveedor WHERE nit='890123456'), CURRENT_DATE - INTERVAL '1 day', 450.00, 0.00, 63.00, 513.00, 'PENDIENTE', 'Pan y masa para pizza', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-011')),
('FAC-20260601-011', (SELECT id_proveedor FROM proveedor WHERE nit='456789028'), CURRENT_DATE, 750.00, 0.00, 105.00, 855.00, 'PENDIENTE', 'Verduras del dia', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-021')),
('FAC-20260601-012', (SELECT id_proveedor FROM proveedor WHERE nit='012345678'), CURRENT_DATE, 300.00, 0.00, 42.00, 342.00, 'PENDIENTE', 'Productos de limpieza', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-031'))
) AS v(nro_factura, id_proveedor, fecha_compra, sub_total, descuento, impuesto, total, estado_pago, observaciones, id_empleado)
WHERE NOT EXISTS (SELECT 1 FROM compra c WHERE c.nro_factura = v.nro_factura);

-- ============================================================
-- 24. DETALLE_COMPRA - 36 (3 per compra)
-- ============================================================

INSERT INTO detalle_compra (cantidad, precio_unitario, sub_total, id_compra, id_stock)
SELECT v.* FROM (VALUES
-- Compra 1
(10.000, 8.50, 85.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260501-001'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-004' AND ss.id_sucursal=2)),
(8.000, 6.50, 52.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260501-001'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-005' AND ss.id_sucursal=2)),
(5.000, 5.50, 27.50, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260501-001'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-006' AND ss.id_sucursal=2)),
-- Compra 2
(15.000, 28.00, 420.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260508-002'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-018' AND ss.id_sucursal=3)),
(5.000, 85.00, 425.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260508-002'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-019' AND ss.id_sucursal=3)),
(10.000, 42.00, 420.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260508-002'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-020' AND ss.id_sucursal=3)),
-- Compra 3
(12.000, 8.50, 102.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260515-003'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-004' AND ss.id_sucursal=4)),
(6.000, 7.00, 42.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260515-003'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-061' AND ss.id_sucursal=4)),
(8.000, 5.50, 44.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260515-003'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-062' AND ss.id_sucursal=4)),
-- Compra 4
(10.000, 42.00, 420.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260518-004'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-025' AND ss.id_sucursal=5)),
(8.000, 48.00, 384.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260518-004'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-026' AND ss.id_sucursal=5)),
(10.000, 4.50, 45.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260518-004'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-028' AND ss.id_sucursal=5)),
-- Compra 5
(20.000, 14.00, 280.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260520-005'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-032' AND ss.id_sucursal=2)),
(5.000, 8.00, 40.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260520-005'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-036' AND ss.id_sucursal=2)),
(10.000, 6.50, 65.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260520-005'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-035' AND ss.id_sucursal=2)),
-- Compra 6
(20.000, 38.00, 760.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260522-006'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-028' AND ss.id_sucursal=3)),
(10.000, 35.00, 350.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260522-006'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-027' AND ss.id_sucursal=3)),
(15.000, 9.00, 135.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260522-006'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-031' AND ss.id_sucursal=3)),
-- Compra 7
(15.000, 8.50, 127.50, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260524-007'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-004' AND ss.id_sucursal=4)),
(8.000, 10.00, 80.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260524-007'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-014' AND ss.id_sucursal=4)),
(5.000, 15.00, 75.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260524-007'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-015' AND ss.id_sucursal=4)),
-- Compra 8
(20.000, 28.00, 560.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260526-008'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-018' AND ss.id_sucursal=5)),
(8.000, 85.00, 680.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260526-008'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-019' AND ss.id_sucursal=5)),
(10.000, 33.00, 330.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260526-008'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-021' AND ss.id_sucursal=5)),
-- Compra 9
(10.000, 4.50, 45.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260528-009'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-028' AND ss.id_sucursal=2)),
(8.000, 35.00, 280.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260528-009'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-030' AND ss.id_sucursal=2)),
(5.000, 22.00, 110.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260528-009'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-029' AND ss.id_sucursal=2)),
-- Compra 10
(15.000, 14.00, 210.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260530-010'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-032' AND ss.id_sucursal=3)),
(5.000, 8.00, 40.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260530-010'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-036' AND ss.id_sucursal=3)),
(8.000, 6.50, 52.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260530-010'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-035' AND ss.id_sucursal=3)),
-- Compra 11
(10.000, 8.50, 85.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260601-011'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-004' AND ss.id_sucursal=4)),
(5.000, 7.00, 35.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260601-011'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-061' AND ss.id_sucursal=4)),
(6.000, 5.50, 33.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260601-011'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-062' AND ss.id_sucursal=4)),
-- Compra 12
(10.000, 6.00, 60.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260601-012'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-077' AND ss.id_sucursal=5)),
(8.000, 7.00, 56.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260601-012'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-078' AND ss.id_sucursal=5)),
(5.000, 5.00, 25.00, (SELECT id_compra FROM compra WHERE nro_factura='FAC-20260601-012'), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-075' AND ss.id_sucursal=5))
) AS v(cantidad, precio_unitario, sub_total, id_compra, id_stock);

-- ============================================================
-- 25. NOTAS_DE_SALIDA - 15
-- ============================================================

INSERT INTO nota_salida (id_sucursal, id_empleado, fecha, descripcion, monto_total, tipo_gasto, estado, observaciones)
SELECT v.* FROM (VALUES
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-001'), CURRENT_DATE - INTERVAL '25 days' + TIME '10:00', 'Pago servicio de electricidad mayo', 350.00, 'SERVICIOS', 'REGISTRADO', 'Factura ENTEL'),
(3, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-011'), CURRENT_DATE - INTERVAL '22 days' + TIME '10:00', 'Alquiler local mayo', 2500.00, 'ALQUILER', 'REGISTRADO', 'Alquiler mensual'),
(4, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-021'), CURRENT_DATE - INTERVAL '20 days' + TIME '10:00', 'Mantenimiento extractor de humo', 450.00, 'MANTENIMIENTO', 'REGISTRADO', 'Reparacion urgente'),
(5, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-031'), CURRENT_DATE - INTERVAL '18 days' + TIME '10:00', 'Transporte de mercaderia', 180.00, 'TRANSPORTE', 'REGISTRADO', 'Flete camion'),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-002'), CURRENT_DATE - INTERVAL '15 days' + TIME '10:00', 'Sueldos personal quincenal', 15000.00, 'SUELDOS', 'REGISTRADO', 'Nomina quincenal sucursal 2'),
(3, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-012'), CURRENT_DATE - INTERVAL '15 days' + TIME '10:00', 'Sueldos personal quincenal', 14500.00, 'SUELDOS', 'REGISTRADO', 'Nomina quincenal sucursal 3'),
(4, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-022'), CURRENT_DATE - INTERVAL '15 days' + TIME '10:00', 'Sueldos personal quincenal', 14000.00, 'SUELDOS', 'REGISTRADO', 'Nomina quincenal sucursal 4'),
(5, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-032'), CURRENT_DATE - INTERVAL '15 days' + TIME '10:00', 'Sueldos personal quincenal', 13500.00, 'SUELDOS', 'REGISTRADO', 'Nomina quincenal sucursal 5'),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-001'), CURRENT_DATE - INTERVAL '10 days' + TIME '10:00', 'Impuesto municipal trimestral', 800.00, 'IMPUESTOS', 'REGISTRADO', 'IT trimestral'),
(3, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-011'), CURRENT_DATE - INTERVAL '8 days' + TIME '10:00', 'Perdida de mercaderia vencida', 120.00, 'PERDIDA', 'REGISTRADO', 'Verduras vencidas'),
(4, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-021'), CURRENT_DATE - INTERVAL '6 days' + TIME '10:00', 'Compra de menaje nuevo', 350.00, 'OTROS', 'REGISTRADO', 'Platos y cubiertos'),
(5, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-031'), CURRENT_DATE - INTERVAL '4 days' + TIME '10:00', 'Pago servicio de internet', 200.00, 'SERVICIOS', 'REGISTRADO', 'Internet mensual'),
(2, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-001'), CURRENT_DATE - INTERVAL '2 days' + TIME '10:00', 'Mantenimiento aire acondicionado', 280.00, 'MANTENIMIENTO', 'REGISTRADO', 'Recarga gas refrigerante'),
(3, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-011'), CURRENT_DATE - INTERVAL '1 day' + TIME '10:00', 'Retiro de efectivo para caja chica', 500.00, 'OTROS', 'REGISTRADO', 'Fondo de reserva'),
(4, (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-021'), CURRENT_DATE + TIME '08:00', 'Pago seguro comercial', 400.00, 'OTROS', 'REGISTRADO', 'Seguro mensual')
) AS v(id_sucursal, id_empleado, fecha, descripcion, monto_total, tipo_gasto, estado, observaciones);

-- ============================================================
-- 26. DETALLE_NOTA_SALIDA - 30 (2 per nota)
-- ============================================================

INSERT INTO detalle_nota_salida (descripcion, cantidad, monto, id_nota_salida, id_stock_sucursal)
SELECT v.* FROM (VALUES
('Servicio de electricidad mayo 2026', 1, 350.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%electricidad%' AND id_sucursal=2 LIMIT 1), NULL),
('Impuesto de alcaldia', 1, 350.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%electricidad%' AND id_sucursal=2 LIMIT 1), NULL),
('Alquiler local comercial mayo', 1, 2500.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%Alquiler local%' AND id_sucursal=3 LIMIT 1), NULL),
('Mantenimiento bomba de agua', 1, 2500.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%Alquiler local%' AND id_sucursal=3 LIMIT 1), NULL),
('Repuesto motor extractor', 1, 250.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%Mantenimiento extractor%' LIMIT 1), NULL),
('Mano de obra tecnico', 1, 200.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%Mantenimiento extractor%' LIMIT 1), NULL),
('Flete camion 3 toneladas', 1, 180.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%Transporte%' LIMIT 1), NULL),
('Gasolina ida y vuelta', 1, 180.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%Transporte%' LIMIT 1), NULL),
('Personal turno manana', 5, 5000.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%Sueldos personal%' AND id_sucursal=2 LIMIT 1), NULL),
('Personal turno tarde', 5, 5000.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%Sueldos personal%' AND id_sucursal=2 LIMIT 1), NULL),
('Personal turno manana', 5, 4800.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%Sueldos personal%' AND id_sucursal=3 LIMIT 1), NULL),
('Personal turno tarde', 5, 4800.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%Sueldos personal%' AND id_sucursal=3 LIMIT 1), NULL),
('Personal turno manana', 5, 4600.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%Sueldos personal%' AND id_sucursal=4 LIMIT 1), NULL),
('Personal turno tarde', 5, 4600.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%Sueldos personal%' AND id_sucursal=4 LIMIT 1), NULL),
('Personal turno manana', 5, 4500.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%Sueldos personal%' AND id_sucursal=5 LIMIT 1), NULL),
('Personal turno tarde', 5, 4500.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%Sueldos personal%' AND id_sucursal=5 LIMIT 1), NULL),
('Impuesto IT trimestral', 1, 800.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%Impuesto municipal%' LIMIT 1), NULL),
('Recargo por mora', 1, 800.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%Impuesto municipal%' LIMIT 1), NULL),
('Verduras vencidas descartadas', 5, 60.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%Perdida%' LIMIT 1), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-004' AND ss.id_sucursal=3)),
('Frutas vencidas descartadas', 3, 60.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%Perdida%' LIMIT 1), (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-061' AND ss.id_sucursal=3)),
('Juego de platos porcelana', 10, 150.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%menaje%' LIMIT 1), NULL),
('Juego de cubiertos acero', 10, 200.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%menaje%' LIMIT 1), NULL),
('Servicio internet fibra optica', 1, 200.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%internet%' LIMIT 1), NULL),
('Instalacion router adicional', 1, 200.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%internet%' LIMIT 1), NULL),
('Gas refrigerante R410a', 2, 140.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%aire acondicionado%' LIMIT 1), NULL),
('Mano de obra tecnico', 1, 140.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%aire acondicionado%' LIMIT 1), NULL),
('Retiro efectivo caja chica', 1, 500.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%Retiro de efectivo%' LIMIT 1), NULL),
('Fondo rotatorio', 1, 500.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%Retiro de efectivo%' LIMIT 1), NULL),
('Prima seguro comercial', 1, 400.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%seguro comercial%' LIMIT 1), NULL),
('Cobertura incendio y robo', 1, 400.00, (SELECT id_nota_salida FROM nota_salida WHERE descripcion LIKE '%seguro comercial%' LIMIT 1), NULL)
) AS v(descripcion, cantidad, monto, id_nota_salida, id_stock_sucursal);

-- ============================================================
-- 27. MOVIMIENTOS_DE_CAJA - 35
-- ============================================================

INSERT INTO movimiento_caja (concepto, fecha, monto, descripcion, tipo, id_caja, id_empleado)
SELECT v.* FROM (VALUES
-- VENTAS (17)
('VENTA', CURRENT_DATE - INTERVAL '20 days' + TIME '21:15', 165.36, 'Venta nota 1 pago efectivo', 'INGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=2 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-001')),
('VENTA', CURRENT_DATE - INTERVAL '19 days' + TIME '22:15', 343.50, 'Venta nota 2 pago tarjeta', 'INGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=3 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-011')),
('VENTA', CURRENT_DATE - INTERVAL '18 days' + TIME '20:45', 139.26, 'Venta nota 3 pago QR', 'INGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=4 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-021')),
('VENTA', CURRENT_DATE - INTERVAL '17 days' + TIME '21:15', 392.52, 'Venta nota 4 pago efectivo', 'INGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=5 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-031')),
('VENTA', CURRENT_DATE - INTERVAL '15 days' + TIME '22:15', 248.44, 'Venta nota 5 pago tarjeta', 'INGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=2 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-002')),
('VENTA', CURRENT_DATE - INTERVAL '14 days' + TIME '21:45', 213.80, 'Venta nota 6 pago efectivo', 'INGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=3 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-012')),
('VENTA', CURRENT_DATE - INTERVAL '12 days' + TIME '20:15', 271.68, 'Venta nota 7 pago tarjeta', 'INGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=4 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-022')),
('VENTA', CURRENT_DATE - INTERVAL '10 days' + TIME '15:15', 96.64, 'Venta nota 8 pago QR', 'INGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=2 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-003')),
('VENTA', CURRENT_DATE - INTERVAL '8 days' + TIME '21:15', 188.72, 'Venta nota 9 pago efectivo', 'INGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=5 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-032')),
('VENTA', CURRENT_DATE - INTERVAL '5 days' + TIME '22:15', 138.12, 'Venta nota 10 pago tarjeta', 'INGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=3 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-013')),
('VENTA', CURRENT_DATE - INTERVAL '20 days' + TIME '13:15', 121.72, 'Venta nota 11 para llevar', 'INGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=2 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-009')),
('VENTA', CURRENT_DATE - INTERVAL '19 days' + TIME '13:45', 100.06, 'Venta nota 12 para llevar', 'INGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=3 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-019')),
('VENTA', CURRENT_DATE - INTERVAL '18 days' + TIME '12:45', 112.60, 'Venta nota 13 para llevar', 'INGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=4 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-028')),
('VENTA', CURRENT_DATE - INTERVAL '15 days' + TIME '18:45', 338.94, 'Venta nota 14 para llevar', 'INGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=2 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-005')),
('VENTA', CURRENT_DATE - INTERVAL '12 days' + TIME '20:15', 121.72, 'Venta nota 15 para llevar', 'INGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=5 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-035')),
('VENTA', CURRENT_DATE - INTERVAL '10 days' + TIME '13:15', 152.94, 'Venta nota 16 para llevar', 'INGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=3 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-020')),
('VENTA', CURRENT_DATE - INTERVAL '8 days' + TIME '13:45', 146.10, 'Venta nota 17 para llevar', 'INGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=4 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-029')),
-- COMPRA (4)
('COMPRA', CURRENT_DATE - INTERVAL '30 days' + TIME '11:00', 1368.00, 'Compra verduras', 'EGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=2 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-004')),
('COMPRA', CURRENT_DATE - INTERVAL '23 days' + TIME '11:00', 2736.00, 'Compra carnes', 'EGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=3 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-014')),
('COMPRA', CURRENT_DATE - INTERVAL '16 days' + TIME '11:00', 912.00, 'Compra verduras', 'EGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=4 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-023')),
('COMPRA', CURRENT_DATE - INTERVAL '13 days' + TIME '11:00', 1710.00, 'Compra lacteos', 'EGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=5 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-033')),
-- NOTA_SALIDA (5)
('NOTA_SALIDA', CURRENT_DATE - INTERVAL '25 days' + TIME '10:30', 350.00, 'Pago servicio electrico', 'EGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=2 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-005')),
('NOTA_SALIDA', CURRENT_DATE - INTERVAL '22 days' + TIME '10:30', 2500.00, 'Pago alquiler mensual', 'EGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=3 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-015')),
('NOTA_SALIDA', CURRENT_DATE - INTERVAL '20 days' + TIME '10:30', 450.00, 'Reparacion extractor', 'EGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=4 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-024')),
('NOTA_SALIDA', CURRENT_DATE - INTERVAL '15 days' + TIME '10:30', 15000.00, 'Pago sueldos personal', 'EGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=2 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-006')),
('NOTA_SALIDA', CURRENT_DATE - INTERVAL '10 days' + TIME '10:30', 800.00, 'Impuesto municipal', 'EGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=2 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-007')),
-- INGRESO_EXTRA (3)
('INGRESO_EXTRA', CURRENT_DATE - INTERVAL '14 days' + TIME '14:00', 500.00, 'Propina colectiva evento', 'INGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=5 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-034')),
('INGRESO_EXTRA', CURRENT_DATE - INTERVAL '8 days' + TIME '16:00', 200.00, 'Venta articulos display', 'INGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=3 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-016')),
('INGRESO_EXTRA', CURRENT_DATE - INTERVAL '2 days' + TIME '15:00', 350.00, 'Donacion patronal', 'INGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=4 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-025')),
-- RETIRO (3)
('RETIRO', CURRENT_DATE - INTERVAL '12 days' + TIME '13:00', 1000.00, 'Retiro gerente general', 'EGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=2 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-008')),
('RETIRO', CURRENT_DATE - INTERVAL '6 days' + TIME '14:00', 500.00, 'Retiro caja chica', 'EGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=3 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-018')),
('RETIRO', CURRENT_DATE - INTERVAL '1 day' + TIME '12:00', 750.00, 'Pago urgente proveedor', 'EGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=5 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-036')),
-- More VENTAS online (2)
('VENTA', CURRENT_DATE - INTERVAL '20 days' + TIME '15:15', 110.32, 'Venta online nota 23', 'INGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=2 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-008')),
('VENTA', CURRENT_DATE - INTERVAL '18 days' + TIME '20:15', 142.68, 'Venta online nota 25', 'INGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=3 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-017')),
('VENTA', CURRENT_DATE - INTERVAL '5 days' + TIME '19:15', 110.32, 'Venta nota 18 para llevar', 'INGRESO', (SELECT id_caja FROM caja WHERE id_sucursal=2 AND estado='CERRADA'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-010'))
) AS v(concepto, fecha, monto, descripcion, tipo, id_caja, id_empleado);

-- ============================================================
-- 28. TRANSACCIONES_ONLINE - 7
-- ============================================================

INSERT INTO transaccion_online (numero_transaccion, moneda, monto, estado, fecha_inicio, fecha_completado, codigo_autorizacion, id_nota_venta)
SELECT v.* FROM (VALUES
('PY-20260001', 'BOB', 110.32, 'COMPLETADA', CURRENT_DATE - INTERVAL '20 days' + TIME '14:00', CURRENT_DATE - INTERVAL '20 days' + TIME '14:38', 'AUTH-001', (SELECT id_nota_venta FROM nota_venta nv JOIN comanda c ON c.id_comanda=nv.id_comanda WHERE c.numero_comanda='CMD-20260601-0024')),
('RP-20260001', 'BOB', 142.68, 'COMPLETADA', CURRENT_DATE - INTERVAL '18 days' + TIME '19:00', CURRENT_DATE - INTERVAL '18 days' + TIME '19:50', 'AUTH-002', (SELECT id_nota_venta FROM nota_venta nv JOIN comanda c ON c.id_comanda=nv.id_comanda WHERE c.numero_comanda='CMD-20260603-0025')),
('PY-20260002', 'BOB', 165.36, 'COMPLETADA', CURRENT_DATE - INTERVAL '15 days' + TIME '20:00', CURRENT_DATE - INTERVAL '15 days' + TIME '20:32', 'AUTH-003', (SELECT id_nota_venta FROM nota_venta nv JOIN comanda c ON c.id_comanda=nv.id_comanda WHERE c.numero_comanda='CMD-20260605-0026')),
('RP-20260002', 'BOB', 152.94, 'COMPLETADA', CURRENT_DATE - INTERVAL '10 days' + TIME '19:00', CURRENT_DATE - INTERVAL '10 days' + TIME '19:05', 'AUTH-004', (SELECT id_nota_venta FROM nota_venta nv JOIN comanda c ON c.id_comanda=nv.id_comanda WHERE c.numero_comanda='CMD-20260608-0027')),
('PY-20260003', 'BOB', 146.10, 'COMPLETADA', CURRENT_DATE - INTERVAL '4 days' + TIME '18:00', CURRENT_DATE - INTERVAL '4 days' + TIME '18:08', 'AUTH-005', (SELECT id_nota_venta FROM nota_venta nv JOIN comanda c ON c.id_comanda=nv.id_comanda WHERE c.numero_comanda='CMD-20260612-0028')),
('RP-20260003', 'BOB', 110.32, 'COMPLETADA', CURRENT_DATE - INTERVAL '2 days' + TIME '19:00', CURRENT_DATE - INTERVAL '2 days' + TIME '19:40', 'AUTH-006', (SELECT id_nota_venta FROM nota_venta nv JOIN comanda c ON c.id_comanda=nv.id_comanda WHERE c.numero_comanda='CMD-20260618-0029')),
('PY-20260004', 'BOB', 159.80, 'PENDIENTE', CURRENT_DATE + TIME '14:00', NULL, 'AUTH-007', (SELECT id_nota_venta FROM nota_venta nv JOIN comanda c ON c.id_comanda=nv.id_comanda WHERE c.numero_comanda='CMD-20260622-0030'))
) AS v(numero_transaccion, moneda, monto, estado, fecha_inicio, fecha_completado, codigo_autorizacion, id_nota_venta);

-- ============================================================
-- 29. ENTREGAS - 12
-- ============================================================

INSERT INTO entrega (direccion_entrega, latitud, longitud, costo_envio, estado, fecha_asignacion, fecha_entrega, observaciones, tiempo_estimado_min, id_comanda, id_empleado)
SELECT v.* FROM (VALUES
('Av. Busch 1234', -17.7833, -63.1821, 8.00, 'ENTREGADO', CURRENT_DATE - INTERVAL '20 days' + TIME '14:10', CURRENT_DATE - INTERVAL '20 days' + TIME '14:38', 'Entrega rapida', 35, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260601-0024'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-007')),
('Calle Linares 567', -17.7856, -63.1798, 9.00, 'ENTREGADO', CURRENT_DATE - INTERVAL '18 days' + TIME '19:10', CURRENT_DATE - INTERVAL '18 days' + TIME '19:50', 'Pago en efectivo', 40, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260603-0025'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-008')),
('Av. Libertador 890', -17.7812, -63.1845, 7.00, 'ENTREGADO', CURRENT_DATE - INTERVAL '15 days' + TIME '20:10', CURRENT_DATE - INTERVAL '15 days' + TIME '20:32', 'Sin observaciones', 30, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260605-0026'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-007')),
('Barrio San Jorge 456', -17.7890, -63.1812, 10.00, 'ENTREGADO', CURRENT_DATE - INTERVAL '10 days' + TIME '19:10', CURRENT_DATE - INTERVAL '10 days' + TIME '19:05', 'Edificio azul piso 3', 35, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260608-0027'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-017')),
('Calle 6 de Agosto 1111', -17.7845, -63.1834, 8.50, 'ENTREGADO', CURRENT_DATE - INTERVAL '4 days' + TIME '18:10', CURRENT_DATE - INTERVAL '4 days' + TIME '18:08', 'Timbre 2B', 35, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260612-0028'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-007')),
('Zona Villa Fatima 789', -17.7798, -63.1856, 9.50, 'ENTREGADO', CURRENT_DATE - INTERVAL '5 days' + TIME '20:10', CURRENT_DATE - INTERVAL '5 days' + TIME '20:40', 'Casa color verde', 35, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260611-0019'), (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-018')),
('Av. Argentina 2222', -17.7867, -63.1789, 7.50, 'PENDIENTE', CURRENT_DATE + TIME '14:10', NULL, 'Departamento 502', 30, (SELECT id_comanda FROM comanda WHERE numero_comanda='CMD-20260622-0030'), NULL)
) AS v(direccion_entrega, latitud, longitud, costo_envio, estado, fecha_asignacion, fecha_entrega, observaciones, tiempo_estimado_min, id_comanda, id_empleado);

-- ============================================================
-- 30. UBICACION_EMPLEADO - 15
-- ============================================================

INSERT INTO ubicacion_empleado (latitud, longitud, fecha_registro, id_empleado)
SELECT v.* FROM (VALUES
(-17.7833, -63.1821, CURRENT_DATE - INTERVAL '20 days' + TIME '14:38', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-007')),
(-17.7856, -63.1798, CURRENT_DATE - INTERVAL '18 days' + TIME '19:50', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-008')),
(-17.7812, -63.1845, CURRENT_DATE - INTERVAL '15 days' + TIME '20:32', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-007')),
(-17.7890, -63.1812, CURRENT_DATE - INTERVAL '12 days' + TIME '19:05', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-017')),
(-17.7845, -63.1834, CURRENT_DATE - INTERVAL '8 days' + TIME '20:08', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-007')),
(-17.7798, -63.1856, CURRENT_DATE - INTERVAL '5 days' + TIME '20:40', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-018')),
(-17.7867, -63.1789, CURRENT_DATE - INTERVAL '2 days' + TIME '18:35', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-027')),
(-17.7820, -63.1830, CURRENT_DATE - INTERVAL '1 days' + TIME '14:00', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-007')),
(-17.7850, -63.1805, CURRENT_DATE - INTERVAL '1 days' + TIME '19:30', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-008')),
(-17.7870, -63.1815, CURRENT_DATE + TIME '13:00', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-017')),
(-17.7805, -63.1842, CURRENT_DATE + TIME '13:05', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-018')),
(-17.7855, -63.1825, CURRENT_DATE + TIME '14:00', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-027')),
(-17.7838, -63.1818, CURRENT_DATE + TIME '18:00', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-007')),
(-17.7862, -63.1795, CURRENT_DATE + TIME '18:05', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-008')),
(-17.7815, -63.1838, CURRENT_DATE + TIME '19:00', (SELECT id_empleado FROM empleado WHERE codigo_empleado='EMP-017'))
) AS v(latitud, longitud, fecha_registro, id_empleado);

-- ============================================================
-- 31. ALERTAS_INVENTARIO - 10
-- ============================================================

INSERT INTO alerta_inv (id_sucursal, id_stock, tipo, estado, fecha_generacion)
SELECT v.* FROM (VALUES
(2, (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-016' AND ss.id_sucursal=2), 'STOCK_MINIMO', 'LEIDA', CURRENT_DATE - INTERVAL '5 days'),
(3, (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-016' AND ss.id_sucursal=3), 'STOCK_MINIMO', 'LEIDA', CURRENT_DATE - INTERVAL '3 days'),
(4, (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-002' AND ss.id_sucursal=4), 'STOCK_MINIMO', 'LEIDA', CURRENT_DATE - INTERVAL '2 days'),
(2, (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-004' AND ss.id_sucursal=2), 'VENCIMIENTO_PROXIMO', 'LEIDA', CURRENT_DATE - INTERVAL '4 days'),
(3, (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-005' AND ss.id_sucursal=3), 'VENCIMIENTO_PROXIMO', 'LEIDA', CURRENT_DATE - INTERVAL '3 days'),
(5, (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-022' AND ss.id_sucursal=5), 'STOCK_MINIMO', 'NO_LEIDA', CURRENT_DATE - INTERVAL '6 days'),
(2, (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-029' AND ss.id_sucursal=2), 'VENCIMIENTO_PROXIMO', 'NO_LEIDA', CURRENT_DATE - INTERVAL '1 day'),
(4, (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-032' AND ss.id_sucursal=4), 'STOCK_MINIMO', 'NO_LEIDA', CURRENT_DATE - INTERVAL '1 day'),
(5, (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-012' AND ss.id_sucursal=5), 'STOCK_MINIMO', 'NO_LEIDA', CURRENT_DATE),
(3, (SELECT id_stock FROM stock_sucursal ss JOIN inventario i ON i.id_inventario=ss.id_inventario WHERE i.codigo='INV-004' AND ss.id_sucursal=3), 'VENCIMIENTO_PROXIMO', 'NO_LEIDA', CURRENT_DATE)
) AS v(id_sucursal, id_stock, tipo, estado, fecha_generacion);

-- ============================================================
-- 32. Resetear secuencias + COMMIT
-- ============================================================

DO $$
BEGIN
  PERFORM setval(pg_get_serial_sequence('usuario', 'id_usuario'), COALESCE((SELECT MAX(id_usuario) FROM usuario), 1));
  PERFORM setval(pg_get_serial_sequence('empleado', 'id_empleado'), COALESCE((SELECT MAX(id_empleado) FROM empleado), 1));
  PERFORM setval(pg_get_serial_sequence('cliente', 'id_cliente'), COALESCE((SELECT MAX(id_cliente) FROM cliente), 1));
  PERFORM setval(pg_get_serial_sequence('proveedor', 'id_proveedor'), COALESCE((SELECT MAX(id_proveedor) FROM proveedor), 1));
  PERFORM setval(pg_get_serial_sequence('producto_final', 'id_producto_final'), COALESCE((SELECT MAX(id_producto_final) FROM producto_final), 1));
  PERFORM setval(pg_get_serial_sequence('inventario', 'id_inventario'), COALESCE((SELECT MAX(id_inventario) FROM inventario), 1));
  PERFORM setval(pg_get_serial_sequence('receta', 'id_receta'), COALESCE((SELECT MAX(id_receta) FROM receta), 1));
  PERFORM setval(pg_get_serial_sequence('ingrediente_receta', 'id_ingrediente_receta'), COALESCE((SELECT MAX(id_ingrediente_receta) FROM ingrediente_receta), 1));
  PERFORM setval(pg_get_serial_sequence('stock_sucursal', 'id_stock'), COALESCE((SELECT MAX(id_stock) FROM stock_sucursal), 1));
  PERFORM setval(pg_get_serial_sequence('lote_inventario', 'id_lote'), COALESCE((SELECT MAX(id_lote) FROM lote_inventario), 1));
  PERFORM setval(pg_get_serial_sequence('empleado_sucursal', 'id'), COALESCE((SELECT MAX(id) FROM empleado_sucursal), 1));
  PERFORM setval(pg_get_serial_sequence('reserva', 'id_reserva'), COALESCE((SELECT MAX(id_reserva) FROM reserva), 1));
  PERFORM setval(pg_get_serial_sequence('reserva_mesa', 'id_reserva_mesa'), COALESCE((SELECT MAX(id_reserva_mesa) FROM reserva_mesa), 1));
  PERFORM setval(pg_get_serial_sequence('comanda', 'id_comanda'), COALESCE((SELECT MAX(id_comanda) FROM comanda), 1));
  PERFORM setval(pg_get_serial_sequence('detalle_comanda', 'id_detalle_comanda'), COALESCE((SELECT MAX(id_detalle_comanda) FROM detalle_comanda), 1));
  PERFORM setval(pg_get_serial_sequence('caja', 'id_caja'), COALESCE((SELECT MAX(id_caja) FROM caja), 1));
  PERFORM setval(pg_get_serial_sequence('nota_venta', 'id_nota_venta'), COALESCE((SELECT MAX(id_nota_venta) FROM nota_venta), 1));
  PERFORM setval(pg_get_serial_sequence('detalle_nota_venta', 'id_detalle_nota_venta'), COALESCE((SELECT MAX(id_detalle_nota_venta) FROM detalle_nota_venta), 1));
  PERFORM setval(pg_get_serial_sequence('compra', 'id_compra'), COALESCE((SELECT MAX(id_compra) FROM compra), 1));
  PERFORM setval(pg_get_serial_sequence('detalle_compra', 'id_detalle_compra'), COALESCE((SELECT MAX(id_detalle_compra) FROM detalle_compra), 1));
  PERFORM setval(pg_get_serial_sequence('nota_salida', 'id_nota_salida'), COALESCE((SELECT MAX(id_nota_salida) FROM nota_salida), 1));
  PERFORM setval(pg_get_serial_sequence('detalle_nota_salida', 'id_detalle'), COALESCE((SELECT MAX(id_detalle) FROM detalle_nota_salida), 1));
  PERFORM setval(pg_get_serial_sequence('movimiento_caja', 'id_movimiento'), COALESCE((SELECT MAX(id_movimiento) FROM movimiento_caja), 1));
  PERFORM setval(pg_get_serial_sequence('transaccion_online', 'id_transaccion'), COALESCE((SELECT MAX(id_transaccion) FROM transaccion_online), 1));
  PERFORM setval(pg_get_serial_sequence('entrega', 'id_entrega'), COALESCE((SELECT MAX(id_entrega) FROM entrega), 1));
  PERFORM setval(pg_get_serial_sequence('ubicacion_empleado', 'id_ubicacion'), COALESCE((SELECT MAX(id_ubicacion) FROM ubicacion_empleado), 1));
  PERFORM setval(pg_get_serial_sequence('alerta_inv', 'id_alerta'), COALESCE((SELECT MAX(id_alerta) FROM alerta_inv), 1));
  PERFORM setval(pg_get_serial_sequence('mesa', 'id_mesa'), COALESCE((SELECT MAX(id_mesa) FROM mesa), 1));
  PERFORM setval(pg_get_serial_sequence('sector', 'id_sector'), COALESCE((SELECT MAX(id_sector) FROM sector), 1));
  PERFORM setval(pg_get_serial_sequence('sucursal', 'id_sucursal'), COALESCE((SELECT MAX(id_sucursal) FROM sucursal), 1));
  PERFORM setval(pg_get_serial_sequence('metodo_pago', 'id_metodo_pago'), COALESCE((SELECT MAX(id_metodo_pago) FROM metodo_pago), 1));
  PERFORM setval(pg_get_serial_sequence('categoria', 'id_categoria'), COALESCE((SELECT MAX(id_categoria) FROM categoria), 1));
  PERFORM setval(pg_get_serial_sequence('carrito_compras', 'id_carrito'), COALESCE((SELECT MAX(id_carrito) FROM carrito_compras), 1));
  PERFORM setval(pg_get_serial_sequence('item_carrito', 'id_item_carrito'), COALESCE((SELECT MAX(id_item_carrito) FROM item_carrito), 1));
END $$;

COMMIT;

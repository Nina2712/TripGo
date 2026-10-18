-- Base de datos de TripGo (MySQL / MariaDB).
-- Impórtala desde phpMyAdmin (pestaña Importar) dentro de la base que creó tu hosting.

CREATE TABLE IF NOT EXISTS administradores (
  id INT AUTO_INCREMENT PRIMARY KEY,
  usuario VARCHAR(40) NOT NULL UNIQUE,
  clave_hash VARCHAR(255) NOT NULL,
  creado TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS catalogo (
  id INT AUTO_INCREMENT PRIMARY KEY,
  tipo ENUM('HOTEL','AUTO','TOUR') NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  ciudad VARCHAR(60) NOT NULL,
  detalle VARCHAR(120) NOT NULL DEFAULT '',
  precio DECIMAL(10,2) NOT NULL,
  activo TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS reservas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  dispositivo VARCHAR(64) NOT NULL,
  id_local INT NOT NULL,
  tipo VARCHAR(10) NOT NULL,
  servicio VARCHAR(100) NOT NULL,
  ciudad VARCHAR(60) NOT NULL,
  precio_unitario DECIMAL(10,2) NOT NULL,
  cantidad INT NOT NULL,
  cliente_nombre VARCHAR(100) NOT NULL,
  cliente_dni VARCHAR(20) NOT NULL,
  precio_total DECIMAL(10,2) NOT NULL,
  fecha_inicio VARCHAR(10) NOT NULL DEFAULT '',
  fecha_fin VARCHAR(10) NOT NULL DEFAULT '',
  creado TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  actualizado TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uq_dispositivo_local (dispositivo, id_local)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Catálogo inicial (el mismo que trae la app en SQLite).
INSERT INTO catalogo (tipo, nombre, ciudad, detalle, precio) VALUES
('HOTEL','Palacio del Inka','Cusco','Hotel 5 estrellas',520.00),
('HOTEL','Casa Andina Premium','Arequipa','Hotel 4 estrellas',310.00),
('HOTEL','Aranwa Paracas Resort','Ica','Resort frente al mar',480.00),
('HOTEL','Hotel DM Nazca','Nazca','Hotel 3 estrellas',190.00),
('HOTEL','Hotel Hilton Iquitos','Iquitos','Hotel 4 estrellas',410.00),
('HOTEL','Hotel Costa del Sol','Piura','Hotel de playa',280.00),
('HOTEL','Gran Hotel Miraflores','Lima','Hotel 5 estrellas',590.00),
('HOTEL','Hotel Plaza Mayor Lima','Lima','Hotel 4 estrellas',340.00),
('HOTEL','Hostal Inti Wasi','Cusco','Hostal 3 estrellas',160.00),
('HOTEL','Boutique Valle Sagrado','Cusco','Hotel 4 estrellas',390.00),
('HOTEL','Hotel Misti Plaza','Arequipa','Hotel 3 estrellas',210.00),
('HOTEL','Hotel Oasis Huacachina','Ica','Hotel 4 estrellas',330.00),
('HOTEL','Hotel Pampa Dorada','Nazca','Hotel 4 estrellas',250.00),
('HOTEL','Amazon Lodge Iquitos','Iquitos','Lodge de selva',360.00),
('HOTEL','Hostal Brisa del Mar','Piura','Hostal 3 estrellas',170.00),
('AUTO','Toyota Corolla','Lima','Modelo 2025',150.00),
('AUTO','Hyundai Tucson','Cusco','Modelo 2025 · SUV',220.00),
('AUTO','Kia Rio','Arequipa','Modelo 2024',120.00),
('AUTO','Toyota RAV4','Lima','Modelo 2025 · SUV',250.00),
('AUTO','Toyota Hilux','Arequipa','Camioneta 4x4',280.00),
('AUTO','Nissan Kicks','Ica','SUV compacta',190.00),
('AUTO','Suzuki Swift','Lima','Modelo 2024 · Económico',110.00),
('AUTO','Toyota Yaris','Cusco','Modelo 2024',130.00),
('AUTO','Mitsubishi L200','Cusco','Camioneta 4x4',300.00),
('AUTO','Hyundai Accent','Ica','Sedán 2024',125.00),
('AUTO','Kia Sportage','Piura','Modelo 2025 · SUV',230.00),
('AUTO','Toyota Hiace','Lima','Van 12 pasajeros',340.00),
('AUTO','Suzuki Jimny','Nazca','4x4 compacto',200.00),
('TOUR','Machu Picchu Clásico','Cusco','2 días',480.00),
('TOUR','Cañón del Colca','Arequipa','2 días',320.00),
('TOUR','Islas Ballestas','Ica','4 horas',90.00),
('TOUR','Sobrevuelo Líneas de Nazca','Nazca','1 hora',260.00),
('TOUR','Amazonía Profunda','Iquitos','3 días',650.00),
('TOUR','Playas de Máncora','Piura','Full day',150.00),
('TOUR','City Tour Lima Colonial','Lima','4 horas',80.00),
('TOUR','Gastronómico en Barranco','Lima','3 horas',120.00),
('TOUR','Valle Sagrado de los Incas','Cusco','Full day',210.00),
('TOUR','Montaña de Siete Colores','Cusco','Full day',180.00),
('TOUR','Monasterio de Santa Catalina','Arequipa','3 horas',70.00),
('TOUR','Dunas y Sandboard en Huacachina','Ica','Medio día',140.00),
('TOUR','Reserva Pacaya Samiria','Iquitos','2 días',480.00),
('TOUR','Surf y Atardecer en Máncora','Piura','3 horas',130.00);

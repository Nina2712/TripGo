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
('AUTO','Toyota Corolla','Lima','Modelo 2025',150.00),
('AUTO','Hyundai Tucson','Cusco','Modelo 2025 · SUV',220.00),
('AUTO','Kia Rio','Arequipa','Modelo 2024',120.00),
('AUTO','Toyota RAV4','Lima','Modelo 2025 · SUV',250.00),
('AUTO','Toyota Hilux','Arequipa','Camioneta 4x4',280.00),
('AUTO','Nissan Kicks','Ica','SUV compacta',190.00),
('TOUR','Machu Picchu Clásico','Cusco','2 días',480.00),
('TOUR','Cañón del Colca','Arequipa','2 días',320.00),
('TOUR','Islas Ballestas','Ica','4 horas',90.00),
('TOUR','Sobrevuelo Líneas de Nazca','Nazca','1 hora',260.00),
('TOUR','Amazonía Profunda','Iquitos','3 días',650.00),
('TOUR','Playas de Máncora','Piura','Full day',150.00);

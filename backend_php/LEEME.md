# TripGo · Servicio REST propio + portal administrativo (PHP + MySQL)

## Contenido
- `sql/tripgo.sql` – tablas `administradores`, `catalogo` y `reservas` + catálogo inicial.
- `api/servicios.php` – GET: catálogo activo de hoteles, autos y tours (JSON).
- `api/reservas.php` – POST: guardar o eliminar una reserva; GET: listar las de un dispositivo.
- `admin/` – portal administrativo: login, reservas (buscar, editar, eliminar) y catálogo (crear, editar, ocultar, eliminar).
- `config.php` – credenciales de la base y clave de la API.

## Publicar en un hosting gratuito con PHP y MySQL
1. Crea una base de datos MySQL en el panel del hosting (anota servidor, base, usuario y contraseña).
2. En phpMyAdmin → **Importar** → sube `sql/tripgo.sql`.
3. Edita `config.php` con esos datos y cambia `API_KEY` por una cadena larga y aleatoria.
4. Sube la carpeta `backend_php` completa al hosting (por ejemplo como `/tripgo`).
5. Abre `https://TU-DOMINIO/tripgo/admin/login.php`: la primera vez pide **crear el administrador**. Hazlo de inmediato.
6. Prueba la API: `https://TU-DOMINIO/tripgo/api/servicios.php?clave=TU_CLAVE` debe mostrar el JSON del catálogo.

## Conectar la app
En `app/src/main/java/com/example/proyectofinal/network/ServidorTripGo.kt` cambia:
- `URL_BASE` → `https://TU-DOMINIO/tripgo/api/`
- `CLAVE_API` → el mismo valor de `API_KEY` en `config.php`

Mientras `URL_BASE` conserve «TU-DOMINIO», la app funciona solo con SQLite y no llama al servidor.

Para probar con XAMPP en tu PC y el emulador: copia `backend_php` a `htdocs/tripgo` y usa `http://10.0.2.2/tripgo/api/`
(el HTTP solo está permitido hacia 10.0.2.2; en un celular real usa un hosting con HTTPS).

## Cómo se relaciona con la app
- Al abrir la app, Retrofit descarga el catálogo activo (`GET servicios.php`) y actualiza SQLite.
- Al confirmar, editar o eliminar una reserva, Retrofit la envía (`POST reservas.php`); el portal la muestra.
- Si no hay internet o el servidor falla, la app sigue funcionando con SQLite.

## Seguridad aplicada
Consultas preparadas (PDO), contraseñas con `password_hash`, token CSRF en los formularios del portal,
escape de salida con `htmlspecialchars`, validación en el servidor y total recalculado por el servidor.
Limitación conocida: la `API_KEY` va dentro de la app, así que protege de accesos casuales, no de un atacante decidido.

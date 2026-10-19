<?php
// GET  /api/reservas.php?dispositivo=ID   -> reservas de ese dispositivo
// POST /api/reservas.php  {accion:"guardar"|"eliminar", dispositivo, id_local, ...}
require_once __DIR__ . '/util.php';
exigir_clave();

try {
    $pdo = db();

    if ($_SERVER['REQUEST_METHOD'] === 'GET') {
        $disp = (string)($_GET['dispositivo'] ?? '');
        if ($disp === '') {
            responder(['ok' => false, 'error' => 'Falta el dispositivo'], 400);
        }
        $st = $pdo->prepare(
            'SELECT id_local, tipo, servicio, ciudad, precio_unitario, cantidad, cliente_nombre, ' .
            'cliente_dni, precio_total, fecha_inicio, fecha_fin FROM reservas ' .
            'WHERE dispositivo = ? ORDER BY id_local DESC'
        );
        $st->execute([$disp]);
        responder($st->fetchAll());
    }

    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        responder(['ok' => false, 'error' => 'Método no permitido'], 405);
    }

    $d = json_decode(file_get_contents('php://input'), true);
    if (!is_array($d)) {
        responder(['ok' => false, 'error' => 'JSON no válido'], 400);
    }

    $accion  = (string)($d['accion'] ?? 'guardar');
    $disp    = trim((string)($d['dispositivo'] ?? ''));
    $idLocal = (int)($d['id_local'] ?? 0);

    if (!preg_match('/^[A-Za-z0-9-]{8,64}$/', $disp) || $idLocal < 1) {
        responder(['ok' => false, 'error' => 'Dispositivo o id_local no válido'], 400);
    }

    if ($accion === 'eliminar') {
        $st = $pdo->prepare('DELETE FROM reservas WHERE dispositivo = ? AND id_local = ?');
        $st->execute([$disp, $idLocal]);
        responder(['ok' => true]);
    }

    if ($accion !== 'guardar') {
        responder(['ok' => false, 'error' => 'Acción desconocida'], 400);
    }

    $tipo     = strtoupper(trim((string)($d['tipo'] ?? '')));
    $servicio = trim((string)($d['servicio'] ?? ''));
    $ciudad   = trim((string)($d['ciudad'] ?? ''));
    $unitario = (float)($d['precio_unitario'] ?? 0);
    $cantidad = (int)($d['cantidad'] ?? 0);
    $nombre   = trim((string)($d['cliente_nombre'] ?? ''));
    $dni      = trim((string)($d['cliente_dni'] ?? ''));
    $inicio   = trim((string)($d['fecha_inicio'] ?? ''));
    $fin      = trim((string)($d['fecha_fin'] ?? ''));

    if (!in_array($tipo, ['VUELO', 'HOTEL', 'AUTO', 'TOUR'], true)) {
        responder(['ok' => false, 'error' => 'Tipo no válido'], 400);
    }
    if ($servicio === '' || $ciudad === '' || $nombre === '' || $dni === '') {
        responder(['ok' => false, 'error' => 'Faltan datos obligatorios'], 400);
    }
    if ($unitario <= 0 || $cantidad < 1 || $cantidad > 30) {
        responder(['ok' => false, 'error' => 'Precio o cantidad no válidos'], 400);
    }
    if (!fecha_valida($inicio) || !fecha_valida($fin)) {
        responder(['ok' => false, 'error' => 'Fechas no válidas (AAAA-MM-DD)'], 400);
    }

    // El total se calcula en el servidor; no se confía en el que envía la app.
    $total = round($unitario * $cantidad, 2);

    $sql = 'INSERT INTO reservas (dispositivo, id_local, tipo, servicio, ciudad, precio_unitario, ' .
           'cantidad, cliente_nombre, cliente_dni, precio_total, fecha_inicio, fecha_fin) ' .
           'VALUES (?,?,?,?,?,?,?,?,?,?,?,?) ' .
           'ON DUPLICATE KEY UPDATE tipo = VALUES(tipo), servicio = VALUES(servicio), ' .
           'ciudad = VALUES(ciudad), precio_unitario = VALUES(precio_unitario), ' .
           'cantidad = VALUES(cantidad), cliente_nombre = VALUES(cliente_nombre), ' .
           'cliente_dni = VALUES(cliente_dni), precio_total = VALUES(precio_total), ' .
           'fecha_inicio = VALUES(fecha_inicio), fecha_fin = VALUES(fecha_fin)';
    $st = $pdo->prepare($sql);
    $st->execute([
        $disp, $idLocal, $tipo, $servicio, $ciudad, $unitario,
        $cantidad, $nombre, $dni, $total, $inicio, $fin,
    ]);

    responder(['ok' => true]);
} catch (Throwable $e) {
    responder(['ok' => false, 'error' => 'Error del servidor'], 500);
}

<?php
// GET /api/servicios.php  ->  catálogo activo de hoteles, autos y tours en JSON.
require_once __DIR__ . '/util.php';
exigir_clave();

try {
    $filas = db()->query(
        'SELECT id, tipo, nombre, ciudad, detalle, precio FROM catalogo WHERE activo = 1 ORDER BY tipo, id'
    )->fetchAll();

    foreach ($filas as &$f) {
        $f['id'] = (int)$f['id'];
        $f['precio'] = (float)$f['precio'];
    }
    unset($f);

    responder($filas);
} catch (Throwable $e) {
    responder(['ok' => false, 'error' => 'Error del servidor'], 500);
}

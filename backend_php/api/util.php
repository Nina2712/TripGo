<?php
require_once __DIR__ . '/../conexion.php';

header('Content-Type: application/json; charset=utf-8');

function responder($datos, int $codigo = 200): void
{
    http_response_code($codigo);
    echo json_encode($datos, JSON_UNESCAPED_UNICODE);
    exit;
}

/** La app debe enviar la clave en la cabecera X-Api-Key (o en ?clave=). */
function exigir_clave(): void
{
    $clave = $_SERVER['HTTP_X_API_KEY'] ?? ($_GET['clave'] ?? '');
    if (!hash_equals(API_KEY, (string)$clave)) {
        responder(['ok' => false, 'error' => 'Clave de API no válida'], 401);
    }
}

function fecha_valida(string $f): bool
{
    if ($f === '') {
        return true;
    }
    if (!preg_match('/^(\d{4})-(\d{2})-(\d{2})$/', $f, $m)) {
        return false;
    }
    return checkdate((int)$m[2], (int)$m[3], (int)$m[1]);
}

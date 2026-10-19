<?php
require_once __DIR__ . '/../conexion.php';

session_set_cookie_params(['httponly' => true, 'samesite' => 'Lax']);
session_start();

function e($v): string
{
    return htmlspecialchars((string)$v, ENT_QUOTES, 'UTF-8');
}

function requerir_login(): void
{
    if (empty($_SESSION['admin'])) {
        header('Location: login.php');
        exit;
    }
}

function csrf_token(): string
{
    if (empty($_SESSION['csrf'])) {
        $_SESSION['csrf'] = bin2hex(random_bytes(32));
    }
    return $_SESSION['csrf'];
}

function csrf_campo(): string
{
    return '<input type="hidden" name="csrf" value="' . e(csrf_token()) . '">';
}

function csrf_validar(): void
{
    $t = (string)($_POST['csrf'] ?? '');
    if (!hash_equals((string)($_SESSION['csrf'] ?? ''), $t)) {
        http_response_code(400);
        exit('Solicitud no válida. Vuelve atrás y reintenta.');
    }
}

function moneda(string $tipo): string
{
    return $tipo === 'VUELO' ? '$' : 'S/';
}

function cabecera(string $titulo, bool $menu = true): void
{
    echo '<!DOCTYPE html><html lang="es"><head><meta charset="utf-8">';
    echo '<meta name="viewport" content="width=device-width, initial-scale=1">';
    echo '<title>' . e($titulo) . ' · TripGo Admin</title><style>';
    echo 'body{font-family:system-ui,Arial,sans-serif;background:#f5f8ff;color:#172b4d;margin:0}';
    echo 'header{background:#1769ff;color:#fff;padding:12px 20px;display:flex;gap:18px;align-items:center;flex-wrap:wrap}';
    echo 'header a{color:#fff;text-decoration:none;font-weight:600}header .sp{flex:1}';
    echo 'main{max-width:1100px;margin:20px auto;padding:0 16px}';
    echo 'table{width:100%;border-collapse:collapse;background:#fff;border-radius:10px;overflow:hidden}';
    echo 'th,td{padding:9px 10px;border-bottom:1px solid #e3e9f5;text-align:left;font-size:14px}th{background:#e8f0ff}';
    echo '.tabla{overflow-x:auto}.card{background:#fff;border-radius:12px;padding:16px;margin-bottom:16px;box-shadow:0 1px 4px #0001}';
    echo 'input,select{padding:8px;border:1px solid #b9c6e0;border-radius:8px;font-size:14px;margin:3px 0;max-width:100%}';
    echo 'label{display:block;font-size:13px;margin-top:8px;color:#64748b}';
    echo 'button,.btn{background:#1769ff;color:#fff;border:0;border-radius:8px;padding:8px 14px;font-size:14px;cursor:pointer;text-decoration:none;display:inline-block}';
    echo '.rojo{background:#d93025}.gris{background:#64748b}.msg{padding:10px;border-radius:8px;margin-bottom:12px}';
    echo '.ok{background:#e6f4ea;color:#137333}.err{background:#fce8e6;color:#c5221f}.stats{display:flex;gap:12px;flex-wrap:wrap}';
    echo '.stat{background:#fff;border-radius:12px;padding:12px 18px;box-shadow:0 1px 4px #0001}.stat b{font-size:22px;display:block}';
    echo 'form.inline{display:inline}</style></head><body>';
    if ($menu) {
        echo '<header><strong>✈️ TripGo Admin</strong><a href="index.php">Reservas</a>';
        echo '<a href="catalogo.php">Catálogo</a><span class="sp"></span>';
        echo '<span>' . e($_SESSION['admin'] ?? '') . '</span><a href="logout.php">Salir</a></header>';
    }
    echo '<main>';
}

function pie(): void
{
    echo '</main></body></html>';
}

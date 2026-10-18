<?php
require_once __DIR__ . '/inc.php';
requerir_login();

$pdo = db();
$id = (int)($_GET['id'] ?? $_POST['id'] ?? 0);

$st = $pdo->prepare('SELECT * FROM reservas WHERE id = ?');
$st->execute([$id]);
$r = $st->fetch();
if (!$r) {
    http_response_code(404);
    exit('Reserva no encontrada.');
}

function fecha_ok(string $f): bool
{
    if ($f === '') {
        return true;
    }
    return (bool)preg_match('/^(\d{4})-(\d{2})-(\d{2})$/', $f, $m) && checkdate((int)$m[2], (int)$m[3], (int)$m[1]);
}

$error = '';
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    csrf_validar();
    $nombre = trim((string)($_POST['cliente_nombre'] ?? ''));
    $dni = trim((string)($_POST['cliente_dni'] ?? ''));
    $cant = (int)($_POST['cantidad'] ?? 0);
    $ini = trim((string)($_POST['fecha_inicio'] ?? ''));
    $fin = trim((string)($_POST['fecha_fin'] ?? ''));

    if ($nombre === '' || $dni === '') {
        $error = 'Nombre y DNI son obligatorios.';
    } elseif ($cant < 1 || $cant > 30) {
        $error = 'La cantidad debe estar entre 1 y 30.';
    } elseif (!fecha_ok($ini) || !fecha_ok($fin)) {
        $error = 'Las fechas deben tener formato AAAA-MM-DD.';
    } elseif ($r['tipo'] === 'AUTO' && ($ini === '' || $fin === '' || $fin <= $ini)) {
        $error = 'Para autos, la fecha de fin debe ser posterior a la de inicio.';
    } else {
        $total = round((float)$r['precio_unitario'] * $cant, 2);
        $up = $pdo->prepare(
            'UPDATE reservas SET cliente_nombre = ?, cliente_dni = ?, cantidad = ?, ' .
            'fecha_inicio = ?, fecha_fin = ?, precio_total = ? WHERE id = ?'
        );
        $up->execute([$nombre, $dni, $cant, $ini, $fin, $total, $id]);
        header('Location: index.php');
        exit;
    }
    $r['cliente_nombre'] = $nombre;
    $r['cliente_dni'] = $dni;
    $r['cantidad'] = $cant;
    $r['fecha_inicio'] = $ini;
    $r['fecha_fin'] = $fin;
}

cabecera('Editar reserva');
?>
<h2>Editar reserva #<?= (int)$r['id'] ?></h2>
<div class="card">
  <p><strong><?= e($r['tipo']) ?></strong> · <?= e($r['servicio']) ?> (<?= e($r['ciudad']) ?>) ·
     Precio unitario <?= e(moneda($r['tipo']) . ' ' . number_format((float)$r['precio_unitario'], 2)) ?></p>
  <?php if ($error !== ''): ?><div class="msg err"><?= e($error) ?></div><?php endif; ?>
  <form method="post">
    <?= csrf_campo() ?>
    <input type="hidden" name="id" value="<?= (int)$r['id'] ?>">
    <label>Cliente</label><input name="cliente_nombre" value="<?= e($r['cliente_nombre']) ?>" required>
    <label>DNI</label><input name="cliente_dni" value="<?= e($r['cliente_dni']) ?>" required>
    <label>Cantidad</label><input type="number" name="cantidad" min="1" max="30" value="<?= (int)$r['cantidad'] ?>" required>
    <label>Fecha inicio (AAAA-MM-DD)</label><input name="fecha_inicio" value="<?= e($r['fecha_inicio']) ?>">
    <label>Fecha fin (AAAA-MM-DD)</label><input name="fecha_fin" value="<?= e($r['fecha_fin']) ?>">
    <p><button type="submit">Guardar cambios</button> <a class="btn gris" href="index.php">Cancelar</a></p>
  </form>
</div>
<?php pie();

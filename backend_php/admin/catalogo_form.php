<?php
require_once __DIR__ . '/inc.php';
requerir_login();

$pdo = db();
$id = (int)($_GET['id'] ?? $_POST['id'] ?? 0);
$item = ['tipo' => 'HOTEL', 'nombre' => '', 'ciudad' => '', 'detalle' => '', 'precio' => ''];

if ($id > 0) {
    $st = $pdo->prepare('SELECT * FROM catalogo WHERE id = ?');
    $st->execute([$id]);
    $fila = $st->fetch();
    if (!$fila) {
        http_response_code(404);
        exit('Elemento no encontrado.');
    }
    $item = $fila;
}

$error = '';
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    csrf_validar();
    $item['tipo'] = (string)($_POST['tipo'] ?? '');
    $item['nombre'] = trim((string)($_POST['nombre'] ?? ''));
    $item['ciudad'] = trim((string)($_POST['ciudad'] ?? ''));
    $item['detalle'] = trim((string)($_POST['detalle'] ?? ''));
    $item['precio'] = (string)($_POST['precio'] ?? '');

    if (!in_array($item['tipo'], ['HOTEL', 'AUTO', 'TOUR'], true)) {
        $error = 'Tipo no válido.';
    } elseif ($item['nombre'] === '' || $item['ciudad'] === '') {
        $error = 'Nombre y ciudad son obligatorios.';
    } elseif (!is_numeric($item['precio']) || (float)$item['precio'] <= 0) {
        $error = 'El precio debe ser un número mayor que cero.';
    } else {
        $precio = round((float)$item['precio'], 2);
        if ($id > 0) {
            $pdo->prepare('UPDATE catalogo SET tipo=?, nombre=?, ciudad=?, detalle=?, precio=? WHERE id=?')
                ->execute([$item['tipo'], $item['nombre'], $item['ciudad'], $item['detalle'], $precio, $id]);
        } else {
            $pdo->prepare('INSERT INTO catalogo (tipo, nombre, ciudad, detalle, precio) VALUES (?,?,?,?,?)')
                ->execute([$item['tipo'], $item['nombre'], $item['ciudad'], $item['detalle'], $precio]);
        }
        header('Location: catalogo.php');
        exit;
    }
}

cabecera($id > 0 ? 'Editar elemento' : 'Nuevo elemento');
?>
<h2><?= $id > 0 ? 'Editar elemento #' . (int)$id : 'Nuevo elemento del catálogo' ?></h2>
<div class="card">
  <?php if ($error !== ''): ?><div class="msg err"><?= e($error) ?></div><?php endif; ?>
  <form method="post">
    <?= csrf_campo() ?>
    <input type="hidden" name="id" value="<?= (int)$id ?>">
    <label>Tipo</label>
    <select name="tipo">
      <?php foreach (['HOTEL', 'AUTO', 'TOUR'] as $t): ?>
        <option value="<?= $t ?>"<?= $item['tipo'] === $t ? ' selected' : '' ?>><?= $t ?></option>
      <?php endforeach; ?>
    </select>
    <label>Nombre</label><input name="nombre" value="<?= e($item['nombre']) ?>" required>
    <label>Ciudad</label><input name="ciudad" value="<?= e($item['ciudad']) ?>" required>
    <label>Detalle</label><input name="detalle" value="<?= e($item['detalle']) ?>">
    <label>Precio (S/)</label><input name="precio" value="<?= e($item['precio']) ?>" required>
    <p><button type="submit">Guardar</button> <a class="btn gris" href="catalogo.php">Cancelar</a></p>
  </form>
</div>
<?php pie();

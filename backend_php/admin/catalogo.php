<?php
require_once __DIR__ . '/inc.php';
requerir_login();

$pdo = db();
$aviso = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    csrf_validar();
    $id = (int)($_POST['id'] ?? 0);
    $accion = (string)($_POST['accion'] ?? '');
    if ($accion === 'eliminar') {
        $pdo->prepare('DELETE FROM catalogo WHERE id = ?')->execute([$id]);
        $aviso = 'Elemento eliminado.';
    } elseif ($accion === 'alternar') {
        $pdo->prepare('UPDATE catalogo SET activo = 1 - activo WHERE id = ?')->execute([$id]);
        $aviso = 'Estado actualizado.';
    }
}

$items = $pdo->query('SELECT * FROM catalogo ORDER BY tipo, id')->fetchAll();

cabecera('Catálogo');
?>
<h2>Catálogo de hoteles, autos y tours</h2>
<p>Lo que esté <strong>activo</strong> se descarga en la app al abrirla.</p>
<?php if ($aviso !== ''): ?><div class="msg ok"><?= e($aviso) ?></div><?php endif; ?>
<p><a class="btn" href="catalogo_form.php">+ Agregar elemento</a></p>

<div class="tabla"><table>
  <tr><th>#</th><th>Tipo</th><th>Nombre</th><th>Ciudad</th><th>Detalle</th><th>Precio (S/)</th><th>Estado</th><th>Acciones</th></tr>
  <?php foreach ($items as $i): ?>
    <tr>
      <td><?= (int)$i['id'] ?></td>
      <td><?= e($i['tipo']) ?></td>
      <td><?= e($i['nombre']) ?></td>
      <td><?= e($i['ciudad']) ?></td>
      <td><?= e($i['detalle']) ?></td>
      <td><?= e(number_format((float)$i['precio'], 2)) ?></td>
      <td><?= (int)$i['activo'] === 1 ? 'Activo' : 'Oculto' ?></td>
      <td>
        <a class="btn" href="catalogo_form.php?id=<?= (int)$i['id'] ?>">Editar</a>
        <form class="inline" method="post">
          <?= csrf_campo() ?><input type="hidden" name="accion" value="alternar">
          <input type="hidden" name="id" value="<?= (int)$i['id'] ?>">
          <button class="gris" type="submit"><?= (int)$i['activo'] === 1 ? 'Ocultar' : 'Activar' ?></button>
        </form>
        <form class="inline" method="post" onsubmit="return confirm('¿Eliminar este elemento?')">
          <?= csrf_campo() ?><input type="hidden" name="accion" value="eliminar">
          <input type="hidden" name="id" value="<?= (int)$i['id'] ?>">
          <button class="rojo" type="submit">Eliminar</button>
        </form>
      </td>
    </tr>
  <?php endforeach; ?>
  <?php if (!$items): ?><tr><td colspan="8">El catálogo está vacío.</td></tr><?php endif; ?>
</table></div>
<?php pie();

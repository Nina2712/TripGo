<?php
require_once __DIR__ . '/inc.php';
requerir_login();

$pdo = db();
$aviso = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    csrf_validar();
    if (($_POST['accion'] ?? '') === 'eliminar') {
        $st = $pdo->prepare('DELETE FROM reservas WHERE id = ?');
        $st->execute([(int)($_POST['id'] ?? 0)]);
        $aviso = 'Reserva eliminada.';
    }
}

$q = trim((string)($_GET['q'] ?? ''));
$like = '%' . $q . '%';
$st = $pdo->prepare(
    'SELECT * FROM reservas WHERE cliente_nombre LIKE ? OR cliente_dni LIKE ? ' .
    'OR servicio LIKE ? OR ciudad LIKE ? ORDER BY actualizado DESC LIMIT 200'
);
$st->execute([$like, $like, $like, $like]);
$reservas = $st->fetchAll();

$porTipo = $pdo->query('SELECT tipo, COUNT(*) AS n FROM reservas GROUP BY tipo')->fetchAll();

cabecera('Reservas');
?>
<h2>Reservas recibidas desde la app</h2>
<?php if ($aviso !== ''): ?><div class="msg ok"><?= e($aviso) ?></div><?php endif; ?>

<div class="stats">
  <div class="stat"><b><?= count($reservas) ?></b>mostradas</div>
  <?php foreach ($porTipo as $t): ?>
    <div class="stat"><b><?= (int)$t['n'] ?></b><?= e($t['tipo']) ?></div>
  <?php endforeach; ?>
</div>

<form method="get" class="card" style="margin-top:16px">
  <input name="q" value="<?= e($q) ?>" placeholder="Buscar por cliente, DNI, servicio o ciudad" size="40">
  <button type="submit">Buscar</button>
  <?php if ($q !== ''): ?><a class="btn gris" href="index.php">Limpiar</a><?php endif; ?>
</form>

<div class="tabla"><table>
  <tr><th>#</th><th>Tipo</th><th>Servicio</th><th>Ciudad</th><th>Cliente</th><th>DNI</th>
      <th>Cant.</th><th>Fechas</th><th>Total</th><th>Acciones</th></tr>
  <?php foreach ($reservas as $r): ?>
    <tr>
      <td><?= (int)$r['id'] ?></td>
      <td><?= e($r['tipo']) ?></td>
      <td><?= e($r['servicio']) ?></td>
      <td><?= e($r['ciudad']) ?></td>
      <td><?= e($r['cliente_nombre']) ?></td>
      <td><?= e($r['cliente_dni']) ?></td>
      <td><?= (int)$r['cantidad'] ?></td>
      <td><?= $r['fecha_inicio'] !== '' ? e($r['fecha_inicio'] . ' → ' . $r['fecha_fin']) : '—' ?></td>
      <td><?= e(moneda($r['tipo']) . ' ' . number_format((float)$r['precio_total'], 2)) ?></td>
      <td>
        <a class="btn" href="reserva.php?id=<?= (int)$r['id'] ?>">Editar</a>
        <form class="inline" method="post" onsubmit="return confirm('¿Eliminar esta reserva?')">
          <?= csrf_campo() ?>
          <input type="hidden" name="accion" value="eliminar">
          <input type="hidden" name="id" value="<?= (int)$r['id'] ?>">
          <button class="rojo" type="submit">Eliminar</button>
        </form>
      </td>
    </tr>
  <?php endforeach; ?>
  <?php if (!$reservas): ?><tr><td colspan="10">No hay reservas.</td></tr><?php endif; ?>
</table></div>
<?php pie();

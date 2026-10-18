<?php
require_once __DIR__ . '/inc.php';

$pdo = db();
$hayAdmins = (int)$pdo->query('SELECT COUNT(*) FROM administradores')->fetchColumn() > 0;
$error = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    csrf_validar();
    $usuario = trim((string)($_POST['usuario'] ?? ''));
    $clave = (string)($_POST['clave'] ?? '');

    if (!$hayAdmins) {
        // Primera ejecución: se crea el administrador inicial.
        $clave2 = (string)($_POST['clave2'] ?? '');
        if (!preg_match('/^[A-Za-z0-9_.-]{3,40}$/', $usuario)) {
            $error = 'Usuario de 3 a 40 caracteres (letras, números, _ . -).';
        } elseif (strlen($clave) < 8) {
            $error = 'La contraseña debe tener al menos 8 caracteres.';
        } elseif ($clave !== $clave2) {
            $error = 'Las contraseñas no coinciden.';
        } else {
            $st = $pdo->prepare('INSERT INTO administradores (usuario, clave_hash) VALUES (?, ?)');
            $st->execute([$usuario, password_hash($clave, PASSWORD_DEFAULT)]);
            session_regenerate_id(true);
            $_SESSION['admin'] = $usuario;
            header('Location: index.php');
            exit;
        }
    } else {
        $st = $pdo->prepare('SELECT clave_hash FROM administradores WHERE usuario = ?');
        $st->execute([$usuario]);
        $hash = $st->fetchColumn();
        if ($hash !== false && password_verify($clave, (string)$hash)) {
            session_regenerate_id(true);
            $_SESSION['admin'] = $usuario;
            header('Location: index.php');
            exit;
        }
        $error = 'Usuario o contraseña incorrectos.';
    }
}

cabecera($hayAdmins ? 'Ingresar' : 'Crear administrador', false);
?>
<div class="card" style="max-width:380px;margin:60px auto">
  <h2>✈️ TripGo Admin</h2>
  <?php if (!$hayAdmins): ?>
    <p>Primera ejecución: crea el administrador del portal.</p>
  <?php endif; ?>
  <?php if ($error !== ''): ?><div class="msg err"><?= e($error) ?></div><?php endif; ?>
  <form method="post">
    <?= csrf_campo() ?>
    <label>Usuario</label>
    <input name="usuario" required autofocus>
    <label>Contraseña</label>
    <input type="password" name="clave" required>
    <?php if (!$hayAdmins): ?>
      <label>Repite la contraseña</label>
      <input type="password" name="clave2" required>
    <?php endif; ?>
    <p><button type="submit"><?= $hayAdmins ? 'Ingresar' : 'Crear y entrar' ?></button></p>
  </form>
</div>
<?php pie();

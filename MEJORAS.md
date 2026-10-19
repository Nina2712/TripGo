# Mejoras aplicadas a TripGo

NUEVO (2da entrega): ReservaFormScreen.kt con calendario y contador de cantidad.
NUEVOS: ui/screens/Componentes.kt, util/Favoritos.kt
REEMPLAZADOS: ui/theme/Color.kt, Theme.kt, Type.kt; ui/screens/HomeScreen.kt, ServiciosScreen.kt,
MisReservasScreen.kt, ProfileScreen.kt; ui/navigation/AppNavigation.kt
MODIFICADOS: LoginScreen.kt, RegisterScreen.kt, VuelosScreen.kt, DestinosScreen.kt, CercaDeMiScreen.kt,
SensoresScreen.kt, ReservaFormScreen.kt, MainActivity.kt, app/build.gradle.kts, res/values/strings.xml

Fotos sin tocar código: guarda en res/drawable con nombre  hotel_<nombre> | auto_<nombre> | tour_<nombre>
(minúsculas, sin tildes, guion bajo). Ej.: auto_toyota_corolla.jpg, tour_machu_picchu_clasico.jpg

3ra entrega: catálogo ampliado (+10 hoteles, autos y tours; 12 vuelos), carruseles en Inicio,
filtros y orden en Vuelos, pantalla Sensores con barras y mensajes claros, botón Sensores más visible.
La base de datos sube a versión 3: se renueva el catálogo pero las reservas NO se borran.

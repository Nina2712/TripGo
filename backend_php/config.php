<?php
// ================= CONFIGURACIÓN DE TRIPGO (servicio REST + portal) =================
// Cambia estos valores por los de tu hosting gratuito (panel -> MySQL Databases).
const DB_HOST = 'localhost';
const DB_NAME = 'tripgo';
const DB_USER = 'root';
const DB_PASS = '';

// Clave que la app envía en la cabecera X-Api-Key. Debe ser igual a CLAVE_API
// en app/.../network/ServidorTripGo.kt. Cámbiala por una cadena larga y aleatoria.
const API_KEY = 'CAMBIA_ESTA_CLAVE';

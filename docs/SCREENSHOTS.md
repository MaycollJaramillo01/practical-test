# Capturas de pantalla de TaskActivity

Para generar automáticamente una captura de la pantalla principal de la aplicación:

```bash
./run_task_app.sh --screenshot
```

El script compilará la app, la instalará en el dispositivo o emulador conectado y,
tras unos segundos, guardará la captura en `./screenshots/` con la marca de tiempo
correspondiente. Si desea especificar otro directorio de salida puede hacerlo así:

```bash
./run_task_app.sh --screenshot ~/imagenes
```

> **Nota:** Necesita un dispositivo con ADB habilitado o un emulador en ejecución.
> En este entorno de laboratorio no contamos con uno, por lo que no se adjunta una
> captura de ejemplo.

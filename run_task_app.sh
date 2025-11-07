#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_ROOT"

if [[ ! -x gradlew ]]; then
  chmod +x gradlew
fi

echo "==> Resolviendo dependencias y compilando la aplicación..."
./gradlew --no-daemon --stacktrace build

device_available() {
  command -v adb >/dev/null 2>&1 || return 1
  adb get-state >/dev/null 2>&1
}

if device_available; then
  echo "==> Instalando la aplicación en el dispositivo disponible..."
  ./gradlew --no-daemon --stacktrace installDebug
  echo "==> Iniciando la actividad principal..."
  adb shell am start -n cr.ac.utn.practicaltest/.TaskActivity >/dev/null 2>&1 || {
    echo "No se pudo iniciar la aplicación automáticamente. Verifique el estado del dispositivo." >&2
  }
else
  cat <<'MSG'
==> No se detectó un dispositivo/emulador Android listo.
    La aplicación ha sido compilada correctamente.
    Conecte un dispositivo o inicie un emulador y ejecute:
        ./gradlew installDebug
        adb shell am start -n cr.ac.utn.practicaltest/.TaskActivity
MSG
fi

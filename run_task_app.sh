#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'USAGE'
Uso: ./run_task_app.sh [--screenshot [directorio]]

  --screenshot [directorio]  Captura una imagen de la pantalla principal tras lanzar
                             la app. El archivo se guardará en el directorio indicado
                             (o en ./screenshots/ si se omite).
USAGE
}

SCREENSHOT_REQUESTED=0
SCREENSHOT_DIR=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    --screenshot)
      SCREENSHOT_REQUESTED=1
      if [[ "${2:-}" != "" && "${2:0:1}" != "-" ]]; then
        SCREENSHOT_DIR="$2"
        shift
      fi
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Opción no reconocida: $1" >&2
      usage >&2
      exit 1
      ;;
  esac
  shift
done

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

capture_screenshot() {
  local destination_dir="$1"
  local timestamp
  timestamp="$(date +%Y%m%d_%H%M%S)"
  local target_dir
  target_dir="${destination_dir:-$PROJECT_ROOT/screenshots}"
  mkdir -p "$target_dir"
  local file="$target_dir/task_activity_${timestamp}.png"
  if adb exec-out screencap -p >"$file"; then
    echo "==> Captura de pantalla guardada en: $file"
  else
    echo "No se pudo capturar la pantalla. Verifique que el dispositivo permita capturas." >&2
    rm -f "$file"
  fi
}

if device_available; then
  echo "==> Instalando la aplicación en el dispositivo disponible..."
  ./gradlew --no-daemon --stacktrace installDebug
  echo "==> Iniciando la actividad principal..."
  if adb shell am start -n cr.ac.utn.practicaltest/.TaskActivity >/dev/null 2>&1; then
    echo "Aplicación iniciada correctamente."
    if [[ $SCREENSHOT_REQUESTED -eq 1 ]]; then
      echo "==> Esperando a que la UI se estabilice para capturar la pantalla..."
      sleep 3
      capture_screenshot "$SCREENSHOT_DIR"
    fi
  else
    echo "No se pudo iniciar la aplicación automáticamente. Verifique el estado del dispositivo." >&2
  fi
else
  cat <<'MSG'
==> No se detectó un dispositivo/emulador Android listo.
    La aplicación ha sido compilada correctamente.
    Conecte un dispositivo o inicie un emulador y ejecute:
        ./gradlew installDebug
        adb shell am start -n cr.ac.utn.practicaltest/.TaskActivity
    Para capturar una pantalla automáticamente, vuelva a ejecutar:
        ./run_task_app.sh --screenshot [directorio]
MSG
fi

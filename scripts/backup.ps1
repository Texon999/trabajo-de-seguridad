# Backup de la base tienda_db con mongodump.
# Uso:  powershell -File scripts/backup.ps1
# Resultado: backup/<fecha>_tienda_db/
# Copia fuera del sitio (opcional):  -DestinoExterno D:\backups-nube  o  $env:BACKUP_EXTERNO
#
# Estrategia 3-2-1: 3 copias, en 2 soportes distintos, 1 fuera del sitio.
# Ajusta RPO (cuanto puedes perder) y RTO (cuanto tardas en recuperar).
#
# Automatizacion (Programador de tareas, diario 3:00 am):
#   schtasks /Create /TN "BackupTienda" /TR "powershell -ExecutionPolicy Bypass -File \"C:\ruta\Tienda\scripts\backup.ps1\"" /SC DAILY /ST 03:00
# Periodicamente PRUEBA la restauracion:  powershell -File scripts/restore.ps1 -Origen backup\<fecha>_tienda_db

param(
    [string]$MongoUri = $env:MONGO_URI,
    [string]$Directorio = "backup",
    [string]$DestinoExterno = $env:BACKUP_EXTERNO
)

if (-not $MongoUri) {
    $MongoUri = "mongodb://localhost:27017"
}

$fecha = Get-Date -Format "yyyyMMdd_HHmmss"
$destino = Join-Path $Directorio "${fecha}_tienda_db"

Write-Host "Respaldando tienda_db desde $MongoUri"
Write-Host "Destino: $destino"

mongodump --uri $MongoUri --db tienda_db --out $destino

if ($LASTEXITCODE -eq 0) {
    Write-Host "Backup completado en $destino"
    Write-Host "PRUEBA SIEMPRE la restauracion: powershell -File scripts/restore.ps1"
    if ($DestinoExterno) {
        $copiaExterna = Join-Path $DestinoExterno (Split-Path $destino -Leaf)
        Copy-Item -Recurse -Force $destino $copiaExterna
        Write-Host "Copia fuera del sitio enviada a $copiaExterna"
    } else {
        Write-Host "No se configuro copia fuera del sitio (usa -DestinoExterno o la variable BACKUP_EXTERNO)"
    }
} else {
    Write-Host "El backup fallo, revisa la salida de mongodump" -ForegroundColor Red
    exit 1
}

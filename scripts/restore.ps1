# Restauracion de tienda_db desde un dump de mongodump.
# Uso:  powershell -File scripts/restore.ps1 -Origen backup\20240101_120000_tienda_db

param(
    [string]$Origen,
    [string]$MongoUri = $env:MONGO_URI
)

if (-not $Origen) {
    Write-Host "Indica la carpeta del dump: powershell -File scripts/restore.ps1 -Origen backup\<fecha>_tienda_db" -ForegroundColor Yellow
    exit 1
}

if (-not (Test-Path $Origen)) {
    Write-Host "No existe la carpeta $Origen" -ForegroundColor Red
    exit 1
}

if (-not $MongoUri) {
    $MongoUri = "mongodb://localhost:27017"
}

Write-Host "Restaurando tienda_db desde $Origen"
mongorestore --uri $MongoUri --db tienda_db $Origen

if ($LASTEXITCODE -eq 0) {
    Write-Host "Restauracion completada"
} else {
    Write-Host "La restauracion fallo" -ForegroundColor Red
    exit 1
}

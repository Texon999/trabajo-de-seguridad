    # Endurece MongoDB en Windows: bloquea el puerto 27017 para conexiones remotas.
$ErrorActionPreference = "Stop"

$puerto = 27017
$regla = "Bloquear MongoDB $puerto"

$esAdministrador = ([Security.Principal.WindowsPrincipal] `
    [Security.Principal.WindowsIdentity]::GetCurrent()
).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)

if (-not $esAdministrador) {
    Write-Host "Ejecuta este script como Administrador." -ForegroundColor Red
    exit 1
}

Remove-NetFirewallRule -DisplayName $regla -ErrorAction SilentlyContinue

New-NetFirewallRule `
    -DisplayName $regla `
    -Direction Inbound `
    -Protocol TCP `
    -LocalPort $puerto `
    -Action Block

Write-Host "Puerto 27017 bloqueado correctamente." -ForegroundColor Green
Write-Host "MongoDB solo queda accesible desde localhost (127.0.0.1)."
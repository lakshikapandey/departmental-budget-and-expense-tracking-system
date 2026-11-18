# Departmental Budget & Expense Tracking System
# Student: Lakshika Pandey (Student ID: PIET25CR029)

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "Departmental Budget & Expense Tracking System" -ForegroundColor Green
Write-Host "Author: Lakshika Pandey (PIET25CR029)" -ForegroundColor Yellow
Write-Host "============================================================" -ForegroundColor Cyan

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

Write-Host "`n[1/2] Compiling Java Sources..." -ForegroundColor Cyan
$sources = (Get-ChildItem -Path "src\main\java" -Recurse -Filter "*.java").FullName
javac -cp "lib/*" -d bin $sources

if ($LASTEXITCODE -ne 0) {
    Write-Host "`n[ERROR] Java Compilation Failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "[SUCCESS] Compilation completed successfully!" -ForegroundColor Green
Write-Host "`n[2/2] Launching Application..." -ForegroundColor Cyan
java -cp "bin;lib/*" com.department.tracker.Main

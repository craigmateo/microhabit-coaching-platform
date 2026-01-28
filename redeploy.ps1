param (
    [string]$TomcatPath = "C:\apache-tomcat"
)

$ErrorActionPreference = "Stop"

$projectDir = Get-Location
$webapps = Join-Path $TomcatPath "webapps"
$bin = Join-Path $TomcatPath "bin"
$warName = "microhabit-coach.war"

Write-Host "=== Micro-Habit Coach Redeploy ==="

Write-Host "Building WAR..."
mvn clean package

$war = Get-ChildItem "$projectDir\target" -Filter "microhabit-coach*.war" | Select-Object -First 1
if (-not $war) {
    throw "WAR not found in target/"
}

Write-Host "Stopping Tomcat..."
& "$bin\shutdown.bat" | Out-Null
Start-Sleep -Seconds 3

Write-Host "Cleaning old deployment..."
Remove-Item "$webapps\$warName" -ErrorAction SilentlyContinue
Remove-Item "$webapps\microhabit-coach" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item "$TomcatPath\work\Catalina\localhost\microhabit-coach" -Recurse -Force -ErrorAction SilentlyContinue

Write-Host "Deploying new WAR..."
Copy-Item $war.FullName "$webapps\$warName" -Force

Write-Host "Starting Tomcat..."
& "$bin\startup.bat"

Write-Host "Done."
Write-Host "URL → http://localhost:8080/microhabit-coach/"

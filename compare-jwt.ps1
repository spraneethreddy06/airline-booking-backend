
$ErrorActionPreference = 'Stop'

$u = Get-Content 'user-service\src\main\java\com\thejas\user_service\auth\JwtHelper.java' -Raw
$f = Get-Content 'flight-service\src\main\java\com\thejas\flight_service\util\JwtUtil.java' -Raw

$um = [regex]::Match($u, 'secret\s*=\s*"([^"]+)"')
$fm = [regex]::Match($f, 'SECRET\s*=\s*"([^"]+)"')

if (-not $um.Success -or -not $fm.Success) {
    Write-Output 'Could not extract both secrets. No secrets displayed.'
    exit 1
}

if ($um.Groups[1].Value -ceq $fm.Groups[1].Value) {
    Write-Output 'JWT_SECRET_MATCH'
} else {
    Write-Output 'JWT_SECRET_MISMATCH'
}

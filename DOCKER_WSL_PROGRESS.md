# Docker + WSL Progress Log

Data: 2026-03-19

## Context
- Docker a fost instalat recent pe PC.
- La pornire/configurare Docker, au apărut erori WSL.

## Ce am încercat
1. Verificare/reset distro Docker WSL:
   - `wsl --unregister docker-desktop`
   - Rezultat: eroare `Wsl/CallMsi/Install/REGDB_E_CLASSNOTREG`

2. Încercare reinstalare WSL:
   - `wsl --install --web-download`
   - Rezultat: aceeași eroare `REGDB_E_CLASSNOTREG`

3. Reparare MSI:
   - `msiexec /unregister`
   - `msiexec /regserver`
   - `net start msiserver` => serviciul era deja pornit
   - `net stop msiserver` => mesaj că stop nu e valid în contextul curent

4. Încercare activare feature WSL prin DISM:
   - A fost rulată o comandă incompletă (`/featurena`)
   - Rezultat: `Error: 87` (opțiune nerecunoscută)

## Comenzi corecte recomandate (de rulat în Administrator CMD/PowerShell)
```cmd
dism /online /enable-feature /featurename:Microsoft-Windows-Subsystem-Linux /all /norestart
dism /online /enable-feature /featurename:VirtualMachinePlatform /all /norestart
DISM /Online /Cleanup-Image /RestoreHealth
sfc /scannow
shutdown /r /t 0
```

## După restart (de verificat)
```cmd
wsl --update
wsl --status
wsl -l -v
```

## Obiectiv următor
- Confirmare că WSL este funcțional.
- Dacă WSL e OK, relansare Docker Desktop pentru recrearea automată a distro-urilor `docker-desktop` și `docker-desktop-data`.

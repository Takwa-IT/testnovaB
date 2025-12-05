@echo off
REM Script de déploiement pour TestNova (Windows)
REM Usage: deploy.bat [environment]
REM Example: deploy.bat prod

setlocal enabledelayedexpansion

set ENVIRONMENT=%1
if "%ENVIRONMENT%"=="" set ENVIRONMENT=prod

set DOCKER_IMAGE_NAME=your-dockerhub-username/testnova
set DOCKER_IMAGE_TAG=latest

echo ==========================================
echo Deploiement de TestNova
echo Environment: %ENVIRONMENT%
echo ==========================================

REM Verifier que Docker est installe
docker --version >nul 2>&1
if errorlevel 1 (
    echo [X] Docker n'est pas installe!
    exit /b 1
)

REM Verifier que docker-compose est installe
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo [X] Docker Compose n'est pas installe!
    exit /b 1
)

REM Verifier que le fichier .env existe
if not exist .env (
    echo [X] Le fichier .env n'existe pas!
    echo [i] Copiez .env.example vers .env et configurez les variables.
    exit /b 1
)

REM Arreter les conteneurs existants
echo [i] Arret des conteneurs existants...
docker-compose down

REM Supprimer les anciennes images
echo [i] Nettoyage des anciennes images...
docker image prune -f

REM Telecharger la derniere image (si deploye depuis Docker Hub)
if "%ENVIRONMENT%"=="prod" (
    echo [i] Telechargement de la derniere image...
    docker-compose pull app
)

REM Construire l'image (pour developpement local)
if "%ENVIRONMENT%"=="dev" (
    echo [i] Construction de l'image Docker...
    docker-compose build --no-cache app
)

REM Demarrer les conteneurs
echo [i] Demarrage des conteneurs...
docker-compose up -d

REM Attendre que l'application soit prete
echo [i] Attente du demarrage de l'application...
timeout /t 30 /nobreak

REM Verifier la sante de l'application
echo [i] Verification de la sante de l'application...
set MAX_RETRIES=10
set RETRY_COUNT=0

:healthcheck
if %RETRY_COUNT% geq %MAX_RETRIES% (
    echo [X] L'application n'a pas demarre correctement!
    echo [i] Logs de l'application:
    docker-compose logs app
    exit /b 1
)

curl -f http://localhost:8080/actuator/health >nul 2>&1
if errorlevel 1 (
    set /a RETRY_COUNT+=1
    echo [i] Tentative !RETRY_COUNT!/%MAX_RETRIES%...
    timeout /t 5 /nobreak
    goto healthcheck
)

echo [OK] L'application est en ligne et fonctionne correctement!

REM Afficher l'état des conteneurs
echo.
echo [i] Etat des conteneurs:
docker-compose ps

echo.
echo ==========================================
echo [OK] Deploiement termine avec succes!
echo ==========================================
echo [i] Application: http://localhost:8080
echo [i] MySQL: localhost:3306
echo.
echo Commandes utiles:
echo   - Logs: docker-compose logs -f
echo   - Arreter: docker-compose down
echo   - Redemarrer: docker-compose restart
echo ==========================================

endlocal

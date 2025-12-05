#!/bin/bash

# Script de déploiement pour TestNova
# Usage: ./deploy.sh [environment]
# Example: ./deploy.sh prod

set -e  # Arrêter le script en cas d'erreur

ENVIRONMENT=${1:-prod}
DOCKER_IMAGE_NAME='https://hub.docker.com/repository/docker/elachikhaoui/testnova'

DOCKER_IMAGE_TAG="latest"

echo "=========================================="
echo "Déploiement de TestNova"
echo "Environment: $ENVIRONMENT"
echo "=========================================="

# Vérifier que Docker est installé
if ! command -v docker &> /dev/null; then
    echo "❌ Docker n'est pas installé!"
    exit 1
fi

# Vérifier que docker-compose est installé
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose n'est pas installé!"
    exit 1
fi

# Vérifier que le fichier .env existe
if [ ! -f .env ]; then
    echo "❌ Le fichier .env n'existe pas!"
    echo "📝 Copiez .env.example vers .env et configurez les variables."
    exit 1
fi

# Arrêter les conteneurs existants
echo "🛑 Arrêt des conteneurs existants..."
docker-compose down

# Supprimer les anciennes images (optionnel)
echo "🧹 Nettoyage des anciennes images..."
docker image prune -f

# Télécharger la dernière image (si déployé depuis Docker Hub)
if [ "$ENVIRONMENT" == "prod" ]; then
    echo "📥 Téléchargement de la dernière image..."
    docker-compose pull app
fi

# Construire l'image (pour développement local)
if [ "$ENVIRONMENT" == "dev" ]; then
    echo "🔨 Construction de l'image Docker..."
    docker-compose build --no-cache app
fi

# Démarrer les conteneurs
echo "🚀 Démarrage des conteneurs..."
docker-compose up -d

# Attendre que l'application soit prête
echo "⏳ Attente du démarrage de l'application..."
sleep 30

# Vérifier la santé de l'application
echo "🏥 Vérification de la santé de l'application..."
MAX_RETRIES=10
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if curl -f http://localhost:8080/actuator/health &> /dev/null; then
        echo "✅ L'application est en ligne et fonctionne correctement!"
        break
    else
        RETRY_COUNT=$((RETRY_COUNT+1))
        echo "⏳ Tentative $RETRY_COUNT/$MAX_RETRIES..."
        sleep 5
    fi
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    echo "❌ L'application n'a pas démarré correctement!"
    echo "📋 Logs de l'application:"
    docker-compose logs app
    exit 1
fi

# Afficher l'état des conteneurs
echo ""
echo "📊 État des conteneurs:"
docker-compose ps

echo ""
echo "=========================================="
echo "✅ Déploiement terminé avec succès!"
echo "=========================================="
echo "🌐 Application: http://localhost:8080"
echo "🗄️  MySQL: localhost:3306"
echo ""
echo "Commandes utiles:"
echo "  - Logs: docker-compose logs -f"
echo "  - Arrêter: docker-compose down"
echo "  - Redémarrer: docker-compose restart"
echo "=========================================="

# 🐳 Guide Docker & Jenkins - TestNova

Ce guide explique comment dockeriser l'application TestNova et la déployer avec Jenkins.

## 📋 Table des matières

1. [Prérequis](#prérequis)
2. [Architecture Docker](#architecture-docker)
3. [Configuration](#configuration)
4. [Déploiement Local](#déploiement-local)
5. [Déploiement avec Jenkins](#déploiement-avec-jenkins)
6. [Commandes Utiles](#commandes-utiles)
7. [Troubleshooting](#troubleshooting)

---

## 🛠️ Prérequis

### Logiciels requis

- **Docker** (version 20.10+)
- **Docker Compose** (version 2.0+)
- **Jenkins** (version 2.x) - pour le déploiement CI/CD
- **Git**
- **Java 17** (pour le build local)
- **Maven 3.9+** (pour le build local)

### Vérification de l'installation

```powershell
docker --version
docker-compose --version
java -version
mvn --version
```

---

## 🏗️ Architecture Docker

### Structure des fichiers

```
testnovaB/
├── testnova/
│   ├── Dockerfile              # Image Docker de l'application
│   ├── .dockerignore           # Fichiers à exclure du contexte Docker
│   └── src/                    # Code source
├── docker-compose.yml          # Orchestration des conteneurs
├── .env.example                # Template des variables d'environnement
├── Jenkinsfile                 # Pipeline CI/CD
├── deploy.sh                   # Script de déploiement (Linux/Mac)
└── deploy.bat                  # Script de déploiement (Windows)
```

### Services Docker

1. **testnova-app** : Application Spring Boot (port 8080)
2. **testnova-mysql** : Base de données MySQL 8.0 (port 3306)

---

## ⚙️ Configuration

### 1. Variables d'environnement

Créez un fichier `.env` à partir du template :

```powershell
Copy-Item .env.example .env
```

Modifiez le fichier `.env` avec vos valeurs :

```env
# Base de données MySQL
MYSQL_ROOT_PASSWORD=your_secure_root_password
MYSQL_USER=testnovauser
MYSQL_PASSWORD=your_secure_password

# API OpenAI/Groq
SPRING_AI_OPENAI_API_KEY=gsk_your_groq_api_key

# Configuration Email
SPRING_MAIL_USERNAME=testnovaplatform@gmail.com
SPRING_MAIL_PASSWORD=your_gmail_app_password
```

### 2. Configuration Docker Hub (optionnel)

Pour publier votre image sur Docker Hub :

1. Créez un compte sur [Docker Hub](https://hub.docker.com/)
2. Modifiez `Jenkinsfile` et remplacez `your-dockerhub-username` par votre nom d'utilisateur
3. Connectez-vous : `docker login`

---

## 🚀 Déploiement Local

### Méthode 1 : Docker Compose (Recommandé)

#### Construction et démarrage

```powershell
# Construire et démarrer tous les services
docker-compose up -d --build

# Vérifier l'état des conteneurs
docker-compose ps

# Voir les logs
docker-compose logs -f
```

#### Arrêt et nettoyage

```powershell
# Arrêter les services
docker-compose down

# Arrêter et supprimer les volumes (⚠️ supprime les données)
docker-compose down -v
```

### Méthode 2 : Script automatisé

#### Windows

```powershell
.\deploy.bat dev
```

#### Linux/Mac

```bash
chmod +x deploy.sh
./deploy.sh dev
```

### Méthode 3 : Build manuel

```powershell
# Se placer dans le dossier testnova
cd testnova

# Construire l'image
docker build -t testnova:latest .

# Lancer le conteneur
docker run -d `
  -p 8080:8080 `
  -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/testnova" `
  -e SPRING_DATASOURCE_USERNAME="root" `
  -e SPRING_DATASOURCE_PASSWORD="yourpassword" `
  -e SPRING_AI_OPENAI_API_KEY="your_api_key" `
  --name testnova-app `
  testnova:latest
```

---

## 🔄 Déploiement avec Jenkins

### 1. Installation de Jenkins

#### Windows (via Docker)

```powershell
docker run -d `
  -p 8081:8080 `
  -p 50000:50000 `
  -v jenkins_home:/var/jenkins_home `
  -v /var/run/docker.sock:/var/run/docker.sock `
  --name jenkins `
  jenkins/jenkins:lts
```

Accédez à Jenkins sur `http://localhost:8081`

### 2. Configuration de Jenkins

#### Installer les plugins nécessaires

1. Ouvrez Jenkins → **Manage Jenkins** → **Manage Plugins**
2. Installez les plugins suivants :
   - **Docker Pipeline**
   - **Git plugin**
   - **Pipeline**
   - **Credentials Binding**
   - **Email Extension**

#### Configurer les outils

1. **Manage Jenkins** → **Global Tool Configuration**
2. Configurez :
   - **JDK 17** : Nom = `JDK17`, JAVA_HOME
   - **Maven** : Nom = `Maven3`, Version = 3.9.6
   - **Docker** : Installation automatique

#### Ajouter les credentials

1. **Manage Jenkins** → **Manage Credentials** → **Global** → **Add Credentials**

Ajoutez les credentials suivants :

| ID | Type | Description |
|---|---|---|
| `dockerhub-credentials` | Username/Password | Identifiants Docker Hub |
| `groq-api-key` | Secret text | Clé API Groq |
| `email-password` | Secret text | Mot de passe email |
| `mysql-root-password` | Secret text | Mot de passe root MySQL |

### 3. Créer le pipeline Jenkins

1. **New Item** → Nom : `TestNova-Pipeline` → Type : **Pipeline**
2. Sous **Pipeline** :
   - **Definition** : Pipeline script from SCM
   - **SCM** : Git
   - **Repository URL** : Votre URL Git
   - **Branch** : `*/main`
   - **Script Path** : `Jenkinsfile`
3. Sauvegardez et lancez : **Build Now**

### 4. Webhook GitHub (optionnel)

Pour déclencher automatiquement le build lors d'un push :

1. **GitHub** → **Settings** → **Webhooks** → **Add webhook**
2. **Payload URL** : `http://your-jenkins-url/github-webhook/`
3. **Content type** : `application/json`
4. **Events** : Just the push event

---

## 📝 Commandes Utiles

### Docker

```powershell
# Voir les conteneurs en cours d'exécution
docker ps

# Voir tous les conteneurs (y compris arrêtés)
docker ps -a

# Voir les images
docker images

# Supprimer une image
docker rmi testnova:latest

# Voir les logs d'un conteneur
docker logs testnova-app -f

# Entrer dans un conteneur
docker exec -it testnova-app sh

# Nettoyer le système Docker
docker system prune -a --volumes
```

### Docker Compose

```powershell
# Démarrer les services
docker-compose up -d

# Arrêter les services
docker-compose down

# Reconstruire les images
docker-compose build --no-cache

# Voir les logs
docker-compose logs -f app

# Redémarrer un service
docker-compose restart app

# Exécuter une commande dans un conteneur
docker-compose exec app sh
```

### MySQL

```powershell
# Se connecter à MySQL
docker-compose exec mysql mysql -u root -p

# Backup de la base de données
docker-compose exec mysql mysqldump -u root -p testnova > backup.sql

# Restore de la base de données
docker-compose exec -T mysql mysql -u root -p testnova < backup.sql
```

---

## 🔧 Troubleshooting

### Problème : Le conteneur ne démarre pas

**Solution :**
```powershell
# Vérifier les logs
docker-compose logs app

# Vérifier l'état
docker-compose ps
```

### Problème : Erreur de connexion à MySQL

**Causes possibles :**
1. MySQL n'est pas encore prêt
2. Mauvaises credentials

**Solution :**
```powershell
# Attendre que MySQL soit prêt
docker-compose logs mysql

# Vérifier les variables d'environnement
docker-compose config
```

### Problème : Port déjà utilisé

**Solution :**
```powershell
# Trouver le processus utilisant le port 8080
netstat -ano | findstr :8080

# Arrêter le processus (remplacer PID par l'ID du processus)
taskkill /PID <PID> /F

# Ou changer le port dans docker-compose.yml
ports:
  - "8081:8080"  # Utiliser 8081 au lieu de 8080
```

### Problème : Image Docker trop volumineuse

**Solution :**
- Utilisez le `.dockerignore`
- Utilisez des images de base plus légères (alpine)
- Multi-stage build (déjà implémenté)

### Problème : Build Maven échoue

**Solution :**
```powershell
# Nettoyer le cache Maven
docker-compose build --no-cache app

# Ou construire localement d'abord
cd testnova
mvn clean package -DskipTests
```

### Problème : Jenkins ne peut pas accéder à Docker

**Solution Windows :**
1. Installez Docker Desktop
2. Activez "Expose daemon on tcp://localhost:2375 without TLS"
3. Redémarrez Docker

### Problème : Mémoire insuffisante

**Solution :**
```yaml
# Dans docker-compose.yml, ajoutez des limites
services:
  app:
    deploy:
      resources:
        limits:
          memory: 1G
        reservations:
          memory: 512M
```

---

## 📊 Monitoring et Logs

### Activer Spring Boot Actuator

L'application expose déjà l'endpoint `/actuator/health`. Pour plus d'endpoints :

```yaml
# Dans application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
```

### Accéder aux métriques

- **Health Check** : `http://localhost:8080/actuator/health`
- **Metrics** : `http://localhost:8080/actuator/metrics`
- **Info** : `http://localhost:8080/actuator/info`

---

## 🔐 Sécurité

### Bonnes pratiques

1. ✅ Ne jamais commiter le fichier `.env`
2. ✅ Utiliser des secrets Jenkins pour les credentials
3. ✅ Changer les mots de passe par défaut
4. ✅ Utiliser un utilisateur non-root dans Docker (déjà fait)
5. ✅ Scanner les images avec `docker scan`
6. ✅ Mettre à jour régulièrement les images de base

### Scanner l'image pour les vulnérabilités

```powershell
docker scan testnova:latest
```

---

## 🚀 Optimisations

### Améliorer la performance du build

1. **Cache Maven** : Utiliser un volume pour le cache Maven
2. **Multi-stage build** : Déjà implémenté
3. **Layers Docker** : Ordonner les commandes du moins changeant au plus changeant

### Réduire la taille de l'image

L'image actuelle utilise déjà :
- ✅ Multi-stage build
- ✅ Image Alpine pour le runtime
- ✅ .dockerignore

---

## 📚 Ressources

- [Documentation Docker](https://docs.docker.com/)
- [Documentation Docker Compose](https://docs.docker.com/compose/)
- [Documentation Jenkins](https://www.jenkins.io/doc/)
- [Spring Boot with Docker](https://spring.io/guides/gs/spring-boot-docker/)

---

## 👥 Support

Pour toute question ou problème :
1. Vérifiez la section [Troubleshooting](#troubleshooting)
2. Consultez les logs : `docker-compose logs -f`
3. Contactez l'équipe de développement

---

**Dernière mise à jour** : Décembre 2025

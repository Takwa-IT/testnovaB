pipeline {
    agent any
    
    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }
    
    environment {
        // Docker Hub credentials (à configurer dans Jenkins)
        DOCKER_HUB_CREDENTIALS = credentials('dockerhub-credentials')
        DOCKER_IMAGE_NAME = 'https://hub.docker.com/repository/docker/elachikhaoui/testnova'
        DOCKER_IMAGE_TAG = "${BUILD_NUMBER}"
        
        // Variables pour l'application
        SPRING_AI_OPENAI_API_KEY = credentials('groq-api-key')
        SPRING_MAIL_PASSWORD = credentials('email-password')
        MYSQL_ROOT_PASSWORD = credentials('mysql-root-password')
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Cloning repository...'
                checkout scm
            }
        }
        
        stage('Build Maven Project') {
            steps {
                dir('testnova') {
                    echo 'Building Maven project...'
                    bat 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('Run Unit Tests') {
            steps {
                dir('testnova') {
                    echo 'Running unit tests...'
                    bat 'mvn test'
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Code Quality Analysis') {
            steps {
                dir('testnova') {
                    echo 'Running SonarQube analysis...'
                    // Décommenter si SonarQube est configuré
                    // bat 'mvn sonar:sonar -Dsonar.host.url=http://your-sonarqube-url'
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                dir('testnova') {
                    echo 'Building Docker image...'
                    script {
                        bat "docker build -t ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ."
                        bat "docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ${DOCKER_IMAGE_NAME}:latest"
                    }
                }
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                echo 'Pushing Docker image to Docker Hub...'
                script {
                    bat "docker login -u ${DOCKER_HUB_CREDENTIALS_USR} -p ${DOCKER_HUB_CREDENTIALS_PSW}"
                    bat "docker push ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}"
                    bat "docker push ${DOCKER_IMAGE_NAME}:latest"
                }
            }
        }
        
        stage('Deploy to Server') {
            steps {
                echo 'Deploying to production server...'
                script {
                    // Option 1: Déploiement via SSH
                    // sshagent(['ssh-credentials']) {
                    //     bat """
                    //         ssh user@server "cd /path/to/app && docker-compose pull && docker-compose up -d"
                    //     """
                    // }
                    
                    // Option 2: Déploiement local (si Jenkins est sur le même serveur)
                    bat """
                        docker-compose down
                        docker-compose pull
                        docker-compose up -d
                    """
                }
            }
        }
        
        stage('Health Check') {
            steps {
                echo 'Performing health check...'
                script {
                    sleep(time: 30, unit: 'SECONDS')
                    // Vérifier que l'application répond
                    bat 'curl -f http://localhost:8080/actuator/health || exit 1'
                }
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline executed successfully!'
            // Notifications (Slack, Email, etc.)
            // emailext(
            //     subject: "SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
            //     body: "Good news! The build succeeded.",
            //     to: "team@example.com"
            // )
        }
        failure {
            echo 'Pipeline failed!'
            // Notifications en cas d'échec
            // emailext(
            //     subject: "FAILURE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
            //     body: "The build failed. Please check the console output.",
            //     to: "team@example.com"
            // )
        }
        always {
            echo 'Cleaning up...'
            // Nettoyer les images Docker non utilisées
            bat 'docker system prune -f'
        }
    }
}

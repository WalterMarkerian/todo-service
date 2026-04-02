pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "todo-backend-prod"
        DOCKER_NETWORK = "web_network"

        // Variables específicas para el contenedor de Producción
        PROD_DB_HOST = "postgres-prod"
        PROD_DB_PORT = "5432"
        PROD_DB_NAME = 'TODO_PROD_DB_NAME'
    }

    stages {
        stage('Compile & Test') {
            steps {
                sh "chmod +x mvnw"
                sh "./mvnw clean package -DskipTests"
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} ."
                sh "docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_IMAGE}:latest"
            }
        }

        stage('Deploy to Production') {
            steps {
                // Usamos los IDs de credenciales que tienes creados en Jenkins
                withCredentials([
                    string(credentialsId: 'TODO_PROD_DB_USER', variable: 'ENV_DB_USER'),
                    string(credentialsId: 'TODO_PROD_DB_PASS', variable: 'ENV_DB_PASS')
                ]) {
                    sh "docker stop todo-backend-prod || true"
                    sh "docker rm todo-backend-prod || true"

                    sh """
                        docker run -d \
                        --name todo-backend-prod \
                        --network ${DOCKER_NETWORK} \
                        --restart unless-stopped \
                        -p 8090:8090 \
                        -e SPRING_PROFILES_ACTIVE=prod \
                        -e DB_HOST=${PROD_DB_HOST} \
                        -e DB_PORT=${PROD_DB_PORT} \
                        -e DB_NAME=${PROD_DB_NAME} \
                        -e DB_USER=${ENV_DB_USER} \
                        -e DB_PASSWORD=${ENV_DB_PASS} \
                        -e JPA_DDL_AUTO=validate \
                        ${DOCKER_IMAGE}:latest
                    """
                }
            }
        }
    }

    post {
        success { echo "🚀 Backend desplegado correctamente en ${PROD_DB_NAME}" }
        failure { echo "❌ Fallo en el despliegue del Backend." }
        always { sh "docker image prune -f" }
    }
}
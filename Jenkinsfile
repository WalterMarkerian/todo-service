pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "todo-backend-prod"
        DOCKER_NETWORK = "web_network"

        // URL específica para la API
        VIRTUAL_HOST = "makeserver.tailc624bd.ts.net"
        VIRTUAL_PATH = "/api/"

        // Credenciales desde el Store de Jenkins
        DB_USER = credentials('DB_USER_PROD')
        DB_PASS = credentials('DB_PASS_PROD')

        // Conexión al contenedor 'postgres-prod'
        DB_URL = "jdbc:postgresql://postgres-prod:5432/todo_prod"
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
                sh "docker stop todo-backend-prod || true"
                sh "docker rm todo-backend-prod || true"

                sh """
                    docker run -d \
                    --name todo-backend-prod \
                    --network ${DOCKER_NETWORK} \
                    --restart unless-stopped \
                    -e VIRTUAL_HOST=${VIRTUAL_HOST} \
                    -e VIRTUAL_PORT=8090 \
                    -e SPRING_PROFILES_ACTIVE=prod \
                    -e SPRING_DATASOURCE_URL=${DB_URL} \
                    -e SPRING_DATASOURCE_USERNAME=${DB_USER} \
                    -e SPRING_DATASOURCE_PASSWORD=${DB_PASS} \
                    ${DOCKER_IMAGE}:latest
                """
            }
        }
    }

    post {
        success { echo "🚀 Backend desplegado en https://${VIRTUAL_HOST}" }
        failure { echo "❌ Fallo en el despliegue del Backend." }
        always { sh "docker image prune -f" }
    }
}
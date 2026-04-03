pipeline {
    agent any

    environment {
        // Configuración de Imagen y Contenedor
        DOCKER_IMAGE = "todo-service"
        CONTAINER_NAME = "todo-app"
        DOCKER_NETWORK = "web_network"

        // CREDENCIALES DIRECTAS (Hardcoded)
        // Deben coincidir exactamente con las que usaste para levantar postgres-prod
        DB_HOST = "postgres-prod"
        DB_PORT = "5432"
        DB_NAME = "todo_prod"
        DB_USER = "prod_user"
        DB_PASS = "s4yBZWMSW4oj33fzRbBB"
    }

    stages {
        stage('Compile & Test') {
            steps {
                script {
                    sh "chmod +x mvnw"
                    sh "./mvnw clean package -DskipTests"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} ."
                    sh "docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_IMAGE}:latest"
                }
            }
        }

        stage('Deploy to Production') {
            steps {
                script {
                    // Aseguramos que la red exista y el contenedor de DB esté conectado
                    sh "docker network create ${DOCKER_NETWORK} || true"
                    sh "docker network connect ${DOCKER_NETWORK} ${DB_HOST} || true"

                    // Eliminamos el contenedor viejo si existe
                    sh "docker rm -f ${CONTAINER_NAME} 2>/dev/null || true"

                    // Despliegue usando las variables del bloque environment
                    sh """
                        docker run -d \
                        --name ${CONTAINER_NAME} \
                        --network ${DOCKER_NETWORK} \
                        --restart unless-stopped \
                        -p 8090:8090 \
                        -e SPRING_PROFILES_ACTIVE=prod \
                        -e DB_HOST=${DB_HOST} \
                        -e DB_PORT=${DB_PORT} \
                        -e DB_NAME=${DB_NAME} \
                        -e DB_USER=${DB_USER} \
                        -e DB_PASSWORD=${DB_PASS} \
                        -e JPA_DDL_AUTO=validate \
                        ${DOCKER_IMAGE}:latest
                    """
                }
            }
        }
    }

    post {
        success {
            echo "🚀 Despliegue finalizado con éxito."
            // Pequeña verificación de logs tras el arranque
            sh "sleep 5 && docker logs ${CONTAINER_NAME} --tail 20"
        }
        failure {
            echo "❌ Falló el despliegue. Logs del contenedor:"
            sh "docker logs ${CONTAINER_NAME} --tail 50 || true"
        }
        always {
            sh "docker image prune -f"
        }
    }
}
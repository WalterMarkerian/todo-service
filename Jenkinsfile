pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "todo-service"
        CONTAINER_NAME = "todo-app"
        DOCKER_NETWORK = "web_network"

        PROD_DB_HOST = "postgres-prod"
        PROD_DB_PORT = "5432"

        // Variables directas para descartar errores de Jenkins Credentials
        PROD_DB_NAME = "todo_prod"
        PROD_DB_USER = "prod_user"
        PROD_DB_PASS = "s4yBZWMSW4oj33fzRbBB"
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
                    // Aseguramos conectividad de red
                    sh "docker network create ${DOCKER_NETWORK} || true"
                    sh "docker network connect ${DOCKER_NETWORK} ${PROD_DB_HOST} || true"

                    // Limpieza del contenedor anterior
                    sh "docker rm -f ${CONTAINER_NAME} 2>/dev/null || true"

                    // Despliegue con la clave HARDCODEADA
                    sh """
                        docker run -d \
                        --name ${CONTAINER_NAME} \
                        --network ${DOCKER_NETWORK} \
                        --restart unless-stopped \
                        -p 8090:8090 \
                        -e SPRING_PROFILES_ACTIVE=prod \
                        -e DB_HOST=${PROD_DB_HOST} \
                        -e DB_PORT=${PROD_DB_PORT} \
                        -e DB_NAME='${PROD_DB_NAME}' \
                        -e DB_USER='${PROD_DB_USER}' \
                        -e DB_PASSWORD='${PROD_DB_PASS}' \
                        -e JPA_DDL_AUTO=validate \
                        ${DOCKER_IMAGE}:latest
                    """
                }
            }
        }
    }

    post {
        success {
            echo "🚀 Despliegue finalizado con clave directa. Verificando logs..."
            // Agregamos un sleep pequeño para dar tiempo a Spring de intentar la conexión
            sh "sleep 15 && docker logs ${CONTAINER_NAME} | grep -i 'HikariPool-1 - Start completed' || echo 'Aún no conectó...'"
        }
        failure {
            echo "❌ Error detectado. Logs de Spring Boot:"
            sh "docker logs ${CONTAINER_NAME} --tail 100 || true"
        }
        always {
            sh "docker image prune -f"
        }
    }
}
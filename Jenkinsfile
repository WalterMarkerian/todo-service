pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "todo-service"
        CONTAINER_NAME = "todo-app"
        DOCKER_NETWORK = "web_network"

        PROD_DB_HOST = "postgres-prod"
        PROD_DB_PORT = "5432"
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
                withCredentials([
                    string(credentialsId: 'POSTGRES_TODO_USER', variable: 'POSTGRES_USER'),
                    string(credentialsId: 'POSTGRES_TODO_PASSWORD', variable: 'POSTGRES_PASSWORD'),
                    string(credentialsId: 'POSTGRES_TODO_DB', variable: 'POSTGRES_DB')
                ]) {
                    script {
                        // Aseguramos conectividad de red entre contenedores
                        sh "docker network create ${DOCKER_NETWORK} || true"
                        sh "docker network connect ${DOCKER_NETWORK} ${PROD_DB_HOST} || true"

                        // Limpieza: Eliminamos el contenedor anterior si existe
                        sh "docker rm -f ${CONTAINER_NAME} 2>/dev/null || true"

                        // Despliegue con la nueva clave alfanumérica
                        // Al no tener caracteres especiales, las comillas dobles bastan
                        sh """
                            docker run -d \
                            --name ${CONTAINER_NAME} \
                            --network ${DOCKER_NETWORK} \
                            --restart unless-stopped \
                            -p 8090:8090 \
                            -e SPRING_PROFILES_ACTIVE=prod \
                            -e DB_HOST=${PROD_DB_HOST} \
                            -e DB_PORT=${PROD_DB_PORT} \
                            -e DB_NAME=${POSTGRES_DB} \
                            -e DB_USER=${POSTGRES_USER} \
                            -e DB_PASSWORD=${POSTGRES_PASSWORD} \
                            -e JPA_DDL_AUTO=validate \
                            ${DOCKER_IMAGE}:latest
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo "🚀 Despliegue finalizado. Probando conexión..."
        }
        failure {
            echo "❌ Error detectado. Logs de Spring Boot:"
            sh "docker logs ${CONTAINER_NAME} --tail 50 || true"
        }
        always {
            sh "docker image prune -f"
        }
    }
}
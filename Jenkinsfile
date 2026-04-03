pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "todo-service"
        CONTAINER_NAME = "todo-app"
        DOCKER_NETWORK = "web_network"

        // Host y puerto (coinciden con tu contenedor postgres-prod)
        DB_HOST = "postgres-prod"
        DB_PORT = "5432"
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
                // 'credentialsId' es el ID en el panel de Jenkins
                // 'variable' es como la vas a llamar dentro de este bloque
                withCredentials([
                    string(credentialsId: 'POSTGRES_TODO_USER', variable: 'J_USER'),
                    string(credentialsId: 'POSTGRES_TODO_PASSWORD', variable: 'J_PASS'),
                    string(credentialsId: 'POSTGRES_TODO_DB', variable: 'J_DB')
                ]) {
                    script {
                        // Aseguramos que la red exista y el host esté vinculado
                        sh "docker network create ${DOCKER_NETWORK} || true"
                        sh "docker network connect ${DOCKER_NETWORK} ${DB_HOST} || true"

                        // Limpieza del contenedor viejo
                        sh "docker rm -f ${CONTAINER_NAME} 2>/dev/null || true"

                        // MAPEO: El nombre a la izquierda del '=' es el que definiste en el .yml
                        // El nombre a la derecha es la variable que viene de withCredentials
                        sh """
                            docker run -d \
                            --name ${CONTAINER_NAME} \
                            --network ${DOCKER_NETWORK} \
                            --restart unless-stopped \
                            -p 8090:8090 \
                            -e SPRING_PROFILES_ACTIVE=prod \
                            -e DB_HOST=${DB_HOST} \
                            -e DB_PORT=${DB_PORT} \
                            -e DB_NAME=${J_DB} \
                            -e DB_USER=${J_USER} \
                            -e DB_PASS=${J_PASS} \
                            -e DB_DDL=update \
                            ${DOCKER_IMAGE}:latest
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo "🚀 Despliegue exitoso. Revisando logs de conexión..."
            sh "sleep 10 && docker logs ${CONTAINER_NAME} --tail 30"
        }
        failure {
            echo "❌ Error en el Pipeline. Verificá las credenciales en Jenkins."
            sh "docker logs ${CONTAINER_NAME} --tail 50 || true"
        }
        always {
            sh "docker image prune -f"
        }
    }
}
pipeline {
    agent any

    environment {
        // Identificadores de imagen y contenedor
        DOCKER_IMAGE = "todo-service"
        CONTAINER_NAME = "todo-app"
        DOCKER_NETWORK = "web_network"

        // Configuración de infraestructura
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
                // Usando los IDs exactos de tus credenciales en Jenkins
                withCredentials([
                    string(credentialsId: 'POSTGRES_TODO_USER', variable: 'POSTGRES_TODO_USER'),
                    string(credentialsId: 'POSTGRES_TODO_PASSWORD', variable: 'POSTGRES_TODO_PASSWORD'),
                    string(credentialsId: 'POSTGRES_TODO_DB', variable: 'POSTGRES_TODO_DB')
                ]) {
                    script {
                        // Lógica Robusta: Matamos cualquier contenedor que use el puerto 8090
                        sh """
                            EXISTING_CONTAINER=\$(docker ps -q --filter "publish=8090")
                            if [ ! -z "\$EXISTING_CONTAINER" ]; then
                                echo "Limpiando puerto 8090 ocupado por: \$EXISTING_CONTAINER"
                                docker stop \$EXISTING_CONTAINER && docker rm \$EXISTING_CONTAINER
                            fi

                            # También removemos por nombre por si quedó un intento fallido anterior
                            docker rm -f ${CONTAINER_NAME} || true
                        """

                        // Despliegue del contenedor
                        // El uso de '${DB_PASS}' protege los caracteres especiales (#, !)
                        sh """
                            docker run -d \
                            --name ${CONTAINER_NAME} \
                            --network ${DOCKER_NETWORK} \
                            --restart unless-stopped \
                            -p 8090:8090 \
                            -e SPRING_PROFILES_ACTIVE=prod \
                            -e DB_HOST=${PROD_DB_HOST} \
                            -e DB_PORT=${PROD_DB_PORT} \
                            -e DB_NAME='${POSTGRES_TODO_DB}' \
                            -e DB_USER='${POSTGRES_TODO_USER}' \
                            -e DB_PASSWORD='${POSTGRES_TODO_PASSWORD}' \
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
            echo "🚀 Backend desplegado con éxito en el puerto 8090."
            echo "El contenedor '${CONTAINER_NAME}' ya es visible para Nginx."
        }
        failure {
            echo "❌ Error en el despliegue. Revisar logs con 'docker logs ${CONTAINER_NAME}'"
        }
        always {
            // Mantenemos el servidor limpio de imágenes intermedias
            sh "docker image prune -f"
        }
    }
}
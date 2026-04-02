pipeline {
    agent any

    environment {
        // Nombre de la imagen (el molde)
        DOCKER_IMAGE = "todo-service"
        // Nombre del contenedor (la instancia que busca Nginx)
        CONTAINER_NAME = "todo-app"
        DOCKER_NETWORK = "web_network"

        // Configuración de red para Spring Boot
        PROD_DB_HOST = "postgres-prod"
        PROD_DB_PORT = "5432"
        PROD_DB_NAME = "todo_prod"
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
                    // Construimos con el tag del número de build y el tag 'latest'
                    sh "docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} ."
                    sh "docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_IMAGE}:latest"
                }
            }
        }

        stage('Deploy to Production') {
            steps {
                // Sincronizado con los IDs de tu captura de pantalla de Jenkins
                withCredentials([
                    string(credentialsId: 'TODO_PROD_DB_USER', variable: 'DB_USER'),
                    string(credentialsId: 'TODO_PROD_DB_PASS', variable: 'DB_PASS'),
                    string(credentialsId: 'TODO_PROD_DB_NAME', variable: 'DB_NAME_VAL')
                ]) {
                    script {
                        // 1. Limpiamos contenedores anteriores si existen
                        sh "docker stop ${CONTAINER_NAME} || true"
                        sh "docker rm ${CONTAINER_NAME} || true"

                        // 2. Ejecutamos el nuevo contenedor
                        // Nota: Usamos comillas simples '${DB_PASS}' para proteger caracteres especiales (#, !)
                        sh """
                            docker run -d \
                            --name ${CONTAINER_NAME} \
                            --network ${DOCKER_NETWORK} \
                            --restart unless-stopped \
                            -p 8090:8090 \
                            -e SPRING_PROFILES_ACTIVE=prod \
                            -e DB_HOST=${PROD_DB_HOST} \
                            -e DB_PORT=${PROD_DB_PORT} \
                            -e DB_NAME=${PROD_DB_NAME} \
                            -e DB_USER=${DB_USER} \
                            -e DB_PASSWORD='${DB_PASS}' \
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
            echo "🚀 Despliegue exitoso. App corriendo en contenedor: ${CONTAINER_NAME}"
        }
        failure {
            echo "❌ El despliegue falló. Revisar logs de Jenkins y 'docker logs ${CONTAINER_NAME}'"
        }
        always {
            // Limpieza de imágenes huérfanas para ahorrar espacio en disco
            sh "docker image prune -f"
        }
    }
}
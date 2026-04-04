pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "todo-backend"
        CONTAINER_NAME = "todo-backend"
        DOCKER_NETWORK = "web_network"
        // Nombre del contenedor de DB que ya levantamos en la infraestructura
        DB_HOST = "postgres-prod"
        DB_PORT = "5432"
    }

    stages {
        stage('Compile & Package') {
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
                    // Construimos la imagen con el tag del build y el tag latest
                    sh "docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} ."
                    sh "docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_IMAGE}:latest"
                }
            }
        }

        stage('Deploy to Production') {
            steps {
                // Inyectamos las credenciales guardadas en Jenkins
                withCredentials([
                    string(credentialsId: 'POSTGRES_TODO_USER', variable: 'J_USER'),
                    string(credentialsId: 'POSTGRES_TODO_PASSWORD', variable: 'J_PASS'),
                    string(credentialsId: 'POSTGRES_TODO_DB', variable: 'J_DB')
                ]) {
                    script {
                        // 1. Limpieza del contenedor anterior si existe
                        sh "docker rm -f ${CONTAINER_NAME} 2>/dev/null || true"

                        // 2. Ejecución del nuevo contenedor
                        // Nota: Usamos DB_DDL=validate para no romper la DB de prod accidentalmente
                        sh """
                            docker run -d \
                            --name ${CONTAINER_NAME} \
                            --network ${DOCKER_NETWORK} \
                            --restart unless-stopped \
                            -e SPRING_PROFILES_ACTIVE=prod \
                            -e DB_HOST=${DB_HOST} \
                            -e DB_PORT=${DB_PORT} \
                            -e DB_NAME=${J_DB} \
                            -e DB_USER=${J_USER} \
                            -e DB_PASS=${J_PASS} \
                            -e DB_DDL=validate \
                            ${DOCKER_IMAGE}:latest
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo "🚀 Despliegue exitoso en ${CONTAINER_NAME}"
        }
        always {
            // BORRAR CÓDIGO FUENTE: Mantenemos el servidor limpio
            deleteDir()
            // Limpieza de imágenes huérfanas para ahorrar espacio
            sh "docker image prune -f"
        }
    }
}
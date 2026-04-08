pipeline {
    agent any

    // Limitamos a los últimos 5 builds para no llenar el disco de logs y carpetas
    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        disableConcurrentBuilds() // Evita que dos builds choquen al intentar usar el mismo contenedor
    }

    environment {
        DOCKER_IMAGE   = "todo-service"
        CONTAINER_NAME = "todo-service"
        DOCKER_NETWORK = "web_network"
        DB_HOST        = "todo-postgres-prod"
        DB_PORT        = "5432"
        // Definimos el tag para poder referenciarlo fácilmente
        IMAGE_TAG      = "${DOCKER_IMAGE}:${BUILD_NUMBER}"
    }

    stages {
        stage('Compile & Package') {
            steps {
                script {
                    sh "chmod +x mvnw"
                    // Agregamos limpieza de cache de Maven para evitar conflictos
                    sh "./mvnw clean package -DskipTests"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Usamos --pull para asegurar que la imagen base (JRE 21) esté actualizada
                    sh "docker build --pull -t ${IMAGE_TAG} ."
                    sh "docker tag ${IMAGE_TAG} ${DOCKER_IMAGE}:latest"
                }
            }
        }

        stage('Deploy to Production') {
            steps {
                withCredentials([
                    string(credentialsId: 'POSTGRES_TODO_USER',     variable: 'DB_USER_VAL'),
                    string(credentialsId: 'POSTGRES_TODO_PASSWORD', variable: 'DB_PASS_VAL'),
                    string(credentialsId: 'POSTGRES_TODO_DB',       variable: 'DB_NAME_VAL')
                ]) {
                    script {
                        // 1. Limpieza prolija
                        sh "docker rm -f ${CONTAINER_NAME} 2>/dev/null || true"

                        // 2. Ejecución con variables inyectadas de forma segura
                        sh """
                            docker run -d \
                            --name ${CONTAINER_NAME} \
                            --network ${DOCKER_NETWORK} \
                            --restart unless-stopped \
                            -e SPRING_PROFILES_ACTIVE=prod \
                            -e DB_HOST=${DB_HOST} \
                            -e DB_PORT=${DB_PORT} \
                            -e DB_NAME=${DB_NAME_VAL} \
                            -e DB_USER=${DB_USER_VAL} \
                            -e DB_PASS=${DB_PASS_VAL} \
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
            echo "🚀 Despliegue exitoso de ${DOCKER_IMAGE} build #${BUILD_NUMBER}"
        }
        failure {
            echo "❌ Falló el despliegue. Revisar logs de Maven o Docker."
        }
        always {
            script {
                // Borramos el código fuente
                deleteDir()

                // --- LIMPIEZA PRO ---
                // 1. Borramos la imagen específica de este build (la :BUILD_NUMBER)
                // Esto evita que se acumulen las imágenes que listamos antes.
                // La versión :latest queda viva porque está "In Use" por el contenedor.
                sh "docker rmi ${IMAGE_TAG} || true"

                // 2. Limpieza general de capas intermedias (dangling images)
                sh "docker image prune -f"
            }
        }
    }
}
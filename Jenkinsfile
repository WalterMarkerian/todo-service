pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "todo-backend-prod"
        DOCKER_NETWORK = "todo-network"

        // Extraemos las credenciales guardadas en Jenkins (ID: 'DB_USER_PROD' y 'DB_PASS_PROD')
        DB_USER = credentials('DB_USER_PROD')
        DB_PASS = credentials('DB_PASS_PROD')

        // Apuntamos al contenedor 'postgres-central' que creamos en el paso 1
        DB_URL = "jdbc:postgresql://postgres-central:5432/todo_prod"
    }

    stages {
        stage('Compile & Test') {
            steps {
                // Aseguramos que el wrapper sea ejecutable
                sh "chmod +x mvnw"
                sh "./mvnw clean package -DskipTests"
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE}:latest ."
            }
        }

        stage('Deploy to Production') {
            steps {
                // Limpieza de contenedores anteriores
                sh "docker stop todo-backend-prod || true && docker rm todo-backend-prod || true"

                // Inyección de variables de entorno de producción
                sh """
                    docker run -d \
                    --name todo-backend-prod \
                    --network ${DOCKER_NETWORK} \
                    --restart unless-stopped \
                    -p 8090:8090 \
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
        success { echo "🚀 Backend de Producción desplegado en puerto 8090" }
        always { sh "docker image prune -f" }
    }
}
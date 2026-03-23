pipeline {
    agent any

    environment {
        // Nombre de la imagen y contenedor
        DOCKER_IMAGE = "todo-api-local"
        CONTAINER_NAME = "todo-api-app"

        // Red de Docker donde corre tu Postgres
        DOCKER_NETWORK = "todo-network"

        // Extraemos las credenciales guardadas en Jenkins
        // Esto inyecta automáticamente las variables al entorno del Pipeline
        DB_USER = credentials('DB_USER')
        DB_PASS = credentials('DB_PASS')

        // La URL apunta al nombre del servicio/contenedor de Postgres en la red
        DB_URL = "jdbc:postgresql://postgres-db:5432/tododb"
    }

    stages {
        stage('Compile') {
            steps {
                // chmod +x mvnw  <-- Asegurate de que el wrapper sea ejecutable en Git
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Build Image') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE}:latest ."
            }
        }

        stage('Deploy') {
            steps {
                // 1. Limpieza de contenedores previos
                sh "docker stop ${CONTAINER_NAME} || true && docker rm ${CONTAINER_NAME} || true"

                // 2. Ejecución con inyección de variables de entorno
                // El puerto 8090 mapea al 8080 interno de Spring
                sh """
                    docker run -d \
                    --name ${CONTAINER_NAME} \
                    -p 8090:8080 \
                    --network ${DOCKER_NETWORK} \
                    -e SPRING_DATASOURCE_URL=${DB_URL} \
                    -e SPRING_DATASOURCE_USERNAME=${DB_USER} \
                    -e SPRING_DATASOURCE_PASSWORD=${DB_PASS} \
                    ${DOCKER_IMAGE}:latest
                """
            }
        }
    }

    post {
        success {
            echo "API desplegada correctamente en puerto 8090"
        }
        failure {
            echo "Falla en el despliegue. Revisar consola de Jenkins."
        }
    }
}
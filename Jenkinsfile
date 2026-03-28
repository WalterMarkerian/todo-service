pipeline {
    agent any

    environment {
        // Nombre de la imagen y contenedor
        DOCKER_IMAGE = "todo-api-local"
        CONTAINER_NAME = "todo-backend"

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
                // Detenemos contenedores previos para evitar conflictos de puerto
                sh "docker stop todo-api-app || true && docker rm todo-api-app || true"

                sh """
                    docker run -d \
                    --name todo-api-app \
                    -p 8090:8090 \
                    --network todo-network \
                    -e SPRING_PROFILES_ACTIVE=prod \
                    -e DB_USER=user_admin \
                    -e DB_PASS=password_secure \
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
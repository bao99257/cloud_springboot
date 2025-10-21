pipeline {
    agent any

    environment {
        DOCKERHUB_USER = 'leanhbao'                    // 👈 Tài khoản DockerHub thật
        DOCKERHUB_CREDENTIALS = 'dockerhub-cred'       // 👈 ID credentials trong Jenkins
        BACKEND_IMAGE = "${DOCKERHUB_USER}/backend"
        FRONTEND_IMAGE = "${DOCKERHUB_USER}/frontend"
        DEPLOY_PATH = "/opt/deploy/app"
    }

    stages {

        stage('Checkout Source') {
            steps {
                echo "📦 Cloning repository..."
                git branch: 'main', url: 'https://github.com/bao99257/cloud_springboot.git'
            }
        }

        stage('Build Backend') {
            steps {
                echo "🧱 Building Spring Boot backend..."
                // ❗ Dùng Maven hệ thống, không dùng mvnw wrapper
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Images') {
            steps {
                echo "🐳 Building Docker images..."
                script {
                    // Build backend (Dockerfile ở thư mục backend)
                    sh "docker build -t ${BACKEND_IMAGE}:latest ./backend"
                    // Build frontend (Dockerfile ở thư mục frontend)
                    sh "docker build -t ${FRONTEND_IMAGE}:latest ./frontend"
                }
            }
        }

        stage('Push to DockerHub') {
            steps {
                echo "📤 Pushing images to DockerHub..."
                withCredentials([usernamePassword(credentialsId: "${DOCKERHUB_CREDENTIALS}", usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                    sh """
                    echo $PASS | docker login -u $USER --password-stdin
                    docker push ${BACKEND_IMAGE}:latest
                    docker push ${FRONTEND_IMAGE}:latest
                    """
                }
            }
        }

        stage('Deploy to Production') {
            steps {
                echo "🚀 Deploying to production..."
                sh """
                cd ${DEPLOY_PATH}
                docker compose down || true
                docker compose pull
                docker compose up -d
                """
            }
        }
    }

    post {
        success {
            echo "✅ Deployment completed successfully!"
        }
        failure {
            echo "❌ Deployment failed. Check the logs for details."
        }
    }
}
